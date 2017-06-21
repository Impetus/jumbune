package org.jumbune.common.integration.notification;

import java.util.List;

import org.jumbune.utils.conf.beans.AlertAction;

import org.jumbune.common.beans.Alert;

public interface AlertNotifier {
	
	public void sendNotification(List<Alert> notificationList, AlertAction alertAction, String clusterName);
	

}
