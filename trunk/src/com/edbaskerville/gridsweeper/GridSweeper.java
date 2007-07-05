package com.edbaskerville.gridsweeper;

import org.ggf.drmaa.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import static com.edbaskerville.gridsweeper.StringUtils.*;
import static com.edbaskerville.gridsweeper.DateUtils.*;

/**
 * The GridSweeper command-line tool for job submission. Takes a .gsweep
 * XML experiment file and submits it to the grid for execution via DRMAA.
 * Warning: Written on a houseboat in Paris. May still contain strange French bugs.
 * @author Ed Baskerville
 *
 */
public class GridSweeper
{
	/**
	 * A state enum for the argument-parsing state machine.
	 * @author Ed Baskerville
	 *
	 */
	enum ArgState
	{
		START,
		ADAPTER,
		EXPERIMENT
	}
	
	static String adapterClassName;
	static String experimentPath;
	
	static String root;
	
	static Experiment experiment;
	static boolean dryRun;
	static List<ExperimentCase> cases;
	
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
		
		preferences = Preferences.sharedPreferences();
		className = GridSweeper.class.toString();
		cal = new GregorianCalendar();
	}
	
	/**
	 * Does everything: loads the experiment, runs the experiment, and (soon)
	 * monitors the experiment.
	 * @param args Command-line arguments.
	 * @throws GridSweeperException If the GRIDSWEEPER_ROOT environment variable
	 * is not set, or if parsing, loading, setup, running, or monitoring jobs
	 * generate any other uncaught exceptions.
	 */
	public static void main(String[] args) throws GridSweeperException
	{
		Logger.entering(className, "main");
		
		adapterClassName = Preferences.sharedPreferences().getProperty("AdapterClass");
		experimentPath = null;
		
		root = System.getenv("GRIDSWEEPER_ROOT");
		if(root == null)
		{
			throw new GridSweeperException("GRIDSWEEPER_ROOT environment variable not set.");
		}
		
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
	
	/**
	 * Parses command-line arguments. Currently only handles -a (adapter class),
	 * -e (experiment file path), and -d (whether to perform a dry run).
	 * @param args Command-line arguments.
	 */
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
	
	/**
	 * Loads the experiment from the provided XML file.
	 * @throws GridSweeperException If the experiment path is not provided,
	 * or if the file cannot be loaded or parsed.
	 */
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
	
	/**
	 * Generates experiment cases in preparation for running the experiment.
	 * @throws GridSweeperException If case generation fails.
	 */
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
	
	/**
	 * Runs the experiment. Experiment results are collated in a master experiment directory,
	 * specified in the user preferences, in a subdirectory tagged with the experiment name
	 * and date/time ({@code <name>/YYYY-MM-DD-hh-mm-ss}). If a shared filesystem is not
	 * available, files are first staged to the experiment results directory on the
	 * file transfer system. Then a DRMAA session is established, and each case is submitted.
	 * 
	 * @throws GridSweeperException
	 */
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
			// Located in <experimentDir>/<experimentName>/<experimentDate>/<experimentTime>
			String expName = experiment.getName();
			dateStr = getDateString(cal);
			timeStr = getTimeString(cal);
			String expSubDir = String.format("%s%s%s-%s", expName, getFileSeparator(), dateStr, getFileSeparator(), timeStr);
			
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
					
					// TODO: ensure that uploadFile() creates directories that don't exist
					// if necessary
					fts.uploadFile(localPath, remotePath);
				}
			}
			
			if(!dryRun)
			{
				// Establish DRMAA session
				drmaaSession = SessionFactory.getFactory().getSession();
				drmaaSession.init(null);
			}
			
			// Set up and run each case
			for(ExperimentCase expCase : cases)
			{
				runCase(expCase);
			}
			if(!dryRun && useFileTransfer) fts.disconnect();
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not set up local dirs", e);
		}
		
		Logger.exiting(className, "prepare");
	}
	
	/**
	 * Submits a single experiment case. This means running one job for each
	 * run of the case (one for each random seed).
	 * @param expCase The experiment case to run.
	 * @throws FileNotFoundException If the case directory cannot be found/created.
	 * @throws DrmaaException If a DRMAA error occurs (in {@link #runCaseRun}).
	 * @throws IOException If the case XML cannot be written out (in {@link #runCaseRun}).
	 */
	private static void runCase(ExperimentCase expCase) throws FileNotFoundException, DrmaaException, IOException
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
	}
	
	/**
	 * Submits a single run of an experiment case.
	 * @param expCase The case to run.
	 * @param caseDir The full path to where files are stored for this case.
	 * @param caseSubDir The case directory relative to the experiment results directory.
	 * @param i The run number for this run.
	 * @param rngSeed The random seed for this run.
	 * @throws DrmaaException If a DRMAA error occurs during job submission.
	 * @throws IOException If the case XML cannot be written out.
	 */
	private static void runCaseRun(ExperimentCase expCase, String caseDir, String caseSubDir, int i, Long rngSeed) throws DrmaaException, IOException
	{
		String caseRunName = experiment.getName() + "-" + dateStr + "-" + timeStr + "-" + caseSubDir + "-" + i;

		// Write XML
		String xmlPath = appendPathComponent(caseDir, "case." + i + ".gsweep");
		ExperimentCaseXMLWriter xmlWriter = new ExperimentCaseXMLWriter(
				xmlPath, experiment, expCase, caseRunName, rngSeed);
		xmlWriter.writeXML();
		
		// Write setup file
		String stdinPath = appendPathComponent(caseDir, ".gsweep_in." + i);
		RunSetup setup = new RunSetup(preferences, experiment.getProperties(),
				experiment.getInputFiles(), caseSubDir, expCase.getParameterMap(),
				i, rngSeed, experiment.getOutputFiles(), adapterClassName);
		ObjectOutputStream stdinStream = new ObjectOutputStream(new FileOutputStream(stdinPath));
		stdinStream.writeObject(setup);
		
		// Generate job template
		JobTemplate jt = drmaaSession.createJobTemplate();
		jt.setJobName(caseRunName);
		jt.setRemoteCommand(appendPathComponent(root, "bin/grunner"));
		if(!useFileTransfer) jt.setWorkingDirectory(caseDir);
		jt.setInputPath(":" + stdinPath);
		jt.setOutputPath(":" + appendPathComponent(caseDir, ".gsweep_out." + i));
		jt.setErrorPath(":" + appendPathComponent(caseDir, ".gsweep_err." + i));
		
		try
		{
			jt.setTransferFiles(new FileTransferMode(true, true, true));
		}
		catch(DrmaaException e)
		{
			// If setTransferFiles isn't supported, we'll hope that the system defaults to
			// transfering them. This works for SGE.
		}
		
		Properties environment = new Properties();
		environment.setProperty("GRIDSWEEPER_ROOT", root);
		jt.setJobEnvironment(environment);
		
		String jobId = drmaaSession.runJob(jt);
		drmaaSession.deleteJobTemplate(jt);
		
		// TODO: Record job id so it can be reported back as tied to this case run.
	}
	
	/**
	 * Cleans up: for now, just closes the DRMAA session.
	 * @throws GridSweeperException If the DRMAA {@code exit()} call fails.
	 */
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
