package org.jumbune.datavalidation.report;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * This class is responsible for generating the data validation report in the form of Json.
 */
public class XmlDVReportGenerator {
	
	/** The null violations. */
	private static String nullViolations = "Null Check";
	
	/** The regex violations. */
	private static String regexViolations = "Regex";
	
	/** The data type violations. */
	private static String dataTypeViolations = "Data Type";
	
	/** fatal Violations */
	private static String fatalViolations = "Fatal Error";
	
	/** other Violations */
	private static String otherViolations = "Other Error";
	
	/** The Constant INDIVIDUAL_VIOLATIONS. */
	private static final String INDIVIDUAL_VIOLATIONS = "individualViolations";

	/**
	 * Generate data validation report.
	 *
	 * @param dvReport the dv report
	 * @return the data validation report in the form of json.
	 */
	public String generateDataValidationReport(String dvReport) {
		XmlDataValidationDashBoardReport boardReport = new XmlDataValidationDashBoardReport();
		JsonElement jelement = new JsonParser().parse(dvReport);
		JsonObject jobject = jelement.getAsJsonObject();
		
		setNullViolations(jobject, boardReport);
		setRegexViolations(jobject, boardReport);
		setDataTypeChecks(jobject, boardReport);
		setFatalViolations(jobject, boardReport);
		setOtherViolations(jobject, boardReport);
		
		JsonElement jsonElement = new Gson().toJsonTree(boardReport, XmlDataValidationDashBoardReport.class);
		jobject.add("DVSUMMARY", jsonElement);
		return jobject.toString();

	}

	/**
	 * Sets the null violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the null violations that will be displayed on the dashboard.
	 */
	private void setNullViolations(final JsonObject jobject, XmlDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(nullViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setNullChecks(element.getAsJsonObject().get(INDIVIDUAL_VIOLATIONS).getAsString());
	}

	/**
	 * Sets the regex violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the regex violations that will be displayed on the dashboard.
	 */
	private void setRegexViolations(final JsonObject jobject, XmlDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(regexViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setRegexViolations((element.getAsJsonObject().get(INDIVIDUAL_VIOLATIONS).getAsString()));
	}

	/**
	 * Sets the data type checks.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the data type violations that will be displayed on the dashboard.
	 */
	private void setDataTypeChecks(final JsonObject jobject, XmlDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(dataTypeViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setDataTypeViolations((element.getAsJsonObject().get(INDIVIDUAL_VIOLATIONS).getAsString()));

	}
	
	/**
	 * Sets the fatal violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the number of fatal violations that will be displayed on the dashboard.
	 */
	private void setFatalViolations(final JsonObject jobject, XmlDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(fatalViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setFatalViolations((element.getAsJsonObject().get(INDIVIDUAL_VIOLATIONS).getAsString()));
	}
	
	/**
	 * Sets the number of other violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the number of other violations that will be displayed on the dashboard.
	 */
	private void setOtherViolations(final JsonObject jobject, XmlDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(otherViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setOtherViolations((element.getAsJsonObject().get(INDIVIDUAL_VIOLATIONS).getAsString()));
	}
}
