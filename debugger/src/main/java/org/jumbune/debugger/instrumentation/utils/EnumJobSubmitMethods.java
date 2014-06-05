package org.jumbune.debugger.instrumentation.utils;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapreduce.Job;
import org.objectweb.asm.Type;

/**
 * This ENUM provides the constants for the job submission methods and the corresponding owners.
 * 
 */

public enum EnumJobSubmitMethods {

	RUN_JOB("runJob", Type.getType(JobClient.class)), JOB_WAIT_FOR_COMPLETION("waitForCompletion", Type.getType(Job.class)), JOB_SUBMIT("submit",
			Type.getType(Job.class)), SUBMIT_JOB("submitJob", Type.getType(JobClient.class));

	private final String submissionType;
	private final Type owner;

	EnumJobSubmitMethods(String submissionType, Type owner) {
		this.submissionType = submissionType;
		this.owner = owner;
	}

	@Override
	public String toString() {
		return submissionType;
	}

	public Type getOwner() {
		return owner;
	}

}
