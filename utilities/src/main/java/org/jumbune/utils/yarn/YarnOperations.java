package org.jumbune.utils.yarn;

import java.io.IOException;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.exceptions.YarnException;

public interface YarnOperations {
	
	  public abstract void killApplication(ApplicationId applicationId) throws YarnException,
      IOException;

	  public abstract ApplicationReport monitorApplication(ApplicationId appId) throws YarnException, IOException;
	  
	  public abstract List<ApplicationReport> getApplications()
		      throws YarnException, IOException;
	  
	  public abstract YarnClusterMetrics getYarnClusterMetrics() throws YarnException,
      IOException;
	  
	  public abstract List<NodeReport> getNodeReports(NodeState... states)
		      throws YarnException, IOException;

	  public abstract QueueInfo getQueueInfo(String queueName) throws YarnException,
      IOException;

	  public abstract List<QueueInfo> getAllQueues() throws YarnException, IOException;
	  
	  public abstract List<QueueInfo> getRootQueueInfos() throws YarnException, IOException;
	  
	  public abstract List<QueueInfo> getChildQueueInfos(String parent) throws YarnException,
      IOException;

	  public abstract List<ContainerReport> getContainers(
		      ApplicationAttemptId applicationAttemptId) throws YarnException,
		      IOException;
	  
	  public abstract ContainerReport getContainerReport(ContainerId containerId)
		      throws YarnException, IOException;

	  public abstract List<ApplicationAttemptReport> getApplicationAttempts(
		      ApplicationId applicationId) throws YarnException, IOException;

	  public abstract ApplicationAttemptReport getApplicationAttemptReport(
		      ApplicationAttemptId applicationAttemptId) throws YarnException, IOException;

	  public abstract List<QueueUserACLInfo> getQueueAclsInfo() throws YarnException,
      IOException;

}
