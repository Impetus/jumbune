package org.jumbune.yarn.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.client.AHSProxy;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;

public class ApplicationHistoryClient extends AbstractService{
	  public ApplicationHistoryClient() {
		super(ApplicationHistoryClient.class.getName());
	}

	protected ApplicationHistoryProtocol ahsClient;
	  protected InetSocketAddress ahsAddress;

	  private static InetSocketAddress getAHSAddress(Configuration conf) {
	    return conf.getSocketAddr(YarnConfiguration.TIMELINE_SERVICE_ADDRESS,
	        YarnConfiguration.DEFAULT_TIMELINE_SERVICE_ADDRESS,
	        YarnConfiguration.DEFAULT_TIMELINE_SERVICE_PORT);
	  }

	  protected void serviceInit(Configuration conf) throws Exception {
	    this.ahsAddress = getAHSAddress(conf);
	  }

	  protected void serviceStart() throws Exception {
	    try {
	      ahsClient = AHSProxy.createAHSProxy(getConfig(),
	          ApplicationHistoryProtocol.class, this.ahsAddress);
	    } catch (IOException e) {
	      throw new YarnRuntimeException(e);
	    }
	  }

	  protected void serviceStop() throws Exception {
	    if (this.ahsClient != null) {
	      RPC.stopProxy(this.ahsClient);
	    }
	  }

	  public ApplicationReport getApplicationReport(ApplicationId appId)
	      throws YarnException, IOException {
	    GetApplicationReportRequest request = GetApplicationReportRequest
	        .newInstance(appId);
	    GetApplicationReportResponse response = ahsClient
	        .getApplicationReport(request);
	    return response.getApplicationReport();
	  }

	  public List<ApplicationReport> getApplications() throws YarnException,
	      IOException {
	    GetApplicationsRequest request = GetApplicationsRequest.newInstance(null,
	        null);
	    GetApplicationsResponse response = ahsClient.getApplications(request);
	    return response.getApplicationList();
	  }

	  public ApplicationAttemptReport getApplicationAttemptReport(
	      ApplicationAttemptId applicationAttemptId) throws YarnException,
	      IOException {
	    GetApplicationAttemptReportRequest request = GetApplicationAttemptReportRequest
	        .newInstance(applicationAttemptId);
	    GetApplicationAttemptReportResponse response = ahsClient
	        .getApplicationAttemptReport(request);
	    return response.getApplicationAttemptReport();
	  }

	  public List<ApplicationAttemptReport> getApplicationAttempts(
	      ApplicationId appId) throws YarnException, IOException {
	    GetApplicationAttemptsRequest request = GetApplicationAttemptsRequest
	        .newInstance(appId);
	    GetApplicationAttemptsResponse response = ahsClient
	        .getApplicationAttempts(request);
	    return response.getApplicationAttemptList();
	  }

	  public ContainerReport getContainerReport(ContainerId containerId)
	      throws YarnException, IOException {
	    GetContainerReportRequest request = GetContainerReportRequest
	        .newInstance(containerId);
	    GetContainerReportResponse response = ahsClient.getContainerReport(request);
	    return response.getContainerReport();
	  }

	  public List<ContainerReport> getContainers(
	      ApplicationAttemptId applicationAttemptId) throws YarnException,
	      IOException {
	    GetContainersRequest request = GetContainersRequest
	        .newInstance(applicationAttemptId);
	    GetContainersResponse response = ahsClient.getContainers(request);
	    return response.getContainerList();
	  }

}
