package org.jumbune.utils.yarn.communicators;


import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.LongRange;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;


/**
 *  Class for creating a client to the Resource Manager.
 *  Use org.jumbune.common.utils.CommunicatorFactory to create object
 */
public class RMCommunicator {
	
	/**
	 * Resource Manager for which this object is created;
	 */
	private String nodeIP;

	/** The proxy. */
	private ApplicationClientProtocol proxy;
	
	private List<ApplicationReport> appListCache;
	
	private long lastCacheUpdateTime = 0;
	
	private List<ApplicationReport> runningAppsCache;
	
	private long lastUpdateTimeRunningApps = 0;
	
	private static GetApplicationsRequest runningAppsRequest = GetApplicationsRequest.newInstance(
			EnumSet.of(YarnApplicationState.RUNNING));
	
	
	
	public RMCommunicator(String nodeIP, ApplicationClientProtocol proxy) {
		this.nodeIP = nodeIP;
		this.proxy = proxy;
	}
	
	public String getNodeIP() {
		return this.nodeIP;
	}
	
	/**
	 * Get list of all applications known to RM.
	 *
	 * @return list of application report
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<ApplicationReport> getApplications() throws YarnException, IOException{
		if ((System.currentTimeMillis() - lastCacheUpdateTime) > 60000) {
			synchronized (this) {
				if ( (System.currentTimeMillis() - lastCacheUpdateTime) > 60000) {
					GetApplicationsResponse response = proxy.getApplications(GetApplicationsRequest.newInstance());
					appListCache = response.getApplicationList();
					lastCacheUpdateTime = System.currentTimeMillis();
				}
			}
		}
		return appListCache;
	}
	
	/**
	 * Get list of all applications known to RM according to time range provided.
	 *@param set of application types
	 * @return list of application report
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	public List<ApplicationReport> getApplications(ApplicationsRequestScope scope, Set<String> users,
			Set<String> queues, Set<String> applicationTypes, Set<String> applicationTags,
			EnumSet<YarnApplicationState> applicationStates, Long finishFrom, Long finishTo, Long limit)
					throws YarnException, IOException {
		return proxy
				.getApplications(GetApplicationsRequest.newInstance(scope, users, queues, applicationTypes,
						applicationTags, applicationStates, null, new LongRange(finishFrom, finishTo), limit))
				.getApplicationList();
	}
	
	/**
	 * Given a list of Application Reports this method extracts only those Application Report whose progress is <1.0
	 *
	 * @param reports the reports
	 * @return list of currently running applications
	 */
	public List<ApplicationReport> getRunningApplications() throws YarnException, IOException {
		if ((System.currentTimeMillis() - lastUpdateTimeRunningApps) > 20000) {
			synchronized (this) {
				if ( (System.currentTimeMillis() - lastUpdateTimeRunningApps) > 20000) {
					runningAppsCache = proxy.getApplications(runningAppsRequest).getApplicationList();
					lastUpdateTimeRunningApps = System.currentTimeMillis();
				}
			}
		}
		return runningAppsCache;
	}
	
	/**
	 * Get the Job Id for the given Application Report.
	 *
	 * @param report the report
	 * @return the job id
	 */
	public JobId getJobId(ApplicationReport report){
		ApplicationId applicationId = report.getApplicationId();
		return MRBuilderUtils.newJobId(applicationId, applicationId.getId());
	}
	
	/**
	 * Get the number of node managers in the cluster.
	 *
	 * @return int, number of node managers
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int getNumOfNodeManagersInCluster() throws YarnException, IOException{
		return proxy.getClusterMetrics(GetClusterMetricsRequest.newInstance())
					.getClusterMetrics()
					.getNumNodeManagers();
	}

	/**
	 * Get the Node Reports for all the nodes of the cluster.
	 *
	 * @return the node reports
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return, the list of node report
	 */
	public List<NodeReport> getNodeReports() throws YarnException, IOException{
		return proxy.getClusterNodes(GetClusterNodesRequest.newInstance())
					.getNodeReports();		
	}
	
	/**
	 * Gets the queue info.
	 *
	 * @param queueName the queue name
	 * @return the queue info
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public QueueInfo getQueueInfo(String queueName)throws YarnException, IOException {
		return proxy.getQueueInfo(GetQueueInfoRequest.newInstance(queueName, true , true, true))
					.getQueueInfo();
	}
	
}