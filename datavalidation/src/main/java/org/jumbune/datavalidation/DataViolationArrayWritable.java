package org.jumbune.datavalidation;

import org.apache.hadoop.io.ArrayWritable;


/**
 * A Writable for arrays containing instances of DataViolationWritableBean.
 * 

 */
public class DataViolationArrayWritable extends ArrayWritable {

	/**
	 * Instantiates a new data violation array writable.
	 */
	public DataViolationArrayWritable() {
		super(FileViolationsWritable.class);
	}

}
