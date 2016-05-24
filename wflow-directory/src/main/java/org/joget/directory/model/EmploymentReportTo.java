package org.joget.directory.model;

import org.joget.commons.spring.model.Auditable;
import java.io.Serializable;
import java.util.Date;

public class EmploymentReportTo implements Serializable, Auditable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5603614156709982699L;
	private String id;
    //join
    private Employment subordinate;
    private Employment reportTo;
    private Date dateCreated;
	private Date dateModified;
	private String createdBy;
	private String modifiedBy;
	private Boolean deleted;
	
    public Employment getReportTo() {
        return reportTo;
    }

    public void setReportTo(Employment reportTo) {
        this.reportTo = reportTo;
    }

    public Employment getSubordinate() {
        return subordinate;
    }

    public void setSubordinate(Employment subordinate) {
        this.subordinate = subordinate;
    }

    public String getAuditTrailId() {
        return subordinate.getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
}
