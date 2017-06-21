package org.jumbune.common.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;

import org.jumbune.common.beans.RMInfo;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;

/**
 * This service is responsible for fetching the Resource Manager information
 *
 */
public class RMInfoService {

	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_XML = "application/xml";
	private static final String GET = "GET";
	private static final String ACTIVE = "ACTIVE";
	private static final Logger LOGGER = LogManager.getLogger(RMInfoService.class);

	private String activeRM = null;

	private long lastChecked = 0;
	private EnterpriseClusterDefinition cluster;
	private String rmWebAppProtocol = null;
	private String rmWebAppPort = null;
	private String rmPort = null;

	public RMInfoService(EnterpriseClusterDefinition cluster) {
		this.cluster = cluster;
	}

	/**
	 * Returns the hostname string of the current active resource manager
	 * @return
	 */
	public String getActiveResourceMananager() {
		if (!cluster.getTaskManagers().isRmHaEnabled()) {
			return cluster.getTaskManagers().getHosts().get(0);
		}
		if (this.activeRM == null) {
			synchronized(this) {
				if (this.activeRM == null) {
					try {
						this.activeRM = fetchActive();
						if (this.activeRM == null) {
							throw new Exception("Couldn't connect to any resource manager");
						}
					} catch (Exception e) {
						LOGGER.error("Unable to fetch active resource manager", e);
						this.activeRM = cluster.getTaskManagers().getHosts().get(0);
						LOGGER.info("Assuming Resource Manager [" + this.activeRM+ "]");
					}
					lastChecked = System.currentTimeMillis();
				} else {
					return this.activeRM;
				}
			}
		} else if (System.currentTimeMillis() - lastChecked > 10000) {
			lastChecked = System.currentTimeMillis();
			try {
				this.activeRM = fetchActive();
				if (this.activeRM == null) {
					throw new Exception("Couldn't connect to any resource manager");
				}
			} catch (Exception e) {
				LOGGER.error("Unable to fetch active resource manager", e);
				this.activeRM = cluster.getTaskManagers().getHosts().get(0);
				LOGGER.info("Assuming Resource Manager [" + this.activeRM+ "]");
			}
		}
		return this.activeRM;
	}


	/**
	 * It sends get request to http://rmHttpAddress:port/ws/v1/cluster/info and
	 * fetches the response
	 * 
	 * @param cluster
	 * @return
	 * @returnjson [JobInfo]
	 * @throws Exception
	 */
	private String fetchActive() {
		HttpURLConnection connection = null;
		StringBuilder builder;
		URL url;
		
		boolean isActiveRMInitialized = false;
		// Checking if previous active RM is already active
		if (this.activeRM != null) {
			try {
				builder = new StringBuilder(getRMWebAppProtocol()).append(this.activeRM)
						.append(Constants.COLON).append(getRMWebAppPort()).append("/ws/v1/cluster/info");
				url = new URL(builder.toString());
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestProperty(ACCEPT, APPLICATION_XML);
				connection.setRequestMethod(GET);

				url = new URL(builder.toString());
				int responseCode = connection.getResponseCode();
				if (responseCode == 200) {
					RMInfo rmInfo = (RMInfo) JAXBContext.newInstance(RMInfo.class).createUnmarshaller()
							.unmarshal(connection.getInputStream());
					connection.disconnect();
					if (rmInfo.getHaState().equalsIgnoreCase(ACTIVE)) {
						return this.activeRM;
					}
				}
			} catch (Exception e) {
				LOGGER.debug("Unable to check whether Resource Manager [" + this.activeRM + "] is active or not", e);
			}
			isActiveRMInitialized = true;
		}

		for (final String host : cluster.getTaskManagers().getHosts()) {
			if (isActiveRMInitialized && this.activeRM.equals(host)) {
				continue;
			}
			try {
				builder = new StringBuilder(getRMWebAppProtocol()).append(host)
						.append(":").append(getRMWebAppPort()).append("/ws/v1/cluster/info");

				url = new URL(builder.toString());

				// if (subject == null) {
				connection = (HttpURLConnection) url.openConnection();
				// }
				// TODO : for secured cluster
				/*
				 * else { connection = Subject.doAs(subject, new
				 * PrivilegedExceptionAction<HttpURLConnection>() {
				 * 
				 * @Override public HttpURLConnection run() throws Exception {
				 * AuthenticatedURL.Token token = new AuthenticatedURL.Token(); return new
				 * AuthenticatedURL().openConnection(url, token); }
				 * 
				 * }); }
				 */

				connection.setRequestProperty(ACCEPT, APPLICATION_XML);
				connection.setRequestMethod(GET);

				int responseCode = connection.getResponseCode();
				if (responseCode == 200) {
					RMInfo rmInfo = (RMInfo) JAXBContext.newInstance(RMInfo.class).createUnmarshaller()
							.unmarshal(connection.getInputStream());
					connection.disconnect();
					if (rmInfo.getHaState().equalsIgnoreCase(ACTIVE)) {
						return host;
					}

				}
			} catch (Exception e) {
				LOGGER.debug("Unable to check whether Resource Manager [" + host+ "] is active or not", e);
			}
		}
		return null;
	}
	
	public String getRMWebAppProtocol() {
		if (this.rmWebAppProtocol == null) {
			String httpPolicy = RemotingUtil.getHadoopConfigurationValue(this.cluster, ExtendedConstants.YARN_SITE_XML,
					"yarn.http.policy");
			
			if (httpPolicy != null && "HTTPS_ONLY".equals(httpPolicy)) {
				this.rmWebAppProtocol = "https://";
			} else {
				this.rmWebAppProtocol = "http://";
			}
		}
		return this.rmWebAppProtocol;
	}
	
	public String getRMWebAppPort() {
		if (this.rmWebAppPort == null) {
			String address;
			if (getRMWebAppProtocol().equals("https://")) {
				address = RemotingUtil.getHadoopConfigurationValue(
						this.cluster, ExtendedConstants.YARN_SITE_XML, "yarn.resourcemanager.webapp.https.address");
				if (address == null) {
					Configuration conf = new Configuration();
					conf.addResource("yarn-default.xml");
					address = conf.get("yarn.resourcemanager.webapp.https.address");
				}
			} else {
				address = RemotingUtil.getHadoopConfigurationValue(
						this.cluster, ExtendedConstants.YARN_SITE_XML, "yarn.resourcemanager.webapp.address");
				if (address == null) {
					Configuration conf = new Configuration();
					conf.addResource("yarn-default.xml");
					address = conf.get("yarn.resourcemanager.webapp.address");
				}
				
			}
			this.rmWebAppPort = address.split(Constants.COLON)[1];
		}
		return this.rmWebAppPort;
	}
	
	public String getRMPort() {
		if (this.rmPort == null) {
			Configuration c = new Configuration();
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(this.cluster);
			RemotingUtil.addHadoopResource(c, this.cluster, hadoopConfDir, "yarn-site.xml");
			RemotingUtil.addHadoopResource(c, this.cluster, hadoopConfDir, "mapred-site.xml");

			//reading history server's address
			String address = c.get("yarn.resourcemanager.address");
			
			if ( address != null && !address.trim().isEmpty()) {
				this.rmPort = address.split(Constants.COLON)[1];
			} else {
				this.rmPort = "8032";
			}
		}
		return this.rmPort;
	}

}
