package org.kecak.oauth.model;

import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.property.model.PropertyEditable;

public abstract class AbstractOauth2Client extends ExtDefaultPlugin implements Oauth2ClientPlugin, PropertyEditable {
    @Override
    public String getI18nLabel() {
        return getLabel();
    }
}
