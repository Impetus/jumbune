package org.jumbune.clusterprofiling.yarn.beans;


import javax.xml.bind.annotation.XmlElement;

public class FairSchedulerQueueInfoAlt {

	private String type;
	private String queueName;
	private String schedulingPolicy;

	private Boolean preemptable;

	private Resource minResources;
	private Resource maxResources;
	private Resource usedResources;

	/**
	 * For Hadoop Versions 2.6.0 and higher, fairResources is 'Instantaneous
	 * Fair Share'. For Hadoop Versions before 2.6.0, fairResources is 'Steady
	 * Fair Share'
	 */
	private Resource fairResources;

	private Resource clusterResources;
	private Resource steadyFairResources;

	private long maxApps;
	private long numActiveApps;
	private long numPendingApps;
	
	// These 5 variables will be used by ui and while writing data to influxdb
	private Long averageWaitingTime;
	private long finalSteadyFairMemory;
	private long finalSteadyFairVCores;
	private double percentUsedMemory; // % used memory = used memory * 100 / steady fair memory or fair memory
	private Double percentUsedVCores;

	private ChildQueues childQueues;

	public String getType() {
		return type;
	}
	
	@XmlElement
	public void setType(String type) {
		this.type = type;
	}


	public String getQueueName() {
		return queueName;
	}

	@XmlElement
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}


	public String getSchedulingPolicy() {
		return schedulingPolicy;
	}

	@XmlElement
	public void setSchedulingPolicy(String schedulingPolicy) {
		this.schedulingPolicy = schedulingPolicy;
	}


	public Boolean getPreemptable() {
		return preemptable;
	}

	@XmlElement
	public void setPreemptable(Boolean preemptable) {
		this.preemptable = preemptable;
	}

	public Resource getMinResources() {
		return minResources;
	}

	@XmlElement
	public void setMinResources(Resource minResources) {
		this.minResources = minResources;
	}

	public Resource getMaxResources() {
		return maxResources;
	}

	@XmlElement
	public void setMaxResources(Resource maxResources) {
		this.maxResources = maxResources;
	}

	public Resource getUsedResources() {
		return usedResources;
	}

	@XmlElement
	public void setUsedResources(Resource usedResources) {
		this.usedResources = usedResources;
	}

	public Resource getFairResources() {
		return fairResources;
	}

	@XmlElement
	public void setFairResources(Resource fairResources) {
		this.fairResources = fairResources;
	}

	public Resource getClusterResources() {
		return clusterResources;
	}

	@XmlElement
	public void setClusterResources(Resource clusterResources) {
		this.clusterResources = clusterResources;
	}

	public Resource getSteadyFairResources() {
		return steadyFairResources;
	}

	@XmlElement
	public void setSteadyFairResources(Resource steadyFairResources) {
		this.steadyFairResources = steadyFairResources;
	}

	public long getMaxApps() {
		return maxApps;
	}

	@XmlElement
	public void setMaxApps(long maxApps) {
		this.maxApps = maxApps;
	}

	public long getNumActiveApps() {
		return numActiveApps;
	}

	@XmlElement
	public void setNumActiveApps(long numActiveApps) {
		this.numActiveApps = numActiveApps;
	}

	public long getNumPendingApps() {
		return numPendingApps;
	}

	@XmlElement
	public void setNumPendingApps(long numPendingApps) {
		this.numPendingApps = numPendingApps;
	}

	public ChildQueues getChildQueues() {
		return childQueues;
	}

	@XmlElement(name = "childQueues")
	public void setChildQueues(ChildQueues childQueues) {
		this.childQueues = childQueues;
	}

	public Long getAverageWaitingTime() {
		return averageWaitingTime;
	}

	public void setAverageWaitingTime(Long averageWaitingTime) {
		this.averageWaitingTime = averageWaitingTime;
	}

	public long getFinalSteadyFairMemory() {
		return finalSteadyFairMemory;
	}

	public void setFinalSteadyFairMemory(long finalSteadyFairMemory) {
		this.finalSteadyFairMemory = finalSteadyFairMemory;
	}

	public long getFinalSteadyFairVCores() {
		return finalSteadyFairVCores;
	}

	public void setFinalSteadyFairVCores(long finalSteadyFairVCores) {
		this.finalSteadyFairVCores = finalSteadyFairVCores;
	}

	public double getPercentUsedMemory() {
		return percentUsedMemory;
	}

	@XmlElement
	public void setPercentUsedMemory(double percentUsedMemory) {
		this.percentUsedMemory = percentUsedMemory;
	}

	public double getPercentUsedVCores() {
		return percentUsedVCores;
	}

	@XmlElement
	public void setPercentUsedVCores(Double percentUsedVCores) {
		this.percentUsedVCores = percentUsedVCores;
	}

}