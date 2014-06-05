package org.jumbune.remoting.server;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * The Class Delegator.
 */
public class Delegator extends SimpleChannelUpstreamHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(Delegator.class);

	/** The receive directory. */
	private String receiveDirectory;

	/**
	 * Instantiates a new delegator.
	 * 
	 * @param receiveDirectory
	 *            the receive directory
	 */
	public Delegator(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(
	 * org.jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		LOGGER.debug("Message received ["+e.getMessage()+"]");
		String relativePath = (String) e.getMessage();
		e.getChannel().write(
				(String) this.receiveDirectory + File.separator + relativePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(
	 * org.jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		LOGGER.warn("Unexpected exception occured from downstream",
				e.getCause());
		e.getChannel().close();
	}

}
