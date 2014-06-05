package org.jumbune.utils.beans;

import java.io.IOException;

/***
 * This a wrapper of class FileSystem.java in hadoop.As FileSystem.java having certain changes in each release of Hadoop, So this class provides one
 * access point for support different version of hadoop's FileSystem class.All methods of this class are taken from Hadoop APIs.
 * 

 * 
 */
public interface VirtualFileSystem {
	
	/**
	 * This method is used to get the file status.
	 *
	 * @param path HDFS file path
	 * @return the file status
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	VirtualFileStatus getFileStatus(String path) throws IOException;

	/**
	 * This method is used to get the length of the file kept at the HDFS path
	 *
	 * @param path HDFS file Path
	 * @return the length of file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	long getLengthOfFile(String path) throws IOException;

	/**
	 * This method is used to get the file block locations.
	 *
	 * @param fileStatus the file status
	 * @param start the start
	 * @param end the end
	 * @return the file block locations
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	Object getFileBlockLocations(VirtualFileStatus fileStatus, long start, long end) throws IOException;

	/**
	 * This method is used to check whether the path exists or not.
	 *
	 * @param path the path
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	boolean exists(String path) throws IOException;

	/**
	 * This method is used to Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void close() throws IOException;

	/**
	 * This method is used to get the space consumed at the HDFS.
	 *
	 * @param hdfsPath the hdfs path
	 * @return the space consumed
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	long getSpaceConsumed(String hdfsPath) throws IOException;

	/**
	 * This method is used to checks if is file or not.
	 *
	 * @param filePath the file path
	 * @return true, if is file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	boolean isFile(String filePath) throws IOException;

	/**
	 * This method is used to delete the file present at the hdfs.
	 *
	 * @param path the path
	 * @param recursive the recursive
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void delete(String path, boolean recursive) throws IOException;
}
