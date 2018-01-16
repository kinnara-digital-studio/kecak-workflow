package org.joget.apps.userview.model;

import org.joget.directory.model.User;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * A base abstract class to develop a Userview/Form Permission plugin. 
 * 
 */
public abstract class UserviewPermission extends ExtElement {

    private User currentUser;
    private WorkflowAssignment workflowAssignment;

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
     * Get current active assignment
     * @return NULL if current element is not being accessed from process
     */
    public WorkflowAssignment getWorkflowAssignment() {
        return workflowAssignment;
    }

    /**
     * Set current assignment
     * @param workflowAssignment
     */
    public void setWorkflowAssignment(WorkflowAssignment workflowAssignment) {
        this.workflowAssignment = workflowAssignment;
    }

    /**
     * Check the current user is authorized to proceed.
     * @return 
     */
    public abstract boolean isAuthorize();
}
