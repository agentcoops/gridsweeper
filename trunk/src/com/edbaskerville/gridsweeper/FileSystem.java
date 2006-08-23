package com.edbaskerville.gridsweeper;

public interface FileSystem
{
	public void connect(Progress progress) throws FileSystemException;
	public void disconnect(Progress progress) throws FileSystemException;
	
	public void uploadFile(String localPath, String remotePath, Progress progress) throws FileSystemException;
	public void downloadFile(String remotePath, String localPath, Progress progress) throws FileSystemException;
	
	public java.util.List<String> list(String path, Progress progress) throws FileSystemException;
	public boolean isDirectory(String path);
}
