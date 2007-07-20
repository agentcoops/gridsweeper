package edu.umich.lsa.cscs.gridsweeper.parameters;

import java.util.*;

import edu.umich.lsa.cscs.gridsweeper.*;

/**
 * An abstract class representing parameter sweeps that combine multiple sub-sweeps.
 * @author Ed Baskerville
 *
 */
public abstract class CombinationSweep implements Sweep
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
	
	public abstract List<ParameterMap> generateMaps() throws SweepLengthException, DuplicateParameterException;
	
	/**
	 * Generates the standard order in which parameters should be presented to the user
	 * in sweep enumerations, for example to name output directories. The order is
	 * given by the order of the {@code children} field, with sub-sweeps of class
	 * {@code CombinationSweep} given recursively, and with instances of class
	 * {@code SingleValueSweep} omitted.
	 * The {@link edu.umich.lsa.cscs.gridsweeper.Experiment#setParameterOrder} method can
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

	public boolean add(Sweep o)
	{
		return children.add(o);
	}

	public List<Sweep> getChildren()
	{
		return children;
	}

	public void setChildren(List<Sweep> children)
	{
		this.children = children;
	}

}
