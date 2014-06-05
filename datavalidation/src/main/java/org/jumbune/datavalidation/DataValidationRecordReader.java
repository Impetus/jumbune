package org.jumbune.datavalidation;

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

/**
 * Treats keys as record numbers in file and value as line.
 * 

 */
public class DataValidationRecordReader extends RecordReader<LongWritable, Text> {

	/**
	 * start - the starting offset of the split in the file.
	 */
	private long start;
	/**
	 * pos - the current global offset in the file being read.
	 */
	private long pos;
	/**
	 * end - the end offset of the split in the file.
	 */
	private long end;
	/**
	 * recordNumber - the number of records which have already been traversed,if file is traversed in sequence.Incrementing recordNumber in
	 * RecordReader would give the global record number in file.
	 */
	private int recordNumber;
	/**
	 * key - the key to be sent to the Mapper.
	 */
	private LongWritable key = null;
	/**
	 * value - the value to be sent to the Mapper.
	 */
	private Text value = null;
	/**
	 * isInsideChunk - tells whether the current position is inside or outside the boundary value of the chunk/split.
	 */
	private boolean isInsideChunk = true;
	/**
	 * fsin - the input stream to the file being read.
	 */
	private FSDataInputStream fsin;
	/**
	 * buffer - the buffer to store the values of the records.
	 */
	private DataOutputBuffer buffer = new DataOutputBuffer();
	/**
	 * recordSeparator - the byte array for storing record separator.
	 */
	private byte[] recordSeparator;

	/**
	 * initializes the start, end, record number of file based on split
	 */
	public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException {
		DataValidationFileSplit split = (DataValidationFileSplit) genericSplit;
		Configuration conf = context.getConfiguration();
		recordSeparator = conf.get(DataValidationConstants.RECORD_SEPARATOR).getBytes();
		final Path path = split.getPath();

		// open the file and seek to the start of the split
		FileSystem fs = path.getFileSystem(conf);
		fsin = fs.open(path);
		start = split.getStart();
		end = start + split.getLength();
		recordNumber = split.getRecordNumber();
		fsin.seek(start);

		if (start != 0) { 
			readNewRecord();
			start = pos;
			buffer.reset();
		}
	}

	/**
	 * gets next record number as key
	 */
	public boolean nextKeyValue() throws IOException {
		// returns false if it has crossed the threshold of the chunk
		if (!isInsideChunk){
			return false;
		}
		if (key == null) {
			key = new LongWritable();
		}
		recordNumber++;
		// key contains the byte offset
		key.set(recordNumber);
		if (value == null) {
			value = new Text();
		}
		isInsideChunk = readNewRecord();
		int lengthToRead = buffer.getLength() - recordSeparator.length;
		value.set(buffer.getData(), 0, lengthToRead);
		buffer.reset();
		return true;
	}

	@Override
	public LongWritable getCurrentKey() {
		return key;
	}

	@Override
	public Text getCurrentValue() {
		return value;
	}

	/**
	 * Get the progress within the split
	 */
	public float getProgress() {
		if (start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	@Override
	public synchronized void close() throws IOException {
		if (fsin != null) {
			fsin.close();
		}
	}

	/**
	 * Reads the current split for a new record. Returns true if a new record is found inside the split,otherwise false.
	 * 
	 * @return returns true if a new record is found inside the split,otherwise false.
	 * @throws IOException
	 */
	private boolean readNewRecord() throws IOException {

		int bufferPos = 0;
		int b = 0;
		while (true) {
			b = fsin.read();
			pos = fsin.getPos();
			buffer.write(b);
			if (b == -1){
				
			return false;
			}	
			if (b == recordSeparator[bufferPos]) {
													
				bufferPos++;
				if (bufferPos == recordSeparator.length) {
					return pos < end;
									
				}
			} else {
				
				bufferPos = 0;
			}

		}
	}
}
