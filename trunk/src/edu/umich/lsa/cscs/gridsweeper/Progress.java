/*
	Progress.java
	
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
 * A simple class to represent the progress of a process, e.g., job submission.
 * @author Ed Baskerville
 *
 */
public class Progress
{
	private double progress;
	private String message;
	
	public Progress()
	{
		progress = 0.0;
		message = "";
	}

	public synchronized double getProgress()
	{
		return progress;
	}

	public synchronized void setProgress(double progress)
	{
		this.progress = progress;
	}

	public synchronized String getMessage()
	{
		return message;
	}

	public synchronized void setMessage(String message)
	{
		this.message = message;
	}
}