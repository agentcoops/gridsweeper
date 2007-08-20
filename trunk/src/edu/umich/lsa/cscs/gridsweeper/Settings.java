/*
	Settings.java
	
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

import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

/**
 * <p>A class to handle user settings. Provides a shared instance that
 * includes standard values for user settings. Settings for plugins
 * are scoped using their reverse-DNS class names, e.g.,
 * {@code edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username}.
 * {@code Settings} is a subclass of {@code StringMap},
 * and adds case-insensitivity (with preservation).
 * 
 * @author Ed Baskerville
 *
 */
public class Settings extends StringMap
{
	private static final long serialVersionUID = 1L;
	static Settings sharedSettings;
	
	StringMap casedKeys = new StringMap();
	
	/**
	 * Returns the shared settings object, creating it if it does not yet exist.
	 * Upon initial creation, the object is initialized with hard-coded default values.
	 * @return The shared settings object.
	 */
	public static Settings sharedSettings()
	{
		if(sharedSettings == null)
		{
			sharedSettings = new Settings();
		}
		
		return sharedSettings;
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
	
	public String getProperty(String key, String defaultValue)
	{
		String value = get(key);
		if(value == null) value = defaultValue;
		return value;
	}
	
	public String getProperty(String key)
	{
		return get(key);
	}
	
	public void setProperty(String key, String value)
	{
		put(key, value);
	}

	public String getSetting(String name)
	{
		return getProperty(name);
	}
	
	@Override
	public String get(Object key)
	{
		if(!(key instanceof String)) return null;
		return super.get(((String)key).toLowerCase());
	}
	
	@Override
	public String put(String key, String value)
	{
		String lcKey = key.toLowerCase();
		casedKeys.put(lcKey, key);
		return super.put(lcKey, value);
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		if(!(key instanceof String)) return false;
		String lcKey = ((String)key).toLowerCase();
		return super.containsKey(lcKey);
	}

	@Override
	public String remove(Object key)
	{
		if(!(key instanceof String)) return null;
		String lcKey = ((String)key).toLowerCase();
		casedKeys.remove(lcKey);
		return super.remove(lcKey);
	}

	public void putAllForClass(Settings settings, String className)
	{
		// Load adapter settings by prepending class prefix
		for(Object keyObj : settings.keySet())
		{
			String key = (String)keyObj;
			setProperty(className + "." + key, settings.getProperty(key));
		}
	}

	public boolean getBooleanProperty(String string, boolean defaultValue)
	{
		String value = getProperty(string);
		if(value == null) return defaultValue;
		return Boolean.parseBoolean(value);
	}

	public String getCasedKey(String key)
	{
		return casedKeys.get(key);
	}
}
