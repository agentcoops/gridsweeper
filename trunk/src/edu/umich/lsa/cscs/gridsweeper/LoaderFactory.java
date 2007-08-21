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
		
		List<URL> URLs = new ArrayList<URL>();
		for(String dir : dirs)
		{
			File dirFile = new File(dir);
			if(dirFile.exists() && dirFile.isDirectory())
			{
				try
				{
					URLs.add(new URL("file", "", dir));
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
						try { URLs.add(new URL("file", "", jarPath)); }
							catch(MalformedURLException e){}
					}
				}
			}
		}
		
		ClassLoader sysLoader = ClassLoader.getSystemClassLoader(); 
		if(URLs.size() == 0)
		{
			loader = sysLoader;
		}
		else
		{
			URL[] urls = (URL[])(URLs.toArray());
			loader = new URLClassLoader(urls, sysLoader); 
		}
		
		return loader;
	}
}
