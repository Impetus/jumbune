package com.websystique.multimodule;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;




public class App {
	public static Server createServer(int port) {

		Server server = new Server(port);
		File warFile = new File("/home/prakruti/Desktop/Jumbune_home/modules/jumbune-web-2.0.war");

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
        webapp.setWar(warFile.getAbsolutePath());
		webapp.setExtractWAR(true);
		
		
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setContextPath("/apis/home/is-mapr");
		webapp1.setWar(warFile.getAbsolutePath());
		webapp1.setExtractWAR(true);
		
		WebAppContext webapp2 = new WebAppContext();
		webapp2.setContextPath("/apis/clusteranalysis/clusters-list");
        webapp2.setWar(warFile.getAbsolutePath());
		webapp2.setExtractWAR(true);
		
		ContextHandlerCollection contexts = new ContextHandlerCollection(
				webapp,webapp1,webapp2
	        );
		server.setHandler(contexts);
		
		
		return server;
	}

	public static void main(String[] args) throws Exception {
		int port = 9009;
		Server server = createServer(port); 

		
		server.start();

		server.dumpStdErr();

		server.join();
	}
}