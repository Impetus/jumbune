package org.jumbune.remoting.client;

import java.io.File;
import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.client.consumers.ObjectResponseHandler;
import org.jumbune.remoting.client.consumers.StringResponseHandler;
import org.jumbune.remoting.common.ChannelConnectException;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.codecs.ArchiveDecoder;
import org.jumbune.remoting.common.codecs.ArchiveEncoder;
import org.jumbune.remoting.common.codecs.LogFilesDecoder;
import org.jumbune.remoting.common.codecs.LogFilesEncoder;
import org.jumbune.remoting.common.command.CommandWritable;

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
public class RemoterNonHA implements Remoter {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(RemoterNonHA.class);
	
	/** The jac. */
	private JumbuneAgentCommunicator jac;
	
	/** The receive directory. */
	private String receiveDirectory;

	/**
	 * Instantiates a new remoter.
	 *
	 * @param host agent host
	 * @param port agent port
	 * @param jobName the job name
	 */	
	public RemoterNonHA(String host, int port, String jobName) {
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
	 * @param host agent host
	 * @param port agent port
	 */
	public RemoterNonHA(String host, int port) {
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

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "J", "A", "S" }, jarAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in sendJar.");
		} catch (ConnectException e) {
			logger.error("Connect Exception in receiveLogFiles.",e);
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
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ArchiveDecoder decoder = new ArchiveDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "J", "A", "R" }, relativePathOfRemoteJar + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in receiveJar");
		} catch (ConnectException e) {
			logger.error("Connect Exception in receiveLogFiles.",e);
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

		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		handlers.add(new LogFilesEncoder());

		StringResponseHandler stringResponseHandler = new StringResponseHandler();
		handlers.add(new StringDecoder());
		handlers.add(stringResponseHandler);

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "T", "X", "S" }, logFilesAbsolutePathAtSource + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePath, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in sendLogFiles");
		} catch (ConnectException e) {
			logger.error("Connect Exception in receiveLogFiles.",e);
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
		ChannelFuture channelFuture;
		CyclicBarrier barrier = new CyclicBarrier(2);

		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		LogFilesDecoder decoder = new LogFilesDecoder(receiveDirectory);
		handlers.add(new StringEncoder());
		handlers.add(decoder);

		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "T", "X", "R" }, relativePathOfRemoteLogFiles + RemotingConstants.PATH_DEMARKER
					+ destinationRelativePathOnLocal, barrier);

			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in receiveLogFiles");
		} catch (ConnectException e) {
			logger.error("Connect Exception in receiveLogFiles.",e);
		}
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
        
		try {
		// sending barrier as channel attachment for dynamic integration of
		// barrier
			channelFuture = acquireChannelFuture(handlers);

			writeToChannel(channelFuture.channel(), new String[] { "C", "M", "D" }, commandWritable, barrier);
			confirmBarrierAndGoFAF(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in fireAndForgetCommand");
	} catch (ConnectException e) {
			logger.error("Connect Exception in fireAndForgetCommand.",e);
		}
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
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), new String[] { "C", "M", "A" }, commandWritable, barrier);
			confirmBarrierAndGo(barrier);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in fireAsyncAndForgetCommand");
	} catch (ConnectException e) {
			logger.error("Connect Exception in fireAsyncAndForgetCommand.",e);
		}
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
		//setting maximum object response size which can be handled by us to 20MB
		handlers.add(new ObjectDecoder(20971520, ClassResolvers.cacheDisabled(null)));
		handlers.add(objectResponseHandler);

		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), new String[] { "C", "M", "O" }, commandWritable, objectResponseHandler);
			confirmBarrierAndGo(barrier);
			channelFuture.channel().close();
		} catch (BrokenBarrierException | ChannelConnectException | TimeoutException e) {
			logger.warn("Exception in fireCommandAndGetObjectResponse");			
			
/*			try{
				//TODO: Need a way for all commands
				if(commandWritable.getBatchedCommands().get(0).getCommandType().equals(CommandType.HADOOP_JOB)){
					cleanUpHdfsOutputDir(commandWritable);
				}
				*/		
			
/*			} catch(ConnectException e1){
				logger.error("Connect Exception in fireCommandAndGetObjectResponse.",e1);
			}
*/		} catch (ConnectException e) {
			logger.error("Connect Exception in fireCommandAndGetObjectResponse.",e);
		}
		
		return objectResponseHandler.getResponseObject();
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
	private void confirmBarrierAndGoFAF(CyclicBarrier barrier) throws BrokenBarrierException, TimeoutException {
		try {
			barrier.await(60,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (BrokenBarrierException | TimeoutException e) {
			logger.warn("The specified waiting time threshold has been reached, hence releasing the barrier");
		}
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
	 * Clean up hdfs output directory.
	 *
	 * @param commandWritable the command writable
	 * @throws ConnectException the connect exception
	 */
/*	private void cleanUpHdfsOutputDir(CommandWritable commandWritable) throws ConnectException {
		String hadoopDir = null;
		String removeHdfsDirCommand = " fs -rm -r ";
		String whereIsHaddopCommand = "whereis hadoop  ";
		String wherIsHadoopResponse = (String)fireCommandAndGetObjectResponse(CommandWritableZKUtil.buildCommandWritableFromLeaderAgent(whereIsHaddopCommand));
		logger.debug("executed whereishadoop command:"+wherIsHadoopResponse);
		if(wherIsHadoopResponse!=null && 2<wherIsHadoopResponse.split(" ").length){
			hadoopDir = wherIsHadoopResponse.split(" ")[1];
		} else{
			String getHadoopHomeCommand = "echo $HADOOP_HOME  \n \n";
			hadoopDir = (String)fireCommandAndGetObjectResponse(CommandWritableZKUtil.buildCommandWritableFromLeaderAgent(getHadoopHomeCommand))+ "/bin/hadoop";
			logger.debug("executed getHadoopHomeCommand command:"+hadoopDir);
		}
		
		List<Command> commands = commandWritable.getBatchedCommands();
		List<String> params = commands.get(commands.size()-1).getParams();
		String hdfsDirPath = params.get(params.size()-1);
		logger.debug("parsed hdfsDirPath:"+hdfsDirPath);
		fireAndForgetCommand(CommandWritableZKUtil.buildCommandWritableFromLeaderAgent(hadoopDir+removeHdfsDirCommand+hdfsDirPath));
		logger.debug("executed command [ "+hadoopDir+removeHdfsDirCommand+hdfsDirPath+" ]");
	}
*/
	/**
	 * Write to channel.
	 *
	 * @param channel the channel
	 * @param magicBytes the magic bytes
	 * @param pathOrCommand the path or command
	 * @param attachment the attachment
	 */
	private void writeToChannel(Channel channel, String[] magicBytes, Object pathOrCommand, Object attachment) throws ConnectException {

		//update leader agent details
//		if (pathOrCommand instanceof CommandWritable
//				&& ((CommandWritable) pathOrCommand).isCommandForMaster()) {
//			CommandWritable commandWritable = (CommandWritable) pathOrCommand;
//			AgentNode agent = ZKUtils.getLeaderAgentfromZK();
//			commandWritable.setNameNodeHost(agent.getHost());
//			if(commandWritable.isHasSshAuthKeysFile()){
//				commandWritable.setSshAuthKeysFile(agent.getPrivateKey());
//			}
//		}

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
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		logger.debug("going to shutdown agent");
		try {
			channelFuture = acquireChannelFuture(handlers);
			writeToChannel(channelFuture.channel(), RemotingConstants.SDA_HA, "", null);
			Thread.sleep(2*1000);
			addCloseListener(channelFuture);
			channelFuture.channel().close();
		} catch (Exception e) {
		
		} finally {
	  
		}
		logger.debug("agent shutdown successful....");
	}


}