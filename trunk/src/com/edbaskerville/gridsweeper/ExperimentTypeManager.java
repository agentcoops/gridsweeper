package com.edbaskerville.gridsweeper;

public interface ExperimentTypeManager
{
	public java.util.Properties fileUploads(Experiment experiment);
	public void runCase(ExperimentCase experimentCase, boolean dryRun);
	
	public java.util.Properties interpretArgs(java.util.List<String> args);
}
