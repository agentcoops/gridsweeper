package com.edbaskerville.gridsweeper;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public interface Adapter 
{
	public RunResults run(ParameterMap parameterMap, int runNumber, long rngSeed, boolean dryRun) throws AdapterException;
}
