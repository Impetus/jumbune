package org.jumbune.common.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * This bean can be used for capturing data of all reports to be shown on HTTP request.
 */
public class HttpReportsBean extends ReportsBean {

	/** The shown. */
	private Set<Module> shown = new HashSet<Module>();
	/**
	 * Method for showing completed reports to user on every ajax call.
	 * @return Map<reportname, jsonString>
	 */
	public Map<String, String> getContentsToShow() {
		Map<String, String> returnMap = new HashMap<String, String>();
		// code to show completed reports
		Set<Module> completed = getCompleted();
		if (completed.size() > 0) {
			synchronized (completed) {
				for (Module type : completed) {
					Map<ReportName, String> report = getReport(type);
					Set<ReportName> names = report.keySet();
					for (ReportName reportName : names) {
						returnMap.put(reportName.toString(), report.get(reportName));
					}
					shown.add(type);
				}
				completed.removeAll(shown);
			}
		}
		// code to show exception
		getProcessException(returnMap);
		if (isExectutionStopped() || !isAnyProcessRunning()) {
			returnMap.put("AJAXCALL", "STOP");
		}
		return returnMap;
	}
}