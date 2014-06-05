package org.jumbune.common.beans;


/**
 * This class is the bean for all Unavailable nodes.
 */

public class UnavailableHost {
	
	/** The node ip. */
	private String nodeIp;
	
	/** The message. */
	private String message;

	/**
	 * Gets the node ip.
	 *
	 * @return the nodeIp
	 */
	public String getNodeIp() {
		return nodeIp;
	}

	/**
	 * Sets the node ip.
	 *
	 * @param nodeIp the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnavailableHost [nodeIp=" + nodeIp + "]";
	}
}
