package org.jumbune.clusterprofiling.yarn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.SlaConf;
import org.jumbune.utils.conf.beans.SlaConfigurations;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

/**
 * The Class ClusterAnalysisMetrics. This class provides methods for gathering various hadoop metrics related to 
 * HDFS blocks, Yarn Apps and their resource utilization.
 */
public class ClusterAnalysisMetrics {

	private final String QUEUE = "queue";

	private final String APP_TYPE = "appType";

	private final String JOB = "job";

	private final String APPLICATION = "application";

	private final String APPLICATION_ID = "applicationID";

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ClusterAnalysisMetrics.class);

	/** The Constant DURATION_MILLIS. */
	private final String DURATION_MILLIS = "durationMillis";

	/** The Constant USER. */
	private final String USER = "user";

	/** The Constant ERROR. */
	private final String ERROR = "ERROR";
	
	/** The Constant MISSING_BLOCKS_KEY. */
	private final String MISSING_BLOCKS_KEY = "FSNamesystem.MissingBlocks";
	
	/** The Constant CORRUPT_BLOCKS_KEY. */
	private final String CORRUPT_BLOCKS_KEY = "FSNamesystem.CorruptBlocks";

	/** The MISSING_BLOCKS. */
	private final String MISSING_BLOCKS = "missingBlocks";
	
	/** The Constant CORRUPT_BLOCKS. */
	private final String CORRUPT_BLOCKS = "corruptedBlocks";

	/** The Constant USED_MEMORY. */
	private final String USED_MEMORY = "usedMemory";

	/** The Constant USED_VCORES. */
	private final String USED_VCORES = "usedVcores";
	
	private static ClusterAnalysisMetrics instance = null;
	
	public static ClusterAnalysisMetrics getInstance() {
		if (instance == null) {
			synchronized (ClusterAnalysisMetrics.class) {
				if (instance == null) {
					instance = new ClusterAnalysisMetrics();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Instantiates a new cluster analysis metrices.
	 *
	 * @param cluster the cluster
	 */
	private ClusterAnalysisMetrics() {
	}
	
	/**
	 * Gets the num applications running.
	 * @param rmCommunicator 
	 *
	 * @return the num applications running
	 */
	public int getNumApplicationsRunning(RMCommunicator rmCommunicator) {
		try {
			return rmCommunicator.getRunningApplications().size();
		} catch (YarnException | IOException e) {
			LOGGER.error("unable to get running applications", e.getMessage());
		}
		return 0;
		
	}
	
	
	/**
	 * Gets the running job name list.
	 * @param rmCommunicator 
	 *
	 * @return the running job name list
	 */
	public List<String> getRunningJobNameList(RMCommunicator rmCommunicator) {
		List<String> jobNameList = new ArrayList<String>();
		
		try {
			for (ApplicationReport report : rmCommunicator.getRunningApplications()) {
				jobNameList.add(report.getName());
			}
		} catch (YarnException | IOException e) {
			LOGGER.error("unable to get running job applications", e.getMessage());
		}
		return jobNameList;

	}

	/**
	 * Gets the num containers running.
	 * @param rmCommunicator 
	 *
	 * @return the num containers running
	 */
	public int getNumContainersRunning(RMCommunicator rmCommunicator) {
		int containers = 0;
		try { 			
			for (NodeReport report : rmCommunicator.getNodeReports()) {
				if(NodeState.RUNNING == report.getNodeState()) {
					containers += report.getNumContainers();
				}
			}
		} catch (YarnException | IOException e) {
            LOGGER.error("unable to get running containers", e.getMessage()); 
		}
		return containers;
	}
	
	
	/**
	 * Gets the long running applications.
	 * This method returns IDs of all the yarn applications which have been running for a long period of time 
	 * depending upon the threshold time period given by the user. 
	 *
	 * @param durationMillis the duration millis
	 * @param cluster 
	 * @param rmCommunicator 
	 * @return the long running applications
	 */
	public List<Map<String, Object>> getLongRunningApplications(
			final long durationMillis, Cluster cluster, RMCommunicator rmCommunicator) 
				throws YarnException, IOException {
		List<Map<String, Object>> appReports = new ArrayList<Map<String, Object>>(5);
		Map<String, Object> attribMap = null;
		long appDuration;
		long endMillis = RemotingUtil.getHadoopClusterTimeMillis(cluster);
		for (ApplicationReport report : rmCommunicator.getRunningApplications()) {
			appDuration = (endMillis - report.getStartTime());
			if (appDuration > durationMillis) {
				attribMap = new HashMap<>(5);
				attribMap.put(APPLICATION_ID, report.getApplicationId().toString());
				attribMap.put(DURATION_MILLIS, appDuration);
				attribMap.put(USER, report.getUser());
				attribMap.put(QUEUE, report.getQueue());
				attribMap.put(APP_TYPE, report.getApplicationType());
				appReports.add(attribMap);
			}
		}
		return appReports;
	}
	
	public Map<String, Long> getJobsDuration(RMCommunicator rmCommunicator) throws YarnException, IOException {

		long appDuration, endMillis = 0;
		Map<String, Long> jobsDuration = new HashMap<String, Long>();
		for (ApplicationReport report : rmCommunicator.getApplications()) {
			if (report.getFinalApplicationStatus().equals(FinalApplicationStatus.SUCCEEDED)) {
				endMillis = report.getFinishTime();
				appDuration = (endMillis - report.getStartTime());
				jobsDuration.put(report.getApplicationId().toString().replace(APPLICATION, JOB), appDuration);
			}
		}	
		return jobsDuration;
	}
	
	public List<String> getSlaApps(Cluster cluster, RMCommunicator rmCommunicator) throws IOException {
		
		SlaConfigurations slaConfigurations = AdminConfigurationUtil.getSlaConfigurations(cluster.getClusterName());
		List<SlaConf> slaConfList = slaConfigurations.getSlaConfList();
		List<String> result = new ArrayList<String>();
		long currentServerTime = RemotingUtil.getHadoopClusterTimeMillis(cluster);
		try {
			for (ApplicationReport report : rmCommunicator.getRunningApplications()) {
				if ( isThresholdExceeded(report, slaConfList, currentServerTime) ) {
					result.add(report.getApplicationId().toString());
				}
			}
		} catch (YarnException | IOException e) {	
            LOGGER.error("unable to get sla apps list", e);
		}	
		return result;
	}
	
	private boolean isThresholdExceeded(ApplicationReport report, List<SlaConf> slaConfList, long currentServerTime) {
		String user = report.getUser();
		for (SlaConf slaConf : slaConfList) {
			if (slaConf.getUser().equals(user)) {
				if ( (currentServerTime - report.getStartTime()) > (slaConf.getMaximumDuration() * 60000)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the faulty blocks.
	 * This method provides metrics for faulty_blocks, running_containers and running_apps (yarn applications) in the cluster.
	 * @param cluster 
	 *
	 * @return the faulty blocks
	 */
	public Map<String, String> getFaultyBlocks(Cluster cluster) {
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		Map<String, String> nameNodeStats = null;
		Map<String, String> faultyBlocks = new HashMap<>(2);
		try {
			nameNodeStats = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, cluster.getNameNode(), 
					cluster.getNameNodes().getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
		    faultyBlocks.put(MISSING_BLOCKS, nameNodeStats.get(MISSING_BLOCKS_KEY));
		    faultyBlocks.put(CORRUPT_BLOCKS, nameNodeStats.get(CORRUPT_BLOCKS_KEY));
		} catch (AttributeNotFoundException | InstanceNotFoundException | IntrospectionException | MBeanException
				| ReflectionException | IOException e) {
			 LOGGER.error("unable to get faulty blocks", e.getMessage());
			 faultyBlocks = new HashMap<>(1);
             faultyBlocks.put(ERROR, "unable to get faulty blocks");       
		}
		return faultyBlocks;
	}
	
	/**
	 * Gets the resource over usage.
	 * This method returns any over usage of resources(vcores and memory) by yarn applications. Criterion for declaring a resource over used is
	 * the threshold(memory and vcores threshold) provided by the user. <br>
	 * It returns such application IDs along with their respective resource consumptions.
	 * 
	 * @param memoryThresholdMB the memory threshold mb
	 * @param vcoresThreshold the vcores threshold
	 * @param rmCommunicator 
	 * @return the resource over usage
	 */
	public Map<String, Map<String, Object>> getResourceOverUsage(
			int memoryThresholdMB, int vcoresThreshold, RMCommunicator rmCommunicator) {
		Resource usedResources = null;
		Map<String, Map<String, Object>> appResourceReports = new HashMap<>(5);
		Map<String, Object> resourceMap = null;
		try {
			for (ApplicationReport report : rmCommunicator.getRunningApplications()) {
				usedResources = report.getApplicationResourceUsageReport().getUsedResources();
				if (usedResources.getMemory() > memoryThresholdMB || usedResources.getVirtualCores() > vcoresThreshold) {
					resourceMap = new HashMap<>(5);
					resourceMap.put(USED_MEMORY, usedResources.getMemory());
					resourceMap.put(USED_VCORES, usedResources.getVirtualCores());
					resourceMap.put(APP_TYPE, report.getApplicationType());
					resourceMap.put(QUEUE, report.getQueue());
					resourceMap.put(USER, report.getUser());
					appResourceReports.put(report.getApplicationId().toString(), resourceMap);
				}
				
			}
		} catch (YarnException | IOException e) {
			LOGGER.error("unable to get running containers", e.getMessage());
		}
		return appResourceReports;
	}
	
}
