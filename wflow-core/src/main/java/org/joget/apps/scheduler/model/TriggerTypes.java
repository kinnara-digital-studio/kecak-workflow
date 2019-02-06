package org.joget.apps.scheduler.model;

/**
 * Kecak Exclusive
 */
public enum TriggerTypes {
	
	CRON("Cron"), Simple("Simple");
	
	private String code;

	private TriggerTypes(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
