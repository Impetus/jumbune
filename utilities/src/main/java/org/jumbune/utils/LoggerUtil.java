package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.SEPARATOR_UNDERSCORE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.MemoryMappedFileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Charsets;
import org.w3c.dom.Document;
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
	private static final String LOG_CATEGORY_CHAIN = "chain.jumbune.instrument";
	private static final String LOG_APPENDER_NAME_CHAIN = "ChainJumbuneAppender";
	private static final String FILENAME_PREFIX_CHAIN_LOGGER = "mrChain";

	// loggers
	private static List<Logger> mapReduceLoggers;
	private static Logger chainLogger;
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
	    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
	    ctx.reconfigure();
        Configuration config = ctx.getConfiguration();
        MemoryMappedFileAppender memoryMappedfileAppender = createMemoryMappedFileAppender(config, LOG_APPENDER_NAME + taskAttemptID, logFileDir, taskAttemptID, 0);
        memoryMappedfileAppender.start();
	    AppenderRef[] ar = new AppenderRef [1];
        ar[0] = AppenderRef.createAppenderRef(LOG_APPENDER_NAME + taskAttemptID , Level.INFO, null);
        LoggerConfig lgf = LoggerConfig.createLogger("false",Level.INFO , LOG_CATEGORY + taskAttemptID , null, ar, null, config, null);
        config.addLogger(LOG_CATEGORY + taskAttemptID, lgf);
        ctx.getLogger(LOG_CATEGORY + taskAttemptID).addAppender(memoryMappedfileAppender);
        ctx.updateLoggers();
        ctx.start();
		mapReduceLoggers = new ArrayList<Logger>(1);
		mapReduceLoggers.add(LogManager.getLogger(LOG_CATEGORY + taskAttemptID));
		LOG.debug("Finished loading logger");

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
	  
	    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
	    ctx.reconfigure();
        Configuration config = ctx.getConfiguration();
        for (int k = 0; k < numberOfLoggers; k++) {
        	  MemoryMappedFileAppender memoryMappedfileAppender = createMemoryMappedFileAppender(config, LOG_APPENDER_NAME + taskAttemptID+k, logFileDir, taskAttemptID, k+1);
               memoryMappedfileAppender.start();
               AppenderRef[] ar = new AppenderRef [1];
               ar[0] = AppenderRef.createAppenderRef(LOG_APPENDER_NAME + taskAttemptID + k, Level.INFO, null);
               LoggerConfig lgf = LoggerConfig.createLogger("false",Level.INFO , LOG_CATEGORY + taskAttemptID + k, "includeLocation", ar, null, config, null);
               config.addLogger(LOG_CATEGORY + taskAttemptID + k, lgf);
               ctx.getLogger(LOG_CATEGORY + taskAttemptID + k).addAppender(memoryMappedfileAppender);
               LOG.debug("Finished loading logger: " + k);
          }
        
        if (numberOfLoggers > 1) {
             MemoryMappedFileAppender memoryMappedfileAppender = createMemoryMappedFileAppender(config, LOG_APPENDER_NAME_CHAIN + taskAttemptID, logFileDir, taskAttemptID, FILENAME_PREFIX_CHAIN_LOGGER);
              memoryMappedfileAppender.start();
              AppenderRef[] ar = new AppenderRef [1];
              ar[0] = AppenderRef.createAppenderRef(LOG_APPENDER_NAME_CHAIN + taskAttemptID, Level.INFO, null);
              LoggerConfig lgf = LoggerConfig.createLogger("false",Level.INFO , LOG_CATEGORY_CHAIN + taskAttemptID, "includeLocation", ar, null, config, null);
              config.addLogger(LOG_CATEGORY_CHAIN + taskAttemptID, lgf);
              ctx.getLogger(LOG_CATEGORY_CHAIN + taskAttemptID).addAppender(memoryMappedfileAppender);
                    LOG.debug("Finished loading logger: ");
        }
        ctx.updateLoggers();
        ctx.start();

        mapReduceLoggers = new ArrayList<Logger>(numberOfLoggers);
        for (int k = 0; k < numberOfLoggers; k++) {
            mapReduceLoggers.add(LogManager.getLogger(LOG_CATEGORY + taskAttemptID + k));
		}
         
		if (numberOfLoggers > 1) {
	        chainLogger = LogManager.getLogger(LOG_CATEGORY_CHAIN + taskAttemptID);
		}
        
	}

	
	private static MemoryMappedFileAppender createMemoryMappedFileAppender(Configuration config,
			String appenderName, String logFileDir, String taskAttemptID, Object loggerNumber) {
			  StringBuilder logFileName = new StringBuilder(YamlUtil.getAndReplaceHolders(logFileDir));
		      
		      if (!(loggerNumber instanceof Integer && (Integer) loggerNumber == 0)) {
		          logFileName.append(loggerNumber).append("-");
		      }
		      logFileName.append(IPRetriever.getCurrentIP()).append(SEPARATOR_UNDERSCORE).append(taskAttemptID).append(LOG_FILE_EXT);
		       // pattern layout
		      PatternLayout pl = PatternLayout.createLayout(LOG_PATTERN, config, null,Charsets.UTF_8 ,false, false, null, null);
		      return MemoryMappedFileAppender.createAppender(logFileName.toString(), "append", appenderName, "false", "33554432", null, pl, null, null, null, config);
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
}
