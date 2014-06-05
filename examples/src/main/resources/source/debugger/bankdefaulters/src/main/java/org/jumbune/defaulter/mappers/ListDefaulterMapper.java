package org.jumbune.defaulter.mappers;

import java.io.IOException;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.jumbune.defaulter.utils.DefaulterConstants;



/**
 * The Mapper takes user information as input and writes <city, defaulter indicator> as output.
 * A user is defaulter if he has paid less than a sum of 10000 or if he is a past month defaulter.
 * 
 * Example for validating debugger to check for erroneous code
 * 
 * 
 */
public class ListDefaulterMapper extends Mapper<LongWritable, Text, Text, BooleanWritable>{
	private String fieldSeparator = "\\\t";

	
	/***
	 * the map method for list defaulter
	 */
	public void map(LongWritable key, Text value, Context context)  throws IOException, InterruptedException {
		
		String recordValue = value.toString();
		String[] columnFields = recordValue.split(fieldSeparator);
		String city = columnFields[2];
		int amount = Integer.parseInt(columnFields[DefaulterConstants.THREE]);
		String isPastMonthDefaulter = columnFields[DefaulterConstants.FOUR];
		

		if(amount < DefaulterConstants.TEN_THOUSAND && "Y".equalsIgnoreCase(isPastMonthDefaulter)){
			context.write(new Text(city), new BooleanWritable(true));
		}else{
			context.write(new Text(city), new BooleanWritable(false));
		}		
		
	}
}