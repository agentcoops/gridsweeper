package com.edbaskerville.gridsweeper;

import org.ggf.drmaa.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import static com.edbaskerville.gridsweeper.StringUtils.*;
import static com.edbaskerville.gridsweeper.DateUtils.*;

public class GridSweeper
{
	enum ArgState
	{
		START,
		ADAPTER,
		EXPERIMENT
	}
	
	static String adapterClassName;
	static String experimentPath;
	
	static Experiment experiment;
	static boolean dryRun;
	static List<ExperimentCase> cases;
	static GridDelegate gridDelegate;
	static int numFailedRuns;
	
	static Preferences preferences;
	
	static boolean useFileTransfer;
	
	static String className;
	static Calendar cal;
	
	static String dateStr;
	static String timeStr;
	static String expDir;
	
	static Session drmaaSession;
	
	static
	{
		experiment = null;
		dryRun = false;
		numFailedRuns = 0;
		gridDelegate = new GridDelegate()
		{
			public void batchCompleted()
			{
				System.err.println("The experiment has completed.");
			}
			
			public void batchFailed(Exception e)
			{
				System.err.println("The experiment failed due to an exception:");
				e.printStackTrace();
			}

			public void runCompleted(ExperimentCase experimentCase, int runNumber)
			{
				String description = experiment.getCaseDescription(experimentCase);
				System.err.println("Completed run " + runNumber + " of case " + description);
			}

			public void runFailed(ExperimentCase experimentCase, int runNumber)
			{
				String description = experiment.getCaseDescription(experimentCase);
				System.err.println("Run " + runNumber + " of case " + description + " failed.");
			}
			
		};
		
		preferences = Preferences.sharedPreferences();
		className = GridSweeper.class.toString();
		cal = new GregorianCalendar();
	}
	
	public static void main(String[] args) throws GridSweeperException
	{
		Logger.entering(className, "main");
		
		adapterClassName = Preferences.sharedPreferences().getProperty("AdapterClass");
		experimentPath = null;
		
		// Parse args
		parseArgs(args);
		
		// Load experiment file
		loadExperiment();
		
		// Generate experiment cases, etc.
		setUpExperiment();
		
		// Run jobs
		run();

		// Wait for job completion
		finish();
		
		Logger.exiting(className, "main");
	}
	
	private static void parseArgs(String[] args)
	{
		Logger.entering(className, "parseArgs");
		
		ArgState state = ArgState.START;
		
		for(String arg : args)
		{
			switch(state)
			{
				case START:
					if(arg.equals("-a")) state = ArgState.ADAPTER;
					else if(arg.equals("-e")) state = ArgState.EXPERIMENT;
					else if(arg.equals("-d")) dryRun = true;
					break;
				case ADAPTER:
					adapterClassName = arg;
					state = ArgState.START;
					break;
				case EXPERIMENT:
					experimentPath = arg;
					state = ArgState.START;
					break;
			}
		}
		
		Logger.exiting(className, "parseArgs");
	}
	
	private static void loadExperiment() throws GridSweeperException
	{
		Logger.entering(className, "loadExperiment");
		
		if(experimentPath == null)
		{
			throw new GridSweeperException("No experiment file provided.");
		}
		
		try
		{
			experiment = new Experiment(new java.net.URL("file", "", experimentPath));
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not load experiment file.", e);
		}
		
		Logger.exiting(className, "loadExperiment");
	}
	
	private static void setUpExperiment() throws GridSweeperException
	{
		try
		{
			// Assemble cases
			cases = experiment.generateCases(new Random());
		}
		catch (ExperimentException e)
		{
			throw new GridSweeperException("Could not generate experiment cases", e);
		}
	}
	
	private static void run() throws GridSweeperException
	{
		Logger.entering(className, "prepare");
		
		useFileTransfer = !preferences.getBooleanProperty("UseSharedFileSystem");
		
		try
		{
			Logger.finer("preferences: " + preferences);
			
			// Set up file transfer system if asked for
			FileTransferSystem fts = null;
			if(!dryRun && useFileTransfer)
			{
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(preferences);
				fts.connect();
			}
			
			String expsDir = expandTildeInPath(preferences.getProperty("ExperimentsDirectory"));
			
			// First set up big directory for the whole experiment
			String expName = experiment.getName();
			dateStr = getDateString(cal);
			timeStr = getTimeString(cal);
			String expSubDir = String.format("%s%s%s-%s", dateStr, getFileSeparator(), expName, timeStr);
			
			expDir = appendPathComponent(expsDir, expSubDir);
			Logger.finer("Experiment subdirectory: " + expDir);
			
			File expDirFile = new File(expDir);
			expDirFile.mkdirs();
			
			// If file transfer is on, make the directory
			// and upload input files
			if(!dryRun && useFileTransfer)
			{
				String inputDir = appendPathComponent(expSubDir, "input");
				fts.makeDirectory(inputDir);
				
				Properties inputFiles = experiment.getInputFiles();
				for(Object localPathObj : inputFiles.keySet())
				{
					String localPath = (String)localPathObj;
					String remotePath = appendPathComponent(inputDir, inputFiles.getProperty(localPath));
					
					fts.uploadFile(localPath, remotePath);
				}
			}
			
			// Establish DRMAA session
			drmaaSession = SessionFactory.getFactory().getSession();
			drmaaSession.init(null);
			
			// Set up and run each case
			for(ExperimentCase expCase : cases)
			{
				runCase(expCase);
			}
			if(useFileTransfer) fts.disconnect();
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not set up local dirs", e);
		}
		
		Logger.exiting(className, "prepare");
	}
	
	private static void runCase(ExperimentCase expCase) throws FileNotFoundException, DrmaaException
	{		
		String caseSubDir = experiment.getDirectoryNameForCase(expCase);
		String caseDir = appendPathComponent(expDir, caseSubDir);
		Logger.finer("Case subdirectory: " + caseDir);
		
		File caseDirFile = new File(caseDir);
		caseDirFile.mkdirs();
		
		// For each run, output XML and run the damn thing
		List<Long> rngSeeds = expCase.getRngSeeds();
		for(int i = 0; i < rngSeeds.size(); i++)
		{
			runCaseRun(expCase, caseDir, caseSubDir, i, rngSeeds.get(i));
		}
		
		/*// Set up an output directory for the case
		if(useFileTransfer)
		{
			String caseRemoteDir = appendPathComponent(expSubDir, caseSubDir);
			String caseRemoteDirInput = appendPathComponent(caseRemoteDir, "input");
			String caseRemoteDirOutput = appendPathComponent(caseRemoteDir, "output");
			fts.makeDirectory(caseRemoteDir);
			fts.makeDirectory(caseRemoteDirOutput);
		}*/
	}
	
	private static void runCaseRun(ExperimentCase expCase, String caseDir, String caseSubDir, int i, Long rngSeed) throws FileNotFoundException, DrmaaException
	{
		String caseRunName = experiment.getName() + "-" + dateStr + "-" + timeStr + "-" + caseSubDir + "-" + i;

		// Write XML
		String xmlPath = appendPathComponent(caseDir, "case." + i + ".gsweep");
		ExperimentCaseXMLWriter xmlWriter = new ExperimentCaseXMLWriter(
				xmlPath, experiment, expCase, caseRunName, rngSeed);
		xmlWriter.writeXML();
		
		// Generate job template
		JobTemplate jt = drmaaSession.createJobTemplate();
		
		jt.setJobName(caseRunName);
		
		jt.setRemoteCommand("/bin/echo");
		jt.setArgs(new String[] {caseSubDir } );
		
		if(!useFileTransfer) jt.setWorkingDirectory(caseDir);
		jt.setOutputPath(appendPathComponent(caseDir, ".gridsweeper_out." + i));
		jt.setTransferFiles(new FileTransferMode(false, true, false));
		
		String jobId = drmaaSession.runJob(jt);
		drmaaSession.deleteJobTemplate(jt);
		
		// TODO: Record job id so it can be reported back as tied to this case run.
	}
	
	private static void finish() throws GridSweeperException
	{
		try
		{
			drmaaSession.exit();
		}
		catch(DrmaaException e)
		{
			throw new GridSweeperException("Received exception ending DRMAA session", e);
		}
	}
}
