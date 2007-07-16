package edu.umich.lsa.cscs.gridsweeper;

import org.junit.*;

public class GridSweeperToolTest
{
	@Test
	public void sample() throws Exception
	{
		String[] args =
			{
				"-a",
				"edu.umich.lsa.cscs.gridsweeper.DroneAdapter",
				"-e",
				getPath("sample")
			};
		
		GridSweeperTool.main(args);
	}
	
	private String getPath(String testName)
	{
		return StringUtils.unescape(getClass().getResource("GridSweeperTest_" + testName + ".gsweep").getPath());
	}
}
