package org.jumbune.remoting.writable;

import java.io.Serializable;
import java.util.List;

import org.jumbune.remoting.common.ApiInvokeHintsEnum;


/**
 * Class Command Writable used for typed remoting
 *
 */
public class CommandWritable implements Serializable{
	
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 2125648606871472571L;
	
	/**
	 * The username
	 */
	 private String username;
	
	/**
	 * The password
	 */
	private String password;
	
	/**
	 * indicates if rsa file is being used
	 */
	private boolean hasRSA;
	
	/**
	 * specifies rsa file path
	 */
	private String rsaFilePath;
	
	/**
	 * indicates if dsa file is being used
	 */
	private boolean hasDSA;
	
	/**
	 * specifies dsa file path
	 */
	private String dsaFilePath;
	
	/**
	 * indicates if given command is to be run on master node
	 */
	private boolean isCommandForMaster;
	
	/**
	 * the master host name
	 */
	 private String masterHostname;
		
	/**
	 * the slave host name
	 */
	private String slaveHostname;
	
	/**
	 * the list of commands that needs to be processed at the remoting end.
	 */
	private List<Command> batchedCommands;
		
	/**
	 * Customized api invocations  
	 */
	private ApiInvokeHintsEnum apiInvokeHints;
	
	/**
	 *  This should be called when command requires a public-key authentication.
	 */
	private boolean isAuthenticationRequired;
	
/*	public CommandWritable(){
		batchedCommands = new ArrayList<Command>(1);
	}
*/	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the hasRSA
	 */
	public boolean isHasRSA() {
		return hasRSA;
	}
	
	/**
	 * @param hasRSA the hasRSA to set
	 */
	public void setHasRSA(boolean hasRSA) {
		this.hasRSA = hasRSA;
	}
	
	/**
	 * @return the rsaFilePath
	 */
	public String getRsaFilePath() {
		return rsaFilePath;
	}
	
	/**
	 * @param rsaFilePath the rsaFilePath to set
	 */
	public void setRsaFilePath(String rsaFilePath) {
		this.rsaFilePath = rsaFilePath;
	}
	
	/**
	 * @return the hasDSA
	 */
	public boolean isHasDSA() {
		return hasDSA;
	}
	
	/**
	 * @param hasDSA the hasDSA to set
	 */
	public void setHasDSA(boolean hasDSA) {
		this.hasDSA = hasDSA;
	}
	
	/**
	 * @return the dsaFilePath
	 */
	public String getDsaFilePath() {
		return dsaFilePath;
	}
	
	/**
	 * @param dsaFilePath the dsaFilePath to set
	 */
	public void setDsaFilePath(String dsaFilePath) {
		this.dsaFilePath = dsaFilePath;
	}
	
	/**
	 * @return the isCommandForMaster
	 */
	public boolean isCommandForMaster() {
		return isCommandForMaster;
	}
	
	/**
	 * @param isCommandForMaster the isCommandForMaster to set
	 */
	public void setCommandForMaster(boolean isCommandForMaster) {
		this.isCommandForMaster = isCommandForMaster;
	}
	
	/**
	 * @return the masterHostname
	 */
	public String getMasterHostname() {
		return masterHostname;
	}

	/**
	 * @param masterHostname the masterHostname to set
	 */
	public void setMasterHostname(String masterHostname) {
		this.masterHostname = masterHostname;
	}

	/**
	 * @return the batchedCommands
	 */
	public List<Command> getBatchedCommands() {
		return batchedCommands;
	}

	/**
	 * @param batchedCommands the batchedCommands to set
	 */
	public void setBatchedCommands(List<Command> batchedCommands) {
		this.batchedCommands = batchedCommands;
	}

	/**
	 * @return the slaveHost
	 */
	public String getSlaveHost() {
		return slaveHostname;
	}
	
	/**
	 * @param slaveHost the slaveHost to set
	 */
	public void setSlaveHost(String slaveHost) {
		this.slaveHostname = slaveHost;
	}
	
	/**
	 * @return the apiInvokeHints
	 */
	public ApiInvokeHintsEnum getApiInvokeHints() {
		return apiInvokeHints;
	}

	/**
	 * @param apiInvokeHints the apiInvokeHints to set
	 */
	public void setApiInvokeHints(ApiInvokeHintsEnum apiInvokeHints) {
		this.apiInvokeHints = apiInvokeHints;
	}
	
	/**
	 * @return isAuthenticationRequired
	 */
	public boolean isAuthenticationRequired() {
		return isAuthenticationRequired;
	}

	/**
	 * @param isAuthenticationRequired the isAuthenticationRequired to set
	 */
	public void setAuthenticationRequired(boolean isAuthenticationRequired) {
		this.isAuthenticationRequired = isAuthenticationRequired;
	}

    /**
     * The command class
     *
     */
	public static class Command implements Serializable{
		/**
		 * Generated Serial Version
		 */
		private static final long serialVersionUID = -849969894219474528L;
		private String commandString;
		private boolean hasParams;
		private List<String> params;
		
		/**
		 * @return the commandString
		 */
		public String getCommandString() {
			return commandString;
		}
		
		/**
		 * @param commandString the commandString to set
		 */
		public void setCommandString(String commandString) {
			this.commandString = commandString;
		}
		
		/**
		 * @return the hasParams
		 */
		public boolean isHasParams() {
			return hasParams;
		}
		
		/**
		 * @param hasParams the hasParams to set
		 */
		public void setHasParams(boolean hasParams) {
			this.hasParams = hasParams;
		}		
		
		/**
		 * @return the params
		 */
		public List<String> getParams() {
			return params;
		}
		
		/**
		 * @param params the params to set
		 */
		public void setParams(List<String> params) {
			this.params = params;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Command [commandString=" + commandString + ", hasParams=" + hasParams + ", params=" + params + "]";
		}
		
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CommandWritable [username=" + username + ", password=" + password + ", hasRSA=" + hasRSA + ", rsaFilePath=" + rsaFilePath
				+ ", hasDSA=" + hasDSA + ", dsaFilePath=" + dsaFilePath + ", isCommandForMaster=" + isCommandForMaster + ", masterHostname="
				+ masterHostname + ", slaveHostname=" + slaveHostname + ", batchedCommands=" + batchedCommands + ", apiInvokeHints="
				+ apiInvokeHints + ", isAuthenticationRequired="+isAuthenticationRequired +"]";
	}
		
}