package edu.umich.lsa.cscs.gridsweeper;


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
		assertEquals("test_empty", experiment.getName());
	}
	
	@Test
	public void properties() throws ExperimentException
	{
		experiment = new Experiment(getURL("properties"));
		assertEquals(10, experiment.getNumRuns());
		assertEquals("value", experiment.getProperties().get("anotherProperty"));
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
