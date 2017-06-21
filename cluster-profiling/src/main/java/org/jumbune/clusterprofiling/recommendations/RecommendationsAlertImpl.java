package org.jumbune.clusterprofiling.recommendations;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusterprofiling.SchedulerService;
import org.jumbune.clusterprofiling.yarn.beans.FairSchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.Scheduler;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.clusterprofiling.RamContainerDetailsUtility;

public class RecommendationsAlertImpl implements RecommendationAlerts {
	
	
	/** The Constant IP_PATTERN. */
	public static final Pattern IP_PATTERN = Pattern.compile("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}") ;
	
	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(RecommendationsAlertImpl.class);
	
	public static volatile RecommendationsAlertImpl instance = null;
	
	public static RecommendationsAlertImpl getInstance() {
		if (instance == null) {
			synchronized (RecommendationsAlertImpl.class) {
				if (instance == null) {
					instance = new RecommendationsAlertImpl();
				}
			}
		}
		return instance;
	}
	
	private RecommendationsAlertImpl() {}

	@Override
	public Set<Recommendations> checkMemoryConfiguration(Cluster cluster){
		Set<Recommendations> recommendationSet = new HashSet<Recommendations>();
		RamContainerDetailsUtility ramContainerDetailsUtility = new RamContainerDetailsUtility();
		Map<String,Double> ipRam = ramContainerDetailsUtility.getRamNodeTable(cluster);
		for(String host : cluster.getWorkers().getHosts()){
			if(!IP_PATTERN.matcher(host).matches()){
				host = RemotingUtil.getIPfromHostName(cluster, host);
			}

			Double physicalRam = ipRam.get(host);
			if(physicalRam==null){
				physicalRam = ipRam.get("127.0.0.1");
			}
			if(physicalRam != null){
			Double systemReservedRam = ramContainerDetailsUtility.getSystemReservedMemory(physicalRam);
			Double actualMemory = physicalRam - systemReservedRam ;
			try {
				int cores  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Core", host);
				int thread  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Thread", host);
				int socket  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Socket", host);
				int totalCores = cores * thread * socket ;
				int actualCores ;
				if(totalCores <= 4){
					actualCores = (totalCores * 2) - 2 ;
				}else{
					actualCores = (totalCores * 2) - 4 ;
				}
				
				String yarnSitePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster) + File.separator + host + File.separator + ExtendedConstants.YARN_SITE_XML ;
				File f = new File(yarnSitePath);
				if(f.exists()){
					f.delete();
				}
				String mapredSitePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster) + File.separator + host + File.separator + ExtendedConstants.MAPRED_SITE_XML ;
				File file = new File(mapredSitePath);
				if(file.exists()){
					file.delete();
				}
				String confMemValue = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.YARN_SITE_XML, "yarn.nodemanager.resource.memory-mb", host);
				String confCorValue = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.YARN_SITE_XML, "yarn.nodemanager.resource.cpu-vcores", host);
				String confMapMemory = RemotingUtil.getHadoopConfigurationValue(cluster,ExtendedConstants.MAPRED_SITE_XML,"mapreduce.map.memory.mb", host);
				String reduceMemory = RemotingUtil.getHadoopConfigurationValue(cluster,ExtendedConstants.MAPRED_SITE_XML,"mapreduce.reduce.memory.mb", host);
				double optsMemory = 1024 ;
				double optsmapMemory = 0 ,optsredMemory = 0;
				if(confMapMemory != null && !confMapMemory.isEmpty()){
					optsmapMemory = Double.parseDouble(confMapMemory);
				}
				if(reduceMemory != null && !reduceMemory.isEmpty()){
					optsredMemory = Double.parseDouble(reduceMemory);
				}
				if(optsmapMemory > optsredMemory){
					optsMemory = optsmapMemory ;
				}else if (optsredMemory > optsmapMemory){
					optsMemory = optsredMemory ;
				}
				// default values
				double confMem = 8192 ; int confCores = 8 ;
				Map<Double,Integer> memVcoreMap = ramContainerDetailsUtility.getBalancedMemVcore(actualMemory, actualCores, optsMemory);
				for (Map.Entry<Double, Integer> entryMap : memVcoreMap.entrySet()) {
					long optimMem = Math.round(entryMap.getKey());
					int	 optimCores =  entryMap.getValue() ;
					if(confMemValue != null && !confMemValue.isEmpty()){
						confMem = Double.parseDouble(confMemValue);
					}
						confMem = (confMem / 1024) ;
						if((optimMem - confMem ) > 0 || (confMem - optimMem) > 0){
						Recommendations recommendations = new Recommendations (InetAddress.getByName(host).getHostName(),"Optimal Capacity as resource can be "+ optimMem +" GB, can be configured using property (yarn.nodemanager.resource.memory-mb) in yarn-site.xml");
						recommendationSet.add(recommendations);	
						}
					
					if(confCorValue !=null && !confCorValue.isEmpty()){
						confCores = Integer.parseInt(confCorValue);
					}
						if(confCores - optimCores > 0 || optimCores - confCores > 0){
						Recommendations recommendations = new Recommendations (InetAddress.getByName(host).getHostName(),"Optimal VCores as resource can be "+ optimCores +", can be configured using property (yarn.nodemanager.resource.cpu-vcores) in yarn-site.xml");
						recommendationSet.add(recommendations);
					
					}
				}
			} catch (Exception e) {
				LOGGER.error("Unable to get memory,vcore recommendations due to: ",e);
			}
		}}
		return recommendationSet;
	}
	
	@Override
	public Set<Recommendations> checkYarnProperty (Cluster cluster){		
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();
		try {
			Configuration configuration = new Configuration();
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
			String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
			File file = new File(localConfFilePath);
			if(!file.exists()){
				String filePath = RemotingUtil.addHadoopResource(configuration, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
				configuration.addResource(new Path(filePath));
			}
			configuration.addResource(new Path(localConfFilePath));
			String setProperty= configuration.get("yarn.scheduler.minimum-allocation-mb");

			if(setProperty == null){
				file.delete();
				Recommendations recommendations = new Recommendations(ExtendedConstants.HYPHEN,"Set property: yarn.scheduler.minimum-allocation-mb in yarn-site.xml");
				recommendationsSet.add(recommendations);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get yarn property[yarn.scheduler.minimum-allocation-mb] recommendation due to: ",e);
		}
		return recommendationsSet;
	}
	
	
	@Override
	public Set<Recommendations> getRecommendedContainerConfiguration(Cluster cluster){		
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();
		try {
			RamContainerDetailsUtility ramContainerDetailsUtility = new RamContainerDetailsUtility();
			Configuration configuration = new Configuration();

			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
			String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster) + ExtendedConstants.YARN_SITE_XML;

			File file = new File(localConfFilePath);
			if(!file.exists()){
				String filePath = RemotingUtil.addHadoopResource(configuration, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
				configuration.addResource(new Path(filePath));
			}
			configuration.addResource(new Path(localConfFilePath));
			String setProperty= configuration.get("yarn.scheduler.minimum-allocation-mb");
			Double containerSize = Double.parseDouble(setProperty);

			Map<Double,List<String>> ramIp= ramContainerDetailsUtility.getRamPerNode(cluster);
			Map<Double,List<String>> recommendedIp =ramContainerDetailsUtility.getRecommendedContainerSize(ramIp);

			for(Map.Entry<Double, List<String>> recommended:recommendedIp.entrySet()){
				if(containerSize < recommended.getKey()){
					Recommendations recommendations = new Recommendations (ExtendedConstants.HYPHEN,"Recommended minimum container size is "+recommended.getKey()+" can be configured using property 'yarn.scheduler.minimum-allocation-mb' in yarn-site.xml");
					recommendationsSet.add(recommendations);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to getRecommendedContainerConfiguration due to : ",e);
		}
		return recommendationsSet;
	}
	
	public  Set<Recommendations> checkTransparentHugePageStatus(Cluster cluster){
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();		
		try{
		List<String> clusterNodeList = cluster.getWorkers().getHosts();
		for(String clusterNode : clusterNodeList){	
		StringBuffer commandToExecute = new StringBuffer(); 
		commandToExecute.append("if [ -f /sys/kernel/mm/transparent_hugepage/enabled ]; then cat /sys/kernel/mm/transparent_hugepage/enabled;");	  
		commandToExecute.append(" elif [ -f /sys/kernel/mm/redhat_transparent_hugepage/enabled ]; then cat /sys/kernel/mm/redhat_transparent_hugepage/enabled;");
		commandToExecute.append(" else echo \"file not found\"; fi && exit");		
		String responseOfCommandExecute = RemotingUtil.executeCommand(cluster,commandToExecute.toString(),clusterNode).trim();		
		if(responseOfCommandExecute.contains("[always]")){
			Recommendations recommendations = new Recommendations (InetAddress.getByName(cluster.getNameNode()).getHostName(),"Transparent Huge Pages found to be enabled , needs to be disabled to gain better performance");
			recommendationsSet.add(recommendations);
		}else if(responseOfCommandExecute.contains("[madvise]")){
			Recommendations recommendations = new Recommendations (InetAddress.getByName(cluster.getNameNode()).getHostName(),"Transparent Huge Pages found to be madvise , needs to be disabled to gain better performance");
			recommendationsSet.add(recommendations);
		}else if(responseOfCommandExecute.contains("file not found")){
			Recommendations recommendations = new Recommendations (InetAddress.getByName(cluster.getNameNode()).getHostName(),"Transparent Huge Pages file do not exists");
			recommendationsSet.add(recommendations);
		}
		}
		}catch(Exception e){			
			LOGGER.error("Unable to checkTransparentHugePageStatus recommendation due to : ",e);
		}		
		return recommendationsSet;
	}
	
	public  Set<Recommendations> checkSELinuxStatus(Cluster cluster){		
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();
		try{
		List<String> clusterNodeList = cluster.getWorkers().getHosts();
		for(String clusterNode : clusterNodeList){	
		StringBuffer commandToExecute = new StringBuffer();
		commandToExecute.append("if [ -f /etc/selinux/config ]; then if [ `cat /etc/selinux/config | grep -w -c SELINUX` -gt 0 ];");
		commandToExecute.append(" then  cat /etc/selinux/config | grep -w SELINUX | cut -d= -f2 | sed 's/ //g'; else echo \"property not found\"; fi else echo \"file not found\"; fi && exit");		
		String responseOfCommandExecute = RemotingUtil.executeCommand(cluster,commandToExecute.toString(),clusterNode).trim();
		if(responseOfCommandExecute.equals("file not found")){
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"Security - Enhanced Linux (SELinux) configuration file do not exist");
			recommendationsSet.add(recommendations);
		}else if(responseOfCommandExecute.equals("property not found")) {		
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"Security - Enhanced Linux (SELinux) property should be configured and set as disabled");
			recommendationsSet.add(recommendations);
		}else if(responseOfCommandExecute.equals("enabled")){			
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"Security - Enhanced Linux (SELinux) property should be disabled");
			recommendationsSet.add(recommendations);
		}
		}
		}catch(Exception e){
			LOGGER.error("Unable to checkSELinuxStatus due to :",e);
		}
		return recommendationsSet;
	}
	
	public  Set<Recommendations> checkVMSwappinessParam(Cluster cluster){		
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();
		try{
		List<String> clusterNodeList = cluster.getWorkers().getHosts();
		for(String clusterNode : clusterNodeList){
		StringBuffer commandToExecute = new StringBuffer();			
		commandToExecute.append("if [ -f /etc/sysctl.conf ]; then if [ `cat /etc/sysctl.conf | grep -w -c vm.swappiness` -gt 0 ];");
		commandToExecute.append(" then  cat /etc/sysctl.conf | grep -w vm.swappiness | cut -d= -f2 | sed 's/ //g'; else echo \"property not found\"; fi else echo \"file not found\"; fi && exit");				
		String responseOfCommandExecute = RemotingUtil.executeCommand(cluster,commandToExecute.toString(),clusterNode).trim();		
		if(responseOfCommandExecute.equals("file not found")){			
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"VirtualMemory Swapiness configuration file sysctl.conf does not exists");
			recommendationsSet.add(recommendations);
		}else if(responseOfCommandExecute.equals("property not found")) {							
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"VirtualMemory Swapiness property should be configured and value to be set between 0 and 10");
			recommendationsSet.add(recommendations);
		}else if(Integer.parseInt(responseOfCommandExecute) > ExtendedConstants.VM_SWAPPINESS_STANDARD_VAL){			
			Recommendations recommendations = new Recommendations (InetAddress.getByName(clusterNode).getHostName(),"VirtualMemory Swapiness can be set between 0 and 10");
			recommendationsSet.add(recommendations);
		}
		}
		}catch(Exception e){
			LOGGER.error("Unable to checkVMSwappinessParam recommendation due to: ",e);
		}
		return recommendationsSet;
	}

	@Override
	public Set<Recommendations> getSparkConfigurations(
			Cluster cluster, boolean fairSchedulerFlag, SchedulerService schedulerService,
			RMCommunicator rmCommunicator) {
		Set<Recommendations> recommendationsSet = new HashSet<Recommendations>();
		Set<String> applicationType = new HashSet<String>();
		applicationType.add(Constants.SPARK);
		
		int totalCores = 0 ;
		Double actualMemory = 0.0 ;
		List<ApplicationReport> applicationReport = null;
		try {
			applicationReport = rmCommunicator.getApplications(applicationType);
		} catch (YarnException | IOException e1) {
			LOGGER.error("Unable to get applications for the type[" + applicationType + "]",e1);
		}
		if(!applicationReport.isEmpty()){
			if(fairSchedulerFlag){
				try {
					Scheduler scheduler = schedulerService.fetchSchedulerInfo(cluster);
					
					for (FairSchedulerQueueInfo fairSchedulerQueueInfo : scheduler.getFairSchedulerLeafQueues()) {
						totalCores += (int) fairSchedulerQueueInfo.getFinalSteadyFairVCores();
						actualMemory += (double) fairSchedulerQueueInfo.getFinalSteadyFairMemory();
					}
				} catch (Exception e) {
				LOGGER.error(e);
				}
			}
		actualMemory = actualMemory/1024;
	//	List<String> workerhosts = cluster.getWorkers().getHosts();
	//	for (String host : workerhosts) {
			//Todo : Need to check the conf for worker nodes as well , keeping to namenode only
			String host = cluster.getNameNode();
			try{
			if(totalCores == 0){
			int cores  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Core", host);
			int thread  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Thread", host);
			int socket  =  RemoteFileUtil.getRemoteThreadsOrCore(cluster,"Socket", host);

			totalCores = cores * thread * socket ;
				}
			int actualCores = 0;
			if(totalCores <= 4){
				actualCores = (totalCores * 2) - 2 ;
			}else{
				actualCores = (totalCores * 2) - 4 ;
			}
			// get Optimum spark.executor.cores 
			// ToDo : Need to calculate as per scheduling policies 
		
			int div4C = actualCores % 4 ; 
			int div5C = actualCores % 5 ;
			int div6C = actualCores % 6 ;
			int executorCor = 0 ;
			if(div4C == 0){
				if(actualCores > 4){
					executorCor = actualCores/4 ;	
				}else {
					executorCor = 4 ;
				}
				
			}else if (div5C == 0){
				if(actualCores > 5){
					executorCor = actualCores/5 ;	
				}else {
					executorCor = 5 ;
				}
			}else if (div6C == 0){
				if(actualCores > 6){
					executorCor = actualCores/6 ;	
				}else {
					executorCor = 6 ;
				}
			}
			// deletes the already existing spark conf file
			String sparkConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster) + File.separator + host + "/spark-defaults.conf" ;
			File sparkCFile = new File(sparkConfFilePath);
			if(sparkCFile.exists()){
				sparkCFile.delete();
			}
			LOGGER.debug("Going to fetch Spark Conf Dir");
			String sparkConfDir = RemotingUtil.executeCommand(cluster, "echo $SPARK_CONF_DIR" , host);
			LOGGER.debug("Found Spark Conf Dir" + sparkConfDir);
			String sparkConfFile = RemotingUtil.copyAndGetConfigurationFilePath(cluster, sparkConfDir, "/spark-defaults.conf",host);
			sparkConfFile = sparkConfFile + "/spark-defaults.conf" ;
			String confExecutorCores = FileUtil.getPropertyFromFile(sparkConfFile, "spark.executor.cores");
			if(confExecutorCores != null  && !confExecutorCores.isEmpty()){
				int confExecutorCore = Integer.parseInt(confExecutorCores);
				if(executorCor - confExecutorCore > 0 || confExecutorCore - executorCor > 0){
					Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark executor cores as resource can be " + executorCor +", can be configured using property (spark.executor.cores) in spark-defaults.conf");
					recommendationsSet.add(recommendations);
				}
			}
			// Optimum executor core end 
			
			// get optimum executor to run 
			String confNumOfExe =  FileUtil.getPropertyFromFile(sparkConfFile, "spark.executor.instances");
			int numOfExecutors = (actualCores/executorCor) ;
			if(confNumOfExe !=null && !confNumOfExe.isEmpty()){
			int confNumExe = Integer.parseInt(confNumOfExe);
			if(numOfExecutors - confNumExe > 0 || confNumExe - numOfExecutors > 0){
			Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark executors as resource can be " + numOfExecutors +", can be configured using property (spark.executor.instances) in spark-defaults.conf");
			recommendationsSet.add(recommendations);
			}}
			// get optimum executor end
			
			RamContainerDetailsUtility ramContainerDetailsUtility = new RamContainerDetailsUtility();
			Map<String,Double> ipRam = ramContainerDetailsUtility.getRamNodeTable(cluster);
			Double physicalRam = ipRam.get(host);
			//todo : change or confirm the host name 
			if(physicalRam==null){
				physicalRam = ipRam.get("127.0.0.1");
			}
			if(physicalRam != null){
			Double systemReservedRam = ramContainerDetailsUtility.getSystemReservedMemory(physicalRam);
			if(actualMemory == 0){
			actualMemory = physicalRam - systemReservedRam ;
			}else{
				actualMemory = actualMemory - systemReservedRam ;
			}
			String yarnSitePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster) + File.separator + host + File.separator + ExtendedConstants.YARN_SITE_XML ;
			File f = new File(yarnSitePath);
			if(f.exists()){
				f.delete();
			}
			String confMemValue = RemotingUtil.getHadoopConfigurationValue(cluster, ExtendedConstants.YARN_SITE_XML, "yarn.nodemanager.resource.memory-mb", host);
			// get optimum spark driver memory
			double confMem = 8192 ; // default memory
			if(confMemValue != null && !confMemValue.isEmpty()){
				confMem = Double.parseDouble(confMemValue);
			}
			Double sparkDriverMemory = ramContainerDetailsUtility.getSparkDriverMemory(confMem);
			Double sparkDriMemOverHead = (0.10 * sparkDriverMemory);
			sparkDriMemOverHead = sparkDriMemOverHead > 0.38 ? (sparkDriMemOverHead) : 0.38 ;
			sparkDriverMemory = sparkDriverMemory - sparkDriMemOverHead ;
			
			String confDriverMem = FileUtil.getPropertyFromFile(sparkConfFile, "spark.driver.memory");
			if(confDriverMem != null && !confDriverMem.isEmpty()){
			int confDriMem = (ConfigurationUtil.getJavaOptsinMB(confDriverMem))/1024;
			if(sparkDriverMemory - confDriMem  > 0 || confDriMem - sparkDriverMemory > 0){
			Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark driver memory as resource can be " + sparkDriverMemory + " GB, can be configured using property (spark.driver.memory) in spark-defaults.conf");
			recommendationsSet.add(recommendations);
			}
			}
			// end optimum spark driver memory
			
			// optimum spark driver overhead memory 
			String confDriverOverMem = FileUtil.getPropertyFromFile(sparkConfFile, "spark.yarn.driver.memoryOverhead");
			if(confDriverOverMem != null && !confDriverOverMem.isEmpty()){
			int confDrivOverMem = (ConfigurationUtil.getJavaOptsinMB(confDriverOverMem))/1024;
			if(sparkDriMemOverHead - confDrivOverMem > 0 || confDrivOverMem - sparkDriMemOverHead > 0){
			Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark driver overhead memory as resource can be " + sparkDriMemOverHead + " GB, can be configured using property (spark.yarn.driver.memoryOverhead) in spark-defaults.conf");
			recommendationsSet.add(recommendations);
			}
			}
			// end optimum spark driver overhead memory
			
			// get optimum spark executor memory  
			Double sparkExecutorMemory = (actualMemory / numOfExecutors) ;
			sparkExecutorMemory = (sparkExecutorMemory - sparkDriverMemory );
			Double sparkExeOverHeadMem = (0.10 * sparkExecutorMemory);
			sparkExeOverHeadMem = sparkExeOverHeadMem > 0.38 ? (sparkExeOverHeadMem) : 0.38 ;
			sparkExecutorMemory = sparkExecutorMemory - sparkExeOverHeadMem ;
			String confExecMem = FileUtil.getPropertyFromFile(sparkConfFile, "spark.executor.memory");
			if(confExecMem !=null && !confExecMem.isEmpty()){
			int confExecMemory = (ConfigurationUtil.getJavaOptsinMB(confExecMem))/1024;
			if(sparkExecutorMemory - confExecMemory > 0 || confExecMemory - sparkExecutorMemory > 0){
			Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark executor memory as resource can be " + Math.floor(sparkExecutorMemory) + " GB, can be configured using property (spark.executor.memory) in spark-defaults.conf");
			recommendationsSet.add(recommendations);
			}}
			// end optimum spark executor memory  
			
			// get optimum spark executor overhead memory  
			String confExeOveMem = FileUtil.getPropertyFromFile(sparkConfFile,"spark.yarn.executor.memoryOverhead");
			if(confExeOveMem != null && !confExeOveMem.isEmpty()){
			int confExOvMem = (ConfigurationUtil.getJavaOptsinMB(confExeOveMem))/1024;
			if(sparkExeOverHeadMem - confExOvMem > 0 || confExOvMem - sparkExeOverHeadMem > 0){
 			Recommendations recommendations = new Recommendations(InetAddress.getByName(host).getHostName(), "Optimal spark executor overhead memory as resource can be " + sparkExeOverHeadMem + " GB, can be configured using property (spark.yarn.executor.memoryOverhead) in spark-defaults.conf");
			recommendationsSet.add(recommendations);
			}}
			// end optimum spark executor overhead memory  
			}
		}catch (Exception e) {
			LOGGER.error("Unable to check spark recommendations due to: ",e);
		}
		
	
		}//}
		return recommendationsSet;
}}