package org.jumbune.datavalidation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.datavalidation.ArrayListWritable;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class executes the Job, reads the output from hdfs and returns the data
 * violation report in JSON format.
 * 
 * 
 * 
 * 
 */
public final class DataValidationJobExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DataValidationJobExecutor.class);

	private static final String TEXTINPUTFORMAT_RECORD_DELIMITER = "textinputformat.record.delimiter";

	/**
	 * Instantiates a new data validation job executor.
	 */
	private DataValidationJobExecutor() {

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws JumbuneException
	 *             the hTF exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static void main(String[] args)
			throws JumbuneException, IOException, InterruptedException, ClassNotFoundException {
		
		Configuration conf = new Configuration();		
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();		
		
		LOGGER.debug("Data validation job received args length [ " + otherArgs.length + "]"
				+ "and values respectively [" + otherArgs[0] + "], " + otherArgs[1] + ", [" + otherArgs[2] + "]" + ", ["
				+ otherArgs[3] + "]");
		StringBuilder sb = new StringBuilder();
		
		int dynamicArgs = 0;
		
		dynamicArgs = ((otherArgs.length)-1);		
		
		for (int j = dynamicArgs; j < otherArgs.length; j++) {
			sb.append(otherArgs[j]);
		}
		String outputPath = DataValidationConstants.OUTPUT_DIR_PATH + new Date().getTime();
		String inputPath = otherArgs[0];		
		String slaveFileLoc = otherArgs[1];		
		String maxViolations = otherArgs[2];
		String noOfReducers = otherArgs[3];		
		String dvBeanString = sb.toString();
		LOGGER.debug("Received dvBean value [" + dvBeanString + "]");
		
		Gson gson = new Gson();
		Type type = new TypeToken<DataValidationBean>() {
		}.getType();
		DataValidationBean dataValidationBean = gson.fromJson(dvBeanString, type);
		String recordSeparator = dataValidationBean.getRecordSeparator();
		List<FieldValidationBean> fieldValidationList = dataValidationBean.getFieldValidationList();
		boolean[][] validateArray = new boolean[3][dataValidationBean.getNumOfFields()];
		for (FieldValidationBean fieldValidationBean : fieldValidationList) {
			validateArray[DataValidationConstants.NULL_MATRIX][(fieldValidationBean.getFieldNumber() - 1)] = validate(
					fieldValidationBean.getNullCheck());
			validateArray[DataValidationConstants.DATA_TYPE_MATRIX][(fieldValidationBean.getFieldNumber()
					- 1)] = validate(fieldValidationBean.getDataType());
			validateArray[DataValidationConstants.REGEX_MATRIX][(fieldValidationBean.getFieldNumber() - 1)] = validate(
					fieldValidationBean.getRegex());
		}
		conf.set(DataValidationConstants.VALIDATE_MATRIX, new Gson().toJson(validateArray));
		conf.set(DataValidationConstants.DATA_VALIDATION_BEAN_STRING, dvBeanString);
		conf.set(DataValidationConstants.RECORD_SEPARATOR, recordSeparator);
		conf.set(DataValidationConstants.SLAVE_FILE_LOC, slaveFileLoc);

		conf.setInt(DataValidationConstants.DV_NUM_REPORT_VIOLATION, Integer.parseInt(maxViolations));
		conf.set(TEXTINPUTFORMAT_RECORD_DELIMITER, recordSeparator);

		/* Partitioner specific configuration */	
				
		conf.setBoolean(DataValidationConstants.CONFIGURE_RR_FOR_PARTITION_NO, true);
		conf.setBoolean(DataValidationConstants.CONFIGURE_RR_FOR_REDUCERS, true);

		Job job = new Job(conf, DataValidationConstants.JOB_NAME);
		job.setJarByClass(DataValidationJobExecutor.class);
		job.setMapperClass(DataValidationMapper.class);
		job.setReducerClass(DataValidationReducer.class);
		job.setPartitionerClass(DataValidationPartitioner.class);
		job.setNumReduceTasks(Integer.parseInt(noOfReducers));
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DataDiscrepanciesArrayWritable.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DataViolationWritable.class);

		Path[] inputPaths = FileUtil.getAllNestedFilePath(job, inputPath);
		
		TextInputFormat.setInputPaths(job, inputPaths);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		LOGGER.debug("Job execution Started");
		job.waitForCompletion(true);

		LOGGER.debug("Job completion over,going to read the result from hdfs");
		Map<String, DataValidationReport> jsonMap = readDataFromHdfs(conf, outputPath);
		final Gson dvReportGson = new Gson();

		final String jsonString = dvReportGson.toJson(jsonMap);
		LOGGER.info("Completed DataValidation");
		LOGGER.info(DataValidationConstants.DV_REPORT + jsonString);
	}

	/**
	 * Read data from hdfs.
	 *
	 * @param conf
	 *            is the hadoop configuration used to read the data from the
	 *            HDFS.
	 * @param outputPath
	 *            is the path of the HDFS data.
	 * @return json Map containing the violations that are present in the data
	 *         on the HDFS.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Map<String, DataValidationReport> readDataFromHdfs(Configuration conf, String outputPath)
			throws IOException {
		Map<String, DataValidationReport> jsonMap = new HashMap<String, DataValidationReport>();

		FileSystem fs = FileSystem.get(conf);
		Path inFile = new Path(outputPath);
		FileStatus[] fss = fs.listStatus(inFile);
		Path path = null;
		Text key = null;
		DataViolationWritable value = null;
		SequenceFile.Reader reader = null;
		DataValidationReport report = null;
		List<FileViolationsWritable> violationList = null;
		ArrayListWritable<FileViolationsWritable> listWritable = new ArrayListWritable<FileViolationsWritable>();
		Map<String, List<DataValidationReport>> reportMap = new HashMap<String, List<DataValidationReport>>();
		for (FileStatus status : fss) {
			path = status.getPath();

			if (!((path.getName().equals(DataValidationConstants.HADOOP_SUCCESS_FILES))
					|| (path.getName().equals(DataValidationConstants.HADOOP_LOG_FILES)))) {
				LOGGER.debug("Going to read the file : [" + path.getName() + "] at path [" + path + "]");
				reader = new SequenceFile.Reader(fs, path, conf);
				Map<Integer, Long> fieldMap = null;
				key = new Text();
				value = new DataViolationWritable();

				while (reader.next(key, value)) {
					long dirtyTuple = 0l;
					long totalViolations = 0l;
					dirtyTuple = value.getDirtyTuple();
					totalViolations = value.getTotalViolations();
					listWritable = value.getFileViolationsWritables();
					fieldMap = new HashMap<Integer, Long>();
					MapWritable mapWritable = value.getFieldMap();

					if (mapWritable != null) {
						for (Map.Entry<Writable, Writable> pairs : mapWritable.entrySet()) {
							int fieldNumber = ((IntWritable) pairs.getKey()).get();
							long fieldViolations = ((IntWritable) pairs.getValue()).get();
							fieldMap.put(fieldNumber, fieldViolations);
						}

					}
					violationList = new ArrayList<FileViolationsWritable>();
					for (FileViolationsWritable fileViolationsWritable : listWritable) {
						violationList.add(fileViolationsWritable);
					}
					report = new DataValidationReport();
					report.setDirtyTuple(dirtyTuple);
					report.setTotalViolations(totalViolations);
					report.setFieldMap(fieldMap);
					report.setViolationList(violationList);
					List<DataValidationReport> reports = null;
					if (reportMap.containsKey(key.toString())) {
						reportMap.get(key.toString()).add(report);
					} else {
						reports = new ArrayList<>();
						reports.add(report);
						reportMap.put(key.toString(), reports);
					}
				}
				reader.close();
			}
		}
		jsonMap = getConsolidatedReport(reportMap);
		return jsonMap;
	}

	private static Map<String, DataValidationReport> getConsolidatedReport(
			Map<String, List<DataValidationReport>> reportMap) {
		Map<String, DataValidationReport> result = new HashMap<String, DataValidationReport>();
		for (Entry<String, List<DataValidationReport>> entry : reportMap.entrySet()) {
			result.put(entry.getKey(), aggregateCounters(entry.getValue()));
		}
		return result;
	}

	private static DataValidationReport aggregateCounters(List<DataValidationReport> reportList) {
		long netDirtyTuple = 0;
		long netTotalViolations = 0;
		Map<Integer, Long> netFieldMap = new HashMap<>();
		List<FileViolationsWritable> netViolationList = new ArrayList<>();
		Integer fieldKey;
		Long fieldValue;
		int index = -1;
		FileViolationsWritable temp = null;
		for (DataValidationReport dvr : reportList) {

			netDirtyTuple += dvr.getDirtyTuple();
			netTotalViolations += dvr.getTotalViolations();

			for (Entry<Integer, Long> fieldMapEntry : dvr.getFieldMap().entrySet()) {
				fieldKey = fieldMapEntry.getKey();
				fieldValue = fieldMapEntry.getValue();
				if (netFieldMap.containsKey(fieldKey)) {
					netFieldMap.put(fieldKey, (netFieldMap.get(fieldKey) + fieldValue));
				} else {
					netFieldMap.put(fieldKey, fieldValue);
				}
			}

			for (FileViolationsWritable fvw : dvr.getViolationList()) {
				index = -1;
				temp = null;
				if ((index = netViolationList.indexOf(fvw)) != -1) {
					temp = netViolationList.get(index);
					temp.setNumOfViolations(temp.getNumOfViolations() + fvw.getNumOfViolations());
					netViolationList.set(index, temp);
				} else {
					netViolationList.add(fvw);
				}
			}
		}
		DataValidationReport report = new DataValidationReport();
		report.setDirtyTuple(netDirtyTuple);
		report.setTotalViolations(netTotalViolations);
		report.setFieldMap(netFieldMap);
		report.setViolationList(netViolationList);
		return report;
	}

	/**
	 * Checks whether the input is a valid value or not.
	 *
	 * @param expectedValue
	 *            performs null check and validate the value
	 * @return true, if successful
	 */
	private static boolean validate(final String expectedValue) {

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
}