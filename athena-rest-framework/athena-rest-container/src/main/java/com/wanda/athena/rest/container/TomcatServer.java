package com.wanda.athena.rest.container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.mortbay.thread.BoundedThreadPool;

import com.wanda.athena.rest.util.StringUtils;
import com.wanda.athena.rest.util.Utils;

public class TomcatServer extends AbstractContainerServer implements
		ContainerServer {
	private static final String WEB_EXTRA_RESOURCE_PATHS = "web.extraResourcePaths";
	private static final String WEB_ASYNC_TIMEOUT = "web.asyncTimeout";
	private static final String WEB_MIN_PROCESSORS = "web.minProcessors";
	private static final String WEB_MAX_PROCESSORS = "web.maxProcessors";
	private static final String WEB_ACCEPT_COUNT = "web.acceptCount";
	private static final String WEB_MAX_THREADS = "web.maxThreads";
	private static final String WEB_MIN_SPARE_THREADS = "web.minSpareThreads";
	private static final String WEB_REDIRECT_PORT = "web.redirectPort";

	private static final Integer DEFAULT_WEB_REDIRECT_PORT = Integer
			.valueOf(8443);
	private static final Integer DEFAULT_ASYNC_TIMEOUT = Integer.valueOf(10000);
	private static final Integer DEFAULT_MIN_PROCESSORS = Integer.valueOf(50);
	private static final Integer DEFAULT_MAX_PROCESSORS = Integer.valueOf(500);
	private static final Integer DEFAULT_ACCEPT_COUNT = Integer.valueOf(1024);
	private static final Integer DEFAULT_MIN_SPARE_THREADS = Integer
			.valueOf(50);
	private static final Integer DEFAULT_MAX_THREADS = Integer.valueOf(500);
	private static final String DEFAULT_CONNECTOR_PROTOCAL = "org.apache.coyote.http11.Http11NioProtocol";

	private final Properties properties = new Properties();

	private Tomcat tomcat;

	public TomcatServer(int port, String contextPath, String webappPath) {
		init();

		this.port = port;
		this.contextPath = contextPath;
		this.webappPath = webappPath;
	}

	public TomcatServer(int port, String contextPath, String webappPath, int minThreads, int maxThreads) {
		this(port, contextPath, webappPath);
		
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}

	private void init() {
		try {
			InputStream propertiesInputStream = TomcatServer.class
					.getResourceAsStream("/tomcat.properties");

			if (propertiesInputStream == null) {
				log.error("Embedded Tomcat Server can't access properties file. Exit...");

				exit();
			}
			properties.load(propertiesInputStream);

		} catch (IOException e) {
			log.error(
					"Embedded Tomcat Server can't access properties file. Exit...",
					e);

			exit();
		}
	}

	private void initTomcat() {
		serverStatus = ServerStatus.STARTING;

		tomcat = new Tomcat();
		tomcat.setPort(port);

		// Changed it to use NIO due to poor performance in burdon test
		Connector connector = new Connector(Utils.getStringProperty(properties, "web.connectorProtocol"));

		
		connector.setURIEncoding("UTF-8");
		connector.setPort(port);
		connector.setUseBodyEncodingForURI(true);
		connector.setAsyncTimeout(Utils.getIntegerValue(properties,
				WEB_ASYNC_TIMEOUT, DEFAULT_ASYNC_TIMEOUT));
		connector.setAttribute("minProcessors", Utils.getIntegerValue(
				properties, WEB_MIN_PROCESSORS, DEFAULT_MIN_PROCESSORS));
		connector.setAttribute("maxProcessors", Utils.getIntegerValue(
				properties, WEB_MAX_PROCESSORS, DEFAULT_MAX_PROCESSORS));
		connector.setAttribute("acceptCount", Utils.getIntegerValue(properties,
				WEB_ACCEPT_COUNT, DEFAULT_ACCEPT_COUNT));
		connector.setAttribute("minSpareThreads", Utils.getIntegerValue(
				properties, WEB_MIN_SPARE_THREADS, DEFAULT_MIN_SPARE_THREADS));
		connector.setAttribute("maxThreads", Utils.getIntegerValue(properties,
				WEB_MAX_THREADS, DEFAULT_MAX_THREADS));
		connector.setRedirectPort(Utils.getIntegerValue(properties,
				WEB_REDIRECT_PORT, DEFAULT_WEB_REDIRECT_PORT));
		
		if (this.minThreads != -1 && this.maxThreads != -1) {
			connector.setAttribute("minThreads", minThreads);
			connector.setAttribute("maxThreads", maxThreads);
		}

		Service tomcatService = tomcat.getService();
		tomcatService.addConnector(connector);
		tomcat.setConnector(connector);

		Context context = null;
		try {
			context = tomcat.addWebapp(contextPath,
					new File(webappPath).getAbsolutePath());
		} catch (ServletException e) {
			log.error("Failed to add webapp + " + webappPath, e);

			exit();
		}
		context.setLoader(new WebappLoader(Thread.currentThread()
				.getContextClassLoader()));

		String extraResourcePaths = properties
				.getProperty(WEB_EXTRA_RESOURCE_PATHS);
		if (!StringUtils.isBlank(extraResourcePaths)) {
			VirtualDirContext virtualDirContext = new VirtualDirContext();
			virtualDirContext.setExtraResourcePaths(extraResourcePaths);
			context.setResources(virtualDirContext);
		}

		StandardServer server = (StandardServer) tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
	}

	protected void doStart() throws Exception {
		log.info("Initializing tomcat server actually.");

		initTomcat();

		log.info("Starting tomcat server actually.");
		tomcat.start();

		log.info("Started tomcat server now.");

	}

	public void doStop() throws LifecycleException {
		log.info("Stoping tomcat server actually.");
		tomcat.stop();

		log.info("Waiting tomcat server to exit.");
		tomcat.getServer().await();

		log.info("Stopped tomcat server now.");
	}

	public ServerStatus status() {
		return serverStatus;
	}

}
