package org.jumbune.common.beans.profiling;

import java.util.Map;

import org.jumbune.common.beans.NodeSystemStats;


/**
 * POJO to store all details to be shown to the user corresponding to a MapReduce Job.
 */
public class JobOutput {

	/** The job id. */
	private String jobID;
	
	/** The user. */
	private String user;
	
	/** The job name. */
	private String jobName;
	
	/** The total time. */
	private long totalTime;
	
	/** The total time in milli sec. */
	private long totalTimeInMilliSec;
	
	/** The outcome. */
	private String outcome;
	
	/** The cpu usage. */
	private Map<Long, Float> cpuUsage;
	
	/** The mem usage. */
	private Map<Long, Float> memUsage;
	
	/** The phase output. */
	private PhaseOutput phaseOutput;
	
	/** The node stats. */
	private Map<String, NodeSystemStats> nodeStats;
	
	/** The launch time. */
	private long launchTime ;
	
	/** The submit time. */
	private long submitTime ;

	/**
	 * Gets the job id.
	 *
	 * @return the jobID
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the job name.
	 *
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the job name.
	 *
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Gets the total time.
	 *
	 * @return the totalTime
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * Sets the total time.
	 *
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * Gets the outcome.
	 *
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * Sets the outcome.
	 *
	 * @param outcome the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	/**
	 * Gets the phase output.
	 *
	 * @return the phaseOutput
	 */
	public PhaseOutput getPhaseOutput() {
		return phaseOutput;
	}

	/**
	 * Sets the phase output.
	 *
	 * @param phaseOutput the phaseOutput to set
	 */
	public void setPhaseOutput(PhaseOutput phaseOutput) {
		this.phaseOutput = phaseOutput;
	}

	/**
	 * Gets the cpu usage.
	 *
	 * @return the cpuUsage
	 */
	public Map<Long, Float> getCpuUsage() {
		return cpuUsage;
	}

	/**
	 * Sets the cpu usage.
	 *
	 * @param cpuUsage the cpuUsage to set
	 */
	public void setCpuUsage(Map<Long, Float> cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	/**
	 * Gets the mem usage.
	 *
	 * @return the memUsage
	 */
	public Map<Long, Float> getMemUsage() {
		return memUsage;
	}

	/**
	 * Sets the mem usage.
	 *
	 * @param memUsage the memUsage to set
	 */
	public void setMemUsage(Map<Long, Float> memUsage) {
		this.memUsage = memUsage;
	}

	/**
	 * Gets the total time in milli sec.
	 *
	 * @return the totalTimeInMilliSec
	 */
	public long getTotalTimeInMilliSec() {
		return totalTimeInMilliSec;
	}

	/**
	 * Sets the total time in milli sec.
	 *
	 * @param totalTimeInMilliSec the totalTimeInMilliSec to set
	 */
	public void setTotalTimeInMilliSec(long totalTimeInMilliSec) {
		this.totalTimeInMilliSec = totalTimeInMilliSec;
	}

	/**
	 * Gets the node stats.
	 *
	 * @return the nodeStats
	 */
	public Map<String, NodeSystemStats> getNodeStats() {
		return nodeStats;
	}

	/**
	 * Sets the node stats.
	 *
	 * @param nodeStats the nodeStats to set
	 */
	public void setNodeStats(Map<String, NodeSystemStats> nodeStats) {
		this.nodeStats = nodeStats;
	}
	
	/**
	 * Gets the launch time.
	 *
	 * @return the launch time
	 */
	public long getLaunchTime() {
		return launchTime;
	}

	/**
	 * Sets the launch time.
	 *
	 * @param launchTime the launch time
	 */
	public void setLaunchTime(long launchTime) {
		this.launchTime = launchTime;
	}

	/**
	 * Gets the submit time.
	 *
	 * @return the submit time
	 */
	public long getSubmitTime() {
		return submitTime;
	}

	/**
	 * Sets the submit time.
	 *
	 * @param submitTime the submit time
	 */
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}

	@Override
	public String toString() {
		return "JobOutput [jobID=" + jobID + ", user=" + user + ", jobName="
				+ jobName + ", totalTime=" + totalTime
				+ ", totalTimeInMilliSec=" + totalTimeInMilliSec + ", outcome="
				+ outcome + ", cpuUsage=" + cpuUsage + ", memUsage=" + memUsage
				+ ", phaseOutput=" + phaseOutput + ", nodeStats=" + nodeStats
				+ ", launchTime=" + launchTime + ", submitTime=" + submitTime
				+ "]";
	}

	

	
	
	

}
