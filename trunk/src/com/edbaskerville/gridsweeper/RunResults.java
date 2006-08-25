package com.edbaskerville.gridsweeper;

import java.io.*;

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
