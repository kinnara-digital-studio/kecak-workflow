package org.joget.directory.model;

import org.joget.commons.spring.model.Auditable;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Role implements Serializable, Auditable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3889499940697373265L;
	
	private String id;
    private String name;
    private String description;
    //join
    @SuppressWarnings("rawtypes")
	private Set users;

    private Date dateCreated;
	private Date dateModified;
	private String createdBy;
	private String modifiedBy;
	private Boolean deleted;
	
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
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (!(other instanceof Role))
            return false;
        Role obj = (Role) other;
        if (id == null) {
            if (obj.id != null)
                return false;
        } else if (!id.equals(obj.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}
