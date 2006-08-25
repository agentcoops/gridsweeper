package com.edbaskerville.gridsweeper;

public interface FileTransferSystem
{
	public void connect() throws FileTransferException;
	public void connect(Progress progress) throws FileTransferException;
	
	public void disconnect() throws FileTransferException;
	public void disconnect(Progress progress) throws FileTransferException;
	
	public void uploadFile(String localPath, String remotePath) throws FileTransferException;
	public void uploadFile(String localPath, String remotePath, Progress progress) throws FileTransferException;
	
	public void downloadFile(String remotePath, String localPath) throws FileTransferException;
	public void downloadFile(String remotePath, String localPath, Progress progress) throws FileTransferException;
	
	public void deleteFile(String path) throws FileTransferException;
	public void deleteFile(String path, Progress progress) throws FileTransferException;
	
	public void makeDirectory(String path) throws FileTransferException;
	public void makeDirectory(String path, Progress progress) throws FileTransferException;
	
	public void removeDirectory(String path) throws FileTransferException;
	public void removeDirectory(String path, Progress progress) throws FileTransferException;
	
	//public java.util.List<String> list(String path) throws FileSystemException;
	//public java.util.List<String> list(String path, Progress progress) throws FileSystemException;
	
	//public boolean isDirectory(String path) throws FileSystemException;
	//public boolean isDirectory(String path, Progress progress) throws FileSystemException;
}
