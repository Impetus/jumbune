package org.jumbune.common.beans;

/***
 * Enum for map, reduce, job-setup, job-cleanup, task-cleanup task types. 
 *
 */
public enum MRTaskType implements TaskType {
	  MAP, REDUCE, JOB_SETUP, JOB_CLEANUP, TASK_CLEANUP
	}