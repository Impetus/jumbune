package org.jumbune.deploy;

public interface Deployer {
	
	String[] getRelativePaths(String versionNumber);
	
	String[] getSchedularJars();

}
