package org.jumbune.web.servlet;

import static org.jumbune.common.utils.Constants.SPACE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConsoleLogUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.SingleNIOEventGroup;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;
import org.jumbune.web.utils.WebConstants;



/**
 * Directs to home page.
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
	
	private static final String YAML_FILE = "/yamlInfo.ser"; 
	
	
	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";
	
	/** The Constant PID_FILE. */
	private static final String PID_FILE = "pid.txt";
	
	
	private static final Logger LOGGER = LogManager
	.getLogger(HomeServlet.class);
	
	@Override
	public void init() throws ServletException {
		super.init();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
		@Override
		public void run() {
		String jHome = System.getenv("JUMBUNE_HOME");
		ObjectInputStream objectinputstream = null;
		InputStream streamIn = null;
		 try {
			 	String yamlFile = jHome+YAML_FILE;
			 	File file = new File(yamlFile);
			 	if(file.exists()){
				 	streamIn = new FileInputStream(yamlFile);
			        objectinputstream= new ObjectInputStream(streamIn);
			        BasicYamlConfig config = (BasicYamlConfig) objectinputstream.readObject();
			        shutTopCmdOnSlaves(config);
			        shutDownNettyEventLoopGroup();
		  			}
		    }catch(IOException e){
		    	LOGGER.error(e.getMessage(), e);
		    } catch (ClassNotFoundException e) {
		    	LOGGER.error(e.getMessage(), e);
				
			}finally{
		    	
		            try {
		            	if(objectinputstream != null){
						objectinputstream .close();
		            	}
					} catch (IOException e) {
					
						LOGGER.error(e.getMessage(), e);
					  } 
		      
		        	try {
		        		  if(streamIn!= null){
						streamIn.close();
		        		  }
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
						
				   }
		 }		
	}});
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);

		StringBuilder sb = new StringBuilder();
		sb.append(System.getenv("JUMBUNE_HOME"))
				.append(WebConstants.TMP_DIR_PATH)
				.append(WebConstants.JUMBUNE_STATE_FILE);

		File file = new File(sb.toString());

		if (file.exists()) {
			file.delete();
		}
		String jHome = System.getenv("JUMBUNE_HOME");
		ObjectInputStream objectinputstream = null;
		InputStream streamIn = null;
		HttpSession session = request.getSession();
		synchronized (session) {
			 try {
				 	String yamlFile = jHome+YAML_FILE;
				 	File jHomeYamlFile = new File(yamlFile);
				 	if(jHomeYamlFile.exists()){
					 	streamIn = new FileInputStream(jHomeYamlFile);
				        objectinputstream= new ObjectInputStream(streamIn);
				        BasicYamlConfig config = (BasicYamlConfig) objectinputstream.readObject();
				        cleanUpJumbuneAgentCurrentJobFolder(config);
				       			}
			    }catch(IOException e){
			    	LOGGER.error(e.getMessage(), e);
			    } catch (ClassNotFoundException e) {
			    	LOGGER.error(e.getMessage(), e);
					
				}finally{
			    
			            try {
			            	if(objectinputstream != null){
							objectinputstream .close();
			            	}
						} catch (IOException e) {
						
							LOGGER.error(e.getMessage(), e);
						}
			          
			        
			        	try {
			        		if(streamIn!= null){
			        		streamIn.close();
			        		}
						} catch (IOException e) {
							LOGGER.error(e.getMessage(), e);
							
						
			        }
			 }		
			session.removeAttribute("ExecutorServReference");
			session.removeAttribute("ReportsBean");
			session.removeAttribute("loader");
		}
		
		final RequestDispatcher rd = getServletContext().getRequestDispatcher(
				WebConstants.HOME_URL);
		rd.forward(request, response);
	}
	
	private static void shutDownNettyEventLoopGroup() {
		SingleNIOEventGroup.eventLoopGroup().shutdownGracefully();
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
		List<String> params = new ArrayList<String>(1);
		params.add(slaveTmpDir);
		List<Command> commands= new ArrayList<Command>(1);
		for (String host : config.getSlaves()) {
			CommandWritable commandWritable = new CommandWritable();	
			CommandWritable.Command cmd = new CommandWritable.Command();
			commands.clear();
			
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
			remoter.fireAndForgetCommand(commandWritable);
		}
		remoter.close();
		ConsoleLogUtil.CONSOLELOGGER.info("Executed command [ShutTop] on worker nodes..");

	}
	
	private static void cleanUpJumbuneAgentCurrentJobFolder(BasicYamlConfig basicYamlConfig){
		//Remove Agent Home/Job jar/Jobname folder

		String hostMaster = basicYamlConfig.getHost();
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(basicYamlConfig.getPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		
		StringBuilder cleanLocationAgentStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(Constants.WHITE_SPACE)
					.append(Constants.AGENT_ENV_VAR_NAME)
					.append(Constants.JOB_JARS_LOC)
					.append(basicYamlConfig.getJumbuneJobName());
			LOGGER.info("Cleanup agent temporary directories command ["
					+ cleanLocationAgentStrBuilder + "]");
			
			builder.addCommand(cleanLocationAgentStrBuilder.toString(),
					false, null);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		    remoter.close();
	}
}
