package org.jumbune.remoting.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;

/**
 * The Class JumbuneAgent is used for running the remoting jar on agent
 */
public final class JumbuneAgent {
	
	private static final String VERBOSE = "-verbose";

	private static final String ROLLING_FILE_APPENDER = "rollingFileAppender";

	/** The jars. */
	private static List<String> jars = new ArrayList<String>(2);
	
	/** The Constant LOGGER. */
	public static final Logger CONSOLE_LOGGER = LogManager.getLogger("EventLogger");
	public static final Logger LOGGER = LogManager.getLogger(JumbuneAgent.class);


	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";
	
	/** The Constant PID_FILE. */
	private static final String PID_FILE = "pid.txt";

	private static final String SPACE = " ";
	
	private static final String YAML_INFO = "/yamlInfo.ser";
	
    private static EventLoopGroup bossGroup;
    
    private static EventLoopGroup workerGroup;
	
	/**
	 * Instantiates a new jumbune agent.
	 */
	private JumbuneAgent() {
	}
	static {
		jars.add("/lib/log4j-api-2.0-beta4.jar");
		jars.add("/lib/log4j-core-2.0-beta4.jar");
	}

	/**
	 * *
	 * This method is used to up and run the  agent on namenode.
	 *
	 * @param jumbuneAgentArgs which is port on which user wants to up jumbune agent on hadoop namenode
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	public static void main(String[] jumbuneAgentArgs) throws IOException, InterruptedException, URISyntaxException {
		 if (jumbuneAgentArgs.length < 1 || jumbuneAgentArgs.length >2) {
			CONSOLE_LOGGER.info("Usage: java -jar <jar-name> <agent-port>  [|-verbose ]");
			System.exit(0);
		}
		checkForLoggingLevel(jumbuneAgentArgs);
		int port = Integer.parseInt(jumbuneAgentArgs[0]);
		final String storageDir = System.getenv("AGENT_HOME");

		if (storageDir == null || "".equals(storageDir.trim())) {
			throw new IllegalArgumentException("$AGENT_HOME is not set properly!!");
		}

		// if path assigned as agent home doesn't exists then creating it on
		// file system.
		File file = new File(storageDir);
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdirs();
		}
		// Creating lib directory and extract all contents in jar's lib into $AGENT_HOME/lib directory
		String libLocation = storageDir + "/lib/";
		file = new File(libLocation);
		if(file.exists()){
			String[] files = file.list();
			for(String fileJars: files){
			    File jarFiles = new File(file.getPath(),fileJars);
			    jarFiles.delete();
			}
			file.delete();
			}
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdir();
			extractlibJars(libLocation);
		}
		copyAgentLibJarsToHadoopLib(jars, storageDir);
		ServerBootstrap bootstrap;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast("JaDecoder", new JumbuneAgentDecoder(storageDir));
                 }
             });

             // Bind and start to accept incoming connections.
             bootstrap.bind(port).sync().channel().closeFuture().sync();
        }finally{
	         bossGroup.shutdownGracefully();
	         workerGroup.shutdownGracefully();
	         
	         // Wait until all threads are terminated.
/*	         bossGroup.terminationFuture().sync();
	         workerGroup.terminationFuture().sync();	         
*/        }

   		CONSOLE_LOGGER.info("Jumbune Agent started successfully on port [" + port + "]");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run(){
				//killing top command
				String agentHome = System.getenv("AGENT_HOME");
				try{
				ObjectInputStream objectinputstream = null;
				InputStream streamIn = null;
				 try {
					File file = new File(agentHome+YAML_INFO);
				 	if(file.exists()){
					 	streamIn = new FileInputStream(agentHome+YAML_INFO);
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
		         bossGroup.shutdownGracefully();
		         workerGroup.shutdownGracefully();
				 LOGGER.debug("Released external resources !");
				}catch (IOException e) {
					LOGGER.error(e);
				}catch (ClassNotFoundException e) {
					LOGGER.error(e);
				}
			}
		});
	}
	
	private static void checkForLoggingLevel(String[] jumbuneAgentArgs) {
		if(jumbuneAgentArgs.length>1){
			if(jumbuneAgentArgs.length==2 && jumbuneAgentArgs[1]!=null && jumbuneAgentArgs[1].equals(VERBOSE)){
				LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
				Configuration config = ctx.getConfiguration();
				LoggerConfig loggerConfig = config.getLoggerConfig(ROLLING_FILE_APPENDER);
				loggerConfig.setLevel(Level.DEBUG);
				ctx.updateLoggers();
				LOGGER.info("logging level changed to [DEBUG]");
			}else{
				CONSOLE_LOGGER.info("Usage: java -jar <jar-name> <agent-port>  [|-verbose]");
			}
		}
	}
	
	/**
	 * *
	 * This method copies specified jars from Agent's lib to hadoop's lib directory.
	 *
	 * @param jars the jars
	 * @param storageDir the storage dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void copyAgentLibJarsToHadoopLib(List<String> jars, String storageDir) throws IOException, InterruptedException {
		String hadoopHomeDir = getHadoopHome();
		LOGGER.debug("Hadoop Home ["+hadoopHomeDir+"]");
		if (hadoopHomeDir != null) {
			for (String libjar : jars) {
				StringBuilder copyToHadoopJar = new StringBuilder().append("cp ").append(storageDir).append(libjar).append(" ").append(hadoopHomeDir)
						.append("/lib/");
				executeCommand(copyToHadoopJar.toString());
			}
		}

	}

	/**
	 * *
	 * This method checks whether a particular file is null or empty or a directory.
	 *
	 * @param file the file
	 * @return boolean
	 */
	private static boolean checkNullEmptyORNotADirectory(File file) {
		return file == null || !file.exists() || !file.isDirectory();
	}

	/**
	 * Extractlib jars.
	 *
	 * @param destinationDir the destination dir
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void extractlibJars(String destinationDir) throws URISyntaxException, IOException, InterruptedException {
		CodeSource codeSource = JumbuneAgent.class.getProtectionDomain().getCodeSource();
		File file = new File(codeSource.getLocation().toURI().getPath());
		byte[] entryContent = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			Enumeration<? extends JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("lib") && !entry.getName().equals("lib/")) {
					String entryName = entry.getName();
					try {
						bis = new BufferedInputStream(jarFile.getInputStream(entry));
						entryContent = new byte[bis.available()];
						bis.read(entryContent);
						fos = new FileOutputStream(new File(destinationDir, entryName.substring(RemotingConstants.FOUR)));
						fos.write(entryContent);
					} finally {
						if (bis != null) {
							bis.close();
						}
						if (fos != null) {
							fos.close();
						}
					}
				}
			}

		} finally {
			if(jarFile!=null){
				jarFile.close();
			}
		}
	}

	/**
	 * execute command using system.getRuntime method
	 *
	 * @param command the command
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void executeCommand(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.destroy();
	}

	/**
	 * Gets the hadoop home.
	 *
	 * @return the hadoop home
	 */
	private static String getHadoopHome() {
		return System.getenv("HADOOP_HOME");
	}
	
	private static void shutTopCmdOnSlaves(BasicYamlConfig config) {
		String slaveTmpDir = config.getTmpDir();
		StringBuilder command = new StringBuilder();
		command.append(CAT_CMD).append(SPACE).append(slaveTmpDir).append(File.separator).append(PID_FILE);
		try{
		List<String> params = new ArrayList<String>(1);
		params.add(slaveTmpDir);
		
		for (String host : config.getSlaves()) {
			CommandWritable commandWritable = new CommandWritable();	
			CommandWritable.Command cmd = new CommandWritable.Command();
			List<Command> commands= new ArrayList<Command>(1);
			
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
			
			CommandDelegator cmdDelegator = new CommandDelegator();
			cmdDelegator.performAction(commandWritable);
		}
		LOGGER.debug("Executed command [ShutTop] on worker nodes..");
		}catch (IOException e) {
			LOGGER.error(e);
		}
	}

}