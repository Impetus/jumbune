package org.jumbune.remoting.client.ha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.HAConfiguration;
import org.jumbune.remoting.client.JumbuneAgentCommunicator;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.consumers.HeartbeatReceptionHandler;
import org.jumbune.remoting.client.consumers.ObjectResponseHandler;
import org.jumbune.remoting.client.consumers.StringResponseHandler;
import org.jumbune.remoting.common.AgentNode;
import org.jumbune.remoting.common.ChannelConnectException;
import org.jumbune.remoting.common.CommandStatus;
import org.jumbune.remoting.common.CommandZNodesUtility;
import org.jumbune.remoting.common.CuratorConnector;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.ZKUtils;
import org.jumbune.remoting.common.codecs.ArchiveDecoder;
import org.jumbune.remoting.common.codecs.ArchiveEncoder;
import org.jumbune.remoting.common.codecs.LogFilesDecoder;
import org.jumbune.remoting.common.codecs.LogFilesEncoder;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * The Class Remoter.
 */
public class RemoterHA implements Remoter {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(RemoterHA.class);
	
	private static final String JUMBUNE_HOME = "JUMBUNE_HOME";
	
	/** The jac. */
	private JumbuneAgentCommunicator jac;
	
	/** The receive directory. */
	private String receiveDirectory;

	private String[] zkHosts;
	
	private CuratorConnector curatorConnector;

	private static Properties haProps;
	
	private static HAConfiguration haConf = null;
	
	private int numApisRetries; 
	
	private int apisRetryCount;
	
	/**
	 * Instantiates a new remoter.
	 *
	 * @param host agent host when HA is not enabled and zookeeper host when HA is enabled.
	 * @param port agent port when HA is not enabled and zookeeper port when HA is enabled.
	 * @param jobName the job name
	 * @param zkHosts 
	 * @param clusterName 
	 */	
	public RemoterHA(String host, int port, String jobName, String[] zkHosts, String clusterName) {
		String agentReceiveDirectory = null;
		String jumbuneHome = System.getenv("JUMBUNE_HOME");
		if (jumbuneHome == null) {
			jumbuneHome = System.getProperty("JUMBUNE_HOME");
		}
		if (jobName != null) {
			agentReceiveDirectory = jumbuneHome + File.separator + RemotingConstants.JOB_JARS_LOC + File.separator + jobName;
		} else {
			agentReceiveDirectory = jumbuneHome;
		}
		jac = new JumbuneAgentCommunicator(host, port);
		this.receiveDirectory = agentReceiveDirectory;
		this. zkHosts = zkHosts;
		this.curatorConnector = CuratorConnector.getInstance(zkHosts);
		haConf = AdminConfigurationUtil.getHAConfiguration(clusterName);
		haProps = new Properties();
		try (InputStream in = Files.newInputStream(Paths.get(
				System.getenv(JUMBUNE_HOME) + RemotingConstants.CONF_DIR + RemotingConstants.HA_CONF_PROPERTIES),
				StandardOpenOption.READ)) {
			    haProps.load(in);
		} catch (IOException e) {
			logger.error("error loading ha_conf.properties file");
		}
		numApisRetries = Integer.parseInt(haProps.getProperty(RemotingConstants.NUM_RETRIES_REMOTER_APIS));
		apisRetryCount = 0;
	}


	/**
	 * client side api to send jar files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local jar will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/myjob.jar
	 * @param jarAbsolutePathAtSource , Absolute Path of Jar which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/MRSolution.jar'
	 * .
	 */
	public void sendJar(String destinationRelativePath, String jarAbsolutePathAtSource) {
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new ArchiveEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(stringResponseHandler);
		
        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), RemotingConstants.JAS_HA, jarAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
				if(apisRetryCount <= numApisRetries) {
					updateLeaderAgentInfo();
					sendJar(destinationRelativePath, jarAbsolutePathAtSource);
					}
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
		}
		apisRetryCount = 0;
	}
	


	/**
	 * client side api to receive jar files from the jumbune-agent {server}.
	 * Creates the destination folder if it doesn't exist
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote jar will be received in
	 * <remoterreceiveDir>/Job-123/ABC/myjob.jar
	 * @param relativePathOfRemoteJar , Relative Path of Remote Jar which requires to be fetched. This could be 'Job-456/MRSolution.jar', then we will fetch
	 * <jumbuneagentreceiveDir>/Job-456/MRSolution.jar from JumbuneAgent
	 */
	public void receiveJar(String destinationRelativePathOnLocal, String relativePathOfRemoteJar) {
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ArchiveDecoder decoder = new ArchiveDecoder(receiveDirectory);
		handlers.add(new StringEncoder());

		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		
		handlers.add(decoder);
		
        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), RemotingConstants.JAR_HA, relativePathOfRemoteJar + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
				if(apisRetryCount <= numApisRetries) {
					updateLeaderAgentInfo();
					receiveJar(destinationRelativePathOnLocal, relativePathOfRemoteJar);
					}
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
		}
		
		apisRetryCount = 0;
	}

	/**
	 * client side api to send log files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local log will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/mmc.log
	 * @param logFilesAbsolutePathAtSource , Absolute Path of Log files which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/mmc.log'.
	 */
	public void sendLogFiles(String destinationRelativePath, String logFilesAbsolutePathAtSource) {
        apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new LogFilesEncoder());
		handlers.add(new StringDecoder());
		
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);

		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(stringResponseHandler);
		
        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), RemotingConstants.TXS_HA, logFilesAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
				if(apisRetryCount <= numApisRetries) {
					updateLeaderAgentInfo();
					sendLogFiles(destinationRelativePath, logFilesAbsolutePathAtSource);
					}
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
		}
		apisRetryCount = 0;
	}
	
	private void updateLeaderAgentInfo() {
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e1) {
		logger.error("unable to find active agent(leader) from zk, queried on ensembles - " + Arrays.toString(zkHosts));
		}
		jac = new JumbuneAgentCommunicator(agentNode.getHost(), agentNode.getPort());
	}


	/**
	 * client side api to receive log files from the jumbune-agent {server}.
	 * Creates the destination folder if it doesn't exist
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote log files will be received in
	 * <remoterreceiveDir>/Job-123/ABC/mmc.log
	 * @param relativePathOfRemoteLogFiles , Relative Path of Remote Log files which requires to be fetched. This could be a folder containing log files or a log file, for
	 * example, 'Job-456/mmc.log', then we will fetch <jumbuneagentreceiveDir>/Job-456/mmc.log from JumbuneAgent
	 */
	public void receiveLogFiles(String destinationRelativePathOnLocal, String relativePathOfRemoteLogFiles) {
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		LogFilesDecoder decoder = new LogFilesDecoder(receiveDirectory);
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(new StringEncoder());
		handlers.add(decoder);
		
        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), RemotingConstants.TXR_HA, relativePathOfRemoteLogFiles + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
				if(apisRetryCount <= numApisRetries) {
					updateLeaderAgentInfo();
					receiveLogFiles(destinationRelativePathOnLocal, relativePathOfRemoteLogFiles);
					}
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
		}
		apisRetryCount = 0;
	}

	/**
	 * Fire and forget command.
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommand(commandWritable);
	 * @param command the command
	 */
	public void fireAndForgetCommand(CommandWritable commandWritable) {
		apisRetryCount++;
		ChannelFuture channelFuture;
		final CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		final ObjectResponseHandler objectResponseHandler = new ObjectResponseHandler(barrier);
		handlers.add(new ObjectEncoder());
		//setting maximum object response size which can be handled by us to 20MB
		handlers.add(new ObjectDecoder(20971520, ClassResolvers.cacheDisabled(null)));
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(objectResponseHandler);
		
        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();
        
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.CMD_HA, commandWritable, objectResponseHandler);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
				if(apisRetryCount <= numApisRetries) {
					updateLeaderAgentInfo();
					fireAndForgetCommand(commandWritable);
					}
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
		}
		apisRetryCount = 0;
	}

	/**
	 * Fire and forget command asynchronous
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommandAsync(commandWritable);
	 * @param command the command
	 */
    @Deprecated
	public void fireAsyncAndForgetCommand(CommandWritable commandWritable) {

	}


	/**
	 * Fire typed command and get object response.
	 * Example Usage:
	 *	CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandForMaster(true);
		commandWritable.setUsername("JumbuneUser");
		remoter.fireCommandAndGetObjectResponse(commandWritable);

	 * @param command the command
	 * @return the object
	 */
	public Object fireCommandAndGetObjectResponse(CommandWritable commandWritable) {
		ChannelFuture channelFuture;
		// Eventually fallen back on battle tested CyclicBarrier, await(),
		// sync() on ChannelFuture didn't worked
		final CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		final ObjectResponseHandler objectResponseHandler = new ObjectResponseHandler(barrier);
		handlers.add(new ObjectEncoder());
		//setting maximum object response size which can be handled by us to 40MB
		handlers.add(new ObjectDecoder(41943040, ClassResolvers.cacheDisabled(null)));
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(objectResponseHandler);
      
		CommandZNodesUtility czu = writeCommandToZK(commandWritable);

        AgentStateListener agentStateListener = new AgentStateListener(hbHandler, barrier, 4);
		new Thread(agentStateListener).start();
		
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.CMO_HA, commandWritable, objectResponseHandler);
			confirmBarrierAndGo(barrier);
			channelFuture.channel().close();
		} catch (Exception e) {
			if(agentStateListener.isAgentFailed()) {
				agentStateListener.stop();
			 return tryRecoveringCommandResponse(commandWritable, czu); 
			} else {
				logger.error(e.getMessage(), e);
			}			
		} finally {
			agentStateListener.stop();
			czu.removeAssociatedZNodes();
		}

		return objectResponseHandler.getResponseObject();
	}	

	

	private Object tryRecoveringCommandResponse(CommandWritable commandWritable, CommandZNodesUtility czu) {
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e1) {
		logger.error("unable to find active agent(leader) from zk, queried on ensembles - " + Arrays.toString(zkHosts));
		}
		jac = new JumbuneAgentCommunicator(agentNode.getHost(), agentNode.getPort());
		
		CommandStatus status = CommandStatus.valueOf(czu.getStatusZNodeData());
		logger.debug("trying to recover from agent failure....current command status - " + status );
		if (status == CommandStatus.SENT || status == CommandStatus.RECEIVED) {
			logger.debug("case - " + status + " response - " + czu.getResponseZNodeData());
			return fireCommandAndGetObjectResponse(commandWritable);
		} else if (status == CommandStatus.EXECUTING) {
			logger.debug("case - " + status + " response - " + czu.getResponseZNodeData());
			status = CommandStatus.valueOf(czu.pollForDataChanges(CommandZNodesUtility.NodeType.STATUS, 3));
			if (status == CommandStatus.COMPLETED) {
				logger.debug("case - " + status + " response - " + czu.getResponseZNodeData());
				return czu.getResponseZNodeData();
			}
		} else if (status == CommandStatus.COMPLETED) {
			logger.debug("case - " + status + " response - " + czu.getResponseZNodeData());
			return czu.getResponseZNodeData();
		}
		return null;
	}


	private CommandZNodesUtility writeCommandToZK(CommandWritable commandWritable) {
	Command command = getCommand(commandWritable);
		CommandZNodesUtility czu = new CommandZNodesUtility(curatorConnector, command.getCommandId());
			czu.createCommandZnodesOnZK();
			czu.setCommandZNodeData(command.getCommandString());
			czu.setStatusZNodeData(CommandStatus.SENT.toString());			
		return czu;
	}


	private Command getCommand(CommandWritable commandWritable) {
		Command command = commandWritable.getBatchedCommands().get(0);
		if(command != null){
			return command;
		} else {
			throw new IllegalArgumentException("wrong arguments in command batch");
		}	    
	}
	
	public void close(){
		//jac.close();
	}

	/**
	 * Adds the close listener.
	 *
	 * @param future the future
	 */
	private synchronized void addCloseListener(ChannelFuture future) {
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				Channel channel = future.channel();
				future.awaitUninterruptibly();
		
					synchronized (channel) {
						if (channel.isOpen()) {
							channel.close();
							if (logger.isDebugEnabled()) {
								logger.debug("operation completed, closing the channel #" + channel.hashCode());
							}
						}
					}
					
			}
		});
	}	

	/**
	 * Confirm barrier and go.
	 *
	 * @param barrier the barrier
	 * @throws BrokenBarrierException, TimeoutException
	 */
	private void confirmBarrierAndGo(CyclicBarrier barrier) throws BrokenBarrierException, TimeoutException {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			logger.warn(e);
		}
	}

	/**
	 * Acquire channel future.
	 *
	 * @param requestedOperation the requested operation
	 * @param handlers the handlers
	 * @return the channel future
	 */
	private ChannelFuture acquireChannelFuture(List<ChannelHandler> handlers) throws ConnectException, ChannelConnectException {
		try {
			return jac.getChannelFuture(handlers);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * Write to channel.
	 *
	 * @param channel the channel
	 * @param magicBytes the magic bytes
	 * @param pathOrCommand the path or command
	 * @param attachment the attachment
	 */
	private void writeToChannel(Channel channel, String[] magicBytes, Object pathOrCommand, Object attachment) throws ConnectException {
		long firstAttempt = System.currentTimeMillis();
		long timeOut = RemotingConstants.TEN * RemotingConstants.THOUSAND;
		while (!channel.isOpen() || !channel.isActive()) {
			if (System.currentTimeMillis() - firstAttempt >= timeOut) {
				try {
					throw new TimeoutException();
				} catch (TimeoutException e) {
					logger.error("Waited for 10 sec for connection reattempt to JumbuneAgent, but failed to connect", e);
				}
				break;
			}
		}
		if (!channel.isActive()) {
			logger.warn("channel #" + channel.hashCode() + " still disconnected, about to write on disconnected Channel");
		}
		if (attachment != null && attachment instanceof CyclicBarrier) {
			channel.attr(RemotingConstants.barrierKey).set((CyclicBarrier)attachment);
		}else if (attachment != null) {
			channel.attr(RemotingConstants.handlerKey).set((ChannelInboundHandler)attachment);
		}
		channel.write(Unpooled.wrappedBuffer(magicBytes[0].getBytes(), magicBytes[1].getBytes(), magicBytes[2].getBytes()));
		channel.write(pathOrCommand);
		channel.flush();
	}
	

	@Override
	public void shutdownAgent() {
		ChannelFuture channelFuture;
		final CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		final ObjectResponseHandler objectResponseHandler = new ObjectResponseHandler(barrier);
		handlers.add(new ObjectEncoder());
		//setting maximum object response size which can be handled by us to 20MB
		handlers.add(new ObjectDecoder(20971520, ClassResolvers.cacheDisabled(null)));
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(objectResponseHandler);
        logger.debug("going to shutdown agent");
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.SDA_HA, "", objectResponseHandler);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
		
		} finally {
	  
		}
		logger.debug("agent shutdown successful....");
	}

	
	private static class AgentStateListener implements Runnable {

		private volatile boolean stop;
		
		private HeartbeatReceptionHandler handler;
		
		private volatile CyclicBarrier barrier;
		
		private boolean agentFailed;
		
		private long agentDeadThresholdMillis;
		
		private int heartbeatMillis;
		

		public AgentStateListener(HeartbeatReceptionHandler handler, CyclicBarrier barrier, int noOfBeatsToMiss) {
			this.handler = handler;
			this.barrier = barrier;
			int threshold = Integer.parseInt(haProps.getProperty(RemotingConstants.THRESHOLD_BEATS_TO_MISS));
			if (noOfBeatsToMiss < threshold) {
				noOfBeatsToMiss = threshold;
			}
			heartbeatMillis = Integer.parseInt(haProps.getProperty(RemotingConstants.HEART_BEAT_MILLIS));
			this.agentDeadThresholdMillis = heartbeatMillis * noOfBeatsToMiss;
		}	

		@Override
		public void run() {
			while (!stop) {
				if ((System.currentTimeMillis() - handler.getLastBeatTime()) > agentDeadThresholdMillis) {
					logger.warn("agent dead !!!");
					agentFailed = true; 
					barrier.reset();
					break;
 				}
				try {
					Thread.sleep(heartbeatMillis);
				} catch (InterruptedException e) {
					logger.error("thread interrupted while monitoring agent");
				}
			}

		}
		
		public void stop() {
			stop = true;
		}

		public boolean isAgentFailed() {
			return agentFailed;
		}	
		
	}

}
