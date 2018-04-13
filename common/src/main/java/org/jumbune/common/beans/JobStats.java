package org.jumbune.common.beans;


public class JobStats implements Stats {

	private String jobName;
	private long heapUtilization; // job tracker
	private long noOfMappers;
	private long noOfReducers;
	private long mapTime; // -time taken by map
	private long reduceTime; // -time taken by reducer
	private long mapSetupTime;
	private long mapCleanupTime;
	private long reduceSetupTime;
	private long reduceCleanupTime;
	private long mapSpilledRecords;
	private long reduceSpilledRecords;
	private long fsBytesRead;
	private long fsBytesWritten;
	private long fileBytesRead;
	private long mapFileBytesWritten;
	private long reduceFileBytesWritten;
	private long mapCombinerInput;
	private long reduceCombinerInput;
	private long mapCombinerOutput;
	private long reduceCombinerOutput;
	private long mapInputRecords;
	private long mapOutputRecords;
	private long mapOutputBytes;
	private long reduceShuffleBytes;
	private long reduceInputRecords;
	private long reduceOutputRecords;
	private long maxOutputRecords;
	private long maxOutputBytes;
	private String jobStatus;

	public long getExecutionTime() {
		return mapTime + reduceTime + mapSetupTime + mapCleanupTime + reduceSetupTime + reduceCleanupTime;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public long getHeapUtilization() {
		return heapUtilization;
	}

	public void setHeapUtilization(long heapUtilization) {
		this.heapUtilization = heapUtilization;
	}

	public long getNoOfMappers() {
		return noOfMappers;
	}

	public void setNoOfMappers(long noOfMappers) {
		this.noOfMappers = noOfMappers;
	}

	public long getNoOfReducers() {
		return noOfReducers;
	}

	public void setNoOfReducers(long noOfReducers) {
		this.noOfReducers = noOfReducers;
	}

	public long getMapTime() {
		return mapTime;
	}

	public void setMapTime(long mapTime) {
		this.mapTime = mapTime;
	}

	public long getReduceTime() {
		return reduceTime;
	}

	public void setReduceTime(long reduceTime) {
		this.reduceTime = reduceTime;
	}

	public long getMapSetupTime() {
		return mapSetupTime;
	}

	public void setMapSetupTime(long mapSetupTime) {
		this.mapSetupTime = mapSetupTime;
	}

	public long getMapCleanupTime() {
		return mapCleanupTime;
	}

	public void setMapCleanupTime(long mapCleanupTime) {
		this.mapCleanupTime = mapCleanupTime;
	}

	public long getReduceSetupTime() {
		return reduceSetupTime;
	}

	public void setReduceSetupTime(long reduceSetupTime) {
		this.reduceSetupTime = reduceSetupTime;
	}

	public long getReduceCleanupTime() {
		return reduceCleanupTime;
	}

	public void setReduceCleanupTime(long reduceCleanupTime) {
		this.reduceCleanupTime = reduceCleanupTime;
	}

	public long getMapSpilledRecords() {
		return mapSpilledRecords;
	}

	public void setMapSpilledRecords(long mapSpilledRecords) {
		this.mapSpilledRecords = mapSpilledRecords;
	}

	public long getReduceSpilledRecords() {
		return reduceSpilledRecords;
	}

	public void setReduceSpilledRecords(long reduceSpilledRecords) {
		this.reduceSpilledRecords = reduceSpilledRecords;
	}

	public long getFsBytesRead() {
		return fsBytesRead;
	}

	public void setFsBytesRead(long fsBytesRead) {
		this.fsBytesRead = fsBytesRead;
	}

	public long getFsBytesWritten() {
		return fsBytesWritten;
	}

	public void setFsBytesWritten(long fsBytesWritten) {
		this.fsBytesWritten = fsBytesWritten;
	}

	public long getFileBytesRead() {
		return fileBytesRead;
	}

	public void setFileBytesRead(long fileBytesRead) {
		this.fileBytesRead = fileBytesRead;
	}

	public long getMapFileBytesWritten() {
		return mapFileBytesWritten;
	}

	public void setMapFileBytesWritten(long mapFileBytesWritten) {
		this.mapFileBytesWritten = mapFileBytesWritten;
	}

	public long getReduceFileBytesWritten() {
		return reduceFileBytesWritten;
	}

	public void setReduceFileBytesWritten(long reduceFileBytesWritten) {
		this.reduceFileBytesWritten = reduceFileBytesWritten;
	}

	public long getMapCombinerInput() {
		return mapCombinerInput;
	}

	public void setMapCombinerInput(long mapCombinerInput) {
		this.mapCombinerInput = mapCombinerInput;
	}

	public long getReduceCombinerInput() {
		return reduceCombinerInput;
	}

	public void setReduceCombinerInput(long reduceCombinerInput) {
		this.reduceCombinerInput = reduceCombinerInput;
	}

	public long getMapCombinerOutput() {
		return mapCombinerOutput;
	}

	public void setMapCombinerOutput(long mapCombinerOutput) {
		this.mapCombinerOutput = mapCombinerOutput;
	}

	public long getReduceCombinerOutput() {
		return reduceCombinerOutput;
	}

	public void setReduceCombinerOutput(long reduceCombinerOutput) {
		this.reduceCombinerOutput = reduceCombinerOutput;
	}

	public long getMapInputRecords() {
		return mapInputRecords;
	}

	public void setMapInputRecords(long mapInputRecords) {
		this.mapInputRecords = mapInputRecords;
	}

	public long getMapOutputRecords() {
		return mapOutputRecords;
	}

	public void setMapOutputRecords(long mapOutputRecords) {
		this.mapOutputRecords = mapOutputRecords;
	}

	public long getMapOutputBytes() {
		return mapOutputBytes;
	}

	public void setMapOutputBytes(long mapOutputBytes) {
		this.mapOutputBytes = mapOutputBytes;
	}

	public long getReduceShuffleBytes() {
		return reduceShuffleBytes;
	}

	public void setReduceShuffleBytes(long reduceShuffleBytes) {
		this.reduceShuffleBytes = reduceShuffleBytes;
	}

	public long getReduceInputRecords() {
		return reduceInputRecords;
	}

	public void setReduceInputRecords(long reduceInputRecords) {
		this.reduceInputRecords = reduceInputRecords;
	}

	public long getReduceOutputRecords() {
		return reduceOutputRecords;
	}

	public void setReduceOutputRecords(long reduceOutputRecords) {
		this.reduceOutputRecords = reduceOutputRecords;
	}

	
	public long getMaxOutputRecords() {
		return maxOutputRecords;
	}

	public void setMaxOutputRecords(long maxOutputRecords) {
		this.maxOutputRecords = maxOutputRecords;
	}

	
	public long getMaxOutputBytes() {
		return maxOutputBytes;
	}

	public void setMaxOutputBytes(long maxOutputBytes) {
		this.maxOutputBytes = maxOutputBytes;
	}

	@Override
	public String toString() {
		return "[jobName : " + jobName + " || heapUtilization =" + heapUtilization + " || noOfMappers = " + noOfMappers + " || noOfReducers = "
				+ noOfReducers + " || mapTime = " + mapTime + " || reduceTime = " + reduceTime + " || mapSetupTime = " + mapSetupTime
				+ " || mapCleanupTime = " + mapCleanupTime + " || reduceSetupTime = " + reduceSetupTime + " || reduceCleanupTime = "
				+ reduceCleanupTime + " || mapSpilledRecords = " + mapSpilledRecords + " || reduceSpilledRecords = " + reduceSpilledRecords
				+ " || fsBytesRead = " + fsBytesRead + " || fsBytesWritten = " + fsBytesWritten + " || fileBytesRead = " + fileBytesRead
				+ " || mapFileBytesWritten = " + mapFileBytesWritten + " || reduceFileBytesWritten = " + reduceFileBytesWritten
				+ " || mapCombinerInput = " + mapCombinerInput + " || reduceCombinerInput = " + reduceCombinerInput + " || mapCombinerOutput = "
				+ mapCombinerOutput + " || reduceCombinerOutput = " + reduceCombinerOutput + " || mapInputRecords = " + mapInputRecords
				+ " || mapOutputRecords = " + mapOutputRecords + " || mapOutputBytes = " + mapOutputBytes + " || reduceShuffleBytes = "
				+ reduceShuffleBytes + " || reduceInputRecords = " + reduceInputRecords + " || reduceOutputRecords = " + reduceOutputRecords +
				"maxOutputRecords="+maxOutputRecords+"maxOutputBytes="+maxOutputBytes+"]";
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

}