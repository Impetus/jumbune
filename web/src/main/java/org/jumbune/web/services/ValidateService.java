package org.jumbune.web.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.Constants;

import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.web.utils.WebConstants;

@Path(WebConstants.VALIDATE_SERVICE_URL)
public class ValidateService {
	
	private static final String JSON_DATA = "jsonData";
	private static final String OPERATING_CLUSTER = "operatingCluster";
	private static final Logger LOGGER = LogManager.getLogger(ValidateService.class);
	
	@POST
	@Path(WebConstants.VALIDATE_JOB_INPUT)
	public Response validateJson(FormDataMultiPart form) {
		Map<String, String> jobInputErrors = null;
		try {

			// Creating JumbuneRequest Object
			String jobConfigJSON = form.getField(JSON_DATA).getValue();
			JobConfig jobConfig = Constants.gson.fromJson(jobConfigJSON,
					JobConfig.class);
			JumbuneRequest jumbuneRequest = new JumbuneRequest();
			jumbuneRequest.setConfig(jobConfig);
			jumbuneRequest.setCluster(
					JobRequestUtil.getClusterByName(jobConfig.getOperatingCluster()));

			// Validating
			ValidateInput validateInput = new ValidateInput();
			jobInputErrors = validateInput.validateJobInputDetails(jumbuneRequest);
		} catch (IOException e) {
			jobInputErrors = new HashMap<String, String>(1);
			LOGGER.error("Error while getting cluster details.", e);
			jobInputErrors.put(OPERATING_CLUSTER, "Unable to read cluster details.");
		}
		

		if (jobInputErrors.isEmpty()) {
			jobInputErrors.put(Constants.STATUS, Constants.SUCCESS);
		} else {
			jobInputErrors.put(Constants.STATUS, Constants.ERROR_);
		}
		return Response.ok(Constants.gson.toJson(jobInputErrors)).build();
	}
	
	@GET
	@Path(WebConstants.JOB_NAME + "/{jobName}")
	public Response isJobAlreadyExists(@PathParam("jobName") String jobName) {
		Map<String, String> jobInputErrors = null;
		ValidateInput validateInput = new ValidateInput();
		jobInputErrors = validateInput.checkJobNameAlreadyExists(jobName);
		
		return Response.ok(Constants.gson.toJson(jobInputErrors)).build();
	}

}
