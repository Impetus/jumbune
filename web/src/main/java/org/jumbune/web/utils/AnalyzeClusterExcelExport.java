package org.jumbune.web.utils;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import org.jumbune.web.utils.WebConstants;

public class AnalyzeClusterExcelExport {

	private final String MS = "ms";
	private final String S = "s ";
	private final String M = "m ";
	private final String H = "h ";
	private final String D = "d ";
	private final String MB = " MB";

	public int queueUtilizationSummary(HSSFWorkbook workbook, HSSFSheet sheet, List<Map<String, Object>> data,
			int rowNumber, boolean isFairScheduler) {
		HSSFRow row;
		HSSFCell cell;
		
		row = sheet.createRow(++rowNumber);
		
		// Merging Cells for Heading
		if (isFairScheduler) {
			sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 2));
		} else {
			sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 1));
		}
		
		// Creating Heading
		setStyleForWidgetHeading(workbook, row, "Queue Utilization Summary");
		
		// Creating column headings
		row = sheet.createRow(++rowNumber);
		cell = row.createCell(0);
		cell.setCellValue("Queue Name");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		cell = row.createCell(1);
		cell.setCellValue("Relative Utilization");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		if (isFairScheduler) {
			cell = row.createCell(2);
			cell.setCellValue("Steady Fair Share");
			setCellStyleForWidgetRowHeading(cell, workbook);
		}
		
		String key = "queueName";
		String key2 = "utilization";
		String key3 = "steadyFairShare";
		DecimalFormat formatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));
		formatter.setRoundingMode( RoundingMode.DOWN );
		// Filling values
		for (Map<String, Object> values : data) {
			row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue((String) values.get(key));
			if (isFairScheduler) {
				// Fair Scheduler, which gives memory usage as well as steady fair share in bytes
				row.createCell(1).setCellValue(formatter.format(((Double) values.get(key2) ) / 1048576) + MB);
				row.createCell(2).setCellValue(formatter.format(((Long) values.get(key3)) / 1048576) + MB);
			} else {
				// Capacity Scheduler, which gives memory usage in percentage usage
				row.createCell(1).setCellValue(formatter.format((Double) values.get(key2) ) + '%');
			}
		}
		return rowNumber;
	}
	
	public int longDurationApps(HSSFWorkbook workbook, HSSFSheet sheet, List<Map<String, Object>> data,
			int rowNumber) {
		HSSFRow row;
		HSSFCell cell;
		
		row = sheet.createRow(++rowNumber);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 4));
		// Creating Heading
		setStyleForWidgetHeading(workbook, row, "Long Duration Applications");
		// Creating column headings
		row = sheet.createRow(++rowNumber);
		cell = row.createCell(0);
		cell.setCellValue("Application ID");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		cell = row.createCell(1);
		cell.setCellValue("Duration");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		cell = row.createCell(2);
		cell.setCellValue("User");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		cell = row.createCell(3);
		cell.setCellValue("	Application Type");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		cell = row.createCell(4);
		cell.setCellValue("Status");
		setCellStyleForWidgetRowHeading(cell, workbook);
		
		String key = "applicationID";
		String key2 = "durationMillis";
		String key3 = "user";
		String key4 = "appType";
		String key5 = "appStatus";
		
		
		for (Map<String, Object> map : data) {
			row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue((String) map.get(key));
			row.createCell(1).setCellValue(convertMillisToString((Long) map.get(key2)));
			row.createCell(2).setCellValue((String) map.get(key3));
			row.createCell(3).setCellValue((String) map.get(key4));
			row.createCell(4).setCellValue((String) map.get(key5));
		}
		return rowNumber;
	}
	
	private String convertMillisToString(long durationInMillis) {
		StringBuilder occuringSince = new StringBuilder();
		if ((durationInMillis / (1000 * 60 * 60 * 24)) >= 1) {

			occuringSince.append(durationInMillis / (1000 * 60 * 60 * 24)).append(D);
			durationInMillis = durationInMillis % (1000 * 60 * 60 * 24);
		}
		
		if ((durationInMillis / (1000 * 60 * 60)) >= 1) {
			occuringSince.append(durationInMillis / (1000 * 60 * 60)).append(H);
			durationInMillis = durationInMillis % (1000 * 60 * 60);
		}

		if ((durationInMillis / (1000 * 60)) >= 1) {
			occuringSince.append(durationInMillis / (1000 * 60)).append(M);
			durationInMillis = durationInMillis % (1000 * 60);
		}

		if ((durationInMillis / 1000) >= 1) {
			occuringSince.append(durationInMillis / 1000).append(S);
			durationInMillis = durationInMillis % 1000;
		}

		if (durationInMillis >= 1 && occuringSince.length() == 0) {
			occuringSince.append(durationInMillis).append(MS);
		}
		occuringSince.trimToSize();
		return occuringSince.toString();
	}
	
	public int resouceUtilizationMetering(HSSFWorkbook workbook, HSSFSheet sheet, List<Map<String, Object>> data,
			int rowNumber, String date) {
		
		HSSFRow row;
		HSSFCell cell;
		
		row = sheet.createRow(++rowNumber);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 8));
		// Creating Heading
		setStyleForWidgetHeading(workbook, row, "Resource Utilization Metering");
		
		row = sheet.createRow(++rowNumber);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 8));
		// Creating Date Row
		setStyleForWidgetHeading(workbook, row, date);
		
		// Creating column headings
		row = sheet.createRow(++rowNumber);
		String[] headings = {"User", "Execution Engine", "Queue Name",
				"VCores Hours Used", "Configured VCores Cost", "Memory(GB) Hours Used",
				"Configured Memory Cost", "Total Cost"};
		
		for (int i = 0; i < headings.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headings[i]);
			setCellStyleForWidgetRowHeading(cell, workbook);
		}
		
		for (Map<String, Object> map : data) {
			row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue((String) map.get(WebConstants.USER));
			row.createCell(1).setCellValue((String) map.get(WebConstants.EXECUTION_ENGINE));
			row.createCell(2).setCellValue((String) map.get(WebConstants.QUEUE_NAME));
			row.createCell(3).setCellValue((Double) map.get(WebConstants.V_CORE_HOURS_USED));
			row.createCell(4).setCellValue((Double) map.get(WebConstants.CONFIGURED_VCORE_COST));
			row.createCell(5).setCellValue((Double) map.get(WebConstants.MEMORY_GB_HOURS_USED));
			row.createCell(6).setCellValue((Double) map.get(WebConstants.CONFIGURED_MEMORY_COST));
			row.createCell(7).setCellValue((Double) map.get(WebConstants.TOTAL_COST));
		}
		
		return rowNumber;
	}
	
	public int detailedresouceUtilizationMetering(HSSFWorkbook workbook,
			HSSFSheet sheet, List<Map<String, Object>> data, int rowNumber, String date) {
		HSSFRow row;
		HSSFCell cell;
		
		row = sheet.createRow(++rowNumber);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 8));
		// Creating Heading
		setStyleForWidgetHeading(workbook, row, "Detailed Resource Utilization Metering");
		
		row = sheet.createRow(++rowNumber);
		sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 8));
		// Creating Date Row
		setStyleForWidgetHeading(workbook, row, date);
		
		// Creating column headings
		row = sheet.createRow(++rowNumber);
		
		String[] headings = {"User", "Execution Engine", "Queue Name", "Job Name", "Job ID",
				"VCores Hours Used", "Configured VCores Cost", "Memory(GB) Hours Used",
				"Configured Memory Cost", "Total Cost"};
		
		for (int i = 0; i < headings.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headings[i]);
			setCellStyleForWidgetRowHeading(cell, workbook);
		}
		
		for (Map<String, Object> map : data) {
			row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue((String) map.get(WebConstants.USER));
			row.createCell(1).setCellValue((String) map.get(WebConstants.EXECUTION_ENGINE));
			row.createCell(2).setCellValue((String) map.get(WebConstants.QUEUE_NAME));
			row.createCell(3).setCellValue((String) map.get(WebConstants.JOB_NAME_1));
			row.createCell(4).setCellValue((String) map.get(WebConstants.JOB_ID));
			row.createCell(5).setCellValue((Double) map.get(WebConstants.V_CORE_HOURS_USED));
			row.createCell(6).setCellValue((Double) map.get(WebConstants.CONFIGURED_VCORE_COST));
			row.createCell(7).setCellValue((Double) map.get(WebConstants.MEMORY_GB_HOURS_USED));
			row.createCell(8).setCellValue((Double) map.get(WebConstants.CONFIGURED_MEMORY_COST));
			row.createCell(9).setCellValue((Double) map.get(WebConstants.TOTAL_COST));
		}
		
		return rowNumber;
	}
	
	
	private void setStyleForWidgetHeading(HSSFWorkbook workbook, HSSFRow row, String widgetHeading) {
		row.setHeightInPoints(25);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(widgetHeading);

		HSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 18);

		HSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell.setCellStyle(style);
	}

	private void setCellStyleForWidgetRowHeading(HSSFCell cell, HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);
		cell.setCellStyle(style);
	}

	public void autoSizeColumns(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> it = sheet.rowIterator();
		Row row;
		while (it.hasNext()) {
			row = it.next();
			for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
				sheet.autoSizeColumn(colNum);
			}
		}

	}
	
	/**
	 * This method return date in a particular format eg. [1 Aug 2017 - 31 Aug 2017]
	 * @param month
	 * @param rangeFrom
	 * @param rangeTo
	 * @return
	 * @throws ParseException
	 */
	public String getTimePeriod(String month, String rangeFrom, String rangeTo) throws ParseException {
		DateFormat df = new SimpleDateFormat(WebConstants.YYYY_MM_DD_HH_MM);
		DateFormat dfFinal = new SimpleDateFormat("dd MMM yyyy");
		Date dateFrom, dateTo;
		Calendar calobj = Calendar.getInstance();
		if (month.equalsIgnoreCase(WebConstants.CURRENT_MONTH)) {
			calobj.set(Calendar.DAY_OF_MONTH, calobj.getActualMinimum(Calendar.DAY_OF_MONTH));
			calobj.set(Calendar.HOUR_OF_DAY, 0);
			calobj.set(Calendar.MINUTE, 0);
			dateFrom = calobj.getTime();
			dateTo = new Date();
		} else if (month.equalsIgnoreCase(WebConstants.PREVIOUS_MONTH)) {
			calobj.add(Calendar.MONTH, -1);
			calobj.set(Calendar.DATE, 1);
			calobj.set(Calendar.HOUR_OF_DAY, 0);
			calobj.set(Calendar.MINUTE, 0);
			dateFrom = calobj.getTime();
			
			calobj.set(Calendar.DATE, calobj.getActualMaximum(Calendar.DATE));
			dateTo = calobj.getTime();
		} else {
			dateFrom = df.parse(rangeFrom);
			dateTo = df.parse(rangeTo);
		}
		
		return dfFinal.format(dateFrom) + " - " + dfFinal.format(dateTo);
	}
	
	

}
