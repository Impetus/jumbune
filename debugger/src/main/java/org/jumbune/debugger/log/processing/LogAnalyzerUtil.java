package org.jumbune.debugger.log.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobCounterBean;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.HadoopJobCounters;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This is the class which analyzes the logs created after instrumentation and
 * returns cluster-wide result in form of a JSON String.
 * 
 
 */

public class LogAnalyzerUtil {
	
	
	private static final String SYMBOL_TABLE_NAME = "symbolTable.log";

	private static Properties props = new Properties();

	/** The LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(LogAnalyzerUtil.class);
	
	/**
	 * nodeFileMap - Map to store list of log files of various types
	 * corresponding to various nodes.
	 */
	private Map<String, Map<String, List<String>>> nodeFileMap = new HashMap<String, Map<String, List<String>>>();

	/**
	 * logMap - Map to store the final cluster-wide result of log analysis.
	 */
	private Map<String, JobBean> logMap = new HashMap<String, JobBean>();

	/**
	 * jobChain - Map to store job chaining information.
	 */
	private List<ChainingInfoBean> jobChain;

	/**
	 * maxNumOfThreads - Maximum number of threads to be spawned
	 */
	private int maxNumOfThreads = 0;

	private int numOfReducers;

	private int totalReducerInputKeys;

	public  List<String> userDefValidationClasses=new ArrayList<String>();
    public  List<String> regexValidationsClasses=new ArrayList<String>();
	
	
	/**
	 * This constructor takes input to set maximum number of threads to be
	 * spawned
	 * 
	 * @param maxNumOfThreads
	 *            the maximum number of threads to be spawned
	 */
	public LogAnalyzerUtil() {
	}

	/**
	 * Checks if the path provided is of a directory, if not,throws an exception
	 * for wrong directory path
	 * 
	 * @param dirPath
	 *            the path of directory where log files are stored
	 * @throws JumbuneException
	 *             the HTF exception for wrong directory path
	 * @throws IOException
	 */
	private Map<String, Map<String, List<String>>> createNodeFileMap(
			 final String dirPath) throws JumbuneException, IOException {
		String modifiedDirPath=dirPath;
		if(dirPath.contains(SYMBOL_TABLE_NAME)){
			modifiedDirPath = dirPath.substring(0, dirPath.indexOf(SYMBOL_TABLE_NAME));
		}
			
		final File directory = new File(modifiedDirPath);
		Map<String, Map<String, List<String>>> mrChainSortedMap = null;
			
		if (directory.isDirectory()) {

			final File[] files = directory.listFiles();
			final List<File> fileList = Arrays.asList(files);
			for (File file : fileList) {
				if (!file.isDirectory() && !(file.getName().equals(SYMBOL_TABLE_NAME))) {
					mrChainSortedMap = addFileToNodeFileMap(file,
								mrChainSortedMap);
					
				}
			}

		} else {
			LOGGER.error("Wrong directory path");
			throw new JumbuneException(
					ErrorCodesAndMessages.DIRECTORY_PATH_NOT_CORRECT);
		}

	
		
		return mrChainSortedMap;

	}

	/**
	 * This is the class which analyzes the logs created after instrumentation
	 * and returns cluster-wide result in form of a JSON String.
	 * 
	 * @param dirPath
	 *            the path of directory where log files are stored
	 * @return String the JSON String containing the final result of log
	 *         analysis
	 * @throws ExecutionException
	 * @throws JumbuneException
	 *             the HTF exception
	 * @throws IOException
	 */
	public final String processLogs(final String dirPath,
			boolean isPartitionerEnabled, Config config, HadoopJobCounters hadoopJobCounters) throws JumbuneException,
			InterruptedException, ExecutionException, IOException {
		InputStream in = ConfigurationUtil.readFile(dirPath.substring(0, dirPath.indexOf("consolidated"))+SYMBOL_TABLE_NAME);
		if(in == null){
			throw new IllegalArgumentException("No Symbol table found!!!");
		}
		props.load(in);
		
		LOGGER.debug("Starting to Process Log files...");
		Map<String, Map<String, List<String>>> mrChainSortedMap = createNodeFileMap(dirPath);

		final int numOfNodes = nodeFileMap.size();

		// If the number of threads are 0 or greater than the number of
		// nodes,then they are set equal to the number of nodes.
		if (maxNumOfThreads > numOfNodes || maxNumOfThreads == 0) {
			maxNumOfThreads = numOfNodes;
		}
		LOGGER.debug("Number of thread launched for processing logs ["+ maxNumOfThreads+"]");
		LOGGER.debug("Log processing started");
		ExecutorService pool = null;
		try {
			pool = Executors.newFixedThreadPool(maxNumOfThreads);
			final List<Future<Map<String, JobBean>>> analysisResults = new ArrayList<Future<Map<String, JobBean>>>();
			Callable<Map<String, JobBean>> nodeAnalysisTask;

			for (Map.Entry<String, Map<String, List<String>>> nodeFilePairs : nodeFileMap
					.entrySet()) {
				nodeAnalysisTask = new LogAnalyzerCallable(
						nodeFilePairs.getKey(), nodeFilePairs.getValue(), config);
				analysisResults.add(pool.submit(nodeAnalysisTask));
			}

			Map<String, JobBean> nodeAnalysisResult;

			final Iterator<Future<Map<String, JobBean>>> iterator = analysisResults
					.iterator();
			while (iterator.hasNext()) {

				// obtaining the result for a node.
				nodeAnalysisResult = (iterator.next()).get();

				// adding the result obtained for a node,to the cluster wide
				// analysis result.
				addToLogMap(nodeAnalysisResult);
				iterator.remove();
			}

		} finally {

			if (pool != null) {
				pool.shutdown();
			}
			if(in != null){
				in.close();
			}

		}

		DebugAnalysisBean debugAnalysisBean = new DebugAnalysisBean();
		debugAnalysisBean.setLogMap(logMap);
		// getting information from logMap about job chaining counters
		if (jobChain != null) {
			setJobChainCounters();
			debugAnalysisBean.setJobChain(jobChain);

		}

		// getting information from logMap about map chaining counters
		if (mrChainSortedMap != null) {
			Map<String, Map<String, List<ChainingInfoBean>>> mrChain = setMRChainCounters(mrChainSortedMap);
			debugAnalysisBean.setMrChain(mrChain);
		}

		// whether the partitioner is enabled or not
		if (isPartitionerEnabled) {
			Map<String, List<PartitionerInfoBean>> partitionerMap = getPartitionCounters(mrChainSortedMap);
			debugAnalysisBean.setPartitionerMap(partitionerMap);
          	}
		JobConfig jobConfig = (JobConfig) config;
		// getting all regex validations
		List<Validation> validations = jobConfig.getRegex();
		for (Validation validation : validations) {
			regexValidationsClasses.add(validation.getClassname());
		}

		// getting all user validations
		validations = jobConfig.getUserValidations();
		for (Validation validation : validations) {
			userDefValidationClasses.add(validation.getClassname());
		}

		// removing entries which are not required
		List<String> classesRequiredInResult = new ArrayList<String>(
				regexValidationsClasses);
		classesRequiredInResult.addAll(userDefValidationClasses);

		Map<String, JobBean> logMap = debugAnalysisBean.getLogMap();
		Set<String> logMapKeys = logMap.keySet();
		List<String> jobList = new ArrayList<String>();
		List<JobCounterBean> jobCounterBeans=hadoopJobCounters.getJobCounterBeans();
		for (JobCounterBean jobCounterBean : jobCounterBeans) {
			jobList.add(jobCounterBean.getJobName());

		}

		for (String jobId : logMapKeys) {
			Set<String> jobMapKeys = logMap.get(jobId).getJobMap().keySet();

			Iterator<String> jobKeys = jobMapKeys.iterator();
			String mapperReducerName = null;
			while (jobKeys.hasNext()) {
				mapperReducerName = jobKeys.next();
				if (!classesRequiredInResult.contains(mapperReducerName)) {
					jobKeys.remove();
				}
			}
			if (jobList.contains(jobId)) {
				// setting total input keys and output records according to
				// hadoop job counters.
				logMap.get(jobId).setTotalInputKeys(
						Integer.valueOf(hadoopJobCounters
								.getValueByJobNameAndProperty(jobId,
										Constants.MAP_INPUT_RECORD)));
				logMap.get(jobId).setTotalContextWrites(
						Integer.valueOf(hadoopJobCounters
								.getValueByJobNameAndProperty(jobId,
										Constants.REDUCE_OUTPUT_RECORD)));

			}
		}

		Map<String, DebugAnalysisBean> debugAnalysisMap = new HashMap<String, DebugAnalysisBean>();
		debugAnalysisMap.put(LPConstants.DEBUG_ANALYSIS, debugAnalysisBean);
		return getJsonFromDebugReport(debugAnalysisMap,debugAnalysisBean);	
	
	}
	/***
	 * This method process debug report and make it in form of json string so that it can be views on UI.
	 * @param debugAnalysisMap
	 * @param debugAnalysisBean
	 * @return 
	 */
	private String getJsonFromDebugReport(Map<String, DebugAnalysisBean> debugAnalysisMap,DebugAnalysisBean debugAnalysisBean) {
		final Gson gson = new Gson();
		String jsonFromReport=null;
		try {
			JsonObject jsonObject = gson.toJsonTree(debugAnalysisMap)
					.getAsJsonObject();
			DebugReport debugReport = new DebugReportGenerator()
					.generateReportFromDebugAnalysisBean(debugAnalysisBean);
			JsonElement jsonElement = gson.toJsonTree(debugReport,
					DebugReport.class);
			jsonObject.add(LPConstants.DEBUGGER_SUMMARY, jsonElement);
			jsonFromReport = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.error("Exception while creating the Json object with appended Debugger Summary",e);
			jsonFromReport = gson.toJson(debugAnalysisMap);
		}
		LOGGER.info("Completed debugger log processing");
		return jsonFromReport;
	}

	/**
	 * Makes a list for different types of log files on a node and add them to
	 * map of that node.
	 * 
	 * @param file
	 *            the file to be added to the list for different types of log
	 *            files on the node
	 * @throws IOException
	 */
	private Map<String, Map<String, List<String>>> addFileToNodeFileMap(
			final File file,
			final Map<String, Map<String, List<String>>> mrChainSortedMap)
			throws IOException {
		Map<String, Map<String, List<String>>> returnedMRChainSortedMap =mrChainSortedMap;
		LOGGER.debug("Adding file to node file map" + file.getName());
		Map<String, List<String>> fileListMap = null;
		List<String> fileList = null;
		final String filePath = file.toString();

		// file name contains type of chaining(if any), IP Address, and
		// mapreduce instance id separated by an underscore
		final String fileName = file.getName();
		int extensionIndex = fileName.lastIndexOf(LPConstants.DOT);

		// removing the extension name from log file
		String fileNameWithoutExtension = fileName.substring(0, extensionIndex);

		final String extensionName = fileName.substring(extensionIndex + 1,
				fileName.length());
		if (!((LPConstants.LOG).equals(extensionName))) {
			extensionIndex = fileNameWithoutExtension
					.lastIndexOf(LPConstants.DOT);
			fileNameWithoutExtension = fileNameWithoutExtension.substring(0,
					extensionIndex);
		}

		// splitting by an underscore
		final String[] fileNameAndInstanceId = fileNameWithoutExtension.split(
				LPConstants.UNDERSCORE, 2);
		String nodeName = fileNameAndInstanceId[0];
		final String mapReduceInstanceId = fileNameAndInstanceId[1];

		String[] nodeNameArr = nodeName.split("-", 2);

		// if map or job chaining is done
		if (nodeNameArr.length > 1) {
			nodeName = nodeNameArr[1];

			String chainResult = nodeNameArr[0];

			// for mr chaining
			if (LPConstants.MR_CHAIN.equals(chainResult)) {

				return makeJobWiseList(returnedMRChainSortedMap, filePath, mapReduceInstanceId);

			}
			// for job chaining
			else if (LPConstants.JOB_CHAIN.equals(chainResult)) {
				// just make a list
				if (jobChain == null) {
					jobChain = new ArrayList<ChainingInfoBean>();
				}

				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(filePath));
				String line = null;
				while (true) {
					line = bufferedReader.readLine();
					if (line == null || line.trim().isEmpty()) {
						break;
					}
					ChainingInfoBean chainingInfoBean = new ChainingInfoBean();
					chainingInfoBean.setName(line);
					jobChain.add(chainingInfoBean);
				}
				if(bufferedReader!=null){
					bufferedReader.close();
				}
					return returnedMRChainSortedMap;
			}
		}

		fileListMap = nodeFileMap.get(nodeName);
		if (fileListMap != null) {
			fileList = fileListMap.get(mapReduceInstanceId);
			if (fileList == null) {
				fileList = new ArrayList<String>();
			}
		} else {
			fileListMap = new HashMap<String, List<String>>();
			fileList = new ArrayList<String>();
		}
		fileList.add(filePath);
		fileListMap.put(mapReduceInstanceId, fileList);
		nodeFileMap.put(nodeName, fileListMap);

		return returnedMRChainSortedMap;

	}
	private Map<String, Map<String, List<String>>> makeJobWiseList(Map<String, Map<String, List<String>>> mRChainSortedMap,
			final String filePath, final String mapReduceInstanceId) throws IOException {
		Map<String, Map<String, List<String>>> returnedMRChainSortedMap=mRChainSortedMap;
		String[] arr = mapReduceInstanceId.split(
				LPConstants.UNDERSCORE, InstrumentConstants.FOUR);
		StringBuilder sb = new StringBuilder(LPConstants.JOB_ATTRIBUTE);
		sb.append(arr[1]).append(LPConstants.UNDERSCORE).append(arr[2]);
		String jobId = sb.toString();

		if (returnedMRChainSortedMap == null) {
			returnedMRChainSortedMap = new HashMap<String, Map<String, List<String>>>();
		}

		Map<String, List<String>> jobDetails = returnedMRChainSortedMap
				.get(jobId);

		if (jobDetails == null) {
			jobDetails = new HashMap<String, List<String>>();
		}

		String key = null;
		if (mapReduceInstanceId
				.contains(LPConstants.REDUCER_IDENTIFIER)) {
			key = LPConstants.REDUCE_METHOD;
		} else {
			key = LPConstants.MAP_METHOD;
		}

		List<String> chainList = jobDetails.get(key);
				
		if (chainList == null) {
			chainList = new ArrayList<String>();
		}
		
	
	

		BufferedReader bufferedReader = new BufferedReader(
				new FileReader(filePath));
		String line = null;
		while (true) {
			line = bufferedReader.readLine();
			if (line == null || line.trim().isEmpty()) {
				break;
			}

			if (!chainList.contains(line)) {
				chainList.add(line);
			}
		}
		if(bufferedReader!=null){
			bufferedReader.close();
		}

	  
		jobDetails.put(key, chainList);
		returnedMRChainSortedMap.put(jobId, jobDetails);
		return returnedMRChainSortedMap;
	}

	/**
	 * Checks if the Job,Mapper or Reducer running on the node already exists,
	 * If not,adds the result Job,Mapper or Reducer directly, otherwise combines
	 * their output
	 * 
	 * @param nodeAnalysisResult
	 *            the log analysis result obtained for the node
	 */
	private void addToLogMap(final Map<String, JobBean> nodeAnalysisResult) {
		String jobId = null;
		JobBean existingJobBean = null;
		JobBean nodeJobBean = null;

		// if node analysis result is null, do nothing
		if (nodeAnalysisResult == null){
			return;
		}

		for (Map.Entry<String, JobBean> pairs : nodeAnalysisResult.entrySet()) {
			jobId = pairs.getKey();
			nodeJobBean = pairs.getValue();
			if (nodeJobBean != null) {
				existingJobBean = logMap.get(jobId);

				// if both results are not null,combine them
				if (existingJobBean != null) {
					existingJobBean = addToJobMap(existingJobBean, nodeJobBean);
					logMap.put(jobId, existingJobBean);
				}
				// else add the node result
				else {
					logMap.put(jobId, nodeJobBean);
				}
			}
		}
	}

	/**
	 * Checks if the Mapper or Reducer for a job running on the node already
	 * exists, If not,adds the result of Mapper or reducer to the Job, otherwise
	 * combines their output
	 * 
	 * @param existingJobBean
	 *            the existing log analysis for a Job on the cluster
	 * @param nodeJobBean
	 *            the log analysis result for a Job of the node
	 * @return Map the combined results for a Job
	 */
	private JobBean addToJobMap(final JobBean existingJobBean,
			final JobBean nodeJobBean) {
		LOGGER.debug("Adding result to jobMap");
		String className = null;
		MapReduceBean existingMapReduceBean = null;
		MapReduceBean nodeMapReduceBean = null;
		final Map<String, MapReduceBean> nodeJobMap = nodeJobBean.getJobMap();
		final Map<String, MapReduceBean> existingJobMap = existingJobBean
				.getJobMap();

		// if node result for the job is null,return the existing result for the
		// job
		if (nodeJobMap != null) {

			// if existing result for the job is null,return the node result for
			// the job
			if (existingJobMap != null) {
				for (Map.Entry<String, MapReduceBean> pairs : nodeJobMap
						.entrySet()) {
					className = (String) pairs.getKey();
					nodeMapReduceBean = pairs.getValue();
					if (nodeMapReduceBean != null) {
						existingMapReduceBean = existingJobMap.get(className);
						if (existingMapReduceBean != null) {

							// combine their results and store it in existing
							// map reduce bean
							existingMapReduceBean = addToMapReduceMap(
									existingMapReduceBean, nodeMapReduceBean);
							existingJobMap
									.put(className, existingMapReduceBean);
						} else {
							existingJobMap.put(className, nodeMapReduceBean);
						}
						existingJobBean.setJobMap(existingJobMap);
					}
				}
				addCumulativeCounters(existingJobBean, existingJobMap);
				return existingJobBean;
			} else {
				return nodeJobBean;
			}
		} else {
			return existingJobBean;
		}

	}

	/**
	 * Adds the result of the particular node to the result of the Mapper or
	 * Reducer
	 * 
	 * @param existingMapReduceBean
	 *            the existing log analysis result for a Mapper or Reducer
	 * @param nodeMapReduceBean
	 *            the log analysis result for a Mapper or Reducer for a job on
	 *            the node
	 * @return Map the combined results for a Mapper or Reducer
	 */
	private MapReduceBean addToMapReduceMap(
			final MapReduceBean existingMapReduceBean,
			final MapReduceBean nodeMapReduceBean) {
		LOGGER.debug("Adding results to MapReduceMap Map");
		String nodeName = null;
		NodeBean nodeBean = null;
		final Map<String, NodeBean> nodeMapReduceMap = nodeMapReduceBean
				.getMapReduceMap();
		final Map<String, NodeBean> existingMapReduceMap = existingMapReduceBean
				.getMapReduceMap();
		if (nodeMapReduceMap != null) {
			if (existingMapReduceMap != null) {
				final Iterator<Map.Entry<String, NodeBean>> iterator = nodeMapReduceMap
						.entrySet().iterator();
				final Map.Entry<String, NodeBean> pairs = (Map.Entry<String, NodeBean>) iterator
						.next();
				nodeName = pairs.getKey();
				nodeBean = pairs.getValue();
				existingMapReduceMap.put(nodeName, nodeBean);
				addCumulativeCounters(existingMapReduceBean,
						existingMapReduceMap);
				existingMapReduceBean.setMapReduceMap(existingMapReduceMap);
				return existingMapReduceBean;
			} else {
				return nodeMapReduceBean;
			}
		} else {
			return existingMapReduceBean;
		}

	}

	/**
	 * Iterates over various parameters in sub map and adds to the parameters of
	 * the bean
	 * 
	 * @param logAnalysisBean
	 *            the bean storing the values of various parameters(number of
	 *            input keys,context writes ,etc)
	 * @param subMap
	 *            the sub map
	 * @return Map the cumulative result
	 */
	@SuppressWarnings("rawtypes")
	private void addCumulativeCounters(AbstractLogAnalysisBean logAnalysisBean,
			Map subMap) {
		int totalInputKeys = 0;
		int totalContextWrites = 0;
		int totalUnmatchedKeys = 0;
		int totalUnmatchedValues = 0;
		int subUnmatchedKeys = 0;
		int subUnmatchedValues = 0;
		AbstractLogAnalysisBean childLogAnalysisBean = null;
		if (subMap != null) {
			Iterator iterator = subMap.entrySet().iterator();
			Map.Entry pairs;
			while (iterator.hasNext()) {
				pairs = (Map.Entry) iterator.next();
				childLogAnalysisBean = (AbstractLogAnalysisBean) pairs
						.getValue();
				totalInputKeys += childLogAnalysisBean.getTotalInputKeys();
				totalContextWrites += childLogAnalysisBean
						.getTotalContextWrites();
				subUnmatchedKeys = childLogAnalysisBean.getTotalUnmatchedKeys();
				subUnmatchedValues = childLogAnalysisBean
						.getTotalUnmatchedValues();

				if (subUnmatchedKeys > 0) {
					totalUnmatchedKeys += subUnmatchedKeys;
				}

				if (subUnmatchedValues > 0) {
					totalUnmatchedValues += subUnmatchedValues;
				}

			}
		}

		if (totalUnmatchedKeys == 0) {
			totalUnmatchedKeys = -1;
		}

		if (totalUnmatchedValues == 0) {
			totalUnmatchedValues = -1;
		}

		logAnalysisBean.setTotalInputKeys(totalInputKeys);
		logAnalysisBean.setTotalContextWrites(totalContextWrites);
		logAnalysisBean.setTotalUnmatchedKeys(totalUnmatchedKeys);
		logAnalysisBean.setTotalUnmatchedValues(totalUnmatchedValues);
	}

	/**
	 * Sets the counters for job chaining if enabled
	 */
	private void setJobChainCounters() {
		if (jobChain == null || jobChain.size() == 0) {
			return;
		}
		String name = null;
		JobBean jobBean = null;
		for (ChainingInfoBean chainingInfoBean : jobChain) {
			name = chainingInfoBean.getName();
			jobBean = logMap.get(name);
			chainingInfoBean.setInputKeys(jobBean.getTotalInputKeys());
			chainingInfoBean.setContextWrites(jobBean.getTotalContextWrites());
		}
	}

	/**
	 * Sets the counters for mapReduce chaining if enabled
	 * 
	 * @param mrChainSortedMap
	 *            contains sorted list of Mappers and Reducers job-wise
	 * @return result of mapreduce chaining job-wise
	 */
	private Map<String, Map<String, List<ChainingInfoBean>>> setMRChainCounters(
			Map<String, Map<String, List<String>>> mrChainSortedMap) {

		Map<String, Map<String, List<ChainingInfoBean>>> mrChain = null;

		String jobId = null;
		Map<String, List<String>> jobDetails;
		List<String> mapChainList = null;
		List<String> reduceChainList = null;
		List<ChainingInfoBean> mapList = null;
		List<ChainingInfoBean> reduceList = null;
		Map<String, List<ChainingInfoBean>> mrChainMap = null;

		for (Map.Entry<String, Map<String, List<String>>> pairs : mrChainSortedMap
				.entrySet()) {

			jobId = pairs.getKey();
			jobDetails = pairs.getValue();

			if (jobDetails != null) {

				mapChainList = jobDetails.get(LPConstants.MAP_METHOD);
				reduceChainList = jobDetails.get(LPConstants.REDUCE_METHOD);

			
			
				if (mrChain == null) {
					mrChain = new HashMap<String, Map<String, List<ChainingInfoBean>>>();
				}

				JobBean jobBean = logMap.get(jobId);
				Map<String, MapReduceBean> jobMap = jobBean.getJobMap();

				MapReduceBean mapReduceBean = null;
				ChainingInfoBean chainingInfoBean = null;

				if (mapChainList != null) {

					jobBean.setTotalInputKeys(jobMap.get(mapChainList.get(0))
							.getTotalInputKeys());
					mapList = new ArrayList<ChainingInfoBean>();
					mrChainMap = new HashMap<String, List<ChainingInfoBean>>();

					for (String str : mapChainList) {

						chainingInfoBean = new ChainingInfoBean();
						mapReduceBean = jobMap.get(str);
						chainingInfoBean.setName(str);
						chainingInfoBean.setInputKeys(mapReduceBean
								.getTotalInputKeys());
						chainingInfoBean.setContextWrites(mapReduceBean
								.getTotalContextWrites());
						mapList.add(chainingInfoBean);
					}

					mrChainMap.put("mapChainList", mapList);

				}

				if (reduceChainList != null) {

					jobBean.setTotalContextWrites(jobMap.get(
							reduceChainList.get(reduceChainList.size() - 1))
							.getTotalContextWrites());

					reduceList = new ArrayList<ChainingInfoBean>();

					if (mrChainMap == null) {
						mrChainMap = new HashMap<String, List<ChainingInfoBean>>();
					}

					for (String str : reduceChainList) {

						chainingInfoBean = new ChainingInfoBean();
						mapReduceBean = jobMap.get(str);
						chainingInfoBean.setName(str);
						chainingInfoBean.setInputKeys(mapReduceBean
								.getTotalInputKeys());
						chainingInfoBean.setContextWrites(mapReduceBean
								.getTotalContextWrites());
						reduceList.add(chainingInfoBean);
					}

					mrChainMap.put("reduceChainList", reduceList);
				}

				mrChain.put(jobId, mrChainMap);

			}
		}

		return mrChain;
	}

	/**
	 * 
	 * @return the map containing information related to partitioning
	 */
	private Map<String, List<PartitionerInfoBean>> getPartitionCounters(
			Map<String, Map<String, List<String>>> mrChainSortedMap) {

		Map<String, List<PartitionerInfoBean>> partitionerMap = new HashMap<String, List<PartitionerInfoBean>>();
		List<PartitionerInfoBean> reducerInstanceList = null;

		String jobId = null;
		JobBean jobBean = null;
		String reducerName = null;
		Map<String, MapReduceBean> jobMap = null;

		for (Map.Entry<String, JobBean> jobPairs : logMap.entrySet()) {
			jobId = jobPairs.getKey();
			reducerName = getReduceNameFromChainList(mrChainSortedMap, jobId);
			jobBean = jobPairs.getValue();
			jobMap = jobBean.getJobMap();

			if (jobMap != null) {

				numOfReducers = 0;
				totalReducerInputKeys = 0;
				reducerInstanceList = new ArrayList<PartitionerInfoBean>();
				Map<String, NodeBean> mapReduceMap = null;
				MapReduceBean mapReduceBean = null;
				String mapperReducerName = null;
				for (Map.Entry<String, MapReduceBean> pairs : jobMap.entrySet()) {

					mapperReducerName = pairs.getKey();
					if ((reducerName == null)
							|| (reducerName.equals(mapperReducerName))) {

						mapReduceBean = pairs.getValue();
						mapReduceMap = mapReduceBean.getMapReduceMap();

						if (mapReduceMap != null) {
							Map<String, MapReduceInstanceBean> nodeMap = null;
							for (NodeBean nodeBean : mapReduceMap.values()) {
								nodeMap = nodeBean.getNodeMap();

								if (nodeMap != null) {
									reducerInstanceList=calculateReducerInputkeysPartition(reducerInstanceList, nodeMap);
								}
							}
						}
					}

				}

				if (numOfReducers > 0) {
					int idealDistribution = totalReducerInputKeys
							/ numOfReducers;
					reducerInstanceList=calculatePartitionVariance(reducerInstanceList, idealDistribution);
					partitionerMap.put(jobId, reducerInstanceList);

				}
			}

		}

		return partitionerMap;

	}
	private List<PartitionerInfoBean> calculatePartitionVariance(List<PartitionerInfoBean> reducerInstanceList, int idealDistribution) {
		List<PartitionerInfoBean> tempReducerInstanceList=reducerInstanceList;
		for (PartitionerInfoBean partitionerInfoBean : tempReducerInstanceList) {
			int inputKeys = partitionerInfoBean.getInputKeys();
			float variance = (float) Math.abs(idealDistribution
					- inputKeys)
					/ idealDistribution * InstrumentConstants.HUNDRED;
			partitionerInfoBean
					.setIdealDistribution(idealDistribution);
			partitionerInfoBean.setVariance(variance);
		}
		return tempReducerInstanceList;
	}
	/***
	 * calculate partitioning of input keys among reducers
	 * @param reducerInstanceList, list of reducer instances
	 * @param nodeMap,
	 * @return instance of reducers after calculating reducer instance list
	 */
	private List<PartitionerInfoBean>  calculateReducerInputkeysPartition(final List<PartitionerInfoBean> reducerInstanceList, Map<String, MapReduceInstanceBean> nodeMap) {
		PartitionerInfoBean partitionerInfoBean = null;
		String mapReduceInstanceId = null;
		MapReduceInstanceBean mapReduceInstanceBean = null;

		for (Map.Entry<String, MapReduceInstanceBean> instancePairs : nodeMap
				.entrySet()) {
			mapReduceInstanceId = instancePairs
					.getKey();
			mapReduceInstanceBean = instancePairs
					.getValue();

			if (mapReduceInstanceId
					.contains(LPConstants.REDUCER_IDENTIFIER)) {
				numOfReducers++;
				int inputKeys = mapReduceInstanceBean
						.getTotalInputKeys();
				totalReducerInputKeys += inputKeys;
				partitionerInfoBean = new PartitionerInfoBean();
				partitionerInfoBean
						.setName(mapReduceInstanceId);
				partitionerInfoBean
						.setInputKeys(inputKeys);
				reducerInstanceList
						.add(partitionerInfoBean);
			}

		}
		return reducerInstanceList;
	}

	/**
	 * To get the name of the reducer from the map-reduce chain list
	 * 
	 * @param mrChainSortedMap
	 * @param jobId
	 * @return the name of the reducer
	 */
	private String getReduceNameFromChainList(
			Map<String, Map<String, List<String>>> mrChainSortedMap,
			String jobId) {
		String reducerName = null;

		if (mrChainSortedMap != null) {

			Map<String, List<String>> jobDetails = mrChainSortedMap.get(jobId);

			if (jobDetails != null) {
				List<String> reduceChainList = jobDetails
						.get(LPConstants.REDUCE_METHOD);

				if ((reduceChainList != null) && (reduceChainList.size() > 0)) {
					reducerName = reduceChainList.get(0);
				}
			}
		}

		return reducerName;

	}

	public static Properties getSystemTable(){
		return props;
	}
}
