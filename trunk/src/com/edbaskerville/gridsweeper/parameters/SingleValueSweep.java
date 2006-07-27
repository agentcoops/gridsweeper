package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

public class SingleValueSweep extends SingleSweep
{
	private String value;
	
	public SingleValueSweep(String name, String value)
	{
		super(name);
		this.value = value;
	}

	@Override
	public List<ParameterMap> generateMaps(Random rng)
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		maps.add(new ParameterMap(name, value));
		
		return maps;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
