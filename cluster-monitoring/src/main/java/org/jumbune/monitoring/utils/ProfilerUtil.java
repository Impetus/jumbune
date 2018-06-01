package org.jumbune.monitoring.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.monitoring.hprof.BinaryHprofReader;
import org.jumbune.monitoring.hprof.HeapAllocSitesBean;
import org.jumbune.monitoring.hprof.HprofData;
import org.jumbune.remoting.common.CommandType;
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
	
	private JobConfig jobconfig;
	
	private static final String DFS_ADMIN_COMMAND = " dfsadmin -report";
	

	private static final String MAPR_CHECK_COMMAND="maprcli node heatmap -view diskspace -long";

	
	
	/**
	 * Creates an instance of ProfilerUtil which would have MessageLoader
	 * 
	 * @throws JumbuneException
	 *             - If messages file is corrupt and unable to load it in MessageLoader
	 */
	public ProfilerUtil(Config config) {
		this.jobconfig = (JobConfig) config;
	}

	/**
	 * This method will iterate over HeapAllocSitesBean to get only topN heapAllocation sites along with there stackTrace element. It will check if
	 * there is any specific package specified by user in json, if yes only those HeapAllocSitesBean's would be selected whose trace contains the
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

		String[] profilingPackages = null;
		JobConfig jobConfig = (JobConfig)jobconfig;
		List<JobDefinition> jobList = jobConfig.getJobs();
		if (!JobConfigUtil.isEnable(jobConfig.getIncludeClassJar())) {
			profilingPackages = getProfilingPackages(profilingPackages, jobList);
		}
		final int maxHeapSampleCount = Constants.PROFILING_MAX_HEAP_SAMPLE_COUNT;;

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
	 * This method gets the dFS admin report command result.
	 *
	 * @param loader the loader
	 * @return the dFS admin report command result
	 */
	public static String[] getDFSAdminReportCommandResult(Cluster cluster) {
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		boolean isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution);
		String response;
		if (isMapr) {
			response = RemotingUtil.executeCommand(cluster, MAPR_CHECK_COMMAND);
		} else {
			response = RemotingUtil.fireCommandAsHadoopDistribution( cluster, DFS_ADMIN_COMMAND, CommandType.HADOOP_FS);
		}
		return response.split("\n");
	}
	
}