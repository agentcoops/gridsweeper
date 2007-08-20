/*
	RunResults.java
	
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

import java.io.*;

/**
 * Represents the results from a model run. This class is just a wrapper around
 * exit status, a user-readable message, data from standard output and error,
 * and, if something went wrong in GridSweeperRunner itself, an exception. 
 * @author Ed Baskerville
 *
 */
public class RunResults implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int status;
	private String message;
	private byte[] stdoutData;
	private byte[] stderrData;
	private Exception exception;
	
	public RunResults(Exception exception)
	{
		this.exception = exception;
	}
	
	public RunResults(int status, String message, byte[] stdoutData, byte[] stderrData)
	{
		this.status = status;
		this.message = message;
		this.stdoutData = stdoutData;
		this.stderrData = stderrData;
	}

	public String getMessage()
	{
		return message;
	}

	public int getStatus()
	{
		return status;
	}

	public byte[] getStderrData()
	{
		return stderrData;
	}

	public byte[] getStdoutData()
	{
		return stdoutData;
	}
	
	public Exception getException()
	{
		return exception;
	}
}
