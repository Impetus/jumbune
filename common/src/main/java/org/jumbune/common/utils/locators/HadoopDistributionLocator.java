package org.jumbune.common.utils.locators;

import org.jumbune.common.yaml.config.YamlConfig;

public interface HadoopDistributionLocator {

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param config
   * @return String directory location of hadoop configurations
   */
  String getHadoopConfDirPath(YamlConfig config);

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param config
   * @return String directory location of hadoop configurations
   */
  String getHadoopHomeDirPath(YamlConfig config);

}
