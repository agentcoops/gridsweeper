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
		connect(null);
	}
	
	public void connect(Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
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
		
		if(progress != null) progress.setProgress(1.0 / 3);
		
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
		
		if(progress != null) progress.setProgress(2.0 / 3);
		
		// Finally, set working directory
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
		
		if(progress != null) progress.setProgress(1.0);
	}

	public void disconnect() throws FileTransferException
	{
		disconnect(null);
	}
	
	public void disconnect(Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
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
		
		if(progress != null) progress.setProgress(0.5);
		
		try
		{
			ftpClient.disconnect();
		}
		catch (IOException e)
		{
			throw new FileTransferException("Got exception disconnecting from FTP server.", e);
		}
		
		if(progress != null) progress.setProgress(1.0);
	}
	
	public void downloadFile(String remotePath, String localPath) throws FileTransferException
	{
		downloadFile(remotePath, localPath, null);
	}
	
	public void downloadFile(String remotePath, String localPath, Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
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
		
		// Read from input stream and write to output stream
		try
		{
			int b;
			while((b = remoteStream.read()) != -1)
			{
				localStream.write(b);
				if(progress != null && size != -1) progress.setProgress(++bytesTransferred / (double)size);
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

	public List<String> list(String path) throws FileTransferException
	{
		return list(path, null);
	}
	
	public List<String> list(String path, Progress progress) throws FileTransferException
	{
		// Get an FTP list parse engine
		
		// Iterate through list while there are more to get,
		// updating progress accordingly
		
		// Return names
		
		return null;
	}
	
	public boolean isDirectory(String path) throws FileTransferException
	{
		return isDirectory(path, null);
	}
	
	public boolean isDirectory(String path, Progress progress) throws FileTransferException
	{
		// Get an FTPFile object for path
		
		// Return whether it's a directory or not
		
		return false;
	}
	
	public void deleteFile(String path) throws FileTransferException
	{
		deleteFile(path, null);
	}
	
	public void deleteFile(String path, Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
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
		
		if(progress != null) progress.setProgress(1.0);
	}
	
	public void uploadFile(String localPath, String remotePath) throws FileTransferException
	{
		uploadFile(localPath, remotePath, null);
	}
	
	public void uploadFile(String localPath, String remotePath, Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
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
		
		// Get size for progress calculations
		long size = localFile.length();
		long bytesTransferred = 0;
		
		// Read from file input stream and write to output stream
		try
		{
			int b;
			while((b = localStream.read()) != -1)
			{
				remoteStream.write(b);
				if(progress != null) progress.setProgress(++bytesTransferred / (double)size);
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
		
		if(progress != null) progress.setProgress(1.0);
	}

	public void makeDirectory(String path, Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
		try
		{
			if(!ftpClient.makeDirectory(path))
			{
				
				throw new FileTransferException("Could not create directory.");
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Got exception creating directory");
		}
		
		if(progress != null) progress.setProgress(1.0);
	}

	public void makeDirectory(String path) throws FileTransferException
	{
		makeDirectory(path, null);
	}

	public void removeDirectory(String path, Progress progress) throws FileTransferException
	{
		if(progress != null) progress.setProgress(0.0);
		
		try
		{
			if(!ftpClient.removeDirectory(path))
			{
				throw new FileTransferException("Could not remove directory.");
			}
		}
		catch(IOException e)
		{
			throw new FileTransferException("Got exception removing directory");
		}
		
		if(progress != null) progress.setProgress(1.0);
		
	}

	public void removeDirectory(String path) throws FileTransferException
	{
		removeDirectory(path, null);
	}
}
