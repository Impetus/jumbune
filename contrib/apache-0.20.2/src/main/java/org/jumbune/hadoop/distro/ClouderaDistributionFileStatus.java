package org.jumbune.hadoop.distro;

import org.apache.hadoop.fs.FileStatus;
import org.jumbune.utils.beans.VirtualFileStatus;



/**
 * The Class Cloudera.
 */
public class ClouderaDistributionFileStatus implements VirtualFileStatus {
	
	/** The file status. */
	private FileStatus fileStatus;
	
	
	/**
	 * Instantiates a new cloudera.
	 *
	 * @param fileStatus the file status
	 */
	public ClouderaDistributionFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	
	/**
	 * This method is used to get the replication.
	 *
	 * @return the replication
	 */
	public short getReplication() {
		return fileStatus.getReplication();
	}

	/**
	 * This method is used to get the object.
	 *
	 * @return the object
	 */
	public Object get() {
		return fileStatus;
	}

}
