/*
	ExperimentXMLWriter.java
	
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

import java.io.FileNotFoundException;

class ExperimentXMLWriter extends XMLWriter
{
	private Experiment experiment;
	private boolean writeSeedInfo;
	
	/**
	 * Constructor for {@code ExperimentXMLWriter}.
	 * @param path The output path for the XML.
	 * @param experiment The experiment object.
	 * @param writeSeedInfo Whether or not to write the experiment's RNG seed.
	 * @throws FileNotFoundException If the output file cannot be opened.
	 */
	public ExperimentXMLWriter(String path, Experiment experiment, boolean writeSeedInfo) throws FileNotFoundException
	{
		super(path);
		this.experiment = experiment;
		this.writeSeedInfo = writeSeedInfo;
	}
	
	/**
	 * Outputs the experiment case XML.
	 */
	public void writeXML()
	{
		printDeclaration();
		
		printExperimentStart();
		
		printSettings();
		printInputFiles();
		printOutputFiles();
		printAbbrevs();
		
		printSweeps();
		
		printExperimentEnd();
		
		close();
	}
	
	/*
	 * Prints the start of the experiment tag, with attributes.
	 */
	private void printExperimentStart()
	{
		StringMap attrs = new StringMap();
		attrs.put("name", experiment.getName());
		attrs.put("numRuns", "" + experiment.getNumRuns());
		if(writeSeedInfo)
		{
			attrs.put("firstSeedRow", "" + experiment.getFirstSeedRow());
			attrs.put("seedCol", "" + experiment.getSeedCol());
		}
		printTagStart("experiment", attrs, false);
	}
	
	/**
	 * Prints the end experiment tag.
	 *
	 */
	private void printExperimentEnd()
	{
		printTagEnd("experiment");
	}
	
	/**
	 * Prints XML tags for all the experiment settings. 
	 *
	 */
	private void printSettings()
	{
		Settings settings = experiment.getSettings();
		
		for(String key : settings.keySet())
		{
			String casedKey = settings.getCasedKey(key);
			String value = settings.getProperty(key);
			
			StringMap attrs = new StringMap();
			attrs.put("key", casedKey);
			attrs.put("value", value);
			printTagStart("setting", attrs, true);
		}
	}
	
	/**
	 * Prints XML tags for all the input files. 
	 *
	 */
	private void printInputFiles()
	{
		StringMap inputFiles = experiment.getInputFiles();
		
		for(Object srcObj : inputFiles.keySet())
		{
			String source = (String)srcObj;
			String destination = inputFiles.get(source);
			
			StringMap attrs = new StringMap();
			attrs.put("source", source);
			attrs.put("destination", destination);
			printTagStart("input", attrs, true);
		}
	}
	
	/**
	 * Prints XML tags for all the output files.
	 *
	 */
	private void printOutputFiles()
	{
		StringList outputFiles = experiment.getOutputFiles();
		
		for(String outputFile : outputFiles)
		{
			StringMap attrs = new StringMap();
			attrs.put("path", outputFile);
			printTagStart("output", attrs, true);
		}
	}
	
	/**
	 * Prints XML tags for all the parameter abbreviations.
	 *
	 */
	private void printAbbrevs()
	{
		StringMap abbrevs = experiment.getAbbreviations();
		
		for(Object paramObj : abbrevs.keySet())
		{
			String param = (String)paramObj;
			String abbrev = abbrevs.get(param);
			
			StringMap attrs = new StringMap();
			attrs.put("param", param);
			attrs.put("abbrev", abbrev);
			printTagStart("abbrev", attrs, true);
		}
	}
	
	/**
	 * Prints XML tags for all the sweeps.
	 */
	private void printSweeps()
	{
		for(Sweep sweep : experiment.getRootSweep().getChildren())
		{
			sweep.writeXML(this);
		}
	}
}
