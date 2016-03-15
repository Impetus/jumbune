package org.jumbune.datavalidation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.scheduler.DataQualityTaskScheduler;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.JobUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * The Mapper takes<record number, record value> as input and writes <data violation type,data violation bean> as output.
 * 

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
	
	/** The data violation type. */
	private Text dataViolationType = new Text();
	
	/** The field validation list. */
	private List<FieldValidationBean> fieldValidationList;
	
	private String contextKey = null;
	
	/** The slave file loc. */
	String SLAVE_FILE_LOC = "slaveFileLoc";
	
	/* Logger */
	private static final Logger LOGGER = LogManager
			.getLogger(DataValidationMapper.class);

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
		noOfToupleProcessd = 0l;
		cleanTupleCounter=0l;
		contextKey = new StringBuffer().append(DataValidationConstants.NUM_OF_FIELDS_CHECK).append("|").append(DataValidationConstants.USER_DEFINED_NULL_CHECK)
					.append("|").append(DataValidationConstants.USER_DEFINED_DATA_TYPE).append("|").append(DataValidationConstants.USER_DEFINED_REGEX_CHECK).toString();
				
		// populating data validation parameters
		String dvBeanString = context.getConfiguration().get(DataValidationConstants.DATA_VALIDATION_BEAN_STRING);
		Gson gson = new Gson();
		Type type = new TypeToken<DataValidationBean>() {
		}.getType();
		DataValidationBean dataValidationBean = gson.fromJson(dvBeanString, type);

		fieldSeparator = dataValidationBean.getFieldSeparator();
		fieldSeparator = fieldSeparator.replaceAll(Constants.SPACE_SEPARATOR, Constants.SPACE);
		fieldValidationList = dataValidationBean.getFieldValidationList();
		expectedNumOfFields = dataValidationBean.getNumOfFields();

		// getting file name
		InputSplit is = context.getInputSplit();
		if (is instanceof DataValidationFileSplit) {
			fileName = ((DataValidationFileSplit) is).getPath().toUri().getPath();
			fileName = fileName.replaceAll("/", ".").substring(1, fileName.length());
		}

	}

	/**
	 * Map function that takes<record number, record value> as input
	 * and writes <data violation type,data violation bean> as output.
	 */
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		noOfToupleProcessd = noOfToupleProcessd +1;
		String recordValue = value.toString();
		String[] fields = recordValue.split(fieldSeparator);
		int actualNumOfFields = fields.length;
		String actualFieldValue = null;
		int fieldNumber = 0;
		String nullCheck = null;
		String dataType = null;
		String regex = null;
		
		DataDiscrepanciesArrayWritable dataValidatoinDiscripancies = new DataDiscrepanciesArrayWritable();
		List<DataViolationWritableBean> dataValidationWritableBeanList = new ArrayList<DataViolationWritableBean>();
		
		// validating the number of fields in the record
		if(!validateNumberOfFields(key, context, actualNumOfFields, dataValidationWritableBeanList)){
			writeViolations(dataValidatoinDiscripancies, dataValidationWritableBeanList, context);
			return;
		}
		
		for (FieldValidationBean fieldValidationBean : fieldValidationList) {
			fieldNumber = fieldValidationBean.getFieldNumber();
			actualFieldValue = fields[fieldNumber];
			nullCheck = fieldValidationBean.getNullCheck();
			dataType = fieldValidationBean.getDataType();
			regex = fieldValidationBean.getRegex();
			if(validate(nullCheck) && !applyNullCheck(nullCheck, actualFieldValue)){
			 writeFailedValidationToOutput(key, context,	actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_NULL_CHECK, nullCheck,dataValidationWritableBeanList);
			}
			if((!isNullOrEmpty(actualFieldValue)) && (validate(dataType) && !applyDataTypeCheck(dataType, actualFieldValue))){
				writeFailedValidationToOutput(key, context,
						actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_DATA_TYPE, dataType,dataValidationWritableBeanList);
				
			}
			
			if((!isNullOrEmpty(actualFieldValue)) && (validate(regex) && applyRegexCheck(regex, actualFieldValue))){
				writeFailedValidationToOutput(key, context,
						actualFieldValue, fieldNumber,
						DataValidationConstants.USER_DEFINED_REGEX_CHECK, regex,dataValidationWritableBeanList);
				
			}
		}
		
		if(!dataValidationWritableBeanList.isEmpty()){
			writeViolations(dataValidatoinDiscripancies, dataValidationWritableBeanList, context);
		}else{
			cleanTupleCounter++;
		}
	}
	
	private void writeViolations(
			DataDiscrepanciesArrayWritable dataValidatoinDiscripancies,
			List<DataViolationWritableBean> dataValidationWritableBeanList, Context context) throws IOException, InterruptedException {
		dataValidatoinDiscripancies.setFileName(fileName);
		dataValidatoinDiscripancies.set( dataValidationWritableBeanList.toArray ( 
				new DataViolationWritableBean[dataValidationWritableBeanList.size()]));
		context.write(new Text(contextKey), dataValidatoinDiscripancies);
	}

	@Override
	protected void cleanup(
			Mapper<Object, Text, Text, DataDiscrepanciesArrayWritable>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		String dir = context.getConfiguration().get(SLAVE_FILE_LOC);
		String dirPath = JobUtil.getAndReplaceHolders(dir);
		dirPath = dirPath +File.separator+"tuple"+File.separator;
		new File(dirPath).mkdirs();
		FileWriter fileWriter =null;
		try{
			fileWriter = new FileWriter(new File(dirPath, context.getTaskAttemptID().getTaskID().toString()));
			fileWriter.write(Long.toString(noOfToupleProcessd)+"\n"+Long.toString(cleanTupleCounter));
			fileWriter.flush();
		}catch (IOException e) {
			LOGGER.error("Error while write info to file ", e);
		}
				finally{
			if(fileWriter!= null){
				try{
				fileWriter.close();
				}catch(IOException ex){
					LOGGER.error("exception occured while closing properties file",ex);
			}
				
			}
		}
				
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
	private void writeFailedValidationToOutput(Object key, Context context,
			String actualFieldValue, int fieldNumber,
			String validationCheckName, String expectedValue, List<DataViolationWritableBean> dataValidationWritableBeanList)
			throws IOException, InterruptedException {
			DataViolationWritableBean dataViolationWritableBean = null;
			dataViolationType.set(validationCheckName);
			dataViolationWritableBean = new DataViolationWritableBean();
			dataViolationWritableBean.setExpectedValue(expectedValue);
			dataViolationWritableBean.setActualValue(actualFieldValue);
			dataViolationWritableBean.setFieldNumber(fieldNumber + 1);
			dataViolationWritableBean.setLineNumber(Integer.parseInt(key.toString()));
			if(DataValidationConstants.USER_DEFINED_REGEX_CHECK.equals(validationCheckName)){
				dataViolationWritableBean.setViolationType(DataValidationConstants.USER_DEFINED_REGEX_CHECK);
				dataValidationWritableBeanList.add(dataViolationWritableBean);
			}else if(DataValidationConstants.USER_DEFINED_NULL_CHECK.equals(validationCheckName)){
				dataViolationWritableBean.setViolationType(DataValidationConstants.USER_DEFINED_NULL_CHECK);
				dataValidationWritableBeanList.add(dataViolationWritableBean);
			}else if(DataValidationConstants.USER_DEFINED_DATA_TYPE.equals(validationCheckName)){
				dataViolationWritableBean.setViolationType(DataValidationConstants.USER_DEFINED_DATA_TYPE);
				dataValidationWritableBeanList.add(dataViolationWritableBean);
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
	private boolean validateNumberOfFields(Object key, Context context,
			int actualNumOfFields, List<DataViolationWritableBean> dataValidationWritableBeanList) throws IOException, InterruptedException {
		DataViolationWritableBean dataViolationWritableBean;
		if (expectedNumOfFields != actualNumOfFields) {
			dataViolationWritableBean = new DataViolationWritableBean();
			dataViolationWritableBean.setExpectedValue(Integer.toString(expectedNumOfFields));
			dataViolationWritableBean.setActualValue(Integer.toString(actualNumOfFields));
			dataViolationWritableBean.setFieldNumber(DataValidationConstants.MINUS_ONE);
			dataViolationWritableBean.setLineNumber(Integer.parseInt(key.toString()));
			dataViolationType.set(DataValidationConstants.NUM_OF_FIELDS_CHECK);
			dataViolationWritableBean.setViolationType(DataValidationConstants.NUM_OF_FIELDS_CHECK);
			dataValidationWritableBeanList.add(dataViolationWritableBean);
			return false;
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
				Integer.parseInt(actualFieldValue);
			case long_type:
				Long.parseLong(actualFieldValue);
			case float_type:
				Float.parseFloat(actualFieldValue);
			case double_type:
				Double.parseDouble(actualFieldValue);
			default:
				break;
			}
		} catch (NumberFormatException nfe) {
			LOGGER.error("exception occured while parsing the number",nfe);
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
		Pattern p = Pattern.compile(expectedValue);
		Matcher matcher = p.matcher(actualFieldValue);
		return matcher.matches();
	}

	/**
	 * Checks whether the input is a valid value or not.
	 *
	 * @param expectedValue performs null check and validate the value
	 * @return true, if successful
	 */
	private boolean validate(final String expectedValue) {
		
		if ((expectedValue == null)) {
			return false;
		} else {
			String expValue;
			expValue = expectedValue.replaceAll("\\u00a0", " ");
			if ((expValue.trim().length() == 0)) {
				return false;
			}

		}
		return true;

	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	private boolean isNullOrEmpty(String value) {
		
		return ((DataValidationConstants.EMPTY_STRING.equals(value.trim()) || (value.equalsIgnoreCase(DataValidationConstants.NULL))));
		
	}
}
