package org.jumbune.common.influxdb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jumbune.common.influxdb.beans.InfluxDBConstants;
import org.jumbune.common.influxdb.beans.Query;
import org.jumbune.common.influxdb.beans.ResultSet;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.conf.beans.InfluxDBConf;

public class InfluxDataReader {

	private Query query;

	private InfluxDBConf configuration;

	public InfluxDataReader() {
	}

	public InfluxDataReader(Query query, InfluxDBConf configuration) {
		this.setQuery(query);
		this.setConfiguration(configuration);
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setConfiguration(InfluxDBConf configuration) {
		this.configuration = configuration;
	}

	private String sendGetRequest() throws Exception {

		// Creating url
		URIBuilder uriBuilder = new URIBuilder();
		int port = Integer.parseInt(configuration.getPort());
		uriBuilder.setScheme("http").setHost(configuration.getHost()).setPort(port).setPath("/query")
				.setParameter("db", configuration.getDatabase())
				.setParameter("u", configuration.getUsername())
				.setParameter("p", configuration.getDecryptedPassword())
				.setParameter("q", query.toString());

		HttpGet httpget = new HttpGet(uriBuilder.build());
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(httpget);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = null;
		String output = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			output = output + reader.readLine();

		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		httpclient.close();
		return output;
	}

	public ResultSet getResult() throws Exception {
		String json = sendGetRequest();
		
		return Constants.gson.fromJson(json, InfluxDBConstants.resultSetType);
	}

}
