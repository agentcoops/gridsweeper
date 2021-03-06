/*
	DroneAdapter.java
	
	Part of GridSweeper
	Copyright (c) 2006 - 2007 Ed Baskerville <software@edbaskerville.com>

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package edu.umich.lsa.cscs.gridsweeper;

import static edu.umich.lsa.cscs.gridsweeper.StringUtils.formatPaddedInt;

import java.io.*;


/**
 * <p>An adapter that runs models designed for Ted Belding's Drone.
 * The Drone model specification leaves quite a bit of room for
 * customization, so {@code DroneAdapter} supports quite a few properties:</p>
 * 
 * <table>
 * 
 * <tr>
 * <td>Property</td>                <td>Description</td>
 * <td>Default</td>
 * </tr>
 * 
 * <tr>
 * <td>{@code model}</td>         <td>The path to the model executable. Required.</td>
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
 * <td>{@code rngSeedOption}</td>   <td>The command-line option for specifying the random seed.</td>
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
class DroneAdapter implements Adapter
{
	private String model;
	
	private String setParamOption;
	
	private String runNumOption;
	private String runNumPrefix;
	
	private String rngSeedOption;

	private boolean useInputFile;
	private String inputFileOption;
	private String inputFilePath;
	
	private String miscOptions;
	
	/**
	 * Standard {@link Adapter} constructor for {@code DroneAdapter}. Assigns properties
	 * to fields.
	 * @param settings Properties for the adapter. See the class description
	 * for supported properties.
	 * @throws AdapterException When no model is specified.
	 */
	public DroneAdapter(Settings settings) throws AdapterException
	{
		model = settings.getProperty("model");
		if(model == null)
		{
			throw new AdapterException("The \"model\" property must be specified.");
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
	 * Runs the Drone model as specified by the properties and the arguments to this method.  
	 * 
	 * @throws AdapterException If an I/O error occurs. 
	 */
	public RunResults run(ParameterMap parameterMap, int runNumber, int numRuns, int rngSeed) throws AdapterException
	{
		StringList arguments = new StringList();
		
		// First, add non-parameter options
		if(miscOptions != null)
		{
			arguments.addAll(StringUtils.tokenize(miscOptions));
		}
		arguments.add(runNumOption + runNumPrefix + formatPaddedInt(runNumber, numRuns - 1));
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
		
		StringBuffer messageBuilder = new StringBuffer(model);
		for(String arg : arguments)
		{
			messageBuilder.append(" " + StringUtils.escape(arg, " "));
		}
		
		int status = 0;
		String message = messageBuilder.toString();
		
		byte[] stdoutData = null;
		byte[] stderrData = null;
		
		// Create command array
		String[] cmdArray = new String[arguments.size() + 1];
		cmdArray[0] = model;
		for(int i = 0; i < arguments.size(); i++)
		{
			cmdArray[i+1] = arguments.get(i);
		}
		
		try
		{
			System.err.println("cmdArray:");
			System.err.println(new StringList(cmdArray).toString());
			
			// Actually run the damn thing, getting a process object with which to interact with it
			Process process = Runtime.getRuntime().exec(cmdArray);
			
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
			throw new AdapterException("Could not run model \"" + model + "\".", e);
		}
		
		return new RunResults(status, message, stdoutData, stderrData);
	}
}
