/*
	FTPFileTransferSystemTest.java
	
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

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * <p>This class runs a number of tests for FTP file transfer. The tests
 * are designed to assume that you have local access to the FTP server,
 * so it's best to set up an FTP server within your own home directory.
 * If it's not in your own home directory, make sure that your user
 * is part of the group, as the default permissions policy appears
 * to be 750 for new files, at least with Mac OS X Tiger's FTP server.</p>
 * 
 * <p> In addition to setting up an FTP server, you need to do the following
 * before running the tests:
 * </p>
 * 
 * <ul>
 * <li>Change hostname/username/password/root directory to match your setup
 * in the {@code setUp()} method</li>
 * <li>Create a file called {@code "download"} in the FTP directory.</li>
 * <li>Create a directory called {@code "subdir"} with three files, {@code "1"},
 * {@code "2"}, and {@code "3"}.   
 * </ul>
 * 
 * <p>Then the tests should work. If you get a missing class error,
 * make sure Jakarta Commons Net and ORO are installed and in the classpath.</p>
 * @author Ed Baskerville
 *
 */
public class FTPFileTransferSystemTest
{
	FTPFileTransferSystem ftpFS;
	
	@Before
	public void setUp() throws Exception
	{
		Settings settings = new Settings();
		settings.setProperty("ftp.Hostname", "localhost");
		settings.setProperty("ftp.Username", "gsweep");
		settings.setProperty("ftp.Password", "gridsweeper");
		settings.setProperty("ftp.Directory", "ftp");
		
		ftpFS = new FTPFileTransferSystem(settings);
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
		
		try
		{
			File file = new File("/tmp/props");
			file.delete();
		}
		catch(Exception e) {}
		
		// Create properties file in /tmp
		Properties tmpProps = new Properties();
		tmpProps.setProperty("hi there", "yo there");
		tmpProps.store(new FileOutputStream("/tmp/props"), "this is a file");
		
		// Upload file to server
		ftpFS.uploadFile("/tmp/props", "props");
		
		// Verify file exists
		File file = new File("/Users/gsweep/ftp/props");
		assertTrue(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void uploadRecursive() throws Exception
	{
		ftpFS.connect();
		
		try { ftpFS.removeDirectory("recursiveUploadDir"); } catch(Exception e) {}
		
		// Create properties file in /tmp
		Properties tmpProps = new Properties();
		tmpProps.setProperty("hi there", "yo there");
		tmpProps.store(new FileOutputStream("/tmp/props"), "this is a file");
		
		// Upload file to server
		ftpFS.uploadFile("/tmp/props", "recursiveUploadDir/subdir/props");
		
		// Verify file exists
		File file = new File("/Users/gsweep/ftp/recursiveUploadDir/subdir/props");
		assertTrue(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void download() throws Exception
	{
		ftpFS.connect();
		
		try
		{
			// Delete local file if it exists
			File file = new File("/tmp/download");
			file.delete();
		}
		catch(Exception e) {}
		
		// Download file to /tmp
		ftpFS.downloadFile("download", "/tmp/download");
		File file = new File("/tmp/download");
		assertTrue(file.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void downloadRecursive() throws Exception
	{
		ftpFS.connect();
		
		// Delete local directory if it exists
		try
		{
			File file = new File("/tmp/downloadRecursive/subdir/download");
			file.delete();
			file = new File("/tmp/downloadRecursive/subdir");
			file.delete();
			file = new File("/tmp/downloadRecursive");
			file.delete();
		}
		catch(Exception e) {}
		
		// Download file to /tmp/downloadRecursive/subdir
		ftpFS.downloadFile("download", "/tmp/downloadRecursive/subdir/download");
		File file = new File("/tmp/downloadRecursive/subdir/download");
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
		File dir = new File("/Users/gsweep/ftp/dir");
		assertTrue(dir.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void makeDirectories() throws Exception
	{
		ftpFS.connect();
		
		try { ftpFS.removeDirectory("dir2"); } catch(Exception e) {}
		
		// Make directory
		ftpFS.makeDirectory("dir2/with/subdir");
		
		// Verify directory exists
		File dir = new File("/Users/gsweep/ftp/dir2/with/subdir");
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
		File dir = new File("/Users/gsweep/ftp/dir");
		assertFalse(dir.exists());
		
		ftpFS.disconnect();
	}
	
	@Test
	public void removeDirectoryRecursive() throws Exception
	{
		makeDirectories();
		
		ftpFS.connect();
		
		// Remove directory
		ftpFS.removeDirectory("dir2");
		
		// Verify directory doesn't exist
		File dir = new File("/Users/gsweep/ftp/dir2");
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
		ftpFS.connect();
		
		assertTrue(ftpFS.isDirectory("subdir"));
		
		ftpFS.disconnect();
	}
	
	@Test
	public void list() throws Exception
	{
		ftpFS.connect();
		
		String[] names = ftpFS.list("subdir");
		for(String name : names) System.out.println(name);
		
		assertTrue(names.length >= 3);
		
		boolean found1 = false, found2 = false, found3 = false;
		
		for(String name : names)
		{
			if(name.equals("1")) found1 = true;
			else if(name.equals("2")) found2 = true;
			else if(name.equals("3")) found3 = true;
		}
		
		assertTrue(found1);
		assertTrue(found2);
		assertTrue(found3);
		
		ftpFS.disconnect();
	}
}
