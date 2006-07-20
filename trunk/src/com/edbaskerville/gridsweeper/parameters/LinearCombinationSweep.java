package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

public class LinearCombinationSweep extends CombinationSweep
{
	public LinearCombinationSweep()
	{
		super();
	}

	public LinearCombinationSweep(List<Sweep> children)
	{
		super(children);
	}

	@Override
	public List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException
	{
		int numChildren = children.size();
		
		if(numChildren == 0) return new ArrayList<ParameterMap>(0);
		
		// Assemble childrens' maps
		int length = -1;
		List<List<ParameterMap>> childMapses = new ArrayList<List<ParameterMap>>();
		for(Sweep child : children)
		{
			List<ParameterMap> childMaps = child.generateMaps();
			
			// Verify length
			if(length == -1) length = childMaps.size();
			else if(childMaps.size() != length) throw new SweepLengthException();
			
			childMapses.add(childMaps);
		}
		
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		
		// Iterate from beginning to end. Combine the maps from each child
		// at each index into one map for that index.
		// If there's ever a duplicate value for a parameter, throw an exception
		for(int i = 0; i < length; i++)
		{
			ParameterMap map = new ParameterMap();
			for(List<ParameterMap> childMaps : childMapses)
			{
				ParameterMap childMap = childMaps.get(i);
				for(String name : childMap.keySet())
				{
					if(map.containsKey(name))
					{
						throw new DuplicateParameterException(name);
					}
					
					map.put(name, childMap.get(name));
				}
			}
			maps.add(map);
		}
		
		return maps;
	}
}
