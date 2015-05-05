package org.jumbune.datavalidation;

import static org.jumbune.datavalidation.DataValidationConstants.SLAVE_FILE_LOC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.JobUtil;

	

/**
 * The Reducer takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
 * different data violation types.
 * 

 * 
 */
public class DataValidationReducer extends Reducer<Text, DataDiscrepanciesArrayWritable, Text, DataViolationWritable> {

	/** The dir path. */
	private String dirPath;
	
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
		dirPath = JobUtil.getAndReplaceHolders(dir);
		fileHandlerMap = new DVLRUCache(DataValidationConstants.TEN);
	}

	/**
	 * reduce method takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
	 * different data violation types.
	 */
	public void reduce(Text key, Iterable<DataDiscrepanciesArrayWritable> values, Context context) throws IOException, InterruptedException {
		createDirectory(key);
		int totalDirtyTuple = 0;
		IntWritable fieldNumber = new IntWritable();
		IntWritable fieldViolations = new IntWritable(0);
		int totalFieldViolations = 0;
		int totalNullCheckViolations = 0;
		int totalDataTypeViolations = 0;
		int totalRegexCheckViolations = 0;
		MapWritable nullCheckMapWritable = null;
		MapWritable dataTypeCheckMapWritable = null;
		MapWritable regexCheckMapWritable = null;
		MapWritable fieldMapWritable = null;
		Map<String, Integer> nullCheckfileViolationsMap = null;
		Map<String, Integer> dataTypeFileViolationsMap = null;
		Map<String, Integer> regexCheckFileViolationsMap = null;
		Map<String, Integer> fieldFileViolationsMap = null;
		StringBuffer wb = new StringBuffer();
		Set<String> dirtyFieldTupleSet = new HashSet<String>();
		Set<String> dirtyDataTypeTupleSet = new HashSet<String>();
		Set<String> dirtyRegexTupleSet = new HashSet<String>();
		Set<String> dirtyNullCheckSet = new HashSet<String>();
		String fileName = null;
		for (DataDiscrepanciesArrayWritable dvarrayWritable : values) {
			totalDirtyTuple++;
			fileName = dvarrayWritable.getFileName();
			for (Writable writable : dvarrayWritable.get()) {
				
				DataViolationWritableBean fileViolationsWritable =(DataViolationWritableBean)writable;
				if(fileViolationsWritable!= null) {
					switch(fileViolationsWritable.getViolationType()){
					case DataValidationConstants.NUM_OF_FIELDS_CHECK:
						dirtyFieldTupleSet.add(fileName+fileViolationsWritable.getLineNumber());
						totalFieldViolations++;
						if(fieldMapWritable == null || fieldFileViolationsMap == null){
							fieldMapWritable = new MapWritable();
							fieldFileViolationsMap = new LinkedHashMap<String, Integer>();
						}	
						processTupleViolation(fieldMapWritable,
								fieldFileViolationsMap, wb,
								fileViolationsWritable,fileName);
						break;
					case DataValidationConstants.USER_DEFINED_NULL_CHECK:
						dirtyNullCheckSet.add(fileName+fileViolationsWritable.getLineNumber());
						totalNullCheckViolations++;
						if(nullCheckMapWritable == null || nullCheckfileViolationsMap == null){
							nullCheckMapWritable = new MapWritable();
							nullCheckfileViolationsMap = new LinkedHashMap<String, Integer>();
						}					
						processTupleViolation(nullCheckMapWritable,
								nullCheckfileViolationsMap, wb,
								fileViolationsWritable, fileName);
						break;
					case DataValidationConstants.USER_DEFINED_DATA_TYPE:
						dirtyDataTypeTupleSet.add(fileName+fileViolationsWritable.getLineNumber());
						totalDataTypeViolations++;
						if(dataTypeCheckMapWritable == null || dataTypeFileViolationsMap == null){
							dataTypeCheckMapWritable = new MapWritable();
							dataTypeFileViolationsMap = new LinkedHashMap<String, Integer>();
						}					
						processTupleViolation(dataTypeCheckMapWritable,
								dataTypeFileViolationsMap, wb,
								fileViolationsWritable, fileName);
						break;
					case DataValidationConstants.USER_DEFINED_REGEX_CHECK:
						dirtyRegexTupleSet.add(fileName+fileViolationsWritable.getLineNumber());
						totalRegexCheckViolations++;
						if(regexCheckMapWritable == null || regexCheckFileViolationsMap == null){
							regexCheckMapWritable = new MapWritable();
							regexCheckFileViolationsMap = new LinkedHashMap<String, Integer>();
						}					
						processTupleViolation(regexCheckMapWritable,
								regexCheckFileViolationsMap, wb,
								fileViolationsWritable, fileName);
						break;
					default:
						break;
					}
				}
			}
		}

		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		fileHandlerMap.clear();
		int dirtyTuple = 0;
		
		if(nullCheckfileViolationsMap!=null){
			dirtyTuple = dirtyNullCheckSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_NULL_CHECK, context, totalNullCheckViolations, fieldNumber,
					fieldViolations, nullCheckMapWritable, nullCheckfileViolationsMap, dirtyTuple, totalDirtyTuple-dirtyTuple);
		}
		if(dataTypeFileViolationsMap!=null){
			dirtyTuple = dirtyDataTypeTupleSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_DATA_TYPE, context, totalDataTypeViolations, fieldNumber,
					fieldViolations, dataTypeCheckMapWritable, dataTypeFileViolationsMap,dirtyTuple, totalDirtyTuple-dirtyTuple);
		}
		if(regexCheckFileViolationsMap!=null){
			dirtyTuple = dirtyRegexTupleSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_REGEX_CHECK, context, totalRegexCheckViolations, fieldNumber,
					fieldViolations, regexCheckMapWritable, regexCheckFileViolationsMap,dirtyTuple, totalDirtyTuple-dirtyTuple);
		}
		if(fieldFileViolationsMap!=null){
			dirtyTuple = dirtyFieldTupleSet.size();
			writeViolations(DataValidationConstants.NUM_OF_FIELDS_CHECK, context, totalFieldViolations, fieldNumber,
					fieldViolations, fieldMapWritable, fieldFileViolationsMap, dirtyTuple,totalDirtyTuple-dirtyTuple);
		}
	}

	private void writeViolations(String violatoinType, Context context,int totalViolations,
			IntWritable fieldNumber, IntWritable fieldViolations,
			MapWritable mapWritable, Map<String, Integer> fileViolationsMap, int dirtyTuple, int cleanTuple)
			throws IOException, InterruptedException {
		List<FileViolationsWritable> list = new ArrayList<FileViolationsWritable>();
		FileViolationsWritable fvWritable;
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
		if(mapWritable == null){
			mapWritable = new MapWritable();
			mapWritable.put(fieldNumber, fieldViolations);
		}
		dataViolationWritable.setFieldMap(mapWritable);
		dataViolationWritable.setTotalViolations(totalViolations);
		dataViolationWritable.setDataViolationArrayWritable(dataViolationArrayWritable);
		dataViolationWritable.setDirtyTuple(dirtyTuple);
		dataViolationWritable.setCleanTuple(cleanTuple);
		context.write(new Text(violatoinType), dataViolationWritable);
	}

	private void processTupleViolation(MapWritable fieldMapWritable,
			Map<String, Integer> fieldFileViolationsMap, StringBuffer wb,
			DataViolationWritableBean fileViolationsWritable, String fileName)
			throws IOException {
		IntWritable fieldNumber = new IntWritable();
		IntWritable fieldViolations = new IntWritable(0);
		int violations;
		fieldNumber = new IntWritable(fileViolationsWritable.getFieldNumber());
		fieldViolations = (IntWritable) fieldMapWritable.get((fieldNumber));
		fieldViolations = setFieldViolations(fieldViolations);
		fieldMapWritable.put(fieldNumber, fieldViolations);
		violations = extractViolationsFromMap(fieldFileViolationsMap, fileName);
		violations += 1;
		fieldFileViolationsMap.put(fileName, violations);
		writeViolationsToBuffer(fileViolationsWritable, fileName, wb, violations);
	}

	private void createDirectory(Text key) {
		String [] violationsTypesArray = key.toString().split("\\|");
		for (String violationType : violationsTypesArray) {
			File f = new File(dirPath + File.separator + violationType);
			f.mkdirs();
			f.setReadable(true, false);
			f.setWritable(true, false);
		}
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
	 * @param stringBuffer refers to the violations that are being written to buffer.
	 * @param violations refers to null,regex,data type violations.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeViolationsToBuffer(DataViolationWritableBean dvwb,
			String fileName, StringBuffer stringBuffer, int violations)
			throws IOException {
		BufferedWriter out = null ;
		int fieldNum =0;
		if (violations <= MAX_VIOLATIONS_IN_REPORT) {
			stringBuffer.append(dvwb.getLineNumber());
			stringBuffer.append(Constants.PIPE_SEPARATOR);
			fieldNum = dvwb.getFieldNumber();
			if (fieldNum == -1) {
				stringBuffer.append("-");
			} else {
				stringBuffer.append(fieldNum);
			}

			stringBuffer.append(Constants.PIPE_SEPARATOR);
			stringBuffer.append(dvwb.getExpectedValue());
			stringBuffer.append(Constants.PIPE_SEPARATOR);
			stringBuffer.append(dvwb.getActualValue());
			stringBuffer.append("\n");
			out = getFileHandler(fileName, dvwb.getViolationType());
			out.write(stringBuffer.toString());
			stringBuffer.delete(0, stringBuffer.length());
		}
	}

	/**
	 * Gets the file handler.
	 *
	 * @param fileName the file name
	 * @param  violatino type 
	 * @return the file handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BufferedWriter getFileHandler(String fileName, String violationType) throws IOException {
		String absoluteFilePath = dirPath +File.separator+ violationType + File.separator+ fileName;
		BufferedWriter out = fileHandlerMap.get(absoluteFilePath);
		if (out == null) {
			File f = new File(absoluteFilePath);
			f.setReadable(true, false);
			out = new BufferedWriter(new FileWriter(f));
			fileHandlerMap.put( absoluteFilePath, out);
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