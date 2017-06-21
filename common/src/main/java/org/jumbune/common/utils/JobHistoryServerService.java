package org.jumbune.common.utils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBContext;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.RemotingUtil;

import org.jumbune.common.beans.JobDetails;

public class JobHistoryServerService {

	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_XML = "application/xml";
	private static final String GET = "GET";
	private Map<String, URL> mrURLs;

	public JobHistoryServerService() {
		mrURLs = new HashMap<>(3);
	}

	/**
	 * It sends get request to
	 * http://mrHttpAddress:port/ws/v1/history/mapreduce/jobs and fetches the
	 * response
	 * 
	 * @param cluster
	 * @return
	 * @returnjson [JobInfo]
	 * @throws Exception
	 */
	public JobDetails getJobDetails(final Cluster cluster) throws Exception {

		HttpURLConnection connection = null;
		connection = (HttpURLConnection) getURL(cluster).openConnection();

		connection.setRequestProperty(ACCEPT, APPLICATION_XML);
		connection.setRequestMethod(GET);

		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			JobDetails jobDetails = (JobDetails) JAXBContext.newInstance(JobDetails.class).createUnmarshaller()
					.unmarshal(connection.getInputStream());
			connection.disconnect();
			return jobDetails;

		} else {
			throw new Error("Error while job data Response Code [" + responseCode + "]."
					+ IOUtils.toString(connection.getErrorStream()));
		}
	}

	private URL getURL(Cluster cluster) throws MalformedURLException {

		URL url = mrURLs.get(cluster.getClusterName());
		if (url == null) {
			String httpPolicy = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.MAPRED_SITE_XML,
					"mapreduce.jobhistory.http.policy");
			if (httpPolicy == null) {
				httpPolicy = "HTTP_ONLY";
			}

			StringBuilder builder = new StringBuilder();

			if ("HTTPS_ONLY".equals(httpPolicy)) {

				String address = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.MAPRED_SITE_XML,
						"mapreduce.jobhistory.webapp.address");
				if (address == null) {
					Configuration conf = new Configuration();
					conf.addResource("mapred-default.xml");
					address = conf.get("mapreduce.jobhistory.webapp.address");
				}
				builder.append("https://");
				if (address.startsWith("0.0.0.0")) {
					String port = address.split(":")[1];
					address = cluster.getResourceManager();
					builder.append(address).append(":").append(port);
				} else {
					builder.append(address);
				}

			} else {

				String address = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.MAPRED_SITE_XML,
						"mapreduce.jobhistory.webapp.address");
				if (address == null) {
					Configuration conf = new Configuration();
					conf.addResource("mapred-default.xml");
					address = conf.get("mapreduce.jobhistory.webapp.address");
				}
				builder.append("http://");
				if (address.startsWith("0.0.0.0")) {
					String port = address.split(":")[1];
					address = cluster.getResourceManager();
					builder.append(address).append(":").append(port);
				} else {
					builder.append(address);
				}

			}
			
			long currentTime = System.currentTimeMillis();
			long pastOneDay = currentTime - (86400000);
			builder.append("/ws/v1/history/mapreduce/jobs?startedTimeBegin=" + pastOneDay + "&startedTimeEnd="
					+ currentTime);

			url = new URL(builder.toString());
			mrURLs.put(cluster.getClusterName(), url);
		}
		return url;
	}

}
