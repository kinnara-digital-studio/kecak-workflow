package org.kecak.apps.scheduler;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DefaultSchedulerPlugin;
import org.joget.apps.app.model.PluginDefaultProperties;
import org.joget.apps.app.model.SchedulerPlugin;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author aristo
 *
 * Job for Scheduler Plugin
 */
public class SchedulerPluginJob implements Job {
    /**
     * Execute
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final ApplicationContext applicationContext = AppUtil.getApplicationContext();
        final PluginManager pluginManager = (PluginManager) applicationContext.getBean("pluginManager");
        final AppDefinitionDao appDefinitionDao = (AppDefinitionDao) applicationContext.getBean("appDefinitionDao");

        Collection<AppDefinition> appDefinitions = appDefinitionDao.findPublishedApps(null, null, null, null);
        if(appDefinitions == null) {
            return;
        }

        appDefinitions.forEach(appDefinition -> {
            Collection<PluginDefaultProperties> pluginDefaultProperties = appDefinition.getPluginDefaultPropertiesList();
            if(pluginDefaultProperties != null) {
                pluginDefaultProperties.forEach(pluginDefaultPropery -> {
                    Stream.of(pluginDefaultPropery)
                            .map(p -> pluginManager.getPlugin(p.getId()))
                            .filter(p -> p instanceof DefaultSchedulerPlugin)
                            .map(p -> (DefaultSchedulerPlugin)p)
                            .forEach(p -> {
                                Map<String, Object> pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginDefaultPropery.getPluginProperties());
                                p.setProperties(pluginProperties);

                                Map<String, Object> parameterProperties = new HashMap<>(pluginProperties);
                                parameterProperties.put(SchedulerPlugin.PROPERTY_APP_DEFINITION, appDefinition);
                                parameterProperties.put(SchedulerPlugin.PROPERTY_PLUGIN_MANAGER, pluginManager);
                                parameterProperties.put(SchedulerPlugin.PROPERTY_TIMESTAMP, context.getFireTime());

                                if(p.filter(parameterProperties)) {
                                    LogUtil.info(getClass().getName(), "Running Scheduler Job Plugin ["+p.getName()+"] for application ["+appDefinition.getAppId()+"]");
                                    p.jobRun(parameterProperties);
                                }
                            });
                });
            }
        });
    }
}
