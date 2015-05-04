package org.jumbune.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.job.JobConfig;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * This servlet is called when a JsonFile is uploaded from UI It saves the
 * YamlFile at a predefined location for further use.
 *

 */
public class UploadServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1l;
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(UploadServlet.class);

	/**
	 * Instantiates a new upload servlet.
	 */
	public UploadServlet() {
		super();
	}

	/**
	 * doGet method for UploadServlet
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * doPost method for UploadServlet
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Process request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			throw new ServletException(e);
		}

		try {

			response.setContentType("text/html");

			String filePath = JobConfig.getJumbuneHome() + Constants.USER_JSON_LOC;

			LOG.debug("File path for uploding json file :: " + filePath);
			WebUtil webUtil = new WebUtil();
			List<File> uploadedFiles = webUtil.uploadFiles(request, filePath);
			// Only one job file could be uploaded at a time
			JobConfig jobConfig = (JobConfig) webUtil.getJobConfFromFile(uploadedFiles.get(0));
			Gson gson = new Gson();
			JsonObject jsonObject = gson.toJsonTree(jobConfig).getAsJsonObject();
			if(jobConfig.getClasspath()!=null && jobConfig.getClasspath().getUserSupplied()!=null){
			
			String[] resources = null;
			}
			String jsonString = gson.toJson(jsonObject);
			out.println(jsonString);
			LOG.debug("Created json object successfully and written it to writer "
					+ jsonString + "\n");

		} catch (IOException ie) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, ie);
			out.println("{\"ErrorAndException\" : \"Could not read from the Yaml File\"}");
			throw new ServletException(ie);
		} catch (JumbuneException e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			out.println("{\"ErrorAndException\" : \"Yaml File data is incorrect\"}");
			throw new ServletException(e);
		} catch (Exception e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			out.println("{\"ErrorAndException\" : \""
					+ ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON + "\"}");
			throw new ServletException(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
