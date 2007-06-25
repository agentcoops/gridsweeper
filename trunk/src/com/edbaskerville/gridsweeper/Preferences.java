package com.edbaskerville.gridsweeper;

import java.util.*;
import java.io.*;

/**
 * <p>A class to handle user preferences. Provides a shared instance that
 * includes standard values for user preferences. Preferences for plugins
 * are scoped using their reverse-DNS class names, e.g.,
 * {@code edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username}.
 * {@code Preferences} is a subclass of {@code java.util.Properties},
 * and simply adds default values and a couple convenience methods.
 * Preferences included by default:</p>
 * 
 * <table>
 * 
 * <tr>
 * <td>Preference Name</td> <td>Default Value</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code RootDirectory}</td> <td>{@code /usr/local/gridsweeper}</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code ExperimentsDirectory}</td> <td>{@code ~/Experiments}</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code AdapterClass}</td> <td>{@code edu.umich.lsa.cscs.gridsweeper.DroneAdapter}</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code UseSharedFileSystem}</td> <td>{@code true}</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code FileTransferSystemClass}</td> <td>{@code edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem}</td>
 * </tr>
 * 
 * <tr>
 * <td>@{code edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username</td> <td>@{code anonymous}</td>
 * </tr>
 * 
 * </table>
 * @author Ed Baskerville
 *
 */
public class Preferences extends Properties
{
	private static final long serialVersionUID = 1L;
	
	static Preferences sharedPreferences;
	
	/** 
	 * Default constructor, simply calls the superclass implementation.
	 *
	 */
	public Preferences()
	{
		super();
	}
	
	/**
	 * One-argument constructor with defaults, simpily calls the superclass implementation.
	 * @param defaults
	 */
	public Preferences(Preferences defaults)
	{
		super(defaults);
	}
	
	/**
	 * Returns the shared preferences object, creating it if it does not yet exist.
	 * Upon initial creation, the object is initialized with hard-coded default values.
	 * @return The shared preferences object.
	 */
	public static Preferences sharedPreferences()
	{
		if(sharedPreferences == null)
		{
			Preferences defaults = new Preferences();
			
			defaults.setProperty("RootDirectory", "/usr/local/gridsweeper");
			defaults.setProperty("ExperimentsDirectory", "~/Experiments");
			
			defaults.setProperty("AdapterClass", "com.edbaskerville.gridsweeper.DroneAdapter");
			
			defaults.setProperty("UseSharedFileSystem", "true");
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
	
	/**
	 * Gets a property, treating the string value as a boolean as per
	 * the {@code java.lang.Boolean#parseBoolean(String)} method.
	 * The value is {@code true} if and only if the string is equal,
	 * ignoring case, to {@code "true"}.
	 * @param key The key of the property to parse.
	 * @return The boolean value of the string.
	 */
	public boolean getBooleanProperty(String key)
	{
		return Boolean.parseBoolean(getProperty(key));
	}
	
	/**
	 * Extracts all the preferences specific to a given class and returns them
	 * in a new {@code Properties} object with the class prefix removed from 
	 * the key.
	 * @param className The name of the class.
	 * @return The properties object for this class.
	 */
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
