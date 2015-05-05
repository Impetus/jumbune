package org.jumbune.utils.yarn.communicators;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.YarnRPC;


/**
 *  Class for creating a client to the Resource Manager
 */
public class RMCommunicator {
	
	private ApplicationClientProtocol proxy;
	
	/**
	 * Instantiates the RM Application Client Protocol Proxy
	 * @param user, the user for which remote RM connection will be established
	 * @param rmAddress, format is system where rm is running:rm rpc port (fdefault is 8032)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public RMCommunicator(String user, final String rmAddress) throws IOException, InterruptedException{
		  UserGroupInformation userUGI=UserGroupInformation.createRemoteUser(user);
		  proxy = userUGI.doAs(new PrivilegedExceptionAction<ApplicationClientProtocol>(){
		    @Override public ApplicationClientProtocol run() throws Exception {
		      Configuration conf = new Configuration();
		      YarnRPC rpc=YarnRPC.create(conf);
		      InetSocketAddress rmSocketAddress = NetUtils.createSocketAddr(rmAddress);
		      return (ApplicationClientProtocol)rpc.getProxy(ApplicationClientProtocol.class, rmSocketAddress, conf);
		    }
		  }
		);
	}	
	
	/**
	 * Get list of all applications known to RM
	 * @return list of application report
	 * @throws YarnException
	 * @throws IOException
	 */
	public List<ApplicationReport> getApplications() throws YarnException, IOException{
		GetApplicationsResponse response = proxy.getApplications(GetApplicationsRequest.newInstance());
		List<ApplicationReport> applicationsReport = response.getApplicationList();
		return applicationsReport;
	}
	
	/**
	 * Get Application Report of Application which matches with Application Id
	 * @param appId
	 * @return
	 * @throws YarnException
	 * @throws IOException
	 */
	public ApplicationReport getApplicationReportById(ApplicationId appId) throws YarnException, IOException{
		List<ApplicationReport> applicationsReport = getApplications();
		for(ApplicationReport report: applicationsReport){
			if(appId.compareTo(report.getApplicationId())==0){
				return report;
			}
		}
		return null;
	}	
	
	/**
	 * Get Application Attempt Report for the given Application Report
	 * @param report, the application report
	 * @return
	 * @throws YarnException
	 * @throws IOException
	 */
	public ApplicationAttemptReport getApplicationAttemptReport(ApplicationReport report) throws YarnException, IOException{
			GetApplicationAttemptReportResponse resp = proxy.getApplicationAttemptReport(GetApplicationAttemptReportRequest.newInstance(report.getCurrentApplicationAttemptId()));
			return resp.getApplicationAttemptReport();
	}
	
	/**
	 * Get the Application Id of the given Application Report
	 * @param report
	 * @return
	 */
	public ApplicationId getApplicationId(ApplicationReport report){
		return report.getApplicationId();
	}
	
	/**
	 * Get the current Application Attempt Id for the given Application Report
	 * @param report
	 * @return
	 */
	public int getCurrentApplicationAttemptId(ApplicationReport report){
		return report.getCurrentApplicationAttemptId().getAttemptId();
	}
	
	/**Get the Job Id for the given Application Report
	 * @param report
	 * @return
	 */
	public JobId getJobId(ApplicationReport report){
		ApplicationId applicationId = getApplicationId(report);
		return MRBuilderUtils.newJobId(applicationId, applicationId.getId());
	}
		
	/**
	 * Get the number of node managers in the cluster
	 * @return int, number of node managers
	 * @throws YarnException
	 * @throws IOException
	 */
	public int getNumOfNodeManagersInCluster() throws YarnException, IOException{
		GetClusterMetricsResponse response = proxy.getClusterMetrics(GetClusterMetricsRequest.newInstance());
		return response.getClusterMetrics().getNumNodeManagers();
	}
	
	/**
	 * Get the list of nodes completed the application attempt
	 * @param applicationAttemptId
	 * @return
	 * @throws YarnException
	 * @throws IOException
	 */
	public Set<String> getNodesWithCompletedAttempt(ApplicationAttemptId applicationAttemptId) throws YarnException, IOException{
		Set<String> nodes = new HashSet<String>();
		GetContainersResponse response = proxy.getContainers(GetContainersRequest.newInstance(applicationAttemptId));
		List<ContainerReport> containers = response.getContainerList();
		for(ContainerReport container: containers){
			if(ContainerState.COMPLETE.equals(container.getContainerState())){
					nodes.add(container.getAssignedNode().getHost());
			}
		}
		return nodes;
	}

	/**
	 * Get the Node Reports for all the nodes of the cluster
	 * @return, the list of node report
	 * @throws YarnException
	 * @throws IOException
	 */
	public List<NodeReport> getNodeReports() throws YarnException, IOException{
		GetClusterNodesRequest request = GetClusterNodesRequest.newInstance();
		GetClusterNodesResponse response = proxy.getClusterNodes(request);
		return response.getNodeReports();		
	}
}