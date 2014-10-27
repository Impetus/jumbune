package org.jumbune.common.yaml.config;
import java.io.IOException;

import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class YamlLoaderTest{
	public static String param = "'-agentlib=hprof=format=b,force=n,thread=y,verbose=n,file=%s,cpu=samples,heap=sites'";
	private Loader testYamlLoader = null;
	@Before
	public void getYamlLoader() {
		testYamlLoader = YamlLoaderProviderTest.getYamlLoader();
	}

		
	
	

	 @Test
	public void testYamlLoaderGetInstrumentation() throws IOException {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		DebuggerConf id = yamlLoader.getInstrumentation();
		Assert.assertTrue(!(id.getLogLevel().isEmpty()));
	}

	

	 @Test
	public void testYamlLoaderGetLogLevelForNull() throws IOException {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		String id = yamlLoader.getLogLevel("");
		Assert.assertTrue(id == null);
	}

	

	@Test
	public void testIsInstrumentEnabledForNull() {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		boolean id = yamlLoader.isInstrumentEnabled("");
		Assert.assertTrue(!id);
	}


	@Test
	public void testGetClasspathOutputType() {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertTrue(4 == yamlLoader.getClasspathOutputType(0));
	}
	@Test
	public void testGetClasspathFolders() {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertTrue(yamlLoader.getClasspathFolders(0).length > 0);
	}


	 @Test
	public void testGetClasspathResources() {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertTrue(yamlLoader.getClasspathResources(0) == null);
	}

	 @Test
	public void testGetClasspathExcludes() {
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertTrue(yamlLoader.getClasspathExcludes(0).length > 0);
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
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertEquals(expected,yamlLoader.getProfilingMaxHeapSampleCount());
	}

	@Test
	public void testGetProfilingMaxCPUSampleCount(){
		int expected = 10;
		 YamlLoader yamlLoader = (YamlLoader)testYamlLoader;
		Assert.assertEquals(expected,yamlLoader.getProfilingMaxCPUSampleCount());
	}
}
