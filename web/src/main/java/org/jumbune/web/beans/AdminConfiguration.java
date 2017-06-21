package org.jumbune.web.beans;

import org.jumbune.utils.conf.beans.AlertActionConfiguration;
import org.jumbune.utils.conf.beans.AlertConfiguration;
import org.jumbune.utils.conf.beans.ChargeBackConfigurations;
import org.jumbune.utils.conf.beans.DlcConfiguration;
import org.jumbune.utils.conf.beans.BackgroundProcessConfiguration;
import org.jumbune.utils.conf.beans.EmailConfiguration;
import org.jumbune.utils.conf.beans.HAConfiguration;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.conf.beans.KDCConfiguration;
import org.jumbune.utils.conf.beans.SlaConfigurations;
import org.jumbune.utils.conf.beans.TicketConfiguration;

/**
 * This class is used to receive and send all configurations between server and UI
 */
public class AdminConfiguration {
	
	private String clusterName;
	
	private AlertConfiguration alertConfiguration;
	
	private AlertActionConfiguration alertActionConfiguration;
	
	private EmailConfiguration emailConfiguration;
	
	private HAConfiguration haConfiguration;
	
	private InfluxDBConf influxDBConfiguration;
	
	private TicketConfiguration ticketConfiguration;
	
	private KDCConfiguration kdcConfiguration;
	
	private SlaConfigurations slaConfigurations;
	
	private DlcConfiguration dlcConfiguration;
	

	private BackgroundProcessConfiguration backgroundProcessConfiguration;

	private ChargeBackConfigurations chargeBackConfigurations;

	
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public AlertConfiguration getAlertConfiguration() {
		return alertConfiguration;
	}

	public void setAlertConfiguration(AlertConfiguration alertConfiguration) {
		this.alertConfiguration = alertConfiguration;
	}

	public AlertActionConfiguration getAlertActionConfiguration() {
		return alertActionConfiguration;
	}

	public void setAlertActionConfiguration(AlertActionConfiguration alertActionConfiguration) {
		this.alertActionConfiguration = alertActionConfiguration;
	}

	public EmailConfiguration getEmailConfiguration() {
		return emailConfiguration;
	}

	public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
		this.emailConfiguration = emailConfiguration;
	}

	public HAConfiguration getHaConfiguration() {
		return haConfiguration;
	}

	public void setHaConfiguration(HAConfiguration haConfiguration) {
		this.haConfiguration = haConfiguration;
	}

	public InfluxDBConf getInfluxDBConfiguration() {
		return influxDBConfiguration;
	}

	public void setInfluxDBConfiguration(InfluxDBConf influxDBConfiguration) {
		this.influxDBConfiguration = influxDBConfiguration;
	}

	public TicketConfiguration getTicketConfiguration() {
		return ticketConfiguration;
	}

	public void setTicketConfiguration(TicketConfiguration ticketConfiguration) {
		this.ticketConfiguration = ticketConfiguration;
	}

	public KDCConfiguration getKdcConfiguration() {
		return kdcConfiguration;
	}

	public void setKdcConfiguration(KDCConfiguration kdcConfiguration) {
		this.kdcConfiguration = kdcConfiguration;
	}
	
	public SlaConfigurations getSlaConfigurations() {
		return slaConfigurations;
	}

	public void setSlaConfigurations(SlaConfigurations slaConfigurations) {
		this.slaConfigurations = slaConfigurations;
	}

	public DlcConfiguration getDlcConfiguration() {
		return dlcConfiguration;
	}

	public void setDlcConfiguration(DlcConfiguration dlcConfiguration) {
		this.dlcConfiguration = dlcConfiguration;
	}

	public ChargeBackConfigurations getChargeBackConfigurations() {
		return chargeBackConfigurations;
	}

	public void setChargeBackConfigurations(ChargeBackConfigurations chargeBackConfigurations) {
		this.chargeBackConfigurations = chargeBackConfigurations;
	}
	
	
	public BackgroundProcessConfiguration getBackgroundProcessConfiguration() {
		return backgroundProcessConfiguration;
	}

	public void setBackgroundProcessConfiguration(BackgroundProcessConfiguration backgroundProcessConfiguration) {
		this.backgroundProcessConfiguration = backgroundProcessConfiguration;
	}
	
}
