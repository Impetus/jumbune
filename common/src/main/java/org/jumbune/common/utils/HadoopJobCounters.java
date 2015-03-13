package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jumbune.common.beans.JobCounterBean;
import org.jumbune.common.beans.Master;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.client.Remoter;

/**
 * The Class HadoopJobCounters. This class populates the job counters according
 * to the logs of the jobs launched.
 */
public class HadoopJobCounters {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(HadoopJobCounters.class);

	/** The Constant ERRORANDEXCEPTION. */
	private static final String ERRORANDEXCEPTION = "ErrorAndException";

	/** The Constant MAPRED_JOBCLIENT. */
	private static final String MAPRED_JOBCLIENT = "mapred.JobClient:";

	/** The Constant COUNTERS. */
	private static final String COUNTERS = "Counters:";

	/** The Constant RUNNING_JOB. */
	private static final String RUNNING_JOB = "Running job: ";

	/** The Constant HADOOP_TYPE. */
	private static final String HADOOP_TYPE = "hadoopType";

	/** The job counter beans. */
	private static List<JobCounterBean> jobCounterBeans = new ArrayList<JobCounterBean>();

	/**
	 * Fetch cluster configuration properties.
	 *
	 * @param loader
	 *            the loader
	 * @return the string
	 */
	private static String fetchClusterConfigurationProperties(Loader loader) {
		YamlLoader yamlLoader = (YamlLoader) loader;
		String expectedLocation = new StringBuilder()
				.append(YamlLoader.getjHome()).append(File.separator)
				.append(Constants.JOB_JARS_LOC)
				.append(yamlLoader.getJumbuneJobName())
				.append("cluster-configuration.properties").toString();
		File file = new File(expectedLocation);
		if (!file.exists() || file.isDirectory()) {
			Master master = yamlLoader.getMasterInfo();
			Remoter remoter = new Remoter(master.getHost(),
					Integer.valueOf(master.getAgentPort()));
			String relativePath = File.separator + Constants.JOB_JARS_LOC
					+ yamlLoader.getJumbuneJobName();
			remoter.receiveLogFiles(relativePath,
					"cluster-configuration.properties");

		}
		return expectedLocation;
	}

	/**
	 * Gets the job counter beans.
	 *
	 * @return the job counter beans
	 */
	public static List<JobCounterBean> getJobCounterBeans() {
		return jobCounterBeans;
	}

	/**
	 * Sets the job counter map.
	 *
	 * @param processName
	 *            the process name
	 * @param response
	 *            the response
	 * @param loader
	 *            the loader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void setJobCounterBeans(String processName, String response,
			Loader loader) throws IOException {
		YamlLoader yamlLoader = (YamlLoader) loader;
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		BufferedReader reader = new BufferedReader(new StringReader(response));
		String line = null;
		String jobName = null;
		boolean isYarnJob = false;
		JobCounterBean jobCounterBean = new JobCounterBean();
		isYarnJob = FileUtil.getPropertyFromFile(
				fetchClusterConfigurationProperties(loader), HADOOP_TYPE)
				.equalsIgnoreCase("yarn");
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			map = new HashMap<String, String>();
			if (line.contains(RUNNING_JOB)) {
				jobName = line.split(RUNNING_JOB)[1];
				jobs.add(jobName);
			} else if (line.contains(COUNTERS)) {
				int count = new Integer(line.split(COUNTERS)[1].trim());
				while (count > 0) {
					line = reader.readLine();
					if (line == null) {
						break;
					}
					if (line.contains("=")) {
						String[] counterDetail = null;

						if (isYarnJob) {
							counterDetail = line.split("=");
						} else {
							counterDetail = line.split(MAPRED_JOBCLIENT)[1]
									.split("=");
						}

						map.put(counterDetail[0].trim(),
								counterDetail[1].trim());
						count--;
					}
				}
				jobCounterBean.setProcessName(processName);
				jobCounterBean.setJobName(jobName);
				jobCounterBean.setJobStatsMap(map);
				jobCounterBeans.add(jobCounterBean);
				jobName = null;
			} else if (line.contains("Exception") || line.contains("Error")) {
				processExceptionCondition(processName, map, reader, jobName,
						line);
			}
		}
		if (reader != null) {
			reader.close();
		}
	}

	/**
	 * Process exception condition.
	 *
	 * @param processName
	 *            the process name
	 * @param map
	 *            the map
	 * @param reader
	 *            the reader
	 * @param jobName
	 *            the job name
	 * @param line
	 *            the line
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void processExceptionCondition(String processName,
			Map<String, String> map, BufferedReader reader, String jobName,
			String line) throws IOException {
		LOGGER.error("Exception occured while executing jar: " + line);
		map.put(ERRORANDEXCEPTION, line);
		while (true) {
			String lineTmp = line;
			lineTmp = reader.readLine();
			if (lineTmp == null) {
				break;
			}
			LOGGER.error(lineTmp);
		}
		JobCounterBean jobCounterBean = new JobCounterBean();
		jobCounterBean.setProcessName(processName);
		jobCounterBean.setJobName(jobName);
		jobCounterBean.setJobStatsMap(map);
		jobCounterBeans.add(jobCounterBean);

	}

}
