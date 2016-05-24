package org.joget.apps.app.model;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class PluginDefaultProperties extends AbstractAppVersionedObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2189672697777490769L;
	@Element(required = false)
    private String pluginName;
	
    @Element(required = false)
    private String pluginDescription;
    
    @Element(required = false)
    private String pluginProperties;
    
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

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public void setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;
    }

    public String getPluginProperties() {
        return pluginProperties;
    }

    public void setPluginProperties(String pluginProperties) {
        this.pluginProperties = pluginProperties;
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
