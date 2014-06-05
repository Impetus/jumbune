package org.jumbune.movierating.mappers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import org.apache.hadoop.io.IntWritable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.hadoop.io.LongWritable;
import org.jumbune.movierating.common.MovieRatingLoader;
import org.jumbune.movierating.mappers.MovieRatingMapper;




/**
 * The Mapper takes<movie id, user id> as input and writes <movie name, user id> as output.
 * Movie name matches to a given regex.
 * 
 * 
 */
public class MovieRatingMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	
	private static final Logger LOGGER = LogManager.getLogger(MovieRatingMapper.class);
	private String fieldSeparator = "\\\t";
	private static final String REGEX_EXPRESSION = "regexExpression";
	private String expectedRegex;
	
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
		
		expectedRegex = context.getConfiguration().get(REGEX_EXPRESSION);
	}
	
	/**
	 * mapper function for MovieRatingMapper
	 * takes<movie id, user id> as input and writes <movie name, user id> as output.
	 */
	public void map(LongWritable key, Text value, Context context)  throws IOException, InterruptedException {
		
		String recordValue = value.toString();
		String[] columnFields = recordValue.split(fieldSeparator);
		
		LOGGER.info("columnFields[0]::: movieId: " + columnFields[0]);
		LOGGER.info("columnFields[1]::: userId: " + columnFields[1]);
		
		String movieId = columnFields[0];
		int userId = Integer.parseInt(columnFields[1]);
		
		String movieName = MovieRatingLoader.getProperty(movieId);
		LOGGER.info("movieName:: " + movieName);
		
		boolean isValidRegex = applyRegexCheck(expectedRegex, movieName);
		
		LOGGER.info("isValidRegex:: " + isValidRegex);
		
		if(isValidRegex){
			context.write(new Text(movieName), new IntWritable(userId));
		}
		
	}
	
	/**
	 * Applies regex check and returns false if validation check fails,else true
	 * 
	 * @param expectedValue
	 *            the value expected by the user
	 * @param actualFieldValue
	 *            the actual value of the field
	 * @return false if validation check fails,else true
	 */
	private boolean applyRegexCheck(String expectedValue, String actualFieldValue) {
		try{
		Pattern p = Pattern.compile(expectedValue);
		Matcher matcher = p.matcher(actualFieldValue);
		return matcher.matches();
		}catch (Exception e) {
			LOGGER.error("An error occured while validating regex:: "+e);
			return false;
		}
	}

}