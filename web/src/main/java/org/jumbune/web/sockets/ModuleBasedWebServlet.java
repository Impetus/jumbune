package org.jumbune.web.sockets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ModuleBasedWebServlet extends WebSocketServlet{

	/**
	 * Serial Version Id
	 */
	private static final long serialVersionUID = 1714809235398001250L;

	@Override
	public void configure(WebSocketServletFactory factory) {
	     factory.setCreator(new ModuleBasedWebSocketCreator());		
	}

}
