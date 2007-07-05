package com.edbaskerville.gridsweeper;

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
}
