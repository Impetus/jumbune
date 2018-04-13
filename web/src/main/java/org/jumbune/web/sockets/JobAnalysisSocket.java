package org.jumbune.web.sockets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.Gson;

/**
 * The Class JobAnalysisSocket.
 */
@WebSocket
public class JobAnalysisSocket {

	private static final Logger LOGGER = LogManager.getLogger(JobAnalysisSocket.class);

	/** The job name. */
	private String jobName;

	/**
	 * Instantiates a new job analysis socket.
	 *
	 * @param jobName
	 *            the job name
	 */
	public JobAnalysisSocket(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * On connect.
	 *
	 * @param session
	 *            the session
	 * @throws Exception
	 *             the exception
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		LOGGER.debug("Connected to result socket. Executing job: " + jobName);
		SocketPinger socketPinger = null;
		try {
			socketPinger = new SocketPinger(session);
			new Thread(socketPinger).start();
			String result = getReport();
			if (isJobScheduled(jobName)) {
				session.getRemote().sendString(getScheduledTuningJobResult(jobName));
			} else if (result != null) {
				session.getRemote().sendString(result);
			} else {
				HttpReportsBean reports = triggerJumbuneJob();
				asyncPublishResult(reports, session);
			}

		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		} finally {
			socketPinger.terminate();
			if (session != null && session.isOpen()) {
				session.close();
			}
			LOGGER.debug("Session closed sucessfully");
		}
	}

	private boolean isJobScheduled(String jobName) {
		String scheduleJobFolder = ExtendedConfigurationUtil.getUserScheduleJobLocation();
		String tuningJobReportPath = scheduleJobFolder + File.separator + jobName + File.separator;
		File file = new File(tuningJobReportPath);
		LOGGER.debug("Checking if [" + tuningJobReportPath + " ] exits or not :::" + file.exists());
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public String getScheduledTuningJobResult(String jobName)
			throws IOException, InterruptedException {
		String scheduleJobFolder = ExtendedConfigurationUtil.getUserScheduleJobLocation();
		String tuningJobReportPath = scheduleJobFolder + File.separator + jobName + File.separator;
		String statusFilePath = tuningJobReportPath + "status";
		File statusFile = new File(statusFilePath);
		// Waiting till the job is completed
		while (!"Completed".equalsIgnoreCase(FileUtils.readFileToString(statusFile).trim())) {
			Thread.sleep(5 * 1000);
		}

		File reportDir = new File(tuningJobReportPath + "reports/");
		File[] reports = reportDir.listFiles();
		Map<String, String> map = new HashMap<String, String>();
		if (reports != null && reports.length != 0) {
			for (File report : reports) {
				map.put(report.getName(), FileUtils.readFileToString(report));
			}
		}
		return new Gson().toJson(map);
	}

	/**
	 * On close.
	 *
	 * @param session
	 *            the session
	 * @param status
	 *            the status
	 * @param reason
	 *            the reason
	 */
	@OnWebSocketClose
	public void onClose(Session session, int status, String reason) {
		// kept for any post processing(s).
	}

	/**
	 * Publish result when available. This method polls on HttpReportsBean
	 * instance and publishes the report onto the socket when they are
	 * available.
	 * 
	 * @param reports
	 *            the reports
	 * @param session
	 *            the session
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	private void asyncPublishResult(HttpReportsBean reports, Session session)
			throws IOException, InterruptedException {
		Gson gson = new Gson();
		int completedModulesCount = reports.getAllCompletedReports().size();
		int expectedCounter = completedModulesCount;
		boolean executing = reports.isAnyProcessRunning();
		do {
			executing = reports.isAnyProcessRunning();
			completedModulesCount = reports.getAllCompletedReports().size();
			if (expectedCounter != completedModulesCount) {
				String report = gson.toJson(reports.getAllReports());
				session.getRemote().sendString(report);
				saveReports(report);
				expectedCounter = completedModulesCount;
			}
			LOGGER.debug("waiting for all modules to finish.....");
			Thread.sleep(2 * 1000);
		} while (executing);
	}

	private String getJobType(String jobName) {
		String[] jobTypes = { WebConstants.ANALYZE_DATA, WebConstants.ANALYZE_JOB };

		String jsonRepoPath = System.getenv(WebConstants.JUMBUNE_HOME) + WebConstants.JSON_REPO;
		String slashJsonName = File.separator + jobName;
		StringBuilder jobConfigFile = null;

		for (String jobType : jobTypes) {
			jobConfigFile = new StringBuilder(jsonRepoPath).append(jobType).append(slashJsonName);
			if (new File(jobConfigFile.toString()).exists()) {
				return jobType;
			}
		}
		return null;
	}

	private void saveReports(String report) {
		if (report.contains("DATA_QUALITY_TIMELINE")) {
			return;
		}
		String jobType = getJobType(jobName);
		if (jobType == null) {
			return;
		}
		PrintWriter out = null;
		String jsonPath = JumbuneInfo.getHome() + WebConstants.JSON_REPO + jobType
				+ File.separator + jobName + WebConstants.JOB_RESPONSE_JSON;
		try {
			out = new PrintWriter(jsonPath);
			out.print(report);
			out.flush();
		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to save result of job [" + jobName + "]", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private String getReport() {
		String jobType = getJobType(jobName);
		if (jobType == null) {
			return "";
		}
		String jsonPath = JumbuneInfo.getHome() + WebConstants.JSON_REPO + jobType
				+ File.separator + jobName + WebConstants.JOB_RESPONSE_JSON;
		String result = null;
		try {
			result = FileUtil.readFileIntoString(jsonPath);
		} catch (IOException e) {
			result = null;
		}
		return result;
	}

	/**
	 * Trigger jumbune job. This method triggers jumbune job(s) based on the
	 * configurations it reads from jumbune's json repository
	 * ($JUMBUNE_HOME/jsonrepo) i.e. json corresponding to jobName.
	 * <code>HttpReportsBean</code> instance returned by this method is
	 * asynchronously updated as and when the triggered modules finish and hence
	 * polling is needed on the returned instance to keep track of running and
	 * finished modules.
	 * 
	 * @return the http reports bean
	 * @throws Exception
	 *             the exception
	 */
	private HttpReportsBean triggerJumbuneJob() throws Exception {
		HttpReportsBean reports = new HttpReportsBean();
		HttpExecutorService service = new HttpExecutorService();
		
		String jobJson = readJobConfig(jobName);
		if (jobJson == null) {
			return reports;
		}
		JumbuneRequest jumbuneRequest = JobRequestUtil.addJobConfigWithCluster(jobJson);
		LOGGER.debug("Triggered jumbune job for jumbuneRequest: " + jumbuneRequest);
		service.runInSeperateThread(jumbuneRequest, reports);
		return reports;
	}
	
	private String readJobConfig(String jobName) throws Exception {
		String[] jobTypes = { WebConstants.ANALYZE_DATA, WebConstants.ANALYZE_JOB};

		String jsonRepoPath = System.getenv(WebConstants.JUMBUNE_HOME) + WebConstants.JSON_REPO;
		String slashJsonName = File.separator + jobName + WebConstants.JOB_REQUEST_JSON;
		StringBuilder jobConfigFile = null;

		for (String jobType : jobTypes) {
			jobConfigFile = new StringBuilder(jsonRepoPath).append(jobType).append(slashJsonName);
			if (new File(jobConfigFile.toString()).exists()) {
				break;
			}
		}
		return FileUtil.readFileIntoString(jobConfigFile.toString());
	}

	public class SocketPinger implements Runnable {

		private volatile boolean running = true;

		private volatile Session session;

		private ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 'a', 'l', 'i', 'v', 'e' });

		protected SocketPinger(Session session) {
			this.session = session;
		}

		private void terminate() {
			running = false;
		}

		@Override
		public void run() {
			try {
				while (running) {
					LOGGER.debug("Pinging client... Keeping session alive till result event....");
					session.getRemote().sendPing(buffer);
					// default idle timeout of socket is 300 secs.
					Thread.sleep(200 * 1000);
				}
			} catch (IOException | InterruptedException e) {
				LOGGER.error(JumbuneRuntimeException.throwInterruptedException(e.getStackTrace()));
			}

		}

	}

}
