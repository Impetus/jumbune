package org.jumbune.web.servlet;

import java.util.List;


/**
 * For storing data validation reports corresponding to a violation failed in a particular file with details like page number,records and total
 * records.
 * 

 */
public class DVReport {

	/** The page. */
	private int page;
	
	/** The records. */
	private int records;
	
	/** The total. */
	private int total;
	
	/** The rows. */
	private List<DVFileReport> rows;

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets the page.
	 *
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Gets the records.
	 *
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 *
	 * @param records the records to set
	 */
	public void setRecords(int records) {
		this.records = records;
	}

	/**
	 * Gets the total.
	 *
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Sets the total.
	 *
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<DVFileReport> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the rows to set
	 */
	public void setRows(List<DVFileReport> rows) {
		this.rows = rows;
	}

}
