package org.jumbune.remoting.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandStatus;
import org.jumbune.remoting.common.CommandZNodesUtility;
import org.jumbune.remoting.common.CuratorConnector;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodInvocationUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.server.invocations.CommandAsObjectResponserMethods;
import org.jumbune.remoting.server.jsch.ChannelReaderResponse;
import org.jumbune.remoting.server.jsch.JschExecutor;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * The Class CommandAsObjectResponser.
 */
public class CommandAsObjectResponserHA extends AbstractCommandHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandAsObjectResponserHA.class);
	
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
	
	private static final String EXECUTE_SHELL_JSCH = "executeShellJsch";
	
	private static final String EXECUTE_EXEC_JSCH = "executeExecJsch";
	
	private static final String EXECUTE_RESPONSIVE_SHELL_JSCH = "executeResponsiveShellJsch";
	
	private static final String EXECUTE_RESPONSIVE_EXEC_JSCH = "executeResponsiveExecJsch";
	
	private static final String EXECUTE_COMMAND_WITH_RUNTIME = "executeCommandWithRuntime";

	private static final String BIN_JAVA = "/bin/java";

	private static final String LIB_ALL = "/lib/*";
	
	private static final String NOHUP = "/usr/bin/nohup";
	
	private static final String HASH_BANG = "#!/usr/bin/env bash";

	private static final String CMD_OUT = "/cmd.out 2> ";

	private static final String CMD_ERR = "/cmd.err < /dev/null ";

	private static final String EXEC = "exec";

	private static final String CMD = "cmd_";

	private static final String SH_EXTENSION = ".sh";

	private static final String SUDO = "sudo";

	private static final String U_ARG = "-u";
	
	private CuratorConnector curatorConnector = null;

	
	public CommandAsObjectResponserHA() {
	  curatorConnector = CuratorConnector.getInstance(JumbuneAgent.getZKHosts());
	}
	
	/* (non-Javadoc)
	 * @see org.jumbune.remoting.server.AbstractCommandHandler#channelRead0(io.netty.channel.ChannelHandlerContext, org.jumbune.remoting.writable.CommandWritable)
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
	 * @throws InterruptedException 
	 */
	private Object performAction(final CommandWritable commandWritable) throws JSchException, IOException, InterruptedException {
		List<Command> commandList = commandWritable.getBatchedCommands();
		for(Command currentCommand : commandList){
			String commandString = currentCommand.getCommandString();
			 CommandZNodesUtility czu = new CommandZNodesUtility(curatorConnector, currentCommand.getCommandId());
			 czu.setStatusZNodeData(CommandStatus.RECEIVED.toString());
			
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
			if(commandString.startsWith("awk '/container-id ")){
				return handleResponsiveAwkContainer(commandWritable, currentCommand);
			}

			if(commandString.contains(CPU_USAGE_COMMAND) || commandString.contains(CPU_USAGE_COMMAND_WITHOUT_CARET)) {
				return handleCpuUsageCommand(commandWritable, currentCommand);
			} 
			if(commandString.contains(CPU_DETAILS_COMMAND)) {
				return executeWithShell(commandWritable, currentCommand);
			} 
			if(commandString.contains(VMSTAT_COMMAND)) {
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
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
		LOGGER.info("Jsch Shell - Command [" +command.getCommandString()+"] returned with response - " + response);
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
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException 
	 */
	private Object appendAbsolutePathAndExecuteJob(CommandWritable commandWritable,
			Command currentCommand, String[] commandWithParams, String jarLocation) throws JSchException, IOException, InterruptedException {
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
	 * @throws InterruptedException 
	 */
	private Object executeJob(CommandWritable commandWritable,
			Command currentCommand, String[] commandWithParams, String jarLocation) throws JSchException, IOException, InterruptedException {
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
		jschExecutor.closeChannel(channelReaderResponse.getChannel());
		jschExecutor.closeSession(session);
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
		BufferedReader reader = (BufferedReader)channelReaderResponse.getReader();
		String response;
		String line = "";
		StringBuilder stringBuilder;
		try{
			reader = (BufferedReader)channelReaderResponse.getReader();
			stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + System.lineSeparator());
				if (line.contains(LOGOUT) || line.matches("[\\d]*[\\.]?[\\d]*[GPMKT]{1}B[ ]{1}[\\d]*[\\.]?[\\d]*[GPMKT]{1}B")) {			
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
	}
	

	/**
	 * Spawns new jvm for command. Blocks till command is completed and returns the response finally. 
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @param methodName the method name
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private Object spawnNewJVMForCommand(CommandWritable commandWritable, Command command, String methodName) throws IOException, InterruptedException {
        String cmd[] = prepareCommand(commandWritable, command, methodName); 
		LOGGER.debug("spawning New JVM, sending command - " + Arrays.toString(cmd));
		Runtime.getRuntime().exec(cmd);		
		CommandZNodesUtility czu = new CommandZNodesUtility(curatorConnector, command.getCommandId());
		CommandStatus status = CommandStatus.valueOf(czu.getStatusZNodeData());
		if(status !=  CommandStatus.EXECUTING){
			czu.setStatusZNodeData(CommandStatus.EXECUTING.toString());
		}				
		czu.pollForDataChanges(CommandZNodesUtility.NodeType.STATUS, 3);
		return czu.getResponseZNodeData();

	}
	
	/**
	 * Prepares command to be executed in a different JVM. It writes the command in the form of 
	 * bash script which is then executed in its own JVM by method <code> spawnNewJVMForCommand(CommandWritable commandWritable, Command command, String methodName)</code>. 
	 *
	 * @param commandWritable the command writable
	 * @param command the command
	 * @param methodName the method name
	 * @return the string[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String[] prepareCommand(CommandWritable commandWritable, Command command, String methodName) throws IOException {
		String javaHome = System.getProperty(RemotingConstants.JAVA_HOME_PROP_KEY);
		// path of jdk
		javaHome = javaHome.substring(0, javaHome.lastIndexOf(File.separator));
		Gson gson = new Gson();
		StringBuilder libEntriesBuilder = new StringBuilder();
        String libEntries = null;
		for(String libEntry : JumbuneAgent.getAgentLibEntries()) {
			libEntriesBuilder.append(RemotingConstants.COLON).append(libEntry);
		}		
		libEntries = libEntriesBuilder.toString().replaceFirst(RemotingConstants.COLON, "");
		
		String commandDir = JumbuneAgent.getHAProps().getProperty(RemotingConstants.COMMAND_LOG_DIR);
		if(commandDir == null || commandDir.isEmpty()) {
			commandDir = JumbuneAgent.getAgentDirPath();
		}
		
		File commandLogsDir = new File(commandDir + File.separator + command.getCommandId());
		commandLogsDir.mkdirs();
		commandLogsDir.setReadable(true, false);
		commandLogsDir.setWritable(true, false);
		String[] cmd = {EXEC, RemotingConstants.SINGLE_SPACE, 			
				NOHUP + RemotingConstants.SINGLE_SPACE + javaHome + BIN_JAVA + RemotingConstants.SINGLE_SPACE
				+ RemotingConstants.CLASSPATH_ARG + RemotingConstants.SINGLE_SPACE + libEntries
				+ RemotingConstants.SINGLE_SPACE + RemotingConstants.COMMAND_EXEC_DRIVER
				+ " '" + gson.toJson(commandWritable) + "'  '"
				+ gson.toJson(command) + "'  '" + gson.toJson(methodName)
				+ "'  '"+ gson.toJson(JumbuneAgent.getZKHosts())
				 +"' > "+commandLogsDir.getAbsolutePath()+CMD_OUT+commandLogsDir.getAbsolutePath()+CMD_ERR+ RemotingConstants.AMPERSAND };
		
		String scriptFilePath =commandLogsDir.getAbsolutePath() + File.separator + CMD + command.getCommandId() + SH_EXTENSION;
		try (BufferedWriter bw = Files.newBufferedWriter(
				Paths.get(scriptFilePath), Charset.defaultCharset(),
				StandardOpenOption.CREATE)) {
			bw.write(HASH_BANG);
			bw.write(System.lineSeparator());
			for (String token : cmd) {
				bw.write(token);
			}
			bw.write(System.lineSeparator());
			bw.flush();
		}
		File f = new File(scriptFilePath);		
		f.setExecutable(true, false);
		
		String[] scriptCmd = { SUDO, U_ARG, command.getSwitchedIdentity().getWorkingUser(), RemotingConstants.BASH,
				scriptFilePath };
		return scriptCmd;
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
	 * @throws InterruptedException 
	 */
	private Object invokeExecutor(CommandWritable commandWritable, Command command) throws SocketException, IOException, JSchException, InterruptedException{
		// Reflective method invocation
		if (commandWritable.getMethodToBeInvoked() != null) {		
			try {
				return RemotingMethodInvocationUtil.invokeMethodFromRemotingMethodConstants(commandWritable, command, CommandAsObjectResponserMethods.class);
			}catch(ReflectiveOperationException e){
				LOGGER.error("Unable to execute method "+ commandWritable.getMethodToBeInvoked(), e.getCause());	
			}			
		} else if(getNameNodeHost().equals(getCurrentMachineEndpoint()) || getNameNodeHost().equals(InetAddress.getLocalHost().getHostName())){
			if(commandWritable.isCommandForMaster()){
				LOGGER.debug("NameNode and JumbuneAgent are on same node and command is for master, executing with Runtime ["+command.getCommandString()+"]");
				return spawnNewJVMForCommand(commandWritable, command, EXECUTE_COMMAND_WITH_RUNTIME);
			}
		}
		return spawnNewJVMForCommand(commandWritable, command, EXECUTE_RESPONSIVE_SHELL_JSCH);
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