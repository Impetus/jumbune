package org.jumbune.remoting.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * The Class CommandResponser.
 */
public class CommandResponser extends SimpleChannelUpstreamHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(CommandResponser.class);

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
		String strCmd = (String) e.getMessage();
		if (!isEmpty(strCmd)) {
			try {
				e.getChannel().write(performAction(strCmd));
			} catch (JSchException e1) {
				LOGGER.error(e.getMessage(), e1);
			} catch (IOException e1) {
				LOGGER.error(e.getMessage(), e1);
			}
		}
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

	/**
	 * Perform action.
	 * 
	 * @param command
	 *            the command
	 * @return the string
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String performAction(String command) throws JSchException,
			IOException {
		if (command.contains(RemotingConstants.DOUBLE_BANG)) {
			String[] cmds = command.split(RemotingConstants.DOUBLE_BANG);
			return execute(cmds);
		} else if (command.contains(RemotingConstants.START_CURLY_BRACKET)) {
			return executeCommandWithParam(command);
		} else {
			return execute(command.split(RemotingConstants.SINGLE_SPACE));
		}

	}

	/**
	 * Execute command with param.
	 * 
	 * @param message
	 *            the message
	 * @return the string
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String executeCommandWithParam(String message)
			throws JSchException, IOException {

		String sReturnValue = null;
		String param = message.substring(
				message.indexOf(RemotingConstants.START_CURLY_BRACKET),
				message.indexOf(RemotingConstants.END_CURLY_BRACKET));
		String[] params = param.split(RemotingConstants.COMMAND_SEPARATOR);

		String command = message.substring(0,
				message.indexOf(RemotingConstants.END_CURLY_BRACKET)).trim();
		Session session = null;
		InputStream in = null;
		try {
			if (command.contains(RemotingConstants.VMSTAT)) {
				if (params.length != RemotingConstants.FOUR){
					throw new IllegalArgumentException("Invalid method parameters!!!");
				}
				session = JschUtil.createSession(params[0], params[1],
						params[2], params[RemotingConstants.THREE]);
				Channel ch = JschUtil.getChannelWithResponse(session, command);
				in = ch.getInputStream();
				ch.connect();
				sReturnValue = converToString(in);
			} else if (command.contains(RemotingConstants.CPU)) {
				if (params.length != RemotingConstants.FOUR){
					throw new IllegalArgumentException("Invalid method parameters!!!");
				}
				session = JschUtil.createSession(params[0], params[1],
						params[2], params[RemotingConstants.THREE]);
				Channel ch = JschUtil.getChannelWithResponse(session, command);
				in = ch.getInputStream();
				ch.connect();
				sReturnValue = converToString(in);
			} else if (command.contains(RemotingConstants.DF)) {
				if (params.length != RemotingConstants.FOUR){throw new IllegalArgumentException("Invalid method parameters!!!");
				
				}
				session = JschUtil.createSession(params[0], params[1],
						params[2], params[RemotingConstants.THREE]);
				Channel ch = JschUtil.getChannelWithResponse(session, command);
				in = ch.getInputStream();
				ch.connect();
				sReturnValue = converToString(in);
			}
		} finally {
			if (session != null){
				session.disconnect();
			}
		}
		return sReturnValue;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param str
	 *            the str
	 * @return true, if is empty
	 */
	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Execute.
	 * 
	 * @param commands
	 *            the commands
	 * @return the string
	 * @throws IOException 
	 */
	private static String execute(String... commands) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(new File(System.getenv(RemotingConstants.AGENT_HOME)));
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
		}finally {
				if (br != null){
					br.close();
				}
		}
		LOGGER.debug("Command Received - "+Arrays.toString(commands));
		LOGGER.debug("Executed command response "+sb);
		return sb.toString();
	}

	/**
	 * Convert input stream  to string format.
	 * 
	 * @param in
	 *            the in
	 * @return the string
	 * @throws IOException 
	 */
	private String converToString(InputStream in) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		if (in != null) {
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			try {
				line = br.readLine();
				while (line != null) {
					sb.append(line).append(RemotingConstants.NEW_LINE);
				}
			} finally {
					if (br != null){
						br.close();
					}
			}
		}
		return sb.toString();
	}
}
