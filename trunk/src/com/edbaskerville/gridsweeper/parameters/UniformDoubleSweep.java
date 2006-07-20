package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

public class UniformDoubleSweep extends StochasticSweep
{
	double start;
	double end;
	int count;
	
	public UniformDoubleSweep(String name, Random rng, double start, double end, int count)
	{
		super(name, rng);
		
		if(count < 0) throw new IllegalArgumentException("count cannot be negative");
		
		this.start = start;
		this.end = end;
		this.count = count;
	}

	@Override
	public List<ParameterMap> generateMaps()
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>(count);
		for(int i = 0; i < count; i++)
		{
			maps.add(new ParameterMap(name, uniformDouble()));
		}
		return maps;
	}
	
	private Double uniformDouble()
	{
		return new Double(start + rng.nextDouble() * (end - start));
	}
}
