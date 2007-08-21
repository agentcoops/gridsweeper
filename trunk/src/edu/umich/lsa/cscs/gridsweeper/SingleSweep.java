/*
	SingleSweep.java
	
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

import java.util.List;

/**
 * An abstract class representing a sweep that deals with a single parameter.
 * @author Ed Baskerville
 *
 */
public abstract class SingleSweep implements Sweep
{
	/**
	 * The parameter name used by this sweep.
	 */
	protected String name;
	
	/**
	 * Constructor for initializing the name, usable by subclasses.
	 */
	public SingleSweep(String name)
	{
		super();
		this.name = name;
	}

	public abstract List<ParameterMap> generateMaps();

	/**
	 * Returns the parameter name used by this sweep.
	 * @return The parameter name used by this sweep.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the parameter name used by this sweep.
	 * @param name The parameter name to use.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
