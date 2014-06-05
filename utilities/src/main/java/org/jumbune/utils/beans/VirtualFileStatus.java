package org.jumbune.utils.beans;
/***
 * This interface is a wrapper for FileStatus.java class in hadoop, which represent the client side information for a file.As FileStatus.java having certain changes in each release of Hadoop. So this class provide one
 * access point for support different version of hadoop's FileStatus class.All methods of this class are taken from  HadoopAPIs.
 * 
 *
 */
public interface VirtualFileStatus {
	
	/**
	 * This method is used to get the replication.
	 *
	 * @return the replication
	 */
	short getReplication();

	/**
	 * This method is used to get the object.
	 *
	 * @return the object
	 */
	Object get();
}
