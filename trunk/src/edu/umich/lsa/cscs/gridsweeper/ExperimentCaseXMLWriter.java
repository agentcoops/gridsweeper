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
		List<BigInteger> rngSeeds = expCase.getRngSeeds();
		for(int i = 0; i < rngSeeds.size(); i++)
		{
			StringMap attrs = new StringMap();
			attrs.put("number", "" + i);
			attrs.put("rngSeed", rngSeeds.get(i).toString());
			printTagStart("run", attrs, true);
		}
	}
}
