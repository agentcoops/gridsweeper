package com.edbaskerville.gridsweeper;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class FTPFileSystemTest
{
	FTPFileSystem ftpFS;
	
	@Before
	public void setUp() throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty("hostname", "localhost");
		properties.setProperty("username", "ftpuser");
		properties.setProperty("password", "ftp");
		properties.setProperty("directory", "tmp");
		
		ftpFS = new FTPFileSystem(properties);
	}
	
	@Test
	public void connectAndDisconnect()
	{
		Progress progress = new Progress();
		try
		{
			ftpFS.connect(progress);
			ftpFS.disconnect(progress);
		}
		catch(FileSystemException e)
		{
			fail();
		}
	}
	
	@Test
	public void upload() throws Exception
	{
		ftpFS.connect();
		
		// Create properties file in /tmp
		Properties tmpProps = new Properties();
		tmpProps.setProperty("hi there", "yo there");
		tmpProps.store(new FileOutputStream("/tmp/props"), "this is a file");
		
		// Upload file to server
		ftpFS.uploadFile("/tmp/props", "props");
		
		// Verify file exists
		File file = new File("/Users/ftpuser/tmp/props");
		assertTrue(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void download() throws Exception
	{
		ftpFS.connect();
		
		// Download file to /tmp
		ftpFS.downloadFile("download", "/tmp/download");
		File file = new File("/tmp/download");
		assertTrue(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void delete() throws Exception
	{
		upload(); // So we have a file to delete
		
		ftpFS.connect();
		
		ftpFS.deleteFile("props");
		
		// Verify file does not exist
		File file = new File("/Users/ftpuser/tmp/props");
		assertFalse(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void makeDirectory() throws Exception
	{
		ftpFS.connect();
		
		try { ftpFS.removeDirectory("dir"); } catch(Exception e) {}
		
		// Make directory
		ftpFS.makeDirectory("dir");
		
		// Verify directory exists
		File dir = new File("/Users/ftpuser/tmp/dir");
		assertTrue(dir.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void removeDirectory() throws Exception
	{
		makeDirectory();
		
		ftpFS.connect();
		
		// Remove directory
		ftpFS.removeDirectory("dir");
		
		// Verify directory doesn't exist
		File dir = new File("/Users/ftpuser/tmp/dir");
		assertFalse(dir.exists());
		
		ftpFS.disconnect();
	}
}
