package org.jumbune.common.beans.profiling;

import java.util.List;
import java.util.Properties;



/**
 * POJO to store details for a MapReduce Job.
 */
public class JobDetails {

	/** The priority. */
	private String priority;
	
	/** The job id. */
	private String jobID;
	
	/** The user. */
	private String user;
	
	/** The job name. */
	private String jobName;
	
	/** The map tasks. */
	private List<TaskDetails> mapTasks;
	
	/** The finish time. */
	private long finishTime;
	
	/** The reduce tasks. */
	private List<TaskDetails> reduceTasks;
	
	/** The submit time. */
	private long submitTime;
	
	/** The launch time. */
	private long launchTime;
	
	/** The total maps. */
	private long totalMaps;
	
	/** The total reduces. */
	private long totalReduces;
	
	/** The other tasks. */
	private List<TaskDetails> otherTasks;
	
	/** The queue. */
	private String queue;
	
	/** The job properties. */
	private Properties jobProperties;
	
	/** The computons per map input byte. */
	private long computonsPerMapInputByte;
	
	/** The computons per map output byte. */
	private long computonsPerMapOutputByte;
	
	/** The computons per reduce input byte. */
	private long computonsPerReduceInputByte;
	
	/** The computons per reduce output byte. */
	private long computonsPerReduceOutputByte;
	
	/** The heap megabytes. */
	private long heapMegabytes;
	
	/** The outcome. */
	private String outcome;
	
	/** The jobtype. */
	private String jobtype;
	
	/** The direct dependant jobs. */
	private List<String> directDependantJobs;
	
	/** The mapper tries to succeed. */
	private List<Float> mapperTriesToSucceed;
	
	/** The failed mapper fraction. */
	private float failedMapperFraction;
	
	/** The relative time. */
	private long relativeTime;
	
	/** The cluster map mb. */
	private long clusterMapMB;
	
	/** The cluster reduce mb. */
	private long clusterReduceMB;
	
	/** The job map mb. */
	private long jobMapMB;
	
	/** The job reduce mb. */
	private long jobReduceMB;

	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * Gets the job id.
	 *
	 * @return the jobID
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the job name.
	 *
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the job name.
	 *
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Gets the map tasks.
	 *
	 * @return the mapTasks
	 */
	public List<TaskDetails> getMapTasks() {
		return mapTasks;
	}

	/**
	 * Sets the map tasks.
	 *
	 * @param mapTasks the mapTasks to set
	 */
	public void setMapTasks(List<TaskDetails> mapTasks) {
		this.mapTasks = mapTasks;
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
	 * Gets the reduce tasks.
	 *
	 * @return the reduceTasks
	 */
	public List<TaskDetails> getReduceTasks() {
		return reduceTasks;
	}

	/**
	 * Sets the reduce tasks.
	 *
	 * @param reduceTasks the reduceTasks to set
	 */
	public void setReduceTasks(List<TaskDetails> reduceTasks) {
		this.reduceTasks = reduceTasks;
	}

	/**
	 * Gets the submit time.
	 *
	 * @return the submitTime
	 */
	public long getSubmitTime() {
		return submitTime;
	}

	/**
	 * Sets the submit time.
	 *
	 * @param submitTime the submitTime to set
	 */
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}

	/**
	 * Gets the launch time.
	 *
	 * @return the launchTime
	 */
	public long getLaunchTime() {
		return launchTime;
	}

	/**
	 * Sets the launch time.
	 *
	 * @param launchTime the launchTime to set
	 */
	public void setLaunchTime(long launchTime) {
		this.launchTime = launchTime;
	}

	/**
	 * Gets the total maps.
	 *
	 * @return the totalMaps
	 */
	public long getTotalMaps() {
		return totalMaps;
	}

	/**
	 * Sets the total maps.
	 *
	 * @param totalMaps the totalMaps to set
	 */
	public void setTotalMaps(int totalMaps) {
		this.totalMaps = totalMaps;
	}

	/**
	 * Gets the total reduces.
	 *
	 * @return the totalReduces
	 */
	public long getTotalReduces() {
		return totalReduces;
	}

	/**
	 * Sets the total reduces.
	 *
	 * @param totalReduces the totalReduces to set
	 */
	public void setTotalReduces(int totalReduces) {
		this.totalReduces = totalReduces;
	}

	/**
	 * Gets the other tasks.
	 *
	 * @return the otherTasks
	 */
	public List<TaskDetails> getOtherTasks() {
		return otherTasks;
	}

	/**
	 * Sets the other tasks.
	 *
	 * @param otherTasks the otherTasks to set
	 */
	public void setOtherTasks(List<TaskDetails> otherTasks) {
		this.otherTasks = otherTasks;
	}

	/**
	 * Gets the queue.
	 *
	 * @return the queue
	 */
	public String getQueue() {
		return queue;
	}

	/**
	 * Sets the queue.
	 *
	 * @param queue the queue to set
	 */
	public void setQueue(String queue) {
		this.queue = queue;
	}

	/**
	 * Gets the job properties.
	 *
	 * @return the jobProperties
	 */
	public Properties getJobProperties() {
		return jobProperties;
	}

	/**
	 * Sets the job properties.
	 *
	 * @param jobProperties the jobProperties to set
	 */
	public void setJobProperties(Properties jobProperties) {
		this.jobProperties = jobProperties;
	}

	/**
	 * Gets the computons per map input byte.
	 *
	 * @return the computonsPerMapInputByte
	 */
	public long getComputonsPerMapInputByte() {
		return computonsPerMapInputByte;
	}

	/**
	 * Sets the computons per map input byte.
	 *
	 * @param computonsPerMapInputByte the computonsPerMapInputByte to set
	 */
	public void setComputonsPerMapInputByte(int computonsPerMapInputByte) {
		this.computonsPerMapInputByte = computonsPerMapInputByte;
	}

	/**
	 * Gets the computons per map output byte.
	 *
	 * @return the computonsPerMapOutputByte
	 */
	public long getComputonsPerMapOutputByte() {
		return computonsPerMapOutputByte;
	}

	/**
	 * Sets the computons per map output byte.
	 *
	 * @param computonsPerMapOutputByte the computonsPerMapOutputByte to set
	 */
	public void setComputonsPerMapOutputByte(int computonsPerMapOutputByte) {
		this.computonsPerMapOutputByte = computonsPerMapOutputByte;
	}

	/**
	 * Gets the computons per reduce input byte.
	 *
	 * @return the computonsPerReduceInputByte
	 */
	public long getComputonsPerReduceInputByte() {
		return computonsPerReduceInputByte;
	}

	/**
	 * Sets the computons per reduce input byte.
	 *
	 * @param computonsPerReduceInputByte the computonsPerReduceInputByte to set
	 */
	public void setComputonsPerReduceInputByte(int computonsPerReduceInputByte) {
		this.computonsPerReduceInputByte = computonsPerReduceInputByte;
	}

	/**
	 * Gets the computons per reduce output byte.
	 *
	 * @return the computonsPerReduceOutputByte
	 */
	public long getComputonsPerReduceOutputByte() {
		return computonsPerReduceOutputByte;
	}

	/**
	 * Sets the computons per reduce output byte.
	 *
	 * @param computonsPerReduceOutputByte the computonsPerReduceOutputByte to set
	 */
	public void setComputonsPerReduceOutputByte(int computonsPerReduceOutputByte) {
		this.computonsPerReduceOutputByte = computonsPerReduceOutputByte;
	}

	/**
	 * Gets the heap megabytes.
	 *
	 * @return the heapMegabytes
	 */
	public long getHeapMegabytes() {
		return heapMegabytes;
	}

	/**
	 * Sets the heap megabytes.
	 *
	 * @param heapMegabytes the heapMegabytes to set
	 */
	public void setHeapMegabytes(int heapMegabytes) {
		this.heapMegabytes = heapMegabytes;
	}

	/**
	 * Gets the outcome.
	 *
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * Sets the outcome.
	 *
	 * @param outcome the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	/**
	 * Gets the jobtype.
	 *
	 * @return the jobtype
	 */
	public String getJobtype() {
		return jobtype;
	}

	/**
	 * Sets the jobtype.
	 *
	 * @param jobtype the jobtype to set
	 */
	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}

	/**
	 * Gets the direct dependant jobs.
	 *
	 * @return the directDependantJobs
	 */
	public List<String> getDirectDependantJobs() {
		return directDependantJobs;
	}

	/**
	 * Sets the direct dependant jobs.
	 *
	 * @param directDependantJobs the directDependantJobs to set
	 */
	public void setDirectDependantJobs(List<String> directDependantJobs) {
		this.directDependantJobs = directDependantJobs;
	}

	/**
	 * Gets the mapper tries to succeed.
	 *
	 * @return the mapperTriesToSucceed
	 */
	public List<Float> getMapperTriesToSucceed() {
		return mapperTriesToSucceed;
	}

	/**
	 * Sets the mapper tries to succeed.
	 *
	 * @param mapperTriesToSucceed the mapperTriesToSucceed to set
	 */
	public void setMapperTriesToSucceed(List<Float> mapperTriesToSucceed) {
		this.mapperTriesToSucceed = mapperTriesToSucceed;
	}

	/**
	 * Gets the failed mapper fraction.
	 *
	 * @return the failedMapperFraction
	 */
	public float getFailedMapperFraction() {
		return failedMapperFraction;
	}

	/**
	 * Sets the failed mapper fraction.
	 *
	 * @param failedMapperFraction the failedMapperFraction to set
	 */
	public void setFailedMapperFraction(float failedMapperFraction) {
		this.failedMapperFraction = failedMapperFraction;
	}

	/**
	 * Gets the relative time.
	 *
	 * @return the relativeTime
	 */
	public long getRelativeTime() {
		return relativeTime;
	}

	/**
	 * Sets the relative time.
	 *
	 * @param relativeTime the relativeTime to set
	 */
	public void setRelativeTime(long relativeTime) {
		this.relativeTime = relativeTime;
	}

	/**
	 * Gets the cluster map mb.
	 *
	 * @return the clusterMapMB
	 */
	public long getClusterMapMB() {
		return clusterMapMB;
	}

	/**
	 * Sets the cluster map mb.
	 *
	 * @param clusterMapMB the clusterMapMB to set
	 */
	public void setClusterMapMB(int clusterMapMB) {
		this.clusterMapMB = clusterMapMB;
	}

	/**
	 * Gets the cluster reduce mb.
	 *
	 * @return the clusterReduceMB
	 */
	public long getClusterReduceMB() {
		return clusterReduceMB;
	}

	/**
	 * Sets the cluster reduce mb.
	 *
	 * @param clusterReduceMB the clusterReduceMB to set
	 */
	public void setClusterReduceMB(int clusterReduceMB) {
		this.clusterReduceMB = clusterReduceMB;
	}

	/**
	 * Gets the job map mb.
	 *
	 * @return the jobMapMB
	 */
	public long getJobMapMB() {
		return jobMapMB;
	}

	/**
	 * Sets the job map mb.
	 *
	 * @param jobMapMB the jobMapMB to set
	 */
	public void setJobMapMB(int jobMapMB) {
		this.jobMapMB = jobMapMB;
	}

	/**
	 * Gets the job reduce mb.
	 *
	 * @return the jobReduceMB
	 */
	public long getJobReduceMB() {
		return jobReduceMB;
	}

	/**
	 * Sets the job reduce mb.
	 *
	 * @param jobReduceMB the jobReduceMB to set
	 */
	public void setJobReduceMB(int jobReduceMB) {
		this.jobReduceMB = jobReduceMB;
	}

}
