package org.jumbune.deploy;


public abstract class NonYarnDeployer implements Deployer{
	
	protected static final String[] ADDITIONAL_HADOOP_JARS = {
		"jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar",
		"hadoop*core*.jar", "guava*.jar", "log4j-*.jar" };
	
	private String[] schedularJARs = {

			"WEB-INF/lib/hadoop-common",
			"WEB-INF/lib/avro",
			"WEB-INF/lib/commons-collections",
			"WEB-INF/lib/guava",
			"WEB-INF/lib/hadoop-auth", // TODO Check if available in non yarn
			"WEB-INF/lib/jackson-core-asl",
			"WEB-INF/lib/jackson-mapper-asl",
			"WEB-INF/lib/log4j",
			"WEB-INF/lib/protobuf-java",
			"WEB-INF/lib/slf4j",
			"WEB-INF/lib/hadoop-core",
			"WEB-INF/lib/protobuf-java",
			"WEB-INF/lib/zkclient",
			"WEB-INF/lib/zookeeper"
	};
	
	public String[] getSchedularJars() {
		return schedularJARs;
	}
	
}
