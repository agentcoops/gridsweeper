package com.edbaskerville.gridsweeper;

import java.util.*;
import javax.xml.parsers.*;

import com.edbaskerville.gridsweeper.parameters.*;

public class Experiment
{
	private String name;
	
	private Properties properties;
	private Properties abbreviations;
	private Properties inputFiles;
	private Properties outputFiles;
	
	private MultiplicativeCombinationSweep rootSweep;
	private List<String> parameterOrder;
	
	private int numRuns;
	private Long rngSeed;
	
	public Experiment()
	{
		numRuns = 1;
		properties = new Properties();
		abbreviations = new Properties();
		inputFiles = new Properties();
		outputFiles = new Properties();
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
			List<ParameterMap> parameterMaps = rootSweep.generateMaps(rng);
			
			for(ParameterMap parameterMap : parameterMaps)
			{
				List<Long> rngSeeds = new ArrayList<Long>(numRuns);
				for(int i = 0; i < numRuns; i++)
				{
					rngSeeds.add(rng.nextLong());
				}
				
				cases.add(new ExperimentCase(parameterMap, rngSeeds));
			}
		}
		catch(Exception e)
		{
			throw new ExperimentException("Received exception creating experiment cases.", e);
		}
		
		return cases;
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void setProperties(Properties settings)
	{
		this.properties = settings;
	}

	public Properties getAbbreviations()
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

	public Properties getInputFiles()
	{
		return inputFiles;
	}

	public void setInputFiles(Properties inputFiles)
	{
		this.inputFiles = inputFiles;
	}

	public Properties getOutputFiles()
	{
		return outputFiles;
	}

	public void setOutputFiles(Properties outputFiles)
	{
		this.outputFiles = outputFiles;
	}

	public void setAbbreviations(Properties abbreviations)
	{
		this.abbreviations = abbreviations;
	}

	public String getCaseDescription(ExperimentCase experimentCase)
	{
		ParameterMap changingParameters = (ParameterMap)experimentCase.getParameterMap().clone();
		
		for(Sweep sweep : rootSweep)
		{
			if(sweep instanceof SingleValueSweep)
			{
				changingParameters.remove(((SingleValueSweep)sweep).getName());
			}
		}
		
		return changingParameters.toString();
	}

	public String getDirectoryNameForCase(ExperimentCase expCase)
	{
		List<String> parameterOrder = getParameterOrderUsed();
		StringBuffer dirName = new StringBuffer();
		
		boolean first = true;
		for(String param : parameterOrder)
		{
			if(!first) dirName.append("-");
			
			if(abbreviations.containsKey(param))
			{
				dirName.append(abbreviations.get(param));
			}
			else
			{
				dirName.append(param);
			}
			
			Object value = expCase.getParameterMap().get(param);
			String valueStr;
			if(value instanceof Double)
			{
				valueStr = String.format("%5g", value);
			}
			else valueStr = value.toString();
			dirName.append("=" + valueStr);
			
			first = false;
		}
		
		return dirName.toString();
	}
	
	private List<String> getParameterOrderUsed()
	{
		if(parameterOrder != null)
		{
			return parameterOrder;
		}
		
		return rootSweep.getParameterOrder();
	}
	
	public List<String> getParameterOrder()
	{
		return parameterOrder;
	}

	public void setParameterOrder(List<String> parameterOrder)
	{
		this.parameterOrder = parameterOrder;
	}
}
