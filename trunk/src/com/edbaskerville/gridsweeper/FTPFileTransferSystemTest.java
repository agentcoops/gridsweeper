package com.edbaskerville.gridsweeper;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class FTPFileTransferSystemTest
{
	FTPFileTransferSystem ftpFS;
	
	@Before
	public void setUp() throws Exception
	{
		Properties properties = new Properties();
		properties.setProperty("Hostname", "localhost");
		properties.setProperty("Username", "ftpuser");
		properties.setProperty("Password", "ftp");
		properties.setProperty("Directory", "tmp");
		
		ftpFS = new FTPFileTransferSystem(properties);
	}
	
	@Test
	public void connectAndDisconnect()
	{
		try
		{
			ftpFS.connect();
			ftpFS.disconnect();
		}
		catch(FileTransferException e)
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
	
	@Test
	public void isDirectoryFalse() throws Exception
	{
		ftpFS.connect();
		
		assertFalse(ftpFS.isDirectory("download"));
		
		ftpFS.disconnect();
	}
	
	@Test
	public void isDirectoryTrue() throws Exception
	{
		makeDirectory();
		
		ftpFS.connect();
		
		assertTrue(ftpFS.isDirectory("dir"));
		
		ftpFS.disconnect();
	}
	
	@Test
	public void list() throws Exception
	{
		ftpFS.connect();
		
		String[] names = ftpFS.list("subdir");
		assertEquals(3, names.length);
		if(names.length == 3)
		{
			assertEquals("1", names[0]);
			assertEquals("2", names[1]);
			assertEquals("3", names[2]);
		}
		
		ftpFS.disconnect();
	}
}
