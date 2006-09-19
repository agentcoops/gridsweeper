package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;
import static com.edbaskerville.gridsweeper.StringUtils.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public class GridSweeperRunner
{
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
				
				Properties inputFiles = setup.getInputFiles();
				
				for(Object key : inputFiles.keySet())
				{
					String path = inputFiles.getProperty((String)key);
					String fileTransferSubpath = setup.getFileTransferSubpath();
					
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
				
				Properties outputFiles = setup.getOutputFiles();
				
				for(Object key : outputFiles.keySet())
				{
					String localPath = (String)key;
					String fileTransferSubpath = setup.getFileTransferSubpath();
					String remotePath = appendPathComponent(fileTransferSubpath, localPath);
					
					fts.uploadFile(localPath, remotePath);
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
