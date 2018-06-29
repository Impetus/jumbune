package org.jumbune.remoting.server.jsch;

import org.jumbune.remoting.common.CommandStatus;
import org.jumbune.remoting.common.CommandZNodesUtility;
import org.jumbune.remoting.common.CuratorConnector;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;

import com.google.gson.Gson;

public class CommandExecDriver {
	
	private static final String EXECUTE_SHELL_JSCH = "executeShellJsch";
	
	private static final String EXECUTE_EXEC_JSCH = "executeExecJsch";
	
	private static final String EXECUTE_RESPONSIVE_SHELL_JSCH = "executeResponsiveShellJsch";
	
	private static final String EXECUTE_RESPONSIVE_EXEC_JSCH = "executeResponsiveExecJsch";
	
	private static final String EXECUTE_COMMAND_WITH_RUNTIME = "executeCommandWithRuntime";
	
	// will accept the json of CommandWritable, command, method_name and zk_hosts as args
	// and persist the output on a znode
	public static void main(String[] args) {
		if (args.length < 4) {
			throw new IllegalArgumentException("unable to execute command. wrong arguments passed to the program");
		}
		
		Gson gson = new Gson();
		CommandWritable commandWritable = gson.fromJson(args[0], CommandWritable.class);
		Command command = gson.fromJson(args[1], Command.class);
		String methodName = gson.fromJson(args[2], String.class);
		String[] zkHosts = gson.fromJson(args[3], String[].class);

		CommandZNodesUtility czu = new CommandZNodesUtility(CuratorConnector.getInstance(zkHosts), command.getCommandId());
		czu.setStatusZNodeData(CommandStatus.EXECUTING.toString());
		
		String response = executeMethod(commandWritable, command, methodName);
		
		//uncomment and get this process' inputstream/errorstream printed(in its parent process) for debugging purposes
		//System.out.println("command["+command.getCommandString()+"] executed, response - " + response);
		
		//persisting response to zk.			    
		czu.setResponseZNodeData(response);
		czu.setStatusZNodeData(CommandStatus.COMPLETED.toString());

	}
	
	
	private static String executeMethod(CommandWritable commandWritable, Command command, String methodName) {
		CommandExecutorHA executorHA = new CommandExecutorHA(commandWritable, command);
		String response = null;
		if(methodName.equals(EXECUTE_EXEC_JSCH)) {
			executorHA.executeExecJsch();
		} else if(methodName.equals(EXECUTE_SHELL_JSCH)) {
			executorHA.executeShellJsch();
		} else if(methodName.equals(EXECUTE_RESPONSIVE_EXEC_JSCH)) {
			response = executorHA.executeResponsiveExecJsch();
		} else if(methodName.equals(EXECUTE_RESPONSIVE_SHELL_JSCH)) {
			response = executorHA.executeResponsiveShellJsch();
		} else if(methodName.equals(EXECUTE_COMMAND_WITH_RUNTIME)) {
			response = executorHA.executeCommandWithRuntime();
		}		
		return response;	
	}

}
