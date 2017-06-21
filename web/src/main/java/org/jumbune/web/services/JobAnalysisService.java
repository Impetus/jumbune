package org.jumbune.web.services;

import static org.jumbune.execution.utils.ExecutionConstants.TEMP_DIR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.DqtViewBean;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.Feature;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.profiling.ProfilingParam;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.profiling.utils.ProfilerConstants;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

/**
 * The Class JobAnalysisService.
 */
@Path(WebConstants.JOB_ANALYSIS)
public class JobAnalysisService{

	private final String DIAGNOSTICS = "DIAGNOSTICS";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(JobAnalysisService.class);

	/**
	 * Save data.
	 *
	 * @param form
	 *            the form
	 * @return the response
	 */
	@POST
	@Path(WebConstants.SAVE)
	public Response saveData(FormDataMultiPart form) {
		Map<String, String> response = null;
		try{
			String enterpriseJobConfigJSON = form.getField("jsonData").getValue();
			LOGGER.debug("Received JSON: " + enterpriseJobConfigJSON);
			response = saveDataAndCreateDirectories(form);
			response.put(Constants.STATUS, Constants.SUCCESS);
			return Response.ok(response).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			if (response == null) {
				response = new HashMap<>(3);
			}
			response.put(Constants.STATUS, Constants.ERROR_);
			response.put(DIAGNOSTICS, e.getMessage());
			return Response.ok(response).build();
		}
	}

	/**
	 * Save data and create directories.
	 *
	 * @param form
	 *            the form
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	public Map<String, String> saveDataAndCreateDirectories(FormDataMultiPart form)
			throws Exception {
		JumbuneRequest jumbuneRequest = saveUserResources(form);
		saveJsonToJumbuneHome(jumbuneRequest.getConfig());

		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		String agentHome = RemotingUtil.getAgentHome(jumbuneRequest.getCluster());
		processClassPathElement(cse, agentHome);

		// place where list of dependent jars' path for instrumented job
		// jar are getting created.
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) jumbuneRequest.getConfig();
		enterpriseJobConfig.setClasspath(new Classpath());
		enterpriseJobConfig.getClasspath().setJumbuneSupplied(cse);

		// sends user uploaded MR job jars on agent
		String jarFilePath = EnterpriseJobConfig.getJumbuneHome() + File.separator
				+ Constants.JOB_JARS_LOC + enterpriseJobConfig.getFormattedJumbuneJobName()
				+ Constants.MR_RESOURCES;

		checkAndSendMrJobJarOnAgent(jumbuneRequest, jarFilePath);
		LOGGER.debug("Configuration received [" + enterpriseJobConfig + "]");

		modifyDebugParameters(enterpriseJobConfig);
		modifyProfilingParameters(enterpriseJobConfig);
		setInputFileInConfig(enterpriseJobConfig);

		Map<String, String> responseMap = new HashMap<>(3);
		HttpExecutorService service = new HttpExecutorService();

		saveJsonToJumbuneHome(jumbuneRequest.getConfig());

		// uncomment the below line of code if you want to trigger jumbune job
		// from here itself
		// i.e. if result page is not capable of connecting to socket.

		// jumbuneRequest = service.runInSeperateThread(jumbuneRequest,
		// reports);

		responseMap.put("JOB_NAME", enterpriseJobConfig.getJumbuneJobName());
		return responseMap;
	}
	

	/**
	 * Check time difference between browser and server time.
	 *
	 * @param sBrowserGMT
	 *            the s browser gmt
	 * @param date
	 *            the date
	 * @return the final date
	 * @throws ParseException
	 *             the parse exception
	 */
	private Date getFinalDate(String sBrowserGMT, Date date) throws ParseException {

		long browserGMT = Long.parseLong(sBrowserGMT);

		Calendar mCalendar = new GregorianCalendar();
		TimeZone mTimeZone = mCalendar.getTimeZone();
		int mGMTOffset = mTimeZone.getRawOffset();
		long serverGMT = TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS);

		long finalTime = date.getTime() - (browserGMT - serverGMT);
		return new Date(finalTime);
	}

	/**
	 * Sets the scheduled job.
	 *
	 * @param config
	 *            the config
	 * @param service
	 *            the service
	 * @param scheduleJobTiming
	 *            the schedule job timing
	 * @param schedulingMessage
	 *            the scheduling message
	 */
	private void setScheduledJob(Config config, HttpExecutorService service,
			String scheduleJobTiming, final String schedulingMessage) {
		LOGGER.debug("Its a request to schedule job scheduleJobTiming " + scheduleJobTiming);
		String scheduledMessage = schedulingMessage;
		try {
			service.scheduleTask(config, false);
		} catch (JumbuneException e) {
			scheduledMessage = e.getMessage();
		}
	}

	/**
	 * Sets the input file in config.
	 *
	 * @param enterpriseJobConfig
	 *            the new input file in config
	 */
	private void setInputFileInConfig(EnterpriseJobConfig enterpriseJobConfig) {
		if ((enterpriseJobConfig.getInputFile() != null)
				&& (!enterpriseJobConfig.getInputFile().contains(Constants.FORWARD_SLASH))) {
			String jarName = enterpriseJobConfig.getJobJarLoc()
					+ enterpriseJobConfig.getFormattedJumbuneJobName()
					+ enterpriseJobConfig.getInputFile();

			enterpriseJobConfig.setInputFile(jarName);
		}
	}

	/**
	 * Modify profiling parameters.
	 *
	 * @param enterpriseJobConfig
	 *            the enterprise job config
	 */
	private void modifyProfilingParameters(EnterpriseJobConfig enterpriseJobConfig) {
		ProfilingParam param = new ProfilingParam();
		if (enterpriseJobConfig.getHadoopJobProfile().getEnumValue()) {
			param.setReducers("0-1");
			param.setMappers("0-1");
			param.setStatsInterval(Constants.FIVE_THOUNSAND);
			enterpriseJobConfig.setProfilingParams(param);
		}
	}

	/**
	 * Modify debug parameters.
	 *
	 * @param enterpriseJobConfig
	 *            the enterprise job config
	 */
	private void modifyDebugParameters(EnterpriseJobConfig enterpriseJobConfig) {
		enterpriseJobConfig.setPartitionerSampleInterval(Constants.FIFTY);
		Map<String, LogLevel> logLevel = new HashMap<String, LogLevel>();
		logLevel.put("ifblock", LogLevel.TRUE);
		logLevel.put("switchcase", LogLevel.TRUE);
		// logLevel.put("partitioner", LogLevel.FALSE);
		if (enterpriseJobConfig.getRegexValidations() != null
				&& !enterpriseJobConfig.getRegexValidations().isEmpty()) {
			logLevel.put("instrumentRegex", LogLevel.TRUE);
		} else {
			logLevel.put("instrumentRegex", LogLevel.FALSE);
		}
		if (enterpriseJobConfig.getUserValidations() != null
				&& !enterpriseJobConfig.getUserValidations().isEmpty()) {
			logLevel.put("instrumentUserDefValidate", LogLevel.TRUE);
		} else {
			logLevel.put("instrumentUserDefValidate", LogLevel.FALSE);
		}
		DebuggerConf debuggerConf = new DebuggerConf();
		debuggerConf.setLogLevel(logLevel);
		// ToDo check max if block nesting level
		debuggerConf.setMaxIfBlockNestingLevel(2);
		enterpriseJobConfig.setDebuggerConf(debuggerConf);
	}

	/**
	 * Check and send mr job jar on agent.
	 *
	 * @param jumbuneRequest
	 *            the jumbune request
	 * @param jarFilePath
	 *            refers to the path of the jar to be sent on agent
	 */
	private void checkAndSendMrJobJarOnAgent(JumbuneRequest jumbuneRequest, String jarFilePath) {
		if (JobConfigUtil.isMRJobJarPresent(jumbuneRequest.getCluster(), jarFilePath)) {
			JobConfigUtil.sendMRJobJarOnAgent(jumbuneRequest, jarFilePath);
		}
	}

	/**
	 * Process class path element.
	 *
	 * @param cse
	 *            the cse
	 * @param agentHome
	 *            the agent home
	 */
	private void processClassPathElement(ClasspathElement cse, String agentHome) {

		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace(Constants.AGENT_ENV_VAR_NAME, agentHome);

		}
	}

	/**
	 * Save json to jumbune home.
	 *
	 * @param config
	 *            the config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void saveJsonToJumbuneHome(Config config) throws IOException {
		BufferedWriter bufferedWriter = null;
		try {
			EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) config;
			String jsonDir = System.getenv("JUMBUNE_HOME") + WebConstants.JSON_REPO;

			Gson gson = new Gson();

			if (Enable.TRUE.equals(enterpriseJobConfig.getEnableDataQualityTimeline())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getIsDataSourceComparisonEnabled())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getEnableJsonDataValidation())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getEnableXmlDataValidation())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getEnableDataProfiling())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getDebugAnalysis())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_JOB);
				jsonDir = jsonDir + WebConstants.ANALYZE_JOB;
			} else if (Enable.TRUE.equals(enterpriseJobConfig.getEnableDataValidation())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			}else if (Enable.TRUE.equals(enterpriseJobConfig.getIsDataCleansingEnabled())) {
				enterpriseJobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			}
			jsonDir = jsonDir + File.separator + enterpriseJobConfig.getJumbuneJobName();
			File jsonDirectory = new File(jsonDir);

			if (!jsonDirectory.exists()) {
				jsonDirectory.mkdirs();
			}

			String jsonData = gson.toJson(enterpriseJobConfig, EnterpriseJobConfig.class);
			jsonDir = jsonDir + WebConstants.JOB_REQUEST_JSON;
			File file = new File(jsonDir);
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

	/**
	 * Save user resources.
	 *
	 * @param form
	 *            the form
	 * @return the jumbune request
	 * @throws Exception
	 *             the exception
	 */
	public JumbuneRequest saveUserResources(FormDataMultiPart form) throws Exception {
		EnterpriseJobConfig enterpriseJobConfig = getEnterpriseJobConfig(form);
		if (enterpriseJobConfig == null) {
			throw new Exception("Error while parsing JSON");
		}
		LOGGER.debug("Decorated EnterpriseJobConfig instance: " + enterpriseJobConfig);

		// JobConfigUtil.checkIfJumbuneHomeEndsWithSlash(enterpriseJobConfig);

		String jsonTempLoc = TEMP_DIR + Constants.FORWARD_SLASH + System.currentTimeMillis();
		File tempDir = new File(EnterpriseJobConfig.getJumbuneHome() + jsonTempLoc);
		WebUtil.makeDirectories(tempDir);

		Cluster cluster = JobRequestUtil
				.getClusterByName(enterpriseJobConfig.getOperatingCluster());
		boolean isProfilingEnabled = enterpriseJobConfig.getHadoopJobProfile().getEnumValue();
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		boolean isYarnEnable = hadoopType.equalsIgnoreCase(ExtendedConstants.YARN);
		checkAvailableNodes(cluster, isProfilingEnabled, isYarnEnable);

		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setConfig(enterpriseJobConfig);
		jumbuneRequest.setCluster(cluster);
		JobConfigUtil.createJumbuneDirectories(jumbuneRequest);

		writeUploadedFileToFileItem(enterpriseJobConfig, form);

		WebUtil.deleteTempFiles(tempDir);

		return jumbuneRequest;
	}

	/**
	 * Write uploaded file to file item.
	 *
	 * @param enterpriseJobConfig
	 *            the enterprise job config
	 * @param form
	 *            the form
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void writeUploadedFileToFileItem(EnterpriseJobConfig enterpriseJobConfig,
			FormDataMultiPart form) throws IOException {
		// Skip adding jar file in case of Data Quality and Data Profiling
		if (enterpriseJobConfig.getEnableDataValidation().getEnumValue() == true
				|| enterpriseJobConfig.getEnableDataProfiling().getEnumValue() == true
				|| enterpriseJobConfig.getEnableDataQualityTimeline().getEnumValue() == true) {
			return;
		}
		String inputFile = enterpriseJobConfig.getInputFile();
		try {
			if (inputFile != null && !inputFile.isEmpty()) {
				String fileName = inputFile
						.substring(inputFile.lastIndexOf(Constants.FORWARD_SLASH) + 1);
				String newJarFileLoc = enterpriseJobConfig.getJobJarLoc()
						+ enterpriseJobConfig.getFormattedJumbuneJobName() + fileName;
				Files.copy(Paths.get(inputFile), Paths.get(newJarFileLoc),
						StandardCopyOption.REPLACE_EXISTING);
			} else {
				FormDataBodyPart jarFile = form.getField("inputFile");
				if(jarFile == null){
					return;
				}
				String fileName = jarFile.getContentDisposition().getFileName();
				if (fileName == null) {
					return;
				}
				InputStream fileInputStream = jarFile.getValueAs(InputStream.class);
				String newJarFileLoc = enterpriseJobConfig.getJobJarLoc()
						+ enterpriseJobConfig.getFormattedJumbuneJobName() + fileName;
				Files.copy(fileInputStream, Paths.get(newJarFileLoc),
						StandardCopyOption.REPLACE_EXISTING);

				// TODO to verify inputFile with older execution model.
				LOGGER.debug("newJarFileLoc: " + newJarFileLoc);
				enterpriseJobConfig.setInputFile(newJarFileLoc);
				enterpriseJobConfig.setIsLocalSystemJar(Enable.TRUE);
			}
		} catch (IOException e) {
			throw new IOException("Unable to write uploaded jar ", e);

		}

	}

	/**
	 * Gets the enterprise job config.
	 *
	 * @param form
	 *            the form
	 * @return the enterprise job config
	 */
	public EnterpriseJobConfig getEnterpriseJobConfig(FormDataMultiPart form) {
		String enterpriseJobConfigJSON = form.getField("jsonData").getValue();
		Gson gson = new Gson();
		EnterpriseJobConfig enterpriseJobConfig = gson.fromJson(enterpriseJobConfigJSON,
				EnterpriseJobConfig.class);
		return enterpriseJobConfig;
	}

	/**
	 * This method performs following steps: 1) Reads profilingParams and
	 * creates object of profilingParams 2) Reads the DataNode and TaskTracker
	 * from ProfilingParams and check the availability of nodes. 3) Saves
	 * UnavialableHost and removes that Host from config.
	 *
	 * @param cluster
	 *            the cluster
	 * @param isProfilingEnabled
	 *            the is profiling enabled
	 * @param isYarnEnable
	 *            the is yarn enable
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void checkAvailableNodes(Cluster cluster, boolean isProfilingEnabled,
			boolean isYarnEnable) throws IOException {

		int dnPort = ProfilerConstants.DEFAULT_DN_PORT;
		int wdPort = ProfilerConstants.DEFAULT_TT_PORT;

		if (isProfilingEnabled) {
			dnPort = Integer.parseInt(cluster.getWorkers().getDataNodeJmxPort());
			wdPort = Integer.parseInt(cluster.getWorkers().getTaskExecutorJmxPort());
		}

		Workers workers = cluster.getWorkers();
		List<String> hosts = workers.getHosts();

		if (Constants.ON.equalsIgnoreCase(cluster.getEnableHostRange())) {
			String fromIP[] = cluster.getHostRangeFromValue().split("\\.");
			String toIP[] = cluster.getHostRangeToValue().split("\\.");
			String hostRangeVal = fromIP[0] + Constants.DOT + fromIP[1] + Constants.DOT + fromIP[2];
			int fromRangeVal = Integer.parseInt(fromIP[fromIP.length - 1]);
			int toRangeVal = Integer.parseInt(toIP[toIP.length - 1]);
			ConfigurationUtil.addSlaveRanges(fromRangeVal, toRangeVal, hostRangeVal, hosts);
		}

		Iterator<String> it = hosts.iterator();
		List<String> unavailableHosts = null;
		boolean isDNAvailable, isNodeAvailable, isWorkerDaemonAvailable;
		String nodeIP, message = null;
		ValidateInput validate = new ValidateInput();

		while (it.hasNext()) {
			nodeIP = it.next();
			isNodeAvailable = ConfigurationUtil.checkIPAdress(nodeIP);

			if ((isNodeAvailable) && (isProfilingEnabled)) {

				// check the availability of Datanode
				isDNAvailable = validate.isPortAvailable(dnPort, nodeIP);
				// check the availability of Datanode
				isWorkerDaemonAvailable = validate.isPortAvailable(wdPort, nodeIP);

				if (isDNAvailable) {
					if (!isWorkerDaemonAvailable) {
						message = isYarnEnable ? ProfilerConstants.NM_NOT_REACHABLE
								: ProfilerConstants.TT_NOT_REACHABLE;
						isNodeAvailable = false;
					}
				} else {
					if (!isWorkerDaemonAvailable) {
						message = isYarnEnable ? ProfilerConstants.DN_NM_NOT_REACHABLE
								: ProfilerConstants.DN_TT_NOT_REACHABLE;
					} else {
						message = ProfilerConstants.DN_NOT_REACHABLE;
					}
					isNodeAvailable = false;
				}
			} else {
				message = ProfilerConstants.NODE_NOT_REACHABLE;
			}
			unavailableHosts = removeUnavailableHosts(isNodeAvailable, nodeIP, message, it);
		}

		ClusterDefinition clusterDefinition = (ClusterDefinition) cluster;
		clusterDefinition.setUnavailableHosts(unavailableHosts);
	}

	/**
	 * Removes the unavailable hosts.
	 *
	 * @param isNodeAvailable
	 *            the is node available
	 * @param nodeIp
	 *            denotes the ip of the host node
	 * @param message
	 *            denotes whether the datanode or tasktracker or both are not
	 *            reachable
	 * @param iterator
	 *            the iterator
	 * @return a list containing a list of unavailable nodes.
	 */
	private List<String> removeUnavailableHosts(boolean isNodeAvailable, String nodeIp,
			String message, Iterator<String> iterator) {

		List<String> unavailHost = new ArrayList<String>();
		if (!isNodeAvailable) {
			unavailHost.add(nodeIp);
			iterator.remove();
		}
		return unavailHost;
	}

	/**
	 * Gets the hadoop type by cluster name.
	 *
	 * @return the cluster by name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path(WebConstants.JOB_HDFS_DETAILS)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDqtDetails() throws IOException {
		Gson gson = new Gson();
		List<DqtViewBean> dqtViewBeans = ConfigurationUtil.getDqtViewDetails();
		String dqtViewDetails = gson.toJson(dqtViewBeans);
		return Response.ok(dqtViewDetails).build();

	}

	/**
	 * Removes the scheduled dataqualitytimeline jobs.
	 *
	 * @param jobName
	 *            the job name
	 * @return the response
	 * @throws JumbuneException
	 *             the jumbune exception
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("dqt/{jobName}")
	public Response removeScheduledDQTJobs(@PathParam("jobName") final String jobName)
			throws JumbuneException {
		GenericEntity<Boolean> entity = new GenericEntity<Boolean>(removeDQTJob(jobName)) {
		};
		return Response.ok(entity).build();
	}

	/**
	 * Removes the dataqualitytimeline job.
	 *
	 * @param jobName
	 *            the job name
	 * @return true, if successful
	 * @throws JumbuneException
	 *             the jumbune exception
	 */
	private boolean removeDQTJob(final String jobName) throws JumbuneException {
		DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
		StringBuilder sb = new StringBuilder();
		String directoryPath = sb.append(JobConfig.getJumbuneHome()).append(File.separator)
				.append("ScheduledJobs").append(File.separator).append("IncrementalDQJobs")
				.append(File.separator).append(jobName).append(File.separator).toString();
		dqts.deleteCurrentJobEntryFromCron(jobName);
		dqts.deleteJobResult(directoryPath);
		return true;
	}
	
}
