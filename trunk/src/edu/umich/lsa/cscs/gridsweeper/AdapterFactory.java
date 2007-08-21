/*
	AdapterFactory.java
	
	Part of GridSweeper
	Copyright (c) 2006 - 2007 Ed Baskerville <software@edbaskerville.com>

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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
		Class theClass;
		try
		{
			theClass = Class.forName(adapterClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new GridSweeperException("Could not find adapter class "
					+ adapterClassName + ".", e);
		}
		return createAdapter(theClass, adapterSettings);
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
		Class[] parameterTypes = new Class[] { Settings.class };
		Constructor adapterConstructor;
		try
		{
			adapterConstructor = adapterClass.getConstructor(parameterTypes);
		}
		catch (SecurityException e)
		{
			throw new GridSweeperException("Received SecurityException trying to get adapter constructor.");
		}
		catch (NoSuchMethodException e)
		{
			throw new GridSweeperException("Adapter class does not implement required constructor.", e);
		}
		Object[] initargs = new Object[] { settings };
		
		try
		{
			return (Adapter)adapterConstructor.newInstance(initargs);
		}
		catch (Exception e)
		{
			throw new GridSweeperException("Received exception trying to create adapter.", e);
		}
	}
}
