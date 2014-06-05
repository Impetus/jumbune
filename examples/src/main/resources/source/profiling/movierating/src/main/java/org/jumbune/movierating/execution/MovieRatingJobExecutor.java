package org.jumbune.movierating.execution;

import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.jumbune.movierating.execution.MovieRatingJobExecutor;
import org.jumbune.movierating.mappers.MovieRatingMapper;
import org.jumbune.movierating.mappers.MovieRatingReducer;




/**
 * Regex Validator MR Job example for heavy operations that can be analysed with Profiler.
 * It provides the no. of users that have rated a movie, where movie name contains a specific regex.
 * 

 * 
 */

public final class MovieRatingJobExecutor {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(MovieRatingJobExecutor.class);
	
	/** The Constant JOB_NAME. */
	private static final String JOB_NAME = "MovieRating";
	
	/** The Constant REGEX_EXPRESSION. */
	private static final String REGEX_EXPRESSION = "regexExpression";

	/**
	 * Instantiates a new regex validation profiler job executor.
	 */
	private MovieRatingJobExecutor(){
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]){
		
		Configuration conf = new Configuration();
		String[] otherArgs;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		

		String inputPath = otherArgs[0];
		String outputPath = otherArgs[1];
		String regex = otherArgs[2];
		conf.set(REGEX_EXPRESSION, regex);
		
		Job job = new Job(conf, JOB_NAME);

		job.setJarByClass(MovieRatingJobExecutor.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(MovieRatingMapper.class);
		job.setReducerClass(MovieRatingReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);
		}
		catch (IOException e) {
			LOGGER.error("IOException:: "+e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFoundException:: "+e);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException:: "+e);
		}

	}
}