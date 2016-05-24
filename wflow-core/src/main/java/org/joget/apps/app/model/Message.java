package org.joget.apps.app.model;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

@Root
public class Message extends AbstractAppVersionedObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1273825281809860016L;
	@NotBlank
    @Element(required = false)
    private String messageKey;
	
    @NotBlank
    @Element(required = false)
    private String locale;
    
    @Element(required = false)
    private String message;
    
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

    @Override
    public String getId() {
        if (messageKey != null && locale != null) {
            return messageKey + ID_SEPARATOR + locale;
        } else {
            return super.getId();
        }
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
