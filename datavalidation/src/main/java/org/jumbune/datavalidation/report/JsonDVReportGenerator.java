package org.jumbune.datavalidation.report;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * The Class JsonDVReportGenerator.
 */
public class JsonDVReportGenerator {
	
	/** The null violations. */
	private static String nullViolations = "NullKey";
	
	/** The regex violations. */
	private static String regexViolations = "RegexKey";
	
	/** The data type violations. */
	private static String dataTypeViolations = "DataKey";
	
	/**  schema Violations. */
	private static String schmeaViolations = "JsonSchemaKey";
	
	/**  missing Violations. */
	private static String missingViolations = "MissingKey";
	
	/** The Constant TOTAL_VIOLATIONS. */
	private static final String TOTAL_VIOLATIONS ="totalKeyViolation";
	
	
	/**
	 * Generate data validation report.
	 *
	 * @param dvReport the dv report
	 * @return the string
	 */
	public String generateDataValidationReport(String dvReport) {
		JsonDataValidationDashBoardReport boardReport = new JsonDataValidationDashBoardReport();
		JsonElement jelement = new JsonParser().parse(dvReport);
		JsonObject jobject = jelement.getAsJsonObject();
		
		setNullViolations(jobject, boardReport);
		setRegexViolations(jobject, boardReport);
		setDataTypeChecks(jobject, boardReport);
		setSchemaViolations(jobject, boardReport);
		setMissingViolations(jobject, boardReport);
		
		JsonElement jsonElement = new Gson().toJsonTree(boardReport, JsonDataValidationDashBoardReport.class);
		jobject.add("DVSUMMARY", jsonElement);
		return jobject.toString();

	}
	
	/**
	 * Sets the null violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the null violations that will be displayed on the dashboard.
	 */
	private void setNullViolations(final JsonObject jobject, JsonDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(nullViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setNullChecks(element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString());
	}

	/**
	 * Sets the regex violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the regex violations that will be displayed on the dashboard.
	 */
	private void setRegexViolations(final JsonObject jobject, JsonDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(regexViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setRegexViolations((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));
	}

	/**
	 * Sets the data type checks.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the data type violations that will be displayed on the dashboard.
	 */
	private void setDataTypeChecks(final JsonObject jobject, JsonDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(dataTypeViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setDataTypeViolations((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));

	}
	
	/**
	 * Sets the fatal violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the number of fatal violations that will be displayed on the dashboard.
	 */
	private void setSchemaViolations(final JsonObject jobject, JsonDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(schmeaViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setSchemaViolations((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));
	}
	
	/**
	 * Sets the number of other violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the number of other violations that will be displayed on the dashboard.
	 */
	private void setMissingViolations(final JsonObject jobject, JsonDataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(missingViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setMissingViolations((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));
	}

}
