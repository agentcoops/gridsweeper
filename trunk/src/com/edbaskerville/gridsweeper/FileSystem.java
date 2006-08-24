package com.edbaskerville.gridsweeper;

public interface FileSystem
{
	public void connect() throws FileSystemException;
	public void connect(Progress progress) throws FileSystemException;
	
	public void disconnect() throws FileSystemException;
	public void disconnect(Progress progress) throws FileSystemException;
	
	public void uploadFile(String localPath, String remotePath) throws FileSystemException;
	public void uploadFile(String localPath, String remotePath, Progress progress) throws FileSystemException;
	
	public void downloadFile(String remotePath, String localPath) throws FileSystemException;
	public void downloadFile(String remotePath, String localPath, Progress progress) throws FileSystemException;
	
	public void deleteFile(String path) throws FileSystemException;
	public void deleteFile(String path, Progress progress) throws FileSystemException;
	
	public void makeDirectory(String path) throws FileSystemException;
	public void makeDirectory(String path, Progress progress) throws FileSystemException;
	
	public void removeDirectory(String path) throws FileSystemException;
	public void removeDirectory(String path, Progress progress) throws FileSystemException;
	
	//public java.util.List<String> list(String path) throws FileSystemException;
	//public java.util.List<String> list(String path, Progress progress) throws FileSystemException;
	
	//public boolean isDirectory(String path) throws FileSystemException;
	//public boolean isDirectory(String path, Progress progress) throws FileSystemException;
}
