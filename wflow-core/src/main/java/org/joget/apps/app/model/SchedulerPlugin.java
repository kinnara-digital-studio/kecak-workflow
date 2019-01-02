package org.joget.apps.app.model;

import org.joget.plugin.property.model.PropertyEditable;
import org.quartz.JobExecutionContext;

import javax.annotation.Nonnull;
import java.util.Map;

public interface SchedulerPlugin extends PropertyEditable {
    boolean filter(@Nonnull Map<String, Object> properties);

    void execute(@Nonnull JobExecutionContext context, @Nonnull Map<String, Object> properties);
}
