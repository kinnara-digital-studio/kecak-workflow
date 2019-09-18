package org.joget.directory.model;

import org.joget.commons.spring.model.Auditable;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.sql.Blob;
import org.joget.commons.util.StringUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class User implements Serializable, Auditable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5248198835477844227L;
	public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;
    public static final String LOGIN_HASH_DELIMINATOR = "::";

    private String id;
    @NotBlank
    @RegExp(value = "^[\\.@0-9a-zA-Z_-]+$")
    private String username;
    private String password;
    @NotBlank
    private String firstName;
    private String lastName;
    private String email;
    private Integer active;
    private String timeZone;
    private String locale;
	private Blob profilePicture;
	private String telephoneNumber;

    //join
    @SuppressWarnings("rawtypes")
	private Set roles;
    @SuppressWarnings("rawtypes")
	private Set groups;
    @SuppressWarnings("rawtypes")
	private Set employments;
    //additional field
    private String oldPassword;
    private String confirmPassword;
    private Boolean readonly = false;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
	
	public Blob getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Blob profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getTimeZoneLabel() {
        return TimeZoneUtil.getList().get(getTimeZone());
    }

    @SuppressWarnings("rawtypes")
	public Set getRoles() {
        return roles;
    }

    public void setRoles(@SuppressWarnings("rawtypes") Set roles) {
        this.roles = roles;
    }

    @SuppressWarnings("rawtypes")
	public Set getGroups() {
        return groups;
    }

    public void setGroups(@SuppressWarnings("rawtypes") Set groups) {
        this.groups = groups;
    }

    @SuppressWarnings("rawtypes")
	public Set getEmployments() {
        return employments;
    }

    public void setEmployments(@SuppressWarnings("rawtypes") Set employments) {
        this.employments = employments;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getAuditTrailId() {
        return username;
    }

    public String getLoginHash() {
        return StringUtil.md5(username + LOGIN_HASH_DELIMINATOR + password);
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

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
