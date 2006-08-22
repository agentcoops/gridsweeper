package com.edbaskerville.gridsweeper;

public interface FileSystem
{
	public void connect(java.util.Properties properties, Progress progress) throws FileSystemException;
	public void disconnect(Progress progress) throws FileSystemException;
	
	public void uploadFile(java.io.InputStream source, String path, Progress progress) throws FileSystemException;
	public void downloadFile(java.io.OutputStream destination, String path, Progress progress) throws FileSystemException;
	
	public java.util.List<String> list(String path, Progress progress) throws FileSystemException;
}
