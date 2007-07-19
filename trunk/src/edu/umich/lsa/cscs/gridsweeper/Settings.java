package edu.umich.lsa.cscs.gridsweeper;

import java.util.*;
import java.io.*;
import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

// TODO: make settings objects case-insensitive by overriding setProperty, getProperty, etc.

/**
 * <p>A class to handle user settings. Provides a shared instance that
 * includes standard values for user settings. Settings for plugins
 * are scoped using their reverse-DNS class names, e.g.,
 * {@code edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username}.
 * {@code Settings} is a subclass of {@code java.util.Properties},
 * and simply adds default values and a couple convenience methods.
 * Settings included by default:</p>
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
 * <td>@{code UseFileTransfer}</td> <td>{@code false}</td>
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
public class Settings extends Properties
{
	private static final long serialVersionUID = 1L;
	
	static final Settings defaultSettings;
	static Settings sharedSettings;
	
	static
	{
		defaultSettings = new Settings();
		
		defaultSettings.setProperty("RootDirectory", "/usr/local/gridsweeper");
		defaultSettings.setProperty("ExperimentsDirectory", "~/Experiments");
		
		defaultSettings.setProperty("AdapterClass", "edu.umich.lsa.cscs.gridsweeper.DroneAdapter");
		
		defaultSettings.setProperty("UseFileTransfer", "false");
		defaultSettings.setProperty("FileTransferSystemClass", "edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem");
		
		defaultSettings.setProperty("edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username", "anonymous");
	}
	
	/** 
	 * Default constructor, simply calls the superclass implementation.
	 *
	 */
	public Settings()
	{
		super();
	}
	
	/**
	 * One-argument constructor with defaults, simpily calls the superclass implementation.
	 * @param defaults
	 */
	public Settings(Properties defaults)
	{
		super(defaults);
	}
	
	/**
	 * Returns the shared settings object, creating it if it does not yet exist.
	 * Upon initial creation, the object is initialized with hard-coded default values.
	 * @return The shared settings object.
	 */
	public static Settings sharedSettings()
	{
		if(sharedSettings == null)
		{
			sharedSettings = new Settings(defaultSettings);
			try
			{
				sharedSettings.load(new FileInputStream(System.getProperty("user.home") + "/.gridsweeper"));
				fine("Loaded user settings:");
				fine(sharedSettings.toString());
			}
			catch(FileNotFoundException e) {}
			catch(IOException e) {}
		}
		
		return sharedSettings;
	}
	
	public static Settings defaultSettings()
	{
		return defaultSettings;
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
	 * Extracts all the settings specific to a given class and returns them
	 * in a new {@code Settings} object with the class prefix removed from 
	 * keys.
	 * @param className The name of the class.
	 * @return The settings object for this class.
	 */
	public Settings getSettingsForClass(String className)
	{
		finer("Getting settings for class " + className);
		
		Settings settings = new Settings();
		String classNamePlusDot = className + ".";
		
		for(Object key : keySet())
		{
			String propName = (String)key;
			finer("checking property " + propName);
			if(propName.indexOf(classNamePlusDot) == 0)
			{
				String shortKey = propName.substring(classNamePlusDot.length());
				settings.setProperty(shortKey, getProperty(propName));
			}
		}
		
		return settings;
	}
	
	public String getSetting(String name)
	{
		return getProperty(name);
	}

	public void putAllForClass(Settings settings, String className)
	{
		// Load adapter settings by prepending class prefix
		for(Object keyObj : settings.keySet())
		{
			String key = (String)keyObj;
			setProperty(className + "." + key, settings.getProperty(key));
		}
		// TODO Auto-generated method stub
		
	}
}
