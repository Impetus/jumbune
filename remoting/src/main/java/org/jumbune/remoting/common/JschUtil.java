package org.jumbune.remoting.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.server.HadoopConfigurationPropertyLoader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


/**
 * The Class Jschutil is an utility class for handling the all channel related operations 
 */
public final class JschUtil {
	
	private static final Logger LOGGER=LogManager.getLogger(JschUtil.class);
	/**
	 * Instantiates a new jsch util.
	 */
	private JschUtil() {
	}
	
	/** The Constant STRICT_HOST_KEY_CHECKING. */
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	private static final String NEW_LINE = "\n";
	
	/**
	 * The logger.
	 *
	 * @param user the user
	 * @param host the host
	 * @param rsaFilePath the rsa file path
	 * @param dsaFilePath the dsa file path
	 * @return the session
	 * @throws JSchException the j sch exception
	 */

	/**
	 * For creating a JSCH session.
	 * 
	 * @param user
	 *            the user
	 * @param host
	 *            the host
	 * @param rsaFilePath
	 *            the rsa file path
	 * @param dsaFilePath
	 *            the dsa file path
	 * @return a JSCH session
	 * @throws JSchException
	 *             the j sch exception
	 */
	public static Session createSession(String user, String host, String rsaFilePath, String dsaFilePath, CommandType commandType) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
	
		SwitchedIdentity switchedIdentity = changeIdentityAsPerCommand(user, rsaFilePath, dsaFilePath, commandType);
		if(commandType.equals(CommandType.FS)){
			jsch.addIdentity(switchedIdentity.getPrivatePath());
		}
		java.util.Properties conf = new java.util.Properties();
		session = jsch.getSession(switchedIdentity.getUser(), host, RemotingConstants.TWENTY_TWO);
		UserInfo info = getSwitchedUser(commandType, switchedIdentity);
		session.setUserInfo(info);
		conf.put(STRICT_HOST_KEY_CHECKING, "no");
		session.setConfig(conf);
		LOGGER.debug("Session Established, for user ["+switchedIdentity.getUser()+"]");
		return session;
	}
	
	public static boolean verifyPassword(String user, String encryptedPasswrd) throws JSchException {
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
	
	private static UserInfo getSwitchedUser(CommandType commandType, SwitchedIdentity switchedIdentity){
		UserInfo info;
		if(commandType.equals(CommandType.HADOOP_FS)){
			info = new JumbuneUserInfo(StringUtil.getPlain(switchedIdentity.getPasswd()));
		}else if(commandType.equals(CommandType.HADOOP_JOB)){
			info = new JumbuneUserInfo(StringUtil.getPlain(switchedIdentity.getPasswd()));
		}else if(commandType.equals(CommandType.MAPRED)){
			info = new JumbuneUserInfo(StringUtil.getPlain(switchedIdentity.getPasswd()));
                }else{
			info = new JumbuneUserInfo();
		}
		return info;
	}
	
	private static SwitchedIdentity changeIdentityAsPerCommand(String user, String rsaFilePath, String dsaFilePath, CommandType commandType){
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
		SwitchedIdentity switchedIdentity = new SwitchedIdentity();
		if(commandType.equals(CommandType.HADOOP_FS)){
			switchedIdentity.setUser(hcpl.getHdfsUser());
			setPasswd(switchedIdentity, hcpl.getHdfsPasswd());
		}else if(commandType.equals(CommandType.HADOOP_JOB)){
			switchedIdentity.setUser(hcpl.getYarnUser());
			setPasswd(switchedIdentity, hcpl.getYarnPasswd());
                }else if(commandType.equals(CommandType.MAPRED)){
			switchedIdentity.setUser(hcpl.getMapredUser());
			setPasswd(switchedIdentity, hcpl.getMapredPasswd());		
		}else{
			switchedIdentity.setUser(user);
			String privateKeyFilePath;
			if(rsaFilePath!= null && !"".equals(rsaFilePath)){
				privateKeyFilePath = rsaFilePath;
			}else{
				privateKeyFilePath = dsaFilePath;
			}
			switchedIdentity.setPrivatePath(privateKeyFilePath);
		}
		LOGGER.warn("For commandType: "+ commandType+ " Switched Identity to: "+ switchedIdentity);
		return switchedIdentity;
	}
		
	private static void setPasswd(SwitchedIdentity switchedIdentity, String passwd){
		if(passwd!= null && !"".equals(passwd)){
				switchedIdentity.setPasswd(passwd);
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
	public static Channel getChannel(Session session, String command) throws JSchException, IOException {
		session.connect();
		LOGGER.debug("Session ["+session+"] connected");		
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
		LOGGER.debug("Channel connected, mode [exec]");
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
			LOGGER.info("setting pty for sudo");
			((ChannelExec) channel).setPty(true);
			LOGGER.info("after setting pty for sudo");
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
		ProcessBuilder pb = new ProcessBuilder(commands);
		String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
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

		String passwd;

		JumbuneUserInfo(){
		}
		
		JumbuneUserInfo(String passwd){
		  this.passwd = passwd;	
		}
		/**
		 * gets the password
		 */
		public String getPassword() {
			return passwd;
		}

		/**
		 * set prompt YES/NO
		 */
		public boolean promptYesNo(String str) {
			return true;
		}

		/**
		 * gets the passphrase
		 */
		public String getPassphrase() {
			return null;
		}

		/**
		 * set the passphrase
		 */
		public boolean promptPassphrase(String message) {
			return true;
		}

		/**
		 * prompt for password
		 */
		public boolean promptPassword(String message) {
			return true;
		}

		/**
		 * set the message
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
	public static Channel getShellChannel(Session session, String command) throws JSchException, IOException {
		session.connect();
		return session.openChannel("shell");
	}
	/***
	 * executes commands from shell
	 * 
	 * @param session
	 * @param simpleCommand
	 * @param lineBreaker It is a small phrase for stoping the iteration of loop while checking response of executed command.
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public static String executeCommandUsingShell(Session session, String simpleCommand, String lineBreaker) throws JSchException, IOException {
		String response = null;
		ChannelShell channelShell = null;
		BufferedReader brIn = null;
		DataOutputStream dataOut = null;

		try {
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.connect();
			brIn = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
			dataOut = new DataOutputStream(channelShell.getOutputStream());
			dataOut.writeBytes(simpleCommand);
			dataOut.flush();
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = brIn.readLine()) != null) {
				stringBuilder.append(line + NEW_LINE);
				if (line.contains(lineBreaker)) {
					if (lineBreaker.contains("echo")) {
						stringBuilder.setLength(0);
						stringBuilder = new StringBuilder(brIn.readLine());
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

	private static class SwitchedIdentity{
		private String user;
		
		private String passwd;
		
		private String privatePath;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPasswd() {
			return passwd;
		}

		public void setPasswd(String passwd) {
			this.passwd = passwd;
		}

		public String getPrivatePath() {
			return privatePath;
		}

		public void setPrivatePath(String privatePath) {
			this.privatePath = privatePath;
		}

		@Override
		public String toString() {
			return "SwitchedIdentity [user=" + user + ", passwd=" + passwd
					+ ", privatePath=" + privatePath + "]";
		}

	}
}
