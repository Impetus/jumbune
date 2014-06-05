package org.jumbune.profiling.beans;

import java.util.Map;


/**
 * The Class CategoryWiseInfo is a bean class for setting and retrieving stats according to the category.
 */
public class CategoryWiseInfo {

	private String category;
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
