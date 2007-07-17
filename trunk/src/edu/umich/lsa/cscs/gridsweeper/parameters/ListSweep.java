package edu.umich.lsa.cscs.gridsweeper.parameters;

import edu.umich.lsa.cscs.gridsweeper.*;

import java.util.*;

/**
 * Represents a 
 * @author ebaskerv
 *
 */
public class ListSweep extends SingleSweep
{
	private StringList values;
	
	public StringList getValues()
	{
		return values;
	}

	public void setValues(StringList values)
	{
		this.values = values;
	}

	public ListSweep(String name, StringList values)
	{
		super(name);
		this.values = values;
	}
	
	public ListSweep(String name)
	{
		super(name);
		this.values = new StringList();
	}
	
	@Override
	public List<ParameterMap> generateMaps(Random rng)
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		for(String value : values)
		{
			maps.add(new ParameterMap(name, value));
		}
		
		return maps;
	}

	public boolean add(String o)
	{
		return values.add(o);
	}
}
