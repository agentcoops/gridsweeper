package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

public class StringUtilsTest
{
	@Test
	public void lastWithRoot()
	{
		String rootPath = "/";
		assertEquals(rootPath, StringUtils.lastPathComponent(rootPath));
	}
	
	@Test
	public void lastWithRootParent()
	{
		String path = "/file";
		assertEquals("file", StringUtils.lastPathComponent(path));
	}
	
	@Test
	public void lastWithTrailingSlash()
	{
		String path = "dir/subdir/";
		assertEquals("subdir", StringUtils.lastPathComponent(path));
	}
	
	@Test
	public void lastWithoutTrailingSlash()
	{
		String path = "dir/file";
		assertEquals("file", StringUtils.lastPathComponent(path));
	}
	
	@Test
	public void lastWithMultipleLevels()
	{
		String path = "dir/sub1/sub2/sub3/file";
		assertEquals("file", StringUtils.lastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithRoot()
	{
		String rootPath = "/";
		assertEquals(rootPath, StringUtils.deleteLastPathComponent(rootPath));
	}
	
	@Test
	public void deleteLastWithRootParent()
	{
		String path = "/file";
		assertEquals("/", StringUtils.deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithTrailingSlash()
	{
		String path = "dir/subdir/";
		assertEquals("dir", StringUtils.deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithoutTrailingSlash()
	{
		String path = "dir/file";
		assertEquals("dir", StringUtils.deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithMultipleLevels()
	{
		String path = "dir/sub1/sub2/sub3/file";
		assertEquals("dir/sub1/sub2/sub3", StringUtils.deleteLastPathComponent(path));
	}
}
