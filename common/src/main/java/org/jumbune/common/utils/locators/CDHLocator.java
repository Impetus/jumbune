package org.jumbune.common.utils.locators;

import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlConfig;

public class CDHLocator extends AbstractDistributionLocator {

	private static final String error_message = "Failed to detect Hadoop! Did you miss setting some environment variables?";
	
	private static final String LS_CDH_POSTFIX_PART = "ls /etc/hadoop -Rl | grep 'conf ->'";

	@Override
	public String getHadoopConfDirPath(YamlConfig config) {
		String possibleDirList = RemotingUtil.executeCommand(config, WHEREIS_HADOOP);
		checkEmptyDir(possibleDirList);
		String[] splittedDirList = possibleDirList.split("\\s+");
		String absoluteDirPath = null;
		for (int index = 1; index < splittedDirList.length; index++) {
			absoluteDirPath = getAbsoluteConfDirPath(splittedDirList[index],
					config);
			if (absoluteDirPath != null && !absoluteDirPath.isEmpty()) {
				break;
			}
		}
		absoluteDirPath = absoluteDirPath.trim();
		checkEmptyDir(absoluteDirPath);
		return absoluteDirPath;
	}

	private void checkEmptyDir(String possibleDirList) {
		if (possibleDirList == null || possibleDirList.isEmpty()) {
			throw new IllegalArgumentException(error_message);
		}
	}

	private String getAbsoluteConfDirPath(String dir, YamlConfig config) {
		String result = null, response = null;
		if (dir == null || dir.trim().isEmpty() || !dir.contains("/")) {
			throw new IllegalArgumentException(error_message);
		}
		response = RemotingUtil.executeCommand(config, LS_PREFIX_PART + dir
				+ LS_CDH_POSTFIX_PART);
		if (response != null || !response.isEmpty()) {
	    result = response.substring((response.indexOf(">") + 1),
					response.length());
		}

		return result;
	}

}
