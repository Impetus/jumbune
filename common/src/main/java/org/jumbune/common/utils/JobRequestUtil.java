package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jumbune.common.beans.cluster.EnterpriseCluster;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.common.job.EnterpriseJobConfig;

public class JobRequestUtil {

	private static final String DQT_SCHEDULED_JSON = "scheduledJson.json";

	private static final String TWO_ = "2_";

	private static final String CLUSTERS_DIR = "/clusters/";

	private static final String JUMBUNE_HOME = "JUMBUNE_HOME";
	private static final Logger LOGGER = LogManager.getLogger(JobRequestUtil.class);

	private static final String DQ_JOBS_DIR = "/ScheduledJobs/IncrementalDQJobs/";

	private static final String TUNING_SCHEDULED_DIR = "/scheduledJobs/userScheduled/";

	private static String JSON_REPO = "/jsonrepo/";

	private static String JOB_REQUEST_JSON = "/request.json";
	
	private static String ANALYZE_DATA = "analyzeData";
	
	private static String ANALYZE_JOB = "analyzeJob";
	
	private static String OPTIMIZE_JOB = "optimizeJob";

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
	 * 
	 * @param data
	 *            the data
	 * @return the Job config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FileUploadException
	 *             the file upload exception
	 */
	public static Config prepareJobConfig(String data) throws IOException {
		Gson gson = new Gson();
		return gson.fromJson(data, EnterpriseJobConfig.class);

	}

	public static JumbuneRequest addJobConfigWithCluster(String jobConfigJSON) throws IOException {
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) prepareJobConfig(
				jobConfigJSON);
		String clusterName = enterpriseJobConfig.getOperatingCluster();
		EnterpriseCluster cluster = getClusterByName(clusterName);
		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setCluster(cluster);
		jumbuneRequest.setConfig(enterpriseJobConfig);
		return jumbuneRequest;
	}

	public static EnterpriseClusterDefinition getClusterByName(String clusterName)
			throws IOException {
		File file = new File(System.getenv(JUMBUNE_HOME) + CLUSTERS_DIR + clusterName + ".json");
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
		Gson gson = new Gson();
		return gson.fromJson(json.toString(), EnterpriseClusterDefinition.class);
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
		return new File(System.getenv(JUMBUNE_HOME) + DQ_JOBS_DIR + jobName).exists();
	}

	public static boolean isJobTuningScheduledType(String jobName) {
		return new File(System.getenv(JUMBUNE_HOME) + TUNING_SCHEDULED_DIR + jobName).exists();
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

	/**
	 * This method is used to load json according to the file path.
	 *
	 * @param filePath
	 *            the file path
	 * @return the object
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	private static EnterpriseJobConfig loadJob(String jobName) throws JsonSyntaxException, IOException {
		return new Gson().fromJson(getJobJson(jobName), EnterpriseJobConfig.class);
	}
	
	public static String getJobJson(String jobName) throws IOException {
		String[] jobTypes = { ANALYZE_DATA, ANALYZE_JOB, OPTIMIZE_JOB };

		String jsonRepoPath = System.getenv(JUMBUNE_HOME) + JSON_REPO;
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
		File dir = new File(System.getenv(JUMBUNE_HOME) + DQ_JOBS_DIR);
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

	private static String getScheduledDQTJobTime(String jobName) throws FileNotFoundException {
		
		EnterpriseJobConfig config = loadJobForDQT(jobName);
		String time = config.getDataQualityTimeLineConfig().getTime();
		if (time == null) {
			time = config.getDataQualityTimeLineConfig().getCronExpression();
		}
		return time;
	}

	private static EnterpriseJobConfig loadJobForDQT(String jobName) throws FileNotFoundException {
		String jobConfigFilePath = System.getenv(JUMBUNE_HOME) + DQ_JOBS_DIR + jobName
				+ DQT_SCHEDULED_JSON;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		try {
			File file = new File(jobConfigFilePath);
			inputStream = new FileInputStream(file);
			Gson gson = new Gson();
			inputStreamReader = new InputStreamReader(inputStream);
			return gson.fromJson(inputStreamReader, EnterpriseJobConfig.class);
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException ioe) {
					LOGGER.error("Failed to close input stream of job config file");
				}
			}
		}
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
		File dir = new File(System.getenv(JUMBUNE_HOME) + DQ_JOBS_DIR + jobName);
		for (File file : dir.listFiles()) {
			if (file.getName().startsWith(TWO_)) {
				return JobStatus.COMPLETED;
			}
		}
		return JobStatus.SCHEDULED;
	}

}
