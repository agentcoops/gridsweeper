package com.edbaskerville.gridsweeper;

import java.io.IOException;
import java.io.*;
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

	public RunResults run(ParameterMap parameterMap, int runNumber, long rngSeed, boolean dryRun) throws AdapterException
	{
		List<String> arguments = new ArrayList<String>();
		
		// First, add non-parameter options
		if(miscOptions != null)
		{
			arguments.addAll(StringUtils.tokenize(miscOptions));
		}
		arguments.add(runNumOption + runNumPrefix + runNumber);
		arguments.add(rngSeedOption + rngSeed);
		if(useInputFile && inputFilePath != null)
		{
			arguments.add(inputFileOption + inputFilePath);
		}
		
		// Now add all parameter settings
		for(String name : parameterMap.keySet())
		{
			arguments.add(setParamOption + name + "=" + parameterMap.get(name));
		}
		
		StringBuilder messageBuilder = new StringBuilder(command);
		for(String arg : arguments)
		{
			messageBuilder.append(" " + StringUtils.escape(arg, " "));
		}
		
		int status = 0;
		String message = messageBuilder.toString();
		
		byte[] stdoutData = null;
		byte[] stderrData = null;
		
		if(!dryRun)
		{
			// Create command array
			String[] cmdArray = new String[arguments.size() + 1];
			cmdArray[0] = command;
			for(int i = 0; i < arguments.size(); i++)
			{
				cmdArray[i+1] = arguments.get(i);
			}
			
			try
			{
				// Actually run the damn thing, getting a process object with which to interact with it
				Process process = Runtime.getRuntime().exec(cmdArray);
				
				// Write to stdin stream
				OutputStream stdinStream = process.getOutputStream();
				if(stdinData != null)
				{
					stdinStream.write(stdinData);
				}
				stdinStream.close();
				
				// Read to end of stdout and stderr streams
				int b;
				
				InputStream stdoutStream = process.getInputStream();
				ByteArrayOutputStream stdoutByteStream = new ByteArrayOutputStream();
				while((b = stdoutStream.read()) != -1)
					stdoutByteStream.write(b);
				stdoutData = stdoutByteStream.toByteArray();
				
				InputStream stderrStream = process.getErrorStream();
				ByteArrayOutputStream stderrByteStream = new ByteArrayOutputStream();
				while((b = stderrStream.read()) != -1)
					stderrByteStream.write(b);
				stderrData = stderrByteStream.toByteArray();
			}
			catch (IOException e)
			{
				throw new AdapterException("Received IOException running process", e);
			}
		}
		
		return new RunResults(status, message, stdoutData, stderrData);
	}
}
