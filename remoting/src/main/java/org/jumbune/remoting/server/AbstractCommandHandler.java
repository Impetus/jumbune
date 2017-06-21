package org.jumbune.remoting.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.ActiveNodeInfo;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.ZKUtils;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.server.jsch.ChannelReaderResponse;
import org.jumbune.remoting.server.jsch.JschExecutor;
import org.jumbune.remoting.server.jsch.JschResponse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class AbstractCommandHandler extends SimpleChannelInboundHandler<CommandWritable>{

	private static final Logger LOGGER = LogManager.getLogger(AbstractCommandHandler.class);

	@Override
	protected abstract void channelRead0(ChannelHandlerContext ctx, CommandWritable msg) throws Exception;	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	  io.netty.channel.Channel ch = ctx.channel();
	  if (cause instanceof TooLongFrameException) {
		  LOGGER.error("Corrupted frame recieved from: " + ch.remoteAddress());
	    return;
	  }

	  if (ch.isActive()) {
	    LOGGER.error("Internal Server Error",cause);
	  }
	}

	protected static com.google.protobuf.Parser<ActiveNodeInfo> PARSER =
            new com.google.protobuf.AbstractParser<ActiveNodeInfo>() {
          public ActiveNodeInfo parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new ActiveNodeInfo(input, extensionRegistry);
          }
     };
     
 	protected void replaceSymbolsWithConfigurationVariable(Command command) {
 		String commandString = command.getCommandString();
		String agentHome = JumbuneAgent.getAgentDirPath();
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
		String hadoopHome = hcpl.getHadoopHome();
		if(command.getCommandType().equals(CommandType.HADOOP_FS) && hcpl.getHadoopHome().contains("lib")){
			hadoopHome = hadoopHome.substring(0,(hadoopHome.length())-1)+"-hdfs" ; 
		}
		command.setCommandString(commandString.replaceAll("HADOOP_HOME", hadoopHome).replaceAll("AGENT_HOME", agentHome));
		List<String> parameters = command.getParams();
		if(parameters!=null){
			for(int i =0; i<parameters.size(); i++){
				String param = parameters.get(i);
				parameters.set(i, param.replaceAll("HADOOP_HOME", hadoopHome).replaceAll("AGENT_HOME", agentHome));
			}
		}
	}
 	
	protected String getNameNodeHost() {
		String activeNN = null;
		try {
			activeNN = getActiveNNFromZK(JumbuneAgent.getZKHosts());
		} catch (Exception e) {
		}		
		if(activeNN == null || activeNN.isEmpty()) {
			try {
				activeNN = getCurrentMachineEndpoint();
				LOGGER.warn("unable to get namenode host, falling back to getCurrentMachineEndpoint() - {}", activeNN);
			} catch (SocketException e1) {
				LOGGER.error("unable to get namenode host");
			}

		}
		
		return activeNN;
	}

	/**
	 * Gets the current machine endpoint.
	 *
	 * @return the current machine endpoint
	 * @throws SocketException the socket exception
	 */
	protected String getCurrentMachineEndpoint() throws SocketException {
		Enumeration<NetworkInterface> ifaces = NetworkInterface
				.getNetworkInterfaces();
		String ipAddress = null;
		for (NetworkInterface iface : Collections.list(ifaces)) {
			Enumeration<InetAddress> raddrs = iface.getInetAddresses();
			for (InetAddress raddr : Collections.list(raddrs)) {
				if (!raddr.isLoopbackAddress() && raddr.isSiteLocalAddress()) {
					if (raddr.toString().startsWith("/")) {
						ipAddress = raddr.toString().split("/")[1];
					}
				}
			}
		}
		return ipAddress;
	}

	/**
	 * Gets the active namenode host.
	 * @param strings 
	 *
	 * @return the active namenode host.
	 */
	private String getActiveNNFromZK(String[] zkHosts){
		byte[] activeHost = null;
		String activeNameNode = null ;
		activeHost = ZKUtils.getLeaderNameNodeFromZK(zkHosts);
		try {    
			ActiveNodeInfo activeNodeInfo = PARSER.parsePartialFrom(activeHost);
			activeNameNode = activeNodeInfo.getHostname();
			LOGGER.debug("active namenode from ZK: "+activeNameNode);
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error(e);			
		} catch (Exception e) {
			LOGGER.error("unable to find active namenode from zk", e.getMessage());			
		}
		return activeNameNode;
	}

	protected Process executeCommandWithRuntime0(Command command) throws IOException{
		String commandString = command.getCommandString();
		Process process = null;
		String finalCommand[] = null;
		if(commandString.contains(RemotingConstants.PIPE_OP) || commandString.contains(RemotingConstants.REDIRECT_SYMBOL)) {
			finalCommand = performBashCommandCuration(command);
			process = Runtime.getRuntime().exec(finalCommand);
		}else{
			finalCommand = performBashCommandCuration(command);
			process = Runtime.getRuntime().exec(finalCommand);
		}
		return process;
	}
	
	protected void performCommandCuration(Command command){
		String commandString = command.getCommandString();
		String workingUser = JschUtil.getCommandAppender(command.getSwitchedIdentity());	     
		commandString = workingUser + commandString;
		replaceSymbolsWithConfigurationVariable(command);
		command.setCommandString(commandString);
	}

	private String[] performBashCommandCuration(Command command) {
		String commandString = command.getCommandString();
		List<String> temp = new ArrayList<>(5);
		String workingUser = JschUtil.getCommandAppender(command
				.getSwitchedIdentity());
		for (String token : workingUser.split("\\s+")) {
			if (token != null && !token.isEmpty()) {
				temp.add(token);
			}
		}
		temp.add(RemotingConstants.sudoEoption);
		temp.add(RemotingConstants.BASH);
		temp.add(RemotingConstants.BASH_ARG_C);
		temp.add(commandString);
		String finalCommand[] = Arrays.copyOf(temp.toArray(), temp.size(), String[].class);
		return finalCommand;
	}

	protected String getStringFromReader(BufferedReader reader) throws IOException{
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line+System.lineSeparator());
		}
		return stringBuilder.toString();
	}

	protected String getStringFromReader(BufferedReader reader, String lineBreaker) throws IOException{
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line+System.lineSeparator());
			if (line.contains(lineBreaker)) {
				break;					
			}
		}
		return stringBuilder.toString();
	}


	protected String getFreeStringFromReader(BufferedReader reader, String lineBreaker) throws IOException{
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			if(line.contains("Mem:")){
			stringBuilder.append(line+System.lineSeparator());
			}
			if(line.contains("-/+ buffers/cache:")){
			stringBuilder.append(line+System.lineSeparator());
			}
			if (line.contains(lineBreaker)) {
				break;					
			}
		}
		return stringBuilder.toString();
	}

	
	protected String getStringFromStream(InputStream inputStream){
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
	
	protected JschResponse getStringFromStream(Channel channel) throws IOException,
			JSchException {
		JschResponse jschResponse = new JschResponse();
		StringBuilder response = new StringBuilder();		
		InputStream in = channel.getInputStream();
		channel.connect();
		jschResponse.setChannelId(channel.getId());
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				response.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				jschResponse.setChannelExitStatus(channel.getExitStatus());
				break;
			}
			try {
			} catch (Exception ee) {
				LOGGER.error("Exception occured while reading stream from Channel #Id["+jschResponse.getChannelId()+"]", ee);
			}
		}
		jschResponse.setResponse(response.toString());
		return jschResponse;
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
	protected String getProcessOutput(Process process) throws InterruptedException {
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