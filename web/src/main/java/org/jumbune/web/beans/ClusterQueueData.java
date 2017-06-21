package org.jumbune.web.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * It contains queue utilization data of all queues of 24 hours.
 * 
 * @author impadmin
 *
 */
public class ClusterQueueData {
	/**
	 * It contains time from which we starts adding values for current hour so
	 * that in next hour or after 60 minutes, we could calculate average for
	 * this hour(checkpoint). It is used to update data in QueueData. For more details see QueueData
	 */
	private Date checkpoint;
	
	/**
	 * Name of cluster
	 */
	private String clusterName;
	
	/**
	 * key is queue name, and object is QueueData
	 */
	private Map<String, QueueData> queueDataMap = new HashMap<String, QueueData>();

	public ClusterQueueData(Date date, String clusterName) {
		this.setCheckpoint(checkpoint);
		this.setClusterName(clusterName);
	}

	public QueueData getQueueDataFromMap(String queueName) {
		return queueDataMap.get(queueName);
	}

	public Map<String, QueueData> getQueueDataMap() {
		return queueDataMap;
	}

	public void setQueueDataMap(Map<String, QueueData> queueDataList) {
		this.queueDataMap = queueDataList;
	}

	public void addQueueDataInMap(String queueName, QueueData queueData) {
		queueDataMap.put(queueName, queueData);
	}

	public Date getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(Date checkpoint) {
		this.checkpoint = checkpoint;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
