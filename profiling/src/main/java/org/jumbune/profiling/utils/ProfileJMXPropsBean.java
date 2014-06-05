package org.jumbune.profiling.utils;

import java.util.List;
import java.text.DecimalFormat;

/**
 * This class holds the properties exposed via JMX beans for Task Tracker and Data/Name nodes.
 * 
 * 
 */
public class ProfileJMXPropsBean {
	
	private static final double BYTES_TO_GB_DIVIDER = 1073741824;
	private static final DecimalFormat CAPACITY_TOTAL_FORMATTER = new DecimalFormat("#,###.####");

	private String readsFromLocalClient;
	private String readsFromRemoteClient;
	private String writeBlockOpAvgTime;
	private String blocksRead;
	private String bytesWritten;
	private String rpcProcessingTimeMaxTime;
	private String rpcQueueTimeMaxTime;
	private String capacityTotal;
	private String blocksTotal;
	private List hotties;	
	
	public List getHotties() {
		return hotties;
	}

	public void setHotties(List hotties) {
		this.hotties = hotties;
	}

	/**
	 * This method returns the property reads_from_local_client from the data node.
	 * 
	 * @return String
	 */
	public String getReadsFromLocalClient() {
		return readsFromLocalClient;
	}

	/**
	 * This method sets the property reads_from_local_client
	 * 
	 * @param readsFromLocalClient
	 */
	public void setReadsFromLocalClient(String readsFromLocalClient) {
		this.readsFromLocalClient = readsFromLocalClient;
	}

	/**
	 * This method returns the property reads_from_remote_client from the data node.
	 * 
	 * @return String
	 */
	public String getReadsFromRemoteClient() {
		return readsFromRemoteClient;
	}

	/**
	 * This method sets the property reads_from_remote_client
	 * 
	 * @param readsFromLocalClient
	 */
	public void setReadsFromRemoteClient(String readsFromRemoteClient) {
		this.readsFromRemoteClient = readsFromRemoteClient;
	}

	/**
	 * This method returns the property writeBlockOpAvgTime from the data node.
	 * 
	 * @return String
	 */
	public String getWriteBlockOpAvgTime() {
		return writeBlockOpAvgTime;
	}

	/**
	 * This method sets the property writeBlockOpAvgTime
	 * 
	 * @param writeBlockOpAvgTime
	 */
	public void setWriteBlockOpAvgTime(String writeBlockOpAvgTime) {
		this.writeBlockOpAvgTime = writeBlockOpAvgTime;
	}

	/**
	 * This method returns the property blocks_read from the data node.
	 * 
	 * @return String
	 */
	public String getBlocksRead() {
		return blocksRead;
	}

	/**
	 * This method sets the property blocks_read
	 * 
	 * @param blocksRead
	 */
	public void setBlocksRead(String blocksRead) {
		this.blocksRead = blocksRead;
	}

	/**
	 * This method returns the property bytes_written from the data node.
	 * 
	 * @return String
	 */
	public String getBytesWritten() {
		return bytesWritten;
	}

	/**
	 * This method sets the property bytes_written
	 * 
	 * @param bytesWritten
	 */
	public void setBytesWritten(String bytesWritten) {
		this.bytesWritten = bytesWritten;
	}

	/**
	 * This method returns the property rpcProcessingTimeMaxTime from the Task Tracker.
	 * 
	 * @return String
	 */
	public String getRpcProcessingTimeMaxTime() {
		return rpcProcessingTimeMaxTime;
	}

	/**
	 * This method sets the property rpcProcessingTimeMaxTime
	 * 
	 * @param rpcProcessingTimeMaxTime
	 */
	public void setRpcProcessingTimeMaxTime(String rpcProcessingTimeMaxTime) {
		this.rpcProcessingTimeMaxTime = rpcProcessingTimeMaxTime;
	}

	/**
	 * This method returns the property rpcQueueTimeMaxTime from the Task Tracker
	 * 
	 * @return String
	 */
	public String getRpcQueueTimeMaxTime() {
		return rpcQueueTimeMaxTime;
	}

	/**
	 * This method sets the property rpcQueueTimeMaxTime
	 * 
	 * @param rpcQueueTimeMaxTime
	 */
	public void setRpcQueueTimeMaxTime(String rpcQueueTimeMaxTime) {
		this.rpcQueueTimeMaxTime = rpcQueueTimeMaxTime;
	}

	/**
	 * This method returns the property reads_from_local_client from the name node.
	 * 
	 * @return String
	 */
	public String getCapacityTotal() {
		return capacityTotal;
	}

	/**
	 * This method sets the property capacityTotal. It converts the
	 * bytes into GBs and stores the same
	 * 
	 * @param capacityTotal
	 */
	public void setCapacityTotal(String capacityTotal) {
		
		double value =  Double.parseDouble(capacityTotal);
		
		final double result = value > 0 ? value/ BYTES_TO_GB_DIVIDER : 0;
		this.capacityTotal = CAPACITY_TOTAL_FORMATTER.format(result);
	}

	/**
	 * This method returns the property blocksTotal from the name node.
	 * 
	 * @return String
	 */
	public String getBlocksTotal() {
		return blocksTotal;
	}

	/**
	 * This method sets the property blocksTotal
	 * 
	 * @param blocksTotal
	 */
	public void setBlocksTotal(String blocksTotal) {
		this.blocksTotal = blocksTotal;
	}

	@Override
	public String toString() {
		return "ProfileJMXPropsBean [reads_from_local_client=" + readsFromLocalClient + ", reads_from_remote_client=" + readsFromRemoteClient
				+ ", writeBlockOpAvgTime=" + writeBlockOpAvgTime + ", blocks_read=" + blocksRead + ", bytes_written=" + bytesWritten
				+ ", rpcProcessingTimeAvgTime=" + rpcProcessingTimeMaxTime + ", rpcQueueTimeAvgTime=" + rpcQueueTimeMaxTime + ", capacityTotal="
				+ capacityTotal + ", blocksTotal=" + blocksTotal + "]";
	}

}