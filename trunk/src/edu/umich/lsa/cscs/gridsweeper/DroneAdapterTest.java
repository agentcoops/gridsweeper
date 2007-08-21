/*
	DroneAdapterTest.java
	
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

import org.junit.*;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

class DroneAdapterTest
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
		RunResults results = adapter.run(parameters, 5, 10, 100);
		
		byte[] stdoutData = results.getStdoutData();
		String stdoutString = new String(stdoutData, "UTF-8");
		
		assertEquals("-N5 -S100 -Dbeta=0.1", stdoutString);
	}
}
