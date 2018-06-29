package org.jumbune.utils.conf.beans;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The Class AlertConfiguration.
 */
public class AlertConfiguration {
    
    /** The update interval. */
    private int updateInterval = 20;

	/** The configurable alerts. */
	private Map<AlertType, SeverityLevel> configurableAlerts;
	
	/** The non configurable alerts. */
	private Map<AlertType, Boolean> nonConfigurableAlerts;
		
	/** The queue stats alerts. */
	private Map<String, SeverityLevel> individualQueueAlerts;
	
	private List<String> hdfsDirPaths ;

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public Map<AlertType, SeverityLevel> getConfigurableAlerts() {
		return configurableAlerts;
	}
	
	public void setConfigurableAlerts(Map<AlertType, SeverityLevel> configurableAlerts) {
		this.configurableAlerts = configurableAlerts;
	}

	public Map<AlertType, Boolean> getNonConfigurableAlerts() {
		return nonConfigurableAlerts;
	}
	
	public void setNonConfigurableAlerts(Map<AlertType, Boolean> nonConfigurableAlerts) {
		this.nonConfigurableAlerts = nonConfigurableAlerts;
	}

	public Map<String, SeverityLevel> getIndividualQueueAlerts() {
		return individualQueueAlerts;
	}

	public void setIndividualQueueAlerts(Map<String, SeverityLevel> individualQueueAlerts) {
		this.individualQueueAlerts = individualQueueAlerts;
	}

	@SuppressWarnings("unchecked")
	public List<String> getHdfsDirPaths() {
		if(hdfsDirPaths == null){
			return Collections.EMPTY_LIST;
		}
		return hdfsDirPaths;
	}

	public void setHdfsDirPaths(List<String> hdfsDirPaths) {
		this.hdfsDirPaths = hdfsDirPaths;
	}
	
}
