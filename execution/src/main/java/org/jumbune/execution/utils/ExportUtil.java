package org.jumbune.execution.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.jumbune.common.utils.Constants;
import org.jumbune.debugger.log.processing.DebugAnalysisBean;
import org.jumbune.debugger.log.processing.ExpressionCounterBean;
import org.jumbune.debugger.log.processing.JobBean;
import org.jumbune.debugger.log.processing.MapReduceBean;
import org.jumbune.debugger.log.processing.MapReduceInstanceBean;
import org.jumbune.debugger.log.processing.NodeBean;
import org.jumbune.utils.UtilitiesConstants;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Utility class for Export operations
 *
 */
public final class ExportUtil {

	/**
	 * private constructor
	 */
	private ExportUtil(){
		
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
	public static boolean writesToExcelFile(Map<String, String> reportsJson, String exportFile, Map<String, String> reports)
			throws IOException, JumbuneException {
		FileOutputStream out = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFCellStyle cellStyle = ExportUtil.setHeaderStyle(wb);

			for (Map.Entry<String, String> json : reportsJson.entrySet()) {
				String reportName = json.getKey();

				switch (reportName) {
				case Constants.DATA_VALIDATION:
					String jsonReport = reports.get(Constants.DATA_VALIDATION);
					ExportUtil.createDataValidation(wb, cellStyle, jsonReport);
					break;
				case Constants.DEBUG_ANALYZER:
					ExportUtil.createDebugAnalyzer(wb, json.getValue(), "DebugAnalyzer", cellStyle);
					break;

				}
			}
			out = new FileOutputStream(exportFile);
			wb.write(out);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		return true;
	}

	private static void createDataValidation(HSSFWorkbook wb, HSSFCellStyle cellStyle, String dvReportJson) throws JumbuneException, IOException {
		Set<String> fileSet = new TreeSet<String>();
		HSSFSheet sheet = null;
		sheet = wb.getSheet(UtilitiesConstants.DATA_VALIDATION_SHEET);
		List<String> row = new ArrayList<String>();
		// adding header to the rows
		if (sheet == null) {
			sheet = wb.createSheet(UtilitiesConstants.DATA_VALIDATION_SHEET);
		}
		JsonElement jreportElement = new JsonParser().parse(dvReportJson);
		JsonObject dvReportObject = jreportElement.getAsJsonObject();
		if(!dvReportObject.getAsJsonObject("DVSUMMARY").toString().equals("{}")){
			row.add("TYPE");
			row.add(UtilitiesConstants.TOTAL_VIOLATIONS);
			Set<String> setOfTotalFiles = getFilesList(dvReportJson, fileSet);
			for (String string : setOfTotalFiles) {
				row.add("file : " + string);
			}
			createStringRows(sheet, cellStyle, 0, row);
			
			writeStatsRowByRowToFile(dvReportJson, setOfTotalFiles, sheet, cellStyle);
		}else{
			row.add("NO DATA VIOLATIONS FOUND");
			HSSFRow sheetRow = sheet.createRow(0);
			HSSFCell cellJobName = sheetRow.createCell(0);
			cellJobName.setCellStyle(cellStyle);
			cellJobName.setCellValue(new HSSFRichTextString("NO DATA VIOLATIONS FOUND"));
		}
		
		

	}

	/***
	 * This method writes columns values from dv reports json to cell of xls file
	 * 
	 * @param dvReportJson
	 * @param set
	 * @param sheet
	 * @param cellStyle
	 */
	private static void writeStatsRowByRowToFile(String dvReportJson, Set<String> set, HSSFSheet sheet, HSSFCellStyle cellStyle) {
		JsonElement jsonElement = null;
		List<String> columnValueList = new ArrayList<String>();
		int lineNum = sheet.getLastRowNum();
		JsonElement jreportElement = new JsonParser().parse(dvReportJson);
		JsonObject dvReportObject = jreportElement.getAsJsonObject();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.NULL_CHECK);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.NULL_CHECK);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			createStringRows(sheet, cellStyle, ++lineNum, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.REGEX);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.REGEX);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			createStringRows(sheet, cellStyle, ++lineNum, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.DATA_TYPE);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.DATA_TYPE);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			createStringRows(sheet, cellStyle, ++lineNum, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.NO_OF_VIOLATION);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.NO_OF_VIOLATION);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			createStringRows(sheet, cellStyle, ++lineNum, columnValueList);
		}
	}

	/***
	 * This method gets the list of column values from a Type violation.
	 * 
	 * @param jsonElement
	 * @param set
	 * @param columnValueList
	 * @return
	 */
	private static List<String> getWritableListToWrite(JsonElement jsonElement, Set<String> set, List<String> columnValueList) {
		JsonArray array = null;
		JsonElement totalViolatonElement = null;
		String tempValue = null;
		String fileName = null;
		totalViolatonElement = jsonElement.getAsJsonObject().get(UtilitiesConstants.TOTAL_VIOLATIONS);
		if (totalViolatonElement != null) {
			columnValueList.add(totalViolatonElement.getAsString());
		}
		// if a perticular column does not matches the specified column name due to column has no violation in that perticular field than it writes a
		// single character there just to increase the list
		// In case if array of file violation is null then else condition write character to the precceding columns
		array = jsonElement.getAsJsonObject().getAsJsonArray(UtilitiesConstants.VIOLATION_LIST_TAG);
		if (array != null) {
			for (String columnName : set) {
				fileName = "";
				for (JsonElement element : array) {
					tempValue = element.getAsJsonObject().get(UtilitiesConstants.FILE_NAME_TAG).getAsString();
					if (tempValue.equals(columnName)) {
						fileName = element.getAsJsonObject().get("numOfViolations").getAsString();
					}
				}
				columnValueList.add(fileName);
			}
		} else {
			fileName = "";
			for (@SuppressWarnings("unused") String columnName : set) {
				columnValueList.add(fileName);
			}
		}
		return columnValueList;
	}

	/***
	 * This method retrieve total number of files in which violation occured.
	 * 
	 * @param dvReportJson
	 * @param set
	 *            of string
	 * @return it returns set of files name in string format
	 */
	private static Set<String> getFilesList(String dvReportJson, Set<String> set) {
		JsonElement jsonElement = null;
		JsonElement jreportElement = new JsonParser().parse(dvReportJson);
		JsonObject dvReportObject = jreportElement.getAsJsonObject();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.NULL_CHECK);
		addFlieList(jsonElement, set);
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.REGEX);
		addFlieList(jsonElement, set);
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.DATA_TYPE);
		addFlieList(jsonElement, set);
		return set;
	}

	private static void addFlieList(JsonElement jsonElement, Set<String> set) {
		JsonArray array = jsonElement.getAsJsonObject().getAsJsonArray(UtilitiesConstants.VIOLATION_LIST_TAG);
		if (jsonElement != null && array != null) {
			for (JsonElement element : array) {
				set.add(element.getAsJsonObject().get(UtilitiesConstants.FILE_NAME_TAG).getAsString());
			}
		}
	}

	private static HSSFCellStyle setHeaderStyle(HSSFWorkbook sampleWorkBook) {
		HSSFFont font = sampleWorkBook.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setColor(IndexedColors.PLUM.getIndex());
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle cellStyle = sampleWorkBook.createCellStyle();
		cellStyle.setFont(font);
		return cellStyle;
	}

	static HSSFRow createStringRows(HSSFSheet sheet, HSSFCellStyle cellStyle, int rowNo, List<String> columnValue) {
		HSSFRow row = sheet.createRow(rowNo);
		for (int i = 0; i < columnValue.size(); i++) {
			HSSFCell cellJobName = row.createCell(i);
			cellJobName.setCellStyle(cellStyle);
			cellJobName.setCellValue(new HSSFRichTextString(columnValue.get(i)));
		}

		return row;
	}
	static void addValuesToStringRow(HSSFRow row, HSSFCellStyle cellStyle, int columnNo, List<String> columnValue) {
		
			for (int i = 0; i < columnValue.size(); i++) {
				
				HSSFCell cellJobName = row.createCell(columnNo + i);
				cellJobName.setCellStyle(cellStyle);
				cellJobName.setCellValue(columnValue.get(i));
			}
		}
	static void createIntegerRows(HSSFRow row, HSSFCellStyle cellStyle, int columnNo, List<Integer> columnValue) {

		for (int i = 0; i < columnValue.size(); i++) {
			HSSFCell cellJobName = row.createCell(columnNo + i);
			cellJobName.setCellStyle(cellStyle);
			cellJobName.setCellValue(columnValue.get(i));
		}
	}

	static enum Grade {
		PURE_JOB_JSON, INSTRUMENTED_JOB_JSON, DATA_DELIDATION, PROFILING, LOG_ANALISYS, DATA_SCIENCE
	};

	static void createDebugAnalyzer(HSSFWorkbook wb, String jSONString, String sheetName, HSSFCellStyle cellStyle) {

		Gson gson = new Gson();
		
		Type type = new TypeToken<Map<String, DebugAnalysisBean>>() {
		}.getType();
		final Map<String, DebugAnalysisBean> debugAnalysis = gson.fromJson(jSONString, type);
		if(debugAnalysis.get("debugAnalysis")==null){
			return;
		}
		final Map<String, JobBean> logChurningMap = debugAnalysis.get("debugAnalysis").getLogMap();
		HSSFSheet sheet = wb.createSheet(sheetName);

		

		List<String> stringList = new ArrayList<String>();
		List<String> rowValueList = new ArrayList<String>();

		appendDocHeaders(stringList);

		ExportUtil.createStringRows(sheet, cellStyle, 0, stringList);

		int rowIndex = 1;
		int columnIndex = 1;

		for (Map.Entry<String, JobBean> it1 : logChurningMap.entrySet()) {


			rowIndex = processLogChurningMap(cellStyle, sheet,
					stringList, rowValueList, rowIndex, columnIndex, it1);
		}
		
	}

	private static int processLogChurningMap(HSSFCellStyle cellStyle,
			HSSFSheet sheet, List<String> stringList,
			List<String> rowValueList, int rowIndex, int columnIndex,
			Map.Entry<String, JobBean> it1) {
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);

		stringList.add(it1.getKey());

		HSSFRow jobRow = ExportUtil.createStringRows(sheet, cellStyle, rowIndexTmp, stringList);

		

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
		

		ExportUtil.addValuesToStringRow(jobRow, cellStyle, columnIndex, rowValueList);



		rowIndexTmp++;
		Map<String, MapReduceBean> jobMap = it1.getValue().getJobMap();

		for (Map.Entry<String, MapReduceBean> it2 : jobMap.entrySet()) {
			clearLists(stringList, rowValueList);

			stringList.add(it2.getKey());

			HSSFRow mrRow = ExportUtil.createStringRows(sheet, cellStyle, rowIndexTmp, stringList);

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
			ExportUtil.addValuesToStringRow(mrRow, cellStyle, columnIndex, rowValueList);



			rowIndexTmp++;

			Map<String, NodeBean> mapReduceMap = it2.getValue().getMapReduceMap();

			for (Map.Entry<String, NodeBean> it3 : mapReduceMap.entrySet()) {

				rowIndexTmp = processMapReduceMap(cellStyle, sheet,
						stringList, rowValueList, rowIndexTmp, columnIndex,
						it3);
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

	private static int processMapReduceMap(HSSFCellStyle cellStyle,
			HSSFSheet sheet, List<String> stringList,
			List<String> rowValueList, int rowIndex, int columnIndex,
			Map.Entry<String, NodeBean> it3) {
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);

		stringList.add(it3.getKey());

		HSSFRow nodeRow = ExportUtil.createStringRows(sheet, cellStyle, rowIndexTmp, stringList);
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
		ExportUtil.addValuesToStringRow(nodeRow, cellStyle, columnIndex, rowValueList);


		rowIndexTmp++;

		Map<String, MapReduceInstanceBean> nodeMap = it3.getValue().getNodeMap();

		for (Map.Entry<String, MapReduceInstanceBean> it4 : nodeMap.entrySet()) {

			rowIndexTmp = processNodeMap(cellStyle, sheet,
					stringList, rowValueList, rowIndexTmp,
					columnIndex, it4);

		}
		return rowIndexTmp;
	}

	private static int processNodeMap(HSSFCellStyle cellStyle,
			HSSFSheet sheet, List<String> stringList,
			List<String> rowValueList, int rowIndex, int columnIndex,
			Map.Entry<String, MapReduceInstanceBean> it4) {
		int tempValue;
		int rowIndexTmp = rowIndex;
		clearLists(stringList, rowValueList);
		stringList.add(it4.getKey());

		HSSFRow instanceRow = ExportUtil.createStringRows(sheet, cellStyle, rowIndexTmp, stringList);

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
		
		ExportUtil.addValuesToStringRow(instanceRow, cellStyle, columnIndex, rowValueList);

		rowIndexTmp++;
		
		Map<String, ExpressionCounterBean> instanceMap = it4.getValue().getInstanceMap();
		if(instanceMap!=null && !instanceMap.isEmpty()){
			rowIndexTmp=ExportUtil.traverseExpresionCounterMap(instanceMap, stringList, rowValueList, sheet, cellStyle, --rowIndexTmp, columnIndex);
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
	 * @param sheet
	 * @param cellStyle
	 * @param rowNo
	 * @param columnNo
	 * @return
	 */
	public static int traverseExpresionCounterMap(Map<String, ExpressionCounterBean> instanceMap, List<String> stringList, List<String> rowValueList,
			HSSFSheet sheet, HSSFCellStyle cellStyle, int rowNo, int columnNo) {
		
		int rowNoTmp = rowNo;
		rowNoTmp++;

		for (Map.Entry<String, ExpressionCounterBean> it5 : instanceMap.entrySet()) {
			int tempValue=0;
			clearLists(rowValueList, stringList);
			stringList.add(it5.getKey());

			HSSFRow counterRow = ExportUtil.createStringRows(sheet, cellStyle, rowNoTmp, stringList);

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


			ExportUtil.addValuesToStringRow(counterRow, cellStyle, columnNo, rowValueList);
			rowNoTmp++;


			Map<String, ExpressionCounterBean> counterMap = it5.getValue().getCounterMap();
			if (counterMap == null) {
				continue;
			} else{
				rowNoTmp = traverseExpresionCounterMap(counterMap, stringList, rowValueList, sheet, cellStyle, --rowNoTmp, columnNo);
			}
		}
		return rowNoTmp;
	}

	
}