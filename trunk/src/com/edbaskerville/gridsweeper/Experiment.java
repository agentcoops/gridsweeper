package com.edbaskerville.gridsweeper;

import java.util.*;
import javax.xml.parsers.*;

import com.edbaskerville.gridsweeper.parameters.*;

public class Experiment
{
	private String type;
	private String name;
	
	private Map<String, String> settings;
	private Map<String, String> abbreviations;
	private MultiplicativeCombinationSweep rootSweep;
	private int numRuns;
	private Long rngSeed;
	private String resultsDir;
	
	public Experiment()
	{
		numRuns = 1;
		settings = new HashMap<String, String>();
		abbreviations = new HashMap<String, String>();
		rootSweep = new MultiplicativeCombinationSweep();
	}
	
	public Experiment(java.net.URL experimentURL) throws ExperimentException
	{
		this();
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ExperimentXMLHandler handler = new ExperimentXMLHandler(this);
			
			parser.parse(experimentURL.toString(), handler);
		}
		catch(Exception e)
		{
			throw new ExperimentException("Received exception trying to parse URL.", e);
		}
	}
	
	public List<ExperimentCase> generateCases(Random rng) throws ExperimentException
	{
		List<ExperimentCase> cases = new ArrayList<ExperimentCase>();
		
		// If there's a provided initial seed for the rng, seed it 
		if(rngSeed != null) rng.setSeed(rngSeed);
		
		// Generate the list of parameter values
		try
		{
			List<ParameterMap> maps = rootSweep.generateMaps(rng);
			
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

	public int getNumRuns()
	{
		return numRuns;
	}

	public void setNumRuns(int numRuns)
	{
		if(numRuns < 1) throw new IllegalArgumentException("numRuns must be positive");
		this.numRuns = numRuns;
	}

	public Long getRngSeed()
	{
		return rngSeed;
	}

	public void setRngSeed(Long rngSeed)
	{
		this.rngSeed = rngSeed;
	}

	public MultiplicativeCombinationSweep getRootSweep()
	{
		return rootSweep;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getResultsDir()
	{
		return resultsDir;
	}

	public void setResultsDir(String resultsDir)
	{
		this.resultsDir = resultsDir;
	}
}
