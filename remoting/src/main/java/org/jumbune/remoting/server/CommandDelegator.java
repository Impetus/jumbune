package org.jumbune.remoting.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodInvocationUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.server.invocations.CommandDelegatorMethods;
import org.jumbune.remoting.server.jsch.JschExecutor;

/**
 * The Class CommandDelegator.
 */
public class CommandDelegator extends AbstractCommandHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandDelegator.class);
	
	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			CommandWritable cmdWritable) throws Exception {
		try {
			if (cmdWritable != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(cmdWritable);
				}
				performAction(cmdWritable);
			}
		} catch (IOException exception) {
			LOGGER.error(cmdWritable + " having some problem", exception);
		}
		LOGGER.debug("Now sending [Ack] back from Delegator");
		ctx.channel().writeAndFlush(RemotingConstants.ACK);
	}

	/**
	 * Perform action.
	 * 
	 * @param command
	 *            the command
	 * @throws IOException
	 */
	public void performAction(final CommandWritable commandWritable)
			throws IOException {
		List<Command> commandList = commandWritable.getBatchedCommands();
		for (Command command : commandList) {
			replaceSymbolsWithConfigurationVariable(command);
			invokeExecutor(commandWritable, command);				
		}
	}
		
	private void invokeExecutor(CommandWritable commandWritable, Command command) throws SocketException, IOException{
		// Reflective Methods
		if (commandWritable.getMethodToBeInvoked() != null) {		
			try {
				RemotingMethodInvocationUtil.invokeMethodFromRemotingMethodConstants(commandWritable, command, CommandDelegatorMethods.class);
			}catch(ReflectiveOperationException e){
				LOGGER.error("Unable to invoke method - "+ commandWritable.getMethodToBeInvoked(), e.getCause());	
			}
		} 	// Local runtime invocation
		   else if(getCurrentMachineEndpoint().equals(getNameNodeHost()) && commandWritable.isCommandForMaster()) {
			try {					 
					executeCommandWithRuntime(commandWritable, command);						
				} catch (InterruptedException e) {
					LOGGER.error("Exception occured while executing the process (launched by runtime) to finish.");;
				}
		} else if (command.getCommandString().contains("top.txt")) {
			JschExecutor executor = new JschExecutor();
			executor.executeShellJsch(commandWritable, command);
		}
		//  remote JSch invocation
		   else {
			JschExecutor executor = new JschExecutor();
			executor.executeShellJsch(commandWritable, command);
		}
	}

	/**
	 * Command expected to be invoked from sudo agent runtime.
	 * Typically command's with user, fs, mapred, yarn and hdfs user switch can be invoked. 
	 * This method should be invoked only when Active NN & Active Jumbune Agent are on the same host
	 * @param commandWritable 
	 * @param command
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void executeCommandWithRuntime(CommandWritable commandWritable, Command command) throws IOException, InterruptedException{
		LOGGER.debug("Namenode and Agent are on same node, and command is for master, hence executing command with Runtime");
		String commandString = command.getCommandString();
		Process process = executeCommandWithRuntime0(command);
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			LOGGER.error("Exception occured while waiting for the process (launched by runtime) to finish.");
		}
		LOGGER.info("Runtime - Executed command [" + commandString +"], exit code [" + process.exitValue()+"]");
	}


}