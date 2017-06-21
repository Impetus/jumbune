package org.jumbune.common.influxdb.beans;

import java.util.Map;
import java.util.HashMap;

public class Point {
	private String measurement;
	
	private Map<String, Object> fields = new HashMap<String, Object>();
	
	private String precision = "s";
	
	private Long time;

	public Point(String measurement) {
		this.measurement = measurement;
		time = System.currentTimeMillis() / 1000;
	}

	public Point(String measurement, Long time) {
		this.measurement = measurement;
		this.time = time;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public void addField(String column, Double value) {
		fields.put(column, value);
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	// In seconds
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
