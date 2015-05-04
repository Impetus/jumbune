package org.jumbune.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class UtilTest {
	private static final String QUALIFIEDCLASSNAME = "org.jumbune.bigdata.StoreData";
	private static final String INTERNALCLASSNAME = "org/jumbune/bigdata/StoreData";
	@Rule
	public final TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void testArrayContainsPartKey() {
		String[] array = new String[] { "abc", "xyz", "zyx", "yzx" };
		String key = "xyzuv";
		boolean result = CollectionUtil.arrayContainsPartKey(array, key);
		Assert.assertEquals(true, result);
	}

	@Test
	public void testInvalidArrayContainsPartKey() {
		String[] array = new String[] { "abc", "zyx", "yzx" };
		String key = "xyzuv";
		boolean result = CollectionUtil.arrayContainsPartKey(array, key);
		Assert.assertEquals(false, result);
	}

	@Test
	public void testArrayContains() {
		String object1 = "abc";
		String object2 = "def";
		Object[] obj = new Object[] { object1, object2 };
		String Key = object1;
		Assert.assertEquals(true, CollectionUtil.arrayContains(obj, Key));
	}

	@Test
	public void testInvalidArrayContains() {
		String object1 = "abc";
		String object2 = "def";
		Object[] obj = new Object[] { object1, object2 };
		String Key = "abcdef";
		Assert.assertEquals(false, CollectionUtil.arrayContains(obj, Key));
	}

	@Test
	public void testInvalidArrayContainsWithDemoClass() {
		DummyObject one = new DummyObject(1);
		Object[] arrayObj = new Object[] { new DummyObject(2), new DummyObject(3) };
		Assert.assertEquals(false, CollectionUtil.arrayContains(arrayObj, one));
	}

	@Test
	public void testArrayContainsWithDemoClass() {
		DummyObject one = new DummyObject(1);
		Object[] arrayObj = new Object[] { new DummyObject(2), one, new DummyObject(3) };
		Assert.assertEquals(true, CollectionUtil.arrayContains(arrayObj, one));
	}

	@Test
	public void testIsNullOrEmpty1() {
		String checkString = "       ";
		Assert.assertEquals(true, CollectionUtil.isNullOrEmpty(checkString));
	}

	@Test
	public void testIsNullOrEmpty2() {
		String checkString = null;
		Assert.assertEquals(true, CollectionUtil.isNullOrEmpty(checkString));
	}

	@Test
	public void testIsNullOrEmpty3() {
		String checkString = "   kkkk  kkk   ";
		Assert.assertEquals(false, CollectionUtil.isNullOrEmpty(checkString));
	}

	@Test
	public void testReadFileData() throws FileNotFoundException, IOException {
		File tempFile = testFolder.newFile("bank.txt");
		FileWriter writer = new FileWriter(tempFile);
		writer.write("hdoop is a big data framework");
		writer.close();
		String result = ConfigurationUtil.readFileData(tempFile.getPath());
		Assert.assertNotNull(result);
	}

	@Test
	public void testReadEmptyFileData() throws FileNotFoundException, IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String result = ConfigurationUtil.readFileData(tempFile.getPath());
		Assert.assertNull(result);
	}

	@Test(expected = FileNotFoundException.class)
	public void testReadFileDataException() throws IOException {
		ConfigurationUtil.readFileData("/bank/note");
	}

	@Test(expected = FileNotFoundException.class)
	public void testReadFileException() throws IOException {
		ConfigurationUtil.readFileData("/bank/note");
	}

	@Test
	public void testReadFile() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		FileWriter writer = new FileWriter(tempFile);
		writer.write("hdoop is a big data framework");
		writer.close();
		Assert.assertNotNull(ConfigurationUtil.readFile(tempFile.getPath()));
	}

	//@Test
	public void testgetServiceYamlPath() {
		Assert.assertNotNull(JobConfigUtil.getServiceJsonPath());
	}

	@Test
	public void testcreatStringFromList() {
		ArrayList<String> listData = new ArrayList<String>();
		listData.add("aaaa");
		listData.add("bbb");
		listData.add("ccc");
		String result = CollectionUtil.createStringFromList(listData, ".");
		Assert.assertEquals("aaaa.bbb.ccc", result);
	}

	@Test
	public void testNullConvertQualifiedClassNameToInternalName() {
		Assert.assertNull(ConfigurationUtil.convertQualifiedClassNameToInternalName(null));
	}

	@Test
	public void testConvertQualifiedClassNameToInternalName() {
		Assert.assertEquals(INTERNALCLASSNAME, ConfigurationUtil.convertQualifiedClassNameToInternalName(QUALIFIEDCLASSNAME));
	}

	@Test
	public void testConvertInternalClassNameToQualifiedName() {
		Assert.assertEquals(QUALIFIEDCLASSNAME, ConfigurationUtil.convertInternalClassNameToQualifiedName(INTERNALCLASSNAME));
	}

	@Test
	public void testNullConvertInternalClassNameToQualifiedName() {
		Assert.assertNull(ConfigurationUtil.convertInternalClassNameToQualifiedName(null));
	}

	@Test
	public void testWriteToFileFirst() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String filePath = tempFile.getPath();
		ConfigurationUtil.writeToFile(filePath, QUALIFIEDCLASSNAME);
		Assert.assertEquals(QUALIFIEDCLASSNAME.length(), tempFile.length());
	}

	@Test
	public void testWriteToFileSecond() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String filePath = tempFile.getPath();
		ConfigurationUtil.writeToFile(filePath, QUALIFIEDCLASSNAME, false);
		Assert.assertEquals(QUALIFIEDCLASSNAME.length(), tempFile.length());
	}

	@Test
	public void testWriteToFileThird() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String filePath = tempFile.getPath();
		ConfigurationUtil.writeToFile(filePath, QUALIFIEDCLASSNAME, true);
		Assert.assertEquals(QUALIFIEDCLASSNAME.length(), tempFile.length());
	}

	@Test
	public void testwriteToFileFourth() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String filePath = tempFile.getPath();
		ConfigurationUtil.writeToFile(filePath, new DummyObject(1));
		Assert.assertTrue(tempFile.length() > 0);
	}

	@Test
	public void testgetAllClasspathFilesfromfolderSecond() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String[] folders = { tempFile.getParent() };
		String[] excludedFiles = { tempFile.getPath(), "bigdata.txt" };
		String[] files = { "abc.txt" };

		Assert.assertEquals(1, ConfigurationUtil.getAllClasspathFiles(folders, excludedFiles, files).size());
	}

	@Test
	public void testGetAllClasspathFilesfromNullfoldersecond() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String[] folders = null;

		String[] excludedFiles = { tempFile.getPath(), "bigdata.txt" };
		String[] files = { "abc.txt" };

		Assert.assertEquals(1, ConfigurationUtil.getAllClasspathFiles(folders, excludedFiles, files).size());
	}

	@Test
	public void testGetAllClasspathFilesfromNullfilesecond() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		String[] folders = { tempFile.getParent() };
		String[] excludedFiles = { tempFile.getPath(), "bigdata.txt" };
		String[] files = null;

		Assert.assertEquals(0, ConfigurationUtil.getAllClasspathFiles(folders, excludedFiles, files).size());
	}

	@Test
	public void testGetAllFileNamesInDir() throws IOException {
		File tempFile = testFolder.newFile("bank.txt");
		File tempFile2 = testFolder.newFile("note.txt");
		Assert.assertEquals(2, ConfigurationUtil.getAllFileNamesInDir(tempFile.getParent()).size());
	}
}
