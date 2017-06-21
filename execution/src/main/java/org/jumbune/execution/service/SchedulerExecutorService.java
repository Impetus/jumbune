package org.jumbune.execution.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.scheduler.ScheduleTaskUtil;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import org.jumbune.utils.exception.ExtendedErrorCodesAndMessages;
/**
 * This class executes any scheduled job
 *
 * 
 */
public class SchedulerExecutorService extends CoreExecutorService {
	private static final Logger LOG = LogManager
			.getLogger(SchedulerExecutorService.class);

	public SchedulerExecutorService() {
		super();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		LOG.debug("Received request to process a scheduled job ******** \n");
		Thread.sleep(5000);
		if (args == null || args.length < 2) {
			LOG.error("Sufficient parameters are not specified so could not execute scheduled job!!");
		}

		SchedulerExecutorService schedulerExecService = new SchedulerExecutorService();
		String currentScheduledJobLoc = args[0];
		String frameworkHome = formatPath(args[1]);

		EnterpriseJobConfig.setJumbuneHome(frameworkHome);
		System.setProperty("JUMBUNE_HOME",frameworkHome );
		String currentScheduledJobJsonFilePath = ExtendedConfigurationUtil
				.getScheduleJobJsonFileLoc(currentScheduledJobLoc);

		boolean isScheduleJobExecuted = false;
		JumbuneRequest jumbuneRequest = null;
		try {
			jumbuneRequest = loadJob(currentScheduledJobJsonFilePath, frameworkHome);
			EnterpriseJobConfig ejc=(EnterpriseJobConfig)jumbuneRequest.getConfig();
			
			ReportsBean reports = new ReportsBean();
			isScheduleJobExecuted = schedulerExecService.run(
					currentScheduledJobLoc, jumbuneRequest, reports);
		} catch (FileNotFoundException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		} catch (JumbuneException e) {
			LOG.error(ErrorCodesAndMessages.JSON_NOT_FOUND);
		} catch (Exception e) {
			LOG.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		} finally {
			try {
				// Even if the job is executed or not but delete its entry from
				// cronTab file
				if (isScheduleJobExecuted) {
					schedulerExecService
					.deleteEntryFromCron(currentScheduledJobLoc);
				}
			} catch (IOException e) {
				LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			} catch (JumbuneException e) {
				LOG.error(ExtendedErrorCodesAndMessages.MESSAGE_REMOVE_CRON_ENTRY
						+ currentScheduledJobLoc + e);
			}
			LOG.debug("Successfully removed entry [" + currentScheduledJobLoc
					+ "] from crontab ");
			try {
				LOG.debug("clean up process slave tmp + agent home schedule case ");
				if (jumbuneRequest != null && jumbuneRequest.getConfig() != null) {
					EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) jumbuneRequest.getConfig();
					cleanUpSlavesTempFldr(jumbuneRequest);
					LOG.debug("clean up done");
				}
			} catch (Exception e) {
				LOG.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
		}
		LOG.info("Completed scheduled task!!!");
		System.exit(1);
	}

	/**
	 * This method is used to load json according to the file path.
	 *
	 * @param filePath the file path
	 * @return the object
	 * @throws IOException 
	 */
	private static JumbuneRequest loadJob(String configfilePath, String jumbuneHome) throws IOException {
		Gson gson = new Gson();
		EnterpriseJobConfig config = gson.fromJson(
				FileUtil.readFileIntoString(configfilePath), EnterpriseJobConfig.class);
		String clusterJsonFilePath = jumbuneHome + "/clusters/" + config.getOperatingCluster() + ".json";
		EnterpriseClusterDefinition enterpriseClusterDefinition = gson.fromJson(
				FileUtil.readFileIntoString(clusterJsonFilePath), EnterpriseClusterDefinition.class);
		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setCluster(enterpriseClusterDefinition);
		jumbuneRequest.setConfig(config);
		return jumbuneRequest;
	}
	
	public static EnterpriseClusterDefinition getClusterByName(String clusterJsonFilePath) throws IOException {
		File file = new File(clusterJsonFilePath);
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


	/**
	 * It deletes entry from crontab file so that the job is never executed
	 * again
	 * 
	 * @param currentJobLoc
	 *            - Current job location or directory from the job is executing
	 * @throws IOException
	 *             - if unable to remove entry from crontab file
	 * @throws JumbuneException
	 */
	private void deleteEntryFromCron(String currentJobLoc) throws IOException,
	JumbuneException {
		ScheduleTaskUtil scheduleTask = new ScheduleTaskUtil();
		scheduleTask.deleteCurrentJobEntryFromCron(currentJobLoc);
	}

	private boolean run(String currentScheduledJobLoc, JumbuneRequest jumbuneRequest,
			ReportsBean reports) throws JumbuneException {
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) jumbuneRequest.getConfig();
		boolean isStartExecution = checkProfilingState();
		if (isStartExecution) {
			updateStatusFile(currentScheduledJobLoc, JobStatus.IN_PROGRESS.toString());
			// Scheduling is not enabled execute job now.
			List<Processor> processors = getProcessorChain(
					enterpriseJobConfig, false);

			int index = 0;
			for (Processor p : processors) {

				Map<Parameters, String> params = new HashMap<Parameters, String>();
				String processName = "PROCESS" + (++index);
				reports.addInitialStatus(processName);
				params.put(Parameters.PROCESSOR_KEY, processName);
				params.put(Parameters.PROCESSOR_KEY, processName);
				createJobJarFolderOnAgent(jumbuneRequest);	
				try {
					p.process(jumbuneRequest, reports, params);
				} catch (JumbuneException e) {
					LOG.error(processName + " completed with errors !!!");
				} finally {
					// Delete file once the task is done
					deleteTokenFile();

					LOG.debug("Marking process [" + processName
							+ "] as complete !");
					// marking the process as complete
					reports.markProcessAsComplete(processName);
				}
			}

			String reportFolderPath = new StringBuilder(currentScheduledJobLoc)
					.append(ExtendedConstants.SCHEDULING_REPORT_FOLDER).toString();
			try {
				persistReports(reports, reportFolderPath);
			} catch (IOException e) {
				LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
			updateStatusFile(currentScheduledJobLoc,
					JobStatus.COMPLETED.toString());
			return true;
		} else {
			try {
				deleteEntryFromCron(currentScheduledJobLoc);
			} catch (IOException e) {
				// Even if unable to remove entry from cron tab atleast schedule
				// the task for re-attempt don't cancel the job
				LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
			LOG.debug("Since some other task is executing will be queue this task and execute it after 10 mins");

			enterpriseJobConfig
			.setJumbuneScheduleTaskTiming(REATTEMPT_TASK_SCHEDULING_TIME);
			scheduleTask(enterpriseJobConfig, false);
			return false;
		}
	}



	private static String formatPath(String path) {
		String pathTmp = path;
		if (pathTmp == null) {
			return null;
		}

		if (!pathTmp.endsWith(File.separator)){
			pathTmp = pathTmp + File.separator;
		}

		return pathTmp;
	}

	private void updateStatusFile(String currentJobLoc, String message) {
		String filePath = currentJobLoc + File.separator + ExtendedConstants.SCHEDULED_STATUS_FILE;


		try {
			ConfigurationUtil.writeToFile(filePath, message, true);
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		}
	}

}
