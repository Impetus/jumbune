package org.jumbune.common.yarn.beans;

import org.jumbune.common.beans.Master;

public class YarnMaster extends Master {

  /** The resource manager jmx port. */
  private String resourceManagerJmxPort;
  
  public String getResourceManagerJmxPort() {
    return resourceManagerJmxPort;
  }

  public void setResourceManagerJmxPort(String resourceManagerJmxPort) {
    this.resourceManagerJmxPort = resourceManagerJmxPort;
  }

  public void setMasterConfiguration(Master master) {
    //setting values of master class.
    String agentPort = master.getAgentPort();
    String dsaFile = master.getDsaFile();
    String host = master.getHost();
    String location = master.getLocation();
    String nameNodeJmxPort = master.getNameNodeJmxPort();
    String receiveDirectory = master.getReceiveDirectory();
    String rsaFile = master.getRsaFile();
    String user = master.getUser();
    String jobTrakerJmxPort = master.getJobTrackerJmxPort();
    if(agentPort!= null){
      this.setAgentPort(agentPort);
    }
    if(dsaFile!= null){
      this.setDsaFile(dsaFile);
    }
    if(dsaFile!= null){
      this.setDsaFile(dsaFile);
    }
    if(host!=null){
      this.setHost(host);  
    }
    if(location!=null){
      this.setLocation(location);
    }
    this.setIsNodeAvailable(master.isAvailable());
    if(nameNodeJmxPort!=null){
      this.setNameNodeJmxPort(nameNodeJmxPort);
    }
    if(receiveDirectory != null){
      this.setReceiveDirectory(receiveDirectory);
    }
    if(rsaFile!= null){
      this.setRsaFile(rsaFile);
    }
    if(user!=null){
      this.setUser(user);
    }
    if(jobTrakerJmxPort!=null){
      super.setJobTrackerJmxPort(jobTrakerJmxPort);
    }
  }

}
