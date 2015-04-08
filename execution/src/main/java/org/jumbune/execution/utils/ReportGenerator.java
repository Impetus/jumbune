package org.jumbune.execution.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jumbune.common.utils.Constants;
import org.jumbune.debugger.log.processing.DebugAnalysisBean;
import org.jumbune.debugger.log.processing.ExpressionCounterBean;
import org.jumbune.debugger.log.processing.JobBean;
import org.jumbune.debugger.log.processing.MapReduceBean;
import org.jumbune.debugger.log.processing.MapReduceInstanceBean;
import org.jumbune.debugger.log.processing.NodeBean;
import org.jumbune.utils.ExportUtil;
import org.jumbune.utils.beans.Worksheet;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Utility class for Export operations
 *
 */
public final class ReportGenerator {
	/**
	 * private constructor
	 */
	private ReportGenerator(){
		
	}
	
	/**
	 * writes the given content in an excel sheet
	 * @param reportsJson
	 * @param exportFile
	 * @param reports
	 * @return
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public static boolean writesToExcelFile(Map<String, String> reportsJson,
			String exportFile, Map<String, String> reports)
					throws IOException, JumbuneException {
		FileOutputStream out = null;
		try {
			Worksheet worksheet = new Worksheet();
			ExportUtil.setHeaderStyle(worksheet);
			for (Map.Entry<String, String> json : reportsJson.entrySet()) {
				String reportName = json.getKey();

				switch (reportName) {
				case Constants.DATA_VALIDATION:
					String jsonReport = reports.get(Constants.DATA_VALIDATION);
					ExportUtil.createDataQualityExcelReport(worksheet, jsonReport);
					break;
				case Constants.DEBUG_ANALYZER:
					ReportGenerator.createDebugAnalyzer(worksheet, json.getValue(), "DebugAnalyzer");
					break;

				}
			}
			out = new FileOutputStream(exportFile);
			ExportUtil.writeWorksheet(worksheet, out);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return true;
	}

	static void createDebugAnalyzer(Worksheet worksheet, String jSONString, String sheetName) {
		Gson gson = new Gson();
		
		Type type = new TypeToken<Map<String, DebugAnalysisBean>>() {
		}.getType();
		final Map<String, DebugAnalysisBean> debugAnalysis = gson.fromJson(jSONString, type);
		if(debugAnalysis.get("debugAnalysis")==null){
			return;
		}
		final Map<String, JobBean> logChurningMap = debugAnalysis.get("debugAnalysis").getLogMap();
		ExportUtil.addSheet(worksheet, sheetName);

		List<String> stringList = new ArrayList<String>();
		List<String> rowValueList = new ArrayList<String>();

		appendDocHeaders(stringList);
		ExportUtil.addHeader(worksheet, sheetName, null, stringList);
		int rowIndex = 1;
		int columnIndex = 1;
		for (Map.Entry<String, JobBean> it1 : logChurningMap.entrySet()) {
			rowIndex = processLogChurningMap(worksheet, sheetName, stringList,
					rowValueList, rowIndex, columnIndex, it1);
		}
	}
	
	private static int processLogChurningMap(Worksheet worksheet, String sheetName,
			List<String> stringList, List<String> rowValueList, int rowIndex,
			int columnIndex, Map.Entry<String, JobBean> it1) {
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);

		stringList.add(it1.getKey());
		int jobRow = ExportUtil.addRow(worksheet, sheetName, rowIndexTmp, stringList);
		
		rowValueList.add(convertToString(it1.getValue().getTotalInputKeys()));
		rowValueList.add(convertToString(it1.getValue().getTotalContextWrites()));
		tempValue=it1.getValue().getTotalUnmatchedKeys();
		tempValue=it1.getValue().getTotalUnmatchedKeys();
		if((tempValue)==-1){
			rowValueList.add("-");
		}else{
		rowValueList.add(convertToString(tempValue));
		}
		tempValue=it1.getValue().getTotalUnmatchedValues();
		if(tempValue==-1){
			rowValueList.add("-");
		}else{
			rowValueList.add(convertToString(tempValue));
		}
		ExportUtil.addValuesToRow(worksheet, sheetName, jobRow, columnIndex, rowValueList);

		rowIndexTmp++;
		Map<String, MapReduceBean> jobMap = it1.getValue().getJobMap();

		for (Map.Entry<String, MapReduceBean> it2 : jobMap.entrySet()) {
			clearLists(stringList, rowValueList);

			stringList.add(it2.getKey());

			int mrRow = ExportUtil.addRow(worksheet, sheetName, rowIndexTmp, stringList);

			rowValueList.add(convertToString(it2.getValue().getTotalInputKeys()));
			rowValueList.add(convertToString(it2.getValue().getTotalContextWrites()));
			tempValue=it2.getValue().getTotalUnmatchedKeys();
			if((tempValue)==-1){
				rowValueList.add("-");
			}else{
			rowValueList.add(convertToString(tempValue));
			}
			tempValue=it2.getValue().getTotalUnmatchedValues();
			if(tempValue==-1){
				rowValueList.add("-");
			}else{
				rowValueList.add(convertToString(tempValue));
			}
			ExportUtil.addValuesToRow(worksheet, sheetName, mrRow, columnIndex, rowValueList);

			rowIndexTmp++;

			Map<String, NodeBean> mapReduceMap = it2.getValue().getMapReduceMap();

			for (Map.Entry<String, NodeBean> it3 : mapReduceMap.entrySet()) {

				rowIndexTmp = processMapReduceMap(worksheet, sheetName, stringList,
						rowValueList, rowIndexTmp, columnIndex, it3);
			}
		}
		return rowIndexTmp;
	}

	private static void clearLists(List<String> stringList,
			List<String> rowValueList) {
		if (!(rowValueList.size() == 0)){
			rowValueList.clear();
		}

		if (!(stringList.size() == 0)){
			stringList.clear();
		}
	}

	private static int processMapReduceMap(Worksheet worksheet, String sheetName,
			List<String> stringList, List<String> rowValueList, int rowIndex,
			int columnIndex, Map.Entry<String, NodeBean> it3) {
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);

		stringList.add(it3.getKey());
		int nodeRow = ExportUtil.addRow(worksheet, sheetName, rowIndexTmp, stringList);
		rowValueList.add(convertToString(it3.getValue().getTotalInputKeys()));
		rowValueList.add(convertToString(it3.getValue().getTotalContextWrites()));
		tempValue=it3.getValue().getTotalUnmatchedKeys();
		if((tempValue)==-1){
			rowValueList.add("-");
		}else{
		rowValueList.add(convertToString(tempValue));
		}
		tempValue=it3.getValue().getTotalUnmatchedValues();
		if((tempValue)==-1){
			rowValueList.add("-");
		}else{
			rowValueList.add(convertToString(tempValue));
		}
		ExportUtil.addValuesToRow(worksheet, sheetName, nodeRow, columnIndex, rowValueList);


		rowIndexTmp++;

		Map<String, MapReduceInstanceBean> nodeMap = it3.getValue().getNodeMap();

		for (Map.Entry<String, MapReduceInstanceBean> it4 : nodeMap.entrySet()) {

			rowIndexTmp = processNodeMap(worksheet, sheetName, stringList, rowValueList,
					rowIndexTmp, columnIndex, it4);

		}
		return rowIndexTmp;
	}

	private static int processNodeMap(Worksheet worksheet, String sheetName,
			List<String> stringList, List<String> rowValueList, int rowIndex,
			int columnIndex, Map.Entry<String, MapReduceInstanceBean> it4) {
			
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);
		stringList.add(it4.getKey());
		int instanceRow = ExportUtil.addRow(worksheet, sheetName, rowIndexTmp, stringList);

		rowValueList.add(convertToString(it4.getValue().getTotalInputKeys()));
		rowValueList.add(convertToString(it4.getValue().getTotalContextWrites()));
		tempValue=it4.getValue().getTotalUnmatchedKeys();
		if(tempValue==-1){
			rowValueList.add("-");
		}else{
		rowValueList.add(convertToString(tempValue));
		}
		tempValue=it4.getValue().getTotalUnmatchedValues();
		if(tempValue==-1){
			rowValueList.add("-");
		}else{
			rowValueList.add(convertToString(tempValue));
		}
		ExportUtil.addValuesToRow(worksheet, sheetName, instanceRow, columnIndex, rowValueList);

		rowIndexTmp++;
		
		Map<String, ExpressionCounterBean> instanceMap = it4.getValue().getInstanceMap();
		if(instanceMap!=null && !instanceMap.isEmpty()){
			rowIndexTmp=ReportGenerator.traverseExpresionCounterMap(instanceMap, stringList, rowValueList, worksheet, sheetName,  --rowIndexTmp, columnIndex);
		}
		return rowIndexTmp;
	}

	private static void appendDocHeaders(List<String> stringList) {
		stringList.add(" ");
		stringList.add("TotalInputKeys");
		stringList.add("TotalContextWrite");
		stringList.add("TotalUnmatchedKeys");
		stringList.add("TotalUnmatchedValues");
		stringList.add("TotalFilteredIn");
		stringList.add("TotalFilteredOut");
	}


	private static String convertToString(int value) {
		return Integer.toString(value);
		
	}

	/**
	 * traverses the counter map
	 * @param instanceMap
	 * @param stringList
	 * @param rowValueList
	 * @param worksheet
	 * @param sheetName
	 * @param rowNo
	 * @param columnNo
	 * @return
	 */
	public static int traverseExpresionCounterMap(Map<String, ExpressionCounterBean> instanceMap,
			List<String> stringList, List<String> rowValueList, Worksheet worksheet, String sheetName,
			int rowNo, int columnNo) {
		int rowNoTmp = rowNo;
		rowNoTmp++;

		for (Map.Entry<String, ExpressionCounterBean> it5 : instanceMap.entrySet()) {
			int tempValue=0;
			clearLists(rowValueList, stringList);
			stringList.add(it5.getKey());
			int counterRow = ExportUtil.addRow(worksheet, sheetName, rowNoTmp, stringList);

			rowValueList.add(convertToString(it5.getValue().getTotalInputKeys()));
			rowValueList.add(convertToString(it5.getValue().getTotalContextWrites()));
			tempValue=it5.getValue().getTotalUnmatchedKeys();
			if((tempValue)==-1){
				rowValueList.add("-");
			}else{
			rowValueList.add(convertToString(tempValue));
			}
			tempValue=it5.getValue().getTotalUnmatchedValues();
			if((tempValue)==-1){
				rowValueList.add("-");
			}else{
				rowValueList.add(convertToString(tempValue));
			}
			rowValueList.add(convertToString(it5.getValue().getTotalFilteredIn()));
			rowValueList.add(convertToString(it5.getValue().getTotalFilteredOut()));

			ExportUtil.addValuesToRow(worksheet, sheetName, counterRow, columnNo, rowValueList);
			rowNoTmp++;


			Map<String, ExpressionCounterBean> counterMap = it5.getValue().getCounterMap();
			if (counterMap == null) {
				continue;
			} else{
				rowNoTmp = traverseExpresionCounterMap(counterMap, stringList, rowValueList, worksheet, sheetName,  --rowNoTmp, columnNo);
			}
		}
		return rowNoTmp;
	}

	
}