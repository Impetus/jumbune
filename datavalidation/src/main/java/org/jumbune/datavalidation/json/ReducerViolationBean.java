package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


/**
 * The Class ReducerViolationBean.
 */
public class ReducerViolationBean implements Writable{
	
	/** The file name. */
	Text fileName;
	
	/** The size. */
	IntWritable size;
	
	/**
	 * Instantiates a new reducer violation bean.
	 */
	public ReducerViolationBean() {

		fileName = new Text();
		size = new IntWritable();
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public Text getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(Text fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public IntWritable getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(IntWritable size) {
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		fileName.write(out);
		size.write(out);
		
		
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		fileName.readFields(in);
		size.readFields(in);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReducerViolationBean [fileName=" + fileName + ", size=" + size
				+ "]";
	}

	
	
}
