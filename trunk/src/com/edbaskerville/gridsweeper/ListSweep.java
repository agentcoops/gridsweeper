package com.edbaskerville.gridsweeper;

import java.util.*;

public class ListSweep<T> extends SingleSweep implements List<T>
{
	private List<T> values;
	
	public ListSweep(String name, List<T> values)
	{
		super(name);
		this.values = values;
	}
	
	public ListSweep(String name)
	{
		super(name);
		this.values = new ArrayList<T>();
	}
	
	@Override
	public List<ParameterMap> generateMaps()
	{
		List<ParameterMap> maps = new ArrayList<ParameterMap>();
		for(T value : values)
		{
			maps.add(new ParameterMap(name, value));
		}
		
		return maps;
	}

	public boolean add(T o)
	{
		return values.add(o);
	}

	public void add(int index, T element)
	{
		values.add(index, element);
	}

	public boolean addAll(Collection<? extends T> c)
	{
		return values.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c)
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

	public T get(int index)
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

	public Iterator<T> iterator()
	{
		return values.iterator();
	}

	public int lastIndexOf(Object o)
	{
		return values.lastIndexOf(o);
	}

	public ListIterator<T> listIterator()
	{
		return values.listIterator();
	}

	public ListIterator<T> listIterator(int index)
	{
		return values.listIterator(index);
	}

	public boolean remove(Object o)
	{
		return values.remove(o);
	}

	public T remove(int index)
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

	public T set(int index, T element)
	{
		return values.set(index, element);
	}

	public int size()
	{
		return values.size();
	}

	public List<T> subList(int fromIndex, int toIndex)
	{
		return values.subList(fromIndex, toIndex);
	}

	public Object[] toArray()
	{
		return values.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a)
	{
		return values.toArray(a);
	}
}
