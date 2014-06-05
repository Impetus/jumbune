package org.jumbune.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.ClusterInfo;
import org.jumbune.profiling.beans.NodeConfig;
import org.jumbune.profiling.beans.NodeStats;
import org.jumbune.profiling.beans.PerformanceStats;
import org.jumbune.profiling.healthview.NetworkLatencyInfo;
import org.jumbune.profiling.service.ClusterViewServiceImpl;
import org.jumbune.profiling.service.ProfilingViewService;
import org.jumbune.profiling.utils.DataDistributionStats;
import org.jumbune.profiling.utils.HTFProfilingException;
import org.jumbune.profiling.utils.ViewHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Servlet to handle cluster profiling requests.
 */
@SuppressWarnings("serial")
public class ProfilerServlet extends HttpServlet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(ProfilerServlet.class);
	
	/** The Constant NODE_VIEW. */
	private static final String NODE_VIEW = "NODE_VIEW";
	
	/** The Constant CLUSTER_VIEW. */
	private static final String CLUSTER_VIEW = "CLUSTER_VIEW";
	
	/** The Constant DEFAULT_VIEW. */
	private static final String DEFAULT_VIEW = CLUSTER_VIEW;
	
	/** The Constant NETWORK_LATENCY_VIEW. */
	private static final String NETWORK_LATENCY_VIEW = "NETWORK_LATENCY_VIEW";
	
	/** The Constant NETWORK_LATENCY_RESULT. */
	private static final String NETWORK_LATENCY_RESULT = "NETWORK_LATENCY_RESULT";
	
	/** The Constant COLOR. */
	private static final String COLOR = "color";
	
	/** The Constant NODE_IP. */
	private static final String NODE_IP = "nodeIp";
	
	/** The Constant NODE_LIST. */
	private static final String NODE_LIST = "NODE_LIST";
	
	/** The Constant REPORT_JSON. */
	private static final String REPORT_JSON = "reports";
	
	/** The Constant NODE_CONFIG. */
	private static final String NODE_CONFIG = "nodeConfig";
	
	/** The Constant COLOR_CONFIG. */
	private static final String COLOR_CONFIG = "colorConfig";
	
	/** The Constant GENERAL_SETTINGS. */
	private static final String GENERAL_SETTINGS = "general_settings";
	
	/** The Constant DATALOAD_VIEW. */
	private static final String DATALOAD_VIEW = "DATALOAD_VIEW";
	
	/** The Constant DATA_DISTRIBUTION_VIEW. */
	private static final String DATA_DISTRIBUTION_VIEW = "DATA_DISTRIBUTION_VIEW";
	
	/** The Constant HDFS_PATH. */
	private static final String HDFS_PATH = "HDFS_PATH";

	
	/** The general settings. */
	private String generalSettings;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		// Getting yaml loader stored in session.
		YamlLoader loader = (YamlLoader) session.getAttribute("loader");
		String isNameNodeRequired = request.getParameter("NAME_NODE");
		boolean addNameNodeIP = checkIfNameNodeIsRequired(isNameNodeRequired,
				false);
		String viewName = request.getParameter("VIEW_NAME");
		viewName = setDefaultView(viewName);
		Gson gson = new Gson();

		YamlConfig config = loader.getYamlConfiguration();
		
		ProfilingViewService profilingViewService = new ClusterViewServiceImpl(
				loader);
		boolean isNodeView = false;
		String json = null;
		generalSettings = request.getParameter(GENERAL_SETTINGS);
		try {
			if ((CLUSTER_VIEW.equals(viewName))
					|| (NETWORK_LATENCY_VIEW.equals(viewName))) {
				generalSettings = setGeneralSettings();
				json = getClusterAndNetWorkLatencyViewJson(addNameNodeIP,
						viewName, gson, config, profilingViewService);
			} else if (NODE_VIEW.equals(viewName)) {
				isNodeView = true;
				List<PerformanceStats> perfStats = null;
				String nodeConfigJson = request.getParameter(NODE_CONFIG);
				String colorConfigJson = request.getParameter(COLOR_CONFIG);
				String nodeIp = request.getParameter(NODE_IP);
				json = getNodeViewJson(gson, profilingViewService, json,
						perfStats, nodeConfigJson, colorConfigJson, nodeIp);
			} else if (NETWORK_LATENCY_RESULT.equals(viewName)) {
				String nodeList = request.getParameter(NODE_LIST);
				isNodeView = true;
				ViewHelper viewHelper = new ViewHelper();
				json = getNetworkLatencyJson(gson, config, nodeList, viewHelper);
			} else if (DATALOAD_VIEW.equals(viewName)) {
				json = createDataloadAndDistributionViewJson(viewName, gson,
						profilingViewService);
			} else if (DATA_DISTRIBUTION_VIEW.equals(viewName)) {
				String dataPath = request.getParameter(HDFS_PATH);
				String nodeIP = request.getParameter("NODE_IP");
				if (nodeIP != null) {
					viewName = DATA_DISTRIBUTION_VIEW + "_NODE";
					DataDistributionStats dataDistributionStats = new DataDistributionStats(
							loader);
					try {
						json = dataDistributionStats.getNodeStats(nodeIP,
								config);
					} catch (Exception e) {
						LOGGER.error(e);
					}
				} else {
					json = setDataDistributionView(viewName, gson, config,
							profilingViewService, dataPath);
				}
			}
			// prepare and writes final JSON to output stream.
			writeFinalJson(viewName, json, isNodeView, response);
		} catch (HTFProfilingException htfe) {
			LOGGER.error(htfe);

		}
	}

	/**
	 * Gets the network latency json.
	 *
	 * @param gson the gson
	 * @param config the config
	 * @param nodeList the node list
	 * @param viewHelper the view helper
	 * @return the network latency json
	 * @throws HTFProfilingException the hTF profiling exception
	 */
	private String getNetworkLatencyJson(Gson gson, YamlConfig config,
			String nodeList, ViewHelper viewHelper)
			throws HTFProfilingException {
		Map<String, String> nodeDetails = convertJsontoMap(nodeList);
		List<NetworkLatencyInfo> networkLatencyInfoList = viewHelper
				.getNetworkLatency(config, nodeDetails);
		return gson.toJson(networkLatencyInfoList);
		
	}

	/**
	 * Sets the data distribution view.
	 *
	 * @param viewName the view name
	 * @param gson the gson
	 * @param config the config
	 * @param profilingViewService the profiling view service
	 * @param dataPath the data path
	 * @return the string
	 * @throws HTFProfilingException the hTF profiling exception
	 */
	private String setDataDistributionView(String viewName, Gson gson,
			YamlConfig config, ProfilingViewService profilingViewService,
			String dataPath) throws HTFProfilingException {
		config.setDistributedHDFSPath(dataPath);
		return createDataloadAndDistributionViewJson(viewName, gson,
				profilingViewService);
		
	}

	/**
	 * Creates the dataload and distribution view json.
	 *
	 * @param viewName refers to Data load or data Distribution view
	 * @param gson the gson
	 * @param profilingViewService Service to return different profiling view.
	 * @return contains gson containing information regarding data distribution view.
	 * @throws HTFProfilingException the hTF profiling exception
	 */
	private String createDataloadAndDistributionViewJson(String viewName, Gson gson,
			ProfilingViewService profilingViewService)
			throws HTFProfilingException {
		
		generalSettings = setGeneralSettings();
		List<PerformanceStats> perfStats = getPerfStatsFromJson(generalSettings);
		ClusterInfo clusterInfo = profilingViewService.getMainView(
				perfStats, viewName);
		return gson.toJson(clusterInfo);
		
	}

	/**
	 * Sets the default view.
	 *
	 * @param viewName the view name
	 * @return the string
	 */
	private String setDefaultView(final String viewName) {
		// Setting default view.
		String viewNameTemp = viewName;
		if (StringUtils.isBlank(viewNameTemp)) {
			viewNameTemp = DEFAULT_VIEW;
		}
		return viewNameTemp;
	}

	/**
	 * Check if name node is required.
	 *
	 * @param isNameNodeRequired the is name node required
	 * @param addNameNodeIP the add name node ip
	 * @return true, if successful
	 */
	private boolean checkIfNameNodeIsRequired(String isNameNodeRequired,
			 final boolean addNameNodeIP) {
		boolean addNameNodeIPTemp= addNameNodeIP;
		if (isNameNodeRequired != null && isNameNodeRequired.equals("TRUE")) {
			addNameNodeIPTemp = true;
		}
		return addNameNodeIPTemp;
	}

	/**
	 * Gets the node view json.
	 *
	 * @param gson the gson
	 * @param profilingViewService the profiling view service
	 * @param json the json
	 * @param perfStats the perf stats
	 * @param nodeConfigJson the node config json
	 * @param colorConfigJson the color config json
	 * @param nodeIp the node ip
	 * @return the node view json
	 * @throws HTFProfilingException the hTF profiling exception
	 */
	private String getNodeViewJson(Gson gson,
			ProfilingViewService profilingViewService, final String json,
			final List<PerformanceStats> perfStats, String nodeConfigJson,
			String colorConfigJson, String nodeIp) throws HTFProfilingException {
		List<PerformanceStats> performanceStats = perfStats;
		String jsonString = json;
		if (nodeConfigJson == null) {
			LOGGER.warn("Node configuration is not configured");
		} else {
			if (colorConfigJson != null) {
				performanceStats = getPerfStatsFromJson(colorConfigJson);
			}
			NodeConfig nodeConfig = gson.fromJson(nodeConfigJson,
					NodeConfig.class);
			nodeConfig.setNodeIp(nodeIp);
			NodeStats nodeStats = profilingViewService.getNodeView(
					nodeConfig, performanceStats);
			jsonString = gson.toJson(nodeStats);
		}
		return jsonString;
	}

	/**
	 * Gets the cluster and net work latency view json.
	 *
	 * @param addNameNodeIP the add name node ip
	 * @param viewName the view name
	 * @param gson the gson
	 * @param config the config
	 * @param profilingViewService the profiling view service
	 * @return the cluster and net work latency view json
	 * @throws HTFProfilingException the hTF profiling exception
	 */
	private String getClusterAndNetWorkLatencyViewJson(boolean addNameNodeIP,
			String viewName, Gson gson, YamlConfig config,
			ProfilingViewService profilingViewService)
			throws HTFProfilingException {
		String json;
		List<PerformanceStats> perfStats = getPerfStatsFromJson(generalSettings);
		ClusterInfo clusterInfo = profilingViewService.getMainView(
				perfStats, viewName);
		json = gson.toJson(clusterInfo);
		if (addNameNodeIP) {
			clusterInfo.setNameNodeIP(config.getMaster().getHost());
			json = gson.toJson(clusterInfo);
		}
		return json;
	}

	/**
	 * Sets the general settings.
	 *
	 * @return the string
	 */
	private String setGeneralSettings() {
		if (generalSettings == null || generalSettings.matches("^[0-9]")
				|| generalSettings.matches("^[-][0-9]")) {
			LOGGER.info("Profiled with default settings");
			generalSettings = getDefaultClusterSettings();
		}
		return generalSettings;
	}

	/**
	 * Write final json.
	 *
	 * @param viewName the view name
	 * @param json the json
	 * @param isNodeView the is node view
	 * @param resp the resp
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeFinalJson(String viewName, String json,
			boolean isNodeView, HttpServletResponse resp) throws IOException {
		String finalJson = null;
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");
		try {
			Gson gson = new Gson();
			/*
			 * If node view then send without formatting.
			 */
			if (isNodeView) {
				finalJson = json;
			} else if (viewName.equals("DATA_DISTRIBUTION_VIEW_NODE")) {
				finalJson = json;
			} else {
				Map<String, String> viewMap = new HashMap<String, String>();
				viewMap.put(viewName, json);
				Map<String, String> finalData = new HashMap<String, String>(2);
				finalData.put(REPORT_JSON, gson.toJson(viewMap));
				finalJson = gson.toJson(finalData);
			}
			LOGGER.debug("Profiled JSON [" + finalJson+"]");
			out.println(finalJson);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * Gets default color settings for the cluster.
	 *
	 * @return the default cluster settings
	 */
	private String getDefaultClusterSettings() {
		return "{\"color\":[{\"stat\":\"cpuUsage\",\"category\":\"systemStats.cpu\",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"},\"bad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}},{\"stat\":\"usedmemory\",\"category\":\"systemStats.memory\",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"},\"bad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}}]}";
		
	}

	/**
	 * Converts the json containing node ip and their password into a
	 * LinkedHashMap.
	 *
	 * @param json the json
	 * @return the map
	 */
	private Map<String, String> convertJsontoMap(final String json) {
		
		String jSon= json;

		Map<String, String> nodeDetails = new LinkedHashMap<String, String>();
		jSon = json.replace("{", "");
		jSon = jSon.replace("}", "");
		jSon = jSon.replace("\"", "");
		String[] nodePairs = jSon.split(",");
		String[] keyValuePair;
		for (String keyValue : nodePairs) {
			keyValuePair = keyValue.split(":");
			nodeDetails.put(keyValuePair[0], keyValuePair[1]);
		}
		return nodeDetails;
	}

	/**
	 * Gets the perf stats from json.
	 *
	 * @param generalSettings the general settings
	 * @return the perf stats from json
	 */
	private List<PerformanceStats> getPerfStatsFromJson(String generalSettings) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, List<PerformanceStats>>>() {
		}.getType();
		Map<String, List<PerformanceStats>> genSettings = gson.fromJson(
				generalSettings, type);
	return genSettings.get(COLOR);
		
	}

}
