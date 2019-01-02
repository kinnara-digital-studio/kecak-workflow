package org.joget.scheduler.model;

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
