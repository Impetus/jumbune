package org.jumbune.common.beans.cluster;

import java.util.List;


/**
 * POJO to store location of the mapreduce attempt.
 */
public class Location {

	/** The layers. */
	private List<String> layers;

	/**
	 * Gets the layers.
	 *
	 * @return the layers
	 */
	public List<String> getLayers() {
		return layers;
	}

	/**
	 * Sets the layers.
	 *
	 * @param layers the layers to set
	 */
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}

}
