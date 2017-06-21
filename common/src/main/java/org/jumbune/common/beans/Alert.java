package org.jumbune.common.beans;

import java.util.Date;

/**
 * The Class Alert.
 */
public class Alert {
	
	/** The level. */
	private String level;
	
	/** The name. */
	private String nodeIP;
	
	/** The message. */
	private String message;
	
	/** The date. */
	private String date;
	
	/** The job id. */
	private String jobId;

	private long firstOccurrence;
	
	private boolean skipOccuringSince;
	
	/**
	 * Instantiates a new alert.
	 */
	public Alert() {
		firstOccurrence = System.currentTimeMillis();
		skipOccuringSince = false;
	}
	
	/**
	 * Instantiates a new alert.
	 *
	 * @param level the level
	 * @param name the name
	 * @param message the message
	 * @param date the date
	 */
	public Alert(String level, String nodeIP, String message, String date) {
		this();
		this.level = level;
		this.nodeIP = nodeIP;
		this.message = message;
		this.date = date;
	}
	
	private Alert(String level, String nodeIP, String message, String date, long firstOccurrence) {
		this(level, nodeIP, message, date);
		this.firstOccurrence = firstOccurrence;
	}
	
	public Alert deepCopy() {
		return new Alert(level, nodeIP, message, date, firstOccurrence);
	}
	
	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * Sets the level.
	 *
	 * @param level the new level
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getNodeIP() {
		return nodeIP;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setNodeIP(String name) {
		this.nodeIP = name;
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
     * Gets the job id.
     *
     * @return the job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the job id.
     *
     * @param jobId the new job id
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
 
	/**
	 * Gets the first occurrence.
	 *
	 * @return the first occurrence
	 */
	public long getFirstOccurrence() {
		return firstOccurrence;
	}
	
	public boolean getSkipOccuringSince() {
		return skipOccuringSince;
	}

	public void setSkipOccuringSince(boolean skipOccuringSince) {
		this.skipOccuringSince = skipOccuringSince;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alert [level=" + level + ", nodeIP=" + nodeIP + ", message=" + message + ", date=" + date + ", jobId="
				+ jobId + ", firstOccurrence=" + new Date(firstOccurrence) + ", skipOccuringSince="
				+ skipOccuringSince + "]";
	}

}
