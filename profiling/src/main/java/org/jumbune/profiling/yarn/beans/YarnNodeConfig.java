package org.jumbune.profiling.yarn.beans;

import org.jumbune.profiling.beans.CategoryInfo;
import org.jumbune.profiling.beans.NodeConfig;

public class YarnNodeConfig implements NodeConfig{
  private String nodeIp;
  private YarnCategoryInfo favourities;
  private YarnCategoryInfo trends;
  /**
   * @return the nodeIp
   */
  public String getNodeIp() {
    return nodeIp;
  }
  /**
   * @param nodeIp the nodeIp to set
   */
  public void setNodeIp(String nodeIp) {
    this.nodeIp = nodeIp;
  }
  /**
   * @return the favourities
   */
  public CategoryInfo getFavourities() {
    return (CategoryInfo) favourities;
  }
  /**
   * @param favourities the favourities to set
   */
  public void setFavourities(YarnCategoryInfo favourities) {
    this.favourities = favourities;
  }
  /**
   * @return the trends
   */
  public CategoryInfo getTrends() {
    return (CategoryInfo) trends;
  }
  /**
   * @param trends the trends to set
   */
  public void setTrends(YarnCategoryInfo trends) {
    this.trends = trends;
  }
}
