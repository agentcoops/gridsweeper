/*
	SingleValueSweep.java
	
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
 * Represents the most primitive concrete sweep: a single value assigned to a parameter.
 * @author Ed Baskerville
 *
 */
class SingleValueSweep extends SingleSweep
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
	public List<ParameterMap> generateMaps()
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

	public void writeXML(XMLWriter xmlWriter)
	{
		StringMap attrs = new StringMap();
		attrs.put("param", getName());
		attrs.put("value", getValue());
		
		xmlWriter.printTagStart("value", attrs, true);
	}
}
