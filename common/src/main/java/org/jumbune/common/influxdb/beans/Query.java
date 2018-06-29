package org.jumbune.common.influxdb.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.jumbune.common.influxdb.beans.InfluxDBConstants.*;

public class Query {

	private String tableName;

	private List<String> tables;

	private List<String> columns;

	private String duration;

	private String rangeFrom;

	private String rangeTo;

	private String aggregateFunction;
	
	private boolean doNotAddGroupByTime = false;

	private static final String dateTimeFormatForRange = YYYY_MM_DD_HH_MM;

	private static final String NOT_EQUAL = "!=";

	/**
	 * It should be double the time of StatsManager.STATS_WRITING_TIME_INTERVAL
	 */
	public static int minimumTimeBetweenTwoRecords = 60;

	private int noOfRecords = 200;
	
	private String groupByTime;
	
	private List<String> groupByColumns = null;
	
	// key is tag name and value is tag value
	private Map<String, String> tags = null;
	
	// tags that we don't want to be included
	private Map<String, String> inequalities = null;
	
	private String customQuery = null;
	
	public static void updateMinimumTime(long time) {
		if (time / 1000 > minimumTimeBetweenTwoRecords) {
			minimumTimeBetweenTwoRecords = (int) (time / 1000 ) + 20;
		}
	}
	
	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * @param columns
	 *            Name of columns
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * Add Column to fetch
	 * 
	 * @param column
	 */
	public void addColumn(String column) {
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		columns.add(column);
	}

	/**
	 * Duration. eg. "3h" = fetch records of past 3 hour, "1m" = 1 minute, "1d"
	 * = 1 day, s = seconds (not recommended) Supported formats are (d, h, m, s)
	 * 
	 * @param from
	 *            the from to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setRangeFrom(String rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public void setRangeTo(String rangeTo) {
		this.rangeTo = rangeTo;
	}

	/**
	 * @param aggregateFunction2
	 *            the aggregateFunction to set (min, max, mean or count)
	 */
	public void setAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	/**
	 * @param rangeFrom
	 *            the rangeFrom to set
	 */
	public void setRange(String rangeFrom, String rangeTo) {

		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}

	public void setDoNotAddGroupByTime(boolean doNotAddGroupByTime) {
		this.doNotAddGroupByTime = doNotAddGroupByTime;
	}
	
	public void setNumberOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	private List<String> getColumnsWithDoubleQuotes() {
		List<String> list = new ArrayList<String>();
		for (String node : columns) {
			list.add(BACKSLASH_QUOTATION + node + BACKSLASH_QUOTATION);
		}
		return list;
	}
	
	private boolean isNullOrEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}
	
	private boolean isNullOrEmpty(List<String> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	// create query
	private StringBuffer getQuery() throws ParseException {
		
		if (getCustomQuery() != null) {
			return new StringBuffer(getCustomQuery());
		}
		
		long from = 0, to = 0;

		if (aggregateFunction == null) {
			aggregateFunction = EMPTY_STRING;
		}

		StringBuffer query = new StringBuffer();
		if (columns != null && columns.size() > 0) {
			List<String> formattedColumns = getColumnsWithDoubleQuotes();
			if (isNullOrEmpty(rangeFrom) && !isNullOrEmpty(rangeTo)) {
				aggregateFunction = EMPTY_STRING;
			}
			// select mean("column1")
			query.append(SELECT_space).append(aggregateFunction).append(OPENING_BRACKET)
					.append(formattedColumns.get(0)).append(CLOSING_BRACKET_space);

			// , mean("column2") , mean("column3")
			for (int i = 1; i < formattedColumns.size(); i++) {
				query.append(COMMA_space).append(aggregateFunction).append(OPENING_BRACKET)
						.append(formattedColumns.get(i)).append(CLOSING_BRACKET_space);
			}
		} else {
			// select *
			query.append(SELECT_STAR_space);
			// setting aggregate function to null so that it will not add group
			// by time()
			aggregateFunction = null;
		}

		// from "tableName"
		if (!isNullOrEmpty(tableName)) {
			query.append(FROM_BACKSLASH_QUOTATION).append(tableName).append(BACKSLASH_QUOTATION);
		} else {
			query.append(FROM_BACKSLASH_QUOTATION).append(tables.get(0))
					.append(BACKSLASH_QUOTATION);

			for (int i = 1; i < tables.size(); i++) {
				query.append(COMMA_QUOTATION).append(tables.get(i)).append(BACKSLASH_QUOTATION);
			}
		}
		
		boolean whereAlreadyAdded = false;
		
		if (!isNullOrEmpty(rangeFrom) && !isNullOrEmpty(rangeTo)) {
			whereAlreadyAdded = true;
			// where time > 123456s and time < 123567s
			from = getFormatted(rangeFrom);
			to = getFormatted(rangeTo);
			query.append(space_WHERE_TIME_GREATER_THAN_space).append(from).append(S)
					.append(space_AND_TIME_LESS_THAN_space).append(to).append(S);
		} else if (!isNullOrEmpty(rangeFrom)) {
			whereAlreadyAdded = true;
			from = getFormatted(rangeFrom);
			to = System.currentTimeMillis() / 1000;
			query.append(space_WHERE_TIME_GREATER_THAN_space).append(from).append(S);
		} else if (!isNullOrEmpty(rangeTo)) {
			whereAlreadyAdded = true;
			to = getFormatted(rangeTo);
			query.append(space_WHERE_TIME_LESS_THAN_space).append(to).append(S);
		} else if (!isNullOrEmpty(duration)) {
			whereAlreadyAdded = true;
			// where time > now() - 1h
			query.append(space_WHERE_TIME_GREATER_THAN_NOW_MINUS_space).append(duration);
		}
		
		// where jobName = 'PI' (ie. where tag_name = 'tag_value' and tag_name1 = 'tagValue2')
		if (tags != null && !tags.isEmpty()) {
			
			for (Entry<String, String> e : tags.entrySet()) {
				if (!whereAlreadyAdded) {
					query.append(SPACE).append(WHERE);
					whereAlreadyAdded = true;
				} else {
					query.append(SPACE).append(AND);
				}
				query.append(SPACE).append(e.getKey()).append(SPACE).append(EQUAL).append(SPACE).append(QUOTE).append(e.getValue()).append(QUOTE);
			}
		}
		
		if (inequalities != null && !inequalities.isEmpty()) {
			for (Entry<String, String> e : inequalities.entrySet()) {
				if (!whereAlreadyAdded) {
					query.append(SPACE).append(WHERE);
					whereAlreadyAdded = true;
				} else {
					query.append(SPACE).append(AND);
				}
				query.append(SPACE).append(e.getKey()).append(SPACE).append(NOT_EQUAL).append(SPACE).append(QUOTE).append(e.getValue()).append(QUOTE);
			}
		}
		
		boolean isGroupByAlreadyAdded = false;
		String temp;
		// group by time(5m)
		if (!doNotAddGroupByTime && !isNullOrEmpty(aggregateFunction)
				&& (!isNullOrEmpty(duration) || !isNullOrEmpty(rangeFrom))) {
			isGroupByAlreadyAdded = true;
			query.append(space_GROUP_BY_TIME);
			if (!isNullOrEmpty(groupByTime)) {
				query.append(groupByTime);
			} else if (!isNullOrEmpty(rangeFrom)) {
				temp = (to - from) + "s";
				query.append(getGroupByTime(temp));
			} else {
				query.append(getGroupByTime(duration));
			}
			query.append(CLOSING_BRACKET);
		}
		
		if (!isNullOrEmpty(groupByColumns)) {
			if (isGroupByAlreadyAdded) {
				query.append(COMMA);
			} else {
				query.append(space_GROUP_BY);
			}
			Iterator<String> it = groupByColumns.iterator();
			query.append(BACKSLASH_QUOTATION).append(it.next()).append(BACKSLASH_QUOTATION);
			while (it.hasNext()) {
				query.append(COMMA_QUOTATION).append(it.next()).append(BACKSLASH_QUOTATION);
			}
		}

		// fill(0)
		query.append(space_FILL_0);

		return query;
	}

	// get group by time(durationFactor)
	private String getGroupByTime(String duration) {
		char format = duration.charAt(duration.length() - 1);
		Double factor = Double.parseDouble((duration.substring(0, duration.length() - 1)));
		int multiplyBy = 1;
		if (format == D) {
			multiplyBy = 24 * 60 * 60;
		} else if (format == H) {
			multiplyBy = 60 * 60;
		} else if (format == M) {
			multiplyBy = 60;
		}
		factor = (factor * multiplyBy) / noOfRecords;
		int temp = factor.intValue();
		if (temp < minimumTimeBetweenTwoRecords) {
			temp = minimumTimeBetweenTwoRecords;
		}
		return String.valueOf(temp) + S;
	}

	// convert date into particular format
	private long getFormatted(String original) throws ParseException {
		DateFormat format = new SimpleDateFormat(dateTimeFormatForRange);
		Date date = format.parse(original);
		long time = date.getTime();
		return time / 1000;
	}
	
	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	
	public List<String> getTables() {
		return tables;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
	
	public Map<String, String> getTags() {
		return tags;
	}
	
	public void addTag(String tagKey, String tagValue) {
		if (tags == null) {
			tags = new HashMap<String, String>(4);
		}
		tags.put(tagKey, tagValue);
	}
	
	public String getGroupByTime() {
		return groupByTime;
	}

	public void setGroupByTime(String groupByTime) {
		this.groupByTime = groupByTime;
	}

	public List<String> getGroupByColumns() {
		return groupByColumns;
	}

	public void setGroupByColumns(List<String> columnsList) {
		this.groupByColumns = columnsList;
	}
	
	public void addGroupByColumn(String column) {
		if (groupByColumns == null) {
			groupByColumns = new ArrayList<String>(1);
		}
		groupByColumns.add(column);
	}

	@Override
	public String toString() {
		try {
			return getQuery().toString();
		} catch (ParseException e) {
			return "";
		}
	}

	public String getCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(String customQuery) {
		this.customQuery = customQuery;
	}

	public void setInequalities(Map<String, String> inequalities) {
		this.inequalities = inequalities;
	}
	
	public void addInequalities(String tagName, String tagValues) {
		if (this.inequalities == null) {
			this.inequalities = new HashMap<String, String>(2);
		}
		this.inequalities.put(tagName, tagValues);
	}
}

