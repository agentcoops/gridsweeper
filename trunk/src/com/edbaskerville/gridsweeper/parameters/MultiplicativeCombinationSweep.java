package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

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

	@Override
	public List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException
	{
		if(children.size() == 0) return new ArrayList<ParameterMap>(0);
		
		return generateMaps(children);
	}
	
	private List<ParameterMap> generateMaps(List<Sweep> children) throws SweepLengthException, DuplicateParameterException
	{
		assert children.size() > 0;
		
		List<ParameterMap> firstChildMaps = children.get(0).generateMaps();
		
		// Termination condition: for a single sweep, just return its generated maps
		if(children.size() == 1) return firstChildMaps;
		
		// Recursion: combine the first element of the list with this function
		// called on the remaining elements
		return combineMaps(firstChildMaps, generateMaps(children.subList(1, children.size())));
	}
	
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
