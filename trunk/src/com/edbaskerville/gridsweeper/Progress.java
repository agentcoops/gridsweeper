package com.edbaskerville.gridsweeper;

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