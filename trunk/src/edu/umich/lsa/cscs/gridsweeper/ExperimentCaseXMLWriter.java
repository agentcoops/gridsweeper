/*
	ExperimentCaseXMLWriter.java
	
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

import java.io.*;
import java.util.*;

/**
 * Generates XML for a specific experiment case, so it can be
 * reproduced exactly at a later time.
 * @author Ed Baskerville
 *
 */
class ExperimentCaseXMLWriter extends XMLWriter
{
	private ExperimentCase expCase;
	private String name;
	
	/**
	 * Constructor for {@code ExperimentCaseXMLWriter}.
	 * @param path The output path for the XML.
	 * @param experiment The experiment object.
	 * @param expCase The experiment case with parameter settings.
	 * @param caseName A name to write out into the XML.
	 * @param rngSeed The random seed.
	 * @throws FileNotFoundException If the output file cannot be opened.
	 */
	public ExperimentCaseXMLWriter(String path, ExperimentCase expCase, String name) throws FileNotFoundException
	{
		super(path);
		this.expCase = expCase;
		this.name = name;
	}
	
	/**
	 * Outputs the experiment case XML.
	 */
	public void writeXML()
	{
		printDeclaration();
		
		printCaseStart();
		
		printParamValues();
		printRuns();
		
		printCaseEnd();
		
		close();
	}
	
	/*
	 * Prints the start of the experiment tag, with attributes.
	 */
	private void printCaseStart()
	{
		StringMap attrs = new StringMap();
		attrs.put("name", name);
		printTagStart("case", attrs, false);
	}
	
	/**
	 * Prints the end experiment tag.
	 *
	 */
	private void printCaseEnd()
	{
		printTagEnd("case");
	}
	
	/**
	 * Prints XML tags for all parameter assignments.
	 *
	 */
	private void printParamValues()
	{
		ParameterMap paramMap = expCase.getParameterMap();
		
		for(String param: paramMap.keySet())
		{
			String value = paramMap.get(param).toString();
			
			StringMap attrs = new StringMap();
			attrs.put("param", param);
			attrs.put("value", value);
			printTagStart("value", attrs, true);
		}
	}
	
	/**
	 * Print
	 */
	private void printRuns()
	{
		List<Integer> rngSeeds = expCase.getRngSeeds();
		for(int i = 0; i < rngSeeds.size(); i++)
		{
			StringMap attrs = new StringMap();
			attrs.put("number", "" + i);
			attrs.put("rngSeed", rngSeeds.get(i).toString());
			printTagStart("run", attrs, true);
		}
	}
}
