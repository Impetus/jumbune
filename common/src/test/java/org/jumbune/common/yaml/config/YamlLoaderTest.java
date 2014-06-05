package org.jumbune.common.yaml.config;
import java.io.IOException;
import java.io.InputStream;

import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.yaml.config.TestYamlLoaderProvider;
import org.jumbune.common.yaml.config.YamlLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class YamlLoaderTest{
	public static String param = "'-agentlib=hprof=format=b,force=n,thread=y,verbose=n,file=%s,cpu=samples,heap=sites'";
	private YamlLoader testYamlLoader = null;
	@Before
	public void getYamlLoader() {
		testYamlLoader = TestYamlLoaderProvider.getYamlLoader();
	}

	//@Test(expected = RuntimeException.class)
	public void yamlLoaderTestArgsYamlConfig() throws IOException {

		InputStream iStream = new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		YamlLoader loader = new YamlLoader(iStream);
	}

	public void testYamlLoader() throws IOException {
		String jHome = System.getenv("JUMBUNE_HOME");
		if (!jHome.endsWith("/")) {
			jHome += "/";
		}

		Assert.assertTrue(("192.168.49.71".equals(testYamlLoader.getMasterInfo().getHost())
				&& ("bin/hadoop".equals(testYamlLoader.getHadoopCommand())) && ("0-5".equals(testYamlLoader.getHadoopJobProfileMaps()))
				&& (param.equals(testYamlLoader.getHadoopJobProfileParams())) && ("0-5".equals(testYamlLoader.getHadoopJobProfileReduces()))
				&& ("jar".equals(testYamlLoader.getHadoopCommandType())) && (10 == testYamlLoader.getProfilingMaxHeapSampleCount())
				&& (10 == (testYamlLoader.getProfilingMaxCPUSampleCount()))
				&& ((jHome + Constants.JMX_FILE_LOC).equals(testYamlLoader.getMasterJmxFileLocation()))
				&& (testYamlLoader.getHadoopHome().equals("/navinhadoop-0.20.2/"))
				&& ("/Desktop/PortPSChain-11Apr2012.jar".equals(testYamlLoader.getInputFile())) && (testYamlLoader.isHadoopJobProfileEnabled())
				&& (50 == testYamlLoader.getPartitionerSampleInterval()) && (testYamlLoader.getSlavesInfo().size() > 0)
				&& (testYamlLoader.getMaxIfBlockNestingLevel() > 0)
				&& ("/home/imadmin/jar/profile/PortPSChain-11Apr2012_p.jar".equals(testYamlLoader.getProfiledOutputFile()))
				&& ((jHome + Constants.CONSOLIDATED_LOG_LOC).equals(testYamlLoader.getMasterConsolidatedLogLocation()))
				&& ((jHome + Constants.SUMMARY_FILE_LOC + Constants.M_PURE_COUNTER_FILE).equals(testYamlLoader.getPureJarCounterLocation()))
				&& (!testYamlLoader.getJobDefinitionList().isEmpty()) && (testYamlLoader.getInstrumentation().getMaxIfBlockNestingLevel()) > 0));
	}

	// @Test
	public void testYamlLoaderNullInput() throws IOException {
		InputStream is = null;
		String jHome = System.getenv("JUMBUNE_HOME");
		if (!jHome.endsWith("/")) {
			jHome += "/";
		}
		YamlLoader loader = new YamlLoader(is);
		Assert.assertTrue((loader.getInstrumentation().getMaxIfBlockNestingLevel()) > 0);
	}

	public void testYamlLoaderIS() throws IOException {
		Assert.assertTrue("192.168.49.71".equals(testYamlLoader.getMasterInfo().getHost()));
	}

	 @Test
	public void testYamlLoaderGetInstrumentation() throws IOException {
		DebuggerConf id = testYamlLoader.getInstrumentation();
		Assert.assertTrue(!(id.getLogLevel().isEmpty()));
	}

	// @Test
	public void testYamlLoaderGetLogLevel() throws IOException {
		String id = testYamlLoader.getLogLevel("switchcase");
		Assert.assertTrue(id.equals("info"));
	}

	 @Test
	public void testYamlLoaderGetLogLevelForNull() throws IOException {
		String id = testYamlLoader.getLogLevel("");
		Assert.assertTrue(id == null);
	}

	// @Test
	public void isInstrumentEnabledTest() {
		boolean id = testYamlLoader.isInstrumentEnabled("switchcase");
		Assert.assertTrue(id);
	}

	 @Test
	public void testIsInstrumentEnabledForNull() {
		boolean id = testYamlLoader.isInstrumentEnabled("");
		Assert.assertTrue(!id);
	}

//	 @Test
	public void testGetLogDefinition() {
		LogConsolidationInfo logConsolidationInfo = testYamlLoader.getLogDefinition();
		Assert.assertTrue("192.168.49.71".equals(logConsolidationInfo.getMaster().getHost()));
	}

	 @Test
	public void testGetClasspathOutputType() {
		Assert.assertTrue(4 == testYamlLoader.getClasspathOutputType(0));
	}

	 @Test
	public void testGetClasspathFolders() {
		Assert.assertTrue(testYamlLoader.getClasspathFolders(0).length > 0);
	}

//	 @Test
	public void testGetClasspathFiles() {
		Assert.assertTrue(testYamlLoader.getClasspathFiles(0).length == 1);
	}

	 @Test
	public void testGetClasspathResources() {
		Assert.assertTrue(testYamlLoader.getClasspathResources(0) == null);
	}

	 @Test
	public void testGetClasspathExcludes() {
		Assert.assertTrue(testYamlLoader.getClasspathExcludes(0).length > 0);
	}

//	@Test
	public void testIsHadoopJobProfileEnabled() {
		boolean id = testYamlLoader.isHadoopJobProfileEnabled();
		Assert.assertTrue(!id);
	}

	@Test
	public void testGetHadoopCommand(){
	String expected = Constants.H_COMMAND;
	Assert.assertEquals(expected,YamlLoader.getHadoopCommand());
	}

	@Test
	public void testGetHadoopCommandType(){
		String expected = Constants.H_COMMAND_TYPE;
		Assert.assertEquals(expected,YamlLoader.getHadoopCommandType());
	}

	@Test
	public void testGetProfilingMaxHeapSampleCount(){
		int expected = 10;
		Assert.assertEquals(expected,testYamlLoader.getProfilingMaxHeapSampleCount());
	}

	@Test
	public void testGetProfilingMaxCPUSampleCount(){
		int expected = 10;
		Assert.assertEquals(expected,testYamlLoader.getProfilingMaxCPUSampleCount());
	}
}
