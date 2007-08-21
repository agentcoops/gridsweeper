/*
	Constants.java
	
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

/**
 * Constants used by multiple GridSweeper classes.
 * @author Ed Baskerville
 *
 */
final class Constants
{
	/**
	 * A variable that can be used in file paths to represent the current
	 * run number. When transferring output files to the server, 
	 * this variable will be replaced by the run number of the current run,
	 * so data can be retrieved for all runs. If the placeholder is missing
	 * from an output file path, it will be appended as an extension
	 * instead.
	 */
	public static final String RunNumberPlaceholder = "$gs_rn_ph$";
}
