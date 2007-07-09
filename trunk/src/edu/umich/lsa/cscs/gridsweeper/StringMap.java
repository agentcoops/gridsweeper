package edu.umich.lsa.cscs.gridsweeper;

import java.util.HashMap;
import java.util.Map;

/**
 * A trivial subclass of {@code java.util.HashMap} supporting only
 * string keys and string values.
 * @author Ed Baskerville
 *
 */
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
