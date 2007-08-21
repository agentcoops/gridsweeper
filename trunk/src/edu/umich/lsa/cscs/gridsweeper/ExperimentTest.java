/*
	ExperimentTest.java
	
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

import org.junit.*;

import static org.junit.Assert.*;

class ExperimentTest
{
	Experiment experiment;
	
	@Before
	public void setUp() throws Exception
	{
		experiment = new Experiment(null);
		experiment.setNumRuns(10);
		experiment.getRootSweep().add(new SingleValueSweep("param", "1"));
	}
	
	@Test
	public void generateCases() throws GridSweeperException
	{
		List<ExperimentCase> cases = experiment.generateCases();
		
		assertEquals(1, cases.size());
		
		ExperimentCase expCase = cases.get(0);
		
		ParameterMap map = expCase.getParameterMap();
		assertEquals(1, map.size());
		assertEquals("1", map.get("param"));
		
		assertEquals(10, expCase.getRngSeeds().size());
	}
}
