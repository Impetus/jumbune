package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableComparable;


/**
 * Writable class for storing the consolidated report for each validation check.
 * 
 * 
 */
public class DataViolationWritable implements WritableComparable<DataViolationWritableBean> {

	/** total number of violations for a particular type of check. */
	private int totalViolations;

	/** Map containing number of violations corresponding each field for a particular data validation check. */
	private MapWritable fieldMap;

	/** Writable class for array of DataViolationWritableBean. */
	private DataViolationArrayWritable dataViolationArrayWritable;

	/**
	 * Instantiates a new data violation writable.
	 */
	public DataViolationWritable() {
		fieldMap = new MapWritable();
		dataViolationArrayWritable = new DataViolationArrayWritable();
	}

	/**
	 * Gets the total violations.
	 *
	 * @return the totalViolations
	 */
	public int getTotalViolations() {
		return totalViolations;
	}

	/**
	 * Sets the total violations.
	 *
	 * @param totalViolations the totalViolations to set
	 */
	public void setTotalViolations(int totalViolations) {
		this.totalViolations = totalViolations;
	}

	/**
	 * Gets the field map.
	 *
	 * @return the fieldMap
	 */
	public MapWritable getFieldMap() {
		return fieldMap;
	}

	/**
	 * Sets the field map.
	 *
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(MapWritable fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * Gets the data violation array writable.
	 *
	 * @return the dataViolationArrayWritable
	 */
	public DataViolationArrayWritable getDataViolationArrayWritable() {
		return dataViolationArrayWritable;
	}

	/**
	 * Sets the data violation array writable.
	 *
	 * @param dataViolationArrayWritable the dataViolationArrayWritable to set
	 */
	public void setDataViolationArrayWritable(DataViolationArrayWritable dataViolationArrayWritable) {
		this.dataViolationArrayWritable = dataViolationArrayWritable;
	}

	/**
	 * writes data violations to output stream
	 */
	public void write(DataOutput out) throws IOException {
		out.writeInt(totalViolations);
		fieldMap.write(out);
		dataViolationArrayWritable.write(out);
	}

	/**
	 * reads the violations from input stream
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		totalViolations = in.readInt();
		fieldMap.readFields(in);
		dataViolationArrayWritable.readFields(in);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataViolationWritableBean arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
