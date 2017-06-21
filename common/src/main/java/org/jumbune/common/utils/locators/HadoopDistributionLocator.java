package org.jumbune.common.utils.locators;

import org.jumbune.common.beans.cluster.Cluster;

public interface HadoopDistributionLocator {

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param jobConfig
   * @return String directory location of hadoop configurations
   */
	 String getHadoopConfDirPath(Cluster cluster);

  /***
   * Gets the hadoop configuration directory path
   * 
   * @param jobConfig
   * @return String directory location of hadoop configurations
   */
	 String getHadoopHomeDirPath(Cluster cluster);

	 /**
 	 * Gets the hive conf dir path.
 	 *
 	 * @param cluster the cluster
 	 * @return the hive conf dir path
 	 */
 	String getHiveConfDirPath(Cluster cluster);
	 

}
