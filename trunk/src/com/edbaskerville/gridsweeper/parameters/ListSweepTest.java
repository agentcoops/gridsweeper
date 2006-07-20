package com.edbaskerville.gridsweeper.parameters;


import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class ListSweepTest
{
	private ListSweep<Long> sweep;
	
	@Before
	public void setUp()
	{
		sweep = new ListSweep<Long>("param");
	}
	
	@Test
	public void emptySweep()
	{
		List<ParameterMap> maps = sweep.generateMaps();
		
		assertEquals(maps.size(), 0);
	}
	
	@Test
	public void simpleSweep()
	{
		sweep.add(5L);
		sweep.add(6L);
		sweep.add(14L);
		sweep.add(-15L);
		
		List<ParameterMap> maps = sweep.generateMaps();
		
		assertTrue((Long)maps.get(0).get("param") == 5);
		assertTrue((Long)maps.get(1).get("param") == 6);
		assertTrue((Long)maps.get(2).get("param") == 14);
		assertTrue((Long)maps.get(3).get("param") == -15);
	}
}
