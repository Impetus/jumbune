package org.jumbune.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.profiling.ProfilingParam;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;




/**
 * This class validates inputs from user and give messages if a field is not in correct format.
 */
public class ValidateInput1 {
	/** The suggestions. */
	private Map<String, Map<String,String>> suggestions = null;
	
	/** The failed validation. */
	private Map<String, Map<String,String>> failedValidation = null;
	
	/** The error messages. */
	private ErrorMessageLoader errorMessages = null;

	/** The j home. */
	private String jumbuneHome = null;
	
	/** The Constant REPORT_FROM_CLUSTER. */
	private static final String REPORT_FROM_CLUSTER = " dfsadmin -report | grep Name";
	
	private static final String HDFS_FILE_EXISTS = " fs -ls ";
	
	/** The Constant NEW_LINE. */
	private static final String NEW_LINE = "\n";
	
	/** Temp directory **/
	String TEMP_DIR = "tmp";
	
	/** Token file name ***/
	String TOKEN_FILE = "/jumbuneState.txt";
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ValidateInput1.class);

	private static final String DATA_VALIDATION_FIELD_LIST = "dataValidation.fieldValidationList[";
	
	private static final String MAPR_DATANODE_IP = "maprcli node list -columns ip | awk '{print $2}'";

	/**
	 * constructor for initialise data member.
	 */
	public ValidateInput1() {
		suggestions = new HashMap<String, Map<String,String>>();
		failedValidation = new HashMap<String, Map<String,String>>();
		errorMessages = ErrorMessageLoader.getInstance();
	}

	/**
	 * *
	 * it validates all field of yaml and return map which contains failhadooped and suggestion map.
	 *
	 * @param config object of yamlConfig class
	 * @return map containing failed and suggestion to given to user
	 */
	public Map<String, Map<String, Map<String, String>>> validateJson(
			JumbuneRequest jumbuneRequest) {
		
		Map<String, Map<String, Map<String,String>>> validateInput = new HashMap<String, Map<String, Map<String,String>>>();
		
		Config config = jumbuneRequest.getConfig();
		 JobConfig jobConfig = jumbuneRequest.getJobConfig();
		if (intialSettingsValidation(config)) {
			validateBasicField(jumbuneRequest);

			if (isEnable(jobConfig.getEnableDataValidation())) {
				validateDataValidation(jumbuneRequest);
			}

			if (isEnable(jobConfig.getDebugAnalysis())) {
				validateDebugField(config);
			}
			if(isEnable(jobConfig.getEnableStaticJobProfiling())){
				validateProfilingField(jumbuneRequest);
			}
			validateJarPath(jobConfig);
		
		}
		if (!failedValidation.isEmpty()) {
			validateInput.put(Constants.FAILURE_KEY, failedValidation);
		}
		if (!suggestions.isEmpty()) {
			validateInput.put(Constants.SUGGESSION_KEY, suggestions);
		}
		return validateInput;
	}

	/**
	 * This method checks whether the jar location given corresponds to a valid
	 * jar file or not.
	 * 
	 * @param jobConfig
	 */
	public void validateJarPath(JobConfig jobConfig) {
	
		//validation to be done only when flow debugging and job profiling (only "Run from jumbune" option) are enabled. 
		if(isEnable(jobConfig.getDebugAnalysis())||(isEnable(jobConfig.getEnableStaticJobProfiling())&&isEnable(jobConfig.getRunJobFromJumbune())))
    	{	    
		     String inputFile = jobConfig.getInputFile();
		     Map<String, String> failedCases = new HashMap<String, String>();
		     boolean isLocalSystemJar = isEnable(jobConfig.getIsLocalSystemJar());
		     if(inputFile!=null)
		     {   
		    	 boolean endsWithJar=inputFile.trim().endsWith(".jar");
		          if ((((!endsWithJar || ! new File(inputFile).exists())&&(!isLocalSystemJar))||(isLocalSystemJar&&!endsWithJar))) 
		          {
		    	 failedCases.put(inputFile,
		    			errorMessages.get(ErrorMessages.SUPPLIED_JAR_INVALID));
		     	 failedValidation.put("M/R-Jobs", failedCases);
		           }
		     }
		     else
		     {
		    	 failedCases.put(inputFile,
			    			errorMessages.get(ErrorMessages.BASIC_INPUT_PATH_EMPTY));
			     	failedValidation.put("M/R-Jobs", failedCases);
		     }
		}
	}
	
	
	private void validateProfilingField(
			JumbuneRequest jumbuneRequest) {
		
		Map<String,String> failedCases = new HashMap<String,String>();
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		if(Enable.FALSE.equals(jobConfig.getRunJobFromJumbune())){
			String existingJobId = jobConfig.getExistingJobName();
			if(!isNullOrEmpty(existingJobId)){		
				String response = RemotingUtil.fireCommandAsHadoopDistribution(cluster,  "job -status "+existingJobId, CommandType.HADOOP_JOB);				

				//checks for a completed hadoop job
				if(!response.contains("Counters:")){
					failedCases.put("profiling",errorMessages.get(ErrorMessages.EXISTING_JOB_INVALID));
					failedValidation.put(Constants.JOB_PROFILING, failedCases);
				}
			}else{
				failedCases.put("profiling",errorMessages.get(ErrorMessages.EXISTING_JOB_INVALID));
				failedValidation.put(Constants.JOB_PROFILING, failedCases);
			}
		}
	}

	/**
	 * This method checks initial settings ex hadoop home is set or not or jumbune home is set or not and check if yaml is not empty and check if a
	 * module is enabled or not.
	 * 
	 * @param config
	 *            the config
	 * @return true if all case passes successfully
	 */
	protected boolean intialSettingsValidation(Config config) {
		Map<String, String> listOfErrors = new HashMap<String, String>();
		boolean result = false;
		if (config != null) {
			jumbuneHome = JobConfig.getJumbuneHome();
			if (!isAtleastOneModuleEnabled(config)) {
				listOfErrors.put("NO_MODULE_SELECT", errorMessages.get(ErrorMessages.NO_MODULE_SELECT));
			} else {
				if (!checkNullEmptyAndMessage(listOfErrors, jumbuneHome, ErrorMessages.JUMBUNE_HOME_NOT_SET, "JUMBUNE_HOME")
						&& checkFileOrDirExist(listOfErrors, jumbuneHome, ErrorMessages.JUMBUNE_HOME_NOT_SET, "JUMBUNE_HOME")) {
					result = checkProfilerState(config, listOfErrors);
				}

			}
		} else {
			listOfErrors.put("INVALID_YAML", errorMessages.get(ErrorMessages.INVALID_JSON));
		}
		if (!listOfErrors.isEmpty()) {
			failedValidation.put(Constants.BASIC_VALIDATION, listOfErrors);
		}
		return result;
	}

	/***
	 * It makes sure that if profiling job is already triggered then jumbune will not accept another profiling job.
	 * 
	 * @param config
	 * @param listOfErrors
	 * @return
	 */
	private boolean checkProfilerState(Config config, Map<String, String> listOfErrors) {
		boolean result = true;
		JobConfig jobConfig = (JobConfig)config;
		if (isEnable(jobConfig.getEnableStaticJobProfiling())) {
			String tokenFilePath = jumbuneHome + TEMP_DIR + TOKEN_FILE;
			File fToken = new File(tokenFilePath);
			if (fToken.exists()) {
				result = false;
				listOfErrors.put("PROFILING", errorMessages.get(ErrorMessages.PROFILER_ALREADY_RUNNING));
			} else {
				result = true;
			}
		}
		return result;
	}

	/**
	 * *
	 * This method checks that is given enum Value is TRUE or FALSE.
	 *
	 * @param enable is a enum
	 * @return true if it is TRUE or false if It is false or null
	 */
	public static boolean isEnable(Enable enable) {
		return (enable != null && Enable.TRUE.equals(enable) ? true : false);

	}

	/**
	 * *
	 * this method validate the inputs of debugging in jumbune.In web UI this field is in Debug Analysis tab
	 *
	 * @param config the config
	 */
	private void validateDebugField(Config config) {
		Map<String,String> failedDebug = new HashMap<String,String>();
		Map<String,String> suggestionDebug = new HashMap<String,String>();
		/**
		if (loader.isInstrumentEnabled(Constants.DEBUG_IF_BLOCK) && (Constants.ZERO >= conf.getMaxIfBlockNestingLevel() || conf.getMaxIfBlockNestingLevel() >= Constants.DEBUG_MAX_IF)) {
				listofFailedDebug.add(errorMessages.get(ErrorMessages.DEB_IF_BLOCK));
		}
		
		if ((loader.isInstrumentEnabled(Constants.DEBUG_PARTITION_KEY))
				&& Constants.MIN_PARTITION_SAMPLE_INTER >= config.getPartitionerSampleInterval()) {

			listofFailedDebug.add(errorMessages.get(ErrorMessages.DEBUG_PARTITION_LEVEL_INVALID));
		}
		*/
		JobConfig jobConfig = (JobConfig)config;
		if(!ifDebuggerValidationsEnabled(config))
		{
			failedDebug.put("debuggerConf.logLevel.instrumentRegex", errorMessages.get(ErrorMessages.BOTH_DEBUGGER_VALIDATIONS_NULL));
		}
		
		if (jobConfig.isInstrumentEnabled(Constants.DEBUG_INSTR_REGEX_KEY)) {
			if (!jobConfig.getRegexValidations().isEmpty()) {
				checkFieldsValue(jobConfig.getRegexValidations(), ErrorMessages.DEBUG_REGEX_CLASS_INVALID, ErrorMessages.DEBUG_REGEX_KEY_INVALID,
						failedDebug,"regexValidations[");
			} else {
				failedDebug.put("debuggerConf.regexValidations",errorMessages.get(ErrorMessages.DEBUG_REGEX_VALIDATION_EMPTY));
			}

		}
		if (jobConfig.isInstrumentEnabled(Constants.DEBUG_INST_USER_KEY)) {
			if (!jobConfig.getUserValidations().isEmpty()) {
				checkFieldsValue(jobConfig.getUserValidations(), ErrorMessages.DEBUG_INST_REGEX_INVALID, ErrorMessages.DEBUG_INST_KEY_INVALID,
						failedDebug,"regexValidations[");
			} else {
				failedDebug.put("debuggerConf.userValidations",errorMessages.get(ErrorMessages.DEBUG_USERDEFINE_VALIDATION_EMPTY));
			}
		}

		addToValidationList(Constants.DEBUGGER_VALIDATION, failedDebug, suggestionDebug);
	}

/**   
 *  This method checks if one of the validations (User or Regex) are enabled while running debugger.
 * 
 * @param config
 * @param failedDebug
 * @return true if one of the validations (User or Regex) are enabled.
 */
	private boolean ifDebuggerValidationsEnabled(Config config)
	{  
		JobConfig jobConfig = (JobConfig)config;
		Map <String,LogLevel> logLevelMap =jobConfig.getDebuggerConf().getLogLevel();
		final String REGEXKEY="instrumentRegex";
		final String USERVALIDATEKEY="instrumentUserDefValidate";
		
		if((logLevelMap.containsKey(USERVALIDATEKEY)&&logLevelMap.get(USERVALIDATEKEY).toString().equalsIgnoreCase("TRUE"))||(logLevelMap.containsKey(REGEXKEY)&&logLevelMap.get(REGEXKEY).toString().equalsIgnoreCase("TRUE")))
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * *
	 * * In fields of debugger it validates regex userdefine validations.
	 *
	 * @param validations list of field which is to be validated
	 * @param classKey value in class field
	 * @param regexKey value of regex field
	 * @param failedValidation list of validation which is failed
	 */
	private void checkFieldsValue(List<Validation> validations, int classKey, int regexKey, Map<String,String> failedValidation,String fieldValue) {
		int count=0;
		for (Validation validation : validations) {
			if (isNullOrEmpty(validation.getClassname())) {
				failedValidation.put(fieldValue+count+"].classname",errorMessages.get(classKey));
			}
			if (isNullOrEmpty(validation.getValue()) && isNullOrEmpty(validation.getKey())) {
				failedValidation.put(fieldValue+count+"].value",errorMessages.get(regexKey));
			}
			count++;
		}

	}

	/**
	 * *
	 * if(config.getHadoopJobProfile()!=null && Enable.TRUE.equals(config.getHadoopJobProfile())) {
	 * 
	 * this mehod validate the data validation inputs from user .In web UI this field is in data validation tab
	 *
	 * @param config the config
	 */

	private void validateDataValidation(
			JumbuneRequest jumbuneRequest) {
		
		Map<String,String> failedDataValidation = new HashMap<String,String>();
		Map<String,String> listOfSuggestions = new HashMap<String,String>();
		JobConfig jobConfig =  jumbuneRequest.getJobConfig();
		if (jobConfig.getDataQualityTimeLineConfig().getDataValidation() != null) {
			DataValidationBean dataValidationBean = checkAndValidateHdfsPath(
					jumbuneRequest, failedDataValidation);

			
			checkIfFieldAndRecordSeparatorAreNull(failedDataValidation,
					dataValidationBean);
			int countForFieldValidation = 0;
			// TODO:  if fieldvalidation list is empty
			List<FieldValidationBean> bean = jobConfig.getDataQualityTimeLineConfig().getDataValidation().getFieldValidationList();
			if (bean != null) {
				for (FieldValidationBean fielValidationBean : bean) {
					if (fielValidationBean.getFieldNumber() >= Constants.ZERO) {
						countForFieldValidation = fielValidationBean.getFieldNumber() + 1;
						if (isNullOrEmpty(fielValidationBean.getNullCheck()) && isNullOrEmpty(fielValidationBean.getDataType())
								&& isNullOrEmpty(fielValidationBean.getRegex())) {
							listOfSuggestions
									.put(DATA_VALIDATION_FIELD_LIST+(countForFieldValidation-1)+"].regex",MessageFormat.format(errorMessages.get(ErrorMessages.DVALID_FIELD_NO_VAL), countForFieldValidation));
						} else {
							// check null check
							checkEmptyAndShowMessage(failedDataValidation, fielValidationBean.getNullCheck(), ErrorMessages.DVALID_NULL_CHECK,
									countForFieldValidation,DATA_VALIDATION_FIELD_LIST+(countForFieldValidation-1)+"].nullCheck");
							// check data type value
							checkEmptyAndShowMessage(listOfSuggestions, fielValidationBean.getDataType(), ErrorMessages.DVALID_DATA_TYPE,
									countForFieldValidation,DATA_VALIDATION_FIELD_LIST+(countForFieldValidation-1)+"].dataType");
							// check regex expression
							checkEmptyAndShowMessage(listOfSuggestions, fielValidationBean.getRegex(), ErrorMessages.DVALID_REGEX,
									countForFieldValidation,DATA_VALIDATION_FIELD_LIST+(countForFieldValidation-1)+"].regex");
						}
					}
				}
			} else {
				failedDataValidation.put("dataValidation.fieldValidationList",errorMessages.get(ErrorMessages.DV_NO_FIELD_TO_VARIFY));
			}

			
		} else {
			failedDataValidation.put("dataValidation",errorMessages.get(ErrorMessages.DATA_VALID_INVALID));
		}

		addToValidationList(Constants.DATA_VALIDATE, failedDataValidation, listOfSuggestions);
	}

	/**
	 * Check if field and record separator are null.
	 *
	 * @param listOfFailedValidation contains the list of failed validation.
	 * @param dataValidationBean contains all validation checks to be applied by the user.
	 */
	private void checkIfFieldAndRecordSeparatorAreNull(
			Map<String,String> listOfFailedValidation,
			DataValidationBean dataValidationBean) {
		if (dataValidationBean.getFieldSeparator() == null) {
			listOfFailedValidation.put("dataValidation.fieldSeparator",errorMessages.get(ErrorMessages.DVALID_FIELD_S_EMPTY));
		}
		

		if (dataValidationBean.getRecordSeparator() == null) {
			listOfFailedValidation.put("dataValidation.recordSeparator",errorMessages.get(ErrorMessages.DVALID_RECORD_S_EMPTY));
		}
	}

	/**
	 * Check and validate hdfs path.
	 *
	 * @param config bean for the yaml file
	 * @param listOfFailedValidation contains a list of failed validation in case of HDFS.
	 * @return all validation checks to be applied by the user
	 */
	private DataValidationBean checkAndValidateHdfsPath(
			JumbuneRequest jumbuneRequest, Map<String,String> listOfFailedValidation) {
		
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		DataValidationBean dataValidationBean = jobConfig.getDataQualityTimeLineConfig().getDataValidation();
		String hadoopInputPath = jobConfig.getHdfsInputPath();
		if (!checkNullEmptyAndMessage(listOfFailedValidation, hadoopInputPath, ErrorMessages.HDFS_FIELD_INVALID,"hdfsInputPath")) {
			try {
				if (!isHadoopInputPath(hadoopInputPath,  jumbuneRequest.getCluster(), jobConfig.getOperatingUser())) {
					listOfFailedValidation.put("hdfsInputPath",errorMessages.get(ErrorMessages.HADOOP_INPUT_PATH_INVALID));
				}
			} catch (JumbuneException e) {
				listOfFailedValidation.put("dataValidation",errorMessages.get(ErrorMessages.HADOOP_NOT_EXIST));
			}

		}
		return dataValidationBean;
	}

	/**
	 * *
	 * this method validate the job jar information .In web UI this field is in job tab
	 *
	 * @param config the config
	 */
	protected void validateJobs(Config config,Map<String,String> failedCases,Map<String,String> suggestion) {

		int countForJobJar = 0;
		JobConfig jobConfig = (JobConfig)config;
		// TODO:  - what if jobs are null!!
		if (!jobConfig.getJobs().isEmpty()) {
			for (JobDefinition jobDefinition : jobConfig.getJobs()) {

				countForJobJar++;
				if (!isEnable(jobConfig.getIncludeClassJar())) {
					checkEmptyAndShowMessage(failedCases, jobDefinition.getJobClass(), ErrorMessages.JOB_CLASS_NAME_INVALID, countForJobJar,"jobs["+countForJobJar+"].jobClass");
				}
				checkEmptyAndShowMessage(failedCases, jobDefinition.getName(), ErrorMessages.JOB_JAR_NAME_INVALID, countForJobJar,"jobs["+countForJobJar+"].name");

				checkEmptyAndShowMessage(suggestion, jobDefinition.getParameters(), ErrorMessages.JOB_PARAMETER_INVALID, countForJobJar,"jobs["+countForJobJar+".parameters");

			}
		} else {
			failedCases.put("jobs",errorMessages.get(ErrorMessages.JOBS_FIELD_ENPTY));
		}
	}


	/**
	 * *
	 * this method validates the very basic entries of jumbune. In web User Interface these entries in basic tab
	 *
	 * @param config the config
	 */
	protected void validateBasicField(JumbuneRequest jumbuneRequest) {
		Map<String,String> failedCases = new HashMap<String,String>();
		Map<String,String> suggestionList = new HashMap<String,String>();
		
		checkIfJumbuneJobEmptyOrNot(jumbuneRequest.getConfig(), failedCases,"jumbuneJobName");
		JobConfig jobConfig =  jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		
		if (isEnable(jobConfig.getDebugAnalysis()) || isEnable(jobConfig.getEnableStaticJobProfiling())) {
			/**
			 * check if slave jumbune home is empty
			 */

			checkNullEmptyAndMessage(failedCases, cluster.getWorkers().getWorkDirectory(), ErrorMessages.BASIC_SLAVE_HOME_EMPTY,"");
			/**
			 * check master host user name is nulll or conatain space
			 */
			checkMasterNodeValidation(failedCases, cluster);
			if (! cluster.getWorkers().getHosts().isEmpty() && !failedCases.containsValue(errorMessages.get(ErrorMessages.SSH_INVALID))) {
				validateSlaveField(failedCases, cluster);
			}
			checkMrJobField(jumbuneRequest.getConfig());

			
		}
		addToValidationList(Constants.BASIC_VALIDATION, failedCases, suggestionList);
	}

	/**
	 * Check master node validation.
	 *
	 * @param failedCases error list
	 * @param master the master
	 */
	private void checkMasterNodeValidation(
			Map<String,String> failedCases, Cluster cluster) {
		if (cluster != null) {
			checkNullEmptyAndMessage(failedCases, cluster.getHadoopUsers().getFsUser(), ErrorMessages.NAMENODE_HOST_USER,"master.user");

			/***
			 * master host validation
			 */
			checkIPAndShowMessage(failedCases, cluster.getNameNode(), ErrorMessages.MASTER_HOST_IP,"master.host");

			checkRsaDsaFileExistence(failedCases, cluster,"master.rsaFile");
		} else {
			failedCases.put("master.user",errorMessages.get(ErrorMessages.MASER_FIELD_INVALID));
		}
	}

	/**
	 * Check mr job field.
	 *
	 * @param config object of yamlConfig class
	 * @param failedCasesList error list
	 */
	protected void checkMrJobField(Config config) {
		Map<String,String> failedCases=new HashMap<String, String>();
		Map<String,String> suggetion=new HashMap<String, String>();
		JobConfig jobConfig = (JobConfig)config;
		if (!isEnable(jobConfig.getEnableStaticJobProfiling()) || jobConfig.getDebugAnalysis().getEnumValue()) {
			checkNullEmptyAndMessage(failedCases, jobConfig.getInputFile(), ErrorMessages.BASIC_INPUT_PATH_EMPTY,"inputFile");
			validateJobs(config,failedCases,suggetion);
		}
		addToValidationList(Constants.JOBS_VALIDATION,failedCases,suggetion );
	}

	/**
	 * Check rsa dsa file existence.
	 *
	 * @param failedCases error list
	 * @param master the master
	 * @param fieldValue
	 */
	private void checkRsaDsaFileExistence(
			Map<String,String> failedCases, Cluster cluster, String fieldValue) {
		/**
		 * RSA file and DSA file existence validation
		 */
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String sshAuthKeyFile = cluster.getAgents().getSshAuthKeysFile();

		StringBuilder lsSsh = new StringBuilder().append(Constants.LS_COMMAND).append(Constants.SPACE).append(sshAuthKeyFile);
		
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(lsSsh.toString(), false, null, CommandType.FS);
		String responseSsh = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		String sshResponse = responseSsh.substring(0, responseSsh.length()-1);
		remoter.close();
		
		if (!(sshAuthKeyFile != null && sshResponse.equalsIgnoreCase(sshAuthKeyFile))) {
			failedCases.put(fieldValue,errorMessages.get(ErrorMessages.SSH_INVALID));
		}
	}

	/**
	 * Check if jumbune job empty or not.
	 *
	 * @param config object of yamlConfig class
	 * @param failedCases error list
	 * @param fieldValue
	 */
	private void checkIfJumbuneJobEmptyOrNot(Config config,
			Map<String,String> failedCases,String fieldValue) {
		/**
		 * check if jumbune job name is empty or not
		 */
		JobConfig jobConfig = (JobConfig)config;
		if (!checkNullEmptyAndMessage(failedCases, jobConfig.getFormattedJumbuneJobName(), ErrorMessages.JUMBUNE_JOB_NAME_BLANK,fieldValue)) {
			String jumbuneJobDirectoryPath = jobConfig.getJobJarLoc() + jobConfig.getFormattedJumbuneJobName() + "/logs/";
			if (new File(jumbuneJobDirectoryPath).exists()) {
				failedCases.put(fieldValue,errorMessages.get(ErrorMessages.BASIC_JOB_NAME_EXIST));
			}
		}
	}

	/**
	 * This method validates Slave information in Basic field Tab in Web UI, and check slaves input is in correct format or not.
	 *
	 * @param failedCases error list
	 * @param Config the  config
	 */
	private void validateSlaveField(Map<String,String> failedCases, Cluster cluster) {
		int count = 0;
		List<String> listOfValidDataNode = new ArrayList<String>();
		
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);		
		if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR)){
			StringBuilder commandBuilder = new StringBuilder().append(MAPR_DATANODE_IP);
			Remoter remoter = RemotingUtil.getRemoter(cluster,"");
			CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder(cluster, null);
			commandWritableBuilder.addCommand(commandBuilder.toString(), false, null, CommandType.HADOOP_FS);
			String commandResponse = (String) remoter.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());
			LOGGER.debug("MAPR datanode command response :" +commandResponse);
			String[] getDataNodes = commandResponse.split(NEW_LINE);
			for(int i=1;i<getDataNodes.length;i++){
				listOfValidDataNode.add(getDataNodes[i]);
			}
		}else{
			String commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(cluster, REPORT_FROM_CLUSTER, CommandType.HADOOP_FS);
			String[] splitArray = commandResponse.split(NEW_LINE);
			for (String line : splitArray) {
				listOfValidDataNode.add(line.split(":")[1].trim());
			}
		}
		Workers workers = cluster.getWorkers();
		checkEmptyAndShowMessage(failedCases, workers.getUser(), ErrorMessages.WORKER_USER_INVALID, count,"slave["+count+"].user");
		List<String> hosts = workers.getHosts();
		for (String workerHost: hosts) {
			count++;
			
			if (workerHost != null) {
				int countForHost = 0;
				countForHost++;
				if (isNullOrEmpty(workerHost) || !(listOfValidDataNode.contains(workerHost))) {
					Integer[] numbers = new Integer[] { count, countForHost };
					failedCases.put("slaves["+(count-1)+"].hosts["+(countForHost-1)+"]",MessageFormat.format(errorMessages.get(ErrorMessages.WORKER_HOST_INVALID), numbers));
				}
			} else {
				failedCases.put("slaves.host",errorMessages.get(ErrorMessages.WORKER_HOST_FIELD_NULL));
			}

			}
		// Adding slave range for host

		if (Constants.ON.equalsIgnoreCase(cluster.getEnableHostRange())) {
			String fromIP[] = cluster.getHostRangeFromValue().split("\\.");
			String toIP[] = cluster.getHostRangeToValue().split("\\.");
			String hostRangeVal = fromIP[0] + Constants.DOT + fromIP[1] + Constants.DOT + fromIP[2];
			int fromRangeVal = Integer.parseInt(fromIP[fromIP.length - 1]);
			int toRangeVal = Integer.parseInt(toIP[toIP.length - 1]);
			if (toRangeVal < fromRangeVal) {
				failedCases.put("slaves.hostRangeToValue",errorMessages.get(ErrorMessages.SLAVE_HOST_RANGE_FROM_GREATER_THAN_TO));
			} else {
				addSlaveRanges(fromRangeVal, toRangeVal, hostRangeVal, hosts);
				workers.setHosts(hosts);
			}
	//		slave.setHosts(hosts.toArray(new String[hosts.size()]));
		}
	}

	

	
	/**
	 * *
	 * 
	 * Add the given list to map if its not empty.
	 *
	 * @param key the key
	 * @param failureList the failure list
	 * @param suggestionList the suggestion list
	 */
	protected void addToValidationList(String key, Map<String,String> failureList, Map<String,String> suggestionList) {
		if (!failureList.isEmpty()) {
			failedValidation.put(key, failureList);
		}
		if (!suggestionList.isEmpty()) {
			suggestions.put(key, suggestionList);
		}
	}

	

	/**
	 * This method check null or space and show message to user.
	 *
	 * @param errorList add error message to list
	 * @param value is string in which validation is applied
	 * @param errorCode code of error which is a integer
	 * @param fieldName for field 
	 * @return true, if successful
	 */
	protected boolean checkNullEmptyAndMessage(Map<String,String> errorList, String value, int errorCode,String fieldName) {
		if (isNullOrEmpty(value)) {
			errorList.put(fieldName,errorMessages.get(errorCode));
			return true;
		}
		return false;
	}

	/**
	 * This method check ip and show message to user.
	 *
	 * @param listOfError add error message to list
	 * @param value is string in which validation is applied
	 * @param errorCode code of error which is a integer
	 * @param filedValue
	 */
	private void checkIPAndShowMessage(Map<String,String> listOfError, final String value, int errorCode,String fieldValue) {
		if (isNullOrEmpty(value) || !checkIPAdress(value)) {
			listOfError.put(fieldValue,errorMessages.get(errorCode));
		}
	}

	/**
	 * This method check file existence and show message to user.
	 *
	 * @param listOfError add error message to list
	 * @param value is string in which validation is applied
	 * @param errorCode code of error which is a integer
	 * @return the boolean
	 */
	private Boolean checkFileOrDirExist(Map<String,String> listOfError, final String value, int errorCode,String fieldValue) {
		if (!isNullOrEmpty(value) && !new File(value).exists()) {
			listOfError.put(fieldValue,errorMessages.get(errorCode));
			return false;
		}
		return true;
	}

	/**
	 * This method check null or space and show message to user.
	 *
	 * @param listOfError add error message to list
	 * @param value is string in which validation is applied
	 * @param errorCode code of error which is a integer
	 * @param fieldIndex which is replace the value in message
	 */
	private void checkEmptyAndShowMessage(Map<String,String> listOfError, String value, int errorCode, int fieldIndex,String fieldValue) {
		if (isNullOrEmpty(value)) {
			listOfError.put(fieldValue,MessageFormat.format(errorMessages.get(errorCode), fieldIndex));
		}
	}

	/**
	 * check if a command is exist or not it returns true if it is exist import
	 * 
	 *
	 * @param value the value
	 * @param inputValue value which is to be tested
	 * @return true if command exist already
	 */
	public Boolean checkCommand(int value, String inputValue) {
		String[] commandArray = errorMessages.get(value).split("\\\n");
		for (String string : commandArray) {
			if (string.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is hadoop input path provided exists on HDFS or not.
	 *
	 * @param path the path
	 * @param config the config
	 * @return true, if is hadoop input path
	 * @throws JumbuneException the hTF exception
	 */
	public boolean isHadoopInputPath(
			String path, Cluster cluster, String operatingUser) throws JumbuneException {
		LOGGER.debug("Valdating HDFS Path :"+HDFS_FILE_EXISTS+path);
		String commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(cluster, HDFS_FILE_EXISTS + path, CommandType.USER, operatingUser);
		LOGGER.debug("HDFS Path ["+path+"] exist? Response :"+commandResponse);
		if(commandResponse!=null && !"".equals(commandResponse)){
			return true;
 		}
		return false;		
	}

	/**
	 * Checks if is atleast one module enabled.
	 *
	 * @param config the config
	 * @return true, if is atleast one module enabled
	 */
	protected boolean isAtleastOneModuleEnabled(Config config) {
		boolean result = false;
		JobConfig jobConfig = (JobConfig)config;
		boolean isProfiling = isProfilingModuleEnabled(config);
		if (isEnable(jobConfig.getEnableDataValidation()) || isEnable(jobConfig.getHadoopJobProfile()) || isEnable(jobConfig.getDebugAnalysis())
				|| isProfiling) {
			result = true;
		}
		return result;

	}

	/**
	 * Checks if  profiling module enabled.
	 *
	 * @param config the config
	 * @return true, if profiling module is enabled
	 */
	protected boolean isProfilingModuleEnabled(Config config) {
		boolean result = false;
		JobConfig jobConfig = (JobConfig)config;
		if (isEnable(jobConfig.getEnableStaticJobProfiling())) {
			result = true;
		}
		return result;

	}

	/**
	 * This method check port is available or not if avaliable it returns true.
	 *
	 * @param port is port number which is to be check
	 * @param inetAddress the inet address
	 * @return true if port is available
	 */
	public boolean isPortAvailable(int port, String inetAddress) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(inetAddress, port));
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				return false;
			}
		}
	}

	/**
	 * Check port availablity.
	 *
	 * @param config the config
	 * @param profilingParam the profiling param
	 * @return true, if successful
	 */
	public boolean checkPortAvailablity(Cluster cluster, ProfilingParam profilingParam) {
		for (String workerHost : cluster.getWorkers().getHosts()) {
			if (isPortAvailable(Integer.parseInt(profilingParam.getDataNodeJmxPort()), workerHost)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check port availablity task.
	 *
	 * @param config the config
	 * @param profilingParam the profiling param
	 * @return true, if successful
	 */
	public boolean checkPortAvailablityTask(Cluster cluster, ProfilingParam profilingParam) {
		for (String workerHost : cluster.getWorkers().getHosts()) {
				if (isPortAvailable(Integer.parseInt(profilingParam.getTaskTrackerJmxPort()), workerHost)) {
					return true;
			}
		}
		return false;
	}
	/**
	 * This method checks whether a given string is null or empty.
	 *
	 * @param str given string
	 * @return true if the given string is null or empty
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
	
		if (str.trim().length() == 0) {
			return true;
		}
	
		return false;
	}
	/**
	 * Adds the slave ranges.
	 *
	 * @param fromRangeVal the from range val
	 * @param toRangeVal the to range val
	 * @param hostRangeValue the host range value
	 * @param hosts the hosts
	 */
	public static void addSlaveRanges(int fromRangeVal, int toRangeVal,
			String hostRangeValue, List<String> hosts) {

		String hostRangeValFinal;
		for (int ipCount = fromRangeVal; ipCount <= toRangeVal; ipCount++) {
			hostRangeValFinal = hostRangeValue + Constants.DOT + ipCount;
			if (checkIPAdress(hostRangeValFinal)
					&& ! hosts.contains(hostRangeValFinal)) {
				hosts.add(hostRangeValFinal);
			}
		}

	}
	
	/**
	 * check whether given ip address is valid or not.
	 *
	 * @param ipaddress the ipaddress
	 * @return true if ip adress is valid
	 */
	public static Boolean checkIPAdress(String ipaddress) {
		try {
			return InetAddress.getByName(ipaddress).isReachable(Constants.FIVE_HUNDRED);
		} catch (Exception e) {
			return false;
		}
	}
	
	

}