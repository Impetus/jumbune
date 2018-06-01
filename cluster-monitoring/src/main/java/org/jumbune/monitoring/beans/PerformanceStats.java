package org.jumbune.monitoring.beans;

/**
 * Pojo to store profiling stat,corresponding category and good and bad parameters criteria for the stat
 * 
 */
public class PerformanceStats {

	private String stat;
	private String category;
	private PerformanceEval good;
	private PerformanceEval bad;

	/**
	 * @return the good
	 */
	public PerformanceEval getGood() {
		return good;
	}

	/**
	 * @param good
	 *            the good to set
	 */
	public void setGood(PerformanceEval good) {
		this.good = good;
	}

	/**
	 * @return the bad
	 */
	public PerformanceEval getBad() {
		return bad;
	}

	/**
	 * @param bad
	 *            the bad to set
	 */
	public void setBad(PerformanceEval bad) {
		this.bad = bad;
	}

	/**
	 * @return the stat
	 */
	public String getStat() {
		return stat;
	}

	/**
	 * @param stat
	 *            the stat to set
	 */
	public void setStat(String stat) {
		this.stat = stat;
	}

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
}
