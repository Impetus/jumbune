package org.jumbune.profiling.utils;

import java.util.Map;

/**
 * The Class StatsVO sets and gets the category.
 */
public class StatsVO {

	/** The category. */
	private String category;
	
	/** The stats. */
	private Map<String, String> stats;

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the stats
	 */
	public Map<String, String> getStats() {
		return stats;
	}

	/**
	 * @param stats
	 *            the stats to set
	 */
	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}

}
