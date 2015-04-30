package org.jumbune.common.yarn.utils;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.JobInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskAttemptInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.PhaseDetails;
import org.jumbune.common.beans.PhaseOutput;
import org.jumbune.common.beans.ResourceUsageMetrics;
import org.jumbune.common.beans.TaskOutputDetails;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.yarn.utils.DecoratedJobHistoryParser.AdditionalJobInfo;
import org.jumbune.common.yarn.utils.DecoratedJobHistoryParser.AdditionalTaskInfo;


/**
 * The Class YarnJobStatsUtility is responsible for parsing the .hist file and populating the details of the job into JobOutput.
 */
public class YarnJobStatsUtility {

	
	private static final Logger LOGGER = LogManager.getLogger(YarnJobStatsUtility.class); 
	
	private static final int CONVERSION_FACTOR_MILLISECS_TO_SECS = 1000;
	
	/**
	 * This method parses the .hist files and returns the job stats..
	 *
	 * @param reLocalPath the re local path
	 * @return the job output contains the details of the job.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JobOutput parseAndGetJobStats(String reLocalPath) throws IOException{
		
		
		
		Path histFilePath = new Path(reLocalPath);
		DecoratedJobHistoryParser decoratedJobHistoryParser = new DecoratedJobHistoryParser(FileSystem.getLocal(new Configuration()), histFilePath);
		JobOutput jobOutput = null;
		try {
			JobInfo jobInfo = decoratedJobHistoryParser.parse();
			jobOutput = getJobOutput(jobInfo);
			
							
	} catch (IOException e) {
	LOGGER.error("Error occured while I/O",e.getMessage());
	
	}
	return jobOutput;

		
	}

	/**
	 * @param jobInfo contains the details of the job.
	 * @param additionalJobInfo contains the cpuusage,memory usage and task type details.
	 * @return the job output containing the details of each phase.
	 */
	@SuppressWarnings("deprecation")
	private JobOutput getJobOutput(JobInfo jobInfo) {
		JobOutput jobOutput = new JobOutput();
		jobOutput.setJobID(jobInfo.getJobId().toString());
		jobOutput.setJobName(jobInfo.getJobname());
		jobOutput.setUser(jobInfo.getUsername());
		jobOutput.setOutcome(jobInfo.getJobStatus());
		long referencedZeroTime = jobInfo.getSubmitTime();
		long startTime = jobInfo.getSubmitTime();
		long finishTime = jobInfo.getFinishTime();
		long timeInMilliSec = (finishTime - startTime);
		long totalTimeInSecs = timeInMilliSec / CONVERSION_FACTOR_MILLISECS_TO_SECS;
		jobOutput.setTotalTime(totalTimeInSecs);
		LOGGER.info("Job total time" + jobOutput.getTotalTime());
		jobOutput.setTotalTimeInMilliSec(timeInMilliSec);
		PhaseOutput phaseOutput = new PhaseOutput();
		PhaseDetails mapPhaseDetails = new PhaseDetails();
		PhaseDetails reducePhaseDetails = new PhaseDetails();
		Map<TaskAttemptID, TaskAttemptInfo> tasks = jobInfo.getAllCompletedTaskAttempts();
		List<TaskOutputDetails> mapTaskOutputDetails = new ArrayList<TaskOutputDetails>();
		List<TaskOutputDetails> reduceTaskOutputDetails = new ArrayList<TaskOutputDetails>();
		
		TaskOutputDetails mapTaskDetails = null;
		TaskOutputDetails reduceTaskDetails = null;
		for (Map.Entry<TaskAttemptID, TaskAttemptInfo> task : tasks
				.entrySet()) {
			if(TaskType.MAP.equals(task.getKey().getTaskType())){
				 mapTaskDetails = addMapPhaseDetails(task,referencedZeroTime);
				mapTaskOutputDetails.add(mapTaskDetails);
				}else if(TaskType.REDUCE.equals(task.getKey().getTaskType())){
				 reduceTaskDetails = addReducePhaseDetails(task, referencedZeroTime);
				reduceTaskOutputDetails.add(reduceTaskDetails);
			}
		}
		
		mapPhaseDetails.setTaskOutputDetails(mapTaskOutputDetails);
		long mapDataFlowRate = calculateAvgDataFlow(mapTaskOutputDetails);
		mapPhaseDetails.setAvgDataFlowRate(mapDataFlowRate);
		phaseOutput.setMapDetails(mapPhaseDetails);
		
		reducePhaseDetails.setTaskOutputDetails(reduceTaskOutputDetails);
		long reduceDataFlowRate = calculateAvgDataFlow(reduceTaskOutputDetails);
		reducePhaseDetails.setAvgDataFlowRate(reduceDataFlowRate);
		phaseOutput.setReduceDetails(reducePhaseDetails);
		
		PhaseDetails setupDetails = prepareSetupDetails(jobInfo,tasks);
		phaseOutput.setSetupDetails(setupDetails);

		PhaseDetails cleanupDetails = prepareCleanupDetails(jobInfo,tasks);
		phaseOutput.setCleanupDetails(cleanupDetails);

		jobOutput.setPhaseOutput(phaseOutput);
		
		return jobOutput;
	}

	
	
	/**
	 * This method is responsible for populating the reduce phase details.
	 * @return TaskOutputDetails contains the details of the reduce phase.
	 */
	private TaskOutputDetails addReducePhaseDetails(
			Entry<TaskAttemptID, TaskAttemptInfo> task, long referencedZeroTime) {
		
		
		TaskAttemptInfo taskAttemptInfo = (TaskAttemptInfo) (task.getValue());
		TaskOutputDetails taskOutputDetails = new TaskOutputDetails();
		taskOutputDetails.setTaskStatus(taskAttemptInfo.getTaskStatus());
		taskOutputDetails.setTaskType(taskAttemptInfo.getTaskType().toString());
		taskOutputDetails.setTaskID(taskAttemptInfo.getAttemptId().getTaskID().toString());
		taskOutputDetails.setLocation(taskAttemptInfo.getHostname());
		Counters counters = taskAttemptInfo.getCounters();
		CounterGroup mapReduceTaskCounters = counters.getGroup("org.apache.hadoop.mapreduce.TaskCounter");
		Counter reduceOutputRecords = mapReduceTaskCounters.findCounter("REDUCE_OUTPUT_RECORDS");
		taskOutputDetails.setOutputRecords(reduceOutputRecords.getValue());
		Counter reduceOutputBytes = mapReduceTaskCounters.findCounter("SPILLED_RECORDS");
		taskOutputDetails.setOutputBytes(reduceOutputBytes.getValue());
		long shuffleStartTime = (taskAttemptInfo.getStartTime()- referencedZeroTime)/CONVERSION_FACTOR_MILLISECS_TO_SECS;
		taskOutputDetails.setStartPoint(shuffleStartTime);
		taskOutputDetails.setShuffleStart(shuffleStartTime);
		LOGGER.info("shuffle start time" + taskOutputDetails.getShuffleStart());
		long shuffleEnd = ((taskAttemptInfo.getShuffleFinishTime()-referencedZeroTime)/CONVERSION_FACTOR_MILLISECS_TO_SECS);
		taskOutputDetails.setShuffleEnd(shuffleEnd);
		LOGGER.info("shuffle end time" + taskOutputDetails.getShuffleEnd());
		taskOutputDetails.setSortStart(shuffleEnd);
		long sortEnd = (taskAttemptInfo.getSortFinishTime()-referencedZeroTime)/CONVERSION_FACTOR_MILLISECS_TO_SECS;
		taskOutputDetails.setSortEnd(sortEnd);
		
		LOGGER.info("sort end time" + taskOutputDetails.getSortEnd());
		taskOutputDetails.setReduceStart(sortEnd);
		taskOutputDetails.setReduceEnd((taskAttemptInfo.getFinishTime()-referencedZeroTime)/CONVERSION_FACTOR_MILLISECS_TO_SECS);
		taskOutputDetails.setEndPoint(taskOutputDetails.getReduceEnd());
		LOGGER.info("Reduce end time" + taskOutputDetails.getReduceEnd());
		long dataFlowRate = reduceOutputBytes.getValue() / (taskOutputDetails.getReduceEnd()-shuffleStartTime);
		taskOutputDetails.setDataFlowRate(dataFlowRate);
		
		Counter physicalMemoryBytes = mapReduceTaskCounters.findCounter("PHYSICAL_MEMORY_BYTES");		
		ResourceUsageMetrics rum = new ResourceUsageMetrics();
		rum.setPhysicalMemoryUsage(physicalMemoryBytes.getValue());
		taskOutputDetails.setResourceUsageMetrics(rum);

		return taskOutputDetails;
		

	}
	/**
	 * This method is responsible for populating the setup phase details.
	 * @return TaskOutputDetails contains the details of the set up phase.
	 */
	private PhaseDetails prepareSetupDetails(JobInfo jobInfo,Map<TaskAttemptID, TaskAttemptInfo> tasks){
		PhaseDetails phaseDetails = new PhaseDetails();
		List<TaskOutputDetails> taskOutputDetails = new ArrayList<TaskOutputDetails>();
		TaskOutputDetails tod;
		tod = new TaskOutputDetails();
		tod.setTaskType("SETUP");
		tod.setTaskID("Setup");
		for (Map.Entry<TaskAttemptID, TaskAttemptInfo> task : tasks
				.entrySet()) {
			TaskAttemptInfo taskAttemptInfo = (TaskAttemptInfo) (task.getValue());
			tod.setLocation(taskAttemptInfo.getHostname());
		}
		long startPoint = jobInfo.getSubmitTime();
		tod.setStartPoint(0);
		long endPoint = (jobInfo.getLaunchTime()-startPoint) / CONVERSION_FACTOR_MILLISECS_TO_SECS;
		tod.setEndPoint(endPoint);
		tod.setDataFlowRate(0);
		taskOutputDetails.add(tod);
		phaseDetails.setTaskOutputDetails(taskOutputDetails);
		phaseDetails.setAvgDataFlowRate(0);
		return phaseDetails;
	}
	
	/**
	 * This method is responsible for populating the clean up phase details.
	 * @return TaskOutputDetails contains the details of the clean up phase.
	 */
	private PhaseDetails prepareCleanupDetails(JobInfo jobInfo, Map<TaskAttemptID, TaskAttemptInfo> tasks){
		PhaseDetails phaseDetails = new PhaseDetails();
		List<TaskOutputDetails> cleanupTaskOuptputDetails = new ArrayList<TaskOutputDetails>();
		TaskOutputDetails taskOutputDetails = new TaskOutputDetails();
		taskOutputDetails.setTaskType("CLEANUP");
		taskOutputDetails.setTaskID("Cleanup");
		for (Map.Entry<TaskAttemptID, TaskAttemptInfo> task : tasks
				.entrySet()) {
			TaskAttemptInfo taskAttemptInfo = (TaskAttemptInfo) (task.getValue());
			taskOutputDetails.setLocation(taskAttemptInfo.getHostname());
		}
		long startPoint = getMaxReduceTime(tasks,jobInfo.getSubmitTime());
		taskOutputDetails.setStartPoint(startPoint);
		LOGGER.info("Clean up start time" + taskOutputDetails.getStartPoint());
		long endPoint = (jobInfo.getFinishTime() - jobInfo.getSubmitTime())/CONVERSION_FACTOR_MILLISECS_TO_SECS;
		taskOutputDetails.setEndPoint(endPoint);
		LOGGER.info("Clean up end time" + taskOutputDetails.getEndPoint());
		taskOutputDetails.setDataFlowRate(0);
		cleanupTaskOuptputDetails.add(taskOutputDetails);
		phaseDetails.setTaskOutputDetails(cleanupTaskOuptputDetails);
		phaseDetails.setAvgDataFlowRate(0);
		return phaseDetails;
	}
	

	/**
	 * *  
	 * @return This method returns the maximum time taken by reduce.
	 */
	@SuppressWarnings("deprecation")
	private long getMaxReduceTime(Map<TaskAttemptID, TaskAttemptInfo> tasks, long referencedZeroTime) {
	long maximumReduceTime = 0l;
	for (Map.Entry<TaskAttemptID, TaskAttemptInfo> task : tasks
			.entrySet()) {
		if(!task.getKey().isMap()){
			TaskAttemptInfo taskAttemptInfo = (TaskAttemptInfo) (task.getValue());
			long reduceEnd = (taskAttemptInfo.getFinishTime()-referencedZeroTime)/CONVERSION_FACTOR_MILLISECS_TO_SECS;
			maximumReduceTime = reduceEnd > maximumReduceTime ? reduceEnd : maximumReduceTime;
		}}
	return maximumReduceTime;
	}
	/**
	 * Adds detail for a Map phase.
	 * @param task2 
	 *
	 * @param task2 the tasks
	 * @param referencedZeroTime 
	 * @param referencedZeroTime the start time
	 * @param additionalJobInfo 
	 * @return the phase details
	 */
	private TaskOutputDetails addMapPhaseDetails(Entry<TaskAttemptID, TaskAttemptInfo> task, long referencedZeroTime) {
					
			TaskAttemptInfo taskAttemptInfo = (TaskAttemptInfo) (task.getValue());
			TaskOutputDetails taskOutputDetails = new TaskOutputDetails();
			taskOutputDetails.setTaskStatus(taskAttemptInfo.getTaskStatus());
			taskOutputDetails.setTaskType(taskAttemptInfo.getTaskType().toString());
			taskOutputDetails.setTaskID(taskAttemptInfo.getAttemptId().getTaskID().toString());
			long startPoint = (taskAttemptInfo.getStartTime() - referencedZeroTime) / CONVERSION_FACTOR_MILLISECS_TO_SECS;
			taskOutputDetails.setStartPoint(startPoint);
			LOGGER.info("Map Start time" + taskOutputDetails.getStartPoint());
			long endPoint = (taskAttemptInfo.getMapFinishTime() - referencedZeroTime) / CONVERSION_FACTOR_MILLISECS_TO_SECS;
			taskOutputDetails.setEndPoint(endPoint);
			LOGGER.info("Map End time" + taskOutputDetails.getEndPoint());
			taskOutputDetails.setTimeTaken(endPoint - startPoint);
			LOGGER.info("Map time" + taskOutputDetails.getTimeTaken());
			taskOutputDetails.setLocation(taskAttemptInfo.getHostname());
			Counters counters = taskAttemptInfo.getCounters();
			CounterGroup fileSystemCounters = counters.getGroup("org.apache.hadoop.mapreduce.FileSystemCounter");
			Counter inputBytes = fileSystemCounters.findCounter("HDFS_BYTES_READ");
			long dataFlowRate = inputBytes.getValue() / (endPoint - startPoint);
			taskOutputDetails.setDataFlowRate(dataFlowRate);
			CounterGroup mapReduceTaskCounters = counters.getGroup("org.apache.hadoop.mapreduce.TaskCounter");
			Counter mapOutputRecords = mapReduceTaskCounters.findCounter("MAP_OUTPUT_RECORDS");
			Counter physicalMemoryBytes = mapReduceTaskCounters.findCounter("PHYSICAL_MEMORY_BYTES");
			ResourceUsageMetrics rum = new ResourceUsageMetrics();
			rum.setPhysicalMemoryUsage(physicalMemoryBytes.getValue());
			taskOutputDetails.setResourceUsageMetrics(rum);
			taskOutputDetails.setOutputRecords(mapOutputRecords.getValue());
			Counter mapOutputBytes = mapReduceTaskCounters.findCounter("MAP_OUTPUT_BYTES");
			taskOutputDetails.setOutputBytes(mapOutputBytes.getValue());
	 		return taskOutputDetails;
	}

	/**
	 * Calulates average data flow for a MapReduce phase.
	 *
	 * @param taskOutputDetails the task output details
	 * @return the long
	 */
	private long calculateAvgDataFlow(List<TaskOutputDetails> taskOutputDetails) {
		long totalDataFlow = 0;
		for (TaskOutputDetails tod : taskOutputDetails) {
			totalDataFlow += tod.getDataFlowRate();
		}if(taskOutputDetails.size()>0){
			return (totalDataFlow / taskOutputDetails.size()) / CONVERSION_FACTOR_MILLISECS_TO_SECS;
		}
		LOGGER.warn("Task output details list has no elements in it. Setting average data flow to [0]");
		return 0;
	}

}
