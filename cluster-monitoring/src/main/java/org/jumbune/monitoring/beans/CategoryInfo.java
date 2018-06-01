package org.jumbune.monitoring.beans;

public interface CategoryInfo {

  ClusterWideInfo getClusterWide();

  WorkerJMXInfo getWorkerJMXInfo();

  SystemStats getSystemStats();

  void setClusterWide(ClusterWideInfo clusterWideInfo);

  void setWorkerJMXInfo(WorkerJMXInfo levelJMXInfo);

  void setSystemStats(SystemStats stats);

}