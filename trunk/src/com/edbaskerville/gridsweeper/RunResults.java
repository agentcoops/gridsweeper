package com.edbaskerville.gridsweeper;

public class RunResults
{
	private int status;
	private String message;
	private byte[] stdoutData;
	private byte[] stderrData;
	
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
}
