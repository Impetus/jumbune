package org.jumbune.debugger.log.processing;


/**
 * The Class display inforamtion of reducer instances on dashboard.
*/
public class ReducerInfo {

	/** The name of reducer when running a job in hadoop */
	private String name;

	/** variance - variance from the ideal condition. */
	private float variance;

	/**
	 * Gets the variance.
	 * 
	 * @return the variance
	 */
	public float getVariance() {
		return variance;
	}

	/**
	 * Sets the variance.
	 * 
	 * @param variance
	 *            the new variance
	 */
	public void setVariance(float variance) {
		this.variance = variance;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
