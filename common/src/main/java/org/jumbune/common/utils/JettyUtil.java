package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.SPACE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.SingleNIOEventGroup;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;



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
	
	private static final String YAML_FILE = "/yamlInfo.ser";
	
	/**
	 * Utility class for performing jetty operations.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

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
				performTopCommandCleanUp();
				specifyStopKey(stopPort, stopKey);
				shutDownNettyEventLoopGroup();
				InetAddress host = InetAddress.getByName("127.0.0.1");
				Socket socket = new Socket(host.getHostName(), stopPort);
				BufferedWriter oos = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				oos.write(stopKey + "\nstop");
				oos.close();
				ConsoleLogUtil.CONSOLELOGGER.info(command + " Jumbune has been shutdown successfully.");

			} else {
				usage("Invalid command : " + command);
			}

		} catch (UnknownHostException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
		} catch (IOException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
		}catch (ClassNotFoundException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
		}
	}


	private static void performTopCommandCleanUp()
			throws IOException, ClassNotFoundException {
		String jHome = System.getenv("JUMBUNE_HOME");
		ObjectInputStream objectinputstream = null;
		InputStream streamIn = null;
		 try {
			 	File file = new File(jHome+YAML_FILE);
			 	if(file.exists()){
				 	streamIn = new FileInputStream(jHome+YAML_FILE);
			        objectinputstream= new ObjectInputStream(streamIn);
			        BasicYamlConfig config = (BasicYamlConfig) objectinputstream.readObject();
			        shutTopCmdOnSlaves(config);
			 	}
		    }finally {
		        if(objectinputstream != null){
		            objectinputstream .close();
		         } 
		        if(streamIn!= null){
		        	streamIn.close();
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
	
	/**
	 * Kills the proces on each node which dumps top result to a file.
	 *
	 */
	private static void shutTopCmdOnSlaves(BasicYamlConfig config) {
		String slaveTmpDir = config.getTmpDir();
		StringBuilder command = new StringBuilder();
		command.append(CAT_CMD).append(SPACE).append(slaveTmpDir).append(File.separator).append(PID_FILE);
		Remoter remoter = new Remoter(config.getHost(), Integer.parseInt(config.getPort()));
		List<String> params = new ArrayList<String>();
		params.add(slaveTmpDir);
		
		for (String host : config.getSlaves()) {
			CommandWritable commandWritable = new CommandWritable();	
			CommandWritable.Command cmd = new CommandWritable.Command();
			List<Command> commands= new ArrayList<Command>();
			
			cmd.setCommandString(command.toString());
			cmd.setHasParams(true);
			cmd.setParams(params);
			commands.add(cmd);

			commandWritable.setBatchedCommands(commands);
			commandWritable.setAuthenticationRequired(true);
			commandWritable.setCommandForMaster(false);
			commandWritable.setDsaFilePath(config.getDsaFile());
			commandWritable.setUsername(config.getUser());
			commandWritable.setRsaFilePath(config.getRsaFile());
			commandWritable.setSlaveHost(host);
			commandWritable.setCommandType(CommandType.FS);
			remoter.fireAndForgetCommand(commandWritable);
			remoter.close();
		}
		ConsoleLogUtil.CONSOLELOGGER.info("Executed command [ShutTop] on worker nodes..");

	}
	
	private static void shutDownNettyEventLoopGroup() {
		SingleNIOEventGroup.eventLoopGroup().shutdownGracefully();
	}	

}
