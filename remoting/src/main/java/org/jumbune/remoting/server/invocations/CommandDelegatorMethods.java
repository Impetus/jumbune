package org.jumbune.remoting.server.invocations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.StringUtil;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.common.command.CommandWritable.Command.SwitchedIdentity;
import org.jumbune.remoting.server.JumbuneAgent;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * The Class CommandDelegatorMethods. Its the container for the methods to be invoked from {@link org.jumbune.remoting.server.CommandDelegator} class. 
 * All the methods of this class are invoked using an instance of {@link org.jumbune.remoting.common.RemotingMethodInvocationUtil}
 */
@SuppressWarnings("unused") // methods are never invoked locally.
public class CommandDelegatorMethods {
	
	private static final Logger LOGGER = LogManager.getLogger(CommandDelegatorMethods.class);
	
	private static final String EXIT_CMD = "exit";
	
	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	
	private static final Object AMPERSAND_DOUBLE = "&&";
	
	private static final Object AMPERSAND_SINGLE = "&";
	
	private static final String JAVA_CMD = "$JAVA_HOME/bin/java -cp $JAVA_HOME/lib/tools.jar:";
	
	private static final String JUMBUNE_JMX_SERVER = "org.jumbune.remoting.jmx.server.JumbuneJMXServer";
	
	private static final String SINGLE_QUOTE = "'";
	
	private static final String BASH_C = "bash -c ";
	
	private static final String JMX_AGENT_LOG = "jmx.log";

	/**
	 * This method transfers jumbune's JMXServer to all the nodes in the cluster.
	 *
	 * @param command the command
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendJumbuneJmxServerToAllNodes(Command command) throws JSchException, IOException {

		String agentFSUser = command.getSwitchedIdentity().getUser();
		// params contain nodes' working directory(absolute path) as first
		// parameter and
		// rest of all parameters are the hosts of different nodes residing in
		// the cluster.
		final List<String> params = command.getParams();
		final String jmxAgentDir = params.get(0);

		if (params.size() < 2) {
			throw new IllegalArgumentException("This command(scp) requires 2 or more command params");
		}
		final StringBuilder jmxJarLocation = new StringBuilder(JumbuneAgent.getAgentDirPath())
				.append(RemotingConstants.LIB_DIR).append(RemotingConstants.JUMBUNE_JMX_JAR);

		StringBuilder scpCommand = new StringBuilder(RemotingConstants.SCP).append(RemotingConstants.SINGLE_SPACE)
				.append(jmxJarLocation).append(RemotingConstants.SINGLE_SPACE).append(agentFSUser)
				.append(RemotingConstants.AT_SIGN);

		// removing duplicates
		Set<String> set = new HashSet<String>();
		for (int i = 1; i < params.size(); i++) {
			set.add(params.get(i));
		}
		params.clear();
		for (String element : set) {
			params.add(element);
		}

		// creating session for agent machine (localhost)
		Session session = null;
		StringBuilder scpCommandForWorker = null;

		for (int i = 0; i < params.size(); i++) {
			session = JschUtil.createSession(command.getSwitchedIdentity(), params.get(i), CommandType.FS);
			JschUtil.executeCommandUsingShell(session, "mkdir -p " + jmxAgentDir + " && exit", "logout");
		}

		// executing scp command for various nodes.
		for (int i = 0; i < params.size(); i++) {
			session = JschUtil.createSession(command.getSwitchedIdentity(), RemotingConstants.LOCALHOST,
					command.getCommandType());
			scpCommandForWorker = new StringBuilder(scpCommand.toString());
			scpCommandForWorker.append(params.get(i)).append(RemotingConstants.COLON).append(jmxAgentDir)
					.append(RemotingConstants.SINGLE_SPACE).append(AMPERSAND_DOUBLE)
					.append(RemotingConstants.SINGLE_SPACE).append(EXIT_CMD);

			JschUtil.executeCommandUsingShell(session, scpCommandForWorker.toString(), "logout");
		}
		LOGGER.debug("Successfully sent JMX agent to all the nodes");
	}
	
	public void shutDownJMXAgents(Command command) throws JSchException, IOException {
		String agentFSUser = command.getSwitchedIdentity().getUser();
		
		// parameters are the hosts of different nodes residing in
		// the cluster.
		final List<String> params = command.getParams();

		// removing duplicates
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < params.size(); i++) {
			set.add(params.get(i));
		}
		params.clear();
		for (String element : set) {
			params.add(element);
		}

		Session session = null;
		StringBuilder scpCommandForWorker = null;
		for (int i = 0; i < params.size(); i++) {
			session = JschUtil.createSession(command.getSwitchedIdentity(), params.get(i), command.getCommandType());
			JschUtil.executeCommandUsingShell(session, "pkill -f JumbuneJMXServer && exit", "logout");
		}

		LOGGER.debug("shutdown JMX Agents on all the nodes");
	}
	
	
	public void establishConnToJMXAgents(Command command) throws JSchException, IOException {

		String agentFSUser = command.getSwitchedIdentity().getUser();
		// params contain nodes' working directory(absolute path) as first
		// parameter and
		// rest of all parameters are the hosts of different nodes residing in
		// the cluster.
		final List<String> params = command.getParams();
		final String jmxAgentDir = params.get(0);
		if (params.size() < 2) {
			throw new IllegalArgumentException("This command "
					+ "($JAVA_HOME/bin/java -cp $JAVA_HOME/lib/tools.jar:/$JMX_JAR_LOCATION/jumbune-jmx-agent.jar "
					+ "org.jumbune.remoting.jmx.server.JumbuneJMXServer) requires 2 or more command params");
		}

		// "bash -c '$JAVA_HOME/bin/java -cp
		// $JAVA_HOME/lib/tools.jar:/home/impadmin/temp//jmx_agent/jumbune-jmx-agent.jar
		// org.jumbune.remoting.jmx.server.JumbuneJMXServer >
		// /home/impadmin/temp/jmx.log&' && exit "

		final StringBuilder javaCommand = new StringBuilder(BASH_C).append(SINGLE_QUOTE).append(JAVA_CMD)
				.append(jmxAgentDir).append(RemotingConstants.JUMBUNE_JMX_JAR).append(RemotingConstants.SINGLE_SPACE)
				.append(JUMBUNE_JMX_SERVER).append(RemotingConstants.SINGLE_SPACE)
				.append(RemotingConstants.REDIRECT_SYMBOL).append(RemotingConstants.SINGLE_SPACE).append(jmxAgentDir)
				.append(JMX_AGENT_LOG).append(AMPERSAND_SINGLE).append(SINGLE_QUOTE)
				.append(RemotingConstants.SINGLE_SPACE).append(AMPERSAND_DOUBLE).append(RemotingConstants.SINGLE_SPACE)
				.append(EXIT_CMD);

		// removing duplicates
		Set<String> set = new HashSet<String>();
		for (int i = 1; i < params.size(); i++) {
			set.add(params.get(i));
		}
		params.clear();
		for (String element : set) {
			params.add(element);
		}

		// session to be created for workers
		Session session = null;
		for (int i = 0; i < params.size(); i++) {
			session = JschUtil.createSession(command.getSwitchedIdentity(), params.get(i), command.getCommandType());
			JschUtil.executeCommandUsingShell(session, javaCommand.toString(), "logout");
		}
		LOGGER.debug("Successfully started JMX Agent on all the nodes");
	}
	
	private void escapeCommandAppender(Command command) throws JSchException, IOException{
		String host = RemotingConstants.LOCALHOST;
		Session session = null;
		CommandType commandType = command.getCommandType();
		try {
			session = JschUtil.createSession(command.getSwitchedIdentity(), host, commandType);
			String commandStr = command.getCommandString();
			if (commandStr.contains(RemotingConstants.AGENT_HOME)) {
				commandStr = commandStr.replace(RemotingConstants.AGENT_HOME, JumbuneAgent.getAgentDirPath());
			}
			//execute command on slaves using shell channel
			if(command.isHasParams()){
				LOGGER.info("Escaped Command Appender and Executed Params ["+commandStr+"]");
				executeCommandWithParams(command, host, session, commandStr);
			}else{
				LOGGER.info("Escaped Command Appender and Executed Jsch ["+commandStr+"]");
				JschUtil.getChannel(session, commandStr);
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
		String line = null;
		Channel channel = JschUtil.getShellChannel(session);
		OutputStream os = channel.getOutputStream();
		ps = new PrintStream(os, true);
		in = channel.getInputStream();
		channel.connect();
		ps.println(commandStr);
		ps.println(EXIT_CMD);
		StringBuilder sb = new StringBuilder();
		if (in != null && in.available()>0) {
			br = new BufferedReader(new InputStreamReader(in));
			line = br.readLine();
			while (line != null) {
				sb.append(line).append(RemotingConstants.NEW_LINE);
				line = br.readLine();
				LOGGER.info("Response line: "+line);
			}
			LOGGER.info("Response ["+sb.toString()+"]");
		}
		}finally{
			if(br != null){
				br.close();
			}
			if(ps != null){
				ps.close();
			}
		}
	}

	/**
	 * Called from class:HadoopLogParser(method:checkAndgetCurrentLogFilePathForYarn),class: ClusterProfilingHelper (method: getJobDetails),Class CopyThread(run method)
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
	        String sudoCommand = "sudo -E su - " + switchedIdentity.getWorkingUser();
	        ((ChannelExec) channel).setCommand(sudoCommand);
	        ((ChannelExec) channel).setPty(true);
	        channel.connect();
	        InputStream inputStream = channel.getInputStream();
	        OutputStream out = channel.getOutputStream();
	        ((ChannelExec) channel).setErrStream(System.err);
	        if(switchedIdentity.getPrivatePath() == null || switchedIdentity.getPrivatePath().isEmpty()){
	        	out.write((StringUtil.getPlain(switchedIdentity.getPasswd()) + "\n").getBytes());
	        }
	        out.flush();
	        Thread.sleep(100);
			LOGGER.debug("Now Sending Command ["+command.getCommandString()+"]");
	        out.write((command.getCommandString() + "\n").getBytes());
	        out.flush();
	        Thread.sleep(100 * 100);
	        out.write(("logout" + "\n").getBytes());
	        out.flush();
	        Thread.sleep(100);
	        out.write(("exit" + "\n").getBytes());
/*			if(channel.getExitStatus()!=0){
				String errorDebug = logJsch(channel, inputStream);
				LOGGER.error("Detailed Debug log for Errored command [" + command.getCommandString() +"]\n ----- \n"+errorDebug+"\n-----");
			}	        
*/	        out.flush();
	        out.close();
	    } catch (Exception e) {
	        LOGGER.error(e.getCause());
	    } finally {
	    	if(session!=null){
				session.disconnect();
	    	}
	    	if(channel!=null){
	    		channel.disconnect();
	    	}
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
		LOGGER.debug("Session Established "+":["+session.isConnected()+"], for user ["+switchedIdentity.getUser()+"]");
		return session;
	}
}