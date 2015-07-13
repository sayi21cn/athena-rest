package com.robert.athena.rest.container;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class AthenaRestMainServer {

	private final static Logger log = Logger
			.getLogger(AthenaRestMainServer.class);

	private static final int DEF_PORT = 10025;

	private static final String DEF_CONTEXT_PATH = "/athena";

	private static final ServerType DEF_SERVER_TYPE = ServerType.JETTY;

	/* Make it public so that some admin service to access the server status. */
	public static ContainerServer containerServer;

	enum ServerType {
		JETTY("JETTY"), TOMCAT("TOMCAT");

		private String name;

		private ServerType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static ServerType parse(String name) {
			if ("TOMCAT".equals(name))
				return TOMCAT;

			return JETTY;
		}
	};

	public static void main(String[] args) throws Exception {

		log.info("args: " + Arrays.toString(args));

		int port = DEF_PORT;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				log.error("Not valid port format!");
			}
		}

		String contextPath = DEF_CONTEXT_PATH;
		if (args.length > 1) {
			contextPath = args[1];
		}

		ServerType serverType = DEF_SERVER_TYPE;

		if (args.length > 2) {
			serverType = ServerType.parse(args[2]);
		}
		
		int minThreads = -1;
		if (args.length > 4) {
			// both the minThreads and maxThreads should be required if we set either 
			try {
				minThreads = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				log.error("Not valid minThreads format!");
			}
		}

		int maxThreads = -1;
		if (args.length > 4) {
			try {
				maxThreads = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				log.error("Not valid maxThreads format!");
			}
		}


		log.info("parsed args " + "port:" + port + " contextPath: "
				+ contextPath + " serverType: " + serverType);

		String webappPath = WebappGen.getInstance().extractWebapp();

		log.info("webappPath: " + new File(webappPath).getAbsolutePath());

		if (ServerType.TOMCAT.equals(serverType))
			containerServer = new TomcatServer(port, contextPath, webappPath, minThreads, maxThreads);
		else
			containerServer = new JettyServer(port, contextPath, webappPath, minThreads, maxThreads);

		log.info("AthenaRestServer is starting.");

		containerServer.start();

		log.info("AthenaRestServer started.");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Receive stop signal and stopping container server.");
				containerServer.stop();
				log.info("Stopped container server.");
			}
		});
	}

}
