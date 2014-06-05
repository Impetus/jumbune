package org.jumbune.profiling.healthview;

/**
 * The Class TaskTrackerStatsInfo is a bean class for storing and retrieving the map and reduce tasks slots
 */
public class TaskTrackerStatsInfo implements ResultInfo {

	private int mapTaskSlots;
	private int reduceTaskSlots;

	/**
	 * @return the mapTaskSlots
	 */
	public int getMapTaskSlots() {
		return mapTaskSlots;
	}

	/**
	 * @param mapTaskSlots
	 *            the mapTaskSlots to set
	 */
	public void setMapTaskSlots(int mapTaskSlots) {
		this.mapTaskSlots = mapTaskSlots;
	}

	/**
	 * @return the reduceTaskSlots
	 */
	public int getReduceTaskSlots() {
		return reduceTaskSlots;
	}

	/**
	 * @param reduceTaskSlots
	 *            the reduceTaskSlots to set
	 */
	public void setReduceTaskSlots(int reduceTaskSlots) {
		this.reduceTaskSlots = reduceTaskSlots;
	}

	/**
	 * @return the runningMaps
	 */
	public int getRunningMaps() {
		return runningMaps;
	}

	/**
	 * @param runningMaps
	 *            the runningMaps to set
	 */
	public void setRunningMaps(int runningMaps) {
		this.runningMaps = runningMaps;
	}

	/**
	 * @return the runningReduces
	 */
	public int getRunningReduces() {
		return runningReduces;
	}

	/**
	 * @param runningReduces
	 *            the runningReduces to set
	 */
	public void setRunningReduces(int runningReduces) {
		this.runningReduces = runningReduces;
	}

	private int runningMaps;
	private int runningReduces;
}
