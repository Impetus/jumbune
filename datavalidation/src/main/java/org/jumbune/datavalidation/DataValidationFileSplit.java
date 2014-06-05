package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;


/**
 * A section of an input file. Returned by {@link InputFormat#getSplits(JobContext)} and passed to
 * {@link InputFormat#createRecordReader(InputSplit,TaskAttemptContext)}.
 * 

 * 
 */
public class DataValidationFileSplit extends InputSplit implements Writable {
	/**
	 * file - the path of the file.
	 */
	private Path file;
	
	/** start - the starting offset of the split. */
	private long start;
	/**
	 * length - the total number of bytes in the offset.
	 */
	private long length;
	/**
	 * recordNumber - the number of records which have already been traversed,if file is traversed in sequence.Incrementing recordNumber in
	 * RecordReader would give the global record number in file.
	 */
	private int recordNumber;
	/**
	 * hosts - hosts where the split is placed.
	 */
	private String[] hosts;

	/**
	 * Instantiates a new data validation file split.
	 */
	DataValidationFileSplit() {
	}

	/**
	 * Constructs a split with host information.
	 *
	 * @param file the file name
	 * @param start the position of the first byte in the file to process
	 * @param length the number of bytes in the file to process
	 * @param recordNumber the record number
	 * @param hosts the list of hosts containing the block, possibly null
	 */
	public DataValidationFileSplit(Path file, long start, long length, int recordNumber,String[] hosts) {
		this.file = file;
		this.start = start;
		this.length = length;
		this.recordNumber = recordNumber;
		this.hosts = hosts.clone();
	}

	/**
	 * The file containing this split's data.
	 *
	 * @return the path
	 */
	public Path getPath() {
		return file;
	}

	/**
	 * The position of the first byte in the file to process.
	 *
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	/**
	 * The number of records in the file to process.
	 *
	 * @return the record number
	 */
	public int getRecordNumber() {
		return recordNumber;
	}

	/**
	 * The number of bytes in the file to process.
	 *
	 * @return the length
	 */
	@Override
	public long getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return file + ":" + start + "+" + length;
	}

	// //////////////////////////////////////////
	// Writable methods
	// //////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		Text.writeString(out, file.toString());
		out.writeLong(start);
		out.writeLong(length);
		out.writeInt(recordNumber);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		file = new Path(Text.readString(in));
		start = in.readLong();
		length = in.readLong();
		recordNumber = in.readInt();
		hosts = null;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.InputSplit#getLocations()
	 */
	@Override
	public String[] getLocations() throws IOException {
		if (this.hosts == null) {
			return new String[] {};
		} else {
			return this.hosts;
		}
	}
}
