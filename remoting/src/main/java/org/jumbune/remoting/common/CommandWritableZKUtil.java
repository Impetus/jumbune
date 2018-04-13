package org.jumbune.remoting.common;

public class CommandWritableZKUtil {
	
	/**
	 * Builds the command writable from leader agent.
	 *
	 * @param command the command
	 * @return the command writable
	 * @throws ConnectException the connect exception
	 */
	/*public static CommandWritable buildCommandWritableFromLeaderAgent(String command) throws ConnectException{
		List<CommandWritable.Command> batchedCommands = new ArrayList<CommandWritable.Command>();
		CommandWritable commandWritable = new CommandWritable();
		//GET AGENTS from cluster object
		AgentNode agents = null;
		AgentNode agent = ZKUtils.getLeaderAgentfromZK(null);
		CommandWritable.Command cmd = new CommandWritable.Command();
		cmd.setCommandString(command);
		cmd.setHasParams(false);
		cmd.setCommandType(CommandType.FS);
		batchedCommands.add(cmd);
		commandWritable.setBatchedCommands(batchedCommands);
		
		commandWritable.setAuthenticationRequired(true);
		commandWritable.setHasSshAuthKeysFile(true);
		commandWritable.setSshAuthKeysFile(agents.getPrivateKey());
		
		commandWritable.setCommandForMaster(true);
		commandWritable.setNameNodeHost(agent.getHost());
		commandWritable.setUsername(agents.getAgentUser());
		
		return commandWritable;
	}*/
	

}
