/*
	ParallelCombinationSweepTest.java
	
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

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

public class ParallelCombinationSweepTest
{
	private ParallelCombinationSweep sweep;
	
	@Before
	public void setUp()
	{
		sweep = new ParallelCombinationSweep();
	}
	
	@Test
	public void emptySweep() throws SweepLengthException, DuplicateParameterException
	{
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 0);
	}
	
	@Test
	public void oneEmptyListSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep listSweep = new ListSweep("param");
		sweep.add(listSweep);
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 0);
	}
	
	@Test
	public void oneListSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep listSweep = new ListSweep("param");
		listSweep.add("hello");
		listSweep.add("there");
		sweep.add(listSweep);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 2);
		assertEquals(maps.get(0).get("param"), "hello");
		assertEquals(maps.get(1).get("param"), "there");
	}
	
	@Test
	public void twoListSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep listSweep1 = new ListSweep("beta");
		listSweep1.add("0.1");
		listSweep1.add("0.2");
		sweep.add(listSweep1);
		
		ListSweep listSweep2 = new ListSweep("gamma");
		listSweep2.add("0.9");
		listSweep2.add("0.4");
		sweep.add(listSweep2);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(2, maps.size());
		assertEquals(maps.get(0).get("beta"), "0.1");
		assertEquals(maps.get(1).get("beta"), "0.2");
		assertEquals(maps.get(0).get("gamma"), "0.9");
		assertEquals(maps.get(1).get("gamma"), "0.4");
	}
	
	@Test
	public void duplicateParameterSweep() throws SweepLengthException
	{
		ListSweep listSweep = new ListSweep("beta");
		listSweep.add("0.1");
		sweep.add(listSweep);
		sweep.add(listSweep);
		
		try
		{
			@SuppressWarnings("unused")
			List<ParameterMap> maps = sweep.generateMaps();
			fail();
		}
		catch(DuplicateParameterException e) {}
	}
	
	@Test
	public void mismatchedLengthSweep() throws DuplicateParameterException
	{
		ListSweep listSweep1 = new ListSweep("beta");
		listSweep1.add("0.1");
		sweep.add(listSweep1);
		
		ListSweep listSweep2 = new ListSweep("gamma");
		listSweep2.add("0.1");
		listSweep2.add("0.2");
		sweep.add(listSweep2);
		
		try
		{
			@SuppressWarnings("unused")
			List<ParameterMap> maps = sweep.generateMaps();
			fail();
		}
		catch(SweepLengthException e) {}
	}
}
