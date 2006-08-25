package com.edbaskerville.gridsweeper;

public class FileTransferException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FileTransferException()
	{
	}

	public FileTransferException(String message)
	{
		super(message);
	}

	public FileTransferException(Throwable cause)
	{
		super(cause);
	}

	public FileTransferException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
