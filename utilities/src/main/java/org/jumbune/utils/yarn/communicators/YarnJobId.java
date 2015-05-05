package org.jumbune.utils.yarn.communicators;


import org.jumbune.utils.beans.NativeJobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;

/**
 * This class constructs the jobId which is required for Yarn distributions of hadoop.
 * 
 */
public class YarnJobId implements NativeJobId<JobId> {
	
	private	String prefix;	
	private int id;	
	private JobId jobId;
	
	public YarnJobId(JobId jobId){
		prefix = String.valueOf(jobId.getAppId().getClusterTimestamp());
		id = jobId.getId();
		this.jobId=jobId;
	}
	
	/**
	 * This YarnJobId is workable on Yarn Hadoop
	 * @return
	 */	
	
	public JobId getJobId() {
		return this.jobId;
		
	}

	
	
	@Override
	public boolean equals(Object obj) {
	       if(this.jobId.toString().equals(obj.toString())){
	    	   return true;
	       }
	       return false;
	}

	@Override
	public String toString() {
		return this.jobId.toString();
	}

}
