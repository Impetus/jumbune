package com.impetus.profiling.regex.execution;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.impetus.profiling.regex.mappers.RegexValidationProfilerMapper;
import com.impetus.profiling.regex.mappers.RegexValidationProfilerReducer;


/**
 * Regex Validator MR Job example for heavy operations that can be analysed with Profiler.
 * It provides the no. of users that have rated a movie, where movie name contains a specific regex.
 * 
 * 
 * 
 */

public final class RegexValidationProfilerJobExecutor {
	
	private static final Logger LOGGER = LogManager.getLogger(RegexValidationProfilerJobExecutor.class);
	private static final String JOB_NAME = "RegexValidator";
	private static final String REGEX_EXPRESSION = "regexExpression";

	private RegexValidationProfilerJobExecutor(){
		
	}
	public static void main(String args[]){
		
		LOGGER.info("Inside RegexValidationJobExecutor");
		
		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		

		LOGGER.info("size of other args is: " + otherArgs.length);
		LOGGER.info("value of other args[0] is: " + otherArgs[0]);
		LOGGER.info("value of other args[1] is: " + otherArgs[1]);
		LOGGER.info("value of other args[2] is: " + otherArgs[2]);
		
		String inputPath = otherArgs[0];
		String outputPath = otherArgs[1];
		String regex = otherArgs[2];
		conf.set(REGEX_EXPRESSION, regex);
		
		Job job = new Job(conf, JOB_NAME);

		job.setJarByClass(RegexValidationProfilerJobExecutor.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(RegexValidationProfilerMapper.class);
		job.setReducerClass(RegexValidationProfilerReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		LOGGER.info("Starting job execution");
		job.waitForCompletion(true);
		}
		catch (IOException e) {
			LOGGER.error("IOException:: "+e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFoundException:: "+e);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException:: "+e);
		}
		LOGGER.info("Job completion over,starting to read the result from hdfs");

	}
}