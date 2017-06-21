package org.jumbune.deploy;


public abstract class YarnDeployer implements Deployer {

	protected static final String[] ADDITIONAL_HADOOP_JARS = {
			"jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar",
			"hadoop-common-*.jar", "hadoop-mapreduce-client-core-*.jar",
			"hadoop-mapreduce-client-hs-*.jar","hadoop-auth-*.jar"};
	
	private String[] schedularJARs = {

			"WEB-INF/lib/hadoop-common",
			"WEB-INF/lib/avro",
			"WEB-INF/lib/commons-collections",
			"WEB-INF/lib/guava",
			"WEB-INF/lib/hadoop-auth",
			"WEB-INF/lib/jackson-core-asl",
			"WEB-INF/lib/jackson-mapper-asl",
			"WEB-INF/lib/log4j",
			"WEB-INF/lib/protobuf-java",
			"WEB-INF/lib/slf4j",
			"WEB-INF/lib/hadoop-mapreduce-client-common",
			"WEB-INF/lib/hadoop-mapreduce-client-core",
			"WEB-INF/lib/hadoop-mapreduce-client-hs",
			"WEB-INF/lib/hadoop-mapreduce-client-jobclient",
			"WEB-INF/lib/hadoop-yarn-api",
			"WEB-INF/lib/hadoop-yarn-client",
			"WEB-INF/lib/hadoop-yarn-common",
			"WEB-INF/lib/protobuf-java",
			"WEB-INF/lib/zkclient",
			"WEB-INF/lib/zookeeper"
	};
	
	public String[] getSchedularJars() {
		return schedularJARs;
	}

}