package org.jumbune.web.services;

import static org.jumbune.web.utils.WebConstants.CLUSTER_NAME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.profiling.beans.ClusterInfo;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.PerformanceStats;
import org.jumbune.profiling.hprof.NodePerformance;
import org.jumbune.profiling.service.ClusterViewServiceImpl;
import org.jumbune.profiling.utils.ClusterMonitoringCategories;
import org.jumbune.profiling.utils.HTFProfilingException;
import org.jumbune.profiling.utils.ProfilerConstants;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.profiling.utils.ProfilerUtil;
import org.jumbune.profiling.yarn.beans.YarnCategoryInfo;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.AlertAction;
import org.jumbune.utils.conf.beans.AlertActionConfiguration;
import org.jumbune.utils.conf.beans.AlertConfiguration;
import org.jumbune.utils.conf.beans.AlertType;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.conf.beans.ProcessType;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.utils.yarn.communicators.MRCommunicator;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.jumbune.clusterprofiling.SchedulerService;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.recommendations.RecommendationAlerts;
import org.jumbune.clusterprofiling.recommendations.Recommendations;
import org.jumbune.clusterprofiling.recommendations.RecommendationsAlertImpl;
import org.jumbune.clusterprofiling.service.ClusterProfilingService;
import org.jumbune.clusterprofiling.yarn.beans.CapacitySchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.ClusterMetrics;
import org.jumbune.clusterprofiling.yarn.beans.FairSchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.Scheduler;
import org.jumbune.clusterprofiling.yarn.AlertGenerator;
import org.jumbune.common.alerts.HAAlert;
import org.jumbune.common.alerts.YarnAlert;
import org.jumbune.common.beans.Alert;
import org.jumbune.common.beans.ApplicationType;
import org.jumbune.common.beans.EffCapUtilizationStats;
import org.jumbune.common.beans.cluster.ClusterCache;
import org.jumbune.common.beans.cluster.EnterpriseCluster;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.clusterprofiling.yarn.MajorCounters;
import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.influxdb.InfluxDataReader;
import org.jumbune.common.influxdb.InfluxDataWriter;
import org.jumbune.common.influxdb.beans.Query;
import org.jumbune.common.influxdb.beans.ResultSet;
import org.jumbune.common.influxdb.beans.ResultSet.Result.Series;
import org.jumbune.common.integration.notification.AlertMailSender;
import org.jumbune.common.integration.notification.AlertNotifier;
import org.jumbune.common.integration.notification.TicketingImpl;
import org.jumbune.common.integration.notification.TrapSender;
import org.jumbune.clusterprofiling.yarn.ClusterAnalysisMetrics;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.JMXUtility;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.clusterprofiling.yarn.QueueAlert;
import org.jumbune.web.beans.AppFinishTimeComparator;
import org.jumbune.web.beans.DataLoad;
import org.jumbune.web.beans.Graph;
import org.jumbune.web.beans.Graphs;
import org.jumbune.web.beans.NodeSpecificSetting;
import org.jumbune.web.process.BackgroundProcessManager;
import org.jumbune.web.utils.AnalyzeClusterExcelExport;
import org.jumbune.web.utils.FairSchedularEnabledCache;
import org.jumbune.web.utils.SessionUtils;
import org.jumbune.web.utils.StatsManager;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.YarnQueuesUtils;

/**
 * The Class ClusterAnalysisService.
 */
@Path(WebConstants.CLUSTER_ANALYSIS_SERVICE_URL)
public class ClusterAnalysisService{

	private List<Alert> saveAlerts;
	
	private YarnQueuesUtils yarnQueuesUtils;
	
	private SchedulerService schedulerService;
	
	private BackgroundProcessManager processesManager;

	private ClusterAnalysisMetrics metrics;
	
	private RecommendationAlerts recommendationAlerts;
	
	private SessionUtils sessionUtils;
	
	private AlertGenerator alertGenerator;
	
	private YarnAlert yarnAlert;
	
	private AppFinishTimeComparator appsComparator;
	
	private static JsonParser jsonParser;
	
	private static Type hmssType;

	/**
	 * It saves the most used Cluster in cache so that we don't have to read the
	 * cluster file again and again. It is used by a lot of classes throughout
	 * Jumbune web module. It contains the latest updated Cluster Object
	 * 
	 * Key = Cluster Name
	 * value = Cluster Object
	 */
	public static ClusterCache cache = new ClusterCache(3);
	
	/**
	 * FairSchedularEnabledCache checks whether fair scheduler is enable on a particular cluster or not
	 * key = clusterName
	 * value = boolean 
	 */
	public static FairSchedularEnabledCache fairSchedularCache;
	
	private static Set<String> appTypes;

	@Context
	private HttpServletRequest servletRequest;
	
	private Subject subject = null;
	private boolean isSubjectInitialized = false;
	
	private static final Logger LOGGER;
	
	static {
		fairSchedularCache = new FairSchedularEnabledCache(3);
		jsonParser = new JsonParser();
		appTypes = new HashSet<>(1);
		appTypes.add("MAPREDUCE");
		hmssType = new TypeToken<Map<String, YarnCategoryInfo>>() {
		}.getType();
		LOGGER = LogManager.getLogger(ClusterAnalysisService.class);
	}

	public ClusterAnalysisService() {
		alertGenerator = AlertGenerator.getInstance();
		appsComparator = AppFinishTimeComparator.getInstance();
		schedulerService = SchedulerService.getInstance();
		metrics = ClusterAnalysisMetrics.getInstance();
		processesManager = BackgroundProcessManager.getInstance();
		recommendationAlerts = RecommendationsAlertImpl.getInstance();
		saveAlerts = new ArrayList<>();
		sessionUtils = SessionUtils.getInstance();
		yarnAlert = QueueAlert.getInstance();
		yarnQueuesUtils = YarnQueuesUtils.getInstance();
	}
	
	private HttpSession getSession() {
		return servletRequest.getSession();
	}
	
	public static void updateClusterCache(String clusterName, EnterpriseCluster cluster) {
		cache.put(clusterName, cluster);
	}

	@POST
	@Path(WebConstants.INIT_CLUSTER + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response initCluster(@PathParam(CLUSTER_NAME) String clusterName) {
		Cluster cluster = null;
		try {
			cluster = cache.getCluster(clusterName);
		} catch (IOException e) {
			LOGGER.error("Unable to get Cluster object [" + clusterName +"]", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		if (cluster.isJmxPluginEnabled()) {
			new JMXUtility()
					.establishConnectionToJmxAgent((EnterpriseClusterDefinition) cluster);
		}
		try {
			AdminConfigurationService.checkAndCreateConfAndInfluxDatabase(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to create influx database", e);
		}
		return Response.ok(Constants.gson.toJson(true)).build();
		
		
	}

	@POST
	@Path(WebConstants.LICENCE + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkNodesAndCluster(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			return Response.ok(Constants.gson.toJson(true)).build();
		} catch (Exception e) {
			LOGGER.error("Error while checking nodes and cluster", e);
			return Response.ok(Constants.gson.toJson(false)).build();
		}
	}
	
	@GET
	@Path("/clusters-list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClustersList() {
		Set<String> clustersList = null;
			clustersList = new HashSet<String>();
			File clusterJsonFile = new File(getClusterJsonDir());
			for (File file : clusterJsonFile.listFiles()) {
				if(file.getName().endsWith(WebConstants.JSON_EXTENSION)){
					clustersList.add(file.getName().replace(WebConstants.JSON_EXTENSION, ""));
				}
			}
		
		Map<String, String> map = new HashMap<String, String>(clustersList.size());
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		for (String clusterName : clustersList) {
			map.put(clusterName, hadoopDistribution);
		}
		return Response.ok(Constants.gson.toJson(map)).build();
	}

	private String getClusterJsonDir() {
		String clusterJsonDir = System.getenv(WebConstants.JUMBUNE_HOME)
				+ WebConstants.CLUSTER_DIR;
		File jsonDirectory = new File(clusterJsonDir);
		if (!jsonDirectory.exists()) {
			jsonDirectory.mkdir();
		}
		return jsonDirectory.getAbsolutePath();
	}
	
	@GET
	@Path(WebConstants.IS_INFLUXDB_LIVE + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isInfluxdbLive(@PathParam(WebConstants.CLUSTER_NAME_PARAM) final String clusterName) {
		Map<String, Boolean> map = new HashMap<String, Boolean>(1);
		try {
			map.put("isAlive", InfluxDBUtil.isInfluxdbLive(
					AdminConfigurationUtil.getInfluxdbConfiguration(clusterName)));
		} catch (Exception e) {
			LOGGER.error("Unable to check whether influxdb is alive or not", e);
			map.put("isAlive", false);
		}
		return Response.ok(Constants.gson.toJson(map)).build();
	}

	/**
	 * Gets the resource over usage. This method returns any over usage of
	 * resources(vcores and memory) by yarn applications. Criterion for
	 * declaring a resource over used is the threshold(memory and vcores
	 * threshold) provided by the user. <br>
	 * It returns such application IDs along with their respective resource
	 * consumptions.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param memoryThresholdMB
	 *            the memory threshold mb
	 * @param vcoresThreshold
	 *            the vcores threshold
	 * @return the resource over usage
	 */
	@GET
	@Path(WebConstants.RESOURCE_OVER_USAGE_URL)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResourceOverUsage(
			@PathParam(WebConstants.CLUSTER_NAME_PARAM) final String clusterName,
			@QueryParam("memoryThresholdMB") int memoryThresholdMB,
			@QueryParam("vcoresThreshold") int vcoresThreshold) {
		Map<String, Object> response = new HashMap<>(1);
		try {
			response.put(WebConstants.RESOURCE_OVER_USAGE_KEY,
					metrics.getResourceOverUsage(memoryThresholdMB, vcoresThreshold,
							sessionUtils.getRM(cache.getCluster(clusterName), getSession())));
			return Response.ok(Constants.gson.toJson(response)).build();
		} catch (Exception e) {
			LOGGER.error("Error occured while fetching resource usage stats", e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path(WebConstants.SLA_APPS + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSlaApps(@PathParam(WebConstants.CLUSTER_NAME_PARAM) final String clusterName) {
		try {
			return Response.ok(Constants.gson.toJson(metrics.getSlaApps(cache.getCluster(clusterName),
					sessionUtils.getRM(cache.getCluster(clusterName), getSession())))).build();
		} catch (Exception e) {
			LOGGER.error("Error occured while fetching resource usage stats", e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the long running yarn applications. This method returns IDs of all
	 * the yarn applications which have been running for a long period of time
	 * depending upon the threshold time period given by the user.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @param thresholdMillis
	 *            the threshold millis
	 * @return the long running apps
	 */
	@GET
	@Path(WebConstants.LONG_RUNNING_APPS_URL)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLongRunningApps(
			@PathParam(WebConstants.CLUSTER_NAME_PARAM) final String clusterName,
			@QueryParam("thresholdMillis") long thresholdMillis) {
		Map<String, Object> response = new HashMap<>(1);
		try {
			response.put(WebConstants.LONG_RUNNING_APPS_KEY,
					metrics.getLongRunningApplications(thresholdMillis, cache.getCluster(clusterName),
							sessionUtils.getRM(cache.getCluster(clusterName), getSession())));
			return Response.ok(Constants.gson.toJson(response)).build();
		} catch (Exception e) {
			LOGGER.error("Unalbe to get long running applications", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the cluster nodes.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the cluster nodes
	 */
	@GET
	@Path(WebConstants.CLUSTER_NODES + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterNodes(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			String clusterNodes = Constants.gson.toJson(JobRequestUtil.getClusterNodes(clusterName));
			return Response.ok(clusterNodes).build();
		} catch (IOException e) {
			LOGGER.error("Unable to get cluster nodes", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the cluster nodes.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the cluster nodes
	 */
	@GET
	@Path("/clusternodeswithlabel/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterNodesWithLabel(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			String clusterNodes = Constants.gson.toJson(JobRequestUtil.getClusterNodesWithLabel(clusterName));
			return Response.ok(clusterNodes).build();
		} catch (IOException e) {
			LOGGER.error("Unable to get cluster nodes", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the dataload and distribution.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the dataload and distribution
	 */
	@GET
	@Path(WebConstants.DATA_LOAD_AND_DISTRIBUTION + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataloadAndDistribution(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			List<NodeInfo> list = null ;
			String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
			// fetching data load distribution command based in case of mapr and jmx based in case of rest
			if(Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
				ClusterViewServiceImpl cvsi = new ClusterViewServiceImpl(cache.getCluster(clusterName));
				list = cvsi.getDataLoadAndDistributionDetails();
			}else{
				list = fetchDataLoadInformation(cache.getCluster(clusterName));
			}
			return Response.ok(Constants.gson.toJson(getModifiedDataLoadAndDistribution(list))).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get data load and distribution", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path(WebConstants.DATA_LOAD_AND_DISTRIBUTION + "details/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataloadAndDistributionDetails(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			List<NodeInfo> list = null ;
			String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
			// fetching data load distribution command based in case of mapr and jmx based in case of rest
			if(Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
				ClusterViewServiceImpl cvsi = new ClusterViewServiceImpl(cache.getCluster(clusterName));
				list = cvsi.getDataLoadAndDistributionDetails();
			}else{
				list = fetchDataLoadInformation(cache.getCluster(clusterName));
			}
			return Response.ok(Constants.gson.toJson(list)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get data load and distribution details", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private List<NodeInfo> fetchDataLoadInformation(Cluster cluster) throws Exception {
		Map<String, String> nameNodeStats = null;
			nameNodeStats = new ProfilerJMXDump()
						.getAllJMXStats(
								JMXDeamons.NAME_NODE, 
								cluster.getNameNode(),
								cluster.getNameNodes().getNameNodeJmxPort(),
								cluster.isJmxPluginEnabled());


		String liveNodesJson = nameNodeStats.get(Constants.NAME_NODE_INFO_LIVE_NODES);
		long totalDfsUsed = 0, localDfsUsed;
		int totalLiveNodes = 0;
		for (Entry<String, JsonElement> e : jsonParser.parse(liveNodesJson).getAsJsonObject().entrySet()) {
			totalDfsUsed += e.getValue().getAsJsonObject().get(Constants.USED_SPACE).getAsLong();
			totalLiveNodes++;
		}
		
		List<String> workersHosts = cluster.getWorkers().getHosts();
		if (totalLiveNodes == 0) {
			totalLiveNodes = workersHosts.size();
		}
		
		NodeInfo node;
		
		List<NodeInfo> list = new ArrayList<NodeInfo>(workersHosts.size());
		
		double dataLoadPercent, differencePercent, idealDataLoadPercent = 100 / totalLiveNodes;
		
		JsonObject obj;
		for (Entry<String, JsonElement> e : jsonParser.parse(liveNodesJson).getAsJsonObject().entrySet()) {
			obj = e.getValue().getAsJsonObject();
			localDfsUsed = obj.get(Constants.USED_SPACE).getAsLong();
			node = new NodeInfo();
			node.setNodeIp(obj.get(Constants.XFERADDR).getAsString().split(Constants.COLON)[0]);
			if (totalDfsUsed == 0) {
				node.setDataLoadStats("0.0");
				node.setPerformance(NodePerformance.Good);
			} else {
				dataLoadPercent = (localDfsUsed * ProfilerConstants.HUNDRED  * 1.0 ) / totalDfsUsed;
				differencePercent = dataLoadPercent - idealDataLoadPercent;
				dataLoadPercent = ProfilerUtil.roundTwoDecimals(dataLoadPercent);
				node.setDataLoadStats(String.valueOf(dataLoadPercent));
				
				if (Math.abs(differencePercent) <= 10.0) {
					node.setPerformance(NodePerformance.Good);
				} else if (Math.abs(differencePercent) <= 20.0) {
					node.setPerformance(NodePerformance.Warn);
				} else {
					node.setPerformance(NodePerformance.Bad);
				}
			}
			list.add(node);
		}
		
		// Fetching dead nodes
		String deadNodesJson = nameNodeStats.get(Constants.NAME_NODE_INFO_DEAD_NODES);
		for (Entry<String, JsonElement> e : jsonParser.parse(deadNodesJson).getAsJsonObject().entrySet()) {
			node = new NodeInfo();
			node.setNodeIp(e.getValue().getAsJsonObject().get(Constants.XFERADDR).getAsString().split(Constants.COLON)[0]);
			node.setDataLoadStats(WebConstants.ZERO);
			node.setPerformance(NodePerformance.Unavailable);
			list.add(node);
		}
		
		return list;
	}

	/**
	 * Gets the alerts.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the alerts
	 */
	@GET
	@Path(WebConstants.ALERTS + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlerts(@PathParam(CLUSTER_NAME) String clusterName,
			@QueryParam("lastCheckpoint") @DefaultValue("0") long lastCheckpoint) {
		
		long newTime = System.currentTimeMillis();
		long oldTime = lastCheckpoint == 0 ? newTime - 600000 : lastCheckpoint;
		
		try {
			Cluster cluster = cache.getCluster(clusterName);
			AlertConfiguration alertConf = AdminConfigurationUtil.getAlertConfiguration(clusterName); 
			Map<AlertType, Boolean> nonConfigurableAlerts = alertConf.getNonConfigurableAlerts();

			List<Alert> alertsList = new ArrayList<>();

			String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
			Alert namenodeDownAlert = null;
			if(!Constants.MAPR.equalsIgnoreCase(hadoopDistribution) && !Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
				namenodeDownAlert = alertGenerator.getNameNodeDownAlert(cluster);
				if (namenodeDownAlert != null) {
					alertsList.add(namenodeDownAlert);
				} else {
					//configurable UNDER_REPLICATED_BLOCKS
					alertsList.addAll(alertGenerator.getUnderReplicatedBlockAlert(cluster));
					//configurable HDFS_UTILIZATION
					alertsList.addAll(alertGenerator.getHDFSSpaceUsageAlert(cluster));
					if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
						//non-configurable HADOOP_DAEMON_DOWN
						LOGGER.debug("Checking for Node Down Alert");
						alertsList.addAll(alertGenerator.getNodeDownAlert(cluster));
					}
				}
				if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {				
					//non-configurable HADOOP_DAEMON_DOWN
					alertsList.addAll(alertGenerator.getDataNodeDownAlert(cluster));
				}
				//configurable DISK_SPACE_UTILIZATION
				alertsList.addAll(alertGenerator.getDiskSpaceUsageAlert(cluster));
				
				//non-configurable DN_VOLUME_FAILURE_CHECK
				if(nonConfigurableAlerts.get(AlertType.DN_VOLUME_FAILURE_CHECK)) {
					alertsList.addAll(alertGenerator.getDataNodeVolumeFailureAlert(cluster));
				}
			}
			
			//Added alert for max files  in a hdfs directory
			AlertConfiguration alertConfiguration = AdminConfigurationUtil.getAlertConfiguration(clusterName);
			if(!alertConfiguration.getHdfsDirPaths().isEmpty()){
			alertsList.addAll(alertGenerator.getHDFSMaxFilesInDirAlert(cluster, alertConfiguration.getHdfsDirPaths()));
			}
			// fragmented files alert added for non-secured cluster and non-mapr cluster only
			if(!Constants.MAPR.equalsIgnoreCase(hadoopDistribution) && !Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
				alertsList.addAll(alertGenerator.getFragmenedFilesAlert(cluster));
			}
			Alert resourceManagerDownAlert = null;
			Alert historyServerDownAlert = null;
			if(nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
			//non-configurable HADOOP_DAEMON_DOWN : Check History server
			
				historyServerDownAlert = yarnAlert.getHistoryServerDownAlert(cluster);
				if(historyServerDownAlert != null){
					alertsList.add(historyServerDownAlert);
				}
				//non-configurable HADOOP_DAEMON_DOWN : Check Resource Manager
				resourceManagerDownAlert = yarnAlert.getResourceManagerDownAlert(cluster);
			}

			if (resourceManagerDownAlert != null) {
				alertsList.add(resourceManagerDownAlert);
			} else {
				if(nonConfigurableAlerts.get(AlertType.CLUSTER_TIME_DESYNC)) {
					//non-configurable CLUSTER_TIME_DESYNC
					alertsList.addAll(yarnAlert.getClusterTimeDesyncAlert(clusterName));
				}
				RMCommunicator rmCommunicator = sessionUtils.getRM(cluster, getSession());
				//configurable QUEUE_UTILIZATION
				alertsList.addAll(yarnAlert.getQueueUtilisationAlert(clusterName,
						rmCommunicator));

				if(nonConfigurableAlerts.get(AlertType.QUEUE_CHILD_CAPACITY_OVERFLOW)) {
					//non-configurable QUEUE_CHILD_CAPACITY_OVERFLOW
					alertsList.addAll(yarnAlert.getChildCapacityAlert(
							rmCommunicator));
				}
				if(nonConfigurableAlerts.get(AlertType.MAP_REDUCE_APP_FAILURE)) {
					//non-configurable MAP_REDUCE_APP_FAILURE 
					alertsList.addAll(yarnAlert.getApplicationFailedAlert(
							rmCommunicator, oldTime, newTime));
				}
				if(nonConfigurableAlerts.get(AlertType.CONTAINER_POOL_UTILIZATION)) {
					//non-configurable CONTAINER_POOL_UTILIZATION
					alertsList.addAll(yarnAlert.getContainerUtilizationAlert(cluster, 
							rmCommunicator));
				}
				//non-configurable 
				alertsList.addAll(yarnAlert.getEffectiveUtlilzationAlert(
						cluster.getClusterName()));

				if(nonConfigurableAlerts.get(AlertType.YARN_PROPERTY_CHECK)) {
					//non-configurable YARN_PROPERTY_CHECK
					alertsList.addAll(yarnAlert.checkYarnPropertySetCorrectly(cluster));
				}
				if(nonConfigurableAlerts.get(AlertType.NODE_UNHEALTHY)) {
					//non-configurable NODE_UNHEALTHY
					alertsList.addAll(yarnAlert.getNodeUnhealthyAlert(
							cluster, rmCommunicator));
				}
			}
			List<Alert> nmDownAlerts = null;
			if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
				//non-configurable HADOOP_DAEMON_DOWN : Check Node Managers
				RMCommunicator rmCommunicator = sessionUtils.getRM(cluster, getSession());
				nmDownAlerts = yarnAlert.getNodeManagerDownAlert(cluster,rmCommunicator);
				alertsList.addAll(nmDownAlerts);
			}
			if( nonConfigurableAlerts.get(AlertType.RESOURCE_UTILIZATION_CHECK)
					&& nmDownAlerts.size() != cluster.getWorkers().getHosts().size()) {
				//non-configurable RESOURCE_UTILIZATION_CHECK
				alertsList.addAll(yarnAlert.getResourceUtilizationAlert(cluster, nmDownAlerts));
			}				
			

			//Sending alert email and SNMP Traps
			sendNotifications(alertsList, clusterName);
			Map<String, Object> map = new HashMap<String, Object>(2);
			map.put("alerts", alertsList);
			map.put("lastCheckpoint", newTime);
			return Response.ok(Constants.gson.toJson(map)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get alerts", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	/**
	 * Gets the recommendations for the cluster.
	 *
	 * @param clusterName the cluster name
	 * @return the recommendations
	 */
	@GET
	@Path(WebConstants.RECOMMENDATIONS + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@PathParam(CLUSTER_NAME) String clusterName) {
		Set<Recommendations> recommendationsSet = null ;
		try{		
			Cluster cluster = cache.getCluster(clusterName);
			recommendationsSet = new HashSet<Recommendations>();
			
			recommendationsSet.addAll(recommendationAlerts.checkMemoryConfiguration(cluster));
			recommendationsSet.addAll(recommendationAlerts.checkYarnProperty(cluster));
			recommendationsSet.addAll(recommendationAlerts.getRecommendedContainerConfiguration(cluster));
			recommendationsSet.addAll(recommendationAlerts.checkTransparentHugePageStatus(cluster));
			recommendationsSet.addAll(recommendationAlerts.checkSELinuxStatus(cluster));
			recommendationsSet.addAll(recommendationAlerts.checkVMSwappinessParam(cluster));
			// spark recommendations
			recommendationsSet.addAll(recommendationAlerts.getSparkConfigurations(cluster,
					fairSchedularCache.isFairScheduler(cluster), schedulerService,
					sessionUtils.getRM(cluster, getSession())));
			
			return Response.ok(Constants.gson.toJson(recommendationsSet)).build();
		} catch (Exception e) {
			if(recommendationsSet != null && recommendationsSet.size() > 0){
				LOGGER.warn("Got" + recommendationsSet.size() + "recommendations of many");
				return Response.ok(Constants.gson.toJson(recommendationsSet)).build();
			}else{
				LOGGER.error("Unable to get recommendations", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
			}
	}

	/**
	 * Send email and trap for alerts according to the alertActionConfiguration
	 * configurations
	 *
	 * @param alertList
	 *            the alert list
	 * @param clusterName
	 */
	private void sendNotifications(List<Alert> alertList, String clusterName) {

		List<AlertAction> alertActions = null;
		AlertActionConfiguration alertActionConfiguration = null;
		AlertNotifier mailNotifier=new AlertMailSender();
		AlertNotifier trapNotifier=new TrapSender();
		AlertNotifier ticketNotifier = null;
		List<Alert> notificationList=null;
		List<Alert> notificationReminderList=null;
		try {
			alertActionConfiguration = AdminConfigurationUtil.getAlertActionConfiguration(clusterName);
			alertActions = alertActionConfiguration.getAlertActions();
			if (alertList.isEmpty()) {
				saveAlerts.clear();
			} else {
				for (AlertAction alertAction : alertActions) {
					notificationList=new ArrayList<>();
					notificationReminderList=new ArrayList<>();
					if (alertAction.getOccuringSinceHours() == 0) {
						for (Alert alert : alertList) {
							if (!(saveAlerts.contains(alert))
									&& (alertAction.getAlertLevel().getLevel().equals(alert.getLevel()))) {
								notificationList.add(alert);					
							}
						}
					} else {
						notificationReminderList=checkReminderAlertEmailAndTrap(alertAction, alertList);
					}
					
					if (notificationList!=null && !notificationList.isEmpty()) {
						mailNotifier.sendNotification(notificationList, alertAction, clusterName);
						trapNotifier.sendNotification(notificationList, alertAction, clusterName);
						if (alertAction.isEnableTicket()) {
							ticketNotifier =  new TicketingImpl(AdminConfigurationUtil.getTicketConfiguration(clusterName));
							ticketNotifier.sendNotification(notificationList, alertAction, clusterName);
						}
					}
					
					if (notificationReminderList!=null && !notificationReminderList.isEmpty()) {
						mailNotifier.sendNotification(notificationReminderList, alertAction, clusterName);
						trapNotifier.sendNotification(notificationReminderList, alertAction, clusterName);
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error while sending Alert Notifications - " + e.getMessage());
		}
		// adding new alerts to save Alerts List
		for (Alert alert : alertList) {
			if (!(saveAlerts.contains((alert)))) {
				saveAlerts.add(alert);
			}
		}
	}

	private List<Alert> checkReminderAlertEmailAndTrap(AlertAction alertAction, List<Alert> alertList) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		Iterator<Alert> saveAlertItr;
		List<Alert> notificationList=new ArrayList<>();
			saveAlertItr = saveAlerts.iterator();
			long occurringTimeHours = alertAction.getOccuringSinceHours().longValue();	
				while (saveAlertItr.hasNext()) {
					try {
						Alert saveAlert = saveAlertItr.next();
						if (!(alertList.contains(saveAlert))) {
							saveAlertItr.remove();
						} else {
							Date alertDate = formatter.parse(saveAlert.getDate());
							long diff = (new Date()).getTime() - (alertDate.getTime());
							long hoursDiff = diff / (60 * 60 * 1000) % 24;
							if ((occurringTimeHours == hoursDiff) && (alertAction.getAlertLevel().getLevel().equals(saveAlert.getLevel()))) {							
								saveAlert.setDate(formatter.format(new Date()));
								notificationList.add(saveAlert);
							}
						}
					}
			 catch (ParseException e) {
						LOGGER.error("Error while sending Reminder Alert Notifications - " + e.getMessage());
					}
				}	
			return notificationList;
	}

	/**
	 * Gets the high availability alert.
	 *
	 * @return the high availability alert
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private HAAlert getHighAvailabilityAlert() {
		HAAlert hAAlert = null;
		Class clazz = null;
		Constructor cons = null;
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		if (hadoopType.equalsIgnoreCase(ExtendedConstants.YARN)) {
			try {
				clazz = Class.forName(WebConstants.YARN_HIGH_AVAILABLITY_ALERT);
				cons = clazz.getConstructor();
				hAAlert = (HAAlert) cons.newInstance();
			} catch (ReflectiveOperationException e) {
				LOGGER.error(
						JumbuneRuntimeException.throwClassNotFoundException(e.getStackTrace()));
			}
		}
		return hAAlert;
	}

	/**
	 * Gets the update interval.
	 *
	 * @return the update interval
	 */
	@GET
	@Path(WebConstants.ALERTS_UPDATE_INTERVAL + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUpdateInterval(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			int updateInterval = AdminConfigurationUtil.getAlertConfiguration(clusterName).getUpdateInterval();	
			updateInterval = updateInterval > 20 ? updateInterval : 20;
			Map<String, Integer> interval = new HashMap<String, Integer>();
			interval.put("interval", updateInterval);
			return Response.ok(Constants.gson.toJson(interval)).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the job output.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @param jobID
	 *            the job id
	 * @return the job output
	 */
	@GET
	@Path(WebConstants.PROFILEJOB)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobOutput(@QueryParam(CLUSTER_NAME) String clusterName,
			@QueryParam("jobID") String jobID) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
			return Response.ok(Constants.gson.toJson(clusterProfilingService.getJobStats(cluster, jobID))).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get job output", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	/**
	 * Returns all categories of hadoop jmx exposed by hadoop cluster in form of
	 * json string.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the filtered categories
	 */
	@GET
	@Path(WebConstants.FILTERED_CATEGORIES + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilteredCategories(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterMonitoringCategories categories = new ClusterMonitoringCategories(cluster);
			return Response.ok(categories.getFilteredCategoriesJSON()).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get filtered categories list", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * This method return all categories of hadoop jmx exposed by hadoop cluster
	 * in form of json string.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the categories
	 */
	@GET
	@Path(WebConstants.CATEGORIES + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategories(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterMonitoringCategories categories = new ClusterMonitoringCategories(cluster);
			return Response.ok(categories.getFilteredCategoriesJSON()).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get categories", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(WebConstants.CATEGORIES + "/nodespecific")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeSpecificCategories(@QueryParam(CLUSTER_NAME) String clusterName,
			@QueryParam("nodeIP") String nodeIP) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterMonitoringCategories categories = new ClusterMonitoringCategories(cluster,
					nodeIP);
			return Response.ok(categories.getNodeSpecificCategories()).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get node specific categories", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path(WebConstants.DATA_CENTER_HEAT_MAP)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataCenterHeatMap(@FormParam(CLUSTER_NAME) String clusterName,
			@FormParam("colorConfigJSON") String colorConfigJSON) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterInfo dataCenterHeatMap = getHeatMap(cluster, colorConfigJSON,
					"CLUSTER_VIEW");
			return Response.ok(Constants.gson.toJson(dataCenterHeatMap)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get data center heat map", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path(WebConstants.DATA_CENTER_HEAT_MAP + "/preview/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataCenterHeatMapPreview(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterInfo dataCenterHeatMap = getHeatMap(cluster, null, "PRE_CLUSTER_VIEW");
			return Response.ok(Constants.gson.toJson(dataCenterHeatMap)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get data center heat map preview", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/queuestats" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQueueStats(@PathParam(CLUSTER_NAME) String clusterName) {
			
			Cluster cluster = null;
			
			try {
				cluster = cache.getCluster(clusterName);
			} catch (Exception e) {
				LOGGER.error("Unable to get cluster", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}

			try {
				Scheduler scheduler = schedulerService.fetchSchedulerInfo(cluster);
				
				if (scheduler.isFairScheduler()) {
					List<FairSchedulerQueueInfo> list = scheduler.getFairSchedulerLeafQueues();
					if (!processesManager.getProcessesStatus(clusterName)
							.get(ProcessType.QUEUE_UTILIZATION)) {
						yarnQueuesUtils.persistFairSchedulerData(list, clusterName);
					}
					return Response.ok(Constants.gson.toJson(list)).build();
				} else {
					List<CapacitySchedulerQueueInfo> list = scheduler.getCapcitySchedulerLeafQueues();
					if (!processesManager.getProcessesStatus(clusterName)
							.get(ProcessType.QUEUE_UTILIZATION)) {
						yarnQueuesUtils.persistCapacitySchedulerData(list, clusterName);
					}
					return Response.ok(Constants.gson.toJson(list)).build();
				}
				
			} catch (Exception e) {
				LOGGER.error("Unable to fetch queue stats using Resource Manager", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
	}
	
	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/avgwaitingtime" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQueueAvgWaitingTime(@PathParam(CLUSTER_NAME) String clusterName) {
			
			Cluster cluster = null;
			
			try {
				cluster = cache.getCluster(clusterName);
			} catch (Exception e) {
				LOGGER.error("Unable to get cluster", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
			
			try {
					ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
					List<QueueStats> list = clusterProfilingService.getQueueStats(cluster);
					return Response.ok(Constants.gson.toJson(yarnQueuesUtils.getAverageWaitingTime(cluster, list))).build();
		
				} catch (Exception e) {
					LOGGER.error("Unable to fetch average waiting time", e);
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
	
	
	@GET
	@Path(WebConstants.IS_FAIR_SCHEDULER + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isFairSchedulerEnabled(
			@PathParam(WebConstants.CLUSTER_NAME_PARAM) final String clusterName) {
		Cluster cluster = null;
		try {
			cluster = cache.getCluster(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to get cluster", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		boolean isDominentResourceFairness = false;
		Map<String, Boolean> map = new HashMap<String, Boolean>(2);
		try {
			if (fairSchedularCache.isFairScheduler(cluster)) {
				map.put("isFairScheduler", true);
				List<FairSchedulerQueueInfo> list = schedulerService.getFairSchedulerLeafQueues(
						cluster);
				
				for (FairSchedulerQueueInfo queueInfo : list) {
					if (queueInfo.getSchedulingPolicy().equalsIgnoreCase(WebConstants.DRF)) {
						isDominentResourceFairness = true;
						break;
					}
				}
			} else {
				map.put("isFairScheduler", false);
			}
			map.put("isDrf", isDominentResourceFairness);
			
			return Response.ok(Constants.gson.toJson(map)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get queues details from api", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/rackAwareStats" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRackAwareStats(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
			return Response.ok(Constants.gson.toJson(clusterProfilingService.getRackAwareStats(cluster)))
					.build();

		} catch (Exception e) {
			LOGGER.error("Unable to get rack aware stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private Integer getInt(String str) {
		if (str == null) {
			return null;
		}
		return Integer.parseInt(str);
	}

	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/capacity-utilization-latest-jobs" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEffCapUtilizationLatestJobs(
			@PathParam(CLUSTER_NAME) String clusterName, @QueryParam(WebConstants.LAST_CHECKPOINT) long lastCheckpoint) {
		try {
			Map<String, Object> map = new HashMap<>(2);
			long currentTime = System.currentTimeMillis();
			map.put(WebConstants.LAST_CHECKPOINT, currentTime);
			
			if (lastCheckpoint == 0) {
				map.put(WebConstants.JOBS_LIST, Collections.EMPTY_LIST);
				
			} else {
				Cluster cluster = cache.getCluster(clusterName);
				List<ApplicationReport> list = sessionUtils.getRM(cluster, getSession()).getApplications(
						null, null, null, appTypes, null, EnumSet.of(YarnApplicationState.FINISHED), lastCheckpoint, currentTime, null);
				
				map.put(WebConstants.JOBS_LIST, getJobsCapacityUtilization(cluster, list, true));
			}
			
			return Response.ok(Constants.gson.toJson(map)).build();

		} catch (Exception e) {
			LOGGER.error("Unable to get Effective capacity utilization stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/capacity-utilization-old-jobs" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEffCapacityUtilizationOldJobs(
			@PathParam(CLUSTER_NAME) String clusterName, @QueryParam(WebConstants.START_TIME) long startTime,
			@QueryParam(WebConstants.END_TIME) long endTime) {
		try {
			Map<String, Object> map = new HashMap<>(3);
			
			if (startTime == 0 && endTime == 0) {
				endTime = System.currentTimeMillis();
				startTime = endTime - 3600000;
			}
			
			long oneYearBefore = System.currentTimeMillis() - 31536000000L;
			
			long listSize = 0;
			
			Cluster cluster = cache.getCluster(clusterName);
			List<ApplicationReport> list = null;
			
			while (listSize == 0 && endTime > oneYearBefore) {
				LOGGER.debug("Getting Jobs between [" + new Date(startTime) + "] and [" + new Date(endTime) + "]");
				list = sessionUtils.getRM(cluster, getSession()).getApplications(
						null, null, null, appTypes, null, EnumSet.of(
								YarnApplicationState.FINISHED), startTime, endTime, null);
				listSize = list.size();
				
				if (listSize == 0) {
					LOGGER.debug("No Jobs Found between [" + new Date(startTime) + "] and [" + new Date(endTime) + "], looking further back..");
					long diff = endTime - startTime;
					endTime = startTime;
					startTime = startTime - 2 * diff;
				}
			}
			
			Collections.sort(list, appsComparator);
			
			if (listSize > 5) {
				list.subList(5, list.size()).clear();
				startTime = list.get(4).getFinishTime();
			}
			
			map.put(WebConstants.START_TIME, startTime);
			map.put(WebConstants.END_TIME, endTime);
			map.put(WebConstants.JOBS_LIST,  getJobsCapacityUtilization(cluster, list, false));
			
			
			return Response.ok(Constants.gson.toJson(map)).build();

		} catch (Exception e) {
			LOGGER.error("Unable to get Effective capacity utilization stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private List<EffCapUtilizationStats> getJobsCapacityUtilization(
			Cluster cluster, List<ApplicationReport> list, boolean toWrite) throws Exception {
		ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
		List<EffCapUtilizationStats> statsList = clusterProfilingService.getEffCapUtilizationStats(cluster, list);
		
		try {
			persistJobDataToInfluxdb(statsList, cluster.getClusterName());
			if (toWrite) {
				persistRunningAppsDataToInfluxdb(cluster);
			}
		} catch(Exception e) {
			LOGGER.error("Error Occured while persisting job stats to influx", e);
		}
		
		Map<String, Map<String, String>> jobHistoryDetails = null;
		
		try {
			jobHistoryDetails = getJobHistoryDetailsData(cluster);
			if (jobHistoryDetails != null) {
				Map<String, String> temp;
				for (EffCapUtilizationStats stats : statsList) {
					temp = jobHistoryDetails.get(stats.getJobId());
					if (temp != null) {
						stats.setUsedVCores(getInt(temp.get(WebConstants.USED_V_CORES)));
						stats.setUsedContainers(getInt(temp.get(WebConstants.USED_CONTAINERS)));
					}
				}
			}
		} catch(Exception e) {
			LOGGER.error("Error Occured while fetching job stats to influx", e);
		}
		return statsList;
	}

	/**
	 * It fetches used vcore and used containers for running applications
	 *  from hadoop (resource manager) and persists into influxdb
	 * @param cluster
	 * @throws Exception
	 */
	private void persistRunningAppsDataToInfluxdb(Cluster cluster) throws Exception {
		
		List<ApplicationReport> list = sessionUtils.getRM(cluster, getSession()).getRunningApplications();
		
		InfluxDataWriter writer = new InfluxDataWriter(
				AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName()));
		writer.setTableName(WebConstants.JOB_HISTORY_DETAILS_TABLE);
		writer.setTimeUnit(TimeUnit.SECONDS);
		
		ApplicationResourceUsageReport usage;
		
		for (ApplicationReport report: list) {
			usage = report.getApplicationResourceUsageReport();
			
			writer.addColumn(WebConstants.USED_V_CORES, usage.getUsedResources().getVirtualCores());
			
			writer.addColumn(WebConstants.USED_CONTAINERS, usage.getNumUsedContainers());
			
			writer.addTag(WebConstants.JOB_ID, report.getApplicationId()
					.toString().replace(WebConstants.APPLICATION, WebConstants.JOB));
			writer.writeData();
		}
	}
	
	/**
	 * It fetches job details (MAX(usedVCores) and MAX(usedContainers), each job) from influxdb 
	 * @param cluster
	 * @return map (key = job_id, value= {'usedVCores' : value1, 'usedContainers' : value2}
	 * @throws Exception
	 */
	private Map<String, Map<String, String>> getJobHistoryDetailsData(Cluster cluster) throws Exception {
		Query query = new Query();
		query.setTableName(WebConstants.JOB_HISTORY_DETAILS_TABLE);
		query.addColumn(WebConstants.USED_V_CORES);
		query.addColumn(WebConstants.USED_CONTAINERS);
		query.setAggregateFunction(WebConstants.MAX);
		query.addGroupByColumn(WebConstants.JOB_ID);
		
		InfluxDataReader reader = new InfluxDataReader(
				query, AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName()));
		ResultSet result = reader.getResult();
		List<Series> seriesList = result.getResults().get(0).getSeries();
		if (seriesList == null) {
			return null;
		}
		Map<String, Map<String, String>> map = new HashMap<>();
		Map<String, String> temp;
		List<String> values;
		for (Series series: seriesList) {
			temp = new HashMap<String, String>();
			values = series.getValues().get(0);
			temp.put(WebConstants.USED_V_CORES, values.get(1));
			temp.put(WebConstants.USED_CONTAINERS, values.get(2));
			map.put(series.getTags().get(WebConstants.JOB_ID), temp);
		}
		return map;
	}


	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/liveContainerStats" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLiveContainerStats(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
			Map<String, Object> map = new HashMap<String, Object>(4);
			map.put("launchableContainers", clusterProfilingService.getContainerStatus(cluster));
			RMCommunicator rmCommunicator = sessionUtils.getRM(cluster, getSession());
			map.put(WebConstants.RUNNING_CONTAINERS_KEY, metrics.getNumContainersRunning(rmCommunicator));
			map.put(WebConstants.RUNNING_APPS_KEY, metrics.getNumApplicationsRunning(rmCommunicator));
			map.put(WebConstants.RUNNING_JOBS_NAME, metrics.getRunningJobNameList(rmCommunicator));
			return Response.ok(Constants.gson.toJson(map)).build();

		} catch (Exception e) {
			LOGGER.error("Unable to get live container stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/copyHistoryFile" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response copyHistoryFileStatus(@PathParam(CLUSTER_NAME) String clusterName) {
		LOGGER.debug("Going to copy history file...");
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
			clusterProfilingService.copyJobHistoryFile(cluster);
			return Response.ok(Constants.gson.toJson(true)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to copy history files", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * To be called when SystemMetricsProcess /
	 * background process is not enabled.
	 * @param clusterName
	 * @param json
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	@POST
	@Path("/update-hadoop-metrics-stats")
	public Response updateHadoopMetricsStats(
			@FormParam(CLUSTER_NAME) String clusterName,
			@FormParam("json") String json)
				throws IOException, Exception {
		/*
		 * json = {
		 * "All Nodes" : {"systemStats": {"cpu": ["NumberOfCores", "CpuUsage"],"memory": ["TotalSwap", "FreeMemory"]}},
		 * "127.0.0.1" : {"workerJMXInfo": {"nodeManager": ["JvmMetrics.GcCount"], "dataNode": ["JvmMetrics.GcCount", "JvmMetrics.LogFatal"]}}
		 * }
		 */
		
		Cluster cluster = cache.getCluster(clusterName);
		Map<String, YarnCategoryInfo> map = Constants.gson.fromJson(json, hmssType);
		
		// All Nodes
		List<String> nodeIPs = JobRequestUtil.getClusterNodes(cluster);
		YarnCategoryInfo categoryInfo = map.get(WebConstants.ALL_NODES);
		for (String nodeIP : nodeIPs) {
			StatsManager.getInstance().submit(clusterName, nodeIP, categoryInfo);
		}
		
		// Node Specific
		for (Entry<String, YarnCategoryInfo> e : map.entrySet()) {
			if (! e.getKey().equals(WebConstants.ALL_NODES)) {
				StatsManager.getInstance().submit(clusterName, e.getKey(), e.getValue());
			}
		}
		return Response.ok("success").build();
	}
	
	/**
	 * To be called when SystemMetricsProcess / background process is enabled.
	 * When even user added or remove a chart in a tab [Hadoop Metrices and system stats]
	 *  in UI, this api will be called.  
	 * @param clusterName
	 * @param tabName tab name (eg. ALL NODES, 127.0.0.1, 172.26.49.1) in which stat is added/removed
	 * @param statName name of stat which is added/removed
	 * @param action action is "added" or "removed"
	 * @return
	 */
	@POST
	@Path("/update-hadoop-metrics-single-stat")
	public Response updateHadoopMetricsSingleStat(
			@FormParam("clusterName") String clusterName,
			@FormParam("tabName") String tabName,
			@FormParam("statName") String statName,
			@FormParam("action") String action) {
		
		try {
			Cluster cluster = cache.getCluster(clusterName);
			if (action.equalsIgnoreCase(WebConstants.ADD)) {
				StatsManager.getInstance().addStat(cluster, tabName, statName);
			} else {
				StatsManager.getInstance().removeStat(cluster, tabName, statName);
			}
			return Response.ok("success").build();
		} catch (Exception e) {
			LOGGER.error("Unable to update hadoop metrics stat", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * To be called when SystemMetricsProcess / background process is enabled.
	 * If the user closes a tab (in ui, Hadoop Metrices and system stats)
	 * then this api will be called. If a tab is closed then it means we 
	 * have to close all the stat inside that tab.
	 * 
	 * @param tabName can be "ALL NODES", "172.26.49.85", "172.26.49.12" etc.
	 * @return "success"
	 */
	@POST
	@Path("/update-hadoop-metrics-remove-tab")
	public Response updateHadoopMetricsRemoveTab(
			@FormParam("tabName") String tabName) {
		
		StatsManager.getInstance().removeTab(tabName);
		return Response.ok("success").build();
	}
	
	/**
	 * To be called when SystemMetricsProcess / background process is enabled.
	 * @param clusterName
	 * @return
	 */
	@GET
	@Path("/get-hadoop-metrics-latest-stats/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHadoopMetricsAndSystemLatestStats(@PathParam(CLUSTER_NAME) String clusterName) {

		try {
			return Response.ok(Constants.gson.toJson(StatsManager.getInstance().getOpenedStats(cache.getCluster(clusterName)))).build();
			/*
			 * json = {
			 * "ALL NODES" : {"systemStats": {"cpu": ["NumberOfCores", "CpuUsage"],"memory": ["TotalSwap", "FreeMemory"]}},
			 * "127.0.0.1" : {"workerJMXInfo": {"nodeManager": ["JvmMetrics.GcCount"], "dataNode": ["JvmMetrics.GcCount", "JvmMetrics.LogFatal"]}}
			 * }
			 */
		} catch (Exception e) {
			LOGGER.error("Unable to get hadoop metrics latest stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	

	/**
	 * 
	 * @param clusterName
	 * 
	 * @param currentOpenedTab tab (In hadoop system metrices ) that is opened in browser
	 * so the we don't have to send the data for every tab/node.
	 * Instead of that we will send the data for that node/tab which is opened.
	 * currentOpenedTab can be "ALL NODES", "172.26.49.85", "172.26.49.12" etc.
	 * 
	 * @param tabSettings
	 * @param selectedNodes selected nodes is used in case currentTab is "ALL NODES"
	 * @return
	 */
	@POST
	@Path("/get-hadoop-metrics-stats-data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHadoopMetricsAndSystemData(@FormParam(CLUSTER_NAME) String clusterName,
			@FormParam("currentOpenedTab") String currentOpenedTab,
			@FormParam("tabSettings") String selectedStats,
			@FormParam("selectedNodes") @DefaultValue("") String selectedNodesJson) {

		/*
		 * tabSettings/selectedStats eg. = [{"category": "systemStats.cpu.NumberOfCores",
		 * "duration": "10m", "aggregateFunction": "mean"}, {"category":
		 * "clusterWide.nameNode.StartupProgress.ElapsedTime", "duration":
		 * "10m", "aggregateFunction": "mean"}]
		 * 
		 * Returning json like this
		 * 
		 * {"threadsPerCore":{"unit":"number","time":["2015-10-06 19:11:24",
		 * "2015-10-06 19:11:42","2015-10-06 19:12:00","2015-10-06 19:12:18" ,
		 * "2015-10-06 19:12:36","2015-10-06 19:12:54", "2015-10-06 19:13:12"
		 * ],"127.0.0.1":[0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,1,1,1,1,1,
		 * 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0]},
		 * "cpuUsage":{"unit":"percent","time":["2015-10-06 18:13:12",
		 * "2015-10-06 18:13:30","2015-10-06 18:13:48","2015-10-06 18:14:06" ,
		 * "2015-10-06 18:14:24","2015-10-06 19:12:18", "2015-10-06 19:12:36",
		 * "2015-10-06 19:12:54","2015-10-06 19:13:12"
		 * ],"127.0.0.1":[0,0,0,0,7,3,2,7,5,4,6,4,4,0,0,0,0,0,0,22,24,23,23,
		 * 24,29,24,28,27,26,27,27,26,21,22,22,24,23,22,22,24,23,17,14,4,9,
		 * 10,0,0,0,0,21,24,20,24,21,22,24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
		 * ,0,0,26,25,25,0,0,0,0,0]},"numberOfCores":{"unit":"number","time" :[
		 * "2015-10-06 19:11:06","2015-10-06 19:11:24", "2015-10-06 19:11:42",
		 * "2015-10-06 19:12:00","2015-10-06 19:12:18" ,"2015-10-06 19:12:36",
		 * "2015-10-06 19:12:54", "2015-10-06 19:13:12"
		 * ],"127.0.0.1":[0,,0,0,0,0,,4,4,4,0,0,0,0,4,4,4,4,4,4,4,0,0,0,0,0,
		 * 0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,0,0,0,0,0]}}
		 */

		Type type = new TypeToken<List<NodeSpecificSetting>>() {}.getType();

		try {
			List<NodeSpecificSetting> requestList = Constants.gson.fromJson(selectedStats, type);

			// Reading Data
			InfluxDBConf influxDBConf;
			try {
				influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
			} catch (Exception e) {
				LOGGER.error("Unable to get Influxdb configuration", e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}

			Graphs graphs = new Graphs(requestList.size());
			
			boolean isAllNodes = currentOpenedTab.equals(WebConstants.ALL_NODES) ? true: false;
	
			List<String> selectedNodes =  null;
			if (isAllNodes) {
				selectedNodes = Constants.gson.fromJson(selectedNodesJson, new TypeToken<List<String>>() {
				}.getType());
			}
			
			for (NodeSpecificSetting setting : requestList) {
				String statName = setting.getCategory();

				Query query = new Query();
				if (isAllNodes) {
					query.setTables(selectedNodes);
				} else {
					query.setTableName(currentOpenedTab);
				}
				
				query.addColumn(statName);
				query.setDuration(setting.getDuration());
				query.setAggregateFunction(setting.getAggregateFunction());
				query.setRange(setting.getRangeFrom(), setting.getRangeTo());

				InfluxDataReader reader = new InfluxDataReader(query, influxDBConf);
				
				try {
					graphs.addGraph(statName, convertData(reader.getResult(), statName));
				} catch (Exception e) {
					LOGGER.error("Problem while getting data for stat : " + statName, e);
				}
				
			}
			return Response.ok(Constants.gson.toJson(graphs.getGraphs())).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get hadoop metrices stats data", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public Graph convertData(ResultSet resultSet, String statName) throws ParseException {
		Graph graph = new Graph(statName);

		String error = resultSet.getError();

		if (error != null) {
			LOGGER.error(error);
			return graph;
		}
		List<Series> seriesList = resultSet.getResults().get(0).getSeries();

		if (seriesList == null || seriesList.isEmpty()) {
			return graph;
		}
		for (Series series : seriesList) {
			String lineName = series.getName();
			for (List<String> row : series.getValues()) {
				graph.addPointInLine(lineName, 
						yarnQueuesUtils.getFormattedDate(row.get(0)),
						(long) Double.parseDouble(row.get(1)));
			}
			graph.removeLastPoint(lineName);
		}
		return graph;
	}
	
	@GET
	@Path(WebConstants.BACKGROUND_PROCESSES + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBackgroundProcesses(@PathParam("clusterName") String clusterName) throws Exception {
		return Response.ok(processesManager.getProcessesStatus(clusterName)).build();
	}
	
	@POST
	@Path(WebConstants.CLUSTER_PROFILING + "/metered-queue-usage")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMeteredQueueUsage(
			@FormParam(CLUSTER_NAME) String clusterName,
			@FormParam("stat") @DefaultValue("stat") String stat,
			@FormParam("duration") @DefaultValue("") String duration,
			@FormParam("rangeFrom") @DefaultValue("") String rangeFrom,
			@FormParam("rangeTo") @DefaultValue("") String rangeTo) {
		
		try {
			
			Graph graph = yarnQueuesUtils.getQueueGraphFromInfluxdb(
					clusterName, stat, duration, 
					rangeFrom, rangeTo);
			
			Graphs graphs = new Graphs(1);
			graphs.addGraph(stat, graph);
			
			return Response.ok(Constants.gson.toJson(graphs.getGraphs())).build();
		} catch (Exception e) {
			LOGGER.error("Unable to fetch metered queue usage from influxdb", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path(WebConstants.CLUSTER_PROFILING + "/queue-utilization-summary" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQueueUtilizationSummary(@PathParam(CLUSTER_NAME) String clusterName) {

		try {
			Cluster cluster = cache.getCluster(clusterName);
			Scheduler scheduler = schedulerService.fetchSchedulerInfo(cluster);
			ClusterMetrics clusterMetrics = schedulerService.fetchClusterMetrics(cluster);
			long clusterCapacity = clusterMetrics.getTotalMB() ;
			
			Map<String, Object> map = new HashMap<>(2);
			map.put("clusterCapacity", clusterCapacity);	
			map.put("summary", yarnQueuesUtils.getQueueUtilizationSummary(
					cluster.getClusterName(), scheduler, clusterCapacity));
			
			return Response.ok(Constants.gson.toJson(map)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get queue utilization summary", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private void persistJobDataToInfluxdb(List<EffCapUtilizationStats> list, String clusterName) throws Exception {		
		
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);		
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return;
		}		
		InfluxDataWriter writer = new InfluxDataWriter(configuration);
		writer.setTimeUnit(TimeUnit.SECONDS);
		for(EffCapUtilizationStats stats : list) {	
			boolean mapCondition = (stats.getAllocatedMapMemory() != null
					&& stats.getAllocatedMapMemory() != 0
					&& stats.getUsedMaxMapMemory() != null
					&& stats.getUsedMaxMapMemory() != 0);
			
			boolean reduceCondition = (stats.getAllocatedReduceMemory() != null
					&& stats.getAllocatedReduceMemory() != 0
					&& stats.getUsedMaxReduceMemory() != null
					&& stats.getUsedMaxReduceMemory() != 0);
			
			if (mapCondition && !reduceCondition) {
				stats.setAllocatedReduceMemory(0l);
				stats.setUsedMaxReduceMemory(0l);
			}
			
			if (mapCondition) {
				double percentageOfEffMapMemory = ((stats.getUsedMaxMapMemory() * 1.0) / stats.getAllocatedMapMemory()) * 100;							
				writer.addColumn(WebConstants.EFF_MAP_MEMORY_PERCENT, 
						String.valueOf(new DecimalFormat(WebConstants.DECIMAL_FORMAT).format(percentageOfEffMapMemory)));
				double percentageOfEffReduceMemory;
				if (reduceCondition) {
					percentageOfEffReduceMemory = ((stats.getUsedMaxReduceMemory() * 1.0) / stats.getAllocatedReduceMemory()) * 100;
				} else {
					percentageOfEffReduceMemory = 0;
				}
				
				writer.setTime(stats.getJobStartTime() / 1000);
				writer.setTableName(WebConstants.JOB_HISTORY);
				writer.addColumn(WebConstants.ALLOCATED_MAP_MEMORY, stats.getAllocatedMapMemory());
				writer.addColumn(WebConstants.ALLOCATED_REDUCE_MEMORY, stats.getAllocatedReduceMemory());	
				writer.addColumn(WebConstants.EFF_REDUCE_MEMORY_PERCENT,
						String.valueOf(new DecimalFormat(WebConstants.DECIMAL_FORMAT).format(percentageOfEffReduceMemory)));
				writer.addColumn(WebConstants.JOB_DURATION, stats.getJobFinishTime() - stats.getJobStartTime());
				writer.addTag(WebConstants.JOB_ID, stats.getJobId());
				writer.addTag(WebConstants.JOB_NAME_1, stats.getJobName().replaceAll("\\s+",""));
				writer.writeData();
			}
		}
	}
	
	/**
	 * 
	 * @param jobName job name
	 * @param clusterName cluster name
	 * @param duration duration for which job history will be returned (eg. 5h) -- 5 hours
	 * @param rangeFrom date from which data will be retrieved. Date format is yyyy/MM/dd HH:mm
	 * @param rangeTo date upto which data will be retrieved. Date format is yyyy/MM/dd HH:mm
	 * @return
	 */
	@GET
	@Path(WebConstants.JOB_HISTORY_URL)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobHistory(
			@QueryParam("jobName") String jobName,
			@QueryParam(CLUSTER_NAME) String clusterName,
			@Context UriInfo info) {
		
		/*
		  Response json ==
			 [
			        {
			        	"jobId": "job_1481101157182_0001", 
			            "effMapMemoryPercent": 55.02,
			            "allocatedMapMemory": "1024 MB",
			            "effReduceMemoryPercent": 75.41,
			            "allocatedReduceMemory": "124 MB"
			        }, 
			        {
			            "jobId": "job_1481101157182_0002", 
			            "effMapMemoryPercent": 25.02,
			            "allocatedMapMemory": "1024 MB",
			            "effReduceMemoryPercent": 35.41,
			            "allocatedReduceMemory": "124 MB"
			        }
			    ]

		 */
		MultivaluedMap<String, String> params = info.getQueryParameters();
		try {
			InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
			if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
				throw new Exception("Influxdb is down");
			}
			Query query = new Query();
			query.setTableName(WebConstants.JOB_HISTORY);
			query.addTag(WebConstants.JOB_NAME_1, jobName.replaceAll("\\s+",""));
			query.setDuration(params.getFirst("duration"));
			query.setRange(params.getFirst("rangeFrom"), params.getFirst("rangeTo"));
			query.addColumn(WebConstants.JOB_ID);
			query.addColumn(WebConstants.EFF_MAP_MEMORY_PERCENT);
			query.addColumn(WebConstants.ALLOCATED_MAP_MEMORY);
			query.addColumn(WebConstants.EFF_REDUCE_MEMORY_PERCENT);
			query.addColumn(WebConstants.ALLOCATED_REDUCE_MEMORY);
			query.addColumn(WebConstants.JOB_DURATION);
			InfluxDataReader reader = new InfluxDataReader(query, configuration);
			
			ResultSet resultSet = reader.getResult();
			if (resultSet.getError() != null) {
				throw new Exception(resultSet.getError());
			}
			List<Series> seriesList = resultSet.getResults().get(0).getSeries();
			if (seriesList == null || seriesList.isEmpty()) {
				return Response.ok(Constants.gson.toJson(Collections.EMPTY_LIST)).build();
			}
			Series series = resultSet.getResults().get(0).getSeries().get(0);
			
			List<Map<String, Object>> data = new ArrayList<>(series.getValues().size());
			
			Map<String, Object> temp = null;
			for (List<String> row : series.getValues()) {
				temp = new HashMap<>(5);
				temp.put(WebConstants.JOB_START_TIME, row.get(0));
				// first column is time. therefore we are starting from 1,
				// columns are in the same order that we added in "query" (query.addColumn())
				temp.put(WebConstants.JOB_ID, row.get(1));
				temp.put(WebConstants.EFF_MAP_MEMORY_PERCENT, Double.parseDouble(row.get(2)));
				temp.put(WebConstants.ALLOCATED_MAP_MEMORY, row.get(3) + WebConstants.MB);
				temp.put(WebConstants.EFF_REDUCE_MEMORY_PERCENT, Double.parseDouble(row.get(4)));
				temp.put(WebConstants.ALLOCATED_REDUCE_MEMORY, row.get(5) + WebConstants.MB);
				temp.put(WebConstants.JOB_DURATION, row.get(6));
				data.add(temp);
			}
			
			return Response.ok(Constants.gson.toJson(data)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get job history for job [" + jobName + "]", e);
			return Response.ok(Constants.gson.toJson(Collections.EMPTY_LIST)).build();
		}	
		
	}
	
	/**
	 * This method persists the user queue utilization information.
	 *
	 * @param clusterName the cluster name
	 * @return the response
	 */
	@POST
	@Path("/user-queue-utilization" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response persistUserQueueUtilization(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			ClusterProfilingService clusterProfilingService = getClusterProfilingService(cluster);
			List<JobQueueBean> jobQueueBeans = clusterProfilingService.getQueueUserStats(cluster);
			yarnQueuesUtils.persistUserQueueUtilizationData(jobQueueBeans,clusterName);
			return Response.ok(Constants.gson.toJson(true)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get user queue stats  " + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	/**
	 * This method updates the user queue utilization information.
	 *
	 * @param clusterName the cluster name
	 * @return the response
	 */
	@POST
	@Path("/update-user-queue-utilization" + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserQueueUtilization(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			yarnQueuesUtils.updateInfluxForHiveImpersonisation(cluster, sessionUtils.getRM(cache.getCluster(clusterName), getSession()));
			return Response.ok(Constants.gson.toJson(true)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to update user queue utilization  ", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * 
	 * @param queueName job name
	 * @param clusterName cluster name
	 * @param duration duration for which job history will be returned (eg. 5h) -- 5 hours
	 * @param rangeFrom date from which data will be retrieved. Date format is yyyy/MM/dd HH:mm
	 * @param rangeTo date upto which data will be retrieved. Date format is yyyy/MM/dd HH:mm
	 * @return
	 */
	@GET
	@Path(WebConstants.USER_QUEUE_UTILIZATION_URL)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserQueueUtilizationData(
			@QueryParam("queueName") String queueName,
			@QueryParam(CLUSTER_NAME) String clusterName,
			@QueryParam("stat") String stat,
			@QueryParam("duration") @DefaultValue("") String duration,
			@QueryParam("rangeFrom") @DefaultValue("") String rangeFrom,
			@QueryParam("rangeTo") @DefaultValue("") String rangeTo) {
		
		try {
			
			List<Map<String, Object>> finalOutput = yarnQueuesUtils
					.getUserQueueUtilizationData( queueName, cache.getCluster(clusterName),
							stat, duration, rangeFrom, rangeTo, metrics,
							sessionUtils.getRM(cache.getCluster(clusterName), getSession()));
			/*
			 * finalOutput json would be like
			 * [{"userName" : "user1","relativePercentUsage" : 40.4},
			 * {"userName" : "user2","relativePercentUsage" : 19.6},
			 * {"userName" : "user3","relativePercentUsage" : 40.0}]
			 */
			return Response.ok(Constants.gson.toJson(finalOutput)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get user queue utilization data", e);
			return Response.ok(Constants.gson.toJson(Collections.EMPTY_LIST)).build();
		}
	}
	
	
	/**
	 * Gets the charge back data based on the current or previous or user configured month.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the charge back data
	 */
	@GET
	@Path(WebConstants.CHARGE_BACK_URL + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChargeBackData(@PathParam(CLUSTER_NAME) String clusterName,
			@QueryParam("month") String month,
			@QueryParam("rangeFrom") @DefaultValue("") String rangeFrom,
			@QueryParam("rangeTo") @DefaultValue("") String rangeTo) {

		try {
			Cluster cluster = cache.getCluster(clusterName);
			List<Map<String, Object>> finalOutput = 
					yarnQueuesUtils.getchargeBackData(cluster,month,rangeFrom,rangeTo, sessionUtils.getRM(cache.getCluster(clusterName), getSession()));
			
			return Response.ok(
					Constants.gson.toJson(yarnQueuesUtils.convertDataChargeBackModelStructure(finalOutput))
					).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get charge back data", e);
			return Response.ok(Constants.gson.toJson(Collections.EMPTY_LIST)).build();
		}
	}

	@GET
	@Path(WebConstants.CLUSTERWIDE_MAJORCOUNTERS + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterWideMajorCounters(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			MajorCounters majorCounters = MajorCounters.getInstance();
			Map<String, String> counters = majorCounters.getMajorCounters(cluster);

			counters.putAll(metrics.getFaultyBlocks(cluster));
			return Response.ok(Constants.gson.toJson(counters)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get cluster wide major counters", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path(WebConstants.MAPR_CLDB_METRICS + "/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaprCldbMetrics(@PathParam(CLUSTER_NAME) String clusterName) {
		try {
			Cluster cluster = cache.getCluster(clusterName);
			MajorCounters majorCounters = MajorCounters.getInstance();
			Map<String, String> counters = majorCounters.getMaprCldbMetrics(cluster);

			return Response.ok(Constants.gson.toJson(counters)).build();
		} catch (Exception e) {
			LOGGER.error("Unable to get mapr cldb metrices stats", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
	
	@GET
	@Path("/export-as-excel")
	@Produces("application/vnd.ms-excel")
	public Response writeExcel(@QueryParam("clusterName") String clusterName,
			@QueryParam("selectedWidgets") String selectedWidgetsJson,
			@QueryParam("parameters") String parametersJson) {
		try {
			HSSFWorkbook workbook = createWorkbook(clusterName, selectedWidgetsJson, parametersJson);
			File file = new File(clusterName + ".xls");
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
			fos.close();
			return Response.ok((Object) file)
					.header("content-disposition","attachment; filename = " + clusterName + ".xls")
					.build();
		} catch (Exception e) {
			LOGGER.error("Unable to create excel file", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
		
	}
	
	public HSSFWorkbook createWorkbook(String clusterName, String selectedWidgetsJson,
			String parametersJson) throws NumberFormatException, Exception {
		Cluster cluster = cache.getCluster(clusterName);
		
		String[] selectedWidgets = Constants.gson.fromJson(selectedWidgetsJson, String [].class);
		Map<String, String> parameters = Constants.gson.fromJson(parametersJson, new TypeToken<Map<String, String>>(){}.getType());
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.createRow(0);
		int rowNumber = 1;
		
		String month = parameters.get("month");
		String rangeFrom = parameters.get("rangeFrom");
		String rangeTo = parameters.get("rangeTo");
		
		AnalyzeClusterExcelExport excel = new AnalyzeClusterExcelExport();
		for (String widget : selectedWidgets) {
			switch (widget) {
			case "Queue Utilization Summary" : {
				boolean isFair = fairSchedularCache.isFairScheduler(cluster);
				try {
					Scheduler scheduler = schedulerService.fetchSchedulerInfo(cluster);
					ClusterMetrics clusterMetrics = schedulerService.fetchClusterMetrics(cluster);
					long clusterCapacity = clusterMetrics.getTotalMB() ;
					List<Map<String, Object>> list = yarnQueuesUtils.getQueueUtilizationSummary(clusterName, scheduler, clusterCapacity);
					
					rowNumber = excel.queueUtilizationSummary(workbook, sheet, list, rowNumber, isFair);
					rowNumber += 2;
				} catch (Exception e) {
					LOGGER.error("Error while getting queue utilization summary, not adding in excel report", e);
				}
				break;
			}
			case "Long Duration Applications" : {
				try {
					List<Map<String, Object>> list = metrics.getLongRunningApplications(
							Long.parseLong(parameters.get("thresholdMillis")), cluster, sessionUtils.getRM(cluster, getSession()));
					rowNumber = excel.longDurationApps(workbook, sheet, list, rowNumber);
					rowNumber += 2;
				} catch (Exception e) {
					LOGGER.error("Unable to get Long running applications", e);
				}
				break;
			}
			
			case "Resource Utilization Metering" : {
				try {
					List<Map<String, Object>> data = 
							yarnQueuesUtils.getchargeBackData(
									cluster, month, rangeFrom, rangeTo, sessionUtils.getRM(cluster, getSession()));
					String date = excel.getTimePeriod(month, rangeFrom, rangeTo);
					rowNumber = excel.resouceUtilizationMetering(workbook, sheet, data, rowNumber, date);
					rowNumber += 2;
				} catch (Exception e) {
					LOGGER.error("Error while getting Charge Back Model data, not adding in excel report", e);
				}
				break;
			}
			
			case "Detailed Resource Utilization Metering" : {
				try {
					
					
					List<Map<String, Object>> data = 
							yarnQueuesUtils.getDetailedChargeBackData(
									cluster, month, rangeFrom, rangeTo, sessionUtils.getRM(cluster, getSession()));
					
					String date = excel.getTimePeriod(month, rangeFrom, rangeTo);
					rowNumber = excel.detailedresouceUtilizationMetering(workbook, sheet, data, rowNumber, date);
					rowNumber += 2;
				} catch (Exception e) {
					LOGGER.error("Error while getting Charge Back Model data details, not adding in excel report", e);
				}
				break;
			}
			}
		}
		excel.autoSizeColumns(workbook);
		
		return workbook;
	}

	/**
	 * Gets the cluster profiling service.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the cluster profiling service
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ClusterProfilingService getClusterProfilingService(Cluster cluster) throws Exception {
		ClusterProfilingService clusterProfilingService = null;
		Class clazz = null;
		Constructor cons = null;
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		try {
			if (hadoopType.equalsIgnoreCase(ExtendedConstants.NON_YARN)) {
				clazz = Class.forName(WebConstants.NON_YARN_CLUSTER_PROFILING_SERVICE);
				cons = clazz.getConstructor(Cluster.class);
				clusterProfilingService = (ClusterProfilingService) cons.newInstance(cluster);
			} else {
				clazz = Class.forName(WebConstants.YARN_CLUSTER_PROFILING_SERVICE);
				cons = clazz.getConstructor(RMCommunicator.class, MRCommunicator.class);
				clusterProfilingService = (ClusterProfilingService) cons.newInstance(
						sessionUtils.getRM(cluster, getSession()), sessionUtils.getMR(cluster, getSession()));
			}
			
			
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Unable to get  cluster profiling service", e);
		}

		return clusterProfilingService;
	}

	/**
	 * return heat map json.
	 *
	 * @param cluster
	 *            the cluster
	 * @param gson
	 *            the gson
	 * @param generalSettings
	 *            the general settings
	 * @param viewName
	 * @return the heat map
	 * @throws HTFProfilingException
	 *             the HTF profiling exception
	 */
	private ClusterInfo getHeatMap(Cluster cluster, String generalSettings,
			String viewName) throws HTFProfilingException {

		if (generalSettings == null || generalSettings.isEmpty()
				|| generalSettings.matches("^[0-9]") || generalSettings.matches("^[-][0-9]")
				|| generalSettings.trim().equals("{}")) {
			generalSettings = WebConstants.DATA_CENTER_DEFAULT_CONFIG;
		}
		ClusterViewServiceImpl cvsi = new ClusterViewServiceImpl(cluster);
		List<PerformanceStats> perfStats = getPerfStatsFromJson(generalSettings);
		return cvsi.getDataCenterDetails(perfStats);
	}

	/**
	 * Gets the performance stats from json.
	 *
	 * @param generalSettings
	 *            the general settings
	 * @return the perf stats from json
	 */
	private List<PerformanceStats> getPerfStatsFromJson(String generalSettings) {
		Type type = new TypeToken<Map<String, List<PerformanceStats>>>() {
		}.getType();
		Map<String, List<PerformanceStats>> genSettings = Constants.gson.fromJson(generalSettings, type);
		return genSettings.get("color");
	}

	private List<DataLoad> getModifiedDataLoadAndDistribution(List<NodeInfo> list) {
		int numOfWorkerNodes = list.size();
		double idealDataLoadPercent = 100 / numOfWorkerNodes;

		int goodCount = 0, warnMinusCount = 0, badMinusCount = 0;
		int warnPlusCount = 0, badPlusCount = 0;
		int unavailableCount = 0;
		double goodPer = 0, warnMinusPer = 0, badMinusPer = 0;
		double warnPlusPer = 0, badPlusPer = 0;

		List<String> goodNodes, warnMinusNodes,
				warnPlusNodes, badMinusNodes, badPlusNodes, unavailableNodes;
		
		if (numOfWorkerNodes < 16) {
			goodNodes = new ArrayList<String>(numOfWorkerNodes);
			warnMinusNodes = new ArrayList<String>(numOfWorkerNodes);
			warnPlusNodes = new ArrayList<String>(numOfWorkerNodes);
			badMinusNodes = new ArrayList<String>(numOfWorkerNodes);
			badPlusNodes = new ArrayList<String>(numOfWorkerNodes);
			unavailableNodes = new ArrayList<String>(numOfWorkerNodes);
		} else {
			goodNodes = new ArrayList<String>();
			warnMinusNodes = new ArrayList<String>();
			warnPlusNodes = new ArrayList<String>();
			badMinusNodes = new ArrayList<String>();
			badPlusNodes = new ArrayList<String>();
			unavailableNodes = new ArrayList<String>();
		}

		for (NodeInfo nodeInfo : list) {
			if (nodeInfo.getPerformance() == NodePerformance.Good) {
				goodCount++;
				goodPer += Double.parseDouble(nodeInfo.getDataLoadStats());
				goodNodes.add(nodeInfo.getNodeIp());
			} else if (nodeInfo.getPerformance() == NodePerformance.Warn) {
				if (Double
						.parseDouble(nodeInfo.getDataLoadStats()) > idealDataLoadPercent) {
					warnPlusCount++;
					warnPlusPer += Double.parseDouble(nodeInfo.getDataLoadStats());
					warnPlusNodes.add(nodeInfo.getNodeIp());
				} else {
					warnMinusCount++;
					warnMinusPer += Double.parseDouble(nodeInfo.getDataLoadStats());
					warnMinusNodes.add(nodeInfo.getNodeIp());
				}
			} else if (nodeInfo.getPerformance() == NodePerformance.Bad) {
				if (Double
						.parseDouble(nodeInfo.getDataLoadStats()) > idealDataLoadPercent) {
					badPlusCount++;
					badPlusPer += Double.parseDouble(nodeInfo.getDataLoadStats());
					badPlusNodes.add(nodeInfo.getNodeIp());
				} else {
					badMinusCount++;
					badMinusPer += Double.parseDouble(nodeInfo.getDataLoadStats());
					badMinusNodes.add(nodeInfo.getNodeIp());
				}
			} else {
				unavailableCount++;
				unavailableNodes.add(nodeInfo.getNodeIp());
			}
		}

		List<DataLoad> nodesList = new ArrayList<DataLoad>();
		if (goodCount > 0) {
			nodesList.add(
					new DataLoad(goodCount, goodPer / goodCount, NodePerformance.Good, goodNodes));
		}
		if (warnPlusCount > 0) {
			nodesList.add(new DataLoad(warnPlusCount, warnPlusPer / warnPlusCount,
					NodePerformance.Warn, warnPlusNodes, true));
		}
		if (warnMinusCount > 0) {
			nodesList.add(new DataLoad(warnMinusCount, warnMinusPer / warnMinusCount,
					NodePerformance.Warn, warnMinusNodes, false));
		}
		if (badPlusCount > 0) {
			nodesList.add(new DataLoad(badPlusCount, badPlusPer / badPlusCount, NodePerformance.Bad,
					badPlusNodes, true));
		}
		if (badMinusCount > 0) {
			nodesList.add(new DataLoad(badMinusCount, badMinusPer / badMinusCount,
					NodePerformance.Bad, badMinusNodes, false));
		}
		if (unavailableCount > 0) {
			nodesList.add(new DataLoad(unavailableCount, 0.0, NodePerformance.Unavailable, unavailableNodes));
		}
		return nodesList;
	}

}
