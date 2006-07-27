package com.edbaskerville.gridsweeper;

public interface ExperimentTypeManager
{
	public java.util.Map<String, String> fileUploads(Experiment experiment);
	public void runCase(ExperimentCase experimentCase, boolean dryRun);
	
	public java.util.Map<String, String> interpretArgs(java.util.List<String> args);
}
