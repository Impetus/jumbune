package org.jumbune.utils.conf.beans;

import java.util.HashMap;
import java.util.Map;

public class BackgroundProcessConfiguration {
	
	private Map<ProcessType, Boolean> processMap;

	public Map<ProcessType, Boolean> getProcessMap() {
		return processMap;
	}

	public void setProcessMap(Map<ProcessType, Boolean> processMap) {
		this.processMap = processMap;
	}
	
	public void addProcess(ProcessType processType, boolean isOn) {
		if (this.processMap == null) {
			this.processMap = new HashMap<ProcessType, Boolean>(3);
		}
		this.processMap.put(processType, isOn);
	}

}
