package com.edbaskerville.gridsweeper;

public interface FileTransferSystem
{
	public void connect() throws FileTransferException;
	
	public void disconnect() throws FileTransferException;
	
	public void uploadFile(String localPath, String remotePath) throws FileTransferException;
	
	public void downloadFile(String remotePath, String localPath) throws FileTransferException;
	
	public void deleteFile(String path) throws FileTransferException;
	
	public void makeDirectory(String path) throws FileTransferException;
	
	public void removeDirectory(String path) throws FileTransferException;
	
	public String[] list(String path) throws FileTransferException;
	
	public boolean isDirectory(String path) throws FileTransferException;
}
