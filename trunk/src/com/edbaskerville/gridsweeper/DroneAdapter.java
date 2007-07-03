package com.edbaskerville.gridsweeper;

import java.io.IOException;
import java.io.*;
import java.util.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

/**
 * <p>An adapter that runs models designed for Ted Belding's Drone.
 * The Drone model specification leaves quite a bit of room for
 * customization, so {@code DroneAdapter} supports quite a few settings:</p>
 * 
 * <table>
 * 
 * <tr>
 * <td>Command</td>                 <td>Description</td>
 * <td>Default</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code command}</td>         <td>The path to the model executable. Required.</td>
 * <td>(none)</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code setParamOption}</td>  <td>The command-line option for parameter assignments
 *                                  <em>param</em>=<em>value</em>.</td>
 * <td>{@code -D}</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code runNumOption}</td>    <td>The command-line option for specifying the run number.</td>
 * <td>{@code -N}</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code runNumPrefix}</td>    <td>A prefix to add before the run number.</td>
 * <td>(none)</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code rngSeedOption}</td>   <td>The command-line option for specifying specify the random seed.</td>
 * <td>{@code -S}</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code useInputFile}</td>    <td>Whether or not to provide an input file.
 *                                  Interpreted as true if and only if the value is equal,
 *                                  ignoring case, to the string {@code "true"}.</td>
 * <td>{@code true}</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code inputFileOption}</td> <td>The command-line option for specifying the input file.</td>
 * <td>{@code -I}</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code inputFilePath}</td>   <td>The path to the input file.</td>
 * <td>(none)</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code miscOptions}</td>     <td>Additional command-line options to supply.</td>
 * <td>(none)</td>
 * </tr>
 * 
 * </table>
 * 
 * <p>For more information, see the
 * <a target="_top" href="http://www.cscs.umich.edu/Software/Drone/">Drone website</a>.</p>
 * 
 * @author Ed Baskerville
 *
 */
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
	
	/**
	 * Standard {@link Adapter} constructor for {@code DroneAdapter}. Assigns settings
	 * to fields.
	 * @param settings Settings for the adapter. See the class description
	 * for supported settings.
	 * @throws AdapterException When no command is specified.
	 */
	public DroneAdapter(Properties settings) throws AdapterException
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
	}

	/**
	 * Runs the Drone model as specified by the settings and the arguments to this method.  
	 * 
	 * @throws AdapterException If an I/O error occurs. TODO: more robust error checking needed.
	 */
	public RunResults run(ParameterMap parameterMap, int runNumber, long rngSeed) throws AdapterException
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
			Object value = parameterMap.get(name);
			if(value instanceof List)
			{
				value = (((List)value).get(runNumber));
			}
			
			arguments.add(setParamOption + name + "=" + parameterMap.get(name));
		}
		
		StringBuilder messageBuilder = new StringBuilder(command);
		for(String arg : arguments)
		{
			messageBuilder.append(" " + StringUtils.escape(arg, " "));
		}
		
		int status = 0;
		// String message = messageBuilder.toString();
		String message = messageBuilder.toString();
		
		byte[] stdoutData = null;
		byte[] stderrData = null;
		
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
			
			/*// Write to stdin stream
			OutputStream stdinStream = process.getOutputStream();
			if(stdinData != null)
			{
				stdinStream.write(stdinData);
			}
			stdinStream.close();*/
			
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
		
		return new RunResults(status, message, stdoutData, stderrData);
	}
}
