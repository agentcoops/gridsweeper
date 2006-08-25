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
	private byte[] stdinData;
	private ParameterMap parameters;
	private int runNumber;
	private long rngSeed;
	private boolean dryRun;
	private Properties outputFiles;
	private Class adapterClass;
	
	public RunSetup(Preferences preferences, Properties properties, Properties inputFiles, String fileTransferSubpath, byte[] stdinData, ParameterMap parameters, int runNumber, long rngSeed, boolean dryRun, Properties outputFiles, Class adapterClass)
	{
		this.preferences = preferences;
		this.properties = properties;
		this.inputFiles = inputFiles;
		this.fileTransferSubpath = fileTransferSubpath;
		this.stdinData = stdinData;
		this.parameters = parameters;
		this.runNumber = runNumber;
		this.rngSeed = rngSeed;
		this.dryRun = dryRun;
		this.outputFiles = outputFiles;
		this.adapterClass = adapterClass;
	}

	public Class getAdapterClass()
	{
		return adapterClass;
	}
	
	public boolean isDryRun()
	{
		return dryRun;
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
	
	public byte[] getStdinData()
	{
		return stdinData;
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
