package org.jumbune.execution.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.DataQualityTimeLineConfig;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.Feature;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.dsc.DataSourceCompValidationInfo;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;
import org.jumbune.datavalidation.DataValidationReport;
import org.jumbune.datavalidation.json.JsonViolationReport;
import org.jumbune.datavalidation.report.DVReportGenerator;
import org.jumbune.datavalidation.report.XmlDVReportGenerator;
import org.jumbune.datavalidation.xml.XmlDataValidationReport;
import org.jumbune.datavalidation.report.JsonDVReportGenerator;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.DataQualityTaskEnum;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.utils.ExecutionConstants;

/**
 * 
 * This processor can be used for performing data validation
 * 
 * 
 */
public class DataQualityProcessor extends BaseProcessor {

	private static final String USER_DATE_PATTERN = "yyyy/MM/dd HH:mm";

	private static final Logger LOGGER = LogManager.getLogger(DataQualityProcessor.class);

	DataQualityTaskEnum dataQualityTaskEnum;

	private String mapTaskAttemptPrefix = "task_";

	/**
	 * constructor for DataValidationProcessor
	 * 
	 * @param isCommandBased
	 * @param dataQualityTaskEnum
	 */
	public DataQualityProcessor(boolean isCommandBased, DataQualityTaskEnum dataQualityTaskEnum) {
		super(isCommandBased);
		this.dataQualityTaskEnum = dataQualityTaskEnum;
	}

	@Override
	protected boolean execute(Map<Parameters, String> params) throws JumbuneException {
		LOGGER.info("Executing [Data Quality] Processor...");

		JumbuneRequest jumbuneRequest = super.getJumbuneRequest();
		if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_SOURCE_COMPARISON)) {
			return generateDataSourceComparisonReport(params, jumbuneRequest);
		} else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_VALIDATION)) {
			return generateDataValidationReport(params, jumbuneRequest);
		} else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_PROFILING)) {
			return generateDataProfilingReport(params, jumbuneRequest.getJobConfig());
		} else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_QUALITY_TIMELINE)) {
			return generateDataQualityTimelineReport(params, jumbuneRequest);
		}else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.XML_DATA_VALIDATION)){
			return generateXmlDataValidationReport(params, jumbuneRequest);
		}else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.JSON_DATA_VALIDATION)){
			return generateJsonDataValidationReport(params, jumbuneRequest);
		}else if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_CLEANSING)){
			return generateDataCleansingReport(params, jumbuneRequest);
		}
		return true;
	}

	/**
	 * Generate json data validation report.
	 *
	 * @param params the params
	 * @param jumbuneRequest the jumbune request
	 * @return true, if successful
	 * @throws JumbuneException the jumbune exception
	 */
	private boolean generateJsonDataValidationReport (Map<Parameters, String> params, JumbuneRequest jumbuneRequest)throws JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		JobConfigUtil.setRelativeWorkingDirectoryForJsonDV(jumbuneRequest);
		String slaveDVLocation = cluster.getWorkers().getRelativeWorkingDirectory();
		String dvReport = null;
		Gson gsonDV = new Gson();
		String inputPath = jobConfig.getHdfsInputPath();
		String additionalParameters = jobConfig.getParameters();
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try{
			DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
			String dvFileDir = slaveDVLocation.substring(0, slaveDVLocation.lastIndexOf('/') + 1);
			Date launchTime = new Date();
			dvReport =processHelper.remoteJsonValidateData(jumbuneRequest, inputPath, dvFileDir, additionalParameters);
			dvReport = new JsonDVReportGenerator().generateDataValidationReport(dvReport);
			if (!cluster.getWorkers().getHosts().isEmpty()) {
				LOGGER.debug("Copying files from all nodes!!!");
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyLogFilesToMaster(cluster);
			}
			dvReport = setCleanTuplesInDVSummary(dvReport, jumbuneRequest, Constants.CONSOLIDATED_JSON_DV_LOC);
			dvReport = dqts.generateDataQualityReport(dvReport, jobConfig, isJsonEmptyReport(dvReport), launchTime);
			LOGGER.info("Successfully Exiting [Json-Data Validation] Processor...dvReport - " + dvReport);
			return true;			
		}catch(Exception e){
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not validate Json data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Json Data Validation" + e);
		}finally{
			report.put(Constants.DATA_VALIDATION, dvReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}
	
	private boolean generateDataCleansingReport (Map<Parameters, String> params, JumbuneRequest jumbuneRequest)throws JumbuneException {
		Gson gsonDV = new Gson();
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		String inputPath = jobConfig.getHdfsInputPath();
		DataValidationBean dataValidationBean = jobConfig.getDataQualityTimeLineConfig().getDataValidation();
		String fieldSeparator = dataValidationBean.getFieldSeparator();
		fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE, Constants.SPACE_SEPARATOR);
		dataValidationBean.setFieldSeparator(fieldSeparator);
		final String dvBeanString = gsonDV.toJson(dataValidationBean);
		String additionalParameters = jobConfig.getParameters();		
		String dvReport = null;
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try {			
			dvReport =	processHelper.launchAndGetDataCleansingResult(jumbuneRequest, inputPath, dvBeanString, additionalParameters);			
			return true;
		} catch (IOException e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not clean input data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Json Data Cleansing" + e);
		}finally{
			report.put(Constants.DATA_CLEANSING, dvReport);		
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);			
		}		
	}
	
	private boolean generateDataSourceComparisonReport(Map<Parameters, String> params, JumbuneRequest jumbuneRequest) throws JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		JobConfigUtil.setRelativeWorkingDirectoryForDV(jumbuneRequest);
		DataSourceCompValidationInfo dscvi = jobConfig.getDataSourceCompValidationInfo();
		dscvi.setSlaveFileLoc(cluster.getWorkers().getRelativeWorkingDirectory());
		dscvi.setJobName(jobConfig.getJumbuneJobName());
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		String additionalParameters = jobConfig.getParameters();
		Gson gson = new Gson();
		String dvReport = null;
		try {
			String dscBean = gson.toJson(dscvi);
			dvReport = processHelper.remoteValidateDataDataSourceComparison(jumbuneRequest, dscBean, additionalParameters);
			if (!cluster.getWorkers().getHosts().isEmpty()) {
				LOGGER.debug("Copying files from all nodes!!!");
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyLogFilesToMaster(cluster);
			}
			LOGGER.info("Successfully Exiting [Data Quality-Data Validation] Processor...dvReport - " + dvReport);
			return true;
		} catch(Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not validate data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Validation" + e);
		} finally {
			report.put(Constants.DATA_VALIDATION, dvReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}
	

	/**
	 * Generate data validation report.
	 *
	 * @param params the params
	 * @param jumbuneRequest the jumbune request
	 * @return true, if successful
	 * @throws JumbuneException the jumbune exception
	 */
	private boolean generateDataValidationReport(Map<Parameters, String> params, JumbuneRequest jumbuneRequest)
			throws JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		JobConfigUtil.setRelativeWorkingDirectoryForDV(jumbuneRequest);
		String slaveDVLocation = cluster.getWorkers().getRelativeWorkingDirectory();
		String dvReport = null;
		Gson gsonDV = new Gson();
		String inputPath = jobConfig.getHdfsInputPath();
		String additionalParameters = jobConfig.getParameters();
		// populating data validation report
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try {

			DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
			DataValidationBean dataValidationBean = jobConfig.getDataQualityTimeLineConfig().getDataValidation();
			String fieldSeparator = dataValidationBean.getFieldSeparator();
			fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE, Constants.SPACE_SEPARATOR);
			dataValidationBean.setFieldSeparator(fieldSeparator);
			final String dvBeanString = gsonDV.toJson(dataValidationBean);
			String dvFileDir = slaveDVLocation.substring(0, slaveDVLocation.lastIndexOf('/') + 1);
			// Added time in DataQualityTimelineConfig while generating
			// datavalidation report to get the count of clean
			// and total no of processed tuple.
			Date launchTime = new Date();
			dvReport = processHelper.remoteValidateData(super.getJumbuneRequest(), inputPath, dvFileDir, dvBeanString, additionalParameters);
			dvReport = new DVReportGenerator().generateDataValidationReport(dvReport);
			// Copy logs from slaves only when there are slaves if there are no
			// slaves then don't go for copying
			if (!cluster.getWorkers().getHosts().isEmpty()) {
				LOGGER.debug("Copying files from all nodes!!!");
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyLogFilesToMasterForDV(cluster, jumbuneRequest);
			}
			dvReport = setCleanTuplesInDVSummary(dvReport, jumbuneRequest, Constants.CONSOLIDATED_DV_LOC);
			dvReport = dqts.generateDataQualityReport(dvReport, jobConfig, isEmptyReport(dvReport), launchTime);

			LOGGER.info("Successfully Exiting [Data Quality-Data Validation] Processor...dvReport - " + dvReport);
			return true;
		} catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not validate data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Validation" + e);
		} finally {
			report.put(Constants.DATA_VALIDATION, dvReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}
	
	/**
	 * Generate xml data validation report.
	 *
	 * @param params the params
	 * @param jumbuneRequest the jumbune request
	 * @return true, if successful
	 * @throws JumbuneException the jumbune exception
	 */
	private boolean generateXmlDataValidationReport(Map<Parameters, String> params, JumbuneRequest jumbuneRequest)
			throws JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		JobConfigUtil.setRelativeWorkingDirectoryForXmlDV(jumbuneRequest);
		String slaveDVLocation = cluster.getWorkers().getRelativeWorkingDirectory();
		String dvReport = null;
		String inputPath = jobConfig.getHdfsInputPath();
		// populating data validation report
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try {

			DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
			String dvFileDir = slaveDVLocation.substring(0, slaveDVLocation.lastIndexOf('/') + 1);
			Date launchTime = new Date();
			dvReport = processHelper.remoteXmlValidateData(super.getJumbuneRequest(), inputPath, dvFileDir);
			dvReport = new XmlDVReportGenerator().generateDataValidationReport(dvReport);
			// Copy logs from slaves only when there are slaves if there are no
			// slaves then don't go for copying
			if (!cluster.getWorkers().getHosts().isEmpty()) {
				LOGGER.debug("Copying files from all nodes!!!");
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyLogFilesToMaster(cluster);
			}
			dvReport = setCleanTuplesInDVSummary(dvReport, jumbuneRequest, Constants.CONSOLIDATED_XML_DV_LOC);
			dvReport = dqts.generateDataQualityReport(dvReport, jobConfig, isXmlEmptyReport(dvReport), launchTime);

			LOGGER.info("Successfully Exiting [Data Quality-Data Validation] Processor...dvReport - " + dvReport);
			return true;
		} catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not validate XML data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during XML Data Validation" + e);
		} finally {
			report.put(Constants.DATA_VALIDATION, dvReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}

	

	/**
	 * Sets the clean and dirty tuples in dv summary.
	 *
	 * @param dvReport the dv report
	 * @param jumbuneRequest the jumbune request
	 * @param dvName the dv name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String setCleanTuplesInDVSummary(String dvReport, JumbuneRequest jumbuneRequest, String dvName) throws IOException {
		JsonObject reportObject = new JsonParser().parse(dvReport).getAsJsonObject();
		JsonObject dvSummaryObject = reportObject.get("DVSUMMARY").getAsJsonObject();

		String dirPath = EnterpriseJobConfig.getJumbuneHome() + File.separator + Constants.JOB_JARS_LOC
				+ jumbuneRequest.getJobConfig().getJumbuneJobName() + File.separator + dvName
				+ "tuple" + File.separator;

		long totalTuples = 0;
		long cleanTuples = 0;
		long dirtyTuples = 0;

		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		for (File file : files) {
			try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()),
					StandardCharsets.UTF_8)) {
				if (file.getName().startsWith(mapTaskAttemptPrefix)) {
					totalTuples += Long.parseLong(br.readLine());
					cleanTuples += Long.parseLong(br.readLine());
				}

			}
		}
		dirtyTuples = totalTuples - cleanTuples;

		Gson gson = new Gson();
		dvSummaryObject.add("dirtyTuples", gson.toJsonTree(dirtyTuples, Long.class));
		dvSummaryObject.add("cleanTuples", gson.toJsonTree(cleanTuples, Long.class));

		return reportObject.toString();
	}

	/**
	 * Generate data profiling report.
	 *
	 * @param params the params
	 * @param jobConfig the job config
	 * @return true, if successful
	 * @throws JumbuneException the jumbune exception
	 */
	private boolean generateDataProfilingReport(Map<Parameters, String> params, JobConfig jobConfig)
			throws JumbuneException {
		String dataProfilingReport = null;
		Gson gsonDV = new Gson();
		String inputPath = jobConfig.getHdfsInputPath();
		String additionalParameters = jobConfig.getParameters();
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try {
			DataProfilingBean dataProfilingBean = jobConfig.getDataProfilingBean();
			if (dataProfilingBean.getNumOfFields() != 0) {
				jobConfig.setCriteriaBasedDataProfiling(Enable.TRUE);
			}
			String dataProfilingBeanString = gsonDV.toJson(dataProfilingBean);
			dataProfilingReport = processHelper.launchDataProfilingJobAndProcessOutput(super.getJumbuneRequest(),
					inputPath, dataProfilingBeanString, dataProfilingBean, additionalParameters);
			LOGGER.debug("Successfully Exiting [Data Quality-Data Profiling] Processor...");
			return true;
		} catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not profile data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dataProfilingReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Profiling" + e);
		} finally {
			report.put(DataProfilingConstants.DATA_PROFILING, dataProfilingReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}

	/**
	 * Generate data quality timeline report.
	 *
	 * @param params the params
	 * @param jumbuneRequest the jumbune request
	 * @return true, if successful
	 * @throws JumbuneException the jumbune exception
	 */
	private boolean generateDataQualityTimelineReport(Map<Parameters, String> params, JumbuneRequest jumbuneRequest)
			throws JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();

		String inputPath = jobConfig.getHdfsInputPath();
		String dataQualityTimelineReport = null;
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		DataValidationBean dataValidationBean = jobConfig.getDataQualityTimeLineConfig().getDataValidation();
		DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
		DataQualityTimeLineConfig dataQualityTimeline = jobConfig.getDataQualityTimeLineConfig();
		String additionalParameters = jobConfig.getParameters();
		Gson gsonDV = new Gson();
		String dvReport;
		try {
			if (JobConfigUtil.isEnable(dataQualityTimeline.getShowJobResult())) {
				dataQualityTimelineReport = dqts.getDataQualityTimeLineReport(jobConfig, jobConfig.getJumbuneJobName());
			} else {
				if (!dqts.isJobAlreadyScheduled(jobConfig)) {
					dqts.scheduleJob(jobConfig);
					LOGGER.debug("Executing scheduled job");
				}
				String fieldSeparator = dataValidationBean.getFieldSeparator();
				fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE, Constants.SPACE_SEPARATOR);
				dataValidationBean.setFieldSeparator(fieldSeparator);
				final String dvBeanString = gsonDV.toJson(dataValidationBean);
				JobConfigUtil.setRelativeWorkingDirectoryForDV(jumbuneRequest);
				Cluster cluster = jumbuneRequest.getCluster();
				Workers workers = cluster.getWorkers();
				String slaveDVLocation = workers.getRelativeWorkingDirectory();
				String dvFileDir = slaveDVLocation.substring(0, slaveDVLocation.lastIndexOf('/') + 1);
				Date launchTime = new Date();
				dvReport = processHelper.remoteValidateData(super.getJumbuneRequest(), inputPath, dvFileDir,
						dvBeanString, additionalParameters);
				// Copy logs from slaves only when there are slaves if there are
				// no
				// slaves then don't go for copying
				if (!workers.getHosts().isEmpty()) {
					RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
					remoteFileUtil.copyLogFilesToMaster(cluster);
				}
				dataQualityTimelineReport = dqts.generateDataQualityReport(dvReport, jobConfig,
						isEmptyReport(dvReport), launchTime);
				dqts.persistDataQualityReport(dqts.getScheduledJobLocation(jobConfig), dqts, jobConfig,
						dataQualityTimelineReport);

				dataQualityTimeline.setShowJobResult(Enable.TRUE);
				saveJsonToJumbuneHome(jumbuneRequest.getConfig());

			}
			LOGGER.debug("Successfully Exiting [Data Quality- Data Quality Timeline] Processor...");
			return true;
		} catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not profile data", e.getMessage());
			errorMap.put(ExecutionConstants.ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dataQualityTimelineReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Quality Timeline" + e);
		} finally {
			File file = new File(JobConfig.getJumbuneHome() + File.separator + "jobJars" + File.separator
					+ jobConfig.getJumbuneJobName() + File.separator + "dv");
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
			report.put(Constants.DATA_QUALITY_TIMELINE, dataQualityTimelineReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}

	public void saveJsonToJumbuneHome(Config config) throws IOException {
		BufferedWriter bufferedWriter = null;
		try {
			EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) config;
			String jsonDir = System.getenv("JUMBUNE_HOME") + "/jsonrepo/";
			String fileName = enterpriseJobConfig.getJumbuneJobName() + ".json";

			Gson gson = new Gson();

			if (Enable.TRUE.equals(enterpriseJobConfig.getEnableDataQualityTimeline())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + "analyzeData" + File.separator + enterpriseJobConfig.getJumbuneJobName() + File.separator;
			}

			File jsonDirectory = new File(jsonDir);
			jsonDirectory.mkdirs();

			String jsonData = gson.toJson(enterpriseJobConfig, EnterpriseJobConfig.class);
			String JOB_REQUEST_JSON = "/request.json";
			File file = new File(jsonDir + JOB_REQUEST_JSON);
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(jsonData);
			LOGGER.debug("Persisted Wizard configuration to Json [" + jsonData + "]");
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		} finally {
			if (bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
		}
	}

	private boolean isEmptyReport(String dvReport) {
		Gson gson = new Gson();
		Map<String, DataValidationReport> dvr = null;
		Type type = new TypeToken<Map<String, DataValidationReport>>() {
		}.getType();
		dvr = gson.fromJson(dvReport, type);
		return dvr.isEmpty();
	}

	private boolean isJsonEmptyReport(String dvReport) {
		Gson gson = new Gson();
		Map<String, JsonViolationReport> dvr = null;
		Type type = new TypeToken<Map<String, JsonViolationReport>>() {
		}.getType();
		dvr = gson.fromJson(dvReport, type);
		return dvr.isEmpty();
	}
	
	private boolean isXmlEmptyReport(String dvReport) {
		Gson gson = new Gson();
		Map<String, XmlDataValidationReport> dvr = null;
		Type type = new TypeToken<Map<String, XmlDataValidationReport>>() {
		}.getType();
		dvr = gson.fromJson(dvReport, type);
		return dvr.isEmpty();
	}
	
	@Override
	protected Module getModuleName() {
		return CommunityModule.DATA_QUALITY;
	}

}
