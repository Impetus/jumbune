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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataQualityTimeLineConfig;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.SchedulingEvent;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.reflect.TypeToken;


/**
 * Executes and schedules DataQuality Timeline job in timely basis. Contains
 * logic and required method to schedule and generate DataQuality Timeline job.
 *
 */
public class DataQualityTaskScheduler extends Scheduler {

	/**  Represents one underscore *. */
	private static final String ONE_UNDERSCORE = "1_";

	/** The Constant SCHEDULED_JOB_JSON. */
	/* Scheduled job json file name * */
	private static final String SCHEDULED_JOB_JSON = "scheduledJob.json";

	/** The Constant SIMPLE_DATE_FORMAT. */
	/* Date fomat DDMMYYY */
	private static final String SIMPLE_DATE_FORMAT = "yyyy_MM_dd";

	/** The Constant INTERVAL. */
	/* Interval */
	private static final String INTERVAL = "INTERVAL";

	/** The Constant EXEC_DIR. */
	/* Execution script parent directory name */
	private static final String EXEC_DIR = "bin" + File.separator;

	/** The Constant SPACE. */
	/* White space */
	private static final String SPACE = " ";

	/** The Constant SCHEDULER_SCRIPT. */
	/* Script name for executing scheduled job */
	private static final String SCHEDULER_SCRIPT = "runDqtScheduler.sh";

	/** The Constant USER_DATE_PATTERN. */
	/* User date format* */
	private static final String USER_DATE_PATTERN = "yyyy/MM/dd HH:mm" ;

	/** The Constant SCHEDULED. */
	/* Scheduled */
	private static final String SCHEDULED = "Scheduled";

	/** The Constant JOBSTATUS. */
	/* Jobstatus file name */
	private static final String JOBSTATUS = "jobstatus";

	/** The Constant SCHEDULED_JOB. */
	/* Scheduled directory name */
	private static final String SCHEDULED_JOB = "ScheduledJobs";

	/** The Constant INCREMENTAL_DQ_JOB. */
	/* directory name for residing incremental DataQualityTimline job */
	private static final String INCREMENTAL_DQ_JOB = "IncrementalDQJobs";
	
	/** The Constant NEW_LINE. */
	/* New line */
	private static final String NEW_LINE = "\n";

	/** The Constant LOGGER. */
	/* Logger */
	private static final Logger LOGGER = LogManager
			.getLogger(DataQualityTaskScheduler.class);

	/* (non-Javadoc)
	 * @see org.jumbune.common.scheduler.Scheduler#scheduleJob(org.jumbune.common.job.Config)
	 */
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
			if (dqtl.getScheduleJob().equalsIgnoreCase("cronExpression")) {
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
			copyExecutionScriptToDestFolder(JumbuneInfo.getHome() + EXEC_DIR
					+ SCHEDULER_SCRIPT, schedluedScriptPath);
		} catch (Exception e) {
			LOGGER.error("Unable to schedule DataQuality Job ", e);
			throw new JumbuneException(e.getMessage());
		}finally{
			if( cronInputStream != null){
				try {
					cronInputStream.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close Cron input stream ", e);
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
	
	/**
	 * Check time difference between browser and server time.
	 *
	 * @param sBrowserGMT the browser gmt
	 * @param scheduledTime the scheduled time
	 * @return the final date
	 * @throws ParseException the parse exception
	 */
	private Date getFinalDate(String sBrowserGMT, String scheduledTime) throws ParseException {
		
	 	long browserGMT = Long.parseLong(sBrowserGMT);
		Calendar mCalendar = new GregorianCalendar();  
		TimeZone mTimeZone = mCalendar.getTimeZone();
		int mGMTOffset = mTimeZone.getRawOffset();
		long serverGMT = TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				USER_DATE_PATTERN);
		Date date = simpleDateFormat.parse(scheduledTime);
		long finalTime = date.getTime() - (browserGMT - serverGMT);
		return new Date(finalTime);
	}

	/**
	 * Gets the cron expression from user input.
	 *
	 * @param dataQualityScheduler the data quality scheduler
	 * @return the cron expression from user input
	 * @throws Exception
	 */
	private String getCronExpressionFromUserInput(
			DataQualityTimeLineConfig dataQualityScheduler) throws Exception {

		return generateCronExpression(dataQualityScheduler.getSchedulingEvent(),
				dataQualityScheduler.getInterval(), dataQualityScheduler.getTime());
	}
	
	public String generateCronExpression(SchedulingEvent schedulingEvent, Integer interval, String time) throws Exception{
		String WHITE_SPACE = " ";
		switch (schedulingEvent) {
		case MINUTE:
			return "*/"+ interval + " * * * *";
		case HOURLY:
			return "0 */" + interval + " * * *";
		case DAILY:
			int hours = Integer.parseInt(time.split(":")[0]);
			int minutes = Integer.parseInt(time.split(":")[1]);
			return new StringBuilder().append(minutes).append(WHITE_SPACE)
					.append(hours).append(WHITE_SPACE).append("*/")
					.append(interval).append(" * *").toString();
		default:
			break;
		}
		throw new Exception("Insufficient Data");
	}

	/**
	 * Copy resources for scheduling.
	 *
	 * @param config the config
	 * @param scheduledJobLoc the scheduled job loc
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void copyResourcesForScheduling(Config config,
			String scheduledJobLoc) throws IOException {
		JobConfig jobConfig = (JobConfig) config;
		updateJobStatus(scheduledJobLoc);
		JobConfig clonedJobConfig = disableOtherModulesIfEnabled(jobConfig);
		writeConfigurationInScheduledJobDirectory(clonedJobConfig);
		LOGGER.debug("Scheduled job resources have been copied successfully");
	}

	/**
	 * Disable other modules if enabled.
	 *
	 * @param jobConfig the job config
	 * @return the job config
	 */
	private JobConfig disableOtherModulesIfEnabled(JobConfig jobConfig) {
		String configJson = Constants.gson.toJson(jobConfig, JobConfig.class);
		JobConfig clonedJobConfig = Constants.gson.fromJson(configJson, JobConfig.class);
		if (JobConfigUtil.isEnable(clonedJobConfig.getDebugAnalysis())) {
			clonedJobConfig.setDebugAnalysis(Enable.FALSE);
		}
		if (JobConfigUtil.isEnable(clonedJobConfig.getEnableDataProfiling())) {
			clonedJobConfig.setEnableDataProfiling(Enable.FALSE);
		}
		if (JobConfigUtil.isEnable(jobConfig.getEnableStaticJobProfiling())) {
			clonedJobConfig.setEnableStaticJobProfiling(Enable.FALSE);
		}
		if (clonedJobConfig.getInputFile() != null) {
			clonedJobConfig.setInputFile(null);
		}
		return clonedJobConfig;
	}
	

	/**
	 * Write configuration in scheduled job directory.
	 *
	 * @param config the config
	 */
	private void writeConfigurationInScheduledJobDirectory(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		String json = Constants.gson.toJson(jobConfig, JobConfig.class);
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

	/**
	 * Update job status.
	 *
	 * @param scheduledJobLocation the scheduled job location
	 */
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

	/**
	 * *
	 * Finds DataTimeline Scheduled job jobstatus file location.
	 *
	 * @param config the config
	 * @return jobstatus file path of current job.
	 */
	public String getScheduledJobLocation(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		String jobName = jobConfig.getJumbuneJobName();
		String scheduledJobDirectory = JumbuneInfo.getHome() + SCHEDULED_JOB + File.separator
				+ INCREMENTAL_DQ_JOB + File.separator + jobName
				+ File.separator;
		File file = new File(scheduledJobDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		return scheduledJobDirectory;
	}

	/**
	 * *
	 * Checks whether the job is scheduled or not.
	 *
	 * @param config the config
	 * @return true if job is scheduled otherwise return false
	 * @throws JumbuneException the jumbune exception
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

	/**
	 * Gets the job status file instance.
	 *
	 * @param config the config
	 * @return the job status file instance
	 * @throws JumbuneException the jumbune exception
	 */
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

	/**
	 * Read property instance.
	 *
	 * @param file the file
	 * @return the properties
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Properties readPropertyInstance(File file) throws IOException {
		Properties prop = new Properties();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			prop.load(fileReader);
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace()));
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return prop;
	}

	/**
	 * Saves DataQuality Timeline scheduled job result in Jumbune installation
	 * directory.
	 *
	 * @param config            YamlConfig
	 * @param report            DataQuality Timeline report
	 * @throws JumbuneException the jumbune exception
	 * @throws IOException Signals that an I/O exception has occurred.
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

	/**
	 * Update job status file interval.
	 *
	 * @param config the config
	 * @param interval the interval
	 * @throws JumbuneException the jumbune exception
	 */
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

	/**
	 * Save property file.
	 *
	 * @param prop the prop
	 * @param location the location
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void savePropertyFile(Properties prop, String location)
			throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(location);
			long currentTime = System.currentTimeMillis();
			prop.setProperty("lastExecutedTime", Long.toString(currentTime));
			prop.store(fw,
					"Updated Interval time [" + currentTime
							+ "]");
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	/**
	 * It Saves result of dataQuality Timeline job result.
	 *
	 * @param scheduledJobLocation            location of scheduled Jumbune DataQuality Timline job
	 * @param dataQualityTaskSchedular            {@link DataQualityTaskScheduler}
	 * @param jobConfig            JobConfig
	 * @param dvReport            report of DataQualityTimeLine job
	 * @throws JumbuneException the jumbune exception
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
	 * @param dvReport            dataQuality job result json.
	 * @param config the config
	 * @param isDataQualityReportEmpty the is data quality report empty
	 * @return String DataQuality Timeline report in json format.
	 */
	public String generateDataQualityReport(String dvReport, Config config,
			boolean isDataQualityReportEmpty , Date launchTime) {
		JobConfig jobConfig = (JobConfig) config;
		Map<String, String> dataQualityReport = new HashMap<String, String>();
		Map<String, Map<String, String>> dqrWrapper = new HashMap<String, Map<String, String>>();
		if (!isDataQualityReportEmpty) {
			Integer totalTupleProcessed = 0, cleanTuple = 0;
			if(Enable.TRUE.equals(jobConfig.getEnableDataValidation()) || Enable.TRUE.equals(jobConfig.getEnableDataQualityTimeline())){
			totalTupleProcessed = getTupleResult(config, 1, Constants.CONSOLIDATED_DV_LOC);
			cleanTuple = getTupleResult(config, 2, Constants.CONSOLIDATED_DV_LOC);
			}else if(Enable.TRUE.equals(jobConfig.getEnableJsonDataValidation())){
				totalTupleProcessed = getTupleResult(config, 1, Constants.CONSOLIDATED_JSON_DV_LOC);
				cleanTuple = getTupleResult(config, 2, Constants.CONSOLIDATED_JSON_DV_LOC);
			}else{
				totalTupleProcessed = getTupleResult(config, 1, Constants.CONSOLIDATED_XML_DV_LOC);
				cleanTuple = getTupleResult(config, 2, Constants.CONSOLIDATED_XML_DV_LOC);
			}
			populateDataTimeLineValues(dataQualityReport, totalTupleProcessed,
					cleanTuple, dvReport);
		} else {
			populateDataTimeLineValues(dataQualityReport, 0, 0, "{}");
		}
		dqrWrapper.put(new Long(launchTime.getTime()).toString(), dataQualityReport);
		return Constants.gson.toJson(dqrWrapper);
	}

	/**
	 * Populate data time line values.
	 *
	 * @param dataQualityReport the data quality report
	 * @param totalTupleProcessed the total tuple processed
	 * @param cleanTuple the clean tuple
	 * @param dvReport the dv report
	 */
	private void populateDataTimeLineValues(
			Map<String, String> dataQualityReport, Integer totalTupleProcessed,
			Integer cleanTuple, String dvReport) {
		dataQualityReport.put("jsonReport", dvReport);
		dataQualityReport.put("totalTupleProcessed",
				totalTupleProcessed.toString());
		dataQualityReport.put("cleanTuple", cleanTuple.toString());
	}

	/**
	 * Gets the tuple result.
	 *
	 * @param config the config
	 * @param lineToget the line toget
	 * @return the tuple result
	 */
	private Integer getTupleResult(Config config, int lineToget, String dvName) {
		JobConfig jobConfig = (JobConfig) config;
		StringBuilder sb = new StringBuilder();
		String tupleDirectory = sb.append(JumbuneInfo.getHome()).append(Constants.JOB_JARS_LOC)
				.append(File.separator).append(jobConfig.getJumbuneJobName())
				.append(File.separator).append(dvName).append(File.separator)
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
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException ioe) {
						LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(ioe.getStackTrace()));
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
		int interval = 0;
		boolean avoidFirstIteration = false;
		StringBuilder sb = new StringBuilder();
		TreeMap<String, Map<String, String>> timeStamp = new TreeMap<String, Map<String, String>>(
				new TimestampComparator());
		String directoryPath = sb.append(JumbuneInfo.getHome()).append(SCHEDULED_JOB)
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
		} catch (JumbuneException je) {
			LOGGER.error("properties file not found", je);
		}
		File file = null;
		sb.setLength(0);
		sb = new StringBuilder();
		for (String jobResult : directories) {
			if (!jobResult.equals("bin")) {
				file = new File(sb.append(JumbuneInfo.getHome()).append(SCHEDULED_JOB)
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
					Map<String, Map<String, String>> scheduleJobResult = Constants.gson
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
							LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(er.getStackTrace()));
						}
					}

				}
				sb.setLength(0);
			}

		}
		return Constants.gson.toJson(timeStamp);

	}
	
	/**
	 * This class is used for comparing timestamp.
	 *
	 */
	class TimestampComparator implements Comparator<String> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
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
