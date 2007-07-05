package com.edbaskerville.gridsweeper.parameters;

import java.util.*;
import com.edbaskerville.gridsweeper.*;

/**
 * An abstract class representing parameter sweeps that combine multiple sub-sweeps.
 * @author Ed Baskerville
 *
 */
public abstract class CombinationSweep implements Sweep, List<Sweep>
{
	/**
	 * A list containing sub-sweeps to be combined.
	 */
	protected List<Sweep> children;
	
	/**
	 * Constructor to initialize {@code children}, for use by subclasses.
	 * @param children
	 */
	public CombinationSweep(List<Sweep> children)
	{
		this.children = children;
	}
	
	/**
	 * Default constructor, initializes {@code children} with an empty list.
	 *
	 */
	public CombinationSweep()
	{
		this.children = new ArrayList<Sweep>();
	}
	
	public abstract List<ParameterMap> generateMaps(Random rng, int numRuns) throws SweepLengthException, DuplicateParameterException;
	
	/**
	 * Generates the standard order in which parameters should be presented to the user
	 * in sweep enumerations, for example to name output directories. The order is
	 * given by the order of the {@code children} field, with sub-sweeps of class
	 * {@code CombinationSweep} given recursively, and with instances of class
	 * {@code SingleValueSweep} omitted.
	 * The {@link com.edbaskerville.gridsweeper.Experiment#setParameterOrder} method can
	 * be used to provide a user override for the default order.
	 * @return A list of parameter names providing the human-readable order.
	 */
	public StringList getParameterOrder()
	{
		StringList parameterOrder = new StringList();
		
		for(Sweep sweep : children)
		{
			if(sweep instanceof SingleValueSweep)
			{
				// Don't include it
			}
			else if(sweep instanceof SingleSweep)
			{
				parameterOrder.add(((SingleSweep)sweep).getName());
			}
			else
			{
				assert(sweep instanceof CombinationSweep);
				parameterOrder.addAll(((CombinationSweep)sweep).getParameterOrder());
			}
		}
		
		return parameterOrder;
	}
	
	public void add(int index, Sweep element)
	{
		children.add(index, element);
	}

	public boolean add(Sweep o)
	{
		return children.add(o);
	}

	public boolean addAll(Collection<? extends Sweep> c)
	{
		return children.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Sweep> c)
	{
		return children.addAll(index, c);
	}

	public void clear()
	{
		children.clear();
	}

	public boolean contains(Object o)
	{
		return children.contains(o);
	}

	public boolean containsAll(Collection<?> c)
	{
		return children.containsAll(c);
	}

	public Sweep get(int index)
	{
		return children.get(index);
	}

	public int indexOf(Object o)
	{
		return children.indexOf(o);
	}

	public boolean isEmpty()
	{
		return children.isEmpty();
	}

	public Iterator<Sweep> iterator()
	{
		return children.iterator();
	}

	public int lastIndexOf(Object o)
	{
		return children.lastIndexOf(o);
	}

	public ListIterator<Sweep> listIterator()
	{
		return children.listIterator();
	}

	public ListIterator<Sweep> listIterator(int index)
	{
		return children.listIterator(index);
	}

	public Sweep remove(int index)
	{
		return children.remove(index);
	}

	public boolean remove(Object o)
	{
		return children.remove(o);
	}

	public boolean removeAll(Collection<?> c)
	{
		return children.removeAll(c);
	}

	public boolean retainAll(Collection<?> c)
	{
		return children.retainAll(c);
	}

	public Sweep set(int index, Sweep element)
	{
		return children.set(index, element);
	}

	public int size()
	{
		return children.size();
	}

	public List<Sweep> subList(int fromIndex, int toIndex)
	{
		return children.subList(fromIndex, toIndex);
	}

	public Object[] toArray()
	{
		return children.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return children.toArray(a);
	}

}
