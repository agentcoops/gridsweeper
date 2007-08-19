package edu.umich.lsa.cscs.gridsweeper;

import java.lang.reflect.Constructor;

/**
 * A factory that creates {@link Adapter} instances. The {@link GridSweeper} object
 * provides the class name and properties to the {@link GridSweeperRunner} object
 * as part of job submission, and the {@code GridSweeperRunner} object instantiates
 * an adapter with {@link AdapterFactory}'s static methods.
 * @author Ed Baskerville
 *
 */
public class AdapterFactory
{
	/**
	 * Creates an adapter from the class name and properties.
	 * @param adapterClassName The name of the adapter class to use.
	 * @param adapterSettings Properties for the adapter, whose meaning is defined
	 * by the class implementation.
	 * @return An adapter with the specified class and properties, or
	 * {@code null} if the adapter could not be created.
	 */
	public static Adapter createAdapter(String adapterClassName, Settings adapterSettings) throws GridSweeperException
	{
		// TODO: Consider throwing an exception describing what went wrong. 
		try
		{
			return createAdapter(Class.forName(adapterClassName), adapterSettings);
		}
		catch(Exception e)
		{
			throw new GridSweeperException(e);
		}
	}
	
	/**
	 * Creates an adapter instance from the class object and properties.
	 * @param adapterClass The adapter class to use.
	 * @param properties for the adapter, whose meaning is defined
	 * by the class implementation.
	 * @return An adapter with the specified class and properties, or
	 * {@code null} if the adapter could not be created.
	 */
	public static Adapter createAdapter(Class adapterClass, Settings settings) throws GridSweeperException
	{
		// TODO: Consider throwing an exception describing what went wrong.
		try
		{
			Class[] parameterTypes = new Class[] { Settings.class };
			Constructor adapterConstructor = adapterClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { settings };
			
			return (Adapter)adapterConstructor.newInstance(initargs);
		}
		catch(Exception e)
		{
			throw new GridSweeperException(e);
		}
	}
}
