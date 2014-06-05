package org.jumbune.web.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.easymock.EasyMock;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.web.utils.WebUtil;
import org.junit.*;
import static org.junit.Assert.*;
import com.google.gson.JsonObject;

public class WebUtilTest {
	private WebUtil fixture = new WebUtil();


	public WebUtil getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testConvetResourceListToString_1()
		throws Exception {
		String[] resourceArray = new String[] {"", "0123456789", "An��t-1.0.txt", null};

		String result = WebUtil.convetResourceListToString(resourceArray);

		assertEquals("\n0123456789\nAn��t-1.0.txt\nnull", result);
	}

	@Test
	public void testConvetResourceListToString_2()
		throws Exception {
		String[] resourceArray = new String[] {""};

		String result = WebUtil.convetResourceListToString(resourceArray);

		assertEquals("", result);
	}

	@Test
	public void testConvetResourceListToString_3()
		throws Exception {
		String[] resourceArray = new String[] {"0123456789"};

		String result = WebUtil.convetResourceListToString(resourceArray);

		assertEquals("0123456789", result);
	}

	@Test
	public void testConvetResourceListToString_4()
		throws Exception {
		String[] resourceArray = new String[] {"", "0123456789", "An��t-1.0.txt", null};
		String separator = "";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("0123456789An��t-1.0.txtnull", result);
	}

	@Test
	public void testConvetResourceListToString_5()
		throws Exception {
		String[] resourceArray = new String[] {""};
		String separator = "0";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("", result);
	}

	@Test
	public void testConvetResourceListToString_6()
		throws Exception {
		String[] resourceArray = new String[] {"0123456789"};
		String separator = "0123456";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("0123456789", result);
	}

	@Test
	public void testConvetResourceListToString_7()
		throws Exception {
		String[] resourceArray = new String[] {""};
		String separator = "";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("", result);
	}

	@Test
	public void testConvetResourceListToString_8()
		throws Exception {
		String[] resourceArray = new String[] {"0123456789"};
		String separator = "0";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("0123456789", result);
	}

	@Test
	public void testConvetResourceListToString_9()
		throws Exception {
		String[] resourceArray = new String[] {"", "0123456789", "An��t-1.0.txt", null};
		String separator = "0123456";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("012345601234567890123456An��t-1.0.txt0123456null", result);
	}

	@Test
	public void testConvetResourceListToString_10()
		throws Exception {
		String[] resourceArray = new String[] {"0123456789"};
		String separator = "";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("0123456789", result);
	}

	@Test
	public void testConvetResourceListToString_11()
		throws Exception {
		String[] resourceArray = new String[] {"", "0123456789", "An��t-1.0.txt", null};
		String separator = "0";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("001234567890An��t-1.0.txt0null", result);
	}

	@Test
	public void testConvetResourceListToString_12()
		throws Exception {
		String[] resourceArray = new String[] {""};
		String separator = "0123456";

		String result = WebUtil.convetResourceListToString(resourceArray, separator);

		assertEquals("", result);
	}

	@Test
	public void testGetLastIndexOfArray_1()
		throws Exception {
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};
		String dependentJarDir = "";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetLastIndexOfArray_2()
		throws Exception {
		String[] resources = new String[] {""};
		String dependentJarDir = "0123456789";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetLastIndexOfArray_3()
		throws Exception {
		String[] resources = new String[] {"0123456789"};
		String dependentJarDir = "0123456789";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetLastIndexOfArray_4()
		throws Exception {
		String[] resources = new String[] {""};
		String dependentJarDir = "";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetLastIndexOfArray_5()
		throws Exception {
		String[] resources = new String[] {"0123456789"};
		String dependentJarDir = "";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetLastIndexOfArray_6()
		throws Exception {
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};
		String dependentJarDir = "0123456789";

		boolean result = WebUtil.getLastIndexOfArray(resources, dependentJarDir);

		assertEquals(false, result);
	}

	@Test
	public void testGetPropertyFromResource_1()
		throws Exception {
		String propertyFile = "";
		String propertyName = "";

		String result = WebUtil.getPropertyFromResource(propertyFile, propertyName);

		assertEquals(null, result);
	}




	@Test
	public void testGetPropertyFromResource_4()
		throws Exception {
		String propertyFile = "";
		String propertyName = "0123456789";

		String result = WebUtil.getPropertyFromResource(propertyFile, propertyName);

		assertEquals(null, result);
	}

	@Test
	public void testGetTabsInformation_fixture_1()
		throws Exception {
		WebUtil fixture2 = getFixture();
		YamlConfig config = new YamlConfig();

		String result = fixture2.getTabsInformation(config);

		assertEquals("", result);
	}



	@Test
	public void testGetYamlConfFromFile_fixture_3()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = File.createTempFile("0123456789", "0123456789");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertEquals(null, result);
	}

	@Test
	public void testGetYamlConfFromFile_fixture_4()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = File.createTempFile("0123456789", "0123456789", (File) null);

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertEquals(null, result);
	}

	@Test
	public void testGetYamlConfFromFile_fixture_5()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = File.createTempFile("An��t-1.0.txt", "An��t-1.0.txt", (File) null);

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertEquals(null, result);
	}


	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetYamlConfFromFile_fixture_7()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = new File("");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetYamlConfFromFile_fixture_8()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = new File("", "");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetYamlConfFromFile_fixture_9()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = new File("0123456789", "0123456789");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetYamlConfFromFile_fixture_10()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = new File((File) null, "");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertNotNull(result);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void testGetYamlConfFromFile_fixture_11()
		throws Exception {
		WebUtil fixture2 = getFixture();
		File file = new File((File) null, "0123456789");

		YamlConfig result = fixture2.getYamlConfFromFile(file);

		assertNotNull(result);
	}

	@Test
	public void testIsRequiredModuleEnable_1()
		throws Exception {
		YamlConfig config = new YamlConfig();

		boolean result = WebUtil.isRequiredModuleEnable(config);

		assertEquals(false, result);
	}


	@Test
	public void testPrepareYamlConfig_1()
		throws Exception {
		String data = "";

		YamlConfig result = WebUtil.prepareYamlConfig(data);

		assertEquals(null, result);
	}




	@Test
	public void testRemoveAndAddJsonAttribute_1()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "";
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_2()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {""};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_3()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "";
		String[] resources = new String[] {"0123456789"};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_4()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "";
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_5()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {""};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_6()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "";
		String[] resources = new String[] {"0123456789"};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_7()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "";
		String[] resources = new String[] {""};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_8()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {"0123456789"};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_9()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_10()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "0123456789";
		String newAttributeValue = "";
		String[] resources = new String[] {""};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_11()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {"0123456789"};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}

	@Test
	public void testRemoveAndAddJsonAttribute_12()
		throws Exception {
		JsonObject jsonObject = new JsonObject();
		String attributeValue = "";
		String newAttributeValue = "0123456789";
		String[] resources = new String[] {"", "0123456789", "An��t-1.0.txt", null};

		WebUtil.removeAndAddJsonAttribute(jsonObject, attributeValue, newAttributeValue, resources);

	}


	@Test
	public void testSubString_3()
		throws Exception {
		String value = "0123456";

		String result = WebUtil.subString(value);

		assertEquals("12345", result);
	}


	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}