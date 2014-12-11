package org.jumbune.deploy;

import org.jumbune.deploy.apache.ApacheNonYarnDeployer;
import org.jumbune.deploy.apache.ApacheYarnDeployer;
import org.jumbune.deploy.cdh.CDHDeployer;
import org.jumbune.deploy.hdp.HDPDeployer;
import org.jumbune.deploy.mapr.MapRDeployer;

public class DeployerFactory {

	public static Deployer getDeployer(String distributionType, String hadoopDistributionType) {
		
		if(distributionType.equalsIgnoreCase("Non-Yarn") && hadoopDistributionType.contains("a")){
			return new ApacheNonYarnDeployer();
		}
		if(distributionType.equalsIgnoreCase("Yarn") && hadoopDistributionType.contains("a")){
			return new ApacheYarnDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase("h")){
			return new HDPDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase("c")){
			return new CDHDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase("m")){
			return new MapRDeployer();
		}
		return null;
		

	}

}
