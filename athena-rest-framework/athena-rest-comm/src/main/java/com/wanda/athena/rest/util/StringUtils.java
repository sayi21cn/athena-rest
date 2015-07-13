package com.wanda.athena.rest.util;

public class StringUtils {
	public static boolean isBlank(String value) {
		return (value == null) || ("".equals(value))
				|| ("".equals(value.trim()));
	}
}
