package org.jumbune.profiling.beans;

import org.jumbune.profiling.beans.NonYarnCategoryInfo;

/**
 * Pojo to store node IP and profiling stats in favourites and trends
 * 
*/
public class NonYarnNodeConfig implements NodeConfig{

    private String nodeIp;
    private NonYarnCategoryInfo favourities;
    private NonYarnCategoryInfo trends;

    /**
     * @return the nodeIp
     */
    public String getNodeIp() {
        return nodeIp;
    }

    /**
     * @param nodeIp
     *            the nodeIp to set
     */
    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    /**
     * @return the favourities
     */
    public NonYarnCategoryInfo getFavourities() {
        return favourities;
    }

    /**
     * @param favourities
     *            the favourities to set
     */
    public void setFavourities(NonYarnCategoryInfo favourities) {
        this.favourities = favourities;
    }

    /**
     * @return the trends
     */
    public NonYarnCategoryInfo getTrends() {
        return trends;
    }

    /**
     * @param trends
     *            the trends to set
     */
    public void setTrends(NonYarnCategoryInfo trends) {
        this.trends = trends;
    }

}
