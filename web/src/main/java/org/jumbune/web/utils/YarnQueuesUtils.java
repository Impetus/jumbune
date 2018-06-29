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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusteranalysis.beans.ClusterMetrics;
import org.jumbune.clusteranalysis.beans.JobQueueBean;
import org.jumbune.clusteranalysis.queues.SchedulerService;
import org.jumbune.clusteranalysis.queues.SchedulerUtil;
import org.jumbune.clusteranalysis.queues.beans.CapacitySchedulerQueueInfo;
import org.jumbune.clusteranalysis.queues.beans.FairSchedulerQueueInfo;
import org.jumbune.clusteranalysis.queues.beans.QueueStats;
import org.jumbune.clusteranalysis.queues.beans.Scheduler;
import org.jumbune.clusteranalysis.yarn.ClusterAnalysisMetrics;
import org.jumbune.common.beans.JobDetails;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.influxdb.InfluxDataReader;
import org.jumbune.common.influxdb.InfluxDataWriter;
import org.jumbune.common.influxdb.beans.InfluxDBConstants;
import org.jumbune.common.influxdb.beans.Query;
import org.jumbune.common.influxdb.beans.ResultSet;
import org.jumbune.common.influxdb.beans.ResultSet.Result.Series;
import org.jumbune.common.utils.JobHistoryServerService;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.web.beans.Graph;
import org.jumbune.web.services.ClusterAnalysisService;

/**
 * Utility functions related to Yarn Queues
 */
public class YarnQueuesUtils {

	private final String _6H = "6h";
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
	public Graph getQueueGraphFromInfluxdb(String clusterName, boolean isFairScheduler,
			String stat, String duration, String rangeFrom, String rangeTo) throws Exception {

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
		
		if (isFairScheduler) {
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
			fairSchedulerQueues = SchedulerUtil.getFairSchedulerLeafQueues(scheduler);
		} else {
			capacitySchedulerQueues = SchedulerUtil.getCapcitySchedulerLeafQueues(scheduler);
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
		if (list.isEmpty()) {
			return;
		}
		InfluxDBConf configuration = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		if (!InfluxDBUtil.isInfluxdbLive(configuration)) {
			return;
		}
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
		Query query = new Query();
		query.setTableName(WebConstants.USER_QUEUE_UTILIZATION);
		query.addColumn(stat);
		query.addTag(WebConstants.QUEUE_NAME,
				queueName.replaceAll(WebConstants.SPACE_REGEX, InfluxDBConstants.EMPTY_STRING));
		query.setAggregateFunction(MEAN);
		query.setDoNotAddGroupByTime(true);
		query.addInequalities(WebConstants.USER_NAME, "");
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
				fairSchedulerQueues = SchedulerUtil.getFairSchedulerLeafQueues(scheduler);
			} else {
				capacitySchedulerQueues = SchedulerUtil.getCapcitySchedulerLeafQueues(scheduler);
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
