/*
	RunSetup.java
	
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
 * A class that encapsulates setup data for a model run. Includes a copy of the
 * settings to be passed on to the running model, file transfer data, run number,
 * random seed, and the adapter class to use.
 * @author Ed Baskerville
 *
 */
class RunSetup implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Settings settings;
	private StringMap inputFiles;
	private String fileTransferSubpath;
	private ParameterMap parameters;
	private int numRuns;
	private int runNumber;
	private int rngSeed;
	private StringList outputFiles;
	
	public RunSetup(Settings settings, StringMap inputFiles, String fileTransferSubpath, ParameterMap parameters, int numRuns, int runNumber, int rngSeed, StringList outputFiles)
	{
		this.settings = settings;
		this.inputFiles = inputFiles;
		this.fileTransferSubpath = fileTransferSubpath;
		this.parameters = parameters;
		this.numRuns = numRuns;
		this.runNumber = runNumber;
		this.rngSeed = rngSeed;
		this.outputFiles = outputFiles;
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public int getRngSeed()
	{
		return rngSeed;
	}
	
	public int getRunNumber()
	{
		return runNumber;
	}
	
	public int getNumRuns()
	{
		return numRuns;
	}
	
	public ParameterMap getParameters()
	{
		return parameters;
	}

	public StringMap getInputFiles()
	{
		return inputFiles;
	}
	
	public StringList getOutputFiles()
	{
		return outputFiles;
	}
	
	public String getFileTransferSubpath()
	{
		return fileTransferSubpath;
	}
	
	public String toString()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("settings", settings);
		map.put("inputFiles", inputFiles);
		map.put("fileTransferSubpath", fileTransferSubpath);
		map.put("parameters", parameters);
		map.put("numRuns", numRuns);
		map.put("runNumber", "" + runNumber);
		map.put("rngSeed", "" + rngSeed);
		map.put("outputFiles", outputFiles);
		
		return map.toString();
	}
}
