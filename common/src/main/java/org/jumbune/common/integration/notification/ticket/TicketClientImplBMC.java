package org.jumbune.common.integration.notification.ticket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;


/**
 * The Class TicketClientImplBMC. BMC specific implementation of {@link TicketClient}
 * <br><br>
 * <b>Prerequisite : </b> For APIs({@code createEntry() and deleteEntry()}) of this class to work, there must be a 'Regular Form' in place on BMC server.
 * This is expected from BMC admin. Along with the predefined fields on regular form, custom fields which should be created are 'Alert Message', 
 * 'Affected Node' and 'Alert Level'.
 * 
 */
public class TicketClientImplBMC implements TicketClient {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(TicketClientImplBMC.class);

	/** The Constant PROTOCOL. */
	private static final String PROTOCOL = "http://";

	/** The Constant COLON. */
	private static final String COLON = ":";

	/** The Constant OPEN_BRACE. */
	private static final String OPEN_BRACE = "{";

	/** The Constant CLOSE_BRACE. */
	private static final String CLOSE_BRACE = "}";

	/** The Constant VALUES. */
	private static final String VALUES = "values";

	/** The Constant USERNAME. */
	private static final String USERNAME = "username";

	/** The Constant PASSWORD. */
	private static final String PASSWORD = "password";

	/** The Constant AR_JWT. */
	private static final String AR_JWT = "AR-JWT ";

	/** The Constant HEADER_AUTH_KEY. */
	private static final String HEADER_AUTH_KEY = "Authorization";

	/** The Constant LOCATION. */
	private static final String LOCATION = "Location";

	/** The Constant SHORT_DESC. */
	private static final String SHORT_DESC = "Short Description";

	/** The Constant SHORT_DESC_VAL. */
	private static final Object SHORT_DESC_VAL = "Alert Reported by Jumbune";

	/** The Constant SUBMITTER. */
	private static final String SUBMITTER = "Submitter";

	/** The Constant SUBMITTER_VAL. */
	private static final Object SUBMITTER_VAL = "jumbune";

	/** The login url. */
	private String loginURL;
	
	/** The logout url. */
	private String logoutURL;
	
	/** The entry base url. */
	private String entryBaseURL;
	
	/** The credentials. */
	private List<NameValuePair> credentials;
	
	/** The form name. */
	private String formName;

	/**
	 * Instantiates a new ticket client impl bmc.
	 *
	 * @param host the host
	 * @param port the port
	 * @param username the username
	 * @param password the password
	 * @param formName the form name
	 */
	public TicketClientImplBMC(String host, String port, String username, String password, String formName) {
        this.formName = formName;
		
		loginURL = PROTOCOL + host + COLON + port + "/api/jwt/login";
		logoutURL = PROTOCOL + host + COLON + port + "/api/jwt/logout";
		entryBaseURL = PROTOCOL + host + COLON + port + "/api/arsys/v1/entry/";

		credentials = new ArrayList<>(2);
		credentials.add(new BasicNameValuePair(USERNAME, username));
		credentials.add(new BasicNameValuePair(PASSWORD, password));

	}

	/**
	 * Acquire auth token.
	 *
	 * @return the string
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String acquireAuthToken() throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String token = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(loginURL);
			httpPost.setEntity(new UrlEncodedFormEntity(credentials));
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			token = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		} finally {
			response.close();
			httpClient.close();
		}
		return token;
	}

	/**
	 * Release auth token.
	 *
	 * @param token the token
	 * @return the status line
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private StatusLine releaseAuthToken(final String token) throws ClientProtocolException, IOException {
		StatusLine status = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(logoutURL);
			httpPost.addHeader(HEADER_AUTH_KEY, AR_JWT + token);
			response = httpClient.execute(httpPost);
			status = response.getStatusLine();
		} finally {
			response.close();
			httpClient.close();
		}
		return status;
	}

	/**
	 * Creates the ticket entry.
	 *
	 * @param entryAttrib the entry attrib
	 * @return If an entry ID is generated, such as on a regular form, status
	 *         code 201 with the Location header is returned.
	 */
	@Override
	public String createEntry(Map<String, Object> entryAttrib) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String token = null;
		Header locationHeader = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(entryBaseURL + formName);
			httpPost.setEntity(new StringEntity(marshal(entryAttrib), ContentType.APPLICATION_JSON));
			token = acquireAuthToken();
			httpPost.addHeader(HEADER_AUTH_KEY, AR_JWT + token);
			response = httpClient.execute(httpPost);
			locationHeader = response.getFirstHeader(LOCATION);

		} catch (IOException e) {
			LOGGER.error("Unable to create ticket entry ", e);
		} finally {
			try {
				response.close();
				httpClient.close();
				releaseAuthToken(token);
			} catch (IOException e) {
				LOGGER.error("Unable to close response or http client ", e);
			}

		}
		return locationHeader.getValue();
	}


	/**
	 * deletes the ticket entry.
	 *
	 * @param locationURL location of ticket entry
	 * @return if the successful
	 */
   
	@Override
	public boolean deleteEntry(String locationURL) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String token = null;
		int status = 0;
		httpClient = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(locationURL);
		try {
			token = acquireAuthToken();
			httpDelete.addHeader(HEADER_AUTH_KEY, AR_JWT + token);
			response = httpClient.execute(httpDelete);
			status = response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			LOGGER.error("Unable to delete ticket entry ", e);
		} finally {
			try {
				response.close();
				httpClient.close();
				releaseAuthToken(token);
			} catch (IOException e) {
				LOGGER.error("Unable to delete entry corresponding to " + locationURL, e);
			}

		}
		return status / 100 == 2;
	}

	/**
	 * Marshals the entity attribute map to the JSON format required by BMC.
	 *
	 * @param entryAttrib the entry attrib
	 * @return the string
	 */
	private String marshal(final Map<String, Object> entryAttrib) {
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();		
		entryAttrib.put(SUBMITTER, SUBMITTER_VAL);
		entryAttrib.put(SHORT_DESC, SHORT_DESC_VAL);		
		builder.append(OPEN_BRACE).append(VALUES).append(COLON).append(gson.toJson(entryAttrib)).append(CLOSE_BRACE);
		return builder.toString();
	}

}
