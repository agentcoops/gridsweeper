package com.edbaskerville.gridsweeper.parameters;


import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;
import static java.lang.Math.*;

public class UniformDoubleSweepTest
{
	UniformDoubleSweep sweep;
	Random rng;
	List<ParameterMap> maps;
	
	@Before
	public void setUp()
	{
		rng = new Random(997);
	}
	
	@Test
	public void negativeCount()
	{
		try
		{
			sweep = new UniformDoubleSweep("param", rng, 0, 1, -1);
			fail();
		}
		catch(Exception e) {}
	}
	
	@Test
	public void simpleUniform()
	{
		sweep = new UniformDoubleSweep("param", rng, 0, 1, 2);
		maps = sweep.generateMaps();
		
		assertEquals(2, maps.size());
		
		double firstValue = ((Double)maps.get(0).get("param")).doubleValue();
		assertTrue(firstValue >= 0 && firstValue < 1);
		
		double secondValue = ((Double)maps.get(1).get("param")).doubleValue();
		assertTrue(secondValue >= 0 && secondValue < 1);
	}
	
	@Test
	public void offsetUniform()
	{
		sweep = new UniformDoubleSweep("param", rng, 10, 20, 2);
		maps = sweep.generateMaps();
		
		assertEquals(2, maps.size());
		
		double firstValue = ((Double)maps.get(0).get("param")).doubleValue();
		assertTrue(firstValue >= 10 && firstValue < 20);
		
		double secondValue = ((Double)maps.get(1).get("param")).doubleValue();
		assertTrue(secondValue >= 10 && secondValue < 20);
	}
	
	@Test
	public void reverseUniform()
	{
		sweep = new UniformDoubleSweep("param", rng, 20, 10, 2);
		maps = sweep.generateMaps();
		
		assertEquals(2, maps.size());
		
		double firstValue = ((Double)maps.get(0).get("param")).doubleValue();
		assertTrue(firstValue >= 10 && firstValue < 20);
		
		double secondValue = ((Double)maps.get(1).get("param")).doubleValue();
		assertTrue(secondValue >= 10 && secondValue < 20);
	}
	
	@Test
	public void largeUniform()
	{
		int numDraws = 1000;
		double start = 10;
		double end = 20;
		
		sweep = new UniformDoubleSweep("param", rng, start, end, numDraws);
		maps = sweep.generateMaps();
		
		assertEquals(numDraws, maps.size());
		
		// Add up draws
		double total = 0;
		for(int i = 0; i < numDraws; i++)
		{
			double value = ((Double)maps.get(i).get("param")).doubleValue();
			assertTrue(value >= start && value < end);
			total += value;
		}
		
		// Calculate mean, verify that it's within expected confidence interval
		double sampleMean = total / numDraws;
		double mean = (start + end)/2;
		double stddev = (end - start) / sqrt(12);
		double confidenceInterval = 2 * stddev/sqrt(numDraws);
		assertEquals(mean, sampleMean, confidenceInterval);
	}
}
