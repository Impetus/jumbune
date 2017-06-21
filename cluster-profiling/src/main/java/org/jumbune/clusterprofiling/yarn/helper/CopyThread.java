package org.jumbune.clusterprofiling.yarn.helper;

import static org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper.CLUSTER_PROFILING;
import static org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper.HDFS_FILE_GET_COMMAND;
import static org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper.JHIST;
import static org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper.PREFIX;
import static org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper.RACKWARE;
import static org.jumbune.common.utils.Constants.SPACE;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.utils.ExtendedConstants;
/**
 * The Class CopyThread. Thread for fetching history files to jumbune.
 */
class CopyThread implements Runnable {

	/** The application report. */
	List<String> jobList = null;

	/** The cluster. */
	Cluster cluster = null;

	/** The jumbune home. */
	String jumbuneHome = null;

	/** The agent home. */
	String agentHome = null;

	/** The history location. */
	String historyLocation = null;
	
	private final static Logger LOGGER = LogManager.getLogger(CopyThread.class);

	/**
	 * Instantiates a new copy thread.
	 *
	 * @param applicationReport
	 *            the application report
	 * @param latch
	 *            the latch
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param cluster
	 *            the cluster
	 */
	public CopyThread(List<String> jobList, Cluster cluster) {
		this.jobList = jobList;
		this.cluster = cluster;
		jumbuneHome = EnterpriseJobConfig.getJumbuneHome();
		agentHome = RemotingUtil.getAgentHome(cluster);
		historyLocation = RemotingUtil.getHistoryDoneLocation(cluster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			for (int i = 0; i < jobList.size(); i++) {
				String jobId = jobList.get(i);
				StringBuffer sb = new StringBuffer().append(jumbuneHome)
						.append(ExtendedConstants.JOB_JARS_LOC)
						.append(cluster.getClusterName())
						.append(CLUSTER_PROFILING).append(jobId)
						.append(File.separator);
				StringBuffer histFilePath = new StringBuffer()
						.append(jumbuneHome)
						.append(ExtendedConstants.JOB_JARS_LOC)
						.append(cluster.getClusterName())
						.append(CLUSTER_PROFILING).append(jobId)
						.append(RACKWARE).append(jobId).append(JHIST);
				File file = new File(histFilePath.toString());

				if (!file.exists()) {
					StringBuffer receivePath = new StringBuffer()
							.append(ExtendedConstants.JOB_JARS_LOC)
							.append(cluster.getClusterName())
							.append(CLUSTER_PROFILING).append(jobId)
							.append(File.separator);
					Remoter remoter = RemotingUtil.getRemoter(cluster);
					CommandWritableBuilder lsBuilder = new CommandWritableBuilder(
							cluster, null);
					StringBuffer remotePath = new StringBuffer()
							.append(ExtendedConstants.JOB_JARS_LOC)
							.append(cluster.getClusterName())
							.append(CLUSTER_PROFILING).append(jobId)
							.append(RACKWARE);
					StringBuffer path = new StringBuffer().append(agentHome)
							.append(remotePath);
					RemotingUtil.mkDir(lsBuilder, remoter, path.toString());
					StringBuffer command = new StringBuffer(
							Constants.HADOOP_ENV_VAR_NAME)
									.append(HDFS_FILE_GET_COMMAND).append(SPACE)
									.append(historyLocation).append(jobId)
									.append("*[^.xml]").append(" ").append(path)
									.append(jobId).append(JHIST);
					LOGGER.debug("Command Executed : " + command.toString());
					if (cluster.getHadoopUsers().isHasSingleUser()) {
						lsBuilder.addCommand(command.toString(), false, null,
								CommandType.MAPRED);
					} else {
						lsBuilder
								.addCommand(command.toString(), false, null,
										CommandType.MAPRED)
								.setMethodToBeInvoked(
										RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
					}
					remoter.fireAndForgetCommand(
							lsBuilder.getCommandWritable());

					// zip hist file;
					lsBuilder.clear();
					command = new StringBuffer().append("zip -j").append(SPACE)
							.append(agentHome).append(remotePath).append(jobId)
							.append(JHIST).append(".zip").append(SPACE)
							.append(agentHome).append(remotePath).append(jobId)
							.append(JHIST);
					lsBuilder.addCommand(command.toString(), false, null,
							CommandType.FS);
					remoter.fireAndForgetCommand(
							lsBuilder.getCommandWritable());

					// delete hist file;
					lsBuilder.clear();
					command = new StringBuffer().append(" rm").append(SPACE)
							.append(agentHome).append(remotePath).append(jobId)
							.append(JHIST);
					lsBuilder.addCommand(command.toString(), false, null,
							CommandType.FS);
					remoter.fireAndForgetCommand(
							lsBuilder.getCommandWritable());

					// copy zip file to jumbunehome;
					remoter.receiveLogFiles(receivePath.toString(),
							remotePath.toString() + jobId + JHIST + ".zip");

					// unzip hist file
					lsBuilder.clear();
					file = new File(sb.append(jobId).append(JHIST)
							.append(".zip").toString());
					if (file.exists()) {
						command = new StringBuffer().append("unzip")
								.append(SPACE).append(PREFIX)
								.append(cluster.getClusterName())
								.append(CLUSTER_PROFILING).append(jobId)
								.append(File.separator).append(jobId)
								.append(JHIST).append(".zip").append(SPACE)
								.append("-d").append(SPACE).append(PREFIX)
								.append(cluster.getClusterName())
								.append(CLUSTER_PROFILING).append(jobId)
								.append(RACKWARE);
						LOGGER.debug("Command Executed " + command.toString());
						Runtime.getRuntime().exec(command.toString());
					}
				}

			}
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException
					.throwUnresponsiveIOException(e.getStackTrace()));
		}
	}
}