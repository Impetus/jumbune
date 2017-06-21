package org.jumbune.clusterprofiling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBContext;

import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusterprofiling.yarn.beans.CapacitySchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.ClusterMetrics;
import org.jumbune.clusterprofiling.yarn.beans.FairSchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.Scheduler;
import org.jumbune.common.beans.cluster.Cluster;

/**
 * It is used to fetch fair scheduler queues information from api
 */
public class SchedulerService {

	private static final char NEW_LINE = '\n';
	private final String GET = "GET";
	private final String ACCEPT = "Accept";
	private final String APPLICATION_XML = "application/xml";
	
	public static volatile SchedulerService instance;
	
	private static final Logger LOGGER = LogManager.getLogger(SchedulerService.class);
	
	public static SchedulerService getInstance() {
		if (instance == null) {
			synchronized (SchedulerService.class) {
				if (instance == null) {
					instance = new SchedulerService();
				}
			}
		}
		return instance;
	}
	
	private SchedulerService() {
	}
	
	public boolean isFairScheduler(Cluster cluster) throws Exception {
		return fetchSchedulerInfo(cluster).isFairScheduler();
	}

	public List<FairSchedulerQueueInfo> getFairSchedulerLeafQueues(final Cluster cluster) throws Exception {
		
		return fetchSchedulerInfo(cluster).getFairSchedulerLeafQueues();
	}
	
	public List<CapacitySchedulerQueueInfo> getCapacitySchedulerLeafQueues(Scheduler scheduler) {
		return scheduler.getCapcitySchedulerLeafQueues();
	}

	/**
	 * It sends get request to http://rmHttpAddress:port/ws/v1/cluster/scheduler and fetches the response
	 * @param cluster
	 * @return Scheduler class object
	 * @throws Exception
	 */
	public Scheduler fetchSchedulerInfo(final Cluster cluster) throws Exception {
		
		HttpURLConnection connection = null;
		final URL url = new URL(cluster.getRMWebAppAddress() + "/ws/v1/cluster/scheduler");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(ACCEPT, APPLICATION_XML);
		connection.setRequestMethod(GET);
		
		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			String result = getString(connection.getInputStream());
			Scheduler scheduler = (Scheduler) JAXBContext.newInstance(Scheduler.class)
					.createUnmarshaller()
					.unmarshal(new StringReader(result));
			connection.disconnect();
			return scheduler;
		} else {
			throw new Error("Error while getting scheduler data. Response Code [" + responseCode + "]."
					+ getString(connection.getErrorStream()));
		}
	}
	
	public ClusterMetrics fetchClusterMetrics(final Cluster cluster) throws Exception {
		
		HttpURLConnection connection = null;
		final URL url = new URL(cluster.getRMWebAppAddress() + "/ws/v1/cluster/metrics");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(ACCEPT, APPLICATION_XML);
		connection.setRequestMethod(GET);
		
		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			String result = getString(connection.getInputStream());
			ClusterMetrics scheduler = (ClusterMetrics) JAXBContext.newInstance(ClusterMetrics.class)
					.createUnmarshaller()
					.unmarshal(new StringReader(result));
			connection.disconnect();
			return scheduler;
		} else {
			throw new Error("Error while getting scheduler data. Response Code [" + responseCode + "]."
					+ getString(connection.getErrorStream()));
		}
	}
	
	/**
	 * Get the string from input stream
	 * 
	 * @param inputStream
	 * @return string, but if error occurs return empty string
	 */
	private String getString(InputStream inputStream) {
		if (inputStream == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try(BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line).append(NEW_LINE);
				line = bufferedReader.readLine();
			}
			return sb.toString();
		} catch (IOException e) {
			LOGGER.error("Unable to get string from input stream.", e);
			return "";
		}
	}

}
