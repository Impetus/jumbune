package org.jumbune.web.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jumbune.common.beans.JumbuneInfo;

public class Starter {

	private final Logger LOGGER = LogManager.getLogger(Starter.class);

	public static void main(String[] args) throws Exception {

		new Starter().start();
	}

	public void start() {

		Server server = new Server(9080);
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/");
		servletContextHandler.setResourceBase("src/main/webapp");

		final String webAppDirectory = JumbuneInfo.getHome() + "resources/webapp";
		final ResourceHandler resHandler = new ResourceHandler();
		resHandler.setResourceBase(webAppDirectory);
		final ContextHandler ctx = new ContextHandler("/");
		ctx.setHandler(resHandler);
		servletContextHandler.setSessionHandler(new SessionHandler());

		ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/apis/*");
		servletHolder.setInitOrder(0);
		servletHolder.setAsyncSupported(true);
		servletHolder.setInitParameter("jersey.config.server.provider.packages", "org.jumbune.web.services");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames",
				"org.glassfish.jersey.media.multipart.MultiPartFeature");

		try {
			server.insertHandler(servletContextHandler);
			server.insertHandler(resHandler);
			server.start();
			server.join();
		} catch (Exception e) {
			LOGGER.error("Error occurred while starting Jetty", e);
			System.exit(1);
		}
	}
}