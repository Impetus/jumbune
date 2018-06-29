package org.jumbune.web.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.ResourceUsageCollector;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.utils.exception.ExtendedErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.Gson;


/**
 * The Class ResultService is responsible for displaying the reports on the UI.
 */
@Path(WebConstants.RESULT_SERVICE_URL)
public class ResultService {
	
	private final String BIN_HADOOP = "/bin/hadoop";
	private final String RUNNING_JOB = "Running job:";
	private final String JOB_KILL = "job -kill ";
	private final String EXECUTED_HADOOP_JOB_INFO = "executedHadoopJob.info";
	private final String HADOOP_JOB_COMPLETED = "Hadoop#Job@Completed......";
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ResultService.class);


	/** The Constant REPORT_JSON. */
	private final String REPORT_JSON = "reports";

	/** The Constant FILE_SEPARATOR. */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	
	@Context
	private HttpServletRequest request;
	
	@Context
	private HttpServletResponse response;

	

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
		String scheduledJobName = (String) request.getParameter("scheduledJobName");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		if (scheduledJobName != null && !scheduledJobName.equals("null")) {
			LOG.debug("This is a scheduled job result request!!!");
			// This is a scheduled job request so fetch all the reports and add
			// it in json
			// Read yamlConfig file and set the number of tabs in yamlResult
			String scheduleJobLoc = ExtendedConfigurationUtil.getUserScheduleJobLocation() + FILE_SEPARATOR + scheduledJobName;

			try {
				String reportsJson = readScheduleJobReports(scheduleJobLoc);
				if (reportsJson.contains(WebConstants.AJAX_CALL) && reportsJson.contains(WebConstants.AJAX_STOP)) {
					LOG.debug("all request has been served.:D");
				}
				
				Map<String, String> reportMap = new HashMap<String, String>(2);

				reportMap.put(REPORT_JSON, reportsJson);

				out.println(Constants.gson.toJson(reportMap));

				LOG.debug("Report Map " + reportMap);
			} catch (JumbuneException e) {
				LOG.error("Unable to fetch reports of schedule job ", e);
			} finally {
				out.close();
				out.flush();
			}
		} else {
			HttpSession session = request.getSession();

			HttpReportsBean reports;
			try {
				if("TRUE".equals(request.getParameter("killJob"))){
					HttpExecutorService service = null;
					JumbuneRequest jumbuneRequest = null;
					synchronized (session) {
						service=(HttpExecutorService) session.getAttribute("ExecutorServReference");
						session.removeAttribute("ExecutorServReference");
						service.stopExecution();
						StringBuilder sb = new StringBuilder();
						sb.append(JumbuneInfo.getHome())
								.append(WebConstants.TMP_DIR_PATH)
								.append(WebConstants.JUMBUNE_STATE_FILE);
						File file = new File(sb.toString());
						jumbuneRequest = (JumbuneRequest) session.getAttribute("jumbuneRequest");
						Remoter remoter = RemotingUtil.getRemoter(jumbuneRequest.getCluster(), "");
						JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
						String relativePath =  File.separator+Constants.JOB_JARS_LOC +jobConfig.getJumbuneJobName();
						remoter.receiveLogFiles(relativePath, relativePath+File.separator+EXECUTED_HADOOP_JOB_INFO);
						File hadoopJobStateFile=new File(JumbuneInfo.getHome()+relativePath+File.separator+EXECUTED_HADOOP_JOB_INFO);
						if(hadoopJobStateFile.exists()){
							readHadoopJobIDAndKillJob(jumbuneRequest.getCluster(), hadoopJobStateFile);
						}
						file.delete();
						ResourceUsageCollector collector = new ResourceUsageCollector(jumbuneRequest);
						collector.shutTopCmdOnSlaves(null);
				}
					final RequestDispatcher rd = request.getServletContext().getRequestDispatcher(
							WebConstants.HOME_URL);
					rd.forward(request, response);
				}else if (session.getAttribute("ReportsBean") != null) {
					reports = (HttpReportsBean) session.getAttribute("ReportsBean");
					Map<String, String> jsonMap = reports.getContentsToShow();

					final Gson gson = new Gson();
					String jsonResponse = gson.toJson(jsonMap);
					
					Map<String, String> reportMap = new HashMap<String, String>(1);
					reportMap.put(REPORT_JSON, jsonResponse);
					out.println(gson.toJson(reportMap));
					LOG.debug("Session reports [" + session.getAttribute("ReportsBean")+"]");
				} else {
					out.println(WebConstants.AJAX_STOP_MSG);
					LOG.warn("Json Bean was not created");
				}
			} catch (Exception e) {
				LOG.error("Unable to get reports of current job: ", e);
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}
		}
	}

	/**
	 * Read schedule job reports.
	 *
	 * @param scheduledJobLoc the scheduled job loc
	 * @return the string
	 * @throws JumbuneException the Jumbune exception
	 */
	private String readScheduleJobReports(String scheduledJobLoc) throws JumbuneException {
		// File scheduleJobLoc
		String reportFolderPath = new StringBuilder(scheduledJobLoc).append(ExtendedConstants.SCHEDULING_REPORT_FOLDER).toString();

		File reportFolder = new File(reportFolderPath);
		Map<String, String> reportMap = new LinkedHashMap<String, String>();

		if (reportFolder.exists()) {
			File[] listOfReports = reportFolder.listFiles();

			if (listOfReports != null) {
				for (File report : listOfReports) {
					try {
						reportMap.put(report.getName(), ConfigurationUtil.readFileData(report.getAbsolutePath()));
					} catch (FileNotFoundException e) {
						LOG.error("Could not find file " + report.getAbsolutePath(), e);
						throw new JumbuneException(ExtendedErrorCodesAndMessages.COULD_NOT_READ_SCHEDULE_REPORT);
					} catch (IOException e) {
						LOG.error("Exception while reading file: " + report.getAbsolutePath(), e);
						throw new JumbuneException(ExtendedErrorCodesAndMessages.COULD_NOT_READ_SCHEDULE_REPORT);
					}
				}
			}
			reportMap.put(WebConstants.AJAX_CALL, WebConstants.AJAX_STOP);

			Gson gson = new Gson();
			return gson.toJson(reportMap);
		}
		return WebConstants.AJAX_STOP_MSG;
	}

	private void readHadoopJobIDAndKillJob(Cluster cluster,
			File hadoopJobStateFile) throws IOException {
		String line = null, jobName = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(hadoopJobStateFile)))) {
			
			while((line=br.readLine())!=null){
				if(line.contains(HADOOP_JOB_COMPLETED)){
					return;
				}
				if(line.contains(RUNNING_JOB)){
					jobName=line.split(RUNNING_JOB)[1].trim();
				}
			}
		}
		Remoter remoter = RemotingUtil.getRemoter(cluster, "");
		StringBuilder sbReport = new StringBuilder();
		sbReport.append(RemotingUtil.getHadoopHome(cluster)).append(BIN_HADOOP).append(" ").append(JOB_KILL)
				.append(" ").append(jobName);
		
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(sbReport.toString(), false, null, CommandType.HADOOP_JOB);
		String commandResponse = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		LOG.info("Hadoop Job has been killed ["+jobName+"]");
		LOG.debug("Killed Hadoop Job command response ["+commandResponse+"]");
		remoter.close();

	}	
}
