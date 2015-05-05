package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;

public class DataDiscrepanciesArrayWritable extends ArrayWritable {
	private String fileName ;
@Override
public void set(Writable[] values) {
	super.set(values);
}

	public DataDiscrepanciesArrayWritable() {
		super(DataViolationWritableBean.class);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeUTF(fileName);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		fileName = in.readUTF();
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
