package org.jumbune.datavalidation;

import static org.jumbune.datavalidation.DataValidationConstants.FILE_SEPARATOR;
import static org.jumbune.datavalidation.DataValidationConstants.SLAVE_FILE_LOC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.YamlUtil;



/**
 * The Reducer takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
 * different data violation types.
 * 

 * 
 */
public class DataValidationReducer extends Reducer<Text, ObjectWritable, Text, DataViolationWritable> {

	/** The dir path. */
	private String dirPath;
	
	/** The folder path. */
	private String folderPath;
	
	/** The file handler map. */
	private Map<String, BufferedWriter> fileHandlerMap;
	
	/** The Constant MAX_VIOLATIONS_IN_REPORT. */
	private static final int MAX_VIOLATIONS_IN_REPORT = 1000;

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(Reducer.Context context) throws IOException, InterruptedException {
		super.setup(context);
		String dir = context.getConfiguration().get(SLAVE_FILE_LOC);
		dirPath = YamlUtil.getAndReplaceHolders(dir);
		fileHandlerMap = new DVLRUCache(DataValidationConstants.TEN);
	}

	/**
	 * reduce method takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
	 * different data violation types.
	 */
	public void reduce(Text key, Iterable<ObjectWritable> values, Context context) throws IOException, InterruptedException {

		int totalViolations = 0;
		DataViolationWritableBean dvwb;
		List<FileViolationsWritable> list = new ArrayList<FileViolationsWritable>();
		IntWritable fieldNumber = new IntWritable();
		IntWritable fieldViolations = new IntWritable(0);
		MapWritable mapWritable = new MapWritable();
		Map<String, Integer> fileViolationsMap = new LinkedHashMap<String, Integer>();
		StringBuilder sb = new StringBuilder(dirPath);
		sb.append(key.toString()).append(FILE_SEPARATOR);
		folderPath = sb.toString();
		new File(folderPath).mkdirs();
		
		String fileName = null;
		StringBuffer wb = new StringBuffer();
		int violations = 0;
		

		for (ObjectWritable objectWritable : values) {
			totalViolations++;
			dvwb = (DataViolationWritableBean) objectWritable.get();
			fileName = dvwb.getFileName();
			fieldNumber = new IntWritable(dvwb.getFieldNumber());
			fieldViolations = (IntWritable) mapWritable.get((fieldNumber));
			fieldViolations = setFieldViolations(fieldViolations);
			mapWritable.put(fieldNumber, fieldViolations);
			violations = extractViolationsFromMap(fileViolationsMap, fileName);
			violations += 1;
			writeViolationsToBuffer(dvwb, fileName, wb, violations);
			fileViolationsMap.put(fileName, violations);
		}

		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		fileHandlerMap.clear();
		FileViolationsWritable fvWritable = null;
		for (Map.Entry<String, Integer> violationMap : fileViolationsMap.entrySet()) {
			fvWritable = new FileViolationsWritable();
			fvWritable.setFileName(violationMap.getKey());
			fvWritable.setNumOfViolations(violationMap.getValue());
			list.add(fvWritable);
		}

		FileViolationsWritable[] arr = list.toArray(new FileViolationsWritable[list.size()]);
		DataViolationArrayWritable dataViolationArrayWritable = new DataViolationArrayWritable();
		dataViolationArrayWritable.set(arr);

		DataViolationWritable dataViolationWritable = new DataViolationWritable();
		mapWritable.put(fieldNumber, fieldViolations);
		dataViolationWritable.setFieldMap(mapWritable);
		dataViolationWritable.setTotalViolations(totalViolations);
		dataViolationWritable.setDataViolationArrayWritable(dataViolationArrayWritable);
		context.write(key, dataViolationWritable);
	}

	/**
	 * Extract violations from map.
	 *
	 * @param fileViolationsMap the file violations map
	 * @param fileName the file name
	 * @return the int
	 */
	private int extractViolationsFromMap(
			Map<String, Integer> fileViolationsMap, String fileName) {
		int violations;
		if (fileViolationsMap.containsKey(fileName)) {
			violations = fileViolationsMap.get(fileName);
		} else {
			violations = 0;
		}
		return violations;
	}

	/**
	 * Sets the field violations.
	 *
	 * @param fieldViolations the field violations
	 * @return the int writable
	 */
	private IntWritable setFieldViolations(final IntWritable fieldViolations) {
		
		IntWritable fldViolations = fieldViolations;
		if (fldViolations != null) {
			fldViolations.set(fldViolations.get() + 1);
		} else {
			fldViolations = new IntWritable(1);
		}
		return fldViolations;
	}

	/**
	 * Write violations to buffer.
	 *
	 * @param dvwb stores the parameters required for data violation report
	 * @param fileName the file name
	 * @param wb refers to the violations that are being written to buffer.
	 * @param violations refers to null,regex,data type violations.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeViolationsToBuffer(DataViolationWritableBean dvwb,
			String fileName, StringBuffer wb, int violations)
			throws IOException {
		BufferedWriter out = null ;
		int fieldNum =0;
		if (violations <= MAX_VIOLATIONS_IN_REPORT) {
			wb.append(dvwb.getLineNumber());
			wb.append(Constants.PIPE_SEPARATOR);
			fieldNum = dvwb.getFieldNumber();
			if (fieldNum == -1) {
				wb.append("-");
			} else {
				wb.append(fieldNum);
			}

			wb.append(Constants.PIPE_SEPARATOR);
			wb.append(dvwb.getExpectedValue());
			wb.append(Constants.PIPE_SEPARATOR);
			wb.append(dvwb.getActualValue());
			wb.append("\n");
			out = getFileHandler(fileName);
			out.write(wb.toString());
			wb.delete(0, wb.length());
		}
	}

	/**
	 * Gets the file handler.
	 *
	 * @param fileName the file name
	 * @return the file handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BufferedWriter getFileHandler(String fileName) throws IOException {
		BufferedWriter out = fileHandlerMap.get(fileName);
		if (out == null) {
			out = new BufferedWriter(new FileWriter(folderPath + fileName));
			fileHandlerMap.put(fileName, out);
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#cleanup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
	}
}