package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;

public class Preferences extends Properties
{
	private static final long serialVersionUID = 1L;
	
	static Preferences sharedPreferences;
	
	public Preferences()
	{
		super();
	}
	
	public Preferences(Preferences defaults)
	{
		super(defaults);
	}
	
	public static Preferences sharedPreferences()
	{
		if(sharedPreferences == null)
		{
			Preferences defaults = new Preferences();
			
			defaults.setProperty("Root", "/usr/local/gridsweeper");
			
			defaults.setProperty("AdapterClass", "com.edbaskerville.gridsweeper.DroneAdapter");
			
			defaults.setProperty("UseSharedFileSystem", "false");
			defaults.setProperty("EnableFileTransfer", "true");
			defaults.setProperty("FileTransferSystemClass", "com.edbaskerville.gridsweeper.FTPFileTransferSystem");
			
			defaults.setProperty("com.edbaskerville.gridsweeper.FTPFileTransferSystem.Username", "anonymous");
			
			sharedPreferences = new Preferences(defaults);
			try
			{
				sharedPreferences.load(new FileInputStream(System.getProperty("user.home") + "/.gridsweeper"));
				Logger.fine("Loaded user preferences:");
				Logger.fine(sharedPreferences.toString());
			}
			catch(FileNotFoundException e) {}
			catch(IOException e) {}
		}
		
		return sharedPreferences;
	}
	
	public boolean getBooleanProperty(String key)
	{
		return Boolean.parseBoolean(getProperty(key));
	}

	public Properties getPropertiesForClass(String className)
	{
		Properties properties = new Properties();
		String classNamePlusDot = className + ".";
		
		for(Object key : properties.keySet())
		{
			String propName = (String)key;
			if(propName.indexOf(classNamePlusDot) == 0)
			{
				String shortKey = propName.substring(classNamePlusDot.length());
				properties.setProperty(shortKey, getProperty(propName));
			}
		}
		
		return properties;
	}
}
