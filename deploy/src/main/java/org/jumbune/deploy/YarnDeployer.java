package org.jumbune.deploy;


public abstract class YarnDeployer implements Deployer {

	protected static final String[] ADDITIONAL_HADOOP_JARS = {
			"jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar",
			"hadoop*core*.jar", "hadoop-mapreduce-client-core-*.jar",
			"hadoop-mapreduce-client-hs-*.jar" };

}