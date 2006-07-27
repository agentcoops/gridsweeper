package com.edbaskerville.gridsweeper.parameters;

import java.util.*;

public class ListSweep extends SingleSweep implements List<String>
{
	private List<String> values;
	
	public ListSweep(String name, List<String> values)
	{
		super(name);
		this.values = values;
	}
	
	public ListSweep(String name)
	{
		super(name);
		this.values = new ArrayList<String>();
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

	public void add(int index, String element)
	{
		values.add(index, element);
	}

	public boolean addAll(Collection<? extends String> c)
	{
		return values.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends String> c)
	{
		return values.addAll(index, c);
	}

	public void clear()
	{
		values.clear();
	}

	public boolean contains(Object o)
	{
		return values.contains(o);
	}

	public boolean containsAll(Collection<?> c)
	{
		return values.containsAll(c);
	}

	public String get(int index)
	{
		return values.get(index);
	}

	public int indexOf(Object o)
	{
		return values.indexOf(o);
	}

	public boolean isEmpty()
	{
		return values.isEmpty();
	}

	public Iterator<String> iterator()
	{
		return values.iterator();
	}

	public int lastIndexOf(Object o)
	{
		return values.lastIndexOf(o);
	}

	public ListIterator<String> listIterator()
	{
		return values.listIterator();
	}

	public ListIterator<String> listIterator(int index)
	{
		return values.listIterator(index);
	}

	public boolean remove(Object o)
	{
		return values.remove(o);
	}

	public String remove(int index)
	{
		return values.remove(index);
	}

	public boolean removeAll(Collection<?> c)
	{
		return values.removeAll(c);
	}

	public boolean retainAll(Collection<?> c)
	{
		return values.retainAll(c);
	}

	public String set(int index, String element)
	{
		return values.set(index, element);
	}

	public int size()
	{
		return values.size();
	}

	public List<String> subList(int fromIndex, int toIndex)
	{
		return values.subList(fromIndex, toIndex);
	}

	public Object[] toArray()
	{
		return values.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return null;
	}
}
