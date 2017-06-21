package org.jumbune.datavalidation.xml.helper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
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
import org.jumbune.datavalidation.xml.XmlDataValidationConstants;

import com.google.common.base.Charsets;

public class XMLFileRecordReader extends RecordReader<LongWritable, Text>  {

	
	private static byte[] headerStart = null;
	private static byte[] headerEnd = null;
    private static byte[] startElement = null;
    private static byte[] endElement = null;
    
    private static byte[] header = null;
    private long start;
    private long end;
    private FSDataInputStream fSDataInputStream;
    private DataOutputBuffer buffer = new DataOutputBuffer();
    private LongWritable key = new LongWritable();
    private Text value = new Text();
    
    public XMLFileRecordReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
    	FileSplit fileSplit = (FileSplit) split;	
        start = fileSplit.getStart();
        end = start + fileSplit.getLength();
        Path file = fileSplit.getPath();
        FileSystem fileSystem = file.getFileSystem(context.getConfiguration());
        fSDataInputStream = fileSystem.open(fileSplit.getPath());
        fSDataInputStream.seek(start);
	}

    
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException 
    {
        if (fSDataInputStream.getPos() < end) 
        {
            if (readUntilMatch(startElement, false)) 
            {
                try 
                {
                    buffer.write(startElement);
                    if (readUntilMatch(endElement, true)) 
                    {
                    	value.set(header, 0, header.length);
                        value.append(buffer.getData(), header.length, header.length+buffer.getLength());
                        key.set(fSDataInputStream.getPos());
                        return true;
                    }
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
    public void initialize(InputSplit is, TaskAttemptContext tac) throws IOException, InterruptedException 
    {
    	Configuration conf  = tac.getConfiguration();
    	
    	headerStart = conf.get(XmlDataValidationConstants.HEADER_START_TAG).getBytes(Charsets.UTF_8);
	    headerEnd   = conf.get(XmlDataValidationConstants.HEADER_END_TAG).getBytes(Charsets.UTF_8);
	    startElement = conf.get(XmlDataValidationConstants.START_TAG).getBytes(Charsets.UTF_8);
	    endElement = conf.get(XmlDataValidationConstants.END_TAG).getBytes(Charsets.UTF_8);
    	
    	header = extractHeader(headerStart, headerEnd);
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
    public void close() throws IOException {
    	fSDataInputStream.close();
    }

    private boolean readUntilMatch(byte[] match, boolean isWithinBlock)
            throws IOException {
        int i = 0;
        while (true) {
            int byteValue = fSDataInputStream.read();

            if (byteValue == -1)
                return false;

            if (isWithinBlock)
                buffer.write(byteValue);

            if (byteValue == match[i]) {
                i++;
                if (i >= match.length)
                    return true;
            } else
                i = 0;

            if (!isWithinBlock && i == 0 && fSDataInputStream.getPos() >= end)
                return false;
        }
    }

    public byte[] extractHeader(byte[] headerStart,byte[] headerEnd) throws IOException, InterruptedException 
    {
        if (fSDataInputStream.getPos() < end) 
        {
            if (readUntilMatch(headerStart, false)) 
            {
                    buffer.write(headerStart);
                    if (readUntilMatch(headerEnd, true)) 
                    {
                        return buffer.getData();
                    }
            }
        }
        return null;
    }
    

}
