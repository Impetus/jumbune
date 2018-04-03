package org.jumbune.execution.processor;

import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_EXECUTION_YAML_COMMAND;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_VALID_INPUT;
import static org.jumbune.profiling.utils.ProfilerConstants.ERRORANDEXCEPTION;
import static org.jumbune.profiling.utils.ProfilerConstants.PURE_PROFILING_EXCEPTION_DETAIL;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.HadoopLogParser;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.ResourceUsageCollector;
import org.jumbune.debugger.instrumentation.instrumenter.Instrumenter;
import org.jumbune.debugger.instrumentation.instrumenter.PureJarInstrumenter;

import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.traverse.JarTraversal;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.UserInputUtil;

import org.jumbune.profiling.utils.HeapAllocStackTraceExclStrat;
import org.jumbune.profiling.utils.ProfilerUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import org.jumbune.execution.beans.CommunityModule;

/**
 * This processor class performs the execution of pure jar and profiling of the job.
 * 
 * 
 */
public class ProfilingProcessor extends BaseProcessor {

	private static final Logger LOGGER = LogManager.getLogger(ProfilingProcessor.class);
	private static final String TEMP_DIR = "tmp";
	private static final String TOKEN_FILE = "/jumbuneState.txt";

	/**
	 * constructor for ProfilingProcessor
	 * @param isCommandBased
	 */
	public ProfilingProcessor(boolean isCommandBased) {
		super(isCommandBased);
	}

	@Override
	protected boolean execute(Map<Parameters, String> params)
			throws JumbuneException {
		LOGGER.info("Executing [Profiling] Processor..");
		Scanner scanner = new Scanner(System.in);
		JarTraversal tra = new JarTraversal();
		List<String> jarJobList = null;
		// if main class is not defined in jar manifest, then only traverse the
		// jar for main classes
		JumbuneRequest jumbuneRequest = super.getJumbuneRequest();
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) jumbuneRequest.getConfig();
		if (!enterpriseJobConfig.isMainClassDefinedInJobJar()) {
			try {
				jarJobList = tra.getAlljobs(enterpriseJobConfig.getInputFile());
			} catch (IOException ioe) {
				LOGGER.error("Not able to get jobs list from jar.", ioe);
				return false;
			}
		}

		executeForCommandBased(scanner);

		// if main class is not defined in jar manifest, matching the yaml jobs
		// classes with main classes in jar
		checkJobClassesInJar(jarJobList);

		String pureJarPath = enterpriseJobConfig.getInputFile();
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		

		Enable runFromJumbune = enterpriseJobConfig.getRunJobFromJumbune();
		ResourceUsageCollector collector = new ResourceUsageCollector(jumbuneRequest);
		LOGGER.debug("Pure MapReduce jar path :" + pureJarPath);
		JobOutput jobOutput = null;
		String jobID = null;
		try {
			if (hadoopType.equalsIgnoreCase(ExtendedConstants.NON_YARN)) {
					if (runFromJumbune.getEnumValue()) {
					LOGGER.debug("Fired top command on Workers");
					String receiveDir = fireTopOnSlaves(collector);

					Map<String, Map<String, String>> jobsCounterMap = processHelper
							.executeJar(pureJarPath, super.isCommandBased(),
									jumbuneRequest, false);
					jobID = getJobIdfromJobCountersMap(jobsCounterMap);

					collector.shutTopCmdOnSlaves(receiveDir);
					LOGGER.debug("Stopped top command on Workers");

					jobOutput = getJobOutput(jobID.trim());
					List<String> selectedHosts = collector
							.getNodesForJob(jobOutput);
					collector.processTopDumpFile(receiveDir, selectedHosts);
					calcStatsFromLogConsolidationInfo(jobOutput, collector);
					LOGGER.debug("Profiling final JSON [" + jobOutput + "]");
				} else {
					jobID = enterpriseJobConfig.getExistingJobName();
					// rumen processing
					jobOutput = getJobOutput(jobID.trim());
					collector.addPhaseResourceUsageForHistoricalJob(jobOutput, jobID);
				}
			} else {
				if (runFromJumbune.getEnumValue()) {
					LOGGER.debug("Fired top command on Workers");
					String receiveDir = fireTopOnSlaves(collector);
					Map<String, Map<String, String>> jobsCounterMap = processHelper
							.executeJar(pureJarPath, super.isCommandBased(),
									jumbuneRequest, false);
					jobID = getJobIdfromJobCountersMap(jobsCounterMap);
					collector.shutTopCmdOnSlaves(receiveDir);
					LOGGER.debug("Stopped top command on Workers");
					jobOutput = getJobOutput(jobID.trim());
					List<String> selectedHosts = collector.getNodesForJob(jobOutput);
					collector.processTopDumpFile(receiveDir, selectedHosts);
					calcStatsFromLogConsolidationInfo(jobOutput, collector);
					LOGGER.debug("Profiling final JSON [" + jobOutput + "]");
				} else {
					jobID = enterpriseJobConfig.getExistingJobName();
					jobOutput = getJobOutput(jobID.trim());
				}
			}

		} catch (IOException e) {
			LOGGER.error("Error while executing pure jar.", e);
			return false;
		} catch (Exception ex) {
			LOGGER.error("Error while processing per phase stats.", ex);
			return false;
		} finally {
			LOGGER.info("Pure Job Execution Completed");
		}

		if (enterpriseJobConfig.isHadoopJobProfileEnabled()) {
			populateProfilingAnalysisReport(jobOutput);
		}
		LOGGER.info("Exited from [Profiling] processor...");
		return true;
	}

	private void calcStatsFromLogConsolidationInfo(JobOutput jobOutput,
			ResourceUsageCollector collector) throws JumbuneException, IOException,
			InterruptedException { 
		Cluster cluster = super.getJumbuneRequest().getCluster();
		cluster.getNameNodes().setRelativeWorkingDirectory(EnterpriseJobConfig.getJumbuneHome() + Constants.SYSTEM_STATS_DIR);
		Workers workers = cluster.getWorkers();
		workers.setRelativeWorkingDirectory(workers.getWorkDirectory());
		copyRemoteStats(jobOutput, collector, cluster);
	}

	private JobOutput getJobOutput(String jobID) throws IOException {
		JobOutput jobOutput;
		HadoopLogParser hadoopLogParser = new HadoopLogParser();
		jobOutput = hadoopLogParser.getJobDetails( super.getJumbuneRequest(), jobID);
		return jobOutput;
	}

	private String fireTopOnSlaves(ResourceUsageCollector collector) {
		String receiveDir = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME);
		collector.fireTopCmdOnSlaves(receiveDir);
		return receiveDir;
	}

	private void copyRemoteStats(JobOutput jobOutput, ResourceUsageCollector collector, 
			Cluster cluster)throws JumbuneException, IOException, InterruptedException {
		
		RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
		LOGGER.debug("Copying remote stats file");
		remoteFileUtil.copyRemoteSysStatsFiles(cluster);
		collector.addPhaseResourceUsage(jobOutput);
	}


	private void checkJobClassesInJar(List<String> jarJobList)
			throws JumbuneException {
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) super.getJumbuneRequest().getConfig();
		if (!enterpriseJobConfig.isMainClassDefinedInJobJar() && (jarJobList != null && !processHelper.validateJobs(enterpriseJobConfig.getJobs(), jarJobList))) {
		
				throw new JumbuneException(ErrorCodesAndMessages.MESSAGE_JOBS_NOT_MATCH);
		
		}
	}

	private void executeForCommandBased(Scanner scanner) throws JumbuneException {
		if (super.isCommandBased()) {
			Config config = super.getJumbuneRequest().getConfig();
			EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) config;
			ExecutionUtil.showDefinedJobs(enterpriseJobConfig.getJobs());
			boolean jsonModification = ExecutionUtil.askYesNoInfo(scanner, MESSAGES.get(MESSAGE_VALID_INPUT),

			MESSAGES.get(MESSAGE_EXECUTION_YAML_COMMAND));

			if (jsonModification) {
				UserInputUtil cbe = new UserInputUtil(config, scanner);
				cbe.getInfo();
			}
		}
	}

	private void populateProfilingAnalysisReport(JobOutput jobOutput) {
		Map<String, String> report = super.getReports().getReport(CommunityModule.PROFILING);
		ProfilerUtil profileUtil = new ProfilerUtil( super.getJumbuneRequest().getConfig());
		try {
			String profilingData = profileUtil.convertProfilingReportToJson(jobOutput);
			// populating profiling analysis report
			report.put(Constants.PURE_PROFILING, profilingData);
		} catch (Exception htfe) {
			Map<String, HashMap<String, String>> errorInfo = new HashMap<String, HashMap<String, String>>();
			Map<String, String> errordetail = new HashMap<String, String>();
			errordetail.put(PURE_PROFILING_EXCEPTION_DETAIL, htfe.toString());
			errorInfo.put(ERRORANDEXCEPTION, (HashMap<String, String>) errordetail);
			Gson gson = new GsonBuilder().setExclusionStrategies(new HeapAllocStackTraceExclStrat()).setPrettyPrinting().create();
			String jsonformat = gson.toJson(errorInfo);
			super.getReports().getReport(CommunityModule.PROFILING).put(Constants.PURE_PROFILING, jsonformat);
			LOGGER.error("Could not parse profiling information  !!! " + htfe.getMessage());
		} finally {
			// setting the profiling analyser report as complete
			super.getReports().setCompleted(CommunityModule.PROFILING);
		}
	}

	@Override
	protected Module getModuleName() {
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) super.getJumbuneRequest().getConfig();
		if (enterpriseJobConfig.isHadoopJobProfileEnabled()){
			return CommunityModule.PROFILING;
		}
		return null;
	}

	/**
	 * Get job Id from job counters map
	 * 
	 * @param jobsCounterMap
	 * @return
	 */
	private String getJobIdfromJobCountersMap(Map<String, Map<String, String>> jobsCounterMap) {
		String attemptName = jobsCounterMap.keySet().iterator().next();
		String[] strArr = attemptName.split(Constants.UNDERSCORE);
		StringBuffer sb = new StringBuffer();
		sb.append(strArr[1]).append(Constants.UNDERSCORE).append(strArr[2]).append(Constants.UNDERSCORE).append(strArr[ExecutionConstants.THREE]);
		return sb.toString();
	}
	
	/***
	 * @see BaseProcessor#postExecute(Map)
	 */
	@Override
	protected void postExecute(Map<Parameters, String> params) throws JumbuneException {
		log(params, "Post Execution phase of processors");
		// deletes jumbunestate.txt token file
		deleteTokenFile();
	}

	/**
	 * This method deletes the token file generated in start of servicing any request. So that any other request can be served
	 */
	private void deleteTokenFile() {
		String tokenFilePath = EnterpriseJobConfig.getJumbuneHome() + TEMP_DIR + TOKEN_FILE;
		LOGGER.debug("Since all the process are complete so deleting the token file kept at " + tokenFilePath);
		File fToken = new File(tokenFilePath);

		if (fToken.exists()) {
			fToken.delete();
			LOGGER.debug("Token files deleted !!!");
		}
	}
	
}
