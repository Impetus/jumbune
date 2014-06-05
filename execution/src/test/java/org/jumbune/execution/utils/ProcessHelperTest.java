package org.jumbune.execution.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.YamlConfigUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.TestYamlLoaderProvider;
import org.jumbune.execution.utils.ProcessHelper;
import org.junit.Before;
import org.junit.Test;


public class ProcessHelperTest {
	ProcessHelper pHelper;
	YamlLoader testYamlLoader;

	@Before
	public void setup() {
		pHelper = new ProcessHelper();
		testYamlLoader = TestYamlLoaderProvider.getYamlLoader();
	}

	@Test
	public void testWritetoServiceFile() {
		ServiceInfo sInfo = new ServiceInfo();
		Assert.assertTrue(pHelper.writetoServiceFile(sInfo));
	}

	// @Test(expected = IOException.class)
	public void testExecuteJar() throws IOException {
		String inputJarPath = YamlConfigUtil.getServiceYamlPath();
		pHelper.executeJar(inputJarPath, false, testYamlLoader, false);
	}

	@Test
	public void testValidateJobs() throws IOException {
		List<JobDefinition> yamlJobDefList = new ArrayList<JobDefinition>();
		List<String> jarJobList = new ArrayList<String>();
		JobDefinition jf = new JobDefinition();
		jf.setJobClass("mapper.class");
		jf.setName("mapper");
		jarJobList.add("mapper.class");
		yamlJobDefList.add(jf);
		boolean check = pHelper.validateJobs(yamlJobDefList, jarJobList);
		Assert.assertTrue(check);
	}

	@Test
	public void testValidateJobsFalse() throws IOException {
		List<JobDefinition> yamlJobDefList = new ArrayList<JobDefinition>();
		List<String> jarJobList = new ArrayList<String>();
		JobDefinition jf = new JobDefinition();
		jf.setJobClass("mapper.class");

		jarJobList.add("mapp");
		yamlJobDefList.add(jf);
		boolean check = pHelper.validateJobs(yamlJobDefList, jarJobList);
		Assert.assertFalse(check);
	}

	@Test
	public void testValidateJobsJobListNull() throws IOException {
		List<JobDefinition> yamlJobDefList = new ArrayList<JobDefinition>();
		boolean check = pHelper.validateJobs(yamlJobDefList, null);
		Assert.assertFalse(check);
	}

}
