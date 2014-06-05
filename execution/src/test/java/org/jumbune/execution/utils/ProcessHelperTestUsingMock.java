package org.jumbune.execution.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;

import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.utils.YamlUtil;
import org.junit.Before;
import org.junit.Test;


public class ProcessHelperTestUsingMock {
	@Mocked
	YamlUtil yamlUtil;
	ProcessHelper pHelper;

	@Before
	public void setup() {
		pHelper = new ProcessHelper();
	}

	@Test
	public void testWritetoServiceFile() {
		final ServiceInfo sInfo = new ServiceInfo();
		new Expectations() {
			{
				YamlUtil.serializeObjectToYaml(sInfo);
				result = new IOException();
			}
		};

		Assert.assertTrue(!pHelper.writetoServiceFile(sInfo));
	}

	@Test
	public void testReadServiceInfo() throws FileNotFoundException {
		final ServiceInfo sInfo = new ServiceInfo();
		sInfo.setDataValidationResultLocation("home");
		final String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceYamlPath();
		new Expectations() {
			{
				YamlUtil.loadYaml(serviceYamlPath);
				result = sInfo;
			}
		};
		ServiceInfo newSInfo;
		newSInfo = pHelper.readServiceInfo();
		Assert.assertTrue(newSInfo.getDataValidationResultLocation().equals("home"));
	}

	@Test
	public void testExceptionReadServiceInfo() throws FileNotFoundException {
		final ServiceInfo sInfo = new ServiceInfo();
		sInfo.setDataValidationResultLocation("home");
		final String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceYamlPath();
		new Expectations() {
			{
				YamlUtil.loadYaml(serviceYamlPath);
				result = new FileNotFoundException();
			}
		};
		ServiceInfo newSInfo;
		newSInfo = pHelper.readServiceInfo();
		Assert.assertTrue(newSInfo == null);
	}
}