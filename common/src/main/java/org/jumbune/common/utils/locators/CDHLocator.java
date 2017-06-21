package org.jumbune.common.utils.locators;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.RemotingUtil;

public class CDHLocator extends AbstractDistributionLocator {

	private static final String error_message = "Failed to detect Hadoop! Did you miss setting some environment variables?";
	
	private static final String LS_CDH_POSTFIX_PART = " -Rl | grep ' ->'";
	
	private static final String expectedConf = "/etc/hadoop";
	
	private static final String expectedHiveConf = "/etc/hive";

	private static final String usrBinDir = "/usr/bin/hadoop";

	public static final Logger LOGGER = LogManager
			.getLogger(CDHLocator.class);

	@Override
	public String getHadoopConfDirPath(Cluster cluster) {
	
		String absoluteDirPath = null;
		absoluteDirPath = getAbsoluteConfDirPath(expectedConf, cluster);
		if (absoluteDirPath == null || absoluteDirPath.isEmpty()) {
			   throw new IllegalArgumentException(
					"Failed to get configuration directory. Expected to get a linked configuration directory");
		}
		LOGGER.debug("Final linked Hadoop conf path:" + absoluteDirPath);
		absoluteDirPath = absoluteDirPath.trim();
		checkEmptyDir(absoluteDirPath);
		return absoluteDirPath;
	}

	public String getHiveConfDirPath(Cluster cluster) {		
		String absoluteDirPath = null;
		absoluteDirPath = getAbsoluteConfDirPath(expectedHiveConf, cluster);
		if (absoluteDirPath == null || absoluteDirPath.isEmpty()) {
			   throw new IllegalArgumentException(
					"Failed to get configuration directory. Expected to get a linked configuration directory");
		}
		LOGGER.debug("Final linked Hive conf path:" + absoluteDirPath);
		absoluteDirPath = absoluteDirPath.trim();
		checkEmptyDir(absoluteDirPath);
		return absoluteDirPath;
	}
	
	private boolean checkEmptyDir(String possibleDirList) {
		if (possibleDirList == null || possibleDirList.isEmpty()) {
			return true;
		}
		return false;
	}

	private String getAbsoluteConfDirPath(
			String dir, Cluster cluster) {
		String result = null, response = null;
		if (dir == null || dir.trim().isEmpty() || !dir.contains("/")) {
			throw new IllegalArgumentException(error_message);
		}
		response = RemotingUtil.executeCommand(cluster, LS_PREFIX_PART + dir
				+ LS_CDH_POSTFIX_PART);
		if (response != null && !response.isEmpty() && response.indexOf(">")!=-1) {
	    result = response.substring((response.indexOf(">") + 1),
					response.length());
		result = result.endsWith(File.separator)?result:result.trim();
		}
		LOGGER.debug("Found linked Hadoop conf path:"+result);
		if(result!=null){
			String recursiveResponse = getAbsoluteConfDirPath(result, cluster);
			if(recursiveResponse!=null){
				result = recursiveResponse;
			}
		}
		return result;
	}

}
