package org.jumbune.web.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.remoting.common.StringUtil;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.ConfigurationConstants;
import org.jumbune.utils.conf.beans.BackgroundProcessConfiguration;
import org.jumbune.utils.conf.beans.EmailConfiguration;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.conf.beans.ProcessType;
import org.jumbune.utils.conf.beans.TicketConfiguration;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.web.beans.AdminConfiguration;
import org.jumbune.web.process.BackgroundProcessManager;
import org.jumbune.web.utils.SessionUtils;
import org.jumbune.web.utils.WebConstants;

@Path(WebConstants.ADMIN_CONFIGURATION_URL)
public class AdminConfigurationService {
	
	@Context
	private HttpServletRequest servletRequest;
	
	private SessionUtils sessionUtils = SessionUtils.getInstance();

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(AdminConfigurationService.class);

	@GET
	@Path("/clusterconfiguration/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterConfigurations(@PathParam("clusterName") final String clusterName) {
		try {
			checkAndCreateConfAndInfluxDatabase(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to create configuration files for cluster [" + clusterName + "]");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
			
		}
			
			AdminConfiguration admin = new AdminConfiguration();
			admin.setAlertActionConfiguration(
					AdminConfigurationUtil.getAlertActionConfiguration(clusterName));
			admin.setAlertConfiguration(
						AdminConfigurationUtil.getAlertConfiguration(clusterName));
			admin.setEmailConfiguration(
						AdminConfigurationUtil.getEmailConfiguration(clusterName));
			admin.setHaConfiguration(AdminConfigurationUtil.getHAConfiguration(clusterName));
			admin.setInfluxDBConfiguration(
						AdminConfigurationUtil.getInfluxdbConfiguration(clusterName));
			admin.setTicketConfiguration(
						AdminConfigurationUtil.getTicketConfiguration(clusterName));
			admin.setSlaConfigurations(AdminConfigurationUtil.getSlaConfigurations(clusterName));
			admin.setBackgroundProcessConfiguration(
						AdminConfigurationUtil.getBackgroundProcessConfiguration(clusterName));
			
			return Response.ok(Constants.gson.toJson(admin)).build();
	}
	
	@GET
	@Path("/cluster-queues-list/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterQueuesList(@PathParam("clusterName") String clusterName) {
		return Response.ok(Constants.gson.toJson(getQueueDetails(clusterName))).build();
	}

	public List<String> getQueueDetails(String clusterName) {		
		List<String> listOfQueuesFromCluster = new ArrayList<String>();
		Cluster cluster = null;
		try {
			cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		} catch (IOException e) {
			LOGGER.error("Unable to get Cluster.", e.getMessage());
			return listOfQueuesFromCluster;
		}
		HttpSession session = servletRequest.getSession();
		try {
			RMCommunicator rmCommunicator = sessionUtils.getRM(cluster, session);
			
			addQueue(rmCommunicator.getQueueInfo(ExtendedConstants.ROOT), listOfQueuesFromCluster);
		} catch (Exception e) {
			LOGGER.error("Unable to create RMCommunicator for Cluster [ " + cluster.getClusterName()
					+ " ], resource manager address [ " + cluster.getRMSocketAddress() + " ]", e);
		}
		return listOfQueuesFromCluster;
	}	
	
	private void addQueue(QueueInfo queueInfo, List<String> list) {
		list.add(queueInfo.getQueueName());
		if (queueInfo.getChildQueues() != null) {
			for (QueueInfo childQueue : queueInfo.getChildQueues()) {
				addQueue(childQueue, list);
			}
		}
	}
	
	@GET
	@Path("/cluster-leaf-queues-list/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterLeafQueuesList(@PathParam("clusterName") String clusterName) {
		return Response.ok(Constants.gson.toJson(getLeafQueueDetails(clusterName))).build();
	}
	
	private List<String> getLeafQueueDetails(String clusterName) {
		List<String> listOfQueuesFromCluster = new ArrayList<String>();
		Cluster cluster = null;
		try {
			cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		} catch (IOException e) {
			LOGGER.error("Unable to get Cluster.", e.getMessage());
			return listOfQueuesFromCluster;
		}
		try {
			HttpSession session = servletRequest.getSession();
			RMCommunicator rmCommunicator = sessionUtils.getRM(cluster, session);
			
			addLeafQueue(rmCommunicator.getQueueInfo(ExtendedConstants.ROOT), listOfQueuesFromCluster);
		} catch (Exception e1) {
			LOGGER.error("Unable to create RMCommunicator, Resource Manager Address: " + cluster.getRMSocketAddress());
		}
		
		return listOfQueuesFromCluster;
	}
	
	private void addLeafQueue(QueueInfo queueInfo, List<String> list) {
		
		if (queueInfo.getChildQueues() == null || queueInfo.getChildQueues().size() == 0) {
			// If leaf queue then
			list.add(queueInfo.getQueueName());
		} else {
			// If not leaf queue
			for (QueueInfo childQueue : queueInfo.getChildQueues()) {
				addLeafQueue(childQueue, list);
			}
		}
	}
	
	/**
	 * This method first check if the configuration files exists of particular
	 * cluster or not. If not exists then it generates configuration files and
	 * then create influx database (database name is cluster name) and update
	 * the influxdb conf file (influxdb.json). If configuration already exists
	 * then it checks if database field in influxdb conf is null or empty.
	 * 
	 * If null or empty then it set the database name with cluster name and
	 * create the database later.
	 * 
	 * @param clusterName
	 * @throws Exception
	 */
	public static void checkAndCreateConfAndInfluxDatabase(String clusterName) throws Exception {

		File clusterConfigurationsDir = new File(JumbuneInfo.getHome()
				+ ConfigurationConstants.CLUSTERS_CONFIGURATION_DIR + clusterName);

		InfluxDBConf influxDBConf = null;

		// Checking if cluster configuration directory exists or not
		if (!clusterConfigurationsDir.exists()) {
			// If not exists

			AdminConfigurationUtil.copyAllConfigurationsToClusterDir(clusterName);

			influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);

			influxDBConf.setDatabase(clusterName);

			AdminConfigurationUtil.saveInfluxdbConfiguration(clusterName, influxDBConf);
		} else {
			influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
			if (influxDBConf.getDatabase() == null || influxDBConf.getDatabase().trim().isEmpty()) {
				influxDBConf.setDatabase(clusterName);
				AdminConfigurationUtil.saveInfluxdbConfiguration(clusterName, influxDBConf);
			}
		}
		try {
			InfluxDBUtil.createDatabase(influxDBConf);
		} catch (Exception e) {
			LOGGER.warn("It seems that influx DB is not installed. Not creating database");
		}
	}
	
	public static void enableWorkersNodeUpdater(Cluster cluster) {
		if (!cluster.getWorkers().isSpotInstances()) {
			return;
		}
		String clusterName = cluster.getClusterName();
		try {
			BackgroundProcessConfiguration bpc = AdminConfigurationUtil
					.getBackgroundProcessConfiguration(clusterName);
			if (bpc == null) {
				checkAndCreateConfAndInfluxDatabase(clusterName);
				bpc = AdminConfigurationUtil
						.getBackgroundProcessConfiguration(clusterName);
			}
			bpc.getProcessMap().put(ProcessType.WORKER_NODES_UPDATER, true);
			AdminConfigurationUtil.saveBackgroundProcessConfiguration(clusterName, bpc);
			BackgroundProcessManager.getInstance().updateProcesses(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to update cluster configurations of cluster [" + clusterName + "]", e.getMessage());
		}
	}

	@POST
	@Path("/saveclusterconfigurations")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response saveClusterConfigurations(AdminConfiguration admin) {
		String clusterName = admin.getClusterName();

		try {

			// Checking if configuration received is null or not, if null then
			// delete the configuration file from that cluster. It is because if
			// the user set option "use default" in UI then server will not
			// receive the json/object of that configuration and server will
			// delete the configuration file from the cluster so that we could
			// use default configuration of that cluster
			AdminConfigurationUtil.saveAlertConfiguration(clusterName, admin.getAlertConfiguration());

			AdminConfigurationUtil.saveAlertActionConfiguration(clusterName,
					admin.getAlertActionConfiguration());

			AdminConfigurationUtil.saveHAConfiguration(clusterName, admin.getHaConfiguration());

			EmailConfiguration emailConf = admin.getEmailConfiguration();

			EmailConfiguration oldEmailConf = AdminConfigurationUtil
					.getEmailConfiguration(clusterName);

			if (emailConf.getSenderPassword() != null
					&& (oldEmailConf.getSenderPassword() == null || !emailConf
					.getSenderPassword().equals(oldEmailConf.getSenderPassword()))) {
				emailConf.setSenderPassword(
						StringUtil.getEncrypted(emailConf.getSenderPassword()));
			}

			AdminConfigurationUtil.saveEmailConfiguration(clusterName, emailConf);

			InfluxDBConf influxDBConf = admin.getInfluxDBConfiguration();
			if (influxDBConf.getDatabase() == null
					|| influxDBConf.getDatabase().trim().isEmpty()) {
				influxDBConf.setDatabase(clusterName);
			}

			InfluxDBConf oldInfluxDBConf = AdminConfigurationUtil
					.getInfluxdbConfiguration(clusterName);

			if (influxDBConf.getPassword() != null && (oldInfluxDBConf.getPassword() == null
					|| !influxDBConf.getPassword().equals(oldInfluxDBConf.getPassword()))) {
				influxDBConf.setPassword(StringUtil.getEncrypted(influxDBConf.getPassword()));
			}

			AdminConfigurationUtil.saveInfluxdbConfiguration(clusterName, influxDBConf);

			try {
				InfluxDBUtil.createDatabase(influxDBConf);
			} catch (Exception e) {
				LOGGER.warn("It seems that influx DB is not installed");
			}

			TicketConfiguration ticketConfiguration = admin.getTicketConfiguration();

			TicketConfiguration oldTicketConfiguration = AdminConfigurationUtil
					.getTicketConfiguration(clusterName);

			if (ticketConfiguration.getPassword() != null
					&& (oldTicketConfiguration.getPassword() == null || !ticketConfiguration
					.getPassword().equals(oldTicketConfiguration.getPassword()))) {
				ticketConfiguration.setPassword(
						StringUtil.getEncrypted(ticketConfiguration.getPassword()));
			}
			AdminConfigurationUtil.saveTicketConfiguration(clusterName, ticketConfiguration);
			
			AdminConfigurationUtil.saveSlaConfigurations(clusterName, admin.getSlaConfigurations());

			BackgroundProcessConfiguration bpc = admin.getBackgroundProcessConfiguration();
			
				AdminConfigurationUtil.saveBackgroundProcessConfiguration(clusterName, bpc);
				/*boolean isQueuesProcessEnabled = bpc.getProcessMap().get(ProcessType.QUEUE_UTILIZATION).booleanValue();
				Cluster cluster = ClusterAnalysisService.cache.getCluster(clusterName);
				
				if (bpc.getProcessMap().get(ProcessType.SYSTEM_METRICS)) {
					StatsManager.getInstance().startBackgroundProcess(ClusterAnalysisService.cache.getCluster(clusterName));
				} else {
					StatsManager.getInstance().stopBackgroundProcess(ClusterAnalysisService.cache.getCluster(clusterName));
				}*/
				BackgroundProcessManager.getInstance().updateProcesses(clusterName);

			return Response.ok("success").build();

		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}

	/*
	 * private void deleteFile(String confFileName, String clusterName) { String
	 * confFilePath = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME) +
	 * ConfigurationConstants.CLUSTERS_CONFIGURATION_DIR + clusterName +
	 * File.separator + confFileName; File confFile = new File(confFilePath); if
	 * (confFile.exists()) { confFile.delete(); } }
	 */

	@GET
	@Path("/defaultclusterconfiguration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefaultClusterConfigurations() {
		AdminConfiguration admin = new AdminConfiguration();
		try {
			/*
			 * admin.setAlertActionConfiguration(
			 * AdminConfigurationUtil.getDefaultAlertActionConfiguration());
			 * 
			 * admin.setEmailConfiguration(AdminConfigurationUtil.
			 * getDefaultEmailConfiguration());
			 */

			admin.setAlertConfiguration(AdminConfigurationUtil.getDefaultAlertConfiguration());

			admin.setHaConfiguration(AdminConfigurationUtil.getDefaultHAConfiguration());

			admin.setInfluxDBConfiguration(
					AdminConfigurationUtil.getDefaultInfluxdbConfiguration());
			
			admin.setBackgroundProcessConfiguration(
					AdminConfigurationUtil.getDefaultBackgroundProcessConfiguration());

			return Response.ok(Constants.gson.toJson(admin)).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}

	/**
	 * 
	 * @param confFileName
	 *            confFileName is the file name of configuration like
	 *            alertActionConfiguration.json, emailConfiguration.json
	 * @return json
	 */
	@GET
	@Path("/defaultclusterconfiguration/{confFileName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefaultConfiguration(@PathParam("confFileName") final String confFileName) {
		try {
			String path = JumbuneInfo.getHome()
					+ ConfigurationConstants.DEFAULT_CONFIGURATION_DIR + confFileName;
			return Response.ok(FileUtil.readFileIntoString(path)).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}

}
