package org.jumbune.utils.yarn.communicators;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;


public class YarnCommunicatorUtil {

	/**
	 * Prepares the JobId instance
	 * @param appId
	 * @param id
	 * @return the JobId
	 */
	public static JobId getJobId(ApplicationId appId, int id){
		return MRBuilderUtils.newJobId(appId, id);
	}
	
	/**
	 * Prepares the Task Id instance
	 * @param jobId
	 * @param id
	 * @param taskType
	 * @return the TaskId
	 */
	public static TaskId getTaskId(JobId jobId, int id, TaskType taskType){
		return MRBuilderUtils.newTaskId(jobId, id, taskType);
	}

	/**
	 * Prepares the Task Attempt Id
	 * @param taskId
	 * @param attemptId
	 * @return the TaskAttemptId
	 */
	public static TaskAttemptId getTaskAttemptId(TaskId taskId, int attemptId){
		return MRBuilderUtils.newTaskAttemptId(taskId, attemptId);
	}
	
	/**
	 *  Prepares JobId instance from the string JobId
	 * @param jobId
	 * @return the JobId
	 */
	public static JobId getJobId(String jobId){
	    return TypeConverter.toYarn(JobID.forName(jobId));
	}
	
	/**
	 * Prepares the Application Id
	 * @param timestamp
	 * @param id
	 * @return
	 */
	public static ApplicationId getApplicationId(final String timestamp, final int id){
		return new ApplicationId() {
			
			@Override
			protected void setId(int id) {
			}
			
			@Override
			protected void setClusterTimestamp(long clusterTimestamp) {
			}
			
			@Override
			public int getId() {
				return id;
			}
			
			@Override
			public long getClusterTimestamp() {
				return Long.valueOf(timestamp);
			}
			
			@Override
			protected void build() {
				
			}
		};
	}
}
