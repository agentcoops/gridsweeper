/*
	Sweep.java
	
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
 * The root interface for parameter sweeps. Defines one method, <code>generateMaps()</code>,
 * which generates parameter name/value maps from parameter sweep settings.
 * @author Ed Baskerville 
 */
public interface Sweep
{
	/**
	 * Generates an enumerated list of {@link ParameterMap} objects, each of which
	 * maps parameters to values. The resulting maps can be passed on to model runs through
	 * {@link edu.umich.lsa.cscs.gridsweeper.Adapter} objects.
	 * @return A list of {@code ParameterMap} objects, one for each set of parameter/value
	 * assignments.
	 * @throws SweepLengthException If a length requirement&mdash;e.g., child sweeps
	 * must be the same length&mdash;is violated. 
	 * @throws DuplicateParameterException If child sweeps generate duplicate/conflicting
	 * settings for a parameter.
	 */
	public List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException;
	
	public void writeXML(XMLWriter xmlWriter);
}
