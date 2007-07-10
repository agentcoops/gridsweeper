package edu.umich.lsa.cscs.gridsweeper;

import java.io.*;
import org.apache.commons.net.ftp.*;

import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

/**
 * <p>An file transfer system for accessing FTP servers. This class is implemented
 * on top of <a target="_top"
 * href="http://jakarta.apache.org/commons/net/">Jakarta Commons Net</a>'s
 * FTP client. Supported properties
 * (all prefixed by umich.cscs.lsa.gridsweeper.FTPFileTransferSystem):</p>
 * 
 * <table>
 * 
 * <tr>
 * <td>Hostname</td>        <td>The DNS hostname or IP address of the server. Required.</td>
 * </tr>
 * 
 * <tr>
 * <td>Port</td>            <td>The server listening port. Optional.</td>
 * </tr>
 * 
 * <tr>
 * <td>Username</td>        <td>The FTP account username. Optional.</td>
 * </tr>
 * 
 * <tr>
 * <td>Password</td>        <td>The FTP account password. Optional.</td>
 * </tr>
 * 
 * <tr>
 * <td>Directory</td>       <td>The directory that will act as the root of
 *                              the file transfer system. Optional.</td>
 * </tr>
 * 
 * </table>
 * @author Ed Baskerville
 *
 */
public class FTPFileTransferSystem implements FileTransferSystem
{
	String hostname;
	int port;
	
	String username;
	String password;
	
	String directory;
	
	FTPClient ftpClient;
	
	/**
	 * Initializes an FTP file transfer system.
	 * @param settings The settings for the file transfer system.
	 * @throws FileTransferException If the hostname is not specified.
	 */
	public FTPFileTransferSystem(Settings settings) throws FileTransferException
	{
		Logger.finer("settings: " + settings);
		
		hostname = settings.getProperty("Hostname");
		if(hostname == null) throw new FileTransferException("Cannot initialize FTP file system without hostname.");
		
		String portStr = settings.getProperty("Port");
		if(portStr != null)
		{
			port = Integer.parseInt(settings.getProperty("Port"));
		}
		else port = 0;
		
		username = settings.getProperty("Username");
		password = settings.getProperty("Password");
		
		directory = settings.getProperty("Directory");
		
		ftpClient = new FTPClient();
	}
	
	public void connect() throws FileTransferException
	{
		// First establish a socket connection
		try
		{
			if(port == 0) ftpClient.connect(hostname);
			else ftpClient.connect(hostname, port);
		}
		catch(Exception e)
		{
			throw new FileTransferException("An exception occurred trying to connect to FTP server.", e);
		}
		
		int replyCode = ftpClient.getReplyCode();
		
		if(!FTPReply.isPositiveCompletion(replyCode))
		{
			try { ftpClient.disconnect(); } catch (IOException e) { }
			throw new FileTransferException("FTP server refused connection.");
		}
		
		// Then log in
		try
		{
			if(!ftpClient.login(username, password))
			{
				throw new FileTransferException("FTP login failed.");
			}
		}
		catch(Exception e)
		{
			throw new FileTransferException("An exception occurred trying to login to FTP server.", e);
		}
		
		// Set passive mode
		try
		{
			ftpClient.enterLocalPassiveMode();
		}
		catch(Exception e)
		{
			throw new FileTransferException("An exception occurred trying to set passive mode.", e);
		}
		
		// Set binary mode
		try
		{
			if(!ftpClient.setFileType(FTP.BINARY_FILE_TYPE))
			{
				throw new FileTransferException("Setting binary mode failed.");
			}
		}
		catch(Exception e)
		{
			throw new FileTransferException("An exception occurred trying to set binary mode.", e);
		}
		
		// Set working directory
		try
		{
			if(!ftpClient.changeWorkingDirectory(directory))
			{
				throw new FileTransferException("Could not change working directory.");
			}
		}
		catch (IOException e)
		{
			throw new FileTransferException("Got exception trying to cd", e);
		}
	}

	public void disconnect() throws FileTransferException
	{
		try
		{
			if(!ftpClient.logout())
			{
				throw new FileTransferException("FTP logout failed.");
			}
		}
		catch (IOException e)
		{
			throw new FileTransferException("Caught exception trying to logout.", e);
		}
		
		try
		{
			ftpClient.disconnect();
		}
		catch (IOException e)
		{
			throw new FileTransferException("Got exception disconnecting from FTP server.", e);
		}
	}
	
	public void downloadFile(String remotePath, String localPath) throws FileTransferException
	{
		if(remotePath.length() != 0 && remotePath.charAt(0) == '/')
			remotePath = remotePath.substring(1);
		
		// Ensure the local directory exists
		String containingDir = deleteLastPathComponent(localPath);
		makeLocalDirectory(containingDir);
		
		// Get an input stream from FTP server
		InputStream remoteStream;
		try
		{
			remoteStream = ftpClient.retrieveFileStream(remotePath);
		}
		catch (IOException e)
		{
			throw new FileTransferException("Received exception getting file stream.");
		}
		
		// Get a file output stream
		FileOutputStream localStream;
		try
		{
			localStream = new FileOutputStream(expandTildeInPath(localPath));
		}
		catch (FileNotFoundException e)
		{
			throw new FileTransferException("Could not open local file", e);
		}
		
		/*
		// Get file size
		long size;
		try
		{
			FTPFile remoteFile = ftpClient.listFiles(remotePath)[0];
			size = remoteFile.getSize();
		}
		catch (Exception e)
		{
			System.err.println("Could not get file size; will not update progress");
			size = -1;
		}
		long bytesTransferred = 0;
		*/
		
		// Read from input stream and write to output stream
		try
		{
			int b;
			while((b = remoteStream.read()) != -1)
			{
				localStream.write(b);
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Received exception copying file", e);
		}
		
		// Close both streams
		try
		{
			remoteStream.close();
			localStream.close();
		}
		catch(IOException e)
		{
			throw new FileTransferException("Got exception closing streams", e);
		}
		
		// Complete pending FTP command
		try
		{
			if(!ftpClient.completePendingCommand())
			{
				throw new FileTransferException("Could not complete file transfer");
			}
		}
		catch (IOException e)
		{
			throw new FileTransferException("Received exception completing file transfer", e);
		}
	}

	protected void makeLocalDirectory(String localDir) throws FileTransferException
	{
		StringList components = pathComponents(localDir);
		
		String pathSoFar = "";
		for(String component : components)
		{
			pathSoFar = appendPathComponent(pathSoFar, component);
			File file = new File(pathSoFar);
			
			if(file.exists())
			{
				if(!file.isDirectory())
				{
					throw new FileTransferException("Non-directory file already exists in desired directory path.");
				}
			}
			else
			{
				if(!file.mkdir())
				{
					throw new FileTransferException("Could not create directory: " + pathSoFar);
				}
			}
		}
	}

	public String[] list(String path) throws FileTransferException
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		try
		{
			FTPFile[] files;
			if(path == null || path.equals("")) files = ftpClient.listFiles();
			else files = ftpClient.listFiles(path);
			
			String[] names = new String[files.length];
			for(int i = 0; i < files.length; i++)
			{
				names[i] = files[i].getName();
			}
			return names;
		}
		catch(Exception e)
		{
			if(e instanceof FileTransferException) throw (FileTransferException)e;
			throw new FileTransferException("An exception occurred listing files", e);
		}
	}
	
	public boolean isDirectory(String path) throws FileTransferException
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		try
		{
			FTPFile file = getFTPFile(path);
			boolean isDir = file.isDirectory();
			return isDir;
		}
		catch(Exception e)
		{
			throw new FileTransferException("An exception occurred getting FTPFile object", e);
		}
	}
	
	public void deleteFile(String path) throws FileTransferException
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		try
		{
			if(!ftpClient.deleteFile(path))
			{
				throw new FileTransferException("Could not delete file.");
			}
		} catch (IOException e)
		{
			throw new FileTransferException("Received IOException deleting file", e);
		}
	}
	
	public void uploadFile(String localPath, String remotePath) throws FileTransferException
	{
		if(remotePath.length() != 0 && remotePath.charAt(0) == '/')
			remotePath = remotePath.substring(1);
		
		// Ensure containing directory exists
		String containingDirectory = deleteLastPathComponent(remotePath);
		makeDirectory(containingDirectory);
		
		
		File localFile = new File(expandTildeInPath(localPath));
		
		// Get a file input stream
		FileInputStream localStream;
		try
		{
			localStream = new FileInputStream(localFile);
		}
		catch (FileNotFoundException e)
		{
			throw new FileTransferException("Could not find specified file", e);
		}
		
		// Get the output stream
		OutputStream remoteStream;
		try
		{
			remoteStream = ftpClient.storeFileStream(remotePath);
		}
		catch(IOException e)
		{
			throw new FileTransferException("Received exception retrieving remote stream");
		}
		
		/*// Get size for progress calculations
		long size = localFile.length();
		long bytesTransferred = 0;*/
		
		// Read from file input stream and write to output stream
		try
		{
			int b;
			while((b = localStream.read()) != -1)
			{
				remoteStream.write(b);
				//if(progress != null) progress.setProgress(++bytesTransferred / (double)size);
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Received exception copying file", e);
		}
		
		// Close both files
		try
		{
			localStream.close();
			remoteStream.close();
		}
		catch(IOException e)
		{
			throw new FileTransferException("Received exception closing streams", e);
		}
		
		// Complete pending FTP command
		try
		{
			if(!ftpClient.completePendingCommand())
			{
				throw new FileTransferException("Could not complete file transfer");
			}
		}
		catch (IOException e)
		{
			throw new FileTransferException("Received exception completing file transfer", e);
		}
	}
	
	public boolean fileExists(String path) throws FileTransferException
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		FTPFile file;
		try
		{
			file = getFTPFile(path);
		}
		catch (Exception e)
		{
			throw new FileTransferException("Got exception getting FTPFile object.");
		}
		return file != null;
	}
	
	public void makeDirectory(String path) throws FileTransferException
	{
		if(path.length() == 0)
		{
			return;
		}
		else if(path.charAt(0) == '/')
			path = path.substring(1);
		
		StringList components = pathComponents(path);
		
		try
		{
			String pathSoFar = "";
			for(String component : components)
			{
				pathSoFar = appendPathComponent(pathSoFar, component);
				if(fileExists(pathSoFar))
				{
					if(!isDirectory(pathSoFar))
					{
						throw new FileTransferException("Non-directory file already exists in desired directory path.");
					}
				}
				else
				{
					if(!ftpClient.makeDirectory(pathSoFar))
					{
						throw new FileTransferException("Could not create directory: " + pathSoFar);
					}
				}
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Got exception creating directory");
		}
	}

	public void removeDirectory(String path) throws FileTransferException
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		try
		{
			String[] listing = list(path);
			
			if(listing.length != 0)
			{
				for(String component : listing)
				{
					String subpath = appendPathComponent(path, component);
					if(isDirectory(subpath)) removeDirectory(subpath);
					else deleteFile(subpath);
				}
			}
			
			if(!ftpClient.removeDirectory(path))
			{
				throw new FileTransferException("Could not remove directory " + path);					
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Got exception removing directory");
		}
	}
	
	/**
	 * Gets a Jakarta Commons {@code FTPFile} object for a provided path.
	 * Needed because the default behavior of file listing is a bit complex.
	 * @param path 
	 * @return The FTP file object.
	 * @throws Exception
	 */
	protected FTPFile getFTPFile(String path) throws Exception
	{
		if(path.length() != 0 && path.charAt(0) == '/')
			path = path.substring(1);
		
		String filename = lastPathComponent(path);
		String dirPath = deleteLastPathComponent(path);
		
		FTPFile[] files;
		if(dirPath.equals(""))
		{
			files = ftpClient.listFiles();
		}
		else
		{
			files = ftpClient.listFiles(dirPath);
		}
		for(FTPFile file : files)
		{
			if(file.getName().equals(filename))
			{
				return file;
			}
		}
		return null;
	}
}
