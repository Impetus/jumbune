package org.jumbune.clusterprofiling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobQueueInfo;
import org.apache.hadoop.mapred.JobStatus;
import org.jumbune.clusterprofiling.beans.LiveCapacityStats;
import org.jumbune.clusterprofiling.beans.NonYarnQueueStats;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.beans.RackAwareStats;
import org.jumbune.common.beans.EffCapUtilizationStats;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.MRTaskType;
import org.jumbune.common.beans.ResourceUsageMetrics;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Location;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.beans.profiling.TaskOutputDetails;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.HadoopLogParser;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.utils.beans.NativeJobId;
import org.jumbune.utils.beans.NonYarnJobId;

/**
 * The Class ClusterProfilingHelper contains all the helper methods required for non-yarn cluster profiling.
 */
public class NonYarnClusterProfilingHelper {
	
	/**
	 * Gets the queue stats.
	 *
	 * @param jumbuneRequest the jumbune request
	 * @param jobClient the job client
	 * @return the queue stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<QueueStats> getQueueStats(Cluster cluster, JobClient jobClient) throws IOException {
	
		JobQueueInfo[]	jobQueueInfos = jobClient.getQueues();
		
		NonYarnQueueStats nonYarnQueueStats = null ;
		List<QueueStats> queueStatList = new ArrayList<QueueStats>();
			for (JobQueueInfo jobQueueInfo : jobQueueInfos) {
				nonYarnQueueStats = new NonYarnQueueStats();
				nonYarnQueueStats.setQueueName(jobQueueInfo.getQueueName());
				nonYarnQueueStats.setQueueState(jobQueueInfo.getQueueState());
				nonYarnQueueStats = getSchedulingInfo(jobQueueInfo.getSchedulingInfo(),nonYarnQueueStats);
				queueStatList.add(nonYarnQueueStats);
		}
			return queueStatList;
	}
	
	
	
	/**
	 * Gets the scheduling info.
	 *
	 * @param schedulingInfo the scheduling info
	 * @param nonYarnQueueStats the non yarn queue stats
	 * @return the scheduling info
	 */
	public NonYarnQueueStats getSchedulingInfo(String schedulingInfo, NonYarnQueueStats nonYarnQueueStats) {
			String[] schedulingInfoArray = schedulingInfo.split("\n");
			Map<String ,Map<String,String>> schedInfoMap = new HashMap<String, Map<String,String>>();
			Map<String,String> queueInfoMap  = null ;
			String parentKey = null;
			for (String schedInfo : schedulingInfoArray) {
		       if(schedInfo.contains("---")) continue;
				
					if (schedInfo.contains(":")) {
					String[] splittedString = schedInfo.split(":");
					queueInfoMap.put(splittedString[0].trim(), splittedString[1].trim());
					schedInfoMap.put(parentKey, queueInfoMap);
					}else{
					parentKey = schedInfo.trim();
					queueInfoMap  = new HashMap<String, String>();
					
			}
			}
			float percCurrentUsage = 0 , mapUsage = 0 , mapTotalCapacity=0 ,reduceUsage =0 ,reduceTotalCapacity =0 ;
			for (Map.Entry<String, Map<String, String>> schedInfoEntry : schedInfoMap.entrySet()) {
				Map<String,String> queueMap= schedInfoEntry.getValue();
				String queueInfoValue = null;
					if(schedInfoEntry.getKey().equalsIgnoreCase("Queue configuration")){
				
						if ((queueInfoValue = queueMap.get("Capacity Percentage"))!=null) {
							float capacity  = Float.parseFloat(queueInfoValue.split("\\%")[0]);
							nonYarnQueueStats.setCapacity(capacity);
							
						}
						
						if ((queueInfoValue = queueMap.get("User Limit"))!=null) {
							float maxCapacity = Float.parseFloat(queueInfoValue.split("\\%")[0]);
							nonYarnQueueStats.setMaximumCapacity(maxCapacity);
						}
						}
					
				
					if (schedInfoEntry.getKey().equalsIgnoreCase("Map tasks")) {
						if((queueInfoValue = queueMap.get("Used capacity"))!=null){
							
							 mapUsage = Float.parseFloat(queueInfoValue.split("\\(")[0]);
					}
						if((queueInfoValue = queueMap.get("Capacity"))!=null){
							 mapTotalCapacity =  Float.parseFloat(queueInfoValue.split("slots")[0]);
							 
						}
					}
						
					if (schedInfoEntry.getKey().equalsIgnoreCase("Reduce tasks")) {
						
						if((queueInfoValue = queueMap.get("Used capacity"))!=null){
							
						 reduceUsage = Float.parseFloat(queueInfoValue.split("\\(")[0]);
					}
						if((queueInfoValue = queueMap.get("Capacity"))!=null){
							 reduceTotalCapacity =  Float.parseFloat(queueInfoValue.split("slots")[0]);
							 
						}
						
						
					}
					
					if(schedInfoEntry.getKey().equalsIgnoreCase("Job info")){
						
						if((queueInfoValue = queueMap.get("Number of Waiting Jobs"))!=null){
							 nonYarnQueueStats.setWaitingJobs(Integer.parseInt(queueInfoValue.split(":")[1]));
						}
						
					}
					
			}
			percCurrentUsage = ((mapUsage*mapTotalCapacity)+(reduceUsage*reduceTotalCapacity))/(mapTotalCapacity+reduceTotalCapacity);
			nonYarnQueueStats.setCurrentCapacity(percCurrentUsage*100);
			return nonYarnQueueStats;
		}
	
	
	/**
	 * Gets the eff utilization stats.
	 *
	 * @param jumbuneRequest the jumbune request
	 * @param jobClient the client
	 * @return the eff utilization stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<EffCapUtilizationStats> getEffUtilizationStats(Cluster cluster, JobClient jobClient) throws IOException{
		
		EffCapUtilizationStats effCapUtilizationStats = null ;
		List<EffCapUtilizationStats> effCapUtilizationStatsList = new ArrayList<EffCapUtilizationStats>();
		String userName = cluster.getHadoopUsers().getFsUser();
		String mapChildJavaOpts = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapred.map.child.java.opts");
		String destinationRelativePathOnLocal = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + cluster.getClusterName();
		String reduceChildJavaOpts = RemotingUtil.parseConfiguration(destinationRelativePathOnLocal,"mapred.reduce.child.java.opts");
		long mapChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(mapChildJavaOpts);
		long reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(reduceChildJavaOpts);
		if(mapChildJavaOptsinMB==0){
			String childJavaOpts = RemotingUtil.parseConfiguration(destinationRelativePathOnLocal,"mapred.child.java.opts");
			mapChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(childJavaOpts);
		}
		if(reduceChildJavaOptsinMB==0){
			String childJavaOpts = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapred.child.java.opts");
			reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(childJavaOpts);
		}
		
		List<NativeJobId> jobIDs = getRunningJobsForUser(userName, jobClient);
		for (NativeJobId nativeJobId : jobIDs) {
			effCapUtilizationStats = new EffCapUtilizationStats();
			effCapUtilizationStats.setJobId(nativeJobId.toString());
			effCapUtilizationStats.setAllocatedMapMemory(mapChildJavaOptsinMB);
			effCapUtilizationStats.setAllocatedReduceMemory(reduceChildJavaOptsinMB);
			JobOutput jobOutput = getJobStats(cluster, nativeJobId.toString());
			effCapUtilizationStats.setUsedMaxMapMemory(getUsedMaxMapMemory(jobOutput));
			effCapUtilizationStats.setUsedMaxReduceMemory(getUsedMaxReduceMemory(jobOutput));
			effCapUtilizationStatsList.add(effCapUtilizationStats);
		}
		return effCapUtilizationStatsList;
	}

	/**
	 * Gets the used max map memory.
	 *
	 * @param jobOutput the job output
	 * @return the used max map memory
	 */
	private long getUsedMaxMapMemory(JobOutput jobOutput) {
		
		List<TaskOutputDetails> taskOutputDetailsList = jobOutput.getPhaseOutput().getMapDetails().getTaskOutputDetails();
		List<Long> mapMemoryValues = new ArrayList<Long>();
		long usedMapMem = 0 ;
		for (TaskOutputDetails taskOutputDetails : taskOutputDetailsList) {
			ResourceUsageMetrics resourceUsageMetrics = taskOutputDetails.getResourceUsageMetrics();
			 usedMapMem = (long) (resourceUsageMetrics.getPhysicalMemoryUsage()/(1024*1024));
			 mapMemoryValues.add(usedMapMem);
		}
		usedMapMem =  getMaxMemory(mapMemoryValues);
		return usedMapMem;
	}

	/**
	 * Gets the used max reduce memory.
	 *
	 * @param jobOutput the job output
	 * @return the used max reduce memory
	 */
	private long getUsedMaxReduceMemory(JobOutput jobOutput) {
		
		List<TaskOutputDetails> taskOutputDetailsList = jobOutput.getPhaseOutput().getReduceDetails().getTaskOutputDetails();
		List<Long> reduceMemoryValues = new ArrayList<Long>();
		long usedRedMem = 0 ;
		for (TaskOutputDetails taskOutputDetails : taskOutputDetailsList) {
			ResourceUsageMetrics resourceUsageMetrics = taskOutputDetails.getResourceUsageMetrics();
			usedRedMem = (long) (resourceUsageMetrics.getPhysicalMemoryUsage()/(1024*1024));
			reduceMemoryValues.add(usedRedMem);
		}
		usedRedMem = getMaxMemory(reduceMemoryValues);
		return usedRedMem;
	}
	
	/**
	 * Gets the max memory.
	 *
	 * @param memoryValueList the rucmem
	 * @return the max memory
	 */
	private static long getMaxMemory(List<Long> memoryValueList) {
		long maxValue = 0 ;
		for (Long memoryValue : memoryValueList) {
			if(memoryValue>maxValue){
			maxValue = memoryValue;
		}
	}
		return maxValue;
	}

	/**
	 * Gets the running jobs for user i.e gives a list of jobIDs that ran for a particular user.
	 *
	 * @param userName the user name
	 * @param jobClient the client
	 * @return the running jobs for user
	 * @throws IOException the IO exception
	 */
	private static List<NativeJobId> getRunningJobsForUser(String userName,JobClient jobClient) throws IOException {
		
		
		 JobStatus[] jobStatuses = jobClient.getAllJobs();
		
		List<NativeJobId> jobIDs = new LinkedList<NativeJobId>();
		for (JobStatus jobStatus : jobStatuses) {
			if (userName.equals(jobStatus.getUsername())) {
				jobIDs.add(new NonYarnJobId(jobStatus.getJobID()));
			}
		}
		return jobIDs;
		
	}
	
	/**
	 * Gets the rack stats.
	 *
	 * @param jumbuneRequest the job config
	 * @param jobClient the client
	 * @return the rack stats
	 * @throws IOException the IO exception
	 */
	public RackAwareStats getRackStats(Cluster cluster, JobClient jobClient) throws IOException{
		RackAwareStats rackAwareStats = new RackAwareStats() ;
		int otherRack = 0 , nodeLocal = 0 , rackLocal = 0 ,  otherCounterRack = 0 , nodeCounterLocal = 0 , rackCounterLocal = 0;
		HadoopLogParser hadoopLogParser = new HadoopLogParser();
		String userName = cluster.getHadoopUsers().getFsUser();
		List<NativeJobId> jobIDs = getRunningJobsForUser(userName, jobClient);
		String location = null ;
		for (NativeJobId nativeJobId : jobIDs) {
			JobOutput jobOutput = hadoopLogParser.getJobDetails(cluster, nativeJobId.toString());	
			List<TaskOutputDetails> taskOutputDetailsList = jobOutput.getPhaseOutput().getMapDetails().getTaskOutputDetails();
			for (TaskOutputDetails taskOutputDetails : taskOutputDetailsList) {
				location = taskOutputDetails.getLocation();
				List<Location> locations = taskOutputDetails.getPreferredLocations();
				for (Location location2 : locations) {
					List<String> preferredLocation = location2.getLayers();
						if(!location.split("/")[1].equalsIgnoreCase(preferredLocation.get(0))){
						 otherRack ++; 
					}else if(location.split("/")[2].equalsIgnoreCase(preferredLocation.get(1)) && preferredLocation.size()<=2){
						nodeLocal ++ ;
					
				}else if(!location.split("/")[2].equalsIgnoreCase(preferredLocation.get(1))){
					rackLocal ++ ;
				}
					
				}
			}
		}
		
		if (otherRack>0 && rackLocal>0  && nodeLocal>0) {
			otherCounterRack++;
			rackAwareStats.setOtherLocalJob(otherCounterRack);
		}else if(rackLocal>0 && nodeLocal>0){
			rackCounterLocal++;
			rackAwareStats.setRackLocalJob(rackCounterLocal);
		}else if(!(rackLocal>0) && nodeLocal>0){
			nodeCounterLocal++;
			rackAwareStats.setDataLocalJob(nodeCounterLocal);
		}
		return rackAwareStats;
		
	}
	
	/**
	 * Gets the job stats.
	 *
	 * @param jumbuneRequest the jumbune request
	 * @param jobID the job id
	 * @return the job stats
	 * @throws IOException the IO exception
	 */
	public JobOutput getJobStats(Cluster cluster, String jobID) throws IOException{
		JobOutput jobOutput = null ;
		HadoopLogParser hadoopLogParser = new HadoopLogParser();
		jobOutput = hadoopLogParser.getJobDetails(cluster, jobID);
		return jobOutput;
		
	}

	/**
	 * Gets the average time a job waits in a particular queue.
	 *
	 * @param jobClient the job client
	 * @param queueName the queue name
	 * @param cluster the jumbune request
	 * @return the average time
	 * @throws IOException the IO exception
	 */
	public Long getAverageTime(JobClient jobClient, String queueName, Cluster cluster) throws IOException {
		long waitingTime = 0 ;
		long count = 0 ;
		JobStatus[] jobStatus = jobClient.getJobsFromQueue(queueName);
		
		for (JobStatus jobStatus2 : jobStatus) {
			HadoopLogParser hadoopLogParser = new HadoopLogParser();
			JobOutput jobOutput = hadoopLogParser.getJobDetails(cluster, jobStatus2.getJobID().toString());
			
			long submitTime = jobOutput.getSubmitTime();
			long launchTime = jobOutput.getLaunchTime();
			waitingTime += (launchTime-submitTime)/1000 ;
			count++;
			}
		
			
		return waitingTime/count;
	}
	
	/** 
	 * Return total cluster slots
	 * @param type
	 * @param clusterStatus
	 * @return
	 */
	private int getClusterTotalSlots(MRTaskType type, ClusterStatus clusterStatus) {
		return (type == MRTaskType.MAP ? clusterStatus.getMaxMapTasks() : clusterStatus.getMaxReduceTasks());
	}
	
	/**
	 * Return map child java opts
	 * @param cluster
	 * @return
	 */
	private long mapChildJavaOpts(Cluster cluster){
		String mapChildJavaOpts = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapred.map.child.java.opts");
		long mapChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(mapChildJavaOpts);
		if(mapChildJavaOptsinMB == 0){
			String destinationRelativePathOnLocal = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + cluster.getClusterName();
			String childJavaOpts = RemotingUtil.parseConfiguration(destinationRelativePathOnLocal,"mapred.child.java.opts");
			mapChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(childJavaOpts);
		}
		return mapChildJavaOptsinMB;
	}
	
	/**
	 * Return reduce child java opts
	 * @param cluster
	 * @return
	 */
	private long reduceChildJavaOpts(Cluster cluster) {
		String destinationRelativePathOnLocal = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + cluster.getClusterName();
		String reduceChildJavaOpts = RemotingUtil.parseConfiguration(destinationRelativePathOnLocal,"mapred.reduce.child.java.opts");
		long reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(reduceChildJavaOpts);
		if(reduceChildJavaOptsinMB == 0){
			String childJavaOpts = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapred.child.java.opts");
			reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(childJavaOpts);
		}
		return reduceChildJavaOptsinMB;
	}
	
	/**
	 * Computes task wise for cluster
	 * @param type
	 * @param clusterStatus
	 * @return
	 */
	private int computeTaskWiseForCluster(MRTaskType type, ClusterStatus clusterStatus){

		int MaxClusterSlots = getClusterTotalSlots(type, clusterStatus);
		int currentlyRunningClusterTasks = getClusterCurrentRunningTasks(type, clusterStatus);
		int noOfAvailableTasksForCluster = MaxClusterSlots - currentlyRunningClusterTasks;
		return noOfAvailableTasksForCluster;
	}
	
	/**
	 * Get cluster current running tasks
	 * @param type
	 * @param clusterStatus
	 * @return
	 */
	private int getClusterCurrentRunningTasks(MRTaskType type, ClusterStatus clusterStatus) {
		return (type == MRTaskType.MAP ? clusterStatus.getMapTasks() : clusterStatus.getReduceTasks());
	}

	
	/**
	 * Slots Calculator
	 * @param cluster
	 * @return
	 * @throws IOException
	 */
	public LiveCapacityStats computeTaskSlots(Cluster cluster) throws IOException {
		JobClient client = RemotingUtil.getJobClient(cluster);
		ClusterStatus clusterStatus = client.getClusterStatus();

		int totalWorkerNodes = cluster.getWorkers().getHosts().size();

		LiveCapacityStats mapCapacityStats = new LiveCapacityStats();
		LiveCapacityStats reduceCapacityStats = new LiveCapacityStats();

		int availableMapperSlotsInCluster = computeTaskWiseForCluster(MRTaskType.MAP, clusterStatus);
		int availableReducerSlotsInCluster = computeTaskWiseForCluster(MRTaskType.REDUCE, clusterStatus);

		int mapTasks, reduceTasks;

		List<LiveCapacityStats> list = new ArrayList<LiveCapacityStats>(2); mapTasks = availableMapperSlotsInCluster/totalWorkerNodes;
		mapCapacityStats.setCapacity(String.valueOf(mapTasks));
		mapCapacityStats.setMessage("Approx. mapper slots are available for the launch of memory size "+ mapChildJavaOpts(cluster) + " MB");
		list.add(mapCapacityStats);

		reduceTasks = availableReducerSlotsInCluster/totalWorkerNodes;
		reduceCapacityStats.setCapacity(String.valueOf(reduceTasks));
		reduceCapacityStats.setMessage("Approx. reducer slots are available for the launch of memory size " + reduceChildJavaOpts(cluster) + " MB");
		list.add(reduceCapacityStats);

	//	return list;
		return reduceCapacityStats;
	}
	
}
