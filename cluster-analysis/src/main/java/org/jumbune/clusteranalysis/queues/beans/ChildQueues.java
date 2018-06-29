package org.jumbune.clusteranalysis.queues.beans;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ChildQueues {
	
	
	private List<FairSchedulerQueueInfoIntermediate> queues;

	public List<FairSchedulerQueueInfoIntermediate> getQueues() {
		return queues;
	}

	@XmlElement(name = "queue")
	public void setQueues(List<FairSchedulerQueueInfoIntermediate> queues) {
		this.queues = queues;
	}

}
