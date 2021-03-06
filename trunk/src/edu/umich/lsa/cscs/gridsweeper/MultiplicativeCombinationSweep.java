/*
	MultipleCombinationSweep.java
	
	Part of GridSweeper
	Copyright (c) 2006 - 2007 Ed Baskerville <software@edbaskerville.com>

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package edu.umich.lsa.cscs.gridsweeper;

import java.util.*;


/**
 * Represents a combination of sub-sweeps, where all combinations of children
 * parameter/value assignments are generated. The only restriction on children
 * is that parameters are not used by multiple children.
 * @author Ed Baskerville
 *
 */
class MultiplicativeCombinationSweep extends CombinationSweep
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
	 * @see edu.umich.lsa.cscs.gridsweeper.CombinationSweep#generateMaps(java.util.Random)
	 */
	@Override
	public List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException
	{
		return generateMaps(false);
	}
	
	public List<ParameterMap> generateMaps(boolean allowOverrides) throws SweepLengthException, DuplicateParameterException
	{
		if(children.size() == 0) return new ArrayList<ParameterMap>(0);
		
		// If overrides are allowed (i.e., this is the root sweep of an experiment),
		// the parameter maps are generated from all values except the
		// SingleValueSweep children. Then, values for any parameters not already
		// used are loaded from the SingleValueSweeps.
		if(allowOverrides)
		{
			List<SingleValueSweep> baseChildren = new ArrayList<SingleValueSweep>();
			List<Sweep> otherChildren = new ArrayList<Sweep>();
			
			for(Sweep child : children)
			{
				if(child instanceof SingleValueSweep)
					baseChildren.add((SingleValueSweep)child);
				else
					otherChildren.add(child);
			}
			
			if(otherChildren.size() > 0)
			{
				List<ParameterMap> maps = generateMaps(otherChildren);
				assert(maps.size() > 0);
				ParameterMap firstMap = maps.get(0);
				
				for(SingleValueSweep baseValue : baseChildren)
				{
					String name = baseValue.getName();
					if(!firstMap.containsKey(name))
					{
						for(ParameterMap map : maps)
						{
							map.put(name, baseValue.getValue());
						}
					}
				}
				
				return maps;
			}
			else
			{
				ParameterMap map = new ParameterMap();
				
				for(SingleValueSweep baseValue : baseChildren)
				{
					map.put(baseValue.getName(), baseValue.getValue());
				}
				
				List<ParameterMap> maps = new ArrayList<ParameterMap>();
				maps.add(map);
				return maps;
			}
		}
		else return generateMaps(children);
	}

	/**
	 * Recursively generates maps from the children that have not yet been combined.
	 * It should be possible (?) to implement this method without recursion as an optimization.
	 * @param children The children left to combine
	 * @return A list of {@code ParameterMap} objects with the partial combinations.
	 * @throws SweepLengthException
	 * @throws DuplicateParameterException
	 */
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

	public void writeXML(XMLWriter xmlWriter)
	{
		xmlWriter.printTagStart("multiplicative", null, false);
		
		for(Sweep sweep : children)
		{
			sweep.writeXML(xmlWriter);
		}
		
		xmlWriter.printTagEnd("multiplicative");
	}
}
