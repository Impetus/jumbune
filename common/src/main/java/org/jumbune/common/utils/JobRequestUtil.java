package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;

public class JobRequestUtil {

	private static final String TWO_ = "2_";

	private static final String CLUSTERS_DIR = "clusters/";

	private static final Logger LOGGER = LogManager.getLogger(JobRequestUtil.class);

	private static final String DQ_JOBS_DIR = "ScheduledJobs/IncrementalDQJobs/";

	private static final String TUNING_SCHEDULED_DIR = "scheduledJobs/userScheduled/";

	private static String JSON_REPO = "/jsonrepo/";

	private static String JOB_REQUEST_JSON = "/request.json";
	
	private static String ANALYZE_DATA = "analyzeData";
	
	private static String ANALYZE_JOB = "analyzeJob";

	private static Properties analyseClusterStats;

	static {
		loadAnalyseClusterStats();
	}

	private static void loadAnalyseClusterStats() {
		analyseClusterStats = new Properties();
		InputStream inputStream = null;
		File file = null;
		try {
			file = new File(System.getenv(Constants.JUMBUNE_ENV_VAR_NAME)
					+ Constants.ANALYSE_CLUSTER_PROPERTIES_FILE);
			inputStream = new FileInputStream(file);
			analyseClusterStats.load(inputStream);
		} catch (IOException e) {
			LOGGER.error("Unable to read File " + file.getAbsolutePath());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error("Unable to close File " + file.getAbsolutePath());
				}
			}
		}
	}

	public static String getClusterStatUnit(String statName) {
		return analyseClusterStats.getProperty(statName);
	}

	/**
	 * Prepare Job config.
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static Config prepareJobConfig(String data) throws IOException {
		return Constants.gson.fromJson(data, JobConfig.class);

	}

	public static JumbuneRequest addJobConfigWithCluster(String jobConfigJSON) throws IOException {
		JobConfig jobConfig = (JobConfig) prepareJobConfig(
				jobConfigJSON);
		String clusterName = jobConfig.getOperatingCluster();
		Cluster cluster = getClusterByName(clusterName);
		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setCluster(cluster);
		jumbuneRequest.setConfig(jobConfig);
		return jumbuneRequest;
	}

	public static ClusterDefinition getClusterByName(String clusterName)
			throws IOException {
		File file = new File(JumbuneInfo.getHome() + CLUSTERS_DIR + clusterName + ".json");
		if (!file.exists()) {
			throw new IOException("Cluster:" + clusterName + " not exists");
		}
		StringBuffer json = new StringBuffer();
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				json.append(line);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return Constants.gson.fromJson(json.toString(), ClusterDefinition.class);
	}

	public static List<String> getClusterNodes(Cluster cluster) {
		List<String> clusterNodes = new ArrayList<String>();
		clusterNodes.addAll(cluster.getWorkers().getHosts());

		List<String> nameNodes = cluster.getNameNodes().getHosts();
		clusterNodes.removeAll(nameNodes);
		clusterNodes.addAll(nameNodes);

		List<String> resourceManagerNodes = cluster.getTaskManagers().getHosts();
		clusterNodes.removeAll(resourceManagerNodes);
		clusterNodes.addAll(resourceManagerNodes);

		return clusterNodes;
	}

	public static Map<String, List<String>> getClusterNodesWithLabel(String clusterName)
			throws IOException {
		Cluster cluster = getClusterByName(clusterName);
		List<String> nodes = getClusterNodes(cluster);
		List<String> workers = cluster.getWorkers().getHosts();
		List<String> nameNodes = cluster.getNameNodes().getHosts();
		List<String> resourceManagers = cluster.getTaskManagers().getHosts();
		Map<String, List<String>> map = new HashMap<String, List<String>>(nodes.size());
		for (String node : nodes) {
			List<String> list = new ArrayList<String>(3);
			if (nameNodes.contains(node)) {
				list.add("NN");
			}
			if (resourceManagers.contains(node)) {
				list.add("RM");
			}
			if (workers.contains(node)) {
				list.add("DN");
			}
			map.put(node, list);
		}
		return map;
	}

	public static List<String> getClusterNodes(String clusterName) throws IOException {
		Cluster cluster = getClusterByName(clusterName);
		List<String> clusterNodes = new ArrayList<String>();
		clusterNodes.addAll(cluster.getWorkers().getHosts());

		List<String> nameNodes = cluster.getNameNodes().getHosts();
		clusterNodes.removeAll(nameNodes);
		clusterNodes.addAll(nameNodes);

		List<String> resourceManagerNodes = cluster.getTaskManagers().getHosts();
		clusterNodes.removeAll(resourceManagerNodes);
		clusterNodes.addAll(resourceManagerNodes);

		return clusterNodes;
	}

	public static boolean isJobDQTScheduledType(String jobName) {
		return new File(JumbuneInfo.getHome() + DQ_JOBS_DIR + jobName).exists();
	}

	public static boolean isJobTuningScheduledType(String jobName) {
		return new File(JumbuneInfo.getHome() + TUNING_SCHEDULED_DIR + jobName).exists();
	}

	public static JobStatus getScheduledTuningJobStatus(String jobName) throws IOException {
		String statusPath = ExtendedConfigurationUtil.getUserScheduleJobLocation() + File.separator
				+ jobName + ExtendedConstants.SCHEDULED_JOB_STATUS_FILE;
		File file = new File(statusPath);
		if (file.exists()) {
			return JobStatus.valueOf(FileUtils.readFileToString(file));
		} else {
			return null;
		}
	}
	
	public static String getJobJson(String jobName) throws IOException {
		String[] jobTypes = { ANALYZE_DATA, ANALYZE_JOB };

		String jsonRepoPath = JumbuneInfo.getHome() + JSON_REPO;
		String slashJsonName = File.separator + jobName + JOB_REQUEST_JSON;
		StringBuilder jobConfigFile = null;

		for (String jobType : jobTypes) {
			jobConfigFile = new StringBuilder(jsonRepoPath).append(jobType).append(slashJsonName);
			if (new File(jobConfigFile.toString()).exists()) {
				break;
			}
		}

		return FileUtil.readFileIntoString(jobConfigFile.toString());
	}
	
	public static List<Map<String, String>> getScheduledDQTJobsList()
			throws IOException, ParseException {
		File dir = new File(JumbuneInfo.getHome() + DQ_JOBS_DIR);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			List<Map<String, String>> list = new ArrayList<Map<String, String>>(files.length);
			Map<String, String> map;
			for (File jobDir : dir.listFiles()) {
				map = new HashMap<String, String>(3);
				map.put("jobName", jobDir.getName());
				
/*				JobStatus jobStatus = getScheduledDQTJobStatus(jobDir.getName());
				map.put("status", jobStatus.getStatus());
				
				if (jobStatus == JobStatus.SCHEDULED) {
					map.put("scheduledTime", getScheduledDQTJobTime(jobDir.getName()));
				}*/
				
				list.add(map);
			}
			return list;
		}
		return Collections.emptyList();
	}

	/**
	 * Return job status of dqt job. This method will return only two status
	 * completed and scheduled. If the 2nd iteration (After 1st time job when
	 * submitted) has not started, it will return scheduled otherwise will
	 * return completed
	 * 
	 * @param jobName
	 *            job name
	 * @return job status
	 */
	public static JobStatus getScheduledDQTJobStatus(String jobName) {
		File dir = new File(JumbuneInfo.getHome() + DQ_JOBS_DIR + jobName);
		for (File file : dir.listFiles()) {
			if (file.getName().startsWith(TWO_)) {
				return JobStatus.COMPLETED;
			}
		}
		return JobStatus.SCHEDULED;
	}
	
	/**
	 * Returns job status of job. It will not work in case of scheduling
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public static JobStatus getJobStatus(String jobName) throws IOException {
		String[] jobTypes = { Constants.ANALYZE_DATA, Constants.ANALYZE_JOB };
		String path;
		File statusFile;
		for (String jobType : jobTypes) {
			path = new StringBuilder(JumbuneInfo.getHome())
					.append("jsonrepo/").append(jobType).append(File.separator)
					.append(jobName).append(File.separator)
					.append(Constants.JOB_STATUS).toString() ;
			statusFile = new File(path);
			if (statusFile.exists()) {
				return JobStatus.valueOf(FileUtils.readFileToString(statusFile));
			}
		}
		return JobStatus.IN_PROGRESS;
	}
	
	public static void setJobStatus(JobConfig jobConfig, JobStatus jobStatus) {
		setJobStatus(jobConfig.getJumbuneJobName(), getJobType(jobConfig), jobStatus);
	}
	
	public static void setJobStatus(String jobName, String jobType, JobStatus jobStatus) {
		String parent = new StringBuilder(JumbuneInfo.getHome())
				.append("jsonrepo/").append(jobType).append(File.separator)
				.append(jobName).append(File.separator).toString();
		File parentFile = new File(parent);
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		try {
			PrintWriter out = new PrintWriter(parent + Constants.JOB_STATUS);
			out.print(jobStatus.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			// case already handled
		}
	}
	
	public static String getJobType(JobConfig jobConfig) {
		if (jobConfig.getEnableDataValidation() == Enable.TRUE
				|| jobConfig.getEnableDataQualityTimeline() == Enable.TRUE
				|| jobConfig.getEnableJsonDataValidation() == Enable.TRUE
				|| jobConfig.getEnableDataProfiling() == Enable.TRUE
				|| jobConfig.getEnableXmlDataValidation() == Enable.TRUE
				|| jobConfig.getIsDataSourceComparisonEnabled() == Enable.TRUE) {
			return Constants.ANALYZE_DATA;
		} else {
			return Constants.ANALYZE_JOB;
		} 
	}

}
