package org.jumbune.common.integration.notification;

import java.util.List;

import org.jumbune.utils.conf.beans.AlertAction;

import org.jumbune.common.beans.AlertInfo;

public interface AlertNotifier {
	
	public void sendNotification(List<AlertInfo> notificationList, AlertAction alertAction, String clusterName);
	

}
