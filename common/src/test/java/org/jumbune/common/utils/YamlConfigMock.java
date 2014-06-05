package org.jumbune.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.DoNotInstrument;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ProfilingParam;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.Validation;
import org.jumbune.utils.YamlUtil;



public class YamlConfigMock {
	public static final int DEFAULT_PARTITION_SAMPLE_INTERVAL = 1000;

	/* Hadoop configurations */
	public static String hadoopHome;
	public static Master master;
	public static List<Slave> slaves;

	/* Profiling configurations */
	public static boolean hadoopJobProfile;
	public static ProfilingParam profilingParams;

	/* Job configurations */
	public static List<JobDefinition> jobs;
	public static String inputFile;

	/* Jar debugging configuration */
	public static DoNotInstrument doNotInstrument;
	public static String[] mapperSuperClasses;
	public static String[] reducerSuperClasses;
	public static DebuggerConf debuggerConf;
	public static Classpath classpath;
	public static List<Validation> regexValidations;
	public static List<Validation> userValidations;
	/** It tells after how many keys the partitioning time should be calculated */
	public static int partitionerSampleInterval;
	public static String sJumbuneHome;

	/** Location of HDFS path where data to be validated is kept */
	public static String hdfsInputPath;

	/* Data Validation Information */
	public static DataValidationBean dataValidation;

	public String getsJumbuneHome() {
		System.out.println("slave loc is " + sJumbuneHome);
		return sJumbuneHome;
	}

	public void setsJumbuneHome(String sJumbuneHome) {
		this.sJumbuneHome = YamlUtil.getAndReplaceHolders(sJumbuneHome);
	}

	public final boolean isHadoopJobProfile() {
		return hadoopJobProfile;
	}

	public final void setHadoopJobProfile(boolean hadoopJobProfile) {
		this.hadoopJobProfile = hadoopJobProfile;
	}

	public final void setInputFile(String inputFile) {
		this.inputFile = YamlUtil.getAndReplaceHolders(inputFile);
	}

	public final String[] getMapperSuperClasses() {
		return mapperSuperClasses;
	}

	public final void setMapperSuperClasses(String[] mapperSuperClasses) {
		this.mapperSuperClasses = mapperSuperClasses;
	}

	public final String[] getReducerSuperClasses() {
		return reducerSuperClasses;
	}

	public final void setReducerSuperClasses(String[] reducerSuperClasses) {
		this.reducerSuperClasses = reducerSuperClasses;
	}

	public final List<JobDefinition> getJobs() {
		return (jobs != null) ? jobs : new ArrayList<JobDefinition>();
	}

	public final DoNotInstrument getDoNotInstrument() {
		return doNotInstrument;
	}

	public final void setDoNotInstrument(DoNotInstrument doNotInstrument) {
		this.doNotInstrument = doNotInstrument;
	}

	public final int getPartitionerSampleInterval() {
		return partitionerSampleInterval;
	}

	public final void setPartitionerSampleInterval(int partitionerSampleInterval) {
		if (partitionerSampleInterval < 0) {
			partitionerSampleInterval = DEFAULT_PARTITION_SAMPLE_INTERVAL;
		}
		this.partitionerSampleInterval = partitionerSampleInterval;
	}

	public String getHadoopHome() {
		return hadoopHome;
	}

	public void setHadoopHome(String hadoopHome) {
		this.hadoopHome = YamlUtil.getAndReplaceHolders(hadoopHome);
		;
	}

	public Master getMaster() {
		return master;
	}

	public void setMaster(Master master) {
		this.master = master;
	}

	public List<Slave> getSlaves() {
		return (slaves != null) ? slaves : new ArrayList<Slave>();
	}

	public void setSlaves(List<Slave> slaves) {
		this.slaves = slaves;
	}

	public DebuggerConf getDebuggerConf() {
		return debuggerConf;
	}

	public void setDebuggerConf(DebuggerConf debuggerConf) {
		this.debuggerConf = debuggerConf;
	}

	public Classpath getClasspath() {
		return classpath;
	}

	public void setClasspath(Classpath classpath) {
		this.classpath = classpath;
	}

	public List<Validation> getRegexValidations() {
		return (regexValidations != null) ? regexValidations : new ArrayList<Validation>();
	}

	public void setRegexValidations(List<Validation> regexValidations) {
		this.regexValidations = regexValidations;
	}

	public List<Validation> getUserValidations() {
		return (userValidations != null) ? userValidations : new ArrayList<Validation>();
	}

	public void setUserValidations(List<Validation> userValidations) {
		this.userValidations = userValidations;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setJobs(List<JobDefinition> jobs) {
		this.jobs = jobs;
	}

	public ProfilingParam getProfilingParams() {
		return profilingParams;
	}

	public void setProfilingParams(ProfilingParam profilingParams) {
		this.profilingParams = profilingParams;
	}

	public final DataValidationBean getDataValidation() {
		return dataValidation;
	}

	public final void setDataValidation(DataValidationBean dataValidation) {
		this.dataValidation = dataValidation;
	}

	public final String getHdfsInputPath() {
		return hdfsInputPath;
	}

	public final void setHdfsInputPath(String hdfsInputPath) {
		this.hdfsInputPath = hdfsInputPath;
	}
}
