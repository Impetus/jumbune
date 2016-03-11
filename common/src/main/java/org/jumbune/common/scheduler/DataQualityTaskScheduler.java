package org.jumbune.common.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataQualityTimeLineConfig;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.CronGenerator;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Executes and schedules DataQuality Timeline job in timely basis. Contains
 * logic and required method to schedule and generate DataQuality Timeline job.
 *
 */
public class DataQualityTaskScheduler extends Scheduler {

	/** Represents one underscore **/
	private static final String ONE_UNDERSCORE = "1_";

	/* Scheduled job json file name * */
	private static final String SCHEDULED_JOB_JSON = "scheduledJob.json";

	/* Date fomat DDMMYYY */
	private static final String SIMPLE_DATE_FORMAT = "yyyy_MM_dd";

	/* Interval */
	private static final String INTERVAL = "INTERVAL";

	/* Execution script parent directory name */
	private static final String EXEC_DIR = "bin" + File.separator;

	/* White space */
	private static final String SPACE = " ";

	/* Script name for executing scheduled job */
	private static final String SCHEDULER_SCRIPT = "runScheduler.sh";

	/* User date format* */
	private static final String USER_DATE_PATTERN = "HH:mm:SS MM/dd/yyyy";

	/* Scheduled */
	private static final String SCHEDULED = "Scheduled";

	/* Jobstatus file name */
	private static final String JOBSTATUS = "jobstatus";

	/* Scheduled directory name */
	private static final String SCHEDULED_JOB = "ScheduledJobs";

	/* directory name for residing incremental DataQualityTimline job */
	private static final String INCREMENTAL_DQ_JOB = "IncrementalDQJobs";

	/* Jumbune home */
	private static final String JUMBUNE_HOME = JobConfig.getJumbuneHome()
			+ File.separator;
	/* New line */
	private static final String NEW_LINE = "\n";

	/* Logger */
	private static final Logger LOGGER = LogManager
			.getLogger(DataQualityTaskScheduler.class);

	@Override
	public void scheduleJob(Config config) throws JumbuneException {
		JobConfig jobConfig = null;
		InputStream cronInputStream = null;
		try {
			jobConfig = (JobConfig) config;
			String cronExpression = null;
			DataQualityTimeLineConfig dqtl = jobConfig.getDataQualityTimeLineConfig();
			String scheduledJobLocation = getScheduledJobLocation(jobConfig);
			copyResourcesForScheduling(jobConfig, scheduledJobLocation);
			if (ValidateInput.isEnable(dqtl.getEnableCronExpression())) {
				cronExpression = dqtl.getCronExpression();
			} else {
				cronExpression = getCronExpressionFromUserInput(dqtl);
			}
			cronInputStream = getCurrentUserCronTabInfo();
			StringBuilder updatedCronInfoBuilder = new StringBuilder(
					readCronTabFile(cronInputStream));
			String schedluedScriptPath = scheduledJobLocation + EXEC_DIR
					+ File.separator + SCHEDULER_SCRIPT;
			updatedCronInfoBuilder.append(getJumbuneScheduleTaskInfo(
					cronExpression, schedluedScriptPath));
			addUpdatedFileToCron();
			writeLatestCronTabToFile(updatedCronInfoBuilder.toString());
			copyExecutionScriptToDestFolder(JUMBUNE_HOME + EXEC_DIR
					+ SCHEDULER_SCRIPT, schedluedScriptPath);
		} catch (Exception e) {
			LOGGER.error("Unable to schedule DataQuality Job ", e);
			throw new JumbuneException(e.getMessage());
		}finally{
			if(cronInputStream != null){
				try {
					cronInputStream.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close cron input stream", e);
				}
			}
		}

	}

	/**
	 * Creates a jumbuneTask scheduling info, appends the scheduleJob date time
	 * along with command.
	 *
	 * @param scheduledJobDateTime
	 *            the scheduled job date time
	 * @param jumbuneScheduleTaskCommand
	 *            the jumbune schedule task command
	 * @return - return string which can be placed in crontab file for
	 *         scheduling a job
	 */
	private String getJumbuneScheduleTaskInfo(String scheduledJobDateTime,
			String jumbuneScheduleTaskCommand) {
		StringBuilder jumbuneSchedulerdBuilder = new StringBuilder(
				scheduledJobDateTime).append(SPACE)
				.append(jumbuneScheduleTaskCommand).append(NEW_LINE)
				.append("#");
		return jumbuneSchedulerdBuilder.toString();
	}

	private String getCronExpressionFromUserInput(
			DataQualityTimeLineConfig dataQualityScheduler) throws ParseException {
		String cronExpression = null;
		String textdate = dataQualityScheduler.getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				USER_DATE_PATTERN);
		Date date = simpleDateFormat.parse(textdate);
		CronGenerator cronGenerator = new CronGenerator(date);
		if (dataQualityScheduler.getInterval() > 1) {
			cronExpression = cronGenerator.generateCronExpression(
					dataQualityScheduler.getSchedulingEvent(),
					dataQualityScheduler.getInterval());
		} else {
			cronExpression = cronGenerator
					.generateCronExpression(dataQualityScheduler
							.getSchedulingEvent());
		}
		return cronExpression;
	}

	private void copyResourcesForScheduling(Config config,
			String scheduledJobLoc) throws IOException {
		JobConfig jobConfig = (JobConfig) config;
		updateJobStatus(scheduledJobLoc);
		JobConfig clonedJobConfig = disableOtherModulesIfEnabled(jobConfig);
		writeConfigurationInScheduledJobDirectory(clonedJobConfig);
		LOGGER.info("Scheduled job resources have been copied successfully");
	}

	private JobConfig disableOtherModulesIfEnabled(JobConfig jobConfig) {
		Gson gson = new Gson();
		String configJson = gson.toJson(jobConfig, JobConfig.class);
		JobConfig clonedJobConfig = gson.fromJson(configJson, JobConfig.class);
		if (ValidateInput.isEnable(clonedJobConfig.getDebugAnalysis())) {
			clonedJobConfig.setDebugAnalysis(Enable.FALSE);
		}
		if (ValidateInput.isEnable(clonedJobConfig.getEnableDataProfiling())) {
			clonedJobConfig.setEnableDataProfiling(Enable.FALSE);
		}
		if (ValidateInput.isEnable(jobConfig.getEnableStaticJobProfiling())) {
			clonedJobConfig.setEnableStaticJobProfiling(Enable.FALSE);
		}
		if (clonedJobConfig.getInputFile() != null) {
			clonedJobConfig.setInputFile(null);
		}
		return clonedJobConfig;
	}

	private void writeConfigurationInScheduledJobDirectory(Config config) {
		Gson gson = new Gson();
		JobConfig jobConfig = (JobConfig) config;
		String json = gson.toJson(jobConfig, JobConfig.class);
		File file = null;
		FileWriter fileWriter = null;
		try {
			file = new File(getScheduledJobLocation(jobConfig)
					+ "scheduledJson.json");
			fileWriter = new FileWriter(file);
			fileWriter.write(json);
			fileWriter.flush();
		} catch (IOException e) {
			LOGGER.error("Unable to save scheduled job configuration ", e);
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException ioe) {
				LOGGER.error(
						"Could not close file writer while saving job configuration ",
						ioe);
			}
		}

	}

	private void updateJobStatus(String scheduledJobLocation) {
		Properties prop = new Properties();
		OutputStream os = null;
		try {
			prop.setProperty(INTERVAL, "0");
			prop.setProperty(JOBSTATUS, SCHEDULED);
			File file = new File(scheduledJobLocation + File.separator
					+ JOBSTATUS);
			os = new FileOutputStream(file);
			prop.store(os, "Job has been scheduled");
		} catch (IOException e) {
			LOGGER.error("Could not load properties in configuration file", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ioe) {
					LOGGER.error("could not close the output stream", ioe);
				}
			}
		}
	}

	/***
	 * Finds DataTimeline Scheduled job jobstatus file location
	 * 
	 * @param config
	 * @return jobstatus file path of current job.
	 */
	public String getScheduledJobLocation(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		String jobName = jobConfig.getJumbuneJobName();
		String scheduledJobDirectory = JobConfig.getJumbuneHome()
				+ File.separator + SCHEDULED_JOB + File.separator
				+ INCREMENTAL_DQ_JOB + File.separator + jobName
				+ File.separator;
		File file = new File(scheduledJobDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		return scheduledJobDirectory;
	}

	/***
	 * Checks whether the job is scheduled or not
	 * 
	 * @param config
	 * @return true if job is scheduled otherwise return false
	 * @throws JumbuneException
	 */
	public boolean isJobAlreadyScheduled(Config config) throws JumbuneException {
		JobConfig jobConfig = (JobConfig) config;
		Properties prop = null;
		if ((prop = getJobStatusFileInstance(jobConfig)) != null) {
			int interval = Integer.parseInt((String) (prop.get(INTERVAL)));
			if (interval >= 0) {
				return true;
			}
		}
		return false;
	}

	private Properties getJobStatusFileInstance(Config config)
			throws JumbuneException {
		JobConfig jobConfig = (JobConfig) config;
		String scheduledJobDirectory = getScheduledJobLocation(jobConfig);
		String scheduledJobStatusFilePath = scheduledJobDirectory
				+ File.separator + JOBSTATUS;
		File file = new File(scheduledJobStatusFilePath);
		try {
			if (file.exists()) {
				return readPropertyInstance(file);
			}
		} catch (IOException e) {
			throw new JumbuneException("Properties instance is not created");
		}
		return null;
	}

	private Properties readPropertyInstance(File file) throws IOException {
		Properties prop = new Properties();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			prop.load(fileReader);
		} catch (IOException e) {
			LOGGER.error("Could not read the file ", e);
		} finally {
			if (fileReader != null) {
				try{
					fileReader.close();
				}catch(IOException ex){
					LOGGER.error("exception occured while closing properties file",ex);
				}
			}
		}
		return prop;
	}

	/**
	 * Saves DataQuality Timeline scheduled job result in Jumbune installation
	 * directory.
	 * 
	 * @param config
	 *            YamlConfig
	 * @param report
	 *            DataQuality Timeline report
	 * @throws JumbuneException
	 * @throws IOException
	 */
	public void saveScheduledJobResult(Config config, String report)
			throws JumbuneException, IOException {
		JobConfig jobConfig = (JobConfig) config;
		String scheduledJobDirectory = getScheduledJobLocation(config);
		Properties prop = getJobStatusFileInstance(jobConfig);
		int interval = Integer.parseInt((String) prop.get(INTERVAL));
		interval += interval;
		String currentDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT)
				.format(new Date());
		String resultLocation = scheduledJobDirectory + File.separator
				+ jobConfig.getJumbuneJobName() + File.separator + "result"
				+ File.separator + interval + "_" + currentDate;
		ConfigurationUtil.writeToFile(resultLocation, report);
		updateJobStatusFileInterval(config, interval);

	}

	private void updateJobStatusFileInterval(Config config, int interval)
			throws JumbuneException {
		Properties prop = getJobStatusFileInstance(config);
		prop.setProperty(INTERVAL, Integer.toString(interval));
		String scheduledJobDirectory = getScheduledJobLocation(config);

		String scheduledJobStatusFilePath = scheduledJobDirectory
				+ File.separator + JOBSTATUS;
		try {
			savePropertyFile(prop, scheduledJobStatusFilePath);
		} catch (IOException e) {
			LOGGER.error(
					"exception occured while saving properties into status file",
					e);
			throw new JumbuneException(e.getMessage());
		}
	}

	private void savePropertyFile(Properties prop, String location)
			throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(location);
			prop.store(fw,
					"Updated Interval time [" + System.currentTimeMillis()
							+ "]");
		} finally {
			if (fw != null) {
				try{
					fw.close();
				}catch(IOException ex){
					LOGGER.error("exception occured while closing properties file",ex);
				}
			}
		}
	}

	/**
	 * It Saves result of dataQuality Timeline job result.
	 * 
	 * @param scheduledJobLocation
	 *            location of scheduled Jumbune DataQuality Timline job
	 * @param dataQualityTaskSchedular
	 *            {@link DataQualityTaskScheduler}
	 * @param jobConfig
	 *            JobConfig
	 * @param dvReport
	 *            report of DataQualityTimeLine job
	 * @throws JumbuneException
	 */
	public void persistDataQualityReport(String scheduledJobLocation,
			DataQualityTaskScheduler dataQualityTaskSchedular,
			JobConfig jobConfig, String dvReport) throws JumbuneException {
		Properties prop = dataQualityTaskSchedular
				.getJobStatusFileInstance(jobConfig);
		int interval = Integer.parseInt(prop.getProperty("INTERVAL"));
		interval = interval + 1;
		prop.setProperty("INTERVAL", Integer.toString(interval));
		String resultLocaton = scheduledJobLocation + interval + "_"
				+ new SimpleDateFormat(SIMPLE_DATE_FORMAT).format(new Date());
		new File(resultLocaton).mkdirs();
		try {
			ConfigurationUtil.writeToFile(resultLocaton + File.separator
					+ SCHEDULED_JOB_JSON, dvReport);
		} catch (IOException e) {
			LOGGER.error(
					"Failed to write scheduled datavalidation result to scheduled job location ",
					e);
			throw new JumbuneException(e.getMessage());
		}
		dataQualityTaskSchedular.updateJobStatusFileInterval(jobConfig,
				interval);

	}

	/**
	 * This method generates DataQualityTimeline report in a formatted way.
	 * 
	 * @param dvReport
	 *            dataQuality job result json.
	 * @param loader
	 *            YamlLoader
	 * @return String DataQuality Timeline report in json format.
	 */
	public String generateDataQualityReport(String dvReport, Config config,
			boolean isDataQualityReportEmpty) {
		JobConfig jobConfig = (JobConfig) config;
		Map<String, String> dataQualityReport = new HashMap<String, String>();
		Map<String, Map<String, String>> dqrWrapper = new HashMap<String, Map<String, String>>();
		if (!isDataQualityReportEmpty) {
			Integer totalTupleProcessed = getTupleResult(config, 1);
			Integer cleanTuple = getTupleResult(config, 2);
			populateDataTimeLineValues(dataQualityReport, totalTupleProcessed,
					cleanTuple, dvReport);
		} else {
			populateDataTimeLineValues(dataQualityReport, 0, 0, "{}");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				USER_DATE_PATTERN);
		Date date = null;
		try {
			date = simpleDateFormat.parse(jobConfig.getDataQualityTimeLineConfig()
					.getTime());
		} catch (ParseException pe) {
			LOGGER.error("Unable to parse date format", pe);
		}
		dqrWrapper.put(new Long(date.getTime()).toString(), dataQualityReport);
		return new Gson().toJson(dqrWrapper);
	}

	private void populateDataTimeLineValues(
			Map<String, String> dataQualityReport, Integer totalTupleProcessed,
			Integer cleanTuple, String dvReport) {
		dataQualityReport.put("jsonReport", dvReport);
		dataQualityReport.put("totalTupleProcessed",
				totalTupleProcessed.toString());
		dataQualityReport.put("cleanTuple", cleanTuple.toString());
	}

	private Integer getTupleResult(Config config, int lineToget) {
		JobConfig jobConfig = (JobConfig) config;
		StringBuilder sb = new StringBuilder();
		String tupleDirectory = sb.append(JobConfig.getJumbuneHome())
				.append(File.separator).append("jobJars")
				.append(File.separator).append(jobConfig.getJumbuneJobName())
				.append(File.separator).append("dv").append(File.separator)
				.append("tuple").toString();
		File dir = new File(tupleDirectory);
		File[] tuples = dir.listFiles();
		BufferedReader br = null;
		int totalTupleProcessed = 0;
		String tuple = null;
		int count = 1;
		for (File file : tuples) {
			count = 1;
			try {
				br = new BufferedReader(new FileReader(file));
				while ((tuple = br.readLine()) != null) {
					if (count == lineToget) {
						totalTupleProcessed = totalTupleProcessed
								+ Integer.parseInt(tuple);
						break;
					}
					count++;
				}
			} catch (IOException e) {
				LOGGER.error("error while reading file", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException ioe) {
						LOGGER.error("Unable to close connection ", ioe);
					}
				}
			}
		}
		return totalTupleProcessed;
	}

	/**
	 * This method collects all previous scheduled DataQualityTimeline job
	 * results by jobname.
	 * 
	 * @param jobConfig
	 *            YamlConfig
	 * @param jobName
	 *            jobname which user wants to get result.
	 * @return String collected DataQualityTimeline job result in json format
	 */
	public String getDataQualityTimeLineReport(JobConfig jobConfig,
			String jobName) {
		Gson gson = new Gson();
		int interval = 0;
		boolean avoidFirstIteration = false;
		StringBuilder sb = new StringBuilder();
		TreeMap<String, Map<String, String>> timeStamp = new TreeMap<String, Map<String, String>>(
				new TimestampComparator());
		String directoryPath = sb.append(JobConfig.getJumbuneHome())
				.append(File.separator).append(SCHEDULED_JOB)
				.append(File.separator).append(INCREMENTAL_DQ_JOB)
				.append(File.separator).append(jobName).append(File.separator)
				.toString();
		File f = new File(directoryPath);
		String[] directories = f.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		jobConfig.setJumbuneJobName(jobName);
		Properties prop = null;
		try {
			prop = getJobStatusFileInstance(jobConfig);
			interval = Integer.parseInt(prop.getProperty(INTERVAL));
			if (interval > 1) {
				avoidFirstIteration = true;
			}
		} catch (JumbuneException je) {
			LOGGER.error("properties file not found", je);
		}
		File file = null;
		sb.setLength(0);
		sb = new StringBuilder();
		for (String jobResult : directories) {
			if (jobResult.startsWith(ONE_UNDERSCORE) && avoidFirstIteration) {
				continue;
			}
			if (!jobResult.equals("bin")) {
				file = new File(sb.append(JobConfig.getJumbuneHome())
						.append(File.separator).append(SCHEDULED_JOB)
						.append(File.separator).append(INCREMENTAL_DQ_JOB)
						.append(File.separator).append(jobName)
						.append(File.separator).append(jobResult)
						.append(File.separator).append(SCHEDULED_JOB_JSON)
						.toString());
				BufferedReader br = null;
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				try {
					br = new BufferedReader(new FileReader(file));
					while ((line = br.readLine()) != null) {
						stringBuilder.append(line);
					}
					line = stringBuilder.toString().trim();
					Type type = new TypeToken<Map<String, Map<String, String>>>() {
					}.getType();
					Map<String, Map<String, String>> scheduleJobResult = gson
							.fromJson(line, type);
					for (Entry<String, Map<String, String>> map : scheduleJobResult
							.entrySet()) {
						timeStamp.put(map.getKey(), map.getValue());
					}

				} catch (IOException e) {
					LOGGER.error("Unable to read scheduled job result files ",
							e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException er) {
							LOGGER.error("Unable to close reader", er);
						}
					}

				}
				sb.setLength(0);
			}

		}
		return gson.toJson(timeStamp);

	}

	/**
	 * This class is used for comparing timestamp.
	 *
	 */
	class TimestampComparator implements Comparator<String> {

		@Override
		public int compare(String firstTimeStamp, String secondTimestamp) {
			long date_one = 0l;
			long date_two = 0l;
			date_one = Long.parseLong(firstTimeStamp);
			date_two = Long.parseLong(secondTimestamp);
			if (date_one > date_two) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
