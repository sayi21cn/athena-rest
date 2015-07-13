package com.robert.athena.rest.container;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;

public class JettyServer extends AbstractContainerServer implements
		ContainerServer {

	private Server server;

	public JettyServer(int port, String contextPath, String webappPath) {
		this.port = port;
		this.contextPath = contextPath;
		this.webappPath = webappPath;
	}

	public JettyServer(int port, String contextPath, String webappPath,
			int minThreads, int maxThreads) {
		this(port, contextPath, webappPath);
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}

	protected void doStart() throws Exception {

		server = new Server();

		if (this.minThreads != -1 && this.maxThreads != -1) {
			BoundedThreadPool btp = new BoundedThreadPool();
			server.setThreadPool(btp);
		}

		SelectChannelConnector connector = new SelectChannelConnector();

		// Set some timeout options to make debugging easier.
		connector.setSoLingerTime(-1);
		connector.setPort(port);

		server.setConnectors(new Connector[] { connector });

		WebAppContext wac = new WebAppContext();

		wac.setServer(server);
		wac.setContextPath(contextPath);
		wac.setWar(webappPath);

		server.setHandler(wac);

		log.info("Starting jetty server actually.");
		server.start();
	}

	protected void doStop() throws Exception {
		try {
			log.info("Stopping jetty server actually.");
			server.stop();

			log.info("Waiting jetty server to exit.");
			server.join();
		} catch (InterruptedException e) {
			log.error("Interruped when stopping jetty server. Please wait until it stops or kill it.");
			throw e;
		}

	}

}
