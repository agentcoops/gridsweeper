package com.edbaskerville.gridsweeper;


import java.util.*;

import org.junit.*;

import com.edbaskerville.gridsweeper.parameters.*;

import static org.junit.Assert.*;

public class ExperimentTest
{
	Experiment experiment;
	
	@Before
	public void setUp() throws Exception
	{
		experiment = new Experiment(new Random(), 10, new SingleValueSweep<String>("param", "1"));
	}
	
	@Test
	public void generateCases() throws ExperimentException
	{
		List<ExperimentCase> cases = experiment.generateCases();
		
		assertEquals(1, cases.size());
		
		ExperimentCase expCase = cases.get(0);
		
		ParameterMap map = expCase.getMap();
		assertEquals(1, map.size());
		assertEquals("1", map.get("param"));
		
		assertEquals(10, expCase.getRngSeeds().size());
	}
}
