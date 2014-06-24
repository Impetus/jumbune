package org.jumbune.common.yaml.config;
import java.io.IOException;

import org.jumbune.common.beans.DebuggerConf;
import org.jumbune.common.utils.Constants;
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

		
	
	

	 @Test
	public void testYamlLoaderGetInstrumentation() throws IOException {
		DebuggerConf id = testYamlLoader.getInstrumentation();
		Assert.assertTrue(!(id.getLogLevel().isEmpty()));
	}

	

	 @Test
	public void testYamlLoaderGetLogLevelForNull() throws IOException {
		String id = testYamlLoader.getLogLevel("");
		Assert.assertTrue(id == null);
	}

	

	 @Test
	public void testIsInstrumentEnabledForNull() {
		boolean id = testYamlLoader.isInstrumentEnabled("");
		Assert.assertTrue(!id);
	}


	 @Test
	public void testGetClasspathOutputType() {
		Assert.assertTrue(4 == testYamlLoader.getClasspathOutputType(0));
	}

	 @Test
	public void testGetClasspathFolders() {
		Assert.assertTrue(testYamlLoader.getClasspathFolders(0).length > 0);
	}


	 @Test
	public void testGetClasspathResources() {
		Assert.assertTrue(testYamlLoader.getClasspathResources(0) == null);
	}

	 @Test
	public void testGetClasspathExcludes() {
		Assert.assertTrue(testYamlLoader.getClasspathExcludes(0).length > 0);
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
