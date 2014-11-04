package org.jumbune.web.utils;



// TODO: Auto-generated Javadoc
/**
 * This class is used to refer constants required for web module.
 *
 */
public interface WebConstants {

	/** The result url. */
	String RESULT_URL = "/jsp/YamlResult.jsp";
	
	/** The home url. */
	String HOME_URL = "/jsp/Home.jsp";

	/** The ajax stop msg. */
	String AJAX_STOP_MSG = "{\"AJAXCALL\":\"STOP\"}";
	
	/** The ajax call. */
	String AJAX_CALL = "AJAXCALL";
	
	/** The ajax stop. */
	String AJAX_STOP = "STOP";
	
	/** The Json File **/
	String JSON_EXTENTION = ".json";
	
	/** The saved json dir name. */
	String SAVED_JSON_DIR_NAME = "/SavedJson";
	
	/** The tmp dir path. */
	String TMP_DIR_PATH = "/tmp";
	
	/** The resource dir path. */
	String RESOURCE_DIR_PATH = "/resources";
	
	/** The sample yaml name. */
	String SAMPLE_YAML_NAME = "Sample.yaml";
	
	/** The jumbune properties yaml. */
	String JUMBUNE_PROPERTIES_YAML = "/Jumbune-UserProperties.yaml";
	
	/** The jumbune state file. */
	String JUMBUNE_STATE_FILE = "/jumbuneState.txt";

	/** The dependent jar include. */
	String DEPENDENT_JAR_INCLUDE = "resource";
	
	/** The dependent jar exclude. */
	String DEPENDENT_JAR_EXCLUDE = "exclude";
	
	/** The dependent jar master machine path. */
	String DEPENDENT_JAR_MASTER_MACHINE_PATH = "folder";
	
	/** The dependent jar split regex exp. */
	String DEPENDENT_JAR_SPLIT_REGEX_EXP = "\\\\n";
	
	/** The dependnet jar resources dir. */
	String DEPENDNET_JAR_RESOURCES_DIR = "dependentJarResource/";
	
	/** The master machine path option. */
	int MASTER_MACHINE_PATH_OPTION = 3;
	
	/** The dependent jar include dir. */
	String DEPENDENT_JAR_INCLUDE_DIR = "resource/";
	
	/** The dependent jar exclude dir. */
	String DEPENDENT_JAR_EXCLUDE_DIR = "exclude/";
	
	/** The dependent jar master dir. */
	String DEPENDENT_JAR_MASTER_DIR = "folders/";
	
	/** The dependent jar include resource. */
	String DEPENDENT_JAR_INCLUDE_RESOURCE = "files";
	
	/** The dependent jar exclude resource. */
	String DEPENDENT_JAR_EXCLUDE_RESOURCE = "excludes";
	
	/** The dependent jar folder resource. */
	String DEPENDENT_JAR_FOLDER_RESOURCE = "folders";

	/** The file name. */
	String FILE_NAME = "fileName";
	
	/** The dv type. */
	String DV_TYPE = "dvType";
	
	/** The page number. */
	String PAGE_NUMBER = "page";
	
	/** The rows. */
	String ROWS = "rows";
	
	/** The copy command. */
	String COPY_COMMAND = "cp ";
	
	/** The lib directory. */
	String LIB_DIRECTORY = "/lib/";
	
	/** The xls ext. */
	String XLS_EXT = ".xls";
	
	/** The reports bean. */
	String REPORTS_BEAN = "ReportsBean";
	
	/** The report dir. */
	String REPORT_DIR = "/ExcelReports";
	
	/** The profiling property file. */
	String PROFILING_PROPERTY_FILE="jumbune-profiling.properties";
	
	/** The profiling system json. */
	String PROFILING_SYSTEM_JSON="system.stats.json";
}
