package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableFactories;


/**
 * The Class FileReducerViolationBean.
 */
public class FileReducerViolationBean implements Writable{
	
	/** The file violation. */
	private Map <Writable,Writable> fileViolation;

	/** The count. */
	private IntWritable count;

	/**
	 * Instantiates a new file reducer violation bean.
	 */
	public FileReducerViolationBean() {
		fileViolation = new HashMap<Writable, Writable>(0);
		count = new IntWritable();
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public IntWritable getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(IntWritable count) {
		this.count = count;
	}

	/**
	 * Gets the file violation.
	 *
	 * @return the file violation
	 */
	public Map<Writable, Writable> getFileViolation() {
		return fileViolation;
	}

	/**
	 * Sets the file violation.
	 *
	 * @param fileViolation the file violation
	 */
	public void setFileViolation(Map<Writable, Writable> fileViolation) {
		this.fileViolation = fileViolation;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		out.writeInt(fileViolation.size());
		for (Map.Entry<Writable, Writable> e: fileViolation.entrySet()) {
				e.getKey().write(out);
			    e.getValue().write(out);
			}
		count.write(out);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		fileViolation.clear();
		int entries = in.readInt();
		for(int i=0;i<entries;i++){
			 Writable key = WritableFactories.newInstance(Text.class);
			 key.readFields(in);
			 Writable value = WritableFactories.newInstance(TotalReducerViolationBean.class);
			 value.readFields(in);
			 fileViolation.put(key, value);		
		}
		count.readFields(in);
		
	}

	 /**
 	 * Put all.
 	 *
 	 * @param t the t
 	 */
 	public void putAll(Map<? extends Writable, ? extends Writable> t) {
		 for (Map.Entry<? extends Writable, ? extends Writable> e: t.entrySet()) {
			 fileViolation.put(e.getKey(), e.getValue());
		     }
		 }
	
	 /**
 	 * Reset file violation.
 	 */
 	public void resetFileViolation(){
		 fileViolation = new HashMap<Writable,Writable>(0);
		}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileReducerViolationBean [fileViolation=" + fileViolation
				+ ", count=" + count + "]";
	}
	 
	 
}
