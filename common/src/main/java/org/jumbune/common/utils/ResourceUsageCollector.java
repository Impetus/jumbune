package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.CPU_DUMP_FILE;
import static org.jumbune.common.utils.Constants.MEM_DUMP_FILE;
import static org.jumbune.common.utils.Constants.SPACE;
import static org.jumbune.common.utils.Constants.UNDERSCORE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.IntervalStats;
import org.jumbune.common.beans.NodeSystemStats;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.beans.profiling.PhaseDetails;
import org.jumbune.common.beans.profiling.PhaseOutput;
import org.jumbune.common.beans.profiling.PhaseType;
import org.jumbune.common.beans.profiling.TaskOutputDetails;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodConstants;

/**
 * Utility class to fetch system resource usage during the period of Job run.
 */
public class ResourceUsageCollector {

	private static final int DEFAULT_INTERVAL = 1;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(ResourceUsageCollector.class);

	/** The Constant DELAY_INTERVAL. */
	private static final int DELAY_INTERVAL = 2;

	/** The Constant NUM_OF_INTERVALS. */
	private static final int NUM_OF_INTERVALS = 20;

	/** The Constant TOP_DUMP_FILE. */
	private static final String TOP_DUMP_FILE = "top.txt";

	/** The Constant TOP_ID. */
	private static final String TOP_ID = "JUMBUNE_TOP";


	/**
	 * The Constant TOP_CMD.
     */ 
	private static final String TOP_CMD  = "bash -c \"exec -a "+ "JUMBUNE_TOP  top -b -d 2"
			+ " | stdbuf -oL awk '/Cpu|Mem:/{print \\$1\\\" \\\"\\$2\\\" \\\"\\$3\\\" \\\"\\$4\\\" \\\"\\$5}'\"";
	
	/** The Constant KILL_TOP. */
	private static final String KILL_TOP = "pkill -f ";
		
	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";

	/** The Constant GREP_CPU_CMD. */
	private static final String GREP_CPU_CMD = "|awk '/Cpu/{print$2}'";

	/** The Constant GREP_MEM_CMD. */
	private static final String GREP_MEM_CMD = " |awk '/Mem/{print $2\" \"$3\" \"$4\" \"$5}'";

	/** The Constant REDIRECT_SYMBOL. */
	private static final String REDIRECT_SYMBOL = ">";


	/** The Constant SYSTEM_STATS_DIR. */
	private static final String SYSTEM_STATS_DIR = "SystemStats";

	/** The Constant DEFAULT_RACK_SUFFIX. */
	private static final String DEFAULT_RACK_SUFFIX = "/default-rack/";

	/** The interval period. */
	private long intervalPeriod = Constants.FOUR;

    private static final String PATTERN_FOR_NON_DIGITS = "[^\\d]+";
	
	private JumbuneRequest jumbuneRequest;

	/** The Constant LOGS **/
	private static final String LOGS = "/logs/";

	/** The Constant HISTORY_DIR_SUFFIX. */
	private static final String HISTORY_DIR_SUFFIX = "/history/done/version-1";

	private static final String LOCAL_HOST = "localhost";

	/**
	 * Instantiates a new resource usage collector.
	 *
	 * @param loader
	 *            the loader
	 */
	public ResourceUsageCollector(JumbuneRequest jumbuneRequest) {
		this.jumbuneRequest = jumbuneRequest;
	}

	/**
	 * Fires top command to be dumped to a file on all hosts.
	 *
	 * @param receiveDirectory
	 *            the receive directory
	 */
	public void fireTopCmdOnSlaves(String receiveDirectory) { 
		Cluster cluster = jumbuneRequest.getCluster();
		String slaveTmpDir = cluster.getWorkers().getWorkDirectory();
		List<String> params = new ArrayList<String>();
		params.add(slaveTmpDir);

		StringBuilder command = new StringBuilder();
		command.append(TOP_CMD).append(REDIRECT_SYMBOL).append(slaveTmpDir)
				.append(File.separator).append(TOP_DUMP_FILE).append(" &");
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		CommandWritableBuilder builder;
		for (String workerHost: cluster.getWorkers().getHosts()) {
			builder = new CommandWritableBuilder(cluster, workerHost);
			builder.addCommand(command.toString(), true, params,
						CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			builder.clear();
			LOGGER.info("SUCCESSFULLY fired top on: "+workerHost);	
		}
		remoter.close();
		LOGGER.debug("Executed command [Top] on worker nodes ["
				+ command.toString() + "]");
	}

	/**
	 * Processes the top result dump file to extract CPU and memory usage to
	 * separate files on given nodes.
	 *
	 * @param receiveDirectory
	 *            the receive directory
	 * @param hosts
	 *            the hosts
	 */
	public void processTopDumpFile(String receiveDirectory, List<String> hosts) {
		Cluster cluster = jumbuneRequest.getCluster();
		String slaveTmpDir = cluster.getWorkers().getWorkDirectory();
		String topDumpFile = slaveTmpDir + File.separator + TOP_DUMP_FILE;
		String cpuDumpFile = slaveTmpDir + File.separator + CPU_DUMP_FILE;
		String memDumpFile = slaveTmpDir + File.separator + MEM_DUMP_FILE;
		List<String> params = new ArrayList<String>();
		params.add(slaveTmpDir);

		StringBuilder cpuCommand = new StringBuilder();
		cpuCommand.append(CAT_CMD).append(SPACE).append(topDumpFile)
				.append(GREP_CPU_CMD).append(REDIRECT_SYMBOL)
				.append(cpuDumpFile);

		StringBuilder memCommand = new StringBuilder();
		memCommand.append(CAT_CMD).append(SPACE).append(topDumpFile)
				.append(GREP_MEM_CMD).append(REDIRECT_SYMBOL)
				.append(memDumpFile);

		StringBuilder finalCpuCommand = null;
		StringBuilder finalMemCommand = null;
		
		Remoter remoter = RemotingUtil.getRemoter(cluster, receiveDirectory);
		CommandWritableBuilder builder;
		for (String workerHost: cluster.getWorkers().getHosts()) {
			builder = new CommandWritableBuilder(cluster, workerHost);
			finalCpuCommand = new StringBuilder(cpuCommand.toString()).append(UNDERSCORE).append(workerHost);
			finalMemCommand = new StringBuilder(memCommand.toString()).append(UNDERSCORE).append(workerHost);
			builder.addCommand(finalCpuCommand.toString(), true, params,
					CommandType.FS)
					.addCommand(finalMemCommand.toString(), true, params,
							CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			builder.clear();
		}
		remoter.close();
		LOGGER.debug("Command (analyzing top dumps) [" + cpuCommand.toString()
				+ "]");
	}

	/**
	 * Kills the proces on each node which dumps top result to a file.
	 *
	 * @param receiveDirectory
	 *            the receive directory
	 */
	public void shutTopCmdOnSlaves(String receiveDirectory) {
		Cluster cluster = jumbuneRequest.getCluster();
		StringBuilder command = new StringBuilder();
		command.append(KILL_TOP).append(TOP_ID);
		Remoter remoter = RemotingUtil.getRemoter(cluster, receiveDirectory);
		CommandWritableBuilder builder;
		for (String workerHost: cluster.getWorkers().getHosts()) {
			builder = new CommandWritableBuilder(cluster, workerHost);
			builder.addCommand(command.toString(), false, null,
					CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			builder.clear();
		}
		LOGGER.debug("Executed command [ShutTop] on worker nodes ["
				+ command.toString() + "]");
		remoter.close();
	}

	/**
	 * Adds CPU and memory usage to Job details.
	 *
	 * @param jobOutput
	 *            the job output
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void addPhaseResourceUsage(JobOutput jobOutput)
			throws IOException {
		long totalTime = jobOutput.getTotalTime();
		intervalPeriod = totalTime < NUM_OF_INTERVALS ? DEFAULT_INTERVAL
				: totalTime / NUM_OF_INTERVALS;
		Map<String, NodeSystemStats> nodeStats = getNodeStats();
		Map<Long, IntervalStats> statsMap = new HashMap<Long, IntervalStats>();
		PhaseOutput po = jobOutput.getPhaseOutput();

		// resource usage for setup phase
		PhaseDetails setupDetails = po.getSetupDetails();
		setPhaseResourceUsage(setupDetails.getTaskOutputDetails(),
				statsMap, nodeStats, PhaseType.SETUP);

		// resource usage for map phase
		PhaseDetails mapDetails = po.getMapDetails();
		setPhaseResourceUsage(mapDetails.getTaskOutputDetails(),
				statsMap, nodeStats, PhaseType.MAP);

		// resource usage for reduce phase
		PhaseDetails reduceDetails = po.getReduceDetails();
		setPhaseResourceUsage(reduceDetails.getTaskOutputDetails(),
				statsMap, nodeStats, PhaseType.REDUCE);

		// resource usage for cleanup phase
		PhaseDetails cleanupDetails = po.getCleanupDetails();
		setPhaseResourceUsage(cleanupDetails.getTaskOutputDetails(),
				statsMap, nodeStats, PhaseType.CLEANUP);
		jobOutput.setNodeStats(nodeStats);
		statsMap = new TreeMap<Long, IntervalStats>(statsMap);
		Map<Long, Float> avgCpuUsage = new LinkedHashMap<Long, Float>();
		Map<Long, Float> avgMemUsage = new LinkedHashMap<Long, Float>();
		IntervalStats is;
		for (Map.Entry<Long, IntervalStats> stats : statsMap.entrySet()) {
			is = stats.getValue();
			avgCpuUsage.put(stats.getKey(), getAvgValue(is.getCpuStats()));
			avgMemUsage.put(stats.getKey(), getAvgValue(is.getMemStats()));
		}
		jobOutput.setCpuUsage(avgCpuUsage);
		jobOutput.setMemUsage(avgMemUsage);
	}

	public void addPhaseResourceUsageForHistoricalJob(JobOutput jobOutput,
			String jobID) throws IOException {
		PhaseOutput po = jobOutput.getPhaseOutput();
		Map<Long, IntervalStats> statsMap = new HashMap<Long, IntervalStats>();
		Map<Long, Float> avgMemUsage = new LinkedHashMap<Long, Float>();
		Map<Long, Float> avgCpuUsage = new LinkedHashMap<Long, Float>();

		// resource usage for setup phase
		PhaseDetails setupDetails = po.getSetupDetails();
		setMemPhaseResourceUsage(setupDetails.getTaskOutputDetails(), statsMap,
				jobOutput, jobID, PhaseType.SETUP);

		// resource usage for map phase
		PhaseDetails mapDetails = po.getMapDetails();
		setMemPhaseResourceUsage(mapDetails.getTaskOutputDetails(), statsMap,
				jobOutput, jobID, PhaseType.MAP);

		// resource usage for reduce phase
		PhaseDetails reduceDetails = po.getReduceDetails();
		setMemPhaseResourceUsage(reduceDetails.getTaskOutputDetails(),
				statsMap, jobOutput, jobID, PhaseType.REDUCE);

		// resource usage for cleanup phase
		PhaseDetails cleanupDetails = po.getCleanupDetails();
		setMemPhaseResourceUsage(cleanupDetails.getTaskOutputDetails(),
				statsMap, jobOutput, jobID, PhaseType.CLEANUP);

		IntervalStats is;
		for (Map.Entry<Long, IntervalStats> stats : statsMap.entrySet()) {
			is = stats.getValue();
			avgCpuUsage.put(stats.getKey(), getAvgValue(is.getCpuStats()));
			avgMemUsage.put(stats.getKey(), getAvgValue(is.getMemStats()));
		}
		jobOutput.setMemUsage(avgMemUsage);
		jobOutput.setCpuUsage(avgCpuUsage);
	}

	/**
	 * Gets the list of nodes on which task attempts ran for the Job.
	 *
	 * @param jobOutput
	 *            the job output
	 * @return the nodes for job
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	public List<String> getNodesForJob(JobOutput jobOutput)
			throws UnknownHostException {
		List<String> hosts = new ArrayList<String>();
		PhaseOutput po = jobOutput.getPhaseOutput();
		PhaseDetails setupDetails = po.getSetupDetails();
		// nodes on which setup tasks ran
		WeakHashMap<String, String> hostConversions = new WeakHashMap<String, String>();

		List<TaskOutputDetails> setupTaskDetails = setupDetails
				.getTaskOutputDetails();
		for (TaskOutputDetails tod : setupTaskDetails) {
			String location = tod.getLocation();
			if (location != null && !hostConversions.containsKey(location)) {
				String host = convertHostNameToIP(location);
				hosts.add(host);
				hostConversions.put(location, host);
			}
		}
		// nodes on which map tasks ran
		PhaseDetails mapDetails = po.getMapDetails();
		List<TaskOutputDetails> mapTaskDetails = mapDetails
				.getTaskOutputDetails();
		for (TaskOutputDetails tod : mapTaskDetails) {
			String location = tod.getLocation();
			if (location != null && !hostConversions.containsKey(location)) {
				String host = convertHostNameToIP(location);
				hosts.add(host);
				hostConversions.put(location, host);
			}
		}
		// nodes on which reduce tasks ran
		PhaseDetails reduceDetails = po.getReduceDetails();
		List<TaskOutputDetails> reduceTaskDetails = reduceDetails
				.getTaskOutputDetails();
		for (TaskOutputDetails tod : reduceTaskDetails) {
			String location = tod.getLocation();
			if (location != null && !hostConversions.containsKey(location)) {
				String host = convertHostNameToIP(location);
				hosts.add(host);
				hostConversions.put(location, host);
			}
		}
		// nodes on which cleanup tasks ran
		PhaseDetails cleanupDetails = po.getCleanupDetails();
		List<TaskOutputDetails> cleanupTaskDetails = cleanupDetails
				.getTaskOutputDetails();
		for (TaskOutputDetails tod : cleanupTaskDetails) {
			String location = tod.getLocation();
			if (location != null && !hostConversions.containsKey(location)) {
				String host = convertHostNameToIP(location);
				hosts.add(host);
				hostConversions.put(location, host);
			}
		}
		hostConversions.clear();
		return hosts;
	}

	/**
	 * Gets Node-wise CPU and memory stats.
	 *
	 * @return the node stats
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Map<String, NodeSystemStats> getNodeStats() throws IOException {
		String systemStatsFile = JobConfig.getJumbuneHome() + File.separator
				+ SYSTEM_STATS_DIR;
		File dir = new File(systemStatsFile);
		File[] files = dir.listFiles();
		String fileName;
		String fileType;
		String hostName;
		Map<String, NodeSystemStats> nodeMap = new HashMap<String, NodeSystemStats>();
		NodeSystemStats nodeStats;
		BufferedReader br;

		String[] fileNameArr;
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			fileName = file.getName();
			fileNameArr = fileName.split(UNDERSCORE);
			fileType = fileNameArr[0];
			hostName = fileNameArr[1];
			if (nodeMap.containsKey(hostName)) {
				nodeStats = nodeMap.get(hostName);
			} else {
				nodeStats = new NodeSystemStats();
			}
			nodeStats.setHostName(hostName);
			br = new BufferedReader(new FileReader(file));
			setCpuAndMemoryStats(fileType, nodeStats, br);
			nodeMap.put(hostName, nodeStats);
		}
		return nodeMap;
	}

	/**
	 * Sets the cpu and memory stats.
	 *
	 * @param fileType
	 *            the file type
	 * @param nodeStats
	 *            Contains cpu and memory usage of the node during the job run
	 * @param br
	 *            the br
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void setCpuAndMemoryStats(String fileType,
			NodeSystemStats nodeStats, BufferedReader br) throws IOException {
		Map<Long, Float> cpuUsage = null;
		Map<Long, Float> memUsage = null;
		String line;
		if (CPU_DUMP_FILE.equals(fileType)) {
			cpuUsage = new HashMap<Long, Float>();
			long interval = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(line.contains("%us")) {
					line = line.substring(0,line.indexOf("%"));
				} else if (line.contains("%sy") || ! NumberUtils.isNumber(line)) {
					continue;
				}
				interval += DELAY_INTERVAL;
				try {
					cpuUsage.put(interval, Float.parseFloat(line));
				} catch (NumberFormatException e) {
					LOGGER.error("Error parsing a record of CPU dump file", e);
				}
			}
			nodeStats.setCpuUsage(cpuUsage);
			nodeStats.setAvgCpu(getAvgValue(cpuUsage.values()));
		} else {
			memUsage = new HashMap<Long, Float>();
			long interval = 0;
			float memPer;
			String usedMem;
			String totalMem;
			while ((line = br.readLine()) != null) {
				interval += DELAY_INTERVAL;
				line = line.trim();
				try {
				String memCpuClubbedStat = line.startsWith("Mem") ? line
						.split("Mem:")[1].trim() : line;
									   					
				String[] memCpuClubbedArray = memCpuClubbedStat.split(PATTERN_FOR_NON_DIGITS);				
				totalMem =  memCpuClubbedArray[0].trim();
				usedMem =  memCpuClubbedArray[1].trim();			

					memPer = Float.parseFloat(usedMem)
							/ Float.parseFloat(totalMem) * Constants.HUNDRED;
					memUsage.put(interval, memPer);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					LOGGER.error("Error parsing a record of MEMORY dump file", e);
				}
			}
           if(!memUsage.isEmpty()){ 
			nodeStats.setMemUsage(memUsage);
			nodeStats.setMaxMem(Collections.max(memUsage.values()));
           }
		}
	}

	@SuppressWarnings("deprecation")
	private void setMemPhaseResourceUsage(List<TaskOutputDetails> taskDetails,
			Map<Long, IntervalStats> statsMap, JobOutput jobOutput,
			String jobID, PhaseType phase) throws UnknownHostException {
		IntervalStats intervalStats;
		List<Float> cpuPer;
		List<Float> memPer;
		float maxPhaseMem = 0;
		int interval = 0;
		int minStartPoint = -1, maxEndPoint = 0;
		
		Cluster cluster = jumbuneRequest.getCluster();
		
		Remoter remoter = RemotingUtil.getRemoter(cluster, null);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, cluster)
				+ File.separator;
		String logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX;
		String command = jobID + RemotingConstants.SINGLE_SPACE + logsHistory;

		builder.addCommand(command, false, null, CommandType.FS)
				.setMethodToBeInvoked(
						RemotingMethodConstants.GET_HADOOP_CONFIG_FILE_FROM_JOBID);
		String configFilePath = (String) remoter
				.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		String fileName = configFilePath.substring(configFilePath
				.lastIndexOf(File.separator) + 1);
		configFilePath = configFilePath.substring(0,
				configFilePath.lastIndexOf(File.separator) + 1);
		String configurationFilePath = RemotingUtil
				.copyAndGetConfigurationFilePath(jumbuneRequest, configFilePath,
						fileName)
				+ File.separator + fileName;
		for (TaskOutputDetails tod : taskDetails) {
			float mem = tod.getResourceUsageMetrics().getPhysicalMemoryUsage();
			minStartPoint = setMinStartPoint(minStartPoint, tod.getStartPoint());
			long start = minStartPoint;
			maxEndPoint = setMaxEndPoint(maxEndPoint, tod.getEndPoint());
			long startPt = Math.max(start, DELAY_INTERVAL);
			float memoryStats = 0.0f;

			String jvmChildOpts = null;
			if (PhaseType.REDUCE.equals(phase)) {
				jvmChildOpts = RemotingUtil.parseConfiguration(
						configurationFilePath, "mapred.reduce.child.java.opts");
				if (jvmChildOpts == null) {
					jvmChildOpts = RemotingUtil.parseConfiguration(
							configurationFilePath, "mapred.child.java.opts");
				}
			} else {
				jvmChildOpts = RemotingUtil.parseConfiguration(
						configurationFilePath, "mapred.map.child.java.opts");
				if (jvmChildOpts == null) {
					jvmChildOpts = RemotingUtil.parseConfiguration(
							configurationFilePath, "mapred.child.java.opts");
				}
			}
			if (jvmChildOpts != null) {
				int childOptsVal = ConfigurationUtil
						.getJavaOptsinMB(jvmChildOpts);
				memoryStats = ((mem / (childOptsVal * 1024 * 1024)) * 100);

				int avg = (int) (tod.getEndPoint() - startPt) / 2;
				interval = (int) (avg + startPt);

				maxPhaseMem = setMaxPhaseMemory(memoryStats, maxPhaseMem);
			}
		}
		if (statsMap.containsKey(interval)) {
			intervalStats = statsMap.get(interval);
			cpuPer = intervalStats.getCpuStats();
			memPer = intervalStats.getMemStats();
		} else {
			intervalStats = new IntervalStats();
			cpuPer = new ArrayList<Float>();
			memPer = new ArrayList<Float>();
		}
		setIntervalStats(statsMap, 0.0f, maxPhaseMem, intervalStats, cpuPer,
				memPer, interval);
	}

	private int setMaxEndPoint(int maxEndPoint, long endPoint) {
		int maxEnd = maxEndPoint;
		if (endPoint > maxEnd) {
			maxEnd = (int) endPoint;
		}
		return maxEnd;
	}

	private int setMinStartPoint(int minStartPoint, long startPoint) {
		int minStart = minStartPoint;
		if (minStart == -1 || startPoint < minStart) {
			minStart = (int) startPoint;
		}
		return minStart;
	}

	/**
	 * Maps job timings to phase resource usage timings.
	 *
	 * @param taskDetails
	 *            the task details
	 * @param statsMap
	 *            the stats map
	 * @param nodeStats
	 *            the node stats
	 * @param phase
	 *            the phase
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	private void setPhaseResourceUsage(List<TaskOutputDetails> taskDetails,
			Map<Long, IntervalStats> statsMap, Map<String, NodeSystemStats> nodeStats,
			PhaseType phase) throws UnknownHostException {
		float cpu = 0.0f;
		float mem = 0.0f;
		IntervalStats intervalStats;
		List<Float> cpuPer;
		List<Float> memPer;
		float maxPhaseMem = 0;
		float totalPhaseCpu = 0;
		int totalPhaseIntervals = 0;
		List<String> allHosts = jumbuneRequest.getCluster().getWorkers().getHosts();
		
		for (TaskOutputDetails tod : taskDetails) {
			String todLocation = tod.getLocation();
			if (todLocation != null) {
				String location = null;
				location = convertHostNameToIP(todLocation);
				if (!(allHosts.contains(location))) {
					location = convertHostNameToIP(LOCAL_HOST);
				}
				NodeSystemStats nss = nodeStats.get(location);
				if(nss==null){
					nss = nodeStats.get(todLocation);
				}
				if (PhaseType.MAP == phase) {
					maxPhaseMem = nss.getMapPhaseMaxMem();
					totalPhaseCpu = nss.getTotalMapPhaseCpu();
					totalPhaseIntervals = nss.getTotalMapIntervals();
				} else if (PhaseType.REDUCE == phase) {
					maxPhaseMem = nss.getReducePhaseMaxMem();
					totalPhaseCpu = nss.getTotalReducePhaseCpu();
					totalPhaseIntervals = nss.getTotalReduceIntervals();
				}
				Map<Long, Float> cpuUsage = nss.getCpuUsage();
				Map<Long, Float> memUsage = nss.getMemUsage();
				long start = tod.getStartPoint();
				long end = tod.getEndPoint();
				long maxPt = getMaxResourceInterval(cpuUsage.keySet());
				long endPt = Math.min(end, maxPt);
				long startPt = Math.max(start, DELAY_INTERVAL);

				for (long i = startPt; i <= endPt; i++) {
					if (i % DELAY_INTERVAL == 0) {
						cpu = cpuUsage.get(i);
						mem = memUsage.get(i);
						maxPhaseMem = setMaxPhaseMemory(mem, maxPhaseMem);
						totalPhaseCpu += cpu;
						totalPhaseIntervals++;
					}

					if (intervalPeriod != 0 && i % intervalPeriod == 0) {
						if (statsMap.containsKey(i)) {
							intervalStats = statsMap.get(i);
							cpuPer = intervalStats.getCpuStats();
							memPer = intervalStats.getMemStats();
						} else {
							intervalStats = new IntervalStats();
							cpuPer = new ArrayList<Float>();
							memPer = new ArrayList<Float>();
						}
						if (i % DELAY_INTERVAL != 0) {
							cpu = (cpuUsage.get(i - 1) + cpuUsage.get(i + 1)) / 2;
							mem = (memUsage.get(i - 1) + memUsage.get(i + 1)) / 2;
						}
						setIntervalStats(statsMap, cpu, mem, intervalStats,
								cpuPer, memPer, i);
					}
				}
				setMapReducePhaseNodeSystemStats(phase, nss, maxPhaseMem,
						totalPhaseCpu, totalPhaseIntervals);
			} else {
				LOGGER.warn("Found null location for task ["
						+ tod.getTaskType()
						+ "], skipping populating PhaseNodeSystemStats for the task");
			}
		}
	}

	/**
	 * Sets the interval stats into stats Map
	 *
	 * @param statsMap
	 *            the stats map
	 * @param cpu
	 *            the cpu
	 * @param mem
	 *            the mem
	 * @param intervalStats
	 *            the interval stats
	 * @param cpuPer
	 *            the cpu per
	 * @param memPer
	 *            the mem per
	 * @param i
	 *            the i
	 */
	private void setIntervalStats(Map<Long, IntervalStats> statsMap, float cpu,
			float mem, IntervalStats intervalStats, List<Float> cpuPer,
			List<Float> memPer, long i) {
		cpuPer.add(cpu);
		memPer.add(mem);
		intervalStats.setCpuStats(cpuPer);
		intervalStats.setMemStats(memPer);
		statsMap.put(i, intervalStats);
	}

	/**
	 * Sets the max phase memory.
	 *
	 * @param mem
	 *            the mem
	 * @param maxPhaseMem
	 *            the max phase mem
	 * @return the float
	 */
	private float setMaxPhaseMemory(float mem, final float maxPhaseMem) {
		float maximumPhaseMemory = maxPhaseMem;
		if (mem > maximumPhaseMemory) {
			maximumPhaseMemory = mem;
		}
		return maximumPhaseMemory;
	}

	/**
	 * Sets the map reduce phase node system stats.
	 *
	 * @param phase
	 *            denotes whether the phase is map or reduce
	 * @param nss
	 *            contains cpu and memory usage of the node during the job run
	 * @param maxPhaseMem
	 *            denotes the maximum phase memory during map or reduce phase
	 * @param totalPhaseCpu
	 *            denotes the total cpu for map or reduce phase
	 * @param totalPhaseIntervals
	 *            denotes the total phase interval for map or reduce phase
	 */
	private void setMapReducePhaseNodeSystemStats(PhaseType phase,
			NodeSystemStats nss, float maxPhaseMem, float totalPhaseCpu,
			int totalPhaseIntervals) {
		if (PhaseType.MAP == phase) {
			nss.setMapPhaseMaxMem(maxPhaseMem);
			nss.setTotalMapPhaseCpu(totalPhaseCpu);
			nss.setTotalMapIntervals(totalPhaseIntervals);

		} else if (PhaseType.REDUCE == phase) {
			nss.setReducePhaseMaxMem(maxPhaseMem);
			nss.setTotalReducePhaseCpu(totalPhaseCpu);
			nss.setTotalReduceIntervals(totalPhaseIntervals);
		}
	}

	/**
	 * Gets the average value for a list.
	 *
	 * @param collection
	 *            the collection
	 * @return the avg value
	 */
	private float getAvgValue(Collection<Float> collection) {
		if (collection == null || collection.isEmpty()) {
			return 0.0f;
		}
		float sum = 0.0f;
		for (float val : collection) {
			sum += val;
		}
		return sum / collection.size();
	}

	/**
	 * Replaces defualt rack suffix and converts host name to node IP.
	 *
	 * @param hostName
	 *            the host name
	 * @return the string
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	private String convertHostNameToIP(final String hostName)
			throws UnknownHostException {
		String hostNameTemp = hostName;
		hostNameTemp = hostNameTemp.replace(DEFAULT_RACK_SUFFIX, "");
		return RemotingUtil.getIPfromHostName(jumbuneRequest.getCluster(), hostNameTemp);
	}

	/**
	 * Finds the maximum known interval for resource stats.
	 *
	 * @param keySet
	 *            the key set
	 * @return the max resource interval
	 */
	private long getMaxResourceInterval(Set<Long> keySet) {
		Long maxInterval = null;

		for (Long interval : keySet) {
			if (maxInterval == null || interval > maxInterval) {
				maxInterval = interval;
			}
		}
		return maxInterval;

	}



}