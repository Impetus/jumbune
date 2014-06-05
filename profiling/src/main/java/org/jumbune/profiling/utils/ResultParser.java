/**
 * 
 */
package org.jumbune.profiling.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jumbune.profiling.healthview.DiskPartitionInfo;


/**
 * This class provides utility methods for parsing different outputs given on
 * execution of shell commands on different nodes of the cluster.
 * 
 */
public class ResultParser {
	
	private static final String SPLITEXP = "\\s+";

	/**
	 * Parses the result of df -h command to give the list of partitions and
	 * their space usage respectively.
	 * 
	 * @param in
	 * @param partitions
	 * @return
	 * @throws IOException
	 */
	public List<DiskPartitionInfo> parseDiskPartitionResult(InputStream in,
			List<DiskPartitionInfo> partitions) throws IOException {

		int lineNum = 0;
		String line;
		String lineArray[];
		String attrib;
		String partition;
		DiskPartitionInfo pi;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				if (lineNum != 0) {
					lineArray = line.split(" ");
					attrib = lineArray[0];
					partition = attrib;

					if (attrib.startsWith("/")) {
						pi = new DiskPartitionInfo();
						pi.setName(partition);
						lineArray = line.split("%");
						lineArray = lineArray[0].split(" ");
						pi.setSpaceUsage(Integer
								.parseInt(lineArray[lineArray.length - 1]));
						partitions.add(pi);
					}
				}
				lineNum++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return partitions;
	}
	
	/**
	 * Parses the remote disk result to give the list of partitions and
	 * their space usage respectively.
	 * 
	 * @param response
	 * @param partitions
	 * @return
	 * @throws IOException
	 */
	public List<DiskPartitionInfo> parseRemoteDiskPartitionResult(
			String response, List<DiskPartitionInfo> partitions)
			throws IOException {

		int lineNum = 0;
		String line;
		String lineArray[];
		String attrib;
		String partition;
		DiskPartitionInfo pi;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getBytes()))); 
																	
																	
																	
																	
			while ((line = br.readLine()) != null) {
				if (lineNum != 0) {
					lineArray = line.split(" ");
					attrib = lineArray[0];
					partition = attrib;

					if (attrib.startsWith("/")) {
						pi = new DiskPartitionInfo();
						pi.setName(partition);
						lineArray = line.split("%");
						lineArray = lineArray[0].split(" ");
						pi.setSpaceUsage(Integer
								.parseInt(lineArray[lineArray.length - 1]));
						partitions.add(pi);
					}
				}
				lineNum++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return partitions;
	}

	/**
	 * Gets vmstats for partitions and thus read and write usage for each
	 * partition
	 * 
	 * @param in
	 * @param partitionStats
	 * @throws IOException
	 */
	public void parseVmStatsPartitionResult(InputStream in,
			List<DiskPartitionInfo> partitionStats) throws IOException {

		int lineNum = 1;
		long reads = 0;
		long writes = 0;
		long totalReads = 0;
		long totalWrites = 0;
		String line;
		String attrib;
		String partition = null;
		String lineArray[];
		DiskPartitionInfo pi;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {

				line = line.trim();
				lineArray = line.split(" ");
				if ((lineNum % 2 == 0) && (partition != null)) {

					attrib = lineArray[0];
					pi = partitionStats.get((lineNum / 2) - 1);
					reads = Long.parseLong(attrib);
					totalReads += reads;
					pi.setReads(reads);

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					pi.setReadSectors(Long.parseLong(attrib));

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					writes = Long.parseLong(attrib);
					totalWrites += writes;
					pi.setWrites(writes);

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					pi.setRequestedWrites(Long.parseLong(attrib));

				} else {
					partition = lineArray[0];
				}
				lineNum++;
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (br != null) {
				br.close();
			}
		}

		double readUsage = 0;
		double writeUsage = 0;
		for (DiskPartitionInfo diskPartitionInfo : partitionStats) {
			readUsage = (double) diskPartitionInfo.getReads() / totalReads
					* ProfilerConstants.HUNDRED;
			diskPartitionInfo.setReadUsage(ProfilerUtil
					.roundTwoDecimals(readUsage));
			writeUsage = (double) diskPartitionInfo.getWrites() / totalWrites
					* ProfilerConstants.HUNDRED;
			diskPartitionInfo.setWriteUsage(ProfilerUtil
					.roundTwoDecimals(writeUsage));
		}
	}

	/**
	 * Parses the remote VM stats result.
	 * 
	 * @param response
	 * @param partitionStats
	 * @return
	 * @throws IOException
	 */
	public void parseRemoteVmStatsPartitionResult(String response,
			List<DiskPartitionInfo> partitionStats) throws IOException {

		int lineNum = 1;
		long reads = 0;
		long writes = 0;
		long totalReads = 0;
		long totalWrites = 0;
		String line;
		String attrib;
		String partition = null;
		String lineArray[];
		DiskPartitionInfo pi;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getBytes())));
			while ((line = br.readLine()) != null) {

				line = line.trim();
				lineArray = line.split(" ");
				if ((lineNum % 2 == 0) && (partition != null)) {

					attrib = lineArray[0];
					pi = partitionStats.get((lineNum / 2) - 1);
					reads = Long.parseLong(attrib);
					totalReads += reads;
					pi.setReads(reads);

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					pi.setReadSectors(Long.parseLong(attrib));

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					writes = Long.parseLong(attrib);
					totalWrites += writes;
					pi.setWrites(writes);

					attrib = ProfilerUtil.trimAndSpilt(line, attrib);
					pi.setRequestedWrites(Long.parseLong(attrib));

				} else {
					partition = lineArray[0];
				}
				lineNum++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		double readUsage = 0;
		double writeUsage = 0;
		for (DiskPartitionInfo diskPartitionInfo : partitionStats) {
			readUsage = (double) diskPartitionInfo.getReads() / totalReads
					* ProfilerConstants.HUNDRED;
			diskPartitionInfo.setReadUsage(ProfilerUtil
					.roundTwoDecimals(readUsage));
			writeUsage = (double) diskPartitionInfo.getWrites() / totalWrites
					* ProfilerConstants.HUNDRED;
			diskPartitionInfo.setWriteUsage(ProfilerUtil
					.roundTwoDecimals(writeUsage));
		}
	}

	/**
	 * Parses the result of top command to get CPU usage
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public float parseCPUUSageResult(InputStream in) throws IOException {
		float usage = 0.0f;
		String line;
		String lineArray[];
		BufferedReader br = null;
		

		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				lineArray = line.split("%");
				lineArray = lineArray[0].split(" ");
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
	 * Parses the result of lscpu command to get CPU details
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public List<Integer> parseCPUDetailsResult(InputStream in)
			throws IOException {
		int len;
		String line;
		String lineArray[];
		BufferedReader br = null;
		List<Integer> cpuStats = new ArrayList<Integer>();
		int processorCount = 0;
		int cpuCores = 0;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("processor")) {
					processorCount++;
				} else if (line.startsWith("cpu core")) {
					lineArray = line.split(SPLITEXP);
					len = lineArray.length;
					cpuCores = Integer.parseInt(lineArray[len - 1]);
				}
			}
			cpuStats.add((int) processorCount / cpuCores);
			cpuStats.add(cpuCores);
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return cpuStats;
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
			cpuStats.add((int) processorCount / cpuCores);
			cpuStats.add(cpuCores);
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return cpuStats;
	}

	/**
	 * Parses the result of mtr command to get network latency between the nodes
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public float parseNetworkLatencyResult(InputStream in) throws IOException {
		String line;
		String lineArray[];
		BufferedReader br = null;
		float avg = 0;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("p")) {
					count++;
					lineArray = line.split(SPLITEXP);
					avg = (avg + Integer.parseInt(lineArray[2])) / count;
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return avg / ProfilerConstants.THOUSAND;
	}

	/**
	 * Parses the result of mtr command to get network latency between the nodes
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public float parseRemoteNetworkLatencyResult(String response)
			throws IOException {
		String line;
		String lineArray[];
		BufferedReader br = null;
		float avg = 0;
		try {
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getBytes())));
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("p")) {
					count++;
					lineArray = line.split(SPLITEXP);
					avg = (avg + Integer.parseInt(lineArray[2])) / count;
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return avg / ProfilerConstants.THOUSAND;
	}
	
			
}