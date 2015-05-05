package org.jumbune.execution.beans;

import org.jumbune.common.beans.Module;

public enum CommunityModule implements Module{

	DATA_QUALITY(0),
	PROFILING(1),
	DEBUG_ANALYSER(2);
	
	
	private int enumValue;
  
	public int getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(int enumValue) {
		this.enumValue = enumValue;
	}

	private CommunityModule(int enumValue) {
		this.enumValue = enumValue;
	}
}
