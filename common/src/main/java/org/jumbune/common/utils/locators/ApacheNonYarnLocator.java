package org.jumbune.common.utils.locators;

import java.io.File;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.TaskManagers;

public class ApacheNonYarnLocator extends AbstractDistributionLocator {

  private static final String CONF_DIR = "conf";

  @Override
  public String getHadoopConfDirPath(Cluster cluster) {
    StringBuilder responseAppender = new StringBuilder();
    responseAppender =
        responseAppender.append(getHadoopHomeDirPath(cluster)).append(File.separator)
            .append(CONF_DIR).append(File.separator);
    return responseAppender.toString();
  }

  @Override
  public String getHiveConfDirPath(Cluster cluster) {
    StringBuilder responseAppender = new StringBuilder();
    responseAppender =
        responseAppender.append(getHadoopHomeDirPath(cluster)).append(File.separator)
            .append(CONF_DIR).append(File.separator);
    return responseAppender.toString();
  }
 
}
