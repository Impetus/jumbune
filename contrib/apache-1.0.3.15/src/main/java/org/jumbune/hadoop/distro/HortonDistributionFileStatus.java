package org.jumbune.hadoop.distro;

import org.apache.hadoop.fs.FileStatus;
import org.jumbune.utils.beans.VirtualFileStatus;




/**
 * The Class HortonFileStatus.
 */
public class HortonDistributionFileStatus implements VirtualFileStatus {
	
	/** The file status. */
	private FileStatus fileStatus;

	/**
	 * Instantiates a new horton file status.
	 *
	 * @param fileStatus the file status
	 */
	public HortonDistributionFileStatus(FileStatus fileStatus) {
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
