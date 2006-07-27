package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

public class UniformDoubleSweep extends SingleSweep
{
	double start;
	double end;
	int count;
	
	public UniformDoubleSweep(String name, double start, double end, int count)
	{
		super(name);
		
		if(count < 0) throw new IllegalArgumentException("count cannot be negative");
		
		this.start = start;
		this.end = end;
		this.count = count;
	}

	@Override
	public List<ParameterMap> generateMaps(Random rng)
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>(count);
		for(int i = 0; i < count; i++)
		{
			maps.add(new ParameterMap(name, uniformDouble(rng)));
		}
		return maps;
	}
	
	private Double uniformDouble(Random rng)
	{
		return new Double(start + rng.nextDouble() * (end - start));
	}
}
