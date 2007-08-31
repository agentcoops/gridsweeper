/*
	ExperimentTestParse.java
	
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
		experiment = new Experiment(getURL("empty"));
		assertEquals("test_empty", experiment.getName());
	}
	
	@Test
	public void settings() throws GridSweeperException
	{
		experiment = new Experiment(getURL("settings"));
		assertEquals(10, experiment.getNumRuns());
		assertEquals("value", experiment.getSettings().get("anotherSetting"));
	}
	
	@Test
	public void abbrevs() throws GridSweeperException
	{
		experiment = new Experiment(getURL("abbrevs"));
		assertEquals("p1", experiment.getAbbreviations().get("param1"));
		assertEquals("p2", experiment.getAbbreviations().get("param2"));
	}
	
	private URL getURL(String testName)
	{
		return getClass().getResource("ExperimentTestParse_" + testName + ".gsexp");
	}
}
