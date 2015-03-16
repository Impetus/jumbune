package org.jumbune.debugger.log.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;




/**
 * It takes the map of the list of various log files of the node as input and
 * returns the result of log processing for the node.
 * 
 */
public class LogAnalyzer {
	/**
	 * logMap - Map to store the final cluster-wide result of log analysis.
	 */
	private Map<String, JobBean> logMap = new HashMap<String, JobBean>();
	
	/**
	 * nodeIP - IP address of the node whose log files are to be analyzed.
	 */
	private String nodeIP;

	/**
	 * mapperReducerName - Name of the Mapper or Reducer.
	 */
	private String mapperReducerName;

	/**
	 * method - name of the method of the Mapper or Reducer.
	 */
	private String method;

	/**
	 * jobId - The job name and the job ID separated by underscore..
	 */
	private String jobId;

	/**
	 * mapReduceInstanceId - The ID of the Instance of the Mapper or Reducer.
	 */
	private String mapReduceInstanceId;

	/**
	 * message - Custom messages inserted during the instrumentation for log
	 * analysis.
	 */
	private String message;

	/**
	 * keyType - The type of counter being processed.
	 */
	private String keyType;

	/**
	 * keyValue - The value of counter being processed.
	 */
	private String keyValue;

	/**
	 * mrName - The name of the Mapper or Reducer.
	 */
	private String mrName;

	/**
	 * currentExpCounter - The name of the expression counter for which the
	 * processing is being done.
	 */
	private String currentExpCounter = LPConstants.NOT_AVAILABLE;

	/**
	 * currentMethod - The name of the current method.
	 */
	private String currentMethod = LPConstants.NOT_AVAILABLE;
	
	private boolean writeKeyValue;
	
	private String blockName = null;
	
	private String lineNumber = null;
	/**
	 * Analyzes logs and return the analysis result for the node
	 * 
	 * @param fileListMap
	 *            the map of lists of different log files for the node
	 * @return the log analysis result for the node
	 * @throws IOException
	 */
	public Map<String, JobBean> analyzeLogs(final String nodeIP,
			final Map<String, List<String>> fileListMap, Loader loader) throws IOException {
		
		if (fileListMap == null){
			return logMap;
		}
		this.nodeIP = nodeIP;
		YamlLoader yamlLoader = (YamlLoader) loader;
		Properties props = LogAnalyzerUtil.getSystemTable();
		
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		boolean logKeyValues = yamlConfig.getLogKeyValues().getEnumValue();
		
		BufferedReader bufferedReader = null;
		FileOutputStream out = null;
		String[] tuples, temp;
		String excelFileLoc = null;
		Stack<String[]> stack = null;
		HSSFWorkbook workbook = null;
		List<String> validationClassSymbols = null;
		HashMap<String, HSSFSheet> sheets = null;
		
		if (logKeyValues == true) {
			workbook = new HSSFWorkbook();
			validationClassSymbols = getValidationClassSymbol(props, yamlLoader);
			sheets = getSheets(workbook, validationClassSymbols, props);
		}

		writeKeyValue = false;
		for (Map.Entry<String, List<String>> pairs : fileListMap.entrySet()) {
			List<String> fileList = pairs.getValue();
			try {
				for (String fileName : fileList) {
					bufferedReader = new BufferedReader(new FileReader(fileName));
					String line = null;
					if (logKeyValues && isValidationClassFile(fileName,validationClassSymbols )) {
						excelFileLoc = fileName;		
						stack = new Stack<String[]>();
					
						while ((line = bufferedReader.readLine()) != null) {
							// parses the line and stores the result in lineMap
							if(line==null || line.trim().isEmpty()){
								continue;
							}
							parseLine(line,props);
							if ((LPConstants.NOT_AVAILABLE.equals(currentExpCounter))
									&& (!LPConstants.METHODS_CHECK_LIST
											.contains(message))) {
								continue;
							}
							// process the line and add the result to logMap
							processLine();
						
							tuples = line.split(LPConstants.PIPE_SEPARATOR, InstrumentConstants.SIX);
						
							if (tuples[2].endsWith("En")) {
								stack.push(tuples);
							} else if (tuples[2].endsWith("Ex")) {
								stack.pop();
							}
							if (writeKeyValue == true) {
								writeKeyValue = false;
								temp = stack.peek();
								temp[2] = blockName;
								temp[3] = lineNumber;
								addRow(temp, tuples, sheets, props);
							}
						}							
						
					} else {
						
						while ((line = bufferedReader.readLine()) != null) {
							// parses the line and stores the result in lineMap
							if(line==null || line.trim().isEmpty()){
								continue;
							}
							parseLine(line,props);
							if ((LPConstants.NOT_AVAILABLE.equals(currentExpCounter))
									&& (!LPConstants.METHODS_CHECK_LIST
											.contains(message))) {
								continue;
							}
							// process the line and add the result to logMap
							processLine();
						}
					
					}
					addRecursiveCounters();
				}
			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}
		}
		if (excelFileLoc != null) {
			String dir = excelFileLoc.substring(0,excelFileLoc.lastIndexOf("/")+1);
			excelFileLoc = dir + "Unmatched_Keys_and_Values.xls";
			try {
				out = new FileOutputStream(new File(excelFileLoc));
				workbook.write(out);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
		return logMap;
	}
	
	/**
	 *  It return hashmap containing symbols and references of excel sheet of classes 
	 * @param workbook
	 * @param classSymbols symbols of class
	 * @param props properties
	 * @return
	 */
	public  HashMap<String, HSSFSheet> getSheets(
			HSSFWorkbook workbook, List<String> classSymbols, Properties props) {
		HashMap<String, HSSFSheet> map = 
				new HashMap<String, HSSFSheet>();
		HSSFRow row;
		String className = null;
		String sheetName = null;
		Iterator<String> it = classSymbols.iterator();
		String symbol;
		int i = 1, j;
		while(it.hasNext()) {
			symbol = it.next();
			className = props.getProperty(symbol);
			sheetName = i + ". " + className.substring(className.lastIndexOf(".")+1, className.length());
			i++;
			HSSFSheet sheet = workbook.createSheet(sheetName);
			map.put(symbol, sheet);
			for (j=0; j<=5; j++) {
				sheet.autoSizeColumn(i);
			}
			
			//Adding class name in first row
			
			row = sheet.createRow(0);
			row.createCell(0).setCellValue(className);
			sheet.addMergedRegion(CellRangeAddress.valueOf("A1:F1"));
			
			row = sheet.createRow(1);
			row.createCell(0).setCellValue("Method");
			row.createCell(1).setCellValue("Block Name");
			row.createCell(2).setCellValue("Line No.");
			row.createCell(3).setCellValue("Input Key");
			row.createCell(4).setCellValue("Output Key");
			row.createCell(5).setCellValue("Output Value");
		}
		return map;
	}
	
	/**
	 * Add a row in a sheet
	 * @param blockLine the blockLine
	 * @param keyValueLine line having unmatched key or value
	 * @param sheets
	 */
	public void addRow(String[] blockLine, String[] keyValueLine,
			HashMap<String, HSSFSheet> sheets, Properties props) {
		
		String methodName = props.getProperty(blockLine[0] + "|" + blockLine[1]);
		
		HSSFSheet sheet = sheets.get(blockLine[0]);
		int rowNum = sheet.getLastRowNum() + 1;
		if (rowNum > 10000) {
			return;
		}
		HSSFRow row = sheet.createRow(rowNum);
		
		row.createCell(0).setCellValue(methodName);
		row.createCell(1).setCellValue(blockLine[2]);					//Block Name
		row.createCell(2).setCellValue(blockLine[3]);					//Line Number
		row.createCell(3).setCellValue(keyValueLine[5]);			//Input Key
		if ("K".equals(keyValueLine[3])) {
			row.createCell(4).setCellValue(keyValueLine[4]);		//Output Key
			row.createCell(5).setCellValue("");								//Output Value
		} else {
			row.createCell(4).setCellValue("");
			row.createCell(5).setCellValue(keyValueLine[4]);
		}
	}
	
	/**
	 * Getting symbols of validation classes
	 * @param props properties
	 * @param yamlLoader yaml loader
	 * @return list of symbols of classes for validation
	 */
	public List<String> getValidationClassSymbol(Properties props, YamlLoader yamlLoader) {
		List<Validation> validationClassesList = new ArrayList<Validation>();
		validationClassesList.addAll(yamlLoader.getRegex());
		validationClassesList.addAll(yamlLoader.getUserValidations());
		
		HashMap<String, String> newProps = new HashMap<String, String>();
		for(String key : props.stringPropertyNames()) {
			newProps.put(props.getProperty(key), key);
		}
		String className;
		List<String> classSymbols = new ArrayList<String>();
		
		Iterator<Validation> it = validationClassesList.iterator();
		while (it.hasNext()) {
			className = it.next().getClassname();
			classSymbols.add(newProps.get(className));
		}
		return classSymbols;
	}
	
	/**
	 * Check if the file needs to be validated to not
	 * @param fileName
	 * @param validationClassSymbols list containing symbols of classes that
	 * 	needs to be validated
	 * @return
	 * @throws IOException
	 */
	private boolean isValidationClassFile(String fileName, List<String> validationClassSymbols) throws IOException {
		BufferedReader bufferedReader = null;
		String line;
		bufferedReader = new BufferedReader(new FileReader(fileName));
		line = bufferedReader.readLine();
		bufferedReader.close();
		line = line.substring(0, line.indexOf("|"));
		if (validationClassSymbols.contains(line)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * It parses a line of the log file and returns the parsed data in form of a
	 * map
	 * 
	 * @param aLine
	 *            the line to be parsed
	 * @return Map the map containing the parsed data
	 
	 */
	private void parseLine(final String aLine,Properties props) {

		String[] lineArray = aLine.split(LPConstants.PIPE_SEPARATOR, InstrumentConstants.FIVE);
		if (LPConstants.INFO_MESSAGE.equals(lineArray[2])) {
			
			mapperReducerName = props.getProperty(lineArray[0]);
			jobId = lineArray[InstrumentConstants.THREE];
			mapReduceInstanceId = lineArray[InstrumentConstants.FOUR];
			message = lineArray[2];
			method = LPConstants.NOT_AVAILABLE;
		} else {
			if (LPConstants.EMPTY_STRING.equals(lineArray[0])) {
				if(mapperReducerName != null){
					mrName = (props.getProperty(mapperReducerName) != null) ? props.getProperty(mapperReducerName) :
						mapperReducerName;
				}
			} else {
				mrName = props.getProperty(lineArray[0]);
			}
			
			String mSymbol = new StringBuilder(lineArray[0]).
					append("|").append(lineArray[1]).toString();
			method = (props.getProperty(mSymbol) != null)
					? props.getProperty(mSymbol)
					: lineArray[1] ;
			message = lineArray[2];
			keyType = lineArray[InstrumentConstants.THREE];
			keyValue = lineArray[InstrumentConstants.FOUR];
		}

	}

	/**
	 * It process the line map, gathers information for a Job and adds the
	 * result to logMap. It also filters out lines whose messages or methods are
	 * not being processed.
	 * 
	 * @param lineMap
	 *            the map containing parsed information for the line
	 */
	private void processLine() {
		JobBean jobBean;
		if (((jobId == null) || ((LPConstants.EMPTY_STRING).equals(jobId)))){
			return;
		}

		jobBean = logMap.get(jobId);
		Map<String, MapReduceBean> jobMap = null;
		if (jobBean != null) {
			jobMap = jobBean.getJobMap();
		} else {
			jobBean = new JobBean();
		}
		jobMap = addJobMap(jobMap);
		jobBean.setJobMap(jobMap);
		logMap.put(jobId, jobBean);

	}

	/**
	 * Checks if the Mapper or Reducer for the Job already exists, If not,adds
	 * the result of Mapper or Reducer directly, otherwise combines their
	 * output.
	 * 
	 * @param lineMap
	 *            the map containing parsed information for the line
	 * @param existingJobMap
	 *            the map containing analysis results of various Mappers and
	 *            Reducers for the Job
	 * @return Map the combined result for various Mappers and Reducers for the
	 *         Job
	 */
	private Map<String, MapReduceBean> addJobMap(final
			Map<String, MapReduceBean> existingJobMap) {
		Map<String, MapReduceBean> returnExistedJobMap=existingJobMap;
		Map<String, NodeBean> mapReduceMap = null;
		MapReduceBean mapReduceBean = null;

		// checks if the Job contains entry for any Mapper or Reducer or not
		if (returnExistedJobMap != null) {
			mapReduceBean = returnExistedJobMap.get(mapperReducerName);

			// if particular Mapper or Reducer already exists in the Job result
			if (mapReduceBean != null) {
				mapReduceMap = mapReduceBean.getMapReduceMap();
			}
			// Mapper or Reducer does not exists in the Job results
			else {
				mapReduceBean = new MapReduceBean();
			}
		}
		// adds the entry for Mapper or Reducer
		else {

			// filtering out those classes which are neither a Mapper or Reducer
			// or any other class which gets called by Mapper or Reducer.
			if (LPConstants.METHODS_CHECK_LIST.contains(message)) {
				returnExistedJobMap = new HashMap<String, MapReduceBean>();
				mapReduceBean = new MapReduceBean();
			} else {
				return returnExistedJobMap;
			}

		}
		mapReduceMap = addMapReduceMap(mapReduceMap);

		// add the values of parameters of various Mappers and Reducers to the
		// values of Job parameters(input keys,context writes,etc).
		mapReduceBean.setMapReduceMap(mapReduceMap);
		returnExistedJobMap.put(mapperReducerName, mapReduceBean);
		return returnExistedJobMap;
	}

	/**
	 * Checks if the node entry for the Mapper or Reducer already exists, If
	 * not,adds the result of node directly, otherwise combines their output.
	 * 
	 * @param lineMap
	 *            the map containing parsed information for the line
	 * @param existingMapReduceMap
	 *            the map containing results of various nodes on which Mappers
	 *            and Reducers are running
	 * @return Map the combined result for various nodes for the Mapper or
	 *         Reducer
	 */
	private Map<String, NodeBean> addMapReduceMap(
			final Map<String, NodeBean> existingMapReduceMap) {
		Map<String, MapReduceInstanceBean> nodeMap = null;
		Map<String, NodeBean> combinedExistingMapReduceMap=existingMapReduceMap;
		NodeBean nodeBean = null;

		// checks if the Mapper or Reducer contains entry for any node or not
		if (combinedExistingMapReduceMap != null) {

			// nodeBean stores the parameter values for the node
			nodeBean = combinedExistingMapReduceMap.get(nodeIP);

			// checks if the Mapper or Reducer contains entry for the particular
			// node or not
			if (nodeBean != null) {
				nodeMap = nodeBean.getNodeMap();
			}
			// add a new node
			else {
				nodeBean = new NodeBean();
			}
		}
		// adds the entry for the node in the Mapper or Reducer
		else {
			combinedExistingMapReduceMap = new HashMap<String, NodeBean>();
			nodeBean = new NodeBean();
		}
		nodeMap = addNodeMap(nodeMap);
		nodeBean.setNodeMap(nodeMap);
		combinedExistingMapReduceMap.put(nodeIP, nodeBean);
		return combinedExistingMapReduceMap;
	}

	/**
	 * Checks if the instance of the Mapper or Reducer running on the node
	 * already exists, If not,adds the result of instance directly, otherwise
	 * combines their output.
	 * 
	 * @param lineMap
	 *            the map containing parsed information for the line
	 * @param existingNodeMap
	 *            the map containing results of various instances of Mappers and
	 *            Reducers running on the node
	 * @return Map the combined result for various instances of the Mapper or
	 *         Reducer running on the node
	 */
	private Map<String, MapReduceInstanceBean> addNodeMap(final
			Map<String, MapReduceInstanceBean> existingNodeMap) {
		Map<String, MapReduceInstanceBean> combineExistingNodeMap=existingNodeMap;
		Map<String, ExpressionCounterBean> instanceMap = null;
		MapReduceInstanceBean mapReduceInstanceBean = null;

		// checks if the Node contains entry for any instance of Mapper or
		// Reducer or not
		if (combineExistingNodeMap != null) {
			mapReduceInstanceBean = combineExistingNodeMap.get(mapReduceInstanceId);

			// checks if the Node contains entry for the particular instance of
			// the Mapper or Reducer or not
			if (mapReduceInstanceBean != null) {

				instanceMap = mapReduceInstanceBean.getInstanceMap();
				if (LPConstants.PARTITIONER_ENTRY.equals(message)) {
					mapReduceInstanceBean.setNumOfSamples(Integer
							.parseInt(keyType));
					mapReduceInstanceBean.setTime(Integer.parseInt(keyValue));
					combineExistingNodeMap.put(mapReduceInstanceId,
							mapReduceInstanceBean);
					return combineExistingNodeMap;
				} else {
					instanceMap = addInstanceMap(instanceMap,
							currentExpCounter, false);
				}

			}
			// add a new instance bean
			else {
				mapReduceInstanceBean = new MapReduceInstanceBean();
				instanceMap = addInstanceMap(instanceMap, currentExpCounter,
						false);
			}
		}
		// adds the entry for the instance of the Mapper or Reducer in the node
		else {
			combineExistingNodeMap = new HashMap<String, MapReduceInstanceBean>();
			mapReduceInstanceBean = new MapReduceInstanceBean();
			instanceMap = addInstanceMap(instanceMap, currentExpCounter, false);
		}

		for (ExpressionCounterBean mrBean : instanceMap.values()) {
			mapReduceInstanceBean.setTotalInputKeys(mrBean.getTotalInputKeys());
			mapReduceInstanceBean.setTotalContextWrites(mrBean
					.getTotalContextWrites());
			mapReduceInstanceBean.setTotalUnmatchedKeys(mrBean
					.getTotalUnmatchedKeys());
			mapReduceInstanceBean.setTotalUnmatchedValues(mrBean
					.getTotalUnmatchedValues());
			mapReduceInstanceBean.setInstanceMap(instanceMap);
		}

		combineExistingNodeMap.put(mapReduceInstanceId, mapReduceInstanceBean);

		return combineExistingNodeMap;
	}

	/**
	 * 
	 * @param existingInstanceMap
	 *            the existing instance map
	 * @param currentCounter
	 *            the current counter for which processing is being done
	 * @param isMethod
	 *            whether the counter is a method or not
	 * @return the modified instance Map
	 */
	private Map<String, ExpressionCounterBean> addInstanceMap(final
			Map<String, ExpressionCounterBean> existingInstanceMap,
			final String currentCounter, boolean isMethod) {
		String tempCurrentCounter=currentCounter;
		ExpressionCounterBean expressionCounterBean = null;
		Map<String, ExpressionCounterBean> counterMap = null;
		Map<String, ExpressionCounterBean> modifiedInstanceMap=existingInstanceMap;

		String[] counterArr = tempCurrentCounter.split(LPConstants.FILE_SEPARATOR,
				2);

		if (counterArr.length > 1) {
			String parentCounter = counterArr[0];
			String currCounter = counterArr[1];
			expressionCounterBean = modifiedInstanceMap.get(parentCounter);
			counterMap = expressionCounterBean.getCounterMap();
			counterMap = addInstanceMap(counterMap, currCounter, false);

			return modifiedInstanceMap;

		}
		// the current counter
		else {
			if (modifiedInstanceMap != null) {
				if ((LPConstants.ENTERED_MAP_MESSAGE.equals(message))
						|| (LPConstants.ENTERED_REDUCE_MESSAGE.equals(message))) {
					tempCurrentCounter = method;
					currentExpCounter = method;
					currentMethod = method;
				}
				expressionCounterBean = modifiedInstanceMap.get(tempCurrentCounter);
				if (expressionCounterBean != null) {
					return countEntryExitAndOtherIndex(tempCurrentCounter,
							expressionCounterBean, modifiedInstanceMap);
				}

			} else {
				modifiedInstanceMap = new LinkedHashMap<String, ExpressionCounterBean>();
			}

			expressionCounterBean = new ExpressionCounterBean();

			if (LPConstants.NOT_AVAILABLE.equals(currentExpCounter)) {
				try{
				setCounterDetails(expressionCounterBean);
				}catch(IllegalArgumentException e){
					return modifiedInstanceMap;
				}
				modifiedInstanceMap.put(method, expressionCounterBean);
			} else if ((isMethod)) {
				StringBuilder sb = new StringBuilder(currentExpCounter);
				sb.append(LPConstants.FILE_SEPARATOR).append(tempCurrentCounter);
				currentExpCounter = sb.toString();
				sb.delete(0, sb.length());
				sb.append(currentMethod).append(LPConstants.FILE_SEPARATOR)
						.append(tempCurrentCounter);
				currentMethod = sb.toString();
				expressionCounterBean.setTotalFilteredIn(1);
				expressionCounterBean.setCounterDetails(LPConstants.METHOD);
				modifiedInstanceMap.put(tempCurrentCounter, expressionCounterBean);
			} else {
				StringBuilder sb = new StringBuilder(currentExpCounter);
				sb.append(LPConstants.FILE_SEPARATOR).append(tempCurrentCounter);
				currentExpCounter = sb.toString();
				expressionCounterBean.setTotalFilteredIn(1);
				expressionCounterBean.setCounterDetails(keyType);
				modifiedInstanceMap.put(tempCurrentCounter, expressionCounterBean);
			}
			return modifiedInstanceMap;

		}
	}

	private void setCounterDetails(ExpressionCounterBean expressionCounterBean){
		if (LPConstants.INFO_MESSAGE.equals(message)) {
			throw new IllegalArgumentException();
		}
		currentExpCounter = method;
		currentMethod = method;
		expressionCounterBean.setTotalInputKeys(1);
		expressionCounterBean.setCounterDetails(LPConstants.METHOD);
	}

	private Map<String, ExpressionCounterBean> countEntryExitAndOtherIndex(
			String tempCurrentCounter,
			ExpressionCounterBean expressionCounterBean,
			Map<String, ExpressionCounterBean> modifiedInstanceMap) {
		int totalInputKeys;
		int totalContextWrites;
		int totalUnmatchedKeys;
		int totalUnmatchedValues;
		int totalFilteredIn;
		int totalFilteredOut;
		Map<String, ExpressionCounterBean> counterMap;
		totalInputKeys = expressionCounterBean.getTotalInputKeys();
		totalContextWrites = expressionCounterBean
				.getTotalContextWrites();
		totalFilteredOut = expressionCounterBean
				.getTotalFilteredOut();
		totalUnmatchedKeys = expressionCounterBean
				.getTotalUnmatchedKeys();
		totalUnmatchedValues = expressionCounterBean
				.getTotalUnmatchedValues();
		totalFilteredIn = expressionCounterBean
				.getTotalFilteredIn();
		counterMap = expressionCounterBean.getCounterMap();

		Integer entryIndex = LPConstants.ENTERING_COUNTER_MESSAGES_MAP
				.get(message);
		Integer exitIndex = LPConstants.EXITING_COUNTER_MESSAGES_MAP
				.get(message);
		Integer otherIndex = LPConstants.OTHER_COUNTER_MESSAGES_MAP
				.get(message);

		// if entering inside any counter
		if (entryIndex != null) {
			counterMap = countEntryIndex(counterMap, entryIndex);

		}

		// if exiting any counter

		else if (exitIndex != null) {
			totalFilteredOut = countExitIndex(totalFilteredOut,
					exitIndex);

		}

		else if (otherIndex != null) {
			return countOuterIndex(counterMap, otherIndex,expressionCounterBean,modifiedInstanceMap,tempCurrentCounter);

		}

		// calculate and add sub maps(by iterating counterMap)
		expressionCounterBean.setCounterMap(counterMap);
		expressionCounterBean
				.setTotalContextWrites(totalContextWrites);
		expressionCounterBean.setTotalFilteredIn(totalFilteredIn);
		expressionCounterBean.setTotalFilteredOut(totalFilteredOut);
		expressionCounterBean.setTotalInputKeys(totalInputKeys);
		expressionCounterBean
				.setTotalUnmatchedKeys(totalUnmatchedKeys);
		expressionCounterBean
				.setTotalUnmatchedValues(totalUnmatchedValues);
		modifiedInstanceMap.put(tempCurrentCounter,
				expressionCounterBean);
		return modifiedInstanceMap;
	}
	private int checkTotalUnmatchedKeyValue(final int totalUnmatchedValues) {
		int tempTotalUnmatchedValues=totalUnmatchedValues;
		
		if (tempTotalUnmatchedValues > 0) {
			tempTotalUnmatchedValues++;
		} else if (tempTotalUnmatchedValues == -1) {
			tempTotalUnmatchedValues = 1;
		}
		return tempTotalUnmatchedValues;
	}
	
	private Map<String, ExpressionCounterBean> countOuterIndex(Map<String, ExpressionCounterBean> counterMap, Integer otherIndex,
			ExpressionCounterBean expressionCounterBean,Map<String, ExpressionCounterBean> instanceMap,String currentCounter) {
		 Map<String, ExpressionCounterBean> tempCounterMap=counterMap;
		int totalInputKeys = 0,totalContextWrites = 0,totalUnmatchedKeys = 0,
		totalUnmatchedValues = 0, totalFilteredIn = 0,totalFilteredOut=0;
		
		totalInputKeys = expressionCounterBean.getTotalInputKeys();
		totalFilteredOut=expressionCounterBean.getTotalFilteredOut();
		totalContextWrites = expressionCounterBean
				.getTotalContextWrites();
		totalFilteredOut = expressionCounterBean
				.getTotalFilteredOut();
		totalUnmatchedKeys = expressionCounterBean
				.getTotalUnmatchedKeys();
		totalUnmatchedValues = expressionCounterBean
				.getTotalUnmatchedValues();
		totalFilteredIn = expressionCounterBean
				.getTotalFilteredIn();
		switch (otherIndex) {
		case 1:
			totalInputKeys++;
			break;
		case 2:
			totalContextWrites++;
			break;
		case InstrumentConstants.THREE:
			writeKeyValue = true;
			if (keyType.equalsIgnoreCase(LPConstants.KEY)) {
				totalUnmatchedKeys=checkTotalUnmatchedKeyValue(totalUnmatchedKeys);
			} else {
				totalUnmatchedValues=checkTotalUnmatchedKeyValue(totalUnmatchedValues);
			}
			break;
		case InstrumentConstants.FOUR:
			tempCounterMap=countInsideMapperAndReducer(tempCounterMap);
			break;
		default:
			break;
		}
		expressionCounterBean.setCounterMap(tempCounterMap);
		expressionCounterBean
				.setTotalContextWrites(totalContextWrites);
		expressionCounterBean.setTotalFilteredIn(totalFilteredIn);
		expressionCounterBean.setTotalFilteredOut(totalFilteredOut);
		expressionCounterBean.setTotalInputKeys(totalInputKeys);
		expressionCounterBean
				.setTotalUnmatchedKeys(totalUnmatchedKeys);
		expressionCounterBean
				.setTotalUnmatchedValues(totalUnmatchedValues);
		instanceMap.put(currentCounter, expressionCounterBean);
		
		return instanceMap;
	}
	private Map<String, ExpressionCounterBean> countInsideMapperAndReducer(Map<String, ExpressionCounterBean> counterMapExpressionBean) {
		Map<String, ExpressionCounterBean> counterMap=counterMapExpressionBean;
		String currCounter = method;
		if (!((LPConstants.MAP_METHOD.equals(method)) || (LPConstants.REDUCE_METHOD
				.equals(method)))) {
			StringBuilder sb = new StringBuilder();
			sb.append(mrName).append(LPConstants.DOT)
					.append(method);
			currCounter = sb.toString();
		}

		if (counterMap != null) {
			ExpressionCounterBean ctrBean = counterMap
					.get(currCounter);
			if (ctrBean != null) {
				ctrBean.setTotalFilteredIn(ctrBean
						.getTotalFilteredIn() + 1);
				StringBuilder sb = new StringBuilder(
						currentExpCounter);
				sb.append(LPConstants.FILE_SEPARATOR)
						.append(currCounter);
				currentExpCounter = sb.toString();
				sb.delete(0, sb.length());
				sb.append(currentMethod)
						.append(LPConstants.FILE_SEPARATOR)
						.append(currCounter);
				currentMethod = sb.toString();
			} else {
				counterMap = addInstanceMap(counterMap,
						currCounter, true);
			}
		} else {
			counterMap = addInstanceMap(counterMap,
					currCounter, true);
		}
		return counterMap;
	}
	
	private int countExitIndex(int totalFilteredOut, Integer exitIndex) {
		int index = 0,tempTotalFilteredOut=totalFilteredOut;
		String counterExited = null;
		switch (exitIndex) {
		case 1:
			currentExpCounter = LPConstants.NOT_AVAILABLE;
			currentMethod = LPConstants.NOT_AVAILABLE;
			break;
		case 2:
			index = currentMethod
					.lastIndexOf(LPConstants.FILE_SEPARATOR);
			String methodExited = currentMethod.substring(
					index + 1, currentMethod.length());
			currentMethod = currentMethod.substring(0, index);
			do {
				index = currentExpCounter
						.lastIndexOf(LPConstants.FILE_SEPARATOR);
				counterExited = currentExpCounter.substring(
						index + 1, currentExpCounter.length());
				currentExpCounter = currentExpCounter
						.substring(0, index);
			} while (!counterExited.equals(methodExited));
			tempTotalFilteredOut++;
			break;
		case InstrumentConstants.THREE:
			index = currentExpCounter
					.lastIndexOf(LPConstants.FILE_SEPARATOR);
			counterExited = currentExpCounter.substring(
					index + 1, currentExpCounter.length());
			currentExpCounter = currentExpCounter.substring(0,
					index);
			if (!counterExited.contains(LPConstants.IF_BLOCK)) {
				index = currentExpCounter
						.lastIndexOf(LPConstants.FILE_SEPARATOR);
				currentExpCounter = currentExpCounter
						.substring(0, index);
			}
			break;
		case InstrumentConstants.FOUR:
			index = currentExpCounter
					.lastIndexOf(LPConstants.FILE_SEPARATOR);
			currentExpCounter = currentExpCounter.substring(0,
					index);
			break;
		default:
			break;
		}
		return tempTotalFilteredOut;
	}

	private Map<String, ExpressionCounterBean> countEntryIndex(
			Map<String, ExpressionCounterBean> tempCounterMap, Integer entryIndex) {
		Map<String, ExpressionCounterBean> counterMap=tempCounterMap;
		String currCounter = null;

		switch (entryIndex) {
		case 1:
			currCounter = LPConstants.IF_BLOCK + keyValue;
			break;
		case 2:
			currCounter = LPConstants.SWITCH + keyValue;
			break;
		case InstrumentConstants.THREE:
			currCounter = LPConstants.SWITCH_CASE + keyValue;
			break;
		case InstrumentConstants.FOUR:
			currCounter = LPConstants.LOOP + keyValue;
			break;
		case InstrumentConstants.FIVE:
			currCounter = LPConstants.IF + keyValue;
			break;
		case InstrumentConstants.SIX:
			currCounter = LPConstants.ELSE_IF + keyValue;
			break;
		case InstrumentConstants.SEVEN:
			currCounter = LPConstants.ELSE;
			break;
		default:
			break;
		}
		blockName = currCounter;
		lineNumber = keyType;
		// if already existing or not
		if (counterMap != null) {
			ExpressionCounterBean ctrBean = counterMap
					.get(currCounter);
			if (ctrBean != null) {
				ctrBean.setTotalFilteredIn(ctrBean
						.getTotalFilteredIn() + 1);
				StringBuilder sb = new StringBuilder(
						currentExpCounter);
				sb.append(LPConstants.FILE_SEPARATOR).append(
						currCounter);
				currentExpCounter = sb.toString();
			} else {
				counterMap = addInstanceMap(counterMap,
						currCounter, false);
			}
		} else {
			counterMap = addInstanceMap(counterMap,
					currCounter, false);
		}
		return counterMap;
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
	private  Map<String, MapReduceBean> confirmJobMap(){
		Map<String, MapReduceBean> jobMap = null;
		
		JobBean jobBean = logMap!=null ? logMap.get(jobId): null;

		if (jobBean == null) {
			throw new IllegalArgumentException();
		}

		jobMap = jobBean.getJobMap();
		if(jobMap==null){
			throw new IllegalArgumentException();
		}
		return jobMap;
	}
	/**
	 * After processing the file,this method adds the cumulative counters for
	 * each job,mapper/reducer,node and instance.
	 */
	private void addRecursiveCounters() {

		Map<String, MapReduceBean> jobMap = null;


		try{
			jobMap=confirmJobMap();
		}catch(IllegalArgumentException iae){
			return;
		}
		MapReduceBean mapReduceBean = jobMap.get(mapperReducerName);

		if (mapReduceBean == null) {
			return;
		}

	
		Map<String, NodeBean> mapReduceMap = mapReduceBean.getMapReduceMap();
		if (mapReduceMap == null) {
			return;
		}

		NodeBean nodeBean = mapReduceMap.get(nodeIP);

		Map<String, MapReduceInstanceBean> nodeMap = nodeBean.getNodeMap();

		if (nodeMap == null) {
			return;
		}

		MapReduceInstanceBean mapReduceInstanceBean = nodeMap
				.get(mapReduceInstanceId);

		if (mapReduceInstanceBean == null) {
			return;
		}

		Map<String, ExpressionCounterBean> instanceMap = mapReduceInstanceBean
				.getInstanceMap();

		if (instanceMap == null) {
			return;
		}
		ExpressionCounterBean ecb = null;
		String methodName = null;
		for (Map.Entry<String, ExpressionCounterBean> pairs : instanceMap
				.entrySet()) {
			methodName = pairs.getKey();
			ecb = pairs.getValue();
			if(ecb!=null){
				setCummulativeInstanceCounters(ecb);
			}

			if (ecb != null) {
				mapReduceInstanceBean.setTotalContextWrites(ecb
						.getTotalContextWrites());
				mapReduceInstanceBean.setTotalUnmatchedKeys(ecb
						.getTotalUnmatchedKeys());
				mapReduceInstanceBean.setTotalUnmatchedValues(ecb
						.getTotalUnmatchedValues());
				addCumulativeCounters(nodeBean, nodeMap);
				addCumulativeCounters(mapReduceBean, mapReduceMap);
				addJobLevelCounters(logMap.get(jobId), jobMap, methodName, ecb);
			}

		}

	}

	/**
	 * For adding the inside counters recursively for a particular expression
	 * counter
	 * 
	 * @param counterBean
	 *            the expression counter bean to store counter details
	 */
	private void setCummulativeInstanceCounters(
			ExpressionCounterBean counterBean) {

			int totalContextWrites = counterBean.getTotalContextWrites();
			int totalUnmatchedKeys = counterBean.getTotalUnmatchedKeys();
			int totalUnmatchedValues = counterBean.getTotalUnmatchedValues();
			int totalFilteredOut = counterBean.getTotalFilteredOut();

			if (totalUnmatchedKeys == -1) {
				totalUnmatchedKeys = 0;
			}

			if (totalUnmatchedValues == -1) {
				totalUnmatchedValues = 0;
			}

			Map<String, ExpressionCounterBean> counterMap = counterBean
					.getCounterMap();

			if (counterMap != null) {

				int subUnmatchedKeys = 0;
				int subUnmatchedValues = 0;

				for (ExpressionCounterBean ecb : counterMap.values()) {

					validateAndSetCommulativeInstanceCounter(ecb);
					totalContextWrites += ecb.getTotalContextWrites();
					subUnmatchedKeys = ecb.getTotalUnmatchedKeys();
					subUnmatchedValues = ecb.getTotalUnmatchedValues();

					if (subUnmatchedKeys > 0) {
						totalUnmatchedKeys += subUnmatchedKeys;
					}
					if (subUnmatchedValues > 0) {
						totalUnmatchedValues += subUnmatchedValues;
					}

				}

			}

			if (totalUnmatchedKeys <= 0) {
				totalUnmatchedKeys = -1;
			}

			if (totalUnmatchedValues <= 0) {
				totalUnmatchedValues = -1;
			}

			counterBean.setCounterMap(counterMap);
			counterBean.setTotalContextWrites(totalContextWrites);
			counterBean.setTotalFilteredOut(totalFilteredOut);
			counterBean.setTotalUnmatchedKeys(totalUnmatchedKeys);
			counterBean.setTotalUnmatchedValues(totalUnmatchedValues);

	}
	private void validateAndSetCommulativeInstanceCounter(ExpressionCounterBean ecb) {
		Map<String, ExpressionCounterBean> ctrMap;
		ctrMap = ecb.getCounterMap();
		
		if ((ctrMap != null) && (ctrMap.size() > 0) && ecb!=null) {
			setCummulativeInstanceCounters(ecb);
		}
	}

	/**
	 * Adding Job level Counters
	 * 
	 * @param logAnalysisBean
	 *            Pojo containing details about the job counters
	 * @param subMap
	 *            Map containing details about Mappers and Reducers for the job
	 * @param methodName
	 *            map or reduce
	 * @param ecb
	 *            Pojo containing details for the instance of Mapper/Reducer
	 */
	@SuppressWarnings("rawtypes")
	private void addJobLevelCounters(AbstractLogAnalysisBean logAnalysisBean,
			Map subMap, String methodName, ExpressionCounterBean ecb) {
		int totalInputKeys = logAnalysisBean.getTotalInputKeys();
		int totalContextWrites = logAnalysisBean.getTotalContextWrites();
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
		if (LPConstants.MAP_METHOD.equals(methodName)) {
			totalInputKeys += ecb.getTotalInputKeys();
		} else {
			totalContextWrites += ecb.getTotalContextWrites();
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

}
