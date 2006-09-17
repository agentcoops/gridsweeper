package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

public class GridSweeperTest
{
	@Test
	public void sample() throws Exception
	{
		String[] args =
			{
				"-a",
				"com.edbaskerville.gridsweeper.DroneAdapter",
				"-e",
				getPath("sample")
			};
		
		GridSweeper.main(args);
	}
	
	private String getPath(String testName)
	{
		return StringUtils.unescape(getClass().getResource("GridSweeperTest_" + testName + ".gsweep").getPath());
	}
}
