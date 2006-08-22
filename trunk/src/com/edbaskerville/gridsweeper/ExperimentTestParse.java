package com.edbaskerville.gridsweeper;


import java.net.URL;
import org.junit.*;
import static org.junit.Assert.*;

public class ExperimentTestParse
{
	URL fileURL;
	Experiment experiment;

	@Test
	public void empty() throws ExperimentException
	{
		experiment = new Experiment(getURL("empty"));
		assertEquals("null", experiment.getType());
		assertEquals("empty", experiment.getName());
	}
	
	@Test
	public void settings() throws ExperimentException
	{
		experiment = new Experiment(getURL("settings"));
		assertEquals(10, experiment.getNumRuns());
		assertEquals("~/Results", experiment.getResultsDir());
		assertEquals("value", experiment.getProperties().get("anotherSetting"));
	}
	
	@Test
	public void abbrevs() throws ExperimentException
	{
		experiment = new Experiment(getURL("abbrevs"));
		assertEquals("p1", experiment.getAbbreviations().get("param1"));
		assertEquals("p2", experiment.getAbbreviations().get("param2"));
	}
	
	private URL getURL(String testName)
	{
		return getClass().getResource("ExperimentTestParse_" + testName + ".gsweep");
	}
}
