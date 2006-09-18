package com.edbaskerville.gridsweeper;

import org.ggf.drmaa.DrmaaException;

import java.io.File;
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
	static List<ExperimentCase> cases;
	static GridDelegate gridDelegate;
	static int numFailedRuns;
	
	static Preferences preferences;
	
	static String className;
	static Calendar cal;
	
	static
	{
		experiment = null;
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
		
		// Prepare filesystems: local always, and remote if shared filesystem is off
		prepareFileSystems();
		
		// Connect, submit, wait for completion, and disconnect
		GridController controller = new GridController(gridDelegate);
		try
		{
			controller.connect();
			controller.submitExperiment(experiment, adapterClassName, true);
			controller.disconnect();
		}
		catch(DrmaaException e)
		{
			throw new GridSweeperException("Could not submit experiment", e);
		}

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
	
	private static void prepareFileSystems() throws GridSweeperException
	{
		Logger.entering(className, "prepareFileSystems");
		
		boolean useFileTransfer = !preferences.getBooleanProperty("UseSharedFileSystem");
		
		try
		{
			Logger.finer("preferences: " + preferences);
			
			FileTransferSystem fts = null;
			if(useFileTransfer)
			{
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(preferences);
				fts.connect();
			}
			
			String expsDir = expandTildeInPath(preferences.getProperty("ExperimentsDirectory"));
			
			// First set up big directory for the whole experiment
			String expName = experiment.getName();
			String dateStr = getDateString(cal);
			String timeStr = getTimeString(cal);
			String expSubDir = String.format("%s%s%s-%s", dateStr, getFileSeparator(), expName, timeStr);
			
			String expDir = appendPathComponent(expsDir, expSubDir);
			Logger.finer("Experiment subdirectory: " + expDir);
			
			File expDirFile = new File(expDir);
			expDirFile.mkdirs();
			
			if(useFileTransfer) fts.makeDirectory(expSubDir);
			
			// Now set up subdirectories for each case
			for(ExperimentCase expCase : cases)
			{
				String caseSubDir = experiment.getDirectoryNameForCase(expCase);
				String caseLocalDir = appendPathComponent(expDir, caseSubDir);
				Logger.finer("Case subdirectory: " + caseLocalDir);

				File caseDirFile = new File(caseLocalDir);
				caseDirFile.mkdirs();
				
				// If file transfer is on, set things up there
				if(useFileTransfer)
				{
					// First set up the directories
					String caseRemoteDir = appendPathComponent(expSubDir, caseSubDir);
					String caseRemoteDirInput = appendPathComponent(caseRemoteDir, "input");
					String caseRemoteDirOutput = appendPathComponent(caseRemoteDir, "output");
					fts.makeDirectory(caseRemoteDirInput);
					fts.makeDirectory(caseRemoteDirOutput);
					
					// Now upload the input files
					
				}
			}
			if(useFileTransfer) fts.disconnect();
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not set up local dirs", e);
		}
		
		Logger.exiting(className, "prepareFileSystems");
	}
}
