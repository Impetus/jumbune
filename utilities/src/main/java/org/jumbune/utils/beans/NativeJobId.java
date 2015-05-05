package org.jumbune.utils.beans;




/**
 * This interface serves as a super type for constructing both Yarn and Non-Yarn based
 * JobIds for hadoop jobs.
 *
 * @param <T>
 */
public interface NativeJobId<T> {
	
	public T getJobId();

}
