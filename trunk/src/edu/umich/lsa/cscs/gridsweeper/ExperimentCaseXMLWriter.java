package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.ParameterMap;

/**
 * Generates XML for a specific experiment case, so it can be
 * reproduced exactly at a later time.
 * @author Ed Baskerville
 *
 */
public class ExperimentCaseXMLWriter extends XMLWriter
{
	private Experiment experiment;
	private ExperimentCase expCase;
	private String caseName;
	private BigInteger rngSeed;
	
	/**
	 * Constructor for {@code ExperimentCaseXMLWriter}.
	 * @param path The output path for the XML.
	 * @param experiment The experiment object.
	 * @param expCase The experiment case with parameter settings.
	 * @param caseName A name to write out into the XML.
	 * @param rngSeed The random seed.
	 * @throws FileNotFoundException If the output file cannot be opened.
	 */
	public ExperimentCaseXMLWriter(String path, Experiment experiment, ExperimentCase expCase, String caseName, BigInteger rngSeed) throws FileNotFoundException
	{
		super(path);
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
		
		printCaseStart();
		
		printSettings();
		printInputFiles();
		printOutputFiles();
		printAbbrevs();
		printParamValues();
		
		printCaseEnd();
		
		close();
	}
	
	/*
	 * Prints the start of the experiment tag, with attributes.
	 */
	private void printCaseStart()
	{
		String name = caseName;
		
		StringMap attrs = new StringMap();
		attrs.put("name", name);
		attrs.put("rngSeed", rngSeed.toString());
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
	 * Prints XML tags for all the experiment settings. 
	 *
	 */
	private void printSettings()
	{
		Settings settings = experiment.getSettings();
		
		for(Object settingObj : settings.keySet())
		{
			String key = (String)settingObj;
			String value = settings.getProperty(key);
			
			StringMap attrs = new StringMap();
			attrs.put("key", key);
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
}
