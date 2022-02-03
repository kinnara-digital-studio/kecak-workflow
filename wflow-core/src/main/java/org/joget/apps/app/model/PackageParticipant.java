package org.joget.apps.app.model;

import java.io.Serializable;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents a mapping for a workflow participant to directory users.
 */
@Root
public class PackageParticipant implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2502700518539888766L;
	public static final String TYPE_GROUP = "group";
    public static final String TYPE_USER = "user";
    public static final String TYPE_REQUESTER = "requester";
    public static final String TYPE_REQUESTER_HOD = "requesterHod";
    public static final String TYPE_REQUESTER_HOD_IGNORE_REPORT_TO = "requesterHodIgnoreReportTo";
    public static final String TYPE_REQUESTER_REPORT_TO = "requesterReportTo";
    public static final String TYPE_REQUESTER_SUBORDINATES = "requesterSubordinates";
    public static final String TYPE_REQUESTER_DEPARTMENT = "requesterDepartment";
    public static final String TYPE_HOD = "hod";
    public static final String TYPE_DEPARTMENT = "department";
    public static final String TYPE_WORKFLOW_VARIABLE = "workflowVariable";
    public static final String TYPE_PLUGIN = "plugin";
    public static final String TYPE_ROLE = "role";
    public static final String VALUE_ROLE_ADMIN = "adminUser";
    public static final String VALUE_ROLE_LOGGED_IN_USER = "loggedInUser";
    
    private PackageDefinition packageDefinition;
    @Element(required = false)
    private String processDefId;
    
    @Element(required = false)
    private String participantId;
    
    @Element(required = false)
    private String type;
    
    @Element(required = false)
    private String value;
    
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
     * Unique ID (primary key) for the object, which consists of the process def ID and participant ID separated by ::.
     * @return
     */
    public String getUid() {
        String key = getProcessDefId() + PackageDefinition.UID_SEPARATOR + getParticipantId();
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

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
