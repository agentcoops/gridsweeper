package com.edbaskerville.gridsweeper;

import java.util.*;

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
			
			// TODO: set up defaults
			
			sharedPreferences = new Preferences(defaults);
		}
		
		return sharedPreferences;
	}
}
