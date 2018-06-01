package org.jumbune.clusteranalysis.queues;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusteranalysis.queues.beans.CapacitySchedulerQueueInfo;
import org.jumbune.clusteranalysis.queues.beans.FairSchedulerQueueInfo;
import org.jumbune.clusteranalysis.queues.beans.Resource;
import org.jumbune.clusteranalysis.queues.beans.Scheduler;
import org.jumbune.common.utils.Constants;

public class SchedulerUtil {
	
	private static final Logger LOGGER = LogManager
			.getLogger(SchedulerUtil.class);
	
	/**
	 * Returns all capacity scheduler leaf queues
	 * @return
	 */
	public static List<CapacitySchedulerQueueInfo> getCapcitySchedulerLeafQueues(Scheduler scheduler) {
		List<CapacitySchedulerQueueInfo> leafQueues = new ArrayList<>(2);
		
		Stack<CapacitySchedulerQueueInfo> stack = new Stack<>();
		stack.addAll(scheduler.getSchedulerInfo().getQueues());
		
		CapacitySchedulerQueueInfo temp;
		while (!stack.isEmpty()) {
			temp = stack.pop();
			if (temp.isLeafQueue()) {
				leafQueues.add(temp);
			} else {
				for (CapacitySchedulerQueueInfo childQueue : temp.getQueues()) {
					stack.push(childQueue);
				}
			}
		}
		return leafQueues;
	}
	
	/**
	 * Returns all fair scheduler leaf queues
	 * @return
	 */
	public static List<FairSchedulerQueueInfo> getFairSchedulerLeafQueues(Scheduler scheduler) {
		List<FairSchedulerQueueInfo> list = new ArrayList<>();
		LOGGER.debug("Fetched scheduler.getSchedulerInfo() json = " + Constants.gson.toJson(scheduler.getSchedulerInfo()));
		setDrfAndGetLeafQueues(scheduler.getSchedulerInfo().getRootQueue(), false, list);
		LOGGER.debug("Parsed list json = " + Constants.gson.toJson(list));
		boolean isDominentResourceFairness = isDRF(list);

		// Setting some variables (to be used later like ui and writing data to
		// influxdb [YarnQueuesUtils] )
		
		for (FairSchedulerQueueInfo queueInfo : list) {
			LOGGER.debug("Queue | " + queueInfo.getQueueName());
			
			/**
			 * In version 2.6.0 there are some changes in api. For Hadoop Versions 2.6.0
			 * and higher, in FairSchedulerQueueInfo, fairResources is 'Instantaneous
			 * Fair Share'. For Hadoop Versions before 2.6.0, fairResources is 'Steady
			 * Fair Share'
			 */
			queueInfo.setFinalSteadyFairMemory(getMemory(queueInfo));
			
			if (queueInfo.getFinalSteadyFairMemory() == 0) {
				queueInfo.setPercentUsedMemory(0);
			} else {
				queueInfo.setPercentUsedMemory(
						(queueInfo.getUsedResources().getMemory() * 100.0) / queueInfo.getFinalSteadyFairMemory());
			}

			if (isDominentResourceFairness) {
				queueInfo.setFinalSteadyFairVCores(getvCores(queueInfo));
				
				if (queueInfo.getSchedulingPolicy().equals(Constants.DRF)) {
					try {
						queueInfo.setPercentUsedVCores(
								(queueInfo.getUsedResources().getvCores() * 100.0) / queueInfo.getFinalSteadyFairVCores());
					} catch (Exception e) {
						queueInfo.setPercentUsedVCores(0.0);
					}
				} else {
					queueInfo.setPercentUsedVCores(0.0);
				}
				
			}
		}

		return list;
	}
	
	private static void setDrfAndGetLeafQueues(FairSchedulerQueueInfo queueInfo,
			boolean isParentDrf, List<FairSchedulerQueueInfo> list) {
		List<FairSchedulerQueueInfo> childs = queueInfo.getChildQueues();
		if (childs != null) {
			boolean isDRF = queueInfo.getSchedulingPolicy().equalsIgnoreCase(Constants.DRF);
			for (FairSchedulerQueueInfo childQueue : childs) {
				setDrfAndGetLeafQueues(childQueue, isDRF, list);
			}
		} else {
			if (isParentDrf) {
				queueInfo.setSchedulingPolicy(Constants.DRF);
			}
			list.add(queueInfo);
		}
	}
	
	private static long getMemory(FairSchedulerQueueInfo queueInfo) {
		Resource resource1 = queueInfo.getSteadyFairResources();
		Resource resource2 = queueInfo.getFairResources();
		if (resource1 == null) {
			return resource2.getMemory();
		}
		if (resource2 == null) {
			return resource1.getMemory();
		}
		return resource1.getMemory() > resource2.getMemory() ? resource1.getMemory() : resource2.getMemory();
	}
	
	private static long getvCores(FairSchedulerQueueInfo queueInfo) {
		Resource resource1 = queueInfo.getSteadyFairResources();
		Resource resource2 = queueInfo.getFairResources();
		if (resource1 == null) {
			return resource2.getvCores();
		}
		if (resource2 == null) {
			return resource1.getvCores();
		}
		return resource1.getvCores() > resource2.getvCores() ? resource1.getvCores() : resource2.getvCores();
	}
	
	
	/**
	 * It checks if any one in the queue has dominant resource fairness or not
	 * @param list
	 * @return
	 */
	private static boolean isDRF(List<FairSchedulerQueueInfo> list) {
		for (FairSchedulerQueueInfo queueInfo : list) {
			if (queueInfo.getSchedulingPolicy().equalsIgnoreCase(Constants.DRF)) {
				return true;
			}
		}
		return false;
	}

}
