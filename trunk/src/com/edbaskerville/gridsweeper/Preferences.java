package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;

public class Preferences extends Properties
{
	private static final long serialVersionUID = 1L;
	
	static Preferences sharedPreferences;
	
	private Preferences()
	{
		super();
	}
	
	private Preferences(Preferences defaults)
	{
		super(defaults);
	}
	
	public static Preferences sharedPreferences()
	{
		if(sharedPreferences == null)
		{
			Preferences defaults = new Preferences();
			
			defaults.setProperty("Root", "/usr/local/gridsweeper");
			
			defaults.setProperty("UseSharedFileSystem", "false");
			defaults.setProperty("FileTransferSystem", "FTP");
			
			defaults.setProperty("FTPUsername", "anonymous");
			
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
}
