package org.jumbune.common.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskTrackersAccumulator extends AccumulatedJMXStats {

	// tasktracker stats

	private Integer allavg_processors = 0;
	private Long allavg_memory_available = 0l;
	private Long allavg_totalPhysicalMemory = 0l;

	public TaskTrackersAccumulator(List<String> taskTrackers, int taskTrackersPort) {
		nodeStatsMapper = new HashMap<String, JMXStats>(taskTrackers.size());
		this.port = taskTrackersPort;
		initializeMapper(taskTrackers);
	}

	public TaskTrackersAccumulator(List<String> taskTrackers) {
		// TODO Auto-generated constructor stub
		nodeStatsMapper = new HashMap<String, JMXStats>(taskTrackers.size());
		for (String taskTracker : taskTrackers) {
			nodeStatsMapper.put(taskTracker, new TaskTrackerStats(taskTracker, port));
		}
	}

	private void initializeMapper(List<String> taskTrackers) {
		for (String taskTracker : taskTrackers) {
			nodeStatsMapper.put(taskTracker, new TaskTrackerStats(taskTracker, port));
		}
	}

	/**
	 * @return the allavg_processors
	 */
	public Integer getAllavg_processors() {
		if (allavg_processors == 0l) {
			long total = 0l;
			Collection<JMXStats> taskTrackerStats = nodeStatsMapper.values();
			int numOfNodes = 0;
			Integer individualProcessorValue = 0;
			for (JMXStats taskTrackerStat : taskTrackerStats) {
				if(taskTrackerStat!=null){
					individualProcessorValue = ((TaskTrackerStats) taskTrackerStat).getProcessors();
					if(individualProcessorValue!=null){
						total += individualProcessorValue;
						numOfNodes++;
					}
				}
			}
			if(numOfNodes>0){
				this.allavg_processors = (int) (total / numOfNodes);
			}
		}
		return allavg_processors;
	}

	/**
	 * @return the allavg_memory_available
	 */
	public Long getAllavg_memory_available() {
		if (allavg_memory_available == 0l) {
			long total = 0l;
			Collection<JMXStats> taskTrackerStats = nodeStatsMapper.values();
			int numOfNodes = 0;
			Long individualMemoryAvailable;
			for (JMXStats taskTrackerStat : taskTrackerStats) {
				if(taskTrackerStat!=null){
					 individualMemoryAvailable =((TaskTrackerStats) taskTrackerStat).getMemory_available();
					 if(individualMemoryAvailable!=null){
						 total += individualMemoryAvailable;
						 numOfNodes++;
					 }
				}
			}
			if(numOfNodes>0){
				this.allavg_memory_available = (total / numOfNodes);
			}
		}
		return allavg_memory_available;
	}

	/**
	 * @return the allavg_totalPhysicalMemory
	 */
	public Long getAllavg_totalPhysicalMemory() {
		if (allavg_totalPhysicalMemory == 0l) {
			long total = 0l;
			Collection<JMXStats> taskTrackerStats = nodeStatsMapper.values();
			int numOfNodes = 0;
			Long individualTotalMemoryAvailable;
			for (JMXStats taskTrackerStat : taskTrackerStats) {
				if(taskTrackerStat!=null){
					individualTotalMemoryAvailable = ((TaskTrackerStats) taskTrackerStat).getTotalPhysicalMemory();
					if(individualTotalMemoryAvailable!=null){
						total += individualTotalMemoryAvailable; 
						numOfNodes++;
					}
				}
			}
			if(numOfNodes>0){
				this.allavg_totalPhysicalMemory = (total / numOfNodes);
			}
		}
		return allavg_totalPhysicalMemory;
	}
}