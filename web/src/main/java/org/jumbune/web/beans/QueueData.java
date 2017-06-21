package org.jumbune.web.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * It contains queue utilization data of 24 hours.
 * @author impadmin
 *
 */
public class QueueData {
	
	/**
	 * dayData contains data of last 24 hours
	 * eg. {"14":39.70005,"15":40.25586,"16":55.0,"13":17.552734,"18":40.866455}
	 */
	private Map<String, Float> oneDayData = new TreeMap<String, Float>();
	
	/**
	 * lastHourPoints contains queue values of current hour (which is started from checkpoint)
	 */
	public List<Float> lastHourPoints = new ArrayList<Float>();

	public void addLastHourPoint(Float value) {
		lastHourPoints.add(value);
	}

	/**
	 * It calculates the average of lastHourPoints ie. values gathered in last hour. 
	 * @param hour
	 */
	public void putLastHourPointsInOneDayData(int hour) {
		Float sum = 0.0f;
		for (Float value : lastHourPoints) {
			sum += value;
		}
		getOneDayData().put(String.valueOf(hour), sum / lastHourPoints.size());
		lastHourPoints = new ArrayList<Float>();
	}

	public Map<String, Float> getOneDayData() {
		return oneDayData;
	}

	public void setOneDayData(Map<String, Float> oneDayData) {
		this.oneDayData = oneDayData;
	}
}
