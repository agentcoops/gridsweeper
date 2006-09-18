package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

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
			FileTransferSystem fts = FileTransferSystemFactory.getFactory().getFileTransferSystem(preferences);
			if(useFileTransfer)
			{
				fts.connect();
				
				Properties inputFiles = setup.getInputFiles();
				
				for(Object key : inputFiles.keySet())
				{
					String path = inputFiles.getProperty((String)key);
					String fileTransferSubpath = setup.getFileTransferSubpath();
					
					String remotePath = fileTransferSubpath + "/" + path;
					String localPath = StringUtils.replace(path, Constants.RunNumberPlaceholder);
					
					fts.downloadFile(remotePath, localPath);
				}
				
				fts.disconnect();
			}
			
			Class adapterClass = setup.getAdapterClass();
			Properties properties = setup.getProperties();
			byte[] stdinData = setup.getStdinData();
			Adapter adapter = AdapterFactory.createAdapter(adapterClass, properties, stdinData);
			
			// Run!
			ParameterMap parameters = setup.getParameters();
			int runNumber = setup.getRunNumber();
			long rngSeed = setup.getRngSeed();
			boolean dryRun = setup.isDryRun();
			results = adapter.run(parameters, runNumber, rngSeed, dryRun);
			
			// Stage files listed in run properties back to server (if asked for)
			if(useFileTransfer)
			{
				fts.connect();
				
				Properties outputFiles = setup.getOutputFiles();
				
				for(Object key : outputFiles.keySet())
				{
					String localPath = StringUtils.replace((String)key, Constants.RunNumberPlaceholder);
					String finalPath = outputFiles.getProperty((String)key);
					String fileTransferSubpath = setup.getFileTransferSubpath();
					String remotePath = fileTransferSubpath + "/" + finalPath;
					
					fts.uploadFile(localPath, remotePath);
				}
				
				fts.disconnect();
			}
		}
		catch(Exception e)
		{
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
