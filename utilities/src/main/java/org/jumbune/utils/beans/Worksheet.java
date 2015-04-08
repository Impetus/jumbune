package org.jumbune.utils.beans;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

public class Worksheet {
	
	//the workbook
	private HSSFWorkbook workbook = null;
	
	// Map having key as sheet name and value as sheet
	private Map<String, HSSFSheet> sheets;
	
	// Map having key as sheet name and value as class symbol
	private Map<String, String> sheetSymbol;
	
	//Cell Style of attributes
	private HSSFCellStyle cellStyle = null;
	
	public Worksheet(){
		workbook = new HSSFWorkbook();
		sheets = new HashMap<String, HSSFSheet>();
		sheetSymbol = new HashMap<String, String>();
	}
	
	/**
	 * @return the workbook
	 */
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	
	/**
	 * @param workbook the workbook to set
	 */
	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	/**
	 * @return the cellStyle
	 */
	public HSSFCellStyle getCellStyle() {
		return cellStyle;
	}
	
	/**
	 * @param cellStyle the cellStyle to set
	 */
	public void setCellStyle(HSSFCellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}
	
	/**
	 * @return the sheets
	 */
	public Map<String, HSSFSheet> getSheets() {
		return sheets;
	}

	/**
	 * @param sheets the sheets to set
	 */
	public void setSheets(Map<String, HSSFSheet> sheets) {
		this.sheets = sheets;
	}

	/**
	 * @return the sheetSymbol
	 */
	public Map<String, String> getSheetSymbol() {
		return sheetSymbol;
	}

	/**
	 * @param sheetSymbol the sheetSymbol to set
	 */
	public void setSheetSymbol(Map<String, String> sheetSymbol) {
		this.sheetSymbol = sheetSymbol;
	}
	
}
