package org.joget.apps.app.model;

import org.joget.plugin.base.ExtDefaultPlugin;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author aristo
 *
 * Default Implementation of Scheduler Plugin
 */
public abstract class DefaultSchedulerPlugin extends ExtDefaultPlugin implements SchedulerPlugin {

    /**
     * Overwrite this method to implement custom filter during scheduler execution
     * @param properties
     * @return
     */
    @Override
    public boolean filter(@Nonnull Map<String, Object> properties) {
        return true;
    }

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
}
