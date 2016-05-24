package org.joget.directory.model;

import org.joget.commons.spring.model.Auditable;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class Group implements Serializable, Auditable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5065541896272292107L;
	@NotBlank
    @RegExp(value = "^[0-9a-zA-Z_-]+$")
    private String id;
    @NotBlank
    private String name;
    private String description;
    private String organizationId;
    //join
    @SuppressWarnings("rawtypes")
	private Set users;
    private Organization organization;
    private Boolean readonly = false;
    private Date dateCreated;
	private Date dateModified;
	private String createdBy;
	private String modifiedBy;
	private Boolean deleted;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @SuppressWarnings("rawtypes")
	public Set getUsers() {
        return users;
    }

    public void setUsers(@SuppressWarnings("rawtypes") Set users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
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
