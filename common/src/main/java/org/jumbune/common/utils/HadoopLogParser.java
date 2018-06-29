package org.jumbune.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.exception.JumbuneRuntimeException;


/**
 * Gets all details pertaining to a job, and its phases.
 */
public class HadoopLogParser {

	private static final String HADOOP_HOME = "HADOOP_HOME";

	/** The LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(HadoopLogParser.class);

	private static final String YARN_JOB_STATS_UTILITY_CLASS = "org.jumbune.common.yarn.utils.YarnJobStatsUtility";

	private static final String YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD = "parseAndGetJobStats";	

	private static final String HISTORY_DIR_SUFFIX_MAPR_NY = "/var/mapr/cluster/yarn/rm/staging/history/done/*/*/*/*/";

	private static final String HDFS_FILE_GET_COMMAND = "/bin/hadoop fs -get";

	private static final Pattern CONTAINER_ID_EPOCH_PATTERN = Pattern.compile("container_(?:[e\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)");

	private static final Pattern LINE_PATTERN = Pattern.compile("(.*)(container_(?:[e\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+))(.*)");
	
	/** The Constant JHIST. */
	private static final String JHIST = ".jhist";
	String localPath;

	public JobOutput getJobDetails(JumbuneRequest jumbuneRequest, String jobID) throws IOException{
		return getJobDetails(jumbuneRequest.getCluster(), jobID, jumbuneRequest.getJobConfig().getFormattedJumbuneJobName());
	}

	public JobOutput getJobDetails(Cluster cluster, String jobID) throws IOException{
		return getJobDetails(cluster, jobID, cluster.getClusterName() + File.separator);
	}


	/**
	 * Gets the job details.
	 *
	 * @param yamlLoader the loader
	 * @param jobID the job id
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException 
	 */
	public JobOutput getJobDetails(Cluster cluster, String jobID, String jumbuneJobName) throws IOException{

		String appHome = JumbuneInfo.getHome();
		String agentHome = RemotingUtil.getAgentHome(cluster);
		Remoter remoter = RemotingUtil.getRemoter(cluster, appHome);
		String logsHistory = null;
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		logsHistory = changeLogHistoryPathAccToHadoopVersion(HADOOP_HOME,
				hadoopDistribution);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);

		String relLocalPath = null;
			if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR) || hadoopDistribution.equalsIgnoreCase(Constants.EMRMAPR))
			{  
				relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;

				//fetching job configuration xml file. 
				StringBuffer jobIdDir = new StringBuffer(agentHome).append(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);				
				getJobFilesForMapR(cluster, remoter, jobIdDir.toString(), logsHistory, jobID);

				StringBuffer relativeJobIdDir = new StringBuffer(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);
				remoter.receiveLogFiles(relLocalPath, relativeJobIdDir.toString());

				File dir = new  File(appHome + File.separator + relativeJobIdDir);
				File histFile = null;
				for(File f : dir.listFiles() ) {
					if(f.getAbsolutePath().endsWith(JHIST)) {
						histFile = f;
					}
				}
				localPath=histFile.getAbsolutePath();
				UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
				UserGroupInformation.setLoginUser(realUser);
				preProcessHistFile(localPath);

			}else{

				String relativeRemotePath = Constants.JOB_JARS_LOC + jumbuneJobName + File.separator + jobID;
				String tmpPath = File.separator+"tmp"+File.separator+relativeRemotePath+File.separator;
				RemotingUtil.mkDir(builder,remoter,tmpPath);

				String remotePath = agentHome + relativeRemotePath;
				RemotingUtil.mkDir(builder,remoter,remotePath);

				checkAndgetCurrentLogFilePathForYarn(remoter,tmpPath, jobID, cluster);
				CommandWritableBuilder fsGetBuilder;
				//Added copy from tmp to agent home
				fsGetBuilder = new CommandWritableBuilder(cluster, null);
				StringBuffer copyFromTmpToAgent = new StringBuffer().append("cp -r").append(Constants.SPACE).append(tmpPath).append("*").append(Constants.SPACE).append(remotePath);
				fsGetBuilder.addCommand(copyFromTmpToAgent.toString(),false, null, CommandType.FS);
				remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

				relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;
				remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
				String absolutePath = appHome + relLocalPath + jobID + File.separator;
				String fileName = checkAndGetHistFile(absolutePath);
				localPath = absolutePath + fileName;

				if(hadoopDistribution.trim().equalsIgnoreCase(Constants.HORTONWORKS)) {  
					LOGGER.debug("preprocessing history file on path[for HDP] - " + localPath);
					preProcessHistFile(localPath);
				}

				
			}
			java.lang.reflect.Method method = null;
			Class<?> yarnJobStatsUtility = null;
			try {
				yarnJobStatsUtility = Class.forName(YARN_JOB_STATS_UTILITY_CLASS);
				method = yarnJobStatsUtility.getDeclaredMethod(YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD, String.class);
				return 	(JobOutput) method.invoke(yarnJobStatsUtility.newInstance(), localPath);
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
	
		return null;
	}


	/**
	 * This method pre processes jhist file to replace the extra container identification to normal container nomenclature.
	 *
	 * @param localPath denotes the path of the jhist file on jumbune system.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void preProcessHistFile(String localPath) throws IOException {

		File file = new File(localPath);
		String name = file.getName();
		String parentDir = file.getParent();
		String newJhist = parentDir + File.separator + "new.jhist";
		File dest = null;
		if (file.exists()) {
			dest = new File(newJhist);
		}
		BufferedWriter bufferedWriter = null ;
		try {
			bufferedWriter = Files.newBufferedWriter(Paths.get(dest.getAbsolutePath()), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE);
			List<String> lines = Files.readAllLines(Paths.get(localPath), StandardCharsets.UTF_8);
			Matcher m = null;
			StringBuilder builder = null;
			for (String line : lines) {
				m = LINE_PATTERN.matcher(line);
				if (m.matches()) {
					builder = new StringBuilder();
					builder.append(m.group(1)).append(getProcessedContainerId(m.group(2))).append(m.group(3));
				} else {
					builder = new StringBuilder(line);
				}
				bufferedWriter.write(builder.toString());
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} finally{
			if(bufferedWriter != null){
				bufferedWriter.close();
			}
		}

		String newHistoryFile = parentDir + File.separator + name;
		file.delete();
		Files.copy(Paths.get(newJhist), Paths.get(newHistoryFile), StandardCopyOption.REPLACE_EXISTING);
		dest.delete();

	}


	/**
	 * Gets the processed container id.
	 *
	 * @param id the id
	 * @return the processed container id
	 */
	private String getProcessedContainerId(String id) {
		StringBuilder builder = new StringBuilder("container");
		Matcher m = CONTAINER_ID_EPOCH_PATTERN.matcher(id);
		if (m.matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				builder.append(Constants.UNDERSCORE + m.group(i));
			}
		}
		return builder.toString();

	}


	private void getJobFilesForMapR(Cluster cluster, Remoter remoter, String jobIdDir,
			String logsHistory, String jobID) {

		StringBuilder getCommand = new StringBuilder()
				.append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND)
				.append(Constants.SPACE).append(logsHistory).append("*")
				.append(jobID).append("*")
				.append(Constants.SPACE).append(jobIdDir);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		RemotingUtil.mkDir(builder,remoter, jobIdDir);		
		builder.clear();
		builder.addCommand(getCommand.toString(), false, null, CommandType.HADOOP_FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

	}

	private String checkAndGetHistFile(String remotePath) {
		String fileName = null;

		File jobFilePath = new File(remotePath);
		if (jobFilePath.exists() && jobFilePath.isDirectory()) {
			File[] getJobFiles = jobFilePath.listFiles();
			for (File file : getJobFiles) {
				if (file.getName().endsWith(".jhist")) {
					fileName = file.getName();
				}
			}
		}
		return fileName;
	}


	/**
	 * This method fetches the intermediate and final .jhist file locations.
	 *
	 * @param remoter the remoter
	 * @param relRemotePath the rel remote path
	 * @param jobID the job id
	 * @param cluster the cluster
	 */
	private void checkAndgetCurrentLogFilePathForYarn( Remoter remoter,String relRemotePath, String jobID, Cluster cluster){
		String historyIntLocation = RemotingUtil.getHistoryIntermediateLocation(cluster);
		String historyDoneLocation = RemotingUtil.getHistoryDoneLocation(cluster);
		checkAndgetCurrentLogFilePathForYarn(cluster, remoter, historyIntLocation, historyDoneLocation ,relRemotePath, jobID);
	}




	/**
	 * 	This api is used to fetch the .hist files containing the job details.
	 * We first try in the intermediate folder and then fall back to done folder on HDFS.
	 * At least one of them is expected to fetch the file.
	 *
	 * @param jobID the job id is the jobname that ran on hadoop
	 * @param remoter the remoter
	 * @param historyInterLocation the intermediate location of .jhist files on hdfs
	 * @param historyDoneLocation  the final location of .jhist files on hdfs
	 * @param builder the builder
	 * @param JobConfig the job config
	 * @param agentHome the agent home
	 * @param relRemotePath the rel remote path
	 * @return the log file path for yarn
	 * @throws FileNotFoundException 
	 */
	private void checkAndgetCurrentLogFilePathForYarn(Cluster cluster, Remoter remoter, String historyInterLocation,String historyDoneLocation,
			String relRemotePath, String jobID){
		CommandWritableBuilder fsGetBuilder;
		StringBuffer doneCommandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(historyDoneLocation)
				.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);

		StringBuffer intermediateCommandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(historyInterLocation)
				.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);
		fsGetBuilder = new CommandWritableBuilder(cluster, null);
		if(!cluster.getHadoopUsers().isHasSingleUser()){
			fsGetBuilder.addCommand(intermediateCommandToExecute.toString(),false, null, CommandType.MAPRED).setMethodToBeInvoked(RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

			fsGetBuilder = new CommandWritableBuilder(cluster, null);
			fsGetBuilder.addCommand(doneCommandToExecute.toString(),false, null, CommandType.MAPRED).setMethodToBeInvoked(RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
		}else{
			fsGetBuilder.addCommand(intermediateCommandToExecute.toString(),false, null, CommandType.MAPRED);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

			fsGetBuilder = new CommandWritableBuilder(cluster, null);
			fsGetBuilder.addCommand(doneCommandToExecute.toString(),false, null, CommandType.MAPRED).populate(cluster, null);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
		}
	}

	/**
	 * Change log history path acc to hadoop version.
	 *
	 * @param remoteHadoop the remote hadoop
	 * @param hadoopVersion constants for hadoop specific versions.
	 * @param user TODO
	 * @return the string
	 */
	public String changeLogHistoryPathAccToHadoopVersion(String remoteHadoop,
			String hadoopDistribution) {
		String logsHistory = null;
		if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR) || (hadoopDistribution.equalsIgnoreCase(Constants.EMRMAPR))) {
			logsHistory =HISTORY_DIR_SUFFIX_MAPR_NY;
		}		
		return logsHistory;
	}

}
