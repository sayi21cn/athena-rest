/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.wanda.athena.rest.container.admin.bean;

import java.util.HashSet;

/**
 * The Enum ReturnStatusEnum.
 */
public enum ReturnStatus {

	SUCCESS(200, "成功"),

	SERVER_ERROR(500, "服务器错误，%s"),

	PARAMETER_ERROR(400, "参数错误，%s"),
	/** The database error. */
	DATABASE_ERROR(501, "数据库操作失败，%s"),

	// 业务异常错误码4位，同一模块的前两位一样，按code号的顺序排列
	BIZ_ERROR(4001, "业务异常"),

	;

	/** call rpcserver error. */
	/** The value. */
	private final int value;

	/** The desc. */
	private final String desc;

	/**
	 * Instantiates a new return status enum.
	 * 
	 * @param value
	 *            the value
	 * @param desc
	 *            the desc
	 */
	private ReturnStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets the desc.
	 * 
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	public String getDesc4Log() {
		return "ERROR_CODE:" + value + "\t" + desc + "\t";
	}

	private static HashSet<Integer> hashSet;

	static {
		hashSet = new HashSet<Integer>();
		hashSet.clear();
		for (ReturnStatus returnStatus : ReturnStatus.values()) {
			hashSet.add(returnStatus.getValue());
		}
	}

	public static boolean isDefined(int value) {
		if (hashSet.contains(value)) {
			return true;
		}
		return false;
	}

	public static ReturnStatus get(int value) {
		for (ReturnStatus o : ReturnStatus.values()) {
			if (value == o.getValue()) {
				return o;
			}
		}
		return null;
	}
}
