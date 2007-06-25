package com.edbaskerville.gridsweeper;

import java.lang.reflect.Constructor;
import java.util.Properties;

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
	 * Creates an adapter from the class name and settings.
	 * @param adapterClassName The name of the adapter class to use.
	 * @param settings Settings for the adapter, whose meaning is defined
	 * by the class implementation.
	 * @return An adapter with the specified class and properties, or
	 * {@code null} if the adapter could not be created.
	 */
	public static Adapter createAdapter(String adapterClassName, Properties settings)
	{
		// TODO: Consider throwing an exception describing what went wrong. 
		try
		{
			return createAdapter(Class.forName(adapterClassName), settings);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Creates an adapter instance from the class object and settings.
	 * @param adapterClass The adapter class to use.
	 * @param settings for the adapter, whose meaning is defined
	 * by the class implementation.
	 * @return An adapter with the specified class and properties, or
	 * {@code null} if the adapter could not be created.
	 */
	public static Adapter createAdapter(Class adapterClass, Properties settings)
	{
		// TODO: Consider throwing an exception describing what went wrong.
		try
		{
			Class[] parameterTypes = new Class[] { Properties.class };
			Constructor adapterConstructor = adapterClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { settings };
			
			return (Adapter)adapterConstructor.newInstance(initargs);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
