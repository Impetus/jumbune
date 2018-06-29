package org.jumbune.web.beans;

public class NodeSpecificSetting {

	private String category;

	private String duration;

	private String aggregateFunction;
	
	private String rangeFrom;
	
	private String rangeTo;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getAggregateFunction() {
		return aggregateFunction;
	}

	public void setAggregateFunction(String aggregateFuntion) {
		this.aggregateFunction = aggregateFuntion;
	}

	public String getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(String rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public String getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(String rangeTo) {
		this.rangeTo = rangeTo;
	}
}
