package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.GENERIC_PARSER_FILES;
import static org.jumbune.utils.UtilitiesConstants.GENERIC_PARSER_LIB_JARS;
import static org.jumbune.utils.UtilitiesConstants.JOB_CONF_JARS;
import static org.jumbune.utils.UtilitiesConstants.JOB_CONF_RESOURCES;
import static org.jumbune.utils.UtilitiesConstants.PROFILE_PARAMS;
import static org.jumbune.utils.UtilitiesConstants.PROFILE_TASK;
import static org.jumbune.utils.UtilitiesConstants.SEPARATOR_COMMA;
import static org.jumbune.utils.UtilitiesConstants.SEPARATOR_UNDERSCORE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;


/**
 * This class provides util methods to deal with map reduce jobs
 * 
 */
@SuppressWarnings("deprecation")
public final class JobUtil {
	/** The LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(JobUtil.class);
	private static final String NULL = "null";
	
	private JobUtil(){
	}
	
	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final String FILESYSTEM_LOC_PATTERN = "(/[a-zA-Z0-9_ -]+\\s*)+(/)?(\\*)?(\\*\\.(log)?\\*)?(\\.\\*)?(\\.json)?";

	private static final char STARTER = '<';
	private static final char CLOSER = '>';
	private static final int INVALID_INDEX = -1;
	private static Pattern ipPattern = Pattern.compile(IPADDRESS_PATTERN);
	private static Pattern filePattern = Pattern.compile(FILESYSTEM_LOC_PATTERN);
	

	/**
	 * This method gets the place holders for the passed string.
	 *
	 * @param str the str
	 * @return the place holders
	 */
	public static String[] getPlaceHolders(String str) {
		ArrayList<String> list = new ArrayList<String>();
		int[] array = null;

		boolean scan = true;
		int scanIndex = 0;

		while (scan) {
			array = getPlaceHolder(str, scanIndex);
			if (array != null) {
				String prop = str.substring(array[0] + 1, array[1]);
				list.add(prop);
				scanIndex = array[1] + 1;
				array = null;
			} else {
				break;
			}
		}

		String[] retArray = null;
		if (list.size() > 0) {
			retArray = new String[list.size()];
			list.toArray(retArray);
		}

		return retArray;
	}

	/**
	 * This api gets the place holder according to the starting index of the string.
	 *
	 * @param str the str
	 * @param start the start
	 * @return the place holder
	 */
	public static int[] getPlaceHolder(String str, int start) {
		String scanString = str.substring(start);

		int index = scanString.indexOf(STARTER);
		if (index != INVALID_INDEX) {
			int endIndex = scanString.indexOf(CLOSER);
			if (endIndex != INVALID_INDEX) {
				return new int[] { start + index, start + endIndex };
			}
		}
		return null;
	}

	/**
	 * This method replaces the holder according to the property.
	 *
	 * @param str the str
	 * @param prop the prop
	 * @return the string
	 */
	public static String replaceHolder(String str, String prop) {
		return str
				.replaceAll(STARTER + prop + CLOSER, System.getProperty(prop));
	}

	/**
	 * This method gets and replace holders for the string passed.
	 *
	 * @param str the str
	 * @return the and replace holders
	 */
	public static String getAndReplaceHolders(String str) {
		if (str != null) {
			String[] props = JobUtil.getPlaceHolders(str);
			return replaceHolders(str, props);
		}
		return null;
	}

	/**
	 * This method is used to replace holders.
	 *
	 * @param str the str
	 * @param props the props
	 * @return the string
	 */
	public static String replaceHolders(String str, String[] props) {
		String strTmp = str;
		if (strTmp != null && props != null && props.length > 0) {
					for (String prop : props) {
					strTmp = replaceHolder(strTmp, prop);
				}
	
		}
		return strTmp;
	}

	/**
	 * This method gets and replace holders for an array of strings.
	 *
	 * @param strArray the str array
	 * @return the and replace holders
	 */
	public static String[] getAndReplaceHolders(String[] strArray) {
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
				String[] props = JobUtil.getPlaceHolders(strArray[i]);
				strArray[i] = replaceHolders(strArray[i], props);
			}
		}
		return strArray;
	}

	/**
	 * This method is used to validate IP address pattern.
	 *
	 * @param address the address
	 * @return true, if successful
	 */
	public static boolean validateIPAddress(String address) {
		Matcher matcher = ipPattern.matcher(address);

		return matcher.matches();
	}

	/**
	 * This method is used to validate file system location.
	 *
	 * @param address the address
	 * @return true, if successful
	 */
	public static boolean validateFileSystemLocation(String address) {
		Matcher matcher = filePattern.matcher(address);
		return matcher.matches();
	}
	
	/**
	 * This method call when injected into a class, will enable the profilng of
	 * the job
	 * 
	 * @param job
	 *            Job to be profiled
	 * @param params
	 *            Profiling params
	 */
	public static void enableProfiler(Job job, String params) {
		job.getConfiguration().set(PROFILE_TASK, Boolean.toString(true));
		job.getConfiguration().set(PROFILE_PARAMS, params);
	}

	/**
	 * This method call when injected into a class, will disable the profilng of
	 * the job
	 * 
	 * @param job
	 *            Job not to be profiled
	 */
	public static void disableProfiler(Job job) {
		job.getConfiguration().set("mapred.task.profile",
				Boolean.toString(false));
	}

	/**
	 * This method call when injected into a class will add the
	 * GenericOptionParser with '-libjars' parameter
	 * 
	 * @param job
	 *            Job to which the classpath to be added
	 * @param jars
	 *            comma separated jars to be added to the classpath
	 * @param resources
	 *            comma separated files to be added to the classpath
	 * @throws IOException 
	 */
	public static void addClassPath(final Job job, String jars, String resources) throws IOException {
		LOGGER.debug("Libraries being added to the classpath: "
				+ job.getConfiguration().get(JOB_CONF_JARS) + "Resources : "
				+ job.getConfiguration().get(JOB_CONF_RESOURCES));
		// taking libjars and files values passed from console while executing
		// job
		StringBuilder oldJars = new StringBuilder().append(job
				.getConfiguration().get(JOB_CONF_JARS));
		StringBuilder oldFiles = new StringBuilder().append(job
				.getConfiguration().get(JOB_CONF_RESOURCES));
		
		String jarsTmp = jars;
		String resourcesTmp = resources;
		if (oldJars != null && !oldJars.toString().equals(NULL)
				&& oldJars.length() > 0) {
			oldJars.append(SEPARATOR_COMMA);
			oldJars.append(jarsTmp);
			jarsTmp = oldJars.toString();
		}

		if (resourcesTmp != null && resourcesTmp.length() > 0) {
			if (oldFiles != null && !oldFiles.toString().equals(NULL)
					&& oldFiles.length() > 0) {
				oldFiles.append(SEPARATOR_COMMA);
				oldFiles.append(resourcesTmp);
				resourcesTmp = oldFiles.toString();
			}
			new GenericOptionsParser(job.getConfiguration(), new String[] {
					GENERIC_PARSER_LIB_JARS, jarsTmp, GENERIC_PARSER_FILES,
					resourcesTmp });
		} else {
			new GenericOptionsParser(job.getConfiguration(), new String[] {
					GENERIC_PARSER_LIB_JARS, jarsTmp });
		}
	}

	/**
	 * This method call when injected into a class will add the
	 * GenericOptionParser with '-libjars' parameter
	 * 
	 * @param job
	 *            JobConf to which the classpath to be added
	 * @param jars
	 *            comma separated jars to be added to the classpath
	 * @param resources
	 *            comma separated files to be added to the classpath
	 * @throws IOException 
	 */
	public static void addClassPath(final JobConf job, String jars,
			String resources) throws IOException {
		LOGGER.debug("Libraries being added to the classpath: "
				+ job.get(JOB_CONF_JARS) + "Resources : "
				+ job.get(JOB_CONF_RESOURCES));
		// taking libjars and files values passed from console while executing
		// job
		StringBuilder oldJars = new StringBuilder().append(job
				.get(JOB_CONF_JARS));
		StringBuilder oldFiles = new StringBuilder().append(job
				.get(JOB_CONF_RESOURCES));
		String jarsTmp = jars;
		String resourcesTmp = resources;
		if (oldJars != null && !oldJars.toString().equals(NULL)
				&& oldJars.length() > 0) {
			oldJars.append(SEPARATOR_COMMA);
			oldJars.append(jarsTmp);
			jarsTmp = oldJars.toString();
		}

		if (resourcesTmp != null && resourcesTmp.length() > 0) {
			if (oldFiles != null && !oldFiles.toString().equals(NULL)
					&& oldFiles.length() > 0) {
				oldFiles.append(SEPARATOR_COMMA);
				oldFiles.append(resourcesTmp);
				resourcesTmp = oldFiles.toString();
			}
			new GenericOptionsParser(job, new String[] {
					GENERIC_PARSER_LIB_JARS, jarsTmp, GENERIC_PARSER_FILES,
					resourcesTmp });
		} else {
			new GenericOptionsParser(job, new String[] {
					GENERIC_PARSER_LIB_JARS, jarsTmp });
		}
	}

	/**
	 * This method call when injected into a class will modify the output path,
	 * only if output is into HDFS
	 * 
	 * @param job
	 *            Job whose output path need to be changed
	 */
	public static void modifyOutputPath(Job job) throws Exception {
		Path path = FileOutputFormat.getOutputPath(job);
		if (path == null) {
			throw new IllegalArgumentException("Job Output path is null, expecting not null path value");
		}
			StringBuilder out = new StringBuilder(path.toString());
			out.append(SEPARATOR_UNDERSCORE).append(System.currentTimeMillis());
			FileOutputFormat.setOutputPath(job, new Path(out.toString()));
	}

	/**
	 * This method call when injected into a class will modify the output path,
	 * only if output is into HDFS
	 * 
	 * @param conf
	 *            Job whose output path need to be changed
	 */
	public static void modifyOutputPath(JobConf conf) {
		Path path = org.apache.hadoop.mapred.FileOutputFormat
				.getOutputPath(conf);
		if (path == null) {
			throw new IllegalArgumentException("Job Output path is null, expecting not null path value");
		}
			StringBuilder out = new StringBuilder(path.toString());
			out.append(SEPARATOR_UNDERSCORE).append(System.currentTimeMillis());
			org.apache.hadoop.mapred.FileOutputFormat.setOutputPath(conf,
					new Path(out.toString()));
	}

	/***
	 * This method call when injected into a class will modify the input path,
	 * only if input is into HDFS
	 * 
	 * @param job
	 *            Job whose input path need to be changed
	 */
	public static void modifyInputPath(Job job, String sampledDataPath) {
		if(sampledDataPath==null){
			throw new IllegalArgumentException("Sampled data path is null, expecting not null path value");
		}
			try {
				LOGGER.debug("Modifying input path changed to: "+ sampledDataPath);
				FileInputFormat.setInputPaths(job, new Path(sampledDataPath));
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

	/***
	 * This method call when injected into a class will modify the input path,
	 * only if input is into HDFS
	 * 
	 * @param job
	 *            Job whose input path need to be changed
	 */
	public static void modifyInputPath(JobConf conf, String sampledDataPath) {
		if(sampledDataPath==null){
			throw new IllegalArgumentException("Sampled data path is null, expecting not null path value");
		}
			org.apache.hadoop.mapred.FileInputFormat.setInputPaths(conf, new Path(sampledDataPath));
	}

}
