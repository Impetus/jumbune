package org.debugging.defaulter.execution;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
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
import org.debugging.defaulter.mappers.ListDefaulterReducer;
import org.debugging.defaulter.mappers.ListDefaulterMapper;

/**
 * List defaulter MR Job example
 * This is the main class for executing the defaulter list example
 * 
 */
public final class ListDefaulterJobExecutor {

	private static final Logger LOGGER = LogManager
			.getLogger(ListDefaulterJobExecutor.class);
	private static final String JOB_NAME = "ListDefaulter";
	
	/**
	 * private constructor
	 */
	private ListDefaulterJobExecutor(){
		
	}

	/**
	 * main method for job execution
	 * @param args
	 */
	public static void main(String[] args) {

		LOGGER.info("Inside ListDefaulterJobExecutor");

		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

			LOGGER.info("size of other args is: " + otherArgs.length);
			LOGGER.info("value of other args[0] is: " + otherArgs[0]);
			LOGGER.info("value of other args[1] is: " + otherArgs[1]);

			String inputPath = otherArgs[0];
			String outputPath = otherArgs[1];

			Job job = new Job(conf, JOB_NAME);

			job.setJarByClass(ListDefaulterJobExecutor.class);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			job.setMapperClass(ListDefaulterMapper.class);
			job.setReducerClass(ListDefaulterReducer.class);

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(BooleanWritable.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));

			LOGGER.info("Starting job execution");
			job.waitForCompletion(true);
		} catch (IOException e) {
			LOGGER.error("IOException:: "+e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFoundException:: "+e);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException:: "+e);
		}
		LOGGER.info("Job completion over,starting to read the result from hdfs");

	}

}

