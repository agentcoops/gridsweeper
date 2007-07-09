package edu.umich.lsa.cscs.gridsweeper.parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * A subclass of {@code HashMap<String, Object>} created to avoid typing {@code HashMap<String, Object>}.
 * @author Ed Baskerville
 *
 */
public class ParameterMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;

	public ParameterMap()
	{
		super();
	}

	public ParameterMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public ParameterMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public ParameterMap(Map<? extends String, ? extends Object> m)
	{
		super(m);
	}
	
	public ParameterMap(String name, Object value)
	{
		super(1, 1);
		put(name, value);
	}
}
