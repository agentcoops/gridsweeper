package com.edbaskerville.gridsweeper;

public class FileSystemException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FileSystemException()
	{
	}

	public FileSystemException(String message)
	{
		super(message);
	}

	public FileSystemException(Throwable cause)
	{
		super(cause);
	}

	public FileSystemException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
