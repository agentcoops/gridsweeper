package com.edbaskerville.gridsweeper;

import java.util.*;
import com.edbaskerville.gridsweeper.parameters.*;

public class ExperimentCase
{
	ParameterMap parameterMap;
	List<Long> rngSeeds;
	
	public ExperimentCase(ParameterMap parameterMap, List<Long> rngSeeds)
	{
		this.parameterMap = parameterMap;
		this.rngSeeds = rngSeeds;
	}

	public ParameterMap getParameterMap()
	{
		return parameterMap;
	}

	public List<Long> getRngSeeds()
	{
		return rngSeeds;
	}
}
