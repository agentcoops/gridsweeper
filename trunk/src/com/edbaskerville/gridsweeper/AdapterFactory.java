package com.edbaskerville.gridsweeper;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class AdapterFactory
{
	public static Adapter createAdapter(String adapterClassName, Properties properties, byte[] stdinData)
	{
		try
		{
			return createAdapter(Class.forName(adapterClassName), properties, stdinData);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static Adapter createAdapter(Class adapterClass, Properties properties, byte[] stdinData)
	{
		try
		{
			Class[] parameterTypes = new Class[] { Properties.class, byte[].class };
			Constructor adapterConstructor = adapterClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { properties, stdinData };
			
			return (Adapter)adapterConstructor.newInstance(initargs);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
