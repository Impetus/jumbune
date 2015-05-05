package org.jumbune.execution.processor;

import static org.jumbune.execution.utils.ExecutionConstants.ERRORANDEXCEPTION;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataQualityTimeLineConfig;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.datavalidation.DataValidationReport;
import org.jumbune.datavalidation.report.DVReportGenerator;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.DataQualityTaskEnum;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * This processor can be used for performing data validation
 * 
 * 
 */
public class DataQualityProcessor extends BaseProcessor {
	
	private static final String USER_DATE_PATTERN = "HH:mm:SS MM/dd/yyyy";


	private static final Logger LOGGER = LogManager.getLogger(DataQualityProcessor.class);
	
	DataQualityTaskEnum dataQualityTaskEnum ;

	/**
	 * constructor for DataValidationProcessor
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
	
		JobConfig jobConfig = (JobConfig) super.getConfig();
		if (dataQualityTaskEnum.equals(DataQualityTaskEnum.DATA_VALIDATION)) {
			return generateDataValidationReport(params, jobConfig);
		} else if (dataQualityTaskEnum
				.equals(DataQualityTaskEnum.DATA_QUALITY_TIMELINE)) {
			return generateDataQualityTimelineReport(params, jobConfig);
		}
		return true;
	}


	private boolean generateDataValidationReport(
			Map<Parameters, String> params, JobConfig jobConfig) throws JumbuneException {
		String dvReport = null;
		Gson gsonDV = new Gson();
		String inputPath = jobConfig.getHdfsInputPath();
		// populating data validation report
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		try {
			// Added time in DataQualityTimelineConfig while generating datavalidation report to get the count of clean 
			// and total no of processed tuple.
			setTime(jobConfig);
			DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
			DataValidationBean dataValidationBean = jobConfig.getDataValidation();
			String fieldSeparator = dataValidationBean.getFieldSeparator();
			fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE, Constants.SPACE_SEPARATOR);
			dataValidationBean.setFieldSeparator(fieldSeparator);
			final String dvBeanString = gsonDV.toJson(dataValidationBean);
			String dvFileDir = jobConfig.getSlaveDVLocation().substring(0,
					jobConfig.getSlaveDVLocation().lastIndexOf('/') + 1);
			dvReport = processHelper.remoteValidateData(super.getConfig(), inputPath, dvFileDir, dvBeanString);
			dvReport = new DVReportGenerator().generateDataValidationReport(dvReport);

			// Copy logs from slaves only when there are slaves if there are no
			// slaves then don't go for copying
			if (jobConfig.getDVDefinition().getSlaves() != null && jobConfig.getDVDefinition().getSlaves().size() > 0) {
				LOGGER.debug("Copying files from all nodes!!!");
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyLogFilesToMaster(jobConfig.getDVDefinition());
			}
			dvReport = dqts.generateDataQualityReport(dvReport, jobConfig,isEmptyReport(dvReport));

			LOGGER.info("Successfully Exiting [Data Quality-Data Validation] Processor...");
			return true;
			} catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not validate data", e.getMessage());
			errorMap.put(ERRORANDEXCEPTION, errorMessageMap);

			Gson gson = new Gson();

			dvReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Validation"+e);
		} finally {
			report.put(Constants.DATA_VALIDATION, dvReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
		}
	}


	private void setTime(JobConfig jobConfig) {
		DateFormat dateFormat = new SimpleDateFormat(USER_DATE_PATTERN);
		Date date = new Date();
		DataQualityTimeLineConfig dqtc = new DataQualityTimeLineConfig();
		dqtc.setTime(dateFormat.format(date));
		jobConfig.setDataQualityTimeLineConfig(dqtc);	
	}


	private boolean generateDataProfilingReport(Map<Parameters, String> params,
			JobConfig jobConfig) throws JumbuneException {
		return false;
	}


	private boolean generateDataQualityTimelineReport(
			Map<Parameters, String> params, JobConfig jobConfig) throws JumbuneException {
		String inputPath = jobConfig.getHdfsInputPath();
		String dataQualityTimelineReport = null;
		LOGGER.info("Executing [Data Quality TimeLine ] Processor...");
		Map<String, String> report = super.getReports().getReport(CommunityModule.DATA_QUALITY);
		DataValidationBean dataValidationBean = jobConfig.getDataValidation();
		DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
		DataQualityTimeLineConfig dataQualityTimeline = jobConfig.getDataQualityTimeLineConfig();
		Gson gsonDV = new Gson();
		String dvReport;
		try{
			if (dataQualityTimeline.getShowJobResult()!=null && ValidateInput.isEnable(dataQualityTimeline.getShowJobResult())) {
				dataQualityTimelineReport = dqts.getDataQualityTimeLineReport(jobConfig,jobConfig.getDataQualityTimeLineConfig().getJobName());
			}else if(dataQualityTimeline.getRemoveJob()!=null && ValidateInput.isEnable(dataQualityTimeline.getRemoveJob())){
				dqts
				.deleteCurrentJobEntryFromCron(dataQualityTimeline
					.getJobName());
				dataQualityTimelineReport = "{\"Scheduled job ["+dataQualityTimeline.getJobName()+"] has been stopped \"}";
			}else {
				if(!dqts.isJobAlreadyScheduled(jobConfig)){
					dqts.scheduleJob(jobConfig);
					LOGGER.info("Executing scheduled job");
				}
				String fieldSeparator = dataValidationBean.getFieldSeparator();
				fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE, Constants.SPACE_SEPARATOR);
				dataValidationBean.setFieldSeparator(fieldSeparator);
				final String dvBeanString = gsonDV.toJson(dataValidationBean);
				String dvFileDir = jobConfig.getSlaveDVLocation().substring(0,
						jobConfig.getSlaveDVLocation().lastIndexOf('/') + 1);
				dvReport = processHelper.remoteValidateData(super.getConfig(), inputPath, dvFileDir, dvBeanString);
				// Copy logs from slaves only when there are slaves if there are no
				// slaves then don't go for copying
				if (jobConfig.getDVDefinition().getSlaves() != null && jobConfig.getDVDefinition().getSlaves().size() > 0) {
					RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
					remoteFileUtil.copyLogFilesToMaster(jobConfig.getDVDefinition());
				}
				dataQualityTimelineReport = dqts.generateDataQualityReport(dvReport, jobConfig,isEmptyReport(dvReport));
				dqts.persistDataQualityReport(dqts.getScheduledJobLocation(jobConfig),dqts,jobConfig,dataQualityTimelineReport);
			}
			LOGGER.info("Successfully Exiting [Data Quality- Data Quality Timeline] Processor...");
			return true;
		}catch (Exception e) {
			Map<String, Map<String, String>> errorMap = new HashMap<String, Map<String, String>>(1);
			Map<String, String> errorMessageMap = new HashMap<String, String>(1);
			errorMessageMap.put("Could not profile data", e.getMessage());
			errorMap.put(ERRORANDEXCEPTION, errorMessageMap);
			Gson gson = new Gson();
			dataQualityTimelineReport = gson.toJson(errorMap);
			throw new JumbuneException("Exception occured during Data Quality Timeline"+e);
		} finally {
			File file = new File(JobConfig.getJumbuneHome()+ File.separator+"jobJars"+File.separator+jobConfig.getJumbuneJobName()+File.separator+"dv");
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				LOGGER.error("unable to delete files and directories recursively", e);
			}
			report.put(Constants.DATA_QUALITY_TIMELINE, dataQualityTimelineReport);
			super.getReports().setCompleted(CommunityModule.DATA_QUALITY);
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

	@Override
	protected void updateServiceInfo(ServiceInfo serviceInfo) throws JumbuneException {
		if (serviceInfo != null){
			JobConfig jobConfig = (JobConfig)super.getConfig();
			serviceInfo.setDataValidationResultLocation(jobConfig.getDataValidationResultLocation());
		}
	}

	@Override
	protected Module getModuleName() {
		return CommunityModule.DATA_QUALITY;
	}

}
