package org.jumbune.common.beans.profiling;

import java.util.List;

import org.jumbune.common.beans.ResourceUsageMetrics;
import org.jumbune.common.beans.cluster.Location;


/**
 * POJO to store MapReduce phase details.
 */
public class TaskOutputDetails {

	/** The task type. */
	private String taskType;
	
	/** The task status. */
	private String taskStatus;
	
	/** The task id. */
	private String taskID;
	
	/** The start point. */
	private long startPoint;
	
	/** The end point. */
	private long endPoint;
	
	/** The location. */
	private String location;
	
	/** The data flow rate. */
	private long dataFlowRate; 
	
	/** The host name. */
	private String hostName;
	
	/** The time taken. */
	private long timeTaken; 
	
	/** The shuffle start. */
	private long shuffleStart;
	
	/** The shuffle end. */
	private long shuffleEnd;
	
	/** The sort start. */
	private long sortStart;
	
	/** The sort end. */
	private long sortEnd;
	
	/** The reduce start. */
	private long reduceStart;
	
	/** The reduce end. */
	private long reduceEnd;
	
	/** The output bytes. */
	private long outputBytes;
	
	/** The output records. */
	private long outputRecords;
	
	/** The preferred locations. */
	private List<Location> preferredLocations ;

	/** The resource usage metrics. */
	private ResourceUsageMetrics resourceUsageMetrics;
	
	/**
	 * Gets the task type.
	 *
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * Sets the task type.
	 *
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * Gets the task status.
	 *
	 * @return the taskStatus
	 */
	public String getTaskStatus() {
		return taskStatus;
	}

	/**
	 * Sets the task status.
	 *
	 * @param taskStatus the taskStatus to set
	 */
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the host name.
	 *
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Gets the time taken.
	 *
	 * @return the timeTaken
	 */
	public long getTimeTaken() {
		return timeTaken;
	}

	/**
	 * Sets the time taken.
	 *
	 * @param timeTaken the timeTaken to set
	 */
	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	/**
	 * Gets the task id.
	 *
	 * @return the taskID
	 */
	public String getTaskID() {
		return taskID;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskID the taskID to set
	 */
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	/**
	 * Gets the start point.
	 *
	 * @return the startPoint
	 */
	public long getStartPoint() {
		return startPoint;
	}

	/**
	 * Sets the start point.
	 *
	 * @param startPoint the startPoint to set
	 */
	public void setStartPoint(long startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * Gets the end point.
	 *
	 * @return the endPoint
	 */
	public long getEndPoint() {
		return endPoint;
	}

	/**
	 * Sets the end point.
	 *
	 * @param endPoint the endPoint to set
	 */
	public void setEndPoint(long endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the data flow rate.
	 *
	 * @return the dataFlowRate
	 */
	public long getDataFlowRate() {
		return dataFlowRate;
	}

	/**
	 * Sets the data flow rate.
	 *
	 * @param dataFlowRate the dataFlowRate to set
	 */
	public void setDataFlowRate(long dataFlowRate) {
		this.dataFlowRate = dataFlowRate;
	}

	/**
	 * Gets the shuffle start.
	 *
	 * @return the shuffleStart
	 */
	public long getShuffleStart() {
		return shuffleStart;
	}

	/**
	 * Sets the shuffle start.
	 *
	 * @param shuffleStart the shuffleStart to set
	 */
	public void setShuffleStart(long shuffleStart) {
		this.shuffleStart = shuffleStart;
	}

	/**
	 * Gets the shuffle end.
	 *
	 * @return the shuffleEnd
	 */
	public long getShuffleEnd() {
		return shuffleEnd;
	}

	/**
	 * Sets the shuffle end.
	 *
	 * @param shuffleEnd the shuffleEnd to set
	 */
	public void setShuffleEnd(long shuffleEnd) {
		this.shuffleEnd = shuffleEnd;
	}

	/**
	 * Gets the sort start.
	 *
	 * @return the sortStart
	 */
	public long getSortStart() {
		return sortStart;
	}

	/**
	 * Sets the sort start.
	 *
	 * @param sortStart the sortStart to set
	 */
	public void setSortStart(long sortStart) {
		this.sortStart = sortStart;
	}

	/**
	 * Gets the sort end.
	 *
	 * @return the sortEnd
	 */
	public long getSortEnd() {
		return sortEnd;
	}

	/**
	 * Sets the sort end.
	 *
	 * @param sortEnd the sortEnd to set
	 */
	public void setSortEnd(long sortEnd) {
		this.sortEnd = sortEnd;
	}

	/**
	 * Gets the reduce start.
	 *
	 * @return the reduceStart
	 */
	public long getReduceStart() {
		return reduceStart;
	}

	/**
	 * Sets the reduce start.
	 *
	 * @param reduceStart the reduceStart to set
	 */
	public void setReduceStart(long reduceStart) {
		this.reduceStart = reduceStart;
	}

	/**
	 * Gets the reduce end.
	 *
	 * @return the reduceEnd
	 */
	public long getReduceEnd() {
		return reduceEnd;
	}

	/**
	 * Sets the reduce end.
	 *
	 * @param reduceEnd the reduceEnd to set
	 */
	public void setReduceEnd(long reduceEnd) {
		this.reduceEnd = reduceEnd;
	}

	/**
	 * Gets the output bytes.
	 *
	 * @return the outputBytes
	 */
	public long getOutputBytes() {
		return outputBytes;
	}

	/**
	 * Sets the output bytes.
	 *
	 * @param outputBytes the outputBytes to set
	 */
	public void setOutputBytes(long outputBytes) {
		this.outputBytes = outputBytes;
	}

	/**
	 * Gets the output records.
	 *
	 * @return the outputRecords
	 */
	public long getOutputRecords() {
		return outputRecords;
	}

	/**
	 * Sets the output records.
	 *
	 * @param outputRecords the outputRecords to set
	 */
	public void setOutputRecords(long outputRecords) {
		this.outputRecords = outputRecords;
	}
	
	/**
	 * Gets the resource usage metrics.
	 *
	 * @return the resourceUsageMetrics
	 */
	public ResourceUsageMetrics getResourceUsageMetrics() {
		return resourceUsageMetrics;
	}

	/**
	 * Sets the resource usage metrics.
	 *
	 * @param resourceUsageMetrics the resourceUsageMetrics to set
	 */
	public void setResourceUsageMetrics(ResourceUsageMetrics resourceUsageMetrics) {
		this.resourceUsageMetrics = resourceUsageMetrics;
	}
	
	/**
	 * Gets the preferred locations.
	 *
	 * @return the preferred locations
	 */
	public List<Location> getPreferredLocations() {
		return preferredLocations;
	}

	/**
	 * Sets the preferred locations.
	 *
	 * @param preferredLocations the preferred locations
	 */
	public void setPreferredLocations(List<Location> preferredLocations) {
		this.preferredLocations = preferredLocations;
	}

	@Override
	public String toString() {
		return "TaskOutputDetails [taskType=" + taskType + ", taskStatus="
				+ taskStatus + ", taskID=" + taskID + ", startPoint="
				+ startPoint + ", endPoint=" + endPoint + ", location="
				+ location + ", dataFlowRate=" + dataFlowRate + ", hostName="
				+ hostName + ", timeTaken=" + timeTaken + ", shuffleStart="
				+ shuffleStart + ", shuffleEnd=" + shuffleEnd + ", sortStart="
				+ sortStart + ", sortEnd=" + sortEnd + ", reduceStart="
				+ reduceStart + ", reduceEnd=" + reduceEnd + ", outputBytes="
				+ outputBytes + ", outputRecords=" + outputRecords
				+ ", preferredLocations=" + preferredLocations
				+ ", resourceUsageMetrics=" + resourceUsageMetrics + "]";
	}

	



}
