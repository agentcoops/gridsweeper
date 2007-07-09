package edu.umich.lsa.cscs.gridsweeper;

public class GridSweeperException extends Exception
{
	private static final long serialVersionUID = 1L;

	public GridSweeperException()
	{
	}

	public GridSweeperException(String message)
	{
		super(message);
	}

	public GridSweeperException(Throwable cause)
	{
		super(cause);
	}

	public GridSweeperException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
