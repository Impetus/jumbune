package org.jumbune.profiling.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.profiling.hprof.CPUSamplesBean.SampleDescriptor;


/**
 * The Class ProfilingReportGenerator.This class is used for generate profiling report in jumbune.To do this it gets all stats from profiler and then
 * collect only those values which will be shown for dash board,by this it sets these values in {@link ProfilingReportGenerator}.
 * 
 */
public class ProfilingReportGenerator {

	/** The cpu sample mapper. */
	private SortedMap<Float, String> cpuSampleMapper = null;

	/** The cpu sample reducer. */
	private SortedMap<Float, String> cpuSampleReducer = null;

	/** The board report. */
	private ProfilerDashBoardReport boardReport = null;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ProfilerDashBoardReport.class);
	
	public SortedMap<Float, String> getCpuSampleMapper() {
		return cpuSampleMapper;
	}

	public void setCpuSampleMapper(SortedMap<Float, String> cpuSampleMapper) {
		this.cpuSampleMapper = cpuSampleMapper;
	}

	public SortedMap<Float, String> getCpuSampleReducer() {
		return cpuSampleReducer;
	}

	public void setCpuSampleReducer(SortedMap<Float, String> cpuSampleReducer) {
		this.cpuSampleReducer = cpuSampleReducer;
	}

	public ProfilerDashBoardReport getBoardReport() {
		return boardReport;
	}

	public void setBoardReport(ProfilerDashBoardReport boardReport) {
		this.boardReport = boardReport;
	}

	/**
	 * Generate profiling report.
	 * 
	 * @param profilingBeanMap
	 *            the profiling bean map
	 * @return the profiler dash board report
	 * @throws Exception
	 *             the exception
	 */
	public ProfilerDashBoardReport generateProfilingReport(Map<String, ProfilerBean> profilingBeanMap) {
		boardReport = new ProfilerDashBoardReport();
		boolean isMapperInstance = false;
		SampleDescriptor descriptor = null;
		ProfilerBean profilerBeanValue = null;
		List<Float> mapperList = null;
		List<Float> reducerList = null;
		cpuSampleMapper = new TreeMap<Float, String>();
		cpuSampleReducer = new TreeMap<Float, String>();
		mapperList = new ArrayList<Float>();
		reducerList = new ArrayList<Float>();
		for (Map.Entry<String, ProfilerBean> entrySetOfProfilerBean : profilingBeanMap.entrySet()) {
			profilerBeanValue = entrySetOfProfilerBean.getValue();
			if (entrySetOfProfilerBean.getKey().contains("_m_")) {
				isMapperInstance = true;
			} else {
				isMapperInstance = false;
			}
			Map<Integer, SampleDescriptor> cpuSample = null;
			cpuSample = profilerBeanValue.getCpuSample();
			if (cpuSample != null) {
				Set<Map.Entry<Integer, SampleDescriptor>> cpuSampleEntrySet = cpuSample.entrySet();
				for (Map.Entry<Integer, SampleDescriptor> cpuSampleIteration : cpuSampleEntrySet) {
					descriptor = cpuSampleIteration.getValue();
					if (isMapperInstance) {
						cpuSampleMapper.put(descriptor.getSelfPercentage(), descriptor.getQualifiedMethod());
						mapperList.add(descriptor.getSelfPercentage());
					} else {
						cpuSampleReducer.put(descriptor.getSelfPercentage(), descriptor.getQualifiedMethod());
						reducerList.add(descriptor.getSelfPercentage());
					}
				}
			}

		}
		if (mapperList != null) {
			Collections.sort(mapperList);
			try {
				setCPUIntensiveMethodInMapper(mapperList, cpuSampleMapper);
			} catch (Exception e) {
				LOGGER.error("Exception while setting CPU intensive method in mapper ::: "+e);
			}
		}
		if (reducerList != null) {
			Collections.sort(reducerList);
			try {
				setCPUIntensiveMethodInReducer(reducerList, cpuSampleReducer);
			} catch (Exception e) {
				LOGGER.error("Exception while setting CPU intensive method in reducer ::: "+e);
			}
		}
		return boardReport;

	}

	/**
	 * Sets the cpu intensive method in mapper.
	 * 
	 * @param mapperList
	 *            the mapper list
	 * @param cpuSampleMapper
	 *            the cpu sample mapper
	 * @throws Exception
	 *             the exception
	 */
	private void setCPUIntensiveMethodInMapper(List<Float> mapperList, SortedMap<Float, String> cpuSampleMapper) {
		int size = mapperList.size();
		Map<Float, String> tempMap = new HashMap<Float, String>();
		if (mapperList.size() > 2) {
			tempMap.put(mapperList.get(size - 1), cpuSampleMapper.get(mapperList.get(size - 1)));
			tempMap.put(mapperList.get(size - 2), cpuSampleMapper.get(mapperList.get(size - 2)));
			boardReport.setCpuSamplesMapper(tempMap);
		}
	}

	/**
	 * Sets the cpu intensive method in reducer.
	 * 
	 * @param reducerList
	 *            the reducer list
	 * @param cpuSampleReducer
	 *            the cpu sample reducer
	 * @throws Exception
	 *             the exception
	 */
	private void setCPUIntensiveMethodInReducer(List<Float> reducerList, SortedMap<Float, String> cpuSampleReducer) {
		Map<Float, String> tempMap = new HashMap<Float, String>();
		int size = reducerList.size();
		if (reducerList.size() > 2) {
			tempMap.put(reducerList.get(size - 1), cpuSampleReducer.get(reducerList.get(size - 1)));
			tempMap.put(reducerList.get(size - 2), cpuSampleReducer.get(reducerList.get(size - 2)));
			boardReport.setCpuSampleReducer(tempMap);
		}
	}
}