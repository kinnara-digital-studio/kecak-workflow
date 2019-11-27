package org.kecak.oauth.service;

import org.kecak.oauth.model.Oauth2ClientPlugin;

import javax.annotation.Nullable;

public interface Oauth2LoginService {
    @Nullable
    <T extends Oauth2ClientPlugin> T getPlugin(String className);

    @Nullable
    <T extends Oauth2ClientPlugin> T getPlugin(Class<T> pluginClass);
}
