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