package org.jumbune.deploy.mapr;

import java.io.File;

import org.jumbune.deploy.Deployer;
import org.jumbune.deploy.NonYarnDeployer;

public class MapRDeployer extends NonYarnDeployer implements Deployer {

	@Override
	public String[] getRelativePaths(String versionNumber) {

		StringBuilder strBuilder;
		String[] relativeJarPaths = new String[ADDITIONAL_HADOOP_JARS.length];
		int i = 0;
		for (String jar : ADDITIONAL_HADOOP_JARS) {
			strBuilder = new StringBuilder().append(File.separator).append("lib")
					.append(File.separator).append(jar);
			relativeJarPaths[i++] = strBuilder.toString();
		}
		return relativeJarPaths;

	}
}
