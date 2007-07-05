package com.edbaskerville.gridsweeper;

/**
 * The interface defining all file transfer operations required of a
 * file transfer system. Although interfaces cannot contain constructors,
 * it is assumed that a file transfer system class contains a constructor
 * that takes one argument, a {@code java.util.Properties} object
 * containing properties for the system.
 * @author Ed Baskerville
 *
 */
public interface FileTransferSystem
{
	/**
	 * Initiates a connection with the file transfer system.
	 * @throws FileTransferException
	 */
	public void connect() throws FileTransferException;
	
	/**
	 * Ends a connection with the file transfer system.
	 * @throws FileTransferException
	 */
	public void disconnect() throws FileTransferException;
	
	/**
	 * Uploads a file. The directory hierarchy containing the destination must already exist.
	 * This method is assumed to clobber any existing file in the destination location.
	 * @param localPath The path of the local file to upload.
	 * @param remotePath The path on the remote filesystem.
	 * @throws FileTransferException
	 */
	public void uploadFile(String localPath, String remotePath) throws FileTransferException;
	
	/**
	 * Downloads a file. The directory hierarchy containing the destination must already exist.
	 * This method is assumed to clobber any existing file in the destination location.
	 * @param remotePath The path of the file in the remote filesystem.
	 * @param localPath The local path at which to store the file.
	 * @throws FileTransferException
	 */
	public void downloadFile(String remotePath, String localPath) throws FileTransferException;
	
	/**
	 * Deletes a file on the server.
	 * @param path The path of the file to delete.
	 * @throws FileTransferException
	 */
	public void deleteFile(String path) throws FileTransferException;
	
	/**
	 * Creates a directory on the server. If any parent directories do not yet exist,
	 * this method should create them as well.
	 * @param path The path to the directory to be created.
	 * @throws FileTransferException
	 */
	public void makeDirectory(String path) throws FileTransferException;
	
	/**
	 * Removes a directory on the server, including all subdirectories and files contained
	 * within it.
	 * @param path
	 * @throws FileTransferException
	 */
	public void removeDirectory(String path) throws FileTransferException;
	
	/**
	 * Lists the contents of a directory.
	 * @param path The directory to list
	 * @return An array of strings containing the contained file and directory names.
	 * @throws FileTransferException
	 */
	public String[] list(String path) throws FileTransferException;
	
	/**
	 * Determines whether a given path is a directory.
	 * @param path The path to evaluate.
	 * @return {@code true} if the path exists and is a directory, {@code false} otherwise.
	 * @throws FileTransferException
	 */
	public boolean isDirectory(String path) throws FileTransferException;
}
