package com.edbaskerville.gridsweeper;


import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class MultiplicativeCombinationSweepTest
{
	MultiplicativeCombinationSweep sweep;
	List<ParameterMap> maps;
	
	@Before
	public void setUp()
	{
		sweep = new MultiplicativeCombinationSweep();
	}
	
	@Test
	public void emptySweep() throws SweepLengthException, DuplicateParameterException
	{
		maps = sweep.generateMaps();
		assertEquals(0, maps.size());
	}
	
	@Test
	public void singleSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep<String> listSweep = new ListSweep<String>("param");
		listSweep.add("hoo-ah");
		listSweep.add("yoo-ha");
		sweep.add(listSweep);
		
		maps = sweep.generateMaps();
		assertEquals(2, maps.size());
		assertEquals("hoo-ah", maps.get(0).get("param"));
		assertEquals("yoo-ha", maps.get(1).get("param"));
	}
	
	@Test
	public void doubleSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep<String> listSweep1 = new ListSweep<String>("param1");
		listSweep1.add("A");
		listSweep1.add("B");
		sweep.add(listSweep1);
		
		ListSweep<String> listSweep2 = new ListSweep<String>("param2");
		listSweep2.add("1");
		listSweep2.add("2");
		sweep.add(listSweep2);
		
		maps = sweep.generateMaps();
		assertEquals(4, maps.size());
		
		assertEquals("A", maps.get(0).get("param1"));
		assertEquals("1", maps.get(0).get("param2"));
		
		assertEquals("A", maps.get(1).get("param1"));
		assertEquals("2", maps.get(1).get("param2"));
		
		assertEquals("B", maps.get(2).get("param1"));
		assertEquals("1", maps.get(2).get("param2"));
		
		assertEquals("B", maps.get(3).get("param1"));
		assertEquals("2", maps.get(3).get("param2"));
	}
	
	@Test
	public void tripleSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep<String> listSweep1 = new ListSweep<String>("param1");
		listSweep1.add("A");
		listSweep1.add("B");
		sweep.add(listSweep1);
		
		ListSweep<String> listSweep2 = new ListSweep<String>("param2");
		listSweep2.add("1");
		listSweep2.add("2");
		sweep.add(listSweep2);
		
		ListSweep<String> listSweep3 = new ListSweep<String>("param3");
		listSweep3.add("a");
		listSweep3.add("b");
		sweep.add(listSweep3);
		
		maps = sweep.generateMaps();
		
		assertEquals("A", maps.get(0).get("param1"));
		assertEquals("1", maps.get(0).get("param2"));
		assertEquals("a", maps.get(0).get("param3"));
		
		assertEquals("A", maps.get(1).get("param1"));
		assertEquals("1", maps.get(1).get("param2"));
		assertEquals("b", maps.get(1).get("param3"));
		
		assertEquals("A", maps.get(2).get("param1"));
		assertEquals("2", maps.get(2).get("param2"));
		assertEquals("a", maps.get(2).get("param3"));
		
		assertEquals("A", maps.get(3).get("param1"));
		assertEquals("2", maps.get(3).get("param2"));
		assertEquals("b", maps.get(3).get("param3"));
		
		assertEquals("B", maps.get(4).get("param1"));
		assertEquals("1", maps.get(4).get("param2"));
		assertEquals("a", maps.get(4).get("param3"));
		
		assertEquals("B", maps.get(5).get("param1"));
		assertEquals("1", maps.get(5).get("param2"));
		assertEquals("b", maps.get(5).get("param3"));
		
		assertEquals("B", maps.get(6).get("param1"));
		assertEquals("2", maps.get(6).get("param2"));
		assertEquals("a", maps.get(6).get("param3"));
		
		assertEquals("B", maps.get(7).get("param1"));
		assertEquals("2", maps.get(7).get("param2"));
		assertEquals("b", maps.get(7).get("param3"));
	}
	
	@Test
	public void duplicateParameterSweep() throws SweepLengthException
	{
		ListSweep<Double> listSweep = new ListSweep<Double>("beta");
		listSweep.add(0.1);
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
}
