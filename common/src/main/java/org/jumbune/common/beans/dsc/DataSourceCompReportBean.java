package org.jumbune.common.beans.dsc;

import java.util.HashMap;
import java.util.Map;

public class DataSourceCompReportBean {

	private Map<String, TransformationViolation> transformationViolationMap;

	private InvalidRows invalidRows;

	private Map<String, Long> noOfFieldsViolationMap;

	public class TransformationViolation {

		private DataSourceCompValidation validation;

		/**
		 * Key is file name and value is number of violations
		 */
		private Map<String, Long> violations;
		
		public TransformationViolation(DataSourceCompValidation validation) {
			this.validation = validation;
		}

		public TransformationViolation(DataSourceCompValidation validation, Map<String, Long> violations) {
			this.validation = validation;
			this.violations = violations;
		}
		
		public void addViolation(String fileName, Long counter) {
			if (violations == null) {
				violations = new HashMap<String, Long>();
			}
			violations.put(fileName, counter);
		}

		public DataSourceCompValidation getValidation() {
			return validation;
		}

		public void setValidation(DataSourceCompValidation validation) {
			this.validation = validation;
		}

		public Map<String, Long> getViolations() {
			return violations;
		}

		public void setViolations(Map<String, Long> violations) {
			this.violations = violations;
		}
	}

	public class InvalidRows {

		private Long transformationViolation = 0l;
		private Long noOfFieldsViolation = 0l;

		public Long getTransformationViolation() {
			return transformationViolation;
		}

		public void setTransformationViolation(Long transformationViolation) {
			this.transformationViolation = transformationViolation;
		}

		public Long getNoOfFieldsViolation() {
			return noOfFieldsViolation;
		}

		public void setNoOfFieldsViolation(Long noOfFieldsViolation) {
			this.noOfFieldsViolation = noOfFieldsViolation;
		}
	}

	public void addTransformationViolation(DataSourceCompValidation validation, String validationCode, String fileName,
			Long counter) {
		if (transformationViolationMap == null) {
			transformationViolationMap = new HashMap<String, TransformationViolation>();
		}
		
		TransformationViolation tv = transformationViolationMap.get(validationCode);
		if (tv == null) {
			tv = new TransformationViolation(validation);
			transformationViolationMap.put(validationCode, tv);
		}
		tv.addViolation(fileName, counter);
	}

	public void addNumberOfFieldsViolation(String fileName, Long count) {
		if (noOfFieldsViolationMap == null) {
			noOfFieldsViolationMap = new HashMap<String, Long>();
		}
		noOfFieldsViolationMap.put(fileName, count);
	}

	public void incrementInvalidRowsNoOfFieldsViolation(Long i) {
		if (invalidRows == null) {
			invalidRows = new InvalidRows();
		}
		invalidRows.setNoOfFieldsViolation(invalidRows.getNoOfFieldsViolation() + i);
	}

	public void incrementInvalidRowsTransformationViolation(Long i) {
		if (invalidRows == null) {
			invalidRows = new InvalidRows();
		}
		invalidRows.setTransformationViolation(invalidRows.getTransformationViolation() + i);
	}

	public InvalidRows getInvalidRows() {
		return invalidRows;
	}

	public void setInvalidRows(InvalidRows invalidRows) {
		this.invalidRows = invalidRows;
	}

	public Map<String, Long> getNoOfFieldsViolationMap() {
		return noOfFieldsViolationMap;
	}

	public void setNoOfFieldsViolationMap(Map<String, Long> noOfFieldsViolation) {
		this.noOfFieldsViolationMap = noOfFieldsViolation;
	}

}
