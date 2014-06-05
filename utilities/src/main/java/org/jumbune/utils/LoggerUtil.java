package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.SEPARATOR_UNDERSCORE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.logging.log4j.core.LoggerContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This class loads log4j properties (appenders etc.) into the existing log4j.properties at run time
 * 
 */
public final class LoggerUtil {
	private static final Logger LOG = LogManager.getLogger(LoggerUtil.class);

	private static final String LOG_CATEGORY = "jumbune.instrument";
	private static final String LOG_APPENDER_NAME = "JumbuneAppender";
	private static final String LOG_PATTERN = "%m%n";
	private static final String LOG_FILE_EXT = ".log";
	private static final String LOG_ELEMENT = "logger";
	private static final String NAME = "name";
	private static final String LOG_LEVEL = "level";
	private static final String LOG_LEVEL_INFO = "info";
	private static final String ADDITIVITY = "additivity";
	private static final String FALSE = "false";
	private static final String APPENDER_REF_ELEMENT = "appender-ref";
	private static final String REFERENCE = "ref";
	private static final String LOGGERS_ELEMENT = "loggers";
	private static final String FILE = "File";
	private static final String FILE_NAME = "fileName";
	private static final String PATTERN_LAYOUT = "PatternLayout";
	private static final String PATTERN = "pattern";
	private static final String APPENDERS_ELEMENT = "appenders";

	private static final String LOG_CATEGORY_CHAIN = "chain.jumbune.instrument";
	private static final String LOG_APPENDER_NAME_CHAIN = "ChainJumbuneAppender";
	private static final String FILENAME_PREFIX_CHAIN_LOGGER = "mrChain";

	// loggers
	private static List<Logger> mapReduceLoggers;
	private static Logger chainLogger;

	private static final String LOG4J_PROPERTY_RESOURCE = "/log4j2.xml";

	/**
	 * Instantiates a new logger util.
	 */
	private LoggerUtil(){
		
	}
	
	/**
	 * <p>
	 * This method loads the required properties into log4j for the logger
	 * </p>
	 * 
	 * @param logFileDir
	 *            Log file directory
	 * @param taskAttemptID
	 *            The task attempt id
	 * 
	 * @throws IOException
	 *             If any error occurs
	 * @throws URISyntaxException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static void loadLogger(String logFileDir, String taskAttemptID) throws IOException, URISyntaxException, ParserConfigurationException,
			SAXException, TransformerException {

		// getting the xml file from resource
		URL resourceUrl = LoggerUtil.class.getResource(LOG4J_PROPERTY_RESOURCE);
		File file = new File(resourceUrl.toURI());
		Document doc = getXmlDocumentFromFile(file);

		// adding a logger and setting appender ref
		Element loggerElement = doc.createElement(LOG_ELEMENT);
		loggerElement.setAttribute(NAME, LOG_CATEGORY + taskAttemptID);
		loggerElement.setAttribute(LOG_LEVEL, LOG_LEVEL_INFO);
		loggerElement.setAttribute(ADDITIVITY, FALSE);
		Element appenderRefElement = doc.createElement(APPENDER_REF_ELEMENT);
		appenderRefElement.setAttribute(REFERENCE, LOG_APPENDER_NAME + taskAttemptID);
		loggerElement.appendChild(appenderRefElement);
		Element loggersElement = (Element) doc.getElementsByTagName(LOGGERS_ELEMENT).item(0);
		loggersElement.appendChild(loggerElement);

		// loading appender
		loadAppender(doc, LOG_APPENDER_NAME + taskAttemptID, logFileDir, taskAttemptID, 0);

		// reloading the log4j configuration
		updateXmlDocumentOnFile(doc, file);
		reloadLoggingProperties();

		// assigning the loggers
		mapReduceLoggers = new ArrayList<Logger>(1);

		mapReduceLoggers.add(LogManager.getLogger(LOG_CATEGORY + taskAttemptID));
		LOG.debug("Finished loading logger");

	}

	/**
	 * Loads an appender to log4j
	 * 
	 * @param props
	 *            existing log4j properties
	 * @param appenderName
	 *            Appender name
	 * @param logFileDir
	 *            Log file directory
	 * @param taskAttemptID
	 *            The task attempt id
	 */
	private static void loadAppender(Document doc, String appenderName, String logFileDir, String taskAttemptID, Object loggerNumber)
			throws IOException {
		StringBuilder logFileName = new StringBuilder(YamlUtil.getAndReplaceHolders(logFileDir));
		if (!(loggerNumber instanceof Integer && (Integer) loggerNumber == 0)) {
			logFileName.append(loggerNumber).append("-");
		}
		logFileName.append(IPRetriever.getCurrentIP()).append(SEPARATOR_UNDERSCORE).append(taskAttemptID).append(LOG_FILE_EXT);

		Element fileAppenderElement = doc.createElement(FILE);
		fileAppenderElement.setAttribute(NAME, appenderName);
		fileAppenderElement.setAttribute(FILE_NAME, logFileName.toString());
		Element patternLayoutElement = doc.createElement(PATTERN_LAYOUT);
		Element patternElement = doc.createElement(PATTERN);
		patternElement.setTextContent(LOG_PATTERN);
		patternLayoutElement.appendChild(patternElement);
		fileAppenderElement.appendChild(patternLayoutElement);

		Element appendersElement = (Element) doc.getElementsByTagName(APPENDERS_ELEMENT).item(0);
		appendersElement.appendChild(fileAppenderElement);

	}

	/**
	 * Reloads the log4j configuration properties
	 * 
	 * @param props
	 *            Modified properties
	 */
	private static void reloadLoggingProperties() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
	}

	/**
	 * <p>
	 * Get the logger
	 * </p>
	 * 
	 * @return Log4JLogger
	 */
	public static Logger getMapReduceLogger(int k) {
		return mapReduceLoggers.get(k);
	}

	/**
	 * Gets the map reduce loggers.
	 *
	 * @return the map reduce loggers
	 */
	public static List<Logger> getMapReduceLoggers() {
		return mapReduceLoggers;
	}

	/**
	 * <p>
	 * Loads multiple loggers
	 * </p>
	 * 
	 * @param taskAttemptID
	 *            The task attempt id
	 * @param numberOfLoggers
	 *            Number of loggers
	 * @throws IOException
	 *             If any error occurs
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws URISyntaxException
	 */
	public static void loadChainLogger(String logFileDir, String taskAttemptID, int numberOfLoggers) throws IOException,
			ParserConfigurationException, SAXException, TransformerException, URISyntaxException {
		// getting the properties from the configuration
		URL resourceUrl = LoggerUtil.class.getResource(LOG4J_PROPERTY_RESOURCE);
		File file = new File(resourceUrl.toURI());
		Document doc = getXmlDocumentFromFile(file);

		Element loggersElement = (Element) doc.getElementsByTagName(LOGGERS_ELEMENT).item(0);
		Element logElement;
		Element appenderRefElement;

		for (int k = 0; k < numberOfLoggers; k++) {

			logElement = doc.createElement(LOG_ELEMENT);
			logElement.setAttribute(NAME, LOG_CATEGORY + taskAttemptID + k);
			logElement.setAttribute(LOG_LEVEL, LOG_LEVEL_INFO);
			logElement.setAttribute(ADDITIVITY, FALSE);
			appenderRefElement = doc.createElement(APPENDER_REF_ELEMENT);
			appenderRefElement.setAttribute(REFERENCE, LOG_APPENDER_NAME + taskAttemptID + k);
			logElement.appendChild(appenderRefElement);
			loggersElement.appendChild(logElement);

			loadAppender(doc, LOG_APPENDER_NAME + taskAttemptID + k, logFileDir, taskAttemptID, k + 1);
			LOG.debug("Finished loading logger: " + k);
		}

		// load chain logger only if number of loggers is > 1
		if (numberOfLoggers > 1) {

			logElement = doc.createElement(LOG_ELEMENT);
			logElement.setAttribute(NAME, LOG_CATEGORY_CHAIN + taskAttemptID);
			logElement.setAttribute(LOG_LEVEL, LOG_LEVEL_INFO);
			logElement.setAttribute(ADDITIVITY, FALSE);
			appenderRefElement = doc.createElement(APPENDER_REF_ELEMENT);
			appenderRefElement.setAttribute(REFERENCE, LOG_APPENDER_NAME_CHAIN + taskAttemptID);
			logElement.appendChild(appenderRefElement);
			loggersElement.appendChild(logElement);

			loadAppender(doc, LOG_APPENDER_NAME_CHAIN + taskAttemptID, logFileDir, taskAttemptID, FILENAME_PREFIX_CHAIN_LOGGER);
		}

		// reloading the log4j properties
		updateXmlDocumentOnFile(doc, file);
		reloadLoggingProperties();

		// assigning the loggers
		mapReduceLoggers = new ArrayList<Logger>(numberOfLoggers);
		for (int k = 0; k < numberOfLoggers; k++) {
			mapReduceLoggers.add(LogManager.getLogger(LOG_CATEGORY + taskAttemptID + k));
		}
		if (numberOfLoggers > 1) {
			chainLogger = LogManager.getLogger(LOG_CATEGORY_CHAIN + taskAttemptID);

		}
	}

	/**
	 * This method gets the chain loggger.
	 *
	 * @return the chain loggger
	 */
	public static Logger getChainLoggger() {
		return chainLogger;
	}

	/**
	 * This method is used to get the xml document from file.
	 *
	 * @param file the file
	 * @return the xml document from file
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Document getXmlDocumentFromFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * This method is used to update xml document on file.
	 *
	 * @param doc the doc
	 * @param file the file
	 * @throws TransformerException the transformer exception
	 */
	private static void updateXmlDocumentOnFile(Document doc, File file) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}

	
}
