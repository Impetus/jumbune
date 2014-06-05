package org.jumbune.profiling.healthview;

import java.util.List;

/**
 * This class contains read/write usage information for all disk partitions on a node.
 * 
 */
public class NodeDiskPartitionsInfo implements ResultInfo {

	private List<DiskPartitionInfo> diskPartitionsList;

	/**
	 * @return the diskPartitionsList
	 */
	public List<DiskPartitionInfo> getDiskPartitionsList() {
		return diskPartitionsList;
	}

	/**
	 * @param diskPartitionsList
	 *            the diskPartitionsList to set
	 */
	public void setDiskPartitionsList(List<DiskPartitionInfo> diskPartitionsList) {
		this.diskPartitionsList = diskPartitionsList;
	}

}
