package org.jumbune.execution.processor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.ClasspathUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.instrumenter.Instrumenter;
import org.jumbune.debugger.instrumentation.instrumenter.JarInstrumenter;
import org.jumbune.debugger.log.processing.LogAnalyzerUtil;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;


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

		YamlLoader yamlLoader = (YamlLoader)super.getLoader();
		// copying user lib files to master from slave
		if (yamlLoader.getClasspath().getUserSupplied().getSource() == ClasspathUtil.SOURCE_TYPE_SLAVES) {
			
				try {
					FileUtil.copyLibFilesToMaster(super.getLoader());
				} catch (InterruptedException e) {
					LOGGER.error("InterruptedException: " + e);
				} catch (IOException e) {
					LOGGER.error("IOException: " + e);
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
			YamlLoader yamlLoader = (YamlLoader)super.getLoader();
			YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
			yamlConfig.getDebuggerConf().getLogLevel().put("ifblock", LogLevel.TRUE);
			yamlConfig.getDebuggerConf().setMaxIfBlockNestingLevel(2);
			String instrumentedJarPath = yamlLoader.getInstrumentOutputFile();
			String locationOfLogFiles = yamlLoader.getLogMaster().getLocation();
			// Instrument the pure jar
			Instrumenter instrumenter = new JarInstrumenter(super.getLoader());
			instrumenter.instrumentJar();
			processHelper.executeJar(instrumentedJarPath, super.isCommandBased(), super.getLoader(), true);
			// marking report as complete
			// Copy logs from slaves only when there are slaves if there are no
			// slaves then don't go for copying
			if (yamlConfig.getSlaves() != null
					&& yamlConfig.getSlaves().size() > 0) {
				RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
				remoteFileUtil.copyDBLogFilesToMaster(yamlLoader.getLogDefinition());
			}

			LogAnalyzerUtil logUtil = new LogAnalyzerUtil(
					yamlLoader.getLogProcessMaxThreads());
			LOGGER.debug("Consolidate logs files kept on master at ["+ locationOfLogFiles+"]");
			debugAnalyserReport = logUtil.processLogs(locationOfLogFiles,
					yamlLoader.isInstrumentEnabled("partitioner"),super.getLoader(),processHelper.getHadoopJobCounters());

		} catch (IOException e) {
			debugAnalyserReport =  Constants.LOG_PROCESSOR_ERROR;
			LOGGER.error(e);
			throw JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace());
		} catch (InterruptedException e) {
			debugAnalyserReport = Constants.LOG_PROCESSOR_ERROR;
			LOGGER.error("LogAnalyser Failed !!!", e);
			throw JumbuneRuntimeException.throwDebugAnalysisFailedException(e.getStackTrace());
		} catch (ExecutionException e) {
			debugAnalyserReport = Constants.LOG_PROCESSOR_ERROR;
			LOGGER.error("LogAnalyser Failed !!!", e);
			throw JumbuneRuntimeException.throwDebugAnalysisFailedException(e.getStackTrace());
		} catch (Exception e) {
			debugAnalyserReport = Constants.LOG_PROCESSOR_ERROR;
			LOGGER.error("LogAnalyser Failed !!!", e);
			throw JumbuneRuntimeException.throwDebugAnalysisFailedException(e.getStackTrace());
		} finally {
			report = super.getReports().getReport(CommunityModule.DEBUG_ANALYSER);
			report.put(Constants.DEBUG_ANALYZER, debugAnalyserReport);
			// setting the debug analyser report as complete
			super.getReports().setCompleted(CommunityModule.DEBUG_ANALYSER);
			LOGGER.info("Exited from [Debug] Processor...");
			return true;
		}
	}

	@Override
	protected void updateServiceInfo(ServiceInfo serviceInfo)
			throws JumbuneException {

		if (serviceInfo != null) {
			YamlLoader yamlLoader = (YamlLoader)super.getLoader();
			serviceInfo.setInstrumentedJarCountersLocation(yamlLoader
					.getLogDefinition().getLogSummaryLocation()
					.getInstrumentedJarCountersLocation());
		}
	}

	@Override
	protected Module getModuleName() {
		return CommunityModule.DEBUG_ANALYSER;
	}

	
}
