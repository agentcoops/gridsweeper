package edu.umich.lsa.cscs.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;
import static edu.umich.lsa.cscs.gridsweeper.StringUtils.*;

public class StringUtilsTest
{
	@Before
	public void setUp()
	{
		StringUtils.setFileSeparator("/");
	}
	
	@Test
	public void lastWithRoot()
	{
		String rootPath = "/";
		assertEquals(rootPath, lastPathComponent(rootPath));
	}
	
	@Test
	public void lastWithRootParent()
	{
		String path = "/file";
		assertEquals("file", lastPathComponent(path));
	}
	
	@Test
	public void lastWithTrailingSlash()
	{
		String path = "dir/subdir/";
		assertEquals("subdir", lastPathComponent(path));
	}
	
	@Test
	public void lastWithoutTrailingSlash()
	{
		String path = "dir/file";
		assertEquals("file", lastPathComponent(path));
	}
	
	@Test
	public void lastWithMultipleLevels()
	{
		String path = "dir/sub1/sub2/sub3/file";
		assertEquals("file", lastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithRoot()
	{
		String rootPath = "/";
		assertEquals(rootPath, deleteLastPathComponent(rootPath));
	}
	
	@Test
	public void deleteLastWithRootParent()
	{
		String path = "/file";
		assertEquals("/", deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithTrailingSlash()
	{
		String path = "dir/subdir/";
		assertEquals("dir", deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithoutTrailingSlash()
	{
		String path = "dir/file";
		assertEquals("dir", deleteLastPathComponent(path));
	}
	
	@Test
	public void deleteLastWithMultipleLevels()
	{
		String path = "dir/sub1/sub2/sub3/file";
		assertEquals("dir/sub1/sub2/sub3", deleteLastPathComponent(path));
	}
	
	@Test
	public void expandTilde()
	{
		String path = "~/some/sub/path";
		String homeDir = System.getProperty("user.home");
		assertEquals(homeDir + "/some/sub/path", expandTildeInPath(path));
	}
	
	@Test
	public void appendSlashesNone()
	{
		String path = "/some/path";
		String subpath = "component";
		
		assertEquals("/some/path/component", appendPathComponent(path, subpath));
	}
	
	@Test
	public void appendSlashesT()
	{
		String path = "/some/path/";
		String subpath = "component";
		
		assertEquals("/some/path/component", appendPathComponent(path, subpath));
	}
	
	@Test
	public void appendSlashesS()
	{
		String path = "/some/path";
		String subpath = "/component";
		
		assertEquals("/some/path/component", appendPathComponent(path, subpath));
	}
	
	@Test
	public void addSlashesTS()
	{
		String path = "/some/path/";
		String subpath = "/component";
		
		assertEquals("/some/path/component", appendPathComponent(path, subpath));
	}
	
	@Test
	public void componentsWithoutRootNoTS()
	{
		String path = "some/path";
		StringList components = pathComponents(path);
		assertEquals(components.size(), 2);
		assertEquals("some", components.get(0));
		assertEquals("path", components.get(1));
	}
	
	@Test
	public void componentsWithoutRootTS()
	{
		String path = "some/path/";
		StringList components = pathComponents(path);
		assertEquals(components.size(), 2);
		assertEquals("some", components.get(0));
		assertEquals("path/", components.get(1));	
	}
	
	@Test
	public void componentsWithRootNoTS()
	{
		String path = "/some/path";
		StringList components = pathComponents(path);
		assertEquals(components.size(), 3);
		assertEquals("/", components.get(0));
		assertEquals("some", components.get(1));
		assertEquals("path", components.get(2));
	}
	
	@Test
	public void componentsWithRootTS()
	{
		String path = "/some/path/";
		StringList components = pathComponents(path);
		assertEquals(components.size(), 3);
		assertEquals("/", components.get(0));
		assertEquals("some", components.get(1));
		assertEquals("path/", components.get(2));
	}
}