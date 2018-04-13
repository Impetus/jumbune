package org.jumbune.common.beans;

public enum CapabilityPerNode {
	CALCULATE_INTERNALLY("CALCULATE_INTERNALLY"),MANUAL("MANUAL"),FAIRSCHEDULER("FAIRSCHEDULER");
	
	private String enumValue ;
	
	
	private CapabilityPerNode(String enumValue) {
		this.setEnumValue(enumValue);
	}

	public String getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}
	
}
