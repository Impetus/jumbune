package org.jumbune.web.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;

import com.google.gson.Gson;
import org.jumbune.web.beans.ClusterQueueData;
import org.jumbune.web.beans.QueueData;
import org.jumbune.web.beans.QueueResponse;

public class MeteredQueueUsage {

	/**
	 * Directory in while all data related to queue utilization will be saved
	 */
	private static String METERED_QUEUE_DIR;

	private static final Logger LOGGER = LogManager.getLogger(MeteredQueueUsage.class);

	private static final Gson gson = new Gson();

	static {
		METERED_QUEUE_DIR = JumbuneInfo.getHome() + Constants.SYSTEM_STATS_DIR + File.separator
				+ "metredQueueUsage/";
		File file = new File(METERED_QUEUE_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * Return jsons for displaying graph of metred queue usage / queues
	 * utilization data
	 * 
	 * @param clusterName
	 * @return
	 */
	public String getGraphSpecificDataFromFile(String clusterName) {

		//
		ClusterQueueData clusterQueueData = getExistingClusterDataFromFile(clusterName);
		if (clusterQueueData == null) {
			return "[]";
		}
		
		// Creating json for client side (UI)
		Map<String, QueueData> queueDataMap = clusterQueueData.getQueueDataMap();
		List<QueueResponse> graphData = new ArrayList<QueueResponse>();
		for (Entry<String, QueueData> e : queueDataMap.entrySet()) {
			QueueData data = e.getValue();
			QueueResponse queueResponse = new QueueResponse(e.getKey());
			Map<String, Float> oneDayData = data.getOneDayData();
			for (int i = 0; i < 24; i++) {
				Float value = oneDayData.get(String.valueOf(i));
				if (value == null) {
					value = 0.0f;
				}
				queueResponse.addValue(String.valueOf(value), String.valueOf(i), e.getKey());
			}
			graphData.add(queueResponse);
		}
		return gson.toJson(graphData);
	}

	public void updateFileInJumbuneDir(String clusterName, List<QueueStats> list) {
		ClusterQueueData clusterQueueData = getExistingClusterDataFromFile(clusterName);
		if (clusterQueueData == null) {
			clusterQueueData = createNewClusterQueueData(clusterName, list);
		} else {
			updateClusterQueueData(clusterQueueData, list);
		}

		PrintWriter out = null;
		try {
			out = new PrintWriter(METERED_QUEUE_DIR + clusterQueueData.getClusterName() + WebConstants.JSON_EXTENSION);
			out.write(gson.toJson(clusterQueueData));
			out.flush();
		} catch (FileNotFoundException e) {
			LOGGER.error("Error while creating/updating file.", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Read file from folder and return ClusterQueueData object, if file not
	 * exists then return null
	 * 
	 * @param clusterName
	 * @return
	 */
	private ClusterQueueData getExistingClusterDataFromFile(String clusterName) {
		File file = new File(METERED_QUEUE_DIR + clusterName + WebConstants.JSON_EXTENSION);
		if (!file.exists()) {
			return null;
		}
		try {
			String json = FileUtils.readFileToString(file);
			return new Gson().fromJson(json, ClusterQueueData.class);
		} catch (IOException e) {
			LOGGER.error("Unable to read file.", e);
			return null;
		}
	}

	/**
	 * Update ClusterQueueData based on QueueStats list Algorithm : Step 1 :
	 * Check if there is time to update final points. Final points means points
	 * having
	 * 
	 * @param clusterQueueData
	 * @param list
	 */
	private void updateClusterQueueData(ClusterQueueData clusterQueueData, List<QueueStats> list) {

		boolean toUpdate = canUpdateFinalPoints(clusterQueueData);
		int hours = getHours(clusterQueueData);

		if (toUpdate) {
			clusterQueueData.setCheckpoint(new Date());
		}
		for (QueueStats queueStats : list) {
			QueueData queueData = clusterQueueData.getQueueDataFromMap(queueStats.getQueueName());
			if (queueData == null) {
				addNewQueue(clusterQueueData, queueStats);
			} else {
				if (toUpdate) {
					queueData.putLastHourPointsInOneDayData(hours + 1);
				}
				queueData.addLastHourPoint(queueStats.getCurrentCapacity());
			}
		}
	}

	/**
	 * It creates new ClusterQueueData, if not already existed before (if there
	 * is no file to read previous data)
	 * 
	 * @param clusterName
	 * @param list
	 * @return
	 */
	private ClusterQueueData createNewClusterQueueData(String clusterName, List<QueueStats> list) {
		ClusterQueueData clusterQueueData = new ClusterQueueData(new Date(), clusterName);
		clusterQueueData.setCheckpoint(new Date());
		for (QueueStats queueStats : list) {
			addNewQueue(clusterQueueData, queueStats);
		}
		return clusterQueueData;
	}

	/**
	 * It checks if checkpoint is 1 hour old, if yes then returns true otherwise
	 * returns false
	 * 
	 * @param clusterQueueData
	 * @return
	 */
	private boolean canUpdateFinalPoints(ClusterQueueData clusterQueueData) {
		Date oldDate = clusterQueueData.getCheckpoint();
		Date newDate = new Date();
		long diff = (newDate.getTime() - oldDate.getTime()) / 60000;
		if (diff >= 60) {
			return true;
		}
		return false;
	}
	
	/**
	 * Extract Hour from checkpoint
	 * @param clusterQueueData
	 * @return
	 */
	private int getHours(ClusterQueueData clusterQueueData) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(clusterQueueData.getCheckpoint());
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/** Add QueueData object for a queue in ClusterQueueData
	 * 
	 * @param clusterQueueData
	 * @param queueStats
	 */
	private void addNewQueue(ClusterQueueData clusterQueueData, QueueStats queueStats) {
		QueueData queueData = new QueueData();
		queueData.addLastHourPoint(queueStats.getCurrentCapacity());
		clusterQueueData.addQueueDataInMap(queueStats.getQueueName(), queueData);
	}

}
