/*
	FileTransferSystemFactory.java
	
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
 * A factory that creates {@link FileTransferSystem} instances.
 *
 * @author Ed Baskerville
 */
public class FileTransferSystemFactory
{
	private static FileTransferSystemFactory factory;
	
	/**
	 * Gets the shared factory instance, creating it if it does not yet exist.
	 * @return The shared factory instance.
	 */
	public static FileTransferSystemFactory getFactory()
	{
		if(factory == null)
		{
			factory = new FileTransferSystemFactory();
		}
		return factory;
	}
	
	/**
	 * Gets a file transfer system from a {@link Settings} object. The {@code Settings}
	 * object contains the class of the file transfer system as well as any
	 * system-specific settings, specified using Java package reverse-DNS naming (e.g.,
	 * edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username).
	 * @param className TODO
	 * @param settings The settings object specifying the file transfer system
	 * and its properties.
	 * @return A file transfer system.
	 * @throws FileTransferException If the object could not be created.
	 */
	public FileTransferSystem getFileTransferSystem(String className, Settings settings) throws FileTransferException
	{
		try
		{
			Class ftsClass = Class.forName(settings.getProperty("FileTransferSystemClass", "edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem"));
			Class[] parameterTypes = new Class[] { Settings.class };
			Constructor constructor = ftsClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { settings };
			FileTransferSystem fts = (FileTransferSystem)constructor.newInstance(initargs);
			
			return fts;
		}
		catch(Exception e)
		{
			throw new FileTransferException("Received an exception getting file system", e);
		}
	}
}
