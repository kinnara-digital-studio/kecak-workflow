package org.kecak.oauth.model;

import org.joget.directory.model.User;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.property.model.PropertyEditable;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOauth2Client extends ExtDefaultPlugin implements Oauth2ClientPlugin, PropertyEditable {
    @Override
    public String getI18nLabel() {
        return getLabel();
    }

    /**
     * propery options shou
     * @return
     */
    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public User getUser(String clientToken) throws BadCredentialsException {
        return null;
    }
}
