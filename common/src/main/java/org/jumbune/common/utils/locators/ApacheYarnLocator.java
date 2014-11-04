package org.jumbune.common.utils.locators;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlConfig;

public class ApacheYarnLocator extends AbstractDistributionLocator {

	private static final String CONF_DIR = "etc" + File.separator + "hadoop";
	private static final String ECHO_HADOOP_CONF_DIR = "echo $HADOOP_CONF_DIR";

	public static final Logger LOGGER = LogManager.getLogger(ApacheYarnLocator.class);

	@Override
	public String getHadoopConfDirPath(YamlConfig config) {
		StringBuilder responseAppender = new StringBuilder();
		String response = RemotingUtil.executeCommand(config, ECHO_HADOOP_CONF_DIR);
		LOGGER.info("Response for hadoop conf" + response);
		if (response != null || !response.isEmpty()) {
			return response;
		}
		responseAppender = responseAppender
				.append(getHadoopHomeDirPath(config)).append(File.separator)
				.append(CONF_DIR);

		return responseAppender.toString();
	}
}
