package org.jumbune.utils.conf.beans;

import java.util.List;

public class AlertActionConfiguration {

	private List<AlertAction> alertActions;

	public List<AlertAction> getAlertActions() {
		return alertActions;
	}

	public void setAlertActions(List<AlertAction> alertActions) {
		this.alertActions = alertActions;
	}

	@Override
	public String toString() {
		return "AlertActionConfiguration [alertActions=" + alertActions + "]";
	}

}
