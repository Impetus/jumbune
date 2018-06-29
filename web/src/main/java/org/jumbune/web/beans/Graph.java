package org.jumbune.web.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.web.utils.WebConstants;

/**
 * It is used to create multiline timeseries graph/chart json
 */
@SuppressWarnings("unused")
public class Graph {

	private String graphName;

	private String unit;

	/**
	 * timeRange (time scale / x-axis) contains two values. 1st element is time
	 * that is minimum in all the lines and 2nd element is time that is maximum
	 * in all the lines. ie. lowerlimit and upperlimit of time among all the
	 * lines(points -> time).
	 */
	private long[] timeRange = { Long.MAX_VALUE, Long.MIN_VALUE };

	/**
	 * [Assuming Multiline chart] key represents line name and List<Point> are
	 * its values
	 */
	private Map<String, List<Point>> linesMap = new HashMap<>(1);

	// temporary varaible
	private transient List<Point> points;

	private class Point {

		private long time;
		private Object value;

		public Point(long time, Object value) {
			this.setTime(time);
			this.setValue(value);
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}
	}

	/**
	 * It will also set the unit of graph (y-axis).
	 * 
	 * @param graphName
	 */
	public Graph(String graphName) {
		this.graphName = graphName;
		this.unit = getUnit(graphName);
	}

	private static String getUnit(String statName) {
		String lastWord = statName.substring(statName.lastIndexOf(WebConstants.DOT) + 1, statName.length());
		String unit = JobRequestUtil.getClusterStatUnit(lastWord);
		if (unit != null) {
			return unit;
		} else {
			return WebConstants.NUMBER;
		}
	}

	/**
	 * Add a point in the line.
	 * 
	 * @param lineName
	 * @param time
	 * @param value
	 */
	public void addPointInLine(String lineName, long time, Object value) {

		if (!linesMap.containsKey(lineName)) {
			points = new ArrayList<>();
			linesMap.put(lineName, points);
		} else {
			points = linesMap.get(lineName);
		}
		points.add(new Point(time, value));
		updateTimeRange(time);
	}

	/**
	 * It removes the last point from the line if in the line, last point value
	 * is zero. If we don't, then at the end the line drops to zero.
	 * 
	 * @param lineName
	 */
	public void removeLastPoint(String lineName) {
		List<Point> points = linesMap.get(lineName);
		// Removing last value if it contains 0
		int pointsSize = points.size();
		if (pointsSize > 2 && ((long) (points.get(pointsSize - 1)).getValue() == 0)) {
			points.remove(pointsSize - 1);
		}
	}

	/**
	 * It will update time range (ie. minimum and maximum time).
	 */
	private void updateTimeRange(long time) {

		if (time < this.timeRange[0]) {
			this.timeRange[0] = time;
		}
		if (time > this.timeRange[1]) {
			this.timeRange[1] = time;
		}
	}
}
