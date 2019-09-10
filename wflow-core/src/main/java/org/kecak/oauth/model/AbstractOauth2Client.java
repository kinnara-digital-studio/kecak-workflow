package org.kecak.oauth.model;

import org.joget.directory.model.User;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.property.model.PropertyEditable;
import org.springframework.security.authentication.BadCredentialsException;

public abstract class DefaultOauth2Client extends ExtDefaultPlugin implements PropertyEditable {
    public abstract User getUser(String clientToken) throws BadCredentialsException;

    @Override
    public String getI18nLabel() {
        return getLabel();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }
}
