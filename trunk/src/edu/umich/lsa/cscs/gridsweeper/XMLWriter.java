package edu.umich.lsa.cscs.gridsweeper;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class XMLWriter
{
	private static String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private PrintStream xmlStream;
	private int level = 0;
	
	public XMLWriter(String path) throws FileNotFoundException
	{
		this.xmlStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)));	
	}
	
	/**
	 * Prints the XML declaration line.
	 *
	 */
	public void printDeclaration()
	{
		xmlStream.println(xmlDeclaration);
	}
	
	/**
	 * Prints a starting XML tag, with optional termination.
	 * @param name The tag name.
	 * @param attrs A map of atributes.
	 * @param terminate Whether or not to terminate the tag.
	 */
	public void printTagStart(String name, StringMap attrs, boolean terminate)
	{
		for(int i = 0; i < level; i++) xmlStream.print("\t");
		xmlStream.print("<" + name);
		
		if(attrs != null) for(String attr : attrs.keySet())
		{
			xmlStream.print(" " + attr + "=\"" + attrs.get(attr) + "\"");
		}
		
		if(terminate) xmlStream.print("/");
		else level++;
		
		xmlStream.println(">");
	}
	
	/**
	 * Prints an ending XML tag
	 * @param level The indentation level for this tag.
	 * @param name The tag name.
	 */
	public void printTagEnd(String name)
	{
		level--;
		for(int i = 0; i < level; i++) xmlStream.print("\t");
		xmlStream.println("</" + name + ">");
	}
	
	public void close()
	{
		xmlStream.close();
	}
}
