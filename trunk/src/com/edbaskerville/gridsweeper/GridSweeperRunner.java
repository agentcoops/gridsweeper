package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;
import static com.edbaskerville.gridsweeper.StringUtils.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

/**
 * The GridSweeperRunner command-line tool to actually run the model
 * on the execution host.
 * @author Ed Baskerville
 *
 */
public class GridSweeperRunner
{
	/*
	 * TODO: fix how file transfer works. Input files should be segregated
	 * in their own directory so that upon run completion the entire model output
	 * directory can be retrieved from the file transer system without needlessly
	 * transferring back input files.
	 * Furthermore, there's no reason for output files to be a Properties object:
	 * the path within the working directory should be the same as the path
	 * in the output directory on the server. 
	 */
	
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
		RunResults results;
		try
		{
			// Load RunSetup object
			ObjectInputStream stdinStream = new ObjectInputStream(System.in);
			RunSetup setup = (RunSetup)stdinStream.readObject();
			
			// Get GridSweeper preferences
			Preferences preferences = setup.getPreferences();
			
			// Download input files
			boolean useFileTransfer = !preferences.getBooleanProperty("UseSharedFileSystem");
			FileTransferSystem fts = null;
			if(useFileTransfer)
			{
				fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(preferences);
				fts.connect();
				
				StringMap inputFiles = setup.getInputFiles();
				
				for(Object key : inputFiles.keySet())
				{
					String path = inputFiles.get((String)key);
					String fileTransferSubpath = appendPathComponent(setup.getFileTransferSubpath(), "input");
					
					String remotePath = appendPathComponent(fileTransferSubpath, path);
					String localPath = path;
					
					fts.downloadFile(remotePath, localPath);
				}
				
				fts.disconnect();
			}
			
			String adapterClassName = setup.getAdapterClassName();
			Properties properties = setup.getProperties();
			Adapter adapter = AdapterFactory.createAdapter(adapterClassName, properties);
			
			// Run!
			ParameterMap parameters = setup.getParameters();
			int runNumber = setup.getRunNumber();
			long rngSeed = setup.getRngSeed();
			results = adapter.run(parameters, runNumber, rngSeed);
			
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
		}
		catch(Exception e) {}
	}
}
