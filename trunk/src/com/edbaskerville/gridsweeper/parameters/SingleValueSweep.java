package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

/**
 * Represents the most primitive concrete sweep: a single value assigned to a parameter.
 * @author Ed Baskerville
 *
 */
public class SingleValueSweep extends SingleSweep
{
	/**
	 * The value of the parameter.
	 */
	private String value;
	
	/**
	 * Initializes the parameter name and value.
	 * @param name The parameter name to use.
	 * @param value The value to assign.
	 */
	public SingleValueSweep(String name, String value)
	{
		super(name);
		this.value = value;
	}

	/**
	 * Generates the primitive single-value list of maps. The list contains one map,
	 * and the map contains one entry, assigning the specified value to the specified
	 * parameter name.
	 * @return The one-item list of a one-entry map.
	 */
	@Override
	public List<ParameterMap> generateMaps(Random rng, int numRuns)
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		maps.add(new ParameterMap(name, value));
		
		return maps;
	}

	/**
	 * Returns the value assigned to the parameter.
	 * @return The value assigned.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the parameter value.
	 * @param value The value to assign.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
}
