package org.joget.directory.model;

import java.io.Serializable;
import java.util.Date;

import org.joget.commons.spring.model.Auditable;

public class UserSalt implements Serializable, Auditable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 964929068235904826L;

	private String id;
	private String userId;
	private String randomSalt;
	private Date dateCreated;
	private Date dateModified;
	private String createdBy;
	private String modifiedBy;
	private Boolean deleted;
	
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getAuditTrailId() {
		return id;
	}

}
