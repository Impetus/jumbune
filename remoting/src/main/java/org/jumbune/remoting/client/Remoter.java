package org.jumbune.remoting.client;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jumbune.remoting.client.consumers.ObjectResponseHandler;
import org.jumbune.remoting.client.consumers.StringResponseHandler;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.handlers.ArchiveDecoder;
import org.jumbune.remoting.handlers.ArchiveEncoder;
import org.jumbune.remoting.handlers.LogFilesDecoder;
import org.jumbune.remoting.handlers.LogFilesEncoder;
import org.jumbune.remoting.handlers.ObjectDecoder;
import org.jumbune.remoting.handlers.ObjectEncoder;
import org.jumbune.remoting.writable.CommandWritable;



/**
 * The Class Remoter.
 */
public class Remoter {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(Remoter.class);
	
	/** The jac. */
	private JumbuneAgentCommunicator jac;
	
	/** The receive directory. */
	private String receiveDirectory;

	/**
	 * Instantiates a new remoter.
	 *
	 * @param host the host
	 * @param port the port
	 * @param jobName the job name
	 */
	public Remoter(String host, int port, String jobName) {
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
	}

	/**
	 * Instantiates a new remoter.
	 *
	 * @param host the host
	 * @param port the port
	 */
	public Remoter(String host, int port) {
		this(host, port, null);
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
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new ArchiveEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);

		channelFuture = acquireChannelFuture("JAS", handlers);
		// sending barrier as channel attachment for dynamic integration of
		// barrier
		writeToChannel(channelFuture.getChannel(), new String[] { "J", "A", "S" }, jarAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePath, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
	}

	/**
	 * client side api to receive jar files from the jumbune-agent {server}.
	 *
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote jar will be received in
	 * <remoterreceiveDir>/Job-123/ABC/myjob.jar
	 * @param relativePathOfRemoteJar , Relative Path of Remote Jar which requires to be fetched. This could be 'Job-456/MRSolution.jar', then we will fetch
	 * <jumbuneagentreceiveDir>/Job-456/MRSolution.jar from JumbuneAgent
	 */
	public void receiveJar(String destinationRelativePathOnLocal, String relativePathOfRemoteJar) {
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		ArchiveDecoder decoder = new ArchiveDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);

		channelFuture = acquireChannelFuture("JAR", handlers);
		// sending barrier as channel attachment for dynamic integration of
		// barrier
		writeToChannel(channelFuture.getChannel(), new String[] { "J", "A", "R" }, relativePathOfRemoteJar + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePathOnLocal, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
	}

	/**
	 * client side api to send log files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local log will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/mmc.log
	 * @param logFilesAbsolutePathAtSource , Absolute Path of Log files which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/mmc.log'.
	 */
	public void sendLogFiles(String destinationRelativePath, String logFilesAbsolutePathAtSource) {

		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(new LogFilesEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);

		channelFuture = acquireChannelFuture("TXS", handlers);
		// sending barrier as channel attachment for dynamic integration of
		// barrier
		writeToChannel(channelFuture.getChannel(), new String[] { "T", "X", "S" }, logFilesAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePath, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
	}

	/**
	 * client side api to receive log files from the jumbune-agent {server}.
	 *
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote log files will be received in
	 * <remoterreceiveDir>/Job-123/ABC/mmc.log
	 * @param relativePathOfRemoteLogFiles , Relative Path of Remote Log files which requires to be fetched. This could be a folder containing log files or a log file, for
	 * example, 'Job-456/mmc.log', then we will fetch <jumbuneagentreceiveDir>/Job-456/mmc.log from JumbuneAgent
	 */
	public void receiveLogFiles(String destinationRelativePathOnLocal, String relativePathOfRemoteLogFiles) {
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		LogFilesDecoder decoder = new LogFilesDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);

		channelFuture = acquireChannelFuture("TXR", handlers);
		// sending barrier as channel attachment for dynamic integration of
		// barrier
		writeToChannel(channelFuture.getChannel(), new String[] { "T", "X", "R" }, relativePathOfRemoteLogFiles + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePathOnLocal, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
	}


	/**
	 * Fire and forget command.
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommand(commandWritable);
	 * @param command the command
	 */
	public void fireAndForgetCommand(CommandWritable commandWritable) {
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new ObjectEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);
		channelFuture = acquireChannelFuture("CMD", handlers);
		// sending barrier as channel attachment for dynamic integration of
		// barrier
		writeToChannel(channelFuture.getChannel(), new String[] { "C", "M", "D" }, commandWritable, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
	}
	
	/**
	 * Fire and forget command asynchronous
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommandAsync(commandWritable);
	 * @param command the command
	 */
	//TODO: test fireAsyncAndForgetCommand method, create async method for fire and get object response 
	public void fireAsyncAndForgetCommand(CommandWritable commandWritable) {
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(new ObjectEncoder());
		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);
		channelFuture = acquireChannelFuture("CMA", handlers);

		writeToChannel(channelFuture.getChannel(), new String[] { "C", "M", "A" }, commandWritable, barrier);
		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		jac.releaseBootstrapResources();
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
				Channel channel = future.getChannel();
				future.awaitUninterruptibly();
		
					synchronized (channel) {
						if (channel.isOpen()) {
							channel.close();
							if (logger.isDebugEnabled()) {
								logger.debug("operation completed, closing the channel #" + channel.getId());
							}
						}
					}
					
			}
		});
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
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		ObjectResponseHandler objectResponseHandler = new ObjectResponseHandler(barrier);
		handlers.add(new ObjectEncoder());
		handlers.add(new ObjectDecoder());
		handlers.add(objectResponseHandler);

		channelFuture = acquireChannelFuture("CMO", handlers);
		writeToChannel(channelFuture.getChannel(), new String[] { "C", "M", "O" }, commandWritable, objectResponseHandler);

		confirmBarrierAndGo(barrier);

		channelFuture.getChannel().close();
		jac.releaseBootstrapResources();

		return objectResponseHandler.getResponseObject();
	}
	

	/**
	 * Confirm barrier and go.
	 *
	 * @param barrier the barrier
	 */
	private void confirmBarrierAndGo(CyclicBarrier barrier) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug(barrier + ": Waiting parties on Barrier before me:" + barrier.getNumberWaiting());
			}
			barrier.await();
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (BrokenBarrierException e) {
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
	private ChannelFuture acquireChannelFuture(String requestedOperation, List<ChannelHandler> handlers) {
		ChannelPipelineFactory pipeLineFactory = jac.createOrGetChannelPipelineFactory(requestedOperation, handlers);
		ChannelFuture channelFuture = jac.getChannelFuture(pipeLineFactory);
		if (logger.isDebugEnabled()) {
			logger.debug("Created Remoting Channel #" + channelFuture.getChannel().getId());
			
		}
		return channelFuture;
	}

	/**
	 * Write to channel.
	 *
	 * @param channel the channel
	 * @param magicBytes the magic bytes
	 * @param pathOrCommand the path or command
	 * @param attachment the attachment
	 */
	private void writeToChannel(Channel channel, String[] magicBytes, Object pathOrCommand, Object attachment) {
		long firstAttempt = System.currentTimeMillis();
		long timeOut = RemotingConstants.TEN * RemotingConstants.THOUSAND;
		while (!channel.isOpen() || !channel.isConnected()) {
			if (System.currentTimeMillis() - firstAttempt >= timeOut) {
				try {
					throw new TimeoutException();
				} catch (TimeoutException e) {
					logger.error("Waited for 10 sec for connection reattempt to JumbuneAgent, but failed to connect", e);
				}
				break;
			}
		}
		if (channel.isConnected()) {
			logger.debug("channel #" + channel.getId() + " connected");
		} else {
			logger.warn("channel #" + channel.getId() + " still disconnected, about to write on disconnected Channel");
		}
		channel.setReadable(false);
		if (attachment != null) {
			channel.setAttachment(attachment);
		}
		
		channel.write(ChannelBuffers.wrappedBuffer(magicBytes[0].getBytes()));
		channel.write(ChannelBuffers.wrappedBuffer(magicBytes[1].getBytes()));
		channel.write(ChannelBuffers.wrappedBuffer(magicBytes[2].getBytes()));
		channel.write(pathOrCommand);
		channel.setReadable(true);
	}
	
	
	
}
