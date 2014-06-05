package org.jumbune.clickstream.mappers;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.io.IntWritable;

/**
 * 
 * Takes the input information of clickstream data for a website for registered user
 * and outputs products visited by the user.
 *
 */
public class ClickstreamAnalysisMapper extends Mapper<LongWritable, Text, Text, Text>{
	
private String fieldSeparator = "\\\t";

	
	/***
	 * the map method for list defaulter
	 */
	public void map(LongWritable key, Text value, Context context)  throws IOException, InterruptedException {
		String recordValue = value.toString();
		String[] columnFields = recordValue.split(fieldSeparator);
		
		String userId = columnFields[13];
		String url = columnFields[12];
		String product[] = url.split("/");
		
		context.write(new Text(userId), new Text(product[product.length-1]));
	}

}
