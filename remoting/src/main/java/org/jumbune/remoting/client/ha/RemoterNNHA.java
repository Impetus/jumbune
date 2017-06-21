package org.jumbune.remoting.client.ha;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.HAConfiguration;
import org.jumbune.utils.ha.HAUtil;

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
public class RemoterNNHA implements Remoter {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(RemoterNNHA.class);
	
	/** The Constant JUMBUNE_HOME. */
	private static final String JUMBUNE_HOME = "JUMBUNE_HOME";
	
	/** The jac. */
	private JumbuneAgentCommunicator jac;
	
	/** The receive directory. */
	private String receiveDirectory;
	
	/** The zk hosts. */
	private String[] zkHosts;
	
	private static HAConfiguration haConf = null;
	
	/** The num apis retries. */
	private int numApisRetries; 
	
	/** The apis retry count. */
	private int apisRetryCount;
	
	/** The curator connector. */
	private CuratorConnector curatorConnector;

	/**
	 * Instantiates a new remoter.
	 *
	 * @param host agent host
	 * @param port agent port
	 * @param jobName the job name
	 * @param zkHosts the zk hosts
	 * @param clusterName 
	 */	
	public RemoterNNHA(String host, int port, String jobName, String[] zkHosts, String clusterName) {
		String agentReceiveDirectory = null;
		String jumbuneHome = System.getenv(JUMBUNE_HOME);
		if (jumbuneHome == null) {
			jumbuneHome = System.getProperty(JUMBUNE_HOME);
		}
		if (jobName != null) {
			agentReceiveDirectory = jumbuneHome + File.separator + RemotingConstants.JOB_JARS_LOC + File.separator + jobName;
		} else {
			agentReceiveDirectory = jumbuneHome;
		}
		haConf = AdminConfigurationUtil.getHAConfiguration(clusterName);
		
		AgentNode agent = null;
		try {
			 agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e1) {
			logger.error("Error while fetching active agent");
		}
		
		if(!agent.getHost().equals(host) || agent.getPort() != port) {
			jac = new JumbuneAgentCommunicator(agent.getHost(), agent.getPort());
		} else {
			jac = new JumbuneAgentCommunicator(host, port);			
		}
		
		this.receiveDirectory = agentReceiveDirectory;
		this.zkHosts = zkHosts;
		this.curatorConnector = CuratorConnector.getInstance(zkHosts);
		
		numApisRetries = haConf.getNumRetriesRemoterApis();
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
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(new ArchiveEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "J", "A", "S" }, jarAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		}  catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
				  logger.warn("Namenode down !! retrying connection to active Namenode");
				  logger.warn("initiating agent failover...");
					electLeaderOnNN(); 
					updateLeaderAgentInfo();
					updateGlobalState();
				    sendJar(destinationRelativePath, jarAbsolutePathAtSource);
				} else {
					logger.error("sendJar - ", e);
				}
			}finally {
				nnListener.stop();
				apisRetryCount = 0;
			}
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
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ArchiveDecoder decoder = new ArchiveDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "J", "A", "R" }, relativePathOfRemoteJar + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		}  catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
				  logger.warn("Namenode down !! retrying connection to active Namenode");
				  logger.warn("Initiating agent failover...");
					electLeaderOnNN(); 
					updateLeaderAgentInfo();
					updateGlobalState();
				    receiveJar(destinationRelativePathOnLocal, relativePathOfRemoteJar);
				} else {
					logger.error("Error while receiving jar - ", e);
				}
			}finally {
				nnListener.stop();
				apisRetryCount = 0;
			}
	}


	/**
	 * client side api to send log files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local log will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/mmc.log
	 * @param logFilesAbsolutePathAtSource , Absolute Path of Log files which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/mmc.log'.
	 */
	public void sendLogFiles(String destinationRelativePath, String logFilesAbsolutePathAtSource) {
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(new LogFilesEncoder());

		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();
		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "T", "X", "S" }, logFilesAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		}  catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
				  logger.warn("Namenode down !! retrying connection to active Namenode");
				  logger.warn("Initiating agent failover...");
					electLeaderOnNN(); 
					updateLeaderAgentInfo();
					updateGlobalState();
				    sendLogFiles(destinationRelativePath, logFilesAbsolutePathAtSource);
				} else {
					logger.error("Error while sending LogFiles - ", e);
				}
			}finally {
				nnListener.stop();
				apisRetryCount = 0;
			}
		
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
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		LogFilesDecoder decoder = new LogFilesDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "T", "X", "R" }, relativePathOfRemoteLogFiles + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		}  catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
				  logger.warn("Namenode down !! retrying connection to active Namenode");
				  logger.warn("Initiating agent failover...");
					electLeaderOnNN(); 
					updateLeaderAgentInfo();
					updateGlobalState();
				    receiveLogFiles(destinationRelativePathOnLocal, relativePathOfRemoteLogFiles);
				} else {
					logger.error("Error while receiving LogFiles - ", e);
				}
			}finally {
				nnListener.stop();
				apisRetryCount = 0;
			}
	}


	/**
	 * Fire and forget command.
	 * 	Example usage: CommandWritable commandWritable = new CommandWritable();
	 * 		commandWritable.setCommandString("Sending Command");
	 * 		remoter.fireAndForgetCommand(commandWritable);
	 *
	 * @param commandWritable the command writable
	 */
	public void fireAndForgetCommand(CommandWritable commandWritable) {
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new ObjectEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();
        
		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "C", "M", "D" }, commandWritable, barrier);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
			  logger.warn("Namenode down !! retrying connection to active Namenode");
			  logger.warn("Initiating agent failover...");
				electLeaderOnNN(); 
				updateLeaderAgentInfo();
				fireAndForgetCommand(commandWritable);
				updateGlobalState();
			} else {
				logger.error("Error in fireAndForget Command - ", e);
			}
		}finally {
			nnListener.stop();
			apisRetryCount = 0;
		}
	}

	/**
	 * Update global state.
	 */
	private void updateGlobalState() {
		AgentNode agent = null;
		try {
			agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e) {
          logger.error(e.getMessage(), e); 
		}
		HAUtil.setActiveAgentHost(agent.getHost());
		HAUtil.setActiveAgentPort(agent.getPort());
		HAUtil.setActiveNNHost(ZKUtils.getActiveNNHost(zkHosts));
	}
	
	/**
	 * Checks if is NN changed.
	 *
	 * @return true, if is NN changed
	 */
	private boolean isNNChanged() {
		return !HAUtil.getActiveNNHost().equals(ZKUtils.getActiveNNHost(zkHosts));
	}

	/**
	 * Checks if is leader agent on nn.
	 *
	 * @return true, if is leader agent on nn
	 */
	private boolean isLeaderAgentOnNN() {
		AgentNode agent = null;
		try {
			agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
			if(agent == null) {
				return false;
			}
		} catch (ConnectException e) {
			logger.error(e.getMessage(), e); 
		}
		return agent.getHost().equals(ZKUtils.getActiveNNHost(zkHosts));
	}
	
	/**
	 * Elect leader on nn.
	 *
	 * @return the agent node
	 */
	private AgentNode electLeaderOnNN() {
		int agentRetryAttempts = 5;
		int retryCount = 1;
		AgentNode agent = null;
		try {
			agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e) {
          logger.error("Cannot obtain active agent from ZK"); 
		}
		synchronized (RemoterNNHA.class) {
			if (isNNChanged() && !isLeaderAgentOnNN()) {
				do {
					agent = forceAgentElection();
					retryCount++;
				} while (!isLeaderAgentOnNN() && retryCount <= agentRetryAttempts);
			}
		}
		return agent;
	}
	
	/**
	 * Force agent election.
	 *
	 * @return the agent node
	 */
	private AgentNode forceAgentElection() {
		AgentNode agent = null;
		try {
			agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
			// send self destruct command to currently active agent so that it
			// relinquishes leadership
			shutdownAgent();
			while (agent != null && HAUtil.compareAgent(agent.getHost(), agent.getPort())) {
				logger.debug("Blocking till a new leader agent is elected...");
				agent = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
				Thread.sleep(1 * 1000);
			}
		} catch (InterruptedException e) {
		} catch (ConnectException e) {
			logger.error("forceAgentElection - ", e);
		}
		return agent;
	}

	/**
	 * Fire and forget command asynchronous
	 * 	Example usage: CommandWritable commandWritable = new CommandWritable();
	 * 		commandWritable.setCommandString("Sending Command");
	 * 		remoter.fireAndForgetCommandAsync(commandWritable);
	 *
	 * @param commandWritable the command writable
	 */
	//TODO: test fireAsyncAndForgetCommand method, create async method for fire and get object response 
	public void fireAsyncAndForgetCommand(CommandWritable commandWritable) {

	}


	/**
	 * Fire typed command and get object response.
	 * Example Usage:
	 * 	CommandWritable commandWritable = new CommandWritable();
	 * 		commandWritable.setCommandForMaster(true);
	 * 		commandWritable.setUsername("JumbuneUser");
	 * 		remoter.fireCommandAndGetObjectResponse(commandWritable);
	 *
	 * @param commandWritable the command writable
	 * @return the object
	 */
	public Object fireCommandAndGetObjectResponse(CommandWritable commandWritable) {
		updateLeaderAgentInfo();
		apisRetryCount++;
		ChannelFuture channelFuture;
		// Eventually fallen back on battle tested CyclicBarrier, await(),
		// sync() on ChannelFuture didn't worked
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		ObjectResponseHandler objectResponseHandler = new ObjectResponseHandler(barrier);
		handlers.add(new ObjectEncoder());
		//setting maximum object response size which can be handled by us to 40MB
		handlers.add(new ObjectDecoder(41943040, ClassResolvers.cacheDisabled(null)));
		HeartbeatReceptionHandler hbHandler = new HeartbeatReceptionHandler();
		handlers.add(hbHandler);
		handlers.add(objectResponseHandler);
		NNStateListener nnListener = new NNStateListener(barrier, zkHosts);
		new Thread(nnListener).start();
		
		CommandZNodesUtility czu = writeCommandToZK(commandWritable);
		
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.CMO_HA, commandWritable, objectResponseHandler);
			confirmBarrierAndGo(barrier);
			channelFuture.channel().close();
		}  catch (Exception e) {
			if((nnListener.isNNFailed() || isNNChanged()) && apisRetryCount <= numApisRetries) {
				  logger.warn("Namenode down !! retrying connection to active Namenode");
				  logger.warn("Initiating agent failover...");
					electLeaderOnNN(); 
					updateLeaderAgentInfo();
					updateGlobalState();
					return tryRecoveringCommandResponse(commandWritable, czu);
				} else {
					logger.error("Error while firing Command And retriving response - ", e);
				}
			}finally {
				nnListener.stop();
				apisRetryCount = 0;
				czu.removeAssociatedZNodes();
			}
		return objectResponseHandler.getResponseObject();
	}	
	
	/**
	 * Write command to zk.
	 *
	 * @param commandWritable the command writable
	 * @return the command z nodes utility
	 */
	private CommandZNodesUtility writeCommandToZK(CommandWritable commandWritable) {
	Command command = getCommand(commandWritable);
		CommandZNodesUtility czu = new CommandZNodesUtility(curatorConnector, command.getCommandId());
			czu.createCommandZnodesOnZK();
			czu.setCommandZNodeData(command.getCommandString());
			czu.setStatusZNodeData(CommandStatus.SENT.toString());			
		return czu;
	}


	/**
	 * Gets the command.
	 *
	 * @param commandWritable the command writable
	 * @return the command
	 */
	private Command getCommand(CommandWritable commandWritable) {
		Command command = commandWritable.getBatchedCommands().get(0);
		if(command != null){
			return command;
		} else {
			throw new IllegalArgumentException("wrong arguments in command batch");
		}	    
	}

	/**
	 * Try recovering command response.
	 *
	 * @param commandWritable the command writable
	 * @param czu the czu
	 * @return the object
	 */
	private Object tryRecoveringCommandResponse(CommandWritable commandWritable, CommandZNodesUtility czu) {
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e1) {
		logger.error("unable to find active agent(leader) from zk, queried on ensembles - " + Arrays.toString(zkHosts));
		}
		jac = new JumbuneAgentCommunicator(agentNode.getHost(), agentNode.getPort());
		
		CommandStatus status = CommandStatus.valueOf(czu.getStatusZNodeData());
		logger.debug("Trying to recover command response. current command status - " + status );
		if (status == CommandStatus.SENT || status == CommandStatus.RECEIVED) {			
			return fireCommandAndGetObjectResponse(commandWritable);
		} else if (status == CommandStatus.EXECUTING) {
			status = CommandStatus.valueOf(czu.pollForDataChanges(CommandZNodesUtility.NodeType.STATUS, 3));
			if (status == CommandStatus.COMPLETED) {
				return czu.getResponseZNodeData();
			}
		} else if (status == CommandStatus.COMPLETED) {
			return czu.getResponseZNodeData();
		}
		return null;
	}
	
	/**
	 * Update leader agent info.
	 */
	private void updateLeaderAgentInfo() {
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentfromZK(zkHosts, haConf);
		} catch (ConnectException e1) {
		logger.error("Unable to find active agent(leader) from zk, queried on ensembles - " + Arrays.toString(zkHosts));
		}
		jac = new JumbuneAgentCommunicator(agentNode.getHost(), agentNode.getPort());
	}
	
	/* (non-Javadoc)
	 * @see org.jumbune.remoting.client.Remoter#close()
	 */
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
	 * @throws BrokenBarrierException the broken barrier exception
	 * @throws TimeoutException the timeout exception
	 */
	private void confirmBarrierAndGo(CyclicBarrier barrier) throws BrokenBarrierException, TimeoutException {
		try {
			barrier.await();
		} catch (InterruptedException e) {
			logger.error("Error while waiting on barrier - ", barrier);
		}
	}

	/**
	 * Acquire channel future.
	 *
	 * @param handlers the handlers
	 * @return the channel future
	 * @throws ConnectException the connect exception
	 * @throws ChannelConnectException the channel connect exception
	 */
	private ChannelFuture acquireChannelFuture(List<ChannelHandler> handlers) throws ConnectException, ChannelConnectException {
		try {
			return jac.getChannelFuture(handlers);
		} catch (InterruptedException e) {
			logger.error("A channel can not be acquired from Jumbune to Agent");
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
	 * @throws ConnectException the connect exception
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
			logger.warn("Channel #" + channel.hashCode() + " still disconnected, about to write on disconnected Channel");
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

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.client.Remoter#shutdownAgent()
	 */
	@Override
	public void shutdownAgent() {
		ChannelFuture channelFuture;
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		logger.debug("Initiating agent shutdown");
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.SDA_HA, "", null);
			Thread.sleep(2*1000);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
		   logger.error("Error shutting down agent ", e);
		} finally {
	  
		}
		logger.debug("Agent shutdown successful....");
	}
	

	/**
	 * This class acts as a listener for a change in leader NameNode in a HA cluster.
	 * In order to listen, the class should be instantiated and the reference should be passed to a new thread.
	 *  It starts listening upon invocation of {@code start()} method of {@link java.lang.Thread}
	 * </br></br>
	 * Listener should be stopped manually by calling {@link NNStateListener.stop()}
	 * 
	 */
	private static class NNStateListener implements Runnable {

		/** The stop. */
		private volatile boolean stop;

		/** The barrier. */
		private volatile CyclicBarrier barrier;

		/** The nn failed. */
		private boolean nnFailed;

		/** The nn znode retries. */
		private int nnZnodeRetries;

		/** The zk hosts. */
		private String[] zkHosts;

		/**
		 * Instantiates a new NN state listener.
		 *
		 * @param barrier the barrier
		 * @param zkHosts the zk hosts
		 */
		public NNStateListener(CyclicBarrier barrier, String[] zkHosts) {
			this.nnZnodeRetries = 2;
			this.barrier = barrier;
			this.zkHosts = zkHosts;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			String currentlyActiveNN = HAUtil.getActiveNNHost();
			int failCount = 0;
			while (!stop) {
				if (!ZKUtils.getActiveNNHost(zkHosts).equals(currentlyActiveNN)) {
					failCount++;
				}
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					logger.error("Thread interrupted while monitoring Namenode state");
				}

				if (failCount == nnZnodeRetries) {
					logger.warn("namenode dead !!! Applying recovery...");
					nnFailed = true;
					barrier.reset();
					break;
				}

			}

		}

		/**
		 * Stop.
		 */
		public void stop() {
			stop = true;
		}

		/**
		 * Checks if is NN failed.
		 *
		 * @return true, if is NN failed
		 */
		public boolean isNNFailed() {
			return nnFailed;
		}

	}


}
