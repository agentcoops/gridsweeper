package com.edbaskerville.gridsweeper;

import java.util.*;
import javax.xml.parsers.*;

import com.edbaskerville.gridsweeper.parameters.*;

/**
 * Represents an experiment. An experiment is specified by a root
 * parameter sweep of class
 * {@link MultiplicativeCombinationSweep},
 * a set of abbreviations for parameter names so that directories can be named
 * more efficiently, and sets of input and output files to transfer (if
 * a shared filesystem is not available). The experiment object is the one
 * most directly represented in the user interface.
 * @author Ed Baskerville
 *
 */
public class Experiment
{
	private String name;
	
	private Properties properties;
	private Properties abbreviations;
	private Properties inputFiles;
	private Properties outputFiles;
	
	private MultiplicativeCombinationSweep rootSweep;
	private StringList parameterOrder;
	
	private int numRuns;
	private Long rngSeed;
	
	/**
	 * The default constructor. Initializes {@code numRuns} to 1, and 
	 * creates empty objects for properties, abbreviations, input/output files,
	 * and the root parameter sweep. 
	 *
	 */
	public Experiment()
	{
		numRuns = 1;
		properties = new Properties();
		abbreviations = new Properties();
		inputFiles = new Properties();
		outputFiles = new Properties();
		rootSweep = new MultiplicativeCombinationSweep();
	}
	
	/**
	 * Loads an experiment from XML. See {@link ExperimentXMLHandler} for
	 * a description of the XML format.
	 * @param experimentURL The URL containing the XML. 
	 * @throws ExperimentException If the XML cannot be parsed.
	 */
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
	
	/**
	 * Generates experiment cases (parameter assignments and random seeds) from
	 * the experiment specification.
	 * @param rng The random number generator to use.
	 * @return A list of experiment cases
	 * @throws ExperimentException
	 */
	public List<ExperimentCase> generateCases(Random rng) throws ExperimentException
	{
		List<ExperimentCase> cases = new ArrayList<ExperimentCase>();
		
		// If there's a provided initial seed for the rng, seed it 
		if(rngSeed != null) rng.setSeed(rngSeed);
		
		// Generate the list of parameter values
		try
		{
			List<ParameterMap> parameterMaps = rootSweep.generateMaps(rng, numRuns);
			
			// If there's an rng seed provided,
			// and only one parameter combo,
			// and only one run,
			// we're going to use that rng seed for the one run.
			// This gives proper behavior for reproducing cases.
			if(rngSeed != null && parameterMaps.size() == 1 && numRuns == 1)
			{
				List<Long> rngSeeds = new ArrayList<Long>(1);
				rngSeeds.add(rngSeed);
				cases.add(new ExperimentCase(parameterMaps.get(0), rngSeeds));
			}
			
			// Otherwise generate a big pile of cases.
			else
			{
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
		}
		catch(Exception e)
		{
			throw new ExperimentException("Received exception creating experiment cases.", e);
		}
		
		return cases;
	}

	/** 
	 * Getter for experiment properties.
	 * @return The properties object.
	 */
	public Properties getProperties()
	{
		return properties;
	}

	/**
	 * Setter for experiment properties.
	 * @param properties The properties object to use.
	 */
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	/**
	 * Getter for abbrevations.
	 * @return The abbreviations object.
	 */
	public Properties getAbbreviations()
	{
		return abbreviations;
	}

	/**
	 * Setter for parameter name abbreviations. 
	 * @param abbreviations The abbreviations object to use.
	 */
	public void setAbbreviations(Properties abbreviations)
	{
		this.abbreviations = abbreviations;
	}

	/**
	 * Getter for the number of runs.
	 * @return The number of runs.
	 */
	public int getNumRuns()
	{
		return numRuns;
	}

	/**
	 * Setter for the number of runs.
	 * @param numRuns The number of times to run each set of parameter assignments.
	 */
	public void setNumRuns(int numRuns)
	{
		if(numRuns < 1) throw new IllegalArgumentException("numRuns must be positive");
		this.numRuns = numRuns;
	}

	/**
	 * Getter for the random seed. This seed is used as a starting point for calculating
	 * the random seeds for runs as well as for stochastic sweeps. 
	 * @return The seed for the random number generator.
	 */
	public Long getRngSeed()
	{
		return rngSeed;
	}

	/**
	 * Setter for the random seed. This seed is used as a starting point for calculating
	 * the random seeds for runs as well as for stochastic sweeps.
	 * @param rngSeed The random seed to use. 
	 */
	public void setRngSeed(Long rngSeed)
	{
		this.rngSeed = rngSeed;
	}

	/**
	 * Getter for the root parameter sweep.
	 * @return The root {@code MultiplicativeCombinationSweep} object.
	 */
	public MultiplicativeCombinationSweep getRootSweep()
	{
		return rootSweep;
	}

	/**
	 * Getter for the experiment name. The name is used in the user interface
	 * as well as to name experiment results directories.
	 * @return The experiment name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Setter for the experiment name.
	 * @param name The experiment name to use.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Getter for the input files object. Keys are source file paths in the
	 * submission host's filesystem, and values are desintation paths relative
	 * to the running model executable's working directory.
	 * This object is only used when file transfer is on.
	 * @return The input files object.
	 */
	public Properties getInputFiles()
	{
		return inputFiles;
	}

	/**
	 * Setter for the input files object. Keys are source file paths in the
	 * submission host's filesystem, and values are destination paths relative
	 * to the running model executable's working directory.
	 * This object is only used when file transfer is on.
	 * @param inputFiles
	 */
	public void setInputFiles(Properties inputFiles)
	{
		this.inputFiles = inputFiles;
	}

	/**
	 * Getter for the output files object. TODO: this will be changed from a Properties
	 * object to a simple list of strings, so that the source path within the
	 * executable working directory will be the same as the destination path
	 * in the output directory on the submission host.
	 * @return The output files object.
	 */
	public Properties getOutputFiles()
	{
		return outputFiles;
	}

	/**
	 * Setter for the output files object. TODO: this will be changed from a Properties
	 * object to a simple list of strings, so that the source path within the
	 * executable working directory will be the same as the destination path
	 * in the output directory on the submission host.
	 * @param outputFiles The output files object to use.
	 */
	public void setOutputFiles(Properties outputFiles)
	{
		this.outputFiles = outputFiles;
	}

	/**
	 * Returns a string description of an experiment case for debugging purposes.
	 * Includes all parameters except SingleValueSweep-set parameters (since
	 * their values don't change).
	 * @param experimentCase The experiment case to get a description for.
	 * @return The string description.
	 */
	public String getCaseDescription(ExperimentCase experimentCase)
	{
		ParameterMap changingParameters = (ParameterMap)experimentCase.getParameterMap().clone();
		
		// TODO: I see a bug. Only SingleValueSweep 
		// objects at the root level will be detected, since there's no recursion.
		for(Sweep sweep : rootSweep)
		{
			if(sweep instanceof SingleValueSweep)
			{
				changingParameters.remove(((SingleValueSweep)sweep).getName());
			}
		}
		
		return changingParameters.toString();
	}

	/**
	 * Generates a directory name for an experiment case. The name
	 * is in the format <em>p1</em>=<em>v1</em>-<em>p2</em>=<em>v2</em>...,
	 * where <em>p1</em>, etc. is the parameter name or, if available,
	 * the abbreviation, and <em>v1</em>, etc. is the parameter value.
	 * {@link SingleValueSweep}-set parameters are left out of the name.
	 * Parameter values of type {@code Double} are formatted using the
	 * {@code %5g} format (scientific notation or float, whichever is shorter).
	 * The order is determined by the {@code parameterOrder} field of the
	 * experiment, if available, or by the {@link CombinationSweep#getParameterOrder}
	 * method.
	 *  
	 * @param expCase
	 * @return The directory name.
	 */
	public String getDirectoryNameForCase(ExperimentCase expCase)
	{
		StringList parameterOrder = getParameterOrderUsed();
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
	
	/**
	 * Returns the user-visble parameter order set, or the root sweep's
	 * parameter order if none has been explicitly set.
	 * @return The parameter order.
	 */
	private StringList getParameterOrderUsed()
	{
		if(parameterOrder != null)
		{
			return parameterOrder;
		}
		
		return rootSweep.getParameterOrder();
	}
	
	/**
	 * Returns the user-visible parameter order set. May be {@code null}.
	 * @return The parameter order.
	 */
	public StringList getParameterOrder()
	{
		return parameterOrder;
	}

	/**
	 * Sets the parameter order to use for directory names, etc.
	 * @param parameterOrder The parameter order to use.
	 */
	public void setParameterOrder(StringList parameterOrder)
	{
		this.parameterOrder = parameterOrder;
	}
}
