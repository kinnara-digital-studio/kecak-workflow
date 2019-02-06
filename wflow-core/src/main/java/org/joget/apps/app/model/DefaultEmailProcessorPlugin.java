package org.joget.apps.app.model;

import org.joget.plugin.base.ExtDefaultPlugin;

import java.util.Map;

/**
 * Kecak Exclusive
 */
public abstract class DefaultEmailProcessorPlugin extends ExtDefaultPlugin implements EmailProcessorPlugin {
    /**
     * NOT USED !!!! For future use !!!
     *
     * @param properties
     * @return NaN
     */
    @Override
    public final Object execute(Map properties) {
        return null;
    }

    /**
     * Parse current email content
     * @param properties
     */
    @Override
    public void parse(Map<String, Object> properties) {
        parse(properties.get(PROPERTY_FROM).toString(), properties.get(PROPERTY_SUBJECT).toString(), properties.get(PROPERTY_BODY).toString(), properties);
    }

    public abstract void parse(String from, String subject, String body, Map<String, Object> properties);
}
