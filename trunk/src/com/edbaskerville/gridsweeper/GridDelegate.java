package com.edbaskerville.gridsweeper;

public interface GridDelegate
{
	public void runCompleted(ExperimentCase experimentCase, int runNumber);
	public void runFailed(ExperimentCase experimentCase, int runNumber);
	
	public void batchCompleted();
	public void batchFailed(Exception e);
}
