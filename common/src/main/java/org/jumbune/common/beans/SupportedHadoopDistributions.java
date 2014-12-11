package org.jumbune.common.beans;


/***
 * constants for hadoop specific versions.
 */
public enum SupportedHadoopDistributions {
	
	/** The default hadoop version for apache 1.2.1 non-yarn */
	HADOOP_NON_YARN("Hadoop 1.2"),
	
	/** The HADOOP 0_20_02 */
	HADOOP_MAPR("Hadoop 1.0.3"),
	
	/** The HADOOP 2.4.1 */
	HADOOP_YARN("Hadoop 2.4."),
	
	/** The CDH Hadoop */
	CDH_5("cdh5"),
	
	/** The Apache Hadoop 0.2X */
	APACHE_02X("Hadoop 0.23.");
	
	/** The version. */
	private String version;
	
	/** The name. */
	private SupportedHadoopDistributions name;

	/**
	 * Instantiates a new supported apache hadoop versions.
	 *
	 * @param version the version
	 */
	private SupportedHadoopDistributions(String version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return version;
	}

	/**
	 * *
	 * This method used for find out hadoop version constants for.
	 *
	 * @param version version of Hadoop
	 * @return the enum by value
	 */
	public static SupportedHadoopDistributions getEnumByValue(String version) {
		SupportedHadoopDistributions name = null;
		for (SupportedHadoopDistributions hadoopVersions : values()) {
			if (version.contains(hadoopVersions.version)) {
				name = hadoopVersions;
				return name;
			}
		}
		return SupportedHadoopDistributions.HADOOP_NON_YARN;

	}
}
