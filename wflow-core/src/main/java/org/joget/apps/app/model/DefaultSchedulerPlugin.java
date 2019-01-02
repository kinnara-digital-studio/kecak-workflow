package org.joget.apps.app.model;

import org.joget.plugin.base.ExtDefaultPlugin;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class DefaultSchedulerPlugin extends ExtDefaultPlugin implements SchedulerPlugin {
    /**
     * Not used
     * @param properties
     * @return
     */
    @Override
    public Object execute(Map properties) {
        return null;
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public boolean filter(@Nonnull Map<String, Object> properties) {
        return true;
    }
}
