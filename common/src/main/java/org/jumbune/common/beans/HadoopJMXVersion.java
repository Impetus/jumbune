package org.jumbune.common.beans;


/**
 * The Enum HadoopJMXVersion.
 */
public enum HadoopJMXVersion {
	
	/** The HADOO p_0_20_2_ jmx. */
	HADOOP_0_20_2_JMX("HADOOP_0_20_2", "hadoop"), 
 /** The HADOO p_1_0_4_ jmx. */
 HADOOP_1_0_4_JMX("HADOOP_1_0_4", "Hadoop"), 
 /** The cdh hadoop jmx nnjt. */
 CDH_HADOOP_JMX_NNJT("HADOOP_1_0_3", "Hadoop"), 
 /** The hdp hadoop jmx. */
 HDP_HADOOP_JMX(
			"HADOOP_1_0_4", "Hadoop"), 
 /** The cdh hadoop jmx dntt. */
 CDH_HADOOP_JMX_DNTT("HADOOP_1_0_3", "hadoop"),
/** The cdh hadoop jmx. */
CDH_HADOOP_JMX("HADOOP_1_0_3", "CDHHadoop") ;
	
	/** The hadoop version. */
	private String hadoopVersion;
	
	/** The hadoop jmx value. */
	private String hadoopJMXValue;

	/**
	 * Instantiates a new hadoop jmx version.
	 *
	 * @param hadoopVersion the hadoop version
	 * @param hadoopJMXValue the hadoop jmx value
	 */
	private HadoopJMXVersion(String hadoopVersion, String hadoopJMXValue) {

		this.hadoopJMXValue = hadoopJMXValue;

	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return hadoopJMXValue;
	}
}
