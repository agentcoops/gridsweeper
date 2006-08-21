package com.edbaskerville.gridsweeper;

public class Progress
{
	private double progress;
	private Status status;
	
	public Progress()
	{
		progress = 0.0;
		status = Status.RUNNING; 
	}

	public synchronized double getProgress()
	{
		return progress;
	}

	public synchronized void setProgress(double progress)
	{
		this.progress = progress;
	}

	public synchronized Status getStatus()
	{
		return status;
	}

	public synchronized void setStatus(Status status)
	{
		this.status = status;
	}
}