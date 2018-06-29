package org.jumbune.monitoring.yarn.beans;

import org.jumbune.monitoring.beans.CategoryInfo;
import org.jumbune.monitoring.beans.ClusterWideInfo;
import org.jumbune.monitoring.beans.SystemStats;
import org.jumbune.monitoring.beans.WorkerJMXInfo;

public class YarnCategoryInfo implements CategoryInfo {

  private YarnClusterWideInfo clusterWide;
  private SystemStats systemStats;
  private YarnWorkerJMXInfo workerJMXInfo;
  /**
   * @return the clusterWide
   */
  public YarnClusterWideInfo getClusterWide() {
    return clusterWide;
  }
  /**
   * @param clusterWide the clusterWide to set
   */
  public void setClusterWide(ClusterWideInfo clusterWide) {
    this.clusterWide = (YarnClusterWideInfo) clusterWide;
  }
  /**
   * @return the systemStats
   */
  public SystemStats getSystemStats() {
    return systemStats;
  }
  /**
   * @param systemStats the systemStats to set
   */
  public void setSystemStats(SystemStats systemStats) {
    this.systemStats = systemStats;
  }
  /**
   * @return the workerJMXInfo
   */
  public YarnWorkerJMXInfo getWorkerJMXInfo() {
    return workerJMXInfo;
  }
  /**
   * @param workerJMXInfo the workerJMXInfo to set
   */
  public void setWorkerJMXInfo(WorkerJMXInfo workerJMXInfo) {
    this.workerJMXInfo = (YarnWorkerJMXInfo) workerJMXInfo;
  }
}
