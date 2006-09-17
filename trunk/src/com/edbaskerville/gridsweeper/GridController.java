package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;

import org.ggf.drmaa.*;

public class GridController
{
	private GridDelegate delegate;
	private Session session;
	
	private static String className;
	
	static
	{
		className = GridController.class.toString();
	}
	
	public GridController()
	{
		super();
	}
	
	public GridController(GridDelegate delegate)
	{
		this.delegate = delegate;
	}
	
	public void setDelegate(GridDelegate delegate)
	{
		this.delegate = delegate;
	}
	
	public GridDelegate getDelegate()
	{
		return delegate;
	}
	
	public void connect() throws DrmaaException
	{
		Logger.entering(className, "connect");
		
		/*session = SessionFactory.getFactory().getSession();
		session.init(null);*/

		Logger.exiting(className, "connect");
	}
	
	public void disconnect() throws DrmaaException
	{
		Logger.entering(className, "disconnect");
		
		//session.exit();
		
		Logger.exiting(className, "disconnect");
	}
	
	public void submitExperiment(Experiment experiment, String adapterClassName, boolean waitForCompletion)
	{
		Logger.entering(className, "submitExperiment");
		
		/*try
		{
			Properties properties = experiment.getProperties();
			
			byte[] stdinData = getStdinData(properties.getProperty("stdinPath"));
			
			// Assemble cases
			List<ExperimentCase> expCases = experiment.generateCases(new Random());
			
			// Set up files/directories for cases
			setupFileSystem(experiment, expCases, properties);
			
			// Submit to grid
			List<String> jobIds = submitCases(experiment, expCases, properties, adapterClassName, stdinData);
			
			if(waitForCompletion)
			{
				int numRuns = experiment.getNumRuns();
				
				for(int i = 0; i < expCases.size() * numRuns; i++)
				{
					JobInfo info = session.wait(Session.JOB_IDS_SESSION_ANY, Session.TIMEOUT_WAIT_FOREVER);
					String jobId = info.getJobId();
					
					int index = jobIds.indexOf(jobId);
					ExperimentCase expCase = expCases.get(index / numRuns);
					int runNumber = index % numRuns;
					
					downloadData(experiment, expCase, runNumber, properties);
					
					if(delegate != null)
					{
						delegate.runCompleted(expCase, runNumber);
					}
				}
			}
			else
			{
				// TODO: wait for completion on another thread and call back delegate from there.
			}
		}
		catch(Exception e)
		{
			if(delegate != null) delegate.batchFailed(e);
		}*/
		
		Logger.exiting(className, "submitExperiment"); 
	}

	private List<String> submitCases(Experiment exp, List<ExperimentCase> cases, Properties properties, String adapterClassName, byte[] stdinData) throws DrmaaException
	{
		Logger.entering(className, "submitCases");
		
		List<String> jobIds = new ArrayList<String>();
		
		for(ExperimentCase expCase : cases)
		{
			for(int i = 0; i < exp.getNumRuns(); i++)
			{
				JobTemplate jt = getJobTemplate(exp, expCase, i, properties, adapterClassName, stdinData);
				String jobId = session.runJob(jt);
				session.deleteJobTemplate(jt);
				jobIds.add(jobId);
			}
		}
		
		Logger.exiting(className, "submitCases");
		
		return jobIds;
	}
	
	private JobTemplate getJobTemplate(Experiment exp, ExperimentCase expCase, int runNumber, Properties properties, String adapterClassName, byte[] stdinData) throws DrmaaException
	{
		Logger.entering(className, "getJobTemplate");
		
		Preferences preferences = Preferences.sharedPreferences();
		
		JobTemplate jt = session.createJobTemplate();
		
		// Set job name for identifiability
		StringBuffer jobNameBuffer = new StringBuffer();
		String expName = exp.getName();
		if(expName != null)
		{
			jobNameBuffer.append(expName + ": ");
		}
		jobNameBuffer.append(exp.getCaseDescription(expCase) + " " + runNumber);
		jt.setJobName(jobNameBuffer.toString());
		
		String caseDir = exp.getDirectoryForCase(expCase);
		
		// If we're using a shared filesystem, set working directory appropriately
		if(preferences.getBooleanProperty("UseSharedFileSystem"))
		{
			jt.setWorkingDirectory(caseDir);
		}
		
		// Set stdin to data generated in setupFileSystem
		jt.setInputPath(caseDir + "/.gridsweeper.in." + runNumber);
		jt.setOutputPath(exp.getDirectoryForCase(expCase) + "/.gridsweeper.out." + runNumber);
		jt.setTransferFiles(new FileTransferMode(true, true, false));

		Logger.exiting(className, "getJobTemplate");
		
		return jt;
	}

	private byte[] getStdinData(String stdinPath)
	{
		if(stdinPath == null) return null;
		
		Logger.entering(className, "getStdinData");
		
		byte[] stdinData;
		
		try
		{
			FileInputStream inputStream = new FileInputStream(stdinPath);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			int b;
			while((b = inputStream.read()) != -1)
			{
				outputStream.write(b);
			}
			
			inputStream.close();
			outputStream.close();
			
			stdinData = outputStream.toByteArray();
		}
		catch(Exception e)
		{
			stdinData = null;
		}
		
		Logger.exiting(className, "getStdinData");
		
		return stdinData;
	}
}
