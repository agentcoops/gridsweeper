package edu.umich.lsa.cscs.gridsweeper;

import org.ggf.drmaa.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;
import static edu.umich.lsa.cscs.gridsweeper.DateUtils.*;
import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

/**
 * The GridSweeper command-line tool for job submission. Takes a .gsexp
 * XML experiment file and submits it to the grid for execution via DRMAA.
 * Warning: begun on a houseboat in Paris. May still contain strange French bugs.
 * 
 * @author Ed Baskerville
 *
 */
public class GridSweeper
{
	enum RunType
	{
		RUN,
		DRY,
		NORUN
	}
	
	static String className;
	
	static
	{
		className = GridSweeper.class.toString();
	}
	
	String root;
	
	Experiment experiment;
	RunType runType = RunType.RUN;
	List<ExperimentCase> cases = null;
	
	boolean useFileTransfer = false;
	
	Calendar cal;
	
	String dateStr;
	String timeStr;
	String expDir;
	
	String fileTransferSubpath;
	
	Session drmaaSession;
	
	public GridSweeper()
	{
		cal = new GregorianCalendar(); 
	}
	
	/**
	 * Generates experiment cases in preparation for running the experiment.
	 * @throws GridSweeperException If case generation fails.
	 */
	public void setUpExperiment() throws GridSweeperException
	{
		if(runType != RunType.NORUN) try
		{
			// Assemble cases
			cases = experiment.generateCases();
		}
		catch (ExperimentException e)
		{
			throw new GridSweeperException("Could not generate experiment cases", e);
		}
	}
	
	/**
	 * Runs the experiment. Experiment results are collated in a master experiment directory,
	 * specified in the user settings, in a subdirectory tagged with the experiment name
	 * and date/time ({@code <name>/YYYY-MM-DD/hh-mm-ss}). If a shared filesystem is not
	 * available, files are first staged to the experiment results directory on the
	 * file transfer system. Then a DRMAA session is established, and each case is submitted.
	 * 
	 * @throws GridSweeperException
	 */
	public void runExperiment() throws GridSweeperException
	{
		Settings settings = experiment.getSettings();
		
		if(runType == RunType.NORUN) return;
		
		entering(className, "run");
		
		useFileTransfer = settings.getBooleanProperty("UseFileTransfer");

		FileTransferSystem fts = null;
		try
		{
			finer("settings: " + settings);
			
			// Set up file transfer system if asked for
			if(runType==RunType.RUN && useFileTransfer)
			{
				String className = settings.getSetting("FileTransferSystemClassName");
				Settings ftsSettings = settings.getSettingsForClass(className);
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(className, ftsSettings);
				fts.connect();
				
				boolean alreadyExists;
				do
				{
					fileTransferSubpath = UUID.randomUUID().toString();
					alreadyExists = fts.fileExists(fileTransferSubpath);
				}
				while(alreadyExists);
			}
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not set up file trasfer system", e);
		}
		
		try
		{
			String expsDir = expandTildeInPath(settings.getProperty("ExperimentsDirectory"));
			
			// First set up big directory for the whole experiment
			// Located in <experimentDir>/<experimentName>/<experimentDate>/<experimentTime>
			String expName = experiment.getName();
			if(expName == null)
			{
				throw new GridSweeperException("Experiment name must be specified.");
			}
			
			dateStr = getDateString(cal);
			timeStr = getTimeString(cal);
			String expSubDir = String.format("%s%s%s%s%s", expName, getFileSeparator(), dateStr, getFileSeparator(), timeStr);
			
			expDir = appendPathComponent(expsDir, expSubDir);
			finer("Experiment subdirectory: " + expDir);
			
			File expDirFile = new File(expDir);
			expDirFile.mkdirs();
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not set up local dirs", e);
		}
		
		try
		{
			// If file transfer is on, make the directory
			// and upload input files
			if(runType==RunType.RUN && useFileTransfer)
			{
				String inputDir = appendPathComponent(fileTransferSubpath, "input");
				fts.makeDirectory(inputDir);
				
				StringMap inputFiles = experiment.getInputFiles();
				for(String localPath : inputFiles.keySet())
				{
					String remotePath = appendPathComponent(inputDir, inputFiles.get(localPath));
					
					fts.uploadFile(localPath, remotePath);
				}
				
				fts.disconnect();
			}
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not create remote dirs", e);
		}
			
		try
		{
			// Write experiment XML
			String xmlPath = appendPathComponent(expDir, "experiment.gsexp");
			experiment.writeToFile(xmlPath, true);
			
			if(runType==RunType.RUN)
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
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Could not run experiments", e);
		}
		
		exiting(className, "prepare");
	}
	
	/**
	 * Submits a single experiment case. This means running one job for each
	 * run of the case (one for each random seed).
	 * @param expCase The experiment case to run.
	 * @throws FileNotFoundException If the case directory cannot be found/created.
	 * @throws DrmaaException If a DRMAA error occurs (in {@link #runCaseRun}).
	 * @throws IOException If the case XML cannot be written out (in {@link #runCaseRun}).
	 */
	public void runCase(ExperimentCase expCase) throws FileNotFoundException, DrmaaException, IOException
	{
		String caseSubDir = experiment.getDirectoryNameForCase(expCase);
		String caseDir = appendPathComponent(expDir, caseSubDir);
		finer("Case subdirectory: " + caseDir);
		
		File caseDirFile = new File(caseDir);
		caseDirFile.mkdirs();
		
		String caseName;
		if(caseSubDir.equals(""))
		{
			caseName = experiment.getName()
			+ " (" + dateStr + ", " + timeStr + ")";
		}
		else
		{
			caseName = experiment.getName() + " - "
			+ caseSubDir + " (" + dateStr + ", " + timeStr + ")";
		}

		// Write XML
		String xmlPath = appendPathComponent(caseDir, "case.gscase");
		ExperimentCaseXMLWriter xmlWriter = new ExperimentCaseXMLWriter(
				xmlPath, expCase, caseName);
		xmlWriter.writeXML();
		
		// Run each individual run on the grid
		List<BigInteger> rngSeeds = expCase.getRngSeeds();
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
	public void runCaseRun(ExperimentCase expCase, String caseDir, String caseSubDir, int i, BigInteger rngSeed) throws DrmaaException, IOException
	{
		Settings settings = experiment.getSettings();
		
		String caseRunName;
		if(caseSubDir.equals(""))
		{
			caseRunName = experiment.getName() + " - run " + i
			+ " (" + dateStr + ", " + timeStr + ")";
		}
		else
		{
			caseRunName = experiment.getName() + " - "
			+ caseSubDir + " - run " + i
			+ " (" + dateStr + ", " + timeStr + ")";
		}
		
		if(runType == RunType.RUN)
		{
			// Write setup file
			String stdinPath = appendPathComponent(caseDir, ".gsweep_in." + i);
			RunSetup setup = new RunSetup(settings,
					experiment.getInputFiles(), caseSubDir, expCase.getParameterMap(),
					i, rngSeed, experiment.getOutputFiles());
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
			jt.setBlockEmail(true);
			
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
			
			String classpath = System.getenv("CLASSPATH");
			if(classpath != null) environment.setProperty("CLASSPATH", classpath);
			jt.setJobEnvironment(environment);
			
			String jobId = drmaaSession.runJob(jt);
			drmaaSession.deleteJobTemplate(jt);
			
			// TODO: Record job id so it can be reported back as tied to this case run.
		}
	}
	
	/**
	 * Cleans up: for now, just closes the DRMAA session.
	 * @throws GridSweeperException If the DRMAA {@code exit()} call fails.
	 */
	public void finish() throws GridSweeperException
	{
		// TODO: wait for all jobs to complete, giving notification
		// as each one arrives
		// TODO: provide mechanism to detach this session to the background,
		// in some way that works even if the user logs out
		// This is a bit like a daemon, so cf:
		// http://pezra.barelyenough.org/blog/2005/03/java-daemon/
		// http://wrapper.tanukisoftware.org/doc/english/prop-daemonize.html
		// TODO: upon full completion, send an email to the user
		
		if(runType == RunType.RUN) try
		{
			drmaaSession.exit();
		}
		catch(DrmaaException e)
		{
			throw new GridSweeperException("Received exception ending DRMAA session", e);
		}
	}

	public void setRunType(RunType runType)
	{
		this.runType = runType;
	}
	
	public void setRoot(String root)
	{
		this.root = root;
	}
	
	public void setExperiment(Experiment experiment)
	{
		this.experiment = experiment;
	}
}
