package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;
import java.util.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.ParameterMap;

/**
 * A class that encapsulates setup data for a model run. Includes a copy of the
 * user preferences to be passed on to the running model, properties for the
 * experiment, file transfer data, run number, random seed, and the adapter
 * class to use.
 * @author Ed Baskerville
 *
 */
public class RunSetup implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Preferences preferences;
	private Properties properties;
	private StringMap inputFiles;
	private String fileTransferSubpath;
	private ParameterMap parameters;
	private int runNumber;
	private long rngSeed;
	private StringList outputFiles;
	private String adapterClassName;
	
	public RunSetup(Preferences preferences, Properties properties, StringMap inputFiles, String fileTransferSubpath, ParameterMap parameters, int runNumber, long rngSeed, StringList outputFiles, String adapterClassName)
	{
		this.preferences = preferences;
		this.properties = properties;
		this.inputFiles = inputFiles;
		this.fileTransferSubpath = fileTransferSubpath;
		this.parameters = parameters;
		this.runNumber = runNumber;
		this.rngSeed = rngSeed;
		this.outputFiles = outputFiles;
		this.adapterClassName = adapterClassName;
	}

	public String getAdapterClassName()
	{
		return adapterClassName;
	}
	
	public Preferences getPreferences()
	{
		return preferences;
	}
	
	public Properties getProperties()
	{
		return properties;
	}
	
	public long getRngSeed()
	{
		return rngSeed;
	}
	
	public int getRunNumber()
	{
		return runNumber;
	}
	
	public ParameterMap getParameters()
	{
		return parameters;
	}

	public StringMap getInputFiles()
	{
		return inputFiles;
	}
	
	public StringList getOutputFiles()
	{
		return outputFiles;
	}
	
	public String getFileTransferSubpath()
	{
		return fileTransferSubpath;
	}
	
	public String toString()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("adapterClassName", adapterClassName);
		map.put("preferences", preferences);
		map.put("properties", properties);
		map.put("inputFiles", inputFiles);
		map.put("fileTransferSubpath", fileTransferSubpath);
		map.put("runNumber", "" + runNumber);
		map.put("rngSeed", "" + rngSeed);
		map.put("outputFiles", outputFiles);
		
		return map.toString();
	}
}
