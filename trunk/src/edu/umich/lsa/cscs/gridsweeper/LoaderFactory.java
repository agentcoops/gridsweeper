package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;
import java.net.*;
import java.util.*;
import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

class LoaderFactory
{
	public static ClassLoader create(StringList dirs)
	{
		ClassLoader loader;
		
		List<URL> urls = new ArrayList<URL>();
		for(String dir : dirs)
		{
			File dirFile = new File(dir);
			if(dirFile.exists() && dirFile.isDirectory())
			{
				try
				{
					urls.add(new URL("file", "", dir));
				}
				catch (MalformedURLException e)
				{
					continue;
				}
				
				String[] fileList = dirFile.list();
				for(String subpath : fileList)
				{
					if(subpath.endsWith(".jar"))
					{
						String jarPath = appendPathComponent(dir, subpath);
						try { urls.add(new URL("file", "", jarPath)); }
							catch(MalformedURLException e){}
					}
				}
			}
		}
		
		ClassLoader sysLoader = ClassLoader.getSystemClassLoader(); 
		if(urls.size() == 0)
		{
			loader = sysLoader;
		}
		else
		{
			int count = urls.size();
			URL[] urlArray = new URL[count];
			for(int i = 0; i < count; i++)
			{
				urlArray[i] = urls.get(i);
			}
			
			loader = new URLClassLoader(urlArray, sysLoader); 
		}
		
		return loader;
	}
}
