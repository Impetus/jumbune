package org.jumbune.deploy;


public abstract class NonYarnDeployer implements Deployer{
	
	protected static final String[] ADDITIONAL_HADOOP_JARS = {
		"jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar",
		"hadoop*core*.jar" };


}
