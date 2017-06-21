package org.jumbune.remoting.client;

import org.jumbune.remoting.common.command.CommandWritable;

/**
 * The Class Remoter.
 */
public interface Remoter {

	/**
	 * client side api to send jar files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local jar will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/myjob.jar
	 * @param jarAbsolutePathAtSource , Absolute Path of Jar which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/MRSolution.jar'
	 * .
	 */
	public void sendJar(String destinationRelativePath, String jarAbsolutePathAtSource);

	/**
	 * client side api to receive jar files from the jumbune-agent {server}.
	 * Creates the destination folder if it doesn't exist
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote jar will be received in
	 * <remoterreceiveDir>/Job-123/ABC/myjob.jar
	 * @param relativePathOfRemoteJar , Relative Path of Remote Jar which requires to be fetched. This could be 'Job-456/MRSolution.jar', then we will fetch
	 * <jumbuneagentreceiveDir>/Job-456/MRSolution.jar from JumbuneAgent
	 */
	public void receiveJar(String destinationRelativePathOnLocal, String relativePathOfRemoteJar);

	/**
	 * client side api to send log files to the jumbune-agent {server}.
	 *
	 * @param destinationRelativePath , Relative Destination Directory on JumbuneAgent. An example can be 'Job-123/ABC', then local log will be send in
	 * <JumbuneAgentreceiveDir>/Job-123/ABC/mmc.log
	 * @param logFilesAbsolutePathAtSource , Absolute Path of Log files which requires to be send. This could be '/home/impadmin/Desktop/Jumbune_Home/JobJars/Job-456/mmc.log'.
	 */
	public void sendLogFiles(String destinationRelativePath, String logFilesAbsolutePathAtSource);

	/**
	 * client side api to receive log files from the jumbune-agent {server}.
	 * Creates the destination folder if it doesn't exist
	 * @param destinationRelativePathOnLocal , Relative Destination Directory on Remoter. An example can be 'Job-123/ABC', then remote log files will be received in
	 * <remoterreceiveDir>/Job-123/ABC/mmc.log
	 * @param relativePathOfRemoteLogFiles , Relative Path of Remote Log files which requires to be fetched. This could be a folder containing log files or a log file, for
	 * example, 'Job-456/mmc.log', then we will fetch <jumbuneagentreceiveDir>/Job-456/mmc.log from JumbuneAgent
	 */
	public void receiveLogFiles(String destinationRelativePathOnLocal, String relativePathOfRemoteLogFiles);
	/**
	 * Fire and forget command.
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommand(commandWritable);
	 * @param command the command
	 */
	public void fireAndForgetCommand(CommandWritable commandWritable);

	/**
	 * Fire and forget command asynchronous
	 *	Example usage: CommandWritable commandWritable = new CommandWritable();
		commandWritable.setCommandString("Sending Command");
		remoter.fireAndForgetCommandAsync(commandWritable);
	 * @param command the command
	 */
	//TODO: test fireAsyncAndForgetCommand method, create async method for fire and get object response 
	public void fireAsyncAndForgetCommand(CommandWritable commandWritable);


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
	public Object fireCommandAndGetObjectResponse(CommandWritable commandWritable);
	
	public void shutdownAgent();
	
	public void close();

}