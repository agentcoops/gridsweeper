package com.edbaskerville.gridsweeper;

public interface Adapter 
{
	public java.util.Map<String, String> getInputFiles(Experiment experiment);
	public java.util.Map<String, String> getInputFiles(ExperimentCase experimentCase);
	
	public RunResults run(RunSetup setup) throws AdapterException;
}
