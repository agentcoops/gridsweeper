package edu.umich.lsa.cscs.gridsweeper.parameters;

import java.util.List;

/**
 * An abstract class representing a sweep that deals with a single parameter.
 * @author Ed Baskerville
 *
 */
public abstract class SingleSweep implements Sweep
{
	/**
	 * The parameter name used by this sweep.
	 */
	protected String name;
	
	/**
	 * Constructor for initializing the name, usable by subclasses.
	 */
	public SingleSweep(String name)
	{
		super();
		this.name = name;
	}

	public abstract List<ParameterMap> generateMaps();

	/**
	 * Returns the parameter name used by this sweep.
	 * @return The parameter name used by this sweep.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the parameter name used by this sweep.
	 * @param name The parameter name to use.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
