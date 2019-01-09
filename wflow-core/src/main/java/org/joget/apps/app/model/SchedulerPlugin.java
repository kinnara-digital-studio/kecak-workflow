package org.joget.apps.app.model;

import org.joget.plugin.property.model.PropertyEditable;

import javax.annotation.Nonnull;
import java.util.Map;

public interface SchedulerPlugin extends PropertyEditable {
    /**
     * Property for Application Definition
     */
    String PROPERTY_APP_DEFINITION = "appDefinition";

    /**
     * Property for Plugin Manager
     */
    String PROPERTY_PLUGIN_MANAGER = "pluginManager";

    /**
     * Property for Job execution time
     */
    String PROPERTY_TIMESTAMP = "timestamp";

    /**
     * Filter method, return true to run plugin during Cron Job
     *
     * @param properties
     * @return
     */
    boolean filter(@Nonnull Map<String, Object> properties);

    /**
     * Filter method, return true to run plugin during Cron Job
     * Job will only run for PUBLISHED application
     *
     * @param properties
     * @return
     */
    void jobRun(@Nonnull Map<String, Object> properties);
}
