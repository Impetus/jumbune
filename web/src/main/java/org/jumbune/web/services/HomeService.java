package org.jumbune.web.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Feature;
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.monitoring.utils.JMXConnectorCache;
import org.jumbune.remoting.client.SingleNIOEventGroup;
import org.jumbune.remoting.common.BasicJobConfig;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.utils.WebConstants;

import io.netty.channel.EventLoopGroup;

/**
 * Directs to home page.
 */
@Path(WebConstants.HOME_SERVICE_URL)
public class HomeService {

	private static final String IS_EMR = "isEmr";

	private static final String JOB_QUALITY = "JobQuality";

	private static final String DATA_QUALITY = "DataQuality";

	private static final String CLUSTER_ANALYSIS = "ClusterAnalysis";

	private static final String SUCCESS = "success";

	private static final String CLUSTER_NAME = "clusterName";

	private static final String INFLUXDB_CONFIGURATION = "influxdbConfiguration";

	private static final String HA_CONFIGURATION = "haConfiguration";

	private static final String EMAIL_CONFIGURATION = "emailConfiguration";

	private static final String ALERT_CONFIGURATION = "alertConfiguration";

	private static final String ALERT_ACTION_CONFIGURATION = "alertActionConfiguration";

	private static final String STATUS = "status";

	private static final String JOB_TYPE = "jobType";

	private static final String JOB_NAME = "jobName";

	private static final String ANALYZE_JOB = "Analyze Job";

	private static final String ANALYZE_DATA = "Analyze Data";

	private static final String IS_MAPR = "isMapr";

	/** The Constant JSON_FILE. */
	private static final String JSON_FILE = "/jsonInfo.ser";

	/** The Constant EXAMPLE_FILE_LOCATION. */
	private final String EXAMPLE_FILE_LOCATION = "examples/resources/samplejson/";

	private static final String DQ_JOBS_DIR = "/ScheduledJobs/IncrementalDQJobs/";

	private static final String TUNING_SCHEDULED_DIR = "/scheduledJobs/userScheduled/";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(HomeService.class);

	public HomeService() {
		super();
	}

	/** The request. */
	@Context
	HttpServletRequest request;

	/** The response. */
	@Context
	HttpServletResponse response;

	/**
	 * Process post.
	 *
	 * @return the response
	 */
	@POST
	public Response processPost() {
		StringBuilder builder = new StringBuilder();
		try {
			service();
			builder.append("SUCCESS");
		} catch (ServletException | IOException e) {
			builder.append("FAILURE due to: " + e);
		}
		GenericEntity<String> entity = new GenericEntity<String>(builder.toString()) {
		};
		return Response.ok(entity).build();
	}

	/**
	 * Process get.
	 *
	 * @return the response
	 */
	@GET
	public Response processGet() {
		return processPost();
	}

	/**
	 * Gets the all jobs from json repo.
	 *
	 * @return the all jobs from json repo
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/supportedFeatures")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportedFeatures() throws IOException {
		try {
			List<String> supportedFeatures = getSupportedFeatures0();
			return Response.ok(Constants.gson.toJson(supportedFeatures)).build();
		}catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/jenkins-build-no")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildNumber() throws IOException {
		try {
			Map<String, String> map = new HashMap<>(1);
			map.put("buildno", "2.0");
			return Response.ok(Constants.gson.toJson(map)).build();
		}catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	
	/**
	 * Gets the all jobs from json repo.
	 *
	 * @return the all jobs from json repo
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/jobs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecentJobsFromJsonRepo() throws IOException {
		try {
			// String[] jobTypes = {WebConstants.ANALYZE_DATA,
			// WebConstants.ANALYZE_JOB};
			Map<String, String> jobTypes = new HashMap<String, String>(3);
			jobTypes.put(WebConstants.ANALYZE_DATA, ANALYZE_DATA);
			jobTypes.put(WebConstants.ANALYZE_JOB, ANALYZE_JOB);

			String jsonRepositoryLocation = System.getenv(WebConstants.JUMBUNE_HOME)
					+ WebConstants.JSON_REPO;

			// Extracting all json files from jsonrepo directory;
			List<File> filesList = new ArrayList<File>();
			File jsonRepoDir = new File(jsonRepositoryLocation);
			if (jsonRepoDir.exists()) {
				for (File jobTypesDir : jsonRepoDir.listFiles()) {
					for (File file : jobTypesDir.listFiles()) {
						filesList.add(file);
					}
				}
			}

			// Sorting files according to last modified time
			File[] filesArray = sort(filesList);

			// Creating json
			List<Map<String, String>> recentJobsList = new ArrayList<Map<String, String>>();
			Map<String, String> map;
			String jobName = null;
			JobStatus jobStatus = null;
			for (File file : filesArray) {
				map = new HashMap<String, String>(2);
				jobName = file.getName();
				map.put(JOB_NAME, jobName);
				map.put(JOB_TYPE, jobTypes.get(file.getParentFile().getName()));
				
				if (JobRequestUtil.isJobTuningScheduledType(jobName)) {
					jobStatus = JobRequestUtil.getScheduledTuningJobStatus(jobName);
				} else if (JobRequestUtil.isJobDQTScheduledType(jobName)) {
					jobStatus = JobStatus.COMPLETED;
				} else if (isResultFileExists(file)) {
					jobStatus = JobStatus.COMPLETED;
				} else {
					jobStatus = JobStatus.IN_PROGRESS;
				}
				map.put(STATUS, jobStatus.getStatus());
				recentJobsList.add(map);
			}

			return Response.ok(Constants.gson.toJson(recentJobsList)).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path(WebConstants.SCHEDULED_DQT_JOBS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getScheduledDQTJobsList() {
		try {
			return Response.ok( Constants.gson.toJson(JobRequestUtil.getScheduledDQTJobsList())).build();
		} catch (IOException | ParseException e) {
			LOGGER.error(e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/configurations/{clusterName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClusterConfigurations(@PathParam(CLUSTER_NAME) final String clusterName) {
		Map<String, Object> configurations = new HashMap<String, Object>(7);
		try {
			configurations.put(ALERT_ACTION_CONFIGURATION, AdminConfigurationUtil.getAlertActionConfiguration(clusterName));
			configurations.put(ALERT_CONFIGURATION, AdminConfigurationUtil.getAlertConfiguration(clusterName));
			configurations.put(EMAIL_CONFIGURATION, AdminConfigurationUtil.getEmailConfiguration(clusterName));
			configurations.put(HA_CONFIGURATION, AdminConfigurationUtil.getHAConfiguration(clusterName));
			configurations.put(INFLUXDB_CONFIGURATION, AdminConfigurationUtil.getInfluxdbConfiguration(clusterName));
			return Response.ok( Constants.gson.toJson(configurations)).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
	
/*	@POST
	@Path("/saveclusterconfigurations")
	@Produces(MediaType.TEXT_PLAIN)
	public Response saveClusterConfigurations(
			@FormParam(CLUSTER_NAME) String clusterName,
			@FormParam(ALERT_ACTION_CONFIGURATION) String alertActionConfiguration,
			@FormParam(ALERT_CONFIGURATION) String alertConfiguration,
			@FormParam(CLUSTER_INFO_CONFIGURATION) String clusterInfoConfiguration,
			@FormParam(EMAIL_CONFIGURATION) String emailConfiguration,
			@FormParam(HA_CONFIGURATION) String haConfiguration,
			@FormParam(INFLUXDB_CONFIGURATION) String influxdbConfiguration ){
		
		try {
			AdminConfigurationUtil.saveAlertActionConfiguration(clusterName, alertActionConfiguration);
			AdminConfigurationUtil.saveAlertConfiguration(clusterName, alertConfiguration);
			AdminConfigurationUtil.saveEmailConfiguration(clusterName, emailConfiguration);
			AdminConfigurationUtil.saveHAConfiguration(clusterName, haConfiguration);
			AdminConfigurationUtil.saveInfluxdbConfiguration(clusterName, influxdbConfiguration);
			return Response.ok( new Gson().toJson(SUCCESS)).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
*/	
	/**
	 * Gets the cluster by name.
	 *
	 * @param jobName
	 *            the job name
	 * @return the cluster by name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/jobs/{jobName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJobsByName(@PathParam(JOB_NAME) final String jobName) throws IOException {
		return Response.ok(JobRequestUtil.getJobJson(jobName)).build();
	}

	@POST
	@Path("/deletejob" + "/{jobName}")
	public Response deleteJob(@PathParam(JOB_NAME) String jobName) {
		try {
			deleteJob1(jobName);
			return Response.ok(SUCCESS).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	/**
	 * Gets the all jumbune example.
	 *
	 * @return the all jumbune example
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/examples")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllJumbuneExample() throws IOException {
		try {

			Map<String, List<String>> allJobs = new HashMap<String, List<String>>();

			StringBuilder exampleFileLocation = new StringBuilder(
					System.getenv(WebConstants.JUMBUNE_HOME)).append(File.separator)
							.append(EXAMPLE_FILE_LOCATION);

			StringBuilder jsonRepoDir = new StringBuilder(exampleFileLocation)
					.append(WebConstants.ANALYZE_DATA).append(File.separator);
			File fileCheck = new File(jsonRepoDir.toString());

			if (fileCheck.exists()) {
				allJobs.put(Feature.ANALYZE_DATA.getEnumValue(),
						getExampleList(jsonRepoDir.toString()));
			}

			jsonRepoDir = new StringBuilder(exampleFileLocation).append(WebConstants.ANALYZE_JOB)
					.append(File.separator);
			fileCheck = new File(jsonRepoDir.toString());

			if (fileCheck.exists()) {
				allJobs.put(Feature.ANALYZE_JOB.getEnumValue(),
						getExampleList(jsonRepoDir.toString()));
			}

			return Response.ok(Constants.gson.toJson(allJobs)).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	private List<String> getExampleList(String jsonRepoDir) {
		File[] files = new File(jsonRepoDir).listFiles();
		List<String> list = new ArrayList<String>();
		for (File file : files) {
			list.add(file.getName().replace(WebConstants.JSON_EXTENSION, ""));
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Gets the example by name.
	 *
	 * @param jobName
	 *            the job name
	 * @return the example by name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/examples/{jobName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExampleByName(@PathParam(JOB_NAME) final String jobName)
			throws IOException {

		String[] jobTypes = { WebConstants.ANALYZE_DATA, WebConstants.ANALYZE_JOB};

		String exampleDir = System.getenv(WebConstants.JUMBUNE_HOME) + File.separator
				+ EXAMPLE_FILE_LOCATION;

		String slashJsonName = File.separator + jobName + WebConstants.JSON_EXTENSION;
		StringBuilder jobConfigFile = null;

		for (String jobType : jobTypes) {
			jobConfigFile = new StringBuilder(exampleDir).append(jobType).append(slashJsonName);
			if (new File(jobConfigFile.toString()).exists()) {
				break;
			}
		}

		String jobJson = FileUtil.readFileIntoString(jobConfigFile.toString());
		return Response.ok(jobJson).build();
	}

	/**
	 * Gets the gather scheduled job result.
	 *
	 * @param scheduledJobName
	 *            the scheduled job name
	 * @return the gather scheduled job result
	 */
	@GET
	@Path(WebConstants.GATHER_SCHEDULED_JOB_RESULT_SERVICE_URL + "/{scheduledJobName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGatherScheduledJobResult(
			@PathParam("scheduledJobName") final String scheduledJobName) {
		try {
			Map<String, String> schedulerMapper = processSchedulerRequest(scheduledJobName);
			String schedulerResponse = Constants.gson.toJson(schedulerMapper);
			return Response.ok(schedulerResponse).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

	}

	/**
	 * Gets the scheduler job result
	 *
	 * @return the scheduler info list
	 */
	@GET
	@Path(WebConstants.SCHEDULED_TUNING_JOB_RESULT + "/{jobName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getScheduledTuningJobResult(@PathParam(JOB_NAME) final String jobName) {
		try {
			String scheduleJobFolder = ExtendedConfigurationUtil.getUserScheduleJobLocation();
			String tuningJobReportPath = scheduleJobFolder + File.separator + jobName
					+ "/reports/SELF_TUNING";
			File file = new File(tuningJobReportPath);
			if (file.exists()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("SELF_TUNING", FileUtils.readFileToString(file));
				return Response.ok(Constants.gson.toJson(map)).build();
			} else {
				throw new Exception(file + " not exists");
			}
		} catch (Throwable e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path(WebConstants.IS_MAPR)
	@Produces(MediaType.APPLICATION_JSON)
	public Response isMapr() {
		try {
			String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
			boolean isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution);
			Map<String, Boolean> map = new HashMap<String, Boolean>(2);
			map.put(IS_MAPR, isMapr);
			map.put(IS_EMR, Constants.EMRAPACHE.equalsIgnoreCase(hadoopDistribution) 
					|| Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution));
			return Response.ok(Constants.gson.toJson(map)).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Inits the.
	 *
	 * @throws ServletException
	 *             the servlet exception
	 */
	@PostConstruct // for one time initialization
	public void init() throws ServletException {

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				String jHome = System.getenv("JUMBUNE_HOME");
				ObjectInputStream objectInputStream = null;
				InputStream streamIn = null;
				try {
					JMXConnectorCache jmxConnectorCache = JMXConnectorCache.getJMXCacheInstance();
					jmxConnectorCache.clear();
					String jsonFile = jHome + JSON_FILE;
					File file = new File(jsonFile);
					if (file.exists()) {
						streamIn = new FileInputStream(jsonFile);
						objectInputStream = new ObjectInputStream(streamIn);
						BasicJobConfig config = (BasicJobConfig) objectInputStream.readObject();
						// shutTopCmdOnSlaves(config);
						shutDownNettyEventLoopGroup();
					}

				} catch (IOException e) {
					LOGGER.error(JumbuneRuntimeException
							.throwUnresponsiveIOException(e.getStackTrace()));
				} catch (ClassNotFoundException e) {
					LOGGER.error(
							JumbuneRuntimeException.throwClassNotFoundException(e.getStackTrace()));
				} finally {
					try {
						if (objectInputStream != null) {
							objectInputStream.close();
						}
					} catch (IOException e) {
						LOGGER.error(JumbuneRuntimeException
								.throwUnresponsiveIOException(e.getStackTrace()));
					}
				}
			}
		});
	}
	
	

	/**
	 * Service.
	 *
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void service() throws ServletException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(JumbuneInfo.getHome()).append(WebConstants.TMP_DIR_PATH)
				.append(WebConstants.JUMBUNE_STATE_FILE);

		File file = new File(sb.toString());
		if (file.exists()) {
			file.delete();
		}
		String jumbuneHome = System.getenv("JUMBUNE_HOME");
		ObjectInputStream objectinputstream = null;
		InputStream streamIn = null;
		HttpSession session = request.getSession();
		synchronized (session) {
			try {

				JMXConnectorCache.getJMXCacheInstance().clear();

				String jsonFile = jumbuneHome + JSON_FILE;
				File jHomeJsonFile = new File(jsonFile);
				if (jHomeJsonFile.exists()) {
					streamIn = new FileInputStream(jHomeJsonFile);
					objectinputstream = new ObjectInputStream(streamIn);
					LOGGER.warn("Cluster not found and hence not cleaning Job folder on Agent");
					// BasicJobConfig config = (BasicJobConfig)
					// objectinputstream.readObject();
					// cleanUpJumbuneAgentCurrentJobFolder(config);
				}
			} catch (IOException e) {
				LOGGER.error(
						JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			} finally {
				try {
					if (objectinputstream != null) {
						objectinputstream.close();
					}
				} catch (IOException e) {
					LOGGER.error(JumbuneRuntimeException
							.throwUnresponsiveIOException(e.getStackTrace()));
				}
			}

			// TODO remove these lines of code. session is no longer maintained.
			session.removeAttribute("ExecutorServReference");
			session.removeAttribute("ReportsBean");
			session.removeAttribute("config");
		}

		final RequestDispatcher rd = request.getServletContext()
				.getRequestDispatcher(WebConstants.HOME_URL);
		rd.forward(request, response);
	}
	
	private void deleteJob1(String jobName) throws Exception {
		String jobJarPath = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC + jobName;
		File jobJarDirectory = new File(jobJarPath);

		if (jobJarDirectory.exists()) {
			FileUtils.forceDelete(jobJarDirectory);
		}

		String[] jobTypes = { WebConstants.ANALYZE_DATA, WebConstants.ANALYZE_JOB};
		String jsonRepoDir = System.getenv(WebConstants.JUMBUNE_HOME) + WebConstants.JSON_REPO;
		String slashJobDir = File.separator + jobName;

		File dir;
		for (String jobType : jobTypes) {
			dir = new File(jsonRepoDir + jobType + slashJobDir);
			if (!dir.exists()) {
				continue;
			}

			// Checking if there is only one job left in that dir (ie.
			// jobType dir), if yes then delete that also.
			if (dir.getParentFile().list().length == 1) {
				FileUtils.forceDelete(new File(jsonRepoDir + jobType));
			} else {
				FileUtils.forceDelete(dir);
			}
			break;

		}
		dir = new File(JumbuneInfo.getHome() + TUNING_SCHEDULED_DIR + jobName);
		if (dir.exists()) {
			FileUtils.forceDelete(dir);
		}

		dir = new File(JumbuneInfo.getHome() + DQ_JOBS_DIR + jobName);
		if (dir.exists()) {
			FileUtils.forceDelete(dir);
			removeDQTJob(jobName);
		}

		LOGGER.debug("Job : " + jobName + " deleted");
	}
	
	private boolean removeDQTJob(String jobName) throws JumbuneException {
		DataQualityTaskScheduler dqts = new DataQualityTaskScheduler();
		StringBuilder sb = new StringBuilder();
		String directoryPath = sb.append(JumbuneInfo.getHome())
				.append("ScheduledJobs").append(File.separator).append("IncrementalDQJobs")
				.append(File.separator).append(jobName).append(File.separator).toString();
		dqts.deleteCurrentJobEntryFromCron(jobName);
		dqts.deleteJobResult(directoryPath);
		return true;
	}

	/**
	 * Shut down netty event loop group.
	 */
	private static void shutDownNettyEventLoopGroup() {
		EventLoopGroup loopGroup = SingleNIOEventGroup.eventLoopGroup();
		loopGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS);
		try {
			loopGroup.terminationFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isResultFileExists(File file) {
		if (file == null) {
			return false;
		}
		String resultFilePath = file.getAbsolutePath() + WebConstants.JOB_RESPONSE_JSON;
		return new File(resultFilePath).exists();
	}

	private File[] sort(List<File> filesList) {
		File[] filesArray = new File[filesList.size()];
		filesList.toArray(filesArray);
		Arrays.sort(filesArray, new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {

				if (file1.lastModified() > file2.lastModified()) {
					return -1;
				} else if (file1.lastModified() < file2.lastModified()) {
					return 1;
				} else {
					return 0;
				}
			}

		});
		return filesArray;
	}

	/**
	 * Process scheduler request.
	 *
	 * @param scheduledJobName
	 *            the scheduled job name
	 * @return the map
	 */
	private Map<String, String> processSchedulerRequest(String scheduledJobName) {
		String scheduleJobLoc = ExtendedConfigurationUtil.getUserScheduleJobLocation()
				+ File.separator + scheduledJobName;
		String jsonFileLoc = ExtendedConfigurationUtil.getScheduleJobJsonFileLoc(scheduleJobLoc);
		Map<String, String> mapper = new HashMap<String, String>();

		mapper.put("scheduledJobName", scheduledJobName);
		mapper.put("jsonLocation", jsonFileLoc);
		mapper.put("JobName", scheduledJobName);

		return mapper;
	}
	
	private List<String> getSupportedFeatures0() throws IllegalArgumentException, JumbuneException {
		List<String> supportedFeatures = new LinkedList<String>();
			supportedFeatures.add(CLUSTER_ANALYSIS);
			supportedFeatures.add(DATA_QUALITY);
			supportedFeatures.add(JOB_QUALITY);
			return supportedFeatures;
	}

}
