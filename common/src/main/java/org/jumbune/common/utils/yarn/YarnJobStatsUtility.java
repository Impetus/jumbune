package org.jumbune.common.utils.yarn;



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
import org.jumbune.common.utils.yarn.DecoratedJobHistoryParser.AdditionalJobInfo;
import org.jumbune.common.utils.yarn.DecoratedJobHistoryParser.AdditionalTaskInfo;


/**
 * The Class YarnJobStatsUtility is responsible for parsing the .hist file and populating the details of the job into JobOutput.
 */
public class YarnJobStatsUtility {

	
	private static final Logger LOGGER = LogManager.getLogger(YarnJobStatsUtility.class); 
	
	private static final int CONVERSION_FACTOR_MILLISECS_TO_SECS = 1000;
	
	/** The Constant DELAY_INTERVAL. */
	private static final int DELAY_INTERVAL = 2;
	
	/** The Constant NUM_OF_INTERVALS. */
	private static final int NUM_OF_INTERVALS = 20;

	private static final int DEFAULT_INTERVAL = 1;
	
	/** The interval period. */
	private long intervalPeriod = Constants.FOUR;	
	
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
			AdditionalJobInfo additionalJobInfo = decoratedJobHistoryParser.getAdditionalJobInfo();
			 jobOutput = getJobOutput(jobInfo,additionalJobInfo);
			
							
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
	private JobOutput getJobOutput(JobInfo jobInfo, AdditionalJobInfo additionalJobInfo) {
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
		intervalPeriod = totalTimeInSecs < NUM_OF_INTERVALS ? DEFAULT_INTERVAL :totalTimeInSecs / NUM_OF_INTERVALS ;
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
			if(task.getKey().isMap()){
				 mapTaskDetails = addMapPhaseDetails(task,referencedZeroTime);
				mapTaskOutputDetails.add(mapTaskDetails);
				}else if(!task.getKey().isMap()){
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
		
		PhaseDetails setupDetails = prepareSetupDetails(jobInfo);
		phaseOutput.setSetupDetails(setupDetails);

		PhaseDetails cleanupDetails = prepareCleanupDetails(jobInfo,tasks);
		phaseOutput.setCleanupDetails(cleanupDetails);

		Map<TaskAttemptID, AdditionalTaskInfo> additionalJobInfoMap = additionalJobInfo.getAdditionalTasksMap();
		AdditionalTaskInfo atInfo;
		
		float[] totalMapCpuUsage = null;
		float[] totalReduceCpuUsage = null;
		int averagingNumberForMap = 0;
		int averagingNumberForReduce = 0;
		for(Map.Entry<TaskAttemptID, AdditionalTaskInfo> additionalJobInfoMapEntry: additionalJobInfoMap.entrySet()){
			atInfo = (AdditionalTaskInfo)additionalJobInfoMapEntry.getValue();
			if(atInfo.getTaskType().equals(TaskType.MAP)){
				totalMapCpuUsage = CpuArrays.addAll(totalMapCpuUsage, atInfo.getCpuUages(), 10);
				averagingNumberForMap++;
			}else if(atInfo.getTaskType().equals(TaskType.REDUCE)){
				totalReduceCpuUsage = CpuArrays.addAll(totalReduceCpuUsage, atInfo.getCpuUages(), 10);
				averagingNumberForReduce++;
			}
		}		
		
		float[] averagedMapCpuUsage;
		float[] averagedReduceCpuUsage;
		averagedMapCpuUsage = CpuArrays.averageOut(totalMapCpuUsage, averagingNumberForMap);
		averagedReduceCpuUsage = CpuArrays.averageOut(totalReduceCpuUsage, averagingNumberForReduce);
		
		LOGGER.info("AvgMapCPUArray:"+Arrays.toString(averagedMapCpuUsage));
		LOGGER.info("AvgReduceCPUArray:"+Arrays.toString(averagedReduceCpuUsage));
		
		Map<String, Float> mapTaskPhysicalMemoryMap = new HashMap<String, Float>();
		for (TaskOutputDetails mapTaskOutputDetail: mapTaskOutputDetails) {
			LOGGER.info("mapTaskPhysicalMemoryMap:"+(mapTaskOutputDetail.getTaskID()+" has physical memory(B):"+mapTaskOutputDetail.getResourceUsageMetrics().getPhysicalMemoryUsage()));
			mapTaskPhysicalMemoryMap.put(mapTaskOutputDetail.getTaskID(), mapTaskOutputDetail.getResourceUsageMetrics().getPhysicalMemoryUsage());
		}
	
		Map<String, Float> reduceTaskPhysicalMemoryMap = new HashMap<String, Float>();
		for (TaskOutputDetails reduceTaskOutputDetail: reduceTaskOutputDetails) {
			LOGGER.info("reduceTaskPhysicalMemoryMap123:"+(reduceTaskOutputDetail.getTaskID()+" has physical memory(B):"+reduceTaskOutputDetail.getResourceUsageMetrics().getPhysicalMemoryUsage()));
			reduceTaskPhysicalMemoryMap.put(reduceTaskOutputDetail.getTaskID(), reduceTaskOutputDetail.getResourceUsageMetrics().getPhysicalMemoryUsage());
		}

		float[] totalMapMemoryUsage = null;
		float [] totalReduceMemoryUsage = null;
		String atTaskId;
		for(Map.Entry<TaskAttemptID, AdditionalTaskInfo> additionalJobInfoMapEntry: additionalJobInfoMap.entrySet()){
			atTaskId = additionalJobInfoMapEntry.getKey().getTaskID().toString();		
			atInfo = (AdditionalTaskInfo)additionalJobInfoMapEntry.getValue();
			if(atInfo.getTaskType().equals(TaskType.MAP)){				
				totalMapMemoryUsage = MemArrays.addAll(totalMapMemoryUsage, atInfo.getPhysicalMemInKBs(), mapTaskPhysicalMemoryMap.get(atTaskId));
			}else if(atInfo.getTaskType().equals(TaskType.REDUCE)){
				totalReduceMemoryUsage = MemArrays.addAll(totalReduceMemoryUsage, atInfo.getPhysicalMemInKBs(), reduceTaskPhysicalMemoryMap.get(atTaskId));
			}
		}
		
		float[] averagedMapMemoryUsage;
		float[] averagedReduceMemoryUsage;		
		averagedMapMemoryUsage = MemArrays.averageOut(totalMapMemoryUsage, averagingNumberForMap);
		averagedReduceMemoryUsage = MemArrays.averageOut(totalReduceMemoryUsage, averagingNumberForReduce);
		
		LOGGER.info("AvgMapMemoryArray:"+Arrays.toString(averagedMapMemoryUsage));
		LOGGER.info("AvgReduceMemoryArray:"+Arrays.toString(averagedReduceMemoryUsage));
		
		
		long[] minimumAndMaximumMapPoints = getMinimumAndMaximum(mapPhaseDetails);
		long[] minimumAndMaximumReducePoints = getMinimumAndMaximumForReduce(reducePhaseDetails);
		
		int differenceInMapPoints = (int) (minimumAndMaximumMapPoints[1]-minimumAndMaximumMapPoints[0]);
		int differenceInReducePoints = (int) (minimumAndMaximumReducePoints[1]-minimumAndMaximumReducePoints[0]);
		
		float mapInterval = (float)differenceInMapPoints/averagedMapCpuUsage.length;
		float reduceInterval = (float)differenceInReducePoints/averagedReduceCpuUsage.length;

		Map<Long, Float> avgCpuUsage = new LinkedHashMap<Long, Float>();
		Map<Long, Float> avgMemUsage = new LinkedHashMap<Long, Float>();

		long point = 0;
		for(int i=0;i<averagedMapCpuUsage.length;i++) {
			if(i==0){
				point = minimumAndMaximumMapPoints[0];
			}else{
				point = (long) (minimumAndMaximumMapPoints[0] + (mapInterval*i));
			}
			avgCpuUsage.put(point,averagedMapCpuUsage[i]);	 			
		}
		
		for(int i=0;i<averagedMapMemoryUsage.length;i++){
			if(i==0){
				point = minimumAndMaximumMapPoints[0];
			}else{
				point = (long) (minimumAndMaximumMapPoints[0] + (mapInterval*i));
			}
			avgMemUsage.put(point,averagedMapMemoryUsage[i]);	 			
		}

		for(int i=0;i<averagedReduceCpuUsage.length;i++){
			if(i==0){
				point = minimumAndMaximumReducePoints[0];
			}else{
				point = (long) (minimumAndMaximumReducePoints[0] + (reduceInterval*i));
			}
			avgCpuUsage.put(point,averagedReduceCpuUsage[i]);	 			
		}
		
		for(int i=0;i<averagedMapMemoryUsage.length;i++){
			if(i==0){
				point = minimumAndMaximumReducePoints[0];
			}else{
				point = (long) (minimumAndMaximumReducePoints[0] + (reduceInterval*i));
			}
			avgMemUsage.put(point,averagedReduceMemoryUsage[i]);	 			
		}
		
		for(Map.Entry<Long, Float>avgMemEntry: avgMemUsage.entrySet()){
			LOGGER.info("Avg Mem Entry:"+avgMemEntry.getKey()+", "+avgMemEntry.getValue());
		}
		
		jobOutput.setCpuUsage(avgCpuUsage);
		jobOutput.setMemUsage(avgMemUsage);

		jobOutput.setPhaseOutput(phaseOutput);
		
		return jobOutput;
	}

	/**
	 * This method returns the maximum and minimum points for plotting the map phase.
	 * 
	 * 	 */
	private long[] getMinimumAndMaximum(PhaseDetails phaseDetails){
		long[] minimumAndMaximum = new long[2];
		long minimumStartPoint = 0;
		long maximumEndPoint = 0;
		for(TaskOutputDetails taskOutputDetails: phaseDetails.getTaskOutputDetails()){
			if(minimumStartPoint == 0){
				minimumStartPoint = taskOutputDetails.getStartPoint();
			}
			minimumStartPoint = minimumStartPoint < taskOutputDetails.getStartPoint()? minimumStartPoint:taskOutputDetails.getStartPoint();
			maximumEndPoint = maximumEndPoint > taskOutputDetails.getEndPoint()? maximumEndPoint:taskOutputDetails.getEndPoint();
		}
		minimumAndMaximum[0] = minimumStartPoint;
		minimumAndMaximum[1] = maximumEndPoint;
		LOGGER.info("MinimumAndMaximum Array: "+Arrays.toString(minimumAndMaximum));
		return minimumAndMaximum;
	}
	
	/**
	 * This method returns the maximum and minimum points for plotting the reduce phase.
	 * 
	 * 	 */
	private long[] getMinimumAndMaximumForReduce(PhaseDetails phaseDetails){
		long[] minimumAndMaximum = new long[2];
		long minimumStartPoint = 0;
		long maximumEndPoint = 0;
		for(TaskOutputDetails taskOutputDetails: phaseDetails.getTaskOutputDetails()){
			if(minimumStartPoint == 0){
				minimumStartPoint = taskOutputDetails.getShuffleStart();
			}
			minimumStartPoint = minimumStartPoint < taskOutputDetails.getShuffleStart()? minimumStartPoint:taskOutputDetails.getShuffleStart();
			maximumEndPoint = maximumEndPoint > taskOutputDetails.getReduceEnd()? maximumEndPoint:taskOutputDetails.getReduceEnd();
		}
		minimumAndMaximum[0] = minimumStartPoint;
		minimumAndMaximum[1] = maximumEndPoint;
		LOGGER.info("MinimumAndMaximum Array: "+Arrays.toString(minimumAndMaximum));
		return minimumAndMaximum;
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
	private PhaseDetails prepareSetupDetails(JobInfo jobInfo){
		PhaseDetails phaseDetails = new PhaseDetails();
		List<TaskOutputDetails> taskOutputDetails = new ArrayList<TaskOutputDetails>();
		TaskOutputDetails tod;
		tod = new TaskOutputDetails();
		tod.setTaskType("SETUP");
		tod.setTaskID("Setup");
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
		}
		return (totalDataFlow / taskOutputDetails.size()) / CONVERSION_FACTOR_MILLISECS_TO_SECS;
		
	}

	private static class CpuArrays{
		
		public static float[] addAll(float[] primaryArray, int[] arrayToAdd, int millisToPercentage){
			int arrayToAddLength = arrayToAdd.length;
			if(primaryArray==null || primaryArray.length==0){
				primaryArray = new float[arrayToAdd.length];
				Arrays.fill(primaryArray, 0);
			}
			for(int i=0;i<arrayToAddLength;i++){
				primaryArray[i]+=arrayToAdd[i]/millisToPercentage;
			}
			return primaryArray;
		}
		
		public static float[] averageOut(float[] array, int averagingNumber){
			float[] floatArray = new float[array.length];
			int primaryArrayLength = array.length;
			for(int i=0;i<primaryArrayLength;i++){
				floatArray[i] = array[i]/averagingNumber;
			}
			return floatArray;			
		}
		
		
	}
	
	private static class MemArrays{
		
		public static float[] addAll(float[] primaryArray, int[] arrayToAdd, float physicalMemInBytes){
			int arrayToAddLength = arrayToAdd.length;
			if(primaryArray==null || primaryArray.length==0){
				primaryArray = new float[arrayToAdd.length];
				Arrays.fill(primaryArray, 0);
			}
			for(int i=0;i<arrayToAddLength;i++){
				primaryArray[i]+=((arrayToAdd[i]/(physicalMemInBytes/1024))*100);
			}
			return primaryArray;
		}	
		
		public static float[] averageOut(float[] array, int averagingNumber){
			float[] floatArray = new float[array.length];
			int primaryArrayLength = array.length;
			for(int i=0;i<primaryArrayLength;i++){
				floatArray[i] = array[i]/averagingNumber;
			}
			return floatArray;			
		}
	}
}
