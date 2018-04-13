package org.debugging.defaulter.mappers;

import java.io.IOException;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/***
 * Reducer for defaulter list example. 
 * It takes the city and defaulter value as input and provides the total count of defaulters per city. 
 *
 */
public class ListDefaulterReducer extends Reducer<Text, BooleanWritable, Text, IntWritable>{
	
	
	/**
	 * reduce method for defaulter list example
	 */
	public void reduce(Text key, Iterable<BooleanWritable> defaulterValues, Context context) throws IOException, InterruptedException {
		
		int defaulterCount = 0;
			for (BooleanWritable defaulter : defaulterValues) {
				defaulterCount++;
			}
			
		context.write(key, new IntWritable(defaulterCount));
	}

}

