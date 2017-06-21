package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableComparable;
import org.jumbune.datavalidation.ArrayListWritable;


/**
 * Writable class for storing the consolidated report for each validation check.
 * 
 * 
 */
public class DataViolationWritable implements WritableComparable<DataViolationWB> {

	/** total number of violations for a particular type of check. */
	private long totalViolations;
	
	/** infected tuple for a perticular type of violation */
	private long dirtyTuple;
	
	/** Map containing number of violations corresponding each field for a particular data validation check. */
	private MapWritable fieldMap;

	/** Writable class for array of DataViolationWritableBean. */
	private ArrayListWritable<FileViolationsWritable> fileViolationsWritables;

	/**
	 * Instantiates a new data violation writable.
	 */
	public DataViolationWritable() {
		fieldMap = new MapWritable();
		fileViolationsWritables = new ArrayListWritable<FileViolationsWritable>();
	}

	/**
	 * Gets the total violations.
	 *
	 * @return the totalViolations
	 */
	public long getTotalViolations() {
		return totalViolations;
	}

	/**
	 * Sets the total violations.
	 *
	 * @param totalViolations the totalViolations to set
	 */
	public void setTotalViolations(long totalViolations) {
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
	 * writes data violations to output stream
	 */
	public void write(DataOutput out) throws IOException {
		out.writeLong(totalViolations);
		out.writeLong(dirtyTuple);
		fieldMap.write(out);
		fileViolationsWritables.write(out);
	}

	/**
	 * reads the violations from input stream
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		totalViolations = in.readLong();
		dirtyTuple = in.readLong();
		fieldMap.readFields(in);
		fileViolationsWritables.readFields(in);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataViolationWB arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the infectedTuple
	 */
	public long getDirtyTuple() {
		return dirtyTuple;
	}

	/**
	 * @param dirtyTuple the infectedTuple to set
	 */
	public void setDirtyTuple(long dirtyTuple) {
		this.dirtyTuple = dirtyTuple;
	}

	
	public ArrayListWritable<FileViolationsWritable> getFileViolationsWritables() {
		return fileViolationsWritables;
	}

	public void setFileViolationsWritables(ArrayListWritable<FileViolationsWritable> fileViolationsWritables) {
		this.fileViolationsWritables = fileViolationsWritables;
	}

	@Override
	public String toString() {
		return "DataViolationWritable [totalViolations=" + totalViolations + ", dirtyTuple=" + dirtyTuple
				+ ", fieldMap=" + fieldMap + ", fileViolationsWritables=" + fileViolationsWritables + "]";
	}

	
	
	
	
}
