package org.jumbune.execution.utils;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Map;
import mockit.Expectations;
import mockit.Mocked;

import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.TestYamlLoaderProvider;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.UserInputUtil;
import org.jumbune.utils.beans.LogLevel;
import org.jumbune.utils.exception.JumbuneException;
import org.junit.Test;

public class UserInputUtilTest {
	private final String MESSAGE1 = "Please enter valid option.";
	private final String MESSAGE2 = "Would you like to change job(s) information provided in your base yaml file?";
	private final String MESSAGE3 = "Please enter job name.";
	private final String MESSAGE4 = "Please enter fully qualified job class name.";
	private final String MESSAGE5 = "Please provide the list of job parameters in a comma separated string.";
	private final String MESSAGE6 = "Would you like enter information for more jobs?";
	private final String MESSAGE7 = "Would you like to change instrumentation enabling information provided in your base yaml file?";
	private final String MESSAGE8 = "Log level for ifblock is TRUE. Would you like to change it?  ";
	private final String MESSAGE9 = "Allowed logs levels are: INFO,DEBUG,VERBOSE,WARN,ERROR.";
	private final String MESSAGE10 = "Log level for switchcase is FALSE. Would you like to change it?  ";
	private final String MESSAGE11 = "Log level for instrumentRegex is FALSE. Would you like to change it?  ";
	private final String MESSAGE12 = "Log level for instrumentUserDefValidate is FALSE. Would you like to change it?  ";
	private final String MESSAGE13 = "Log level for partitioner is FALSE. Would you like to change it?  ";
	@Mocked
	ExecutionUtil exeUtil;
	@Test
	public void testGetInfo() throws JumbuneException, IOException {
		new Expectations() {
			{
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE2);
				result = true;
				ExecutionUtil.readInputFromConsole(null, MESSAGE1, MESSAGE3);
				result = "name";
				ExecutionUtil.readInputFromConsole(null, MESSAGE1, MESSAGE4);
				result = "name";
				ExecutionUtil.readInputFromConsole(null, MESSAGE1, MESSAGE5);
				result = "name";
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE6);
				result = false;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE7);
				result = true;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE8);
				result = true;
				ExecutionUtil.askLogLevelInfo(null, MESSAGE1, MESSAGE9);
				result = LogLevel.INFO;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE10);
				result = false;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE11);
				result = false;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE12);
				result = false;
				ExecutionUtil.askYesNoInfo(null, MESSAGE1, MESSAGE13);
				result = false;
			}
		};
		YamlLoader loader = TestYamlLoaderProvider.getYamlLoader();
		DebuggerConf instruDef = loader.getInstrumentation();
		UserInputUtil util = new UserInputUtil(loader, null);
		util.getInfo();
		Map<String, LogLevel> logLevelMap = instruDef.getLogLevel();
		LogLevel logLevel = logLevelMap.get("ifblock");
		assertTrue(LogLevel.INFO == logLevel);
	}
}
