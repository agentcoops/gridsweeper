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
