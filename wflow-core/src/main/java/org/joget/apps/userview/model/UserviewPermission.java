package org.joget.apps.userview.model;

import org.joget.apps.form.model.FormData;
import org.joget.directory.model.User;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * A base abstract class to develop a Userview/Form Permission plugin. 
 * 
 */
public abstract class UserviewPermission extends ExtElement {

    private User currentUser;
    private FormData formData;

    /**
     * Get current form data, null if permission is not assigned to a form
     * @return
     */
    public FormData getFormData() {
        return formData;
    }

    /**
     * Set current form data
     * @param formData
     */
    public void setFormData(FormData formData) {
        this.formData = formData;
    }

    /**
     * Gets current logged in user. 
     * @return NULL if anonymous.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets current logged in user.
     * @param currentUser 
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Check the current user is authorized to proceed.
     * @return 
     */
    public abstract boolean isAuthorize();
}
