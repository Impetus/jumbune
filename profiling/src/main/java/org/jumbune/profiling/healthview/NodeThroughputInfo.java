package org.jumbune.profiling.healthview;

/**
 * This class provides read,write and speed throughput for a node.
 * 
 */
public class NodeThroughputInfo implements ResultInfo {

	private int readThroughput;
	private int writeThroughput;
	private int processingThroughput;

	/**
	 * @return the readThroughput
	 */
	public int getReadThroughput() {
		return readThroughput;
	}

	/**
	 * @param readThroughput
	 *            the readThroughput to set
	 */
	public void setReadThroughput(int readThroughput) {
		this.readThroughput = readThroughput;
	}

	/**
	 * @return the writeThroughput
	 */
	public int getWriteThroughput() {
		return writeThroughput;
	}

	/**
	 * @param writeThroughput
	 *            the writeThroughput to set
	 */
	public void setWriteThroughput(int writeThroughput) {
		this.writeThroughput = writeThroughput;
	}

	/**
	 * @return the processingThroughput
	 */
	public int getProcessingThroughput() {
		return processingThroughput;
	}

	/**
	 * @param processingThroughput
	 *            the processingThroughput to set
	 */
	public void setProcessingThroughput(int processingThroughput) {
		this.processingThroughput = processingThroughput;
	}

}
