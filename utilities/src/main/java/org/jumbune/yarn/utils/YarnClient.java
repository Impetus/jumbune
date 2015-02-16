package org.jumbune.yarn.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.ApplicationIdNotProvidedException;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YarnClient extends AbstractService implements YarnOperations {

	private boolean historyServiceEnabled;
	private long submitPollIntervalMillis;
	private long asyncApiPollIntervalMillis;
	private long asyncApiPollTimeoutMillis;

	// RMClient handle
	private ApplicationClientProtocol applicationsManager;
	private ApplicationHistoryClient historyClient;

	public static final Logger LOGGER = LogManager.getLogger(YarnClient.class);
	private static final String ROOT = "root";

	public YarnClient() throws Exception {
		this(YarnClient.class.getName());
	}

	public YarnClient(String name) throws Exception {
		super(name);
		serviceStart();
	}

	/* The first step that a client needs to do is to connect to the ResourceManager or to be more specific, 
	 * the ApplicationsManager (AsM) interface of the ResourceManager.
	 * 
	 * @see org.apache.hadoop.service.AbstractService#serviceStart()
	 */
	@Override
	protected void serviceStart() throws Exception {
		try {
			applicationsManager = ClientRMProxy.createRMProxy(getConfig(),
					ApplicationClientProtocol.class);
			if (historyServiceEnabled) {
				historyClient.start();
			}
		} catch (IOException e) {
			throw new YarnRuntimeException(e);
		}
		super.serviceStart();
	}
	
	@Override
	public Configuration getConfig(){
		Configuration conf =  new YarnConfiguration();
		conf.set("yarn.resourcemanager.address", "192.168.49.52:8032");
		conf.set("yarn.nodemanager.aux-services", "mapreduce_shuffle");
		conf.set("yarn.resourcemanager.scheduler.address", "192.168.49.52:8030");
		conf.set("yarn.resourcemanager.resource-tracker.address", "192.168.49.52:8031");
		conf.set("yarn.resourcemanager.webapp.address", "192.168.49.52:8088");
		conf.set("yarn.resourcemanager.admin.address", "192.168.49.52:8033");
		return conf;
	}

	@Override
	protected void serviceStop() throws Exception {
		if (this.applicationsManager != null) {
			RPC.stopProxy(this.applicationsManager);
		}
		if (historyServiceEnabled) {
			historyClient.stop();
		}
		super.serviceStop();
	}
	
	public ApplicationId createAndSubmitApplication(String appName, boolean isFileOnHDFS, boolean isFile, LocalResourceVisibility visibility, String jarPathString, String classPathAppend, String command, int numOfCopies) throws YarnException, IOException{
		YarnClientApplication clientApplication = createApplication(appName);
		System.out.println("Application Id:"+clientApplication.getApplicationSubmissionContext().getApplicationId().toString());
		ApplicationSubmissionContext applicationSubmissionContext = clientApplication.getApplicationSubmissionContext();
		applicationSubmissionContext.setAMContainerSpec(createContainerLaunchContext(isFile, isFileOnHDFS, visibility, jarPathString, classPathAppend, command, numOfCopies));
		return submitApplication(applicationSubmissionContext);
	}
	
	/**
	 * Create a new container launch context for the AM's container
	 * @param isFile 
	 * @param numOfCopies 
	 * @return
	 * @throws IOException 
	 */
	private ContainerLaunchContext createContainerLaunchContext(boolean isFile, boolean isFileOnHDFS, LocalResourceVisibility visibility, String jarPathString, String classPathAppend, String command, int numOfCopies) throws IOException{
	    ContainerLaunchContext amContainer = 
		        Records.newRecord(ContainerLaunchContext.class);
	    LocalResource amJarRsrc = Records.newRecord(LocalResource.class);
	    
	    if(isFile){
	    	amJarRsrc.setType(LocalResourceType.FILE);
	    }else{
	    	amJarRsrc.setType(LocalResourceType.ARCHIVE);
	    }	    
	    amJarRsrc.setVisibility(visibility);

	    if(isFileOnHDFS){
		    //TODO: Properly Initialize FS
		    FileSystem fs = null;
		    Path jarPath = new Path(jarPathString);
		    FileStatus jarStatus = fs.getFileStatus(jarPath);
		    amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));			
		    amJarRsrc.setTimestamp(jarStatus.getModificationTime());
		    amJarRsrc.setSize(jarStatus.getLen());
	    }else{
	    	File file = new File(jarPathString);
	    	URL packageUrl = ConverterUtils.getYarnUrlFromPath(
	    	        FileContext.getFileContext().makeQualified(new Path(jarPathString)));
	    	amJarRsrc.setResource(packageUrl);
		    Path jarPath = new Path(jarPathString);
		    amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
		    amJarRsrc.setTimestamp(file.lastModified());
		    amJarRsrc.setSize(file.length());
	    }
	    
	    Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
	    localResources.put("AppMaster.jar",  amJarRsrc);
	    amContainer.setLocalResources(localResources);
	    
	 // Set up the environment needed for the launch context
	    Map<String, String> env = new HashMap<String, String>();
	    //String classPathEnv = "$CLASSPATH:./*:"+classPathAppend;
	    String classPathEnv = "$CLASSPATH:./*:"+"home/impadmin/Downloads/simple-yarn-app-master/target/simple-yarn-app-1.1.0.jar";
	    
	    env.put("CLASSPATH", classPathEnv);
	    amContainer.setEnvironment(env);
	    
	    
	    List<String> commands = Collections.singletonList(
	    		"$JAVA_HOME/bin/java" +
	    		" -Xmx256M" +
	    		" com.hortonworks.simpleyarnapp.ApplicationMaster" +
	    		" " + command +
	    		" " + String.valueOf(numOfCopies)
	    		);

	    amContainer.setCommands(commands);
	    

	    return amContainer;
	}

	private YarnClientApplication createApplication(String appName) throws YarnException,
			IOException {
		ApplicationSubmissionContext context = Records
				.newRecord(ApplicationSubmissionContext.class);
		GetNewApplicationResponse newApp = getNewApplication();
		ApplicationId appId = newApp.getApplicationId();
		context.setApplicationId(appId);
		context.setApplicationName(appName);
		context.setResource(Resource.newInstance(512, 1));
		return new YarnClientApplication(newApp, context);
	}

	/**
	 * The response from the ASM for a new application also contains information about the cluster such as the minimum/maximum resource capabilities of the cluster.
	 * @param yarnClientApplication
	 * @return
	 */
	private Resource getMaxResourceCapabilitiesOfRM(YarnClientApplication yarnClientApplication){
		GetNewApplicationResponse newApp = yarnClientApplication.getNewApplicationResponse();
		return newApp.getMaximumResourceCapability();
	}

	/**
	 * Once a handle is obtained to the ASM, the client needs to request the ResourceManager for a new ApplicationId. 
	 * @return
	 * @throws YarnException
	 * @throws IOException
	 */
	private GetNewApplicationResponse getNewApplication() throws YarnException,
			IOException {
		GetNewApplicationRequest request = Records
				.newRecord(GetNewApplicationRequest.class);
		return applicationsManager.getNewApplication(request);
	}
	
	private ApplicationId submitApplication(
			ApplicationSubmissionContext appContext) throws YarnException,
			IOException {
		ApplicationId applicationId = appContext.getApplicationId();
		if (applicationId == null) {
			throw new ApplicationIdNotProvidedException(
					"ApplicationId is not provided in ApplicationSubmissionContext");
		}
		SubmitApplicationRequest request = Records
				.newRecord(SubmitApplicationRequest.class);
		request.setApplicationSubmissionContext(appContext);

		// TODO: YARN-1763:Handle RM failovers during the submitApplication
		// call.
		applicationsManager.submitApplication(request);

		int pollCount = 0;
		long startTime = System.currentTimeMillis();

		while (true) {
			try {
				YarnApplicationState state = getApplicationReport(applicationId)
						.getYarnApplicationState();
				if (!state.equals(YarnApplicationState.NEW)
						&& !state.equals(YarnApplicationState.NEW_SAVING)) {
					LOGGER.info("Submitted application " + applicationId);
					break;
				}

				long elapsedMillis = System.currentTimeMillis() - startTime;
				if (enforceAsyncAPITimeout()
						&& elapsedMillis >= asyncApiPollTimeoutMillis) {
					throw new YarnException(
							"Timed out while waiting for application "
									+ applicationId
									+ " to be submitted successfully");
				}

				// Notify the client through the log every 10 poll, in case the
				// client
				// is blocked here too long.
				if (++pollCount % 10 == 0) {
					LOGGER.info("Application submission is not finished, "
							+ "submitted application " + applicationId
							+ " is still in " + state);
				}
				try {
					Thread.sleep(submitPollIntervalMillis);
				} catch (InterruptedException ie) {
					LOGGER.error("Interrupted while waiting for application "
							+ applicationId + " to be successfully submitted.");
				}
			} catch (ApplicationNotFoundException ex) {
				// FailOver or RM restart happens before RMStateStore saves
				// ApplicationState
				LOGGER.info("Re-submit application " + applicationId
						+ "with the " + "same ApplicationSubmissionContext");
				applicationsManager.submitApplication(request);
			}
		}

		return applicationId;
	}

	@Override
	public ApplicationReport monitorApplication(ApplicationId appId)
			throws YarnException, IOException {
		GetApplicationReportResponse response = null;
		try {
			GetApplicationReportRequest request = Records
					.newRecord(GetApplicationReportRequest.class);
			request.setApplicationId(appId);
			response = applicationsManager.getApplicationReport(request);
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (!(e.getClass() == ApplicationNotFoundException.class)) {
				throw e;
			}
			return historyClient.getApplicationReport(appId);
		}
		return response.getApplicationReport();
	}

	@Override
	public List<ApplicationReport> getApplications() throws YarnException,
			IOException {
		return getApplications(null, null);
	}

	public List<ApplicationReport> getApplications(Set<String> applicationTypes)
			throws YarnException, IOException {
		return getApplications(applicationTypes, null);
	}

	public List<ApplicationReport> getApplications(
			EnumSet<YarnApplicationState> applicationStates)
			throws YarnException, IOException {
		return getApplications(null, applicationStates);
	}

	public List<ApplicationReport> getApplications(
			Set<String> applicationTypes,
			EnumSet<YarnApplicationState> applicationStates)
			throws YarnException, IOException {
		GetApplicationsRequest request = GetApplicationsRequest.newInstance(
				applicationTypes, applicationStates);
		GetApplicationsResponse response = applicationsManager
				.getApplications(request);
		return response.getApplicationList();
	}

	@Override
	public YarnClusterMetrics getYarnClusterMetrics() throws YarnException,
			IOException {
		GetClusterMetricsRequest request = Records
				.newRecord(GetClusterMetricsRequest.class);
		GetClusterMetricsResponse response = applicationsManager
				.getClusterMetrics(request);
		return response.getClusterMetrics();
	}

	@Override
	public List<NodeReport> getNodeReports(NodeState... states)
			throws YarnException, IOException {
		EnumSet<NodeState> statesSet = (states.length == 0) ? EnumSet
				.allOf(NodeState.class) : EnumSet.noneOf(NodeState.class);
		for (NodeState state : states) {
			statesSet.add(state);
		}
		GetClusterNodesRequest request = GetClusterNodesRequest
				.newInstance(statesSet);
		GetClusterNodesResponse response = applicationsManager
				.getClusterNodes(request);
		return response.getNodeReports();
	}

	@Override
	public QueueInfo getQueueInfo(String queueName) throws YarnException,
			IOException {
		GetQueueInfoRequest request = getQueueInfoRequest(queueName, true,
				false, false);
		Records.newRecord(GetQueueInfoRequest.class);
		return applicationsManager.getQueueInfo(request).getQueueInfo();
	}

	private GetQueueInfoRequest getQueueInfoRequest(String queueName,
			boolean includeApplications, boolean includeChildQueues,
			boolean recursive) {
		GetQueueInfoRequest request = Records
				.newRecord(GetQueueInfoRequest.class);
		request.setQueueName(queueName);
		request.setIncludeApplications(includeApplications);
		request.setIncludeChildQueues(includeChildQueues);
		request.setRecursive(recursive);
		return request;
	}

	@Override
	public List<QueueInfo> getAllQueues() throws YarnException, IOException {
		List<QueueInfo> queues = new ArrayList<QueueInfo>();

		QueueInfo rootQueue = applicationsManager.getQueueInfo(
				getQueueInfoRequest(ROOT, false, true, true)).getQueueInfo();
		getChildQueues(rootQueue, queues, true);
		return queues;
	}

	@Override
	public List<QueueInfo> getRootQueueInfos() throws YarnException,
			IOException {
		List<QueueInfo> queues = new ArrayList<QueueInfo>();

		QueueInfo rootQueue = applicationsManager.getQueueInfo(
				getQueueInfoRequest(ROOT, false, true, true)).getQueueInfo();
		getChildQueues(rootQueue, queues, false);
		return queues;
	}

	private void getChildQueues(QueueInfo parent, List<QueueInfo> queues,
			boolean recursive) {
		List<QueueInfo> childQueues = parent.getChildQueues();

		for (QueueInfo child : childQueues) {
			queues.add(child);
			if (recursive) {
				getChildQueues(child, queues, recursive);
			}
		}
	}

	@Override
	public List<QueueInfo> getChildQueueInfos(String parent)
			throws YarnException, IOException {
		List<QueueInfo> queues = new ArrayList<QueueInfo>();

		QueueInfo parentQueue = applicationsManager.getQueueInfo(
				getQueueInfoRequest(parent, false, true, false)).getQueueInfo();
		getChildQueues(parentQueue, queues, true);
		return queues;
	}

	@Override
	public List<ContainerReport> getContainers(
			ApplicationAttemptId applicationAttemptId) throws YarnException,
			IOException {
		try {
			GetContainersRequest request = Records
					.newRecord(GetContainersRequest.class);
			request.setApplicationAttemptId(applicationAttemptId);
			GetContainersResponse response = applicationsManager
					.getContainers(request);
			return response.getContainerList();
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (e.getClass() != ApplicationNotFoundException.class) {
				throw e;
			}
			return historyClient.getContainers(applicationAttemptId);
		}
	}

	@Override
	public ContainerReport getContainerReport(ContainerId containerId)
			throws YarnException, IOException {
		try {
			GetContainerReportRequest request = Records
					.newRecord(GetContainerReportRequest.class);
			request.setContainerId(containerId);
			GetContainerReportResponse response = applicationsManager
					.getContainerReport(request);
			return response.getContainerReport();
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (e.getClass() != ApplicationNotFoundException.class) {
				throw e;
			}
			return historyClient.getContainerReport(containerId);
		}
	}

	@Override
	public List<ApplicationAttemptReport> getApplicationAttempts(
			ApplicationId applicationId) throws YarnException, IOException {
		try {
			GetApplicationAttemptsRequest request = Records
					.newRecord(GetApplicationAttemptsRequest.class);
			request.setApplicationId(applicationId);
			GetApplicationAttemptsResponse response = applicationsManager
					.getApplicationAttempts(request);
			return response.getApplicationAttemptList();
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (e.getClass() != ApplicationNotFoundException.class) {
				throw e;
			}
			return historyClient.getApplicationAttempts(applicationId);
		}
	}

	@Override
	public ApplicationAttemptReport getApplicationAttemptReport(
			ApplicationAttemptId applicationAttemptId) throws YarnException,
			IOException {
		try {
			GetApplicationAttemptReportRequest request = Records
					.newRecord(GetApplicationAttemptReportRequest.class);
			request.setApplicationAttemptId(applicationAttemptId);
			GetApplicationAttemptReportResponse response = applicationsManager
					.getApplicationAttemptReport(request);
			return response.getApplicationAttemptReport();
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (e.getClass() != ApplicationNotFoundException.class) {
				throw e;
			}
			return historyClient
					.getApplicationAttemptReport(applicationAttemptId);
		}
	}

	@Override
	public List<QueueUserACLInfo> getQueueAclsInfo() throws YarnException,
			IOException {
		GetQueueUserAclsInfoRequest request = Records
				.newRecord(GetQueueUserAclsInfoRequest.class);
		return applicationsManager.getQueueUserAcls(request)
				.getUserAclsInfoList();
	}


	boolean enforceAsyncAPITimeout() {
		return asyncApiPollTimeoutMillis >= 0;
	}

	private ApplicationReport getApplicationReport(ApplicationId appId)
			throws YarnException, IOException {
		GetApplicationReportResponse response = null;
		try {
			GetApplicationReportRequest request = Records
					.newRecord(GetApplicationReportRequest.class);
			request.setApplicationId(appId);
			response = applicationsManager.getApplicationReport(request);
		} catch (YarnException e) {
			if (!historyServiceEnabled) {
				// Just throw it as usual if historyService is not enabled.
				throw e;
			}
			// Even if history-service is enabled, treat all exceptions still
			// the same
			// except the following
			if (!(e.getClass() == ApplicationNotFoundException.class)) {
				throw e;
			}
			return historyClient.getApplicationReport(appId);
		}
		return response.getApplicationReport();
	}
	
	@Override
	public void killApplication(ApplicationId applicationId)
			throws YarnException, IOException {
		KillApplicationRequest request = Records
				.newRecord(KillApplicationRequest.class);
		request.setApplicationId(applicationId);

		try {
			int pollCount = 0;
			long startTime = System.currentTimeMillis();

			while (true) {
				KillApplicationResponse response = applicationsManager
						.forceKillApplication(request);
				if (response.getIsKillCompleted()) {
					LOGGER.info("Killed application " + applicationId);
					break;
				}

				long elapsedMillis = System.currentTimeMillis() - startTime;
				if (enforceAsyncAPITimeout()
						&& elapsedMillis >= this.asyncApiPollTimeoutMillis) {
					throw new YarnException(
							"Timed out while waiting for application "
									+ applicationId + " to be killed.");
				}

				if (++pollCount % 10 == 0) {
					LOGGER.info("Waiting for application " + applicationId
							+ " to be killed.");
				}
				Thread.sleep(asyncApiPollIntervalMillis);
			}
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while waiting for application "
					+ applicationId + " to be killed.");
		}
	}
	
	
	public static void main(String args[]) throws Exception{
		YarnClient yarnClient = new YarnClient();
		
		List<QueueInfo> queues = yarnClient.getAllQueues();
		System.out.println(queues.get(0));
		
		List<ApplicationReport> applicationReports = yarnClient.getApplications();
		for(ApplicationReport report: applicationReports){
			System.out.println(report.getUser()+", "+report.getStartTime());
		}
		
		
		boolean isFileOnHDFS = false;
		boolean isFile = true;
		LocalResourceVisibility visibility = LocalResourceVisibility.APPLICATION;
		String jarPathString = "/home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.4.1.jar";
		String classPathAppend = null;
//		String command = "bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.4.1.jar grep input output 'dfs[a-z.]+'";
		String command = "bin/top";
		String appName = "ShellEveningDate";
		int numOfCopies = 2;
		yarnClient.createAndSubmitApplication(appName, isFileOnHDFS, isFile, visibility, jarPathString, classPathAppend, command, numOfCopies);
	}

}
