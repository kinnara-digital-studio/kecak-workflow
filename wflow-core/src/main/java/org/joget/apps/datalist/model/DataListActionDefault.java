package org.joget.apps.datalist.model;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.service.JsonUtil;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.workflow.util.WorkflowUtil;

import java.util.Map;

/**
 * Base class for a data list action
 */
public abstract class DataListActionDefault extends ExtDefaultPlugin implements DataListAction {

    /**
     * Flag that decide to show an action object or not when no record
     * 
     * Default to the value of property "visible".
     * 
     * @return 
     */
    public Boolean getVisibleOnNoRecord() {
        if (getPropertyString("visible") != null && "true".equalsIgnoreCase(getPropertyString("visible"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPermitted() {
        UserviewPermission permission = generatePlugin((Map<String, Object>) getProperty(JsonUtil.PROPERTY_PERMISSION));
        if(WorkflowUtil.getCurrentUsername() == null || permission == null) {
            return true;
        }

        DirectoryManager directoryManager = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
        User user = directoryManager.getUserByUsername(WorkflowUtil.getCurrentUsername());
        permission.setCurrentUser(user);
        return permission.isAuthorize();
    }

    private <T extends Plugin> T generatePlugin(Map<String, Object> plugin) {
        if(plugin == null)
            return null;

        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        Map<String, Object> pluginProperties = (Map<String, Object>)plugin.get("properties");
        String pluginClassName = String.valueOf(plugin.get("className"));
        Plugin pluginObject = pluginManager.getPlugin(pluginClassName);
        if(pluginObject == null)
            return null;

        ((PropertyEditable)pluginObject).setProperties(pluginProperties);
        return (T) pluginObject;
    }
}
