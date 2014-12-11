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
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.ResourceUsageCollector;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.web.utils.WebConstants;

import com.google.gson.Gson;


/**
 * The Class ResultServlet is responsible for displaying the reports on the UI.
 */
public class ResultServlet extends HttpServlet {
	private  final String JOBJARS = "/jobJars/";
	private final String BIN_HADOOP = "/bin/hadoop";
	private final String RUNNING_JOB = "Running job:";
	private final String JOB_KILL = "job -kill ";
	private final String EXECUTED_HADOOP_JOB_INFO = "executedHadoopJob.info";
	private final String HADOOP_JOB_COMPLETED = "Hadoop#Job@Completed......";
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ResultServlet.class);

	
	/** The Constant REPORT_JSON. */
	private final String REPORT_JSON = "reports";

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
					HttpExecutorService service = null;
					YamlLoader yamlLoader = null;
					synchronized (session) {
						service=(HttpExecutorService) session.getAttribute("ExecutorServReference");
						session.removeAttribute("ExecutorServReference");
						service.stopExecution();
						StringBuilder sb = new StringBuilder();
						sb.append(System.getenv("JUMBUNE_HOME"))
								.append(WebConstants.TMP_DIR_PATH)
								.append(WebConstants.JUMBUNE_STATE_FILE);
						File file = new File(sb.toString());
						yamlLoader =(YamlLoader) session.getAttribute("loader");
						YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
						Remoter remoter = RemotingUtil.getRemoter(yamlConfig, "");
						String relativePath =  File.separator+Constants.JOB_JARS_LOC +yamlLoader.getJumbuneJobName();
						remoter.receiveLogFiles(relativePath, relativePath+File.separator+EXECUTED_HADOOP_JOB_INFO);
						File hadoopJobStateFile=new File(YamlLoader.getjHome()+File.separator+relativePath+File.separator+EXECUTED_HADOOP_JOB_INFO);
						if(hadoopJobStateFile.exists()){
							readHadoopJobIDAndKillJob(yamlLoader, hadoopJobStateFile);
						}
						file.delete();
						ResourceUsageCollector collector = new ResourceUsageCollector(yamlLoader);
						collector.shutTopCmdOnSlaves(null);
				}
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
				if (out != null) {
					out.flush();
					out.close();
				}
			}
		}

	private void readHadoopJobIDAndKillJob(Loader loader,
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
		if(br!=null){
			br.close();
		}
		Remoter remoter = RemotingUtil.getRemoter(loader, "");
		YamlLoader yamlLoader = (YamlLoader)loader;
		StringBuilder sbReport = new StringBuilder();
		sbReport.append(yamlLoader.getHadoopHome(loader)).append(BIN_HADOOP).append(" ").append(JOB_KILL)
				.append(" ").append(jobName);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(sbReport.toString(), false, null, CommandType.HADOOP_JOB).populate(yamlLoader.getYamlConfiguration(), null);
		String commandResponse = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		LOG.info("Hadoop Job has been killed ["+jobName+"]");
		LOG.debug("Killed Hadoop Job command response ["+commandResponse+"]");
		remoter.close();

	}
}
