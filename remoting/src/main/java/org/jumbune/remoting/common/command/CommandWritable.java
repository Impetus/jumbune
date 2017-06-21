package org.jumbune.remoting.common.command;

import java.io.Serializable;
import java.util.List;

import org.jumbune.remoting.common.CommandType;


/**
 * Class Command Writable used for typed remoting.
 */
public class CommandWritable implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The username. */
	private String username;

	/** The password. */
	private String password;

	/** indicates if rsa file is being used. */
	private boolean hasSshAuthKeysFile;

	/** The ssh Auth Keys file. */
	private String sshAuthKeysFile;
	
	/** indicates if given command is to be run on master node. */
	private boolean isCommandForMaster;

	/** the name node host */
	private String nameNodeHost;

	/** the worker host */
	private String workerHost;
	/**
	 * the list of commands that needs to be processed at the remoting end.
	 */
	private List<Command> batchedCommands;

	/**
	 * This should be called when command requires a public-key authentication.
	 */
	private boolean isAuthenticationRequired;

	/** The method name which is to be invoked by agent. */
	private String methodToBeInvoked;
		
	/**
	 * Gets the method to be invoked.
	 *
	 * @return the method to be invoked
	 */
	public String getMethodToBeInvoked() {
		return methodToBeInvoked;
	}

	/**
	 * Sets the method to be invoked.
	 *
	 * @param methodToBeInvoked the new method to be invoked
	 */
	public void setMethodToBeInvoked(String methodToBeInvoked) {
		this.methodToBeInvoked = methodToBeInvoked;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isHasSshAuthKeysFile() {
		return hasSshAuthKeysFile;
	}

	public void setHasSshAuthKeysFile(boolean hasSshAuthKeysFile) {
		this.hasSshAuthKeysFile = hasSshAuthKeysFile;
	}

	public String getSshAuthKeysFile() {
		return sshAuthKeysFile;
	}

	public void setSshAuthKeysFile(String sshAuthKeysFile) {
		this.sshAuthKeysFile = sshAuthKeysFile;
	}

	/**
	 * Checks if is command for master.
	 *
	 * @return the isCommandForMaster
	 */
	public boolean isCommandForMaster() {
		return isCommandForMaster;
	}

	/**
	 * Sets the command for master.
	 *
	 * @param isCommandForMaster            the isCommandForMaster to set
	 */
	public void setCommandForMaster(boolean isCommandForMaster) {
		this.isCommandForMaster = isCommandForMaster;
	}

	/**
	 * @return the nameNodeHost
	 */
	public String getNameNodeHost() {
		return nameNodeHost;
	}

	/**
	 * @param nameNodeHost the nameNodeHost to set
	 */
	public void setNameNodeHost(String nameNodeHost) {
		this.nameNodeHost = nameNodeHost;
	}

	/**
	 * @return the workerHost
	 */
	public String getWorkerHost() {
		return workerHost;
	}

	/**
	 * @param workerHost the workerHost to set
	 */
	public void setWorkerHost(String workerHost) {
		this.workerHost = workerHost;
	}

	/**
	 * Gets the batched commands.
	 *
	 * @return the batchedCommands
	 */
	public List<Command> getBatchedCommands() {
		return batchedCommands;
	}

	/**
	 * Sets the batched commands.
	 *
	 * @param batchedCommands            the batchedCommands to set
	 */
	public void setBatchedCommands(List<Command> batchedCommands) {
		this.batchedCommands = batchedCommands;
	}

	/**
	 * Checks if is authentication required.
	 *
	 * @return isAuthenticationRequired
	 */
	public boolean isAuthenticationRequired() {
		return isAuthenticationRequired;
	}

	/**
	 * Sets the authentication required.
	 *
	 * @param isAuthenticationRequired            the isAuthenticationRequired to set
	 */
	public void setAuthenticationRequired(boolean isAuthenticationRequired) {
		this.isAuthenticationRequired = isAuthenticationRequired;
	}





	/**
	 * The command class.
	 */
	public static class Command implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** The command string. */
		private String commandString;
		
		/** The has params. */
		private boolean hasParams;
		
		/** The params. */
		private List<String> params;
		
		/**
		 * Identity of the user which helps the appropriate JSCH session to be created
		 */
		private SwitchedIdentity switchedIdentity;

		/** Can be either a storage user or an execution user. */
		private CommandType commandType;		

		private String commandId;
		
		public String getCommandId() {
			return commandId;
		}

		public void setCommandId(String commandId) {
			this.commandId = commandId;
		}

		/**
		 * Gets the command string.
		 *
		 * @return the commandString
		 */
		public String getCommandString() {
			return commandString;
		}

		/**
		 * Sets the command string.
		 *
		 * @param commandString            the commandString to set
		 */
		public void setCommandString(String commandString) {
			this.commandString = commandString;
		}

		/**
		 * Checks if is checks for params.
		 *
		 * @return the hasParams
		 */
		public boolean isHasParams() {
			return hasParams;
		}

		/**
		 * Sets the checks for params.
		 *
		 * @param hasParams            the hasParams to set
		 */
		public void setHasParams(boolean hasParams) {
			this.hasParams = hasParams;
		}

		/**
		 * Gets the params.
		 *
		 * @return the params
		 */
		public List<String> getParams() {
			return params;
		}

		/**
		 * Sets the params.
		 *
		 * @param params            the params to set
		 */
		public void setParams(List<String> params) {
			if(params.size()>0){
				this.hasParams = true;
			}
			this.params = params;
		}
		
		/**
		 * getting the command type, please refer {@linkCommandType}.
		 *
		 * @return the command type
		 */
		public CommandType getCommandType() {
			return commandType;
		}

		/**
		 * Setting the command type, please refer {@linkCommandType}.
		 *
		 * @param commandType the new command type
		 */
		public void setCommandType(CommandType commandType) {
			this.commandType = commandType;
		}	
		
		public SwitchedIdentity getSwitchedIdentity() {
			return switchedIdentity;
		}

		public void setSwitchedIdentity(SwitchedIdentity switchedIdentity) {
			this.switchedIdentity = switchedIdentity;
		}
		
		
		public static class SwitchedIdentity implements Serializable{

			private String user;
			
			private String passwd;
			
			private String privatePath;
			
			private String workingUser;

			public String getUser() {
				return user;
			}

			public void setUser(String user) {
				this.user = user;
			}

			public String getPasswd() {
				return passwd;
			}

			public void setPasswd(String passwd) {
				this.passwd = passwd;
			}

			public String getPrivatePath() {
				return privatePath;
			}

			public void setPrivatePath(String privatePath) {
				this.privatePath = privatePath;
			}
			
			
			public String getWorkingUser() {
				return workingUser;
			}

			public void setWorkingUser(String workingUser) {
				this.workingUser = workingUser;
			}

			@Override
			public String toString() {
				return "SwitchedIdentity [user=" + user + ( (passwd != null)? ", password=*****" : ", hasPassword=false") 
						+ ", privatePath=" + privatePath
						+ ", workingUser=" + workingUser + "]";
			}
			
			

		}
			
			
		@Override
		public String toString() {
			return "Command [commandString=" + commandString + ", hasParams="
					+ hasParams + ", params=" + params + ", switchedIdentity="
					+ switchedIdentity + ", commandType=" + commandType + "]";
		}	


	}

	@Override
	public String toString() {
		return "CommandWritable [username=" + username 
				+ ( (password != null)? ", hasPassword=true" : ", hasPassword=false")
				+ ", hasSshAuthKeysFile=" + hasSshAuthKeysFile
				+ ", sshAuthKeysFile=" + sshAuthKeysFile
				+ ", isCommandForMaster=" + isCommandForMaster
				+ ", nameNodeHost=" + nameNodeHost + ", workerHost="
				+ workerHost + ", batchedCommands=" + batchedCommands
				+ ", isAuthenticationRequired=" + isAuthenticationRequired
				+ ", methodToBeInvoked=" + methodToBeInvoked 
				+ ", methodToBeInvoked=" + methodToBeInvoked + "]";
	}

	

}