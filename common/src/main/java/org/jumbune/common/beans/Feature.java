package org.jumbune.common.beans;

public enum Feature {
	
	ANALYZE_JOB("Analyze Job"),
	
	ANALYZE_DATA("Analyze Data");
	
	private String enumValue;

	private Feature(String enumValue){
		this.enumValue = enumValue;		
	}
	
	public String getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}
		
}
