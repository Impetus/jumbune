package org.jumbune.datavalidation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.utils.Constants;
import org.jumbune.datavalidation.ArrayListWritable;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.LRUCache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The Mapper takes<record number, record value> as input and writes <data violation type,data violation bean> as output.
 * 
 */
public class DataValidationMapper extends Mapper<Object, Text, Text, DataDiscrepanciesArrayWritable> {
	
	
	/** No of records processed by a single mapper **/ 
	private long noOfToupleProcessd;
	
	private long cleanTupleCounter;

	/** The expected num of fields. */
	private int expectedNumOfFields;
	
	/** The file name. */
	private String fileName = null;
	
	/** The field separator. */
	private String fieldSeparator;
	
	private int lineVWCounterNOF;
	
	private Integer lineVWCounterNC ;
	
	private Integer lineVWCounterDT ;
	
	private Integer lineVWCounterRX ;
	
	private DataViolationWB[] dataViolationWBArr;
	
	private static final String DDAW = "DDAW";
	
	/** The field validation list. */
	private List<FieldValidationBean> fieldValidationList;
	
	/** The slave file loc. */
	String SLAVE_FILE_LOC = "slaveFileLoc";
	
	private LRUCache<String, Pattern> keyPattern = null ;
	
	private DataViolationWB dataViolationWBNOF = null ;
	private DataViolationWB dataViolationWBNC = null;
	private DataViolationWB dataViolationWBDT = null ;
	private DataViolationWB dataViolationWBRX = null ;
	private DataDiscrepanciesArrayWritable dataValidatoinDiscripancies = null;
	
	private boolean [][] validateArray = null ;

	/** The line number of record. Incremented with each new record  */
	private int lineNumber;
	
	/** The split start offset. */
	private long splitStartOffset;
	
	/** The split end offset. */
	private long splitEndOffset;
	
	/** The records emitt by map. */
	private long recordsEmittByMap ;

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
		lineNumber = 0;		
		recordsEmittByMap = 0l ;
		
		noOfToupleProcessd = 0l;
		cleanTupleCounter=0l;
		lineVWCounterNOF = 0;
		lineVWCounterNC = 0;
		lineVWCounterDT = 0;
		lineVWCounterRX = 0;
		dataValidatoinDiscripancies = new DataDiscrepanciesArrayWritable();
		dataViolationWBNOF = new DataViolationWB();
		dataViolationWBNC = new DataViolationWB();
		dataViolationWBDT = new DataViolationWB();
		dataViolationWBRX = new DataViolationWB();
		
		dataViolationWBArr = new DataViolationWB[1];
		
		// populating data validation parameters
		String dvBeanString = context.getConfiguration().get(DataValidationConstants.DATA_VALIDATION_BEAN_STRING);
		String validateMatrix = context.getConfiguration().get(DataValidationConstants.VALIDATE_MATRIX);
		Gson gson = new Gson();
		Type type = new TypeToken<DataValidationBean>() {
		}.getType();
		DataValidationBean dataValidationBean = gson.fromJson(dvBeanString, type);

		fieldSeparator = dataValidationBean.getFieldSeparator();
		fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE_SEPARATOR, Constants.SPACE);
		fieldValidationList = dataValidationBean.getFieldValidationList();
		expectedNumOfFields = dataValidationBean.getNumOfFields();
		validateArray = gson.fromJson(validateMatrix, boolean[][].class);
		keyPattern = new LRUCache<String, Pattern>(expectedNumOfFields) {

			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, Pattern> eldest) {
				if (size() > super.getCapacity()) {
					return true;
				}
				return false;
			}
		};
		

		FileSplit split = ((FileSplit) context.getInputSplit());		
		splitStartOffset = split.getStart();
		//calculating end offset of current split
		splitEndOffset = splitStartOffset + split.getLength() - 1;
		fileName = split.getPath().toUri().getPath();
		fileName = fileName.replaceAll("/", ".").substring(1, fileName.length());
	}

	/**
	 * Map function that takes<record number, record value> as input
	 * and writes <data violation type,data violation bean> as output.
	 */
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {		
		
		noOfToupleProcessd = noOfToupleProcessd + 1;		
		lineNumber++;
		recordsEmittByMap++;	
		
		String recordValue = value.toString();		
		String[] fields = recordValue.split(fieldSeparator);		
		
		int actualNumOfFields = fields.length;
		String actualFieldValue = null;
		int fieldNumber = 0;
		String nullCheck = null;
		String dataType = null;
		String regex = null;
		boolean hasViolations = false;		
		
		// validating the number of fields in the record
		if(!validateNumberOfFields(actualNumOfFields, dataViolationWBNOF)){			
			if(lineVWCounterNOF > DataValidationConstants.MAX_VIOLATIONS){				
				writeViolations(dataValidatoinDiscripancies, dataViolationWBNOF, context, DataValidationConstants.NUM_OF_FIELDS_CHECK);
			}
			return;
		}
		
		boolean nullCheckAlreadyViolated;
		for (FieldValidationBean fieldValidationBean : fieldValidationList) {
			fieldNumber = fieldValidationBean.getFieldNumber();
			actualFieldValue = fields[fieldNumber-1];
			nullCheck = fieldValidationBean.getNullCheck();
			dataType = fieldValidationBean.getDataType();
			regex = fieldValidationBean.getRegex();
			nullCheckAlreadyViolated = false;
			if(validateArray[DataValidationConstants.NULL_MATRIX][fieldNumber-1] && !applyNullCheck(nullCheck, actualFieldValue)){
				hasViolations = true;
				addFailedValidationToOutput(actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_NULL_CHECK, nullCheck,dataViolationWBNC);
				
				nullCheckAlreadyViolated = true;	
				lineVWCounterNC++;
				if(lineVWCounterNC >= DataValidationConstants.MAX_VIOLATIONS){					
					writeViolations(dataValidatoinDiscripancies, dataViolationWBNC, context, DataValidationConstants.USER_DEFINED_NULL_CHECK);
				}
			}
			if(!nullCheckAlreadyViolated &&(validateArray[DataValidationConstants.DATA_TYPE_MATRIX][fieldNumber-1] && !applyDataTypeCheck(dataType, actualFieldValue))){
				hasViolations = true;
				addFailedValidationToOutput(actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_DATA_TYPE, dataType,dataViolationWBDT);
				lineVWCounterDT++;
				if(lineVWCounterDT >= DataValidationConstants.MAX_VIOLATIONS){
					writeViolations(dataValidatoinDiscripancies, dataViolationWBDT, context, DataValidationConstants.USER_DEFINED_DATA_TYPE);
				}
				
			}
			if(!nullCheckAlreadyViolated && (validateArray[DataValidationConstants.REGEX_MATRIX][fieldNumber-1] && !applyRegexCheck(regex, actualFieldValue))){
				hasViolations = true;
				addFailedValidationToOutput(actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_REGEX_CHECK, regex,dataViolationWBRX);
				lineVWCounterRX++;
				if(lineVWCounterRX >= DataValidationConstants.MAX_VIOLATIONS){
					writeViolations(dataValidatoinDiscripancies, dataViolationWBRX, context, DataValidationConstants.USER_DEFINED_REGEX_CHECK);
				}
			}
		}
		if(!hasViolations){
			cleanTupleCounter++;
		}
	}
	
	private void writeViolations(DataDiscrepanciesArrayWritable dataValidationDiscripancies,
			DataViolationWB dataViolationWB, Context context, String outputKey)
					throws IOException, InterruptedException {		
		Text fName = new Text();
		fName.set(fileName);
		LongWritable startOffsetWritable = new LongWritable();
		LongWritable endOffsetWritable = new LongWritable();
		LongWritable recordsEmitMapWritable = new LongWritable();
		
		startOffsetWritable.set(splitStartOffset);
		endOffsetWritable.set(splitEndOffset);
		recordsEmitMapWritable.set(recordsEmittByMap);

		dataViolationWB.setSplitEndOffset(endOffsetWritable);
		dataViolationWB.setFileName(fName);
		dataViolationWB.setTotalRecordsEmittByMap(recordsEmitMapWritable);

		dataViolationWBArr[0] = dataViolationWB;
		dataValidationDiscripancies.set(dataViolationWBArr);
		StringBuilder builder = new StringBuilder();
		builder.append(outputKey).append(DDAW).append(dataValidationDiscripancies.get().hashCode());
		Text finalMapKey = new Text();
		finalMapKey.set(builder.toString());
		context.write(finalMapKey, dataValidationDiscripancies);
		
		MapWritable fieldMap = dataViolationWB.getFieldMap();
		
		for (Map.Entry<Writable, Writable> entry : fieldMap.entrySet()) {
			FieldLWB fieldLWB = (FieldLWB) fieldMap.get(entry.getKey());
			MapWritable typeViolateMap = fieldLWB.getTypeViolationMap();
			for (Map.Entry<Writable, Writable> tvEntry : typeViolateMap.entrySet()) {
				ViolationLWB vLWB = (ViolationLWB) typeViolateMap.get(tvEntry.getKey());
				vLWB.getLineLWBList().clear();
			}
			fieldLWB.getTypeViolationMap().clear();
			fieldLWB.resetTypeViolationMap();
		}		
		dataViolationWB.getFieldMap().clear();
		fieldMap = null;
		dataViolationWB.resetFieldMap();
		dataValidationDiscripancies = new DataDiscrepanciesArrayWritable();
		dataViolationWB = new DataViolationWB();
		
		if (outputKey.equals(DataValidationConstants.NUM_OF_FIELDS_CHECK)){
			lineVWCounterNOF = 0;	
		}else if(outputKey.equals(DataValidationConstants.USER_DEFINED_NULL_CHECK)) {
			lineVWCounterNC = 0;	
		}else if(outputKey.equals(DataValidationConstants.USER_DEFINED_DATA_TYPE)) {
			lineVWCounterDT = 0;	
		}else if (outputKey.equals(DataValidationConstants.USER_DEFINED_REGEX_CHECK)){
			lineVWCounterRX = 0;	
		}		
	}

	@Override
	protected void cleanup(
			Mapper<Object, Text, Text, DataDiscrepanciesArrayWritable>.Context context)
			throws IOException, InterruptedException {		
		writeViolations(dataValidatoinDiscripancies, dataViolationWBNOF, context, DataValidationConstants.NUM_OF_FIELDS_CHECK);
		writeViolations(dataValidatoinDiscripancies, dataViolationWBNC, context, DataValidationConstants.USER_DEFINED_NULL_CHECK);
		writeViolations(dataValidatoinDiscripancies, dataViolationWBDT, context, DataValidationConstants.USER_DEFINED_DATA_TYPE);
		writeViolations(dataValidatoinDiscripancies, dataViolationWBRX, context, DataValidationConstants.USER_DEFINED_REGEX_CHECK);
		String dir = context.getConfiguration().get(SLAVE_FILE_LOC);
		String dirPath = JobUtil.getAndReplaceHolders(dir);
		dirPath = dirPath +File.separator+"tuple"+File.separator;
		File f = new File(dirPath);
		if(!f.exists()){
			f.mkdirs();
		}
		f.setReadable(true, false);
		f.setWritable(true, false);
		BufferedWriter bufferedWriter =null;
		try{
			File attemptFile = new File(dirPath, context.getTaskAttemptID().getTaskID().toString());
			if(!attemptFile.getParentFile().exists()){
				attemptFile.getParentFile().mkdirs();
			}
			attemptFile.createNewFile();
			attemptFile.setReadable(true, false);
			attemptFile.setWritable(true, false);
			bufferedWriter = new BufferedWriter(new FileWriter(attemptFile));
			bufferedWriter.write(Long.toString(noOfToupleProcessd)+"\n"+Long.toString(cleanTupleCounter));
		}finally{
			if(bufferedWriter!= null){
				bufferedWriter.close();
			}
		}
		super.cleanup(context);	
	}
	
	/**
	 * Write failed validation to output.
	 *
	 * @param key refers to the record number in the file present on HDFS.
	 * @param context is used to write the data violation that are present in the file.
	 * @param actualFieldValue refers to the actual value of the field that is present on HDFS.
	 * @param fieldNumber refers to the number of the field that is present in the file on HDFS.
	 * @param validationCheckName  refers to null,regex and data type violations name.
	 * @param expectedValue refers to what the value of the field on HDFS should be.
	 * @param dataValidationWritableBeanList is an array which holds all the violations for a tuple. 
	 * @return
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void addFailedValidationToOutput(String actualFieldValue, int fieldNumber,
			String validationCheckName, String expectedValue, DataViolationWB dataViolationWB)
					throws IOException, InterruptedException {
		ArrayListWritable<LineLWB> listWritable = null;
		LineLWB lineLWB = null;
		ViolationLWB violationLWB = null;
		Text validationChkName = new Text(validationCheckName);
		IntWritable fieldNoWritable = new IntWritable(fieldNumber);
		Text actualValue = new Text(actualFieldValue);
		Text expectedFldvalue = new Text(expectedValue);
		LongWritable lineNo = new LongWritable(lineNumber);
		// possible optimization......
		// validationChkName.clear();
		// validationChkName.set(validationCheckName);
		MapWritable fieldMap = dataViolationWB.getFieldMap();
		if (fieldMap.containsKey(fieldNoWritable)) {
			FieldLWB fieldLWB = (FieldLWB) fieldMap.get(fieldNoWritable);
			MapWritable typeViolationMap = fieldLWB.getTypeViolationMap();
			// if field found and violation not found
			if (!typeViolationMap.containsKey(validationChkName)) {
				lineLWB = new LineLWB();
				lineLWB.setActualValue(actualValue);
				lineLWB.setLineNumber(lineNo);
				listWritable = new ArrayListWritable<LineLWB>();
				listWritable.add(lineLWB);				
				violationLWB = new ViolationLWB();
				violationLWB.setExpectedValue(expectedFldvalue);
				violationLWB.setLineLWBList(listWritable);
				typeViolationMap.put(validationChkName, violationLWB);
			} else {
				// if field and violation are found
				violationLWB = (ViolationLWB) typeViolationMap.get(validationChkName);
				listWritable = violationLWB.getLineLWBList();
				lineLWB = new LineLWB();
				lineLWB.setActualValue(actualValue);
				lineLWB.setLineNumber(lineNo);
				listWritable.add(lineLWB);				
			}
		} else {
			// if both field and violation not found
			lineLWB = new LineLWB();
			listWritable = new ArrayListWritable<>();
			violationLWB = new ViolationLWB();
			MapWritable typeViolMap = new MapWritable();
			FieldLWB fieldLWB = new FieldLWB();
			lineLWB.setActualValue(actualValue);
			lineLWB.setLineNumber(lineNo);
			listWritable.add(lineLWB);
			violationLWB.setExpectedValue(expectedFldvalue);
			violationLWB.setLineLWBList(listWritable);
			typeViolMap.put(validationChkName, violationLWB);
			fieldLWB.putAll(typeViolMap);
			fieldMap.put(fieldNoWritable, fieldLWB);
		}
	}

	/**
	 * Validate number of fields.
	 *
	 * @param key refers to the record number in the file present on HDFS.
	 * @param context is used to write the data violation that are present in the file.
	 * @param actualNumOfFields denotes the number of fields that are present in the file on the HDFS.
	 * @param dataValidationWritableBeanList 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private boolean validateNumberOfFields(int actualNumOfFields, DataViolationWB dataViolationWB) throws IOException, InterruptedException {
		LineLWB lineLWB = null;
		ViolationLWB violationLWB = null;		
		ArrayListWritable<LineLWB> listWritable;	
		
		Text actualValue = new Text(Integer.toString(actualNumOfFields));
		
		Text expectedFldvalue = new Text(Integer.toString(expectedNumOfFields));
		LongWritable lineNo = new LongWritable(lineNumber);		
		
		IntWritable fieldNoWritable = new IntWritable(DataValidationConstants.MINUS_ONE);		
		
		if (expectedNumOfFields != actualNumOfFields) {
			MapWritable fieldMap = dataViolationWB.getFieldMap();			
			if (fieldMap.containsKey(fieldNoWritable)){			
				
				FieldLWB fieldLWB = (FieldLWB) fieldMap.get(fieldNoWritable);
				MapWritable typeViolationMap = fieldLWB.getTypeViolationMap();
				violationLWB = (ViolationLWB) typeViolationMap.get(new Text(DataValidationConstants.NUM_OF_FIELDS_CHECK));				
				listWritable = violationLWB.getLineLWBList();
				lineLWB = new LineLWB();
				lineLWB.setActualValue(actualValue);
				lineLWB.setLineNumber(lineNo);
				
				listWritable.add(lineLWB);
				lineVWCounterNOF++;				
				return false;
			} else {				
				lineLWB = new LineLWB();
				lineLWB.setActualValue(actualValue);
				lineLWB.setLineNumber(lineNo);		
				
				listWritable = new ArrayListWritable<LineLWB>();
				listWritable.add(lineLWB);
				
				lineVWCounterNOF++;		
				
				violationLWB = new ViolationLWB();				
				violationLWB.setExpectedValue(expectedFldvalue);
				violationLWB.setLineLWBList(listWritable);
				
				MapWritable typeViolMap = new MapWritable();
				typeViolMap.put(new Text(DataValidationConstants.NUM_OF_FIELDS_CHECK), violationLWB);
				FieldLWB fieldLWB = new FieldLWB();
				fieldLWB.putAll(typeViolMap);
				
				fieldMap = new MapWritable();
				fieldMap.put(new IntWritable(DataValidationConstants.MINUS_ONE), fieldLWB);
				dataViolationWB.putAll(fieldMap);
				
				return false;
			}			
		}
		return true;
	}

	/**
	 * applies null check and returns false if validation check fails,else true.
	 *
	 * @param expectedValue the value expected by the user
	 * @param actualFieldValue the actual value of the field
	 * @return false if validation check fails,else true
	 */
	private boolean applyNullCheck(String expectedValue, String actualFieldValue) {

		boolean flag = true;
		if (DataValidationConstants.NOT_NULL.equals(expectedValue)) {
			if (isNullOrEmpty(actualFieldValue)) {
				flag = false;
			}
		} else {
			if (!isNullOrEmpty(actualFieldValue)) {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * applies data type check and returns false if validation check fails,else true.
	 *
	 * @param expectedValue the value expected by the user
	 * @param actualFieldValue the actual value of the field
	 * @return false if validation check fails,else true
	 */
	private boolean applyDataTypeCheck(String expectedValue, String actualFieldValue) {
		boolean flag = true;
		DataValidationConstants.DataTypes dataType = DataValidationConstants.DataTypes.valueOf(expectedValue);
		try {
			switch (dataType) {
			case int_type:	
				flag = isInteger(actualFieldValue);
				break;
			case long_type:
				Long.parseLong(actualFieldValue);
				break;
			case float_type:
				Float.parseFloat(actualFieldValue);
				break;
			case double_type:
				Double.parseDouble(actualFieldValue);
				break;
			default:
				break;
			}
		} catch (NumberFormatException nfe) {
			flag = false;
		}
		return flag;

	}

	/**
	 * Applies regex check and returns false if validation check fails,else true.
	 *
	 * @param expectedValue the value expected by the user
	 * @param actualFieldValue the actual value of the field
	 * @return false if validation check fails,else true
	 */
	private boolean applyRegexCheck(String expectedValue, String actualFieldValue) {
		Pattern pattern ;
		if(keyPattern.containsKey(expectedValue)){
			pattern = keyPattern.get(expectedValue);
		}else{
			pattern = Pattern.compile(expectedValue);
			keyPattern.put(expectedValue, pattern);
		}
		Matcher matcher = pattern.matcher(actualFieldValue);
		return matcher.matches();
	}

	
	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	private boolean isNullOrEmpty(String value) {
		
		return ((DataValidationConstants.NULL.equalsIgnoreCase(value)) || (DataValidationConstants.EMPTY_STRING.equals(value.trim())));
		
	}
	
	/**
	 * Checks if the given string is integer or not.
	 *
	 * @param str the str
	 * @return true, if is integer
	 */
	private boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
}