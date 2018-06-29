package org.jumbune.datavalidation.dsc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.dsc.DataSourceCompMapperInfo;
import org.jumbune.common.beans.dsc.DataSourceCompReportBean;
import org.jumbune.common.beans.dsc.DataSourceCompValidation;
import org.jumbune.common.beans.dsc.DataSourceCompValidationInfo;
import org.jumbune.datavalidation.DataValidationConstants;

import com.google.gson.Gson;

public class DataSourceCompJobExecutor {

	private static final String DOT = ".";
	private static final String COMMA = ",";
	private static final Logger LOGGER = LogManager.getLogger(DataSourceCompJobExecutor.class);

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		// LOGGER.debug("Data validation job received args length [ " +
		// otherArgs.length + "]");
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < otherArgs.length; j++) {
			sb.append(otherArgs[j]);
		}
		String validationInfoJson = sb.toString();
		Gson gson = new Gson();
		DataSourceCompValidationInfo validationInfo = gson.fromJson(validationInfoJson,
				DataSourceCompValidationInfo.class);
		DataSourceCompJobExecutor dscJobExecutor = new DataSourceCompJobExecutor();
		dscJobExecutor.removeSlash(validationInfo);
		dscJobExecutor.addTransformationNumber(validationInfo);
		DataSourceCompMapperInfo mapperInfo = dscJobExecutor.createMapperInfo(validationInfo);
		String outputPath = DataSourceCompConstants.OUTPUT_DIR_PATH + new Date().getTime();
		// String outputPath = "/destination";
		conf.set("validationInfoJson", gson.toJson(validationInfo));
		conf.set("mapperInfoJson", gson.toJson(mapperInfo));

		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

		FileSystem fileSystem = FileSystem.get(conf);
		List<Path> mapperFilesList = dscJobExecutor.getFiles(validationInfo.getSourcePath(), fileSystem);
		mapperFilesList.addAll(dscJobExecutor.getFiles(validationInfo.getDestinationPath(), fileSystem));

		Map<String, String> filesMap = dscJobExecutor.encodeFilesMap(mapperFilesList);

		Map<String, String> reverseFilesMap = dscJobExecutor.invertMap(filesMap);

		Path[] patharr = new Path[mapperFilesList.size()];
		for (int i = 0; i < mapperFilesList.size(); i++) {
			patharr[i] = mapperFilesList.get(i);
		}

		conf.set("filesMap", gson.toJson(filesMap));
		String recordSeparator = validationInfo.getRecordSeparator();
		if (recordSeparator == null || recordSeparator.trim().isEmpty()) {
			recordSeparator = "\n";
		}
		conf.set("textinputformat.record.delimiter", recordSeparator);
		
		Job job = Job.getInstance(conf, "jumbune_dsc_" + validationInfo.getJobName());
		job.setJarByClass(DataSourceCompJobExecutor.class);
		job.setMapperClass(org.jumbune.datavalidation.dsc.DataSourceCompMapper.class);
		job.setReducerClass(DataSourceCompReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DataSourceCompMapValueWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		
		FileInputFormat.setInputPaths(job, patharr);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		MultipleOutputs.setCountersEnabled(job, true);

		job.waitForCompletion(true);
		String workerDirPath = validationInfo.getSlaveFileLoc();
		dscJobExecutor.copyResult(conf, outputPath, workerDirPath);
		dscJobExecutor.renameFiles(workerDirPath, reverseFilesMap);
		DataSourceCompReportBean reportBean = dscJobExecutor.calculateCounters(job, outputPath, reverseFilesMap,
				validationInfo.getValidationsList());
		LOGGER.info(DataValidationConstants.DV_REPORT + gson.toJson(reportBean));
	}

	private void addTransformationNumber(DataSourceCompValidationInfo validationInfo) {
		int i = 1;
		for (DataSourceCompValidation validation : validationInfo.getValidationsList()) {
			if (validation.getTransformationNumber() == null) {
				validation.setTransformationNumber(DataSourceCompConstants.TRANSFORMATION_NUMBER + i++);
			}
		}
	}

	private void copyResult(Configuration conf, String outputDirPath, String workerDirPath) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);
		Path path = new Path(
				outputDirPath + DataSourceCompConstants.SLASH + DataSourceCompConstants.TRANSFORMATION_VIOLATION);
		if (fileSystem.exists(path)) {
			fileSystem.copyToLocalFile(path, new Path(workerDirPath));
		}
	}

	private void renameFiles(String workerDirPath, Map<String, String> reverseFileMap) throws IOException {

		/**
		 * Here is the sample list of output files.
		 * invalidRows/noOfFieldsViolation/2/
		 * invalidRows/transformationViolation/2/
		 * invalidRows/transformationViolation/3/
		 * invalidRows/transformationViolation/4/
		 * noOfFieldsViolation/2/
		 * transformationViolation/T3/2/
		 * transformationViolation/T3/3/
		 * transformationViolation/T3/4/
		 * transformationViolation/T5/2/
		 * transformationViolation/T5/3/
		 * transformationViolation/T5/4/
		 * transformationViolation/T6/4/
		 * 
		 * We are going to decode the encoded file names (2, 3, 4 etc.) in
		 * transformationViolation/ directory only and delete the rest of the
		 * files because others are not necessary
		 */
		File workerDir = new File(workerDirPath);
		if (!workerDir.exists()) {
			return;
		}
		File[] violationsDirList = workerDir.listFiles();
		for (File violationTypeDir : violationsDirList) {
			if (violationTypeDir.isFile()
					|| !violationTypeDir.getName().equals(DataSourceCompConstants.TRANSFORMATION_VIOLATION)) {
				FileUtils.forceDelete(violationTypeDir);
				continue;
			}

			for (File transformation : violationTypeDir.listFiles()) {
				for (File violationFileDir : transformation.listFiles()) {
					String fileName = reverseFileMap.get(violationFileDir.getName());
					fileName = fileName.substring(1).replaceAll(DataSourceCompConstants.SLASH, DOT);
					String newPath = violationFileDir.getParent() + DataSourceCompConstants.SLASH + fileName;
					violationFileDir.renameTo(new File(newPath));
				}
			}
		}
	}

	/**
	 * We are encoding the file paths beacuse we are using filepaths in hadoop
	 * counters and Hadoop counters key length can't exceed 65 character limit
	 * otherwise it will truncate the key eg. It will truncate
	 * invalidRows/transformationViolation/Jumbune.Demo.input.preprocessed.input/data1/=8
	 * into invalidRows/transformationViolation/Jumbune.Demo.input.preprocess=4
	 * and it will become useless. Therefore we are providing each filepath a
	 * unique number.
	 * 
	 * @param filesList
	 * @return map where key is filepath and value is its encoded value
	 */
	private Map<String, String> encodeFilesMap(List<Path> filesList) {
		Map<String, String> map = new HashMap<String, String>();
		int count = 0;
		for (Path p : filesList) {
			map.put(p.toUri().getPath(), String.valueOf(count++));
		}
		return map;
	}

	private DataSourceCompReportBean calculateCounters(Job job, String outputPath,
			Map<String, String> reverseFileMap, List<DataSourceCompValidation> validations) throws IOException {
		if (!outputPath.endsWith(DataSourceCompConstants.SLASH)) {
			outputPath = outputPath + DataSourceCompConstants.SLASH;
		}
		Counters cs = job.getCounters();
		CounterGroup cg = cs.getGroup("org.apache.hadoop.mapreduce.lib.output.MultipleOutputs");

		DataSourceCompReportBean reportBean = new DataSourceCompReportBean();
		String filePath, violationType;
		int firstIndex, secondIndex, thirdIndex;
		Iterator<Counter> it = cg.iterator();
		/*
		 * Here are the sample list of counters.
		 * invalidRows/noOfFieldsViolation/2/=1
		 * invalidRows/transformationViolation/2/=4
		 * invalidRows/transformationViolation/3/=5
		 * invalidRows/transformationViolation/4/=9
		 * noOfFieldsViolation/2/=1
		 * transformationViolation/T3/2/=2
		 * transformationViolation/T3/3/=4
		 * transformationViolation/T3/4/=3
		 * transformationViolation/T5/2/=2
		 * transformationViolation/T5/3/=1
		 * transformationViolation/T5/4/=3
		 * transformationViolation/T6/4/=3
		 * 
		 * The numbers 2,3,4,5.. etc. are encoded file names
		 * 
		 * 
		 * We have to create a json like this
		 * 
		 * {"transformationViolationMap":{"T5":{"validation":{"validationCode":
		 * "T5","sourcefieldNumber":5,"destinationFieldNumber":4},"violations":{
		 * "testing.output.output2":1,"testing.output.output3":2,
		 * "testing.output.output1":3}},"T6":{"validation":{"validationCode":
		 * "T6","sourcefieldNumber":6,"destinationFieldNumber":5},"violations":{
		 * "testing.output.output1":3}},"T3":{"validation":{"validationCode":
		 * "T3","sourcefieldNumber":3,"destinationFieldNumber":2,
		 * "transformationMethod":"in.co.impetus.JobExecutor.getTransformed"},
		 * "violations":{"testing.output.output2":4,"testing.output.output3":2,
		 * "testing.output.output1":3}}},"invalidRows":{
		 * "transformationViolation":18,"noOfFieldsViolation":1},
		 * "noOfFieldsViolationMap":{"testing.output.output3":1}}
		 */
		while (it.hasNext()) {
			Counter c = it.next();
			Long count = c.getValue();
			filePath = c.getName();
			firstIndex = filePath.indexOf(DataSourceCompConstants.SLASH);
			secondIndex = filePath.indexOf(DataSourceCompConstants.SLASH, firstIndex + 1);
			violationType = filePath.substring(0, firstIndex);

			if (violationType.equals(DataSourceCompConstants.INVALID_ROWS)) {
				String dueTo = filePath.substring(firstIndex + 1, secondIndex);
				thirdIndex = filePath.indexOf(DataSourceCompConstants.SLASH, secondIndex + 1);
				if (dueTo.equals(DataSourceCompConstants.NO_OF_FIELDS_VIOLATION)) {
					reportBean.incrementInvalidRowsNoOfFieldsViolation(count);
				} else {
					reportBean.incrementInvalidRowsTransformationViolation(count);
				}

			} else if (violationType.equals(DataSourceCompConstants.TRANSFORMATION_VIOLATION)) {

				String transformationNumber = filePath.substring(firstIndex + 1, secondIndex);
				thirdIndex = filePath.indexOf(DataSourceCompConstants.SLASH, secondIndex + 1);
				String encodedFileName = filePath.substring(secondIndex + 1, thirdIndex);
				String decodedFileName = removeSlash(reverseFileMap.get(encodedFileName));
				reportBean.addTransformationViolation(getValidation(transformationNumber, validations), transformationNumber,
						decodedFileName, count);
			} else {
				String encodedFileName = filePath.substring(firstIndex + 1, secondIndex);
				String decodedFileName = removeSlash(reverseFileMap.get(encodedFileName));
				reportBean.addNumberOfFieldsViolation(decodedFileName, count);
			}
		}
		return reportBean;
	}

	private String removeSlash(String filePath) {
		return filePath.substring(1).replaceAll(DataSourceCompConstants.SLASH, DOT);
	}

	private DataSourceCompValidation getValidation(String transformationNumber, List<DataSourceCompValidation> validations) {

		for (DataSourceCompValidation validation : validations) {
			if (transformationNumber.equals(validation.getTransformationNumber())) {
				return validation;
			}
		}

		return null;
	}

	private DataSourceCompMapperInfo createMapperInfo(DataSourceCompValidationInfo validationInfo) {
		DataSourceCompMapperInfo mapperInfo = new DataSourceCompMapperInfo();

		String[] sTemp = validationInfo.getSourcePrimaryKey().split(COMMA);
		int[] ints = new int[sTemp.length];
		for (int i = 0; i < sTemp.length; i++) {
			ints[i] = Integer.parseInt(sTemp[i]);
		}
		mapperInfo.setSourcePrimaryKey(ints);

		if (validationInfo.getDestinationPrimaryKey() != null) {
			sTemp = validationInfo.getDestinationPrimaryKey().split(COMMA);
			ints = new int[sTemp.length];
			for (int i = 0; i < sTemp.length; i++) {
				ints[i] = Integer.parseInt(sTemp[i]);
			}
			mapperInfo.setDestinationPrimaryKey(ints);
		} else {
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (DataSourceCompValidation validation : validationInfo.getValidationsList()) {
				map.put(validation.getSourcefieldNumber(), validation.getDestinationFieldNumber());
			}
			ints = new int[sTemp.length];
			int[] temp = mapperInfo.getDestinationPrimaryKey();
			for (int i = 0; i < sTemp.length; i++) {
				ints[i] = map.get(temp[i]);
			}
		}
		mapperInfo.setFieldSeparator(validationInfo.getFieldSeparator());
		mapperInfo.setSourcePath(validationInfo.getSourcePath());
		return mapperInfo;
	}

	private void removeSlash(DataSourceCompValidationInfo validationInfo) {
		String str = validationInfo.getSourcePath();
		if (str.endsWith(DataSourceCompConstants.SLASH)) {
			str = str.substring(0, str.length() - 1);
			validationInfo.setSourcePath(str);
		}
		str = validationInfo.getDestinationPath();
		if (str.endsWith(DataSourceCompConstants.SLASH)) {
			str = str.substring(0, str.length() - 1);
			validationInfo.setDestinationPath(str);
		}
	}

	private List<Path> getFiles(String path, FileSystem fileSystem)
			throws IllegalArgumentException, IOException {
		List<Path> list = new ArrayList<Path>();
		Stack<FileStatus> stack = new Stack<FileStatus>();
		stack.push(fileSystem.getFileStatus(new Path(path)));
		FileStatus fileStatus;
		while (!stack.isEmpty()) {
			fileStatus = stack.pop();
			if (fileStatus.isDir()) {
				for (FileStatus temp : fileSystem.listStatus(fileStatus.getPath())) {
					stack.push(temp);
				}
			} else {
				list.add(fileStatus.getPath());
			}
		}

		return list;
	}
	
	private <V, K> Map<V, K> invertMap(Map<K, V> map) {

	    Map<V, K> inverse = new HashMap<V, K>();

	    for (Entry<K, V> entry : map.entrySet()) {
	        inverse.put(entry.getValue(), entry.getKey());
	    }
	    return inverse;
	}

}
