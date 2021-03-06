/*
	GridSweeperToolTest.java
	
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

import java.math.BigDecimal;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class GridSweeperToolTest
{
	@Test
	public void parseSingleValue() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		SingleValueSweep sweep = (SingleValueSweep)tool.parseSweepArg("r=0.5");
		assertTrue(sweep instanceof SingleValueSweep);
		assertEquals("r", sweep.getName());
		assertEquals("0.5", sweep.getValue());
	}
	
	@Test
	public void parseSingleValueWS() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		SingleValueSweep sweep = (SingleValueSweep)tool.parseSweepArg(" r  =  0.5 ");
		assertTrue(sweep instanceof SingleValueSweep);
		assertEquals("r", sweep.getName());
		assertEquals("0.5", sweep.getValue());
	}
	
	@Test
	public void parseList() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		ListSweep sweep = (ListSweep)tool.parseSweepArg("r=0.1,0.2,0.3");
		assertTrue(sweep instanceof ListSweep);
		assertEquals("r", sweep.getName());
		StringList values = sweep.getValues();
		assertEquals(3, values.size());
		assertEquals("0.1", values.get(0));
		assertEquals("0.2", values.get(1));
		assertEquals("0.3", values.get(2));
	}
	
	@Test
	public void parseListWS() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		ListSweep sweep = (ListSweep)tool.parseSweepArg(" r = 0.1,0.2, 0.3 ");
		assertTrue(sweep instanceof ListSweep);
		assertEquals("r", sweep.getName());
		StringList values = sweep.getValues();
		assertEquals(3, values.size());
		assertEquals("0.1", values.get(0));
		assertEquals("0.2", values.get(1));
		assertEquals("0.3", values.get(2));
	}
	
	@Test
	public void parseRange() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		RangeListSweep sweep = (RangeListSweep)tool.parseSweepArg("r=0.1:0.2:1.0");
		assertTrue(sweep instanceof RangeListSweep);
		assertEquals("r", sweep.getName());
		assertEquals(new BigDecimal("0.1"), sweep.getStart());
		assertEquals(new BigDecimal("0.2"), sweep.getIncrement());
		assertEquals(new BigDecimal("1.0"), sweep.getEnd());
	}
	
	@Test
	public void parseRangeWS() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		RangeListSweep sweep = (RangeListSweep)tool.parseSweepArg("r = 0.1 : 0.2 : 1.0");
		assertTrue(sweep instanceof RangeListSweep);
		assertEquals("r", sweep.getName());
		assertEquals(new BigDecimal("0.1"), sweep.getStart());
		assertEquals(new BigDecimal("0.2"), sweep.getIncrement());
		assertEquals(new BigDecimal("1.0"), sweep.getEnd());
	}
	
	@Test
	public void parseParallelRange() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		ParallelCombinationSweep sweep = 
			(ParallelCombinationSweep)tool.parseSweepArg("r s = 0.1:0.2:1.0 0.2:0.4:2.0");
		
		List<Sweep> children = sweep.getChildren();
		assertEquals(2, children.size());
		
		RangeListSweep child0 = (RangeListSweep)children.get(0);
		assertTrue(child0 instanceof RangeListSweep);
		assertEquals("r", child0.getName());
		assertEquals(new BigDecimal("0.1"), child0.getStart());
		assertEquals(new BigDecimal("0.2"), child0.getIncrement());
		assertEquals(new BigDecimal("1.0"), child0.getEnd());
		
		RangeListSweep child1 = (RangeListSweep)children.get(1);
		assertTrue(child1 instanceof RangeListSweep);
		assertEquals("s", child1.getName());
		assertEquals(new BigDecimal("0.2"), child1.getStart());
		assertEquals(new BigDecimal("0.4"), child1.getIncrement());
		assertEquals(new BigDecimal("2.0"), child1.getEnd());
	}
	
	@Test
	public void parseParallelListForm1() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		ParallelCombinationSweep sweep =
			(ParallelCombinationSweep)tool.parseSweepArg("r s = 0.1 0.2, 0.3 0.4, 0.4 0.5, 0.5 0.6");
		assertTrue(sweep instanceof ParallelCombinationSweep);
		
		List<Sweep> children = sweep.getChildren();
		assertEquals(2, children.size());
		
		ListSweep child0 = (ListSweep)children.get(0);
		assertTrue(child0 instanceof ListSweep);
		StringList values0 = child0.getValues();
		assertEquals(4, values0.size());
		assertEquals("0.1", values0.get(0));
		assertEquals("0.3", values0.get(1));
		assertEquals("0.4", values0.get(2));
		assertEquals("0.5", values0.get(3));
		
		ListSweep child1 = (ListSweep)children.get(1);
		assertTrue(child1 instanceof ListSweep);
		StringList values1 = child1.getValues();
		assertEquals(4, values1.size());
		assertEquals("0.2", values1.get(0));
		assertEquals("0.4", values1.get(1));
		assertEquals("0.5", values1.get(2));
		assertEquals("0.6", values1.get(3));
	}
	
	@Test
	public void parseParallelListForm2() throws GridSweeperException
	{
		GridSweeperTool tool = new GridSweeperTool();
		ParallelCombinationSweep sweep =
			(ParallelCombinationSweep)tool.parseSweepArg("r s = 0.1, 0.3, 0.4, 0.5 0.2, 0.4, 0.5, 0.6");
		assertTrue(sweep instanceof ParallelCombinationSweep);
		
		List<Sweep> children = sweep.getChildren();
		assertEquals(2, children.size());
		
		ListSweep child0 = (ListSweep)children.get(0);
		assertTrue(child0 instanceof ListSweep);
		StringList values0 = child0.getValues();
		assertEquals(4, values0.size());
		assertEquals("0.1", values0.get(0));
		assertEquals("0.3", values0.get(1));
		assertEquals("0.4", values0.get(2));
		assertEquals("0.5", values0.get(3));
		
		ListSweep child1 = (ListSweep)children.get(1);
		assertTrue(child1 instanceof ListSweep);
		StringList values1 = child1.getValues();
		assertEquals(4, values1.size());
		assertEquals("0.2", values1.get(0));
		assertEquals("0.4", values1.get(1));
		assertEquals("0.5", values1.get(2));
		assertEquals("0.6", values1.get(3));
	}
}
