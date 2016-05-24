package org.joget.apps.app.model;

import java.io.Serializable;
import java.util.Date;

import org.joget.apps.form.model.Form;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Represents a mapping for a workflow activity to a form.
 */
@Root
public class PackageActivityForm implements Serializable, Cloneable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6264588131279128690L;
	public static final String ACTIVITY_FORM_TYPE_SINGLE = "SINGLE";
    public static final String ACTIVITY_FORM_TYPE_EXTERNAL = "EXTERNAL";
    
    private PackageDefinition packageDefinition;
    
    @Element(required = false)
    private String processDefId;
    
    @Element(required = false)
    private String activityDefId;
    
    @Element(required = false)
    private String formId;
    
    @Element(required = false)
    private String formUrl;
    
    @Element(required = false)
    private String formIFrameStyle;
    
    @Element(required = false)
    private String type;
    
    @Element(required = false)
    private boolean autoContinue;
    
    @Element(required = false)
    private Boolean disableSaveAsDraft;
    
    @Element(required = false)
    private Form form;
    
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormIFrameStyle() {
        return formIFrameStyle;
    }

    public void setFormIFrameStyle(String formIFrameStyle) {
        this.formIFrameStyle = formIFrameStyle;
    }

    public boolean isAutoContinue() {
        return autoContinue;
    }

    public void setAutoContinue(boolean autoContinue) {
        this.autoContinue = autoContinue;
    }

    public Boolean getDisableSaveAsDraft() {
        if (disableSaveAsDraft == null) {
            disableSaveAsDraft = false;
        }
        return disableSaveAsDraft;
    }

    public void setDisableSaveAsDraft(Boolean disableSaveAsDraft) {
        this.disableSaveAsDraft = disableSaveAsDraft;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
