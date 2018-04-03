package org.jumbune.profiling.yarn.beans;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.profiling.beans.ClusterWideInfo;

public class YarnClusterWideInfo extends ClusterWideInfo {
  private List<String> resourceManager;

  public List<String> getResourceManager() {
    return resourceManager;
  }

  public void setResourceManager(List<String> resourceManager) {
    this.resourceManager = resourceManager;
  }
  
  public void addResourceManagerStat(String resourceManagerStat) {
		if (this.resourceManager == null) {
			this.resourceManager = new ArrayList<String>(2);
		}
		this.resourceManager.add(resourceManagerStat);
	}

  public void setClusterWideInfoConfiguration(ClusterWideInfo clusterWideInfo) {
    List<String> nameNodeList = clusterWideInfo.getNameNode();
    List<String> jobTracker = clusterWideInfo.getJobTracker();
    if (nameNodeList != null && !nameNodeList.isEmpty()) {
      this.setNameNode(nameNode);
    }
    if (jobTracker != null && !jobTracker.isEmpty()) {
      super.setJobTracker(jobTracker);
    }
  }

}
