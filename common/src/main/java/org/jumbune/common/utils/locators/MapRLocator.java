package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.RemotingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapRLocator extends AbstractDistributionLocator {

	private static final Logger LOGGER = LogManager
			.getLogger(MapRLocator.class);
	private static final String CONF = "conf";
	private static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME";

	@Override
	public String getHadoopConfDirPath(JobConfig config) {
		String response = RemotingUtil.executeCommand(config, ECHO_HADOOP_HOME);
		String confDir = response + File.separator + CONF + File.separator;
		return confDir;
	}
}
