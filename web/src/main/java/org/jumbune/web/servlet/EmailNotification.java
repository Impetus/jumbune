package org.jumbune.web.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;



/**
 * This class implements email notification method used in the framework.
 * <p>
 * </p>
 * 

 */

public class EmailNotification {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(EmailNotification.class);

	/** The Constant SMTP_USER. */
	private static final String SMTP_USER = "mail.user";
	
	/** The Constant SMTP_PASSWORD. */
	private static final String SMTP_PASSWORD = "mail.password";
	
	/** The Constant NOTIFICATION_MSG. */
	private static final String NOTIFICATION_MSG = "Please check the node ";

	/**
	 * Send notification.
	 *
	 * @param message the message
	 * @param nodeIp the node ip
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MessagingException the messaging exception
	 */
	public void sendNotification(String message, String nodeIp)
			throws IOException, MessagingException {
		StringBuffer sb = new StringBuffer(NOTIFICATION_MSG);
		sb.append(nodeIp).append(". ").append(message);
		LOGGER.debug("Jumbune Email notification: " + sb.toString());
		Properties props = ConfigurationUtil.readConfigurationFromProperties();
		final String user = props.getProperty(SMTP_USER);
		final String pswd = props.getProperty(SMTP_PASSWORD);
		Session mailSession = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user, pswd);
					}
				});

		Message simpleMessage = new MimeMessage(mailSession);
		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;
		fromAddress = new InternetAddress(Constants.FROM);
		toAddress = new InternetAddress(Constants.TO);
		simpleMessage.setFrom(fromAddress);
		simpleMessage.setRecipient(RecipientType.TO, toAddress);
		simpleMessage.setSubject(Constants.SUBJECT);
		simpleMessage.setText(sb.toString());
		Transport.send(simpleMessage);
	}
}
