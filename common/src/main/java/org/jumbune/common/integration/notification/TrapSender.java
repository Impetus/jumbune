package org.jumbune.common.integration.notification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.AlertInfo;
import org.jumbune.utils.conf.beans.AlertAction;
import org.jumbune.utils.conf.beans.SNMPTraps;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


public class TrapSender implements AlertNotifier{

	/** The Constant LOGGER_OBJECT. */
	private static final Logger LOGGER = LogManager.getLogger(TrapSender.class);

	private static final String COMMUNITY = "public";

	/**
	 * This methods sends the trap to the ipAddress in the port
	 * 
	 * @param trapMessage
	 * @param string
	 */
	@Override
	public void sendNotification(List<AlertInfo> notificationList, AlertAction alertAction,
			String clusterName) {
		Snmp snmp = null;
		try {
			SNMPTraps snmpTrapsConfiguration=alertAction.getSnmpTraps();
			if (snmpTrapsConfiguration == null
					|| isNullOrEmpty(snmpTrapsConfiguration.getIpAddress())
					|| isNullOrEmpty(snmpTrapsConfiguration.getTrapOID())
					|| snmpTrapsConfiguration.getPort() == null) {
				LOGGER.warn("SNMP Traps are not configured.. Not sending Traps");
			} else {
				String alertTrapOid = snmpTrapsConfiguration.getTrapOID();
				String ipAddress = snmpTrapsConfiguration.getIpAddress();
				Integer port = snmpTrapsConfiguration.getPort();

				CommunityTarget comtarget = new CommunityTarget();
				comtarget.setCommunity(new OctetString(COMMUNITY));
				comtarget.setVersion(SnmpConstants.version2c);
				comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
				comtarget.setRetries(2);
				comtarget.setTimeout(5000);
				TransportMapping transport = new DefaultUdpTransportMapping();
				transport.listen();
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
				pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));
				pdu.add(new VariableBinding(new OID(alertTrapOid), new OctetString(notificationList.toString())));
				pdu.setType(PDU.NOTIFICATION);
				snmp = new Snmp(transport);
				LOGGER.debug("Sending Trap to " + ipAddress + " on Port " + port);
				snmp.send(pdu, comtarget);
			}

		} catch (IOException e) {
			LOGGER.error("Cannot send SNMP Traps.. "+e.getMessage());
		} finally {
			try {
				if (snmp != null)
					snmp.close();
			} catch (IOException e) {
				LOGGER.error("Problem while sending SNMP Traps.. "+e.getMessage());
			}
		}
	}
	
	private boolean isNullOrEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}


}
