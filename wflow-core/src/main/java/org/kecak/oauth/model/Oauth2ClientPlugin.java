package org.kecak.oauth.model;

import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.Plugin;
import org.springframework.security.authentication.BadCredentialsException;

public interface Oauth2ClientPlugin extends Plugin {
    /**
     * Validate clientToken and find {@link User} from {@link DirectoryManager} based on clientToken
     *
     * @param clientToken token sent from client / browser that needs to be verified
     * @return respective {@link User} based on clientToken
     * @throws BadCredentialsException if clientToken is invalid or miss formed
     */
    User getUser(String clientToken) throws BadCredentialsException;

    /**
     * Render oauth2 login button in login page
     * @return
     */
    String renderHtmlLoginButton();
}
