package org.jumbune.web.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.RequestDispatcher;
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
import org.jumbune.common.job.Config;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.utils.exception.ExtendedErrorCodesAndMessages;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;


/**
 * service implementation class GatherScheduledJobResultService. This service will get
 * all relevant information/reports about the scheduled job. It will also
 * redirect to Result page from which would show all the reports
 */

@Path(WebConstants.GATHER_SCHEDULED_JOB_RESULT_SERVICE_URL)
public class GatherScheduledJobResultService {

	private static final Logger LOG = LogManager
			.getLogger(GatherScheduledJobResultService.class);
	
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
	
	
	
	private void doPost() throws ServletException, IOException {
		LOG.debug("Received request to get scheduling results!!!");
		try {
			processRequest(request, response);
		} catch (JumbuneException e) {
			throw new ServletException(e);
		}
	}
	
    /**
     * This method is used to load json according to the file path.
     *
     * @param filePath the file path
     * @return the object
     * @throws FileNotFoundException the file not found exception
     */
    private static Object loadJob(String filePath) throws FileNotFoundException {
            InputStreamReader inputStreamReader = null;
            try {
                    Gson gson = new Gson();
                    inputStreamReader  = new InputStreamReader(new FileInputStream(new File(filePath)));
                    Config config = (Config)gson.fromJson(inputStreamReader, EnterpriseJobConfig.class);
                    return config;
            } finally {
                    if(inputStreamReader != null){
                            try{
                         	   inputStreamReader.close();
                            }catch(IOException ioe){
                                    LOG.error("Failed to close input stream of job config file");
                            }
                    }
            }
    }
    

	/**
	 * Process request.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws JumbuneException the Jumbune exception
	 */
	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws JumbuneException {
		String scheduledJobName = request.getParameter("scheduledJobName");
		LOG.debug("Scheduled job name " + scheduledJobName);
		// Read jobConfig file and set the number of tabs in jobResult
		String scheduleJobLoc = ExtendedConfigurationUtil.getUserScheduleJobLocation() + File.separator
				+ scheduledJobName;
		String jsonFileLoc = ExtendedConfigurationUtil.getScheduleJobJsonFileLoc(scheduleJobLoc);
		try {
			EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) loadJob(jsonFileLoc);
			

			WebUtil util = new WebUtil();
			String tabs = util.getTabsInformation(enterpriseJobConfig);

			request.setAttribute("tabs", tabs);
			request.setAttribute("scheduledJobName", scheduledJobName);
			request.setAttribute("yamlLocation", jsonFileLoc);
			request.setAttribute("stats_interval", Constants.TEN_THOUSAND);
			request.setAttribute("JobName", scheduledJobName);

			final RequestDispatcher requestDispatcher = request.getServletContext()
					.getRequestDispatcher(WebConstants.RESULT_URL);
			requestDispatcher.forward(request, response);
		} catch (FileNotFoundException e) {
			LOG.error("Scheduled job yaml file could not be found ", e);
			throw new JumbuneException(
					ExtendedErrorCodesAndMessages.UNABLE_TO_FIND_SCH_JSON);
		} catch (ServletException e) {
			LOG.error("Could not re-direct to result page!!", e);
			throw new JumbuneException(
					ExtendedErrorCodesAndMessages.UNABLE_TO_REDIRECT_TO_RESULT);
		} catch (IOException e) {
			LOG.error("Could not re-direct to result page!!", e);
			throw new JumbuneException(
					ExtendedErrorCodesAndMessages.UNABLE_TO_REDIRECT_TO_RESULT);
		}

	}

}
