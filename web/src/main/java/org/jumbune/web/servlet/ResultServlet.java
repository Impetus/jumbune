package org.jumbune.web.servlet;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.ResourceUsageCollector;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.Gson;


/**
 * The Class ResultServlet is responsible for displaying the reports on the UI.
 */
public class ResultServlet extends HttpServlet {
	private static final String JOBJARS = "/jobJars/";
	private static final String BIN_HADOOP = "/bin/hadoop";
	private static final String RUNNING_JOB = "Running job:";
	private static final String JOB_KILL = "job -kill ";
	private static final String EXECUTED_HADOOP_JOB_INFO = "executedHadoopJob.info";
	private static final String HADOOP_JOB_COMPLETED = "Hadoop#Job@Completed......";
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ResultServlet.class);

	
	/** The Constant REPORT_JSON. */
	private static final String REPORT_JSON = "reports";

	/**
	 * Instantiates a new result servlet.
	 */
	public ResultServlet() {
		super();
	}

	/**
	 * doGet method for ResultServlet
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * doPost method for ResultServlet
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

			HttpSession session = request.getSession();

			HttpReportsBean reports;
			try {
				if("TRUE".equals(request.getParameter("killJob"))){
					StringBuilder sb = new StringBuilder();
					sb.append(System.getenv("JUMBUNE_HOME"))
							.append(WebConstants.TMP_DIR_PATH)
							.append(WebConstants.JUMBUNE_STATE_FILE);
					File file = new File(sb.toString());
					YamlLoader yamlLoader=(YamlLoader) session.getAttribute("loader");
					YamlConfig config=yamlLoader.getYamlConfiguration();
					RemotingUtil.receiveLogFilesFromAgent(yamlLoader, EXECUTED_HADOOP_JOB_INFO);
					File hadoopJobStateFile=new File(YamlLoader.getjHome()+JOBJARS+config.getJumbuneJobName()+"/"+EXECUTED_HADOOP_JOB_INFO);
					if(hadoopJobStateFile.exists()){
					readHadoopJobIDAndKillJob(yamlLoader, hadoopJobStateFile);
					}
					file.delete();
					ResourceUsageCollector collector = new ResourceUsageCollector(yamlLoader);
					collector.shutTopCmdOnSlaves(null);
					HttpExecutorService service=(HttpExecutorService) session.getAttribute("ExecutorServReference");
					service.stopExecution();
					final RequestDispatcher rd = getServletContext().getRequestDispatcher(
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
				out.close();
				out.flush();
			}
		}

	private void readHadoopJobIDAndKillJob(YamlLoader yamlLoader,
			File hadoopJobStateFile) throws IOException {
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(hadoopJobStateFile)));
		String line=null,jobName=null;
		while((line=br.readLine())!=null){
			if(line.contains(HADOOP_JOB_COMPLETED)){
				return;
			}
			if(line.contains(RUNNING_JOB)){
				jobName=line.split(RUNNING_JOB)[1].trim();
			}
		}
		Remoter remoter = RemotingUtil.getRemoter(yamlLoader, "");
		StringBuilder sbReport = new StringBuilder();
		sbReport.append(yamlLoader.getHadoopHome(yamlLoader)).append(BIN_HADOOP).append(" ").append(JOB_KILL)
				.append(" ").append(jobName);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(sbReport.toString(), false, null).populate(yamlLoader.getYamlConfiguration(), null);
		String commandResponse = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		LOG.info("Hadoop Job has been killed ["+jobName+"]");
		LOG.debug("Killed Hadoop Job command response ["+commandResponse+"]");
		remoter.close();

	}
}
