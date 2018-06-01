package org.jumbune.remoting.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.server.JumbuneAgent;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


/**
 * The Class Jschutil is an utility class for handling the all channel related operations.
 */
public final class JschUtil {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER=LogManager.getLogger(JschUtil.class);
	/**
	 * Instantiates a new jsch util.
	 */
	private JschUtil() {
	}
	
	/** The Constant STRICT_HOST_KEY_CHECKING. */
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

	/** The Constant NEW_LINE. */
	private static final String NEW_LINE = "\n";
	
	/** The Constant GB. */
	private static final String GB = "GB";
	
	/** The Constant LOGOUT. */
	private static final String LOGOUT = "logout";

	private static final String PID_COMMAND_PREFIX = "ps -ef | awk '/\\.";

	private static final String PID_COMMAND_SUFFIX = "/{print $0}' && exit";
		
	/**
	 * The logger.
	 *
	 * @param switchedIdentity the switched identity
	 * @param host the host
	 * @param commandType the command type
	 * @return the session
	 * @throws JSchException the j sch exception
	 */

	/**
	 * For creating a JSCH session.
	 * 
	 * @param host
	 *            the host
	 * @return a JSCH session
	 * @throws JSchException
	 *             the jsch exception
	 */
	public static Session createSession(CommandWritable.Command.SwitchedIdentity switchedIdentity, String host, CommandType commandType) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		if(switchedIdentity.getPrivatePath() != null && !switchedIdentity.getPrivatePath().isEmpty()){
			jsch.addIdentity(switchedIdentity.getPrivatePath());	
		}
		java.util.Properties conf = new java.util.Properties();
		session = jsch.getSession(switchedIdentity.getUser(), host, RemotingConstants.TWENTY_TWO);
		conf.put(STRICT_HOST_KEY_CHECKING, "no");
		if(switchedIdentity.getPasswd()!=null){
			try {
				session.setPassword(StringUtil.getPlain(switchedIdentity.getPasswd()));
			} catch (Exception e) {
				LOGGER.error("Unable to get decrypted Password", e);
			}
		}
		session.setConfig(conf);
		return session;
	}
	
	/**
	 * Verify password.
	 *
	 * @param user the user
	 * @param encryptedPasswrd the encrypted passwrd
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	//TODO: Require to make sure we are not using localhost
	public static boolean verifyPassword(String user, String encryptedPasswrd) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
		java.util.Properties conf = new java.util.Properties();
		session = jsch.getSession(user, "localhost", RemotingConstants.TWENTY_TWO);
		UserInfo info = new JumbuneUserInfo(StringUtil.getPlain(encryptedPasswrd));
		session.setUserInfo(info);
		conf.put(STRICT_HOST_KEY_CHECKING, "no");
		session.setConfig(conf);
	//	LOGGER.debug("Session Established, for user ["+user+"]");
		boolean isConnected = false;
		if(session!=null){
			session.connect();
			isConnected = session.isConnected();
			LOGGER.debug("Session Connected, for user ["+user+"]");
			session.disconnect();
		}
		return isConnected;
	}
	
	/**
	 * Gets the command appender.
	 *
	 * @param switchedIdentity the switched identity
	 * @return the command appender
	 */
	public static String getCommandAppender(CommandWritable.Command.SwitchedIdentity switchedIdentity){
		String command = null ;
		command = "sudo "+"-u " + switchedIdentity.getWorkingUser() + RemotingConstants.SINGLE_SPACE;
		return command ;
	}
	
	/**
	 * Gets the channel.
	 * 
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the channel
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Channel getChannel(Session session, String command) throws JSchException, IOException {
		session.connect();
		Channel channel = session.openChannel("exec");
	
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);

		InputStream in = channel.getInputStream();

		channel.connect();
		((ChannelExec) channel).setErrStream(System.err);

		byte[] tmp = new byte[RemotingConstants.ONE_ZERO_TWO_FOUR];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, RemotingConstants.ONE_ZERO_TWO_FOUR);
				if (i < 0){
					break;
				}
			}
			if (channel.isClosed()) {
				break;
			}
		}
		return channel;
	}

	/**
	 * Gets the channel with response.
	 * 
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the channel with response
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Channel getChannelWithResponse(Session session, String command) throws JSchException, IOException {
		session.connect();
		Channel channel = session.openChannel("exec");
		if(command.contains("sudo")){
			((ChannelExec) channel).setPty(true);
		}
		((ChannelExec) channel).setCommand(command);
		((ChannelExec) channel).setErrStream(System.err);		
		return channel;
	}

	/**
	 * Exec slave clean up task.
	 *
	 * @param commands the command
	 * @param directory the directory
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String execSlaveCleanUpTask(String[] commands, String directory) throws IOException {
        List<String> cmds = new ArrayList<>();  
        String[] splits;

        for (int i = 0; i < commands.length; i++) {
		    splits = commands[i].split("\\s+");
		    for (String split : splits) {
				cmds.add(split);
			}
        }
		
		ProcessBuilder pb = new ProcessBuilder(cmds);
		String agentHome = JumbuneAgent.getAgentDirPath();
		if (directory != null && !directory.isEmpty()){
			pb.directory(new File(directory));
		}
		else{
			pb.directory(new File(agentHome));
		}
		pb.redirectErrorStream(true);
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();
				while (line != null) {
					sb.append(line).append(RemotingConstants.NEW_LINE);
					line = br.readLine();
				}
			}
			LOGGER.debug("Received command for Slave clean up "+Arrays.toString(commands));
			
		}  finally {
				if (br != null){
					br.close();
				}
		}
		return sb.toString();
	}

	/**
	 * The Class JumbuneUserInfo.
	 */
	private static class JumbuneUserInfo implements UserInfo {

		/** The passwd. */
		String passwd;

		/**
		 * Instantiates a new jumbune user info.
		 */
		JumbuneUserInfo(){
		}
		
		/**
		 * Instantiates a new jumbune user info.
		 *
		 * @param passwd the passwd
		 */
		JumbuneUserInfo(String passwd){
		  this.passwd = passwd;	
		}
		
		/**
		 * gets the password.
		 *
		 * @return the password
		 */
		public String getPassword() {
			return passwd;
		}

		/**
		 * set prompt YES/NO.
		 *
		 * @param str the str
		 * @return true, if successful
		 */
		public boolean promptYesNo(String str) {
			return true;
		}

		/**
		 * gets the passphrase.
		 *
		 * @return the passphrase
		 */
		public String getPassphrase() {
			return null;
		}

		/**
		 * set the passphrase.
		 *
		 * @param message the message
		 * @return true, if successful
		 */
		public boolean promptPassphrase(String message) {
			return true;
		}

		/**
		 * prompt for password.
		 *
		 * @param message the message
		 * @return true, if successful
		 */
		public boolean promptPassword(String message) {
			return true;
		}

		/**
		 * set the message.
		 *
		 * @param message the message
		 */
		public void showMessage(String message) {
		}
	}

	/**
	 * Gets the channel.
	 * 
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the channel
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Channel getShellChannel(Session session) throws JSchException, IOException {
		session.connect();
		return session.openChannel("shell");
	}
	
	/**
	 * *
	 * executes commands from shell.
	 *
	 * @param session the session
	 * @param simpleCommand the simple command
	 * @param lineBreaker It is a small phrase for stoping the iteration of loop while checking response of executed command.
	 * @return the string
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String executeCommandUsingShell(Session session, String simpleCommand, String lineBreaker) throws JSchException, IOException {
		String response = null;
		ChannelShell channelShell = null;
		BufferedReader brIn = null;
		DataOutputStream dataOut = null;
		try {
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			channelShell.connect();
			brIn = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
			
			PrintStream ps = new PrintStream(channelShell.getOutputStream(), true);
			ps.println(simpleCommand);

			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = brIn.readLine()) != null) {
				stringBuilder.append(line + NEW_LINE);
				if (line.contains(lineBreaker)) {
					if (lineBreaker.contains("echo")) {
						stringBuilder.setLength(0);
						stringBuilder = new StringBuilder(brIn.readLine());
					}
					if(line.contains(LOGOUT) || lineBreaker.contains(GB) && line.contains(GB)){
						stringBuilder = new StringBuilder(line);
					}
					break;					
				}

			}
			response = stringBuilder.toString();
			LOGGER.debug("Executed  Command [" + simpleCommand + "]");
		} finally {
			if (brIn != null) {
				brIn.close();
			}
			if (dataOut != null) {
				dataOut.close();
			}
			if (channelShell != null) {
				channelShell.disconnect();
			}
		}
		return response;
	}
	
	public static String getDaemonProcessId(Session session, String daemon) throws JSchException, IOException {
		String command = PID_COMMAND_PREFIX + daemon + PID_COMMAND_SUFFIX;
		session.connect();
		Channel channel = session.openChannel("shell");
		channel.connect();
		OutputStream os = channel.getOutputStream();
		InputStream is = channel.getInputStream();
		PrintStream ps = new PrintStream(os, true);
		String pid = "";

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			ps.println(command);
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.contains(daemon) && !line.contains("awk ")) {
					pid = line.split("\\s+")[1];
				}
			}
		}
		LOGGER.debug(" exit status - " + channel.getExitStatus() + ", daemon = " + daemon + ", PID = " + pid);

		if (channel != null && channel.isConnected()) {
			channel.disconnect();
		}
		if (session != null && session.isConnected()) {
			session.disconnect();
		}
		return pid;
	}
	
}
