package org.jumbune.web.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.ProfilerJMXDump;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jumbune.common.beans.cluster.EnterpriseCluster;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.common.utils.JMXUtility;
import org.jumbune.web.services.ClusterAnalysisService;
import org.jumbune.web.utils.StatsManager;
import org.jumbune.web.utils.WebConstants;

/**
 * It updates the worker nodes list in cluster json for AWS Spot Instances. It
 * uses namenode jmx to fetch worker nodes list.
 */
public class WorkersUpdaterProcess extends Thread implements BackgroundProcess {

	private final String NAME_NODE_INFO_LIVE_NODES = "NameNodeInfo.LiveNodes";

	private final String REGEX = ":";

	private final String INFO_ADDR = "infoAddr";

	private String clusterName;

	private boolean isOn;

	private long repeatInterval;

	private int maxErrors;

	private int totalErrors;

	private EnterpriseCluster cluster;

	private Type type;

	private Gson gson;

	private static Logger LOGGER = LogManager.getLogger(WorkersUpdaterProcess.class);

	public WorkersUpdaterProcess(String clusterName, long repeatInterval, int maxErrors) {
		this.clusterName = clusterName;
		this.repeatInterval = repeatInterval;
		this.maxErrors = maxErrors;
		this.isOn = true;
		this.totalErrors = 0;

		try {
			setClusterObject();
		} catch (IOException e) {
			LOGGER.error("Unable to get cluster [" + clusterName + "]", e.getMessage());
			this.isOn = false;
			return;
		}
		
		if (this.cluster.isJmxPluginEnabled()) {
			JMXUtility utility = new JMXUtility();
			utility.sendJmxAgentToAllDaemons(cluster);
			utility.establishConnectionToJmxAgent((EnterpriseClusterDefinition) cluster);
		}
		
		this.gson = new Gson();
		
		this.type = new TypeToken<Map<String, Map<String, String>>>() {
		}.getType();
	}
	
	@Override
	public void run() {
		while (isOn) {
			try {
				setClusterObject();
			} catch (IOException e) {
				LOGGER.error("Unable to get cluster [" + clusterName + "]", e.getMessage());
				this.isOn = false;
				return;
			}
			checkAndUpdateNodes();
			if (totalErrors > maxErrors) {
				isOn = false;
			}
			try {
				Thread.sleep(repeatInterval);
			} catch (InterruptedException e) {
				LOGGER.error("Unable to sleep the WorkersUpdaterProcess, cluster [" + clusterName + "]", e);
				return;
			}
		}
	}

	private void setClusterObject() throws IOException {
		this.cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		if (this.cluster == null) {
			File file = new File(System.getenv("JUMBUNE_HOME") + "/clusters/" + clusterName + ".json");
			StringBuffer json = new StringBuffer();
			BufferedReader br = null;
			try {
				String line;
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					json.append(line);
				}
			} finally {
				if (br != null) {
					br.close();
				}
			}
			this.cluster = new Gson().fromJson(json.toString(), EnterpriseClusterDefinition.class);
			ClusterAnalysisService.updateClusterCache(clusterName, this.cluster);
		}
	}

	private void checkAndUpdateNodes() {
		
		// Fetching the live worker nodes from Namenode JMX stats
		Map<String, String> nameNodeStats = null;
		try {
			nameNodeStats = new ProfilerJMXDump()
						.getAllJMXStats(
								JMXDeamons.NAME_NODE, 
								this.cluster.getNameNode(),
								this.cluster.getNameNodes().getNameNodeJmxPort(),
								this.cluster.isJmxPluginEnabled());

		} catch (Exception e) {
			LOGGER.error("Unable to get Namenode stats", e.getMessage());
			totalErrors++;
			return;
		}

		String liveNodesJson = nameNodeStats.get(NAME_NODE_INFO_LIVE_NODES);
		
		/** Live Nodes sample json
		  {"impetus-i0056.impetus.co.in":{"infoAddr":"172.26.32.35:1006",
		  "infoSecureAddr":"172.26.32.35:0","xferaddr":"172.26.32.35:1004",
		  "lastContact":2,"usedSpace":14934016,"adminState":"In Service"
		  ,"nonDfsUsedSpace":525248630784,"capacity":2977024937984,"numBlocks":
		  140,"version":"2.6.0-cdh5.7.3","used":14934016,"remaining":
		  2451761373184,"blockScheduled":0,"blockPoolUsed":14934016,
		  "blockPoolUsedPercent":5.016423E-4,"volfails":0},
		  "impetus-i0054.impetus.co.in":{"infoAddr":"172.26.32.33:1006",
		  "infoSecureAddr":"172.26.32.33:0","xferaddr":"172.26.32.33:1004",
		  "lastContact":1,"usedSpace":14934016,"adminState":"In Service"
		  ,"nonDfsUsedSpace":666285215744,"capacity":2999543734272,"numBlocks":
		  140,"version":"2.6.0-cdh5.7.3","used":14934016,"remaining":
		  2333243584512,"blockScheduled":0,"blockPoolUsed":14934016,
		  "blockPoolUsedPercent":4.9787626E-4,"volfails":0},
		  "impetus-i0060.impetus.co.in":{"infoAddr":"172.26.32.42:1006",
		  "infoSecureAddr":"172.26.32.42:0","xferaddr":"172.26.32.42:1004",
		  "lastContact":1,"usedSpace":14934016,"adminState":"In Service"
		  ,"nonDfsUsedSpace":593141149696,"capacity":2928222224384,"numBlocks":
		  140,"version":"2.6.0-cdh5.7.3","used":14934016,"remaining":
		  2335066140672,"blockScheduled":0,"blockPoolUsed":14934016,
		  "blockPoolUsedPercent":5.1000284E-4,"volfails":0}}
		 */
		// Parsing json and fetching live nodes ip address
		Map<String, Map<String, String>> liveNodesMap = gson.fromJson(liveNodesJson, type);
		List<String> liveNodes = new ArrayList<>(liveNodesMap.size());
		for (Map.Entry<String, Map<String, String>> e : liveNodesMap.entrySet()) {
			liveNodes.add(e.getValue().get(INFO_ADDR).split(REGEX)[0]);
		}
		
		List<String> currentNodes = this.cluster.getWorkers().getHosts();
		
		// Checking if the nodes have been changed
		if (! liveNodes.equals(currentNodes)) {
			
			// Updating StatsManager class
			StatsManager statsManager = StatsManager.getInstance();
			for (String node : currentNodes) {
				if ( !liveNodes.contains(node)) {
					statsManager.removeNode(node);
				}
			}
			
			// Update nodes list in cluster object
			this.cluster.getWorkers().setHosts(liveNodes);
			
			// Update cluster object in ClusterAnalysisService class
			ClusterAnalysisService.updateClusterCache(clusterName, this.cluster);
			
			// Creating path for saving cluster json where it has to be saved
			String jsonFilePath = getClusterJsonDir() + File.separator + cluster.getClusterName() + WebConstants.JSON_EXTENSION;
			
			// Finally saving the updated cluster json
			try {
				PrintWriter out = new PrintWriter(jsonFilePath);
				out.print(gson.toJson(this.cluster));
				out.flush();
				out.close();
			} catch (IOException e) {
				LOGGER.error("Unable to update cluster worker nodes", e.getMessage());
				totalErrors++;
			}
			
			if (this.cluster.isJmxPluginEnabled()) {
				JMXUtility utility = new JMXUtility();
				utility.sendJmxAgentToAllDaemons(cluster);
				utility.establishConnectionToJmxAgent((EnterpriseClusterDefinition) cluster);
			}
		}
	}
	
	private String getClusterJsonDir() {
		String clusterJsonDir = System.getenv(WebConstants.JUMBUNE_HOME)
				+ WebConstants.CLUSTER_DIR;
		File jsonDirectory = new File(clusterJsonDir);
		if (!jsonDirectory.exists()) {
			jsonDirectory.mkdir();
		}
		return jsonDirectory.getAbsolutePath();
	}

	@Override
	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public boolean isOn() {
		return isOn;
	}

}
