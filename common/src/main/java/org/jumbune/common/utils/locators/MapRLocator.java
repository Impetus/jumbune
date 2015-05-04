package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.RemotingUtil;

public class MapRLocator extends AbstractDistributionLocator {

	private static final String CONF = "conf";
	@Override
	public String getHadoopConfDirPath(JobConfig config) {
		String response = RemotingUtil.executeCommand(config, WHEREIS_HADOOP);
		String confDir = null;
		String[] splittedResponse = response.split("\\s+");
		for (int i = 1; i < splittedResponse.length; i++) {
      // iterate over where is hadoop response whichis response
			if (splittedResponse[i].contains(CONF)) {
				confDir = splittedResponse[i] + File.separator;
				break;
			}
		}
		return confDir;
	}
}
