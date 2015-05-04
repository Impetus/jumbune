package org.jumbune.datavalidation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



/**
 * This class executes the Job, reads the output from hdfs and returns the data violation report in JSON format.
 * 
 * 
 * 
 * 
 */
public final class DataValidationJobExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DataValidationJobExecutor.class);
	
	
	
	/**
	 * Instantiates a new data validation job executor.
	 */
	private DataValidationJobExecutor(){
		
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws JumbuneException the hTF exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static void main(String[] args) throws JumbuneException, IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		LOGGER.info("Data validation job received args length [ " + otherArgs.length+"]"+"and values respectively ["+otherArgs[0]+"], "+
				otherArgs[1]+", ["+otherArgs[2]+"]");
		StringBuilder sb = new StringBuilder();
		for (int j = 2; j < otherArgs.length; j++) {
			sb.append(otherArgs[j]);
		}
		String outputPath = DataValidationConstants.OUTPUT_DIR_PATH + new Date().getTime();
		String inputPath = otherArgs[0];
		String slaveFileLoc = otherArgs[1];
		String dvBeanString = sb.toString();
		LOGGER.info("Received dvBean value [" + dvBeanString+"]");

		Gson gson = new Gson();
		Type type = new TypeToken<DataValidationBean>() {
		}.getType();
		DataValidationBean dataValidationBean = gson.fromJson(dvBeanString, type);
		String recordSeparator = dataValidationBean.getRecordSeparator();

		conf.set(DataValidationConstants.DATA_VALIDATION_BEAN_STRING, dvBeanString);
		conf.set(DataValidationConstants.RECORD_SEPARATOR, recordSeparator);
		conf.set(DataValidationConstants.SLAVE_FILE_LOC, slaveFileLoc);

		Job job = new Job(conf, DataValidationConstants.JOB_NAME);
		job.setJarByClass(DataValidationJobExecutor.class);
		job.setMapperClass(DataValidationMapper.class);
		job.setReducerClass(DataValidationReducer.class);
		job.setInputFormatClass(DataValidationInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DataDiscrepanciesArrayWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DataViolationWritable.class);
		DataValidationInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		LOGGER.info("Job execution Started");
		job.waitForCompletion(true);

		LOGGER.info("Job completion over,going to read the result from hdfs");
		Map<String, DataValidationReport> jsonMap = readDataFromHdfs(conf,
				outputPath);
		final Gson dvReportGson = new Gson();

		final String jsonString = dvReportGson.toJson(jsonMap);
		LOGGER.info("Completed DataValidation");
		LOGGER.info(DataValidationConstants.DV_REPORT + jsonString);
	}

	/**
	 * Read data from hdfs.
	 *
	 * @param conf is the hadoop configuration used to read the data from the HDFS.
	 * @param outputPath is the path of the HDFS data.
	 * @return json Map containing the violations that are present in the data on the HDFS.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static Map<String, DataValidationReport> readDataFromHdfs(
			Configuration conf, String outputPath) throws IOException {
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

		
		for (FileStatus status : fss) {
			path = status.getPath();

			
			if (!((path.getName().equals(DataValidationConstants.HADOOP_SUCCESS_FILES)) || (path.getName()
					.equals(DataValidationConstants.HADOOP_LOG_FILES)))) {
				LOGGER.info("Going to read the file : [" +path.getName()+"] at path ["+path+"]");
				reader = new SequenceFile.Reader(fs, path, conf);
				DataViolationArrayWritable dvaw = null;
				Map<Integer, Integer> fieldMap = null;
				key = new Text();
				value = new DataViolationWritable();
				while (reader.next(key, value)) {
					int dirtyTuple = value.getDirtyTuple();
					int cleanTuple = value.getCleanTuple();
					int totalViolations = value.getTotalViolations();
					dvaw = value.getDataViolationArrayWritable();
					fieldMap = new HashMap<Integer, Integer>();
					MapWritable mapWritable = value.getFieldMap();

					if (mapWritable != null) {
						for (Map.Entry<Writable, Writable> pairs : mapWritable.entrySet()) {
							int fieldNumber = ((IntWritable) pairs.getKey()).get();
							int fieldViolations = ((IntWritable) pairs.getValue()).get();
							fieldMap.put(fieldNumber, fieldViolations);
						}

					}
					violationList = new ArrayList<FileViolationsWritable>();
				    FileViolationsWritable bean = null;
					Writable[] arr = dvaw.get();

					if (arr != null) {
						for (int i = 0; i < arr.length; i++) {
							bean = (FileViolationsWritable) arr[i];
							violationList.add(bean);
						}
					}

					report = new DataValidationReport();
					report.setDirtyTuple(dirtyTuple);
					report.setCleanTuple(cleanTuple);
					report.setTotalViolations(totalViolations);
					report.setFieldMap(fieldMap);
					report.setViolationList(violationList);

					jsonMap.put(key.toString(), report);
				}
				reader.close();
			}
		}
		return jsonMap;
	}
}