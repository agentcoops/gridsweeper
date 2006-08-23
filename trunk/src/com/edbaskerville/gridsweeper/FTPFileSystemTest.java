package com.edbaskerville.gridsweeper;

import java.io.ByteArrayInputStream;
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
	public void connectAndDisconnect() throws FileSystemException
	{
		Progress progress = new Progress();
		ftpFS.connect(progress);
		ftpFS.disconnect(progress);
	}
	
	@Test
	public void upload() throws FileSystemException
	{
		Progress progress = new Progress();
		
		ftpFS.connect(progress);
		
		/*ByteArrayInputStream byteStream = new ByteArrayInputStream(new byte[]{'a', 'b', 'c'});
		ftpFS.uploadFile(byteStream, "abc", progress);*/
		
		ftpFS.disconnect(progress);
	}
}
