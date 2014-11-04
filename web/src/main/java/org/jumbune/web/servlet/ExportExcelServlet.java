package org.jumbune.web.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.utils.ExportUtil;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.web.utils.WebConstants;



/**
 * This class is used to convert the reports data into downloaded excel format.
 */
public class ExportExcelServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ExportExcelServlet.class);

	/**
	 * Instantiates a new export excel servlet.
	 */
	public ExportExcelServlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		HttpReportsBean reports;
		Map<String, String> map = null;
		Map<String, String> reportsJson = null;
		Loader loader = null;
		String jumbuneJobName = null;
		try {

			if (!(session.getAttribute(WebConstants.REPORTS_BEAN) == null )) {
								 
					reports = (HttpReportsBean) session.getAttribute(WebConstants.REPORTS_BEAN);
					loader = (Loader) session.getAttribute("loader");
					map = reports.getAllReports();
					reportsJson = (Map<String, String>) ((HashMap<String, String>) reports.getAllReports()).clone();
				callTowriteToExcelFile(request, out, map, reportsJson, loader,
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
	 * @param loader the loader
	 * @param jumbuneJobName the jumbune job name
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the Jumbune exception
	 */
	private void callTowriteToExcelFile(HttpServletRequest request,
			PrintWriter out, Map<String, String> map,
			Map<String, String> reportsJson, Loader loader,
			final String jumbuneJobName) throws FileNotFoundException, IOException,
			JumbuneException {
		String jobName = jumbuneJobName;
		YamlLoader yamlLoader = (YamlLoader)loader;
		if (loader != null){
			jobName = yamlLoader.getJumbuneJobName().substring(0, (yamlLoader.getJumbuneJobName().length()) - 1);}
		String contextRealPath = request.getRealPath("");
		String xlsFolder = WebConstants.REPORT_DIR;
		String fileName = "/" + (jobName == null ? "Report" : jobName) + System.currentTimeMillis() + WebConstants.XLS_EXT;

		File xlsDirectory = new File(contextRealPath + xlsFolder);
		checkAndMakeExcelDirectory(xlsDirectory);
		addDataValidationReportToMap(map, loader);

		ExportUtil.writesToExcelFile(map, contextRealPath + xlsFolder + fileName, reportsJson);
		LOG.info("Exported to Excel Successfully....");

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
	 * @param loader loads the yaml
	 */
	private void addDataValidationReportToMap(Map<String, String> map,
			Loader loader) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		if (map.containsKey(Constants.DATA_VALIDATION) && loader != null) {
			map.put(Constants.DATA_VALIDATION, yamlLoader.getJumbuneJobLoc());
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
