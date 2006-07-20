package com.edbaskerville.gridsweeper.parameters;

import java.util.List;

public abstract class SingleSweep implements Sweep
{
	protected String name;
	
	public SingleSweep(String name)
	{
		super();
		this.name = name;
	}

	public abstract List<ParameterMap> generateMaps();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
