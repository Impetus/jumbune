package org.jumbune.web.servlet;

import static org.jumbune.execution.utils.ExecutionConstants.TEMP_DIR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class DataQualityServlet extends HttpServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Constant REPORTS_BEAN. */
	private final String REPORTS_BEAN = "ReportsBean";
	
	private final String JOB_NAME = "JobName";
	
	private final String TABS = "tabs";

	private final String CONFIG = "config";
	
	/** The json string. */
	private String jsonString;
	
	private static final String JOB_JSON = "jobJson";
	
	
	
	private static final Logger LOG = LogManager.getLogger(DataQualityServlet.class);
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
		Config config = null;
		
		HttpReportsBean reports = new HttpReportsBean();
		HttpSession session = request.getSession();
		LOG.info("\n\n *************************** NEW REQUEST ************************* \n\n");
		try {
			HttpExecutorService service = new HttpExecutorService();

			if (ServletFileUpload.isMultipartContent(request)) {
				config = saveUsersResources(request);
				JobConfig jobConfig = (JobConfig)config;
				saveJsonToJumbuneHome(jobConfig);
				
				WebUtil webUtil = new WebUtil();
			
				LOG.debug("Configuration received ["+jobConfig+"]");
				
				
				session.setAttribute(REPORTS_BEAN, reports);
				String tabs = webUtil.getTabsInformation(jobConfig);
				request.setAttribute(JOB_NAME, jobConfig.getJumbuneJobName());
				
				config = service.runInSeperateThread(jobConfig, reports);
				session.setAttribute("ExecutorServReference",service);
				session.setAttribute(CONFIG, config);
				request.setAttribute(JOB_JSON, this.jsonString);
				request.setAttribute(TABS, tabs);

				final RequestDispatcher rd = getServletContext().getRequestDispatcher(WebConstants.RESULT_URL);
				rd.forward(request, response);

			} else {
				LOG.info("Servicing a request that is not multipart/form-data !!!");
			}

		} catch (JumbuneException htfException) {
			throw new ServletException(htfException);
		} catch (Exception e) {
			LOG.error("Exception Occurred", e);
			throw new ServletException(e);
		}
	}
	/**
	 * This method performs following steps: 1) Reads yamlConfig json and creates and object of YamlConfig 2) Reads the jumbuneJobName from this
	 * yamlConfig 3) Creates various directories of jumbune under folder jHome/jobJars/<jumbuneJobName>/logs or profiling or jars 4) Saves user jar in
	 * the pre-defined directory structure 5) Delete temporary files.
	 * 
	 * @param request
	 *            the request
	 * @return the job config
	 * @throws JumbuneException
	 *             the hTF exception
	 */
	private Config saveUsersResources(HttpServletRequest request) throws JumbuneException {
		String yamlData = null;
		String yamlTempLoc = TEMP_DIR + File.separator + System.currentTimeMillis();
		File repository = new File(JobConfig.getJumbuneHome() + yamlTempLoc);

		makeDirectories(repository);

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(repository);
		factory.setSizeThreshold(-1);
		ServletFileUpload upload = new ServletFileUpload(factory);

		Config config = null;

		try {
			List<FileItem> uploadedItems = upload.parseRequest(request);
			FileItem jarFileItem = null;

			for (FileItem fileItem : uploadedItems) {
				if (fileItem.getFieldName().equals("jsonData")) {
					StringWriter writer = new StringWriter();
					IOUtils.copy(fileItem.getInputStream(), writer);
					yamlData = writer.toString();
					this.jsonString = yamlData;
					config = WebUtil.prepareJobConfig(yamlData);
					if (config == null) {
						return null;
					}
					JobConfig jobConfig = (JobConfig) config;
					LOG.debug("jobConfig: " + jobConfig);
					JobConfigUtil.checkIfJumbuneHomeEndsWithSlash(config);
					
				}

				
			}

			if (config != null) {
				JobConfigUtil jobConfigUtil = new JobConfigUtil(config);
				jobConfigUtil.createJumbuneDirectories();
		
			}
			// Once the data has been collected on delete this temp file
			deleteTempFiles(repository);
			return config;
		} catch (FileUploadException e) {
			LOG.error("Could not find given jar file ", e);
			throw JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace());
		} catch (IOException e) {
			LOG.error("Could not find given jar/yaml file ", e);
			throw JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace());
		}  catch (Exception e) {
			LOG.error("Unable to create jumbune directories ", e);
			throw new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY,e);
		}
	}
	
	/**
	 * @param repository creates the directories if they not exist
	 */
	private void makeDirectories(File repository) {
		if (!repository.exists()) {
			repository.mkdirs();
		}
	}
	
	/**
	 * @param repository
	 */
	private void deleteTempFiles(File repository) {
		File folder = new File(repository.getAbsolutePath());

		// Delete all the temp files that created because of this
		// parseRequest!!
		File[] downloadedFiles = folder.listFiles();
		if (downloadedFiles != null) {
			for (File f : downloadedFiles) {
				if (f.getName().endsWith(".tmp")) {
					f.delete();
				}
			}
		}
	}
	
	/**
	 * Save json to jumbune home.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveJsonToJumbuneHome(JobConfig jobConfig) throws IOException {
		Gson gson = new Gson();
		String jsonDir = System.getenv("JUMBUNE_HOME") + "/jsonrepo/";
		String fileName = jobConfig.getJumbuneJobName() + ".json";
		File jsonDirectory = new File(jsonDir);
		if (!jsonDirectory.exists()) {
			jsonDirectory.mkdir();
		}
		String json = this.jsonString;
		String jsonData = gson.toJson(jobConfig,JobConfig.class);
		jsonDir = jsonDir + fileName;
		File file = new File(jsonDir);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		bufferedWriter.write(jsonData);
		bufferedWriter.flush();
		bufferedWriter.close();
		LOG.debug("Persisted YamlWizard configuration [" + jsonData + "]");

	}

}
