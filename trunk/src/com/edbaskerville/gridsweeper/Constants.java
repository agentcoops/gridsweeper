package com.edbaskerville.gridsweeper;

/**
 * Constants used by multiple GridSweeper classes.
 * @author Ed Baskerville
 *
 */
public final class Constants
{
	/**
	 * A variable that can be used in file paths to represent the current
	 * run number. When transferring output files to the server, 
	 * this variable will be replaced by the run number of the current run,
	 * so data can be retrieved for all runs. 
	 */
	public static final String RunNumberPlaceholder = "$gs_rn_ph$";
}
