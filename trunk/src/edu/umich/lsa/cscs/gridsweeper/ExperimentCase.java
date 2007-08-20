/*
	ExperimentCase.java
	
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

import edu.umich.lsa.cscs.gridsweeper.parameters.*;

/**
 * Represents a single case of an experiment, including parameter settings
 * and a list of random seeds, one for each run of the case.
 * @author Ed Baskerville
 *
 */
public class ExperimentCase
{
	ParameterMap parameterMap;
	List<Integer> rngSeeds;
	
	/**
	 * Initializes the experiment case with the parameter settings and random seeds.
	 * @param parameterMap The parameter assignments.
	 * @param rngSeeds The random seeds.
	 */
	public ExperimentCase(ParameterMap parameterMap, List<Integer> rngSeeds)
	{
		this.parameterMap = parameterMap;
		this.rngSeeds = rngSeeds;
	}

	/**
	 * Getter for the parameter assignments for this case.
	 * @return The parameter assignments for this case.
	 */
	public ParameterMap getParameterMap()
	{
		return parameterMap;
	}

	/**
	 * Getter for the list of random seeds.
	 * @return The list of random seeds.
	 */
	public List<Integer> getRngSeeds()
	{
		return rngSeeds;
	}
}
