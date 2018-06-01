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
	
	/** The classpath type libjars. */
	int CLASSPATH_TYPE_LIBJARS = 4;

	/** The source type master. */
	int SOURCE_TYPE_MASTER = 3;
	
	/** The source type slaves. */
	int SOURCE_TYPE_SLAVES = 4;
}