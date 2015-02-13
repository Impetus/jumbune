package org.jumbune.common.beans;


/**
 * POJO to store details of all phases.
 */
public class PhaseOutput {

	/** The setup details. */
	private PhaseDetails setupDetails;
	
	/** The map details. */
	private PhaseDetails mapDetails;
	
	/** The reduce details. */
	private PhaseDetails reduceDetails;
	
	/** The cleanup details. */
	private PhaseDetails cleanupDetails;

	/**
	 * Gets the setup details.
	 *
	 * @return the setupDetails
	 */
	public PhaseDetails getSetupDetails() {
		return setupDetails;
	}

	/**
	 * Sets the setup details.
	 *
	 * @param setupDetails the setupDetails to set
	 */
	public void setSetupDetails(PhaseDetails setupDetails) {
		this.setupDetails = setupDetails;
	}

	/**
	 * Gets the map details.
	 *
	 * @return the mapDetails
	 */
	public PhaseDetails getMapDetails() {
		return mapDetails;
	}

	/**
	 * Sets the map details.
	 *
	 * @param mapDetails the mapDetails to set
	 */
	public void setMapDetails(PhaseDetails mapDetails) {
		this.mapDetails = mapDetails;
	}

	/**
	 * Gets the reduce details.
	 *
	 * @return the reduceDetails
	 */
	public PhaseDetails getReduceDetails() {
		return reduceDetails;
	}

	/**
	 * Sets the reduce details.
	 *
	 * @param reduceDetails the reduceDetails to set
	 */
	public void setReduceDetails(PhaseDetails reduceDetails) {
		this.reduceDetails = reduceDetails;
	}

	/**
	 * Gets the cleanup details.
	 *
	 * @return the cleanupDetails
	 */
	public PhaseDetails getCleanupDetails() {
		return cleanupDetails;
	}

	/**
	 * Sets the cleanup details.
	 *
	 * @param cleanupDetails the cleanupDetails to set
	 */
	public void setCleanupDetails(PhaseDetails cleanupDetails) {
		this.cleanupDetails = cleanupDetails;
	}

	@Override
	public String toString() {
		return "PhaseOutput [setupDetails=" + setupDetails + ", mapDetails="
				+ mapDetails + ", reduceDetails=" + reduceDetails
				+ ", cleanupDetails=" + cleanupDetails + "]";
	}


}
