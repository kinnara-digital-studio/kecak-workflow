package org.joget.apps.app.model;

import org.joget.plugin.base.ExtDefaultPlugin;

import java.util.Map;

/**
 * @author aristo
 *
 * Default Implementation of Scheduler Plugin
 */
public abstract class DefaultSchedulerPlugin extends ExtDefaultPlugin implements SchedulerPlugin {
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
