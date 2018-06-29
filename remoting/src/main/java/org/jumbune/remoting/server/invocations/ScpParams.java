package org.jumbune.remoting.server.invocations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jumbune.remoting.common.command.CommandWritable.Command;

public class ScpParams{
	
	private static final String S = "\\s+";
	private static final String AT_THE_RATE = "@";
	private static final String COLON = ":";
	private String sourceUser;
	private String sourceHost;
	private String sourceLocation;
	
	private String destinationUser;
	private String destinationHost;
	private String destinationLocation;
	
	private List<String> scpOptions;
	
	public ScpParams(Command command){
		
		String commandString = command.getCommandString();
		String[] splits = commandString.split(S);
		//scp -params user@sourcehost:sourcelocation destionationuser@destinationhost:destinationlocation

		//first split is scp, hence ignoring

		for(int i=1;i<splits.length;i++){
			String split = splits[i];
			if(!split.contains(File.separator) || i<(splits.length-2)){
				if(scpOptions==null){
					scpOptions = new ArrayList<String>();
				}
				scpOptions.add(split);
			}
		}
		
		String sourceSplit = splits[splits.length-2];
		String destinationSplit = splits[splits.length-1];
		
		String[] sourceSplits = sourceSplit.split(AT_THE_RATE);
		if(sourceSplits.length==2){
			sourceUser = sourceSplits[0];
			String[] sourcehostSplits = sourceSplits[1].split(COLON);
			if(sourcehostSplits.length==2){
				sourceHost = sourcehostSplits[0];
				sourceLocation = sourcehostSplits[1];
			}
		} else {
			String[] sourceHostLocationSplits = sourceSplit.split(COLON);
			if(sourceHostLocationSplits.length==2){
				sourceHost = sourceHostLocationSplits[0];
				sourceLocation = sourceHostLocationSplits[1];
			} else {
				sourceLocation = sourceSplit;
			}
		}
		
		if (sourceLocation.endsWith("*")) {
			sourceLocation = sourceLocation.substring(0, sourceLocation.length() - 1 );
		}
		
		String[] destinationSplits = destinationSplit.split(AT_THE_RATE);
		if(destinationSplits.length==2){
			destinationUser = destinationSplits[0];
			String[] destinationhostSplits = destinationSplits[1].split(COLON);
			if(destinationhostSplits.length==2){
				destinationHost = destinationhostSplits[0];
				destinationLocation = destinationhostSplits[1];
			}
		} else {
			String[] destinationHostLocationSplits = destinationSplit.split(COLON);
			if(destinationHostLocationSplits.length==2){
				destinationHost = destinationHostLocationSplits[0];
				destinationLocation = destinationHostLocationSplits[1];
			} else {
				destinationLocation = destinationSplit;
			}
		}
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public String getSourceHost() {
		return sourceHost;
	}

	public String getSourceLocation() {
		return sourceLocation;
	}

	public String getDestinationUser() {
		return destinationUser;
	}

	public String getDestinationHost() {
		return destinationHost;
	}

	public String getDestinationLocation() {
		return destinationLocation;
	}

	public List<String> getScpOptions() {
		return scpOptions;
	}
	
	
}