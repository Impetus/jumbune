package org.jumbune.clusteranalysis.queues.beans;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class SchedulerInfo {
	
	/**
	 * type is either 'capacityScheduler' or 'fairScheduler'
	 */
	private String type;
	
	/**
	 * Should be used if Fair Scheduler is enabled
	 */
	private FairSchedulerQueueInfo rootQueue;
	
	/**
	 * Should be used if Capacity Scheduler is enabled
	 */
	private ArrayList<CapacitySchedulerQueueInfo> queues;
	
	public FairSchedulerQueueInfo getRootQueue() {
		return rootQueue;
	}
	
	@XmlElement
	public void setRootQueue(FairSchedulerQueueInfo rootQueue) {
		this.rootQueue = rootQueue;
	}
	public String getType() {
		return type;
	}
	
	@XmlAttribute(namespace = "http://www.w3.org/2001/XMLSchema-instance", name="type")
	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<CapacitySchedulerQueueInfo> getQueues() {
		return queues;
	}

	@XmlElementWrapper(name="queues")
	@XmlElement(name = "queue")
	public void setQueues(ArrayList<CapacitySchedulerQueueInfo> queues) {
		this.queues = queues;
	}
}
