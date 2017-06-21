package org.jumbune.web.process;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.ConfigurationConstants;
import org.jumbune.utils.conf.beans.BackgroundProcessConfiguration;
import org.jumbune.utils.conf.beans.ProcessType;

import org.jumbune.web.utils.StatsManager;

/**
 * It maintains the Background processes/threads [QUEUE_UTILIZATION
 * ,HISTORY_FILES, SYSTEM_METRICS] of clusters
 */
public class BackgroundProcessManager {
	
	private static volatile BackgroundProcessManager instance;

	private static Logger LOGGER = LogManager.getLogger(BackgroundProcessManager.class);

	/**
	 * key = cluster name, value = ClusterProcessesManager
	 * 
	 * @see ClusterProcessesManager
	 */
	private Map<String, ClusterProcessesManager> clusterProcessManagers = new HashMap<>(2);
	
	public static BackgroundProcessManager getInstance() {
		if (instance == null) {
			synchronized (StatsManager.class) {
				if (instance == null) {
					instance = new BackgroundProcessManager();
				}
			}
		}
		return instance;
	}

	/**
	 * This method has to be run at the starting of jumbune so that it could
	 * start all background processes of all clusters
	 */
	public void initiate() {

		File configurationDir = new File(System.getenv(ConfigurationConstants.JUMBUNE_ENV_VAR_NAME)
				+ ConfigurationConstants.CLUSTERS_CONFIGURATION_DIR);
		if (!configurationDir.exists() || configurationDir.list() == null) {
			return;
		}
		String[] clustersList = configurationDir.list();
		for (String clusterName : clustersList) {
			try {
				updateProcesses(clusterName);
			} catch (Exception e) {
				LOGGER.error("Unable to get background process configuration of '" + clusterName + "' cluster");
			}
		}
	}

	/**
	 * When called, fetch latest background process configuration and rechecks
	 * all processes of cluster. If a process is on/true/enabled then it starts
	 * otherwise stop the process/thread
	 * 
	 * @param clusterName cluster name
	 * @throws Exception
	 */
	public void updateProcesses(String clusterName) throws Exception {
		BackgroundProcessConfiguration bpc = AdminConfigurationUtil.getBackgroundProcessConfiguration(clusterName);
		for (Entry<ProcessType, Boolean> process : bpc.getProcessMap().entrySet()) {
			if (process.getValue()) {
				startProcess(clusterName, process.getKey());
			} else {
				stopProcess(clusterName, process.getKey());
			}
		}
	}

	/**
	 * It starts the process/thread
	 * @param clusterName
	 * @param processType
	 */
	public void startProcess(String clusterName, ProcessType processType) {
		ClusterProcessesManager manager = clusterProcessManagers.get(clusterName);
		if (manager == null) {
			manager = new ClusterProcessesManager(clusterName);
			clusterProcessManagers.put(clusterName, manager);
		}
		manager.startProcess(processType);
	}

	/**
	 * It stops the process/thread
	 * @param clusterName
	 * @param processType
	 */
	public void stopProcess(String clusterName, ProcessType processType) {
		ClusterProcessesManager manager = clusterProcessManagers.get(clusterName);
		if (manager != null) {
			manager.stopProcess(processType);
		}
	}

	/**
	 * Returns currently running processes/threads of a cluster
	 * 
	 * @param clusterName
	 * @return
	 * @throws Exception
	 */
	public Map<ProcessType, Boolean> getProcessesStatus(String clusterName) {
		ClusterProcessesManager manager = clusterProcessManagers.get(clusterName);
		if (manager == null) {
			manager = new ClusterProcessesManager(clusterName);
			clusterProcessManagers.put(clusterName, manager);
		}
		return manager.getAllProcessStatus();
	}

}
