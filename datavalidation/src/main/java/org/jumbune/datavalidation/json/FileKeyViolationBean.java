package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


/**
 * The Class FileKeyViolationBean.
 */
public class FileKeyViolationBean implements Writable{
	
	/** The file name. */
	private Text fileName; 
	
	/** The json line violation list. */
	private ArrayListWritable <JsonLineViolationBean> jsonLineViolationList;
	
	/** The split end offset. */
	private LongWritable splitEndOffset;
	
	/** The total records emitted by map. */
	private LongWritable totalRecordsEmittByMap ;

	/**
	 * Instantiates a new file key violation bean.
	 */
	public FileKeyViolationBean() {
			fileName = new Text();
		    jsonLineViolationList = new ArrayListWritable <JsonLineViolationBean>();
		    splitEndOffset = new LongWritable();
		    totalRecordsEmittByMap = new LongWritable();
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
	 * Gets the violation list.
	 *
	 * @return the violation list
	 */
	public ArrayListWritable<JsonLineViolationBean> getViolationList() {
		return jsonLineViolationList;
	}

	/**
	 * Sets the violation list.
	 *
	 * @param jsonLineViolationList the new violation list
	 */
	public void setViolationList(ArrayListWritable<JsonLineViolationBean> jsonLineViolationList) {
		this.jsonLineViolationList = jsonLineViolationList;
	}

	
	/**
	 * Gets the split end offset.
	 *
	 * @return the split end offset
	 */
	public LongWritable getSplitEndOffset() {
		return splitEndOffset;
	}

	/**
	 * Sets the split end offset.
	 *
	 * @param splitEndOffset the new split end offset
	 */
	public void setSplitEndOffset(LongWritable splitEndOffset) {
		this.splitEndOffset = splitEndOffset;
	}

		
	/**
	 * Gets the total records emitted by map.
	 *
	 * @return the total records emitted by map
	 */
	public LongWritable getTotalRecordsEmittByMap() {
		return totalRecordsEmittByMap;
	}

	/**
	 * Sets the total records emitted by map.
	 *
	 * @param totalRecordsEmittByMap the new total records emitted by map
	 */
	public void setTotalRecordsEmittByMap(LongWritable totalRecordsEmittByMap) {
		this.totalRecordsEmittByMap = totalRecordsEmittByMap;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		fileName.write(out);
		jsonLineViolationList.write(out);
		splitEndOffset.write(out);
		totalRecordsEmittByMap.write(out);
		
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		fileName.readFields(in);
		jsonLineViolationList.readFields(in);
		splitEndOffset.readFields(in);
		totalRecordsEmittByMap.readFields(in);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileKeyViolationBean [fileName=" + fileName
				+ ", jsonLineViolationList=" + jsonLineViolationList
				+ ", splitEndOffset=" + splitEndOffset
				+ ", totalRecordsEmittByMap=" + totalRecordsEmittByMap + "]";
	}

	
	

}
