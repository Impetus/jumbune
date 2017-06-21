package org.jumbune.common.beans;

/**
 * The Class XmlElementBean is a pojo containing the xml validations to be performed.
 */
public class XmlElementBean {
	
	/** The element name. */
	private String elementName ;
	
	/** The null check. */
	private String nullCheck ;
	
	/** The regex. */
	private String regex;

	
	/**
	 * Gets the null check.
	 *
	 * @return the null check
	 */
	public String getNullCheck() {
		return nullCheck;
	}

	/**
	 * Sets the null check.
	 *
	 * @param nullCheck the new null check
	 */
	public void setNullCheck(String nullCheck) {
		this.nullCheck = nullCheck;
	}

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Sets the regex.
	 *
	 * @param regex the new regex
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * Gets the element name.
	 *
	 * @return the element name
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * Sets the element name.
	 *
	 * @param elementName the new element name
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	@Override
	public String toString() {
		return "XmlElementBean [elementName=" + elementName + ", nullCheck=" + nullCheck + ", regex=" + regex + "]";
	}

	
	
	
}
