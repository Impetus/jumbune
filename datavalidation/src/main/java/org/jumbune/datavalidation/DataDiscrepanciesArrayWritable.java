package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;

public class DataDiscrepanciesArrayWritable extends ArrayWritable {

	@Override
	public void set(Writable[] values) {
		super.set(values);
	}

	public DataDiscrepanciesArrayWritable() {
		super(DataViolationWB.class);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);

	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
	}

	@Override
	public String toString() {
		return "DataDiscrepanciesArrayWritable [fileName=" + ", getValueClass()=" + getValueClass() + ", toStrings()="
				+ Arrays.toString(toStrings()) + ", toArray()=" + toArray() + ", get()=" + Arrays.toString(get())
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
