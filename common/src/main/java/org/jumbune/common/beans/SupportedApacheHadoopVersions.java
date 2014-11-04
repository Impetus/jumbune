package org.jumbune.common.beans;


/***
 * constants for hadoop specific versions.
 */
public enum SupportedApacheHadoopVersions {
	
	/** The default hadoop version for apache non-yarn */
	HADOOP_NON_YARN("Hadoop 1.2.1"),
	
	/** The HADOOP 0_23_11 */
	HADOOP_MAPR("Hadoop 0.23.11"),
	
	/** The HADOOP 2.4.1 */
	HADOOP_YARN("Hadoop 2.4.1");
	
	/** The version. */
	private String version;
	
	/** The name. */
	private SupportedApacheHadoopVersions name;

	/**
	 * Instantiates a new supported apache hadoop versions.
	 *
	 * @param version the version
	 */
	private SupportedApacheHadoopVersions(String version) {
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
	public static SupportedApacheHadoopVersions getEnumByValue(String version) {
		SupportedApacheHadoopVersions name = null;
		for (SupportedApacheHadoopVersions hadoopVersions : values()) {
			if (hadoopVersions.version.equalsIgnoreCase(version)) {
				name = hadoopVersions;
				return name;
			}
		}
		return SupportedApacheHadoopVersions.HADOOP_NON_YARN;

	}
}
