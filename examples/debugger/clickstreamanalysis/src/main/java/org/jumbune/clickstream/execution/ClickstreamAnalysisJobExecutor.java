package org.jumbune.clickstream.execution;
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
import org.jumbune.clickstream.execution.ClickstreamAnalysisJobExecutor;
import org.jumbune.clickstream.mappers.ClickstreamAnalysisMapper;
import org.jumbune.clickstream.mappers.ClickstreamAnalysisReducer;





/**
 * Map reduce job to perform analysis on website clickstream data.
 * 
 * Use case of an online shopping website that collects the information of products
 * that its registered customers have visited, based on which it provides recommendations for
 * products they might like to buy in future. 
 *
 */
public final class ClickstreamAnalysisJobExecutor {
	
	private static final Logger LOGGER = LogManager
	.getLogger(ClickstreamAnalysisJobExecutor.class);
	private static final String JOB_NAME = "ClickstreamAnalysis";

	/**
	 * private constructor
	 */
	private ClickstreamAnalysisJobExecutor(){
		
	}

	/**
	 * executes the job
	 * @param args
	 */
	public static void main(String args[]){
		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

			String inputPath = otherArgs[0];
			String outputPath = otherArgs[1];

			Job job = new Job(conf, JOB_NAME);

			job.setJarByClass(ClickstreamAnalysisJobExecutor.class);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			job.setMapperClass(ClickstreamAnalysisMapper.class);
			job.setReducerClass(ClickstreamAnalysisReducer.class);

			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			FileInputFormat.addInputPath(job, new Path(inputPath));
			FileOutputFormat.setOutputPath(job, new Path(outputPath));

			job.waitForCompletion(true);
		} catch (IOException e) {
			LOGGER.error("IOException:: "+e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFoundException:: "+e);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException:: "+e);
		}
		
	}
}
