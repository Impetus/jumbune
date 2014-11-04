package org.jumbune.profiling.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.hprof.BinaryHprofReader;
import org.jumbune.profiling.hprof.CPUSamplesBean;
import org.jumbune.profiling.hprof.HeapAllocSitesBean;
import org.jumbune.profiling.hprof.HprofData;
import org.jumbune.profiling.hprof.CPUSamplesBean.SampleDescriptor;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * This class is a utility class for finding and reading Profiling data when a job is executed. It converts the binary data into Json format for all
 * map/reducers
 * 
 */
public class ProfilerUtil {

	/** The LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ProfilerUtil.class);
	private static final String HPROF_SUMMARY = "HPROF_PROFILER_SUMMERY";
	/** MessageLoader used to load messages from a properties file. */
	private MessageLoader messageLoader = MessageLoader.getInstance();
	private YamlLoader loader;

	private static final String HADOOP = "Hadoop";
	/**
	 * Creates an instance of ProfilerUtil which would have MessageLoader
	 * 
	 * @throws JumbuneException
	 *             - If messages file is corrupt and unable to load it in MessageLoader
	 */
	public ProfilerUtil(Loader loader) {
		this.loader = (YamlLoader) loader;
		try {
			initializeMessageLoader();
		} catch (JumbuneException e) {
			LOGGER.error("Unable to load messages file so could not go further for parsing profiling file--- ", e);
		}
	}

	/**
	 * Read messages file and creates instance of MessageLoader
	 * 
	 * @throws JumbuneException
	 *             - If unable to load message in MessageLoader
	 */
	private void initializeMessageLoader() throws JumbuneException {
		final String messageFileName = "profilerMessage.en";
		final InputStream input = ProfilerUtil.class.getClassLoader().getResourceAsStream(messageFileName);
		messageLoader = new MessageLoader(input);
	}

	/**
	 * This is the core API of ProfilerUnit it reads all the profiling files in specified folder and will fetch HeapSample and CPU sample from each
	 * file
	 * 
	 * @param profileDirLocation
	 *            - Location where all profiling files are kept
	 * @return - A map of ProfilingFile name and its CPU Sample and HeapSamples
	 * 
	 * @throws JumbuneException
	 */
	public Map<String, ProfilerBean> parseProfilingInfo(final String profileDirLocation) throws JumbuneException {
		LOGGER.info("Starting JVM-TI based HProf JVM Profiling...");
		Map<String, ProfilerBean> profilerInfoMap = new LinkedHashMap<String, ProfilerBean>();
		List<File> profilingFilesList = getAllFilesInDirectory(profileDirLocation);

		for (File profilingFile : profilingFilesList) {
			try {
				profilerInfoMap.put(profilingFile.getName(), readFileAndGetTopNSamples(profilingFile));
			} catch (final IOException htfEx) {
				LOGGER.error("Unable to read file so passing null in its values", htfEx);
				profilerInfoMap.put(profilingFile.getName(), null);
			}
		}
		LOGGER.debug("Successfully completed Static (Hprof) profiling");
		return profilerInfoMap;
	}


	/**
	 * It will read a single profile file and collect information of top N CPUSamples and HeapSample
	 * 
	 * @param filepath
	 *            - the path of profile file to be read
	 * @return - ProfilerBean containing information of HeapSamples and CPU samples
	 * @throws JumbuneException
	 *             - If unable to read current file
	 */
	private ProfilerBean readFileAndGetTopNSamples(final File filepath) throws IOException {
		FileInputStream fs = null;
		final ProfilerBean pBean = new ProfilerBean();
		BufferedInputStream bis = null;
		try {
			fs = new FileInputStream(filepath);
			LOGGER.debug("Currently reading file " + filepath);
			bis = new BufferedInputStream(fs);
			final BinaryHprofReader reader = new BinaryHprofReader(bis);
			reader.setStrict(false);
			reader.read();

			pBean.setHeapAllocation(getTopNHeapMap(reader.getHeapBean(), reader));
			pBean.setCpuSample(getTopNCPUSample(reader.getCPUSamples()));

		} catch (final IOException ie) {
			LOGGER.error("Error reading profiling file.", ie);
			throw ie;
		} finally {
			try {
				if(bis!=null){
					bis.close();
				}
			} catch (final IOException e) {
				LOGGER.error("Error closing stream.", e);
				throw e;
			}
		}
		return pBean;
	}

	/**
	 * It will filter only topN samples having highest CPU utilization
	 * 
	 * @param cpuBean
	 *            CPU sample read from file
	 * @return A map which contains sorted SampleDescriptor based on percentage in descending order
	 */
	private Map<Integer, SampleDescriptor> getTopNCPUSample(final CPUSamplesBean cpuBean) {
		final List<SampleDescriptor> cpuSampleDescList = new ArrayList<SampleDescriptor>();

		final List<CPUSamplesBean.SampleDescriptor> completeCPUSampleList = cpuBean.getSampleDescriptorList();
		int lowestCountInList = 0;
		int currentCount = 0;

		final int maxCPUSampleCount = loader.getProfilingMaxCPUSampleCount();

		for (SampleDescriptor sample : completeCPUSampleList) {
			currentCount = sample.getCount();
			if (lowestCountInList == 0) {
				lowestCountInList = currentCount;
			}

			if (maxCPUSampleCount > cpuSampleDescList.size()) {
				cpuSampleDescList.add(sample);

				if (currentCount < lowestCountInList) {
					lowestCountInList = currentCount;
				}
			}
		}

		return sortCPUSampleByCount(cpuSampleDescList);
	}

	/**
	 * This method will sort CPUSample list on the basis of count in descending order
	 * 
	 * @param sampleDesc
	 * @return Will return a ranked map of SampleDescriptor
	 */
	private Map<Integer, SampleDescriptor> sortCPUSampleByCount(final List<SampleDescriptor> sampleDescList) {

		Collections.sort(sampleDescList, new Comparator<SampleDescriptor>() {

			@Override
			public int compare(final SampleDescriptor currentSampleDesc, final SampleDescriptor nextSampleDesc) {
				final Integer nextSampleCount = nextSampleDesc.getCount();
				return nextSampleCount.compareTo(currentSampleDesc.getCount());
			}

		});

		final Map<Integer, SampleDescriptor> cpuSampleRankedMap = new LinkedHashMap<Integer, CPUSamplesBean.SampleDescriptor>();
		int rank = 1;
		for (CPUSamplesBean.SampleDescriptor cpuSample : sampleDescList) {
			cpuSampleRankedMap.put(rank++, cpuSample);
		}

		return cpuSampleRankedMap;
	}

	/**
	 * This method will iterate over HeapAllocSitesBean to get only topN heapAllocation sites along with there stackTrace element. It will check if
	 * there is any specific package specified by user in Yaml, if yes only those HeapAllocSitesBean's would be selected whose trace contains the
	 * specified package.
	 * 
	 * @param heapBean
	 * @param reader
	 * @return A Map sorted on BytesAllocated will be returned
	 */
	public Map<Integer, HTFHeapAllocStackTraceBean> getTopNHeapMap(HeapAllocSitesBean heapBean, BinaryHprofReader reader) {
		final List<HeapAllocSitesBean.SiteDescriptor> heapSitelist = heapBean.getSiteDetails();
		final Map<Integer, HprofData.StackTrace> idToStackTrace = reader.getIdToStackTrace();
		final Map<Integer, HTFHeapAllocStackTraceBean> heapAllocStackTraceMap = new TreeMap<Integer, HTFHeapAllocStackTraceBean>();

		Config config = loader.getYamlConfiguration();
		String[] profilingPackages = null;
		YamlConfig yamlConfig = (YamlConfig)config;
		List<JobDefinition> jobList = yamlConfig.getJobs();
		if (!ValidateInput.isEnable(yamlConfig.getIncludeClassJar())) {
			profilingPackages = getProfilingPackages(profilingPackages, jobList);
		}
		final int maxHeapSampleCount = loader.getProfilingMaxHeapSampleCount();

		boolean showTraceOfAllPackages = false;
		showTraceOfAllPackages = applyShowTraceOfPackages(profilingPackages,
				showTraceOfAllPackages);
		float lowestByteAllocation = 0;
		int lowestByteAllocStackTraceId = 0;
		for (HeapAllocSitesBean.SiteDescriptor heap : heapSitelist) {
			final float currentBytesAlloc = heap.getBytesAllocated();

			if (lowestByteAllocation == 0) {
				lowestByteAllocation = currentBytesAlloc;
				lowestByteAllocStackTraceId = heap.getStackTraceId();
			}

			List<String> stackTraceList = idToStackTrace.get(heap.getStackTraceId()).getStackTraceList();

			if (showTraceOfAllPackages || isRelevantTrace(profilingPackages, stackTraceList)) {
				/**
				 * Put all values in heapAllocStackTraceMap till it reaches desired size. If desired size is attained only those elements will be
				 * added to map if their heapAllocBytes is greater than an already existing heapAllocBytes in map
				 */
				if (heapAllocStackTraceMap.size() <= maxHeapSampleCount) {

					populateHeapAllocMap(heap, stackTraceList, heapAllocStackTraceMap);

					if (currentBytesAlloc < lowestByteAllocation) {
						lowestByteAllocation = currentBytesAlloc;
						lowestByteAllocStackTraceId = heap.getStackTraceId();
					}
				} else if (currentBytesAlloc > lowestByteAllocation) {

					heapAllocStackTraceMap.remove(lowestByteAllocStackTraceId);

					populateHeapAllocMap(heap, stackTraceList, heapAllocStackTraceMap);

					lowestByteAllocStackTraceId = getLowestHeapTraceId(heapAllocStackTraceMap);
					lowestByteAllocation = heapAllocStackTraceMap.get(lowestByteAllocStackTraceId).getHeapAllocSiteBean().getBytesAllocated();
				}
			}
		}
		return sortByHeapAllocBytes(heapAllocStackTraceMap);
	}

	/**
	 * Apply show trace of packages.
	 *
	 * @param profilingPackages the profiling packages
	 * @param showTraceOfAllPackages the show trace of all packages
	 * @return true, if successful
	 */
	private boolean applyShowTraceOfPackages(String[] profilingPackages,
			boolean showTraceOfAllPackages) {
		boolean showTraceOfAllPackagesTmp = showTraceOfAllPackages;
		if (profilingPackages == null || profilingPackages.length == 0) {
			showTraceOfAllPackagesTmp = true;
		}
		return showTraceOfAllPackagesTmp;
	}

	/**
	 * Gets the profiling packages.
	 *
	 * @param profilingPackages the profiling packages
	 * @param jobList the job list
	 * @return the profiling packages
	 */
	private String[] getProfilingPackages(String[] profilingPackages,
			List<JobDefinition> jobList) {
		String[] profilingPackagesTmp  = profilingPackages;
		if (jobList != null) {
			List<String> packageList = new ArrayList<String>();

			for (JobDefinition definition : jobList) {

				String packageName = getPackageToFilter(definition.getJobClass());

				if (!packageList.contains(packageName)) {
					packageList.add(packageName);
				}
				LOGGER.debug(packageName);
			}
			
			profilingPackagesTmp = packageList.toArray(new String[packageList.size()]);
		}
		return profilingPackagesTmp;
	}

	/**
	 * It returns a package name on which the filtering of heap sites will be done. It returns the package name based on the level
	 * Constants.PROFILING_PACKAGE_FILTERING_LEVEL e.g: fullyQualifiedClassName = org.jumbune.MyClass return org.jumbune
	 * 
	 * fullyQualifiedClassName = org.MyClass return org.jumbune
	 * 
	 * fullyQualifiedClassName = com.MyClass return (empty string)
	 * 
	 * @param fullyQualifiedClassName
	 *            = MyClass return null
	 * 
	 * @return either a desired package name as described above or null
	 */
	private String getPackageToFilter(String fullyQualifiedClassName) {
		if (fullyQualifiedClassName == null || fullyQualifiedClassName.equals("")) {
			return null;
		}
		String fullyQualifiedClassNameTmp = fullyQualifiedClassName;
		final char packageSeparator = '.';
		final int packageLevel = Constants.PROFILING_PACKAGE_FILTERING_LEVEL;

		String packageName = null;

		fullyQualifiedClassNameTmp = fullyQualifiedClassNameTmp.replace(packageSeparator, '@');

		String[] packages = fullyQualifiedClassNameTmp.split("@");
		StringBuilder packageBuilder = new StringBuilder();

		int finalLevel = 0;

		if (packages.length > packageLevel) {
			finalLevel = packageLevel;
		} else if (packages.length <= packageLevel) {
			finalLevel = packages.length - 1;
		}

		for (int i = 0; i < finalLevel; i++) {
			packageBuilder.append(packages[i]);

			if (i < (finalLevel - 1)) {
				packageBuilder.append(packageSeparator);
			}
		}
		packageName = packageBuilder.toString();
		return packageName;
	}

	/**
	 * It will construct HTFHeapAllocStackTraceBean and put it in heapAllocStackTraceMap
	 * 
	 * @param siteDesc
	 *            - SiteDescriptor object to be added in map
	 * @param stackTrace
	 *            - StackTrace linked to this SiteDescriptor
	 * @param heapAllocStackTraceMap
	 *            map in which newly created HTFHeapAllocStackTraceBean is added
	 */
	private void populateHeapAllocMap(final HeapAllocSitesBean.SiteDescriptor siteDesc, final List<String> stackTrace,
			final Map<Integer, HTFHeapAllocStackTraceBean> heapAllocStackTraceMap) {

		final HTFHeapAllocStackTraceBean heapAllocStackTrace = new HTFHeapAllocStackTraceBean();

		heapAllocStackTrace.setHeapAllocSiteBean(siteDesc);
		heapAllocStackTrace.setStackTraceList(stackTrace);

		heapAllocStackTraceMap.put(siteDesc.getStackTraceId(), heapAllocStackTrace);
	}

	/**
	 * It tests if the stackTrace contains packages specified by user
	 * 
	 * @param packages
	 *            - List of packages specified by user
	 * @param stackTraceList
	 *            - StackTrace linked to a HeapSite
	 * @return true if stackTraceList contains user specified package false otherwise
	 */
	private boolean isRelevantTrace(final String[] packages, final List<String> stackTraceList) {

		for (String profilePackage : packages) {
			for (String trace : stackTraceList) {
				if (trace.startsWith(profilePackage)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method will calculate the traceId of HTFHeapAllocStackTraceBean whose heap allocation is lowest in given map
	 * 
	 * @param heapAllocStackTraceMap
	 * @return traceId of HTFHeapAllocStackTraceBean whose heapAllocation is lowest
	 */
	private int getLowestHeapTraceId(final Map<Integer, HTFHeapAllocStackTraceBean> heapAllocStackTraceMap) {
		float lowestByte = 0;
		int lowestByteTraceId = 0;

		for (Map.Entry<Integer, HTFHeapAllocStackTraceBean> heapEntry : heapAllocStackTraceMap.entrySet()) {
			HTFHeapAllocStackTraceBean bean = heapEntry.getValue();

			final float heapAlloc = bean.getHeapAllocSiteBean().getBytesAllocated();

			if (lowestByte > heapAlloc || lowestByte == 0) {
				lowestByte = heapAlloc;
				lowestByteTraceId = heapEntry.getKey();

			}
		}
		return lowestByteTraceId;
	}

	/**
	 * This method will sort Map<Integer, HeapAllocStackTraceBean> based on heapAllocBytes in descending order. A comparator could be implemented in
	 * HeapAllocStackTraceBean but doing so would sort the map on every entry which would decrease the performance.
	 * 
	 * @param heapAllocStackTraceMap
	 * 
	 * @return - A map is returned whose key is Rank instead of TraceId
	 */
	private Map<Integer, HTFHeapAllocStackTraceBean> sortByHeapAllocBytes(final Map<Integer, HTFHeapAllocStackTraceBean> heapAllocStackTraceMap) {

		List<HTFHeapAllocStackTraceBean> heapAllocStackTraceList = new ArrayList<HTFHeapAllocStackTraceBean>(heapAllocStackTraceMap.values());
		Collections.sort(heapAllocStackTraceList, new HeapSiteComparator());

		int rank = 1;
		Map<Integer, HTFHeapAllocStackTraceBean> sortedMap = new LinkedHashMap<Integer, HTFHeapAllocStackTraceBean>();
		for (HTFHeapAllocStackTraceBean entry : heapAllocStackTraceList) {
			sortedMap.put(rank++, entry);
		}

		return sortedMap;
	}

	/**
	 * This method converts the ProfilerBean map into JsonString and also filters few attributes by using ExclusionStrategies
	 * 
	 * @param profilerInfoMap
	 * @return Json string for entire ProfilerBean
	 */
	public String convertProfilingReportToJson(JobOutput jobOutput) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new HeapAllocStackTraceExclStrat()).setPrettyPrinting().create();
		String resultJson = null;
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("graphData", gson.toJsonTree(jobOutput, JobOutput.class));
		resultJson = jsonObject.toString();
		
		LOGGER.debug("MR JVM profiler resultJson:" + resultJson);
		return resultJson;
	}

	/**
	 * This API will get all files of profiling in specified directory i.e. the files must end with .profile extension
	 * 
	 * @param dirPath
	 *            - Path where profiling files are kept
	 * @return - List of files in directory
	 */
	private List<File> getAllFilesInDirectory(final String dirPath) {
		final File directory = new File(dirPath);
		final List<File> profilingFiles = new ArrayList<File>();
		if (directory.isDirectory()) {
			final File[] files = directory.listFiles();

			for (File profileFile : files) {
				if (profileFile.isFile() && profileFile.getName().endsWith(messageLoader.get(ProfilerConstants.PROFILER_FILE_EXTN))) {
					profilingFiles.add(profileFile);
				}
			}
		}
		return profilingFiles;
	}

	/**
	 * Splits and trims the line for getting next attribute
	 * 
	 * @param line
	 * @param attrib
	 * @return the trimmed line
	 */
	public static String trimAndSpilt(String line, String attrib) {
		String lineTmp = line;
		int index = lineTmp.indexOf(attrib);
		lineTmp = lineTmp.substring(index + attrib.length() + 1, lineTmp.length());
		lineTmp = lineTmp.trim();
		String[] lineArray = lineTmp.split(" ");
		return lineArray[0];
	}

	/**
	 * Rounds off the double value upto two decimal places
	 * 
	 * @param d
	 *            the value to be rounded off
	 * @return the value after being rounded off
	 */
	public static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	/**
	 * This api Converts the value in kb to mb.
	 *
	 * @param valueinKB the valuein kb
	 * @return the double
	 */
	public static double convertKBtoMB(long valueinKB) {
		double valueInMB = (double) valueinKB / (ProfilerConstants.ONE_ZERO_TWO_FOUR);
		return roundTwoDecimals(valueInMB);
	}

	/**
	 * This api Converts the value in kb to gb.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static double convertKBtoGB(long value) {
		double valueInGB = (double) value / (ProfilerConstants.ONE_ZERO_TWO_FOUR * ProfilerConstants.ONE_ZERO_TWO_FOUR);
		return roundTwoDecimals(valueInGB);
	}

	/**
	 * This method gets the dFS admin report command result.
	 *
	 * @param loader the loader
	 * @return the dFS admin report command result
	 */
	public static String[] getDFSAdminReportCommandResult(Loader loader) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		Remoter remoter = RemotingUtil.getRemoter(loader, "");
		StringBuilder sbReport = new StringBuilder();
		sbReport.append(yamlLoader.getHadoopHome(loader)).append("/bin/hadoop").append(" ").append("dfsadmin")
				.append(" ").append("-report");
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(sbReport.toString(), false, null).populate(yamlLoader.getYamlConfiguration(), null);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response.split("\n");
	}

	/**
	 * This method gets the block status command result.
	 *
	 * @param loader
	 *            the loader
	 * @return the block status command result
	 */
	public static String getBlockStatusCommandResult(Loader loader,
			String hdfsFilePath) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		YamlConfig yamlConfig = (YamlConfig)yamlLoader.getYamlConfiguration();
		Remoter remoter = RemotingUtil.getRemoter(yamlLoader, "");
		StringBuilder sbReport = new StringBuilder();
		if (Enable.TRUE.equals(yamlConfig.getEnableYarn())) {
			sbReport.append(yamlLoader.getHadoopHome(loader))
					.append("/bin/hdfs fsck ").append(hdfsFilePath).append(" ")
					.append("-files -blocks -locations ");
		} else {
			sbReport.append(yamlLoader.getHadoopHome(loader))
					.append("/bin/hadoop fsck ").append(hdfsFilePath)
					.append(" ").append("-files -blocks -locations ");
		}
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(sbReport.toString(), false, null).populate(
				yamlConfig, null);
		return (String) remoter.fireCommandAndGetObjectResponse(builder
				.getCommandWritable());
	}	
	/***
	 * This method finds out the Hadoop URL which is used while monitoring remote hadoop jmx based on the hadoop version.
	 * 
	 * @param hadoopVersion
	 * @param jmxDaemon
	 * @return
	 */
	public static String getHadoopJMXURLPrefix(SupportedApacheHadoopVersions hadoopVersion, JMXDeamons jmxDaemon) {
		String hadoopJMXURL = null;
		switch (hadoopVersion) {
		/*case HADOOP_0_20_2:
			hadoopJMXURL = "hadoop";
			break;*/
		default:
			hadoopJMXURL = HADOOP;
			break;
		}
		return hadoopJMXURL;

	}
}