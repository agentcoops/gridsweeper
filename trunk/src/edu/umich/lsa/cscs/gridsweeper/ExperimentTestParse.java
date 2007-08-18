package edu.umich.lsa.cscs.gridsweeper;


import java.net.URL;
import org.junit.*;
import static org.junit.Assert.*;

public class ExperimentTestParse
{
	URL fileURL;
	Experiment experiment;

	@Test
	public void empty() throws GridSweeperException
	{
		experiment = new Experiment(null, getURL("empty"));
		assertEquals("test_empty", experiment.getName());
	}
	
	@Test
	public void properties() throws GridSweeperException
	{
		experiment = new Experiment(null, getURL("properties"));
		assertEquals(10, experiment.getNumRuns());
		assertEquals("value", experiment.getSettings().get("anotherProperty"));
	}
	
	@Test
	public void abbrevs() throws GridSweeperException
	{
		experiment = new Experiment(null, getURL("abbrevs"));
		assertEquals("p1", experiment.getAbbreviations().get("param1"));
		assertEquals("p2", experiment.getAbbreviations().get("param2"));
	}
	
	private URL getURL(String testName)
	{
		return getClass().getResource("ExperimentTestParse_" + testName + ".gsweep");
	}
}
