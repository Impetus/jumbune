package org.jumbune.deploy.apache;

import java.io.File;

import org.jumbune.deploy.Deployer;
import org.jumbune.deploy.YarnDeployer;

public class ApacheYarnDeployer extends YarnDeployer implements Deployer {

	@Override
	public String[] getRelativePaths(String versionNumber) {

		StringBuilder strBuilder;

		String[] relativeJarPaths = new String[ADDITIONAL_HADOOP_JARS.length+1];
		int i = 0;
		for (String jar : ADDITIONAL_HADOOP_JARS) {
			if (jar.contains("hadoop")
					&& jar.contains("hadoop-common-*.jar")) {
				strBuilder = new StringBuilder().append(File.separator)
						.append("share").append(File.separator)
						.append("hadoop").append(File.separator)
						.append("common").append(File.separator)
						.append("hadoop-common-").append(versionNumber)
						.append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			} else if (jar.contains("hadoop")
					&& jar.contains("hadoop-mapreduce-client-core-*.jar")) {
				strBuilder = new StringBuilder().append(File.separator)
						.append("share").append(File.separator)
						.append("hadoop").append(File.separator)
						.append("mapreduce").append(File.separator)
						.append("hadoop-mapreduce-client-core-")
						.append(versionNumber).append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			} else if (jar.contains("hadoop") && jar.contains("hadoop-mapreduce-client-hs-*.jar")) {
				strBuilder = new StringBuilder().append(File.separator)
						.append("share").append(File.separator)
						.append("hadoop").append(File.separator)
						.append("mapreduce").append(File.separator)
						.append("hadoop-mapreduce-client-hs-")
						.append(versionNumber).append(".jar");
				relativeJarPaths[i++] = strBuilder.toString();
			}else {
				strBuilder = new StringBuilder().append(File.separator)
						.append("share").append(File.separator)
						.append("hadoop").append(File.separator)
						.append("common").append(File.separator).append("lib")
						.append(File.separator).append(jar);
				relativeJarPaths[i++] = strBuilder.toString();

			}

		}
		strBuilder = new StringBuilder().append(File.separator).append("share/hadoop/common/lib").append(File.separator).append("htrace-core*.jar");
		relativeJarPaths[i++] = strBuilder.toString();
		return relativeJarPaths;
	}

}