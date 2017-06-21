package org.jumbune.profiling.yarn.beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jumbune.profiling.beans.WorkerJMXInfo;

public class YarnWorkerJMXInfo extends WorkerJMXInfo {
  private Set<String> nodeManager;

  /**
   * @return the nodeManager
   */
  public Set<String> getNodeManager() {
    return nodeManager;
  }

  /**
   * @param nodeManager the nodeManager to set
   */
  public void setNodeManager(Set<String> nodeManager) {
	 this.nodeManager = nodeManager;
  }
  
  public void addToNodeManager(String stat) {
	 if (this.nodeManager == null) {
	  this.nodeManager = new TreeSet<String>();
	}
    this.nodeManager.add(stat);
  }
  
  public void addStatsToNodeManager(List<String> stats) {
	 if (this.nodeManager == null) {
	  this.nodeManager = new TreeSet<String>();
	}
    this.nodeManager.addAll(stats);
  }

  public void setWorkerJMXInfoConfiguration(WorkerJMXInfo workerJMXInfo) {
    List<String> datanode = workerJMXInfo.getDataNode();
    Set<String> taskTracker = workerJMXInfo.getTaskTracker();
    if (datanode != null) {
      super.setDataNode(datanode);
    }
    if (taskTracker != null) {
      super.setTaskTracker(taskTracker);
    }
  }

  public void addNodeManagerStat(String stat) {
	if (this.nodeManager == null) {
		this.nodeManager = new HashSet<String>(3);
	}
	this.nodeManager.add(stat);
  }

}
