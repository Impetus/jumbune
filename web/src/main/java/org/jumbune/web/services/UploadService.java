package org.jumbune.web.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.job.Config;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jumbune.common.job.JobConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;


/**
 * This servlet is called when a JsonFile is uploaded from UI It saves the
 * JsonFile at a predefined location for further use.
 *
 */
@Path(WebConstants.UPLOAD_SERVICE_URL)
public class UploadService {
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(UploadService.class);


	
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
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			LOG.error(ErrorCodesAndMessages.UNABLE_TO_LOAD_JSON, e);
			throw new ServletException(e);
		}

		try {

			response.setContentType("text/html");

			String filePath = JumbuneInfo.getHome() + Constants.USER_YAML_LOC;

			LOG.debug("File path for uploding json file :: " + filePath);
			WebUtil util = new WebUtil();
			List<File> uploadedFiles = util.uploadFiles(request, filePath);
			// Only one yaml file could be uploaded at a time
			Config config = util.getJobConfFromFile(uploadedFiles.get(0));
			JobConfig jobConfig = (JobConfig)config;
			ClasspathElement classpathElement = jobConfig.getClasspath()
					.getUserSupplied();
			Gson gson = new Gson();
			JsonObject jsonObject = gson.toJsonTree(jobConfig).getAsJsonObject();
			String jsonString = gson.toJson(jsonObject);

			out.println(jsonString);

			LOG.debug("Created yaml object successfully and written it to writer "
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
