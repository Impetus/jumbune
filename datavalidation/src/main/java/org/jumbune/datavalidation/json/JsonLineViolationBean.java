package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableFactories;


/**
 * The Class JsonLineViolationBean.
 */
public class JsonLineViolationBean implements Writable{
	
	/** The line number. */
	private IntWritable lineNumber;
	
	/** The json key violation list. */
	private List <JsonKeyViolationBean> jsonKeyViolationList;
	
	/**
	 * Instantiates a new json line violation bean.
	 */
	public JsonLineViolationBean(){
		jsonKeyViolationList = new ArrayList<JsonKeyViolationBean>(0);
		lineNumber = new IntWritable();
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
	 * Gets the json key violation list.
	 *
	 * @return the json key violation list
	 */
	public List<JsonKeyViolationBean> getJsonKeyViolationList() {
		return jsonKeyViolationList;
	}


	/**
	 * Sets the json key violation list.
	 *
	 * @param keyJsonViolationList the new json key violation list
	 */
	public void setJsonKeyViolationList(List<JsonKeyViolationBean> keyJsonViolationList) {
		this.jsonKeyViolationList = keyJsonViolationList;
	}

	/**
	 * Reset json key violation list.
	 */
	public void resetJsonKeyViolationList(){
		this.jsonKeyViolationList = new ArrayList<JsonKeyViolationBean>(0);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		lineNumber.write(out);
		out.writeInt(jsonKeyViolationList.size());
		for(int i =0; i < jsonKeyViolationList.size() ; i++){
			jsonKeyViolationList.get(i).write(out);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		lineNumber.readFields(in);
		int listSize = in.readInt();
		for(int i =0; i <listSize ; i++){
			Writable value = WritableFactories.newInstance(JsonKeyViolationBean.class);
			value.readFields(in);
			jsonKeyViolationList.add((JsonKeyViolationBean) value);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonLineViolationBean [lineNumber=" + lineNumber
				+ ", jsonKeyViolationList=" + jsonKeyViolationList + "]";
	}

}
