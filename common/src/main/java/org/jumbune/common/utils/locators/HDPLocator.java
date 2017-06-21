package org.jumbune.common.utils.locators;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.RemotingUtil;

/**
 * The Class HDPLocator is reponsible for locating the configuration files folder in HDP.
 */
public class HDPLocator extends AbstractDistributionLocator{
	
	
	private static final String error_message = "Failed to detect Hadoop! Did you miss setting some environment variables?";
	
	private static final String LS_HDP_POSTFIX_PART = " -Rl | grep 'conf ->'";
	
	private static final String expectedConf = "/etc/hadoop";

	private static final String expectedHiveConf = "/etc/hive";

	public static final Logger LOGGER = LogManager.getLogger(HDPLocator.class);


	@Override
	public String getHadoopConfDirPath(Cluster cluster) {
		
		String absoluteDirPath = null;
		absoluteDirPath = getAbsoluteConfDirPath(expectedConf, cluster);
		if (absoluteDirPath == null || absoluteDirPath.isEmpty()) {
			throw new IllegalArgumentException(
					"Failed to get configuration directory. Expected to get a linked configuration from "
							+ expectedConf);
		}
		absoluteDirPath = absoluteDirPath.trim();
		return absoluteDirPath;
	}
	
	/**
	 * Gets the absolute conf dir path.
	 *
	 * @param dir the dir
	 * @param cluster the cluster
	 * @return the absolute conf dir path
	 */
	private String getAbsoluteConfDirPath(
			String dir, Cluster cluster) {
		
		String result = null, response = null;
		if (dir == null || dir.trim().isEmpty() || !dir.contains("/")) {
			throw new IllegalArgumentException(error_message);
		}
		response = RemotingUtil.executeCommand(cluster, LS_PREFIX_PART + dir
				+ LS_HDP_POSTFIX_PART);
		if (response != null && !response.isEmpty() && response.indexOf(">")!=-1) {
	    result = response.substring((response.indexOf(">") + 1),
					response.length());
		result = result.endsWith(File.separator)?result:result.trim()+File.separator;
		}
		LOGGER.debug("Found linked Hadoop conf path:"+result);
		return result;
	}
	
	public String getHiveConfDirPath(Cluster cluster) {
		
		String absoluteDirPath = null;
		absoluteDirPath = getAbsoluteConfDirPath(expectedHiveConf, cluster);
		if (absoluteDirPath == null || absoluteDirPath.isEmpty()) {
			throw new IllegalArgumentException(
					"Failed to get configuration directory. Expected to get a linked configuration from "
							+ expectedConf);
		}
		absoluteDirPath = absoluteDirPath.trim();
		return absoluteDirPath;
	}


}
