package org.jumbune.utils.yarn.communicators;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.mapreduce.v2.api.HSClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetJobReportResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptReportResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskReportResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.KillJobRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.KillJobResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb.GetJobReportRequestPBImpl;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb.GetTaskAttemptReportRequestPBImpl;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb.GetTaskReportRequestPBImpl;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb.KillJobRequestPBImpl;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.proto.MRServiceProtos.GetJobReportRequestProto;
import org.apache.hadoop.mapreduce.v2.proto.MRServiceProtos.GetTaskAttemptReportRequestProto;
import org.apache.hadoop.mapreduce.v2.proto.MRServiceProtos.GetTaskReportRequestProto;
import org.apache.hadoop.mapreduce.v2.proto.MRServiceProtos.KillJobRequestProto;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.ipc.YarnRPC;

/**
 * The communication client layer for MR Job History Server
 */
public class MRCommunicator {
	
	
	MRClientProtocol proxy;
	
	/**
	 * Instantiates the proxy for MR Client which will communicate with MR History Server
	 * @param user, the remote user for which client communication with happen
	 * @param mrHistoryServerRPCURI, the rpc (not web) uri for mr history server, typically defined as "mapreduce.jobhistory.address" property in mapred-site.xml
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public MRCommunicator(String user, final String mrHistoryServerRPCURI) throws IOException, InterruptedException{
		UserGroupInformation userUGI=UserGroupInformation.createRemoteUser(user);
		this.proxy = userUGI.doAs(new PrivilegedExceptionAction<MRClientProtocol>(){
		    @Override public MRClientProtocol run() throws Exception {
		      Configuration conf = new Configuration();
		      YarnRPC rpc=YarnRPC.create(conf);
		      InetSocketAddress rmAddress = NetUtils.createSocketAddr(mrHistoryServerRPCURI);
		      return (MRClientProtocol)rpc.getProxy(HSClientProtocol.class, rmAddress, conf);
		    }
		  }
		);
	}
	
	/**
	 * Given the required details (application id and suffix id) for JobId it gives the JobReport
	 * @param appid, the Application Id instance
	 * @param id, the suffix id
	 * @return the Job Report
	 * @throws IOException
	 */
	public JobReport getJobReport(ApplicationId appId, int id) throws IOException{
		JobId jobId = YarnCommunicatorUtil.getJobId(appId, (int)1);
		GetJobReportRequestProto proto = GetJobReportRequestProto.getDefaultInstance();
		GetJobReportRequest request = new GetJobReportRequestPBImpl(proto);
		request.setJobId(jobId);
		GetJobReportResponse jobReportResponse = proxy.getJobReport(request);
		return jobReportResponse.getJobReport();
	}

	/**
	 * Given the taskId details (JobId, suffix id and task type), it gives the TaskReport
	 * @param jobId, the JobId instance
	 * @param id, the suffix id as int
	 * @param taskType, the task type
	 * @return the Task Report
	 * @throws IOException
	 */
	public TaskReport getTaskReport(JobId jobId, int id, TaskType taskType) throws IOException{
		TaskId taskId = YarnCommunicatorUtil.getTaskId(jobId, id, taskType);
		GetTaskReportRequestProto proto = GetTaskReportRequestProto.getDefaultInstance();	
		GetTaskReportRequest getTaskReportRequest = new GetTaskReportRequestPBImpl(proto);
		getTaskReportRequest.setTaskId(taskId);
		GetTaskReportResponse taskReportResponse =  proxy.getTaskReport(getTaskReportRequest);
		return taskReportResponse.getTaskReport();
	}
	
	/**
	 * This method tries to extract all Map & Reduce attempt Task Reports for a given Job Id
	 * @param jobId, the Job Id for which all Task Reports requires to be extracted
	 * @return, Map<TaskId, TaskReport>
	 * @throws IOException
	 */
	public Map<TaskId, TaskReport> getAllTaskReports(JobId jobId) throws IOException{
		Map<TaskId, TaskReport> reports = new HashMap<TaskId, TaskReport>();
		TaskReport report;

		//Attempting to extract Map Attempt Reports
		boolean rme = false;
		int id = 0;
		do{
			try{
				report = getTaskReport(jobId, id, TaskType.MAP);
				TaskId taskId = MRBuilderUtils.newTaskId(jobId, id, TaskType.MAP);
				reports.put(taskId, report);
				id++;
			}catch(RemoteException re){
				rme = true;
			}
		}while(!rme);

		//Attempting to extract Reduce Attempt Reports
		id = 0;
		rme = false;
		do{
			try{
				report = getTaskReport(jobId, id, TaskType.REDUCE);
				TaskId taskId = MRBuilderUtils.newTaskId(jobId, id, TaskType.REDUCE);
				reports.put(taskId, report);
				id++;
			}catch(RemoteException re){
				rme = true;
			}
		}while(!rme);

		return reports;
	}

	/**
	 * Given the taskAttempt details (task id and attempt id), it gives the TaskAttemptReport
	 * @param taskId, the taskId instance
	 * @param attemptId, the attempt id as int
	 * @return the Task Attempt Report
	 * @throws IOException
	 */
	public TaskAttemptReport getTaskAttemptReport(TaskId taskId, int attemptId) throws IOException{
		TaskAttemptId taskAttemptId = YarnCommunicatorUtil.getTaskAttemptId(taskId, 0);
		GetTaskAttemptReportRequestProto request = GetTaskAttemptReportRequestProto.getDefaultInstance();
		GetTaskAttemptReportRequest getTaskAttemptRequest = new GetTaskAttemptReportRequestPBImpl(request);
		getTaskAttemptRequest.setTaskAttemptId(taskAttemptId);
		GetTaskAttemptReportResponse taskAttemptReportResponse = proxy.getTaskAttemptReport(getTaskAttemptRequest);
		return taskAttemptReportResponse.getTaskAttemptReport();
	}
	
	/**
	 * Kill the job, given the jobId
	 * @param jobId
	 * @return
	 * @throws IOException
	 */
	public boolean killJob(JobId jobId) throws IOException{
		KillJobRequestProto proto = KillJobRequestProto.getDefaultInstance();
		KillJobRequest request = new KillJobRequestPBImpl(proto);
		request.setJobId(jobId);
		KillJobResponse killJobResponse = proxy.killJob(request);
		if(killJobResponse!=null){
			return true;
		}
		return false;
	}

	/**
	 * This method prepares a Map containing Node Manager details (hostname, port) on which successful attempts of the job ran
	 * @param jobId
	 * @return Map<String, Integer> containing hostname and rpc port of Node Managers
	 * @throws IOException
	 */
	public Map<String, Integer> getAttemptedNodes(JobId jobId) throws IOException{
		Map<String, Integer> nodes = new HashMap<String, Integer>();
		Map<TaskId, TaskReport> reports = getAllTaskReports(jobId);
		for(Map.Entry<TaskId, TaskReport> report: reports.entrySet()){
			TaskId taskId = report.getKey();
			TaskReport taskReport = report.getValue();
			TaskAttemptId attemptId = taskReport.getSuccessfulAttempt();
			TaskAttemptReport taskAttemptReport = getTaskAttemptReport(taskId, attemptId.getId());
			nodes.put(taskAttemptReport.getNodeManagerHost(), taskAttemptReport.getNodeManagerPort());
		}
		return nodes;
	}
}