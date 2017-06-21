package org.jumbune.utils.conf.beans;

public class AlertAction {

	private AlertLevel alertLevel;
	private Double occuringSinceHours;
	private String emailTo;
	private SNMPTraps snmpTraps;
	private boolean enableTicket;
	
	public boolean isEnableTicket() {
		return enableTicket;
	}

	public AlertLevel getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(AlertLevel alertLevel) {
		this.alertLevel = alertLevel;
	}

	public Double getOccuringSinceHours() {
		return occuringSinceHours;
	}

	public void setOccuringSinceHours(Double occuringSinceHours) {
		this.occuringSinceHours = occuringSinceHours;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public SNMPTraps getSnmpTraps() {
		return snmpTraps;
	}

	public void setSnmpTraps(SNMPTraps snmpTraps) {
		this.snmpTraps = snmpTraps;
	}

	@Override
	public String toString() {
		return "AlertAction [alertLevel=" + alertLevel + ", occuringSinceHours="
				+ occuringSinceHours + ", emailTo=" + emailTo + ", snmpTraps=" + snmpTraps + "]";
	}
}
