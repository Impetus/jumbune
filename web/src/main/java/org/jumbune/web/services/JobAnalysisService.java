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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.beans.profiling.ProfilingParam;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.profiling.utils.ProfilerConstants;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;

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
			String jobConfigJSON = form.getField("jsonData").getValue();
			LOGGER.debug("Received JSON: " + jobConfigJSON);
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
	public Map<String, String> saveDataAndCreateDirectories(FormDataMultiPart form) throws Exception {
		JumbuneRequest jumbuneRequest = saveUserResources(form);
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = ClusterAnalysisService.cache.getCluster(jobConfig.getOperatingCluster());

		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		String agentHome = RemotingUtil.getAgentHome(jumbuneRequest.getCluster());
		processClassPathElement(cse, agentHome);

		// place where list of dependent jars' path for instrumented job
		// jar are getting created.

		String tempDir = jobConfig.getTempDirectory().trim();
		if (!tempDir.endsWith("/")) {
			tempDir = tempDir + "/";
		}
		jobConfig.setTempDirectory(tempDir);
		jobConfig.setOperatingUser(cluster.getAgents().getUser());
		jobConfig.setClasspath(new Classpath());
		jobConfig.getClasspath().setJumbuneSupplied(cse);

		// sends user uploaded MR job jars on agent
		String jarFilePath = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC
				+ jobConfig.getFormattedJumbuneJobName() + Constants.MR_RESOURCES;

		checkAndSendMrJobJarOnAgent(jumbuneRequest, jarFilePath);
		LOGGER.debug("Configuration received [" + jobConfig + "]");

		modifyDebugParameters(jobConfig);
		modifyProfilingParameters(jobConfig);
		setInputFileInConfig(jobConfig);

		Map<String, String> responseMap = new HashMap<>(3);
		saveJsonToJumbuneHome(jumbuneRequest.getConfig());
		JobRequestUtil.setJobStatus(jobConfig, JobStatus.INITIALIZED);
		responseMap.put("JOB_NAME", jobConfig.getJumbuneJobName());
		return responseMap;
	}

	/**
	 * Sets the input file in config.
	 *
	 * @param jobConfig
	 *            the new input file in config
	 */
	private void setInputFileInConfig(JobConfig jobConfig) {
		if ((jobConfig.getInputFile() != null)
				&& (!jobConfig.getInputFile().contains(Constants.FORWARD_SLASH))) {
			String jarName = jobConfig.getJobJarLoc()
					+ jobConfig.getFormattedJumbuneJobName()
					+ jobConfig.getInputFile();

			jobConfig.setInputFile(jarName);
		}
	}

	/**
	 * Modify profiling parameters.
	 *
	 * @param jobConfig
	 *            the job config
	 */
	private void modifyProfilingParameters(JobConfig jobConfig) {
		ProfilingParam param = new ProfilingParam();
		if (jobConfig.getHadoopJobProfile().getEnumValue()) {
			param.setReducers("0-1");
			param.setMappers("0-1");
			param.setStatsInterval(Constants.FIVE_THOUNSAND);
			jobConfig.setProfilingParams(param);
		}
	}

	/**
	 * Modify debug parameters.
	 *
	 * @param jobConfig
	 *            the job config
	 */
	private void modifyDebugParameters(JobConfig jobConfig) {
		jobConfig.setPartitionerSampleInterval(Constants.FIFTY);
		Map<String, LogLevel> logLevel = new HashMap<String, LogLevel>();
		logLevel.put("ifblock", LogLevel.TRUE);
		logLevel.put("switchcase", LogLevel.TRUE);
		// logLevel.put("partitioner", LogLevel.FALSE);
		if (jobConfig.getRegexValidations() != null
				&& !jobConfig.getRegexValidations().isEmpty()) {
			logLevel.put("instrumentRegex", LogLevel.TRUE);
		} else {
			logLevel.put("instrumentRegex", LogLevel.FALSE);
		}
		if (jobConfig.getUserValidations() != null
				&& !jobConfig.getUserValidations().isEmpty()) {
			logLevel.put("instrumentUserDefValidate", LogLevel.TRUE);
		} else {
			logLevel.put("instrumentUserDefValidate", LogLevel.FALSE);
		}
		DebuggerConf debuggerConf = new DebuggerConf();
		debuggerConf.setLogLevel(logLevel);
		// ToDo check max if block nesting level
		debuggerConf.setMaxIfBlockNestingLevel(2);
		jobConfig.setDebuggerConf(debuggerConf);
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
			JobConfig jobConfig = (JobConfig) config;
			String jsonDir = System.getenv("JUMBUNE_HOME") + WebConstants.JSON_REPO;

			Gson gson = new Gson();

			if (Enable.TRUE.equals(jobConfig.getEnableDataQualityTimeline())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(jobConfig.getIsDataSourceComparisonEnabled())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(jobConfig.getEnableJsonDataValidation())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(jobConfig.getEnableXmlDataValidation())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(jobConfig.getEnableDataProfiling())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			} else if (Enable.TRUE.equals(jobConfig.getDebugAnalysis())) {
				jobConfig.setActivated(Feature.ANALYZE_JOB);
				jsonDir = jsonDir + WebConstants.ANALYZE_JOB;
			} else if (Enable.TRUE.equals(jobConfig.getEnableDataValidation())) {
				jobConfig.setActivated(Feature.ANALYZE_DATA);
				jsonDir = jsonDir + WebConstants.ANALYZE_DATA;
			}
			jsonDir = jsonDir + File.separator + jobConfig.getJumbuneJobName();
			File jsonDirectory = new File(jsonDir);

			if (!jsonDirectory.exists()) {
				jsonDirectory.mkdirs();
			}

			String jsonData = gson.toJson(jobConfig, JobConfig.class);
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
		JobConfig jobConfig = getJobConfig(form);
		if (jobConfig == null) {
			throw new Exception("Error while parsing JSON");
		}
		LOGGER.debug("Decorated JobConfig instance: " + jobConfig);

		// JobConfigUtil.checkIfJumbuneHomeEndsWithSlash(jobConfig);

		String jsonTempLoc = TEMP_DIR + Constants.FORWARD_SLASH + System.currentTimeMillis();
		File tempDir = new File(JumbuneInfo.getHome() + jsonTempLoc);
		WebUtil.makeDirectories(tempDir);

		Cluster cluster = JobRequestUtil.getClusterByName(jobConfig.getOperatingCluster());
		boolean isProfilingEnabled = jobConfig.getHadoopJobProfile().getEnumValue();
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		boolean isYarnEnable = hadoopType.equalsIgnoreCase(ExtendedConstants.YARN);
		checkAvailableNodes(cluster, isProfilingEnabled, isYarnEnable);
		writeUploadedFileToFileItem(jobConfig, form);

		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setConfig(jobConfig);
		jumbuneRequest.setCluster(cluster);

		WebUtil.deleteTempFiles(tempDir);

		return jumbuneRequest;
	}

	/**
	 * Write uploaded file to file item.
	 *
	 * @param jobConfig
	 *            the enterprise job config
	 * @param form
	 *            the form
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void writeUploadedFileToFileItem(JobConfig jobConfig, FormDataMultiPart form) throws IOException {
		// Skip adding jar file in case of Data Quality and Data Profiling
		if ((jobConfig.getEnableDataValidation().getEnumValue() == true)
				|| (jobConfig.getEnableDataProfiling().getEnumValue() == true)
				|| (jobConfig.getEnableDataQualityTimeline().getEnumValue() == true)) {
			return;
		}
		String inputFile = jobConfig.getInputFile();
		try {
			if ((inputFile != null) && (!inputFile.isEmpty())) {
				if (jobConfig.getDebugAnalysis().getEnumValue() == true) {
					File logLocation = new File(jobConfig.getMasterConsolidatedLogLocation());
					WebUtil.makeDirectories(logLocation);
					File instrumentedFileLoc = new File(jobConfig.getJobJarLoc()
							+ jobConfig.getFormattedJumbuneJobName() + Constants.INSTRUMENTED_JAR_LOC);
					WebUtil.makeDirectories(instrumentedFileLoc);
				}
				String fileName = inputFile.substring(inputFile.lastIndexOf(Constants.FORWARD_SLASH) + 1);

				String newJarFileLoc = jobConfig.getJobJarLoc()
						+ jobConfig.getFormattedJumbuneJobName() + fileName;
				File f = new File(JumbuneInfo.getHome() + Constants.JOB_JARS_LOC
						+ jobConfig.getFormattedJumbuneJobName());
				WebUtil.makeDirectories(f);
				Files.copy(Paths.get(inputFile), Paths.get(newJarFileLoc), StandardCopyOption.REPLACE_EXISTING);
				jobConfig.setInputFile(newJarFileLoc);
			} else {
				if (jobConfig.getDebugAnalysis().getEnumValue() == true) {
					File logLocation = new File(jobConfig.getMasterConsolidatedLogLocation());
					WebUtil.makeDirectories(logLocation);
					File instrumentedFileLoc = new File(jobConfig.getJobJarLoc()
							+ jobConfig.getFormattedJumbuneJobName() + Constants.INSTRUMENTED_JAR_LOC);
					WebUtil.makeDirectories(instrumentedFileLoc);
				}
				FormDataBodyPart jarFile = form.getField("inputFile");
				if (jarFile == null) {
					return;
				}
				String fileName = jarFile.getContentDisposition().getFileName();
				if (fileName == null) {
					return;
				}
				InputStream fileInputStream = (InputStream) jarFile.getValueAs(InputStream.class);

				String newJarFileLoc = jobConfig.getJobJarLoc()
						+ jobConfig.getFormattedJumbuneJobName() + fileName;
				File f = new File(JumbuneInfo.getHome() + Constants.JOB_JARS_LOC
						+ jobConfig.getFormattedJumbuneJobName());
				WebUtil.makeDirectories(f);
				Files.copy(fileInputStream, Paths.get(newJarFileLoc), StandardCopyOption.REPLACE_EXISTING);
				// TODO to verify inputFile with older execution model.
				LOGGER.debug("newJarFileLoc: " + newJarFileLoc);
				jobConfig.setInputFile(newJarFileLoc);
				jobConfig.setIsLocalSystemJar(Enable.TRUE);
			}
		} catch (IOException e) {
			throw new IOException("Unable to write uploaded jar ", e);
		}
	}

	/**
	 * Gets the job config.
	 *
	 * @param form
	 *            the form
	 * @return the job config
	 */
	public JobConfig getJobConfig(FormDataMultiPart form) {
		String jobConfigJSON = form.getField("jsonData").getValue();
		Gson gson = new Gson();
		JobConfig jobConfig = gson.fromJson(jobConfigJSON,
				JobConfig.class);
		return jobConfig;
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
		String directoryPath = sb.append(JumbuneInfo.getHome())
				.append("ScheduledJobs").append(File.separator).append("IncrementalDQJobs")
				.append(File.separator).append(jobName).append(File.separator).toString();
		dqts.deleteCurrentJobEntryFromCron(jobName);
		dqts.deleteJobResult(directoryPath);
		return true;
	}
	
}
