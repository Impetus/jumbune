package org.jumbune.common.utils.locators;

import org.jumbune.common.job.JobConfig;

public interface HadoopDistributionLocator {

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param jobConfig
   * @return String directory location of hadoop configurations
   */
  String getHadoopConfDirPath(JobConfig jobConfig);

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param jobConfig
   * @return String directory location of hadoop configurations
   */
  String getHadoopHomeDirPath(JobConfig jobConfig);

}
