package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

public class PreferencesTest
{
	Preferences preferences;
	
	@Before
	public void setUp()
	{
		preferences = Preferences.sharedPreferences();
	}
	
	@Test
	public void setAndGet()
	{
		preferences.setProperty("key", "value");
		assertEquals("value", preferences.getProperty("key"));
	}
}
