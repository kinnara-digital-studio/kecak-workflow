package org.joget.directory.model;

import java.io.Serializable;

import org.joget.commons.spring.model.Auditable;

public class UserSalt implements Serializable, Auditable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 964929068235904826L;

	private String id;
	private String userId;
	private String randomSalt;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRandomSalt() {
		return randomSalt;
	}

	public void setRandomSalt(String randomSalt) {
		this.randomSalt = randomSalt;
	}

	public String getAuditTrailId() {
		return id;
	}

}
