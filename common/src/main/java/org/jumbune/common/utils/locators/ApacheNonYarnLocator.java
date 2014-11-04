package org.jumbune.common.utils.locators;

import java.io.File;
import org.jumbune.common.yaml.config.YamlConfig;

public class ApacheNonYarnLocator extends AbstractDistributionLocator {

  private static final String CONF_DIR = "conf";

  @Override
  public String getHadoopConfDirPath(YamlConfig config) {
    StringBuilder responseAppender = new StringBuilder();
    responseAppender =
        responseAppender.append(getHadoopHomeDirPath(config)).append(File.separator)
            .append(CONF_DIR).append(File.separator);
    return responseAppender.toString();
  }

}
