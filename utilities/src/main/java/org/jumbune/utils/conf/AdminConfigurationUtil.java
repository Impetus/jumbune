package org.jumbune.utils.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.beans.AlertAction;
import org.jumbune.utils.conf.beans.AlertActionConfiguration;
import org.jumbune.utils.conf.beans.AlertConfiguration;
import org.jumbune.utils.conf.beans.AlertType;
import org.jumbune.utils.conf.beans.BackgroundProcessConfiguration;
import org.jumbune.utils.conf.beans.EmailConfiguration;
import org.jumbune.utils.conf.beans.HAConfiguration;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.conf.beans.ProcessType;
import org.jumbune.utils.conf.beans.SeverityLevel;
import org.jumbune.utils.conf.beans.SlaConf;
import org.jumbune.utils.conf.beans.SlaConfigurations;
import org.jumbune.utils.conf.beans.TicketConfiguration;

import com.google.gson.Gson;

public final class AdminConfigurationUtil {

	private static Gson gson = new Gson();

	public static String confPath;

	private static String defaultConfPath;

	private static Map<String, AlertActionConfiguration> alertActionConfiguration;

	private static Map<String, AlertConfiguration> alertConfiguration;

	private static Map<String, InfluxDBConf> influxdbMap;
	
	private static Map<String, SlaConfigurations> slaMap;
	
	private static Map<String, BackgroundProcessConfiguration> bpcMap;
	
	private static final Logger LOGGER = LogManager.getLogger(AdminConfigurationUtil.class);

	static {
		confPath = System.getenv(ConfigurationConstants.JUMBUNE_ENV_VAR_NAME) + File.separator
				+ ConfigurationConstants.CLUSTERS_CONFIGURATION_DIR;
		defaultConfPath = System.getenv(ConfigurationConstants.JUMBUNE_ENV_VAR_NAME) + File.separator
				+ ConfigurationConstants.DEFAULT_CONFIGURATION_DIR;
		alertActionConfiguration = new ConcurrentHashMap<>();
		alertConfiguration = new HashMap<>(2);
		influxdbMap = new HashMap<>(2);
		slaMap = new HashMap<>(2);
		bpcMap = new HashMap<>(2);
	}

	/**
	 * It copies all files (json files / configuration files) from
	 * jumbuneHome/conf/defaultConfigurations directory to
	 * jumbuneHome/conf/clusterConfigurations/clusterName/
	 * 
	 * @param clusterName
	 * @throws IOException
	 */
	public static void copyAllConfigurationsToClusterDir(String clusterName) throws IOException {
		File clusterDir = new File(confPath + clusterName);
		clusterDir.mkdirs();

		String defaultConfPath = System.getenv(ConfigurationConstants.JUMBUNE_ENV_VAR_NAME)
				+ ConfigurationConstants.DEFAULT_CONFIGURATION_DIR;
		File defaultDir = new File(defaultConfPath);

		FileUtils.copyDirectory(defaultDir, clusterDir);
	}

	public static void deleteConfigurations(String clusterName) throws IOException {
		FileUtils.forceDelete(new File(confPath + clusterName));
	}
	
	private static String getJson(String clusterName, String confFileName) {
		String path = confPath + clusterName + File.separator + confFileName;
		return readFileIntoString(path);
	}
	
	/**
	 * Read the contents of a file into String.
	 *
	 * @param path
	 *            the path
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static String readFileIntoString(String path) {
		try (FileInputStream stream = new FileInputStream(new File(path))) {
			FileChannel fileChannel = stream.getChannel();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			return Charset.defaultCharset().decode(mappedByteBuffer).toString();
		} catch (Exception e) {
			LOGGER.error("Unable to read the configuration file from path [" + path + "]");
			return null;
		}
	}
	

	/************************************** Alert **************************************/
	
	
	public static void saveAlertConfiguration(String clusterName, AlertConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.ALERT_CONFIGURATION_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
		alertConfiguration.put(clusterName, conf);
	}

	public static AlertConfiguration getAlertConfiguration(String clusterName) {
		AlertConfiguration alertConf = null;
		if ((alertConf = alertConfiguration.get(clusterName)) == null) {
			String json = getJson(clusterName, ConfigurationConstants.ALERT_CONFIGURATION_FILE);
			if (json == null) {
				alertConf = getDefaultAlertConfiguration();
			} else {
				alertConf = gson.fromJson(json, AlertConfiguration.class);
			}
			alertConfiguration.put(clusterName, alertConf);
		}
		return alertConf;
	}
	
	public static AlertConfiguration getDefaultAlertConfiguration() {
		String path = defaultConfPath + ConfigurationConstants.ALERT_CONFIGURATION_FILE;
		String json = readFileIntoString(path);
		if (json != null) {
			return gson.fromJson(json, AlertConfiguration.class);
		}
		AlertConfiguration alertConf = new AlertConfiguration();
		
		Map<AlertType, SeverityLevel> configurableAlerts = new HashMap<>(4);
		configurableAlerts.put(AlertType.HDFS_UTILIZATION, new SeverityLevel(75, 90));
		configurableAlerts.put(AlertType.QUEUE_UTILIZATION, new SeverityLevel(60, 80));
		configurableAlerts.put(AlertType.UNDER_REPLICATED_BLOCKS, new SeverityLevel(60, 80));
		configurableAlerts.put(AlertType.DISK_SPACE_UTILIZATION, new SeverityLevel(75, 90));
		alertConf.setConfigurableAlerts(configurableAlerts);
		
		Map<AlertType, Boolean> nonConfigurableAlerts = new HashMap<>(10);
		nonConfigurableAlerts.put(AlertType.CLUSTER_TIME_DESYNC, true);
		nonConfigurableAlerts.put(AlertType.MAP_REDUCE_APP_FAILURE, true);
		nonConfigurableAlerts.put(AlertType.DN_VOLUME_FAILURE_CHECK, true);
		nonConfigurableAlerts.put(AlertType.HADOOP_DAEMON_DOWN, true);
		nonConfigurableAlerts.put(AlertType.CONTAINER_POOL_UTILIZATION, true);
		nonConfigurableAlerts.put(AlertType.YARN_PROPERTY_CHECK, true);
		nonConfigurableAlerts.put(AlertType.EXCESSIVE_RESOURCE_ALLOCATION, true);
		nonConfigurableAlerts.put(AlertType.RESOURCE_UTILIZATION_CHECK, true);
		nonConfigurableAlerts.put(AlertType.NODE_UNHEALTHY, true);
		nonConfigurableAlerts.put(AlertType.QUEUE_CHILD_CAPACITY_OVERFLOW, true);
		alertConf.setNonConfigurableAlerts(nonConfigurableAlerts);
		
		alertConf.setIndividualQueueAlerts(new HashMap<String, SeverityLevel>(1));
		
		alertConf.setUpdateInterval(20);
		
		alertConf.setHdfsDirPaths(new ArrayList<String>(1));
		
		return alertConf;
	}
	
	
	/*********************************** Alert Action ***********************************/
	
	
	public static void saveAlertActionConfiguration(String clusterName, AlertActionConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.ALERT_ACTION_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
		alertActionConfiguration.put(clusterName, conf);
	}

	public static AlertActionConfiguration getAlertActionConfiguration(String clusterName) {
		AlertActionConfiguration actionConfiguration = null;
		if ((actionConfiguration = alertActionConfiguration.get(clusterName)) != null) {
			return actionConfiguration;
		} else {
			String json =  getJson(clusterName, ConfigurationConstants.ALERT_ACTION_FILE);
			if (json != null) {
				actionConfiguration = gson.fromJson(json, AlertActionConfiguration.class);
				alertActionConfiguration.put(clusterName, actionConfiguration);
			} else {
				actionConfiguration = new AlertActionConfiguration();
				actionConfiguration.setAlertActions(new ArrayList<AlertAction>(1));
			}
		}
		return actionConfiguration;
	}
	
	
	/************************* Background Process Configuration *************************/
	
	
	public static void  saveBackgroundProcessConfiguration(String clusterName, BackgroundProcessConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.BACKGROUND_PROCESS_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
		bpcMap.put(clusterName, conf);
	}
	
	public static BackgroundProcessConfiguration getBackgroundProcessConfiguration(String clusterName) {
		BackgroundProcessConfiguration conf = null;
		if ((conf = bpcMap.get(clusterName)) == null) {
			String json = getJson(clusterName, ConfigurationConstants.BACKGROUND_PROCESS_FILE);
			if (json != null) {
				conf = gson.fromJson(json, BackgroundProcessConfiguration.class);
			} else {
				conf = getDefaultBackgroundProcessConfiguration();
			}
			bpcMap.put(clusterName, conf);
		}
		
		return conf;
	}
	
	public static BackgroundProcessConfiguration getDefaultBackgroundProcessConfiguration() {
		String path = defaultConfPath + ConfigurationConstants.BACKGROUND_PROCESS_FILE;
		String json = readFileIntoString(path);
		if (json != null) {
			return gson.fromJson(json, BackgroundProcessConfiguration.class);
		}
		BackgroundProcessConfiguration conf = new BackgroundProcessConfiguration();
		conf.addProcess(ProcessType.QUEUE_UTILIZATION, false);
		conf.addProcess(ProcessType.SYSTEM_METRICS, false);
		conf.addProcess(ProcessType.WORKER_NODES_UPDATER, true);
		return conf;
	}
	

	/************************************** Email **************************************/
	
	
	public static void saveEmailConfiguration(String clusterName, EmailConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.EMAIL_CONFIGURATION_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
	}

	public static EmailConfiguration getEmailConfiguration(String clusterName) {
		String json = getJson(clusterName, ConfigurationConstants.EMAIL_CONFIGURATION_FILE);
		if (json != null) {
			return gson.fromJson(json, EmailConfiguration.class);
		}
		EmailConfiguration conf = new EmailConfiguration();
		return conf;
	}
	

	/****************************** HA (High Availability ******************************/
	
	
	public static void saveHAConfiguration(String clusterName, HAConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.HA_CONFIGURATION_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));

	}

	public static HAConfiguration getHAConfiguration(String clusterName) {
		String json = getJson(clusterName, ConfigurationConstants.HA_CONFIGURATION_FILE);
		if (json == null) {
			return getDefaultHAConfiguration();
		} else {
			return gson.fromJson(json, HAConfiguration.class);
		}
	}
	
	public static HAConfiguration getDefaultHAConfiguration() {
		String path = defaultConfPath + ConfigurationConstants.HA_CONFIGURATION_FILE;
		String json = readFileIntoString(path);
		if (json != null) {
			return gson.fromJson(json, HAConfiguration.class);
		}
		HAConfiguration conf = new HAConfiguration();
		conf.setAgentConnMillis(5000);
		conf.setCommandLogDir("");
		conf.setHeartBeatMillis(5000);
		conf.setNumRetriesAgentConn(10);
		conf.setNumRetriesRemoterApis(3);
		conf.setThresholdBeatsToMiss(4);
		return conf;
	}
	

	/************************************* Influxdb *************************************/
	
	
	public static void saveInfluxdbConfiguration(String clusterName, InfluxDBConf conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.INFLUXDB_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
		influxdbMap.put(clusterName, conf);
	}

	public static InfluxDBConf getInfluxdbConfiguration(String clusterName) {

		InfluxDBConf influxdbConf = influxdbMap.get(clusterName);
		if (influxdbConf == null) {
			String json =  getJson(clusterName, ConfigurationConstants.INFLUXDB_FILE);
			
			if (json != null) {
				influxdbConf = gson.fromJson(json, InfluxDBConf.class);
			} else {
				influxdbConf = getDefaultInfluxdbConfiguration();
			}
			
			influxdbMap.put(clusterName, influxdbConf);
			
		}
		return influxdbConf;
	}
	
	public static InfluxDBConf getDefaultInfluxdbConfiguration() {
		String path = defaultConfPath + ConfigurationConstants.INFLUXDB_FILE;
		String json = readFileIntoString(path);
		if (json != null) {
			return gson.fromJson(json, InfluxDBConf.class);
		}
		
		InfluxDBConf influxdbConf = new InfluxDBConf();
		influxdbConf.setDatabase("");
		influxdbConf.setHost("localhost");
		influxdbConf.setPassword("98z6dNFZGz/Elzi79nctrA\u003d\u003d"); //root
		influxdbConf.setPort("8086");
		influxdbConf.setRetentionPeriod("90");
		influxdbConf.setUsername("root");
		return influxdbConf;
	}
	
	/*************************************** SLA ***************************************/
	
	
	public static void saveSlaConfigurations(String clusterName, SlaConfigurations conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.SLA_CONFIGURATIONS;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
		slaMap.put(clusterName, conf);
	}
	
	public static SlaConfigurations getSlaConfigurations(String clusterName) {
		SlaConfigurations slaConfigurations = slaMap.get(clusterName);
		if (slaConfigurations == null) {
			String json = getJson(clusterName, ConfigurationConstants.SLA_CONFIGURATIONS);
			if (json != null) {
				slaConfigurations = gson.fromJson(json, SlaConfigurations.class);
				slaMap.put(clusterName, slaConfigurations);
			} else {
				slaConfigurations = new SlaConfigurations();
				slaConfigurations.setSlaConfList(new ArrayList<SlaConf>(1));
			}
			
		}
		return slaConfigurations;
	}
	

	/************************************* Ticket **************************************/
	
	
	public static void saveTicketConfiguration(String clusterName, TicketConfiguration conf) throws IOException {
		String path = confPath + clusterName + File.separator + ConfigurationConstants.TICKET_CONFIGURATION_FILE;
		FileUtils.writeStringToFile(new File(path), gson.toJson(conf));
	}

	public static TicketConfiguration getTicketConfiguration(String clusterName) {
		String json = getJson(clusterName, ConfigurationConstants.TICKET_CONFIGURATION_FILE);
		if (json != null) {
			return gson.fromJson(json, TicketConfiguration.class);
		} else {
			return new TicketConfiguration();
			
		}
	}

}
