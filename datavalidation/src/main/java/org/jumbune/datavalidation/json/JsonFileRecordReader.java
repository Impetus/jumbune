package org.jumbune.datavalidation.json;

import java.io.IOException;


import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.google.common.base.Charsets;

public class JsonFileRecordReader extends RecordReader<LongWritable, Text>{
	
		private static byte[] startJsonCurlyTag = null;
		private static byte[] endJsonCurlyTag = null;
		private static byte[] startJsonSquareTag = null;
		private static byte[] endJsonSquareTag = null;
	

	   	private long start;
	    private long end;
	    private FSDataInputStream fSDataInputStream;
	    private DataOutputBuffer buffer = new DataOutputBuffer();
	    private LongWritable key = new LongWritable();
	    private Text value = new Text();
		
	
	public JsonFileRecordReader(InputSplit split, TaskAttemptContext context) throws IOException{
		FileSplit fileSplit = (FileSplit) split;	
        start = fileSplit.getStart();
        end = start + fileSplit.getLength();
        Path file = fileSplit.getPath();
        FileSystem fileSystem = file.getFileSystem(context.getConfiguration());
        fSDataInputStream = fileSystem.open(fileSplit.getPath());
        fSDataInputStream.seek(start);
	}
	
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		
		if (fSDataInputStream.getPos() < end) 
	        {
	            if (readUntilMatch(startJsonCurlyTag,startJsonSquareTag,endJsonCurlyTag, endJsonSquareTag)) 
	            {
	                try 
	                {
                    	value.set(buffer.getData(), 0,buffer.getLength());
                    	key.set(fSDataInputStream.getPos());
	                    return true;
	                    }
	                finally 
	                {
	                    buffer.reset();
	                }
	            }
	        }
	        return false;
	}
	
	
	@Override
	public void close() throws IOException {
		fSDataInputStream.close();
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		 return (fSDataInputStream.getPos() - start) / (float) (end - start);
	}

	@Override
	public void initialize(InputSplit arg0, TaskAttemptContext arg1)
			throws IOException, InterruptedException {
		startJsonCurlyTag = ("{").getBytes(Charsets.UTF_8);
		endJsonCurlyTag = ("}").getBytes(Charsets.UTF_8);
		startJsonSquareTag = ("[").getBytes(Charsets.UTF_8);
		endJsonSquareTag = ("]").getBytes(Charsets.UTF_8);
	}

	
		
	private boolean readUntilMatch(byte[] match1,byte[] match2 ,byte[] match3 ,byte[] match4)
            throws IOException {
        
        int squareCounter = 0;
        int curlyCounter = 0;
        while (true) {
        	int i = 0;
        	int j =0;
            int byteValue = fSDataInputStream.read();

            if (byteValue == -1)
                return true;
            
            byte[] newLine = "\n".getBytes(Charsets.UTF_8);
            if(byteValue == newLine[j]){
           	 j++;
                if (j >= newLine.length)
               	 continue;
            }

                buffer.write(byteValue);

            if (byteValue == match1[i]) {
                i++;
                if (i >= match1.length)
                	curlyCounter++;
            } else if(byteValue == match2[i]){
            	i++;
                if (i >= match2.length)
                	squareCounter++; 
            }else if (byteValue == match3[i]) {
                i++;
                if (i >= match3.length)
                	if(curlyCounter>0)
                	curlyCounter--;    
            } else if(byteValue == match4[i]){
            	i++;
                if (i >= match4.length)
                	if(squareCounter > 0)
                	squareCounter--;              
            }
            else
                i = 0;
            	if(squareCounter == 0 && curlyCounter == 0) return true;


            if (i == 0 && fSDataInputStream.getPos() >= end)
                return false;
        }
    }
}
