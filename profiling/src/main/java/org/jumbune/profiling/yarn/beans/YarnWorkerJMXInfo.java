package org.jumbune.profiling.yarn.beans;

import java.util.List;

import org.jumbune.profiling.beans.WorkerJMXInfo;

public class YarnWorkerJMXInfo extends WorkerJMXInfo {
  private List<String> nodeManager;

  /**
   * @return the nodeManager
   */
  public List<String> getNodeManager() {
    return nodeManager;
  }

  /**
   * @param nodeManager the nodeManager to set
   */
  public void setNodeManager(List<String> nodeManager) {
    this.nodeManager = nodeManager;
  }

  public void setWorkerJMXInfoConfiguration(WorkerJMXInfo workerJMXInfo) {
    List<String> datanode = workerJMXInfo.getDataNode();
    List<String> taskTracker = workerJMXInfo.getTaskTracker();
    if (datanode != null) {
      super.setDataNode(datanode);
    }
    if (taskTracker != null) {
      super.setTaskTracker(taskTracker);
    }
  }

}
