package org.jumbune.common.beans;



/**
 * This class is bean for the classpath settings from the yaml.
 */
public class Classpath {
	
	/** The user supplied. */
	private ClasspathElement userSupplied;
	
	/** The jumbune supplied. */
	private ClasspathElement jumbuneSupplied;

	/**
	 * <p>
	 * See {@link #setuserSupplied(ClasspathElement)}
	 * </p>.
	 *
	 * @return Returns the userSupplied.
	 */
	public ClasspathElement getUserSupplied() {
		return userSupplied;
	}

	/**
	 * <p>
	 * Set the value of <code>userSupplied</code>.
	 * </p>
	 * 
	 * @param userSupplied
	 *            The userSupplied to set.
	 */
	public void setUserSupplied(ClasspathElement userSupplied) {
		this.userSupplied = userSupplied;
	}

	/**
	 * <p>
	 * See {@link #setJumbuneSupplied(ClasspathElement)}
	 * </p>.
	 *
	 * @return Returns the jumbuneSupplied.
	 */
	public ClasspathElement getJumbuneSupplied() {
		return jumbuneSupplied;
	}

	/**
	 * <p>
	 * Set the value of <code>jumbuneSupplied</code>.
	 * </p>
	 * 
	 * @param jumbuneSupplied
	 *            The jumbuneSupplied to set.
	 */
	public void setJumbuneSupplied(ClasspathElement jumbuneSupplied) {
		this.jumbuneSupplied = jumbuneSupplied;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Classpath [userSupplied=" + userSupplied + ", jumbuneSupplied=" + jumbuneSupplied + "]";
	}
}
