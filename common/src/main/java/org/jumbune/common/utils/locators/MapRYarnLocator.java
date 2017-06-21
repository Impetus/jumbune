package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.TaskManagers;
import org.jumbune.common.utils.RemotingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapRYarnLocator extends AbstractDistributionLocator {

	private static final Logger LOGGER = LogManager
			.getLogger(MapRYarnLocator.class);
	private static final String CONF_DIR = "etc" + File.separator + "hadoop" + File.separator;
	private static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME";

	@Override
	public String getHadoopConfDirPath(Cluster cluster) {
		String response = RemotingUtil.executeCommand(cluster, ECHO_HADOOP_HOME);
		String confDir = response + File.separator + CONF_DIR + File.separator;
		return confDir;
	}
	
	public String getHiveConfDirPath(Cluster cluster) {
		String response = RemotingUtil.executeCommand(cluster, ECHO_HADOOP_HOME);
		String confDir = response + File.separator + CONF_DIR + File.separator;
		return confDir;
	}
	
}
