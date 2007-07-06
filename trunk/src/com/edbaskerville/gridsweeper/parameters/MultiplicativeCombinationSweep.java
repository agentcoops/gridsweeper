package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

/**
 * Represents a combination of sub-sweeps, where all combinations of children
 * parameter/value assignments are generated. The only restriction on children
 * is that parameters are not used by multiple children.
 * @author Ed Baskerville
 *
 */
public class MultiplicativeCombinationSweep extends CombinationSweep
{

	public MultiplicativeCombinationSweep(List<Sweep> children)
	{
		super(children);
	}

	public MultiplicativeCombinationSweep()
	{
		super();
	}
	
	/**
	 * Generates parameter maps from all combinations of child parameter maps. For example,
	 * if there are two children, one that sweeps <em>alpha</em>
	 * from 0 to 1 in increments of 0.5, and one that sets beta to 0 and 0.01,
	 * there will be six combinations:
	 * 
	 * <ul>
	 * <li><em>alpha</em> = 0, <em>beta</em> = 0</li>
	 * <li><em>alpha</em> = 0, <em>beta</em> = 0.01</li>
	 * <li><em>alpha</em> = 0.5, <em>beta</em> = 0</li>
	 * <li><em>alpha</em> = 0.5, <em>beta</em> = 0.01</li>
	 * <li><em>alpha</em> = 1, <em>beta</em> = 0</li>
	 * <li><em>alpha</em> = 1, <em>beta</em> = 0.01</li>
	 * </ul>
	 * 
	 * @return A list of {@link ParameterMap} objects, one for each combination
	 * of child parameter maps.
	 * @see com.edbaskerville.gridsweeper.parameters.CombinationSweep#generateMaps(java.util.Random)
	 */
	@Override
	public List<ParameterMap> generateMaps(Random rng) throws SweepLengthException, DuplicateParameterException
	{
		if(children.size() == 0) return new ArrayList<ParameterMap>(0);
		
		return generateMaps(children, rng);
	}
	
	private List<ParameterMap> generateMaps(List<Sweep> children, Random rng) throws SweepLengthException, DuplicateParameterException
	/**
	 * Recursively generates maps from the children that have not yet been combined.
	 * It should be possible (?) to implement this method without recursion as an optimization.
	 * @param children The children left to combine
	 * @param rng The random number generator to use (for stochastic children).
	 * @return A list of {@code ParameterMap} objects with the partial combinations.
	 * @throws SweepLengthException
	 * @throws DuplicateParameterException
	 */
	{
		assert children.size() > 0;
		
		List<ParameterMap> firstChildMaps = children.get(0).generateMaps(rng);
		
		// Termination condition: for a single sweep, just return its generated maps
		if(children.size() == 1) return firstChildMaps;
		
		// Recursion: combine the first element of the list with this function
		// called on the remaining elements
		return combineMaps(firstChildMaps, generateMaps(children.subList(1, children.size()), rng));
	}
	
	/**
	 * Combines two lists of parameter maps to create a new list consisting of all
	 * the possible combinations of maps. 
	 * @param firstMaps One list of maps.
	 * @param secondMaps The other list of maps.
	 * @return A list of combined maps.
	 * @throws DuplicateParameterException
	 */
	private List<ParameterMap> combineMaps(List<ParameterMap> firstMaps, List<ParameterMap> secondMaps) throws DuplicateParameterException
	{
		List<ParameterMap> combinedMaps = new ArrayList<ParameterMap>(firstMaps.size() * secondMaps.size());
		
		// Put each combination of parameters into the combined maps
		for(ParameterMap firstMap : firstMaps)
		for(ParameterMap secondMap : secondMaps)
		{
			ParameterMap combinedMap = new ParameterMap(firstMap);
			for(String name : secondMap.keySet())
			{
				if(combinedMap.containsKey(name))
				{
					throw new DuplicateParameterException(name);
				}
				combinedMap.put(name, secondMap.get(name));
			}
			combinedMaps.add(combinedMap);
		}
		
		return combinedMaps;
	}
}
