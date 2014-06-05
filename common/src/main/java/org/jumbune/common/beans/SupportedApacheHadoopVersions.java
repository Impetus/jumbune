package org.jumbune.common.beans;


/***
 * constants for hadoop specific versions.
 * 
 * 
 * 
 */
public enum SupportedApacheHadoopVersions {
	
	/** The Hadoop_1_0_4. */
	Hadoop_1_0_4("Hadoop 1.0.4"),
 /** The HADOO p_0_20_2. */
 HADOOP_0_20_2("Hadoop 0.20.2"), 
 /** The HADOO p_1_0_3. */
 HADOOP_1_0_3("Hadoop 1.0.3.15"),
/** Hadoop version for cloudera */
 HADOOP_2_0_CDH("Hadoop 2.0.0-cdh4.3.0"),
/** default Hadoop version **/
HADOOP_DEFAULT	("Hadoop 1.0.4");
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
		return SupportedApacheHadoopVersions.HADOOP_DEFAULT;

	}
}
