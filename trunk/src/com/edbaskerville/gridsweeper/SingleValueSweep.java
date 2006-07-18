package com.edbaskerville.gridsweeper;

import java.util.*;

public class SingleValueSweep<T> extends SingleSweep
{
	private T value;
	
	public SingleValueSweep(String name, T value)
	{
		super(name);
		this.value = value;
	}

	@Override
	public List<ParameterMap> generateMaps()
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		maps.add(new ParameterMap(name, value));
		
		return maps;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}
}
