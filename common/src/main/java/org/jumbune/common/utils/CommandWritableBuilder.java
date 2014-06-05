package org.jumbune.common.utils;


import java.util.ArrayList;
import java.util.List;

import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.RemoterUtility;
import org.jumbune.remoting.writable.CommandWritable;


/**
 * Builder class for Command Writable
 *
 */
public class CommandWritableBuilder {

	private CommandWritable commandWritable;
	//Stateful instance
	private List<CommandWritable.Command> commandBatch;

	/**
	 * Constructor to set the command writable
	 */
	public CommandWritableBuilder() {
		setCommandWritable(new CommandWritable());
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
	public CommandWritableBuilder populate(YamlConfig config, String slaveHost) {
		Master master = config.getMaster();
		getCommandWritable().setAuthenticationRequired(true);
		boolean isCommandForMaster = false;
		
		if (RemoterUtility.isNotEmpty(master.getDsaFile())) {
			getCommandWritable().setHasDSA(true);
			getCommandWritable().setDsaFilePath(master.getDsaFile());
		} else if (RemoterUtility.isNotEmpty(master.getRsaFile())) {
			getCommandWritable().setHasRSA(true);
			getCommandWritable().setRsaFilePath(master.getRsaFile());
		} else {
			throw new IllegalArgumentException("No DSA/RSA file path found in yaml configuration");
		}
		if(slaveHost== null){
			isCommandForMaster = true;
		}
		if (isCommandForMaster) {
			getCommandWritable().setCommandForMaster(true);
			getCommandWritable().setMasterHostname(master.getHost());
			getCommandWritable().setUsername(master.getUser());
		} else {
			getCommandWritable().setCommandForMaster(false);
			Slave slave = config.getSlaves().get(0);
			getCommandWritable().setSlaveHost(slaveHost);
			getCommandWritable().setUsername(slave.getUser());		
		}
		return this;
	}
	
	/**
	 * This should be called when command requires a public-key authentication.
	 * It takes care of populating RSA/DSA file path, master/slaves host names
	 * and username in the CommandWritable instance using LogConsolidationInfo
	 * 
	 * @param info
	 * @param slaveHost
	 * @return
	 */
	public CommandWritableBuilder populateFromLogConsolidationInfo(LogConsolidationInfo info, String slaveHost){
		Master master = info.getMaster();
		getCommandWritable().setAuthenticationRequired(true);
		boolean isCommandForMaster = false;
		
		if (RemoterUtility.isNotEmpty(master.getDsaFile())) {
			getCommandWritable().setHasDSA(true);
			getCommandWritable().setDsaFilePath(master.getDsaFile());
		} else if (RemoterUtility.isNotEmpty(master.getRsaFile())) {
			getCommandWritable().setHasRSA(true);
			getCommandWritable().setRsaFilePath(master.getRsaFile());
		} else {
			throw new IllegalArgumentException("No DSA/RSA file path found in yaml configuration");
		}
		if(slaveHost== null){
			isCommandForMaster = true;
		}
		if (isCommandForMaster) {
			getCommandWritable().setCommandForMaster(true);
			getCommandWritable().setMasterHostname(master.getHost());
			getCommandWritable().setUsername(master.getUser());
		} else {
			getCommandWritable().setCommandForMaster(false);
			Slave slave = info.getSlaves().get(0);
			getCommandWritable().setSlaveHost(slaveHost);
			getCommandWritable().setUsername(slave.getUser());
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
	 * sets the api invoke hint 
	 * @param apiInvokeHints
	 * @return
	 */
	public CommandWritableBuilder setApiInvokeHints(ApiInvokeHintsEnum apiInvokeHints) {
		getCommandWritable().setApiInvokeHints(apiInvokeHints);
		return this;
	}

	/**
	 * adds a new command to be executed over remoting
	 * the command may optionally have params too
	 * @param commandStr
	 * @param hasParams
	 * @param params
	 * @return
	 */
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
		getCommandBatch().add(cmd);
		getCommandWritable().setBatchedCommands(getCommandBatch());
		return this;
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
	


}
