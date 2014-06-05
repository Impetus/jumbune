package org.jumbune.profiling.beans;

/**
 * Categories to differentiate stats.
 * 
 */
public enum StatsCategory {

	JMX_STATS("JMX Stats"), OS_STATS("OS Stats");

	private String displayValue;

	private StatsCategory(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
