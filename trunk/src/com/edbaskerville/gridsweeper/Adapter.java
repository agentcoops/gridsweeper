package com.edbaskerville.gridsweeper;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public interface Adapter 
{
	public RunResults run(ParameterMap parameters, long rngSeed) throws AdapterException;
}
