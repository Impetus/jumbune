package org.profiling.regex.mappers;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * The Reducer takes<movie name, user id> as input and writes <movie name, no. of users> as output.
 * 
 * @author purva bindal
 * 
 */

public class RegexValidationProfilerReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		
		int userCount = 0;
		for (IntWritable val : values) {
			userCount++;
		}
		context.write(key, new IntWritable(userCount));
	}
}