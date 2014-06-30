package org.jumbune.remoting.server;

import static org.jumbune.remoting.common.RemotingConstants.REDIRECT_SYMBOL;
import static org.jumbune.remoting.common.RemotingConstants.SINGLE_SPACE;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * The Class CommandDelegator.
 */
public class CommandDelegator extends SimpleChannelInboundHandler<CommandWritable> {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandDelegator.class);
	private static final String CONST_1 = "[1]";
	private static final String CONST_PID = "pid=";
	private static final String EXIT_CMD = "exit";
	private static final String ECHO_CMD = "echo";
	private static final String PID_FILE = "pid.txt";
	private static final String KILL_PID_CMD = "kill -9";
	
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
		LOGGER.debug("Now sending Ack");
		ctx.channel().writeAndFlush("Ack");
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
		String rippedCommand = null;
		List<Command> commandList = commandWritable.getBatchedCommands();

		for (Command command : commandList) {
			if (ApiInvokeHintsEnum.JOB_EXECUTION.equals(commandWritable.getApiInvokeHints())) {
				String arr[] = command.getCommandString().split(RemotingConstants.SINGLE_SPACE);
				String[] jschArray = new String[arr.length+1];
				jschArray[0] = RemotingConstants.SSH;
				System.arraycopy(arr, 0, jschArray, 1, arr.length);
				JschUtil.execSlaveCleanUpTask(jschArray, null);
			} else if (commandWritable.isAuthenticationRequired()) {
				executeCommandsWithJsch(commandWritable, command);
			} else  {
				String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
				rippedCommand = command.getCommandString().replace(RemotingConstants.AGENT_HOME, agentHome).trim();
				execute(rippedCommand.split(RemotingConstants.SINGLE_SPACE));
			}
		}
	}

	/**
	 * Execute.
	 * 
	 * @param commands
	 *            the commands
	 */
	private static void execute(String... commands) {
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(new File(System.getenv(RemotingConstants.AGENT_HOME)));
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		LOGGER.debug("command executed " + Arrays.toString(commands));
	}

	/**
	 * Execute commands with jsch.
	 * 
	 * @param commandWritable
	 *            the command
	 * @param command 
	 */
	private void executeCommandsWithJsch(CommandWritable commandWritable, Command command) {
		LOGGER.debug("Processing parameterized command");
		
		String user = commandWritable.getUsername();
		String rsaFile = commandWritable.getRsaFilePath();
		String dsaFile = commandWritable.getDsaFilePath();
		String host = commandWritable.getMasterHostname();
		
		if(host == null){
			// command to be executed on slave
			host = commandWritable.getSlaveHost();
		}
		Session session = null;
		try {
			session = JschUtil.createSession(user, host, rsaFile, dsaFile);
			String commandStr = command.getCommandString();
			if (commandStr.contains(RemotingConstants.AGENT_HOME)) {
				commandStr = commandStr.replace(RemotingConstants.AGENT_HOME, System.getenv(RemotingConstants.AGENT_HOME));
			}
			LOGGER.debug("processing command : "+commandStr);
			
			//execute command on slaves using shell channel
			if(command.isHasParams()){
				executeCommandWithParams(command, host, session, commandStr);
			}else{
				JschUtil.getChannel(session, commandStr);
				LOGGER.debug("Operation [" + commandStr + "] got performed");
			}
		} catch (JSchException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}finally {
			session.disconnect();
		}
	}

	private void executeCommandWithParams(Command command, String host,
			Session session, String commandStr) throws JSchException,
			IOException {
		BufferedReader br = null;
		InputStream in = null;
		PrintStream ps = null;
		
		try{
		LOGGER.debug("Executing command on slaves");
		
		String line = null;
		com.jcraft.jsch.Channel channel = JschUtil.getShellChannel(session, commandStr);
		OutputStream os = channel.getOutputStream();
		ps = new PrintStream(os, true);
		in = channel.getInputStream();
		channel.connect();
		String commandToExec = commandStr;
		if (commandStr.contains("grep")) {
			commandToExec = commandStr + "_" + host;
		}
		ps.println(commandToExec);
		br = new BufferedReader(new InputStreamReader(in));
		
		if(commandStr.contains("grep")){
			ps.println(EXIT_CMD);
		}
		while ((line = br.readLine()) != null) {
			if (line.contains(CONST_1)) {
				line = line.replace(CONST_1, "");
				String pid = line.trim();
				StringBuffer sb = new StringBuffer(ECHO_CMD);
				sb.append(SINGLE_SPACE).append("'").append(CONST_PID).append(pid).append("'").append(REDIRECT_SYMBOL).append(command.getParams().get(0))
						.append(File.separator).append(PID_FILE);
				ps.println(sb.toString());
				ps.println(EXIT_CMD);
				LOGGER.debug("Executed commmand [" + sb + EXIT_CMD + "] host" + host);			
			}
			//executing shut down command 
			else if (line.contains(CONST_PID)) {
				String pid = line.replace(CONST_PID, "");
				ps.println(KILL_PID_CMD + SINGLE_SPACE + pid);
				ps.println(EXIT_CMD);
				LOGGER.debug("Shutting down Slave - [" + KILL_PID_CMD + SINGLE_SPACE + pid + EXIT_CMD + "]  host [" + host + "]");	
			}
		}
		}finally{
			if(br != null){
				br.close();
			}
			if(in != null){
				in.close();
			}
			if(ps != null){
				ps.close();
			}
			
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	  io.netty.channel.Channel ch = ctx.channel();
	  if (cause instanceof TooLongFrameException) {
		  LOGGER.error("Corrupted fram recieved from: " + ch.remoteAddress());
	    return;
	  }

	  if (ch.isActive()) {
	    LOGGER.error("Internal Server Error",cause);
	  }
	}	

}
