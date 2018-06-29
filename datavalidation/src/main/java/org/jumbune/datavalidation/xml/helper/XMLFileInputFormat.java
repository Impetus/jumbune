/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


/**
 * @author vivek.shivhare
 *
 */
public class XMLFileInputFormat extends	TextInputFormat {

	
	@Override
	public RecordReader<LongWritable, Text>  createRecordReader(InputSplit split, TaskAttemptContext context) {
		RecordReader<LongWritable, Text> recordReader = null;
		try 
	    {
			recordReader = new XMLFileRecordReader(split, context);
	    }
	    catch (IOException ioe) 
	    {
	    	ioe.printStackTrace();	    	
	    }
		return recordReader; 
	}

}
