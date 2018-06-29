package org.jumbune.common.influxdb;

import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_P_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_Q_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.COLON;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.CREATE_DATABASE;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.HTTP;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.INSUFFICIENT_INFORMATION_TO_WRITE_DATA;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.SLASH_QUERY;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.beans.InfluxDBConf;

public class InfluxDBUtil {

	private static final String X_INFLUXDB_VERSION = "X-Influxdb-Version";
	private static final String MOZILLA_5_0 = "Mozilla/5.0";
	private static final String USER_AGENT = "User-Agent";
	private static final String GET = "GET";
	private static final String PING = "/ping";
	private static final Logger LOGGER = LogManager.getLogger(InfluxDBUtil.class);
	
	// key is hostname
	private static Map<String, Boolean> isInfluxdbLive = new HashMap<>(4);

	public static void writeClusterChartDataForNodeSpecific(InfluxDBConf configuration,
			String nodeIP, Map<String, String> values) throws Exception {
		
		if (nodeIP == null) {
			LOGGER.warn(INSUFFICIENT_INFORMATION_TO_WRITE_DATA);
			return;
		} else if (values == null) {
			LOGGER.warn(INSUFFICIENT_INFORMATION_TO_WRITE_DATA, nodeIP);
			return;
		} else if (values.isEmpty()) {
			LOGGER.debug("No data to write to Influxdb");
			return;
		}

		InfluxDataWriter writer = new InfluxDataWriter(configuration);
		writer.setTableName(nodeIP);
		writer.setTimeUnit(TimeUnit.SECONDS);
		writer.setTime(System.currentTimeMillis() / 1000);

		for (Entry<String, String> e : values.entrySet()) {
			try {
				writer.addColumn(e.getKey(), Double.parseDouble(e.getValue()));
			} catch (NumberFormatException ex) {
				LOGGER.error("Could write stat [" + e.getKey() + "] in influxdb because its not a number");
			}
		}

		writer.writeData();
	}

	public static void writeClusterChartDataForClusterWide(InfluxDBConf configuration,
			List<String> nodeIPs, List<Map<String, String>> columns) throws Exception {

		InfluxDataWriter writer = new InfluxDataWriter(configuration);
		writer.setTimeUnit(TimeUnit.SECONDS);
		writer.setTime(System.currentTimeMillis() / 1000);

		for (int i = 0; i < nodeIPs.size(); i++) {
			writer.setTableName(nodeIPs.get(i));

			for (Entry<String, String> e : columns.get(i).entrySet()) {
				writer.addColumn(e.getKey(), Double.parseDouble(e.getValue()));
			}

			writer.writeData();
		}
	}

	/**
	 * Create database in influxdb according to configuration provided
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	public static void createDatabase(InfluxDBConf configuration) throws Exception {
		if (configuration == null) {
			return;
		}
		StringBuffer url = new StringBuffer();
		url.append(HTTP).append(configuration.getHost().trim()).append(COLON)
				.append(configuration.getPort()).append(SLASH_QUERY);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url.toString());
		httpPost.setEntity(new StringEntity(CREATE_DATABASE + configuration.getDatabase() + "\"",
				ContentType.APPLICATION_FORM_URLENCODED));
		httpClient.execute(httpPost);
		httpClient.close();
		createRetentionPolicy(configuration);
		updateRetentionPolicy(configuration);
	}

	/**
	 * Create database in influxdb according to configuration provided
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	public static void dropDatabase(InfluxDBConf configuration) throws Exception {

		StringBuffer url = new StringBuffer();
		url.append(HTTP).append(configuration.getHost().trim()).append(COLON)
				.append(configuration.getPort()).append("/query?u=")
				.append(configuration.getUsername()).append(AND_P_EQUAL_TO)
				.append(configuration.getDecryptedPassword()).append(AND_Q_EQUAL_TO)
				.append("drop%20database%20" + configuration.getDatabase());

		URL obj = new URL(url.toString());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod(GET);
		con.setRequestProperty(USER_AGENT, MOZILLA_5_0);
		con.getContent();
		con.disconnect();
	}

	/**
	 * Create database in influxdb according to configuration provided
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	public static void createRetentionPolicy(InfluxDBConf configuration) {
		try {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(configuration.getHost().trim()).append(COLON)
					.append(configuration.getPort()).append("/query?u=")
					.append(configuration.getUsername()).append(AND_P_EQUAL_TO)
					.append(configuration.getDecryptedPassword()).append(AND_Q_EQUAL_TO)
					.append("CREATE%20RETENTION%20POLICY%20ret_" + configuration.getDatabase()
							+ "%20on%20" + configuration
									.getDatabase()
							+ "%20DURATION%2090d%20REPLICATION%201%20DEFAULT");

			URL obj = new URL(url.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(GET);
			con.setRequestProperty(USER_AGENT, MOZILLA_5_0);
			con.getContent();
			con.disconnect();
		} catch (Exception e) {
			LOGGER.error("Unable to create retention policy in influxdata for database + "
					+ configuration.getDatabase());
		}
	}

	public static void updateRetentionPolicy(InfluxDBConf configuration) {
		try {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(configuration.getHost().trim()).append(COLON)
					.append(configuration.getPort()).append("/query?u=")
					.append(configuration.getUsername()).append(AND_P_EQUAL_TO)
					.append(configuration.getDecryptedPassword()).append(AND_Q_EQUAL_TO);

			url.append("ALTER%20RETENTION%20POLICY%20ret_" + configuration.getDatabase()
					+ "%20on%20" + configuration.getDatabase() + "%20DURATION%20"
					+ configuration.getRetentionPeriod() + "d");

			URL obj = new URL(url.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(GET);
			con.setRequestProperty(USER_AGENT, MOZILLA_5_0);
			con.getContent();
			con.disconnect();
		} catch (Exception e) {
			LOGGER.error("Unable to update retention policy in influxdata for database + "
					+ configuration.getDatabase());
		}
	}
	
	public static boolean isInfluxdbLive(InfluxDBConf configuration) {
		Boolean isAlive = isInfluxdbLive.get(configuration.getHost());
		if (isAlive == null) {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(configuration.getHost()).append(COLON).append(configuration.getPort()).append(PING);
			HttpURLConnection con = null;
			try {
				URL obj = new URL(url.toString());
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod(GET);
				con.setRequestProperty(USER_AGENT, MOZILLA_5_0);
				if (con.getHeaderField(X_INFLUXDB_VERSION) != null) {
					isAlive = true;
				} else {
					isAlive = false;
				}
			} catch (Exception e) {
				isAlive = false;
			} finally {
				if (con != null) {
					con.disconnect();
				}
			}
			isInfluxdbLive.put(configuration.getHost(), isAlive);
		}
		return isAlive;
		
	}

}