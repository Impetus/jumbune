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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
import org.jumbune.common.beans.Doc;
import org.jumbune.common.beans.DqtViewBean;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.Property;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.utils.Versioning;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.Gson;


/**
 * This class implements utility methods being used in the framework.
 * <p>
 * </p>
 * 
 
 */
public class ConfigurationUtil {
	
	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(ConfigurationUtil.class);

	
	
	/** The Constant DOT. */
	private static final char DOT = '.';
	
	/** The Constant SLASH. */
	private static final char SLASH = '/';
	
	private static final short standardMapReduceChildJavaOpts = 200;
	
	/**
	 * Instantiates a new configuration util.
	 */
	protected ConfigurationUtil()
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
		LOGGER.debug("Copying json file to destination folder " + sourceFilePath + " destination path " + destinationLoc);
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
	 * This method is to load all the Classpath information related to jumbune supplied jars
	 *
	 * @return - Classpath element for JumbuneSupplied jars
	 * @throws JumbuneException, the Jumbune exception
	 */
	public static ClasspathElement loadJumbuneSuppliedJarList() throws JumbuneException {
		ClasspathElement classpathElement = new ClasspathElement();
		String[] files = {"AGENT_HOMElib/jumbune-utils-"+ Versioning.COMMUNITY_BUILD_VERSION + Versioning.COMMUNITY_DISTRIBUTION_NAME + ".jar","AGENT_HOMElib/log4j-api-2.1.jar","AGENT_HOMElib/log4j-core-2.1.jar"};
		classpathElement.setFiles(files);
		classpathElement.setSource(-1);
		return classpathElement;
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
	
	public static int getJavaOptsinMB(String stringMapJavaOpts) {
		
		if(stringMapJavaOpts.contains("-D") && stringMapJavaOpts.contains("-Xmx")){
			String[] splits = stringMapJavaOpts.split("-Xmx");
			stringMapJavaOpts = "-Xmx" + splits[1];
		}else if(stringMapJavaOpts.contains("-D")){
			return 0;
		}
		int memoryDenotationIndex = getLastIndexofJVMMemorySettings(stringMapJavaOpts);
		char ch = getDenotationOfJVMMemorySettings(stringMapJavaOpts);
		switch (ch) {
		case 'k': case 'K':{
			return Integer.parseInt(stringMapJavaOpts.substring(stringMapJavaOpts.lastIndexOf('x') + 1, memoryDenotationIndex)) / 1024;
		}
		case 'm':case 'M':{
			return Integer.parseInt(stringMapJavaOpts.substring(stringMapJavaOpts.lastIndexOf('x') + 1, memoryDenotationIndex));
		}
		case 'g': case 'G':{
			return Integer.parseInt(stringMapJavaOpts.substring(stringMapJavaOpts.lastIndexOf('x') + 1, memoryDenotationIndex)) * 1024;
		}
	}
		return standardMapReduceChildJavaOpts;
	}

	private static int getLastIndexofJVMMemorySettings(String stringMapJavaOpts) {
		int index = -1;
		if ((index = stringMapJavaOpts.lastIndexOf('k')) != -1) {
			return index;
		} else if ((index = stringMapJavaOpts.lastIndexOf('K')) != -1) {
			return index;
		} else if ((index = stringMapJavaOpts.lastIndexOf('m')) != -1 && index != 2) {
			return index;
		} else if ((index = stringMapJavaOpts.lastIndexOf('M')) != -1) {
			return index;
		} else if ((index = stringMapJavaOpts.lastIndexOf('g')) != -1) {
			return index;
		} else if ((index = stringMapJavaOpts.lastIndexOf('G')) != -1) {
			return index;
		}
		return index;
	}

	private static char getDenotationOfJVMMemorySettings(String stringMapJavaOpts) {
		int index = -1;
		if (stringMapJavaOpts.lastIndexOf('k') != -1) {
			return 'k';
		} else if (stringMapJavaOpts.lastIndexOf('K') != -1) {
			return 'K';
		} else if ((index = stringMapJavaOpts.lastIndexOf('m')) != -1 && index != 2) {
			return 'm';
		} else if ((index = stringMapJavaOpts.lastIndexOf('M')) != -1) {
			return 'M';
		} else if ((index = stringMapJavaOpts.lastIndexOf('g')) != -1) {
			return 'g';
		} else if ((index = stringMapJavaOpts.lastIndexOf('G')) != -1) {
			return 'G';
		}
		return (char) -1;
	}
	
	/**
	 * Fetches the details of the already run dqt job.
	 *
	 * @return the dqt view details
	 */
	public static List<DqtViewBean> getDqtViewDetails() {
		Gson gson = new Gson();
		JobConfig jobConfig = null;
		DqtViewBean dqtViewBean = null ;
		List<DqtViewBean> dqtViewBeans = new ArrayList<DqtViewBean>();
		StringBuilder sb = new StringBuilder();
		String directoryPath = sb.append(JumbuneInfo.getHome()).append("ScheduledJobs")
				.append(File.separator).append("IncrementalDQJobs").append(File.separator).toString();
		
		Properties prop = new Properties();
		File jobStatusFile = null ;
		File f = new File(directoryPath);
		FileInputStream fileInputStream = null;
		File file = null;
		sb.setLength(0);
		sb = new StringBuilder();
		if(f.isDirectory() && f.exists()){
				File[] fileArray = f.listFiles();
				for (int i = 0; i < fileArray.length; i++) {
					dqtViewBean = new DqtViewBean();
					file = new File(fileArray[i] + File.separator + "scheduledJson.json");
					jobStatusFile = new File(fileArray[i] + File.separator + "jobstatus");
					if(file.exists()){
						String json = null;
						try {
							json = FileUtil.readFileIntoString(file.toString());
							jobConfig = gson.fromJson(json, JobConfig.class);
							dqtViewBean.setHdfsPath(jobConfig.getHdfsInputPath());
							dqtViewBean.setJobName(jobConfig.getJumbuneJobName());
							dqtViewBean.setOperatingCluster(jobConfig.getOperatingCluster());
							dqtViewBean.setRecuringInterval(jobConfig.getDataQualityTimeLineConfig().getSchedulingEvent().toString());
							
							if(jobStatusFile.exists()){
								fileInputStream = new FileInputStream(jobStatusFile);
								prop.load(fileInputStream);
								String lastExecutedTime = prop.getProperty("lastExecutedTime");
								Date date = new Date(Long.valueOf(lastExecutedTime));
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm" );
								dqtViewBean.setLastExecutedTime(simpleDateFormat.format(date));
							}
						} catch (IOException e) {
							LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
						}finally{
							if(fileInputStream != null){
								try {
									fileInputStream.close();
								} catch (IOException e) {
									LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
								}
							}
						}
						dqtViewBeans.add(dqtViewBean);
					}
				}
		}
			return dqtViewBeans;
	}
	
	/**
	 * Gets the local configuration file path.
	 *
	 * @param cluster the cluster
	 * @return the local configuration file path
	 */
	public static String getLocalConfigurationFilePath(Cluster cluster){
		String clusterName = cluster.getClusterName() + File.separator;
		String destinationReceiveDir = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + clusterName;
		return destinationReceiveDir;
	}

	/** This method parses the xml file and returns the property value according to the key
	 * @param filePath : The path of the xml file to be read
	 * @param key : The property to be retrived from the xml file
	 * @return : Returns the property value
	 * 
	 */
	public static String readProperty(String filePath, String key) {
		Doc doc;
		try {
			if (new File(filePath).length() == 0) {
				return null;
			}
			doc = (Doc) JAXBContext.newInstance(Doc.class).createUnmarshaller()
					.unmarshal(new FileInputStream(filePath));
			for (Property p : doc.configuration) {
				if (p.name.equals(key)) {
					return p.value.trim();
				}
			}
		} catch (FileNotFoundException | JAXBException e) {
			LOGGER.error("Unable to read file [" + filePath + "]" + "property[ " + key + "]" ,e);
		}
		return null;
	}


}		
			