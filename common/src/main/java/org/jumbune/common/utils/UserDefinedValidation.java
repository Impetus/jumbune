/**
 * 
 */
package org.jumbune.common.utils;

import org.jumbune.common.utils.ConfigurationUtil;


/**
 * This Bean class takes UserDefinedValidation classes defined by user.
 */
public class UserDefinedValidation {
	
	/** The map key validator. */
	private String mapKeyValidator;
	
	/** The map value validator. */
	private String mapValueValidator;
	
	/** The reduce key validator. */
	private String reduceKeyValidator;
	
	/** The reduce value validator. */
	private String reduceValueValidator;

	/**
	 * <p>
	 * See {@link #setmapKeyValidator(String)}
	 * </p>.
	 *
	 * @return Returns the mapKeyValidator.
	 */
	public final String getMapKeyValidator() {
		return mapKeyValidator;
	}

	/**
	 * <p>
	 * Set the value of <code>mapKeyValidator</code>.
	 * </p>
	 * 
	 * @param mapKeyValidator
	 *            The mapKeyValidator to set.
	 */
	public final void setMapKeyValidator(String mapKeyValidator) {
		this.mapKeyValidator = ConfigurationUtil.convertQualifiedClassNameToInternalName(mapKeyValidator);
		
	}

	/**
	 * <p>
	 * See {@link #setmapValueValidator(String)}
	 * </p>.
	 *
	 * @return Returns the mapValueValidator.
	 */
	public final String getMapValueValidator() {
		return mapValueValidator;
	}

	/**
	 * <p>
	 * Set the value of <code>mapValueValidator</code>.
	 * </p>
	 * 
	 * @param mapValueValidator
	 *            The mapValueValidator to set.
	 */
	public final void setMapValueValidator(String mapValueValidator) {
		this.mapValueValidator = ConfigurationUtil.convertQualifiedClassNameToInternalName(mapValueValidator);
	}

	/**
	 * <p>
	 * See {@link #setreduceKeyValidator(String)}
	 * </p>.
	 *
	 * @return Returns the reduceKeyValidator.
	 */
	public final String getReduceKeyValidator() {
		return reduceKeyValidator;
	}

	/**
	 * <p>
	 * Set the value of <code>reduceKeyValidator</code>.
	 * </p>
	 * 
	 * @param reduceKeyValidator
	 *            The reduceKeyValidator to set.
	 */
	public final void setReduceKeyValidator(String reduceKeyValidator) {
		this.reduceKeyValidator = ConfigurationUtil.convertQualifiedClassNameToInternalName(reduceKeyValidator);
	}

	/**
	 * <p>
	 * See {@link #setreduceValueValidator(String)}
	 * </p>.
	 *
	 * @return Returns the reduceValueValidator.
	 */
	public final String getReduceValueValidator() {
		return reduceValueValidator;
	}

	/**
	 * <p>
	 * Set the value of <code>reduceValueValidator</code>.
	 * </p>
	 * 
	 * @param reduceValueValidator
	 *            The reduceValueValidator to set.
	 */
	public final void setReduceValueValidator(String reduceValueValidator) {
		this.reduceValueValidator = ConfigurationUtil.convertQualifiedClassNameToInternalName(reduceValueValidator);
	}

}
