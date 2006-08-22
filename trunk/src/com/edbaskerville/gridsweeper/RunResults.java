package com.edbaskerville.gridsweeper;

import java.util.*;

public class RunResults
{
	private int status;
	private String message;
	private byte[] stdoutData;
	private byte[] stderrData;
	private Properties outputFiles;
	
	public RunResults(int status, String message, byte[] stdoutData, byte[] stderrData, Properties outputFiles)
	{
		this.status = status;
		this.message = message;
		this.stdoutData = stdoutData;
		this.stderrData = stderrData;
		this.outputFiles = outputFiles;
	}

	public String getMessage()
	{
		return message;
	}

	public Properties getOutputFiles()
	{
		return outputFiles;
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
