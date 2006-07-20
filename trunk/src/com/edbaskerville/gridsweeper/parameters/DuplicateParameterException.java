package com.edbaskerville.gridsweeper.parameters;

public class DuplicateParameterException extends Exception
{
	private String name;
	
	public DuplicateParameterException(String name)
	{
		super();
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	private static final long serialVersionUID = 1L;
}
