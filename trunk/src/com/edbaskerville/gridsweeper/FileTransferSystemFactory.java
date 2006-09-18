package com.edbaskerville.gridsweeper;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class FileTransferSystemFactory
{
	private static FileTransferSystemFactory factory;
	
	public static FileTransferSystemFactory getFactory()
	{
		if(factory == null)
		{
			factory = new FileTransferSystemFactory();
		}
		return factory;
	}
	
	public FileTransferSystem getFileTransferSystem(Preferences preferences) throws FileTransferException
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
			throw new FileTransferException("Received an exception getting file system", e);
		}
	}
}
