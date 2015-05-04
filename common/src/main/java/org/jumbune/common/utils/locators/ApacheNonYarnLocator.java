package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.job.JobConfig;

public class ApacheNonYarnLocator extends AbstractDistributionLocator {

  private static final String CONF_DIR = "conf";

  @Override
  public String getHadoopConfDirPath(JobConfig jobConfig) {
    StringBuilder responseAppender = new StringBuilder();
    responseAppender =
        responseAppender.append(getHadoopHomeDirPath(jobConfig)).append(File.separator)
            .append(CONF_DIR).append(File.separator);
    return responseAppender.toString();
  }

}
