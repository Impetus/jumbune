package org.jumbune.remoting.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemoterUtility;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * The Class CommandAsObjectResponser.
 */
public class CommandAsObjectResponser extends SimpleChannelInboundHandler<CommandWritable> {

	private static final String HADOOP_JOB_COMPLETED = "Hadoop#Job@Completed......";
	private static final String DONE_VERSION_1 = "done/version-1";
	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandAsObjectResponser.class);
	private static final String LOG_DIR_SUFFIX = "000000";
	

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
	 * @param command
	 *            the command
	 * @return the object
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Object performAction(final CommandWritable commandWritable) throws JSchException, IOException {
		LOGGER.debug("going to perform action");
		List<Command> commandList = commandWritable.getBatchedCommands();
		for(Command command : commandList){	
			String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
			ApiInvokeHintsEnum apiInvokeHint = commandWritable.getApiInvokeHints();
			
			//checks if command uses some api invoke hint
			if(apiInvokeHint!=null){
				switch(apiInvokeHint){
					case GET_JOB_LOG_FILE_OP:
						String[] strArr = command.getCommandString().split(RemotingConstants.SINGLE_SPACE);
						return getJobHistoryFilefromJobID(strArr[0], strArr[1]);
					case GET_HADOOP_CONFIG:
						String[] strTmp = command.getCommandString().split(RemotingConstants.SINGLE_SPACE);
						return getHadoopConfigFilefromJobID(strTmp[0], strTmp[1]);
					case HOST_TO_IP_OP:
						return convertHostNameToIP(command.getCommandString());
					case JOB_EXECUTION:
						return processJob(command.getCommandString(), agentHome);
					case GET_FILES:
					File file = processGetFiles(command, agentHome);
						return file.list();
					case DB_DOUBLE_HASH:
						return processDBOptSteps(command.getCommandString());
				}
			}
			else{
				if (commandWritable.isAuthenticationRequired()) {
					// execute with jsch
					return executeCommandWithJsch(commandWritable, command);
					
				}else if (command.isHasParams()) {
					String[] commands = command.getCommandString().split(RemotingConstants.REGEX_FOR_PIPE_DELIMITED);
					checkValidParams(commands);
					String[] params = (String[]) command.getParams().toArray();
					return this.executeCommand(params, commands[1], commands[2]);
				}else {
					//execute with process builder
					return execute(command.getCommandString().split(RemotingConstants.SINGLE_SPACE));
				}
			}
		}
		LOGGER.debug("returning back default");
		return commandWritable;
	}

	private void checkValidParams(String[] commands) {
		if (commands.length != RemotingConstants.THREE) {
			throw new IllegalArgumentException("Invalid params received from Remoter!!!");
		}
	}

	private File processGetFiles(Command command, String agentHome) {
		File file = new File(command.getCommandString().replace(RemotingConstants.AGENT_HOME, agentHome).trim());
		if (!file.isDirectory()) {
			throw new  IllegalArgumentException("Not a directory!!!");
		}
		return file;
	}

	/**
	 * Execute command with param.
	 * 
	 * @param commandWritable
	 *            the message
	 * @param command2 
	 * @return the object
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Object executeCommandWithJsch(final CommandWritable commandWritable, Command commandObj) throws JSchException, IOException {

		String sReturnValue = null;
		Session session = null;
		try {
			String lineBreaker = null;
			String command = commandObj.getCommandString();
			String user = commandWritable.getUsername();
			String rsaFile = commandWritable.getRsaFilePath();
			String dsaFile = commandWritable.getDsaFilePath();
			String host = commandWritable.getMasterHostname();
			if (host == null) {
				host = commandWritable.getSlaveHost();
			}
			session = JschUtil.createSession(user, host, rsaFile, dsaFile);
			if ((lineBreaker = extractLineBreaker(command)) != null) {
				sReturnValue = JschUtil.executeCommandUsingShell(session, command + RemotingConstants.DOUBLE_NEWLINE, lineBreaker);
			} else {
				Channel ch = JschUtil.getChannelWithResponse(session, command);
				InputStream in = ch.getInputStream();
				ch.connect();
				sReturnValue = converToString(in);
			}
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
		LOGGER.debug("command response :: [" + sReturnValue + "]");
		return sReturnValue;
	}

	/***
	 * This command find out the stop word from the given command.
	 * 
	 * @param command
	 * @return stop word for the command
	 */
	private String extractLineBreaker(String command) {
		String lineBreaker = null;
		if (command.contains("echo $AGENT")) {
			lineBreaker = "$ echo $AGENT_HOME";
		} else if (command.contains("echo $HADOOP")) {
			lineBreaker = "$ echo $HADOOP_HOME";
		} else if (command.contains("free -m")) {
			lineBreaker = "Swap:";
		}
		return lineBreaker;
	}

	/***
	 * This method takes command for data validation at agent side and execute it.
	 * 
	 * @param message
	 * @return
	 */
	private String processJob(String message, String agentHome) throws IOException{
		String msgToExec = message.replaceAll("AGENT_HOME", agentHome);
		msgToExec = msgToExec.replaceAll("HADOOP_HOME", RemoterUtility.getHadoopHome());
		return executeJob(msgToExec.split(" "));
	}

	private String processDBOptSteps(String command) throws IOException {
		
		String arr[] = command.split("[-]");
		String absLocation = arr[2];
		String response = JschUtil.execSlaveCleanUpTask(new String[] { "ssh", arr[1], "ls", "-1", absLocation }, null);
		String[] s = response.split("\n");
		for (String string : s) {
			if (!string.contains("mrChain")) {
				response = JschUtil.execSlaveCleanUpTask(new String[] { "ssh", arr[1], "zip", "-j", absLocation + string + ".zip",
						absLocation + string }, null);
				JschUtil.execSlaveCleanUpTask(new String[] { "ssh", arr[1], "rm", absLocation + string }, null);
			}
		}
		LOGGER.info("Cleaned Debugger log files on slaves");
		return "Done";
	}


	/**
	 * Execute command in remote machine.
	 * 
	 * @param commands
	 *            array of command to be execute on the remote machine
	 * @return the string response of the command in string format
	 */
	private static String execute(String... commands) throws IOException{
		ProcessBuilder pb = new ProcessBuilder(commands);
		String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
		pb.directory(new File(agentHome));
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
		}  finally {
				if (br != null) {
					br.close();
				}
		}
		LOGGER.debug("Executed command ["+Arrays.toString(commands) +"], sending back response ["+sb.toString()+"]");
		return sb.toString();
	}

	/**
	 * Execute.
	 * 
	 * @param commands
	 *            the commands
	 * @param jarLocation
	 *            the jar location
	 * @param dirLocation
	 *            the dir location
	 * @return the string
	 */
	private String executeCommand(String[] commands, String jarLocation, String dirLocation) throws IOException{
		String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
		if (!agentHome.endsWith("/")) {
			agentHome += "/";
		}
		String hadoopHome=RemoterUtility.getHadoopHome();
		for(int i=0;i<commands.length;i++){
			commands[i]=commands[i].replaceAll("AGENT_HOME", agentHome);
			commands[i]=commands[i].replaceAll("HADOOP_HOME", hadoopHome);	
		}
		String jobJarAbsolutePath = agentHome + jarLocation;
		File fileLoc = new File(jobJarAbsolutePath);
		if(!commands[2].contains(RemotingConstants.DATA_VALIDATION_JAR)){
			String[] fileList = fileLoc.list();
			String jarName = null;
			for (String filename : fileList) {
				if(filename.contains(".jar")){
					jarName = filename;
					break;
				}
			}
			commands[2] = jobJarAbsolutePath + "/" + jarName;
		}
		ProcessBuilder pb = new ProcessBuilder(commands);
		File loc = new File(agentHome + dirLocation);
		if (!loc.exists()) {
			loc.mkdir();
		}	
		pb.directory(loc);
		pb.redirectErrorStream(true);
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				sb.append(convertToString(is,dirLocation));
			}
		} finally {
				if (br != null) {
					br.close();
				}
		}
		LOGGER.debug("Executed command ["+Arrays.toString(commands) +"], sending back response ["+sb.toString()+"]");
		return sb.toString();
		
	}

	private String executeJob(String[] commands) throws IOException {
		String hadoopHome = System.getenv("HADOOP_HOME");
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(new File(hadoopHome));
		pb.redirectErrorStream(true);
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				sb.append(converToString(is));
			}
		}finally {
				if (br != null) {
					br.close();
				}
		}
		LOGGER.debug("Received Command - "+Arrays.toString(commands));
		LOGGER.debug("Executed command response "+sb);
		return sb.toString();
	}



	/**
	 * takes inputsteam and convert into the string format.
	 * 
	 * @param in
	 *            the input stream which user wants to convert
	 * @return the string
	 * 
	 * @throws IOException
	 */
	private String converToString(InputStream in) throws IOException {

		StringBuilder sb = new StringBuilder();
		if (in != null) {
			try {
				int charByte;
				while ((charByte = in.read()) != -1) {
					sb.append((char) charByte);
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
		return sb.toString();
	}
	/**
	 * takes inputsteam and convert into the string format.
	 * 
	 * @param in
	 *            the input stream which user wants to convert
	 * @return the string
	 * 
	 * @throws IOException
	 */
	private String convertToString(InputStream in,String jobDirectory) throws IOException {
		String agentHome = System.getenv(RemotingConstants.AGENT_HOME);
		if (!agentHome.endsWith("/")) {
			agentHome += "/";
		}
		String [] jobDirs=jobDirectory.split("/");
		String jobJarAbsolutePath = agentHome + jobDirs[0]+"/"+jobDirs[1]+"/executedHadoopJob.info";
		StringBuilder sb = new StringBuilder();
		BufferedReader bufferReader=new BufferedReader(new InputStreamReader(in));
		String line=null;
		File file =new File(jobJarAbsolutePath);
		if(file.exists()){
			file.delete();
		}
		FileWriter fileWriter=new FileWriter(file,true);
		BufferedWriter bw=new BufferedWriter(fileWriter);
		try{
		while((line=bufferReader.readLine())!=null){
			sb.append(line+"\n");
			bw.write(line+"\n");
			bw.flush();	
		}
		bw.write(HADOOP_JOB_COMPLETED);
		bw.flush();
		}catch(IOException io){
			LOGGER.error(io);
		}finally{
			if(bw!=null){
				bw.close();
			}
			if(bufferReader!=null){
				bufferReader.close();
			}
		}
		return sb.toString();
	}

	/**
	 * Gets the job history file given a job Id
	 * 
	 * @param jobID
	 * @param jobHistoryDir
	 * @return
	 */
	private String getJobHistoryFilefromJobID(String jobID, String jobHistoryDir) {
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
	
	/**
	 * Gets the config file given a job Id
	 * 
	 * @param jobID
	 * @param jobHistoryDir
	 * @return
	 */
	private String getHadoopConfigFilefromJobID(String jobID, String jobHistoryDir) {
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
	 * This method iterate over list of files and return absolute path of specific file which having desired JOBID.
	 * @param fileList
	 * @param jobID
	 * @return
	 */
	private String iterateFileListAndGetAbsolutePath(List<File> fileList, String jobID) {
		String fileName = null;
		for (File f : fileList) {
			fileName = f.getName();
			if ((fileName.contains(jobID)) && (!fileName.endsWith("crc")) && (!fileName.endsWith("xml"))) {
				return f.getAbsolutePath();
			}
		}
		return null;

	}
	
	/***
	 * This method iterate over list of files and return absolute path of config file which having desired JOBID.
	 * @param fileList
	 * @param jobID
	 * @return
	 */
	private String iterateFileListAndGetConfigFile(List<File> fileList, String jobID) {
		String fileName = null;
		for (File f : fileList) {
			fileName = f.getName();
			if ((fileName.contains(jobID)) && fileName.endsWith("xml")) {
				return f.getAbsolutePath();
			}
		}
		return null;

	}

	private String convertHostNameToIP(String hostName) throws UnknownHostException {
		InetAddress addr = InetAddress.getByName(hostName);
		return addr.getHostAddress();
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

