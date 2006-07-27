package com.edbaskerville.gridsweeper;

import java.util.*;
import com.edbaskerville.gridsweeper.parameters.*;

public class Experiment
{
	private Map<String, String> settings;
	private Map<String, String> abbreviations;
	private Sweep rootSweep;
	private int numRuns;
	private Random rng;
	private Long rngSeed;
	
	public Experiment(Random rng, int numRuns, Sweep rootSweep)
	{
		this.rng = rng;
		this.numRuns = numRuns;
		this.rootSweep = rootSweep;
	}
	
	public List<ExperimentCase> generateCases() throws ExperimentException
	{
		List<ExperimentCase> cases = new ArrayList<ExperimentCase>();
		
		// If there's a provided initial seed for the rng, seed it 
		if(rngSeed != null) rng.setSeed(rngSeed);
		
		// Generate the list of parameter values
		try
		{
			List<ParameterMap> maps = rootSweep.generateMaps();
			
			for(ParameterMap map : maps)
			{
				List<Long> rngSeeds = new ArrayList<Long>(numRuns);
				for(int i = 0; i < numRuns; i++)
				{
					rngSeeds.add(rng.nextLong());
				}
				
				cases.add(new ExperimentCase(settings, abbreviations, map, rngSeeds));
			}
		}
		catch(Exception e)
		{
			throw new ExperimentException("Received exception creating experiment cases.", e);
		}
		
		return cases;
	}

	public Map<String, String> getSettings()
	{
		return settings;
	}

	public void setSettings(Map<String, String> settings)
	{
		this.settings = settings;
	}

	public Map<String, String> getAbbreviations()
	{
		return abbreviations;
	}

	public void setAbbreviations(Map<String, String> abbreviations)
	{
		this.abbreviations = abbreviations;
	}

	public int getNumRuns()
	{
		return numRuns;
	}

	public void setNumRuns(int numRuns)
	{
		if(numRuns < 1) throw new IllegalArgumentException("numRuns must be positive");
		this.numRuns = numRuns;
	}

	public Random getRng()
	{
		return rng;
	}

	public void setRng(Random rng)
	{
		this.rng = rng;
	}

	public Long getRngSeed()
	{
		return rngSeed;
	}

	public void setRngSeed(Long rngSeed)
	{
		this.rngSeed = rngSeed;
	}

	public Sweep getRootSweep()
	{
		return rootSweep;
	}

	public void setRootSweep(Sweep rootSweep)
	{
		this.rootSweep = rootSweep;
	}
}
