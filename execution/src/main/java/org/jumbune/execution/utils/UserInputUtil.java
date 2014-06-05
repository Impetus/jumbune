package org.jumbune.execution.utils;

import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_ALLOWED_LOG_LEVEL;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_INFO_CHANGE_QUESTION;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_JOB_CLASS_NAME;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_JOB_NAME;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_JOB_PARAMETERS;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_LOG_LEVEL;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_MORE_JOBS;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_VALID_INPUT;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;


/**
 * This class shows various options to user to change the properties mentioned
 * in his customized yaml file
 * 
 */
public class UserInputUtil {

	private static final Logger LOGGER = LogManager
			.getLogger(UserInputUtil.class);

	private YamlLoader loader;
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	private static String validInput;
	private Scanner scanner;

	/**
	 * public constructor for UserInputUtil
	 * @param loader
	 * @param scanner
	 */
	public UserInputUtil(YamlLoader loader, Scanner scanner) {
		this.scanner = scanner;
		this.loader = loader;
		validInput = MESSAGES.get(MESSAGE_VALID_INPUT);
	}

	/**
	 * This is the core method of this class which performs the task of showing
	 * questions to user and take answers for it and will set them accordingly
	 * Three type of questions are asked to user: 1) Change Job information 2)
	 * Change instrumentation enabling information 3) Change LogLevel
	 * informations
	 * 
	 * @throws Exception
	 */
	public void getInfo() throws JumbuneException {
		LOGGER.info("Enquire user to change values provided in yaml file !!!");

		String jobInfoChangeQuestion = MessageFormat.format(
				MESSAGES.get(MESSAGE_INFO_CHANGE_QUESTION), "job(s)");

		// Changing job related information
		if (ExecutionUtil.askYesNoInfo(scanner, validInput,
				jobInfoChangeQuestion)) {
			List<JobDefinition> jobDefList = new ArrayList<JobDefinition>();
			while (true) {

				JobDefinition jobDef = new JobDefinition();
				jobDef.setName(ExecutionUtil.readInputFromConsole(scanner,
						validInput, MESSAGES.get(MESSAGE_JOB_NAME)));
				jobDef.setJobClass(ExecutionUtil.readInputFromConsole(scanner,
						validInput, MESSAGES.get(MESSAGE_JOB_CLASS_NAME)));

				String answer = ExecutionUtil.readInputFromConsole(scanner,
						validInput, MESSAGES.get(MESSAGE_JOB_PARAMETERS));

				jobDef.setParameters(answer);
				jobDefList.add(jobDef);
				if (!ExecutionUtil.askYesNoInfo(scanner, validInput,
						MESSAGES.get(MESSAGE_MORE_JOBS))){
					break;
				}
			}
			loader.setJobDefinitionList(jobDefList);
		}

		String askForLoggingEnabled = MessageFormat.format(
				MESSAGES.get(MESSAGE_INFO_CHANGE_QUESTION),
				"instrumentation enabling");

		// Changing instrumentation enabling information
		if (ExecutionUtil.askYesNoInfo(scanner, validInput,
				askForLoggingEnabled)) {
			DebuggerConf instruDef = loader.getInstrumentation();

			Map<String, LogLevel> logLevelMap = instruDef.getLogLevel();

			for (Map.Entry<String, LogLevel> logLevel : logLevelMap.entrySet()) {

				String logLevelQuestion = MessageFormat.format(
						MESSAGES.get(MESSAGE_LOG_LEVEL), logLevel.getKey(),
						logLevel.getValue());

				if (ExecutionUtil.askYesNoInfo(scanner, validInput,
						logLevelQuestion)) {
					LogLevel logLevelVal = ExecutionUtil.askLogLevelInfo(
							scanner, validInput,
							MESSAGES.get(MESSAGE_ALLOWED_LOG_LEVEL));
					logLevelMap.put(logLevel.getKey(), logLevelVal);
				}
			}
		}
	}

}
