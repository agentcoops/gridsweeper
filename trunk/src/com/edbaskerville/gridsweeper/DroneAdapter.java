package com.edbaskerville.gridsweeper;

import java.util.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public class DroneAdapter implements Adapter
{
	private String command;
	
	private String setParamOption;
	
	private String runNumOption;
	private String runNumPrefix;

	private boolean useInputFile;
	private String inputFileOption;
	private String inputFilePath;
	
	private String miscOptions;
	
	byte[] stdinData;
	
	public DroneAdapter(Properties settings, byte[] stdinData) throws AdapterException
	{
		command = settings.getProperty("command");
		if(command == null)
		{
			throw new AdapterException("\"command\" property must be specified.");
		}
		
		setParamOption = settings.getProperty("setParamOption", "-D");
		
		runNumOption = settings.getProperty("runNumOption", "-N");
		runNumPrefix = settings.getProperty("runNumPrefix", "");
		
		useInputFile = Boolean.parseBoolean(settings.getProperty("useInputFile", "true"));
		inputFileOption = settings.getProperty("inputFileOption", "-I");
		inputFilePath = settings.getProperty("inputFilePath");
		
		miscOptions = settings.getProperty("miscOptions");
		
		this.stdinData = stdinData;
	}

	public RunResults run(ParameterMap parameters, long rngSeed) throws AdapterException
	{
		return null;
	}
}
