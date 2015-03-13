/**
 * 
 */
package org.jumbune.web.servlet;

import static org.jumbune.execution.utils.ExecutionConstants.TEMP_DIR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yarn.beans.YarnMaster;
import org.jumbune.common.yarn.beans.YarnSlaveParam;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.beans.ProfilingParam;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.SupportedHadoopDistributions;
import org.jumbune.common.beans.UnavailableHost;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.HadoopJobCounters;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.common.utils.YamlConfigUtil;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.service.HttpExecutorService;
import org.jumbune.profiling.beans.CategoryInfo;
import org.jumbune.profiling.beans.ClusterWideInfo;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.beans.NonYarnCategoryInfo;
import org.jumbune.profiling.beans.SystemStats;
import org.jumbune.profiling.beans.WorkerJMXInfo;
import org.jumbune.profiling.utils.JMXConnectorCache;
import org.jumbune.profiling.utils.ProfilerConstants;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.profiling.yarn.beans.YarnCategoryInfo;
import org.jumbune.profiling.yarn.beans.YarnClusterWideInfo;
import org.jumbune.profiling.yarn.beans.YarnWorkerJMXInfo;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * The Class ExecutionServlet.
 * 
 *
 */
@SuppressWarnings("serial")
public class ExecutionServlet extends HttpServlet {
	private static final String JOB_JSON = "jobJson";

	/***
	 * Mapping identifier for JobName
	 */
	private final String JOB_NAME = "JobName";

	private final String STATS_INTERVAL = "stats_interval";

	private final String TABS = "tabs";

	private final String LOADER = "loader";
	
	private boolean isYarnEnable = false;	

	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ExecutionServlet.class);

	/** The Constant FORWARD_SLASH. */
	private final String FORWARD_SLASH = "/";

	
	/** The Constant REPORTS_BEAN. */
	private final String REPORTS_BEAN = "ReportsBean";
	
	private final String JMX_CACHE="jmxCache";

	
	/** The json string. */
	private String jsonString;

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
	

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
		Config config = null;
		
		HttpReportsBean reports = new HttpReportsBean();
		HttpSession session = request.getSession();
		LOG.info("\n\n *************************** NEW REQUEST ************************* \n\n");


		try {
			HttpExecutorService service = new HttpExecutorService();

			if (ServletFileUpload.isMultipartContent(request)) {
				config = saveUsersResources(request);
				YamlConfig yamlConfig = (YamlConfig)config;
				saveJsonToJumbuneHome(yamlConfig);
				String agentHome = RemotingUtil.getAgentHome(yamlConfig);

				WebUtil util = new WebUtil();
				ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
				processClassPathElement(cse, agentHome);

				// place where list of dependent jars' path for instrumented job
				// jar are getting created.
				yamlConfig.getClasspath().setJumbuneSupplied(cse);
				// sends user uploaded MR job jars on agent
				String jarFilePath = YamlLoader.getjHome() + "/" + Constants.JOB_JARS_LOC + yamlConfig.getFormattedJumbuneJobName()
						+ Constants.MR_RESOURCES;

				checkAndSendMrJobJarOnAgent(yamlConfig, jarFilePath);
				LOG.debug("Configuration received ["+yamlConfig+"]");
				modifyDebugParameters(yamlConfig);
				modifyProfilingParameters(yamlConfig);
				setInputFileInConfig(yamlConfig);
				
				session.setAttribute(REPORTS_BEAN, reports);
				request.setAttribute(STATS_INTERVAL, Constants.TEN_THOUSAND);
				String tabs = util.getTabsInformation(yamlConfig);
				request.setAttribute(JOB_NAME, yamlConfig.getJumbuneJobName());
				Loader loader = service.runInSeperateThread(yamlConfig, reports);
				session.setAttribute("ExecutorServReference",service);
				setClusterProfilingAttributes(request, yamlConfig, loader);
				session.setAttribute(LOADER, loader);
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
	 * @param request
	 * @param config
	 * @param loader class loads the yaml
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws IntrospectionException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	private void setClusterProfilingAttributes(HttpServletRequest request,
			Config config, Loader loader)
			throws AttributeNotFoundException, InstanceNotFoundException,
			IntrospectionException, MBeanException, ReflectionException,
			IOException {
		YamlConfig yamlConfig = (YamlConfig)config;
		if (yamlConfig.getHadoopJobProfile().equals(Enable.TRUE)) {
		    isYarnEnable= yamlConfig.getEnableYarn().equals(Enable.TRUE)?true:false;
			request.setAttribute("clusterProfilerCategoriesJson", getProfilerCategoryJson(loader));
			request.setAttribute(STATS_INTERVAL, Constants.FIVE_THOUNSAND);

		}
	}

	/**
	 * @param config this method modifies the parameters needed for debugger.
	 */
	private void modifyDebugParameters(Config config) {
		YamlConfig yamlConfig = (YamlConfig)config;
		yamlConfig.setPartitionerSampleInterval(Constants.FIFTY);
		DebuggerConf debugConf = yamlConfig.getDebuggerConf();
		debugConf.setMaxIfBlockNestingLevel(Constants.FOUR);
		Map<String, LogLevel> logLevel = debugConf.getLogLevel();
		if (logLevel == null) {
			logLevel = new HashMap<String, LogLevel>();
		}

		logLevel.put("ifblock", LogLevel.TRUE);
		yamlConfig.getDebuggerConf().setMaxIfBlockNestingLevel(2);
		logLevel.put("switchcase", LogLevel.TRUE);
		logLevel.put("partitioner", LogLevel.FALSE);
	}

	/**
	 * @param config this method sets the input file in config
	 */
	private void setInputFileInConfig(Config config) {
		YamlConfig yamlConfig = (YamlConfig)config;
		if ((yamlConfig.getInputFile() != null) && (!yamlConfig.getInputFile().contains(FORWARD_SLASH))) {
			String jarName = YamlLoader.getJobJarLoc() + yamlConfig.getFormattedJumbuneJobName() + yamlConfig.getInputFile();
			yamlConfig.setInputFile(jarName);
		}
	}

	/**
	 * @param config bean for the yaml file
	 * @param jarFilePath refers to the path of the jar to be sent on agent 
	 */
	private void checkAndSendMrJobJarOnAgent(Config config,
			String jarFilePath) {
		if (YamlConfigUtil.isMRJobJarPresent(config, jarFilePath)) {
			YamlConfigUtil.sendMRJobJarOnAgent(config, jarFilePath);
		}
	}

	/**
	 * @param config refers to parameters used for modifying profiling parameters.
	 */
	private void modifyProfilingParameters(Config config) {
		ProfilingParam param = new ProfilingParam();
		YamlConfig yamlConfig = (YamlConfig)config;
		if (yamlConfig.getHadoopJobProfile().getEnumValue()) {
			param.setReducers("0-1");
			param.setMapers("0-1");
			param.setStatsInterval(Constants.FIVE_THOUNSAND);
			yamlConfig.setProfilingParams(param);
		}
	}


    /***
     * This method return all categories of hadoop jmx exposed by hadoop cluster in form of json
     * string.
     * 
     * @param loader YamlLoader instance
     * @return hadoop jmx categories in form of json String .
     * 
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws IntrospectionException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws IOException
     */
    private String getProfilerCategoryJson(Loader loader) throws AttributeNotFoundException,
        InstanceNotFoundException, IntrospectionException, MBeanException, ReflectionException,
        IOException {
      Gson gson = new Gson();
      YamlLoader yamlLoader = (YamlLoader) loader;
      YamlConfig yamlConfig = (YamlConfig) ((YamlLoader)loader).getYamlConfiguration();
      YarnSlaveParam slave =  yamlConfig.getSlaveParam();
      YarnMaster master = yamlConfig.getMaster();
      CategoryInfo categoryInfo = null;
      if(isYarnEnable){
        categoryInfo = new YarnCategoryInfo();
      }else {
        categoryInfo = new NonYarnCategoryInfo();
      }
      SupportedHadoopDistributions hadoopVersion =
          RemotingUtil.getHadoopVersion(yamlLoader.getYamlConfiguration());
      WorkerJMXInfo levelJMXInfo =null;
      ClusterWideInfo clusterWideInfo = new ClusterWideInfo();
      if(isYarnEnable){
        clusterWideInfo = new YarnClusterWideInfo();
        levelJMXInfo = new YarnWorkerJMXInfo();
      }else{
        clusterWideInfo = new ClusterWideInfo();
        levelJMXInfo = new WorkerJMXInfo();
      }
      ProfilerJMXDump dump = new ProfilerJMXDump();
      
      setClusterLevelDaemons(master, clusterWideInfo, hadoopVersion, dump);
      setNodeLevelDaemons(yamlLoader, master, slave, hadoopVersion, dump, levelJMXInfo);
      String systemStatsJson =
          WebUtil.getPropertyFromResource(WebConstants.PROFILING_PROPERTY_FILE,
              WebConstants.PROFILING_SYSTEM_JSON);
      SystemStats stats = gson.fromJson(systemStatsJson, SystemStats.class);
      
      categoryInfo.setClusterWide(clusterWideInfo);
      categoryInfo.setWorkerJMXInfo(levelJMXInfo);
      categoryInfo.setSystemStats(stats);
      return gson.toJson(categoryInfo);
    }


    private void setNodeLevelDaemons(YamlLoader loader, YarnMaster master, YarnSlaveParam slave,
        SupportedHadoopDistributions hadoopVersion, ProfilerJMXDump dump, WorkerJMXInfo levelJMXInfo)
        throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException,
        ReflectionException, IntrospectionException {
    	YamlConfig yamlConfig = (YamlConfig) ((YamlLoader)loader).getYamlConfiguration();
      for (Slave slaves : yamlConfig.getSlaves()) {
        for (String slaveIp : slaves.getHosts()) {
          if (master.getHost().equalsIgnoreCase(slaveIp)) {
            setYarnAndNonYarnDaemons(master.getHost(), slave, hadoopVersion, dump, levelJMXInfo);
          } else {
            setYarnAndNonYarnDaemons(slaveIp, slave, hadoopVersion, dump, levelJMXInfo);
          }
        }
      }
    }
    
    private void setYarnAndNonYarnDaemons(String host, YarnSlaveParam slave,
            SupportedHadoopDistributions hadoopVersion, ProfilerJMXDump dump,
            WorkerJMXInfo levelJMXInfo) throws IOException,
            AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException,
            IntrospectionException {
          levelJMXInfo.setDataNode(dump.getAllJMXAttribute(JMXDeamons.DATA_NODE, hadoopVersion, host,
              slave.getDataNodeJmxPort()));
          if(isYarnEnable){
            YarnWorkerJMXInfo ywji = (YarnWorkerJMXInfo) levelJMXInfo;
            ywji.setNodeManager(dump.getAllJMXAttribute(JMXDeamons.NODE_MANAGER,
                hadoopVersion, host, slave.getNodeManagerJmxPort()));
          }else{
            levelJMXInfo.setTaskTracker(dump.getAllJMXAttribute(JMXDeamons.TASK_TRACKER, hadoopVersion, host,
                  slave.getTaskTrackerJmxPort()));
          }
        }

	private void setClusterLevelDaemons(YarnMaster master,
			ClusterWideInfo clusterWideInfo,
			SupportedHadoopDistributions hadoopVersion, ProfilerJMXDump dump)
			throws IOException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IntrospectionException {
		clusterWideInfo.setNameNode(dump.getAllJMXAttribute(
				JMXDeamons.NAME_NODE, hadoopVersion, master.getHost(),
				master.getNameNodeJmxPort()));
		if (isYarnEnable) {
			YarnClusterWideInfo ycwi = (YarnClusterWideInfo) clusterWideInfo;
			ycwi.setResourceManager(dump.getAllJMXAttribute(
					JMXDeamons.RESOURCE_MANAGER, hadoopVersion,
					master.getHost(), master.getResourceManagerJmxPort()));
		} else {
			clusterWideInfo.setJobTracker(dump.getAllJMXAttribute(
					JMXDeamons.JOB_TRACKER, hadoopVersion, master.getHost(),
					master.getJobTrackerJmxPort()));
		}
	}
    
	/**
	 * Process class path element.
	 * 
	 * @param cse
	 *            the cse
	 * @param agentHome
	 *            the agent home
	 */
	private void processClassPathElement(ClasspathElement cse, String agentHome) {

		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace(Constants.AGENT_ENV_VAR_NAME, agentHome);

		}
	}

	/**
	 * Save yaml to jumbune home.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveJsonToJumbuneHome(YamlConfig config) throws IOException {
		Gson gson = new Gson();
		String jsonDir = System.getenv("JUMBUNE_HOME") + "/jsonrepo/";
		String fileName = config.getJumbuneJobName() + ".json";
		File yamlDirectory = new File(jsonDir);
		if (!yamlDirectory.exists()) {
			yamlDirectory.mkdir();
		}
		String json = this.jsonString;
		ClasspathElement classpathElement = config.getClasspath().getUserSupplied();
		if (WebConstants.MASTER_MACHINE_PATH_OPTION == classpathElement.getSource()) {
			String[] resources = null;
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_MASTER_MACHINE_PATH, jsonObject);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFolders(path);
			}
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_INCLUDE, jsonObject);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFiles(path);
			}
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_EXCLUDE, jsonObject);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setExcludes(path);
			}
		}
		String jsonData = gson.toJson(config,YamlConfig.class);
		jsonDir = jsonDir + fileName;
		File file = new File(jsonDir);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(jsonData);
		bw.flush();
		bw.close();
		LOG.debug("Persisted YamlWizard configuration [" + jsonData + "]");

	}

	

	/**
	 * This method performs following steps: 1) Reads yamlConfig json and creates and object of YamlConfig 2) Reads the jumbuneJobName from this
	 * yamlConfig 3) Creates various directories of jumbune under folder jHome/jobJars/<jumbuneJobName>/logs or profiling or jars 4) Saves user jar in
	 * the pre-defined directory structure 5) Delete temporary files.
	 * 
	 * @param request
	 *            the request
	 * @return the yaml config
	 * @throws JumbuneException
	 *             the hTF exception
	 */
	private Config saveUsersResources(HttpServletRequest request) throws JumbuneException {
		String yamlData = null;
		String yamlTempLoc = TEMP_DIR + FORWARD_SLASH + System.currentTimeMillis();
		File repository = new File(YamlLoader.getjHome() + yamlTempLoc);

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
					config = WebUtil.prepareYamlConfig(yamlData);
					if (config == null) {
						return null;
					}
					YamlConfig yamlConfig = (YamlConfig) config;
					isYarnEnable= yamlConfig.getEnableYarn().equals(Enable.TRUE);
					YamlConfigUtil.checkIfJumbuneHomeEndsWithSlash(config);

				}

				if (fileItem.getFieldName().equals("inputFile")) {
					jarFileItem = fileItem;
				}
			}

			if (config != null) {
				checkAvailableNodes(config);
				YamlLoader loader = new YamlLoader(config);
				YamlConfig yamlConfig = (YamlConfig)config;
				ClasspathElement classpathElement = yamlConfig.getClasspath().getUserSupplied();
		
				saveUserSuppliedJar(yamlData, config, classpathElement);
				loader.createJumbuneDirectories();
				writeUploadedFileToFileItem(config, jarFileItem);
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
	 * @param config refers to the bean containing the yaml parameters.
	 * @param jarFileItem represents a file or form item that was received within a multipart/form-data POST request. 
	 * @throws Exception
	 */
	private void writeUploadedFileToFileItem(Config config,
			FileItem jarFileItem) {
		YamlConfig yamlConfig = (YamlConfig)config;
		if (yamlConfig.getInputFile() != null) {
			// Remove the / from name
			String fileName = yamlConfig.getInputFile().substring(yamlConfig.getInputFile().lastIndexOf(FORWARD_SLASH) + 1);
			String newJarFileLoc = YamlLoader.getJobJarLoc() + yamlConfig.getFormattedJumbuneJobName() + fileName;
			LOG.debug("Job jar's newly located path ["  + newJarFileLoc+"]");
			File uploadedFile = new File(newJarFileLoc);
			try {
				jarFileItem.write(uploadedFile);
			} catch (Exception e) {
				LOG.error("Unable to write uploaded file into FileItem", e);
			}

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
	 * @param yamlData contains the data supplied in the yaml form.
	 * @param config refers to the bean containing the yaml parameters.
	 * @param classpathElement refers to the bean containing the classpath elements.
	 */
	private void saveUserSuppliedJar(String yamlData, Config config,
			ClasspathElement classpathElement) {
		String[] resources= null;
		YamlConfig yamlConfig = (YamlConfig)config;
		if (WebConstants.MASTER_MACHINE_PATH_OPTION == classpathElement.getSource() && WebUtil.isRequiredModuleEnable(config)) {
			String filePath = YamlLoader.getJobJarLoc() + yamlConfig.getFormattedJumbuneJobName() + WebConstants.DEPENDNET_JAR_RESOURCES_DIR;
			JsonObject json = (JsonObject) new JsonParser().parse(yamlData);
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_MASTER_MACHINE_PATH, json);
			if (resources != null) {
				String[] path  = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFolders(path);
				WebUtil.getLastIndexOfArray(resources, (filePath + WebConstants.DEPENDENT_JAR_MASTER_DIR));
			}
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_INCLUDE, json);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFiles(path);
				WebUtil.getLastIndexOfArray(resources, (filePath + WebConstants.DEPENDENT_JAR_INCLUDE_DIR));
			}
			resources = WebUtil.jsonValueOfMasterMachineField(WebConstants.DEPENDENT_JAR_EXCLUDE, json);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setExcludes(path);
				WebUtil.getLastIndexOfArray(resources, (filePath + WebConstants.DEPENDENT_JAR_EXCLUDE_DIR));
			}
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
	 * This method performs following steps: 1) Reads profilingParams and creates object of profilingParams 2) Reads the DataNode and TaskTracker from
	 * ProfilingParams and check the availability of nodes. 3) Saves UnavialableHost and removes that Host from config.
	 * 
	 * @param conf
	 *            the conf
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws MessagingException
	 *             the messaging exception
	 */
		public void checkAvailableNodes(Config conf) throws IOException, MessagingException {
		boolean isDNAvailable;
		boolean isWorkerDaemonAvailable;
		String dataNodePort = null;
		String workerDaemonPort = null;
		boolean isNodeAvailable = true;
		int dnPort = ProfilerConstants.DEFAULT_DN_PORT;
		int wdPort = ProfilerConstants.DEFAULT_TT_PORT;
		String nodeIp;
		String message = null;
		ValidateInput validate = new ValidateInput();
		YamlConfig yamlConfig = (YamlConfig)conf;
		boolean isProfilingEnabled = yamlConfig.getHadoopJobProfile().getEnumValue();
		YarnSlaveParam ysp = yamlConfig.getSlaveParam();
		if (isProfilingEnabled) {
			dataNodePort = yamlConfig.getSlaveParam().getDataNodeJmxPort();
			workerDaemonPort = isYarnEnable? ysp.getNodeManagerJmxPort():ysp.getTaskTrackerJmxPort();
			dnPort = Integer.parseInt(dataNodePort);
			wdPort = Integer.parseInt(workerDaemonPort);
		}
		for (Slave slave : yamlConfig.getSlaves()) {
			List<UnavailableHost> unavailableHosts = null;
			List<String> hosts = new ArrayList<String>(Arrays.asList(slave.getHosts()));
			if (Constants.ON.equalsIgnoreCase(slave.getEnableHostRange())) {
				String fromIP[] = slave.getHostRangeFromValue().split("\\.");
				String toIP[] = slave.getHostRangeToValue().split("\\.");
				String hostRangeVal = fromIP[0] + Constants.DOT + fromIP[1] + Constants.DOT + fromIP[2];
				int fromRangeVal = Integer.parseInt(fromIP[fromIP.length - 1]);
				int toRangeVal = Integer.parseInt(toIP[toIP.length - 1]);
				ConfigurationUtil.addSlaveRanges(fromRangeVal, toRangeVal, hostRangeVal, hosts);
			}
			Iterator<String> it = hosts.iterator();
		
			while (it.hasNext()) {
				nodeIp = it.next();
				isNodeAvailable = ConfigurationUtil.checkIPAdress(nodeIp);

				if ((isNodeAvailable) && (isProfilingEnabled)) {

					// check the availability of Datanode
					isDNAvailable = validate.isPortAvailable(dnPort, nodeIp);
					// check the availability of Datanode
					isWorkerDaemonAvailable = validate.isPortAvailable(wdPort, nodeIp);

					if (isDNAvailable) {
						if (!isWorkerDaemonAvailable) {
							message = isYarnEnable? ProfilerConstants.NM_NOT_REACHABLE : ProfilerConstants.TT_NOT_REACHABLE;
							isNodeAvailable = false;
						}
					} else {
						if (!isWorkerDaemonAvailable) {
							message = isYarnEnable? ProfilerConstants.DN_NM_NOT_REACHABLE : ProfilerConstants.DN_TT_NOT_REACHABLE;} else {
							message = ProfilerConstants.DN_NOT_REACHABLE;
						}
						isNodeAvailable = false;
					}
				} else {
					message = ProfilerConstants.NODE_NOT_REACHABLE;

				}

				unavailableHosts = removeUnavailableHosts(isNodeAvailable,
						nodeIp, message, unavailableHosts, it);

			}
			slave.setHosts(hosts.toArray(new String[hosts.size()]));
			slave.setUnavailableHosts(unavailableHosts);
		}
	}

	/**
	 * @param isNodeAvailable
	 * @param nodeIp denotes the ip of the host node
	 * @param message denotes whether the datanode or tasktracker or both are not reachable
	 * @param unavailableHosts containing the list of nodes which are unavailable
	 * @param it contains a list of hosts.
	 * @return a list containing a list of unavailable nodes.
	 */
	private List<UnavailableHost> removeUnavailableHosts(
			boolean isNodeAvailable, String nodeIp, String message,
			final List<UnavailableHost> unavailableHosts, Iterator<String> it) {
		UnavailableHost unavailableHost;
		List<UnavailableHost> unavailHost = unavailableHosts;
		if (!isNodeAvailable) {
			if (unavailHost == null) {
				unavailHost = new ArrayList<UnavailableHost>();
			}
			unavailableHost = new UnavailableHost();
			unavailableHost.setNodeIp(nodeIp);
			unavailableHost.setMessage(message);
			unavailHost.add(unavailableHost);

			LOG.info(message);
			it.remove();
		}
		return unavailHost;
	}



}
