package org.jumbune.web.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.job.Config;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.execution.utils.ReportGenerator;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;



/**
 * This class is used to convert the reports data into downloaded excel format.
 */
@Path(WebConstants.EXPORT_EXCEL_SERVICE_URL)
public class ExportExcelService {
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ExportExcelService.class);

	/**
	 * Instantiates a new export excel servlet.
	 */
	public ExportExcelService() {
		super();
	}

	@Context
	HttpServletRequest request;
	
	@Context
	HttpServletResponse response;
	
	
	

	@POST
	public Response processPost(){
		StringBuilder builder = new StringBuilder();
		try {
			doPost();
			builder.append("SUCCESS");
		} catch (ServletException | IOException e) {
			builder.append("FAILURE due to: "+e);
		}
		GenericEntity<String> entity = new GenericEntity<String>(
				builder.toString()) {
		};
		return Response.ok(entity).build();
	}

	@GET
	public Response processGet(){
		return processPost();
	}

		
	public void doPost() throws ServletException, IOException {
		HttpSession session = request.getSession();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		HttpReportsBean reports;
		String isScheduledJob = request.getParameter("isScheduledJob");
		Map<String, String> map = null;
		Map<String, String> reportsJson = null;
		Config config = null;
		String jumbuneJobName = null;
		try {

			if (!(session.getAttribute(WebConstants.REPORTS_BEAN) == null && isScheduledJob.equals("FALSE"))) {
				if (isScheduledJob.equals("TRUE")) {
					String yamlFileLoc = request.getParameter("yamlLocation");
					InputStream inputStream = ConfigurationUtil.readFile(yamlFileLoc);
					EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig)JobConfigUtil.jumbuneRequest(inputStream).getJobConfig();
					String scheduledJobName = enterpriseJobConfig.getJumbuneJobName();
					String scheduleJobLoc = ExtendedConfigurationUtil.getUserScheduleJobLocation() + File.separator + scheduledJobName;
					String reportJson = WebUtil.readScheduleJobReports(scheduleJobLoc);
					Type type = new TypeToken<Map<String, String>>() {
					}.getType();
					Gson gson = new Gson();
					map = gson.fromJson(reportJson, type);
					reportsJson = gson.fromJson(reportJson, type);

				} else {
					reports = (HttpReportsBean) session.getAttribute(WebConstants.REPORTS_BEAN);
					config = (Config) session.getAttribute("config");
					map = reports.getAllReports();
					reportsJson = (Map<String, String>) ((HashMap<String, String>) reports.getAllReports()).clone();

				}
				callTowriteToExcelFile(request, out, map, reportsJson, config,
						jumbuneJobName);

			} else {
				out.println(" Export Utility failure : Server Side Exception : Could not fetch reports from the Server");
				LOG.error("Json Bean was not created during ExportExcel Servlet....");
			}

		} catch (FileNotFoundException e) {
			out.println(e.getMessage() + " Export Utility failure");
			LOG.error(e.getMessage());
		} catch (IOException e) {
			out.println(e.getMessage() + " Export Utility failure");
			LOG.error(e.getMessage());
		} catch (Exception e) {
			out.println(e.getMessage() + " Export Utility failure");
			LOG.error(e.getMessage());
		} finally {
			out.flush();
			out.close();
			
		}
	}

	/**
	 * Call to write to excel file.
	 *
	 * @param request the request
	 * @param out the out
	 * @param map the map
	 * @param reportsJson the reports json
	 * @param config the loader
	 * @param jumbuneJobName the jumbune job name
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the Jumbune exception
	 */
	private void callTowriteToExcelFile(HttpServletRequest request,
			PrintWriter out, Map<String, String> map,
			Map<String, String> reportsJson, Config config,
			final String jumbuneJobName) throws FileNotFoundException, IOException,
			JumbuneException {
		String jobName = jumbuneJobName;
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig)config;
		if (enterpriseJobConfig != null){
			jobName = enterpriseJobConfig.getJumbuneJobName().substring(0, (enterpriseJobConfig.getJumbuneJobName().length()) - 1);}
		String contextRealPath = request.getRealPath("");
		String xlsFolder = WebConstants.REPORT_DIR;
		String fileName = "/" + (jobName == null ? "Report" : jobName) + System.currentTimeMillis() + WebConstants.XLS_EXT;

		File xlsDirectory = new File(contextRealPath + xlsFolder);
		checkAndMakeExcelDirectory(xlsDirectory);
		addDataValidationReportToMap(map, config);

		ReportGenerator.writesToExcelFile(map, contextRealPath + xlsFolder + fileName, reportsJson);
		LOG.debug("Exported to Excel Successfully....");

		out.println(getURLWithContextPath(request) + xlsFolder + fileName);
	}

	/**
	 * Check and make excel directory.
	 *
	 * @param xlsDirectory the xls directory
	 */
	private void checkAndMakeExcelDirectory(File xlsDirectory) {
		if (!xlsDirectory.exists()) {
			xlsDirectory.mkdir();
		}
	}

	/**
	 * Adds the data validation report to map.
	 *
	 * @param map the map
	 * @param config loads the config
	 */
	private void addDataValidationReportToMap(Map<String, String> map,
			Config config) {
		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig)config;
		if (map.containsKey(Constants.DATA_VALIDATION) && enterpriseJobConfig != null) {
			map.put(Constants.DATA_VALIDATION, enterpriseJobConfig.getJumbuneJobLoc());
		}
	}

	

	/**
	 * Gets the uRL with context path.
	 *
	 * @param request the request
	 * @return the uRL with context path
	 */
	public String getURLWithContextPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	

	
}
