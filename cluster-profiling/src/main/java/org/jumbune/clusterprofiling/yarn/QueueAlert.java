package org.jumbune.clusterprofiling.yarn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.AlertConfiguration;
import org.jumbune.utils.conf.beans.AlertType;
import org.jumbune.utils.conf.beans.SeverityLevel;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jumbune.common.alerts.AlertConstants;
import org.jumbune.common.alerts.YarnAlert;
import org.jumbune.common.beans.Alert;
import org.jumbune.common.beans.EffCapUtilizationStats;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ExtendedConstants;

/**
 * The Class QueueAlert.
 */
public class QueueAlert implements YarnAlert {


	private static final String FAILED = " Failed";

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(QueueAlert.class);

	/** The Constant CLUSTER_PROFILING. */
	private static final String CLUSTER_PROFILING = "/clusterprofiling/";

	/** The Constant UTILIZATION_STATS. */
	private static final String UTILIZATION_STATS = "/utilizationStats/";

	/** The Constant JSON. */
	private static final String JSON = ".json";

	/** The Constant EFFECTIVE_MAX_UTILISATION. */
	private static final String EFFECTIVE_MAX_UTILISATION = "maxUtilisation";

	/** The Constant DEAMON_WENT_DOWN. */
	public static final String DEAMON_WENT_DOWN = " went down";
	
	/** The Constant DEAMON_WENT_UNHEALTHY. */
	public static final String DEAMON_IS_UNHEALTHY = " is unhealthy";
	
	private static Type type = new TypeToken<Map<String, Map<String, String>>>() {
	}.getType();
	
	private static volatile QueueAlert instance = null;
	
	public static QueueAlert getInstance() {
		if (instance == null) {
			synchronized (QueueAlert.class) {
				if (instance == null) {
					instance = new QueueAlert();
				}
			}
		}
		return instance;
	}
	
	private QueueAlert() {
	}

	public List <Alert> getClusterTimeDesyncAlert(String clusterName) { 
		List<Alert> queueAlert = new ArrayList<Alert>();
		StringBuffer queueJsonFile = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
				.append(clusterName).append(File.separator).append(clusterName).append(ExtendedConstants.QUEUE).append(JSON);
		File queueDataFile = new File(queueJsonFile.toString());

		try {
			Map <String,Map<String,String>> allQueue = new HashMap <String, Map<String,String>>(); 
			if(queueDataFile.exists()){
				String queueDataJsonReader;
				try {
					queueDataJsonReader = ConfigurationUtil.readFileData(queueDataFile.toString());
					allQueue = Constants.gson.fromJson(queueDataJsonReader, type);
				} catch (IOException e) {
					LOGGER.error("Error while reading queue json file : ", e);
				}
				for(Map.Entry<String, Map<String,String>> queueData : allQueue.entrySet()){
					Map<String, String>queueInformation = queueData.getValue();
					Long waitingTime = Long.parseLong(queueInformation.get("WaitingTime"));
					if(waitingTime <0){
						Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.ALL,"Cluster node's time needs to be synchronized", getDate());
						queueAlert.add(alert);
						break;
					}
				}
			}
		} catch (JsonSyntaxException | NumberFormatException e) {
			LOGGER.error("Unable to getClusterTimeDesyncAlert : ",e);
		}
		return queueAlert;
	}
	
	
	/**
	 * Gets the queue utilisation alert.
	 * @param rmCommunicator 
	 * 
	 * @return the queue utilisation alert
	 * @throws YarnException
	 *             the yarn exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<Alert> getQueueUtilisationAlert(String clusterName, RMCommunicator rmCommunicator) {
		
		List<Alert> queueAlert = new ArrayList<Alert>();
		AlertConfiguration alertConfiguration = AdminConfigurationUtil.getAlertConfiguration(clusterName);
		Map<String, SeverityLevel> mapForSeverity = alertConfiguration.getIndividualQueueAlerts();

		SeverityLevel severityLevelForIndivQAlertsGlobal = alertConfiguration
				.getConfigurableAlerts().get(AlertType.QUEUE_UTILIZATION);

		int criticalLevelGlobal = severityLevelForIndivQAlertsGlobal
				.getCriticalLevel();
		int warningLevelGlobal = severityLevelForIndivQAlertsGlobal
				.getWarningLevel();	

		List<QueueInfo> list = new ArrayList<QueueInfo>();
		
		try {
			addQueue(rmCommunicator.getQueueInfo(ExtendedConstants.ROOT), list);
			
			float finalCapacity;
			for (QueueInfo temp : list) {
				if(!temp.getQueueName().equalsIgnoreCase(ExtendedConstants.ROOT)){
				SeverityLevel severityLevelForIndivQAlerts = null;
				int criticalLevel = 0;
				int warningLevel = 0;

				if (mapForSeverity.containsKey(temp.getQueueName())) {					
					severityLevelForIndivQAlerts = mapForSeverity.get(temp
							.getQueueName());
					criticalLevel = severityLevelForIndivQAlerts
							.getCriticalLevel();
					warningLevel = severityLevelForIndivQAlerts
							.getWarningLevel();					
				} else {					
					criticalLevel = criticalLevelGlobal;
					warningLevel = warningLevelGlobal;
				}
				finalCapacity  = temp.getCurrentCapacity() > 0.0 ? temp.getCurrentCapacity() : temp.getCapacity();
				if ((finalCapacity * 100) >= criticalLevel) {
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL,
							ExtendedConstants.HYPHEN, "Queue "
									+ temp.getQueueName() + ":"
									+ ExtendedConstants.QUEUE_LIMIT, getDate());
					queueAlert.add(alert);					
				} else if (((finalCapacity * 100) >= warningLevel)
						&& ((temp.getCurrentCapacity() * 100) < criticalLevel)) {
					Alert alert = new Alert(ExtendedConstants.WARNING_LEVEL,
							ExtendedConstants.HYPHEN, "Queue "
									+ temp.getQueueName() + ":"
									+ ExtendedConstants.QUEUE_LIMIT, getDate());
					queueAlert.add(alert);					
				}
			}}
		} catch (YarnException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Unable to getQueueUtilisationAlert due to : ",e);
		}

		return queueAlert;
	}
	
	private void addQueue(QueueInfo queueInfo, List<QueueInfo> list) {
		list.add(queueInfo);
		if (queueInfo.getChildQueues() != null) {
			for (QueueInfo childQueue : queueInfo.getChildQueues()) {
				addQueue(childQueue, list);
			}
		}
	}

	/**
   	 * Traverse queues bf.
   	 *
   	 * @param queueName the queue name
   	 * @param queueAlert the queue alert
	 * @param rmCommunicator 
   	 * @throws YarnException the yarn exception
   	 * @throws IOException Signals that an I/O exception has occurred.
   	 */
   	private void traverseQueuesBF(String queueName, List<Alert> queueAlert, RMCommunicator rmCommunicator) throws YarnException, IOException {
		   final String parentQueue = queueName;
		   QueueInfo qi = rmCommunicator.getQueueInfo(queueName);
	       List<String> queueNames = new ArrayList<String>(5); 
		   float childrenCapacity = 0.0f;
		   for (QueueInfo info : qi.getChildQueues()) {
			   queueNames.add(info.getQueueName());
			   childrenCapacity += info.getCapacity();
		   }
			if(childrenCapacity > 1.0){
				Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"Queue "+parentQueue+":"+"child capacity exceeded 100 percent", getDate());
				queueAlert.add(alert);
			}
		   for(String name : queueNames) {
			   traverseQueuesBF(name, queueAlert, rmCommunicator);
		   }
		   
	   }
	
	
	/**
	 * Gets the child capacity alert.
	 *
	 * @return the child capacity alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List <Alert> getChildCapacityAlert(RMCommunicator rmCommunicator) { 
		List<Alert> queueAlert = new ArrayList<>();
		try {
			traverseQueuesBF(ExtendedConstants.ROOT, queueAlert, rmCommunicator);
		} catch (YarnException | IOException e) {
		LOGGER.error("Unable to get child queue capacity alert due to: ",e);
		}
		return queueAlert;
	}
	
	/**
	 * Gets the application failed alert.
	 *
	 * @return the application failed alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	public List <Alert> getApplicationFailedAlert(RMCommunicator rmCommunicator,
			long finishFrom, long finishTo) {
		
		List<Alert> appFailedAlert = new ArrayList<Alert>();
		List<ApplicationReport> applicationReport = null;

		try {
			applicationReport = rmCommunicator.getApplications(
					null, null, null, null, null, EnumSet.of(
							YarnApplicationState.FINISHED, YarnApplicationState.FAILED), finishFrom, finishTo, null);
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get failed applications from yarn api due to : ",e);
		}
		for (ApplicationReport ar:applicationReport) {
			if (ar.getFinalApplicationStatus().equals(FinalApplicationStatus.FAILED)) {
					Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN,
							ar.getApplicationId().toString() +FAILED, getDate());
					alert.setSkipOccuringSince(true);
					appFailedAlert.add(alert);
			}
		}

		return appFailedAlert;
	}


	/**
	 * Gets the container utilization alert.
	 *
	 * @param cluster the cluster
	 * @param rmCommunicator 
	 * @return the container utilization alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	public List <Alert> getContainerUtilizationAlert(Cluster cluster, RMCommunicator rmCommunicator){

		List<Alert> containerAlert = new ArrayList<Alert>();
		float containerBasedonVcores, containerBasedonMemory, container;

		List<NodeReport> nodeReports;
		try {
			nodeReports = rmCommunicator.getNodeReports();
		
		for(NodeReport nr:nodeReports){

			if (nr.getNumContainers() > 0){
				containerBasedonVcores = nr.getCapability().getVirtualCores()/getMinimumParameterMandatoryForContainer(ExtendedConstants.YARN_CONTAINER_MINIMUM_VCORE,1,cluster);
				containerBasedonMemory = nr.getCapability().getMemory()/getMinimumParameterMandatoryForContainer(ExtendedConstants.YARN_CONTAINER_MINIMUM_MEMORY,1,cluster);
				container = Math.min(containerBasedonVcores, containerBasedonMemory);
				if (((nr.getNumContainers()/container)*100) > 80){
					String host = nr.getNodeId().getHost();
					if(!host.contains(".")){
						host = InetAddress.getByName(host).getHostAddress();
					}
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL,host,"Container capacity is over utilized", getDate() );
					containerAlert.add(alert);
				}
			}
		}} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get container utilization alert due to : ",e);
		}

		return containerAlert;
	}


	
	@Override
	public List<Alert> getEffectiveUtlilzationAlert(String clusterName){
		List<Alert> effectiveUtilisationAlert = new ArrayList<Alert>(4);
		String[] jobIdList = getJobList(clusterName);
		if (jobIdList == null) {
			return effectiveUtilisationAlert;
		}
		for(String jobId:jobIdList){
			StringBuffer capUtilJsonFileName = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
					.append(clusterName).append(CLUSTER_PROFILING).append(jobId).append(UTILIZATION_STATS).append(jobId).append(EFFECTIVE_MAX_UTILISATION).append(JSON);
			StringBuffer capUtilAlertJsonFileName = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
					.append(clusterName).append(CLUSTER_PROFILING).append(jobId).append(UTILIZATION_STATS).append(jobId).append(EFFECTIVE_MAX_UTILISATION).append(ExtendedConstants.WARNING_LEVEL).append(JSON);

			File capUtilJsonFile = new File(capUtilJsonFileName.toString());
			File capUtilAlertJsonFile = new File(capUtilAlertJsonFileName.toString());
			try {
			if(capUtilJsonFile.exists()){
				if(!capUtilAlertJsonFile.exists()){
					
						Files.copy(capUtilJsonFile.toPath(), capUtilAlertJsonFile.toPath());
					
					String capUtilJsonReader = ConfigurationUtil.readFileData(capUtilJsonFileName.toString());
					EffCapUtilizationStats effCapUtilStats = new EffCapUtilizationStats();
					effCapUtilStats = Constants.gson.fromJson(capUtilJsonReader, EffCapUtilizationStats.class);
					boolean mapAlert = false, reduceAlert = false;
					if( effCapUtilStats.getUsedMaxMapMemory().floatValue() > 0){
						Float usedMapAlert = effCapUtilStats.getUsedMaxMapMemory().floatValue()/effCapUtilStats.getAllocatedMapMemory().floatValue();
						if(usedMapAlert<0.5){
							mapAlert = true;
						}
					}
					if(effCapUtilStats.getUsedMaxReduceMemory().floatValue() > 0){
						Float usedReduceAlert = effCapUtilStats.getUsedMaxReduceMemory().floatValue()/effCapUtilStats.getAllocatedReduceMemory().floatValue();
						if(usedReduceAlert<0.5){
							reduceAlert = true;
						}
					}
					String str = null;
					if (mapAlert && reduceAlert) {
						str = "Mapper & Reducer";
					} else if (mapAlert) {
						str = "Mapper";
					} else if (reduceAlert) {
						str = "Reducer";
					}
					if (mapAlert || reduceAlert) {
						Alert alert = new Alert(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN, 
								"Found excessive allocation of resource for " + str +
								", unused resource were over 50% in the containers for Job ("+jobId
								+"), recommended to tune the allocation to effectively utilize cluster capability.",getDate());
						alert.setJobId(jobId);
						effectiveUtilisationAlert.add(alert);
					}
				}
			}} catch (IOException e) {
				LOGGER.error("Unable to getEffectiveUtlilzationAlert due to : ",e);
			}
		}
		return effectiveUtilisationAlert;
	}


	
	@Override
	public Alert getResourceManagerDownAlert(Cluster cluster){

		try {
			String resourceManagerInstance = null;
			resourceManagerInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getResourceManager(),
					JMXDeamons.RESOURCE_MANAGER.toString());
			if (resourceManagerInstance.isEmpty()) {
				return new Alert(ExtendedConstants.CRITICAL_LEVEL, cluster.getResourceManager(),
						AlertConstants.RESOURCE_MANAGER + DEAMON_WENT_DOWN, getDate());
			}
		} catch (Exception e) {
			LOGGER.error("Unable to getResourceManagerDownAlert due to :  ",e);
		}
		return null;

	}


	
	@Override
	public Alert getHistoryServerDownAlert(Cluster cluster){
		
		try {
			String historyServerInstance = null;
			historyServerInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getHistoryServer(),
					JMXDeamons.JOB_HISTORY_SERVER.toString());
			if (historyServerInstance.isEmpty()) {
				return new Alert(ExtendedConstants.CRITICAL_LEVEL, cluster.getHistoryServer(),
						AlertConstants.HISTORY_SERVER + DEAMON_WENT_DOWN, getDate());
			}
		} catch (Exception e) {
			LOGGER.error("Unable to getHistoryServerDownAlert due to : ",e);
		}
		return null;

	}
	

	
	@Override
	public List<Alert> getResourceUtilizationAlert(Cluster cluster, List<Alert> nmAlerts) {
		
		Map<String, Object> nodeManagerOSStats = null;

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<Alert> alertList = new ArrayList<Alert>();
		String response= null;

		for(String worker:workers){
			if (isNodeManagerDown(worker, nmAlerts)) {
				continue;
			}
			LOGGER.debug("Going to check for ResourceUtilizationAlert");
			try {
				nodeManagerOSStats = jmxDump.getOSJMXStats(JMXDeamons.NODE_MANAGER, worker, cluster.getWorkers().getTaskExecutorJmxPort(), cluster.isJmxPluginEnabled());
				response = jmxDump.getFreeMemoryResponse(cluster, worker).trim();
				String[] memory = response.split("\\s+");
				Double totalPhysicalMemorySize = Double.parseDouble(memory[1]);
				Double freePhysicalMemorySize = Double.parseDouble(memory[3]);
				Double cpuLoad = Double.parseDouble(nodeManagerOSStats.get("SystemCpuLoad").toString());
				
				Double freeMemory = ((totalPhysicalMemorySize-freePhysicalMemorySize)/totalPhysicalMemorySize);		
				if(cpuLoad > 0.8){
					Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,worker,"CPU utilization exhausted threshold of 80%", getDate());
					alertList.add(alert);			
				}
				if(freeMemory > 0.90){
					Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,worker,"Memory utilization exhausted threshold of 90%", getDate());
					alertList.add(alert);			
				}	
			} catch(Exception e) {
				LOGGER.error( "Error while retrieving Resource Utilization Alert",e);
			}
		}
		return alertList;
	}
	
	private boolean isNodeManagerDown(String worker, List<Alert> nmAlerts) {
		if (nmAlerts == null) {
			return false;
		}
		for (Alert alert: nmAlerts) {
			if (worker.equals(alert.getNodeIP())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<Alert> getNodeManagerDownAlert(Cluster cluster, RMCommunicator rmCommunicator) {
		List<String> workers = cluster.getWorkers().getHosts();
		List<Alert> alertList = new ArrayList<>(workers.size());
		try {
			for (NodeReport nodeReport : rmCommunicator.getNodeReports()) {

				if (nodeReport.getNodeState().toString().equalsIgnoreCase("LOST")) {
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, nodeReport.getNodeId().getHost(),
							AlertConstants.NODE_MANAGER + DEAMON_WENT_DOWN, getDate());
					alertList.add(alert);
				}

			}
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get node report details",e);		
		}
		return alertList;
	}
	
	@Override
	public List<Alert> getNodeUnhealthyAlert(Cluster cluster, RMCommunicator rmCommunicator){

		List<NodeReport> nodeReportList;
		List<Alert> alertList = new ArrayList<Alert>();
		try {
			nodeReportList = rmCommunicator.getNodeReports();

		
			for (NodeReport nodeReport : nodeReportList) {
				String host = InetAddress.getByName(nodeReport.getNodeId().getHost()).getHostAddress();
				if (nodeReport.getNodeState().toString().equals("UNHEALTHY")) {
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, host,
							" NodeManager" + DEAMON_IS_UNHEALTHY, getDate());
					alertList.add(alert);
				}

			}
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get unhealthy nodes from the api due to: ",e);		
		}
		return alertList;
	}

	
	@Override
	public List<Alert> checkYarnPropertySetCorrectly (Cluster cluster){
		List<Alert> alertList = new ArrayList<Alert>();

		Configuration configuration = new Configuration();
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
		File file = new File(localConfFilePath);
		if(!file.exists()){
			String filePath = RemotingUtil.addHadoopResource(configuration, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
			configuration.addResource(new Path(filePath));
		}
		configuration.addResource(new Path(localConfFilePath));
		Double minimumAllocatedMemory= Double.parseDouble(configuration.get("yarn.scheduler.minimum-allocation-mb"));
		Double maxVcores = Double.parseDouble(configuration.get("yarn.scheduler.maximum-allocation-vcores"));
		Double minVcores = Double.parseDouble(configuration.get("yarn.scheduler.minimum-allocation-vcores"));
		Double maxAllocatedMemory = Double.parseDouble(configuration.get("yarn.scheduler.maximum-allocation-mb"));
		Double nodeManagerCore = Double.parseDouble(configuration.get("yarn.nodemanager.resource.cpu-vcores"));
		Double nodeManagerMemory = Double.parseDouble(configuration.get("yarn.nodemanager.resource.memory-mb"));

		
		if(maxVcores < minVcores){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores is greater than yarn.scheduler.maximum-allocation-vcores, change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		if(maxAllocatedMemory < minimumAllocatedMemory){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb is greater than yarn.scheduler.maximum-allocation-mb, change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}	
		if(minVcores < 1){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores value is less than one, change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		if(nodeManagerCore < minVcores){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores is greater than yarn.nodemanager.resource.cpu-vcores, change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		if(maxVcores < 1){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores value is less than one,change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		if(nodeManagerMemory < minimumAllocatedMemory){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb is greater than yarn.nodemanager.resource.memory-mb,change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		if(minimumAllocatedMemory < 1024){
			file.delete();
			Alert alert = new Alert (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb value is less than 1024,change property value in yarn-site.xml", getDate());
			alertList.add(alert);
		}
		return alertList;
	}

	/**
	 * Gets the all completed job id.
	 *  
	 */
	private String[] getJobList(String clusterName) {
        StringBuffer jobIdsPath = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(Constants.JOB_JARS_LOC)
                        .append(clusterName).append(CLUSTER_PROFILING);
        File f = new File(jobIdsPath.toString());
        String[] jobIds = f.list();
        return jobIds;
     }


	/**
	 * Gets the minimum parameter mandatory for container.
	 *
	 * @param parameter the parameter
	 * @param value the value
	 * @param cluster the cluster
	 * @return the minimum parameter mandatory for container
	 */
	private int getMinimumParameterMandatoryForContainer(String parameter , int value, Cluster cluster) {
		Configuration c = new Configuration();
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
		File file = new File(localConfFilePath);
		if(!file.exists()){
			String filePath = RemotingUtil.addHadoopResource(c, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
			c.addResource(new Path(filePath));
		}
		c.addResource(new Path(localConfFilePath));
		return c.getInt(parameter, value);
	}

	/**
	 * Gets the current date and time.
	 *
	 * @return the date
	 */
	private String getDate(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(ExtendedConstants.TIME_FORMAT);	
		return sdf.format(date);
	}
	
}
