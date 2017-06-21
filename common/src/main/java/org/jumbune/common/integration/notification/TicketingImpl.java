package org.jumbune.common.integration.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.beans.AlertAction;
import org.jumbune.utils.conf.beans.TicketConfiguration;

import org.jumbune.common.beans.Alert;
import org.jumbune.common.integration.notification.ticket.TicketClient;
import org.jumbune.common.integration.notification.ticket.TicketClientImplBMC;


/**
 * The Class TicketingImpl. Implementation of {@link AlertNotifier}
 */
public class TicketingImpl implements AlertNotifier {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(TicketingImpl.class);

	/** The Constant ALERT_MESSAGE. */
	private static final String ALERT_MESSAGE = "Alert Message";

	/** The Constant AFFECTED_NODE. */
	private static final String AFFECTED_NODE = "Affected Node";

	/** The Constant ALERT_LEVEL. */
	private static final String ALERT_LEVEL = "Alert Level";

	/** The ticket client. */
	private TicketClient ticketClient = null;

	/**
	 * Instantiates a new alert notifier ticketing impl.
	 *
	 * @param conf the conf
	 */
	public TicketingImpl(TicketConfiguration conf) {
		try {
			ticketClient = new TicketClientImplBMC(conf.getHost(), conf.getPort(), conf.getUsername(),
					conf.getDecryptedPassword(), conf.getFormName());
		} catch (Exception e) {
			LOGGER.error("Error in password Decryption. Unable to create Ticket. Exiting...");
		}
	}

	/**
	 * Sends ticketing notification i.e. creates a ticket corresponding to all the alerts on the provided ticketing system.
	 * @param notificationList list of alerts for which tickets are to be created.
	 * @param alertAction
	 * @param clusterName
	 * 
	 */
	@Override
	public void sendNotification(List<Alert> notificationList, AlertAction alertAction, String clusterName) {
			Map<String, Object> entryAttrib = null;
			for (Alert alert : notificationList) {
				entryAttrib = new HashMap<>(3);
				entryAttrib.put(ALERT_MESSAGE, alert.getMessage());
				entryAttrib.put(AFFECTED_NODE, alert.getNodeIP());
				entryAttrib.put(ALERT_LEVEL, alert.getLevel());
				ticketClient.createEntry(entryAttrib);

		}

	}

}
