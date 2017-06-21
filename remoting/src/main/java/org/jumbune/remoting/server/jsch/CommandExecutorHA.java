package org.jumbune.remoting.server.jsch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class CommandExecutorHA {

	private static final Logger LOGGER = LogManager.getLogger(CommandExecutorHA.class);

	private CommandWritable commandWritable;
	
	private Command command;
	
	public CommandExecutorHA(CommandWritable commandWritable, Command command) {
		this.commandWritable = commandWritable;
		this.command = command;
	}

	public void executeShellJsch() {
		ChannelShell channelShell = null;
		PrintStream ps = null;
		Session session = getSession(commandWritable, command);
		String errorDebug = "DEBUG EMPTY";
		try {
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			InputStream is = channelShell.getInputStream();
			channelShell.connect();
			ps = new PrintStream(channelShell.getOutputStream(), true);
			ps.println(command.getCommandString());
			ps.println("logout");
			errorDebug = logJsch(channelShell, is);
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
			LOGGER.info("Shell JSch - Executed command [" + command.getCommandString() +"], exit code [" + channelShell.getExitStatus()+"]");
			if(channelShell.getExitStatus()!=0){
				LOGGER.error("Detailed Debug log for Errored command [" + command.getCommandString() +"]\n ----- \n"+errorDebug +"\n-----");
			}
			closeChannel(channelShell);
			closeSession(session);
		}		
	}
	
	private static String logJsch(Channel channel, InputStream in) 
	{
		StringBuilder builder = new StringBuilder();
	    try {
	        byte[] tmp = new byte[1024];
	        while (true) {
	            while (in.available() > 0) {
	                int i = in.read(tmp, 0, 1024);
	                if (i < 0)
	                    break;
	                builder.append((new String(tmp, 0, i)));
	            }
	            if (channel.isClosed()) {
	            //	LOGGER.debug("exit-status: " + channel.getExitStatus());
	                break;
	            }
	        }
	    } catch (Exception ex) {
	    	LOGGER.error(ex);
	    }
	    return builder.toString();
	}
	


	private void performCommandCuration(Command command){
		String commandString = command.getCommandString();
		String workingUser = JschUtil.getCommandAppender(command.getSwitchedIdentity());	     
		commandString = workingUser + commandString;
		command.setCommandString(commandString);
	}


	public void executeExecJsch(){
		performCommandCuration(command);
		ChannelExec channelExec = null;
		PrintStream ps = null;
		Session session = getSession(commandWritable, command);
		String errorDebug = "DEBUG EMPTY";
		try {
			session.connect();
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setPty(true);
			InputStream is = channelExec.getInputStream();
			channelExec.connect();
			ps = new PrintStream(channelExec.getOutputStream(), true);
			ps.println(command.getCommandString());
			errorDebug = logJsch(channelExec, is);
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
			LOGGER.info("Exec JSch - Executed command [" + command.getCommandString() +"], exit code [" + channelExec.getExitStatus()+"]");
			if(channelExec.getExitStatus()!=0){
				LOGGER.error("Detailed Debug log for Errored command [" + command.getCommandString() +"]\n ----- \n"+errorDebug +"\n-----");
			}
			closeChannel(channelExec);
			closeSession(session);
		}		
	}

	public String executeResponsiveShellJsch(){
		ChannelShell channelShell = null;
		InputStream in = null;
		PrintStream ps = null;
		String response = null;
		LOGGER.info("executing command using shell - ", command);
		Session session = getSession(commandWritable, command);
		try {
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			in = channelShell.getInputStream();
			channelShell.connect();
			ps = new PrintStream(channelShell.getOutputStream(), true);
			ps.println(command.getCommandString());
			ps.println("logout");
			response = getStringFromStream(in);
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
			closeChannel(channelShell);
			closeSession(session);
		}
		return  response;
	}

	public String executeResponsiveExecJsch(){
		ChannelExec channelExec = null;
		PrintStream ps = null;
		InputStream in = null;
		String response = null;
		Session session = getSession(commandWritable, command);
		try {
			session.connect();
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setPty(true);
			in = channelExec.getInputStream();
			channelExec.connect();
			ps = new PrintStream(channelExec.getOutputStream(), true);
			ps.println(command.getCommandString());
			response = getStringFromStream(in);
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
			closeChannel(channelExec);
			closeSession(session);
		}
		return  response;
	}

	private Session getSession(CommandWritable commandWritable, Command command){
		Session session = null;

		CommandType commandType = command.getCommandType();
		String host = commandWritable.getNameNodeHost();
		if(host == null){
			host = commandWritable.getWorkerHost();
		}
		try {
			session = JschUtil.createSession(command.getSwitchedIdentity(), host, commandType);
		} catch (JSchException e) {
			LOGGER.error("Error [" + e.getMessage() + "] while creating JSchSession for (user, host) pair ("
					+ command.getSwitchedIdentity().getUser() + ", " + host + ")");
		}
		return session;
	}

	public void closeSession(Session session){
		if(session!=null && session.isConnected()){
			session.disconnect();
		}
	}

	public void closeChannel(Channel channel){
		if(channel!=null && channel.isConnected()){
			channel.disconnect();
		}
	}
	
	private String getStringFromStream(InputStream inputStream){
		StringBuilder response = new StringBuilder();	     
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			for (String line = br.readLine(); br != null && line != null; line = br.readLine()) {
				response.append(line).append(System.lineSeparator());
 			}
 		}catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return response.toString();
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
	public String executeCommandWithRuntime(){
		String commandString = command.getCommandString();
		String response = null;
		Process process = null;
		try {
			process = executeCommandWithRuntime0(command);
		response = getProcessOutput(process);		
		LOGGER.info("Runtime - Executed command [" + commandString +"], exit code - " + process.exitValue());
		LOGGER.info("response - " + response);
		} catch (IOException | InterruptedException e) {			
			LOGGER.error(e.getMessage(), e);
		}
		return response;
	}


	private Process executeCommandWithRuntime0(Command command) throws IOException{
		String commandString = command.getCommandString();
		Process process = null;
		String finalCommand[] = null;
		if(commandString.contains(RemotingConstants.PIPE_OP) || commandString.contains(RemotingConstants.REDIRECT_SYMBOL)) {
			finalCommand = performBashCommandCuration(command);
			process = Runtime.getRuntime().exec(finalCommand);
		}else{
	//		commandString = performCommandCuration(command);
	//		process = Runtime.getRuntime().exec(commandString);
			finalCommand = performBashCommandCuration(command);
			process = Runtime.getRuntime().exec(finalCommand);
		}
		return process;
	}
	
	private String[] performBashCommandCuration(Command command) {
		String commandString = command.getCommandString();
		List<String> temp = new ArrayList<>(5);
        // to be tested in multi-user envs
		/*		String workingUser = JschUtil.getCommandAppender(command
				.getSwitchedIdentity());
		for (String token : workingUser.split("\\s+")) {
			if (token != null && !token.isEmpty()) {
				temp.add(token);
			}
		}*/
		temp.add(RemotingConstants.BASH);
		temp.add(RemotingConstants.BASH_ARG_C);
		temp.add(commandString);
		String finalCommand[] = Arrays.copyOf(temp.toArray(), temp.size(), String[].class);
		LOGGER.info("final command - " + Arrays.toString(finalCommand));
		return finalCommand;
	}
	
	/**
	 * This method gathers the output of process passed in and returns the string response.
	 * It captures InputStream and ErrorStream(using class {@link ProcessOutputReader}) of the process so those streams should not be flushed before 
	 * the invocation of this method. Though the output and errors are usually in order as produced by the process but this 
	 * method does not guarantee to preserve their order in extreme race conditions.
	 *   
	 * @param process
	 * @return
	 * @throws InterruptedException
	 */
	private String getProcessOutput(Process process) throws InterruptedException {
		StringBuilder output = new StringBuilder();
		Thread t1 = new Thread(new ProcessOutputReader(process.getErrorStream(), output));
		t1.start();
		Thread t2 = new Thread(new ProcessOutputReader(process.getInputStream(), output));
		t2.start();
		process.waitFor();
        
		//lets wait for output aggregation by both the threads
		t1.join();
		t2.join();
		return output.toString();
	}

	private static class ProcessOutputReader implements Runnable {

		InputStream in = null;
		StringBuilder output = null;

		ProcessOutputReader(InputStream in, StringBuilder output) {
			this.in = in;
			this.output = output;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				for (String line = br.readLine(); br != null && line != null; line = br.readLine()) {
					synchronized (output) {
						output.append(line).append(System.lineSeparator());						
					}
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

	}

}