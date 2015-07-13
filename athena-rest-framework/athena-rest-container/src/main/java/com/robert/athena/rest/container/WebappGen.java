package com.robert.athena.rest.container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.robert.athena.rest.util.FileUtils;

public class WebappGen {
	private static final String DEF_WEB_XML_CLASSPATH = "/webapp/WEB-INF/web.xml";

	private static final String WEB_XML_CLASSPATH = "/webapp/WEB-INF/web-cust.xml";

	private static final String WEB_XML_ROOT_PATH = "webapp/";

	private static final String WEB_XML_PATH = WEB_XML_ROOT_PATH
			+ "WEB-INF/web.xml";

	private static WebappGen webappGen = new WebappGen();

	private WebappGen() {
	};

	public String extractWebapp() throws IOException {
		String webXmlClasspath = DEF_WEB_XML_CLASSPATH;

		if (isClasspathResource(WEB_XML_CLASSPATH))
			webXmlClasspath = WEB_XML_CLASSPATH;

		extractClasspathResource(webXmlClasspath,
				new File(WEB_XML_PATH).getAbsolutePath());

		return WEB_XML_ROOT_PATH;
	}

	private void extractClasspathResource(String classpathPath, String filePath)
			throws IOException {
		InputStream fileSteam = WebappGen.class
				.getResourceAsStream(classpathPath);

		FileUtils.esurePathExist(filePath);
		try {
			FileUtils.saveStream(fileSteam, new File(filePath));
		} finally {
			fileSteam.close();
		}
	}

	private boolean isClasspathResource(String classpathPath)
			throws IOException {
		InputStream fileStream = null;
		try {
			fileStream = WebappGen.class.getResourceAsStream(classpathPath);

			return fileStream != null;
		} finally {
			if (fileStream != null)
				fileStream.close();
		}
	}

	public static WebappGen getInstance() {
		return webappGen;
	}
}
