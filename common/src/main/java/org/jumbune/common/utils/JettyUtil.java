package org.jumbune.common.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.jumbune.remoting.client.SingleNIOEventGroup;



/**
 * The Class JettyUtil.
 */
public final class JettyUtil {

	
	/**
	 * Instantiates a new jetty util.
	 */
	private JettyUtil(){
		
	}
	
	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";
	
	/** The Constant PID_FILE. */
	private static final String PID_FILE = "pid.txt";
	
	private static final String JSON_FILE = "/jsonInfo.ser";
	
	/**
	 * Utility class for performing jetty operations.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		BufferedWriter bufferedWriter = null;
		Socket socket = null;
		try {

			String command = null;
			int stopPort = 0;
			String stopKey = "";
		

			for (int i = 0; i < args.length; i++) {

				if ("--command".equals(args[i])){
					command = args[++i];}
				else if ("--stop-port".equals(args[i])){
					stopPort = Integer.parseInt(args[++i]);}
				else if ("--stop-key".equals(args[i])){
					stopKey = args[++i];}
			}

			if (command != null && "stop".equalsIgnoreCase(command)) {
				specifyStopKey(stopPort, stopKey);
				shutDownNettyEventLoopGroup();
				InetAddress host = InetAddress.getByName("127.0.0.1");
				socket = new Socket(host.getHostName(), stopPort);
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bufferedWriter.write(stopKey + "\nstop");
				ConsoleLogUtil.CONSOLELOGGER.info(command + " Jumbune has been shutdown successfully.");

			} else {
				usage("Invalid command : " + command);
			}

		} catch (UnknownHostException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
		} catch (IOException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
		}finally{
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				ConsoleLogUtil.CONSOLELOGGER.error("Unable to close socket", e);		
			}
			try {
				if(bufferedWriter!=null){
					bufferedWriter.close();
			    	  }
					} catch (IOException e) {
				ConsoleLogUtil.CONSOLELOGGER.error("Unable to close connection", e);
			}
		}
	}




	/**
	 * Specify stop key.
	 *
	 * @param stopPort the stop port
	 * @param stopKey the stop key
	 */
	private static void specifyStopKey(int stopPort, String stopKey) {
		switch ((stopPort > 0 ? 1 : 0) + (stopKey != null ? 2 : 0)) {
		case 1:
			usage("Must specify --stop-key when --stop-port is specified");
			break;

		case 2:
			usage("Must specify --stop-port when --stop-key is specified");
			break;

		}
	}

	/**
	 * Usage.
	 *
	 * @param error the error
	 */
	public static void usage(String error) {
		if (error != null){
		ConsoleLogUtil.CONSOLELOGGER.error("ERROR: " + error);}
		ConsoleLogUtil.CONSOLELOGGER.error("Usage: java -class org.jumbune.common.util.jettyUtil [server opts] ");
		ConsoleLogUtil.CONSOLELOGGER.error("Server Options:");
		ConsoleLogUtil.CONSOLELOGGER.error(" --command                          - command to execute( at present 'stop' is supported");
		ConsoleLogUtil.CONSOLELOGGER.error(" --stop-port n                      - jetty port listening for stop command");
		ConsoleLogUtil.CONSOLELOGGER.error(" --stop-key n                       - security string for stop command (required if --stop-port is present)");
		System.exit(1);
	}

	private static void shutDownNettyEventLoopGroup() {
		SingleNIOEventGroup.eventLoopGroup().shutdownGracefully();
	}	

}
