package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * The Class LineLWB is a pojo containing the line number and actual value of the field that got violated.
 */
public class LineLWB implements Writable{

	/** The line number. */
	private LongWritable lineNumber;
	
	/** The actual value. */
	private Text actualValue;
	
	/**
	 * Instantiates a new line lwb.
	 */
	public LineLWB() {
		this.lineNumber = new LongWritable();
		this.actualValue = new Text();
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		lineNumber.readFields(in);
		actualValue.readFields(in);
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		lineNumber.write(out);
		actualValue.write(out);
	}

	
	/**
	 * Gets the actual value.
	 *
	 * @return the actual value
	 */
	public Text getActualValue() {
		return actualValue;
	}

	/**
	 * Sets the actual value.
	 *
	 * @param actualValue the new actual value
	 */
	public void setActualValue(Text actualValue) {
		this.actualValue = actualValue;
	}

	/**
	 * Gets the line number.
	 *
	 * @return the line number
	 */
	public LongWritable getLineNumber() {
		return lineNumber;
	}

	/**
	 * Sets the line number.
	 *
	 * @param lineNumber the new line number
	 */
	public void setLineNumber(LongWritable lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "LineLWB [lineNumber=" + lineNumber + ", actualValue=" + actualValue + "]";
	}	
	
	
	
}
