package org.jumbune.web.services;

import java.io.File;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;


/**
 * This servlet is called when a MR Job jar is uploaded from UI It saves the
 * jar at a predefined location for further use.
 */
@Path(WebConstants.UPLOAD_JOB_JAR_SERVICE_URL)
public class UploadJobJarService {
		
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(UploadJobJarService.class);
	
	@Context
	private HttpServletRequest request;
	
	@Context
	private HttpServletResponse response;

	@POST
	public Response processPost(){
		StringBuilder builder = new StringBuilder();
		try {
			processRequest();
			builder.append("SUCCESS");
		} catch (ServletException e) {
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


	/**
	 * Process request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 */
	private void processRequest() throws ServletException {
		
		try{
		String jobName = (String)request.getParameter("jobName");
		String resourceFolder = System.getenv("JUMBUNE_HOME") +File.separator+Constants.JOB_JARS_LOC+jobName+ Constants.MR_RESOURCES;
		String filePath = EnterpriseJobConfig.getJumbuneHome() +File.separator+Constants.JOB_JARS_LOC+ jobName+ Constants.MR_RESOURCES;
		File resourceDir = new File(resourceFolder);
		if (!resourceDir.exists()) {
			resourceDir.mkdirs();
		}
		LOG.debug("File path for uploding multiple job jar files :: " + filePath);
		WebUtil util = new WebUtil();
		List<File> uploadedFiles = util.uploadFiles(request, filePath);	
		LOG.debug("uploadedFiles :: " + uploadedFiles);
		
		} catch (JumbuneException e) {
			throw new ServletException(e);
		} catch (Exception e) {
			throw new ServletException(e);
		} 
		
	}
}
