package org.jumbune.execution.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;

import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.utils.YamlUtil;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;


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
		final Gson gson = new Gson();
		final ServiceInfo sInfo = new ServiceInfo();
		new Expectations() {
			{
				gson.toJson(sInfo,ServiceInfo.class);		
				result = new IOException();
			}
		};

		Assert.assertTrue(!pHelper.writetoServiceFile(sInfo));
	}

	@Test
	public void testReadServiceInfo() throws FileNotFoundException {
		final ServiceInfo sInfo = new ServiceInfo();
		final Gson gson = new Gson();
		sInfo.setDataValidationResultLocation("home");
		final String serviceJsonPath = org.jumbune.common.utils.YamlConfigUtil.getServiceJsonPath();
		new Expectations() {
			{
				gson.fromJson(new FileReader(serviceJsonPath),ServiceInfo.class);
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
		final Gson gson = new Gson();
		sInfo.setDataValidationResultLocation("home");
		final String serviceJsonPath = org.jumbune.common.utils.YamlConfigUtil.getServiceJsonPath();
		new Expectations() {
			{
				gson.fromJson(new FileReader(serviceJsonPath),ServiceInfo.class);
				result = new FileNotFoundException();
			}
		};
		ServiceInfo newSInfo;
		newSInfo = pHelper.readServiceInfo();
		Assert.assertTrue(newSInfo == null);
	}
}