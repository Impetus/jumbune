package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.utils.exception.JumbuneException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;



/**
 * This class implements utility methods being used in the framework.
 * <p>
 * </p>
 * 
 
 */
public final class ConfigurationUtil {
	
	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(ConfigurationUtil.class);

	
	
	/** The Constant DOT. */
	private static final char DOT = '.';
	
	/** The Constant SLASH. */
	private static final char SLASH = '/';

	/**
	 * Instantiates a new configuration util.
	 */
	private ConfigurationUtil()
	{
		
	}
	
	/**
	 * This method reads file data and returns a string. If no data found null will be returned
	 *
	 * @param fileName the file name
	 * @return the string
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readFileData(String fileName) throws IOException {
		LOGGER.debug("Reading data from file." + fileName);
		StringBuilder fileData = new StringBuilder();

		String lineSeparator = System.getProperty("line.separator");
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				fileData.append(line);
				fileData.append(lineSeparator);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		if (fileData != null && fileData.length() > 0) {
			return fileData.toString();
		}
		return null;
	}

	/**
	 * This method reads a file at specfied path and returns its inputStream.
	 *
	 * @param filePath - Path of the file whose input stream is desired
	 * @return InputStream of the file
	 * @throws FileNotFoundException the file not found exception
	 */
	public static InputStream readFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		return new FileInputStream(file);
		
	}

	/**
	 * This utility will write given data to specified file name.
	 *
	 * @param fileName - Name of file in which data should be written
	 * @param data - data to be written
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeToFile(String fileName, String data) throws IOException {
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(fileName));
			output.write(data);
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * This utility method will check if it has to create a file or not. If boolean is set to true it will check if the file does not exist it will
	 * create a new file and then will write given data to specified file name
	 *
	 * @param fileName - Name of file in which data should be written
	 * @param data - data to be written
	 * @param createFile - specify if a file does not it creates it or not
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeToFile(String fileName, String data, boolean createFile) throws IOException {
		if (createFile) {
			File file = new File(fileName);

			if (!file.exists()) {
				String parent = file.getParent();
				File parentDir = new File(parent);
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}
		}
		writeToFile(fileName, data);
	}

	/**
	 * This method will serialize the object and write this serialized object to specified file.
	 *
	 * @param fileName - file name in which serialized object should be written
	 * @param dataObject - The object which is to be serialized and written to file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeToFile(String fileName, Object dataObject) throws IOException {
		FileOutputStream fileStream = null;
		ObjectOutputStream os = null;
		try {
			fileStream = new FileOutputStream(fileName);
			os = new ObjectOutputStream(fileStream);
			os.writeObject(dataObject);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * This method gets list of all the classpath files
	 * 
	 * <ul>
	 * <li>All the files are picked in all the specified folders
	 * <li>All the specified excluded files are excluded from the above list
	 * <li>All the specified files are picked
	 * </ul>.
	 *
	 * @param folders All the files in these folders
	 * @param excludedFiles The files which are in these folders and are to be excluded
	 * @param files All the files to be copied
	 * @return List of all the files
	 */
	public static List<String> getAllClasspathFiles(String[] folders, String[] excludedFiles, String[] files) {
		List<String> fileList = new ArrayList<String>();

		if (folders != null) {
			// In each folder
			for (String folderName : folders) {
				File folder = new File(folderName);

				if (folder.exists() && folder.isDirectory()) {
					
						File[] libs = folder.listFiles();
						for (File lib : libs) {
							// file is included
							if (excludedFiles == null || !CollectionUtil.arrayContains(excludedFiles, lib.getPath())) {
									ConsoleLogUtil.LOGGER.debug("Included cleasspath file ["+ lib.getPath()+"]");
									LOGGER.debug("Included cleasspath file ["+ lib.getPath()+"]");
								fileList.add(lib.getPath());
							} else {
								// file is excluded
									ConsoleLogUtil.LOGGER.debug("Excluded file from classpath [" + lib.getPath()+"]");
									LOGGER.debug("Excluded file from classpath [" + lib.getPath()+"]");
							}
						}
					
				}
			}
		}

		addDebuggerClassPathFiles(files, fileList);

		return fileList;
	}
	
	/**
	 * Adds the debugger class path files.
	 *
	 * @param files   All the files to be copied
	 * @param fileList List of all the files
	 */
	private static void addDebuggerClassPathFiles(String[] files,
			List<String> fileList) {
		if (files != null) {
			for (String file : files) {
					ConsoleLogUtil.LOGGER.debug("Added file to debugger file [" + file+"]");
					LOGGER.debug("Added file to debugger file [" + file+"]");
				fileList.add(file);
			}
		}
	}

	/**
	 * Gets the all file names in dir.
	 *
	 * @param folderName the folder name
	 * @return the all file names in dir
	 */
	public static List<String> getAllFileNamesInDir(String folderName) {
		List<String> fileList = new ArrayList<String>();
		File folder = new File(folderName);
		if (folder.exists() && folder.isDirectory()) {
			
				File[] libs = folder.listFiles();
				for (File lib : libs) {
					fileList.add(lib.getPath());
				}
			
		}

		return fileList;
	}

	/**
	 * This method converts given qualified class name to internal name by replacing . with /
	 *
	 * @param className the class name
	 * @return the string
	 */
	public static String convertQualifiedClassNameToInternalName(String className) {
		if (className == null) {
			return className;
		}
		return className.replace(DOT, SLASH);
	}

	/**
	 * This method converts given qualified class name to internal name by replacing / with .
	 * 
	 * @param className
	 *            name to be converted
	 * @return converted class name
	 */
	public static String convertInternalClassNameToQualifiedName(String className) {
		if (className == null) {
			return className;
		}
		return className.replace(SLASH, DOT);
	}


	/**
	 * It copies a file from one location to another. The name of file at destination folder will remain same
	 *
	 * @param sourceFilePath - absolute path of file which is to be copied
	 * @param destinationLoc - absolute path of the location where this file has to be copied
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void copyFileToDestinationLocation(String sourceFilePath, String destinationLoc) throws IOException {
		LOGGER.debug("Copying yaml file to destination folder " + sourceFilePath + " destination path " + destinationLoc);
		String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf('/') + 1);
		File destinationFolder = new File(destinationLoc, fileName);
		InputStream inputStream = null;
		OutputStream out = null;
		try {
			inputStream = new FileInputStream(new File(sourceFilePath));
			File parent = destinationFolder.getParentFile();

			if (!parent.exists()) {
				parent.mkdirs();
			}

			out = new FileOutputStream(destinationFolder);
			byte buf[] = new byte[Constants.ONE_ZERO_TWO_FOUR];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 * Reads email configuration properties.
	 *
	 * @return the properties
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Properties readConfigurationFromProperties() throws IOException {
		Properties prop = new Properties();
		prop.load(ConfigurationUtil.class.getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE));
		prop.getProperty(Constants.FROM);
		prop.getProperty(Constants.TO);
		prop.getProperty(Constants.SUBJECT);
		return prop;
	}

	/**
	 * Gets the xml document from file.
	 *
	 * @param filePath the file path
	 * @return the xml document from file
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Document getXmlDocumentFromFile(String filePath) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Update xml document on file.
	 *
	 * @param doc the doc
	 * @param filePath the file path
	 * @throws TransformerException the transformer exception
	 */
	public static void updateXmlDocumentOnFile(Document doc, String filePath) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filePath));
		transformer.transform(source, result);
	}

	/**
	 * This method reads a file Jumbune.yaml to load all the Classpath information related to jumbune supplied jars
	 *
	 * @return - Classpath element for JumbuneSupplied jars
	 * @throws JumbuneException the hTF exception
	 */
	public static ClasspathElement loadJumbuneSuppliedJarList() throws JumbuneException {
		InputStream input = null;
		ClasspathElement classpathEle;
		input = ConfigurationUtil.class.getResourceAsStream("/InstrumentationDependency.yaml");

		Constructor constructor = new Constructor(ClasspathElement.class);

		TypeDescription desc = new TypeDescription(ClasspathElement.class);
		constructor.addTypeDescription(desc);
		Yaml yaml = new Yaml(constructor);
		classpathEle = (ClasspathElement) yaml.load(input);

		return classpathEle;
	}

	/**
	 * Adds the slave ranges.
	 *
	 * @param fromRangeVal the from range val
	 * @param toRangeVal the to range val
	 * @param hostRangeValue the host range value
	 * @param hosts the hosts
	 */
	public static void addSlaveRanges(int fromRangeVal, int toRangeVal,
			String hostRangeValue, List<String> hosts) {

		String hostRangeValFinal;
		for (int ipCount = fromRangeVal; ipCount <= toRangeVal; ipCount++) {
			hostRangeValFinal = hostRangeValue + Constants.DOT + ipCount;
			if (ConfigurationUtil.checkIPAdress(hostRangeValFinal)
					&& !hosts.contains(hostRangeValFinal)) {
				hosts.add(hostRangeValFinal);
			}
		}

	}
	
	/**
	 * check whether given ip address is valid or not.
	 *
	 * @param ipaddress the ipaddress
	 * @return true if ip adress is valid
	 */
	public static Boolean checkIPAdress(String ipaddress) {
		try {
			return InetAddress.getByName(ipaddress).isReachable(Constants.FIVE_HUNDRED);
		} catch (Exception e) {
			return false;
		}
	}
}