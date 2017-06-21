package org.jumbune.web.process;

import java.util.HashMap;
import java.util.Map;

import org.jumbune.utils.conf.beans.ProcessType;

/**
 * It maintains the Background processes of a cluster
 */
public class ClusterProcessesManager {

	private String clusterName;

	private static final int INTERVAL = 15000, MAX_ERRORS = 10;

	private static final long WORKERS_UPDATER_INTERVAL = 1800000;

	// key = process type, value = a thread that is currently RUNNING
	private Map<ProcessType, BackgroundProcess> processMap;

	public ClusterProcessesManager(String clusterName) {
		this.clusterName = clusterName;
		this.processMap = new HashMap<>(5);
	}

	/**
	 * Start a process/thread
	 * 
	 * @param processType
	 *            process type
	 */
	public void startProcess(ProcessType processType) {
		BackgroundProcess process = processMap.get(processType);
		if (process == null
				|| !(process.getState() == Thread.State.RUNNABLE || process.getState() == Thread.State.TIMED_WAITING)) {
			if (processType == ProcessType.QUEUE_UTILIZATION) {
				process = new QueueUtilizationProcess(clusterName, INTERVAL, MAX_ERRORS);
			} else if (processType == ProcessType.SYSTEM_METRICS) {
				process = new SystemMetricsProcess(clusterName, INTERVAL, MAX_ERRORS);
			} else if (processType == ProcessType.WORKER_NODES_UPDATER) {
				process = new WorkersUpdaterProcess(clusterName, WORKERS_UPDATER_INTERVAL, MAX_ERRORS);
			}
			processMap.put(processType, process);
			process.start();
		}
	}

	/**
	 * Stop a process/thread
	 * 
	 * @param processType
	 *            process type
	 */
	public void stopProcess(ProcessType processType) {
		BackgroundProcess process = processMap.get(processType);
		if (isOn(processType)) {
			process.setOn(false);
		}
		processMap.remove(processType);
	}

	/**
	 * Returns status (running or not) of all processes of the cluster.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<ProcessType, Boolean> getAllProcessStatus() {
		Map<ProcessType, Boolean> map = new HashMap<>(3);
		map.put(ProcessType.QUEUE_UTILIZATION, isOn(ProcessType.QUEUE_UTILIZATION));
		map.put(ProcessType.SYSTEM_METRICS, isOn(ProcessType.SYSTEM_METRICS));
		return map;
	}

	private boolean isOn(ProcessType processType) {
		BackgroundProcess process = processMap.get(processType);
		if (process != null
				&& (process.getState() == Thread.State.RUNNABLE || process.getState() == Thread.State.TIMED_WAITING)) {
			return true;
		} else {
			return false;
		}
	}

}
