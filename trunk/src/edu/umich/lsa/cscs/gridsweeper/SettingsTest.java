/*
	SettingsTest.java
	
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

public class SettingsTest
{
	Settings settings;
	
	@Before
	public void setUp()
	{
		settings = Settings.sharedSettings();
	}
	
	@Test
	public void setAndGet()
	{
		settings.setProperty("key", "value");
		assertEquals("value", settings.getProperty("key"));
	}
}
