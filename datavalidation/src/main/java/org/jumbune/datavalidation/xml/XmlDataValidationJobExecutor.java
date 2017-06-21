/**
 * 
 */
package org.jumbune.datavalidation.xml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.datavalidation.DataValidationConstants;
import org.jumbune.datavalidation.xml.helper.XMLFileInputFormat;
import org.jumbune.datavalidation.xml.helper.XsdParser;
import org.jumbune.datavalidation.ArrayListWritable;

import com.google.gson.Gson;

/**
 * @author vivek.shivhare
 *
 */
public class XmlDataValidationJobExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(XmlDataValidationJobExecutor.class);

	/**
	 * Instantiates a new data validation job executor.
	 */
	private XmlDataValidationJobExecutor() {

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException,
			URISyntaxException, XmlException{
		Configuration conf = new Configuration();

		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		LOGGER.info("XML Data validation job received args length [ " + otherArgs.length + "]"
				+ "and values respectively " + "[" + otherArgs[0] + "], " + " [" + otherArgs[1] + "], " + " ["
				+ otherArgs[2] + "], " + " [" + otherArgs[3] + "]");

		conf.set(XmlDataValidationConstants.HEADER_START_TAG, XmlDataValidationConstants.XML_HEADER_START_TAG);
		conf.set(XmlDataValidationConstants.HEADER_END_TAG, XmlDataValidationConstants.XML_HEADER_END_TAG);

		String outputPath = XmlDataValidationConstants.OUTPUT_DIR_PATH + new Date().getTime();

		String inputPath = otherArgs[0];
		String slaveFileLoc = otherArgs[1];
		String maxoilations = otherArgs[2];
		String schemaDef = otherArgs[3];

		conf.set(XmlDataValidationConstants.SLAVE_FILE_LOC, slaveFileLoc);

		XsdParser schemaParser = new XsdParser(new File(schemaDef));
		String rootElement = schemaParser.processSchema();

		conf.set(XmlDataValidationConstants.START_TAG, XmlDataValidationConstants.XML_START_ELEMENT_TAG + rootElement);
		conf.set(XmlDataValidationConstants.END_TAG, XmlDataValidationConstants.XML_END_ELEMENT_TAG + rootElement
				+ XmlDataValidationConstants.XML_ELEMENT_END_TAG);

		JumbuneDistributedCache.addCacheFile(new URI(schemaDef), conf);

		Job job = new Job(conf, XmlDataValidationConstants.XML_JOB_NAME);
		job.setJarByClass(XmlDataValidationJobExecutor.class);
		job.setMapperClass(XmlDataValidationMapper.class);
		job.setReducerClass(XmlDataValidationReducer.class);
		job.setPartitionerClass(XmlDataValidationPartitioner.class);
		job.setNumReduceTasks(5);

		job.setInputFormatClass(XMLFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(XmlDVWB.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(XMLVoilationsWB.class);

		Path[] inputPaths = FileUtil.getAllNestedXMLFilePath(job, inputPath);

		XMLFileInputFormat.setInputPaths(job, inputPaths);
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		LOGGER.debug("Job execution Started");
		job.waitForCompletion(true);

		LOGGER.debug("Job completed,going to read the result from hdfs");
		Map<String, XmlDataValidationReport> jsonMap = readDataFromHdfs(conf, outputPath);

		if (jsonMap != null) {
			final Gson dvReportGson = new Gson();

			final String jsonString = dvReportGson.toJson(jsonMap);
			LOGGER.info("Completed DataValidation");
			LOGGER.info(XmlDataValidationConstants.XML_DV_REPORT + jsonString);
		} else {
			LOGGER.info("Completed DataValidation, but no issues found");
		}

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
	private static Map<String, XmlDataValidationReport> readDataFromHdfs(Configuration conf, String outputPath)
			throws IOException {
		Map<String, XmlDataValidationReport> jsonMap = new HashMap<String, XmlDataValidationReport>();

		FileSystem fs = FileSystem.get(conf);
		Path inFile = new Path(outputPath);
		FileStatus[] fss = fs.listStatus(inFile);
		Path path = null;
		Text key = null;
		XMLVoilationsWB value = null;
		SequenceFile.Reader reader = null;

		ArrayList<XmlFileViolationsWritable> dataTypeFileViolationsList = new ArrayList<XmlFileViolationsWritable>();
		ArrayList<XmlFileViolationsWritable> regexCheckFileViolationsList = new ArrayList<XmlFileViolationsWritable>();
		ArrayList<XmlFileViolationsWritable> nullCheckFileViolationsList = new ArrayList<XmlFileViolationsWritable>();
		ArrayList<XmlFileViolationsWritable> otherXmlViolationsList = new ArrayList<XmlFileViolationsWritable>();
		ArrayList<XmlFileViolationsWritable> fatalViolationsList = new ArrayList<XmlFileViolationsWritable>();

		long totalDirtyTuple = 0;

		long totalDataTypeViolations = 0;
		long totalRegexCheckViolations = 0;
		long totalNullCheckViolations = 0;
		long totalOtherXmlViolations = 0;
		long totalFatalViolations = 0;

		for (FileStatus status : fss) {
			path = status.getPath();

			if (!status.isDirectory()) {
				if (!((path.getName().equals(XmlDataValidationConstants.HADOOP_SUCCESS_FILES))
						|| (path.getName().equals(XmlDataValidationConstants.HADOOP_LOG_FILES)))) {

					LOGGER.debug("Going to read the file : [" + path.getName() + "] at path [" + path + "]");

					reader = new SequenceFile.Reader(fs, path, conf);

					key = new Text();
					value = new XMLVoilationsWB();

					while (reader.next(key, value)) {

						switch (key.toString()) {

						case XmlDataValidationConstants.USER_DEFINED_DATA_TYPE:

							totalDataTypeViolations = totalDataTypeViolations + value.getIndividualVoilations().get();
							totalDirtyTuple = totalDirtyTuple + value.getIndividualVoilations().get();

							for (XmlFileViolationsWritable bean : value.getViolationList()) {
								dataTypeFileViolationsList.add(bean);
							}

							break;

						case XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK:

							totalRegexCheckViolations = totalRegexCheckViolations
									+ value.getIndividualVoilations().get();
							totalDirtyTuple = totalDirtyTuple + value.getIndividualVoilations().get();

							for (XmlFileViolationsWritable bean : value.getViolationList()) {
								regexCheckFileViolationsList.add(bean);
							}

							break;

						case XmlDataValidationConstants.USER_DEFINED_NULL_CHECK:

							totalNullCheckViolations = totalNullCheckViolations + value.getIndividualVoilations().get();
							totalDirtyTuple = totalDirtyTuple + value.getIndividualVoilations().get();

							for (XmlFileViolationsWritable bean : value.getViolationList()) {
								nullCheckFileViolationsList.add(bean);
							}

							break;

						case XmlDataValidationConstants.FATAL_ERROR:

							totalFatalViolations = totalFatalViolations + value.getIndividualVoilations().get();
							totalDirtyTuple = totalDirtyTuple + value.getIndividualVoilations().get();

							for (XmlFileViolationsWritable bean : value.getViolationList()) {
								fatalViolationsList.add(bean);
							}

							break;

						case XmlDataValidationConstants.OTHER_XML_ERROR:

							totalOtherXmlViolations = totalOtherXmlViolations + value.getIndividualVoilations().get();
							totalDirtyTuple = totalDirtyTuple + value.getIndividualVoilations().get();

							for (XmlFileViolationsWritable bean : value.getViolationList()) {
								otherXmlViolationsList.add(bean);
							}

							break;

						default:
							break;

						}

					}
					reader.close();
				}
			}
		}
		if (!dataTypeFileViolationsList.isEmpty()) {
			XmlDataValidationReport report = new XmlDataValidationReport();
			report.setTotalViolations(totalDirtyTuple);
			report.setIndividualViolations(totalDataTypeViolations);
			report.setViolationList(dataTypeFileViolationsList);
			jsonMap.put(XmlDataValidationConstants.USER_DEFINED_DATA_TYPE, report);
		}
		if (!regexCheckFileViolationsList.isEmpty()) {
			XmlDataValidationReport report = new XmlDataValidationReport();
			report.setTotalViolations(totalDirtyTuple);
			report.setIndividualViolations(totalRegexCheckViolations);
			report.setViolationList(regexCheckFileViolationsList);
			jsonMap.put(XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK, report);
		}
		if (!nullCheckFileViolationsList.isEmpty()) {
			XmlDataValidationReport report = new XmlDataValidationReport();
			report.setTotalViolations(totalDirtyTuple);
			report.setIndividualViolations(totalNullCheckViolations);
			report.setViolationList(nullCheckFileViolationsList);
			jsonMap.put(XmlDataValidationConstants.USER_DEFINED_NULL_CHECK, report);
		}
		if (!fatalViolationsList.isEmpty()) {
			XmlDataValidationReport report = new XmlDataValidationReport();
			report.setTotalViolations(totalDirtyTuple);
			report.setIndividualViolations(totalFatalViolations);
			report.setViolationList(fatalViolationsList);
			jsonMap.put(XmlDataValidationConstants.FATAL_ERROR, report);
		}
		if (!otherXmlViolationsList.isEmpty()) {
			XmlDataValidationReport report = new XmlDataValidationReport();
			report.setTotalViolations(totalDirtyTuple);
			report.setIndividualViolations(totalOtherXmlViolations);
			report.setViolationList(otherXmlViolationsList);
			jsonMap.put(XmlDataValidationConstants.OTHER_XML_ERROR, report);
		}

		return jsonMap;
	}

}
