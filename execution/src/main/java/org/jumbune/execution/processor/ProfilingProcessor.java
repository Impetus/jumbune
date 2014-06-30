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
import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.beans.ReportsBean.ReportName;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.HadoopLogParser;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.ResourceUsageCollector;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.instrumenter.Instrumenter;
import org.jumbune.debugger.instrumentation.instrumenter.PureJarInstrumenter;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.traverse.JarTraversal;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.UserInputUtil;
import org.jumbune.profiling.utils.HeapAllocStackTraceExclStrat;
import org.jumbune.profiling.utils.ProfilerBean;
import org.jumbune.profiling.utils.ProfilerUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	protected boolean execute(Map<Parameters, String> params) throws JumbuneException {
		LOGGER.info("Executing [Profilng] Processor..");
		Scanner scanner = new Scanner(System.in);
		JarTraversal tra = new JarTraversal();
		Gson json = new Gson();
		List<String> jarJobList = null;
		// if main class is not defined in jar manifest, then only traverse the
		// jar for main classes
		if (!super.getLoader().isMainClassDefinedInJobJar()) {
			try {
				jarJobList = tra.getAlljobs(super.getLoader().getInputFile());
			} catch (IOException ioe) {
				LOGGER.error("Not able to get jobs list from jar.", ioe);
				return false;
			}
		}

		executeForCommandBased(scanner);

		// if main class is not defined in jar manifest, matching the yaml jobs
		// classes with main classes in jar
		checkJobClassesInJar(jarJobList);

		String pureJarPath = super.getLoader().getInputFile();

		// Generate profiling information . For this first instrument the jar to enable
		// profiling option on job basis. Then reset the pureJarPath to the profiled Jar path
		Instrumenter profilingInstrumenter = new PureJarInstrumenter(super.getLoader());
		JobOutput jobOutput = null;
		try {
			profilingInstrumenter.instrumentJar();
		} catch (IOException e) {
			LOGGER.error("Not able to instrument jar.", e);
			return false;
		}
		pureJarPath = super.getLoader().getProfiledOutputFile();
				LOGGER.debug("Pure MapReduce jar path :" + pureJarPath);
		String pureJarCounterJson = null;
		try {

			ResourceUsageCollector collector = new ResourceUsageCollector(super.getLoader());
			LOGGER.debug("Fired top command on Workers");
			String receiveDir = fireTopOnSlaves(collector);
			persistYamlInfoForShutdownHook(super.getLoader(), super.getLoader().getjHome());
			
			Map<String, Map<String, String>> jobsCounterMap = HELPER.executeJar(pureJarPath, super.isCommandBased(), super.getLoader(), false);
			pureJarCounterJson = json.toJson(jobsCounterMap);
			String jobID = getJobIdfromJobCountersMap(jobsCounterMap);
			collector.shutTopCmdOnSlaves(receiveDir);
			LOGGER.debug("Stopped top command on Workers");
			jobOutput = getJobOutput(jobID);
			LOGGER.info("Received Job Output");
			List<String> selectedHosts = collector.getNodesForJob(jobOutput);
			collector.processTopDumpFile(receiveDir, selectedHosts);
			calcStatsFromLogConsolidationInfo(jobOutput, collector);
			// populating data validation report
			populateReport(pureJarCounterJson);
			LOGGER.debug("Profiling final JSON ["+jobOutput+"]");
		} catch (IOException e) {
			LOGGER.error("Error while executing pure jar.", e);
			return false;
		} catch (Exception ex) {
			LOGGER.error("Error while processing per phase stats."+ex.getMessage());
			return false;
		} finally {
			LOGGER.info("Pure Job Execution Completed");
		}

		if (super.getLoader().isHadoopJobProfileEnabled()) {
			populateProfilingAnalysisReport(jobOutput, pureJarCounterJson);
		}
		LOGGER.info("Exited from [Profiling] processor...");
		return true;
	}

	private void calcStatsFromLogConsolidationInfo(JobOutput jobOutput,
			ResourceUsageCollector collector) throws JumbuneException, IOException,
			InterruptedException {
		LogConsolidationInfo lci = super.getLoader().getSysResourceFileConsolidation();
		copyRemoteStats(jobOutput, collector, lci);
	}

	private JobOutput getJobOutput(String jobID) throws IOException {
		JobOutput jobOutput;
		HadoopLogParser hlp = new HadoopLogParser();
		jobOutput = hlp.getJobDetails(super.getLoader(), jobID);
		return jobOutput;
	}

	private String fireTopOnSlaves(ResourceUsageCollector collector) {
		String receiveDir = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME);
		collector.fireTopCmdOnSlaves(receiveDir);
		return receiveDir;
	}

	private void copyRemoteStats(JobOutput jobOutput,
			ResourceUsageCollector collector, LogConsolidationInfo lci)
			throws JumbuneException, IOException, InterruptedException {
		RemoteFileUtil cu = new RemoteFileUtil();
		LOGGER.debug("Copying remote stats file");
		cu.copyRemoteSysStatsFiles(lci);
		collector.addPhaseResourceUsage(jobOutput);
	}

	private void populateReport(String pureJarCounterJson) {
		Map<ReportName, String> pureJarReport = super.getReports().getReport(Module.PURE_AND_INSTRUMENTED);
		pureJarReport.put(ReportName.PURE_JAR_COUNTER, pureJarCounterJson);
	}

	private void checkJobClassesInJar(List<String> jarJobList)
			throws JumbuneException {
		if (!super.getLoader().isMainClassDefinedInJobJar() && (jarJobList != null && !HELPER.validateJobs(super.getLoader().getJobDefinitionList(), jarJobList))) {
		
				throw new JumbuneException(ErrorCodesAndMessages.MESSAGE_JOBS_NOT_MATCH);
		
		}
	}

	private void executeForCommandBased(Scanner scanner) throws JumbuneException {
		if (super.isCommandBased()) {

			ExecutionUtil.showDefinedJobs(super.getLoader().getJobDefinitionList());
			boolean yamlModification = ExecutionUtil.askYesNoInfo(scanner, MESSAGES.get(MESSAGE_VALID_INPUT),

			MESSAGES.get(MESSAGE_EXECUTION_YAML_COMMAND));

			if (yamlModification) {
				UserInputUtil cbe = new UserInputUtil(super.getLoader(), scanner);
				cbe.getInfo();
			}
		}
	}

	private void populateProfilingAnalysisReport(JobOutput jobOutput,
			String pureJarCounterJson) {
		Map<ReportName, String> report = super.getReports().getReport(Module.PROFILING);
		if (pureJarCounterJson.contains(ERRORANDEXCEPTION)) {
			report.put(ReportName.PURE_PROFILING, pureJarCounterJson);
			// setting the profiling analyser report as complete
			super.getReports().setCompleted(Module.PROFILING);
		} else {
			ProfilerUtil profileUtil = new ProfilerUtil(super.getLoader());
			try {
				Map<String, ProfilerBean> profilerInfoMap = profileUtil.parseProfilingInfo(super.getLoader().getLogDefinition().getLogSummaryLocation()
						.getProfilingFilesLocation());

				String profilingData = profileUtil.convertProfilingReportToJson(profilerInfoMap, jobOutput);
				// populating profiling analysis report
				report.put(ReportName.PURE_PROFILING, profilingData);
			} catch (Exception htfe) {
				Map<String, HashMap<String, String>> errorInfo = new HashMap<String, HashMap<String, String>>();
				Map<String, String> errordetail = new HashMap<String, String>();
				errordetail.put(PURE_PROFILING_EXCEPTION_DETAIL, htfe.toString());
				errorInfo.put(ERRORANDEXCEPTION, (HashMap<String, String>) errordetail);

				Gson gson = new GsonBuilder().setExclusionStrategies(new HeapAllocStackTraceExclStrat()).setPrettyPrinting().create();
				String jsonformat = gson.toJson(errorInfo);
				super.getReports().getReport(Module.PROFILING).put(ReportName.PURE_PROFILING, jsonformat);
				LOGGER.error("Could not parse profiling information  !!! " + htfe.getMessage());
			} finally {
				// setting the profiling analyser report as complete
				super.getReports().setCompleted(Module.PROFILING);
			}
		}
	}

	@Override
	protected void updateServiceInfo(ServiceInfo serviceInfo) throws JumbuneException {
		if (serviceInfo != null) {

			serviceInfo.setPureJarCounterLocation(super.getLoader().getLogDefinition().getLogSummaryLocation().getPureJarCounterLocation());

			if (super.getLoader().isHadoopJobProfileEnabled()) {
				serviceInfo.setPureJarProfilingCountersLocation(super.getLoader().getLogDefinition().getLogSummaryLocation()
						.getPureJarProfilingCountersLocation());
			}
		}
	}

	@Override
	protected Module getModuleName() {
		return (super.getLoader().isHadoopJobProfileEnabled()) ? Module.PROFILING : Module.PURE_AND_INSTRUMENTED;
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

		if (serviceInfo != null) {
			boolean status = HELPER.writetoServiceFile(serviceInfo);

			if (status) {
				log(params, "Service Info written successfully");
			} else {
				log(params, "Error occured while writing service info");
			}
		}
		// deletes jumbunestate.txt token file
		deleteTokenFile();
	}

	/**
	 * This method deletes the token file generated in start of servicing any request. So that any other request can be served
	 */
	private void deleteTokenFile() {
		String tokenFilePath = YamlLoader.getjHome() + TEMP_DIR + TOKEN_FILE;
		LOGGER.debug("Since all the process are complete so deleting the token file kept at " + tokenFilePath);
		File fToken = new File(tokenFilePath);

		if (fToken.exists()) {
			fToken.delete();
			LOGGER.debug("Token files deleted !!!");
		}
	}
	
	}
