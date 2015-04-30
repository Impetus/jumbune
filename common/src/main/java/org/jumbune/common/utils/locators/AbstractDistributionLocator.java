package org.jumbune.common.utils.locators;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.utils.exception.JumbuneRuntimeException;

public abstract class AbstractDistributionLocator implements
		HadoopDistributionLocator {

	public static final Logger LOGGER = LogManager
			.getLogger(AbstractDistributionLocator.class);
	protected static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME \n \n ";
	private static final String LS_POSTFIX_PART = " -Rl | grep /";
	protected static final String LS_PREFIX_PART = "ls ";
	protected static final String WHEREIS_HADOOP = "whereis hadoop";

	/* (non-Javadoc)
	 * @see org.jumbune.common.utils.HadoopDistributionLocator#getHadoopHomeDirPath(org.jumbune.common.yaml.config.YamlConfig)
	 */
	@Override
	public String getHadoopHomeDirPath(YamlConfig config) {
		LOGGER.debug("Trying to locate Hadoop with echo $HADOOP_HOME");
		String hadoopHome = RemotingUtil.executeCommand(config,
				ECHO_HADOOP_HOME);
		LOGGER.debug("Hadoop location with echo $HADOOP_HOME " + hadoopHome);
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			LOGGER.debug("Trying to locate Hadoop with where is hadoop");
			String possibleHadoopHome = RemotingUtil.executeCommand(config,
					WHEREIS_HADOOP);
			validateHadoopLocation(possibleHadoopHome);
			String[] hadoopSplits = possibleHadoopHome.split("\\s+");
			LOGGER.debug("Found entries of whereis hadoop:"
					+ Arrays.toString(hadoopSplits));
			for (String split : hadoopSplits) {
				if (split.contains("/lib/") && containsHadoopLib(split, config)) {
					hadoopHome = split;
				}
			}
			hadoopHome = hadoopHome.replace("\n", "");
			validateHadoopLocation(hadoopHome);
		}
		return hadoopHome;
	}

	private void validateHadoopLocation(String hadoopHome) {
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			throw new JumbuneRuntimeException(
					"Unable to find location of Hadoop! Please make sure Hadoop deployment instruction are followed as recommended, then retry running the deployment again.");
		}
	}

	private boolean containsHadoopLib(String location, YamlConfig config) {
		boolean result = false;
		String listedDirectory = RemotingUtil.executeCommand(config,
				LS_PREFIX_PART + location + LS_POSTFIX_PART);
		if (listedDirectory != null && !listedDirectory.isEmpty()) {
			String[] directoryList = listedDirectory.split("\n");
			for (int index = 1; index < directoryList.length; index++) {
				if (directoryList[index].contains("lib")) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
