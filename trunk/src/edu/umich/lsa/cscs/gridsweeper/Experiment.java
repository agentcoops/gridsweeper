package edu.umich.lsa.cscs.gridsweeper;

import java.io.FileNotFoundException;
import java.util.*;
import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

import javax.xml.parsers.*;
import org.xml.sax.SAXParseException;

import cern.jet.random.*;
import cern.jet.random.engine.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.*;

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
	
	private Settings settings;
	private StringMap abbreviations;
	private StringMap inputFiles;
	private StringList outputFiles;
	
	private MultiplicativeCombinationSweep rootSweep;
	private StringList parameterOrder;
	
	private int numRuns;
	private int firstSeedRow; // Row in RandomSeedTable to start using seeds from
	private int seedCol;      // Column in RandomSeedTable to use seeds from
	
	/**
	 * The default constructor. Initializes {@code numRuns} to 1, and 
	 * creates empty objects for settings, abbreviations, input/output files,
	 * and the root parameter sweep. 
	 *
	 */
	public Experiment()
	{
		this(null);
	}
	
	public Experiment(Settings settings)
	{
		numRuns = 1;
		this.settings = new Settings();
		if(settings != null) this.settings.putAll(settings);
		abbreviations = new StringMap();
		inputFiles = new StringMap();
		outputFiles = new StringList();
		rootSweep = new MultiplicativeCombinationSweep();
		
		Uniform uniform = new Uniform(new MersenneTwister(new Date()));
		
		firstSeedRow = uniform.nextIntFromTo(0, Integer.MAX_VALUE);
		seedCol = uniform.nextIntFromTo(0, RandomSeedTable.COLUMNS);
	}
	
	/**
	 * Loads an experiment from XML. See {@link ExperimentXMLHandler} for
	 * a description of the XML format.
	 * @param experimentURL The URL containing the XML. 
	 * @throws ExperimentException If the XML cannot be parsed.
	 */
	public Experiment(Settings settings, java.net.URL experimentURL) throws GridSweeperException
	{
		this(settings);
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ExperimentXMLHandler handler = new ExperimentXMLHandler(this);
			
			parser.parse(experimentURL.toString(), handler);
			
			if(name == null)
			{
				setName(lastPathComponent(experimentURL.getPath()));
			}
		}
		catch(SAXParseException e)
		{
			throw new GridSweeperException("Experiment file contains an error " +
					"at line " + e.getLineNumber() + ", column " +
					e.getColumnNumber() + ": " +
					e.getMessage(), e);
		}
		catch(Exception e)
		{
			throw new GridSweeperException("Received exception trying to parse experiment XML: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Generates experiment cases (parameter assignments and random seeds) from
	 * the experiment specification.
	 * @param rng The random number generator to use.
	 * @return A list of experiment cases
	 * @throws ExperimentException
	 */
	public List<ExperimentCase> generateCases() throws GridSweeperException
	{
		List<ExperimentCase> cases = new ArrayList<ExperimentCase>();
		
		RandomSeedGenerator seedGen = new RandomSeedGenerator(firstSeedRow, seedCol);
		
		// Generate the list of parameter values
		try
		{
			List<ParameterMap> parameterMaps = rootSweep.generateMaps(true);
			
			// Generate the experiment cases
			for(ParameterMap parameterMap : parameterMaps)
			{
				List<Integer> rngSeeds = new ArrayList<Integer>(numRuns);
				for(int i = 0; i < numRuns; i++)
				{
					rngSeeds.add(seedGen.nextSeed());
				}
				
				cases.add(new ExperimentCase(parameterMap, rngSeeds));
			}
		}
		catch(DuplicateParameterException e)
		{
			throw new GridSweeperException("Could not generate experiment cases: " +
					"parameter \"" + e.getName() + "\" is used by multiple sweeps.");
		}
		catch(SweepLengthException e)
		{
			throw new GridSweeperException("Could not generate experiment cases: " + 
					"mismatched child sweep lengths in a parallel combination sweep.");
		}
		
		return cases;
	}

	/** 
	 * Getter for experiment settings.
	 * @return The settings object.
	 */
	public Settings getSettings()
	{
		return settings;
	}

	/**
	 * Setter for experiment settings.
	 * @param settings The settings object to use.
	 */
	public void setSettings(Settings settings)
	{
		this.settings = settings;
	}

	/**
	 * Getter for abbrevations.
	 * @return The abbreviations object.
	 */
	public StringMap getAbbreviations()
	{
		return abbreviations;
	}

	/**
	 * Setter for parameter name abbreviations. 
	 * @param abbreviations The abbreviations object to use.
	 */
	public void setAbbreviations(StringMap abbreviations)
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

	public int getFirstSeedRow()
	{
		return firstSeedRow;
	}
	
	public int getSeedCol()
	{
		return seedCol;
	}

	public void setFirstSeedRow(int firstSeedRow)
	{
		this.firstSeedRow = firstSeedRow;
	}
	
	public void setSeedCol(int seedCol)
	{
		this.seedCol = seedCol;
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
	public StringMap getInputFiles()
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
	public void setInputFiles(StringMap inputFiles)
	{
		this.inputFiles = inputFiles;
	}

	/**
	 * Getter for the output files object.
	 * @return The output files object.
	 */
	public StringList getOutputFiles()
	{
		return outputFiles;
	}

	/**
	 * Setter for the output files object.
	 * @param outputFiles The output files object to use.
	 */
	public void setOutputFiles(StringList outputFiles)
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
		ParameterMap parameterMap = (ParameterMap)experimentCase.getParameterMap();
		
		ParameterMap changingParameters = new ParameterMap();
		
		for(String name : getParameterOrderUsed())
		{
			changingParameters.put(name, parameterMap.get(name));
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

	public void writeToFile(String path, boolean writeRngSeed) throws FileNotFoundException
	{
		ExperimentXMLWriter writer = new ExperimentXMLWriter(path, this, writeRngSeed);
		writer.writeXML();
	}
}
