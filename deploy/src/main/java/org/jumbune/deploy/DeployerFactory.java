package org.jumbune.deploy;

import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.deploy.apache.ApacheYarnDeployer;
import org.jumbune.deploy.cdh.CDHDeployer;
import org.jumbune.deploy.emr.EMRApacheDeployer;
import org.jumbune.deploy.emr.EMRMaprDeployer;
import org.jumbune.deploy.hdp.HDPDeployer;
import org.jumbune.deploy.mapr.MapRYarnDeployer;

public class DeployerFactory {

	public static Deployer getDeployer(String distributionType, String hadoopDistributionType) {
		
		if(distributionType.equalsIgnoreCase(ExtendedConstants.YARN) && hadoopDistributionType.equals(ExtendedConstants.APACHE)){
			return new ApacheYarnDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase(ExtendedConstants.HORTONWORKS)){
			return new HDPDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase(ExtendedConstants.CLOUDERA)){
			return new CDHDeployer();
		}
		if(hadoopDistributionType.equalsIgnoreCase(ExtendedConstants.EMRAPACHE)){
			return new EMRApacheDeployer();
		}
		
		if(hadoopDistributionType.equalsIgnoreCase(ExtendedConstants.EMRMAPR)){
			return new EMRMaprDeployer();
		}
		
		// begin mapr code changes
		if (distributionType.equalsIgnoreCase(ExtendedConstants.YARN) && hadoopDistributionType.equalsIgnoreCase(ExtendedConstants.MAPR)) {
			return new MapRYarnDeployer();
		}
		return null;
		

	}

}
