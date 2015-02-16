package org.jumbune.common.yarn.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.jobhistory.EventType;
import org.apache.hadoop.mapreduce.jobhistory.HistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser;
import org.apache.hadoop.mapreduce.jobhistory.MapAttemptFinishedEvent;
import org.apache.hadoop.mapreduce.jobhistory.ReduceAttemptFinishedEvent;

/**
 * Decorated class for JobHistoryParser to extract out CPU and Physical RAM usages for completed Map and Reduce events
 *
 */
public class DecoratedJobHistoryParser extends JobHistoryParser {

	/**
	 * This holds the whole Job related additional Information
	 */
	AdditionalJobInfo additionalJobInfo;

	/**
	 * This is the constructor which should be called to invoke parsing job history files.
	 * @param fs
	 * @param historyFile
	 * @throws IOException
	 */
	public DecoratedJobHistoryParser(FileSystem fs, Path historyFile)
			throws IOException {
		super(fs, historyFile);
		additionalJobInfo = new AdditionalJobInfo();
	}
	
	/**
	 * This method can called anytime after making {@link JobHistoryParser} parse method call.
	 * @return AdditionalJobInfo instance
	 */
	public AdditionalJobInfo getAdditionalJobInfo(){
		return additionalJobInfo;
	}
	
	  /* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser#handleEvent(org.apache.hadoop.mapreduce.jobhistory.HistoryEvent)
	 */
	@Override
	  public void handleEvent(HistoryEvent event)  { 
	    EventType type = event.getEventType();

	    switch (type) {
	    case MAP_ATTEMPT_FINISHED:
	      handleMapAttemptFinishedEvent((MapAttemptFinishedEvent) event);
			super.handleEvent(event);
	      break;
	    case REDUCE_ATTEMPT_FINISHED:
	      handleReduceAttemptFinishedEvent((ReduceAttemptFinishedEvent) event);
			super.handleEvent(event);
	      break;
	    default:
	    	super.handleEvent(event);
	      break;
	    }
	  }
	

	/**
	 * Customized methods for handling MapAttemptFinishedEvents
	 * @param event
	 */
	private void handleMapAttemptFinishedEvent(MapAttemptFinishedEvent event) {
		Map<TaskAttemptID, AdditionalTaskInfo> additionalJobInfoMap = additionalJobInfo
				.getAdditionalTasksMap();
		if (!additionalJobInfoMap.containsKey(event.getAttemptId())) {
			AdditionalTaskInfo additionalTaskInfo = new AdditionalTaskInfo();
			additionalTaskInfo.taskType = event.getTaskType();
			additionalTaskInfo.cpuUsages = event.getCpuUsages();
			additionalTaskInfo.physicalMemInKBs = event.getPhysMemKbytes();
			additionalJobInfoMap.put(event.getAttemptId(), additionalTaskInfo);
		}
	}

	/**
	 * Customized methods for handling ReduceAttemptFinishedEvents
	 * @param event
	 */
	private void handleReduceAttemptFinishedEvent(
			ReduceAttemptFinishedEvent event) {
		Map<TaskAttemptID, AdditionalTaskInfo> additionalJobInfoMap = additionalJobInfo
				.getAdditionalTasksMap();
		if (!additionalJobInfoMap.containsKey(event.getAttemptId())) {
			AdditionalTaskInfo additionalTaskInfo = new AdditionalTaskInfo();
			additionalTaskInfo.taskType = event.getTaskType();
			additionalTaskInfo.cpuUsages = event.getCpuUsages();
			additionalTaskInfo.physicalMemInKBs = event.getPhysMemKbytes();
			additionalJobInfoMap.put(event.getAttemptId(), additionalTaskInfo);
		}
	}

	/**
	 * Parent Additional Job Info class
	 */
	public static class AdditionalJobInfo {
		Map<TaskAttemptID, AdditionalTaskInfo> additionalTasksMap;

		public AdditionalJobInfo() {
			additionalTasksMap = new HashMap<TaskAttemptID, AdditionalTaskInfo>();
		}

		public Map<TaskAttemptID, AdditionalTaskInfo> getAdditionalTasksMap() {
			return additionalTasksMap;
		}

	}

	/**
	 * Task Info class, to hold cpu usages and physical memory usages
	 */
	public static class AdditionalTaskInfo {
		int[] cpuUsages;
		int[] physicalMemInKBs;
		TaskType taskType;

		public AdditionalTaskInfo() {
			cpuUsages = new int[] {};
			physicalMemInKBs = new int[] {};
			}

		public void printAll() {
			System.out.println("Task Type:"+taskType.name());
			System.out.println("CPU Usages []:"+Arrays.toString(cpuUsages));
			System.out.println("Physical Mem(KB) []:"+Arrays.toString(physicalMemInKBs));
			
		}
		
		public int[] getCpuUages() {
			return cpuUsages;
		}

		public int[] getPhysicalMemInKBs() {
			return physicalMemInKBs;
		}
		
		public TaskType getTaskType(){
			return taskType;
		}
	}

}
