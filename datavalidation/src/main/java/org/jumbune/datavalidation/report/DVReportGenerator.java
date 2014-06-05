package org.jumbune.datavalidation.report;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * This class is responsible for generating the data validation report in the form of Json.
 */
public class DVReportGenerator {
	
	/** The null violations. */
	private static String nullViolations = "Null Check";
	
	/** The regex violations. */
	private static String regexViolations = "Regex";
	
	/** The data type violations. */
	private static String dataTypeViolations = "Data Type";
	
	/** The number of fields. */
	private static String numberOfFields = "Number of Fields";
	
	/** The Constant TOTAL_VIOLATIONS. */
	private static final String TOTAL_VIOLATIONS = "totalViolations";

	/**
	 * Generate data validation report.
	 *
	 * @param dvReport the dv report
	 * @return the data validation report in the form of json.
	 */
	public String generateDataValidationReport(String dvReport) {
		DataValidationDashBoardReport boardReport = new DataValidationDashBoardReport();
		JsonElement jelement = new JsonParser().parse(dvReport);
		JsonObject jobject = jelement.getAsJsonObject();
		setNullViolations(jobject, boardReport);
		setRegexViolations(jobject, boardReport);
		setDataTypeChecks(jobject, boardReport);
		setNumberOfFieldViolation(jobject, boardReport);
		JsonElement jsonElement = new Gson().toJsonTree(boardReport, DataValidationDashBoardReport.class);
		jobject.add("DVSUMMARY", jsonElement);
		return jobject.toString();

	}

	/**
	 * Sets the null violations.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the null violations that will be displayed on the dashboard.
	 */
	private void setNullViolations(final JsonObject jobject, DataValidationDashBoardReport boardReport) {
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
	private void setRegexViolations(final JsonObject jobject, DataValidationDashBoardReport boardReport) {
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
	private void setDataTypeChecks(final JsonObject jobject, DataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(dataTypeViolations);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setDataTypeViolations((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));

	}
	
	/**
	 * Sets the number of field violation.
	 *
	 * @param jobject refers to the json object
	 * @param boardReport contains the number of fields  violations that will be displayed on the dashboard.
	 */
	private void setNumberOfFieldViolation(final JsonObject jobject, DataValidationDashBoardReport boardReport) {
		JsonObject jsonObject;
		jsonObject = jobject.getAsJsonObject(numberOfFields);
		if (jsonObject == null) {
			return;
		}
		JsonElement element = jsonObject.getAsJsonObject();
		boardReport.setNumberOfFields((element.getAsJsonObject().get(TOTAL_VIOLATIONS).getAsString()));
	}
}
