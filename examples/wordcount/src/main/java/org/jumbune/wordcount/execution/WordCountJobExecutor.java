package org.jumbune.wordcount.execution;

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
import org.jumbune.wordcount.execution.WordCountJobExecutor;
import org.jumbune.wordcount.mappers.WordCountMapper;
import org.jumbune.wordcount.mappers.WordCountReducer;

/**
 * Word count MR Job example
 * This is the main class for executing the word count example
 * 
 */
public final class WordCountJobExecutor {

	private static final Logger LOGGER = LogManager.getLogger(WordCountJobExecutor.class);
	private static final String JOB_NAME = "WordCount";

	/***
	 * private constructor
	 */
	private WordCountJobExecutor(){
		
	}
	
	/***
	 * main method for job execution
	 * @param args
	 */
	public static void main(String[] args) {

		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		
		String inputPath = otherArgs[0];
		String outputPath = otherArgs[1];

		Job job = new Job(conf, JOB_NAME);

		job.setJarByClass(WordCountJobExecutor.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);

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