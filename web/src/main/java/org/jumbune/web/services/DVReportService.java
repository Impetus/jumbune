package org.jumbune.web.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.utils.Constants;
import org.jumbune.datavalidation.dsc.DataSourceCompConstants;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.beans.DVFileReport;
import org.jumbune.web.beans.DVReport;
import org.jumbune.web.beans.DataSourceCompReport;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * For fetching data validation reports corresponding to a violation failed in a
 * particular file.
 * 
 *
 */

@Path(WebConstants.DV_REPORT_SERVICE_URL)
public class DVReportService{

	/** The Constant JOB_JAR_LOCATION. */
	private final String JOB_JAR_LOCATION = "jobJars/";

	/** The Constant DV_FOLDER_LOCATION. */
	private final String DV_FOLDER_LOCATION = "dv/";

	/** The json dv folder location. */
	private final String JSON_DV_FOLDER_LOCATION = "jdv/";

	/** The Constant NUM_OF_ROWS. */
	private final int NUM_OF_ROWS = 200;

	/** The Constant DEAFULT_PAGE. */
	private final int DEAFULT_PAGE = 1;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DVReportService.class);

	/** The Constant ARRAY_SUFFIX. */
	private static final String ARRAY_SUFFIX = "Array";

	/**
	 * Process get.
	 *
	 * @param fileName
	 *            the file name
	 * @param dvType
	 *            the dv type
	 * @param pageNum
	 *            the page num
	 * @param rows
	 *            the rows
	 * @param jobName
	 *            the job name
	 * @return the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response processGet(@QueryParam("fileName") String fileName, @QueryParam("dvType") String dvType,
			@QueryParam("page") String pageNum, @QueryParam("rows") String rows, @QueryParam("jobName") String jobName)
			throws IOException {
		LOGGER.debug("Starting to process Data Validation report");
		DVReport dvReport = getDVReport(fileName, dvType, pageNum, rows, jobName);
		return Response.ok(dvReport).build();

	}

	/**
	 * Process json get.
	 *
	 * @param fileName
	 *            the file name
	 * @param dvType
	 *            the dv type
	 * @param pageNum
	 *            the page num
	 * @param rows
	 *            the rows
	 * @param jobName
	 *            the job name
	 * @return the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@POST
	@Path(WebConstants.JSON_TABLE)
	@Produces(MediaType.APPLICATION_JSON)
	public Response processJsonGet(@QueryParam("fileName") String fileName, @QueryParam("dvType") String dvType,
			@QueryParam("page") String pageNum, @QueryParam("rows") String rows, @QueryParam("jobName") String jobName)
			throws IOException {
		LOGGER.debug("Starting to process Json Data Validation report");
		DVReport dvReport = getJSONDVReport(fileName, dvType, pageNum, rows, jobName);
		return Response.ok(dvReport).build();

	}

	@POST
	@Path("/data-source-table")
	@Produces(MediaType.APPLICATION_JSON)
	public Response processDataSourceComparison(@QueryParam("transformationNumber") String transformationNumber,
			@QueryParam("fileName") String fileName, @QueryParam("page") String sPageNo,
			@QueryParam("rows") String sRows, @QueryParam("jobName") String jobName) {
		BufferedReader br = null;
		try {
			DataSourceCompReport dvReport = new DataSourceCompReport();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(JumbuneInfo.getHome()).append(JOB_JAR_LOCATION).append(jobName)
					.append(File.separator).append(Constants.SLAVE_DV_LOC).append(File.separator)
					.append(DataSourceCompConstants.TRANSFORMATION_VIOLATION).append(File.separator)
					.append(transformationNumber).append(File.separator).append(fileName);

			File dir = new File(stringBuilder.toString());
			String regex = "\\|";
			String dot = ".";
			int totalRecords = 0;
			int records = 0;
			int pageNo = Integer.parseInt(sPageNo);
			int rows = Integer.parseInt(sRows);
			int startRow = rows * (pageNo - 1);
			int endRow = rows * pageNo;
			int totalPgCount = 0;
			String line = null;
			String[] lineValue = null;

			for (File file : dir.listFiles()) {
				
				if (file.getName().startsWith(dot)) {
					continue;
				}
				try {
					br = new BufferedReader(new FileReader(file));
					while (((line = br.readLine()) != null)) {
						if (line.isEmpty()) {
							continue;
						}
						
						if ((totalRecords >= startRow) && (totalRecords < endRow)) {
							lineValue = line.split(regex);
							dvReport.addRow(lineValue[0], lineValue[1], lineValue[2], lineValue[3]);
							records = records + 1;
						}
						totalRecords++;
					}
					br.close();
				} finally {
					if (br != null) {
						br.close();
					}
				}
			}

			dvReport.setPage(pageNo);
			if (totalRecords % rows == 0) {
				totalPgCount = (totalRecords / rows);
			} else {
				totalPgCount = (totalRecords / rows) + 1;
			}
			dvReport.setTotal(totalPgCount);
			dvReport.setRecords(totalRecords);
			return Response.ok(Constants.gson.toJson(dvReport)).build();
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
	}

	/**
	 * Gets the json schema.
	 *
	 * @param stream
	 *            the stream
	 * @return the json schema
	 */
	@POST
	@Path(WebConstants.JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJsonSchema(@FormDataParam("inputFile") InputStream stream) {
		

		// data contains jobName, jobSubmissionUser and selectedCluster
		// Type type = new TypeToken<LinkedHashMap<String, String>>() {
		// }.getType();
		// Map<String, String> data =
		// gson.fromJson(form.getField("jsonData").getValue(), type);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String jsonSchema;
		JsonElement jsonElement = null;

		try {
			jsonSchema = IOUtils.toString(stream, "UTF-8");
			jsonElement = new JsonParser().parse(jsonSchema);
			return Response.ok(Constants.gson.toJson(populateDataTypeKey(jsonElement, "", map))).build();
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Gets the DV report.
	 *
	 * @param fileName
	 *            the file name
	 * @param dvType
	 *            the dv type
	 * @param pageNum
	 *            the page num
	 * @param noOfRows
	 *            the no of rows
	 * @param jobName
	 *            the job name
	 * @return the DV report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public DVReport getDVReport(String fileName, String dvType, String pageNum, String noOfRows, String jobName)
			throws IOException {
		int pageNo;
		int rows;
		if (pageNum == null) {
			pageNo = DEAFULT_PAGE;
		} else {
			pageNo = Integer.parseInt(pageNum);
		}
		if (noOfRows == null) {
			rows = NUM_OF_ROWS;
		} else {
			rows = Integer.parseInt(noOfRows);
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JumbuneInfo.getHome()).append(JOB_JAR_LOCATION).append(jobName)
				.append(File.separator).append(DV_FOLDER_LOCATION);
		String dataValidationDirPath = stringBuilder.toString();
		StringBuffer sb = new StringBuffer(JobUtil.getAndReplaceHolders(dataValidationDirPath));

		LOGGER.debug("datavalidation folder path ----> [" + dataValidationDirPath + "]");

		sb.append(dvType).append(Constants.FORWARD_SLASH).append(fileName);
		List<DVFileReport> fileReport = new ArrayList<DVFileReport>();
		return generateDataValidationReport(pageNo, rows, sb, fileReport);
	}

	/**
	 * Gets the JSONDV report.
	 *
	 * @param fileName
	 *            the file name
	 * @param dvType
	 *            the dv type
	 * @param pageNum
	 *            the page num
	 * @param noOfRows
	 *            the no of rows
	 * @param jobName
	 *            the job name
	 * @return the JSONDV report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public DVReport getJSONDVReport(String fileName, String dvType, String pageNum, String noOfRows, String jobName)
			throws IOException {
		int pageNo;
		int rows;
		if (pageNum == null) {
			pageNo = DEAFULT_PAGE;
		} else {
			pageNo = Integer.parseInt(pageNum);
		}
		if (noOfRows == null) {
			rows = NUM_OF_ROWS;
		} else {
			rows = Integer.parseInt(noOfRows);
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JumbuneInfo.getHome()).append(JOB_JAR_LOCATION).append(jobName)
				.append(File.separator).append(JSON_DV_FOLDER_LOCATION);
		String dataValidationDirPath = stringBuilder.toString();
		StringBuffer sb = new StringBuffer(JobUtil.getAndReplaceHolders(dataValidationDirPath));

		LOGGER.debug("datavalidation folder path ----> [" + dataValidationDirPath + "]");

		sb.append(dvType).append(Constants.FORWARD_SLASH).append(fileName);
		List<DVFileReport> fileReport = new ArrayList<DVFileReport>();
		return generateDataValidationReport(pageNo, rows, sb, fileReport);
	}

	/**
	 * Populate data type key.
	 *
	 * @param jsonElement
	 *            the json element
	 * @param prefix
	 *            the prefix
	 * @param schema
	 *            the schema
	 * @return the linked hash map
	 */
	private LinkedHashMap<String, String> populateDataTypeKey(JsonElement jsonElement, String prefix,
			LinkedHashMap<String, String> schema) {
		if (jsonElement.isJsonObject()) {
			for (Map.Entry<String, JsonElement> element : jsonElement.getAsJsonObject().entrySet()) {

				if (element.getValue().isJsonObject()) {
					prefix = prefix + element.getKey() + ".";
					populateDataTypeKey(element.getValue(), prefix, schema);
				} else {
					if (element.getValue().isJsonPrimitive()) {

						if (!prefix.isEmpty()) {
							if (element.getValue().getAsJsonPrimitive().isBoolean()) {
								schema.put(prefix + element.getKey(), "BOOLEAN");
							} else if (element.getValue().getAsJsonPrimitive().isString()) {
								if (element.getValue().getAsJsonPrimitive().toString().isEmpty()) {
									schema.put(prefix + element.getKey(), "NULL");
								} else {
									schema.put(prefix + element.getKey(), "STRING");
								}
							} else {
								schema.put(prefix + element.getKey(), "NUMBER");
							}
						} else {

							if (element.getValue().getAsJsonPrimitive().isBoolean()) {
								schema.put(element.getKey(), "BOOLEAN");
							} else if (element.getValue().getAsJsonPrimitive().isString()) {
								if (element.getValue().getAsJsonPrimitive().toString().isEmpty()) {
									schema.put(element.getKey(), "NULL");

								} else {
									schema.put(element.getKey(), "STRING");
								}
							} else {
								schema.put(element.getKey(), "NUMBER");

							}
						}

					} else if (element.getValue().isJsonArray()) {
						for (JsonElement elements : element.getValue().getAsJsonArray()) {
							if (elements.isJsonObject()) {
								populateDataTypeKey(elements, element.getKey() + ".", schema);
							} else {
								if (elements.getAsJsonPrimitive().isNumber()) {
									schema.put(prefix + element.getKey() + ARRAY_SUFFIX, "NUMBER");
								} else if (elements.getAsJsonPrimitive().isString()) {
									schema.put(prefix + element.getKey() + ARRAY_SUFFIX, "STRING");
								} else if (elements.getAsJsonPrimitive().isBoolean()) {
									schema.put(prefix + element.getKey() + ARRAY_SUFFIX, "BOOLEAN");
								}
							}
						}

					} else if (element.getValue().isJsonNull()) {
						if (prefix.isEmpty()) {
							schema.put(element.getKey(), "NULL");
						} else {
							schema.put(prefix + element.getKey(), "NULL");
						}
					} else {
						schema.put(prefix + element.getKey(), "MAP");
					}
				}
			}
		} else if (jsonElement.isJsonArray()) {
			for (JsonElement elements : jsonElement.getAsJsonArray()) {
				populateDataTypeKey(elements, "", schema);
			}
		}
		return schema;
	}

	/**
	 * Generate data validation report.
	 *
	 * @param pageNo
	 *            the page no
	 * @param rows
	 *            the rows
	 * @param sb
	 *            the sb
	 * @param fileReport
	 *            for fetching data validation reports corresponding to a
	 *            violation failed in a particular file.
	 * @return the DV report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private DVReport generateDataValidationReport(int pageNo, int rows, StringBuffer sb, List<DVFileReport> fileReport)
			throws IOException {
		DVFileReport dvFileReport;
		int totalRecords = 0;
		int records = 0;
		int startRow = rows * (pageNo - 1);
		int endRow = rows * pageNo;
		int totalPgCount = 0;
		String line;
		String[] lineValue;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(sb.toString()));
			while (((line = br.readLine()) != null)) {
				if ((totalRecords >= startRow) && (totalRecords < endRow)) {
					dvFileReport = new DVFileReport();
					lineValue = line.split("\\|");
					dvFileReport.setLineNumber(Integer.parseInt(lineValue[0]));
					dvFileReport.setFieldNumber(lineValue[1]);
					dvFileReport.setExpectedValue(lineValue[2]);
					dvFileReport.setActualValue(lineValue[Constants.THREE]);
					fileReport.add(dvFileReport);
					records = records + 1;
				}
				totalRecords++;
			}
			DVReport dvReport = new DVReport();
			dvReport.setPage(pageNo);
			if (totalRecords % rows == 0) {
				totalPgCount = (totalRecords / rows);
			} else {
				totalPgCount = (totalRecords / rows) + 1;
			}
			dvReport.setTotal(totalPgCount);
			dvReport.setRecords(totalRecords);
			dvReport.setRows(fileReport);
			return dvReport;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}
