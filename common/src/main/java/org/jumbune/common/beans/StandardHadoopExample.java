package org.jumbune.common.beans;

/**
 * The Enum StandardHadoopExample.
 */
public enum StandardHadoopExample {

	/** The pi. */
	PI("PI"), 
	/** The wordcount. */
	WORDCOUNT("WORDCOUNT"), 
	/** The teragen. */
	TERAGEN("TERAGEN"),
	/** The terasort. */
	TERASORT("TERASORT"), 
	/** The randomwriter. */
	RANDOMWRITER("RANDOMWRITER");

	/** The enumvalues. */
	private String enumvalues;

	/**
	 * Instantiates a new standard hadoop example.
	 *
	 * @param examples
	 *            the examples
	 */
	private StandardHadoopExample(String examples) {
		this.enumvalues = examples;
	}

	/**
	 * Gets the enumvalues.
	 *
	 * @return the enumvalues
	 */
	public String getEnumvalues() {
		return enumvalues;
	}

	/**
	 * Sets the enumvalues.
	 *
	 * @param enumvalues
	 *            the new enumvalues
	 */
	public void setEnumvalues(String enumvalues) {
		this.enumvalues = enumvalues;
	}

}
