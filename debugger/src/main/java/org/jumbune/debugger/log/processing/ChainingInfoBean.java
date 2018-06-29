package org.jumbune.debugger.log.processing;

/**
 * This is for storing information related to Map-Reduce chaining or Job chaining
 * 
 * 
 */
public class ChainingInfoBean {

	/**
	 * name - the name of the Mapper,Reduce or any Job ID
	 */
	private String name;

	/**
	 * inputKeys - the total number of input keys
	 */
	private int inputKeys;

	/**
	 * contextWrites - the total number of context writes
	 */
	private int contextWrites;

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
	 * @return the contextWrites
	 */
	public int getContextWrites() {
		return contextWrites;
	}

	/**
	 * @param contextWrites
	 *            the contextWrites to set
	 */
	public void setContextWrites(int contextWrites) {
		this.contextWrites = contextWrites;
	}

	@Override
	public String toString() {
		return "ChainingInfoBean [name=" + name + ", inputKeys=" + inputKeys
				+ ", contextWrites=" + contextWrites + "]";
	}

	
}
