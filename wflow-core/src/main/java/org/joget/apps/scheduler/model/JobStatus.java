package org.joget.apps.scheduler.model;

public enum JobStatus {

	SUCCESS("Success"), FAIL("Fail");

	private String code;

	private JobStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
