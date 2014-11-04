package org.jumbune.common.yarn.beans;

import org.jumbune.common.beans.SlaveParam;

/**
 * Sub Class of Slave Parameter to hold Yarn specific parameters, like Node Manager JMX Port 
 *
 */
public class YarnSlaveParam extends SlaveParam{
  
	private String nodeManagerJmxPort;
  
 /**
 * Method to set the TaskTracker and Data Node JMX port for given Slave Param instance 
 * @param slaveParam, the Slave Parameter instance
 */
public void setSlaveParamConfiguration(SlaveParam slaveParam){
    String tTJmxPort = slaveParam.getTaskTrackerJmxPort();
    String dNJmxPort = slaveParam.getDataNodeJmxPort();
    if(tTJmxPort!=null){
      super.setTaskTrackerJmxPort(tTJmxPort);
    }
    if(dNJmxPort!=null){
      this.dataNodeJmxPort= dNJmxPort;
    }
  }
  
  /**
   * @return the nodeManagerJmxPort
   */
  public String getNodeManagerJmxPort() {
    return nodeManagerJmxPort;
  }

  /**
   * @param nodeManagerJmxPort the nodeManagerJmxPort to set
   */
  public void setNodeManagerJmxPort(String nodeManagerJmxPort) {
    this.nodeManagerJmxPort = nodeManagerJmxPort;
  }
}
