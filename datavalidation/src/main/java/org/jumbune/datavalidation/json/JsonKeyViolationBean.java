package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


/**
 * The Class JsonKeyViolationBean.
 */
public class JsonKeyViolationBean implements Writable{
	
	/** The line number. */
	private IntWritable lineNumber;
	
	/** The json node. */
	private Text jsonNode;

	/** The expected value. */
	private Text expectedValue;
	
	/** The actual value. */
	private Text actualValue;

	/**
	 * Instantiates a new json key violation bean.
	 */
	public JsonKeyViolationBean(){
		lineNumber = new IntWritable();
		jsonNode = new Text();
		expectedValue = new Text();
		actualValue = new Text();
	}

	/**
	 * Gets the line number.
	 *
	 * @return the line number
	 */
	public IntWritable getLineNumber() {
		return lineNumber;
	}

	/**
	 * Sets the line number.
	 *
	 * @param lineNumber the new line number
	 */
	public void setLineNumber(IntWritable lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * Gets the json node.
	 *
	 * @return the json node
	 */
	public Text getJsonNode() {
		return jsonNode;
	}

	/**
	 * Sets the json node.
	 *
	 * @param jsonNode the new json node
	 */
	public void setJsonNode(Text jsonNode) {
		this.jsonNode = jsonNode;
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


	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		lineNumber.write(out);
		jsonNode.write(out);
		expectedValue.write(out);
		actualValue.write(out);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		lineNumber.readFields(in);
		jsonNode.readFields(in);
		expectedValue.readFields(in);
		actualValue.readFields(in);	
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonKeyViolationBean [lineNumber=" + lineNumber + ", jsonNode="
				+ jsonNode + ", expectedValue=" + expectedValue
				+ ", actualValue=" + actualValue + "]";
	}

}
