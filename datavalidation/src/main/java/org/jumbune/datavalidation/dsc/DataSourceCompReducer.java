package org.jumbune.datavalidation.dsc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.jumbune.common.beans.dsc.DataSourceCompValidation;
import org.jumbune.common.beans.dsc.DataSourceCompValidationInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataSourceCompReducer extends Reducer<Text, DataSourceCompMapValueWritable, Text, Text> {

	private static final String EMPTY_STRING = "";
	private static final char DOT = '.';
	private static final String _1 = "1";
	private DataSourceCompValidationInfo validationInfo;
	private MultipleOutputs<NullWritable, Text> multipleOutputs;
	private Map<String, String> filesMap;
	Gson gson = new Gson();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(Reducer.Context context) {
		
		Configuration conf = context.getConfiguration();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();

		filesMap = gson.fromJson(conf.get("filesMap"), type);
		validationInfo = gson.fromJson(conf.get("validationInfoJson"), DataSourceCompValidationInfo.class);
		multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
	}

	public void reduce(Text primaryKey, Iterable<DataSourceCompMapValueWritable> rows, Context context)
			throws IOException, InterruptedException {
		Iterator<DataSourceCompMapValueWritable> it = rows.iterator();
		String[] sourceFields = null, targetFields = null;
		String sourceFilePath = null, targetFilePath = null;

		while (it.hasNext()) {
			DataSourceCompMapValueWritable temp = it.next();
			if (temp.getIsSource().get()) {
				sourceFilePath = temp.getFilePath().toString();
				sourceFields = getSplits(temp.getRow().toString(), validationInfo.getFieldSeparator());
			} else {
				targetFilePath = temp.getFilePath().toString();
				targetFields = getSplits(temp.getRow().toString(), validationInfo.getFieldSeparator());
			}
		}

		if (sourceFilePath == null || targetFilePath == null) {
			return;
		}

		String sourceField, targetField, transformationMethod;
		StringBuilder transformationViolationOutputPath;
		boolean flag = true;
		for (DataSourceCompValidation validation : validationInfo.getValidationsList()) {
			try {
				sourceField = sourceFields[validation.getSourcefieldNumber() - 1];
			} catch (IndexOutOfBoundsException e) {
				writeNoOfFieldsViolation(sourceFilePath);
				writeInvalidRowsViolation(sourceFilePath, DataSourceCompConstants.NO_OF_FIELDS_VIOLATION);
				return;
			}
			try {
				targetField = targetFields[validation.getDestinationFieldNumber() - 1];
			} catch (IndexOutOfBoundsException e) {
				writeNoOfFieldsViolation(targetFilePath);
				writeInvalidRowsViolation(targetFilePath, DataSourceCompConstants.NO_OF_FIELDS_VIOLATION);
				return;
			}
			transformationMethod = validation.getTransformationMethod();

			sourceField = getTargetField(transformationMethod, sourceField);

			if (!sourceField.equals(targetField)) {
				transformationViolationOutputPath = new StringBuilder(DataSourceCompConstants.TRANSFORMATION_VIOLATION)
						.append(DataSourceCompConstants.SLASH)
						.append(validation.getTransformationNumber())
						.append(DataSourceCompConstants.SLASH)
						.append(filesMap.get(targetFilePath))
						.append(DataSourceCompConstants.SLASH);
				writeTransformationViolation(transformationViolationOutputPath.toString(), primaryKey.toString(),
						transformationMethod, sourceField, targetField);
				if (flag) {
					flag = false;
					writeInvalidRowsViolation(targetFilePath, DataSourceCompConstants.TRANSFORMATION_VIOLATION);
				}
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		multipleOutputs.close();
	}

	private void writeNoOfFieldsViolation(String outputPath) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder(DataSourceCompConstants.NO_OF_FIELDS_VIOLATION)
				.append(DataSourceCompConstants.SLASH).append(filesMap.get(outputPath))
				.append(DataSourceCompConstants.SLASH);
		multipleOutputs.write(NullWritable.get(), new Text(_1), sb.toString());
	}

	private void writeInvalidRowsViolation(String outputPath, String dueTo) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder(DataSourceCompConstants.INVALID_ROWS)
				.append(DataSourceCompConstants.SLASH).append(dueTo).append(DataSourceCompConstants.SLASH)
				.append(filesMap.get(outputPath)).append(DataSourceCompConstants.SLASH);

		multipleOutputs.write(NullWritable.get(), new Text(_1), sb.toString());
	}

	/**
	 * 
	 * @param path
	 *            hdfs path where voilation will be written
	 * @param key
	 *            primary key or composite key
	 * @param transformationMethod
	 *            transformation method (packagename.classname.methodname eg.
	 *            org.jumbune.datavalidation.DataValidationJobExecutor.aggregateCounters)
	 * @param expected
	 *            expected target field
	 * @param actual
	 *            actual target field
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void writeTransformationViolation(String path, String key, String transformationMethod, String expected,
			String actual) throws IOException, InterruptedException {
		if (transformationMethod == null) {
			transformationMethod = EMPTY_STRING;
		}
		StringBuilder output = new StringBuilder().append(key).append(DataSourceCompConstants.PIPE_SEPARATOR)
				.append(transformationMethod).append(DataSourceCompConstants.PIPE_SEPARATOR).append(expected)
				.append(DataSourceCompConstants.PIPE_SEPARATOR).append(actual);
		multipleOutputs.write(NullWritable.get(), new Text(output.toString()), path);
	}

	private String getTargetField(String transformationMethod, String sourceField) {

		if (transformationMethod != null && !transformationMethod.trim().isEmpty()) {
			String className = transformationMethod.substring(0, transformationMethod.lastIndexOf(DOT));
			String methodName = transformationMethod.substring(transformationMethod.lastIndexOf(DOT) + 1);
			try {
				Class<?> transformationClass = Class.forName(className);
				Method method = transformationClass.getMethod(methodName, String.class);
				sourceField = (String) method.invoke(transformationClass.newInstance(), new String(sourceField));

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
		return sourceField;
	}
	
	private String[] getSplits(String line, String character) {
		int from = 0, to = 0, charLength = character.length();
		List<String> splits = new ArrayList<>();
		while (true) {
			to = line.indexOf(character, from);
			// if this is the last split
			if (to == -1) {
				splits.add(line.substring(from));
				break;
			}
			// if line ends with character
			if (to == line.length() - 1) {
				splits.add(line.substring(from, to));
				splits.add("");
				break;
			}
			splits.add(line.substring(from, to));
			from = to + charLength;
		}
		return splits.toArray(new String[splits.size()]);
	}

}
