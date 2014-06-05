package org.jumbune.common.beans;


/**
 * This class is the bean for the map reduce regex entries from yaml.
 */
public class RegexDefinition {
	
	/** The map key. */
	private String mapKey;
	
	/** The map value. */
	private String mapValue;
	
	/** The reduce key. */
	private String reduceKey;
	
	/** The reduce value. */
	private String reduceValue;

	/**
	 * <p>
	 * See {@link #setmapKey(String)}
	 * </p>.
	 *
	 * @return Returns the mapKey.
	 */
	public final String getMapKey() {
		return mapKey;
	}

	/**
	 * <p>
	 * Set the value of <code>mapKey</code>.
	 * </p>
	 * 
	 * @param mapKey
	 *            The mapKey to set.
	 */
	public final void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	/**
	 * <p>
	 * See {@link #setmapValue(String)}
	 * </p>.
	 *
	 * @return Returns the mapValue.
	 */
	public final String getMapValue() {
		return mapValue;
	}

	/**
	 * <p>
	 * Set the value of <code>mapValue</code>.
	 * </p>
	 * 
	 * @param mapValue
	 *            The mapValue to set.
	 */
	public final void setMapValue(String mapValue) {
		this.mapValue = mapValue;
	}

	/**
	 * <p>
	 * See {@link #setreduceKey(String)}
	 * </p>.
	 *
	 * @return Returns the reduceKey.
	 */
	public final String getReduceKey() {
		return reduceKey;
	}

	/**
	 * <p>
	 * Set the value of <code>reduceKey</code>.
	 * </p>
	 * 
	 * @param reduceKey
	 *            The reduceKey to set.
	 */
	public final void setReduceKey(String reduceKey) {
		this.reduceKey = reduceKey;
	}

	/**
	 * <p>
	 * See {@link #setreduceValue(String)}
	 * </p>.
	 *
	 * @return Returns the reduceValue.
	 */
	public final String getReduceValue() {
		return reduceValue;
	}

	/**
	 * <p>
	 * Set the value of <code>reduceValue</code>.
	 * </p>
	 * 
	 * @param reduceValue
	 *            The reduceValue to set.
	 */
	public final void setReduceValue(String reduceValue) {
		this.reduceValue = reduceValue;
	}
}
