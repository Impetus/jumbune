/**
 * 
 */
package org.jumbune.monitoring.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides utility methods for parsing different outputs given on
 * execution of shell commands on different nodes of the cluster.
 * 
 */
public class ResultParser {
	
	private static final String SPLITEXP = "\\s+";

	/**
	 * Parses the result of top command to get CPU usage
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public float parseRemoteCPUUSageResult(String response) throws IOException {
		float usage = 0.0f;
		String line;
		String lineArray[];
		BufferedReader br = null;
		String cpuValueWithSuffix;

		try {
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getBytes())));
			while ((line = br.readLine()) != null) {
				line=line.replace("%", "");
				lineArray = line.split("us");
				cpuValueWithSuffix=lineArray[0].trim();
				lineArray = cpuValueWithSuffix.split(" ");
				usage = Float.parseFloat(lineArray[lineArray.length - 1]);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return usage;
	}


	/**
	 * Parses the result to get CPU details
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public List<Integer> parseRemoteCPUDetailsResult(String response)
			throws IOException {
		int len;
		String line;
		String lineArray[];
		BufferedReader br = null;
		List<Integer> cpuStats = new ArrayList<Integer>();
		int processorCount = 0;
		int cpuCores = 0;
		try {
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getBytes())));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("processor")) {
					processorCount++;
				} else if (line.startsWith("cpu core")) {
					lineArray = line.split(SPLITEXP);
					len = lineArray.length;
					cpuCores = Integer.parseInt(lineArray[len - 1]);
				}
			}
			if(cpuCores==0){
				cpuStats.add(1);
				cpuStats.add(processorCount);				
			}else{
				cpuStats.add((int) processorCount / cpuCores);
				cpuStats.add(cpuCores);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return cpuStats;
	}
			
}