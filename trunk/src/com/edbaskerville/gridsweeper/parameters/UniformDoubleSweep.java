package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

/**
 * Represents the assignment of some number of values to a parameter from a
 * uniform distribution of type double.
 * @author Ed Baskerville
 *
 */
public class UniformDoubleSweep extends SingleSweep
{
	/**
	 * The lower bound of the uniform distribution.
	 */
	double start;
	
	/**
	 * The upper bound of the uniform distribution.
	 */
	double end;
	
	/**
	 * The number of values to draw from the distribution.
	 */
	int count;
	
	/**
	 * Initializes the parameter name and uniform distribution.
	 * @param name The parameter name to use.
	 * @param start The lower bound of the distribution.
	 * @param end The upper bound of the distribution.
	 * @param count The number of values to draw from the distribution.
	 */
	public UniformDoubleSweep(String name, double start, double end, int count)
	{
		super(name);
		
		if(count < 0) throw new IllegalArgumentException("count cannot be negative");
		
		this.start = start;
		this.end = end;
		this.count = count;
	}

	/**
	 * Generates {@code count} value assignments from the specified uniform distribution.
	 */
	@Override
	public List<ParameterMap> generateMaps(Random rng, int numRuns)
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>(count);
		for(int i = 0; i < count; i++)
		{
			ArrayList<Double> valueList = new ArrayList<Double>(numRuns);
			for(int j = 0; j < numRuns; j++)
			{
				valueList.add(uniformDouble(rng));
			}
			maps.add(new ParameterMap(name, valueList));
		}
		return maps;
	}
	
	/**
	 * Generates a single number from the uniform distribution.
	 * @param rng The random number generator to use.
	 * @return A random number from the uniform distribution between
	 * {@code start} and {@code end}.
	 */
	private Double uniformDouble(Random rng)
	{
		return new Double(start + rng.nextDouble() * (end - start));
	}
}
