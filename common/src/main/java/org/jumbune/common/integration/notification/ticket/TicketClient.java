package org.jumbune.common.integration.notification.ticket;

import java.util.Map;

/**
 * The Interface TicketClient. This contract needs to be implemented by each and every vendor specific ticket management service.
 * 
 */
public interface TicketClient {
	
	/**
	 * Creates the entry. Marshalling of map param {@code entryAttrib} is implementation specific according to request format of vendor.
	 *
	 * @param entryAttrib  contains all the attributes of alert which should be included in ticket.
	 * @return the location URL of newly created entry.
	 */
	String createEntry(Map<String, Object> entryAttrib);
	
	/**
	 * Delete entry.
	 *
	 * @param entryURI the entry uri
	 * @return true, if successful
	 */
	boolean deleteEntry(String entryURI);

}
