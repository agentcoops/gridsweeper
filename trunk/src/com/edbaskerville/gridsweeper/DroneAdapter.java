package com.edbaskerville.gridsweeper;

import java.util.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public class DroneAdapter implements Adapter
{
	private String command;
	
	private String setParamOption;
	
	private String runNumOption;
	private String runNumPrefix;
	
	private String rngSeedOption;

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
		
		rngSeedOption = settings.getProperty("rngSeedOption", "-S");
		
		useInputFile = Boolean.parseBoolean(settings.getProperty("useInputFile", "true"));
		inputFileOption = settings.getProperty("inputFileOption", "-I");
		inputFilePath = settings.getProperty("inputFilePath");
		
		miscOptions = settings.getProperty("miscOptions");
		
		this.stdinData = stdinData;
	}

	public RunResults run(ParameterMap parameters, int runNumber, long rngSeed, boolean dryRun) throws AdapterException
	{
		List<String> arguments = new ArrayList<String>();
		
		// First, add non-parameter options
		arguments.add(runNumOption + runNumPrefix + runNumber);
		arguments.add(rngSeedOption + rngSeed);
		if(useInputFile && inputFilePath != null)
		{
			arguments.add(inputFileOption + inputFilePath);
		}
		if(miscOptions != null)
		{
			arguments.addAll(StringUtils.tokenize(miscOptions));
		}
		
		// Now add all parameter settings
		for(String name : parameters.keySet())
		{
			arguments.add(setParamOption + name + "=" + parameters.get(name));
		}
		
		StringBuffer messageBuffer = new StringBuffer(command);
		for(String arg : arguments)
		{
			messageBuffer.append(" " + StringUtils.escape(arg, " "));
		}
		
		int status = 0;
		String message = messageBuffer.toString();
		byte[] stdoutData = null;
		byte[] stderrData = null;
		
		return new RunResults(status, message, stdoutData, stderrData);
	}
}
