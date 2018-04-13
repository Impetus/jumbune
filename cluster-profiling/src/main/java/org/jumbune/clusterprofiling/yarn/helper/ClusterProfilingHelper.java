package org.jumbune.clusterprofiling.yarn.helper;

import static org.jumbune.common.utils.Constants.SPACE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.JobInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.LiveCapacityStats;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.beans.RackAwareStats;
import org.jumbune.clusterprofiling.yarn.beans.YarnQueueStats;
import org.jumbune.common.beans.EffCapUtilizationStats;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yarn.utils.DecoratedJobHistoryParser;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.utils.yarn.communicators.MRCommunicator;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.utils.yarn.communicators.YarnCommunicatorUtil;

/**
 * The Class ClusterProfilingHelper.
 */
public class ClusterProfilingHelper {

	private static final String DOUBLE_QUOTE = "\"";

	private static final String CONTAINER_E0_9 = "container_[e0-9]*_";

	private static final String CONTAINER = "container_";

	private static final String E = "-e \"";

	private static final String NEW_LINE = "\\n";

	/** The LOGGER. */
	private final static Logger LOGGER = LogManager.getLogger(ClusterProfilingHelper.class);

	/** The Constant ROOT. */
	private final String ROOT = "root";

	/** The Constant CLUSTER_PROFILING. */
	public final static String CLUSTER_PROFILING = "/clusterprofiling/";

	/** The Constant UTILIZATION_STATS. */
	public final static String UTILIZATION_STATS = "/utilizationStats/";

	/** The Constant JSON. */
	public final static String JSON = ".json";

	/** The Constant RACKWARE. */
	public final static String RACKWARE = "/rackaware/";

	/** The Constant RACKWARE. */
	private final String RACK = "/rackaware";

	/** The Constant JHIST. */
	public final static String JHIST = ".jhist";

	/** The Constant JOB_COUNTER. */
	private final String JOB_COUNTER = "org.apache.hadoop.mapreduce.JobCounter";

	/** The Constant DATA_LOCAL_MAPS. */
	private final String DATA_LOCAL_MAPS = "DATA_LOCAL_MAPS";

	/** The Constant RACK_LOCAL_MAPS. */
	private final String RACK_LOCAL_MAPS = "RACK_LOCAL_MAPS";

	/** The Constant OTHER_LOCAL_MAPS. */
	private final String OTHER_LOCAL_MAPS = "OTHER_LOCAL_MAPS";

	/** The Constant YARN_JOB_STATS_UTILITY_CLASS. */
	private final String YARN_JOB_STATS_UTILITY_CLASS = "org.jumbune.common.yarn.utils.YarnJobStatsUtility";

	/** The Constant YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD. */
	private final String YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD = "parseAndGetJobStats";	

	/** The Constant HDFS_FILE_GET_COMMAND. */
	public final static String HDFS_FILE_GET_COMMAND = "/bin/hadoop fs -get";

	/** The Constant EFFECTIVE_MAX_UTILISATION. */
	private final String EFFECTIVE_MAX_UTILISATION = "maxUtilisation";

	/** The Constant YARN_MINIMUM_VCORE. */

	private final String YARN_CONTAINER_MINIMUM_VCORE = "yarn.scheduler.minimum-allocation-vcores";
	/** The Constant YARN_CONTAINER_MINIMUM_MEMORY. */
	private final String YARN_CONTAINER_MINIMUM_MEMORY = "yarn.scheduler.minimum-allocation-mb";

	/**  The Constant COPY. */
	private final String COPY = "_copy";
	
	private static final String REGEX1 = "(.*)(container_(?:[e\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+))(.*)";
	
	/** The Constant SUFFIX. */
	public final static String SUFFIX = UTILIZATION_STATS+"containerList"+JSON;
	
	private final String SORTING_COMMAND = "|sort -h -r|head -1 | sed 's/  */,/g' | sed 's/:,/|/g' | cut -d\\| -f2,3 | cut -d\\| -f2 | cut -d, -f1,2,3,4,5 | sed 's/,of,/|/g' | sed 's/,/ /g' | sed 's/|/ /g'";
	
	private final String CONTAINER_UTILIZATION_CDH = 
			"(FOUNDING_STARTED=0;for FILE in $(shopt -s nocaseglob;ls -1 /var/log/hadoop-yarn/*yarn*NODEMANAGER*.log*);"
			+ " do THIS_LOOP_FOUND=0; if grep -q -F \"$( printf '%s' )\" $FILE; then cat $FILE | grep -F \"$( printf '%s' )\" "
			+ "| grep -F \"$( printf 'KB\\nMB\\nGB\\nTB\\nPB' )\" "
			+ SORTING_COMMAND
			+ "; fi; if [ $FOUNDING_STARTED == 1 ] && [ $THIS_LOOP_FOUND = 0 ]; then break; fi; done) && exit";
	
	private final String CONTAINER_UTILIZATION_APACHE = 
			"(FOUNDING_STARTED=0;for FILE in $(shopt -s nocaseglob;ls -1 HADOOP_HOME/logs/*yarn*nodemanager*.log*);"
			+ " do if grep -q -F \"$( printf '%s' )\" $FILE; then cat $FILE | grep -F \"$( printf '%s' )\" "
			+ "| grep -F \"$( printf 'KB\\nMB\\nGB\\nTB\\nPB' )\" "
			+ SORTING_COMMAND
			+ "; fi; if [ $FOUNDING_STARTED == 1 ] && [ $THIS_LOOP_FOUND = 0 ]; then break; fi; done) && exit";
	
	//	(for FILE in $(ls -d -1 /home/impadmin/mapr/*.* | grep 'yarn' | grep -i 'NODEMANAGER'); do if [[ -r $FILE ]] && grep -q -e "container_[e0-9]*_1498734007164_0001_01_000003" $FILE; then cat $FILE | grep -e "container_[e0-9]*_1498734007164_0001_01_000003" | grep -F "$( printf 'KB\nMB\nGB\nTB\nPB' )" |sort -h -r|head -2 | sed 's/  */,/g' | sed 's/:,/|/g';break; fi done) && exit
	private final String CONTAINER_UTILIZATION_MAPR = 
			"(FOUNDING_STARTED=0;for FILE in $(shopt -s nocaseglob;ls -1 HADOOP_HOME/logs/*yarn*nodemanager*.log*);"
			+ " do if grep -q %s $FILE; then cat $FILE | grep %s "
			+ "| grep -F \"$( printf 'KB\\nMB\\nGB\\nTB\\nPB' )\" "
			+ SORTING_COMMAND
			+ "; fi; if [ $FOUNDING_STARTED == 1 ] && [ $THIS_LOOP_FOUND = 0 ]; then break; fi; done) && exit";
	
	private final String CONTAINER_UTILIZATION_HDP = 
			"(FOUNDING_STARTED=0;for FILE in $(shopt -s nocaseglob;ls -1 /var/log/hadoop-yarn/*/*yarn*nodemanager*.log*);"
			+ " do if grep -q %s $FILE; then cat $FILE | grep %s "
			+ "| grep -F \"$( printf 'KB\\nMB\\nGB\\nTB\\nPB' )\" "
			+ SORTING_COMMAND
			+ "; fi; if [ $FOUNDING_STARTED == 1 ] && [ $THIS_LOOP_FOUND = 0 ]; then break; fi; done) && exit";
	
	/** The Constant PREFIX. */
	public final static String PREFIX = JumbuneInfo.getHome() + ExtendedConstants.JOB_JARS_LOC;
	
	private static volatile ClusterProfilingHelper instance = null;
	
	private Pattern pattern = Pattern.compile("container_(?:[e\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)");
	
	public static ClusterProfilingHelper getInstance() {
		if (instance == null) {
			synchronized (ClusterProfilingHelper.class) {
				if (instance == null) {
					instance = new ClusterProfilingHelper();
				}
			}
		}
		return instance;
	}

	private ClusterProfilingHelper() {
		
	}


	/**
	 * Populate queue stats.
	 *
	 * @param cluster the cluster
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public List<QueueStats> populateQueueStats(String clusterName, RMCommunicator rmCommunicator) throws IOException, InterruptedException {
		List<QueueStats> queueInformation = new ArrayList<QueueStats>();
		QueueInfo queueInfo;
		try {
			queueInfo = rmCommunicator.getQueueInfo(ROOT);
			Stack<QueueInfo> stack = new Stack<QueueInfo>();
			stack.push(queueInfo);
			QueueInfo temp;
			List<QueueInfo> list = null;

			while (!stack.isEmpty()){
				temp = stack.pop();
				YarnQueueStats yarnQueueStats = new YarnQueueStats();
				if(!temp.getQueueName().equals(ROOT)){
					yarnQueueStats.setQueueName(temp.getQueueName());
					if (Constants.CLOUDERA.equals(FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION))
							|| Constants.MAPR.equals(FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION))|| Constants.EMRMAPR.equals(FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION))){//mapr code changes
						yarnQueueStats.setMaximumCapacity(temp.getCapacity()*100);
						yarnQueueStats.setCapacity(temp.getCapacity()*100);
						yarnQueueStats.setCurrentCapacity(temp.getCurrentCapacity()*100);
					}else{
						yarnQueueStats.setMaximumCapacity(temp.getMaximumCapacity()*100);
						yarnQueueStats.setCapacity(temp.getCapacity()*100);
						yarnQueueStats.setCurrentCapacity(temp.getCurrentCapacity()*100);
					}
					queueInformation.add(yarnQueueStats);
				}
				list = temp.getChildQueues();
				if (list != null && !list.isEmpty()) {
					Iterator<QueueInfo> it = list.iterator();
					while (it.hasNext()) {
						stack.push(it.next());
					}
				}
				yarnQueueStats =null;
			}
			return queueInformation;
		} catch (YarnException e) {
			LOGGER.error(JumbuneRuntimeException.throwYarnException(e.getStackTrace()));
		}
		return queueInformation;

	}

	

	/**
	 * Gets the container report information.
	 *
	 * @param applicationAttemptId the application attempt id
	 * @return the container report info
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public List <ContainerReport> getContainerReportInfo(
			ApplicationAttemptId applicationAttemptId,
			RMCommunicator rmCommunicator)
					throws IOException, InterruptedException{
		List <ContainerReport> containerReport = null;
		try {
			containerReport = rmCommunicator.getContainerReport(applicationAttemptId);			
		} catch (YarnException e) {
			LOGGER.error(JumbuneRuntimeException.throwYarnException(e.getStackTrace()));
		} 
		return containerReport;

	}


	/**
	 * Prepare and persist running container utilization.
	 * @param curentTime 
	 *
	 * @param cluster the cluster
	 * @return 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public LinkedList<EffCapUtilizationStats> prepareAndPersistRunningContainerUtilization(
			Cluster cluster, MRCommunicator mrCommunicator,
			List<ApplicationReport> applicationReport) throws Exception {
		
		JobId jobId;
		StringBuffer capUtilJsonFilePath;
		Map<TaskId, TaskReport> reports;
		TaskReport taskReports;
		TaskAttemptId attemptId;
		TaskAttemptReport taskAttemptReport;
		// key = nodeIP, value = (key = "MAP ? REDUCE", value =containers list that would be available in that node.)
		Map<String, Map<String, Set<String>>> nodesContainers;
		
		EffCapUtilizationStats effCapUtilStats;
		File capUtilJsonFile;
		String sJobId, capUtilJsonReader, taskType;
		LinkedList<EffCapUtilizationStats> effCapUtilizationStats = new LinkedList<EffCapUtilizationStats>();
		
		
		// Fetching the jobs container values which haven't been fetched yet.
		for (ApplicationReport appReport : applicationReport) {
			// Getting Job id in string form
			sJobId = appReport.getApplicationId().toString().replace(Constants.APPLICATION, Constants.JOB);
			
			// Creating json file path eg.
			// /home/impadmin/jumbunehome/jobJars/SampleClouderaCluster/clusterprofiling/job_1503661274675_5856
			// /utilizationStats/job_1503661274675_5856maxUtilisation.json
			capUtilJsonFilePath = new StringBuffer().append(JumbuneInfo.getHome())
										.append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
										.append(cluster.getClusterName()).append(CLUSTER_PROFILING)
										.append(sJobId).append(UTILIZATION_STATS).append(sJobId)
										.append(EFFECTIVE_MAX_UTILISATION).append(JSON);
			capUtilJsonFile = new File(capUtilJsonFilePath.toString());
			
			// Checking if that file already exists, if exists then just read the contents of that file
			if (capUtilJsonFile.exists()) {
				capUtilJsonReader = ConfigurationUtil.readFileData(capUtilJsonFilePath.toString());
				effCapUtilStats = Constants.gson.fromJson(capUtilJsonReader, EffCapUtilizationStats.class);
				effCapUtilizationStats.push(effCapUtilStats);
				continue;
			}
			
			// otherwise fetch all the containers memory usage of that job by firing command,
			// calculate effective capacity utilization based on containers memory usage (maximum)
			// and then save  effective capacity utilization to the file
			try {
				jobId = YarnCommunicatorUtil.getJobId(sJobId);
				reports = mrCommunicator.getAllTaskReports(jobId);
				// key = nodeIP, value = (key = "MAP ? REDUCE", value = containers list that would be available in that node.)
				nodesContainers = new HashMap<>(5);
				for (Map.Entry<TaskId, TaskReport> report : reports.entrySet()) {
					taskReports = report.getValue();
					attemptId = taskReports.getSuccessfulAttempt();
					if (attemptId != null) {
						taskAttemptReport = mrCommunicator.getTaskAttemptReport(
								attemptId.getTaskId(), attemptId.getId());
						// task type ie. MAP or REDUCE
						taskType = taskAttemptReport.getTaskAttemptId().getTaskId().getTaskType().toString();
						addSubValue(nodesContainers, taskAttemptReport.getNodeManagerHost(), taskType,
								taskAttemptReport.getContainerId().toString());
					}
				}
				
				//containerValues, key = container id, value = container memory usage
				
				effCapUtilStats = new EffCapUtilizationStats();
				effCapUtilStats.setJobId(jobId.toString());
				effCapUtilStats.setJobName(appReport.getName());
				effCapUtilStats.setJobStartTime(appReport.getStartTime());
				effCapUtilStats.setJobFinishTime(appReport.getFinishTime());
				
				effCapUtilStats.setUsedMaxMapMemory(0L);
				effCapUtilStats.setAllocatedMapMemory(0L);
				effCapUtilStats.setUsedMaxReduceMemory(0L);
				effCapUtilStats.setAllocatedReduceMemory(0L);
				
				ClusterProfilingHelper.getInstance()
						.fetchContainersMaxUtilization(cluster, nodesContainers, effCapUtilStats);
				
				// Writing effective capacity utilization to a file as a json for use in future
				capUtilJsonFile.getParentFile().mkdirs();
				ConfigurationUtil.writeToFile(capUtilJsonFilePath.toString(), Constants.gson.toJson(effCapUtilStats));
				
				// add effCapUtilStats to the list to return.
				effCapUtilizationStats.push(effCapUtilStats);
				
			} catch (Exception e) {
				LOGGER.error("Unable to fetch capacity utilization of [" + sJobId + " ]", e);
			}
		}
		
		return effCapUtilizationStats;
	}

	private void addSubValue(Map<String, Map<String, Set<String>>> map, String nodeIP, String taskType, String containerId) {
		Map<String, Set<String>> taskTypesContainers = map.get(nodeIP);
		if (taskTypesContainers == null) {
			taskTypesContainers = new HashMap<>(2);
			map.put(nodeIP, taskTypesContainers);
		}
		Set<String> containers = taskTypesContainers.get(taskType);
		if (containers == null) {
			containers = new HashSet<>(5);
			taskTypesContainers.put(taskType, containers);
		}
		containers.add(containerId);
	}
	
	private Set<Set<String>> getSuperSet(Set<String> list, int batchSize) {
		Set<Set<String>> superset;
		if (list.size() < batchSize) {
			superset = new HashSet<>(1);
			superset.add(list);
		} else {
			int count = 0;
			superset = new HashSet<>(3);
			Set<String> temp = new HashSet<>();
			for (String string : list) {
				temp.add(string);
				count++;
				if (count == batchSize) {
					superset.add(temp);
					temp = new HashSet<>();
					count = 0;
				}
			}
			if (!temp.isEmpty()) {
				superset.add(temp);
			}
		}
		return superset;
	}
	
	/**
	 * 
	 * @param cluster
	 * @param nodesContainer
	 *            key = nodeIP, value = (key = "MAP or REDUCE", value = containers
	 *            list that would be available in that node.
	 * @param effCapUtilStats 
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> fetchContainersMaxUtilization(Cluster cluster,
			Map<String, Map<String, Set<String>>> nodesContainer,
			EffCapUtilizationStats effCapUtilStats)
			throws IOException {
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		
		String hadoopDistribution = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_DISTRIBUTION);
		Map<String, String> containerValues = new HashMap<String, String>();
		
		String node, join, response, taskType;
		Long usedMemory, allocatedMemory;
		String[] splitResponse;
		StringBuffer command = null;
		List<String> hostName;
		Set<Set<String>> superset;
		CommandWritableBuilder lsBuilder;
		
		for (Entry<String, Map<String, Set<String>>> nodesMap : nodesContainer.entrySet()) {
			node = nodesMap.getKey();
			for (Entry<String, Set<String>> taskTypeMap : nodesMap.getValue().entrySet()) {
				taskType = taskTypeMap.getKey();
				superset =  getSuperSet(taskTypeMap.getValue(), 30);
				for (Set<String> containersList : superset) {
					
					switch (hadoopDistribution.toLowerCase()) {
					
					case (ExtendedConstants.CLOUDERA) :
					case (ExtendedConstants.EMRAPACHE) : {
						join = StringUtils.join(NEW_LINE, containersList);
						command = new StringBuffer(StringUtils.format(CONTAINER_UTILIZATION_CDH, join, join));
						break;
					}
					case (ExtendedConstants.APACHE) : {
						join = StringUtils.join(NEW_LINE, containersList);
						command = new StringBuffer(StringUtils.format(CONTAINER_UTILIZATION_APACHE, join, join));
						break;
					}
					case (ExtendedConstants.MAPR) : {
						List<String> list = new ArrayList<>();
						for (String container : containersList) {
							list.add(E + container.replace(CONTAINER, CONTAINER_E0_9) + DOUBLE_QUOTE);
						}
						join = StringUtils.join(Constants.SPACE, list);
						command = new StringBuffer(StringUtils.format(CONTAINER_UTILIZATION_MAPR, join, join));
						break;
					}
					case (ExtendedConstants.HORTONWORKS) : {
						List<String> list = new ArrayList<>();
						for (String container : containersList) {
							list.add(E + container.replace(CONTAINER, CONTAINER_E0_9) + DOUBLE_QUOTE);
						}
						join = StringUtils.join(Constants.SPACE, list);
						command = new StringBuffer(StringUtils.format(CONTAINER_UTILIZATION_HDP, join, join));
						break;
					}
					}
					
					hostName = new ArrayList<String>(1);
					hostName.add(node);
					lsBuilder = new CommandWritableBuilder(cluster, node);
					lsBuilder.addCommand(command.toString(),  true, hostName, CommandType.FS)
							.setMethodToBeInvoked(RemotingMethodConstants.PROCESS_AWK_COMMAND_TO_GET_MAX_USED_MEMORY);
					response = (String) remoter.fireCommandAndGetObjectResponse(lsBuilder.getCommandWritable());
					if (response == null || response.trim().isEmpty()) {
						continue;
					}
					splitResponse = response.split(SPACE);
					
					usedMemory = convertMemory(splitResponse[1], Double.parseDouble(splitResponse[0]));
					if (taskType.equals(Constants.MAP)) {
						if (usedMemory > effCapUtilStats.getUsedMaxMapMemory()){
							effCapUtilStats.setUsedMaxMapMemory(usedMemory);
							allocatedMemory = convertMemory(splitResponse[3], Double.parseDouble(splitResponse[2]));
							effCapUtilStats.setAllocatedMapMemory(allocatedMemory);
						} else {
							if (usedMemory > effCapUtilStats.getUsedMaxReduceMemory()) {
								effCapUtilStats.setUsedMaxReduceMemory(usedMemory);
								allocatedMemory = convertMemory(splitResponse[3], Double.parseDouble(splitResponse[2]));
								effCapUtilStats.setAllocatedReduceMemory(allocatedMemory);
							}
						}
					}
				}
			}
		}
		return containerValues;
	}

	/**
	 * Convert memory based on TB, GB, KB.
	 *
	 * @param memorySuffix the memory suffix
	 * @param memory the memory
	 * @return the long
	 */
	private Long convertMemory(String memorySuffix, Double memory){
		switch (memorySuffix) {
		case Constants.MB : 
			return memory.longValue();
		case Constants.GB :
			memory = memory * 1024;
			return memory.longValue();
		case Constants.TB :
			memory = memory * 1048576;
			return memory.longValue();
		case Constants.PB :
			memory = memory * 1073741824;
			return memory.longValue();
		case Constants.EB :
			memory = memory * 1024 * 1024 * 1024 * 1024;
			return memory.longValue();
		default : 
			return memory.longValue();

		}
	}

	/**
	 * Copy job history file to JumbuneHome cluster-profiling folder.
	 *
	 * @param cluster the cluster
	 * @param rmCommunicator 
	 */
	public void copyJobHistoryFile(Cluster cluster, RMCommunicator rmCommunicator) throws Exception {
		List<ApplicationReport> applicationReport = null;
		List<String> pendingJobHistoryList = new ArrayList<String>();
		
		applicationReport = rmCommunicator.getApplications();
		
		for(ApplicationReport appReport:applicationReport){
			if(appReport.getProgress() == 1.0f && appReport.getFinalApplicationStatus().equals(FinalApplicationStatus.SUCCEEDED)){
					String applicationId = appReport.getApplicationId().toString();
					String jobId = applicationId.replace("application", "job");
					StringBuffer containerList = new StringBuffer().append(PREFIX)
							.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append(SUFFIX);
					File containerDataFile = new File(containerList.toString());
					if(containerDataFile.exists()){
					StringBuffer allContainersResponseFileName = new StringBuffer().append(PREFIX)
							.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append(UTILIZATION_STATS).append(jobId).append(JSON);
					StringBuffer histFilePath = new StringBuffer().append(PREFIX)
							.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append(RACKWARE).append(jobId).append(JHIST);
					File allContainerResponseFile = new File(allContainersResponseFileName.toString());
					File histFile = new File(histFilePath.toString());
					if(allContainerResponseFile.exists() && !histFile.exists()){
						pendingJobHistoryList.add(jobId);
						if(pendingJobHistoryList.size()>5){
							break;
						}
					}
				}
			}
		}
		
		int noOfFiles = pendingJobHistoryList.size();
		int noOfThreads = 4;
	
		ExecutorService service = Executors.newFixedThreadPool(noOfThreads); 
		if(noOfFiles > 0){	
			LOGGER.debug("no of hist files - "+ noOfFiles +" no of threads launched - " + noOfThreads);
			service.submit(new CopyThread(pendingJobHistoryList, cluster)); 
		}
		service.shutdown();
		LOGGER.debug("finished copying history files...");		
	}
	
	/**
	 * Gets the processed container id.
	 *
	 * @param id the id
	 * @return the processed container id
	 */
	private String getProcessedContainerId(String id){
		StringBuilder builder = new StringBuilder("container");		
		
		Matcher matcher = pattern.matcher(id);
		
		if(matcher.matches()) {
			for(int i = 1; i <= matcher.groupCount(); i++){
				builder.append(Constants.UNDERSCORE + matcher.group(i));
			}
		}
		return builder.toString();

	}
	
	/**
	 * Correct container id in hist file for hortonworks.
	 *
	 * @param sourceHistFile the source hist file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void preProcessContainerId(String sourceHistFile) throws IOException{
		
		File oldFile = new File(sourceHistFile);
		String newJhist = oldFile.getParent()+File.separator+oldFile.getName()+"new";
		File destination = null;
		if (oldFile.exists()) {
			destination = new File(newJhist);
		}
		if(!destination.exists()){
		BufferedWriter bufferWriter = Files.newBufferedWriter(Paths.get(destination.getAbsolutePath()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		List<String> lines = Files.readAllLines(Paths.get(sourceHistFile), StandardCharsets.UTF_8);
	
		Pattern pattern = Pattern.compile(REGEX1);
		Matcher matcher = null;
		StringBuilder builder = null;		
		for(String line : lines) {
		    matcher = pattern.matcher(line);			
			if(matcher.matches()){
				builder = new StringBuilder();
				builder.append(matcher.group(1)).append(getProcessedContainerId(matcher.group(2))).append(matcher.group(3));
			} else {
				builder = new StringBuilder(line);
			}		
			bufferWriter.write(builder.toString());
			bufferWriter.newLine();
			bufferWriter.flush();
		}
       
		String oldHistoryFile = oldFile.getParent() + File.separator + oldFile.getName();
		Files.copy(Paths.get(newJhist), Paths.get(oldHistoryFile), StandardCopyOption.REPLACE_EXISTING);
		}
		
	}

	
	/**
	 * Gets the rack aware stats.
	 *
	 * @param cluster the cluster
	 * @param rmCommunicator 
	 * @return the rack aware
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public RackAwareStats getRackAwareStats(Cluster cluster, RMCommunicator rmCommunicator) throws Exception {
		RackAwareStats rackAwareStats = new RackAwareStats ();
		List<ApplicationReport> applicationReport = rmCommunicator.getApplications();
		Long dataLocalJob = (long) 0;  
		Long rackLocalJob = (long) 0;
		Long otherLocalJob = (long) 0;
		Long totalCounter = (long) 0;
		StringBuffer jsonFile = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
				.append(cluster.getClusterName()).append(RACK).append(JSON);
		File rackAwareFile = new File(jsonFile.toString());

		if(rackAwareFile.exists()){
			String rackAwareJsonReader = ConfigurationUtil.readFileData(jsonFile.toString());
			rackAwareStats = Constants.gson.fromJson(rackAwareJsonReader, RackAwareStats.class);
			dataLocalJob = rackAwareStats.getDataLocalJob();
			rackLocalJob = rackAwareStats.getRackLocalJob();
			otherLocalJob = rackAwareStats.getOtherLocalJob();
		}

		for(ApplicationReport appReport:applicationReport){
			String applicationId = appReport.getApplicationId().toString();
			String jobId=applicationId.replace(Constants.APPLICATION, Constants.JOB);
			if(appReport.getFinalApplicationStatus().equals(FinalApplicationStatus.SUCCEEDED)){
				StringBuffer histFilePath = new StringBuffer().append(JumbuneInfo.getHome()).append(ExtendedConstants.JOB_JARS_LOC)
						.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append(RACKWARE).append(jobId).append(JHIST);
				StringBuffer newHistFilePath = new StringBuffer().append(JumbuneInfo.getHome()).append(ExtendedConstants.JOB_JARS_LOC)
						.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append(RACKWARE).append(jobId).append(COPY).append(JHIST);
				File histFile = new File(histFilePath.toString());
				File copiedHistFile = new File(newHistFilePath.toString());

				if(histFile.exists()){	
					if(!copiedHistFile.exists()){		
						Files.copy(histFile.toPath(), copiedHistFile.toPath());
						if(Constants.HORTONWORKS.equals(FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION))){
							preProcessContainerId(newHistFilePath.toString());					
						}
						Path path = new Path(newHistFilePath.toString());
						//begin mapr code changes
						if(Constants.MAPR.equals(FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION))){
							UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
							UserGroupInformation.setLoginUser(realUser);
						}
						//end mapr code changes
						DecoratedJobHistoryParser decoratedJobHistoryParser = new DecoratedJobHistoryParser(FileSystem.getLocal(new Configuration()), path);
						JobInfo jobInfo = decoratedJobHistoryParser.parse();
						if(jobInfo.getTotalCounters().findCounter(JOB_COUNTER, OTHER_LOCAL_MAPS).getValue()> 0){
							otherLocalJob = otherLocalJob + jobInfo.getTotalCounters().findCounter(JOB_COUNTER, OTHER_LOCAL_MAPS).getValue();
						}
						if(jobInfo.getTotalCounters().findCounter(JOB_COUNTER, RACK_LOCAL_MAPS).getValue() > 0){
							rackLocalJob = rackLocalJob + jobInfo.getTotalCounters().findCounter(JOB_COUNTER, RACK_LOCAL_MAPS).getValue();
						}
						if(jobInfo.getTotalCounters().findCounter(JOB_COUNTER, DATA_LOCAL_MAPS).getValue() > 0){
							dataLocalJob = dataLocalJob + jobInfo.getTotalCounters().findCounter(JOB_COUNTER, DATA_LOCAL_MAPS).getValue();
						}
					}
				}
			}
		}

		rackAwareStats.setDataLocalJob(dataLocalJob);
		rackAwareStats.setRackLocalJob(rackLocalJob);
		rackAwareStats.setOtherLocalJob(otherLocalJob);

		File parentFile = new File(rackAwareFile.getParent());
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		String json = Constants.gson.toJson(rackAwareStats);
		ConfigurationUtil.writeToFile(jsonFile.toString(), json);
		totalCounter = (dataLocalJob+rackLocalJob+otherLocalJob);

		if(totalCounter > 0){
			rackAwareStats.setDataLocalJob((dataLocalJob*100)/totalCounter);
			rackAwareStats.setOtherLocalJob((otherLocalJob*100)/totalCounter);
			rackAwareStats.setRackLocalJob(100-rackAwareStats.getOtherLocalJob()-rackAwareStats.getDataLocalJob());
		}else{
			rackAwareStats.setDataLocalJob(0);
			rackAwareStats.setOtherLocalJob(0);
			rackAwareStats.setRackLocalJob(0);
		}

		return rackAwareStats;
	}

	/**
	 * Gets the job details as per JobId.
	 *
	 * @param cluster the cluster
	 * @param jobId the job id
	 * @return the job details
	 */
	public JobOutput getJobDetails(Cluster cluster, String jobId){
		StringBuffer sb = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
				.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append("/jobprofiling/").append(jobId).append(JHIST);
		String localPath = sb.toString();
		Method method = null;
		Class<?> yarnJobStatsUtility = null;
	
		File file = new File (sb.toString());
		if (!file.exists()){
		CommandWritableBuilder lsBuilder = new CommandWritableBuilder(cluster, null);
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String agentHome = RemotingUtil.getAgentHome(cluster);	
		String historyLocation = RemotingUtil.getHistoryDoneLocation(cluster);
		String receivePath = ExtendedConstants.JOB_JARS_LOC + cluster.getClusterName() + CLUSTER_PROFILING + jobId + File.separator;					
		
		StringBuffer remotePath = new StringBuffer().append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
				.append(cluster.getClusterName()).append(CLUSTER_PROFILING).append(jobId).append("/jobprofiling/");
		StringBuffer path = new StringBuffer().append(agentHome).append(remotePath);
		RemotingUtil.mkDir(lsBuilder, remoter, path.toString());
		
		
		StringBuffer command = new StringBuffer(Constants.HADOOP_ENV_VAR_NAME).append(HDFS_FILE_GET_COMMAND).append(SPACE).append(historyLocation).append(jobId).append("*[^.xml]")
				.append(" ").append(path).append(jobId).append(JHIST);
		LOGGER.debug("Command Executed : "+command.toString());	
		if(cluster.getHadoopUsers().isHasSingleUser()){
			lsBuilder.addCommand(command.toString(), false, null,CommandType.MAPRED);
		}else {
			lsBuilder.addCommand(command.toString(), false, null,CommandType.MAPRED).setMethodToBeInvoked(RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
		}
		remoter.fireAndForgetCommand(lsBuilder.getCommandWritable());
		lsBuilder.clear();

		remoter.receiveLogFiles(receivePath, remotePath.toString());	
		}
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		try {
			if(Constants.HORTONWORKS.equals(hadoopDistribution)){
				try {
					preProcessContainerId(sb.toString());
				} catch (IOException e) {
					LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
					}
				}
			if(Constants.MAPR.equals(hadoopDistribution) || Constants.EMRMAPR.equals(hadoopDistribution)){
				UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
				UserGroupInformation.setLoginUser(realUser);
				preProcessContainerId(sb.toString());
			}
			yarnJobStatsUtility = Class.forName(YARN_JOB_STATS_UTILITY_CLASS);
			method = yarnJobStatsUtility.getDeclaredMethod(YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD, String.class);
			JobOutput jobOutput = (JobOutput) method.invoke(yarnJobStatsUtility.newInstance(), localPath);
			return jobOutput;
		} catch (Exception e) {
			LOGGER.error("Unable to get job profiling for [" + jobId + "]" + "due to" , e);
		}
		return null;
	}

	/**
	 * Get the container status.
	 * @param rmCommunicator 
	 *
	 * @return the container status
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public LiveCapacityStats getContainerStatus(Cluster cluster, RMCommunicator rmCommunicator) throws  IOException {

		LiveCapacityStats liveStats = new LiveCapacityStats();
		try {
			Integer container = getMaxPossibleContainersAvailableInCluster(cluster, rmCommunicator);
			Integer minimumVcore = getMinimumParameterMandatoryForContainer(YARN_CONTAINER_MINIMUM_VCORE, 1, cluster);
			Integer minimumMem = getMinimumParameterMandatoryForContainer(YARN_CONTAINER_MINIMUM_MEMORY, 1024, cluster);
			StringBuffer sb= new StringBuffer().append("Approx. containers available of capacity ").append(minimumVcore.toString())
					.append(" vcore, ").append(minimumMem.toString()).append(" MB");
			liveStats.setCapacity(container.toString());
			liveStats.setMessage(sb.toString());
		} catch (YarnException e) {	
			LOGGER.error(JumbuneRuntimeException.throwYarnException(e.getStackTrace()));
		}

		return liveStats;
	}


	/**
	 * Get the max possible containers available in cluster.
	 * @param cluster 
	 * @param rmCommunicator 
	 *
	 * @return the max possible containers available in cluster
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private int getMaxPossibleContainersAvailableInCluster(Cluster cluster, RMCommunicator rmCommunicator) throws YarnException, IOException{
		int containersBasedOnVcores=0;
		int containersBasedOnMemory=0;
		int finalContainers=0;
		containersBasedOnVcores = getAvailableVCoresInCluster(rmCommunicator)/getMinimumParameterMandatoryForContainer(YARN_CONTAINER_MINIMUM_VCORE, 1, cluster);
		containersBasedOnMemory=getTotalMemoryAvailableInCluster(rmCommunicator)/getMinimumParameterMandatoryForContainer(YARN_CONTAINER_MINIMUM_MEMORY, 1024, cluster);
		finalContainers = Math.min(containersBasedOnVcores, containersBasedOnMemory);
		if(finalContainers<0){
			finalContainers =0;
		}
		return finalContainers;

	}

	/**
	 * Get the available v cores in cluster.
	 * @param rmCommunicator 
	 *
	 * @return the available v cores in cluster
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private int getAvailableVCoresInCluster(RMCommunicator rmCommunicator) throws IOException {
		List<NodeReport> nodeReports = null;
		try {
			nodeReports = rmCommunicator.getNodeReports();
		} catch (YarnException e) {
			LOGGER.error(JumbuneRuntimeException.throwYarnException(e.getStackTrace()));
		}
		Set<String> hostname = new HashSet<String>();
		
		int totalVCores = 0;
		int usedVCores = 0;
		for(NodeReport report: nodeReports){
			if(!hostname.contains(report.getHttpAddress())  && report.getNodeState().equals(NodeState.RUNNING)){
			hostname.add(report.getHttpAddress());
			totalVCores += report.getCapability().getVirtualCores();
			if(report.getUsed()!=null){
				usedVCores += report.getUsed().getVirtualCores();
				}
			}
		}
		int availableVCores = totalVCores - usedVCores;
		return availableVCores ;
	}

	/**
	 * Get the total memory available in cluster.
	 * @param rmCommunicator 
	 *
	 * @return the total memory available in cluster
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private int getTotalMemoryAvailableInCluster(RMCommunicator rmCommunicator) throws YarnException, IOException{
		List<NodeReport> reports=rmCommunicator.getNodeReports();
		int availableMemory=0;
		
		Set<String> hostname = new HashSet<String>();
		
		for (NodeReport nodeReport : reports) {
			if(!hostname.contains(nodeReport.getHttpAddress())  && nodeReport.getNodeState().equals(NodeState.RUNNING)){
			hostname.add(nodeReport.getHttpAddress());
			availableMemory+=nodeReport.getCapability().getMemory() - 
					(nodeReport.getUsed()==null?0:nodeReport.getUsed().getMemory());
			}
		}
		return availableMemory;
	}


	/**
	 * Get the minimum parameter mandatory for container.
	 *
	 * @param parameter the parameter
	 * @param value the value
	 * @return the minimum parameter mandatory for container
	 */
	private int getMinimumParameterMandatoryForContainer(String parameter , int value, Cluster cluster) {
		Configuration configuration = new Configuration();
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
		File file = new File(localConfFilePath);
		if(!file.exists()){
			String filePath = RemotingUtil.addHadoopResource(configuration, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
			configuration.addResource(new Path(filePath));
		}
		configuration.addResource(new Path(localConfFilePath));
		return configuration.getInt(parameter, value);
	}

	/**
	 * Gets the queue user stats.
	 *
	 * @param cluster the cluster
	 * @param rmCommunicator 
	 * @return the queue user stats
	 */
	public List<JobQueueBean> getQueueUserStats(Cluster cluster, RMCommunicator rmCommunicator) throws Exception {
		
		List<JobQueueBean> jobQueueBeans = new ArrayList<>();

		for (ApplicationReport reports : rmCommunicator.getRunningApplications()) {
			JobQueueBean jobQueueBean = new JobQueueBean();
			jobQueueBean.setJobId(rmCommunicator.getJobId(reports).toString());
			jobQueueBean.setQueueName(reports.getQueue());
			jobQueueBean.setUser(reports.getUser());
			jobQueueBean.setUsedCores(reports.getApplicationResourceUsageReport().getUsedResources().getVirtualCores());
			jobQueueBean.setUsedMemory(reports.getApplicationResourceUsageReport().getUsedResources().getMemory());
			jobQueueBean.setExecutionEngine(reports.getApplicationType());
			jobQueueBean.setJobName(reports.getName());
			jobQueueBeans.add(jobQueueBean);
		}
	
		return jobQueueBeans;
	}


}