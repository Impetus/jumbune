package org.jumbune.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.cluster.Agents;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.dsc.DataSourceCompValidationInfo;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ErrorMessageLoader;
import org.jumbune.common.utils.ErrorMessages;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;

import org.jumbune.common.job.EnterpriseJobConfig;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class ValidateInput {

	private static final String REGEX_SAPCE = " ";

	private static final String A = "a";

	private static final String JAVA_NET_CONNECT_EXCEPTION = "java.net.ConnectException";

	private static final String NAME = "Name";

	private static final String JOB_NAME = "jobName";

	private static final String HDFS_INPUT_PATH = "hdfsInputPath";

	private static final String JOB_ID = "jobID";

	private static final String CRON_EXPRESSION = "cronExpression";

	private static final String FILE_PATH = "filePath";

	/** The failed validation. */
	private Map<String, String> jobInputErrors = null;

	/** The error messages. */
	private ErrorMessageLoader errorMessages = null;

	/** The j home. */
	private String jumbuneHome = null;

	/** The Constant REPORT_FROM_CLUSTER. */
	private static final String REPORT_FROM_CLUSTER = " dfsadmin -report | grep Name";

	private static final String HDFS_FILE_EXISTS = " fs -du -s ";

	/** The Constant NEW_LINE. */
	private static final String NEW_LINE = "\n";

	/** Temp directory **/
	String TEMP_DIR = "tmp";

	/** Token file name ***/
	String TOKEN_FILE = "/jumbuneState.txt";

	private static final String MAPR_DATANODE_IP = "maprcli node list -columns ip | awk '{print $2}'";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ValidateInput.class);

	public ValidateInput() {
		errorMessages = ErrorMessageLoader.getInstance();

	}

	/**
	 * Setting the error message property file name(overriding the default)
	 */
	static {
		ErrorMessageLoader.setInputValidationMessageFile("validationmessages.error");
	}

	/**
	 * * it validates all field of json and return map which contains
	 * failhadooped and suggestion map.
	 *
	 * @param config
	 *            object of jobConfig class
	 * @return map containing failed and suggestion to given to user
	 */
	public Map<String, String> validateJobInputDetails(JumbuneRequest jumbuneRequest) {
		jobInputErrors = new HashMap<String, String>();
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) jumbuneRequest.getConfig();
		
	/*	if (isJobNameAlreadyExists(enterpriseJobConfig.getJumbuneJobName())) {
			jobInputErrors.put(JOB_NAME, errorMessages.get(ErrorMessages.BASIC_JOB_NAME_EXIST));
		}
*/		validateJarPath(enterpriseJobConfig);

		checkIfHdfsPathExists(jumbuneRequest);
		
		if (isEnable(enterpriseJobConfig.getIsDataSourceComparisonEnabled())) {
			checkDscSourceAndDestinationPath(jumbuneRequest);
		}

		return jobInputErrors;
	}

	public List<String> validateClusterDetails(Cluster cluster) {
		List<String> clusterErrors = new ArrayList<String>();

		if (!isIPAdressValid(cluster.getNameNode())) {
			clusterErrors.add(errorMessages.get(ErrorMessages.MASTER_HOST_IP));
		}
		if (!isAgentPrivateKeyOrPathValid(cluster)) {
			clusterErrors.add(errorMessages.get(ErrorMessages.SSH_INVALID));
		}
		validateWorkersField(cluster, clusterErrors);
		return clusterErrors;
	}

	public Map<String, String> checkJobNameAlreadyExists(String jobName) {
		jobInputErrors = new HashMap<String, String>(2);
		if (isJobNameAlreadyExists(jobName)) {
			jobInputErrors.put(JOB_NAME, errorMessages.get(ErrorMessages.BASIC_JOB_NAME_EXIST));
			jobInputErrors.put(Constants.STATUS, Constants.ERROR_);
		} else {
			jobInputErrors.put(Constants.STATUS, Constants.SUCCESS);
		}
		return jobInputErrors;
	}

	
	public boolean isJobNameAlreadyExists(String jobName) {
		String jumbuneHome = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME);
		StringBuilder jobJarPath = new StringBuilder(jumbuneHome).append(File.separator)
				.append("jobJars").append(File.separator).append(jobName);
		File file = new File(jobJarPath.toString());
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * This method checks whether the jar location given corresponds to a valid
	 * jar file or not.
	 * 
	 * @param jobConfig
	 */
	public void validateJarPath(EnterpriseJobConfig enterpriseJobConfig) {
		Enable isLocalSystemJar = enterpriseJobConfig.getIsLocalSystemJar();
		
		if ( isLocalSystemJar == null || isLocalSystemJar == Enable.FALSE) {
			
			String inputFile = enterpriseJobConfig.getInputFile();
			if (!isNullOrEmpty(inputFile)) {
				boolean endsWithJar = inputFile.trim().endsWith(".jar");
				if (!endsWithJar || !new File(inputFile).exists()) {
					jobInputErrors.put(FILE_PATH,
							errorMessages.get(ErrorMessages.SUPPLIED_JAR_INVALID));
				}
			}
		}
		
	}

	private boolean isJobIdExists(String existingJobId, Cluster cluster) {
		String response = RemotingUtil.fireCommandAsHadoopDistribution(cluster,
				"job -status " + existingJobId, CommandType.HADOOP_JOB);
		if (response.contains("Counters:")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * validate cron expression for scheduled jumbune job.
	 *
	 * @param config
	 *            is a object of job config class
	 */
	private void validateTuningScheduleDate(String tuningScheduleDate) {

		if (!isNullOrEmpty(tuningScheduleDate)
				&& !isDateValidFormat("yyyy/MM/dd HH:mm", tuningScheduleDate)) {
			jobInputErrors.put(CRON_EXPRESSION,
					errorMessages.get(ExtendedErrorMessages.SCHEDULING_INVALID));
		}

	}

	public boolean isDateValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			return false;
		}
		return date != null;
	}

	/**
	 * This method checks initial settings ex hadoop home is set or not or
	 * jumbune home is set or not
	 * 
	 * @param config
	 *            the config
	 * @return true if all case passes successfully
	 */
	protected boolean jumbuneHomeValid() {
		jumbuneHome = JobConfig.getJumbuneHome();
		if (isNullOrEmpty(jumbuneHome) || !new File(jumbuneHome).exists()) {
			return false;
		}
		return true;
	}

	/**
	 * Check and validate hdfs path.
	 *
	 * @param config
	 *            bean for the yaml file
	 * @param listOfFailedValidation
	 *            contains a list of failed validation in case of HDFS.
	 * @return all validation checks to be applied by the user
	 */
	private void checkIfHdfsPathExists(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		String hadoopInputPath = jobConfig.getHdfsInputPath();
		if (hadoopInputPath == null || hadoopInputPath.trim().isEmpty()) {
			return;
		}
		try {
			LOGGER.debug("Valdating HDFS Path :" + HDFS_FILE_EXISTS + hadoopInputPath);
			String commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(
					jumbuneRequest.getCluster(), HDFS_FILE_EXISTS + hadoopInputPath,
					CommandType.USER, jobConfig.getOperatingUser());
			LOGGER.debug("HDFS Path [" + hadoopInputPath + "] exist? Response :"
					+ commandResponse);
			if (commandResponse == null) {
				jobInputErrors.put(HDFS_INPUT_PATH,
						errorMessages.get(ErrorMessages.HADOOP_INPUT_PATH_INVALID));
			} else if (commandResponse.contains("No such file or directory")) {
				jobInputErrors.put(HDFS_INPUT_PATH,
						errorMessages.get(ErrorMessages.HADOOP_INPUT_PATH_INVALID));
			}  else if (commandResponse.contains("java.net.ConnectException")) {
				jobInputErrors.put("destinationPath",
						errorMessages.get(ErrorMessages.HADOOP_SERVICES_DOWN));
			} else {
				if (FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION).startsWith(A)) {
					String[] arr = commandResponse.split(NEW_LINE);
					commandResponse = arr[arr.length - 1];
				}
				commandResponse = commandResponse.split(REGEX_SAPCE)[0];
				if ("0".equals(commandResponse)) {
					jobInputErrors.put(HDFS_INPUT_PATH,
							errorMessages.get(ErrorMessages.HADOOP_DIRECTORY_EMPTY));
				}
			}

		} catch (Exception e) {
			jobInputErrors.put(HDFS_INPUT_PATH, errorMessages.get(ErrorMessages.HADOOP_NOT_EXIST));
		}

	}
	
	private void checkDscSourceAndDestinationPath(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		DataSourceCompValidationInfo dscvi = jobConfig.getDataSourceCompValidationInfo();
		String sourcePath = dscvi.getSourcePath();
		String destinationPath = dscvi.getDestinationPath();
		String commandResponse = null;
		
		try {
			
			if (sourcePath == null || sourcePath.trim().isEmpty()) {
				jobInputErrors.put("sourcePath",
						errorMessages.get(ErrorMessages.FIELD_LEFT_BLANK));
			} else {
				LOGGER.debug("Valdating HDFS Path :" + HDFS_FILE_EXISTS + sourcePath);
				
				commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(
						jumbuneRequest.getCluster(), HDFS_FILE_EXISTS + sourcePath,
						CommandType.USER, jobConfig.getOperatingUser());
				
				LOGGER.debug("HDFS Path [" + sourcePath + "] exist? Response :"
						+ commandResponse);
				
				if (isNullOrEmpty(commandResponse)
						|| commandResponse.contains("No such file or directory")) {
					jobInputErrors.put("sourcePath",
							errorMessages.get(ErrorMessages.HADOOP_INPUT_PATH_INVALID));
				} else if (commandResponse.contains("java.net.ConnectException")) {
					jobInputErrors.put("sourcePath",
							errorMessages.get(ErrorMessages.HADOOP_SERVICES_DOWN));
					return;
				}
			}
			
			if (destinationPath == null || destinationPath.trim().isEmpty()) {
				jobInputErrors.put("destinationPath",
						errorMessages.get(ErrorMessages.FIELD_LEFT_BLANK));
				
			} else {
				
				commandResponse = null;
				
				LOGGER.debug("Valdating HDFS Path :" + HDFS_FILE_EXISTS + destinationPath);
				
				commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(
						jumbuneRequest.getCluster(), HDFS_FILE_EXISTS + destinationPath,
						CommandType.USER, jobConfig.getOperatingUser());
				
				LOGGER.debug("HDFS Path [" + destinationPath + "] exist? Response :"
						+ commandResponse);
				
				if (isNullOrEmpty(commandResponse)
						|| commandResponse.contains("No such file or directory")) {
					jobInputErrors.put("destinationPath",
							errorMessages.get(ErrorMessages.HADOOP_INPUT_PATH_INVALID));
				} else if (commandResponse.contains("java.net.ConnectException")) {
					jobInputErrors.put("destinationPath",
							errorMessages.get(ErrorMessages.HADOOP_SERVICES_DOWN));
				}
			}
			
		} catch (Exception e) {
			jobInputErrors.put("sourcePath", errorMessages.get(ErrorMessages.HADOOP_NOT_EXIST));
		}
	}

	/**
	 * Check rsa dsa file existence.
	 *
	 * @param failedCases
	 *            error list
	 * @param nameNode
	 *            the nameNode
	 * @param fieldValue
	 */
	private boolean isAgentPrivateKeyOrPathValid(Cluster cluster) {
		Agents agents = cluster.getAgents();
		Session session = null;
		try {
			session = establishConnection(agents.getUser(), cluster.getNameNode(),
					agents.getPassword(), agents.getSshAuthKeysFile());
		} catch (JSchException e) {
			return false;
		}
		if (session == null || !session.isConnected()) {
			return false;
		} else {
			session.disconnect();
			return true;
		}
	}

	public Session establishConnection(String username, String nodeIP, String password,
			String privateKeyPath) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		session = jsch.getSession(username, nodeIP, Constants.TWENTY_TWO);
		if (password != null) {
			session.setPassword(password);
		} else {
			jsch.addIdentity(privateKeyPath);

		}
		class JumbuneUserInfo implements UserInfo {

			public String getPassword() {
				return null;
			}

			public boolean promptYesNo(String str) {
				return true;
			}

			public String getPassphrase() {
				return null;
			}

			public boolean promptPassphrase(String message) {
				return true;
			}

			public boolean promptPassword(String message) {
				return true;
			}

			public void showMessage(String message) {
			}
		}
		UserInfo info = new JumbuneUserInfo();

		session.setUserInfo(info);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		if (password != null) {
			config.put("PreferredAuthentications", "password");
		}
		session.setConfig(config);
		session.connect();

		return session;
	}

	/**
	 * This method validates Slave information in Basic field Tab in Web UI, and
	 * check slaves input is in correct format or not.
	 * 
	 * @param clusterErrors2
	 *
	 * @param failedCases
	 *            error list
	 * @param Config
	 *            the config
	 */
	private void validateWorkersField(Cluster cluster, List<String> clusterErrors) {
		List<String> listOfValidDataNode = new ArrayList<String>();

		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		if (hadoopDistribution.equalsIgnoreCase(Constants.MAPR)) {
			StringBuilder commandBuilder = new StringBuilder().append(MAPR_DATANODE_IP);
			Remoter remoter = RemotingUtil.getRemoter(cluster, "");
			CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder(cluster,
					null);
			commandWritableBuilder.addCommand(commandBuilder.toString(), false, null,
					CommandType.HADOOP_FS);
			String commandResponse = (String) remoter
					.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());
			LOGGER.debug("MAPR datanode command response :" + commandResponse);
			String[] getDataNodes = commandResponse.split(NEW_LINE);
			for (int i = 1; i < getDataNodes.length; i++) {
				listOfValidDataNode.add(getDataNodes[i]);
			}
		} else {
			String commandResponse = RemotingUtil.fireCommandAsHadoopDistribution(cluster,
					REPORT_FROM_CLUSTER, CommandType.HADOOP_FS);
			if (commandResponse.contains(JAVA_NET_CONNECT_EXCEPTION)) {
				clusterErrors.add(errorMessages.get(ErrorMessages.HADOOP_SERVICES_DOWN));
				return;
			}
			String[] splitArray = commandResponse.split(NEW_LINE);
			for (String line : splitArray) {
				if (line.startsWith(NAME)) {
					listOfValidDataNode.add(line.split("\\:")[1].trim());
				}
			}
		}
		Workers workers = cluster.getWorkers();
		List<String> hosts = workers.getHosts();
		if (hosts == null || hosts.isEmpty()) {
			clusterErrors.add(errorMessages.get(ErrorMessages.WORKER_HOST_LIST_EMPTY));
		}
		for (String workerHost : hosts) {
			if (isNullOrEmpty(workerHost) || !(listOfValidDataNode.contains(workerHost))) {
				clusterErrors.add(MessageFormat
						.format(errorMessages.get(ErrorMessages.WORKER_HOST_INVALID), workerHost));
			}
		}
	}

	/**
	 * check whether given ip address is valid or not.
	 *
	 * @param ipaddress
	 *            the ipaddress
	 * @return true if ip adress is valid
	 */
	public boolean isIPAdressValid(String ipAddress) {
		if (isNullOrEmpty(ipAddress)) {
			return false;
		}
		try {
			return InetAddress.getByName(ipAddress).isReachable(Constants.FIVE_HUNDRED);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * check if a command is exist or not it returns true if it is exist import
	 * 
	 *
	 * @param value
	 *            the value
	 * @param inputValue
	 *            value which is to be tested
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
	 * This method check port is available or not if avaliable it returns true.
	 *
	 * @param port
	 *            is port number which is to be check
	 * @param inetAddress
	 *            the inet address
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
	 * This method checks whether a given string is null or empty.
	 *
	 * @param str
	 *            given string
	 * @return true if the given string is null or empty
	 */
	public boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().isEmpty()) ? true : false;
	}

	/**
	 * * This method checks that is given enum Value is TRUE or FALSE.
	 *
	 * @param enable
	 *            is a enum
	 * @return true if it is TRUE or false if It is false or null
	 */
	public static boolean isEnable(Enable enable) {
		return (enable != null && Enable.TRUE.equals(enable) ? true : false);

	}

}
