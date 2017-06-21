package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.jumbune.datavalidation.ArrayListWritable;

/**
 * The Class ViolationLWB is reponsible for storing the expected value of the field and list of LineLWB.
 */
public class ViolationLWB implements Writable {

	/** The expected value. */
	private Text expectedValue;

	/** The line lwb list. */
	private ArrayListWritable<LineLWB> lineLWBList;

	/**
	 * Instantiates a new violation lwb.
	 */
	public ViolationLWB() {
		this.expectedValue = new Text();
		this.lineLWBList = new ArrayListWritable<LineLWB>();
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		expectedValue.readFields(in);
		lineLWBList.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		expectedValue.write(out);
		lineLWBList.write(out);
	}

	/**
	 * Gets the expected value.
	 *
	 * @return the expected value
	 */
	public Text getExpectedValue() {
		return expectedValue;
	}

	/**
	 * Sets the expected value.
	 *
	 * @param expectedValue the new expected value
	 */
	public void setExpectedValue(Text expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * Gets the line lwb list.
	 *
	 * @return the line lwb list
	 */
	public ArrayListWritable<LineLWB> getLineLWBList() {
		return lineLWBList;
	}

	/**
	 * Sets the line lwb list.
	 *
	 * @param lineLWBList the new line lwb list
	 */
	public void setLineLWBList(ArrayListWritable<LineLWB> lineLWBList) {
		this.lineLWBList = lineLWBList;
	}
	
	@Override
	public String toString() {
		return this.hashCode() + "-ViolationLWB [expectedValue=" + getExpectedValue() + ", lineLWBList="
				+ getLineLWBList() + "]";
	}

}
