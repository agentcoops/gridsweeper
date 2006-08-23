package com.edbaskerville.gridsweeper;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.*;

public class FTPFileSystem implements FileSystem
{
	String hostname;
	int port;
	
	String username;
	String password;
	
	String directory;
	
	FTPClient ftpClient;
	
	public FTPFileSystem(Properties properties) throws FileSystemException
	{
		hostname = properties.getProperty("hostname");
		if(hostname == null) throw new FileSystemException("Cannot initialize FTP file system without hostname.");
		
		String portStr = properties.getProperty("port");
		if(portStr != null)
		{
			port = Integer.parseInt(properties.getProperty("port"));
		}
		else port = 0;
		
		username = properties.getProperty("username");
		password = properties.getProperty("password");
		
		directory = properties.getProperty("directory");
		
		ftpClient = new FTPClient();
	}
	
	public void connect(Progress progress) throws FileSystemException
	{
		progress.setProgress(0.0);
		
		// First establish a socket connection
		try
		{
			if(port == 0) ftpClient.connect(hostname);
			else ftpClient.connect(hostname, port);
		}
		catch(Exception e)
		{
			throw new FileSystemException("An exception occurred trying to connect to FTP server.", e);
		}
		
		progress.setProgress(0.5);
		
		// Then log in
		try
		{
			ftpClient.login(username, password);
		}
		catch(Exception e)
		{
			throw new FileSystemException("An exception occurred trying to login to FTP server.", e);
		}
		
		progress.setProgress(1.0);
	}

	public void disconnect(Progress progress) throws FileSystemException
	{
		progress.setProgress(0.0);
		
		try
		{
			ftpClient.logout();
		}
		catch (IOException e)
		{
			throw new FileSystemException("Could not logout", e);
		}
		
		progress.setProgress(0.5);
		
		try
		{
			ftpClient.disconnect();
		}
		catch (IOException e)
		{
			throw new FileSystemException("Could not disconnect", e);
		}
		
		progress.setProgress(1.0);
	}
	
	public void downloadFile(String remotePath, String localPath, Progress progress) throws FileSystemException
	{
		// Get file size
		
		// Get an input stream from FTP server
		
		// Get a file output stream
		
		// Read from input stream and write to output stream
		
		// Close both streams
		
		// Complete pending FTP command
	}

	public List<String> list(String path, Progress progress) throws FileSystemException
	{
		// Get an FTP list parse engine
		
		// Iterate through list while there are more to get,
		// updating progress accordingly
		
		// Return names
		
		return null;
	}
	
	public boolean isDirectory(String path)
	{
		// Get an FTPFile object for path
		
		// Return whether it's a directory or not
		
		return false;
	}
	
	public void uploadFile(String localPath, String remotePath, Progress progress) throws FileSystemException
	{
		// Get file size
		
		// Get a file input stream
		
		// Get an output stream
		
		// Read from file input stream and write to output stream
		
		// Close both files
		
		// Complete pending FTP command
	}

}
