package org.jumbune.datavalidation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.utils.Constants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * The Mapper takes<record number, record value> as input and writes <data violation type,data violation bean> as output.
 * 

 * 
 */
public class DataValidationMapper extends Mapper<Object, Text, Text, ObjectWritable> {

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

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {

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

		

		String recordValue = value.toString();
		String[] fields = recordValue.split(fieldSeparator);
		int actualNumOfFields = fields.length;

		// validating the number of fields in the record
		validateNumberOfFields(key, context, actualNumOfFields);

		checkFieldValidationListNullOrEmpty();
		String actualFieldValue = null;
		boolean isValid = true;
		int fieldNumber = 0;
		String nullCheck = null;
		String dataType = null;
		String regex = null;
		int validationCheckNum = 0;
		String validationCheckName = null;
		String expectedValue = null;
		for (FieldValidationBean fieldValidationBean : fieldValidationList) {
			fieldNumber = fieldValidationBean.getFieldNumber();
			actualFieldValue = fields[fieldNumber];
			nullCheck = fieldValidationBean.getNullCheck();
			dataType = fieldValidationBean.getDataType();
			regex = fieldValidationBean.getRegex();
			for (Map.Entry<Integer, String> fieldValidationEntry : DataValidationConstants.VALIDATION_CHECK_MAP.entrySet()) {
				validationCheckNum = fieldValidationEntry.getKey();
				validationCheckName = fieldValidationEntry.getValue();

				// if none of the violation has failed
				if (isValid) {
					switch (validationCheckNum) {
					case 1:
						expectedValue = nullCheck;
						if (validate(expectedValue)) {
							isValid = applyNullCheck(expectedValue, actualFieldValue);
						}

						break;
					case 2:
						expectedValue = dataType;
						if (validate(expectedValue)) {
							isValid = applyDataTypeCheck(expectedValue, actualFieldValue);
						}
						break;
					case DataValidationConstants.THREE:
						expectedValue = regex;
						if (validate(expectedValue)) {
							isValid = applyRegexCheck(expectedValue, actualFieldValue);
						}

						break;
					default:
						break;
					}

					// if any of the validation check has failed,write to the
					// output
					isValid = writeFailedValidationToOutput(key, context,
							actualFieldValue, isValid, fieldNumber,
							validationCheckName, expectedValue);
				}
			}

		}
	}

	/**
	 * Check field validation list null or empty.
	 */
	private void checkFieldValidationListNullOrEmpty() {
		if (fieldValidationList == null || fieldValidationList.isEmpty()) {
			return;
		}
	}

	/**
	 * Write failed validation to output.
	 *
	 * @param key refers to the record number in the file present on HDFS.
	 * @param context is used to write the data violation that are present in the file.
	 * @param actualFieldValue refers to the actual value of the field that is present on HDFS.
	 * @param isValid flag denotes whether there are violations present to be written to the output.
	 * @param fieldNumber refers to the number of the field that is present in the file on HDFS.
	 * @param validationCheckName  refers to null,regex and data type violations name.
	 * @param expectedValue refers to what the value of the field on HDFS should be.
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private boolean writeFailedValidationToOutput(Object key, Context context,
			String actualFieldValue, final boolean isValid, int fieldNumber,
			String validationCheckName, String expectedValue)
			throws IOException, InterruptedException {
		DataViolationWritableBean dataViolationWritableBean = null;
		boolean isItValid = isValid;
		if (!isValid) {
			dataViolationType.set(validationCheckName);
			dataViolationWritableBean = new DataViolationWritableBean();
			dataViolationWritableBean.setExpectedValue(expectedValue);
			dataViolationWritableBean.setActualValue(actualFieldValue);
			dataViolationWritableBean.setFieldNumber(fieldNumber + 1);
			dataViolationWritableBean.setLineNumber(Integer.parseInt(key.toString()));
			dataViolationWritableBean.setFileName(fileName);
			context.write(dataViolationType, new ObjectWritable(DataViolationWritableBean.class, dataViolationWritableBean));
			// reset the flag
			isItValid = true;
		}
		return isItValid;
	}

	/**
	 * Validate number of fields.
	 *
	 * @param key refers to the record number in the file present on HDFS.
	 * @param context is used to write the data violation that are present in the file.
	 * @param actualNumOfFields denotes the number of fields that are present in the file on the HDFS.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void validateNumberOfFields(Object key, Context context,
			int actualNumOfFields) throws IOException, InterruptedException {
		DataViolationWritableBean dataViolationWritableBean;
		if (expectedNumOfFields != actualNumOfFields) {
			dataViolationWritableBean = new DataViolationWritableBean();
			dataViolationWritableBean.setExpectedValue(Integer.toString(expectedNumOfFields));
			dataViolationWritableBean.setActualValue(Integer.toString(actualNumOfFields));
			dataViolationWritableBean.setFieldNumber(DataValidationConstants.MINUS_ONE);
			dataViolationWritableBean.setLineNumber(Integer.parseInt(key.toString()));
			dataViolationWritableBean.setFileName(fileName);
			dataViolationType.set(DataValidationConstants.NUM_OF_FIELDS_CHECK);
			context.write(dataViolationType, new ObjectWritable(DataViolationWritableBean.class, dataViolationWritableBean));
			return;
		}
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
		
		return ((DataValidationConstants.EMPTY_STRING.equals(value) || (value.equalsIgnoreCase(DataValidationConstants.NULL))));
		
	}
}
