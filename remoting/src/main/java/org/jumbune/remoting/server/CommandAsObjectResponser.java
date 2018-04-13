package org.jumbune.remoting.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodInvocationUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.server.invocations.CommandAsObjectResponserMethods;
import org.jumbune.remoting.server.jsch.ChannelReaderResponse;
import org.jumbune.remoting.server.jsch.JschExecutor;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * The Class CommandAsObjectResponser.
 */
public class CommandAsObjectResponser extends AbstractCommandHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandAsObjectResponser.class);
	
	/** The Constant SINGLE_QUOTE. */
	private static final String SINGLE_QUOTE = "'";
	
	/** The Constant LOGOUT. */
	private static final String LOGOUT = "logout";	
	
	/** The Constant CPU_USAGE_COMMAND. */
	private static final String CPU_USAGE_COMMAND = "top -d 0.8 -b -n 2 |grep ^Cpu && exit";

	/** The Constant CPU_USAGE_COMMAND_WITHOUT_CARET. */
	private static final String CPU_USAGE_COMMAND_WITHOUT_CARET = "top -d 0.8 -b -n 2 |grep Cpu && exit";

	/** The Constant CPU_DETAILS_COMMAND. */
	private static final String CPU_DETAILS_COMMAND = "cat /proc/cpuinfo && exit";
	
	/** The Constant VMSTAT_COMMAND. */
	private static final String VMSTAT_COMMAND = "vmstat -s && exit";
	
	/** The Constant THP_COMMAND. */
	private static final String THP_COMMAND = "/sys/kernel/mm/transparent_hugepage/enabled";
	
	/** The Constant VMSWAPINESS_COMMAND. */
	private static final String VMSWAPINESS_COMMAND = "/etc/sysctl.conf";
	
	/** The Constant SELINUX_COMMAND. */
	private static final String SELINUX_COMMAND = "/etc/selinux/config";

	private static final CharSequence FOR_FILE_IN_$_LS_D_1 = "for FILE in $(shopt -s nocaseglob;ls -1";
	
	/* (non-Javadoc)
	 * @see org.jumbune.remoting.server.AbstractCommandHandler#channelRead0(io.netty.channel.ChannelHandlerContext, org.jumbune.remoting.common.command.CommandWritable;)
	 */
	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			CommandWritable msg) throws Exception {
		if (msg != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg);
			}
			ctx.channel().writeAndFlush(performAction(msg));
		}
	}

	/**
	 * Perform action.
	 *
	 * @param commandWritable the command writable
	 * @return the object
	 * @throws JSchException             the JSch exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 */
	private Object performAction(final CommandWritable commandWritable) throws JSchException, IOException {
		List<Command> commandList = commandWritable.getBatchedCommands();
		for(Command currentCommand : commandList){
			String commandString = currentCommand.getCommandString();
			//Echo Handling
			String[] commandStringSplits = commandString.split("\\s+");
			if(commandStringSplits[0].startsWith("echo")){
				return handleResponsiveEcho(commandWritable, currentCommand, commandStringSplits[1]);
			}		
			
			replaceSymbolsWithConfigurationVariable(currentCommand);
			// Command string containing 'free -m' with Jsch ShellChannel
			if(commandString.contains("free -m")){
				return handleResponsiveFree(commandWritable, currentCommand);
			}
			// Command string starting with awk '/container-id with Jsch ShellChannel
			if(commandString.contains(FOR_FILE_IN_$_LS_D_1)){
				return handleResponsiveAwkContainer(commandWritable, currentCommand);
			}
			if(commandString.contains(CPU_USAGE_COMMAND) || commandString.contains(CPU_USAGE_COMMAND_WITHOUT_CARET) || commandString.equalsIgnoreCase("lscpu |grep Core && exit")
				|| commandString.equalsIgnoreCase("lscpu |grep Thread && exit") || commandString.equalsIgnoreCase("lscpu |grep Socket && exit") 
				||commandString.contains(THP_COMMAND) || commandString.contains(VMSWAPINESS_COMMAND) || commandString.contains(SELINUX_COMMAND)) {
				return handleCpuUsageCommand(commandWritable, currentCommand);
			} 
			if(commandString.contains(CPU_DETAILS_COMMAND) || commandString.contains(VMSTAT_COMMAND)) {
				return executeWithShell(commandWritable, currentCommand);
			}
			//Execute Debugger and User Jobs 
			if(currentCommand.isHasParams() && currentCommand.getCommandString().contains("instrument")){
				String[] commands = currentCommand.getCommandString().split(RemotingConstants.REGEX_FOR_PIPE_DELIMITED);
				checkexpectedTwoParams(commands);
				String[] params = new String[currentCommand.getParams().size()];
				currentCommand.getParams().toArray(params);
				return appendAbsolutePathAndExecuteJob(commandWritable, currentCommand, params, commands[0]);	
			}
			
			//Execute Data Validation and User Jobs 
			if(currentCommand.isHasParams()  && !currentCommand.getCommandString().contains("instrument")){
				String[] commands = currentCommand.getCommandString().split(RemotingConstants.REGEX_FOR_PIPE_DELIMITED);
				checkexpectedTwoParams(commands);
				String[] params = new String[currentCommand.getParams().size()];
				currentCommand.getParams().toArray(params);
				return executeJob(commandWritable, currentCommand, params, commands[0]);
			
			}
			//execute remaining unfiltered commands
			return invokeExecutor(commandWritable, currentCommand);
		}
		LOGGER.debug("returning back default");
		return commandWritable;
	}

	/**
	 * Execute with shell.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object executeWithShell(CommandWritable commandWritable, Command command) throws IOException {
		JschExecutor jschExecutor = new JschExecutor();
		Session session = jschExecutor.getSession(commandWritable, command);
		ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveShellJsch(session, command);	
		String response = getStringFromReader((BufferedReader)channelReaderResponse.getReader());
		channelReaderResponse.getReader().close();
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		LOGGER.info("Jsch Shell - Command [" +command.getCommandString()+"] returned with exit code " + channelReaderResponse.getExitCode());
		if(channelReaderResponse.getExitCode()!=0 && !command.getCommandString().endsWith("&& exit")){
			LOGGER.debug("Jsch Shell - Command [" +command.getCommandString()+"] exited with unexpected code, returned response "+response);
		}		
		return response;
	}

	/**
	 * Handle cpu usage command.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object handleCpuUsageCommand(CommandWritable commandWritable, Command command) throws IOException {
		JschExecutor jschExecutor = new JschExecutor();
		Session session = jschExecutor.getSession(commandWritable, command);
		ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveShellJsch(session, command);
		String lineBeforeLogout = "";
		try (BufferedReader br = (BufferedReader)channelReaderResponse.getReader()) {
			for (String line = br.readLine(); br != null && line != null; line = br.readLine()) {
				if(!line.equals(LOGOUT)) {
					lineBeforeLogout = line;
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}finally{
		channelReaderResponse.getReader().close();
		}
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		return lineBeforeLogout;
	}

	/**
	 * Append absolute path and execute job.
	 *
	 * @param commandWritable the command writable
	 * @param currentCommand the current command
	 * @param commandWithParams the command with params
	 * @param jarLocation the jar location
	 * @return the object
	 * @throws JSchException the jsch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object appendAbsolutePathAndExecuteJob(CommandWritable commandWritable,
			Command currentCommand, String[] commandWithParams, String jarLocation) throws JSchException, IOException {
		String agentHome = JumbuneAgent.getAgentDirPath();
		String jobJarAbsolutePath = agentHome + jarLocation;
		File fileLoc = new File(jobJarAbsolutePath);						
		String[] fileList = fileLoc.list();
		String jarName = null;
		for (String filename : fileList) {
			if(filename.contains(".jar")){
				jarName = filename;
				break;
			}
		}
		commandWithParams[2] = jobJarAbsolutePath + "/" + jarName;
		return executeJob(commandWritable, currentCommand, commandWithParams, jarLocation);
	}

	/**
	 * Execute job.
	 *
	 * @param commandWritable the command writable
	 * @param currentCommand the current command
	 * @param commandWithParams the command with params
	 * @param jarLocation the jar location
	 * @return the object
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Object executeJob(CommandWritable commandWritable,
			Command currentCommand, String[] commandWithParams, String jarLocation) throws JSchException, IOException {
		StringBuilder compositeCommandString = new StringBuilder();
		for(String str: commandWithParams){
			if(str.trim().startsWith("-D")){
				compositeCommandString.append(str).append(RemotingConstants.SINGLE_SPACE);
			}else{
				compositeCommandString.append(SINGLE_QUOTE).append(str).append(SINGLE_QUOTE).append(RemotingConstants.SINGLE_SPACE);
			}
		}
		currentCommand.setCommandString(compositeCommandString.toString());
		String response = (String) invokeExecutor(commandWritable, currentCommand);
		return response;
	}
		

	protected String handleResponsiveGrep(){
		return null;
	}

	protected String handleResponsiveTop(){
		return null;
	}
	
	protected String handleResponsiveValidationJar(){
		return null;		
	}
	
	/**
	 * Handle responsive echo.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @param commandSplitString the command split string
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String handleResponsiveEcho(CommandWritable commandWritable, Command command, String commandSplitString) throws IOException{
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
		    if(commandSplitString == null){
				LOGGER.warn("Handled an unexpected echo command, received null after echo.");
				return null;
		    }
			switch (commandSplitString){
			case "$HADOOP_HOME":
				LOGGER.debug("Sending back short circuit response for echo $HADOOP_HOME AS ["+hcpl.getHadoopHome()+"]");
				return hcpl.getHadoopHome();
			case "$AGENT_HOME":
				String agentHomeDir = JumbuneAgent.getAgentDirPath();
				agentHomeDir = agentHomeDir.endsWith(File.separator) ? agentHomeDir:agentHomeDir+File.separator;
				LOGGER.debug("Sending back short circuit response for echo $AGENT_HOME AS ["+agentHomeDir+"]");
				return agentHomeDir;
			case "$HADOOP_CONF_DIR":
				String lineBreaker = "echo $HADOOP_CONF_DIR";
				JschExecutor jschExecutor = new JschExecutor();
				Session session = jschExecutor.getSession(commandWritable, command);
				ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveShellJsch(session, command);
				BufferedReader reader = (BufferedReader)channelReaderResponse.getReader();
				String response;
				StringBuilder stringBuilder;
				try{
					stringBuilder = new StringBuilder();
					String line = "";
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line + System.lineSeparator());
						if (line.contains(lineBreaker)) {
							stringBuilder = new StringBuilder(line);
							break;
						}
					}
				}finally{
					if(reader!=null){
						reader.close();
					}
				}
				response = stringBuilder.toString();				
				jschExecutor.closeChannel(channelReaderResponse.getChannel());
				jschExecutor.closeSession(session);
				return response;
			case "$SPARK_CONF_DIR" :
				String lineBr = "spark";
				JschExecutor jschExe = new JschExecutor();
				Session sess = jschExe.getSession(commandWritable, command);
				ChannelReaderResponse channelReaderRes = jschExe.executeResponsiveShellJsch(sess, command);
				BufferedReader bufferedReader = (BufferedReader)channelReaderRes.getReader();
				String commandResponse;
				StringBuilder strBuilder;
				try{
					strBuilder = new StringBuilder();
					String line = "";
					while ((line = bufferedReader.readLine()) != null) {
						strBuilder.append(line + System.lineSeparator());
						if (line.contains(lineBr)) {
							strBuilder = new StringBuilder(line);
							break;
						}
					}
				}finally{
					if(bufferedReader!=null){
						bufferedReader.close();
					}
				}
				commandResponse = strBuilder.toString();				
				jschExe.closeChannel(channelReaderRes.getChannel());
				jschExe.closeSession(sess);
				return commandResponse;
				
			default:
				LOGGER.warn("Handled an unknown echo command");
				return null;
			}
	}

	/**
	 * Handle responsive free.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String handleResponsiveFree(CommandWritable commandWritable, Command command) throws IOException{
		JschExecutor jschExecutor = new JschExecutor();
		Session session = jschExecutor.getSession(commandWritable, command);
		ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveShellJsch(session, command);
		String lineBreaker = "Swap:";
		String response =  getFreeStringFromReader((BufferedReader)channelReaderResponse.getReader(), lineBreaker);
		channelReaderResponse.getReader().close();
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		LOGGER.info("Jsch Shell - Command [" +command.getCommandString()+"] returned with response code - " + channelReaderResponse.getExitCode());
		//logging response for all non zero terminations, except commands ending with '&& exit', which always returns -1
		if(channelReaderResponse.getExitCode()!=0 && !command.getCommandString().endsWith("&& exit")){
			LOGGER.debug("Jsch Shell - Command [" +command.getCommandString()+"] exited with unexpected code, response - " + response);
		}
		return response;	
	}
	
	/**
	 * Handle responsive awk container.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String handleResponsiveAwkContainer(CommandWritable commandWritable, Command command) throws IOException{
		JschExecutor jschExecutor = new JschExecutor();
		Session session = jschExecutor.getSession(commandWritable, command);
		ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveShellJsch(session, command);
		String line = null;
		StringBuilder stringBuilder = new StringBuilder("");
		try (BufferedReader reader = (BufferedReader)channelReaderResponse.getReader()) {
			stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.contains(LOGOUT)) {
					break;
				}
				if (line.contains("KB") || line.contains("MB") || line.contains("GB")
						|| line.contains("TB") || line.contains("PB")) {			
					
					int totalSpaces = line.length() - line.replaceAll(" ", "").length();
					if (totalSpaces != 3 || !line.split(" ")[0].matches("[-+]?\\d*\\.?\\d+")) {
						continue;
					}
					stringBuilder = new StringBuilder(line);					
					break;
				}
			}
		}
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		return stringBuilder.toString();
	}
	

	/**
	 * Invoke executor.
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @return the object
	 * @throws SocketException the socket exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSchException the j sch exception
	 */
	private Object invokeExecutor(CommandWritable commandWritable, Command command) throws SocketException, IOException, JSchException{
		// Reflective method invocation
		if (commandWritable.getMethodToBeInvoked() != null) {		
			try {
				return RemotingMethodInvocationUtil.invokeMethodFromRemotingMethodConstants(commandWritable, command, CommandAsObjectResponserMethods.class);
			}catch(ReflectiveOperationException e){
				LOGGER.error("Unable to execute method "+ commandWritable.getMethodToBeInvoked(), e.getCause());	
			}			
		} else if(getNameNodeHost().equals(getCurrentMachineEndpoint()) || getNameNodeHost().equals(InetAddress.getLocalHost().getHostName())){
			try {
				if(commandWritable.isCommandForMaster()){
					LOGGER.debug("NameNode and JumbuneAgent are on same node and command is for master, executing with Runtime ["+command.getCommandString()+"]");
					return executeCommandWithRuntime(command);
				}
			} catch (InterruptedException e) {
				LOGGER.error("Exception occured while executing the process (launched by runtime) to finish.");
			}
		}
		JschExecutor jschExecutor = new JschExecutor();
		Session session = jschExecutor.getSession(commandWritable, command);
		ChannelReaderResponse channelReaderResponse = jschExecutor.executeResponsiveExecJsch(session, command);
		String response = getStringFromReader((BufferedReader)channelReaderResponse.getReader());
		if(channelReaderResponse.getReader()!=null){
			channelReaderResponse.getReader().close();
		}
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		LOGGER.info("JSch Exec - Executed command with JSch[" + command.getCommandString() +"], exit code - " + channelReaderResponse.getExitCode());
		if(channelReaderResponse.getExitCode()!=0){
			LOGGER.debug("JSch Exec - command [" + command.getCommandString() +"], exited with unexpected code, response - " + response);
		}
		return response;
	}

	/**
	 * Command expected to be invoked from sudo agent runtime.
	 * Typically command's with user, fs, mapred, yarn and hdfs user switch can be invoked. 
	 * This method should be invoked only when Active NN & Active Jumbune Agent are on the same host
	 *
	 * @param command the command
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private String executeCommandWithRuntime(Command command) throws IOException, InterruptedException{
		String commandString = command.getCommandString();
		Process process = executeCommandWithRuntime0(command);
		String response = null;
		response = getProcessOutput(process);		
		LOGGER.info("Runtime - Executed command [" + commandString +"], exit code - " + process.exitValue());
		if(process.exitValue()!=0){
			LOGGER.debug("Runtime - command [" + commandString +"], exited with unexpected code, response - " + response);
		}
		return response;
	}

	/**
	 * Execute command with param.
	 *
	 * @param commands the commands
	 * @return the object
	 */
	private void checkexpectedTwoParams(String[] commands) {
		if (commands.length != RemotingConstants.TWO) {
			throw new IllegalArgumentException("Invalid params received from Remoter!!!");
		}
	}

}