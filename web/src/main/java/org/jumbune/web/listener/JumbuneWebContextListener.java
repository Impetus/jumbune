package org.jumbune.web.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.web.process.BackgroundProcessManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Web Context Listener.
 *
 * @see JumbuneWebContextEvent
 */
public class JumbuneWebContextListener implements ServletContextListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(JumbuneWebContextListener.class);

	/** The Constant INSTRUMENTATION_ATTRIB. */
	private static final String INSTRUMENTATION_ATTRIB = "mapred.tasktracker.instrumentation";

	/** The Constant PROPERTY. */
	private static final String PROPERTY = "property";

	/** The Constant NAME. */
	private static final String NAME = "name";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		BackgroundProcessManager.getInstance().initiate();
	}

	/**
	 * Removes the inst clazz entry.
	 *
	 * @param filePath
	 *            the file path
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the sAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerException
	 *             the transformer exception
	 */
	public void removeInstClazzEntry(String filePath)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		Document doc = ConfigurationUtil.getXmlDocumentFromFile(filePath);
		NodeList nList = doc.getElementsByTagName(PROPERTY);
		Node node = null;
		for (int ctr = 0; ctr < nList.getLength(); ctr++) {
			node = nList.item(ctr);
			Element element = (Element) node;
			if (INSTRUMENTATION_ATTRIB.equals(element
					.getElementsByTagName(NAME).item(0).getTextContent())) {
				element.getParentNode().removeChild(element);
			}
		}
		ConfigurationUtil.updateXmlDocumentOnFile(doc, filePath);
	}

}
