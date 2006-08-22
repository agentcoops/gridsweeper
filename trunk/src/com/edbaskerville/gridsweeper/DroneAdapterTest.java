package com.edbaskerville.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

import com.edbaskerville.gridsweeper.parameters.*;
import java.util.*;

public class DroneAdapterTest
{
	@Test
	public void testDryRunWithDefaults() throws AdapterException
	{
		Properties properties = new Properties();
		properties.setProperty("command", "mymodel");
		
		DroneAdapter adapter = new DroneAdapter(properties, null);
		
		ParameterMap parameters = new ParameterMap();
		parameters.put("beta", "0.1");
		
		RunResults results = adapter.run(parameters, 5, 100, true);
		
		String message = results.getMessage();
		assertEquals("mymodel -N5 -S100 -Dbeta=0.1", message);
	}
}
