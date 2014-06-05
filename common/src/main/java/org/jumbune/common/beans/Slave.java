package org.jumbune.common.beans;

import java.util.Arrays;
import java.util.List;

import org.jumbune.utils.YamlUtil;
import org.yaml.snakeyaml.error.YAMLException;



/**
 * This class is the bean for the nodes information for log collection and lib
 * distribution.
 */
public class Slave {
	
	/** The hosts. */
	private String[] hosts;
	
	/** The location. */
	private String location;
	
	/** The user. */
	private String user;
	
	/** The unavailable hosts. */
	private List<UnavailableHost> unavailableHosts;
	
	/** The host range from value. */
	private String hostRangeFromValue;
	
	/** The host range to value. */
	private String hostRangeToValue;
	
	/** The enable host range. */
	private String enableHostRange;

	

	/**
	 * Gets the hosts.
	 *
	 * @return the hosts
	 */
	public String[] getHosts() {
		return hosts;
	}

	/**
	 * Sets the hosts.
	 *
	 * @param hosts the new hosts
	 */
	public void setHosts(String[] hosts) {

		this.hosts = hosts.clone();
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * Gets the unavailable hosts.
	 *
	 * @return the unavailableHosts
	 */
	public List<UnavailableHost> getUnavailableHosts() {
		return unavailableHosts;
	}

	/**
	 * Sets the unavailable hosts.
	 *
	 * @param unavailableHosts the unavailableHosts to set
	 */
	public void setUnavailableHosts(List<UnavailableHost> unavailableHosts) {
		this.unavailableHosts = unavailableHosts;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public final void setLocation(final String location) {
		String locationTemp =location;
		if (locationTemp != null) {
			locationTemp = YamlUtil.getAndReplaceHolders(locationTemp);
			if (!YamlUtil.validateFileSystemLocation(locationTemp)) {
				throw new YAMLException(
						"Location provided in Slave is not in correct format!!");
			}
			this.location = locationTemp;
		}
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public final void setUser(String user) {
		this.user = YamlUtil.getAndReplaceHolders(user);
	}

	
	/**
	 * Gets the host range from value.
	 *
	 * @return the host range from value
	 */
	public String getHostRangeFromValue() {
		return hostRangeFromValue;
	}

	/**
	 * Sets the host range from value.
	 *
	 * @param hostRangeFromValue the new host range from value
	 */
	public void setHostRangeFromValue(String hostRangeFromValue) {
		this.hostRangeFromValue = hostRangeFromValue;
	}

	/**
	 * Gets the host range to value.
	 *
	 * @return the host range to value
	 */
	public String getHostRangeToValue() {
		return hostRangeToValue;
	}

	/**
	 * Sets the host range to value.
	 *
	 * @param hostRangeToValue the new host range to value
	 */
	public void setHostRangeToValue(String hostRangeToValue) {
		this.hostRangeToValue = hostRangeToValue;
	}

	
	/**
	 * Gets the enable host range.
	 *
	 * @return the enable host range
	 */
	public String getEnableHostRange() {
		return enableHostRange;
	}

	/**
	 * Sets the enable host range.
	 *
	 * @param enableHostRange the new enable host range
	 */
	public void setEnableHostRange(String enableHostRange) {
		this.enableHostRange = enableHostRange;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Slave [hosts=" + Arrays.toString(hosts) + ", location="
				+ location + ", user=" + user + ", enableHostRange=" + enableHostRange 
				+", hostRangeFromValue="+hostRangeFromValue +", hostRangeToValue="+hostRangeToValue+"]";
	}
}
