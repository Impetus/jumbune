package org.jumbune.clusteranalysis.queues.beans;

import java.util.List;

import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.QueueState;
import org.apache.hadoop.yarn.api.records.QueueInfo;


/**
 * The Class YarnQueueStats.
 */
public class YarnQueueStats extends QueueStats{

	
    /** The child queues. */
    private List<QueueInfo> childQueues; 
    
    /** The applications. */
    private List<ApplicationReport> applications;
    
    /** The queue state. */
    private QueueState queueState;
	
       
	/**
	 * Gets the child queues.
	 *
	 * @return the child queues
	 */
	public List<QueueInfo> getChildQueues() {
		return childQueues;
	}
	
	/**
	 * Sets the child queues.
	 *
	 * @param childQueues the new child queues
	 */
	public void setChildQueues(List<QueueInfo> childQueues) {
		this.childQueues = childQueues;
	}
	
	/**
	 * Gets the applications.
	 *
	 * @return the applications
	 */
	public List<ApplicationReport> getApplications() {
		return applications;
	}
	
	/**
	 * Sets the applications.
	 *
	 * @param applications the new applications
	 */
	public void setApplications(List<ApplicationReport> applications) {
		this.applications = applications;
	}
	
	/**
	 * Gets the queue state.
	 *
	 * @return the queue state
	 */
	public QueueState getQueueState() {
		return queueState;
	}
	
	/**
	 * Sets the queue state.
	 *
	 * @param queueState the new queue state
	 */
	public void setQueueState(QueueState queueState) {
		this.queueState = queueState;
	}
    
    
}
