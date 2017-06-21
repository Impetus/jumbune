package org.jumbune.web.utils;

import static org.jumbune.common.influxdb.beans.InfluxDBConstants.EMPTY_STRING;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.SPACE;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.T;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.YYYY_MM_DD_space_HH_MM_SS;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.Z;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.yarn.beans.FairSchedulerQueueInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.ChargeBackConf;
import org.jumbune.utils.conf.beans.ChargeBackConfigurations;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.influxdb.InfluxDataReader;
import org.jumbune.common.influxdb.InfluxDataWriter;
import org.jumbune.common.influxdb.beans.InfluxDBConstants;
import org.jumbune.common.influxdb.beans.Query;
import org.jumbune.common.influxdb.beans.ResultSet;
import org.jumbune.common.influxdb.beans.ResultSet.Result;
import org.jumbune.common.influxdb.beans.ResultSet.Result.Series;
import org.jumbune.clusterprofiling.yarn.ClusterAnalysisMetrics;
import org.jumbune.web.beans.Graph;
import org.jumbune.web.services.ClusterAnalysisService;

import org.jumbune.clusterprofiling.yarn.beans.CapacitySchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.Scheduler;
import org.jumbune.web.utils.WebConstants;

import org.jumbune.clusterprofiling.SchedulerService;
import org.jumbune.common.beans.JobDetails;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobHistoryServerService;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.web.utils.YarnQueuesUtils;

import org.jumbune.clusterprofiling.yarn.beans.ClusterMetrics;

/**
 * Utility functions related to Yarn Queues
 */
public class YarnQueuesUtils {

	private final String _6H = "6h";
	private final String ALL = "ALL";
	private final String WITH_KEY_QUEUE_NAME = "\" WITH KEY = \"queueName\"";
	private final String SHOW_TAG_VALUES_FROM = "SHOW TAG VALUES FROM \"";
	private final String USER_NAME = "userName";
	private final String _2F = "%.2f";
	private final String MEAN = "mean";
	private final String FAIR = "fair";
	private final String CAPACITY = "capacity";
	private final String ONE_MONTH = "30d";
	private final String QUEUE_NAME = "queueName";
	private final String SCHEDULER_TYPE = "schedulerType";
	private final String QUEUE_UTILIZATION_TABLE = "queueUtilization";
	private final String USED_RESOURCES_MEMORY_PERCENT = "usedResourcesMemoryPercent";
	private final String USED_RESOURCES_V_CORES_PERCENT = "usedResourcesVCoresPercent";
	private final String _30D = "30d";
	private final String _1D = "1d";
	private final String CUSTOM = "custom";
	
	private static volatile YarnQueuesUtils instance;

	private SchedulerService schedulerService;

	
	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(YarnQueuesUtils.class);
	
	
	public static YarnQueuesUtils getInstance() {
		if (instance == null) {
			synchronized (YarnQueuesUtils.class) {
				if (instance == null) {
					instance = new YarnQueuesUtils();
				}
			}
		}
		return instance;
	}
	
	private YarnQueuesUtils() {
		schedulerService = SchedulerService.getInstance();
	}

	/**
	 * It persists capacity scheduler data to influxdb. The data will be used by
	 * Metered Queue Usage
	 * 
	 * @param list
	 * @param clusterName
	 * @throws Exception
	 */
	public void persistCapacitySchedulerData(List<CapacitySchedulerQueueInfo> list, String clusterName) throws Exception {

		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return;
		}
		InfluxDataWriter writer = new InfluxDataWriter(configuration);
		writer.setTimeUnit(TimeUnit.SECONDS);
		writer.setTime(System.currentTimeMillis() / 1000);
		writer.setTableName(QUEUE_UTILIZATION_TABLE);

		for (CapacitySchedulerQueueInfo queueInfo : list) {
			writer.addColumn(WebConstants.USED_RESOURCES_MEMORY, queueInfo.getResourcesUsed() != null ? queueInfo.getResourcesUsed().getMemory() : 0);
			writer.addColumn(WebConstants.USED_RESOURCES_V_CORES, queueInfo.getResourcesUsed() != null ? queueInfo.getResourcesUsed().getvCores() : 0);
			writer.addTag(QUEUE_NAME, queueInfo.getQueueName());
			writer.addTag(SCHEDULER_TYPE, CAPACITY);
			writer.writeData();
		}
	}

	/**
	 * It persists Fair scheduler data to influxdb. The data will be used by
	 * Metered Queue Usage
	 * 
	 * @param list
	 * @param clusterName
	 * @throws Exception
	 */
	public void persistFairSchedulerData(List<FairSchedulerQueueInfo> list, String clusterName) throws Exception {

		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return;
		}
		InfluxDataWriter writer = new InfluxDataWriter(configuration);
		writer.setTimeUnit(TimeUnit.SECONDS);
		writer.setTime(System.currentTimeMillis() / 1000);
		writer.setTableName(QUEUE_UTILIZATION_TABLE);

		boolean isDominentResourceFairness = isDRF(list);

		for (FairSchedulerQueueInfo queueInfo : list) {

			writer.addTag(QUEUE_NAME, queueInfo.getQueueName());

			writer.addColumn(USED_RESOURCES_MEMORY_PERCENT, queueInfo.getPercentUsedMemory());
			writer.addColumn(WebConstants.USED_RESOURCES_MEMORY, queueInfo.getUsedResources().getMemory());

			if (isDominentResourceFairness) {
				writer.addColumn(USED_RESOURCES_V_CORES_PERCENT, queueInfo.getPercentUsedVCores());
				writer.addColumn(WebConstants.USED_RESOURCES_V_CORES, queueInfo.getUsedResources().getvCores());
			}
			writer.addTag(SCHEDULER_TYPE, FAIR);
			writer.writeData();
		}
	}


	private boolean isDRF(List<FairSchedulerQueueInfo> list) {
		for (FairSchedulerQueueInfo queueInfo : list) {
			if (queueInfo.getSchedulingPolicy().equalsIgnoreCase(WebConstants.DRF)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * It fetches queue data (whether it is fair scheduler or capacity
	 * scheduler) from influxdb. Used by Metered Queue Usage
	 * 
	 * @param stat
	 *            In case of capacity scheduler, stat can be [currentCapacity /
	 *            maximumCapacity / capacity / averageWaitingTime]. In case of
	 *            fair scheduler stat can be [usedResourcesMemoryPercent /
	 *            usedResourcesMemory / averageWaitingTime]. If Dominant
	 *            Resource Fairness applied in a queue in fair scheduler then
	 *            additional stats are [ usedResourcesVCoresPercent /
	 *            usedResourcesVCores ]
	 * @param aggregateFunction
	 * @param duration
	 * @param rangeFrom
	 * @param rangeTo
	 * @param groupByTime
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public Graph getQueueGraphFromInfluxdb(String clusterName, String stat, String duration,
			String rangeFrom, String rangeTo) throws Exception {

		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return new Graph(stat);
		}
		Cluster cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		Query query = new Query();
		query.setTableName(QUEUE_UTILIZATION_TABLE);
		query.setAggregateFunction(WebConstants.MEAN);
		if (stat.startsWith("usedResourcesMemoryPercent")) {
			query.addColumn("usedResourcesMemory");
		} else {
			query.addColumn(stat);
		}
		query.setNumberOfRecords(40);
		if (StringUtils.isBlank(duration)) {
			query.setRange(rangeFrom, rangeTo);
		} else {
			query.setDuration(duration);
			if (duration.equals(_1D) || duration.equals(_6H)) {
				query.setNumberOfRecords(24);
			}
			if (duration.equals(_30D)) {
				query.setNumberOfRecords(30);
			}
		}
		
		query.addGroupByColumn(QUEUE_NAME);
		
		if (ClusterAnalysisService.fairSchedularCache
				.isFairScheduler(cluster)) {
			query.addTag(SCHEDULER_TYPE, FAIR);
		} else {
			query.addTag(SCHEDULER_TYPE, CAPACITY);
		}
		
		LOGGER.debug("Query Created for fetching Metered Queue Usage from Influxdb [ " + query.toString() + " ]");
		InfluxDataReader reader = new InfluxDataReader(query, configuration);
		return convertData(reader.getResult(), stat, cluster);
	}

	/**
	 * By default influxdb returns datetime in format yyyy-MM-ddTHH:mm:ssZ and
	 * it is also UTC Time. So in this method we convert the datetime in local
	 * time by adding offset (difference between local time and offset time) and
	 * return the datetime in milliseconds.
	 * 
	 * @param original
	 * @return
	 * @throws ParseException
	 */
	public long getFormattedDate(String original) throws ParseException {
		original = original.replace(T, SPACE).replace(Z, EMPTY_STRING);
		DateFormat format = new SimpleDateFormat(YYYY_MM_DD_space_HH_MM_SS);
		Date date = format.parse(original);

		// Calculating timeoffset
		TimeZone timezone = TimeZone.getDefault();
		Calendar calender = GregorianCalendar.getInstance(timezone);

		return (date.getTime() + timezone.getOffset(calender.getTimeInMillis()));
	}

	/**
	 * Returns queue utilization summary
	 * 
	 * @param schedulerService
	 * @param cluster
	 * @param isFairScheduler
	 * @param rmCommunicator 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getQueueUtilizationSummary(
			String clusterName, Scheduler scheduler, long clusterCapacity) throws Exception {

		InfluxDBConf influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(influxDBConf)) {
			throw new Exception("Influxdb not alive");
		}
		Query query = new Query();
		query.setTableName(QUEUE_UTILIZATION_TABLE);
		query.setAggregateFunction(MEAN);
		query.setDuration(ONE_MONTH);
		query.addGroupByColumn(QUEUE_NAME);
		query.setDoNotAddGroupByTime(true);
		query.addColumn(WebConstants.USED_RESOURCES_MEMORY);
		if (scheduler.isFairScheduler()) {
			query.addTag(SCHEDULER_TYPE, FAIR);
		} else {
			query.addTag(SCHEDULER_TYPE, CAPACITY);
		}
		LOGGER.debug("Query created for fetching Queue Utilization Summary from Influxdb [ " + query.toString() + " ]");
		InfluxDataReader reader = new InfluxDataReader(query, influxDBConf);
		ResultSet resultSet = reader.getResult();
		if (resultSet.getError() != null) {
			throw new Exception(resultSet.getError());
		}
		List<Series> seriesList = resultSet.getResults().get(0).getSeries();
		if (seriesList == null || seriesList.isEmpty()) {
			return Collections.emptyList();
		}
		List<FairSchedulerQueueInfo> fairSchedulerQueues = null;
		List<CapacitySchedulerQueueInfo> capacitySchedulerQueues = null;
		if (scheduler.isFairScheduler()) {
			fairSchedulerQueues = scheduler.getFairSchedulerLeafQueues();
		} else {
			capacitySchedulerQueues = scheduler.getCapcitySchedulerLeafQueues();
		}

		List<Map<String, Object>> list = new ArrayList<>(seriesList.size());
		Map<String, Object> map;
		String queueName;

		long steadyFairShare;
		double queueCapacityWRTCluster;
		double usedResourceMemory;
		for (Series series : seriesList) {
			if (scheduler.isFairScheduler()) {
				map = new HashMap<>(3);
			} else {
				map = new HashMap<>(5);
			}
			queueName = series.getTags().get(QUEUE_NAME);
			map.put(QUEUE_NAME, queueName);
			List<String> row = series.getValues().get(0);
			usedResourceMemory = Double.parseDouble(row.get(1));
			map.put(WebConstants.USED_RESOURCES_MEMORY, usedResourceMemory);
			map.put(WebConstants.UTILIZAION_PERCENT_WRT_CLUSTER, ( usedResourceMemory * 100 ) / clusterCapacity);
			if (scheduler.isFairScheduler()) {
				steadyFairShare = getSteadyFairShare(queueName, fairSchedulerQueues);
				if (steadyFairShare == 0) {
					LOGGER.error("Queue [" + queueName + "] doesn't exists, therefore skipping it.");
					continue;
				}
				map.put(WebConstants.STEADY_FAIR_SHARE, steadyFairShare);
				map.put(WebConstants.UTILIZAION_PERCENT_WRT_QUEUE, (Double.parseDouble(row.get(1)) * 100) / steadyFairShare);
			} else {
				queueCapacityWRTCluster = getAbsoluteCapacity(queueName, capacitySchedulerQueues);
				if (queueCapacityWRTCluster == 0.0f) {
					LOGGER.error("Queue [" + queueName + "] doesn't exists, therefore skipping it.");
					continue;
				}
				map.put(WebConstants.ABSOLUTE_QUEUE_CAPACITY_PERCENT, queueCapacityWRTCluster);
				map.put(WebConstants.UTILIZAION_PERCENT_WRT_QUEUE, 
						(usedResourceMemory * 10000.0) / ( queueCapacityWRTCluster * clusterCapacity));
			}
			
			list.add(map);
		}
		return list;
	}

	private float getAbsoluteCapacity(String queueName, List<CapacitySchedulerQueueInfo> capacitySchedulerQueues) {
		for (CapacitySchedulerQueueInfo queue : capacitySchedulerQueues) {
			if (queue.getQueueName().equals(queueName)) {
				return queue.getAbsoluteCapacity();
			}
		}
		return 0.0f;
	}

	private long getSteadyFairShare(String queueName, List<FairSchedulerQueueInfo> queuesInfo) {
		for (FairSchedulerQueueInfo info : queuesInfo) {
			if (info.getQueueName().equals(queueName)) {
				return info.getFinalSteadyFairMemory() * 1048576;
			}
		}
		return 0;
	}

	/**
	 * It persists user queue utilization data into influxdb.
	 * 
	 * @param list
	 * @param clusterName
	 * @throws Exception
	 */
	public void persistUserQueueUtilizationData(List<JobQueueBean> list, String clusterName) throws Exception {
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return;
		}
		if (!list.isEmpty()) {
			InfluxDataWriter writer = new InfluxDataWriter(configuration);
			writer.setTimeUnit(TimeUnit.SECONDS);
			writer.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
			writer.setTime(System.currentTimeMillis() / 1000);
			for (JobQueueBean stats : list) {
				writer.addTag(WebConstants.QUEUE_NAME,
						stats.getQueueName().replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
				writer.addTag(WebConstants.USER_NAME, stats.getUser());
				writer.addTag(WebConstants.JOB_ID, stats.getJobId());
				writer.addTag(WebConstants.EXECUTION_ENGINE, stats.getExecutionEngine());
				writer.addColumn(WebConstants.USED_CORES, stats.getUsedCores());
				writer.addColumn(WebConstants.USED_MEMORY, stats.getUsedMemory());
				writer.writeData();
			}
		}
	}

	/**
	 * It fetches user queue utilization data from influxdb
	 * 
	 * @param queueName
	 * @param clusterName
	 * @param stat
	 * @param duration
	 * @param rangeFrom
	 * @param rangeTo
	 * @param metrics
	 * @param rmCommunicator 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserQueueUtilizationData(
			String queueName, Cluster cluster, String stat,
			String duration, String rangeFrom, String rangeTo,
			ClusterAnalysisMetrics metrics, RMCommunicator rmCommunicator) throws Exception {

		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName());
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			throw new Exception("Influxdb is down");
		}
		String impersonationCheckUser = StringUtils.EMPTY ;
			//get the impersonated user
		 impersonationCheckUser = RemotingUtil.getHiveKerberosPrincipal(cluster);
		Query query = new Query();
		query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
		query.addColumn(stat);
		query.addTag(WebConstants.QUEUE_NAME,
				queueName.replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
		query.setAggregateFunction(MEAN);
		query.setDoNotAddGroupByTime(true);
		query.addInequalities(WebConstants.USER_NAME, impersonationCheckUser);
		query.addGroupByColumn(WebConstants.USER_NAME);
		query.addGroupByColumn(WebConstants.JOB_ID);
		query.setDuration(duration);
		query.setRange(rangeFrom, rangeTo);
		
		LOGGER.debug("UserQueueUtilization query: " + query);

		InfluxDataReader reader = new InfluxDataReader(query, configuration);

		ResultSet resultSet = reader.getResult();
		/*
		 * Result set will be like this
		 * {"results":[{"series":[{"name":"userQueueUtilization","tags":{
		 * "jobId":"job_1481552662911_0037","userName":"mapr"},"columns"
		 * :["time","mean","mean_1"],"values":[["1970-01-01T00:00:00Z",5.75,
		 * 6912]]},{"name":"userQueueUtilization","tags":{"jobId":
		 * "job_1481552662911_0038","userName":"impadmin"},"columns":["time"
		 * ,"mean","mean_1"],"values":[["1970-01-01T00:00:00Z",7,8192]]},{
		 * "name":"userQueueUtilization","tags":{"jobId":
		 * "job_1481552662911_0039","userName":"impadmin"},"columns":["time"
		 * ,"mean","mean_1"],"values":[["1970-01-01T00:00:00Z",5.75,6912]]},
		 * {"name":"userQueueUtilization","tags":{"jobId":
		 * "job_1481552662911_0040","userName":"impadmin"},"columns":["time"
		 * ,"mean","mean_1"],"values":[["1970-01-01T00:00:00Z",8,9192]]}]}]}
		 */
		if (resultSet.getError() != null) {
			throw new Exception(resultSet.getError());
		}
		List<Series> seriesList = resultSet.getResults().get(0).getSeries();
		if (seriesList == null || seriesList.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> finalOutput = parseDataForUserQueueUtilization(seriesList, metrics, rmCommunicator);
		return finalOutput;
	}
	

	private void parseDataForDetailedChargeBackQueueUtilization(List<Series> seriesList, double memoryCost, double vCoreCost,
			String queueName, String executionEngine, List<Map<String, Object>> chargeBackList) {
		
		double totalCores ;
		double totalMemory ;
		double vCore = 0;
		double memory = 0;
		Map<String, Object> chargeBackMap = null ;
		for (Series series : seriesList) {
			totalCores = 0;
			totalMemory = 0;
			chargeBackMap = new HashMap<String, Object>(9);
			for (List<String> row : series.getValues()) {
				vCore = Double.parseDouble(row.get(1));
				if (vCore >= 0) {
					totalCores += vCore;
				}
				memory = Double.parseDouble(row.get(2));
				if (memory >= 0) {
					totalMemory += memory;
				}
			}
			double memoryInGb = totalMemory / 1024.0;
			double totalmemoryHours = Double.parseDouble(String.format(_2F, ((memoryInGb * 15.0) / 3600)));
			double totalVcoreHours = Double.parseDouble(String.format(_2F, ((totalCores * 15.0) / 3600)));
			double totalmemoryCost = totalmemoryHours * memoryCost;
			double totalVCoresCost = totalVcoreHours * vCoreCost;
			double totalCost = Double.parseDouble(String.format(_2F, (totalmemoryCost + totalVCoresCost)));
			chargeBackMap.put(WebConstants.USER, series.getTags().get(WebConstants.USER_NAME));
			chargeBackMap.put(WebConstants.QUEUE_NAME, queueName);
			chargeBackMap.put(WebConstants.V_CORE_HOURS_USED, totalVcoreHours);
			chargeBackMap.put(WebConstants.CONFIGURED_VCORE_COST, vCoreCost);
			chargeBackMap.put(WebConstants.MEMORY_GB_HOURS_USED, totalmemoryHours);
			chargeBackMap.put(WebConstants.CONFIGURED_MEMORY_COST, memoryCost);
			chargeBackMap.put(WebConstants.TOTAL_COST, totalCost);
			chargeBackMap.put(WebConstants.EXECUTION_ENGINE, executionEngine);
			chargeBackMap.put(WebConstants.JOB_ID, series.getTags().get(WebConstants.JOB_ID));
			chargeBackMap.put(WebConstants.JOB_NAME_1, series.getTags().get(WebConstants.JOB_NAME_1));
			chargeBackList.add(chargeBackMap);
		}
	}		

	private List<Map<String, Object>> parseDataForUserQueueUtilization(List<Series> seriesList,
			ClusterAnalysisMetrics metrics, RMCommunicator rmCommunicator)
					throws YarnException, IOException {

		Map<String, Map<String, Double>> usersMap = new HashMap<String, Map<String, Double>>();

		Map<String, Double> userMap = null;
		String userName, jobID;
		for (Series series : seriesList) {
			userName = series.getTags().get(WebConstants.USER_NAME);
			userMap = usersMap.get(userName);
			if (userMap == null) {
				userMap = new HashMap<String, Double>();
				usersMap.put(userName, userMap);
			}
			jobID = series.getTags().get(WebConstants.JOB_ID);
			for (List<String> row : series.getValues()) {
				userMap.put(jobID, Double.parseDouble(row.get(1)));
			}
		}

		Map<String, Long> jobsDuration = metrics.getJobsDuration(rmCommunicator);
		// jobsDuration
		// {job_1482214932026_0002=51735, job_1482214932026_0001=54594,
		// job_1482214932026_0003=10965}

		double temp;
		Long duration;
		Map<String, Object> temp1 = null;
		Map<String, Double> jobValues = null;

		List<Map<String, Object>> finalOutput = new ArrayList<Map<String, Object>>(usersMap.size());

		for (Entry<String, Map<String, Double>> individualUserMap : usersMap.entrySet()) {
			// individualUserMap
			// {user1={job_1482214932026_0002=7500.0,
			// job_1482214932026_0001=7500.0, job_1482214932026_0003=3000.0}}
			userName = individualUserMap.getKey();
			jobValues = individualUserMap.getValue();
			temp = 0;
			for (Entry<String, Double> jobValue : jobValues.entrySet()) {
				jobID = jobValue.getKey();
				duration = jobsDuration.get(jobID);
				if (duration == null) {
					continue;
				}
				temp += (jobValue.getValue() * duration) / 60000;
			}
			temp1 = new HashMap<String, Object>(2);
			temp1.put(WebConstants.USER_NAME, userName);
			temp1.put(WebConstants.RELATIVE_PERCENT, temp);
			finalOutput.add(temp1);
		}

		double totalSum = 0;
		for (Map<String, Object> map : finalOutput) {
			totalSum += (Double) map.get(WebConstants.RELATIVE_PERCENT);
		}

		if (totalSum == 0) {
			return Collections.emptyList();
		}

		for (Map<String, Object> map : finalOutput) {
			map.put(WebConstants.RELATIVE_PERCENT, ((Double) map.get(WebConstants.RELATIVE_PERCENT) * 100) / totalSum);
		}

		/*
		 * finalOutput json would be like [{"userName" : "user1",
		 * "relativePercentUsage" : 40.4}, {"userName" : "user2",
		 * "relativePercentUsage" : 19.6}, {"userName" : "user3",
		 * "relativePercentUsage" : 40.0}]
		 */
		return finalOutput;
	}

	/**
	 * Gets the charge back data.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the charge back data
	 * @throws Exception
	 *             the exception
	 */
	public List<Map<String, Object>> getchargeBackData(String clusterName, String month, String rangeFrom,
			String rangeTo) throws Exception {

		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			throw new Exception("Influxdb is down");
		}
		Set<String> executionEngines = new HashSet<>(2);
		ChargeBackConfigurations chargeBackConfigurations = AdminConfigurationUtil
				.getChargeBackConfiguration(clusterName);

		if (chargeBackConfigurations.getChargeBackConfList().isEmpty()) {
			executionEngines.add(WebConstants.MAPREDUCE);
			executionEngines.add(WebConstants.SPARK);
		} else {
			for (ChargeBackConf conf : chargeBackConfigurations.getChargeBackConfList()) {

				if (conf.getExecutionEngine().equalsIgnoreCase(ALL)) {
					executionEngines.add(WebConstants.MAPREDUCE);
					executionEngines.add(WebConstants.SPARK);
				} else {
					executionEngines.add(conf.getExecutionEngine());
				}

			}
		}
		List<Map<String, Object>> chargeBackList = new ArrayList<Map<String, Object>>();
		List<String> queueList = extractQueueName(configuration);
		for (String executionEngine : executionEngines) {
			for (String queueName : queueList) {
				Query query = new Query();
				query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
				query.addColumn(WebConstants.USED_CORES);
				query.addColumn(WebConstants.USED_MEMORY);
				query.addTag(WebConstants.QUEUE_NAME,
						queueName.replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
				query.addTag(WebConstants.EXECUTION_ENGINE, executionEngine);
				query.addGroupByColumn(WebConstants.USER_NAME);
				query.addGroupByColumn(WebConstants.JOB_ID);
				DateFormat df = new SimpleDateFormat(WebConstants.YYYY_MM_DD_HH_MM);
				Calendar calobj = Calendar.getInstance();
				if (month.equalsIgnoreCase(WebConstants.CURRENT_MONTH)) {
					calobj.set(Calendar.DAY_OF_MONTH, calobj.getActualMinimum(Calendar.DAY_OF_MONTH));
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					query.setRangeFrom(df.format(calobj.getTime()));
				} else if (month.equalsIgnoreCase(WebConstants.PREVIOUS_MONTH)) {
					calobj.add(Calendar.MONTH, -1);
					calobj.set(Calendar.DATE, 1);
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					Date firstDateOfPreviousMonth = calobj.getTime();
					calobj.set(Calendar.DATE, calobj.getActualMaximum(Calendar.DATE));
					calobj.set(Calendar.HOUR_OF_DAY, 24);
					calobj.set(Calendar.MINUTE, 0);
					Date lastDateOfPreviousMonth = calobj.getTime();
					query.setRange(df.format(firstDateOfPreviousMonth), df.format(lastDateOfPreviousMonth));
				} else {
					query.setRange(rangeFrom, rangeTo);
				}

				InfluxDataReader reader = new InfluxDataReader(query, configuration);

				ResultSet resultSet = reader.getResult();
				/*
				 * Result set will be like this
				 * ResultSet{"results":[{"series":[{"name":
				 * "userQueueUtilization","tags":{"jobId":
				 * "job_1487671879452_0003","userName":"impadmin"},
				 * "columns":["time","usedCores","usedMemory"],"values":[[
				 * "2017-02-21T13:09:20Z",1,3072],["2017-02-21T13:09:30Z",1,3072
				 * ],["2017-02-21T13:09:45Z",1,3072]
				 * ,["2017-02-21T13:10:05Z",6,5120],["2017-02-21T13:10:15Z",6,
				 * 5120],["2017-02-21T13:10:39Z",6,5120],["2017-02-21T13:10:49Z"
				 * ,6,5120],
				 * ["2017-02-21T13:11:05Z",6,5120],["2017-02-21T13:11:19Z",6,
				 * 5120],["2017-02-21T13:11:35Z",6,5120],["2017-02-21T13:11:51Z"
				 * ,6,5120]]}, {"name":"userQueueUtilization","tags":{"jobId":
				 * "job_1487830488079_0018","userName":"impadmin"},"columns":[
				 * "time","usedCores","usedMemory"]
				 * ,"values":[["2017-03-03T07:59:15Z",7,8192],[
				 * "2017-03-03T07:59:30Z",7,8192],["2017-03-03T07:59:45Z",2,3072
				 * ]]},{"name":"userQueueUtilization",
				 * "tags":{"jobId":"job_1488535948794_0001","userName":
				 * "impadmin"},"columns":["time","usedCores","usedMemory"],
				 * "values":[["2017-03-06T10:36:42Z",7,8192],[
				 * "2017-03-06T10:36:57Z",7,8192],["2017-03-06T10:37:12Z",5,6144
				 * ]]}
				 */
				if (resultSet.getError() != null) {
					throw new Exception(resultSet.getError());
				}

				List<Series> seriesList = resultSet.getResults().get(0).getSeries();
				if (seriesList == null) {
					continue;
				}
				ChargeBackConf conf = getConf(queueName, chargeBackConfigurations,executionEngine);
				if (conf != null) {
					parseDataForChargeBackQueueUtilization(seriesList,
							conf.getMemory(), conf.getvCore(), queueName, conf.getExecutionEngine(), chargeBackList);
				} else {
					parseDataForChargeBackQueueUtilization(seriesList, 0, 0,
							queueName, executionEngine, chargeBackList);
				}

			}
		}
		return chargeBackList;

	}

	/**
	 * Gets the charge back configuration based on the queueName.
	 *
	 * @param queueName
	 *            the queue name
	 * @param chargeBackConfigurations
	 *            the charge back configurations
	 * @param executionEngine 
	 * @return the conf
	 */
	public ChargeBackConf getConf(String queueName, ChargeBackConfigurations chargeBackConfigurations, String executionEngine) {
		for (ChargeBackConf conf : chargeBackConfigurations.getChargeBackConfList()) {
			List<String> executionList = new ArrayList<>(2);
			if (conf.getExecutionEngine().equalsIgnoreCase(ALL)) {
				executionList.add(WebConstants.MAPREDUCE);
				executionList.add(WebConstants.SPARK);
			}
			if(!executionList.isEmpty()){
				for (String executionEng : executionList) {
					if (conf.getQueueName().equals(queueName) && executionEng.equals(executionEngine)) {
						ChargeBackConf chargeBackConf = new ChargeBackConf();
						chargeBackConf.setQueueName(queueName);
						chargeBackConf.setExecutionEngine(executionEng);
						chargeBackConf.setMemory(conf.getMemory());
						chargeBackConf.setvCore(conf.getvCore());
						return chargeBackConf;
				}
				}
				
			}else if (conf.getQueueName().equals(queueName) && conf.getExecutionEngine().equals(executionEngine)) {
				return conf;
			}
		}
		return null;
	}

	/**
	 * This method extracts the queue name from USER_QUEUE_UTILIZATION .
	 *
	 * @param configuration
	 *            the configuration
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<String> extractQueueName(InfluxDBConf configuration) throws Exception {
		Query query = new Query();
		query.setCustomQuery(
				SHOW_TAG_VALUES_FROM + WebConstants.USER_QUEUE_UTILIZATION + WITH_KEY_QUEUE_NAME);
		InfluxDataReader reader = new InfluxDataReader(query, configuration);
		ResultSet resultSet = reader.getResult();
		List<String> list = new ArrayList<String>();
		List<Result> resultsList = resultSet.getResults();
		if (resultsList == null || resultsList.size() == 0) {
			return list;
		}
		List<Series> seriesList = resultsList.get(0).getSeries();
		if (seriesList == null || seriesList.size() == 0) {
			return list;
		}
		List<List<String>> temp = seriesList.get(0).getValues();
		if (temp == null) {
			return list;
		}
		for (List<String> temp1 : temp) {
			list.add(temp1.get(1));
		}
		return list;

	}


	/**
	 * Parses the data for charge back queue utilization.
	 *
	 * @param seriesList
	 *            the series list
	 * @param memoryCost
	 *            the memory cost
	 * @param vCoreCost
	 *            the v core cost
	 * @param queueName
	 *            the queue name
	 * @param chargeBackList2 
	 * @param string
	 * @return the map
	 * @throws ParseException
	 *             the parse exception
	 */
	private void parseDataForChargeBackQueueUtilization(List<Series> seriesList, double memoryCost,
			double vCoreCost, String queueName, String executionEngine, List<Map<String, Object>> chargeBackList) throws ParseException {

		double totalCores = 0;
		double totalMemory = 0;
		double vCore = 0;
		double memory = 0;
		HashSet<String> userNames = new HashSet<String>();
		Map<String, Object> chargeBackMap = new HashMap<String, Object>(7);
		for (Series series : seriesList) {
			userNames.add(series.getTags().get(USER_NAME));
		}
		for (String user : userNames) {
			for (Series series : seriesList) {
				if (user.equalsIgnoreCase(series.getTags().get(USER_NAME))) {
					if (!chargeBackMap.containsValue(user)) {
						totalCores = 0;
						totalMemory = 0;
						chargeBackMap = new HashMap<String, Object>(7);
					}
					for (List<String> row : series.getValues()) {
						vCore = Double.parseDouble(row.get(1));
						if (vCore >= 0) {
							totalCores += vCore;
						}
						memory = Double.parseDouble(row.get(2));
						if (memory >= 0) {
							totalMemory += memory;
						}
					}
					double memoryInGb = totalMemory / 1024.0;
					double totalmemoryHours = Double.parseDouble(String.format(_2F, ((memoryInGb * 15.0) / 3600)));
					double totalVcoreHours = Double.parseDouble(String.format(_2F, ((totalCores * 15.0) / 3600)));
					double totalmemoryCost = totalmemoryHours * memoryCost;
					double totalVCoresCost = totalVcoreHours * vCoreCost;
					double totalCost = Double.parseDouble(String.format(_2F, (totalmemoryCost + totalVCoresCost)));
					chargeBackMap.put(WebConstants.USER, user);
					chargeBackMap.put(WebConstants.QUEUE_NAME, queueName);
					chargeBackMap.put(WebConstants.V_CORE_HOURS_USED, totalVcoreHours);
					chargeBackMap.put(WebConstants.CONFIGURED_VCORE_COST, vCoreCost);
					chargeBackMap.put(WebConstants.MEMORY_GB_HOURS_USED, totalmemoryHours);
					chargeBackMap.put(WebConstants.CONFIGURED_MEMORY_COST, memoryCost);
					chargeBackMap.put(WebConstants.TOTAL_COST, totalCost);
					chargeBackMap.put(WebConstants.EXECUTION_ENGINE, executionEngine);

				}
			}

			chargeBackList.add(chargeBackMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> convertDataChargeBackModelStructure(List<Map<String, Object>> chargeBackList) {
		String user, queue, engine;
		Double userCost, queueCost, cost;
		Map<String, Object> finalOutput = new HashMap<>();
		Map<String, Object> userMap, queuesDetails, queueMap, engineDetails;
		
		for (Map<String, Object> userQueueEngineData : chargeBackList) {
			user = (String) userQueueEngineData.get(WebConstants.USER);
			queue = (String) userQueueEngineData.get(WebConstants.QUEUE_NAME);
			engine = (String) userQueueEngineData.get(WebConstants.EXECUTION_ENGINE);
			if (engine.equals(WebConstants.SPARK)) {
				engine = WebConstants.SPARK2;
			}
			if (engine.equals(WebConstants.MAPREDUCE)) {
				engine = WebConstants.MAP_REDUCE;
			}
			
			userMap = (Map<String, Object>) finalOutput.get(user);
			if (userMap == null) {
				userMap = new HashMap<>(2);
				finalOutput.put(user, userMap);
			}
			
			userCost = (Double) userMap.get(WebConstants.USER_COST);
			if (userCost == null) {
				userCost = new Double(0);
			}
			
			queuesDetails = (Map<String, Object>) userMap.get(WebConstants.QUEUES_DETAILS);
			if (queuesDetails == null) {
				queuesDetails = new HashMap<>();
				userMap.put(WebConstants.QUEUES_DETAILS, queuesDetails);
			}
			
			queueMap = (Map<String, Object>) queuesDetails.get(queue);
			if (queueMap == null) {
				queueMap = new HashMap<>(2);
				queuesDetails.put(queue, queueMap);
			}
			
			queueCost = (Double) queueMap.get(WebConstants.QUEUE_COST);
			
			if (queueCost == null) {
				queueCost = new Double(0);
				
			}
			
			engineDetails = (Map<String, Object>) queueMap.get(WebConstants.EXECUTION_ENGINE_DETAILS);
			if (engineDetails == null) {
				engineDetails = new HashMap<>(2);
				queueMap.put(WebConstants.EXECUTION_ENGINE_DETAILS, engineDetails);
			}
			
			userQueueEngineData.remove(WebConstants.USER);
			userQueueEngineData.remove(WebConstants.QUEUE_NAME);
			userQueueEngineData.remove(WebConstants.EXECUTION_ENGINE);
			
			cost = (Double) userQueueEngineData.get(WebConstants.TOTAL_COST);
			queueCost += cost;
			queueMap.put(WebConstants.QUEUE_COST, Double.parseDouble(String.format(_2F,queueCost)));
			userCost += cost;
			userMap.put(WebConstants.USER_COST, Double.parseDouble(String.format(_2F,userCost)));
			
			engineDetails.put(engine, userQueueEngineData);
			
		}
		
		return finalOutput;
	}
	
	/**
	 * Gets the detailed charge back data.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the charge back data
	 * @throws Exception
	 *             the exception
	 */
	public List<Map<String, Object>> getDetailedChargeBackData(Cluster cluster, String month, String rangeFrom,
			String rangeTo,RMCommunicator rmCommunicator) throws Exception {
		
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName());
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			throw new Exception("Influxdb is down");
		}
		Set<String> executionEngines = new HashSet<>(3);
		ChargeBackConfigurations chargeBackConfigurations = AdminConfigurationUtil
				.getChargeBackConfiguration(cluster.getClusterName());

		if (chargeBackConfigurations.getChargeBackConfList().isEmpty()) {
			executionEngines.add(WebConstants.MAPREDUCE);
			executionEngines.add(WebConstants.SPARK);
			executionEngines.add(WebConstants.TEZ);
		} else {
			for (ChargeBackConf conf : chargeBackConfigurations.getChargeBackConfList()) {

				if (conf.getExecutionEngine().equalsIgnoreCase(ALL)) {
					executionEngines.add(WebConstants.MAPREDUCE);
					executionEngines.add(WebConstants.SPARK);
					executionEngines.add(WebConstants.TEZ);
				} else {
					executionEngines.add(conf.getExecutionEngine());
				}

			}
		}
		
		String impersonationCheckUser = StringUtils.EMPTY ;
			//get the impersonated user
	//	 impersonationCheckUser = RemotingUtil.getHiveKerberosPrincipal(cluster);
		List<Map<String, Object>> chargeBackList = new ArrayList<Map<String, Object>>();
		List<String> queueList = extractQueueName(configuration);
		for (String executionEngine : executionEngines) {
			for (String queueName : queueList) {
				Query query = new Query();
				query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
				query.addColumn(WebConstants.USED_CORES);
				query.addColumn(WebConstants.USED_MEMORY);
				query.addTag(WebConstants.QUEUE_NAME,
						queueName.replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
				query.addTag(WebConstants.EXECUTION_ENGINE, executionEngine);
				query.addInequalities(WebConstants.USER_NAME, impersonationCheckUser);
				query.addGroupByColumn(WebConstants.USER_NAME);
				query.addGroupByColumn(WebConstants.JOB_NAME_1);
				query.addGroupByColumn(WebConstants.JOB_ID);
				DateFormat df = new SimpleDateFormat(WebConstants.YYYY_MM_DD_HH_MM);
				Calendar calobj = Calendar.getInstance();
				if (month.equalsIgnoreCase(WebConstants.CURRENT_MONTH)) {
					calobj.set(Calendar.DAY_OF_MONTH, calobj.getActualMinimum(Calendar.DAY_OF_MONTH));
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					query.setRangeFrom(df.format(calobj.getTime()));
				} else if (month.equalsIgnoreCase(WebConstants.PREVIOUS_MONTH)) {
					calobj.add(Calendar.MONTH, -1);
					calobj.set(Calendar.DATE, 1);
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					Date firstDateOfPreviousMonth = calobj.getTime();
					calobj.set(Calendar.DATE, calobj.getActualMaximum(Calendar.DATE));
					calobj.set(Calendar.HOUR_OF_DAY, 24);
					calobj.set(Calendar.MINUTE, 0);
					Date lastDateOfPreviousMonth = calobj.getTime();
					query.setRange(df.format(firstDateOfPreviousMonth), df.format(lastDateOfPreviousMonth));
				} else {
					query.setRange(rangeFrom, rangeTo);
				}
				LOGGER.debug("Detailed Charge back Query: " + query);
				InfluxDataReader reader = new InfluxDataReader(query, configuration);

				ResultSet resultSet = reader.getResult();
				/*
				 * Result set will be like this
				 * ResultSet{"results":[{"series":[{"name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0003","userName":"arpan"},
				 * "columns":["time","usedCores","usedMemory"],"values":[["2017-07-25T15:09:24Z",4,5120]]},{"name":"userQueueUtilization",
				 * "tags":{"jobId":"job_1500984547459_0004","userName":"arpan"},"columns":["time","usedCores","usedMemory"],
				 * "values":[["2017-07-25T15:25:01Z",2,3072]]},{"name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0005","userName":"arpan"}
				 * ,"columns":["time","usedCores","usedMemory"],"values":[["2017-07-25T15:30:31Z",4,5120]]}
				 * ,{"name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0006","userName":"arpan"},"columns":["time","usedCores","usedMemory"]
				 * ,"values":[["2017-07-26T09:14:05Z",7,8192],["2017-07-26T09:14:50Z",7,8192],["2017-07-26T09:15:00Z",7,8192]]},
				 * {"name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0007","userName":"arpan"},"columns":["time","usedCores","usedMemory"],
				 * "values":[["2017-07-26T09:17:30Z",1,2048],["2017-07-26T09:17:45Z",7,8192],["2017-07-26T09:18:00Z",7,8192],["2017-07-26T09:18:15Z",7,8192]
				 * ,["2017-07-26T09:18:30Z",7,8192],["2017-07-26T09:18:45Z",2,3072]]},{"name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0008","userName":"arpan"}
				 * ,"columns":["time","usedCores","usedMemory"],"values":[["2017-07-26T09:20:30Z",1,2048],["2017-07-26T09:20:46Z",7,8192],
				 * ["2017-07-26T09:21:00Z",7,8192],["2017-07-26T09:21:15Z",7,8192],["2017-07-26T09:21:30Z",7,8192],["2017-07-26T09:21:45Z",3,4096]]},{
				 * "name":"userQueueUtilization","tags":{"jobId":"job_1500984547459_0009","userName":"arpan"},"columns":["time","usedCores","usedMemory"]
				 *  */
				if (resultSet.getError() != null) {
					throw new Exception(resultSet.getError());
				}

				List<Series> seriesList = resultSet.getResults().get(0).getSeries();
				if (seriesList == null) {
					continue;
				}
				ChargeBackConf conf = getConf(queueName, chargeBackConfigurations,executionEngine);
				if (conf != null) {
					parseDataForDetailedChargeBackQueueUtilization(seriesList,
							conf.getMemory(), conf.getvCore(), queueName, conf.getExecutionEngine(), chargeBackList);
				} else {
					parseDataForDetailedChargeBackQueueUtilization(seriesList, 0, 0,
							queueName, executionEngine, chargeBackList);
				}

			}
		}
		return chargeBackList;

	}
	
	
	/**
	 * Gets the charge back data.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the charge back data
	 * @throws Exception
	 *             the exception
	 */
	public List<Map<String, Object>> getchargeBackData(Cluster cluster, String month, String rangeFrom,
			String rangeTo,RMCommunicator rmCommunicator) throws Exception {
		
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName());
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			throw new Exception("Influxdb is down");
		}
		Set<String> executionEngines = new HashSet<String>(3);
		ChargeBackConfigurations chargeBackConfigurations = AdminConfigurationUtil
				.getChargeBackConfiguration(cluster.getClusterName());

		if (chargeBackConfigurations.getChargeBackConfList().isEmpty()) {
			executionEngines.add(WebConstants.MAPREDUCE);
			executionEngines.add(WebConstants.SPARK);
			executionEngines.add(WebConstants.TEZ);
		} else {
			for (ChargeBackConf conf : chargeBackConfigurations.getChargeBackConfList()) {

				if (conf.getExecutionEngine().equalsIgnoreCase(ALL)) {
					executionEngines.add(WebConstants.MAPREDUCE);
					executionEngines.add(WebConstants.SPARK);
					executionEngines.add(WebConstants.TEZ);
				} else {
					executionEngines.add(conf.getExecutionEngine());
				}

			}
		}
		
		String impersonationCheckUser = StringUtils.EMPTY ;
//		 impersonationCheckUser = RemotingUtil.getHiveKerberosPrincipal(cluster);
		List<Map<String, Object>> chargeBackList = new ArrayList<Map<String, Object>>();
		List<String> queueList = extractQueueName(configuration);
		for (String executionEngine : executionEngines) {
			for (String queueName : queueList) {
				Query query = new Query();
				query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
				query.addColumn(WebConstants.USED_CORES);
				query.addColumn(WebConstants.USED_MEMORY);
				query.addTag(WebConstants.QUEUE_NAME,
						queueName.replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
				query.addTag(WebConstants.EXECUTION_ENGINE, executionEngine);
				query.addInequalities(WebConstants.USER_NAME, impersonationCheckUser);
				query.addGroupByColumn(WebConstants.USER_NAME);
				query.addGroupByColumn(WebConstants.JOB_ID);
				DateFormat df = new SimpleDateFormat(WebConstants.YYYY_MM_DD_HH_MM);
				Calendar calobj = Calendar.getInstance();
				if (month.equalsIgnoreCase(WebConstants.CURRENT_MONTH)) {
					calobj.set(Calendar.DAY_OF_MONTH, calobj.getActualMinimum(Calendar.DAY_OF_MONTH));
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					query.setRangeFrom(df.format(calobj.getTime()));
				} else if (month.equalsIgnoreCase(WebConstants.PREVIOUS_MONTH)) {
					calobj.add(Calendar.MONTH, -1);
					calobj.set(Calendar.DATE, 1);
					calobj.set(Calendar.HOUR_OF_DAY, 0);
					calobj.set(Calendar.MINUTE, 0);
					Date firstDateOfPreviousMonth = calobj.getTime();
					calobj.set(Calendar.DATE, calobj.getActualMaximum(Calendar.DATE));
					calobj.set(Calendar.HOUR_OF_DAY, 24);
					calobj.set(Calendar.MINUTE, 0);
					Date lastDateOfPreviousMonth = calobj.getTime();
					query.setRange(df.format(firstDateOfPreviousMonth), df.format(lastDateOfPreviousMonth));
				} else {
					query.setRange(rangeFrom, rangeTo);
				}
				LOGGER.debug("Charge back Query: " + query);
				InfluxDataReader reader = new InfluxDataReader(query, configuration);

				ResultSet resultSet = reader.getResult();
				/*
				 * Result set will be like this
				 * ResultSet{"results":[{"series":[{"name":
				 * "userQueueUtilization","tags":{"jobId":
				 * "job_1487671879452_0003","userName":"impadmin"},
				 * "columns":["time","usedCores","usedMemory"],"values":[[
				 * "2017-02-21T13:09:20Z",1,3072],["2017-02-21T13:09:30Z",1,3072
				 * ],["2017-02-21T13:09:45Z",1,3072]
				 * ,["2017-02-21T13:10:05Z",6,5120],["2017-02-21T13:10:15Z",6,
				 * 5120],["2017-02-21T13:10:39Z",6,5120],["2017-02-21T13:10:49Z"
				 * ,6,5120],
				 * ["2017-02-21T13:11:05Z",6,5120],["2017-02-21T13:11:19Z",6,
				 * 5120],["2017-02-21T13:11:35Z",6,5120],["2017-02-21T13:11:51Z"
				 * ,6,5120]]}, {"name":"userQueueUtilization","tags":{"jobId":
				 * "job_1487830488079_0018","userName":"impadmin"},"columns":[
				 * "time","usedCores","usedMemory"]
				 * ,"values":[["2017-03-03T07:59:15Z",7,8192],[
				 * "2017-03-03T07:59:30Z",7,8192],["2017-03-03T07:59:45Z",2,3072
				 * ]]},{"name":"userQueueUtilization",
				 * "tags":{"jobId":"job_1488535948794_0001","userName":
				 * "impadmin"},"columns":["time","usedCores","usedMemory"],
				 * "values":[["2017-03-06T10:36:42Z",7,8192],[
				 * "2017-03-06T10:36:57Z",7,8192],["2017-03-06T10:37:12Z",5,6144
				 * ]]}
				 */
				if (resultSet.getError() != null) {
					throw new Exception(resultSet.getError());
				}

				List<Series> seriesList = resultSet.getResults().get(0).getSeries();
				if (seriesList == null) {
					continue;
				}
				ChargeBackConf conf = getConf(queueName, chargeBackConfigurations,executionEngine);
				if (conf != null) {
					parseDataForChargeBackQueueUtilization(seriesList,
							conf.getMemory(), conf.getvCore(), queueName, conf.getExecutionEngine(), chargeBackList);
				} else {
					parseDataForChargeBackQueueUtilization(seriesList, 0, 0,
							queueName, executionEngine, chargeBackList);
				}

			}
		}
		return chargeBackList;

	}
	
	
	public void updateInfluxForHiveImpersonisation(Cluster cluster, RMCommunicator rmCommunicator) throws YarnException, IOException, Exception {
		
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(cluster.getClusterName());
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			throw new Exception("Influxdb is down");
		}
		
		// checkif security is enabled
		// Hive impersonation case
		// check for the impersonation enabled	
		String impersonationCheckUser = RemotingUtil.getHiveKerberosPrincipal(cluster);
		String jobId, actualUser;
		for (ApplicationReport applicationReport : rmCommunicator.getApplications()) {
		
			if (applicationReport.getUser().equalsIgnoreCase(impersonationCheckUser)) {
				if (applicationReport.getProgress() == 1.0f
						&& applicationReport.getFinalApplicationStatus().equals(FinalApplicationStatus.SUCCEEDED)) {
					jobId = rmCommunicator.getJobId(applicationReport).toString();
					actualUser = RemotingUtil.getHiveSubmissionUser(cluster, jobId, Constants.HIVE_ACCESS_SUBJECT_NAME);
					if (actualUser == null) {
						continue;
					}
					Query query = new Query();
					query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
					query.addColumn(WebConstants.EXECUTION_ENGINE);
					query.addColumn(WebConstants.JOB_ID);
					query.addColumn(WebConstants.JOB_NAME_1);
					query.addColumn(WebConstants.QUEUE_NAME);
					query.addColumn(WebConstants.USED_CORES);
					query.addColumn(WebConstants.USED_MEMORY);
					query.addColumn(WebConstants.USER_NAME);
					query.addTag(WebConstants.JOB_ID, jobId);
					InfluxDataReader reader = new InfluxDataReader(query, configuration);
					ResultSet resultSet = reader.getResult();
					if (resultSet.getError() != null) {
						throw new Exception(resultSet.getError());
					}
					List<Series> seriesList = resultSet.getResults().get(0).getSeries();
					if (seriesList == null) {
						continue;
					}
					InfluxDataWriter writer = new InfluxDataWriter(configuration);
					writer.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
					writer.setTimeUnit(TimeUnit.SECONDS);
					for (Series series : seriesList) {

						for (List<String> valuesList : series.getValues()) {
							try {
							writer.setTime(getFormattedDate(valuesList.get(0)));
							if (valuesList.get(1) != null) {
								writer.addTag(WebConstants.EXECUTION_ENGINE,valuesList.get(1));
							}
							writer.addTag(WebConstants.JOB_ID, valuesList.get(2));
							if (valuesList.get(3) != null) {
								writer.addTag(WebConstants.JOB_NAME_1,valuesList.get(3));
							}
							writer.addTag(WebConstants.QUEUE_NAME, valuesList.get(4));
							writer.addColumn(WebConstants.USED_CORES, valuesList.get(5));
							writer.addColumn(WebConstants.USED_MEMORY, valuesList.get(6));
							// write actual username
							writer.addTag(WebConstants.USER_NAME, actualUser);
							if (!actualUser.equalsIgnoreCase(valuesList.get(7))) {
								writer.writeData();
							}
							}catch(ParseException pe) {
								LOGGER.error("Unable to parse date ["+valuesList.get(0)+"] fetched from Influx Database", pe.getCause());
							}
						}
					}
					// remove the impersonised user details
					query.setCustomQuery(String.format(
							"DROP SERIES FROM userQueueUtilization where userName='%s' and jobId='%s'",
							applicationReport.getUser(), rmCommunicator.getJobId(applicationReport).toString()));
					reader.setQuery(query);
					reader.getResult();
				}
			}
		}
	}
	

	/**
	 * @param cluster
	 * @param subject
	 * @param queueStats
	 * @return This method returns the avergage waiting time of the configured queue in the past last hour
	 * @throws Exception
	 */
	public Map<String, Long> getAverageWaitingTime(Cluster cluster, List<QueueStats> queueStats) throws Exception {
		long waitingTime = 0 ;
		JobHistoryServerService jobHistoryServerService = new JobHistoryServerService();
		JobDetails jobDetails = jobHistoryServerService.getJobDetails(cluster);
		Map <String,List<Long>> queueData = new HashMap <String, List<Long>>();
		Map<String, Long> queueAvgWaiTime = new HashMap<String,Long>(queueStats.size());
		List<Long> waitingTimeList = new ArrayList<Long>();
		if(jobDetails.getJob() != null && !jobDetails.getJob().isEmpty()){
		for (org.jumbune.common.beans.JobInfo jobInfo : jobDetails.getJob()) {
			for (QueueStats queueStat : queueStats) {
				if(queueStat.getQueueName().equalsIgnoreCase(jobInfo.getQueue())){
					if(queueData.containsKey(queueStat.getQueueName())){
					 waitingTime = ((jobInfo.getStartTime()-jobInfo.getSubmitTime())/1000); // in secs
					 waitingTimeList.add(waitingTime);
					 queueData.put(queueStat.getQueueName(), waitingTimeList);
					}else{
						waitingTime = 0 ;
						waitingTime += ((jobInfo.getStartTime()- jobInfo.getSubmitTime())/1000); // in secs
						waitingTimeList = new ArrayList<>();
						waitingTimeList.add(waitingTime);
						queueData.put(queueStat.getQueueName(), waitingTimeList);
					}
					
				}
			}
		}}
		for (Map.Entry<String, List<Long>> entries : queueData.entrySet()) {
			long totalTime = 0;

			for (Long waitTime : entries.getValue()) {
				totalTime += waitTime;
			}
			queueAvgWaiTime.put(entries.getKey(), (totalTime / entries.getValue().size()));
		}
			for (QueueStats qStats : queueStats) {
			 // setting average waiting time of queues to zero in which the job has not been submitted for the past one hour 
				if(!queueAvgWaiTime.containsKey(qStats.getQueueName())){
					queueAvgWaiTime.put(qStats.getQueueName(), (long) 0);
				}
			}
		
		
		return  queueAvgWaiTime;

	}
	
	/**
	 * It converts the incoming influxdb data into data that we could send to UI
	 * so that it could display the graphs / charts
	 * 
	 * @param resultSet
	 *            influxdb data
	 * @param statName
	 * @param cluster 
	 * @param subject 
	 * @param scheduler 
	 * @return
	 * @throws Exception
	 */
	private Graph convertData(ResultSet resultSet, String statName, Cluster cluster) throws Exception {

		String error = resultSet.getError();

		if (error != null) {
			throw new Exception(error);
		}

		Graph graph = new Graph(statName);

		List<Series> seriesList = resultSet.getResults().get(0).getSeries();

		if (seriesList == null || seriesList.isEmpty()) {
			return graph;
		}
		
		Scheduler scheduler = null;
		List<FairSchedulerQueueInfo> fairSchedulerQueues = null;
		List<CapacitySchedulerQueueInfo> capacitySchedulerQueues = null;
		long clusterCapacity = 0l;
		
		if (statName.startsWith("usedResourcesMemoryPercent")) {
			scheduler = schedulerService.fetchSchedulerInfo(cluster);
			if (scheduler.isFairScheduler()) {
				fairSchedulerQueues = scheduler.getFairSchedulerLeafQueues();
			} else {
				capacitySchedulerQueues = scheduler.getCapcitySchedulerLeafQueues();
				ClusterMetrics clusterMetrics = schedulerService.fetchClusterMetrics(cluster);
				clusterCapacity = clusterMetrics.getTotalMB() ;
			}
		}
		
		for (Series series : seriesList) {
			String queueName = series.getTags().get(QUEUE_NAME);
			
			if (statName.startsWith("usedResourcesMemoryPercent")) {
				if (scheduler.isFairScheduler()) {
					long steadyFairShare = getSteadyFairShare(queueName, fairSchedulerQueues);
					if (steadyFairShare == 0) {
						LOGGER.error("Queue [" + queueName + "] doesn't exists, therefore skipping it.");
						continue;
					}
					for (List<String> row : series.getValues()) {
						graph.addPointInLine(queueName, getFormattedDate(row.get(0)),
								(long) ((Double.parseDouble(row.get(1)) * 100) / steadyFairShare) );
					}
				} else {
					float queueCapacityWRTCluster = getAbsoluteCapacity(queueName, capacitySchedulerQueues);
					if (queueCapacityWRTCluster == 0.0f) {
						LOGGER.error("Queue [" + queueName + "] doesn't exists, therefore skipping it.");
						continue;
					}
					for (List<String> row : series.getValues()) {
						graph.addPointInLine(queueName, getFormattedDate(row.get(0)),
								(long) ((Double.parseDouble(row.get(1)) * 10000.0) / (queueCapacityWRTCluster * clusterCapacity)) );
					}
				}
			} else {
				for (List<String> row : series.getValues()) {
					graph.addPointInLine(queueName, getFormattedDate(row.get(0)), (long) Double.parseDouble(row.get(1)));
				}
			}
			
			graph.removeLastPoint(queueName);
		}

		return graph;
	}

}
