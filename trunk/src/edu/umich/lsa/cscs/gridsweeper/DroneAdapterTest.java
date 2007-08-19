package edu.umich.lsa.cscs.gridsweeper;

import org.junit.*;
import static org.junit.Assert.*;

import edu.umich.lsa.cscs.gridsweeper.parameters.*;

import java.io.UnsupportedEncodingException;

public class DroneAdapterTest
{
	@Test
	public void testRealRunWithDefaults() throws AdapterException, UnsupportedEncodingException
	{
		Settings settings = new Settings();
		settings.setProperty("model", "/bin/echo");
		settings.setProperty("miscOptions", "-n");
		DroneAdapter adapter = new DroneAdapter(settings);
		
		ParameterMap parameters = new ParameterMap();
		parameters.put("beta", "0.1");
		RunResults results = adapter.run(parameters, 5, 100);
		
		byte[] stdoutData = results.getStdoutData();
		String stdoutString = new String(stdoutData, "UTF-8");
		
		assertEquals("-N5 -S100 -Dbeta=0.1", stdoutString);
	}
}
