package org.jumbune.common.utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.HadoopUsers;
import org.jumbune.common.beans.cluster.NameNodes;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemoterUtility;
import org.jumbune.remoting.common.command.CommandWritable;


/**
 * Builder class for Command Writable
 *
 */
public class CommandWritableBuilder {

	private CommandWritable commandWritable;
	//Stateful instance
	private List<CommandWritable.Command> commandBatch;
	
	private static final Logger LOGGER=LogManager.getLogger(CommandWritableBuilder.class);
	
	private Cluster cluster;
	
	private static final String MD5 = "MD5";

	/**
	 * Constructor to set the command writable
	 *
	 * This should be called when command requires a public-key authentication.
	 * It takes care of populating RSA/DSA file path, master/slaves host names
	 * and username in the CommandWritable instance
	 * 
	 * @param config
	 * @param slaveHost
	 * @return
	 */
	public CommandWritableBuilder(Cluster cluster, String workerHost) {
		this.cluster = cluster; 
		
		setCommandWritable(new CommandWritable());
		getCommandWritable().setAuthenticationRequired(true);	
		boolean isCommandForMaster = false;
		
		String sshAuthKeysFile = cluster.getAgents().getSshAuthKeysFile();
		if (RemoterUtility.isNotEmpty(sshAuthKeysFile)) {
			getCommandWritable().setHasSshAuthKeysFile(true);
			getCommandWritable().setSshAuthKeysFile(sshAuthKeysFile);
		} else {
			String password = cluster.getAgents().getPassword();
			if (RemoterUtility.isNotEmpty(password)){
				getCommandWritable().setPassword(password);
			}else{
				throw new IllegalArgumentException("No ssh Authentication Keys file path or Password found in json configuration");
			}
		}
		if(workerHost == null){
			isCommandForMaster = true;
		}
		if (isCommandForMaster) {
			getCommandWritable().setCommandForMaster(true);
			getCommandWritable().setNameNodeHost(cluster.getNameNode());
			getCommandWritable().setUsername(cluster.getHadoopUsers().getFsUser());
		} else {
			getCommandWritable().setCommandForMaster(false);
			getCommandWritable().setWorkerHost(workerHost);
			getCommandWritable().setUsername(cluster.getWorkers().getUser());
		}
	}
	
   
	public CommandWritableBuilder(Cluster cluster) {
     this(cluster, null);		
	}

	/**
	 * This should be called when command requires a public-key authentication.
	 * It takes care of populating RSA/DSA file path, master/slaves host names
	 * and username in the CommandWritable instance
	 * 
	 * @param config
	 * @param slaveHost
	 * @return
	 */
	public CommandWritableBuilder populate(Cluster cluster, String workerHost) {
		NameNodes nameNodes = cluster.getNameNodes();
		getCommandWritable().setAuthenticationRequired(true);
		boolean isCommandForMaster = false;
		
		String sshAuthKeysFile = cluster.getAgents().getSshAuthKeysFile();
		if (RemoterUtility.isNotEmpty(sshAuthKeysFile)) {
			getCommandWritable().setHasSshAuthKeysFile(true);
			getCommandWritable().setSshAuthKeysFile(sshAuthKeysFile);
		} else {
			String password = cluster.getAgents().getPassword();
			if (RemoterUtility.isNotEmpty(password)){
				getCommandWritable().setPassword(password);
			}else{
				throw new IllegalArgumentException("No ssh Authentication Keys file path or Password found in json configuration");
			}
		}
		if(workerHost == null){
			isCommandForMaster = true;
		}
		if (isCommandForMaster) {
			getCommandWritable().setCommandForMaster(true);
			getCommandWritable().setNameNodeHost(cluster.getNameNode());
			getCommandWritable().setUsername(cluster.getHadoopUsers().getFsUser());
		} else {
			getCommandWritable().setCommandForMaster(false);
			getCommandWritable().setWorkerHost(workerHost);
			getCommandWritable().setUsername(cluster.getWorkers().getUser());
		}
		return this;
	}

	/**
	 * Facilatates to override username, typically called to override username
	 * set by populate() method
	 * 
	 * @param username
	 * @return
	 */
	public CommandWritableBuilder setUser(String username) {
		getCommandWritable().setUsername(username);
		return this;
	}
	
	/**
	 * adds a new command to be executed over Remoting
	 * the command may optionally have params too
	 * @param commandStr, the command String
	 * @param hasParams, whether the command has parameters
	 * @param params, command parameters
	 * @param commandType, whether the command is an hadoop fs command, an execution command or fs command
	 * @return
	 */
	public CommandWritableBuilder addCommand(String commandStr, boolean hasParams, List<String> params, CommandType commandType) {
		if (getCommandBatch() == null) {
			setCommandBatch(new ArrayList<CommandWritable.Command>());
		}
		CommandWritable.Command cmd = new CommandWritable.Command();
		cmd.setCommandString(commandStr);
		cmd.setHasParams(hasParams);
		if (hasParams) {
			cmd.setParams(params);
		}
		CommandWritable.Command.SwitchedIdentity switchedIdentity = changeIdentityAsPerCommand(commandType,null);
		cmd.setSwitchedIdentity(switchedIdentity);
		cmd.setCommandType(commandType);
        setCommandId(cmd, commandStr);
		getCommandBatch().add(cmd);
		getCommandWritable().setBatchedCommands(getCommandBatch());
		return this;
	}	
	
	private void setCommandId(CommandWritable.Command cmd, String commandString) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(MD5);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("can not instantiate MessageDigest", e);
		}
		digest.update(commandString.getBytes());
		cmd.setCommandId(new BigInteger(digest.digest()).toString(16));
	}	
	
	/**
	 * Adds a new command to be executed over Remoting.
	 *
	 * @param commandStr the command string that has to be executed
	 * @param hasParams, whether the command has parameters
	 * @param params, command parameters
	 * @param commandType, whether the command is an hadoop fs command, an execution command or fs command
	 * @param operatingUser the operating user is the job submission user
	 * @return the command writable builder
	 */
	public CommandWritableBuilder addCommand(String commandStr, boolean hasParams, List<String> params, CommandType commandType, String operatingUser) {
		if (getCommandBatch() == null) {
			setCommandBatch(new ArrayList<CommandWritable.Command>());
		}
		CommandWritable.Command cmd = new CommandWritable.Command();
		cmd.setCommandString(commandStr);
		cmd.setHasParams(hasParams);
		if (hasParams) {
			cmd.setParams(params);
		}
		CommandWritable.Command.SwitchedIdentity switchedIdentity = changeIdentityAsPerCommand(commandType,operatingUser);
		cmd.setSwitchedIdentity(switchedIdentity);
		cmd.setCommandType(commandType);
		setCommandId(cmd, commandStr);
		getCommandBatch().add(cmd);
		getCommandWritable().setBatchedCommands(getCommandBatch());
		return this;
	}	
	
	/**
	 * adds a new command to be executed over remoting
	 * the command may optionally have params too
	 * @param commandStr, the command String
	 * @param hasParams, whether the command has parameters
	 * @param params, command parameters
	 * @return
	 */
	@Deprecated
	public CommandWritableBuilder addCommand(String commandStr, boolean hasParams, List<String> params) {
		if (getCommandBatch() == null) {
			setCommandBatch(new ArrayList<CommandWritable.Command>());
		}
		CommandWritable.Command cmd = new CommandWritable.Command();
		cmd.setCommandString(commandStr);
		cmd.setHasParams(hasParams);
		if (hasParams) {
			cmd.setParams(params);
		}
		setCommandId(cmd, commandStr);
		getCommandBatch().add(cmd);
		getCommandWritable().setBatchedCommands(getCommandBatch());
		return this;
	}

	private CommandWritable.Command.SwitchedIdentity changeIdentityAsPerCommand(CommandType commandType, String operatingUser){
		HadoopUsers users = getHadoopUsers();
		CommandWritable.Command.SwitchedIdentity switchedIdentity = new CommandWritable.Command.SwitchedIdentity();
		switchedIdentity.setUser(users.getFsUser());
		String privateKeyFilePath = users.getFsPrivateKeyPath();
		if(privateKeyFilePath != null && !privateKeyFilePath.isEmpty()){
		switchedIdentity.setPrivatePath(privateKeyFilePath);
		}else{
			setPasswd(switchedIdentity, users.getFsUserPassword());
		}
		if (users.isHasSingleUser()) {
			if (commandType.equals(CommandType.USER)) {
				switchedIdentity.setWorkingUser(operatingUser);
			} else {
				switchedIdentity.setWorkingUser(users.getFsUser());
			}
		}else{
		if(commandType.equals(CommandType.HADOOP_FS)){
            switchedIdentity.setWorkingUser(users.getHdfsUser());;
		}else if(commandType.equals(CommandType.HADOOP_JOB)){
            switchedIdentity.setWorkingUser(users.getYarnUser());
		}else if(commandType.equals(CommandType.MAPRED)){
			switchedIdentity.setWorkingUser(users.getMapredUser());
		}else if(commandType.equals(CommandType.USER)){
            switchedIdentity.setWorkingUser(operatingUser);
		}else if(commandType.equals(CommandType.FS)){
    		switchedIdentity.setWorkingUser(users.getFsUser());
		}}
		return switchedIdentity;
	}

	private HadoopUsers getHadoopUsers() {
		return cluster.getHadoopUsers();
	}

	private static void setPasswd(CommandWritable.Command.SwitchedIdentity switchedIdentity, String passwd){
		if(passwd!= null && !"".equals(passwd)){
				switchedIdentity.setPasswd(passwd);
		}		
	}
	
   /**
	 * sets the password
	 * @param password
	 * @return
	 */
	public CommandWritableBuilder setPassword(String password) {
		getCommandWritable().setPassword(password);
		return this;
	}

	/**
	 * sets commandWritable
	 * @param commandWritable
	 */
	public final void setCommandWritable(CommandWritable commandWritable) {
		this.commandWritable = commandWritable;
	}

	/**
	 * get the commandWritable
	 * @return
	 */
	public CommandWritable getCommandWritable() {
		return commandWritable;
	}
	
	/**
	 * set isCommandForMaster
	 * @param isCommandForMaster
	 * @return
	 */
	public CommandWritableBuilder setCommandForMaster(boolean isCommandForMaster){
		getCommandWritable().setCommandForMaster(isCommandForMaster);
		return this;
	}

	/**
	 * set commandBatch
	 * @param commandBatch
	 */
	public void setCommandBatch(List<CommandWritable.Command> commandBatch) {
		this.commandBatch = commandBatch;
	}

	/**
	 * get the commandBatch
	 * @return
	 */
	public List<CommandWritable.Command> getCommandBatch() {
		return commandBatch;
	}
	
	
	/**
	 * sets the methodName
	 * @param methodName
	 * @return
	 */
	public CommandWritableBuilder setMethodToBeInvoked(String methodToBeInvoked) {
		getCommandWritable().setMethodToBeInvoked(methodToBeInvoked);
		return this;
	}
	
	public void clear(){
		getCommandBatch().clear();
		commandWritable.setMethodToBeInvoked(null);
	}

}