/*
	ListSweep.java
	
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

import edu.umich.lsa.cscs.gridsweeper.*;

import java.util.*;

/**
 * Represents a 
 * @author ebaskerv
 *
 */
public class ListSweep extends SingleSweep
{
	private StringList values;
	
	public StringList getValues()
	{
		return values;
	}

	public void setValues(StringList values)
	{
		this.values = values;
	}

	public ListSweep(String name, StringList values)
	{
		super(name);
		this.values = values;
	}
	
	public ListSweep(String name)
	{
		super(name);
		this.values = new StringList();
	}
	
	@Override
	public List<ParameterMap> generateMaps()
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		for(String value : values)
		{
			maps.add(new ParameterMap(name, value));
		}
		
		return maps;
	}

	public boolean add(String o)
	{
		return values.add(o);
	}

	public void writeXML(XMLWriter xmlWriter)
	{
		StringMap attrs = new StringMap();
		attrs.put("param", getName());
		xmlWriter.printTagStart("list", attrs, false);
		
		for(String value : values)
		{
			StringMap itemAttrs = new StringMap();
			itemAttrs.put("value", value);
			xmlWriter.printTagStart("item", itemAttrs, true); 
		}
		
		xmlWriter.printTagEnd("list");
	}
}
