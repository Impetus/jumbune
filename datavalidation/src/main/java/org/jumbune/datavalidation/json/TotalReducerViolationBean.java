package org.jumbune.datavalidation.json;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;


/**
 * The Class TotalReducerViolationBean.
 */
public class TotalReducerViolationBean implements Writable{
	
	/** The total line violation. */
	IntWritable totalLineViolation;
	
	/** The total key violation. */
	IntWritable totalKeyViolation;
	
	/** The reducer violation bean list. */
	ArrayListWritable<ReducerViolationBean> reducerViolationBeanList ;

	/**
	 * Instantiates a new total reducer violation bean.
	 */
	public TotalReducerViolationBean(){
		totalLineViolation = new IntWritable();
		totalKeyViolation = new IntWritable();
		reducerViolationBeanList = new ArrayListWritable<ReducerViolationBean>();
	}
	
	
	
	/**
	 * Gets the total line violation.
	 *
	 * @return the total line violation
	 */
	public IntWritable getTotalLineViolation() {
		return totalLineViolation;
	}

	/**
	 * Sets the total line violation.
	 *
	 * @param totalLineViolation the new total line violation
	 */
	public void setTotalLineViolation(IntWritable totalLineViolation) {
		this.totalLineViolation = totalLineViolation;
	}

	/**
	 * Gets the total key violation.
	 *
	 * @return the total key violation
	 */
	public IntWritable getTotalKeyViolation() {
		return totalKeyViolation;
	}

	/**
	 * Sets the total key violation.
	 *
	 * @param totalKeyViolation the new total key violation
	 */
	public void setTotalKeyViolation(IntWritable totalKeyViolation) {
		this.totalKeyViolation = totalKeyViolation;
	}

	/**
	 * Gets the reducer violation bean list.
	 *
	 * @return the reducer violation bean list
	 */
	public ArrayListWritable<ReducerViolationBean> getReducerViolationBeanList() {
		return reducerViolationBeanList;
	}

	/**
	 * Sets the reducer violation bean list.
	 *
	 * @param reducerViolationBeanList the new reducer violation bean list
	 */
	public void setReducerViolationBeanList(
			ArrayListWritable<ReducerViolationBean> reducerViolationBeanList) {
		this.reducerViolationBeanList = reducerViolationBeanList;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */
	public void write(DataOutput out) throws IOException {
		totalLineViolation.write(out);
		totalKeyViolation.write(out);
		reducerViolationBeanList.write(out);
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */
	public void readFields(DataInput in) throws IOException {
		totalLineViolation.readFields(in);
		totalKeyViolation.readFields(in);
		reducerViolationBeanList.readFields(in);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ListReducerViolationBean [totalViolation=" + totalLineViolation
				+ ", keyViolation=" + totalKeyViolation
				+ ", reducerViolationBeanList=" + reducerViolationBeanList
				+ "]";
	}


}
