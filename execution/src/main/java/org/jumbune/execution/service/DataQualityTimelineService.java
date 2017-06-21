package org.jumbune.execution.service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.ConsoleLogUtil;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import com.jcraft.jsch.JSchException;

public class DataQualityTimelineService extends CoreExecutorService {
	private static final String TEMP = "tmp";
	private static final String SCHEDULED_JSON_FILE = "scheduledJson.json";
	private static final String USER_DATE_PATTERN = "yyyy/MM/dd HH:mm";
	private static final Logger LOGGER = LogManager
			.getLogger(DataQualityTimelineService.class);
	
	private static final String DQ_DIR = "/ScheduledJobs/IncrementalDQJobs/";
	
	private static final String CHECKSUM_FILE = ".checksum";

	private static String checksumFilePath = null;
	
	public DataQualityTimelineService() throws JumbuneException {
	}

	

	public static void main(String[] args) throws JumbuneException,
			InterruptedException {
		DataQualityTimelineService service = new DataQualityTimelineService();
		try {
			ReportsBean reports = new ReportsBean();
			String jumbunehome = args[1];
			JumbuneRequest jumbuneRequest = null;
			
			String scheduledJobDirectoryPath = args[0];

			System.setProperty("JUMBUNE_HOME", jumbunehome);
			JobConfig.setJumbuneHome(args[1]);
			scheduledJobDirectoryPath = scheduledJobDirectoryPath.substring(0,
					scheduledJobDirectoryPath.lastIndexOf(File.separator));

			scheduledJobDirectoryPath = scheduledJobDirectoryPath
					+ File.separator + SCHEDULED_JSON_FILE;
			jumbuneRequest = loadJob(scheduledJobDirectoryPath, jumbunehome);
			String jobName = jumbuneRequest.getJobConfig().getJumbuneJobName();
			
			checksumFilePath = jumbunehome + DQ_DIR + jobName + File.separator + CHECKSUM_FILE;
			
			File file = new File(new StringBuilder().append(jumbunehome)
					.append(File.separator).append(TEMP)
					.append(File.separator).append("DQScheduler").append(jobName).append(".token")
					.toString());
			if(!file.getParentFile().exists() && file.getParentFile().mkdirs());
			if (file.exists()) {
				ConsoleLogUtil.CONSOLELOGGER
						.info("Other scheuled Data Quality job is running, could not execute this iteration !!!!!");
				ConsoleLogUtil.CONSOLELOGGER
						.info("Shutting down DataQuality job !!!!!");
				System.exit(1);
			} else {
				boolean launchFlag = true;
				File checksumFile = new File(checksumFilePath);
				Path path = Paths.get(checksumFilePath);
				if (!checksumFile.exists()) {
					Files.write(path, service.computeChecksumOfInputData(jumbuneRequest).getBytes(),
							StandardOpenOption.CREATE);
				} else {
					String persistedChecksum = new String(Files.readAllBytes(path));
					String computedChecksum = service.computeChecksumOfInputData(jumbuneRequest);
					if (persistedChecksum.equals(computedChecksum)) {
						launchFlag = false;
						ConsoleLogUtil.CONSOLELOGGER.info("No data changes have been detected on input path ["
								+ jumbuneRequest.getJobConfig().getHdfsInputPath()
								+ "] and hence not launching DQT Job");
					} else {
						Files.write(path, computedChecksum.getBytes(), StandardOpenOption.CREATE);
					}
				}
				if (launchFlag) {
					file.createNewFile();
					service.run(jumbuneRequest, reports, jumbunehome);
				}					
			}
			System.exit(0);
		} catch (Exception e) {
			LOGGER.error(e);
			System.exit(1);
		} 
	}
	
	/**
	 * Compute checksum of input data. 
	 * This method computes consolidated checksum of all the files present on the HDFS input path.
	 *
	 * @param request the JumbuneRequest
	 * @return consolidated checksum
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private String computeChecksumOfInputData(JumbuneRequest request) throws NoSuchAlgorithmException {
		String hdfsInputPath = request.getJobConfig().getHdfsInputPath();
		RemoteFileUtil rfu = new RemoteFileUtil();
		Cluster cluster = request.getCluster();
		List<String> paths = rfu.getHDFSPathsRecursively(cluster, hdfsInputPath, false);
		MessageDigest md = MessageDigest.getInstance("MD5");
		for(String path : paths) {
			md.update(path.getBytes());
		}	
		return new BigInteger(1,md.digest()).toString(16);
	}

	private JumbuneRequest run(JumbuneRequest jumbuneRequest, ReportsBean reports, String jumbuneHome)
			throws JumbuneException, IOException, JSchException,
			InterruptedException {
		
		try{
		Config config = jumbuneRequest.getConfig();
		loadInitialSetup(jumbuneRequest);
		disableModules(config);
		JobConfigUtil.createJumbuneDirectories(jumbuneRequest);
		createJobJarFolderOnAgent(jumbuneRequest);
		startExecution(reports, jumbuneRequest);
		}finally{
			try {
				LOGGER.debug("Cleaning up Agent and Slaves temporary directories");
				cleanUpJumbuneAgentCurrentJobFolder(jumbuneRequest);
				cleanUpSlavesTempFldr(jumbuneRequest);
				deleteTokenFile(jumbuneRequest.getJobConfig().getJumbuneJobName());
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
			ConsoleLogUtil.CONSOLELOGGER.debug("clean up done");
			
		}
		
		return jumbuneRequest;
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

	protected void deleteTokenFile(String jobName) {
		String jumbunehome = JobConfig.getJumbuneHome();
		File file = new File(new StringBuilder().append(jumbunehome)
				.append(File.separator).append(TEMP).append(File.separator)
				.append("DQScheduler").append(jobName).append(".token").toString());
		LOGGER.debug("token file has been deleted status [" + file.delete()
				+ "]");

	}

	private void startExecution(ReportsBean reports, JumbuneRequest jumbuneRequest)
			throws IOException, JumbuneException {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		List<Processor> processors = getProcessorChain(
				jobConfig, true);
		int index = 0;
		for (Processor p : processors) {
			Map<Parameters, String> params = new HashMap<Parameters, String>();
			String processName = "PROCESS" + ++index;
			reports.addInitialStatus(processName);
			params.put(Parameters.PROCESSOR_KEY, processName);
			try {
				p.process(jumbuneRequest, reports, params);
			} catch (JumbuneException e) {
				LOGGER.error(processName + " completed with errors !!!");
			} finally {
				reports.markProcessAsComplete(processName);
				try{
					cleanUpJumbuneAgentCurrentJobFolder(jumbuneRequest);
					cleanUpSlavesTempFldr(jumbuneRequest);
					deleteTokenFile(jobConfig.getJumbuneJobName());
				}catch(InterruptedException e){
					LOGGER.error(JumbuneRuntimeException.throwInterruptedException(e.getStackTrace()));
				}finally{
					LOGGER.debug("clean up to all slaves has been completed");
				}
			}
		}
		ConsoleLogUtil.CONSOLELOGGER
				.info("!!! Data Quality timeline process has been finished succesfully !!!\n ");

	}

	private void disableModules(Config config) {
		JobConfig jobConfig = (JobConfig)config;
		jobConfig.setEnableStaticJobProfiling(Enable.FALSE);
		jobConfig.setHadoopJobProfile(Enable.FALSE);
		jobConfig.setEnableDataProfiling(Enable.FALSE);
	}

	private void loadInitialSetup(JumbuneRequest jumbuneRequest) throws JumbuneException {
		String agentHome = RemotingUtil.getAgentHome(jumbuneRequest.getCluster());
		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		processClassPathElement(cse, agentHome);
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		if (jobConfig.getClasspath() == null) {
			jobConfig.setClasspath(new Classpath());
		}
		jobConfig.getClasspath().setJumbuneSupplied(cse);
		if (!JobConfigUtil.isJumbuneSuppliedJarPresent(jumbuneRequest.getCluster())) {
			JobConfigUtil
					.sendJumbuneSuppliedJarOnAgent(jumbuneRequest.getCluster(), cse, agentHome);
		}

		DateFormat dateFormat = new SimpleDateFormat(USER_DATE_PATTERN);
		Date date = new Date();
		jobConfig.getDataQualityTimeLineConfig().setTime(dateFormat.format(date));
	}

	private void processClassPathElement(ClasspathElement cse, String agentHome) {
		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace("AGENT_HOME", agentHome);
		}
	}

}
