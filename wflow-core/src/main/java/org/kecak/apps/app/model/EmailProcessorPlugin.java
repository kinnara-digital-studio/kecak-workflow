package org.kecak.apps.app.model;

import org.joget.plugin.property.model.PropertyEditable;

import java.util.Map;

/**
 * @author aristo
 */
public interface EmailProcessorPlugin extends PropertyEditable {
    String PROPERTY_APP_DEFINITION = "appDefinition";
    String PROPERTY_FROM = "from";
    String PROPERTY_SUBJECT = "subject";
    String PROPERTY_BODY = "body";
    String PROPERTY_EXCHANGE = "exchange";

    void parse(Map<String, Object> properties);
}
