package org.joget.apps.app.model;

import java.io.Serializable;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents a mapping for a workflow activity tool to a plugin.
 */
@Root
public class PackageActivityPlugin implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5949274768412153553L;
	
	private PackageDefinition packageDefinition;
	
    @Element(required = false)
    private String processDefId;
    
    @Element(required = false)
    private String activityDefId;
    
    @Element(required = false)
    private String pluginName;
    
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

    /**
     * Unique ID (primary key) for the object, which consists of the process def ID and activity def ID separated by ::.
     * @return
     */
    public String getUid() {
        String key = getProcessDefId() + PackageDefinition.UID_SEPARATOR + getActivityDefId();
        return key;
    }

    public void setUid(String uid) {
    }

    public PackageDefinition getPackageDefinition() {
        return packageDefinition;
    }

    public void setPackageDefinition(PackageDefinition packageDefinition) {
        this.packageDefinition = packageDefinition;
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    public String getActivityDefId() {
        return activityDefId;
    }

    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
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
}
