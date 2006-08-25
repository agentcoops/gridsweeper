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
			boolean enableFileTransfer = preferences.getBooleanProperty("EnableFileTransfer");
			if(enableFileTransfer)
			{
				FileTransferSystem fts = getFileTransferSystem(preferences);
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
			
			
			// Set up adapter object
			Class adapterClass = setup.getAdapterClass();
			Class[] parameterTypes = new Class[] { Properties.class, byte[].class };
			Constructor adapterConstructor = adapterClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { setup.getProperties(), setup.getStdinData() };
			
			Adapter adapter = (Adapter)adapterConstructor.newInstance(initargs);
			
			// Run!
			ParameterMap parameters = setup.getParameters();
			int runNumber = setup.getRunNumber();
			long rngSeed = setup.getRngSeed();
			boolean dryRun = setup.isDryRun();
			results = adapter.run(parameters, runNumber, rngSeed, dryRun);
			
			// Stage files listed in run properties back to server (if asked for)
			if(enableFileTransfer)
			{
				FileTransferSystem fts = getFileTransferSystem(preferences);
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

	private static FileTransferSystem getFileTransferSystem(Preferences preferences)
	{
		String className = preferences.getProperty("FileTransferSystemClass");
		Properties ftpProperties = preferences.getPropertiesForClass(className);
		
		try
		{
			Class ftsClass = Class.forName(preferences.getProperty("FileTransferSystemClass"));
			Class[] parameterTypes = new Class[] { Properties.class };
			Constructor constructor = ftsClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { ftpProperties };
			FileTransferSystem fts = (FileTransferSystem)constructor.newInstance(initargs);
			
			return fts;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
