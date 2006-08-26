package com.edbaskerville.gridsweeper;

import org.ggf.drmaa.DrmaaException;

public class GridSweeper
{
	enum ArgState
	{
		START,
		ADAPTER,
		EXPERIMENT
	}
	
	static Experiment experiment;
	static GridDelegate gridDelegate;
	static int numFailedRuns;
	
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
	}
	
	public static void main(String[] args)
	{
		String adapterClassName = null;
		String experimentFile = null;
		
		ArgState state = ArgState.START;
		
		// Parse args
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
					experimentFile = arg;
					state = ArgState.START;
					break;
			}
		}
		
		// Load experiment
		if(experimentFile == null) exit("Experiment file must be provided.");
		try
		{
			experiment = new Experiment(new java.net.URL("file://" + experimentFile));
		}
		catch(Exception e)
		{
			exit("Could not load experiment file.");
		}
		
		// Submit runs and wait for completion.
		GridController controller = new GridController(gridDelegate);
		try
		{
			controller.connect();
			controller.submitExperiment(experiment, adapterClassName, true);
			controller.disconnect();
		}
		catch(DrmaaException e)
		{
			System.out.println("Failed due to a DrmaaException:");
			e.printStackTrace();
		}
	}
	
	public static void exit(String message)
	{
		System.err.println(message);
		System.exit(1);
	}
}
