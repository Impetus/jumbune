package org.jumbune.common.yaml.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.DoNotInstrument;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ProfilingParam;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.SlaveParam;
import org.jumbune.common.beans.Validation;
import org.jumbune.utils.YamlUtil;


/**
 * This class is the bean for the yaml file.
 */
public class YamlConfig{

	/** The Constant DEFAULT_PARTITION_SAMPLE_INTERVAL. */
	private static final int DEFAULT_PARTITION_SAMPLE_INTERVAL = 1000;
	
	/** The distributed hdfs path. */
	private String distributedHDFSPath;
	/* Hadoop configurations */
	/** The master. */
	private Master master;
	
	/** The slaves. */
	private List<Slave> slaves;
	/* Jumbune Modules */
	/** The hadoop job profile. */
	private Enable hadoopJobProfile = Enable.FALSE;
	
	/** The enable data validation. */
	private Enable enableDataValidation = Enable.FALSE;
	
	/** The debug analysis. */
	private Enable debugAnalysis = Enable.FALSE;

	/** The enable static job profiling. */
	private Enable enableStaticJobProfiling = Enable.FALSE;
	/* Profiling configurations */
	/** The profiling params. */
	private ProfilingParam profilingParams;

	/* Job configurations */
	/** The jobs. */
	private List<JobDefinition> jobs;
	
	/** The include class jar. */
	private Enable includeClassJar = Enable.FALSE;

	/** The input file. */
	private String inputFile;

	/* Jar debugging configuration */
	/** The do not instrument. */
	private DoNotInstrument doNotInstrument;
	
	/** The mapper super classes. */
	private String[] mapperSuperClasses;
	
	/** The reducer super classes. */
	private String[] reducerSuperClasses;
	
	/** The debugger conf. */
	private DebuggerConf debuggerConf;
	
	/** The classpath. */
	private Classpath classpath;
	
	/** The regex validations. */
	private List<Validation> regexValidations;
	
	/** The user validations. */
	private List<Validation> userValidations;
	
	/** It tells after how many keys the partitioning time should be calculated. */
	private int partitionerSampleInterval;
	
	/** The s jumbune home. */
	private String sJumbuneHome;

	/** Location of HDFS path where data to be validated is kept. */
	private String hdfsInputPath;

	/* Data Validation Information */
	/** The data validation. */
	private DataValidationBean dataValidation;

	/** String specifying name of jumbune Job */
	private String jumbuneJobName;

	/** The slave param. */
	private SlaveParam slaveParam;

	/**
	 * Gets the formatted jumbune job name.
	 *
	 * @return the formatted jumbune job name
	 */
	public String getFormattedJumbuneJobName() {
		return formatJumbuneJobName(jumbuneJobName);
	}

	/**
	 * Gets the jumbune job name.
	 *
	 * @return the jumbune job name
	 */
	public String getJumbuneJobName() {
		return jumbuneJobName;
	}

	/**
	 * Gets the un formatted jumbune job name.
	 *
	 * @return the un formatted jumbune job name
	 */
	public String getUnFormattedJumbuneJobName() {
		return jumbuneJobName;
	}

	/**
	 * Sets the jumbune job name.
	 *
	 * @param jumbuneJobName the new jumbune job name
	 */
	public void setJumbuneJobName(final String jumbuneJobName) {
		String jumbuneJobNameTemp = jumbuneJobName;
		jumbuneJobNameTemp = jumbuneJobNameTemp.trim();
		this.jumbuneJobName = jumbuneJobNameTemp;
	}


	/**
	 * Appends jobName and adds / at the end if it is missing. Modifying jumbune
	 * job name is required as all the jobs related folder should be under this
	 * jumbunejob name
	 *
	 * @param jobName the job name
	 * @return the string
	 */
	private String formatJumbuneJobName(final String jobName) {
		String jobNameTemp = jobName;
		if (jobNameTemp == null) {
			return null;
		}

		if (!jobNameTemp.endsWith("/")) {
			jobNameTemp += "/";
		}
		return jobNameTemp;
	}

	/**
	 * Gets the s jumbune home.
	 *
	 * @return the s jumbune home
	 */
	public String getsJumbuneHome() {
		return YamlUtil.getAndReplaceHolders(sJumbuneHome);
	}

	/**
	 * Sets the s jumbune home.
	 *
	 * @param sJumbuneHome the new s jumbune home
	 */
	public void setsJumbuneHome(String sJumbuneHome) {
		this.sJumbuneHome = sJumbuneHome;
	}

	/**
	 * <p>
	 * Gets the jumbune home at slave nodes. This path contains place holders.
	 * Place holders to be replaced with values, where this value is used.
	 * </p>
	 * 
	 * @return String Slave jumbune home with place holders
	 */
	public String getSlaveJumbuneHomeWithPlaceHolder() {
		return sJumbuneHome;
	}

	/**
	 * Gets the hadoop job profile.
	 *
	 * @return the hadoop job profile
	 */
	public Enable getHadoopJobProfile() {
		return hadoopJobProfile;
	}

	/**
	 * Sets the hadoop job profile.
	 *
	 * @param hadoopJobProfile the new hadoop job profile
	 */
	public void setHadoopJobProfile(Enable hadoopJobProfile) {
		this.hadoopJobProfile = hadoopJobProfile;
	}

	/**
	 * Gets the enable data validation.
	 *
	 * @return the enable data validation
	 */
	public Enable getEnableDataValidation() {
		return enableDataValidation;
	}

	/**
	 * Sets the enable data validation.
	 *
	 * @param enableDataValidation the new enable data validation
	 */
	public void setEnableDataValidation(Enable enableDataValidation) {
		this.enableDataValidation = enableDataValidation;
	}

	/**
	 * Gets the debug analysis.
	 *
	 * @return the debug analysis
	 */
	public Enable getDebugAnalysis() {
		return debugAnalysis;
	}

	/**
	 * Sets the debug analysis.
	 *
	 * @param debugAnalysis the new debug analysis
	 */
	public void setDebugAnalysis(Enable debugAnalysis) {
		this.debugAnalysis = debugAnalysis;
	}


	/**
	 * Sets the input file.
	 *
	 * @param inputFile the new input file
	 */
	public final void setInputFile(String inputFile) {
		this.inputFile = YamlUtil.getAndReplaceHolders(inputFile);
	}

	/**
	 * Gets the include class jar.
	 *
	 * @return the include class jar
	 */
	public Enable getIncludeClassJar() {
		return includeClassJar;
	}

	/**
	 * Sets the include class jar.
	 *
	 * @param includeClassJar the new include class jar
	 */
	public void setIncludeClassJar(Enable includeClassJar) {
		this.includeClassJar = includeClassJar;
	}

	/**
	 * Gets the mapper super classes.
	 *
	 * @return the mapper super classes
	 */
	public final String[] getMapperSuperClasses() {
		return mapperSuperClasses;
	}

	/**
	 * Sets the mapper super classes.
	 *
	 * @param mapperSuperClasses the new mapper super classes
	 */
	public final void setMapperSuperClasses(String[] mapperSuperClasses) {
		if (mapperSuperClasses != null && mapperSuperClasses.length > 0) {
			this.mapperSuperClasses = Arrays.copyOf(mapperSuperClasses,
					mapperSuperClasses.length);
		}
	}

	/**
	 * Gets the reducer super classes.
	 *
	 * @return the reducer super classes
	 */
	public final String[] getReducerSuperClasses() {
		return reducerSuperClasses;
	}

	/**
	 * Sets the reducer super classes.
	 *
	 * @param reducerSuperClasses the new reducer super classes
	 */
	public final void setReducerSuperClasses(String[] reducerSuperClasses) {
		if (reducerSuperClasses != null && reducerSuperClasses.length > 0) {
			this.reducerSuperClasses = Arrays.copyOf(reducerSuperClasses,
					reducerSuperClasses.length);
		}
	}

	/**
	 * Gets the jobs.
	 *
	 * @return the jobs
	 */
	public final List<JobDefinition> getJobs() {
		return (jobs != null) ? jobs : new ArrayList<JobDefinition>();
	}

	/**
	 * Gets the do not instrument.
	 *
	 * @return the do not instrument
	 */
	public final DoNotInstrument getDoNotInstrument() {
		return doNotInstrument;
	}

	/**
	 * Sets the do not instrument.
	 *
	 * @param doNotInstrument the new do not instrument
	 */
	public final void setDoNotInstrument(DoNotInstrument doNotInstrument) {
		this.doNotInstrument = doNotInstrument;
	}

	/**
	 * Gets the partitioner sample interval.
	 *
	 * @return the partitioner sample interval
	 */
	public final int getPartitionerSampleInterval() {
		return partitionerSampleInterval;
	}

	/**
	 * Sets the partitioner sample interval.
	 *
	 * @param partitionerSampleInterval the new partitioner sample interval
	 */
	public final void setPartitionerSampleInterval(
			final int partitionerSampleInterval) {
		int partionerSampleInterval = partitionerSampleInterval;
		if (partionerSampleInterval < 0) {
			partionerSampleInterval = DEFAULT_PARTITION_SAMPLE_INTERVAL;
		}
		this.partitionerSampleInterval = partionerSampleInterval;
	}

	/**
	 * Gets the master.
	 *
	 * @return the master
	 */
	public Master getMaster() {
		return master;
	}

	/**
	 * Sets the master.
	 *
	 * @param master the new master
	 */
	public void setMaster(Master master) {
		this.master = master;
	}

	/**
	 * Gets the slaves.
	 *
	 * @return the slaves
	 */
	public List<Slave> getSlaves() {
		return (slaves != null) ? slaves : new ArrayList<Slave>();
	}

	/**
	 * Sets the slaves.
	 *
	 * @param slaves the new slaves
	 */
	public void setSlaves(List<Slave> slaves) {
		this.slaves = slaves;
	}

	/**
	 * Gets the debugger conf.
	 *
	 * @return the debugger conf
	 */
	public DebuggerConf getDebuggerConf() {
		return debuggerConf;
	}

	/**
	 * Sets the debugger conf.
	 *
	 * @param debuggerConf the new debugger conf
	 */
	public void setDebuggerConf(DebuggerConf debuggerConf) {
		this.debuggerConf = debuggerConf;
	}

	/**
	 * Gets the classpath.
	 *
	 * @return the classpath
	 */
	public Classpath getClasspath() {
		return classpath;
	}

	/**
	 * Sets the classpath.
	 *
	 * @param classpath the new classpath
	 */
	public void setClasspath(Classpath classpath) {
		this.classpath = classpath;
	}

	/**
	 * Gets the regex validations.
	 *
	 * @return the regex validations
	 */
	public List<Validation> getRegexValidations() {
		return (regexValidations != null) ? regexValidations
				: new ArrayList<Validation>();
	}

	/**
	 * Sets the regex validations.
	 *
	 * @param regexValidations the new regex validations
	 */
	public void setRegexValidations(List<Validation> regexValidations) {
		this.regexValidations = regexValidations;
	}

	/**
	 * Gets the user validations.
	 *
	 * @return the user validations
	 */
	public List<Validation> getUserValidations() {
		return (userValidations != null) ? userValidations
				: new ArrayList<Validation>();
	}

	/**
	 * Sets the user validations.
	 *
	 * @param userValidations the new user validations
	 */
	public void setUserValidations(List<Validation> userValidations) {
		this.userValidations = userValidations;
	}

	/**
	 * Gets the input file.
	 *
	 * @return the input file
	 */
	public String getInputFile() {
		String searchStr = "akepath";
		if (inputFile != null && inputFile.indexOf(searchStr) > 0) {
			String split[] = inputFile.split(searchStr);
			inputFile = split[1];
		}
		return inputFile;
	}

	/**
	 * Sets the jobs.
	 *
	 * @param jobs the new jobs
	 */
	public void setJobs(List<JobDefinition> jobs) {
		this.jobs = jobs;
	}

	/**
	 * Gets the profiling params.
	 *
	 * @return the profiling params
	 */
	public ProfilingParam getProfilingParams() {
		return profilingParams;
	}

	/**
	 * Sets the profiling params.
	 *
	 * @param profilingParams the new profiling params
	 */
	public void setProfilingParams(ProfilingParam profilingParams) {
		this.profilingParams = profilingParams;
	}

	/**
	 * Gets the data validation.
	 *
	 * @return the data validation
	 */
	public final DataValidationBean getDataValidation() {
		return dataValidation;
	}

	/**
	 * Sets the data validation.
	 *
	 * @param dataValidation the new data validation
	 */
	public final void setDataValidation(DataValidationBean dataValidation) {
		this.dataValidation = dataValidation;
	}

	/**
	 * Gets the hdfs input path.
	 *
	 * @return the hdfs input path
	 */
	public final String getHdfsInputPath() {
		return hdfsInputPath;
	}

	/**
	 * Sets the hdfs input path.
	 *
	 * @param hdfsInputPath the new hdfs input path
	 */
	public final void setHdfsInputPath(String hdfsInputPath) {
		this.hdfsInputPath = hdfsInputPath;
	}



	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "YamlConfig [master=" + master + ", slaves=" + slaves
				+ ", hadoopJobProfile=" + hadoopJobProfile
				+ ", enableDataValidation=" + enableDataValidation
				+ ", debugAnalysis=" + debugAnalysis 
				+ ", profilingParams=" + profilingParams + ", jobs=" + jobs
				+ ", includeClassJar=" + includeClassJar + ", inputFile="
				+ inputFile + ", doNotInstrument=" + doNotInstrument
				+ ", mapperSuperClasses=" + Arrays.toString(mapperSuperClasses)
				+ ", reducerSuperClasses="
				+ Arrays.toString(reducerSuperClasses) + ", debuggerConf="
				+ debuggerConf + ", classpath=" + classpath
				+ ", regexValidations=" + regexValidations
				+ ", userValidations=" + userValidations
				+ ", partitionerSampleInterval=" + partitionerSampleInterval
				+ ", sJumbuneHome=" + sJumbuneHome + ", hdfsInputPath="
				+ hdfsInputPath + ", dataValidation=" + dataValidation
				+ ", jumbuneJobName=" + jumbuneJobName + "]";
	}

	/**
	 * Sets the distributed hdfs path.
	 *
	 * @param distributedHDFSPath the distributedHDFSPath to set
	 */
	public void setDistributedHDFSPath(String distributedHDFSPath) {
		this.distributedHDFSPath = distributedHDFSPath;
	}

	/**
	 * Gets the distributed hdfs path.
	 *
	 * @return the distributedHDFSPath
	 */
	public String getDistributedHDFSPath() {
		return distributedHDFSPath;
	}

	/**
	 * Sets the enable static job profiling.
	 *
	 * @param enableStaticJobProfiling the enableStaticJobProfiling to set
	 */
	public void setEnableStaticJobProfiling(Enable enableStaticJobProfiling) {
		this.enableStaticJobProfiling = enableStaticJobProfiling;
	}

	/**
	 * Gets the enable static job profiling.
	 *
	 * @return the enableStaticJobProfiling
	 */
	public Enable getEnableStaticJobProfiling() {
		return enableStaticJobProfiling;
	}

	/**
	 * Sets the slave param.
	 *
	 * @param slaveParam the new slave param
	 */
	public void setSlaveParam(SlaveParam slaveParam) {
		this.slaveParam = slaveParam;
	}

	/**
	 * Gets the slave param.
	 *
	 * @return the slave param
	 */
	public SlaveParam getSlaveParam() {
		return slaveParam;
	}
}
