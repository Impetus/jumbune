package org.jumbune.web.servlet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.utils.YamlUtil;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.Gson;


/**
 * For fetching data validation reports corresponding to a violation failed in a particular file.
 * 
 *
 */
@SuppressWarnings("serial")
public class DVReportServlet extends HttpServlet {
	
	/** The Constant JOB_JAR_LOCATION. */
	private final String JOB_JAR_LOCATION = "jobJars/";
	
	/** The Constant DV_FOLDER_LOCATION. */
	private final String DV_FOLDER_LOCATION = "dv/";
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DVReportServlet.class);
	
	/** The Constant NUM_OF_ROWS. */
	private final int NUM_OF_ROWS = 200;
	
	/** The Constant DEAFULT_PAGE. */
	private final int DEAFULT_PAGE = 1;

	

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("Start to process Data Validation report");
		HttpSession session = request.getSession();
		// Getting yamlloader stored in session.
		YamlLoader loader = (YamlLoader) session.getAttribute("loader");
		String fileName = request.getParameter(WebConstants.FILE_NAME);
		String dvType = request.getParameter(WebConstants.DV_TYPE);
		String pageNum = request.getParameter(WebConstants.PAGE_NUMBER);
		String noOfRows = request.getParameter(WebConstants.ROWS);
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
		stringBuilder.append(YamlLoader.getjHome()).append(JOB_JAR_LOCATION).append(loader.getJumbuneJobName()).append(DV_FOLDER_LOCATION);
		String dir = stringBuilder.toString();
		StringBuffer sb = new StringBuffer(YamlUtil.getAndReplaceHolders(dir));
		LOGGER.debug("datavalidation folder path ----> [" + dir + "]");
		sb.append(dvType).append(Constants.FORWARD_SLASH).append(fileName);
		List<DVFileReport> fileReport = new ArrayList<DVFileReport>();
		
		generateDataValidationReport(response, pageNo, rows, sb, fileReport);

	}

	/**
	 * Generate data validation report.
	 *
	 * @param response the response
	 * @param pageNo the page no
	 * @param rows the rows
	 * @param sb the sb
	 * @param fileReport for fetching data validation reports corresponding to a violation failed in a particular file.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void generateDataValidationReport(HttpServletResponse response,
			int pageNo, int rows, StringBuffer sb, List<DVFileReport> fileReport)
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
		PrintWriter out = null;

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
					records=records+1;
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
			Gson gson = new Gson();
			String dvReportJson = gson.toJson(dvReport);
			out = response.getWriter();
			response.setContentType("text/html");
			out.println(dvReportJson);

		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to read file ", e);

		} finally {
			if(out!=null){
				out.close();
			}
			if(br!=null){
				br.close();
			}
		}
	}
}
