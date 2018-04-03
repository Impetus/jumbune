package org.jumbune.deploy.hdp;

import java.io.File;

import org.jumbune.deploy.Deployer;
import org.jumbune.deploy.YarnDeployer;

public class HDPDeployer extends YarnDeployer implements Deployer {

	@Override
	public String[] getRelativePaths(String versionNumber) {

		StringBuilder strBuilder;
		String[] relativeJarPaths = new String[ADDITIONAL_HADOOP_JARS.length+1];
	
		int i = 0;
		for (String jar : ADDITIONAL_HADOOP_JARS) {
			if (jar.contains("hadoop")
					&& jar.contains("hadoop-common-*.jar")) {
				strBuilder = new StringBuilder().append(File.separator)
						.append("hadoop-common-").append(versionNumber)
						.append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			} else if (jar.contains("hadoop")
					&& jar.contains("hadoop-mapreduce-client-core-*.jar")) {
				strBuilder = new StringBuilder().append("-mapreduce")
						.append(File.separator)
						.append("hadoop-mapreduce-client-core-")
						.append(versionNumber).append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			} else if (jar.contains("hadoop") && jar.contains("hadoop-mapreduce-client-hs-*.jar")) {
				strBuilder = new StringBuilder().append("-mapreduce")
						.append(File.separator)
						.append("hadoop-mapreduce-client-hs-")
						.append(versionNumber).append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			} else if (jar.contains("hadoop") && jar.contains("hadoop-auth-*.jar")) {
				strBuilder = new StringBuilder().append("-mapreduce")
						.append(File.separator)
						.append("hadoop-auth-")
						.append(versionNumber).append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			}else {
				strBuilder = new StringBuilder().append(File.separator)
						.append("lib").append(File.separator).append(jar);
				relativeJarPaths[i++] = strBuilder.toString();
			}

		}
		strBuilder = new StringBuilder().append(File.separator).append("lib").append(File.separator).append("htrace-core*.jar");
		relativeJarPaths[i++] = strBuilder.toString();
		return relativeJarPaths;
	}

}
