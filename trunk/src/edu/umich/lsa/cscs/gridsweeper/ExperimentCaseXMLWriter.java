package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;
import java.util.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.ParameterMap;

/**
 * Generates XML for a specific experiment case, so it can be
 * reproduced exactly at a later time.
 * @author Ed Baskerville
 *
 */
public class ExperimentCaseXMLWriter
{
	private static String xmlDeclaration = "<?xml version=\"1.0\"?>";
	
	private PrintStream xmlStream;
	private Experiment experiment;
	private ExperimentCase expCase;
	private String caseName;
	private long rngSeed;
	
	/**
	 * Constructor for {@code ExperimentCaseXMLWriter}.
	 * @param path The output path for the XML.
	 * @param experiment The experiment object.
	 * @param expCase The experiment case with parameter settings.
	 * @param caseName A name to write out into the XML.
	 * @param rngSeed The random seed.
	 * @throws FileNotFoundException If the output file cannot be opened.
	 */
	public ExperimentCaseXMLWriter(String path, Experiment experiment, ExperimentCase expCase, String caseName, long rngSeed) throws FileNotFoundException
	{
		this.xmlStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)));
		this.experiment = experiment;
		this.expCase = expCase;
		this.caseName = caseName;
		this.rngSeed = rngSeed;
	}
	
	/**
	 * Outputs the experiment case XML.
	 */
	public void writeXML()
	{
		printDeclaration();
		
		printExperimentStart();
		
		printProperties();
		printInputFiles();
		printOutputFiles();
		printAbbrevs();
		printParamValues();
		
		printExperimentEnd();
		
		xmlStream.close();
	}
	
	/**
	 * Prints the XML declaration line.
	 *
	 */
	private void printDeclaration()
	{
		xmlStream.println(xmlDeclaration);
	}
	
	/*
	 * Prints the start of the experiment tag, with attributes.
	 */
	private void printExperimentStart()
	{
		String name = caseName;
		String numRuns = "1";
		
		StringMap attrs = new StringMap();
		attrs.put("name", name);
		attrs.put("numRuns", numRuns);
		attrs.put("rngSeed", new Long(rngSeed).toString());
		printTagStart(0, "experiment", attrs, false);
	}
	
	/**
	 * Prints the end experiment tag.
	 *
	 */
	private void printExperimentEnd()
	{
		printTagEnd(0, "experiment");
	}
	
	/**
	 * Prints XML tags for all the experiment properties. 
	 *
	 */
	private void printProperties()
	{
		Properties properties = experiment.getSettings();
		
		for(Object settingObj : properties.keySet())
		{
			String key = (String)settingObj;
			String value = properties.getProperty(key);
			
			StringMap attrs = new StringMap();
			attrs.put("key", key);
			attrs.put("value", value);
			printTagStart(1, "property", attrs, true);
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
			printTagStart(1, "input", attrs, true);
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
			printTagStart(1, "output", attrs, true);
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
			printTagStart(1, "abbrev", attrs, true);
		}
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
			printTagStart(1, "value", attrs, true);
		}
	}
	
	/**
	 * Prints a starting XML tag, with optional termination.
	 * @param level The indentation level for this tag.
	 * @param name The tag name.
	 * @param attrs A map of atributes.
	 * @param terminate Whether or not to terminate the tag.
	 */
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
	
	/**
	 * Prints an ending XML tag
	 * @param level The indentation level for this tag.
	 * @param name The tag name.
	 */
	private void printTagEnd(int level, String name)
	{
		for(int i = 0; i < level; i++) xmlStream.print("\t");
		xmlStream.println("</" + name + ">"); 
	}
}
