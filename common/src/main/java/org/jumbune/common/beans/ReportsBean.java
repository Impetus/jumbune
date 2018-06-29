package org.jumbune.common.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Bean that can store reports and is shared with various instances of
 * processor. Every processor should populate the respective report object and
 * mark them as complete when its ready to show on screen.
 * 
 * 
 * 
 */
public class ReportsBean {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(HttpReportsBean.class);
	
	/** The stop exectution. */
	private AtomicBoolean stopExectution = new AtomicBoolean(false);
	
	/** The reports. */
	private List<Map<String, String>> reports = new ArrayList<Map<String, String>>();
	
	/** The completed. */
	private Set<Module> completed = new ConcurrentSkipListSet<Module>();
	
	/** The status map. */
	private Map<String, Status> statusMap = new ConcurrentHashMap<String, Status>();

	/**
	 * Gets the all reports.
	 *
	 * @return the all reports
	 */
	public Map<String, String> getAllReports() {
		Map<String, String> returnExcelMap = new HashMap<String, String>();
		for (Map<String, String> map : reports) {
			returnExcelMap.putAll(map);
		}
		return returnExcelMap;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, String> clone(){
		try {
			return (Map<String, String>) super.clone();
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e);
		}
		return null;
	}

	/**
	 * Instantiates a new reports bean.
	 */
	public ReportsBean() {
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
		reports.add(new ConcurrentHashMap<String, String>());
	}

	/**
	 * Method for adding initial status of any process.
	 *
	 * @param processName the process name
	 */
	public void addInitialStatus(String processName) {
		statusMap.put(processName, new Status());
	}

	/**
	 * Method for marking completion of any process.
	 *
	 * @param processName the process name
	 */
	public void markProcessAsComplete(String processName) {
		statusMap.get(processName).setCompleted(true);
	}

	/**
	 * Method for adding exception details to any process status.
	 *
	 * @param processName the process name
	 * @param exception the exception
	 * @param module the module
	 */
	public void addException(String processName, JumbuneException exception,
			Module module) {
		Status status = statusMap.get(processName);
		status.setException(exception);
		status.setModule(module);
	}

	/**
	 * Method for checking if any process is still running.
	 *
	 * @return true, if is any process running
	 */
	public boolean isAnyProcessRunning() {
		Collection<Status> values = statusMap.values();
		boolean returnVal = false;

		if (values.size() == 0) {
			LOGGER.info(" !!! NO PROCESS FOUND !!!");
			return true;
		}

		for (Status value : values) {

			if (value.isCompleted() || value.isCompletedWithError()) {
				continue;
			} else {
				returnVal = true;
				break;
			}
		}

		return returnVal;
	}

	/**
	 * Checks if is exectution stopped.
	 *
	 * @return true, if is exectution stopped
	 */
	public boolean isExectutionStopped() {
		return stopExectution.get();
	}

	/**
	 * Stop exectution.
	 */
	public void stopExectution() {
		this.stopExectution.set(true);
	}

	/**
	 * Gets the completed.
	 *
	 * @return the completed
	 */
	public Set<Module> getCompleted() {
		return completed;
	}

	/**
	 * Method for getting json of all completed reports.
	 *
	 * @return the all completed reports
	 */
	public Map<String, String> getAllCompletedReports() {
		Map<String, String> returnMap = new HashMap<String, String>();

		// code to show completed reports
		if (completed.size() > 0) {
			synchronized (completed) {

				for (Module type : completed) {
					returnMap.putAll(reports.get(type.getEnumValue()));
				}
			}
		}

		return returnMap;
	}

	/**
	 * Method for getting report type of all completed reports.
	 *
	 * @return the completed reports
	 */
	public List<Module> getCompletedReports() {

		List<Module> completedReports = new ArrayList<Module>();
		completedReports.addAll(completed);
		return completedReports;
	}

	/**
	 * Method for getting any report based on its type.
	 *
	 * @param type the type
	 * @return the report
	 */
	public Map<String, String> getReport(Module type) {
		return reports.get(type.getEnumValue());
	}

	/**
	 * Method for marking completion status of any report.
	 *
	 * @param type the new completed
	 */
	public void setCompleted(Module type) {
		synchronized (completed) {
			completed.add(type);
		}
	}

	/**
	 * Method that populated the provided map with the process exception if any.
	 *
	 * @param map the map
	 * @return true when exception has occurred else false
	 */
	protected boolean getProcessException(Map<String, String> map) {
		Collection<Status> values = statusMap.values();
		List<String> list = new ArrayList<String>();

		for (Iterator<Status> iterator = values.iterator(); iterator.hasNext();) {
			Status value = iterator.next();
			if (value.isCompletedWithError()) {
				list.add(value.getException().toString());
			}
		}

		if (list.size() > 0) {
			String[] returnVal = list.toArray(new String[list.size()]);
			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			final String jsonString = gson.toJson(returnVal);
			map.put("EXCEPTION", jsonString);
			return true;
		}

		return false;
	}

	/**
	 * This class specifies the status of any execution process and captures any
	 * exception is process get over due to any error.
	 */
	public final class Status {

		/** The completed. */
		private boolean completed;
		
		/** The exception. */
		private JumbuneException exception;
		
		/** The module. */
		private Module module;

		/**
		 * Checks if is completed.
		 *
		 * @return true, if is completed
		 */
		public boolean isCompleted() {
			return completed;
		}

		/**
		 * Checks if is completed with error.
		 *
		 * @return true, if is completed with error
		 */
		public boolean isCompletedWithError() {
			return (exception != null) ? true : false;
		}

		/**
		 * Sets the completed.
		 *
		 * @param completed the new completed
		 */
		public void setCompleted(boolean completed) {
			this.completed = completed;
		}

		/**
		 * Gets the exception.
		 *
		 * @return the exception
		 */
		public JumbuneException getException() {
			return exception;
		}

		/**
		 * Sets the exception.
		 *
		 * @param exception the new exception
		 */
		public void setException(JumbuneException exception) {
			this.exception = exception;
		}

		/**
		 * Gets the module.
		 *
		 * @return the module
		 */
		public Module getModule() {
			return module;
		}

		/**
		 * Sets the module.
		 *
		 * @param module the new module
		 */
		public void setModule(Module module) {
			this.module = module;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "status[" + completed + "]";
		}
	}

	

}
