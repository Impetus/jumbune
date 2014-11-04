package org.jumbune.deploy;

import org.jumbune.deploy.apache.ApacheNonYarnDeployer;
import org.jumbune.deploy.apache.ApacheYarnDeployer;
import org.jumbune.deploy.cdh.CDHDeployer;
import org.jumbune.deploy.hdp.HDPDeployer;
import org.jumbune.deploy.mapr.MapRDeployer;

public class DeployerFactory {

	public static Deployer getDeployer(String distributionType) {
		switch(distributionType){
		case "APACHE-NY":
			return new ApacheNonYarnDeployer();
		case "APACHE-Y":
			return new ApacheYarnDeployer();
		case "CDH":
			return new CDHDeployer();
		case "HDP":
			return new HDPDeployer();
		case "MAPR":
			return new MapRDeployer();
		default:
			return null;	
		}

	}

}
