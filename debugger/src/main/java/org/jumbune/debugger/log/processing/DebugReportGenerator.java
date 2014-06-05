package org.jumbune.debugger.log.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jumbune.debugger.log.processing.DebugAnalysisBean;
import org.jumbune.debugger.log.processing.JobBean;
import org.jumbune.debugger.log.processing.MapReduceBean;
import org.jumbune.debugger.log.processing.PartitionerInfoBean;



/**
 * The Class DebugReportGenerator.
 */
public class DebugReportGenerator {
	
	/** The map reduce job beans list. */
	private List<MapReduceJobBean> mapReduceJobBeansList = new ArrayList<MapReduceJobBean>();
	
	/** The reducer info list. */
	private List<ReducerInfo> reducerInfoList = new ArrayList<ReducerInfo>();

	/**
	 * Generate report from debug analysis bean.
	 *
	 * @param analysisBean the analysis bean
	 * @return the debug report
	 */
	public DebugReport generateReportFromDebugAnalysisBean(DebugAnalysisBean analysisBean) {
		MapReduceJobBean mapReduceJobBean = null;
		MapReduceBean mapReduceBean = null;

		JobBean jobBean = null;
		Map<String, JobBean> logMap = analysisBean.getLogMap();
		for (Map.Entry<String, JobBean> entryValue : logMap.entrySet()) {
			mapReduceJobBean = new MapReduceJobBean();
			mapReduceJobBean.setJobMapReduceName(entryValue.getKey());
			jobBean = entryValue.getValue();
			mapReduceJobBean.setTotalUnmatchedKeys(jobBean.getTotalUnmatchedKeys());
			mapReduceJobBean.setTotalUnmatchedValues(jobBean.getTotalUnmatchedValues());
			mapReduceJobBeansList.add(mapReduceJobBean);
			for (Map.Entry<String, MapReduceBean> mapReduceEntrySet : jobBean.getJobMap().entrySet()) {
				mapReduceJobBean = new MapReduceJobBean();
				mapReduceBean = mapReduceEntrySet.getValue();
				mapReduceJobBean.setJobMapReduceName(mapReduceEntrySet.getKey());
				mapReduceJobBean.setTotalUnmatchedKeys(mapReduceBean.getTotalUnmatchedKeys());
				mapReduceJobBean.setTotalUnmatchedValues(mapReduceBean.getTotalUnmatchedValues());
				mapReduceJobBeansList.add(mapReduceJobBean);
			}
		}

		Map<String, List<PartitionerInfoBean>> partitionerMap = analysisBean.getPartitionerMap();
		ReducerInfo reducerInfo = null;
		for (List<PartitionerInfoBean> listPartitionerInfoBeans : partitionerMap.values()) {
			for (PartitionerInfoBean partitionerInfoBean : listPartitionerInfoBeans) {
				reducerInfo = new ReducerInfo();
				reducerInfo.setVariance(partitionerInfoBean.getVariance());
				reducerInfo.setName(partitionerInfoBean.getName());
				reducerInfoList.add(reducerInfo);
			}
		}
		DebugReport debugReport = new DebugReport();
		debugReport.setMapperReducerNames(mapReduceJobBeansList);
		debugReport.setReducerInfo(reducerInfoList);
		return debugReport;
	}
}
