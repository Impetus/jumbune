package org.jumbune.debugger.instrumentation.utils;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;



/**
 * <p>
 * This class provides util method for file related operations
 * </p>

 */
public final class FileUtil {
	private FileUtil(){};
	private static final String JAR_FILE_EXTENSION = ".jar";

	/**
	 * <p>
	 * This method provides list of all the classpath files
	 * </p>
	 * 
	 * @return list of all classpath files
	 
	 */
	public static List<String> getClassPathFileList(Config config) {
		List<String> listFiles = getJumbuneClasspathFileList(config);
/*		List<String> userSuppliedClasspathFileList = getUserClasspathFileList(config);
		if (userSuppliedClasspathFileList != null) {
			listFiles.addAll(getUserClasspathFileList(config));
		}*/
		return listFiles;
	}

	/**
	 * <p>
	 * Gets the list of all user supplied classpath files
	 * </p>
	 * 
	 * @param config
	 *            Config
	 * @return List<String> List of files
	 */
	private static List<String> getUserClasspathFileList(Config config) {
		int userClassPathSource = getUserClassPathSource(config);

		if (userClassPathSource == ClasspathUtil.SOURCE_TYPE_MASTER) {
			// get the list as configured in config
			return getClassPathFileList(config, ClasspathUtil.USER_SUPPLIED);
		} else if (userClassPathSource == ClasspathUtil.SOURCE_TYPE_SLAVES) {
			// get the list from userLib folder on master
			JobConfig jobConfig = (JobConfig)config;
			return ConfigurationUtil
					.getAllFileNamesInDir(jobConfig.getUserLibLocationAtMaster());
		}

		return null;
	}

	/**
	 * <p>
	 * Gets the list of all jumbune supplied classpath files
	 * </p>
	 * 
	 * @param config
	 *            Config
	 * @return List<String> List of files
	 */
	private static List<String> getJumbuneClasspathFileList(Config config) {
		List<String> listFiles = getClassPathFileList(config,
				ClasspathUtil.JUMBUNE_SUPPLIED);

		return listFiles;
	}

	/**
	 * <p>
	 * This method provides list of all the lib files for the required type:
	 * user supplied or Jumbune supplied
	 * </p>
	 * 
	 * @param classpathType
	 *            User suppplied or Jumbune supplied
	 * @return List<String> List of lib files
	 */
	private static List<String> getClassPathFileList(Config config,
			int classpathType) {
		JobConfig jobConfig = (JobConfig)config;
		return ConfigurationUtil.getAllClasspathFiles(
				jobConfig.getClasspathFolders(classpathType),
				jobConfig.getClasspathExcludes(classpathType),
				jobConfig.getClasspathFiles(classpathType));
	}

	/**
	 * <p>
	 * This method provides list of jars, if they are opted for not to be
	 * included in the instrumented jar. The list will include User and/or
	 * Jumbune dependencies.
	 * </p>
	 * 
	 * @return List<String> List of jars
	 */
	public static List<String> getClassPathFilesForThinJar(Config config) {
//		int classpathTypeUser = getUserClassPathType(config);
		int classpathTypeFramework = getJumbuneClassPathType(config);
		List<String> listFiles = null;
		if (classpathTypeFramework == ClasspathUtil.CLASSPATH_TYPE_LIBJARS) {
			listFiles = getJumbuneClasspathFileList(config);
		}
/*		if (classpathTypeUser == ClasspathUtil.CLASSPATH_TYPE_LIBJARS) {
			if (listFiles == null) {
				listFiles = new ArrayList<String>();
			}
			listFiles.addAll(getUserClasspathFileList(config));
		}*/
		return listFiles;
	}

	

	/**
	 * <p>
	 * This method provides separate list of jar and resource files from a given
	 * list of files.
	 * <ul>
	 * <li>index 0: List of jar files</li>
	 * <li>index 1: List of resource files</li>
	 * </ul>
	 * </p>
	 * 
	 * @param files
	 *            List of files
	 * @return List<List<String>> Separate list of jar and resource files
	 */
	public static List<List<String>> getJarsAndResources(List<String> files) {
		if (files != null) {
			List<List<String>> list = new ArrayList<List<String>>();
			List<String> jars = new ArrayList<String>();
			List<String> resources = new ArrayList<String>();

			for (String file : files) {
				if (file.endsWith(JAR_FILE_EXTENSION)) {
					jars.add(file);
				} else {
					resources.add(file);
				}
			}
			list.add(jars);
			list.add(resources);
			return list;
		} else {
			return null;
		}
	}

	/**
	 * <p>
	 * Gets the user classpath type
	 * 
	 * @param config
	 *            Config
	 * @return int Classpath type
	 */
	public static int getUserClassPathType(Config config) {
		int classpathSourceUser = getUserClassPathSource(config);
		return (classpathSourceUser == ClasspathUtil.SOURCE_TYPE_SLAVES || classpathSourceUser == ClasspathUtil.SOURCE_TYPE_MASTER) ? ClasspathUtil.CLASSPATH_TYPE_LIBJARS
				: ClasspathUtil.CLASSPATH_TYPE_NONE;
	}

	/**
	 * <p>
	 * Gets the jumbune classpath type
	 * <ul>
	 * <li>Only thin jar is supported
	 * </ul>
	 * 
	 * @param loader
	 *            Yaml loader
	 * @return int Classpath type
	 */
	public static int getJumbuneClassPathType(Config config) {
		return ClasspathUtil.CLASSPATH_TYPE_LIBJARS;
	}

	/**
	 * <p>
	 * Gets the source, how user dependencies are supplied
	 * </p>
	 * 
	 * @param config
	 *            Config
	 * @return int source
	 */
	public static int getUserClassPathSource(Config config) {
		JobConfig jobConfig= (JobConfig)config;
		return jobConfig.getClasspath().getUserSupplied().getSource();
	}
}
