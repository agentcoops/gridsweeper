package com.edbaskerville.gridsweeper.parameters;

import java.util.List;
import java.util.Random;

public abstract class StochasticSweep extends SingleSweep
{
	protected Random rng;
	
	public StochasticSweep(String name, Random rng)
	{
		super(name);
		this.rng = rng;
	}
	
	public abstract List<ParameterMap> generateMaps();
}
