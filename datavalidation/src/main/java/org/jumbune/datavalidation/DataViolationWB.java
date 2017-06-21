package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;


/**
 * This is custom writable class for storing parameters required for data violation report.
 * 
 * 
 * 
 */
public class DataViolationWB implements WritableComparable<DataViolationWB>{
	
	
	/** The split end offset. */
	private LongWritable splitEndOffset;
	
	/** The file name. */
	private Text fileName;
	
	private LongWritable totalRecordsEmittByMap ;
	
	//key : fieldNumber value : FieldLWB
	private MapWritable fieldMap;

	/**
	 * Instantiates a new data violation writable bean.
	 */
	public DataViolationWB() {
		fieldMap = new MapWritable();
		
		splitEndOffset = new LongWritable();
		fileName = new Text();
		totalRecordsEmittByMap = new LongWritable();
	}

		
	public Text getFileName() {
		return fileName;
	}

	public void setFileName(Text fileName) {
		this.fileName = fileName;
	}

	public LongWritable getSplitEndOffset() {
		return splitEndOffset;
	}

	public void setSplitEndOffset(LongWritable splitEndOffset) {
		this.splitEndOffset = splitEndOffset;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		fieldMap.readFields(in);
		splitEndOffset.readFields(in);
		fileName.readFields(in);
		totalRecordsEmittByMap.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		fieldMap.write(out);
		splitEndOffset.write(out);
		fileName.write(out);
		totalRecordsEmittByMap.write(out);
	}
	
	 public void putAll(Map<? extends Writable, ? extends Writable> t) {
		 for (Map.Entry<? extends Writable, ? extends Writable> e: t.entrySet()) {
			 fieldMap.put(e.getKey(), e.getValue());
		     }
		 }
	 

	 /* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataViolationWB arg0) {
		return 0;
	}



	
	public void resetFieldMap(){
		fieldMap = new MapWritable();
	}



	public LongWritable getTotalRecordsEmittByMap() {
		return totalRecordsEmittByMap;
	}



	public void setTotalRecordsEmittByMap(LongWritable totalRecordsEmittByMap) {
		this.totalRecordsEmittByMap = totalRecordsEmittByMap;
	}


	@Override
	public String toString() {
		return "DataViolationWB [splitEndOffset=" + splitEndOffset + ", fileName=" + fileName
				+ ", totalRecordsEmittByMap=" + totalRecordsEmittByMap + ", fieldMap=" + fieldMap + "]";
	}


	public MapWritable getFieldMap() {
		return fieldMap;
	}

}
