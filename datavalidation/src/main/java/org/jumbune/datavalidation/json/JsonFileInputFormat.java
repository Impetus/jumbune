package org.jumbune.datavalidation.json;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JsonFileInputFormat extends TextInputFormat{
	private static final Logger LOGGER = LogManager.getLogger(JsonFileInputFormat.class);
	@Override
	public RecordReader<LongWritable, Text>  createRecordReader(InputSplit split, TaskAttemptContext context) {
		RecordReader<LongWritable, Text> recordReader = null;
		try 
	    {
			recordReader = new JsonFileRecordReader(split, context);
	    }
	    catch (IOException ioe) 
	    {
	    	LOGGER.error(ioe);
	    }
		return recordReader; 
	}
}
