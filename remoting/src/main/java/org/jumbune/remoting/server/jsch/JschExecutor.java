package org.jumbune.remoting.server.jsch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschExecutor {

	private static final Logger LOGGER = LogManager.getLogger(JschExecutor.class);

	public void executeShellJsch(CommandWritable commandWritable, Command command) {
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


	public void executeExecJsch(CommandWritable commandWritable, Command command){
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

	public ChannelReaderResponse executeResponsiveShellJsch(Session session, Command command){
		ChannelReaderResponse channelReaderResponse = new ChannelReaderResponse();
		ChannelShell channelShell = null;
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			br = new BufferedReader(new InputStreamReader(
					channelShell.getInputStream()));
			channelShell.connect();
			ps = new PrintStream(channelShell.getOutputStream(), true);
			ps.println(command.getCommandString());
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		channelReaderResponse.setChannel(channelShell);
		channelReaderResponse.setReader(br);
		channelReaderResponse.setExitCode(channelShell.getExitStatus());
		return channelReaderResponse;
	}

	public ChannelReaderResponse executeResponsiveExecJsch(Session session, Command command){
		ChannelReaderResponse channelReaderResponse = new ChannelReaderResponse();
		ChannelExec channelExec = null;
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			session.connect();
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setPty(true);
			br = new BufferedReader(new InputStreamReader(
					channelExec.getInputStream()));
			channelExec.connect();
			ps = new PrintStream(channelExec.getOutputStream(), true);
			ps.println(command.getCommandString());
		} catch (Exception e) {
			LOGGER.error("Error ["+ e.getMessage() + "] while executing command [" +command.getCommandString()+"]");
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
		channelReaderResponse.setChannel(channelExec);
		channelReaderResponse.setReader(br);
		return channelReaderResponse;
	}

	public Session getSession(CommandWritable commandWritable, Command command){
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

}