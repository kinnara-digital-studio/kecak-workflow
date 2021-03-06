package org.kecak.directory.ext;

import org.kecak.directory.model.service.DirectoryManagerAuthenticator;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.ExtDefaultPlugin;

/**
 * Delegate class to perform user authentication.
 */
public class DirectoryManagerAuthenticatorImpl extends ExtDefaultPlugin implements DirectoryManagerAuthenticator {

    public String getName() {
        return "DirectoryManager Authenticator";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "DirectoryManager Authenticator";
    }
    
    /**
     * Authenticate a user based on the username and password using the specified DirectoryManager.
     * @param directoryManager
     * @param username
     * @param password
     * @return 
     */
    public boolean authenticate(DirectoryManager directoryManager, String username, String password) {
        boolean authenticated = directoryManager.authenticate(username, password);
        return authenticated;
    }
    
}
