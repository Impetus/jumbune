package org.jumbune.web.beans;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.utils.conf.AdminConfigurationUtil;

public class DataSourceCompReport {
	
	/** The page. */
	private int page;
	
	/** The records. */
	private int records;
	
	/** The total. */
	private int total;
	
	/** The rows. */
	private List<Violation> rows;
	
	public class Violation {

		private String primaryKey;
		private String transformationMethod;
		private String expected;
		private String actual;

		public Violation(String primaryKey, String transformationMethod, String expected, String actual) {			
			this.primaryKey = primaryKey;
			this.transformationMethod = transformationMethod;
			this.expected = expected;
			this.actual = actual;
		}

		public String getPrimaryKey() {
			return primaryKey;
		}

		public void setPrimaryKey(String primaryKey) {
			this.primaryKey = primaryKey;
		}

		public String getTransformationMethod() {
			return transformationMethod;
		}

		public void setTransformationMethod(String transformationMethod) {
			this.transformationMethod = transformationMethod;
		}

		public String getExpected() {
			return expected;
		}

		public void setExpected(String expected) {
			this.expected = expected;
		}

		public String getActual() {
			return actual;
		}

		public void setActual(String actual) {
			this.actual = actual;
		}
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Violation> getRows() {
		return rows;
	}

	public void setRows(List<Violation> rows) {
		this.rows = rows;
	}
	
	public void addRow(String primaryKey, String transformationMethod, String expected, String actual) {
		if (rows == null) {
			rows = new ArrayList<Violation>();
		}
		rows.add(new Violation(primaryKey, transformationMethod, expected, actual));
	}
}
