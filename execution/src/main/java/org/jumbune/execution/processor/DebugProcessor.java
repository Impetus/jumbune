package org.jumbune.execution.processor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.debugger.instrumentation.instrumenter.Instrumenter;
import org.jumbune.debugger.instrumentation.instrumenter.JarInstrumenter;
import org.jumbune.debugger.log.processing.LogAnalyzerUtil;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import org.jumbune.common.job.EnterpriseJobConfig;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.Parameters;

/**
 * This processor instrument the provided job jar and analyse the instrumented
 * jar output log.
 * 
 * 
 */
public class DebugProcessor extends BaseProcessor {

	private static final Logger LOGGER = LogManager
			.getLogger(DebugProcessor.class);

	/**
	 * constructor for DebugProcessor
	 * @param isCommandBased
	 */
	public DebugProcessor(boolean isCommandBased) {
		super(isCommandBased);
	}

	/**
	 * This will be executed before the processor gets executed.
	 * 
	 * Following tasks are performed:
	 * <ul>
	 * <li>If user provides the dependencies on slaves, they are copied in the
	 * user lib folder on master node.
	 * </ul>
	 * 
	 * @param params
	 * @throws JumbuneException
	 * 
	 * @see Constants#USER_LIB_LOC
	 */
	@Override
	protected void preExecute(Map<Parameters, String> params)
			throws JumbuneException {
		super.preExecute(params);

		EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) super.getJumbuneRequest().getConfig();
		// copying user lib files to master from slave
		if (enterpriseJobConfig.getClasspath().getUserSupplied().getSource() == ClasspathUtil.SOURCE_TYPE_SLAVES) {
			
				try {
					FileUtil.copyLibFilesToMaster(super.getJumbuneRequest());
				} catch (InterruptedException e) {
					LOGGER.error(JumbuneRuntimeException.throwInterruptedException(e.getStackTrace()));
				} catch (IOException e) {
					LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
				}
		
		}
	}

	@Override
	protected boolean execute(Map<Parameters, String> params)
			throws JumbuneException {

		String debugAnalyserReport = null;
		// populating pure and instrumented report
		Map<String, String> report = null;
		

		try {
			LOGGER.info("Executing [Debug] Processor...");
			JumbuneRequest jumbuneRequest = super.getJumbuneRequest();
			Config config = jumbuneRequest.getConfig();
			EnterpriseJobConfig enterpriseJobConfig = (EnterpriseJobConfig) config;
			Cluster cluster = jumbuneRequest.getCluster();
			
			enterpriseJobConfig.getDebuggerConf().getLogLevel().put("ifblock", LogLevel.TRUE);
			enterpriseJobConfig.getDebuggerConf().setMaxIfBlockNestingLevel(2);
			String instrumentedJarPath = enterpriseJobConfig.getInstrumentOutputFile();
			
			JobConfigUtil.setRelativeWorkingDirectoryForLog(jumbuneRequest);
			String slaveLogLocation = cluster.getWorkers().getRelativeWorkingDirectory();
			String locationOfLogFiles = cluster.getNameNodes().getRelativeWorkingDirectory();
			// Instrument the pure jar
			
			Instrumenter instrumenter = new JarInstrumenter(config, slaveLogLocation);
			instrumenter.instrumentJar();
			processHelper.executeJar(instrumentedJarPath, super.isCommandBased(), jumbuneRequest, true);
			// marking report as complete
			// Copy logs from slaves only when there are slaves if there are no
			// slaves then don't go for copying
			if (cluster.getWorkers().getHosts() != null && ! cluster.getWorkers().getHosts().isEmpty()) {
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyRemoteDBLogFiles(cluster);
			}
			LogAnalyzerUtil logUtil = new LogAnalyzerUtil();
			LOGGER.debug("Consolidate logs files kept on master at ["+ locationOfLogFiles+"]");
			debugAnalyserReport = logUtil.processLogs(locationOfLogFiles,
					enterpriseJobConfig.isInstrumentEnabled("partitioner"), config,processHelper.getHadoopJobCounters());
			return true;
		} catch (Exception e) {
			debugAnalyserReport = Constants.LOG_PROCESSOR_ERROR;
			throw JumbuneRuntimeException.throwDebugAnalysisFailedException(e.getStackTrace());
		} finally {
			report = super.getReports().getReport(CommunityModule.DEBUG_ANALYSER);
			report.put(Constants.DEBUG_ANALYZER, debugAnalyserReport);
			// setting the debug analyser report as complete
			super.getReports().setCompleted(CommunityModule.DEBUG_ANALYSER);
			LOGGER.info("Exited from [Debug] Processor...");
		}
	}

	@Override
	protected Module getModuleName() {
		return CommunityModule.DEBUG_ANALYSER;
	}

	
}
