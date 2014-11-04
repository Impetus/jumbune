package org.jumbune.utils.yarn;

public class YarnExecutionCommandsBuilder {
	
	private static String executorPrefix = "yarn";
	
	private static String jarPrefix = "jar";
	
	private static String listApplicationPrefix = "application -list";
	
	private static String applicationStatesPrefix = "-appStates";
	
	private static String typesPrefix = "-appTypes";
	
	private static String statusPrefix = "application -status";
	
	private static String killPrefix = "-kill";
	
	private static String space = " ";
	
	private static String nodeListPrefix = "node -list";
	
	public static String submitExecution(String jarAbsolutePath, String mainClass, String... args){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(jarPrefix).append(space).append(jarAbsolutePath).append(space).append(mainClass).append(space).append(args);
		return builder.toString();
	}
	
	public static String getApplicationsForState(String validState){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(listApplicationPrefix).append(space).append(applicationStatesPrefix).append(space).append(validState);
		return builder.toString();
	}
	
	public static String getApplicationsForType(String applicationType){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(listApplicationPrefix).append(space).append(typesPrefix).append(space).append(applicationType);
		return builder.toString();
	}
	
	public static String getApplicationStatus(String applicationId){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(statusPrefix).append(space).append(applicationId);
		return builder.toString();
	}
	
	public static String kill(String applicationId){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(killPrefix).append(space).append(applicationId);
		return builder.toString();
	}
	
	public static String getAllRunningNodes(){
		StringBuilder builder = new StringBuilder();
		builder.append(executorPrefix).append(space).append(nodeListPrefix);
		return builder.toString();
	}
}
