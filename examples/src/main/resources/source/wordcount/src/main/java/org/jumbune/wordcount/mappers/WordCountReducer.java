package org.jumbune.wordcount.mappers;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/***
 * Reducer for word count example. 
 * It takes the word and its each occurrence as input and provides the total count of specified word as output. 
 *
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	
	/***
	 * method which performs the reduce operation for word count
	 */
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		context.write(key, new IntWritable(sum));
	}

}