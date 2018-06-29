package org.jumbune.common.beans.profiling;

import org.jumbune.common.beans.ResourceUsageMetrics;
import org.jumbune.common.beans.cluster.Location;


/**
 * POJO to store MapReduce attempt details.
 */
public class AttemptDetails {

	/** The location. */
	private Location location;
	
	/** The host name. */
	private String hostName;
	
	/** The start time. */
	private long startTime;
	
	/** The result. */
	private String result;
	
	/** The finish time. */
	private long finishTime;
	
	/** The attempt id. */
	private String attemptID;
	
	/** The shuffle finished. */
	private long shuffleFinished;
	
	/** The sort finished. */
	private long sortFinished;
	
	/** The hdfs bytes read. */
	private long hdfsBytesRead;
	
	/** The hdfs bytes written. */
	private long hdfsBytesWritten;
	
	/** The file bytes read. */
	private long fileBytesRead;
	
	/** The file bytes written. */
	private long fileBytesWritten;
	
	/** The map input records. */
	private long mapInputRecords;
	
	/** The map output bytes. */
	private long mapOutputBytes;
	
	/** The map output records. */
	private long mapOutputRecords;
	
	/** The combine input records. */
	private long combineInputRecords;
	
	/** The reduce input groups. */
	private long reduceInputGroups;
	
	/** The reduce input records. */
	private long reduceInputRecords;
	
	/** The reduce shuffle bytes. */
	private long reduceShuffleBytes;
	
	/** The reduce output records. */
	private long reduceOutputRecords;
	
	/** The spilled records. */
	private long spilledRecords;
	
	/** The map input bytes. */
	private long mapInputBytes;
	
	/** The resource usage metrics. */
	private ResourceUsageMetrics resourceUsageMetrics;

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
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
	 * Gets the start time.
	 *
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Sets the result.
	 *
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Gets the finish time.
	 *
	 * @return the finishTime
	 */
	public long getFinishTime() {
		return finishTime;
	}

	/**
	 * Sets the finish time.
	 *
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * Gets the attempt id.
	 *
	 * @return the attemptID
	 */
	public String getAttemptID() {
		return attemptID;
	}

	/**
	 * Sets the attempt id.
	 *
	 * @param attemptID the attemptID to set
	 */
	public void setAttemptID(String attemptID) {
		this.attemptID = attemptID;
	}

	/**
	 * Gets the shuffle finished.
	 *
	 * @return the shuffleFinished
	 */
	public long getShuffleFinished() {
		return shuffleFinished;
	}

	/**
	 * Sets the shuffle finished.
	 *
	 * @param shuffleFinished the shuffleFinished to set
	 */
	public void setShuffleFinished(long shuffleFinished) {
		this.shuffleFinished = shuffleFinished;
	}

	/**
	 * Gets the sort finished.
	 *
	 * @return the sortFinished
	 */
	public long getSortFinished() {
		return sortFinished;
	}

	/**
	 * Sets the sort finished.
	 *
	 * @param sortFinished the sortFinished to set
	 */
	public void setSortFinished(long sortFinished) {
		this.sortFinished = sortFinished;
	}

	/**
	 * Gets the hdfs bytes read.
	 *
	 * @return the hdfsBytesRead
	 */
	public long getHdfsBytesRead() {
		return hdfsBytesRead;
	}

	/**
	 * Sets the hdfs bytes read.
	 *
	 * @param hdfsBytesRead the hdfsBytesRead to set
	 */
	public void setHdfsBytesRead(long hdfsBytesRead) {
		this.hdfsBytesRead = hdfsBytesRead;
	}

	/**
	 * Gets the hdfs bytes written.
	 *
	 * @return the hdfsBytesWritten
	 */
	public long getHdfsBytesWritten() {
		return hdfsBytesWritten;
	}

	/**
	 * Sets the hdfs bytes written.
	 *
	 * @param hdfsBytesWritten the hdfsBytesWritten to set
	 */
	public void setHdfsBytesWritten(long hdfsBytesWritten) {
		this.hdfsBytesWritten = hdfsBytesWritten;
	}

	/**
	 * Gets the file bytes read.
	 *
	 * @return the fileBytesRead
	 */
	public long getFileBytesRead() {
		return fileBytesRead;
	}

	/**
	 * Sets the file bytes read.
	 *
	 * @param fileBytesRead the fileBytesRead to set
	 */
	public void setFileBytesRead(long fileBytesRead) {
		this.fileBytesRead = fileBytesRead;
	}

	/**
	 * Gets the file bytes written.
	 *
	 * @return the fileBytesWritten
	 */
	public long getFileBytesWritten() {
		return fileBytesWritten;
	}

	/**
	 * Sets the file bytes written.
	 *
	 * @param fileBytesWritten the fileBytesWritten to set
	 */
	public void setFileBytesWritten(long fileBytesWritten) {
		this.fileBytesWritten = fileBytesWritten;
	}

	/**
	 * Gets the map input records.
	 *
	 * @return the mapInputRecords
	 */
	public long getMapInputRecords() {
		return mapInputRecords;
	}

	/**
	 * Sets the map input records.
	 *
	 * @param mapInputRecords the mapInputRecords to set
	 */
	public void setMapInputRecords(long mapInputRecords) {
		this.mapInputRecords = mapInputRecords;
	}

	/**
	 * Gets the map output bytes.
	 *
	 * @return the mapOutputBytes
	 */
	public long getMapOutputBytes() {
		return mapOutputBytes;
	}

	/**
	 * Sets the map output bytes.
	 *
	 * @param mapOutputBytes the mapOutputBytes to set
	 */
	public void setMapOutputBytes(long mapOutputBytes) {
		this.mapOutputBytes = mapOutputBytes;
	}

	/**
	 * Gets the map output records.
	 *
	 * @return the mapOutputRecords
	 */
	public long getMapOutputRecords() {
		return mapOutputRecords;
	}

	/**
	 * Sets the map output records.
	 *
	 * @param mapOutputRecords the mapOutputRecords to set
	 */
	public void setMapOutputRecords(long mapOutputRecords) {
		this.mapOutputRecords = mapOutputRecords;
	}

	/**
	 * Gets the combine input records.
	 *
	 * @return the combineInputRecords
	 */
	public long getCombineInputRecords() {
		return combineInputRecords;
	}

	/**
	 * Sets the combine input records.
	 *
	 * @param combineInputRecords the combineInputRecords to set
	 */
	public void setCombineInputRecords(long combineInputRecords) {
		this.combineInputRecords = combineInputRecords;
	}

	/**
	 * Gets the reduce input groups.
	 *
	 * @return the reduceInputGroups
	 */
	public long getReduceInputGroups() {
		return reduceInputGroups;
	}

	/**
	 * Sets the reduce input groups.
	 *
	 * @param reduceInputGroups the reduceInputGroups to set
	 */
	public void setReduceInputGroups(int reduceInputGroups) {
		this.reduceInputGroups = reduceInputGroups;
	}

	/**
	 * Gets the reduce input records.
	 *
	 * @return the reduceInputRecords
	 */
	public long getReduceInputRecords() {
		return reduceInputRecords;
	}

	/**
	 * Sets the reduce input records.
	 *
	 * @param reduceInputRecords the reduceInputRecords to set
	 */
	public void setReduceInputRecords(int reduceInputRecords) {
		this.reduceInputRecords = reduceInputRecords;
	}

	/**
	 * Gets the reduce shuffle bytes.
	 *
	 * @return the reduceShuffleBytes
	 */
	public long getReduceShuffleBytes() {
		return reduceShuffleBytes;
	}

	/**
	 * Sets the reduce shuffle bytes.
	 *
	 * @param reduceShuffleBytes the reduceShuffleBytes to set
	 */
	public void setReduceShuffleBytes(long reduceShuffleBytes) {
		this.reduceShuffleBytes = reduceShuffleBytes;
	}

	/**
	 * Gets the reduce output records.
	 *
	 * @return the reduceOutputRecords
	 */
	public long getReduceOutputRecords() {
		return reduceOutputRecords;
	}

	/**
	 * Sets the reduce output records.
	 *
	 * @param reduceOutputRecords the reduceOutputRecords to set
	 */
	public void setReduceOutputRecords(int reduceOutputRecords) {
		this.reduceOutputRecords = reduceOutputRecords;
	}

	/**
	 * Gets the spilled records.
	 *
	 * @return the spilledRecords
	 */
	public long getSpilledRecords() {
		return spilledRecords;
	}

	/**
	 * Sets the spilled records.
	 *
	 * @param spilledRecords the spilledRecords to set
	 */
	public void setSpilledRecords(long spilledRecords) {
		this.spilledRecords = spilledRecords;
	}

	/**
	 * Gets the map input bytes.
	 *
	 * @return the mapInputBytes
	 */
	public long getMapInputBytes() {
		return mapInputBytes;
	}

	/**
	 * Sets the map input bytes.
	 *
	 * @param mapInputBytes the mapInputBytes to set
	 */
	public void setMapInputBytes(long mapInputBytes) {
		this.mapInputBytes = mapInputBytes;
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

}
