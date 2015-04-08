package org.jumbune.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jumbune.utils.beans.Worksheet;
import org.jumbune.utils.exception.JumbuneException;



public class ExportUtil {
	
	/**
	 * private constructor
	 */
	private ExportUtil(){
		
	}
	
	/**
	 * Write excel workbook
	 * @param worksheet the worksheet
	 * @param out file output stream object
	 * @throws IOException
	 */
	public static void writeWorksheet(Worksheet worksheet, FileOutputStream out) 
			throws IOException {
		if (worksheet != null && out != null) {
			worksheet.getWorkbook().write(out);
		}
	}
	
	/**
	 * Adds a sheet in the Workbook
	 * @param worksheet the worksheet
	 * @param sheetName name of sheet
	 */
	public static void addSheet(Worksheet worksheet, String sheetName) {
		HSSFWorkbook workbook = null;
		HSSFSheet sheet= null;
		String classSymbol = null;
		
		workbook = worksheet.getWorkbook();
		if (workbook.getSheet(sheetName) != null) {
			return;
		}
		sheet = workbook.createSheet(sheetName);
		
		Map<String, HSSFSheet> sheets = worksheet.getSheets();
		Map<String, String> sheetSymbol = worksheet.getSheetSymbol();
		
		classSymbol = sheetSymbol.get(sheetName);
		 if ( classSymbol != null) {
			 sheets.put(classSymbol, sheet);
		 } else {
			 sheets.put(sheetName, sheet);
		 }
	}

	/**
	 * Adds header to the sheet
	 * @param worksheet the worksheet
	 * @param sheetName name of sheet
	 * @param title title of the sheet (can be null or empty)
	 * @param attributes header attributes
	 */
	public static void addHeader(Worksheet worksheet, String sheetName,
			String title, List<String> attributes) {
		HSSFSheet sheet = worksheet.getSheets().get(sheetName);
		HSSFCellStyle cellStyle = worksheet.getCellStyle();
		HSSFRow row = null;
		int rowNum = 0;
		
		if (title != null && ! title.isEmpty()) {
			row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(title);
			char y = (char) ((int) 'A' + attributes.size() - 1);
			String cell = "A1:" + y + "1";
			sheet.addMergedRegion(CellRangeAddress.valueOf(cell));
		}
		
		row = sheet.createRow(rowNum);
		for (int i=0; i<attributes.size(); i++) {
			addCell(row,cellStyle, i, attributes.get(i));
		}
	}
	
	/**
	 * Adds a row to the sheet
	 * @param worksheet the worksheet
	 * @param sheetName name of sheet
	 * @param cellValues list of cell values
	 * @return
	 */
	public static int addRow(Worksheet worksheet, String sheetName, List<String> cellValues) {
		
		HSSFSheet sheet = worksheet.getSheets().get(sheetName);
		int rowNum = sheet.getLastRowNum() + 1;
		if (rowNum > 20000) {
			return 20000;
		}
		return addRow(sheet, rowNum, cellValues);
	}
	
	/**
	 * Adds a row to the sheet
	 * @param worksheet the worksheet
	 * @param sheetName name of sheet
	 * @param rowNum row Number
	 * @param cellValues	values of the cell to be added in the row
	 * @return row number of the cell
	 */
	public static int addRow(Worksheet worksheet, String sheetName, int rowNum, List<String> cellValues) {
		
		HSSFSheet sheet = worksheet.getSheets().get(sheetName);
		return addRow(sheet, rowNum, cellValues);
	}
	
	/**
	 * Adds a row to the sheet
	 * @param sheet the sheet
	 * @param rowNum row Number
	 * @param cellValues	values of the cell to be added in the row
	 * @return row number of the cell
	 */
	private static int addRow(HSSFSheet sheet, int rowNum, List<String> cellValues) {
		HSSFRow row = sheet.createRow(rowNum);
		for (int i = 0; i < cellValues.size(); i++) {
			addCell(row, i, cellValues.get(i));
		}
		return row.getRowNum();
	}
	
	/**
	 * Adds a row to the sheet
	 * @param row the row
	 * @param index index of the cell
	 * @param value value to be added
	 */
	private static void addCell(HSSFRow row, int index, String value) {
			row.createCell(index).setCellValue(value);
	}
	
	/**
	 * Adds a cell to the row
	 * @param row the row
	 * @param cellStyle cell style
	 * @param index index of the cell
	 * @param value value to be added
	 */
	private static void addCell(HSSFRow row, HSSFCellStyle cellStyle, int index, String value) {
		HSSFCell cell = row.createCell(index);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
		cell.setCellValue(new HSSFRichTextString(value));
	}

	/**
	 * Sets header style
	 * @param worksheet the worksheet
	 * @param fontName font name
	 * @param fontColor font color
	 * @param fontBoldweight font weight
	 */
	public static void setHeaderStyle(Worksheet worksheet, String fontName,
			short fontColor, short fontBoldweight) {
		HSSFWorkbook workbook = worksheet.getWorkbook();
		HSSFFont font = workbook.createFont();
		font.setFontName(fontName);
		font.setColor(fontColor);
		font.setBoldweight(fontBoldweight);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		worksheet.setCellStyle(cellStyle);
	}
	
	/**
	 * Sets cell style
	 * @param worksheet
	 */
	public static void setHeaderStyle(Worksheet worksheet) {
		setHeaderStyle(worksheet, HSSFFont.FONT_ARIAL, IndexedColors.PLUM.getIndex(),
				HSSFFont.BOLDWEIGHT_BOLD);
	}

	/**
	 * Add values to the row
	 * @param worksheet the worksheet
	 * @param sheetName sheet name
	 * @param rowNum index of row
	 * @param startingColumnIndex starting column index
	 * @param cellValues values of the cell that are to be added
	 */
	public static void addValuesToRow(Worksheet worksheet, String sheetName, 
			int rowNum, int startingColumnIndex, List<String> cellValues) {
			HSSFSheet sheet = worksheet.getSheets().get(sheetName);
		HSSFRow row = sheet.getRow(rowNum);
		for (int i = 0; i<cellValues.size(); i++) {
			addCell(row, startingColumnIndex + i, cellValues.get(i));
		}
	}
	

	// Functions specific to Debugger for generating Workbook having unmatched key values
	 
	
	/**
	 * Creates worksheet for debugger
	 * @param classSymbols class symbols list
	 * @param props properties file, contains symbols and their meaining
	 * @return worksheet
	 */
	public  static Worksheet getUnmatchedKeyValuesDebuggerWorksheet(
			List<String> classSymbols, Properties props) {
		Worksheet worksheet = new Worksheet();
		Map<String, String> map = worksheet.getSheetSymbol();

		List<String> list = new ArrayList<String>();
		list.add("Method");
		list.add("BlockName");
		list.add("LineNo");
		list.add("Input Key");
		list.add("Output Key");
		list.add("Output Value");
		
		String className, sheetName;
		int i = 1;
		
		for (String symbol:classSymbols) {
			className = props.getProperty(symbol);
			sheetName = i + ". " + className.substring(className.lastIndexOf(".")+1, className.length());
			map.put(sheetName, symbol);
			addSheet(worksheet, sheetName);
			addHeader(worksheet, symbol, className, list);
			i++;
		}
		return worksheet;
	}

	/**
	 * Add a row to the sheet
	 * @param worksheet the worksheet
	 * @param blockLine the blockLine
	 * @param keyValueLine line having unmatched key or value
	 * @param props properties
	 */
	public static void addUnmatchedKeyValuesRow(Worksheet worksheet,
			String[] blockLine, String[] keyValueLine, Properties props) {
		
		String methodName = props.getProperty(blockLine[0] + "|" + blockLine[1]);
		List<String> cellValues = new ArrayList<String>();
		cellValues.add(methodName);
		cellValues.add( blockLine[2]);							//Block Name
		cellValues.add(blockLine[3]);							//Line Number
		cellValues.add(keyValueLine[5]);					//Input Key
		
		if ("K".equals(keyValueLine[3])) {
			cellValues.add(keyValueLine[4]);				//Output Key
			cellValues.add("");										//Output Value
		} else {
			cellValues.add("");
			cellValues.add(keyValueLine[4]);
		}
		addRow(worksheet, blockLine[0], cellValues);
	}
	
		// Functions specific to Data Quality
	
	/**
	 * 
	 * @param worksheet
	 * @param dvReportJson
	 * @throws JumbuneException
	 * @throws IOException
	 */
	public static void createDataQualityExcelReport(Worksheet worksheet, String dvReportJson) throws JumbuneException, IOException {
		Set<String> fileSet = new TreeSet<String>();
		
		List<String> row = new ArrayList<String>();
		// adding header to the rows
		String sheetName = UtilitiesConstants.DATA_VALIDATION_SHEET;
		addSheet(worksheet, sheetName);
		JsonElement jreportElement = new JsonParser().parse(dvReportJson);
		JsonObject dvReportObject = jreportElement.getAsJsonObject();
		if(!dvReportObject.getAsJsonObject("DVSUMMARY").toString().equals("{}")){
			row.add("TYPE");
			row.add(UtilitiesConstants.TOTAL_VIOLATIONS);
			Set<String> setOfTotalFiles = getFilesList(dvReportJson, fileSet);
			for (String string : setOfTotalFiles) {
				row.add("file : " + string);
			}
			addHeader(worksheet, sheetName, null, row);
			
			writeStatsRowByRowToFile(dvReportJson, setOfTotalFiles, worksheet, sheetName);
		}else{
			row.add("NO DATA VIOLATIONS FOUND");
			addHeader(worksheet, sheetName, null , row);
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
	private static void writeStatsRowByRowToFile(String dvReportJson, Set<String> set,
			Worksheet worksheet, String sheetName) {
		JsonElement jsonElement = null;
		List<String> columnValueList = new ArrayList<String>();
		JsonElement jreportElement = new JsonParser().parse(dvReportJson);
		JsonObject dvReportObject = jreportElement.getAsJsonObject();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.NULL_CHECK);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.NULL_CHECK);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			addRow(worksheet, sheetName, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.REGEX);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.REGEX);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			addRow(worksheet, sheetName, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.DATA_TYPE);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.DATA_TYPE);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			addRow(worksheet, sheetName, columnValueList);
		}
		columnValueList.clear();
		jsonElement = dvReportObject.getAsJsonObject(UtilitiesConstants.NO_OF_VIOLATION);
		if (jsonElement != null) {
			columnValueList.add(UtilitiesConstants.NO_OF_VIOLATION);
			columnValueList = getWritableListToWrite(jsonElement, set, columnValueList);
			addRow(worksheet, sheetName, columnValueList);
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
		JsonArray array = null;
		if(jsonElement != null && (array = jsonElement.getAsJsonObject().getAsJsonArray(UtilitiesConstants.VIOLATION_LIST_TAG))!= null){
			for (JsonElement element : array) {
				set.add(element.getAsJsonObject().get(UtilitiesConstants.FILE_NAME_TAG).getAsString());
			}
		}
	}
	
	
	
}
