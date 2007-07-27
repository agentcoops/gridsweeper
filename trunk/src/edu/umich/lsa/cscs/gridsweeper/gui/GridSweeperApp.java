package edu.umich.lsa.cscs.gridsweeper.gui;

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
