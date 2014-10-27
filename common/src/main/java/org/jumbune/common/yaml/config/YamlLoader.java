package org.jumbune.common.yaml.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.DoNotInstrument;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.LogSummaryLocation;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ProfilingParam;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.Validation;
import org.jumbune.common.beans.ReportsBean.ReportName;
import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.Constants;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.utils.YamlUtil;
import org.jumbune.utils.beans.LogLevel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.jcraft.jsch.JSchException;



/**
 * This class loads the yaml.
 */
public class YamlLoader implements Loader{
	
	/** The Constant ECHO_HADOOP_HOME. */
	private  static final  String ECHO_HADOOP_HOME = "echo $HADOOP_HOME \n \n";
	
	/** The Constant logger. */
	private static final Logger LOGGER = LogManager.getLogger(YamlLoader.class);
	
	/** The Constant FORWARD_SLASH. */
	private static final String FORWARD_SLASH = "/";
	
	/** The Constant SYSTEM_STATS_DIR. */
	private static final String SYSTEM_STATS_DIR = "SystemStats";
	/** The Constant MKDIR_P_CMD. */
	private static final String MKDIR_P_CMD = "mkdir -p ";
	
	/** The sampled data output path. */
	private static String sampledDataOutputPath;
	
	/** The u conf. */
	private YamlConfig uConf;
	


	/** This must be set explicitly. */
	private static String jHome;

	/** The hadoop home. */
	private static String hadoopHome;

	/** The Constant CONSOLELOGGER. */
	public static final Logger CONSOLELOGGER = LogManager.getLogger("EventLogger");

	static {
		// locating jumbune home form env properties
		setjHome(null);
		setHadoopHome(null);
	}

	/**
	 * Instantiates a new yaml loader.
	 */
	protected YamlLoader() {
		LOGGER.debug("JUMBUNE_HOME is set to : " + jHome);
		if (!YamlUtil.validateFileSystemLocation(jHome)) {
			LOGGER.error("Environment variable \'JUMBUNE_HOME\' is either not set or is in incorrect format");
			System.exit(1);
		}
	}

	/**
	 * Instantiates a new yaml loader.
	 *
	 * @param config the config
	 */
	public YamlLoader(Config config) {
		this();
		this.uConf = (YamlConfig)config;
	}

	/**
	 * Instantiates a new yaml loader.
	 *
	 * @param is the is
	 */
	public YamlLoader(InputStream is) {
		this();
		this.uConf = (YamlConfig) loadYAML(is, Constants.DEFAULT_USER_CONFIGURATION, YamlConfig.class);
	}

	/**
	 * Gets the yaml configuration.
	 *
	 * @return the yaml configuration
	 */
	public Config getYamlConfiguration() {
		return uConf;
	}

	/**
	 * Gets the url.
	 *
	 * @param config the config
	 * @return the url
	 * @throws JumbuneYamlException the hTF yaml exception
	 */
	private URL getURL(String config) {
		URL url;
		try {
			url = new URL(config);
		} catch (MalformedURLException e) {
			ClassLoader loader = YamlLoader.class.getClassLoader();
			url = loader.getResource(config);
			if (url == null) {
				throw new IllegalArgumentException("Yaml file is not found.");
			}
		}
		return url;
	}

	/**
	 * Load yaml.
	 *
	 * @param input the input
	 * @param config the config
	 * @param configClass the config class
	 * @return the object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object loadYAML(final InputStream input, String config, Class configClass) {
		try {
			// Loading YAML from default location this will help developers when
			// using YAMLLoader from command prompt
			InputStream inputStream = input;
			if (inputStream == null) {
				URL url = getURL(config);
				LOGGER.debug(" InputStream is null. Loading file using config : " + config);
				LOGGER.debug(" loading yaml  " + url);
				inputStream = url.openStream();
			}
			LOGGER.debug("Loading settings from " + inputStream.available());
			Constructor constructor = new Constructor(configClass);

			TypeDescription desc = new TypeDescription(configClass);
			constructor.addTypeDescription(desc);
			Yaml yaml = new Yaml(constructor);
			return yaml.load(inputStream);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return null;

	}

	

	/**
	 * Sets the job definition list.
	 *
	 * @param jobDefList the new job definition list
	 */
	public void setJobDefinitionList(List<JobDefinition> jobDefList) {
		uConf.setJobs(jobDefList);
	}

	/**
	 * Gets the job definition list.
	 *
	 * @return the job definition list
	 */
	public List<JobDefinition> getJobDefinitionList() {
		return uConf.getJobs();
	}

	/**
	 * Gets the instrumentation.
	 *
	 * @return the instrumentation
	 * @throws IllegalArgument Exception
	 */
	public DebuggerConf getInstrumentation() {
		DebuggerConf id = uConf.getDebuggerConf();
		if (id == null) {
			throw new IllegalArgumentException("Yaml property instrumentaion is not defined in given FrameworkProperty file.");
		}

		return id;
	}

	/**
	 * Validate log level.
	 *
	 * @throws IllegalArgument Exception
	 */
	public void validateLogLevel() {
		DebuggerConf id = getInstrumentation();
		Map<String, LogLevel> map = id.getLogLevel();
		if (map == null) {
			throw new IllegalArgumentException("Yaml property instrumentaion is not defined in given FrameworkProperty file.");
		}
	}

	/**
	 * Gets the log level.
	 *
	 * @param instrumentType the instrument type
	 * @return the log level
	 */
	public String getLogLevel(String instrumentType) {
		DebuggerConf id = null;
		Map<String, LogLevel> map = null;
		LogLevel level = null;

		try {
			id = getInstrumentation();

		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		}
		map = id.getLogLevel();
		if (map == null) {
			return null;
		}
		level = map.get(instrumentType);
		return (level != null && Constants.LOG_LEVEL_TRUE.equalsIgnoreCase(level.toString())) ? Constants.LOG_LEVEL_INFO.toLowerCase() : null;
	}

	/**
	 * Checks if is instrument enabled.
	 *
	 * @param instrumentType the instrument type
	 * @return true, if is instrument enabled
	 */
	public boolean isInstrumentEnabled(String instrumentType) {
		boolean isInstrumentedEnabled = false;

		isInstrumentedEnabled = getLogLevel(instrumentType) != null ? true : false;

		return isInstrumentedEnabled;
	}



	/**
	 * Must not use this one. Prefer getHadoopHome(YamlLoader yamlLoader)
	 *
	 * @return the hadoop home
	 */
	@Deprecated
	public static final String getHadoopHome() {
		// Just check if this the first time fetching jHome and it not set try
		// to set it.
		if (hadoopHome == null) {
			setHadoopHome(null);
		}
		LOGGER.debug("HADOOP_HOME is set to ---" + hadoopHome);
		return hadoopHome;
	}

	/**
	 * Gets the remoter.
	 *
	 * @param loader the loader
	 * @param receiveDiretory the receive diretory
	 * @return the remoter
	 */
	private Remoter getRemoter(Loader loader, final String receiveDiretory) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String receiveDirectory = receiveDiretory;
		Master master = yamlLoader.getMasterInfo();
		String masterHost = master.getHost();
		int agentPort = Integer.valueOf(master.getAgentPort());
		if (receiveDirectory == null || receiveDirectory.trim().equals("")){
			receiveDirectory = YamlLoader.getjHome();}
		return new Remoter(masterHost, agentPort, yamlLoader.getJumbuneJobName());

	}

	/**
	 * Gets the hadoop home.
	 *
	 * @param loader the loader
	 * @return the hadoop home
	 */
	public String getHadoopHome(Loader loader) {

		Remoter remoter = getRemoter(loader, "");
		
		String hadoopHomeTemp;
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		YamlLoader yamlLoader = (YamlLoader)loader;
		builder.addCommand(ECHO_HADOOP_HOME, false, null).populate(yamlLoader.getYamlConfiguration(), null);
		hadoopHomeTemp = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return hadoopHomeTemp;
	}

	/**
	 * Gets the hadoop command.
	 *
	 * @return the hadoop command
	 */
	public static String getHadoopCommand() {
		return Constants.H_COMMAND;
	}

	/**
	 * Gets the hadoop command type.
	 *
	 * @return the hadoop command type
	 */
	public static String getHadoopCommandType() {
		return Constants.H_COMMAND_TYPE;
	}

	/**
	 * Gets the input file.
	 *
	 * @return the input file
	 */
	public final String getInputFile() {
		return uConf.getInputFile();
	}

	/**
	 * Gets the profiled output file.
	 *
	 * @return the profiled output file
	 */
	public final String getProfiledOutputFile() {

		return getProfiledOutputFile(uConf.getInputFile());
	}

	/**
	 * Gets the instrument output file.
	 *
	 * @return the instrument output file
	 */
	public String getInstrumentOutputFile() {
		return getInstrumentOutputFile(uConf.getInputFile());
	}

	/**
	 * Gets the profiling max heap sample count.
	 *
	 * @return the profiling max heap sample count
	 */
	public final int getProfilingMaxHeapSampleCount() {
		return Constants.PROFILING_MAX_HEAP_SAMPLE_COUNT;
	}

	/**
	 * Gets the profiling max cpu sample count.
	 *
	 * @return the profiling max cpu sample count
	 */
	public final int getProfilingMaxCPUSampleCount() {
		return Constants.PROFILING_MAX_CPU_SAMPLE_COUNT;
	}

	/**
	 * Gets the do not instrument.
	 *
	 * @return the do not instrument
	 */
	public DoNotInstrument getDoNotInstrument() {
		return uConf.getDoNotInstrument();
	}

	/**
	 * Gets the include anyways list.
	 *
	 * @return the include anyways list
	 */
	public List<String> getIncludeAnywaysList() {
		if (uConf.getDoNotInstrument() != null) {
			return uConf.getDoNotInstrument().getIncludeAnyways();
		}
		return null;
	}

	/**
	 * Gets the complete do not instrument list.
	 *
	 * @return the complete do not instrument list
	 */
	public List<String> getCompleteDoNotInstrumentList() {
		List<String> completeListOfExclusions = new ArrayList<String>();

		if (uConf.getDoNotInstrument() != null && uConf.getDoNotInstrument().getPackages() != null) {
			completeListOfExclusions.addAll(uConf.getDoNotInstrument().getPackages());
		}

		if (uConf.getDoNotInstrument() != null && uConf.getDoNotInstrument().getClasses() != null) {
			completeListOfExclusions.addAll(uConf.getDoNotInstrument().getClasses());
		}
		return completeListOfExclusions;
	}

	/**
	 * Gets the mapper super classes.
	 *
	 * @return the mapper super classes
	 */
	public String[] getMapperSuperClasses() {
		return uConf.getMapperSuperClasses();
	}

	/**
	 * Gets the reducer super classes.
	 *
	 * @return the reducer super classes
	 */
	public String[] getReducerSuperClasses() {
		return uConf.getReducerSuperClasses();
	}

	/**
	 * Gets the master jmx file location.
	 *
	 * @return the master jmx file location
	 */
	public String getMasterJmxFileLocation() {
		return jHome + Constants.JMX_FILE_LOC;
	}

	/**
	 * Gets the slave jmx file location.
	 *
	 * @return the slave jmx file location
	 */
	public String getSlaveJmxFileLocation() {
		return uConf.getsJumbuneHome() + Constants.JMX_FILE_LOC;
	}

	/**
	 * Gets the profiling params.
	 *
	 * @return the profiling params
	 */
	public ProfilingParam getProfilingParams() {
		return uConf.getProfilingParams();
	}

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public List<Validation> getRegex() {
		List<Validation> regex = uConf.getRegexValidations();
		if (regex == null) {
			LOGGER.error("Fatal error: regex are not defined in yaml");
			
			System.exit(1);
		}

		return regex;
	}

	/**
	 * <p>
	 * This method provides regex for map/reduce key for the given class
	 * </p>.
	 *
	 * @param strClassName Class name
	 * @return String Regex for key
	 */
	public String getMapReduceKeyRegex(String strClassName) {
		Validation validation = Validation.getValidation(getRegex(), strClassName);
		if (validation != null) {
			return validation.getKey();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides regex for map/reduce value for the given class
	 * </p>.
	 *
	 * @param strClassName Class name
	 * @return String Regex for value
	 */
	public String getMapReduceValueRegex(String strClassName) {
		Validation validation = Validation.getValidation(getRegex(), strClassName);
		if (validation != null) {
			return validation.getValue();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides validation for map/reduce key for the given class
	 * </p>.
	 *
	 * @param strClassName class name
	 * @return String validation class
	 */
	public String getMapReduceKeyValidator(String strClassName) {
		Validation validation = Validation.getValidation(getUserValidations(), strClassName);
		if (validation != null) {
			return validation.getKey();
		}
		return null;
	}

	/**
	 * <p>
	 * This method provides validation for map/reduce value for the given class
	 * </p>.
	 *
	 * @param strClassName class name
	 * @return String validation class
	 */
	public String getMapReduceValueValidator(String strClassName) {
		Validation validation = Validation.getValidation(getUserValidations(), strClassName);
		if (validation != null) {
			return validation.getValue();
		}
		return null;
	}

	/**
	 * Gets the shell user report location.
	 *
	 * @return the shell user report location
	 */
	public String getShellUserReportLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC;
	}

	/**
	 * Gets the profiled jar location.
	 *
	 * @return the profiled jar location
	 */
	public final String getProfiledJarLocation() {

		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.PROFILED_JAR_LOC;
	}

	/**
	 * Gets the pure jar location.
	 *
	 * @return the pure jar location
	 */
	public final String getPureJarLocation() {

		return getJobJarLoc() + uConf.getFormattedJumbuneJobName();
	}

	/**
	 * Gets the instrumented jar location.
	 *
	 * @return the instrumented jar location
	 */
	public final String getInstrumentedJarLocation() {

		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.INSTRUMENTED_JAR_LOC;
	}

	/** The log definition. */
	private LogConsolidationInfo logDefinition;

	/** The dv definition. */
	private LogConsolidationInfo dvDefinition;

	/**
	 * Gets the dV definition.
	 *
	 * @return the dV definition
	 */
	public LogConsolidationInfo getDVDefinition() {

		if (dvDefinition == null) {

			// getting master
			Master master = getMasterInfo();

			// setting consolidated log location
			master.setLocation(getMasterConsolidatedDVLocation());

			// getting slaves
			List<Slave> slaves = getSlavesInfo();

			// setting slaves log location
			for (Slave slave : slaves) {
				slave.setLocation(getSlaveDVLocation());
			}

		

			dvDefinition = new LogConsolidationInfo();
			dvDefinition.setMaster(master);
			dvDefinition.setSlaves(slaves);
		}

		return dvDefinition;
	}

	/**
	 * Gets the log definition.
	 *
	 * @return the log definition
	 */
	public LogConsolidationInfo getLogDefinition() {

		// getting master
		Master master = getMasterInfo();

		// setting consolidated log location
		master.setLocation(getMasterConsolidatedLogLocation());

		// getting slaves
		List<Slave> slaves = getSlavesInfo();

		// setting slaves log location
		for (Slave slave : slaves) {
			slave.setLocation(getSlaveLogLocation());
		}
		LogSummaryLocation lsl = new LogSummaryLocation();
		lsl.setPureJarCounterLocation(getPureJarCounterLocation());
		lsl.setPureJarProfilingCountersLocation(getPureJarProfilingCountersLocation());
		lsl.setInstrumentedJarCountersLocation(getInstrumentedJarCountersLocation());
		lsl.setLogsConsolidatedSummaryLocation(getLogsConsolidatedSummaryLocation());
		lsl.setProfilingFilesLocation(getProfilingFilesLocation());

		logDefinition = new LogConsolidationInfo();
		logDefinition.setMaster(master);
		logDefinition.setSlaves(slaves);
		logDefinition.setLogSummaryLocation(lsl);

		return logDefinition;
	}

	/**
	 * Gets the sys resource file consolidation.
	 *
	 * @return the sys resource file consolidation
	 */
	public LogConsolidationInfo getSysResourceFileConsolidation() {

		// getting master
		Master master = getMasterInfo();

		// setting consolidated log location
		master.setLocation(jHome + SYSTEM_STATS_DIR);

		// getting slaves
		List<Slave> slaves = getSlavesInfo();

		// setting slaves log location
		for (Slave slave : slaves) {
			slave.setLocation(uConf.getsJumbuneHome());
		}

		LogConsolidationInfo fileDefinition = new LogConsolidationInfo();
		fileDefinition.setMaster(master);
		fileDefinition.setSlaves(slaves);
		return fileDefinition;
	}

	/**
	 * Gets the log master.
	 *
	 * @return the log master
	 */
	public Master getLogMaster() {
		return getLogDefinition().getMaster();
	}

	/**
	 * Gets the log slaves.
	 *
	 * @return the log slaves
	 */
	public List<Slave> getLogSlaves() {
		return getLogDefinition().getSlaves();
	}

	/**
	 * Gets the user validations.
	 *
	 * @return the user validations
	 */
	public List<Validation> getUserValidations() {
		return uConf.getUserValidations();
	}

	/**
	 * Gets the classpath.
	 *
	 * @return the classpath
	 */
	public Classpath getClasspath() {
		{
			Classpath cp = uConf.getClasspath();
			if (cp == null) {
				LOGGER.error("Fatal error: classpath is not defined in yaml");
				
				System.exit(1);
			}
			return cp;
		}
	}

	/**
	 * This method provides the way in which dependencies are supplied to Jumbune.
	 * <ul>
	 * <li>User dependencies could be supplied:
	 * <ol>
	 * <li>Thick jar
	 * <li>Placed in HADOOP_LIB folder on all nodes
	 * <li>Placed on master node
	 * <li>Placed on all slave nodes
	 * </ol>
	 * <li>Not valid for Jumbune dependencies.
	 * </ul>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param type
	 *            Dependency type
	 * @return int The source
	 */
	public int getClasspathSourceType(int type) {
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getSource();
		}
		return -1;
	}

	/**
	 * Gets the classpath output type.
	 *
	 * @param type the type
	 * @return the classpath output type
	 */
	@Deprecated
	public int getClasspathOutputType(int type) {
		return ClasspathUtil.CLASSPATH_TYPE_LIBJARS;
	}

	/**
	 * Gets the classpath folders.
	 *
	 * @param type the type
	 * @return the classpath folders
	 */
	public String[] getClasspathFolders(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getFolders();
		} else {
			return getClasspath().getJumbuneSupplied().getFolders();
		}
	}

	/**
	 * Gets the classpath resources.
	 *
	 * @param type the type
	 * @return the classpath resources
	 */
	@Deprecated
	public String[] getClasspathResources(int type) {
		return null;
	}

	/**
	 * Gets the classpath excludes.
	 *
	 * @param type the type
	 * @return the classpath excludes
	 */
	public String[] getClasspathExcludes(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getExcludes();
		} else {
			return getClasspath().getJumbuneSupplied().getExcludes();
		}
	}

	/**
	 * Checks if is hadoop job profile enabled.
	 *
	 * @return true, if is hadoop job profile enabled
	 */
	public boolean isHadoopJobProfileEnabled() {
		return uConf.getEnableStaticJobProfiling().equals(Enable.TRUE) ? true : false;
	}

	/**
	 * Gets the hadoop job profile params.
	 *
	 * @return the hadoop job profile params
	 */
	public String getHadoopJobProfileParams() {
		return "\'" + Constants.H_PROFILE_PARAM + uConf.getProfilingParams().getHadoopJobProfileParams() + "\'";
	}

	/**
	 * Gets the hadoop job profile maps.
	 *
	 * @return the hadoop job profile maps
	 */
	public String getHadoopJobProfileMaps() {
		return uConf.getProfilingParams().getMapers();
	}

	/**
	 * Gets the hadoop job profile reduces.
	 *
	 * @return the hadoop job profile reduces
	 */
	public String getHadoopJobProfileReduces() {
		return uConf.getProfilingParams().getReducers();
	}

	// setting the values as 0 to use all nodes
	// TODO : removal of this method
	/**
	 * Gets the log process max threads.
	 *
	 * @return the log process max threads
	 */
	public int getLogProcessMaxThreads() {
		return 0;
	}

	/**
	 * Gets the master info.
	 *
	 * @return the master info
	 */
	public Master getMasterInfo() {
		return uConf.getMaster();
	}

	/**
	 * Gets the partitioner sample interval.
	 *
	 * @return the partitioner sample interval
	 */
	public int getPartitionerSampleInterval() {
		return uConf.getPartitionerSampleInterval();
	}

	/**
	 * Gets the slaves info.
	 *
	 * @return the slaves info
	 */
	public List<Slave> getSlavesInfo() {
		return uConf.getSlaves();
	}

	/**
	 * Gets the max if block nesting level.
	 *
	 * @return the max if block nesting level
	 */
	public int getMaxIfBlockNestingLevel() {
		return uConf.getDebuggerConf().getMaxIfBlockNestingLevel();
	}

	/**
	 * Gets the master consolidated log location.
	 *
	 * @return the master consolidated log location
	 */
	public String getMasterConsolidatedLogLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.CONSOLIDATED_LOG_LOC;
	}

	/**
	 * Gets the slave log location.
	 *
	 * @return the slave log location
	 */
	public String getSlaveLogLocation() {

		return uConf.getsJumbuneHome() + Constants.JOB_JARS_LOC + uConf.getFormattedJumbuneJobName() + Constants.SLAVE_LOG_LOC;

	}

	/**
	 * Gets the master consolidated dv location.
	 *
	 * @return the master consolidated dv location
	 */
	public String getMasterConsolidatedDVLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.CONSOLIDATED_DV_LOC;
	}

	/**
	 * Gets the slave dv location.
	 *
	 * @return the slave dv location
	 */
	public String getSlaveDVLocation() {

		return uConf.getsJumbuneHome() + Constants.JOB_JARS_LOC + uConf.getFormattedJumbuneJobName() + Constants.SLAVE_DV_LOC;

	}

	/**
	 * <p>
	 * Gets the jumbune slave location at slave nodes. This path contains place holders. Place holders to be replaced with values, where this value is
	 * used.
	 * </p>
	 * 
	 * @return String Slave log files location with place holder
	 */
	public String getSlaveLogLocationWithPlaceHolder() {

		return uConf.getSlaveJumbuneHomeWithPlaceHolder() + Constants.JOB_JARS_LOC + uConf.getFormattedJumbuneJobName() + Constants.SLAVE_LOG_LOC;

	}

	/**
	 * Gets the slave dv location with place holder.
	 *
	 * @return the slave dv location with place holder
	 */
	public String getSlaveDVLocationWithPlaceHolder() {

		return uConf.getSlaveJumbuneHomeWithPlaceHolder() + Constants.JOB_JARS_LOC + uConf.getFormattedJumbuneJobName() + Constants.SLAVE_DV_LOC;

	}

	/**
	 * Gets the pure jar counter location.
	 *
	 * @return the pure jar counter location
	 */
	public String getPureJarCounterLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC + ReportName.PURE_JAR_COUNTER;
	}

	/**
	 * Gets the pure jar profiling counters location.
	 *
	 * @return the pure jar profiling counters location
	 */
	public String getPureJarProfilingCountersLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC + ReportName.PURE_PROFILING;
	}

	/**
	 * Gets the instrumented jar counters location.
	 *
	 * @return the instrumented jar counters location
	 */
	public String getInstrumentedJarCountersLocation() {

		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC + ReportName.INSTRUMENTED_JAR_COUNTER;

	}

	/**
	 * Gets the logs consolidated summary location.
	 *
	 * @return the logs consolidated summary location
	 */
	public String getLogsConsolidatedSummaryLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC + Constants.M_SUMMARY_FILE;
	}

	/**
	 * Gets the log summary location.
	 *
	 * @return the log summary location
	 */
	public String getLogSummaryLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.SUMMARY_FILE_LOC;
	}

	/**
	 * Gets the profiling files location.
	 *
	 * @return the profiling files location
	 */
	public String getProfilingFilesLocation() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.PROFILING_FILE_LOC;
	}

	/**
	 * Gets the profiled output file.
	 *
	 * @param inputFile the input file
	 * @return the profiled output file
	 */
	public String getProfiledOutputFile(String inputFile) {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.PROFILED_JAR_LOC + getFileName(inputFile)
				+ Constants.PROFILED_FILE_SUFFIX + ".jar";
	}

	/**
	 * Gets the instrument output file.
	 *
	 * @param inputFile the input file
	 * @return the instrument output file
	 */
	public String getInstrumentOutputFile(String inputFile) {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + Constants.INSTRUMENTED_JAR_LOC + getFileName(inputFile)
				+ Constants.INSTRUMENTED_FILE_SUFFIX + ".jar";
	}

	/**
	 * Gets the file name.
	 *
	 * @param inputFileLoc the input file loc
	 * @return the file name
	 */
	private String getFileName(String inputFileLoc) {
		return inputFileLoc.substring(inputFileLoc.lastIndexOf('/') + 1, inputFileLoc.lastIndexOf('.'));
	}

	/**
	 * Gets the data validation.
	 *
	 * @return the data validation
	 */
	public final DataValidationBean getDataValidation() {
		return uConf.getDataValidation();
	}

	/**
	 * Gets the data validation result location.
	 *
	 * @return the data validation result location
	 */
	public final String getDataValidationResultLocation() {

		return getJobJarLoc() + uConf.getFormattedJumbuneJobName() + "datavalidation/dataviolations";

	}

	/**
	 * Gets the hdfs input path.
	 *
	 * @return the hdfs input path
	 */
	public final String getHdfsInputPath() {
		return uConf.getHdfsInputPath();
	}

	/**
	 * Gets the classpath files.
	 *
	 * @param type the type
	 * @return the classpath files
	 */
	public String[] getClasspathFiles(int type) {
		// 1 = user defined, 2 = framework defined
		if (type == ClasspathUtil.USER_SUPPLIED) {
			return getClasspath().getUserSupplied().getFiles();
		} else {
			return getClasspath().getJumbuneSupplied().getFiles();
		}
	}


	/**
	 * Get the FrameWorkHome set by user.
	 *
	 * @return the j home
	 */
	public static final String getjHome() {
		// Just check if this the first time fetching jHome and it not set try
		// to set it.
		if (jHome == null) {
			setjHome(null);
		}
		return jHome;
	}

	/**
	 * Get the Jumbune Job name set by user.
	 *
	 * @return the jumbune job name
	 */
	public final String getJumbuneJobName() {
		return uConf.getFormattedJumbuneJobName();
	}

	/**
	 * Gets the jumbune job loc.
	 *
	 * @return the jumbune job loc
	 */
	public final String getJumbuneJobLoc() {
		return getJobJarLoc() + uConf.getFormattedJumbuneJobName();
	}

	/**
	 * Set the FrameworkHome.
	 *
	 * @param jHome the new j home
	 */
	public static final void setjHome(final String jHome) {
		String frameworkHome = jHome;
		if (frameworkHome == null) {
			frameworkHome = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME);
		}
		if (frameworkHome != null && !(frameworkHome.endsWith(FORWARD_SLASH))) {
			frameworkHome += FORWARD_SLASH;
		}
		YamlLoader.jHome = frameworkHome;
		LOGGER.debug("JUMBUNE HOME [" + YamlLoader.jHome+"]");
	}

	/**
	 * Set the Hadoop Home.
	 *
	 * @param hadoopHome the new hadoop home
	 */
	public static final void setHadoopHome(final String hadoopHome) {
		String hadopHome=hadoopHome;
		if (hadopHome == null) {
			hadopHome = System.getenv(Constants.HADOOP_ENV_VAR_NAME);
		}

		if (hadopHome != null && !(hadopHome.endsWith(FORWARD_SLASH))) {
			hadopHome += FORWARD_SLASH;
		}

		YamlLoader.hadoopHome = hadopHome;
		LOGGER.debug("HADOOP HOME [" + YamlLoader.hadoopHome+"]");
	}


	/**
	 * <p>
	 * This method gets the location of jumbune lib folder
	 * </p>.
	 *
	 * @return String Location of jumbune lib folder
	 */
	public String getJumbuneLibLocation() {
		return jHome + Constants.JUMBUNE_LIB_LOC;
	}

	/**
	 * Gets the job jar loc.
	 *
	 * @return the job jar loc
	 */
	public static String getJobJarLoc() {
		return jHome + Constants.JOB_JARS_LOC;
	}

	/**
	 * Gets the root directory name.
	 *
	 * @return the root directory name
	 */
	public String getRootDirectoryName() {
		return Constants.JOB_JARS_LOC;
	}

	/**
	 * <p>
	 * This method provides the location of userLib folder on master.
	 * </p>
	 * 
	 * @return String location of userLib on master
	 * 
	 * @see Constants#USER_LIB_LOC
	 */
	public String getUserLibLocatinAtMaster() {
		return jHome + Constants.USER_LIB_LOC;
	}

	/**
	 * Creates the jumbune directories.
	 *
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void createJumbuneDirectories() throws JSchException, IOException, InterruptedException {
		createMasterDirectories();
		if (uConf.getSlaves().size() > 0){
			createSlaveDirectories();}
	}

	/**
	 * Creates the master directories.
	 */
	public void createMasterDirectories() {
		File joblocaion = new File(getJobJarLoc() + uConf.getFormattedJumbuneJobName());
		joblocaion.mkdirs();

		File reportLocation = new File(getShellUserReportLocation());
		reportLocation.mkdirs();

		File profilejarLocation = new File(getProfiledJarLocation());
		profilejarLocation.mkdirs();

		File insturmentedjarLocation = new File(getInstrumentedJarLocation());
		insturmentedjarLocation.mkdirs();

		File consolidationLocation = new File(getMasterConsolidatedLogLocation());
		consolidationLocation.mkdirs();

		File consolidationDVLocation = new File(getMasterConsolidatedDVLocation());
		consolidationDVLocation.mkdirs();

	}

	/**
	 * Creates the slave directories.
	 *
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void createSlaveDirectories() throws JSchException, IOException, InterruptedException {
		makeRemoteSlaveLogDirectory(getLogDefinition());
		makeRemoteSlaveLogDirectory(getDVDefinition());
	}
	
	/**
	 * Make remote slave log directory.
	 * 
	 * @param logCollection
	 *            the log collection
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private static void makeRemoteSlaveLogDirectory(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {

		List<Slave> listSlave = logCollection.getSlaves();
		Master master = logCollection.getMaster();
		String masterHost = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());
		
		Remoter remoter = new Remoter(masterHost, agentPort);
		for (Slave slaveDefinition : listSlave) {
			
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();
			
			for (String hostNode : hostsNode) {
				String command = MKDIR_P_CMD + getFolderName(locationNode);
				LOGGER.debug("Log directory generation command on WorkerNode ["+command+"]");
				
				CommandWritableBuilder builder = new CommandWritableBuilder();
				builder.addCommand(command, false, null).populateFromLogConsolidationInfo(logCollection, hostNode);
				
				remoter.fireAndForgetCommand(builder.getCommandWritable());
			}
			LOGGER.info("Log directory created on WorkerNodes ");
			CONSOLELOGGER.info("Log directory generation on WorkerNodes ");

		}
		remoter.close();
	}
	
	
	/**
	 * <p>
	 * Gets folder name from file name
	 * </p>
	 * .
	 * 
	 * @param file
	 *            file name
	 * @return folder name
	 */
	private static String getFolderName(String file) {
		String folderName = null;
		int lastIndexOfDot = file.lastIndexOf('.');
		int lastIndexOfSeparator = file.lastIndexOf('/');
		if (lastIndexOfDot == -1) {
			folderName = file;
		} else if (lastIndexOfSeparator != -1) {
			if (lastIndexOfSeparator > lastIndexOfDot) {
				folderName = file;
			} else {
				folderName = file.substring(0, lastIndexOfSeparator);
			}
		}

		return folderName;
	}	
	

	/**
	 * <p>
	 * This method provides whether main class is defined in job jar manifest file or not
	 * </p>.
	 *
	 * @return boolean true if main class is defined in jar manifest
	 */
	public boolean isMainClassDefinedInJobJar() {
		return uConf.getIncludeClassJar().getEnumValue();
	}

	/**
	 * Gets the sampled data output path.
	 *
	 * @return the sampled data output path
	 */
	public static String getSampledDataOutputPath() {
		return sampledDataOutputPath;
	}

	/**
	 * Sets the sampled data output path.
	 *
	 * @param sampledDataOutputPath the new sampled data output path
	 */
	public static void setSampledDataOutputPath(String sampledDataOutputPath) {
		YamlLoader.sampledDataOutputPath = sampledDataOutputPath;
	}



}
