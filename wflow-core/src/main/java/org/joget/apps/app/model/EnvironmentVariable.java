package org.joget.apps.app.model;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class EnvironmentVariable extends AbstractAppVersionedObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3023741158112072008L;
	@Element(required = false)
    private String value;
	
    @Element(required = false)
    private String remarks;
    
    @Element(required = false)
    private Date dateCreated;
    
    @Element(required = false)
    private Date dateModified;
    
    @Element(required = false)
	private String createdBy;
	
	@Element(required = false)
	private String modifiedBy;
	
	@Element(required = false)
	private Boolean deleted;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getValue() {
        return value;
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

	public void setValue(String value) {
        this.value = value;
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
