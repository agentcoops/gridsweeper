package com.edbaskerville.gridsweeper;

import java.io.*;
import java.util.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public class RunSetup implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Preferences preferences;
	private Properties properties;
	private Properties inputFiles;
	private String fileTransferSubpath;
	private ParameterMap parameters;
	private int runNumber;
	private long rngSeed;
	private Properties outputFiles;
	private String adapterClassName;
	
	public RunSetup(Preferences preferences, Properties properties, Properties inputFiles, String fileTransferSubpath, ParameterMap parameters, int runNumber, long rngSeed, Properties outputFiles, String adapterClassName)
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

	public Properties getInputFiles()
	{
		return inputFiles;
	}
	
	public Properties getOutputFiles()
	{
		return outputFiles;
	}
	
	public String getFileTransferSubpath()
	{
		return fileTransferSubpath;
	}
}
