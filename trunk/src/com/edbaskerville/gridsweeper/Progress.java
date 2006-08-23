package com.edbaskerville.gridsweeper;

public class Progress
{
	private double progress;
	
	public Progress()
	{
		progress = 0.0; 
	}

	public synchronized double getProgress()
	{
		return progress;
	}

	public synchronized void setProgress(double progress)
	{
		this.progress = progress;
	}
}