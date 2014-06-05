package org.jumbune.wordcount.mappers;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/***
 * Mapper for word count example. 
 * It reads the HDFS input file and provides output as word and its each occurrence to reducer. 
 *
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	private  static final IntWritable ONE = new IntWritable(1);

	private Text word = new Text();

	/**
	 * method for performing map operation
	 */
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word.set(tokenizer.nextToken());
			context.write(word, ONE);
		}
	}

}
