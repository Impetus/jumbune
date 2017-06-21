package org.jumbune.clusterprofiling.recommendations;

/**
 * The Class Recommendations is a pojo containing the recommendations information.
 */
public class Recommendations {

	/** The hostName. */
	private String hostName;

	/** The recommendation. */
	private String recommendation;

	/**
	 * Instantiates a new recommendations.
	 *
	 * @param hostName the host name
	 * @param recommendation the recommendation
	 */
	public Recommendations(String hostName, String recommendation) {
		this.hostName = hostName;
		this.recommendation = recommendation;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the host name.
	 *
	 * @param hostName the new host name
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Gets the recommendation.
	 *
	 * @return the recommendation
	 */
	public String getRecommendation() {
		return recommendation;
	}

	/**
	 * Sets the recommendation.
	 *
	 * @param recommendation the new recommendation
	 */
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	@Override
	public String toString() {
		return "Recommendations [hostName=" + hostName + ", recommendation=" + recommendation + "]";
	}

}
