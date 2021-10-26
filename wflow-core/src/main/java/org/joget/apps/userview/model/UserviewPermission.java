package org.joget.apps.userview.model;

import org.joget.directory.model.User;
import org.kecak.apps.userview.model.Platform;

/**
 * A base abstract class to develop a Userview/Form Permission plugin. 
 * 
 */
public abstract class UserviewPermission extends ExtElement {
    private User currentUser;
    private Platform platform = Platform.WEB;

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

    /**
     * Get current platform
     *
     * @return
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Set current platform
     *
     * @param platform
     */
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
