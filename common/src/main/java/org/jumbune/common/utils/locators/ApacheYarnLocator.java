package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.beans.cluster.Cluster;

public class ApacheYarnLocator extends AbstractDistributionLocator {

	private static final String CONF_DIR = "etc" + File.separator + "hadoop" + File.separator;

	@Override
	public String getHadoopConfDirPath(Cluster cluster) {
		StringBuilder responseAppender = new StringBuilder();
		responseAppender = responseAppender
				.append(getHadoopHomeDirPath(cluster)).append(File.separator)
				.append(CONF_DIR);

		return responseAppender.toString();
	}

	public String getHiveConfDirPath(Cluster cluster) {
		StringBuilder responseAppender = new StringBuilder();
		responseAppender = responseAppender
				.append(getHadoopHomeDirPath(cluster)).append(File.separator)
				.append(CONF_DIR);

		return responseAppender.toString();
	}

}
