package org.jumbune.web.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.web.utils.WebConstants;



/**
 * Servlet implementation class SchedulerInfoService.
 */
@Path(WebConstants.SCHEDULED_TUNING_JOBS)
public class SchedulerInfoService {

	/** The Constant LOG. */
	private static final Logger LOGGER = LogManager
			.getLogger(SchedulerInfoService.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response processGet() {
		try {
			Map<String, String> fileArrayMap = getScheduledJobList();
			Gson gson = new Gson();
			String scheduledJobArray = gson.toJson(fileArrayMap);
			return Response.ok(scheduledJobArray).build();
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	/**
	 * Gets the scheduled job list.
	 *
	 * @return the scheduled job list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Map<String, String> getScheduledJobList() throws IOException {
		String scheduleJob = ExtendedConfigurationUtil.getUserScheduleJobLocation();
		Map<String, String> scheduledJobNames = new LinkedHashMap<String, String>();
		File scheduleJobLoc = new File(scheduleJob);

		if (scheduleJobLoc.exists()) {
			List<String> responseList = RemoteFileUtil.executeResponseList(
					Constants.SORT_COMMAND.split(" "), scheduleJobLoc.getAbsolutePath());
			if(!responseList.isEmpty()){
				BufferedReader bufferedReader = null;
				String status = null;
				
				for(String scheduledJob : responseList) {
					File userSchDir = new File(scheduleJobLoc+File.separator+scheduledJob);
					if (userSchDir.isDirectory()) {
						FileReader reader =new FileReader(userSchDir + 
								ExtendedConstants.SCHEDULED_JOB_STATUS_FILE);
						try {
							bufferedReader = new BufferedReader(reader);
							status = bufferedReader.readLine();
							scheduledJobNames.put(userSchDir.getName(), status);
						} finally {
							if (bufferedReader != null) {
								bufferedReader.close();
							}
						}
					}
				}
				
			}		
		}
		return scheduledJobNames;
	}

}
