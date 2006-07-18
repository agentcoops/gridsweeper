package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

public class LinearCombinationSweepTest
{
	private LinearCombinationSweep sweep;
	
	@Before
	public void setUp()
	{
		sweep = new LinearCombinationSweep();
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
		ListSweep<String> listSweep = new ListSweep<String>("param");
		sweep.add(listSweep);
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(maps.size(), 0);
	}
	
	@Test
	public void oneListSweep() throws SweepLengthException, DuplicateParameterException
	{
		ListSweep<String> listSweep = new ListSweep<String>("param");
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
		ListSweep<Double> listSweep1 = new ListSweep<Double>("beta");
		listSweep1.add(0.1);
		listSweep1.add(0.2);
		sweep.add(listSweep1);
		
		ListSweep<Double> listSweep2 = new ListSweep<Double>("gamma");
		listSweep2.add(0.9);
		listSweep2.add(0.4);
		sweep.add(listSweep2);
		
		List<ParameterMap> maps = sweep.generateMaps();
		assertEquals(2, maps.size());
		assertEquals(maps.get(0).get("beta"), new Double(0.1));
		assertEquals(maps.get(1).get("beta"), new Double(0.2));
		assertEquals(maps.get(0).get("gamma"), new Double(0.9));
		assertEquals(maps.get(1).get("gamma"), new Double(0.4));
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
	
	@Test
	public void mismatchedLengthSweep() throws DuplicateParameterException
	{
		ListSweep<Double> listSweep1 = new ListSweep<Double>("beta");
		listSweep1.add(0.1);
		sweep.add(listSweep1);
		
		ListSweep<Double> listSweep2 = new ListSweep<Double>("gamma");
		listSweep2.add(0.1);
		listSweep2.add(0.2);
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
