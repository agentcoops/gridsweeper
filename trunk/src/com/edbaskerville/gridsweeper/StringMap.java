package com.edbaskerville.gridsweeper;

import java.util.HashMap;
import java.util.Map;

public class StringMap extends HashMap<String, String>
{
	private static final long serialVersionUID = 1L;

	public StringMap()
	{
		super();
	}

	public StringMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public StringMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public StringMap(Map<? extends String, ? extends String> m)
	{
		super(m);
	}
	
}
