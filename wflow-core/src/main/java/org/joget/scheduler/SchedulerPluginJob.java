package org.joget.scheduler;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.apps.app.model.DefaultSchedulerPlugin;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;

public class SchedulerPluginJob implements Job {
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");

        List<Plugin> schedulerPluginList = (List<Plugin>) pluginManager.list(DefaultSchedulerPlugin.class);

        // scheduler plugins not available
        if(schedulerPluginList == null)
            return;

        schedulerPluginList.stream()
                .map(p -> (DefaultSchedulerPlugin)p)
                .peek(p -> LogUtil.info(getClass().getName(), "Executing scheduler plugins ["+p.getClassName()+"]"))
                .filter(p -> p.filter(new HashMap<>()))
                .forEach(p -> p.execute(context, new HashMap<>()));
    }
}
