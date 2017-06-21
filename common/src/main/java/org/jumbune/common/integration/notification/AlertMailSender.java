package org.jumbune.common.integration.notification;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.AlertAction;
import org.jumbune.utils.conf.beans.EmailConfiguration;

import org.jumbune.common.beans.Alert;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * The Class AlertMailSender.
 */
public class AlertMailSender implements AlertNotifier{

	/** The Constant LOGGER_OBJECT. */
	private static final Logger LOGGER = LogManager.getLogger(AlertMailSender.class);

	/** The Constant SLASH. */
	private static final String SLASH = "/";
	
	/** The Constant JUMBUNE_ENV_VAR. */
	private static final String JUMBUNE_ENV_VAR = "JUMBUNE_HOME";
	
	/** The Constant JUMBUNE_RESOURCES_PATH. */
	private static final String JUMBUNE_RESOURCES_PATH = System.getenv(JUMBUNE_ENV_VAR) + "resources" + SLASH;

	/** The Constant JUMBUNE_IMAGES_PATH. */
	private static final String JUMBUNE_IMAGES_PATH = JUMBUNE_RESOURCES_PATH + "images" + SLASH;

	/**
	 * Method for sending simply mail without attachment.
	 *
	 * @param notificationList the notification list
	 * @param alertAction the alert action
	 * @param clusterName the cluster name
	 * @return true, if successful
	 */
	@Override
	public void sendNotification(List<Alert> notificationList, AlertAction alertAction,
			String clusterName) {
		try {
			EmailConfiguration emailConfiguration = AdminConfigurationUtil.getEmailConfiguration(clusterName);
			String emailTo=alertAction.getEmailTo();

			if (emailConfiguration ==null || emailConfiguration.getSenderEmailID() == null || emailConfiguration.getSenderEmailID().isEmpty()
					|| emailTo == null || emailTo.isEmpty()) {
				LOGGER.warn("Alert Email Configurations is not set properly.. Not sending alert mail. ");
			} else {
				
				final String senderID = emailConfiguration.getSenderEmailID();
				final String password = emailConfiguration.getDecryptedPassword();
				String senderName = null;
				if((senderName = emailConfiguration.getSenderName())==null)
					senderName="";
				Properties props = System.getProperties();
				props.put("mail.smtp.starttls.enable", emailConfiguration.getAuthentication().toString());
				props.put("mail.smtp.host", emailConfiguration.getHostName());
				props.put("mail.smtp.port", emailConfiguration.getPortNumber().toString());
				props.put("mail.smtp.auth", emailConfiguration.getAuthentication().toString());
				props.put("mail.smtp.user", emailTo);
				props.put("mail.smtp.password", password);

				Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(senderID, password);
					}
				});
				Configuration cfg = new Configuration();
				Message message = new MimeMessage(mailSession);
				message.setFrom(new InternetAddress(senderName + "<" + senderID + ">"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));

				List<Alert> tempList = new ArrayList<>();
				for (Alert alert : notificationList) {
					tempList.add(alert.deepCopy());
				}
				Date date = new Date();
				for (Alert alert : tempList) {
					if (!alert.getSkipOccuringSince()) {
						alert.setDate(getDateDifference(date.getTime(), alert.getFirstOccurrence()));
					}
				}
				message.setSubject(notificationList.get(0).getLevel()+ " Alert(s)!! on "+clusterName+" cluster");
				cfg.setDirectoryForTemplateLoading(new File(JUMBUNE_RESOURCES_PATH));
				Template template = cfg.getTemplate("mailBodyTemplate.html");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("alerts", tempList);
				File inputFile = new File(JUMBUNE_RESOURCES_PATH + "mailBody.html");
				Writer file = new FileWriter(inputFile);
				template.process(data, file);
				file.flush();
				file.close();

				StringBuilder sb = new StringBuilder();

				try (Scanner scanner = new Scanner(inputFile)) {
					while (scanner.hasNextLine()) {
						sb.append(scanner.nextLine());
					}
				}
				String body1 = sb.toString();
				MimeMultipart multipart = new MimeMultipart("related");
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(body1, "text/html");
				multipart.addBodyPart(messageBodyPart);
				messageBodyPart = new MimeBodyPart();
				DataSource fds = new FileDataSource(JUMBUNE_IMAGES_PATH + "logo.png");
				messageBodyPart.setDataHandler(new DataHandler(fds));
				messageBodyPart.setHeader("Content-ID", "<image>");
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
				Transport.send(message);
				LOGGER.debug("Mail Sent successsfully");
			}
		} catch (Exception e) {
			LOGGER.error("Cannot send Alert Email.."+e.getMessage());
		}

	}
	
	private static String getDateDifference(long newDate, long oldDate) {
		long dateDiff = newDate - oldDate;
		String finalValue = "";
		
		if (dateDiff / (1000 * 60 * 60 * 24) > 1) {
			finalValue = dateDiff / (1000*60*60*24) + " Days ";
		} else if (dateDiff / (1000*60*60*24) == 1) {
			finalValue = "1 Day ";
		}
		dateDiff = dateDiff % (1000 * 60 * 60 * 24);
		if (dateDiff / (1000 * 60 * 60) > 1) {
			finalValue += dateDiff / (1000 * 60 * 60) + " Hours ";
		} else if (dateDiff / (1000 * 60 * 60) == 1) {
			finalValue += "1 Hour ";
		}
		dateDiff = dateDiff % (1000 * 60 * 60);
		if (dateDiff / (1000 * 60) > 1) {
			finalValue += dateDiff / (1000 * 60) + " Minutes ";
		} else if (dateDiff / (1000 * 60) == 1) {
			finalValue += "1 Minute ";
		}
		dateDiff = dateDiff % (1000 * 60);
		if (dateDiff / (1000 * 60) > 1) {
			finalValue += dateDiff / (1000 * 60) + " Seconds ";
		}
		if (finalValue.isEmpty()) {
			finalValue = "Just Now";
		}
		return finalValue;
	}

}
