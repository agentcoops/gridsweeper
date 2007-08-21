/*
	GridSweeperApp.java
	
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

import java.util.*;


/**
 * The main controller class for the GridSweeper graphical user interface.
 * Manages experiment document windows, 
 * @author Ed Baskerville
 *
 */
public class GridSweeperApp
{
	private static GridSweeperApp sharedInstance;
	
	public static GridSweeperApp getSharedInstance()
	{
		if(sharedInstance == null)
		{
			sharedInstance = new GridSweeperApp();
		}
		return sharedInstance;
	}
	
	private int untitledCounter;
	List<ExperimentController> controllers;
	
	public GridSweeperApp()
	{
		untitledCounter = 0;
		controllers = new ArrayList<ExperimentController>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		GridSweeperApp gui = GridSweeperApp.getSharedInstance();
		gui.run(args);
	}
	
	public void run(String[] args)
	{
		controllers.add(new ExperimentController());
	}
	
	public int getUntitledCounter()
	{
		return ++untitledCounter;
	}
}
