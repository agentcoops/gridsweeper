package edu.umich.lsa.cscs.gridsweeper;

import java.math.BigDecimal;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import edu.umich.lsa.cscs.gridsweeper.parameters.*;

import static edu.umich.lsa.cscs.gridsweeper.DLogger.*;

/**
 * The SAX-based XML parser for experiment XML (.gsexp) files.
 * TODO: Write a complete specification for the file format. For now, see the example files. 
 * @author Ed Baskerville
 *
 */
public class ExperimentXMLHandler extends DefaultHandler
{
	private List<Object> stack;
	private Experiment experiment;
	
	private Locator locator;

	/**
	 * An enum representing the supported XML tags.
	 * @author Ed Baskerville
	 *
	 */
	private enum Tag
	{
		SETTING,
		ABBREV,
		ITEM,
		INPUT,
		OUTPUT
	}
	
	/**
	 * Default constructor.
	 * @param experiment The experiment to write values into.
	 */
	public ExperimentXMLHandler(Experiment experiment)
	{
		this.experiment = experiment;
		stack = new ArrayList<Object>();
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument()
	{
		fine("beginning parsing");
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException
	{
		fine("ending parsing");
		if(peek() != null)
		{
			SAXException exception = new SAXParseException("Encountered end of document before closing experiment tag.", locator); 
			throw exception;
		}
	}

	/**
	 * Parses the start tag of an XML element. Performs error checking
	 * and updates the parse stack and the Experiment object as appropriate.
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		finer("startElement: " + qName);
		
		StringMap attrMap = getMapFromAttributes(attributes);
		Object top = peek();
		
		if(qName.equals("experiment"))
		{
			if(top != null)
				throwUnexpected("experiment");
			
			experiment.setName(attrMap.get("name"));
			
			String numRunsStr = attrMap.get("numRuns");
			if(numRunsStr != null)
			{
				experiment.setNumRuns(Integer.parseInt(numRunsStr));
			}
			
			String firstSeedRowStr = attrMap.get("firstSeedRow");
			if(firstSeedRowStr != null)
			{
				experiment.setFirstSeedRow(Integer.parseInt(firstSeedRowStr));
			}
			
			String seedColStr = attrMap.get("seedCol");
			if(seedColStr != null)
			{
				experiment.setSeedCol(Integer.parseInt(seedColStr));
			}
			
			push(experiment);
		}
		else if(qName.equals("input"))
		{
			if(top != experiment)
				throwUnexpected("input");
			
			String source = attrMap.get("source");
			String destination = attrMap.get("destination");
			
			if(source == null)
				throwMissing("input", "source");
			if(destination == null)
				throwMissing("input", "destination");
			
			experiment.getInputFiles().put(source, destination);
			
			push(Tag.INPUT);
		}
		else if(qName.equals("output"))
		{
			if(top != experiment)
				throwUnexpected("output");
			
			String path = attrMap.get("path");
			
			if(path == null)
				throwMissing("output", "path");
			
			experiment.getOutputFiles().add(path);
			
			push(Tag.OUTPUT);
		}
		else if(qName.equals("setting"))
		{
			if(top != experiment)
				throwUnexpected("setting");
			
			String key = attrMap.get("key");
			String value = attrMap.get("value");
			
			if(key == null)
				throwMissing("setting", "key");
			if(value == null)
				throwMissing("setting", "value");
			
			experiment.getSettings().put(key, value);
			
			push(Tag.SETTING);
		}
		else if(qName.equals("abbrev"))
		{
			if(top != experiment)
				throwUnexpected("abbrev");
			
			String param = attrMap.get("param");
			String abbrev = attrMap.get("abbrev");
			
			if(param == null)
				throwMissing("abbrev", "param");
			if(abbrev == null)
				throwMissing("abbrev", "abbrev");
			
			experiment.getAbbreviations().put(param, abbrev);
			push(Tag.ABBREV);
		}
		else if(qName.equals("value"))
		{
			startSweepElement(qName, attrMap);
		}
		else if(qName.equals("list"))
		{
			startSweepElement(qName, attrMap);
		}
		else if(qName.equals("item"))
		{
			if(!(top instanceof ListSweep))
				throwUnexpected("item");
			ListSweep listSweep = (ListSweep)top;
			
			String value = attrMap.get("value");
			
			if(value == null)
				throwMissing("item", "value");
			
			listSweep.add(value);
			
			push(Tag.ITEM);
		}
		else if(qName.equals("range"))
		{
			startSweepElement(qName, attrMap);
		}
		else if(qName.equals("multiplicative"))
		{
			startSweepElement(qName, attrMap);
		}
		else if(qName.equals("parallel"))
		{
			startSweepElement(qName, attrMap);
		}
		else
		{
			throwUnknown("qName");
		}
	}

	/**
	 * Parses the end tag of an XML element. Performs error checking and updates
	 * the state of the stack and Experiment object as appropriate.
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		finer("endElement: " + qName);
		Object top = peek();
		
		if(top == null)
			throw new SAXParseException("end tag found with empty stack", locator);
		
		if(qName.equals("experiment"))
		{
			if(top != experiment)
				throw new SAXParseException("mismatched experiment end tag", locator);
		}
		else if(qName.equals("input"))
		{
			if(top != Tag.INPUT)
				throw new SAXParseException("mismatched input end tag", locator);
		}
		else if(qName.equals("output"))
		{
			if(top != Tag.OUTPUT)
				throw new SAXParseException("mismatched input end tag", locator);
		}
		else if(qName.equals("setting"))
		{
			if(top != Tag.SETTING)
				throw new SAXParseException("mismatched setting end tag", locator);
		}
		else if(qName.equals("abbrev"))
		{
			if(top != Tag.ABBREV)
				throw new SAXParseException("mismatched abbrev end tag", locator);
		}
		else if(qName.equals("value"))
		{
			if(!(top instanceof SingleValueSweep))
				throw new SAXParseException("mismatched value end tag", locator);
		}
		else if(qName.equals("list"))
		{
			if(!(top instanceof ListSweep))
				throw new SAXParseException("mismatched list end tag", locator);
		}
		else if(qName.equals("item"))
		{
			if(top != Tag.ITEM)
				throw new SAXParseException("mismatched item end tag", locator);
		}
		else if(qName.equals("range"))
		{
			if(!(top instanceof RangeListSweep))
				throw new SAXParseException("mismatched range end tag", locator);
		}
		else if(qName.equals("multiplicative"))
		{
			if(!(top instanceof MultiplicativeCombinationSweep))
				throw new SAXParseException("mismatched multiplicative end tag", locator);
		}
		else if(qName.equals("parallel"))
		{
			if(!(top instanceof ParallelCombinationSweep))
				throw new SAXParseException("mismatched parallel end tag", locator);
		}
		else
		{
			throw new SAXParseException("unknown end tag " + qName, locator);
		}
		
		pop();
	}
    
	/**
	 * Converts an XML attributes object to a simple string map.
	 * @param attributes The XML attributes object.
	 * @return A string map representing the attributes.
	 */
    private StringMap getMapFromAttributes(Attributes attributes)
    {
        StringMap expressionMap = new StringMap();
        int length = attributes.getLength();
        
        for(int i = 0; i < length; i++)
        {
            expressionMap.put(attributes.getQName(i), attributes.getValue(i));
        }
        
        return expressionMap;
    }
    
    /**
     * Peeks at the top of the parse stack without removing the top object.
     * @return The top object on the stack, or {@code null} if the stack is empty.
     */
    private Object peek()
    {
    	if(stack.size() > 0) return stack.get(stack.size() - 1);
    	return null;
    }

    /**
     * Pushes an object to the top of the parse stack.
     * @param obj The object to push.
     */
	private void push(Object obj)
	{
		stack.add(obj);
	}
	
	/**
	 * Pops the top object off of the stack.
	 */
	private void pop()
	{
		if(stack.size() > 0) stack.remove(stack.size() - 1);
	}
	
	/**
	 * Parses a sweep element, passing the parent on the appropriate method for the
	 * appropriate type of sweep.
	 * @param qName The tag name.
	 * @param attrMap The attributes of the sweep.
	 * @throws SAXException
	 */
	private void startSweepElement(String qName, StringMap attrMap) throws SAXException
	{
		Object top = peek();
		
		CombinationSweep parent;
		if(top == experiment)
		{
			parent = experiment.getRootSweep();
		}
		else if(top instanceof CombinationSweep)
		{
			parent = (CombinationSweep)top;
		}
		else
			throw new SAXParseException("value tag with non-sweep on top of stack", locator);
		
		if(qName.equals("value"))
		{
			startValueElement(parent, attrMap);
		}
		else if(qName.equals("list"))
		{
			startListElement(parent, attrMap);
		}
		else if(qName.equals("range"))
		{
			startRangeElement(parent, attrMap);
		}
		else if(qName.equals("multiplicative"))
		{
			startMultiplicativeElement(parent, attrMap);
		}
		else if(qName.equals("parallel"))
		{
			startParallelElement(parent, attrMap);
		}
	}
	
	private void startValueElement(CombinationSweep parent, StringMap attrMap) throws SAXException
	{
		String param = attrMap.get("param");
		String value = attrMap.get("value");
		
		if(param == null)
			throw new SAXParseException("param attribute missing from value tag", locator);
		if(value == null)
			throw new SAXParseException("value attribute missing from value tag", locator);
		
		SingleValueSweep sweep = new SingleValueSweep(param, value); 
		parent.add(sweep);
		push(sweep);
	}
	
	/**
	 * Parses the start tag of a list sweep.
	 * @param parent The sweep's parent.
	 * @param attrMap The sweep's attributes.
	 * @throws SAXException If the parameter name attribute is missing.
	 */
	private void startListElement(CombinationSweep parent, StringMap attrMap) throws SAXException
	{
		String param = attrMap.get("param");
		
		if(param == null)
			throw new SAXParseException("param attribute missing from list tag", locator);
		
		ListSweep listSweep = new ListSweep(param);
		parent.add(listSweep);
		
		push(listSweep);		
	}
	
	/**
	 * Parses the start tag of a range sweep.
	 * @param parent The sweep's parent.
	 * @param attrMap The sweep's attributes.
	 * @throws SAXException If any required attributes are missing.
	 */
	private void startRangeElement(CombinationSweep parent, StringMap attrMap) throws SAXException
	{
		String param = attrMap.get("param");
		String start = attrMap.get("start");
		String end = attrMap.get("end");
		String increment = attrMap.get("increment");
		
		if(param == null)
			throw new SAXParseException("param attribute missing from range tag", locator);
		if(start== null)
			throw new SAXParseException("start attribute missing from range tag", locator);
		if(end == null)
			throw new SAXParseException("end attribute missing from range tag", locator);
		if(increment == null)
			throw new SAXParseException("increment attribute missing from range tag", locator);
		
		try
		{
			RangeListSweep sweep = new RangeListSweep(param, new BigDecimal(start),
					new BigDecimal(end), new BigDecimal(increment)); 
			parent.add(sweep);
			push(sweep);
		}
		catch(NumberFormatException e)
		{
			throw new SAXParseException("badly formatted number in range tag", locator);
		}
	}
	
	/**
	 * Parses the start of a multiplicative combination sweep element.
	 * @param parent The sweep's parent.
	 * @param attrMap The sweep's attributes.
	 * @throws SAXException Currently, never.
	 */
	private void startMultiplicativeElement(CombinationSweep parent, StringMap attrMap) throws SAXException
	{
		MultiplicativeCombinationSweep sweep = new MultiplicativeCombinationSweep();
		parent.add(sweep);
		push(sweep);
	}
	
	/**
	 * Parses the start of a parallel sweep.
	 * @param parent The sweep's parent.
	 * @param attrMap The sweep's attributes
	 * @throws SAXException Currently, never.
	 */
	private void startParallelElement(CombinationSweep parent, StringMap attrMap) throws SAXException
	{
		ParallelCombinationSweep sweep = new ParallelCombinationSweep();
		parent.add(sweep);
		push(sweep);
	}
	
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}
	
	private void throwUnexpected(String tag) throws SAXParseException
	{
		throw new SAXParseException("Unexpected \"" + tag + "\" element.", locator);
	}
	
	private void throwMissing(String tag, String attribute) throws SAXParseException
	{
		throw new SAXParseException("Missing \"" + attribute + "\" attribute from \""
				+ tag + "\" element.", locator);
	}
	
	private void throwUnknown(String tag) throws SAXParseException
	{
		throw new SAXParseException("Unknown element \"" + tag + "\".", locator);
	}
}
