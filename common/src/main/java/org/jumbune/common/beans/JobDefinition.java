package org.jumbune.common.beans;


/**
 * This class is the bean for the job settings entries from yaml.
 */
public class JobDefinition {
	
	/** The name. */
	private String name;
	
	/** The job class. */
	private String jobClass;
	
	/** The parameters. */
	private String parameters;

	/**
	 * Gets the job class.
	 *
	 * @return the job class
	 */
	public final String getJobClass() {
		return jobClass;
	}

	/**
	 * Sets the job class.
	 *
	 * @param jobClass the new job class
	 */
	public final void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public final void setName(String name) {
		this.name = name;
	}



	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public final String getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters the new parameters
	 */
	public final void setParameters(String parameters) {
		this.parameters = parameters;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobDefinition [name=" + name + ", jobClass=" + jobClass + ", parameters=" + parameters + "]";
	}
}
