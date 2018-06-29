package org.jumbune.monitoring.healthview;

/**
 * This class stores read/write usage information related to a disk partition on a node of the cluster.
 * 
 */
public class DiskPartitionInfo {

	private String name;

	private long reads;

	private long readSectors;

	private long writes;

	private long requestedWrites;

	private double readUsage;

	private double writeUsage;

	private int spaceUsage;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the reads
	 */
	public long getReads() {
		return reads;
	}

	/**
	 * @param reads
	 *            the reads to set
	 */
	public void setReads(long reads) {
		this.reads = reads;
	}

	/**
	 * @return the readSectors
	 */
	public long getReadSectors() {
		return readSectors;
	}

	/**
	 * @param readSectors
	 *            the readSectors to set
	 */
	public void setReadSectors(long readSectors) {
		this.readSectors = readSectors;
	}

	/**
	 * @return the writes
	 */
	public long getWrites() {
		return writes;
	}

	/**
	 * @param writes
	 *            the writes to set
	 */
	public void setWrites(long writes) {
		this.writes = writes;
	}

	/**
	 * @return the requestedWrites
	 */
	public long getRequestedWrites() {
		return requestedWrites;
	}

	/**
	 * @param requestedWrites
	 *            the requestedWrites to set
	 */
	public void setRequestedWrites(long requestedWrites) {
		this.requestedWrites = requestedWrites;
	}

	/**
	 * @return the readUsage
	 */
	public double getReadUsage() {
		return readUsage;
	}

	/**
	 * @param readUsage
	 *            the readUsage to set
	 */
	public void setReadUsage(double readUsage) {
		this.readUsage = readUsage;
	}

	/**
	 * @return the writeUsage
	 */
	public double getWriteUsage() {
		return writeUsage;
	}

	/**
	 * @param writeUsage
	 *            the writeUsage to set
	 */
	public void setWriteUsage(double writeUsage) {
		this.writeUsage = writeUsage;
	}

	/**
	 * @return the spaceUsage
	 */
	public int getSpaceUsage() {
		return spaceUsage;
	}

	/**
	 * @param spaceUsage
	 *            the spaceUsage to set
	 */
	public void setSpaceUsage(int spaceUsage) {
		this.spaceUsage = spaceUsage;
	}

}
