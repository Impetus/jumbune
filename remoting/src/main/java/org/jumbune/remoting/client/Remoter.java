package org.jumbune.remoting.client;

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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.client.consumers.ObjectResponseHandler;
import org.jumbune.remoting.client.consumers.StringResponseHandler;
import org.jumbune.remoting.codecs.ArchiveDecoder;
import org.jumbune.remoting.codecs.ArchiveEncoder;
import org.jumbune.remoting.codecs.LogFilesDecoder;
import org.jumbune.remoting.codecs.LogFilesEncoder;
import org.jumbune.remoting.common.RemotingConstants;
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
		writeToChannel(channelFuture.channel(), new String[] { "J", "A", "S" }, jarAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePath, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
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
		writeToChannel(channelFuture.channel(), new String[] { "J", "A", "R" }, relativePathOfRemoteJar + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePathOnLocal, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
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
		writeToChannel(channelFuture.channel(), new String[] { "T", "X", "S" }, logFilesAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePath, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
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
		writeToChannel(channelFuture.channel(), new String[] { "T", "X", "R" }, relativePathOfRemoteLogFiles + RemotingConstants.PATH_DEMARKER
				+ destinationRelativePathOnLocal, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
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
		writeToChannel(channelFuture.channel(), new String[] { "C", "M", "D" }, commandWritable, barrier);

		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
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

		writeToChannel(channelFuture.channel(), new String[] { "C", "M", "A" }, commandWritable, barrier);
		confirmBarrierAndGo(barrier);
		addCloseListener(channelFuture);
		channelFuture.channel().close();
	}
	
	
	public void close(){
/*		try {
			jac.releaseBootstrapResources();
		} catch (InterruptedException e) {
			logger.warn(e);			
		}
*/	}
	
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
		handlers.add(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		handlers.add(objectResponseHandler);

		channelFuture = acquireChannelFuture("CMO", handlers);
		writeToChannel(channelFuture.channel(), new String[] { "C", "M", "O" }, commandWritable, objectResponseHandler);

		confirmBarrierAndGo(barrier);
		channelFuture.channel().close();
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
	private void writeToChannel(Channel channel, String[] magicBytes, Object pathOrCommand, Object attachment) {
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
		if (channel.isActive()) {
			logger.debug("channel #" + channel.hashCode() + " connected");
		} else {
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

}
