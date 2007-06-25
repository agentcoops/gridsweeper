package com.edbaskerville.gridsweeper;

import java.lang.reflect.Constructor;
import java.util.Properties;

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
	 * Gets a file transfer system from a {@link Preferences} object. The {@code Preferences}
	 * object contains the class of the file transfer system as well as any
	 * system-specific preferences, specified using Java package reverse-DNS naming (e.g.,
	 * edu.umich.lsa.cscs.gridsweeper.FTPFileTransferSystem.Username).
	 * @param preferences The preferences object specifying the file transfer system
	 * and its settings.
	 * @return A file transfer system.
	 * @throws FileTransferException If the object could not be created.
	 */
	public FileTransferSystem getFileTransferSystem(Preferences preferences) throws FileTransferException
	{
		String className = preferences.getProperty("FileTransferSystemClass");
		
		// TODO: Generally fix nomenclature confusion between properties,
		// preferences, and settings. Pick one, or two if there's a real distinction. 
		Properties ftsProperties = preferences.getPropertiesForClass(className);
		
		try
		{
			Class ftsClass = Class.forName(preferences.getProperty("FileTransferSystemClass"));
			Class[] parameterTypes = new Class[] { Properties.class };
			Constructor constructor = ftsClass.getConstructor(parameterTypes);
			Object[] initargs = new Object[] { ftsProperties };
			FileTransferSystem fts = (FileTransferSystem)constructor.newInstance(initargs);
			
			return fts;
		}
		catch(Exception e)
		{
			throw new FileTransferException("Received an exception getting file system", e);
		}
	}
}
