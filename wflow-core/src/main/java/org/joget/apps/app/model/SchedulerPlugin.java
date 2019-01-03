package org.joget.apps.app.model;

import org.joget.plugin.property.model.PropertyEditable;

import javax.annotation.Nonnull;
import java.util.Map;

public interface SchedulerPlugin extends PropertyEditable {
    /**
     * Filter method, return true to run plugin during Cron Job
     * @param properties
     * @return
     */
    boolean filter(@Nonnull Map<String, Object> properties);

    /**
     * Filter method, return true to run plugin during Cron Job
     * @param properties
     * @return
     */
    void jobRun(@Nonnull Map<String, Object> properties);
}
