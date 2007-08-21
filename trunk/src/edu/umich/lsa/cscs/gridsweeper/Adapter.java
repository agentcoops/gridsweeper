/*
	Adapter.java
	
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


/**
 * The interface for adapters, which execute a model from a
 * set of parameters and return results to the GridSweeperRunner object.
 * Java interfaces cannot specify constructors, but <code>Adapter</code>
 * objects are assumed to support a single-argument constructor with signature
 * {@code Adapter(java.util.Properties properties)}.
 * @author Ed Baskerville
 *
 */
public interface Adapter 
{
	/**
	 * Executes a model and returns results. 
	 * @param parameterMap The parameter assignments for this run.
	 * @param runNumber A number that identifies the run among multiple
	 * runs with the same parameter settings. This number may be used
	 * to name output files, for example.
	 * @param rngSeed The seed for the model's random number generator.
	 * @return A {@link RunResults} object containing the process exit status,
	 * a string message describing the run, and data from standard input
	 * and standard error.
	 * @throws AdapterException For any reason defined by the implementing class.
	 */
	public RunResults run(ParameterMap parameterMap, int runNumber, int rngSeed) throws AdapterException;
}
