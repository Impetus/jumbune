
package org.jumbune.common.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO to store details for a MapReduce Job.
 */
@XmlRootElement(name="jobs")
public class JobDetails {
	
	private List<JobInfo> job ;

	public List<JobInfo> getJob() {
		return job;
	}

	@XmlElement
	public void setJob(List<JobInfo> job) {
		this.job = job;
	}

	@Override
	public String toString() {
		return "JobDetails [job=" + job + "]";
	}
	
}
