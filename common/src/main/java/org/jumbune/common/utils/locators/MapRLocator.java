package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.TaskManagers;
import org.jumbune.common.utils.RemotingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapRLocator extends AbstractDistributionLocator {

	private static final Logger LOGGER = LogManager
			.getLogger(MapRLocator.class);
	private static final String CONF = "conf";
	private static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME";
	private static final String ECHO_HIVE_HOME = "echo $HIVE_HOME";

	@Override
	public String getHadoopConfDirPath(Cluster cluster) {
		String response = RemotingUtil.executeCommand(cluster, ECHO_HADOOP_HOME);
		String confDir = response + File.separator + CONF + File.separator;
		return confDir;
	}
	
	public String getHiveConfDirPath(Cluster cluster) {
		String response = RemotingUtil.executeCommand(cluster, ECHO_HIVE_HOME);
		String confDir = response + File.separator + CONF + File.separator;
		return confDir;
	}
	
}
