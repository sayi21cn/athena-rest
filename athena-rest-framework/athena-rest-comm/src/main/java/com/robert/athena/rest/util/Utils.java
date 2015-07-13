package com.robert.athena.rest.util;

import java.util.Properties;

public class Utils {
	public static String getStringProperty(Properties properties, String key) {
		String value = System.getProperty(key);
		if (StringUtils.isBlank(value)) {
			value = properties.getProperty(key);
		}

		if (value != null) {
			value = value.trim();
		}
		return value;
	}

	public static int getIntegerValue(Properties properties, String key,
			int defaultValue) {
		String value = getStringProperty(properties, key);

		Integer result = null;
		try {
			result = Integer.valueOf(value);
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}

}
