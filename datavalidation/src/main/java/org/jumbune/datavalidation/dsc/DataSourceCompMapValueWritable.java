package org.jumbune.datavalidation.dsc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class DataSourceCompMapValueWritable implements Writable {

	private BooleanWritable isSource;

	private Text filePath;

	private Text row;

	public DataSourceCompMapValueWritable() {
		isSource = new BooleanWritable();
		filePath = new Text();
		row = new Text();
	}

	public BooleanWritable getIsSource() {
		return isSource;
	}

	public void setIsSource(BooleanWritable isSource) {
		this.isSource = isSource;
	}

	public Text getFilePath() {
		return filePath;
	}

	public void setFilePath(Text filePath) {
		this.filePath = filePath;
	}

	public Text getRow() {
		return row;
	}

	public void setRow(Text row) {
		this.row = row;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		isSource.readFields(in);
		filePath.readFields(in);
		row.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		isSource.write(out);
		filePath.write(out);
		row.write(out);
	}

}
