package org.jumbune.debugger.instrumentation.utils;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;



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
	public static List<String> getClassPathFileList(Loader loader) {
		List<String> listFiles = getJumbuneClasspathFileList(loader);
		List<String> userSuppliedClasspathFileList = getUserClasspathFileList(loader);
		if (userSuppliedClasspathFileList != null) {
			listFiles.addAll(getUserClasspathFileList(loader));
		}
		return listFiles;
	}

	/**
	 * <p>
	 * Gets the list of all user supplied classpath files
	 * </p>
	 * 
	 * @param loader
	 *            Yaml loader
	 * @return List<String> List of files
	 */
	private static List<String> getUserClasspathFileList(Loader loader) {
		int userClassPathSource = getUserClassPathSource(loader);

		if (userClassPathSource == ClasspathUtil.SOURCE_TYPE_MASTER) {
			// get the list as configured in yaml
			return getClassPathFileList(loader, ClasspathUtil.USER_SUPPLIED);
		} else if (userClassPathSource == ClasspathUtil.SOURCE_TYPE_SLAVES) {
			// get the list from userLib folder on master
			YamlLoader yamlLoader = (YamlLoader)loader;
			return ConfigurationUtil
					.getAllFileNamesInDir(yamlLoader.getUserLibLocatinAtMaster());
		}

		return null;
	}

	/**
	 * <p>
	 * Gets the list of all jumbune supplied classpath files
	 * </p>
	 * 
	 * @param loader
	 *            Yaml loader
	 * @return List<String> List of files
	 */
	private static List<String> getJumbuneClasspathFileList(Loader loader) {
		List<String> listFiles = getClassPathFileList(loader,
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
	private static List<String> getClassPathFileList(Loader loader,
			int classpathType) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		return ConfigurationUtil.getAllClasspathFiles(
				yamlLoader.getClasspathFolders(classpathType),
				yamlLoader.getClasspathExcludes(classpathType),
				yamlLoader.getClasspathFiles(classpathType));
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
	public static List<String> getClassPathFilesForThinJar(Loader loader) {
		int classpathTypeUser = getUserClassPathType(loader);
		int classpathTypeFramework = getJumbuneClassPathType(loader);
		List<String> listFiles = null;
		if (classpathTypeFramework == ClasspathUtil.CLASSPATH_TYPE_LIBJARS) {
			listFiles = getJumbuneClasspathFileList(loader);
		}
		if (classpathTypeUser == ClasspathUtil.CLASSPATH_TYPE_LIBJARS) {
			if (listFiles == null) {
				listFiles = new ArrayList<String>();
			}
			listFiles.addAll(getUserClasspathFileList(loader));
		}
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
	 * @param loader
	 *            Yaml loader
	 * @return int Classpath type
	 */
	public static int getUserClassPathType(Loader loader) {
		int classpathSourceUser = getUserClassPathSource(loader);
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
	public static int getJumbuneClassPathType(Loader loader) {
		return ClasspathUtil.CLASSPATH_TYPE_LIBJARS;
	}

	/**
	 * <p>
	 * Gets the source, how user dependencies are supplied
	 * </p>
	 * 
	 * @param loader
	 *            Yaml loader
	 * @return int source
	 */
	public static int getUserClassPathSource(Loader loader) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		return yamlLoader.getClasspath().getUserSupplied().getSource();
	}
}
