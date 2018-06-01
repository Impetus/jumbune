package org.jumbune.clusteranalysis.queues.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jumbune.common.utils.Constants;

/**
 * This class is used for queues related information, It can be created using
 * SchedulerService
 */
@XmlRootElement(name = "scheduler")
public class Scheduler {

	private SchedulerInfo schedulerInfo;

	public SchedulerInfo getSchedulerInfo() {
		return schedulerInfo;
	}

	@XmlElement
	public void setSchedulerInfo(SchedulerInfo schedulerInfo) {
		this.schedulerInfo = schedulerInfo;
	}

	public boolean isFairScheduler() {
		return schedulerInfo.getType().equals(Constants.FAIR_SCHEDULER);
	}

}
