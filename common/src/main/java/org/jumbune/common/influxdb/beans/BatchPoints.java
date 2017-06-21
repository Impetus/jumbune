package org.jumbune.common.influxdb.beans;

import java.util.List;
import java.util.ArrayList;

public class BatchPoints {

	private String database;
	
	private List<Point> points = new ArrayList<Point>();

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public void addPoint(Point point) {
		points.add(point);
	}
	
	@Override
	public String toString() {
		return "BatchPoints [database=" + database + ", points=" + points + "]";
	}

}
