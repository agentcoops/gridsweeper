/*
	GridSweeperRunner.java
	
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

import java.io.*;
import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

/**
 * The GridSweeperRunner command-line tool to actually run the model
 * on the execution host.
 * @author Ed Baskerville
 *
 */
public class GridSweeperRunner
{
	/**
	 * Runs the model. First, reads the {@link RunSetup} object from standard input,
	 * and extracts settings for the run. If file transfer is on, input files
	 * are then downloaded from the file transfer system. Then an adapter object
	 * is created as specified in the run setup and used to actually run the model.
	 * Finally, if necessary, files are staged back to the file transfer system
	 * to be retrieved at the submission host. 
	 */
	public static void main(String[] args)
	{
		System.err.println("GridSweeperRunner main() starting...");
		
		RunResults results;
		try
		{
			// Load RunSetup object
			ObjectInputStream stdinStream = new ObjectInputStream(System.in);
			RunSetup setup = (RunSetup)stdinStream.readObject();
			
			System.err.println("RunSetup object:");
			System.err.println(setup.toString());
			
			// Get GridSweeper settings
			Settings settings = setup.getSettings();
			
			/*
			// Download input files
			boolean useFileTransfer = settings.getBooleanProperty("UseFileTransfer", false);
			FileTransferSystem fts = null;
			if(useFileTransfer)
			{
				String className = settings.getProperty("FileTransferSystemClassName", "edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem");
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(className, settings);
				fts.connect();
				
				StringMap inputFiles = setup.getInputFiles();
				
				for(String key : inputFiles.keySet())
				{
					String path = inputFiles.get(key);
					String fileTransferSubpath = appendPathComponent(setup.getFileTransferSubpath(), "input");
					
					String remotePath = appendPathComponent(fileTransferSubpath, path);
					String localPath = path;
					
					fts.downloadFile(remotePath, localPath);
				}
				
				fts.disconnect();
			}
			*/
			
			String adapterClassName = settings.getProperty("AdapterClass", "edu.umich.lsa.cscs.gridsweeper.DroneAdapter");
			Adapter adapter = AdapterFactory.createAdapter(adapterClassName, settings);
			System.err.println("Adapter loaded.");
			
			// Run!
			ParameterMap parameters = setup.getParameters();
			int runNumber = setup.getRunNumber();
			int numRuns = setup.getNumRuns();
			int rngSeed = setup.getRngSeed();
			results = adapter.run(parameters, runNumber, rngSeed);
			
			/*
			// Stage files listed in run properties back to server (if asked for)
			if(useFileTransfer)
			{
				fts.connect();
				
				StringList outputFiles = setup.getOutputFiles();
				
				for(String outputFile : outputFiles)
				{
					String fileTransferSubpath = setup.getFileTransferSubpath();
					String remotePath = appendPathComponent(fileTransferSubpath, outputFile);
					
					fts.uploadFile(outputFile, remotePath);
				}
				
				fts.disconnect();
			}
			*/
			
			// If file transfer is off, write standard output and standard error
			// to local files.
			// If file transfer is on, this will happen at the client end of things
			/*if(!useFileTransfer)
			{*/
				String rnStr = getRNString(numRuns, runNumber);
				
				String stdoutFilename = "stdout." + rnStr;
				byte[] stdoutData = results.getStdoutData();
				if(stdoutData.length > 0)
					writeData(stdoutFilename, stdoutData);
				
				String stderrFilename = "stderr." + rnStr;
				byte[] stderrData = results.getStderrData();
				if(stderrData.length > 0)
					writeData(stderrFilename, stderrData);
			//}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			results = new RunResults(e); 
		}
		
		// Write results to stdout
		// Set up ObjectOutputStream to write RunResults object
		try
		{
			ObjectOutputStream stdoutStream = new ObjectOutputStream(System.out);
			stdoutStream.writeObject(results);
			stdoutStream.close();
		}
		catch(Exception e) {}
	}
	
	private static String getRNString(int numRuns, int runNumber)
	{
		String maxRNStr = "" + (numRuns - 1);
		String rnNoZerosStr = "" + runNumber;
		
		StringBuffer rnStrBuf = new StringBuffer();
		int diff = maxRNStr.length() - rnNoZerosStr.length();
		for(int i = 0; i < diff; i++)
		{
			rnStrBuf.append("0");
		}
		rnStrBuf.append(rnNoZerosStr);
		return rnStrBuf.toString();
	}

	private static void writeData(String filename, byte[] data) throws IOException
	{
		OutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
		os.write(data);
		os.close();
	}
}
