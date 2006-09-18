package com.edbaskerville.gridsweeper;

import java.io.*;
import java.util.*;
import org.apache.commons.net.ftp.*;

public class FTPFileTransferSystem implements FileTransferSystem
{
	String hostname;
	int port;
	
	String username;
	String password;
	
	String directory;
	
	FTPClient ftpClient;
	
	public FTPFileTransferSystem(Properties properties) throws FileTransferException
	{
		hostname = properties.getProperty("Hostname");
		if(hostname == null) throw new FileTransferException("Cannot initialize FTP file system without hostname.");
		
		String portStr = properties.getProperty("Port");
		if(portStr != null)
		{
			port = Integer.parseInt(properties.getProperty("Port"));
		}
		else port = 0;
		
		username = properties.getProperty("Username");
		password = properties.getProperty("Password");
		
		directory = properties.getProperty("Directory");
		
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
			localStream = new FileOutputStream(localPath);
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

	public String[] list(String path) throws FileTransferException
	{
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
		File localFile = new File(localPath);
		
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
		FTPFile file;
		try
		{
			file = getFTPFile(path);
		}
		catch (Exception e)
		{
			throw new FileTransferException("Got exception getting FTPFIle object.");
		}
		return file != null;
	}
	
	public void makeDirectory(String path) throws FileTransferException
	{
		List<String> components = StringUtils.pathComponents(path);
		
		try
		{
			String pathSoFar = "";
			for(String component : components)
			{
				pathSoFar = StringUtils.appendPathComponent(pathSoFar, component);
				if(fileExists(path))
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
		try
		{
			String[] listing = list(path);
			
			if(listing.length != 0)
			{
				for(String component : listing)
				{
					String subpath = StringUtils.appendPathComponent(path, component);
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
	
	protected FTPFile getFTPFile(String path) throws Exception
	{
		String filename = StringUtils.lastPathComponent(path);
		String dirPath = StringUtils.deleteLastPathComponent(path);
		
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
