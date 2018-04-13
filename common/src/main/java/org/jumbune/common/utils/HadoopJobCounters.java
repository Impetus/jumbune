package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobCounterBean;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
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
	private List<JobCounterBean> jobCounterBeans = new ArrayList<JobCounterBean>();

	/**
	 * Fetch cluster configuration properties.
	 *
	 * @param config
	 *            the loader
	 * @return the string
	 */
	private String fetchClusterConfigurationProperties(JumbuneRequest jumbuneRequest) {
		
		JobConfig jobConfig =  jumbuneRequest.getJobConfig();
		String expectedLocation = new StringBuilder()
				.append(JumbuneInfo.getHome())
				.append(Constants.JOB_JARS_LOC)
				.append(jobConfig.getJumbuneJobName()).append(File.separator)
				.append("cluster-configuration.properties").toString();
		File file = new File(expectedLocation);
		if (!file.exists() || file.isDirectory()) {
			Cluster cluster = jumbuneRequest.getCluster();
			Remoter remoter = RemotingUtil.getRemoter(cluster);
			String relativePath = File.separator + Constants.JOB_JARS_LOC
					+ jobConfig.getJumbuneJobName();
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
	public List<JobCounterBean> getJobCounterBeans() {
		return jobCounterBeans;
	}

	/**
	 * Sets the job counter map.
	 *
	 * @param processName
	 *            the process name
	 * @param response
	 *            the response
	 * @param config
	 *            the loader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void setJobCounterBeans(String processName, String response, 	JumbuneRequest jumbuneRequest) throws IOException {
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		BufferedReader reader = new BufferedReader(new StringReader(response));
		String line = null;
		String jobName = null;
		boolean isYarnJob = false;
		JobCounterBean jobCounterBean = new JobCounterBean();
		isYarnJob = FileUtil.getPropertyFromFile(
				fetchClusterConfigurationProperties( jumbuneRequest), HADOOP_TYPE)
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
	private void processExceptionCondition(String processName,
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

	/**
	 * Gets the value by job name and property.
	 *
	 * @param jobList
	 *            the job list
	 * @param jobName
	 *            the job name
	 * @param property
	 *            the property
	 * @return the value by job name and property
	 */
	public String getValueByJobNameAndProperty(String jobName, String property) {

		for (JobCounterBean bean : jobCounterBeans) {
			if (bean.getJobName().equalsIgnoreCase(jobName)) {
				return bean.getJobStatsMap().get(property);
			}

		}

		return null;
	}

}
