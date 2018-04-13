package org.jumbune.clusterprofiling.yarn.beans;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ChildQueues {
	
	
	private List<FairSchedulerQueueInfoAlt> queues;

	public List<FairSchedulerQueueInfoAlt> getQueues() {
		return queues;
	}

	@XmlElement(name = "queue")
	public void setQueues(List<FairSchedulerQueueInfoAlt> queues) {
		this.queues = queues;
	}

}
