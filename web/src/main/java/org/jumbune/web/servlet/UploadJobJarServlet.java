package org.jumbune.web.servlet;

import java.io.File;
import java.io.IOException;
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




/**
 * This servlet is called when a MR Job jar is uploaded from UI It saves the
 * jar at a predefined location for further use.
 */
public class UploadJobJarServlet extends HttpServlet{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1l;
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(UploadJobJarServlet.class);

	/**
	 * Instantiates a new upload job jar servlet.
	 */
	public UploadJobJarServlet() {
		super();
	}

	/**
	 * doGet method for UploadJobJarServlet
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
	 * doPost method for UploadJobJarServlet
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
		
		try{
		String jobName = (String)request.getParameter("jobName");
		String resourceFolder = System.getenv("JUMBUNE_HOME") + File.separator+Constants.JOB_JARS_LOC+jobName+ Constants.MR_RESOURCES;
		String filePath = JobConfig.getJumbuneHome() +File.separator+Constants.JOB_JARS_LOC+ jobName+ Constants.MR_RESOURCES;
		File resourceDir = new File(resourceFolder);
		if (!resourceDir.exists()) {
			resourceDir.mkdirs();
		}
		LOG.info("File path for uploding multiple job jar files :: " + filePath);
		WebUtil util = new WebUtil();
		List<File> uploadedFiles = util.uploadFiles(request, filePath);	
		LOG.debug("uploadedFiles :: " + uploadedFiles);
		
		} catch (JumbuneException e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			throw new ServletException(e);
		} catch (Exception e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			throw new ServletException(e);
		} 
		
	}
}
