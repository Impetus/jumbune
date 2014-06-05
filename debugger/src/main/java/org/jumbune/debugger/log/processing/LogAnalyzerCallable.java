package org.jumbune.debugger.log.processing;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This is the Callable class which gets called for every thread spawned. It
 * takes the map of the list of various log files of the node as input and
 * returns the result of log processing for the node.
 */

public class LogAnalyzerCallable implements Callable<Map<String, JobBean>> {

	/**
	 * fileListMap - the map of lists of different log files
	 */
	private String nodeIP;

	/**
	 * fileListMap - the map of lists of different log files
	 */
	private Map<String, List<String>> fileListMap;

	/**
	 * This constructor takes input to set the file list map
	 * 
	 * @param fileListMap
	 *            the map of lists of different log files for the node
	 */
	public LogAnalyzerCallable(final String nodeIP,
			final Map<String, List<String>> fileListMap) {
		this.nodeIP = nodeIP;
		this.fileListMap = fileListMap;
	}

	/**
	 * The call method which calls analyzeLogs method and return the analysis
	 * result for the node
	 * 
	 * @return Map the result of log analysis for the node
	 */
	public Map<String, JobBean> call() throws IOException {

		final LogAnalyzer logAnalyzer = new LogAnalyzer();
		Map<String, JobBean> logMap = null;

			logMap = logAnalyzer.analyzeLogs(nodeIP, fileListMap);
		return logMap;
	}
}