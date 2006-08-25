package com.edbaskerville.gridsweeper;

public class GridSweeper
{
	enum ArgState
	{
		START,
		ADAPTER,
		EXPERIMENT
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
		Experiment experiment = null;
		try
		{
			experiment = new Experiment(new java.net.URL("file://" + experimentFile));
		}
		catch(Exception e)
		{
			exit("Could not load experiment file.");
		}
		
		// Submit runs and wait for completion.
		GridController controller = new GridController();
		controller.submitExperiment(experiment, adapterClassName, true);  
	}
	
	public static void exit(String message)
	{
		System.err.println(message);
		System.exit(1);
	}
}
