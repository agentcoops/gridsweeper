/*
	ParallelCombinationSweep.java
	
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

package edu.umich.lsa.cscs.gridsweeper.parameters;

import java.util.*;

import edu.umich.lsa.cscs.gridsweeper.XMLWriter;

/**
 * Represents a combination of sub-sweeps that are enumerated in parallel.
 * The list of parameter maps is generated by combining the parameter maps of sub-sweeps
 * at each index. That is, the first map will be a combination of the first map of each
 * child; the second map will be a combination of the second map of each child; and so on.
 * Each child must generate the same number of parameter maps, and no parameter can appear
 * in multiple children.
 * @author Ed Baskerville
 *
 */
public class ParallelCombinationSweep extends CombinationSweep
{
	public ParallelCombinationSweep()
	{
		super();
	}

	public ParallelCombinationSweep(List<Sweep> children)
	{
		super(children);
	}

	/**
	 * Generates parameter maps using a parallel combination of children.
	 * For example, if there are two children, one that sweeps <em>alpha</em>
	 * from 0 to 1 in increments of 0.2, and one that sweeps <em>beta</em>
	 * from 0 to 0.1 in increments of 0.02, this method will combine the assignments
	 * in parallel, producing six parameter/value assignments:
	 * 
	 * <ul>
	 * <li><em>alpha</em> = 0, <em>beta</em> = 0</li>
	 * <li><em>alpha</em> = 0.2, <em>beta</em> = 0.02</li>
	 * <li><em>alpha</em> = 0.4, <em>beta</em> = 0.04</li>
	 * <li><em>alpha</em> = 0.6, <em>beta</em> = 0.06</li>
	 * <li><em>alpha</em> = 0.8, <em>beta</em> = 0.08</li>
	 * <li><em>alpha</em> = 1, <em>beta</em> = 0.1</li>
	 * </ul>
	 * @return A list of {@link ParameterMap} objects, one for each combination
	 * of child parameter maps
	 * @see edu.umich.lsa.cscs.gridsweeper.parameters.CombinationSweep#generateMaps(java.util.Random)
	 */
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

	public void writeXML(XMLWriter xmlWriter)
	{
		xmlWriter.printTagStart("parallel", null, false);
		
		for(Sweep sweep : children)
		{
			sweep.writeXML(xmlWriter);
		}
		
		xmlWriter.printTagEnd("parallel");
	}
}
