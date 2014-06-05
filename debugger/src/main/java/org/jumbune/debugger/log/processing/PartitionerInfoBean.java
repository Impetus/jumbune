package org.jumbune.debugger.log.processing;

/**
 * This is for storing information related to performance of the partitioner
 * 
 */
public class PartitionerInfoBean {

	/**
	 * name - the name of the instance of the reducer
	 */
	private String name;

	/**
	 * inputKeys - the total number of input keys
	 */
	private int inputKeys;

	/**
	 * idealDistribution - ideally expected number of input keys
	 */
	private int idealDistribution;

	/**
	 * variance - variance from the ideal condition
	 */
	private float variance;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the inputKeys
	 */
	public int getInputKeys() {
		return inputKeys;
	}

	/**
	 * @param inputKeys
	 *            the inputKeys to set
	 */
	public void setInputKeys(int inputKeys) {
		this.inputKeys = inputKeys;
	}

	/**
	 * @return the idealDistribution
	 */
	public int getIdealDistribution() {
		return idealDistribution;
	}

	/**
	 * @param idealDistribution
	 *            the idealDistribution to set
	 */
	public void setIdealDistribution(int idealDistribution) {
		this.idealDistribution = idealDistribution;
	}

	/**
	 * @return the variance
	 */
	public float getVariance() {
		return variance;
	}

	/**
	 * @param variance
	 *            the variance to set
	 */
	public void setVariance(float variance) {
		this.variance = variance;
	}

}
