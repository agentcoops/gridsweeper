/*
	RangeListSweepTest.java
	
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

import java.math.*;
import java.util.*;

public class RangeListSweepTest
{
	@Test
	public void singleSweep()
	{
		BigDecimal start = new BigDecimal("0.1234581111111111111111111111111111111");
		BigDecimal end = new BigDecimal("0.1234581111111111111111111111111111111");
		BigDecimal increment = new BigDecimal("1");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).get("param"), start); 
	}
	
	@Test
	public void singleSweepZeroIncrement()
	{
		BigDecimal start = new BigDecimal("0.1234581111111111111111111111111111111");
		BigDecimal end = new BigDecimal("0.1234581111111111111111111111111111111");
		BigDecimal increment = new BigDecimal("0");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 1);
		assertEquals(maps.get(0).get("param"), start); 
	}
	
	@Test
	public void zeroIncrement()
	{
		BigDecimal start = new BigDecimal("0.1234581111111111111111111111111111111");
		BigDecimal end = new BigDecimal("0.1234591111111111111111111111111111111");
		BigDecimal increment = new BigDecimal("0");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 2);
		assertEquals(maps.get(0).get("param"), start);
		assertEquals(maps.get(1).get("param"), end);
	}
	
	@Test
	public void multiValueExact()
	{
		BigDecimal start = new BigDecimal("0.1");
		BigDecimal end = new BigDecimal("0.3");
		BigDecimal increment = new BigDecimal("0.1");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 3);
		assertEquals(maps.get(0).get("param"), start);
		assertEquals(maps.get(1).get("param"), new BigDecimal("0.2"));
		assertEquals(maps.get(2).get("param"), end);
	}
	
	@Test
	public void multiValueExactNegative()
	{
		BigDecimal start = new BigDecimal("0.3");
		BigDecimal end = new BigDecimal("0.1");
		BigDecimal increment = new BigDecimal("0.1");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 3);
		assertEquals(maps.get(0).get("param"), start);
		assertEquals(maps.get(1).get("param"), new BigDecimal("0.2"));
		assertEquals(maps.get(2).get("param"), end);
	}
	
	@Test
	public void multiValueInexact()
	{
		BigDecimal start = new BigDecimal("0.1");
		BigDecimal end = new BigDecimal("0.35");
		BigDecimal increment = new BigDecimal("0.1");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 3);
		assertEquals(maps.get(0).get("param"), start);
		assertEquals(maps.get(1).get("param"), new BigDecimal("0.2"));
		assertEquals(maps.get(2).get("param"), new BigDecimal("0.3"));
	}
	
	@Test
	public void multiValueInexactNegative()
	{
		BigDecimal start = new BigDecimal("0.35");
		BigDecimal end = new BigDecimal("0.1");
		BigDecimal increment = new BigDecimal("0.1");
		
		RangeListSweep sweep = new RangeListSweep("param", start, end, increment);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 3);
		assertEquals(maps.get(0).get("param"), start);
		assertEquals(maps.get(1).get("param"), new BigDecimal("0.25"));
		assertEquals(maps.get(2).get("param"), new BigDecimal("0.15"));
	}
}

