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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * @param job
	 *            Job whose output path need to be changed
	 */
	public static void modifyOutputPath(JobConf job) {
		Path path = org.apache.hadoop.mapred.FileOutputFormat
				.getOutputPath(job);
		if (path == null) {
			throw new IllegalArgumentException("Job Output path is null, expecting not null path value");
		}
			StringBuilder out = new StringBuilder(path.toString());
			out.append(SEPARATOR_UNDERSCORE).append(System.currentTimeMillis());
			org.apache.hadoop.mapred.FileOutputFormat.setOutputPath(job,
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
