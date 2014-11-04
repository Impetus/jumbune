package org.jumbune.utils.yarn.client;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

public class YarnClientDecorator extends YarnClientImpl{
	
//	YarnClient yarnClient;

	protected YarnClientDecorator() throws Exception {
		super();
	//	yarnClient = createYarnClient();
		serviceStart();
	}
	
	@Override
	public Configuration getConfig(){
		Configuration conf =  new YarnConfiguration();
/*		conf.set("yarn.resourcemanager.address", "192.168.49.52:8050");
		conf.set("yarn.nodemanager.aux-services", "mapreduce_shuffle");
		conf.set("yarn.resourcemanager.scheduler.address", "192.168.49.52:8030");
		conf.set("yarn.resourcemanager.webapp.address", "192.168.49.52:8088");
		conf.set("yarn.resourcemanager.admin.address", "192.168.49.52:8041");
*/		return conf;
	}
	
	
  @Override
  protected void serviceStart() throws Exception {
     super.serviceStart();
	 super.serviceInit(this.getConfig());
/*	  try{
      super.rmClient = ClientRMProxy.createRMProxy(getConfig(),
              ApplicationClientProtocol.class);
      
//          if (historyServiceEnabled) {
//            historyClient.start();
//          }
        } catch (IOException e) {
          throw new YarnRuntimeException(e);
        }
//        super.serviceStart();	  
*/  }

	@Override
	public YarnClientApplication createApplication() throws YarnException,
			IOException {
		return super.createApplication();
	}

	@Override
	public ApplicationId submitApplication(
			ApplicationSubmissionContext appContext) throws YarnException,
			IOException {
		return super.submitApplication(appContext);
	}

	@Override
	public void killApplication(ApplicationId applicationId)
			throws YarnException, IOException {
		super.killApplication(applicationId);
	}

	@Override
	public ApplicationReport getApplicationReport(ApplicationId appId)
			throws YarnException, IOException {
		return super.getApplicationReport(appId);
	}

	@Override
	public Token<AMRMTokenIdentifier> getAMRMToken(ApplicationId appId)
			throws YarnException, IOException {
		return super.getAMRMToken(appId);
	}

	@Override
	public List<ApplicationReport> getApplications() throws YarnException,
			IOException {
		return super.getApplications();
	}

	@Override
	public List<ApplicationReport> getApplications(Set<String> applicationTypes)
			throws YarnException, IOException {
		return super.getApplications(applicationTypes);
	}

	@Override
	public List<ApplicationReport> getApplications(
			EnumSet<YarnApplicationState> applicationStates)
			throws YarnException, IOException {
		return super.getApplications(applicationStates);
	}

	@Override
	public List<ApplicationReport> getApplications(
			Set<String> applicationTypes,
			EnumSet<YarnApplicationState> applicationStates)
			throws YarnException, IOException {
		return super.getApplications(applicationTypes, applicationStates);
	}

	@Override
	public YarnClusterMetrics getYarnClusterMetrics() throws YarnException,
			IOException {		
		return super.getYarnClusterMetrics();
	}

	@Override
	public List<NodeReport> getNodeReports(NodeState... states)
			throws YarnException, IOException {
		return super.getNodeReports(states);
	}

	@Override
	public org.apache.hadoop.yarn.api.records.Token getRMDelegationToken(
			Text renewer) throws YarnException, IOException {
		return super.getRMDelegationToken(renewer);
	}

	@Override
	public QueueInfo getQueueInfo(String queueName) throws YarnException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueueInfo> getAllQueues() throws YarnException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueueInfo> getRootQueueInfos() throws YarnException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueueInfo> getChildQueueInfos(String parent)
			throws YarnException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QueueUserACLInfo> getQueueAclsInfo() throws YarnException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationAttemptReport getApplicationAttemptReport(
			ApplicationAttemptId applicationAttemptId) throws YarnException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ApplicationAttemptReport> getApplicationAttempts(
			ApplicationId applicationId) throws YarnException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerReport getContainerReport(ContainerId containerId)
			throws YarnException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ContainerReport> getContainers(
			ApplicationAttemptId applicationAttemptId) throws YarnException,
			IOException {
		return super.getContainers(applicationAttemptId);
	}

	@Override
	public void moveApplicationAcrossQueues(ApplicationId appId, String queue)
			throws YarnException, IOException {
		super.moveApplicationAcrossQueues(appId, queue);
	}
	
	public ApplicationId createAndSubmitApplication(String appName, boolean isFileOnHDFS, boolean isFile, LocalResourceVisibility visibility, String jarPathString, String classPathAppend, String command, int numOfCopies) throws YarnException, IOException{
		YarnClientApplication clientApplication = createApplication(appName);
		System.out.println("Application Id:"+clientApplication.getApplicationSubmissionContext().getApplicationId().toString());
		ApplicationSubmissionContext applicationSubmissionContext = clientApplication.getApplicationSubmissionContext();
		applicationSubmissionContext.setAMContainerSpec(createContainerLaunchContext(isFile, isFileOnHDFS, visibility, jarPathString, classPathAppend, command, numOfCopies));
		return submitApplication(applicationSubmissionContext);
	}

	/**
	 * Create a new container launch context for the AM's container
	 * @param isFile 
	 * @param numOfCopies 
	 * @return
	 * @throws IOException 
	 */
	private ContainerLaunchContext createContainerLaunchContext(boolean isFile, boolean isFileOnHDFS, LocalResourceVisibility visibility, String jarPathString, String classPathAppend, String command, int numOfCopies) throws IOException{
	    ContainerLaunchContext amContainer = 
		        Records.newRecord(ContainerLaunchContext.class);
	    LocalResource amJarRsrc = Records.newRecord(LocalResource.class);

	    if(isFile){
	    	amJarRsrc.setType(LocalResourceType.FILE);
	    }else{
	    	amJarRsrc.setType(LocalResourceType.ARCHIVE);
	    }
	    amJarRsrc.setVisibility(visibility);

	    if(isFileOnHDFS){
		    //TODO: Properly Initialize FS
		    FileSystem fs = null;
		    Path jarPath = new Path(jarPathString);
		    FileStatus jarStatus = fs.getFileStatus(jarPath);
		    amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));			
		    amJarRsrc.setTimestamp(Long.valueOf("1403330711000"));
		    amJarRsrc.setSize(jarStatus.getLen());
	    }else{
	    	File file = new File(jarPathString);
	    	URL packageUrl = ConverterUtils.getYarnUrlFromPath(
	    	        FileContext.getFileContext().makeQualified(new Path(jarPathString)));
	    	amJarRsrc.setResource(packageUrl);
		    Path jarPath = new Path(jarPathString);
		    amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(jarPath));
		    amJarRsrc.setTimestamp(Long.valueOf("1403330711000"));
		    amJarRsrc.setSize(file.length());
	    }
	    
	    Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
	    localResources.put("AppMaster.jar",  amJarRsrc);
	    amContainer.setLocalResources(localResources);
	    
	 // Set up the environment needed for the launch context
	    Map<String, String> env = new HashMap<String, String>();
	    //String classPathEnv = "$CLASSPATH:./*:"+classPathAppend;
	    String classPathEnv = "$CLASSPATH:./*:"+"/home/impadmin/Downloads/hadoop-2.4.1/etc/hadoop/*:/home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/common/*:/home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/common/lib/*:/home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/yarn/*:/home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/yarn/lib/*:/home/impadmin/simple-yarn.jar";
	    
	    env.put("CLASSPATH", classPathEnv);
	    amContainer.setEnvironment(env);
	    
	    
	    List<String> commands = Collections.singletonList(
	    		"$JAVA_HOME/bin/java" +
	    		" -Xmx256M" +
	    		" org.jumbune.utils.YarnCommunicator.ApplicationMaster" +
	    		" " + command +
	    		" " + String.valueOf(numOfCopies)
	    		);

	    amContainer.setCommands(commands);
	    

	    return amContainer;
	}
	
	
	private YarnClientApplication createApplication(String appName)
			throws YarnException, IOException {
		ApplicationSubmissionContext context = Records
				.newRecord(ApplicationSubmissionContext.class);
		GetNewApplicationResponse newApp = getNewApplication();
		ApplicationId appId = newApp.getApplicationId();
		context.setApplicationId(appId);
		context.setApplicationName(appName);
		context.setResource(Resource.newInstance(256, 1));
		return new YarnClientApplication(newApp, context);
	}
	
	/**
	 * Once a handle is obtained to the ASM, the client needs to request the ResourceManager for a new ApplicationId. 
	 * @return
	 * @throws YarnException
	 * @throws IOException
	 */
	private GetNewApplicationResponse getNewApplication() throws YarnException,
			IOException {
		GetNewApplicationRequest request = Records
				.newRecord(GetNewApplicationRequest.class);
		return rmClient.getNewApplication(request);
	}
		
	
	public static void main(String args[]) throws Exception{
		YarnClientDecorator yarnClient = new YarnClientDecorator();
				
		boolean isFileOnHDFS = false;
		boolean isFile = true;
		LocalResourceVisibility visibility = LocalResourceVisibility.PUBLIC;
		String jarPathString = "file:///home/impadmin/Downloads/hadoop-2.4.1/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.4.1.jar";
		String classPathAppend = null;
//		String command = "bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.4.1.jar grep input output 'dfs[a-z.]+'";
		String command = "/bin/date";
		String appName = "DateApp";
		int numOfCopies = 2;
		yarnClient.createAndSubmitApplication(appName, isFileOnHDFS, isFile, visibility, jarPathString, classPathAppend, command, numOfCopies);
		
	}
	
}
