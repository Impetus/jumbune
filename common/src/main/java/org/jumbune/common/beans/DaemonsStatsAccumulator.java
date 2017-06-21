package com.impetus.jumbune.common.beans;

import java.util.List;


public class DaemonsStatsAccumulator {

	public static enum OPTS {
		HADOOP_TASKTRACKER_OPTS, HADOOP_DATANODE_OPTS, HADOOP_NAMENODE_OPTS, HADOOP_JOBTRACKER_OPTS, HADOOP_RESOURCEMANAGER_OPTS, HADOOP_NODEMANAGER_OPTS;
	}

	private TaskTrackersAccumulator taskTrackersAccumulator;
	private JobTrackerStats jobTrackerStats;

	public DaemonsStatsAccumulator(List<String> taskTrackers, String jobTracker) {
		
		taskTrackersAccumulator = new TaskTrackersAccumulator(taskTrackers);
		jobTrackerStats = new JobTrackerStats(jobTracker, 0);
		
	}

	public DaemonsStatsAccumulator(String jobTracker, int jobTrackerPort, List<String> taskTrackers, int taskTrackersPort) {

		taskTrackersAccumulator = new TaskTrackersAccumulator(taskTrackers, taskTrackersPort);
		jobTrackerStats = new JobTrackerStats(jobTracker, jobTrackerPort);

	}

	
	public JobTrackerStats getJobTrackerStats() {
		return jobTrackerStats;
	}

	public void setJobTrackerStats(JobTrackerStats jobTrackerStats) {
		this.jobTrackerStats = jobTrackerStats;
	}

	
	/**
	 * @return the taskTrackersAccumulator
	 */
	public TaskTrackersAccumulator getTaskTrackersAccumulator() {
		return taskTrackersAccumulator;
	}

	/**
	 * @param taskTrackersAccumulator the taskTrackersAccumulator to set
	 */
	public void setTaskTrackersAccumulator(TaskTrackersAccumulator taskTrackersAccumulator) {
		this.taskTrackersAccumulator = taskTrackersAccumulator;
	}

	@Override
	public String toString() {
		return "DaemonsStatsAccumulator [taskTrackersAccumulator=" + taskTrackersAccumulator + ", jobTrackerStats="
				+ jobTrackerStats + "]";
	}

	

	

}
