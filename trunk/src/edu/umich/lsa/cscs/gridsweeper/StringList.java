package edu.umich.lsa.cscs.gridsweeper;

import java.util.ArrayList;
import java.util.Collection;

public class StringList extends ArrayList<String>
{
	private static final long serialVersionUID = 1L;

	public StringList()
	{
	}

	public StringList(int capacity)
	{
		super(capacity);
	}

	public StringList(Collection<? extends String> collection)
	{
		super(collection);
	}
	
	public StringList(String[] strings)
	{
		super(strings.length);
		for(String string : strings)
		{
			add(string);
		}
	}
}
