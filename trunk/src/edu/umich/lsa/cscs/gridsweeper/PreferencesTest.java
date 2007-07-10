package edu.umich.lsa.cscs.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

public class PreferencesTest
{
	Settings settings;
	
	@Before
	public void setUp()
	{
		settings = Settings.sharedSettings();
	}
	
	@Test
	public void setAndGet()
	{
		settings.setProperty("key", "value");
		assertEquals("value", settings.getProperty("key"));
	}
}
