package org.jumbune.remoting.server.invocations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.StringUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.common.command.CommandWritable.Command.SwitchedIdentity;
import org.jumbune.remoting.server.HadoopConfigurationPropertyLoader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
 * The Class CommandDelegatorMethods. Its the container for the methods to be invoked from {@link org.jumbune.remoting.server.CommandAsObjectResponser} class. 
 * All the methods of this class are invoked using an instance of {@link org.jumbune.remoting.common.RemotingMethodInvocationUtil}
 */
// methods are never invoked locally.
public class CommandAsObjectResponserMethods {

	private static final String DONE_VERSION_1 = "done/version-1";
	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandAsObjectResponserMethods.class);
	private static final String LOG_DIR_SUFFIX = "000000";
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

	/**
	 * Gets the job history file given a job Id
	 * Used by reflection, see RemotingMethodConstants for usage
	 * @param jobID
	 * @param jobHistoryDir
	 * @return
	 */
	public String getJobHistoryFilefromJobID(Command command) {
		String[] strArr = command.getCommandString().split(RemotingConstants.SINGLE_SPACE);
		String jobID=strArr[0];
		String jobHistoryDir=strArr[1];
		long timestamp = 0, latestTimestamp = 0;
		String name, jobDir = null, jobHistoryFile = null;
		File file = new File(jobHistoryDir);
		String[] nameAttribs;
		File[] files = file.listFiles();
		List<File> fileList = Arrays.asList(files);
		// check if need to modify path.Not required for older versions of hadoop.
		if (jobHistoryDir.contains(DONE_VERSION_1)) {
			for (File f : fileList) {
				name = f.getName();
				nameAttribs = name.split("_");
				timestamp = Long.parseLong(nameAttribs[nameAttribs.length - 1]);
				if (timestamp > latestTimestamp) {
					latestTimestamp = timestamp;
					jobDir = name;
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String currentDate = dateFormat.format(date);
			String[] dateArr = currentDate.split("-");
			// preparing the absolute path to job history log file
			StringBuffer sb = new StringBuffer(jobHistoryDir);
			sb.append(File.separator).append(jobDir).append(File.separator).append(dateArr[0]).append(File.separator).append(dateArr[1])
					.append(File.separator).append(dateArr[2]).append(File.separator).append(LOG_DIR_SUFFIX);
			file = new File(sb.toString());
			files = file.listFiles();
			fileList = Arrays.asList(files);
			jobHistoryFile = iterateFileListAndGetAbsolutePath(fileList, jobID);

		} else {
			jobHistoryFile = iterateFileListAndGetAbsolutePath(fileList, jobID);
		}
		return jobHistoryFile;
	}
	
	/***
	 * This method iterate over list of files and return absolute path of specific file which having desired JOBID.
	 * @param fileList
	 * @param jobID
	 * @return
	 */
	public String iterateFileListAndGetAbsolutePath(List<File> fileList, String jobID) {
		String fileName = null;
		for (File f : fileList) {
			fileName = f.getName();
			if ((fileName.contains(jobID)) && (!fileName.endsWith("crc")) && (!fileName.endsWith("xml"))) {
				return f.getAbsolutePath();
			}
		}
		return null;

	}
	
	public List<String> processGetFiles(Command command) {
		File file = new File(command.getCommandString().trim());
		if (!file.isDirectory()) {
			throw new  IllegalArgumentException("Not a directory!!!");
		}
		return Arrays.asList(file.list());
	}

	/**
	 * Used by reflection, see RemotingMethodConstants for usage
	 * @param command
	 * @return
	 * @throws IOException
	 */
	public String processDBOptSteps(Command command) throws IOException {
		String commandString=command.getCommandString();
		String workingUser = JschUtil.getCommandAppender(command.getSwitchedIdentity());
		String arr[] = commandString.split("[-]");
		String absLocation = arr[2];
		String response ;
		if(command.getSwitchedIdentity().getPrivatePath() != null && command.getSwitchedIdentity().getPrivatePath().endsWith(".pem")){
		response = JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh -i "+command.getSwitchedIdentity().getPrivatePath(), arr[1], "ls", "-1", absLocation }, null);
		}else{
		response = JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh", arr[1], "ls", "-1", absLocation }, null);
		}
		String[] s = response.split("\n");
		for (String string : s) {
			if (!string.contains("mrChain")) {
				if(command.getSwitchedIdentity().getPrivatePath() != null && command.getSwitchedIdentity().getPrivatePath().endsWith(".pem")){
				response = JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh -i " +command.getSwitchedIdentity().getPrivatePath(), arr[1], "zip", "-j", absLocation + string + ".zip",
						absLocation + string }, null);}
				else{
					response = JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh", arr[1], "zip", "-j", absLocation + string + ".zip",
							absLocation + string }, null);
				}
				if(command.getSwitchedIdentity().getPrivatePath() != null && command.getSwitchedIdentity().getPrivatePath().endsWith(".pem")){
				JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh -i " +command.getSwitchedIdentity().getPrivatePath(), arr[1], "rm", absLocation + string }, null);
				}else{
					JschUtil.execSlaveCleanUpTask(new String[] {workingUser+"ssh", arr[1], "rm", absLocation + string }, null);
				}
			}
		}
		LOGGER.debug("Cleaned Debugger log files on slaves");
		return "Done";
	}
	

	/**
	 * Gets the config file given a job Id
	 * Used by reflection, see RemotingMethodConstants for usage
	 * @param jobID
	 * @param jobHistoryDir
	 * @return
	 */
	public String getHadoopConfigFilefromJobID(Command command) {
		String[] strTmp = command.getCommandString().split(RemotingConstants.SINGLE_SPACE);
		String jobID=strTmp[0];
		String jobHistoryDir=strTmp[1];
		long timestamp = 0, latestTimestamp = 0;
		String name, jobDir = null, configFile = null;
		File file = new File(jobHistoryDir);
		String[] nameAttribs;
		File[] files = file.listFiles();
		List<File> fileList = Arrays.asList(files);

		// check if need to modify path.Not required for older versions of hadoop.
		if (jobHistoryDir.contains(DONE_VERSION_1)) {
			for (File f : fileList) {
				name = f.getName();
				nameAttribs = name.split("_");
				timestamp = Long.parseLong(nameAttribs[nameAttribs.length - 1]);
				if (timestamp > latestTimestamp) {
					latestTimestamp = timestamp;
					jobDir = name;
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String currentDate = dateFormat.format(date);
			String[] dateArr = currentDate.split("-");
			// preparing the absolute path to job history log file
			StringBuffer sb = new StringBuffer(jobHistoryDir);
			sb.append(File.separator).append(jobDir).append(File.separator).append(dateArr[0]).append(File.separator).append(dateArr[1])
					.append(File.separator).append(dateArr[2]).append(File.separator).append(LOG_DIR_SUFFIX);
			file = new File(sb.toString());
			files = file.listFiles();
			fileList = Arrays.asList(files);
			configFile = iterateFileListAndGetConfigFile(fileList, jobID);

		} else {
			configFile = iterateFileListAndGetConfigFile(fileList, jobID);
		}
		return configFile;
	}
	
	/***
	 * This method iterate over list of files and return absolute path of config file which having desired JOBID.
	 * @param fileList
	 * @param jobID
	 * @return
	 */
	public String iterateFileListAndGetConfigFile(List<File> fileList, String jobID) {
		String fileName = null;
		for (File f : fileList) {
			fileName = f.getName();
			if ((fileName.contains(jobID)) && fileName.endsWith("xml")) {
				return f.getAbsolutePath();
			}
		}
		return null;

	}
	
	/**
	 * Used by reflection, see RemotingMethodConstants for usage
	 * @param command
	 * @return
	 * @throws UnknownHostException
	 */
	public String convertHostNameToIP(Command command) throws UnknownHostException {
		String hostName=command.getCommandString();
		InetAddress addr = InetAddress.getByName(hostName);
		return addr.getHostAddress();
	}

	public String getDaemonProcessId(Command command) {
		String[] commandStringArr = command.getCommandString().split("\\s+");
		String host = commandStringArr[0];
		String daemonName = commandStringArr[1];
		String pid = null;
		Session session = null;
		try {
			session = JschUtil.createSession(command.getSwitchedIdentity(), host, CommandType.FS);
			//begin mapr code changes
			HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
			String hadoopDistribution = hcpl.getDistributionType();
			boolean isMapr = RemotingConstants.MAPR.equalsIgnoreCase(hadoopDistribution);
			if(isMapr && (!(RemotingConstants.DATANODE.equals(daemonName))||!(RemotingConstants.NAMENODE.equals(daemonName)))){
				pid = JschUtil.getDaemonProcessId(session, daemonName);
			}else{
				pid = JschUtil.getDaemonProcessId(session, daemonName);
			}
			//end mapr code changes
		} catch (JSchException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return pid;
	}
	
	
	public String getMaxUtilisationMemoryFromAwkCommand(Command command) {
		Session session =null;
		String response = null;
		try{
		List <String> host = command.getParams();
		String hostName = host.get(0);
		String commandString = command.getCommandString();
		String workingUser = JschUtil.getCommandAppender(command.getSwitchedIdentity());
		commandString = workingUser + commandString ;
		session = JschUtil.createSession(command.getSwitchedIdentity(), hostName, command.getCommandType());
		//How we will handle the cases where the container sizes are in MBs, say 512MB
		response = JschUtil.executeCommandUsingShell(session, commandString, "GB");
		}catch(JSchException e){
			LOGGER.debug("Error in creating Session through Jsch",e);
		}catch (IOException e){
			LOGGER.debug("Error in getting MaxUtilisation through Awk Command",e);
		}
		finally{
		if(session!=null){
			session.disconnect();
		}
		}
		return response;
	}

	
	/**
	 * Gets the hadoop cluster time millis.
	 *
	 * @param command the command
	 * @return the hadoop cluster time millis
	 */
	public long getHadoopClusterTimeMillis(Command command) {
		return System.currentTimeMillis();
	}
	
	/**
	 * Called from class:HadoopLogParser(method:checkAndgetCurrentLogFilePathForYarn)
	 * @param command
	 * @throws JSchException
	 * @throws IOException
	 */
	public void executeRemoteCommandAsSudo(Command command) throws JSchException, IOException
	{
	    Session session = null;
	    Channel channel = null;
	    String host = RemotingConstants.LOCALHOST;
	    SwitchedIdentity switchedIdentity = command.getSwitchedIdentity();
		CommandType commandType = command.getCommandType();
	    try {
			session = createSession(switchedIdentity, host, commandType);
	        channel = session.openChannel("exec");
	        String sudoCommand = "sudo su - " + switchedIdentity.getWorkingUser();
	        ((ChannelExec) channel).setCommand(sudoCommand);
	        ((ChannelExec) channel).setPty(true);
	        channel.connect();
	        InputStream inputStream = channel.getInputStream();
	        OutputStream out = channel.getOutputStream();
	        ((ChannelExec) channel).setErrStream(System.err);
	        out.write((StringUtil.getPlain(switchedIdentity.getPasswd()) + "\n").getBytes());
	        out.flush();
	        Thread.sleep(100);
			LOGGER.debug("Now Sending Command ["+command.getCommandString()+"]");
	        out.write((command.getCommandString() + "\n").getBytes());
	        out.flush();
	        Thread.sleep(100 * 100);
	        out.write(("logout" + "\n").getBytes());
	        out.flush();
	        Thread.sleep(100);
	        logJsch(channel, inputStream);
	        out.write(("exit" + "\n").getBytes());
	        out.flush();
	        out.close();
	    } catch (Exception ex) {
	        LOGGER.error(ex);
	    } finally {
	    	if(session!=null){
				session.disconnect();
	    	}
	    	if(channel!=null){
	    		channel.disconnect();
	    	}
	    }
	}
	
	private static void logJsch(Channel channel, InputStream in) 
	{
	    try {
	        byte[] tmp = new byte[1024];
	        while (true) {
/*	            while (in.available() > 0) {
	                int i = in.read(tmp, 0, 1024);
	                if (i < 0)
	                    break;
	                LOGGER.debug(new String(tmp, 0, i));
	            }
*/	            if (channel.isClosed()) {
	            	LOGGER.debug("exit-status: " + channel.getExitStatus());
	                break;
	            }
	        }
	    } catch (Exception ex) {
	    	LOGGER.error(ex);
	    }
	}
	
	
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
	private Session createSession(CommandWritable.Command.SwitchedIdentity switchedIdentity, String host, CommandType commandType) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		if(switchedIdentity.getPrivatePath() != null && !switchedIdentity.getPrivatePath().isEmpty()){
			jsch.addIdentity(switchedIdentity.getPrivatePath());	
		}
		java.util.Properties conf = new java.util.Properties();
		session = jsch.getSession(switchedIdentity.getUser(), host, RemotingConstants.TWENTY_TWO);
		if(switchedIdentity.getPasswd()!=null){
			try{
				session.setPassword(StringUtil.getPlain(switchedIdentity.getPasswd()));
			}catch(Exception e){
				LOGGER.error("Failed to Decrypt the password", e);
			}
		}
		conf.put(STRICT_HOST_KEY_CHECKING, "no");
		session.setConfig(conf);
	    session.connect();
		LOGGER.debug("Session Established, for user ["+switchedIdentity.getUser()+"]"+":["+session.isConnected()+"]");
		return session;
	}

}
