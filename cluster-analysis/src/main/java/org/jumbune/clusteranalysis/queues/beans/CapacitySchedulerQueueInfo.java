package org.jumbune.clusteranalysis.queues.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class CapacitySchedulerQueueInfo {

	private String queueName;

	/**
	 * type contains either 'capacitySchedulerLeafQueueInfo' or null
	 */
	private String type;
	
	/**
	 * percentage used of defined capacity
	 */
	private float usedCapacity;
	

	/**
	 * percent defined of root queue
	 */
	private float absoluteCapacity;
	
	/**
	 * Currently using percent of root queue
	 */
	private float absoluteUsedCapacity;
	
	
	private Resource resourcesUsed;
	
	private List<CapacitySchedulerQueueInfo> queues;

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getType() {
		return type;
	}

	@XmlAttribute(name="type")
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * percentage used of defined capacity
	 * @return
	 */
	public float getUsedCapacity() {
		return usedCapacity;
	}

	public void setUsedCapacity(float usedCapacity) {
		this.usedCapacity = usedCapacity;
	}

	/**
	 *  percent defined of root queue
	 * @return
	 */
	public float getAbsoluteCapacity() {
		return absoluteCapacity;
	}

	public void setAbsoluteCapacity(float absoluteCapacity) {
		this.absoluteCapacity = absoluteCapacity;
	}

	public float getAbsoluteUsedCapacity() {
		return absoluteUsedCapacity;
	}

	public void setAbsoluteUsedCapacity(float absoluteUsedCapacity) {
		this.absoluteUsedCapacity = absoluteUsedCapacity;
	}

	public Resource getResourcesUsed() {
		return resourcesUsed;
	}

	public void setResourcesUsed(Resource resourcesUsed) {
		this.resourcesUsed = resourcesUsed;
	}

	public List<CapacitySchedulerQueueInfo> getQueues() {
		return queues;
	}

	@XmlElementWrapper(name="queues")
	@XmlElement(name = "queue")
	public void setQueues(List<CapacitySchedulerQueueInfo> queues) {
		this.queues = queues;
	}
	
	public boolean isLeafQueue() {
		return queues == null || (type != null && type.equals("capacitySchedulerLeafQueueInfo"));
	}

}
