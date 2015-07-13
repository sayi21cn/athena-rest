package com.wanda.athena.rest.container.admin.bean;

public class BaseResult {
	private int status = ReturnStatus.SUCCESS.getValue();
	private String message = ReturnStatus.SUCCESS.getDesc();

	@Override
	public String toString() {
		return "Result [status=" + status + ", message=" + message + "]";
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
