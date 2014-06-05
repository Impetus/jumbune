package org.jumbune.hadoop.distro;

import org.apache.hadoop.fs.FileStatus;
import org.jumbune.utils.beans.VirtualFileStatus;



/**
 * The Class ApacheFileStatus.
 */
public class ApacheDistributionFileStatus implements VirtualFileStatus {
	
	/** The file status. */
	private FileStatus fileStatus;

	/**
	 * Instantiates a new apache file status.
	 *
	 * @param fileStatus the file status
	 */
	public ApacheDistributionFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileStatus#getReplication()
	 */
	@Override
	public short getReplication() {
		return fileStatus.getReplication();
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileStatus#get()
	 */
	@Override
	public Object get() {
		return fileStatus;
	}

}
