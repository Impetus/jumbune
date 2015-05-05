package org.jumbune.utils.beans;

import org.jumbune.utils.beans.NativeJobId;
import org.apache.hadoop.mapreduce.JobID;

/**
 * This class constructs the jobId which is required for Non-Yarn distributions of hadoop.
 * 
 */
public class NonYarnJobId implements NativeJobId<JobID>{

	private String prefix;
	private int id;
	
	
	 public NonYarnJobId(JobID jobId){
		prefix = jobId.getJtIdentifier();
		id = jobId.getId();
	 }
	

	 /**
	 * This NativeJobId is workable on Non Yarn Hadoop
	 * @return
	 */

	@Override
	public JobID getJobId() {
		return new JobID(prefix, id);
	
	}
	
	
}
