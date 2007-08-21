/*
	ParameterMap.java
	
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

import java.util.HashMap;
import java.util.Map;

/**
 * A subclass of {@code HashMap<String, Object>} created to avoid typing {@code HashMap<String, Object>}.
 * @author Ed Baskerville
 *
 */
public class ParameterMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;

	public ParameterMap()
	{
		super();
	}

	public ParameterMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public ParameterMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public ParameterMap(Map<? extends String, ? extends Object> m)
	{
		super(m);
	}
	
	public ParameterMap(String name, Object value)
	{
		super(1, 1);
		put(name, value);
	}
}
