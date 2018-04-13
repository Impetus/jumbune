package org.jumbune.common.influxdb;

import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_DB_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_PRECISION_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_P_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.COLON;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.COMMA;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.EQUAL;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.HTTP;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.SPACE;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.WRITE_U;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jumbune.common.influxdb.beans.InfluxDBConstants;
import org.jumbune.common.influxdb.beans.ResultSet;
import org.jumbune.common.influxdb.beans.TimeUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.conf.beans.InfluxDBConf;

import com.google.gson.Gson;

public class InfluxDataWriter {

	private InfluxDBConf configuration;
	
	private String tableName;
	
	private Map<String, Object> fields;
	
	private Map<String, String> tags;
	
	private TimeUnit timeUnit;
	
	private Long time;

	public InfluxDataWriter(InfluxDBConf configuration) {
		this.configuration = configuration;
	}
	
	public void writeData() throws Exception {
		if (tableName == null || tableName.isEmpty() || fields == null || fields.isEmpty()) {
		//	throw new Exception(INSUFFICIENT_INFORMATION_TO_WRITE_DATA);
			return;
		}
		
		// Creating String/data =
		//  tableName,tagKey1=tagValue1,tagKey2=tagValue2 column1=value1,column2=value2,column3=value3 timestamp
		StringBuffer sb = new StringBuffer(tableName);
		
		if (tags != null && !tags.isEmpty()) {
			for (Entry<String, String> e : tags.entrySet()) {
				sb.append(COMMA).append(e.getKey()).append(EQUAL).append(e.getValue());
			}
		}
		sb.append(SPACE);
		Entry<String, Object> e;

		Iterator<Entry<String, Object>> it = fields.entrySet().iterator();
		if (it.hasNext()) {
			e = it.next();
			sb.append(e.getKey()).append(EQUAL).append(String.valueOf(e.getValue()));
		}
		while (it.hasNext()) {
			e = it.next();
			sb.append(COMMA).append(e.getKey()).append(EQUAL).append(String.valueOf(e.getValue()));
		}
		sb.append(SPACE);
		if (time != null) {
			sb.append(time);
		} else {
			sb.append(System.currentTimeMillis() / 1000);
		}
		
		// Sending data
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(getURL());
			httpPost.setEntity(new StringEntity(sb.toString(), ContentType.DEFAULT_BINARY));
			httpClient.execute(httpPost);
			
			CloseableHttpResponse response = httpClient.execute(httpPost);
			
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != 204) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ResultSet resultSet = new Gson().fromJson(EntityUtils.toString(responseEntity), ResultSet.class);
					String error = resultSet.getError();
					if (error != null && !error.isEmpty()) {
						if (error.toLowerCase().contains("database not found")) {
							InfluxDBUtil.createDatabase(configuration);
						} else {
							throw new Exception("Influxdb Exception : Problem while writing data into database, " + error);
						}
					}
				}
			}
			
		} finally {
			fields.clear();
			if (tags != null) {
				tags.clear();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	// create and get url to send post request
	private String getURL() throws Exception {
		if (timeUnit == null) {
			setTimeUnit(TimeUnit.SECONDS);
		}
		String host = configuration.getHost();
		String port = configuration.getPort();
		String username = configuration.getUsername();
		String password = configuration.getDecryptedPassword();
		String database = configuration.getDatabase();
		StringBuffer url = new StringBuffer();
		url.append(HTTP).append(host.trim()).append(COLON).append(port)
			.append(WRITE_U).append(username)
			.append(AND_P_EQUAL_TO).append(password)
			.append(AND_DB_EQUAL_TO).append(database)
			.append(AND_PRECISION_EQUAL_TO).append(TimeUtil.toTimePrecision(timeUnit));
		return url.toString();
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}
	
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	public void addTag(String tagKey, String tagValue) {
		if (tags == null) {
			tags = new HashMap<String, String>();
		}
		tags.put(tagKey, tagValue.replaceAll(InfluxDBConstants.REGEX4, InfluxDBConstants.REGEX3)
				.replaceAll(Constants.COMMA, InfluxDBConstants.REGEX2)
				.replaceAll(Constants.SPACE_REGEX, InfluxDBConstants.REGEX1));
	}

	public void setTime(Long time, TimeUnit timeUnit) {
		this.time = time;
		this.setTimeUnit(timeUnit);
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void addColumn(String columnName, Object value) {
		if (fields == null) {
			fields = new HashMap<String, Object>();
		}
		fields.put(columnName, value);
	}
	
	/*
	@Deprecated
	// Can be used with influxdb version 9.1 or before, uses json protocol to write data
	private void writeDataUsingJson() throws Exception {
		if (tableName == null || tableName.isEmpty() || fields == null || fields.isEmpty()) {
			throw new Exception(INSUFFICIENT_INFORMATION_TO_WRITE_DATA);
		}
		if (timeUnit == null) {
			setTimeUnit(TimeUnit.SECONDS);
		}
		Point point = new Point(tableName);
		point.setFields(fields);
		point.setPrecision(TimeUtil.toTimePrecision(timeUnit));
		point.setTime(time);
		BatchPoints batchPoints = new BatchPoints();
		batchPoints.addPoint(point);
		batchPoints.setDatabase(configuration.getDatabase());
		Gson gson = new Gson();
		String json = gson.toJson(batchPoints);
		String url = getURL();
		// OkHttpClient -> jar is com.squareup.okhttp
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(MediaType.parse(APPLICATION_JSON_CHARSET_UTF_8), json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();

		BufferedReader reader = null;
		String line = EMPTY_STRING;
		StringBuffer reponseJSON = new StringBuffer();
		if (response != null) {
			try {
				reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
				while ((line = reader.readLine()) != null) {
					reponseJSON.append(line);
				}
				String temp = reponseJSON.toString();
				if (!temp.trim().isEmpty()) {
					throw new Exception(temp);
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
	}
	*/

}