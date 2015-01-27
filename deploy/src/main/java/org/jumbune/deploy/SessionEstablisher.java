package org.jumbune.deploy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * This class is used to establish connection during deployment
 *
 */
public final class SessionEstablisher {

	static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME \n \n";
	private static final String SCP_COMMAND = "scp -f ";
	private static final Logger LOGGER = LogManager.getLogger("EventLogger");
	public static final String WHERE_IS_HADOOP = "whereis hadoop";
	private static byte[] bufs;
	public static final String LS_PREFIX_PART = "ls ";
	public static final String LS_POSTFIX_PART = " -Rl | grep :";
	private static final String HADOOP_VERSION_YARN_COMMAND = "bin/yarn version";
	private static final String HADOOP_VERSION_NON_YARN_COMMAND = "bin/hadoop version";
	public static final String LL_COMMAND = "ll /usr/bin/hadoop \n";
	private Deployer deployerInstance;
	private static List<String> jars = new ArrayList<String>(2);
	
	static {
		jars.add("/lib/log4j-api-2.0.jar");
		jars.add("/lib/log4j-core-2.0.jar");
	}
	public SessionEstablisher(Deployer deployer){
		this.deployerInstance = deployer;
	}
	
	/**
	 * method for establishing connection while authentication
	 * @param username
	 * @param namenodeIP
	 * @param nnpwd
	 * @param privateKeyPath
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Session establishConnection(String username, String namenodeIP, String nnpwd, String privateKeyPath) {
		JSch jsch = new JSch();
		Session session = null;
		try {

			session = jsch.getSession(username, namenodeIP, Constants.TWENTY_TWO);
		} catch (JSchException e) {
			LOGGER.error(e);
		}
		if(nnpwd!=null){
		session.setPassword(nnpwd);
		}
		UserInfo info = new JumbuneUserInfo();
		try {
			jsch.addIdentity(privateKeyPath);
		} catch (JSchException e) {
			LOGGER.error(e);
		}
		session.setUserInfo(info);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		if(nnpwd!=null){
		config.put("PreferredAuthentications", "password");
		}
		session.setConfig(config);
		try {
			session.connect();
		} catch (JSchException e) {
			LOGGER.error("Failed to authenticate, check username and password");
		}
		return session;
	}

	/**
	 * method for copying hadoop jars from namenode
	 * @param session
	 * @param username
	 * @param namenodeIP
	 * @param hadoopHome
	 * @param destinationAbsolutePath
	 * @param listOfFiles
	 * @throws JSchException
	 * @throws IOException
	 */
	public static void fetchHadoopJarsFromNamenode(Session session, String username, String namenodeIP, String hadoopHome, String destinationAbsolutePath, String hadoopDistributionType, String distributionType, String... files)
			throws JSchException, IOException {
		new File(destinationAbsolutePath).mkdirs();
		Deployer deployer = DeployerFactory.getDeployer(distributionType,hadoopDistributionType);
		String versionCommand = null ;
		String versionNumber = null ;
		if(!hadoopDistributionType.equalsIgnoreCase("m")){
		if (hadoopDistributionType.equalsIgnoreCase("c")
				|| hadoopDistributionType.equalsIgnoreCase("h")) {
			versionCommand = File.separator + "usr" + File.separator + HADOOP_VERSION_YARN_COMMAND;
		}else if(distributionType.equalsIgnoreCase("Non-Yarn")){
			versionCommand = hadoopHome + File.separator + HADOOP_VERSION_NON_YARN_COMMAND;
		} else {
			versionCommand = hadoopHome + File.separator
					+ HADOOP_VERSION_YARN_COMMAND;
		}
		String versionResponse = executeCommand(session, versionCommand);
		 versionNumber = getVersionNumber(versionResponse);
		}
		String[] listOfFiles = deployer.getRelativePaths(versionNumber);
		LOGGER.info("Syncing Jars from Hadoop to Jumbune...");
		for (String fileName : listOfFiles) {
			String command = SCP_COMMAND + hadoopHome + fileName;
			copyRemoteFile(session, command, destinationAbsolutePath);
		}
		LOGGER.info("Done.");		
	}

	/**
	 * Gets the version number after parsing the response from the hadoop version commands.
	 *
	 * @param versionResponse the version response
	 * @return the version number
	 */
	private static String getVersionNumber(String versionResponse) {
		if(versionResponse!=null){
		String[] versionResponseSplits = versionResponse.split("Subversion");
		String[] hadoopSplit = versionResponseSplits[0].split(" ");
		return hadoopSplit[1].trim();
		}
		return null;
	}

	private static void copyRemoteFile(Session session, String command, String fileLocation) throws JSchException, IOException {
		FileOutputStream fos = null;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		channel.connect();
		bufs = new byte[Constants.ONE_ZERO_TWO_FOUR];

		// send '\0'
		bufs[0] = 0;
		out.write(bufs, 0, 1);
		out.flush();

		while (true) {
			int c = checkAck(in);
			if (c != 'C') {
				break;
			}

			// read '0644 '
			in.read(bufs, 0, Constants.FIVE);

			long filesize = 0L;
			while (true) {
				if (in.read(bufs, 0, 1) < 0) {
					// error
					break;
				}
				if (bufs[0] == ' '){
					break;
				}
				filesize = filesize * Constants.TENL + (long) (bufs[0] - '0');
			}

			String file = null;
			for (int i = 0;; i++) {
				in.read(bufs, i, 1);
				if (bufs[i] == (byte) Constants.ZERO_CROSS_ZERO_A) {
					file = new String(bufs, 0, i);
					break;
				}
			}
			
			// send '\0'
			bufs[0] = 0;
			out.write(bufs, 0, 1);
			out.flush();

			// read a content of file
			fos = new FileOutputStream(fileLocation == null ? fileLocation : fileLocation + file);
			filesize = readFile(fos, in, filesize);
			fos.close();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			// send '\0'
			bufs[0] = 0;
			out.write(bufs, 0, 1);
			out.flush();
		}
	}

	private static long readFile(FileOutputStream fos, InputStream in,
			long filesize) throws IOException {
		int foo;
		long tempfilesize=filesize;
		while (true) {
			if (bufs.length < tempfilesize){
				foo = bufs.length;
			}else {
				foo = (int) tempfilesize;
			}
			foo = in.read(bufs, 0, foo);
			if (foo < 0) {
				// error
				break;
			}
			fos.write(bufs, 0, foo);
			tempfilesize -= foo;
			if (tempfilesize == 0L){
				break;
			}
		}
		return tempfilesize;
	}

	/**
	 * Method used to check the input
	 * b may be 0 for success,
 	 * 1 for error,
	 * 2 for fatal error,
	 * -1
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0 || b == -1){
			return b;
		}

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { 
				LOGGER.error(sb.toString());
			}
			if (b == 2) { 
				LOGGER.error(sb.toString());
			}
		}
		return b;
	}


	/**
	 * This method execute the given command over SSH
	 * 
	 * @param session
	 * @param command
	 *            Command to be executed
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	static String executeCommand(Session session, String command) throws JSchException, IOException {
		InputStream in = null;
		Channel channel = null;
		String msg = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();
			msg = validateCommandExecution(channel, in);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		} finally {
			if (in != null) {
				in.close();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
		return msg;
	}

	/**
	 * This method validates the executed command
	 * 
	 * @param channel
	 *            SSH channel
	 * @param in
	 *            input stream
	 * @return Error message if any

	 *             If any error occurred
	 */
	private static String validateCommandExecution(Channel channel, InputStream in) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		byte[] tmp = new byte[Constants.ONE_ZERO_TWO_FOUR];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0,Constants.ONE_ZERO_TWO_FOUR);
				if (i < 0){
					break;
				}
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				break;
			}
			Thread.sleep(Constants.THOUSAND);
		}
		return sb.toString();
	}

	private static class JumbuneUserInfo implements UserInfo {
		
		/**
		 * gets the password
		 */
		public String getPassword() {
			return null;
		}

		/**
		 * set boolean YES/NO
		 */
		public boolean promptYesNo(String str) {
			return true;
		}

		/**
		 * get the passphrase
		 */
		public String getPassphrase() {
			return null;
		}

		/**
		 * set passphrase message
		 */
		public boolean promptPassphrase(String message) {
			return true;
		}

		/**
		 * set the password
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
	
	public static String executeCommandUsingShell(Session session, String simpleCommand,String lineBreaker) throws JSchException, IOException {
		String response = null;
		ChannelShell channelShell = null;
		BufferedReader brIn = null;
		DataOutputStream dataOut = null;

		try {
			channelShell = (ChannelShell) session.openChannel("shell");
			InputStream is = channelShell.getInputStream();
			brIn = new BufferedReader(new InputStreamReader(is));
			dataOut = new DataOutputStream(channelShell.getOutputStream());
			channelShell.setPty(true);
			channelShell.connect();
			dataOut.writeBytes(simpleCommand);
			dataOut.flush();
			String line = null;
			StringBuilder stringBuilder = null;
			while ((line = brIn.readLine()) != null) {
				if (line.contains(lineBreaker)) {
					stringBuilder = new StringBuilder();
					stringBuilder.append(line);
					response = stringBuilder.toString();
					break;
				}
				if((line.contains(session.getUserName())) && (line.trim().endsWith("$") || line.trim().endsWith("#"))){
					break;
			}
			}
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

}
