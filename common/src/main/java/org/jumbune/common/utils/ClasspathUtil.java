package org.jumbune.common.utils;


/**
 * This interface defines constants for classpath settings.
 */
public interface ClasspathUtil {
	// constants for classpath for type
	/** The user supplied. */
	int USER_SUPPLIED = 1;
	
	/** The jumbune supplied. */
	int JUMBUNE_SUPPLIED = 2;

	// constants for classpath type
	/** The classpath type none. */
	int CLASSPATH_TYPE_NONE = 0;
	
	/** The classpath type thick jar. */
	int CLASSPATH_TYPE_THICK_JAR = 1;
	
	/** The classpath type hadoop lib. */
	int CLASSPATH_TYPE_HADOOP_LIB = 2;
	
	/** The classpath type external. */
	int CLASSPATH_TYPE_EXTERNAL = 3;
	
	/** The classpath type libjars. */
	int CLASSPATH_TYPE_LIBJARS = 4;

	// constants for source types
	/** The source type thick jar. */
	int SOURCE_TYPE_THICK_JAR = 1;
	
	/** The source type hadoop lib. */
	int SOURCE_TYPE_HADOOP_LIB = 2;
	
	/** The source type master. */
	int SOURCE_TYPE_MASTER = 3;
	
	/** The source type slaves. */
	int SOURCE_TYPE_SLAVES = 4;
}