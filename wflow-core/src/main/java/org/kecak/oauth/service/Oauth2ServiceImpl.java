package org.kecak.oauth.service;

import org.joget.commons.util.SetupManager;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;
import org.kecak.oauth.model.Oauth2ClientPlugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class Oauth2ServiceImpl implements Oauth2Service {
    private PluginManager pluginManager;

    @Nullable
    @Override
    public <T extends Oauth2ClientPlugin> T getPlugin(String className) {
        return Optional.ofNullable(className)
                .map(pluginManager::getPlugin)
                .filter(p -> p instanceof Oauth2ClientPlugin)
                .map(p -> (T)p)
                .map(p -> {
                    String jsonProperties = SetupManager.getSettingValue(p.getClass().getName());
                    Map<String, Object> properties = PropertyUtil.getPropertiesValueFromJson(jsonProperties);

                    if(p instanceof PropertyEditable) {
                        ((PropertyEditable) p).setProperties(properties);
                    }

                    return p;
                })
                .orElse(null);
    }

    @Nullable
    @Override
    public <T extends Oauth2ClientPlugin> T getPlugin(Class<T> pluginClass) {
        return getPlugin(pluginClass.getName());
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}
