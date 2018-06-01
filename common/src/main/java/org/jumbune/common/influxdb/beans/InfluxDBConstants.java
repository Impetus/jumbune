package org.jumbune.common.influxdb.beans;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public interface InfluxDBConstants {

	// Constants used in InfluxDataReader class

	char T = 'T';
	char S = 's';
	char M = 'm';
	char H = 'h';
	char D = 'd';
	char COLON = ':';
	char SPACE = ' ';
	char COMMA = ',';
	char EQUAL = '=';
	char QUOTE = '\'';
	char OPENING_BRACKET = '(';
	char CLOSING_BRACKET = ')';

	String Z = "Z";
	String AND = "and";
	String REGEX1 = "\\\\ ";
	String REGEX2 = "\\\\,";
	String REGEX3 = "\\\\=";
	String REGEX4 = "=";
	String TIME = "time";
	String HTTP = "http://";
	String WHERE = "where";
	String NEW_LINE = "\n";
	String QUERY_DB = "/query?db=";
	String SLASH_QUERY = "/query";
	String COMMA_space = ", ";
	String SELECT_space = "select ";
	String EMPTY_STRING = "";
	String space_FILL_0 = " fill(0)";
	String space_GROUP_BY = " group by ";
	String AND_Q_EQUAL_TO = "&q=";
	String AND_DB_EQUAL_TO = "&db=";
	String COMMA_QUOTATION = ", \"";
	String CREATE_DATABASE = "q=CREATE DATABASE \"";
	String YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm";
	String SELECT_STAR_space = "select * ";
	String space_GROUP_BY_TIME = " group by time(";
	String BACKSLASH_QUOTATION = "\"";
	String CLOSING_BRACKET_space = ") ";
	String AND_PRECISION_EQUAL_TO = "&precision=";
	String INFLUX_DB_CONF_IS_NULL = "InfluxDBConf is null";
	String TABLE_NAME_NOT_SPECIFIED = "Table name not specified";
	String FROM_BACKSLASH_QUOTATION = "from \"";
	String YYYY_MM_DD_space_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	String space_AND_TIME_LESS_THAN_space = " and time < ";
	String space_WHERE_TIME_LESS_THAN_space = "where time < ";
	String space_WHERE_TIME_GREATER_THAN_space = " where time > ";
	String ERROR_WHILE_FETCHING_DATA_FROM_INFLUX_DB = "Error while fetching data from influxDB";
	String space_WHERE_TIME_GREATER_THAN_NOW_MINUS_space = " where time > now() - ";

	// Constants used in InfluxDataWriter class

	String WRITE_U = "/write?u=";
	String AND_U_EQUAL_TO = "&u=";
	String AND_P_EQUAL_TO = "&p=";
	String NEWlINE_JSON_EQUAL_TO = "\njson=";
	String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
	String SENDING_POST_REQUEST_TO_INFLUX_DB_URL = "Sending post request to influxDB...\nURL=";
	String INSUFFICIENT_INFORMATION_TO_WRITE_DATA = "Insufficient information to write data";
	String ERROR_WHILE_SENDING_POST_REQUEST_TO_INFLUXDB = "Error while sending post request to influxdb, ";
	
	String NAMENODE = "clusterWide.nameNode.";
	String RESOURCE_MANAGER = "clusterWide.resourceManager.";
	String JOB_TRACKER = "clusterWide.jobTracker.";
	String WORKERJMX_DATANODE = "workerJMXInfo.dataNode.";
	String WORKERJMX_NODE_MANAGER = "workerJMXInfo.nodeManager.";
	String HADOOPJMX_DATANODE = "hadoopJMX.dataNode.";
	String HADOOPJMX_TASKTRACKER = "hadoopJMX.taskTracker.";
	String SYSTEMSTATS_CPU = "systemStats.cpu.";
	String SYSTEMSTATS_MEMORY = "systemStats.memory.";
	String SYSTEMSTATS_OS = "systemStats.os.";
	
	Type resultSetType = new TypeToken<ResultSet>() {
	}.getType();
}
