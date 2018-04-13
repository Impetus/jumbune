package org.jumbune.web.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.XmlElementBean;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.datavalidation.xml.XmlDataValidationConstants;
import org.jumbune.datavalidation.xml.helper.SchemaGenerator;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.beans.XmlDVFileReport;
import org.jumbune.web.beans.XmlDVReport;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;


/**
 * For fetching data validation reports corresponding to a violation failed in a particular file.
 * 
 *
 */

@Path(WebConstants.XML_DV_REPORT_SERVICE_URL)
public class XmlDVReportService{
	
	/** The Constant JOB_JAR_LOCATION. */
	private final String JOB_JAR_LOCATION = "jobJars/";
	
	/** The Constant XML_DV_FOLDER_LOCATION. */
	private final String XML_DV_FOLDER_LOCATION = "xdv/";
	
	/** The Constant XML_DV_FOLDER_LOCATION. */
	private final String SCHEMA_LOCATION = "template.xsd";
	
	/** The Constant NUM_OF_ROWS. */
	private final int NUM_OF_ROWS = 200;
	
	/** The Constant DEAFULT_PAGE. */
	private final int DEAFULT_PAGE = 1;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(XmlDVReportService.class);
	
	
	@POST
	@Path("/inferSchema")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response inferSchema(FormDataMultiPart form) throws IOException{
		LOGGER.debug("Start to infer Schema");
		boolean response = false;
		String schemaDef;
		try {
			FormDataBodyPart jarFile = form.getField("inputFile");
			String fileName = jarFile.getContentDisposition().getFileName();
			JobConfig jobConfig = getJobConfig(form);
			if (fileName == null) {
				return null;
			}
			File fileObject = jarFile.getValueAs(File.class);
			schemaDef = generateSchema(fileObject, fileName);
			response = saveDataAndCreateDirectories(jobConfig.getJumbuneJobName(), schemaDef);
			if (response) {
				return Response.ok(response).build();
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	
	@POST
	@Path("/updateSchema")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSchema(FormDataMultiPart form) throws IOException {
		LOGGER.debug("Start to update Schema");
		boolean schemaResponse = false;
		try {
			JobConfig jobConfig = getJobConfig(form);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(JumbuneInfo.getHome()).append(JOB_JAR_LOCATION)
					.append(jobConfig.getJumbuneJobName()).append(File.separator)
					.append(XML_DV_FOLDER_LOCATION).append(SCHEMA_LOCATION);

			String schemaDirPath = JobUtil.getAndReplaceHolders(stringBuilder.toString());
			SchemaGenerator schemaGenerator = new SchemaGenerator();
			List<XmlElementBean> xmlElementBeanList = jobConfig.getXmlElementBeanList();
			if (!xmlElementBeanList.isEmpty()) {
				Map<String, XmlElementBean> elementsMap = new HashMap<String, XmlElementBean>();
				for (XmlElementBean xmlElementBean : xmlElementBeanList) {
					elementsMap.put(xmlElementBean.getElementName(), xmlElementBean);
				}
				schemaResponse = schemaGenerator.updateSchema(schemaDirPath, elementsMap);
			} else {
				schemaResponse = true;
			}

			if (schemaResponse) {
				return Response.ok(schemaResponse).build();
			} else {
				return Response.ok(schemaResponse).build();
			}
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generateReport")
	public Response generateReport(@QueryParam("fileName") String fileName,@QueryParam("dvType") String dvType, @QueryParam("page") String pageNum,
			@QueryParam("rows") String rows, @QueryParam("jobName") String jobName) throws IOException{
		LOGGER.debug("Starting to process Xml Data Validation report");
		try{
		XmlDVReport dvReport = getDVReport(fileName,dvType, pageNum, rows, jobName);
		return Response.ok(dvReport).build();
		}catch(IOException ex){
			LOGGER.error(JumbuneRuntimeException.throwException(ex.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public boolean saveDataAndCreateDirectories(String jumbuneJobName, String schemaInput)
			throws Exception {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JumbuneInfo.getHome())
		.append(JOB_JAR_LOCATION)
		.append(jumbuneJobName).append(File.separator)
		.append(XML_DV_FOLDER_LOCATION)
		.append(SCHEMA_LOCATION);
		 
		String schemaDirPath = JobUtil.getAndReplaceHolders(stringBuilder.toString());
		String dir = schemaDirPath.substring(0, schemaDirPath.lastIndexOf('/'));
		WebUtil.makeDirectories(new File(dir));
		 
		try {
		PrintWriter out = new PrintWriter(schemaDirPath);
		out.print(schemaInput);
		out.flush();
		out.close();
		LOGGER.debug("newSchemaFileLoc: " + schemaDirPath);
		return true;
		} catch (IOException e) {
		LOGGER.error("Unable to write uploaded file ", e);
		return false;
		}

		}
	
	public String  generateSchema(File modelSchema,String fileName) throws XmlException, IOException{
			if(fileName.endsWith((XmlDataValidationConstants.XML_LITERAL))){
	        	
	        	SchemaGenerator schemaGenerator = new SchemaGenerator();
	        	SchemaDocument schemaDocument;
					schemaDocument = schemaGenerator.generateSchema(modelSchema);
				
	      	  
	            StringWriter writer = new StringWriter();
					schemaDocument.save(writer, new XmlOptions().setSavePrettyPrint());
	            String schema = writer.toString();
					writer.close();
	            return schema;
				
	            
	        
	        }else if(fileName.endsWith(XmlDataValidationConstants.XSD_LITERAL)){
	        	
	        	StringBuffer buf = new StringBuffer();
	            BufferedReader in = new BufferedReader(new FileReader(modelSchema));

	            try {
	                String line = null;
	                while ((line = in.readLine()) != null) {
	                    buf.append(line);
	                }
	            }
	            finally {
	                in.close();
	            }
	            return buf.toString();
	        	
	        }else{
	        	LOGGER.error("Please provide a definition to validate");
	        	return "";
	        }
	}

	public XmlDVReport getDVReport(String fileName, String dvType,String pageNum, String noOfRows, String jobName) throws IOException {
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
		stringBuilder.append(JumbuneInfo.getHome())
					.append(JOB_JAR_LOCATION)
					.append(jobName).append(File.separator)
					.append(XML_DV_FOLDER_LOCATION);
		String dataValidationDirPath = stringBuilder.toString();
		StringBuffer sb = new StringBuffer(JobUtil.getAndReplaceHolders(dataValidationDirPath));
		
		LOGGER.debug("Xml datavalidation folder path ----> [" + dataValidationDirPath + "]");
		
		sb.append(dvType).append(Constants.FORWARD_SLASH).append(fileName);
		List<XmlDVFileReport> fileReport = new ArrayList<XmlDVFileReport>();		
		return generateDataValidationReport(pageNo, rows, sb, fileReport);
	}
		
	
	/**
	 * Generate data validation report.
	 *
	 * @param pageNo the page no
	 * @param rows the rows
	 * @param sb the sb
	 * @param fileReport for fetching data validation reports corresponding to a violation failed in a particular file.
	 * @return
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private XmlDVReport generateDataValidationReport(int pageNo, int rows,
			StringBuffer sb, List<XmlDVFileReport> fileReport) throws IOException {
		XmlDVFileReport dvFileReport;
		int totalRecords = 0;
		int records = 0;
		int startRow = rows * (pageNo - 1);
		int endRow = startRow+rows * pageNo;
		int totalPgCount = 0;
		String line;
		String[] lineValue;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(sb.toString()));
			while (((line = br.readLine()) != null)) {
				if ((totalRecords >= startRow) && (totalRecords < endRow)) {
					dvFileReport = new XmlDVFileReport();
					lineValue = line.split("\\|");
					dvFileReport.setLineNumber(Integer.parseInt(lineValue[0]));
					dvFileReport.setMessage(StringUtils.remove(lineValue[1],"'"));
					fileReport.add(dvFileReport);
					records = records + 1;
					totalRecords++;
				}
				
			}
			XmlDVReport dvReport = new XmlDVReport();
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
	
	/**
	 * Gets the job config.
	 *
	 * @param form
	 *            the form
	 * @return the job config
	 */
	public JobConfig getJobConfig(FormDataMultiPart form) {
		String jobConfigJSON = form.getField("jsonData").getValue();
		Gson gson = new Gson();
		JobConfig jobConfig = gson.fromJson(jobConfigJSON,
				JobConfig.class);
		return jobConfig;
	}

}
