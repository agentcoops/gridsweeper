package com.edbaskerville.gridsweeper;

import java.io.*;
import java.util.*;

import com.edbaskerville.gridsweeper.parameters.ParameterMap;

public class ExperimentCaseXMLWriter
{
	private static String xmlDeclaration = "<?xml version=\"1.0\"?>";
	
	private PrintStream xmlStream;
	private Experiment experiment;
	private ExperimentCase expCase;
	private String caseName;
	private long rngSeed;
	
	public ExperimentCaseXMLWriter(String path, Experiment experiment, ExperimentCase expCase, String caseName, long rngSeed) throws FileNotFoundException
	{
		this.xmlStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)));
		this.experiment = experiment;
		this.expCase = expCase;
		this.caseName = caseName;
		this.rngSeed = rngSeed;
	}
	
	public void writeXML()
	{
		printDeclaration();
		
		printExperimentStart();
		
		printSettings();
		printInputFiles();
		printOutputFiles();
		printAbbrevs();
		printParamValues();
		
		printExperimentEnd();
		
		xmlStream.close();
	}
	
	private void printDeclaration()
	{
		xmlStream.println(xmlDeclaration);
	}
	
	private void printExperimentStart()
	{
		String name = experiment.getName()
			+ "-" + caseName;
		String numRuns = "1";
		
		StringMap attrs = new StringMap();
		attrs.put("name", name);
		attrs.put("numRuns", numRuns);
		attrs.put("rngSeed", new Long(rngSeed).toString());
		printTagStart(0, "experiment", attrs, false);
	}
	
	private void printExperimentEnd()
	{
		printTagEnd(0, "experiment");
	}
	
	private void printSettings()
	{
		Properties settings = experiment.getProperties();
		
		for(Object settingObj : settings.keySet())
		{
			String key = (String)settingObj;
			String value = settings.getProperty(key);
			
			StringMap attrs = new StringMap();
			attrs.put("key", key);
			attrs.put("value", value);
			printTagStart(1, "setting", attrs, true);
		}
	}
	
	private void printInputFiles()
	{
		Properties inputFiles = experiment.getInputFiles();
		
		for(Object srcObj : inputFiles.keySet())
		{
			String source = (String)srcObj;
			String destination = inputFiles.getProperty(source);
			
			StringMap attrs = new StringMap();
			attrs.put("source", source);
			attrs.put("destination", destination);
			printTagStart(1, "input", attrs, true);
		}
	}
	
	private void printOutputFiles()
	{
		Properties outputFiles = experiment.getOutputFiles();
		
		for(Object srcObj : outputFiles.keySet())
		{
			String source = (String)srcObj;
			String destination = outputFiles.getProperty(source);
			
			StringMap attrs = new StringMap();
			attrs.put("source", source);
			attrs.put("destination", destination);
			printTagStart(1, "output", attrs, true);
		}
	}
	
	private void printAbbrevs()
	{
		Properties abbrevs = experiment.getAbbreviations();
		
		for(Object paramObj : abbrevs.keySet())
		{
			String param = (String)paramObj;
			String abbrev = abbrevs.getProperty(param);
			
			StringMap attrs = new StringMap();
			attrs.put("param", param);
			attrs.put("abbrev", abbrev);
			printTagStart(1, "abbrev", attrs, true);
		}
	}
	
	private void printParamValues()
	{
		ParameterMap paramMap = expCase.getParameterMap();
		
		for(String param: paramMap.keySet())
		{
			String value = paramMap.get(param).toString();

			StringMap attrs = new StringMap();
			attrs.put("param", param);
			attrs.put("value", value);
			printTagStart(1, "value", attrs, true);
		}
	}
	
	private void printTagStart(int level, String name, StringMap attrs, boolean terminate)
	{
		for(int i = 0; i < level; i++) xmlStream.print("\t");
		xmlStream.print("<" + name);
		
		for(String attr : attrs.keySet())
		{
			xmlStream.print(" " + attr + "=\"" + attrs.get(attr) + "\"");
		}
		
		if(terminate) xmlStream.print("/");
		xmlStream.println(">");
	}
	
	private void printTagEnd(int level, String name)
	{
		for(int i = 0; i < level; i++) xmlStream.print("\t");
		xmlStream.println("</" + name + ">"); 
	}
}
