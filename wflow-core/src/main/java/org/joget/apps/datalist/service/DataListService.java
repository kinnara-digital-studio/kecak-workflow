package org.joget.apps.datalist.service;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.apps.userview.model.UserviewTheme;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Service class to manage data lists
 */
@Service
public class DataListService {

    @Autowired
    PluginManager pluginManager;

    @Autowired
    WorkflowUserManager workflowUserManager;

    @Autowired
    @Qualifier("main")
    ExtDirectoryManager directoryManager;

    /**
     * Create a DataList object from JSON definition
     *
     * @param json
     * @param ignoreColumnPermission
     * @return
     */
    public DataList fromJson(String json, boolean ignoreColumnPermission, @Nullable UserviewTheme theme) {
        json = AppUtil.processHashVariable(json, null, StringUtil.TYPE_JSON, null);

        final DataList dataList = JsonUtil.fromJson(json, DataList.class);

        final Optional<UserviewPermission> optPermission = Optional.ofNullable(dataList)
                .map(DataList::getPermission);

        if (optPermission.isPresent()) {
            final UserviewPermission permission = optPermission.get();
            final DirectoryManager directoryManager = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
            final User user = directoryManager.getUserByUsername(WorkflowUtil.getCurrentUsername());

            permission.setCurrentUser(user);
            if (!permission.isAuthorize()) {
                LogUtil.info(getClass().getName(), "User [" + user.getUsername() + "] is unauthorized to access datalist [" + dataList.getId() + "]");
                return null;
            }
        }

        // check column permission
        if (dataList != null) {
            DataListColumn[] columns = Optional.of(dataList)
                    .map(DataList::getColumns)
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .filter(dataListColumn -> ignoreColumnPermission || dataListColumn.isPermitted())
                    .toArray(DataListColumn[]::new);

            dataList.setColumns(columns);

            dataList.setTheme(theme);
        }

        return dataList;
    }

    public DataList fromJson(String json, boolean ignoreColumnPermission) {
        return fromJson(json, ignoreColumnPermission, null);
    }

    /**
     * Create a DataList object from JSON definition
     *
     * @param json
     * @return
     */
    public DataList fromJson(String json) {
        return fromJson(json, true);
    }

    /**
     * Retrieve a binder plugin by ID. For now the ID is the class name
     *
     * @param id
     * @return
     */
    public DataListBinder getBinder(String id) {
        DataListBinder binder = null;
        try {
            String className = id;
            binder = (DataListBinder) pluginManager.getPlugin(className);
        } catch (Exception ex) {
            LogUtil.error(DataListService.class.getName(), ex, "");
        }
        return binder;
    }

    /**
     * Retrieve an action plugin by class name.
     *
     * @param className
     * @return
     */
    public DataListAction getAction(String className) {
        DataListAction action = null;
        try {
            action = (DataListAction) pluginManager.getPlugin(className);
        } catch (Exception ex) {
            LogUtil.error(DataListService.class.getName(), ex, "");
        }
        return action;
    }

    /**
     * Returns an array of available binder plugins. For now, ID is the fully qualified class name.
     *
     * @return
     */
    public DataListBinder[] getAvailableBinders() {
        Collection<DataListBinder> list = new ArrayList<DataListBinder>();
        Collection<Plugin> pluginList = pluginManager.list(DataListBinder.class);
        for (Plugin plugin : pluginList) {
            if (plugin instanceof DataListBinder) {
                list.add((DataListBinder) plugin);
            }
        }
        DataListBinder[] result = (DataListBinder[]) list.toArray(new DataListBinder[0]);
        return result;
    }

    /**
     * Returns an array of available action plugins. For now, ID is the fully qualified class name.
     *
     * @return
     */
    public DataListAction[] getAvailableActions() {
        Collection<DataListAction> list = new ArrayList<DataListAction>();
        Collection<Plugin> pluginList = pluginManager.list(DataListAction.class);
        for (Plugin plugin : pluginList) {
            if (plugin instanceof DataListAction) {
                list.add((DataListAction) plugin);
            }
        }
        DataListAction[] result = (DataListAction[]) list.toArray(new DataListAction[0]);
        return result;
    }

    /**
     * Returns an array of available formatter plugins. For now, ID is the fully qualified class name.
     *
     * @return
     */
    public DataListColumnFormat[] getAvailableFormats() {
        Collection<DataListColumnFormat> list = new ArrayList<DataListColumnFormat>();
        Collection<Plugin> pluginList = pluginManager.list(DataListColumnFormat.class);
        for (Plugin plugin : pluginList) {
            if (plugin instanceof DataListColumnFormat) {
                list.add((DataListColumnFormat) plugin);
            }
        }
        DataListColumnFormat[] result = (DataListColumnFormat[]) list.toArray(new DataListColumnFormat[0]);
        return result;
    }


    /**
     * Check authorization using permission
     *
     * @param dataList
     * @return
     */
    public boolean isAuthorize(DataList dataList) {
        return Optional.ofNullable(dataList)
                .map(DataList::getPermission)
                .map(userviewPermission -> {
                    User user = Optional.of(workflowUserManager.getCurrentUsername())
                            .map(directoryManager::getUserByUsername)
                            .orElse(null);

                    userviewPermission.setCurrentUser(user);
                    return userviewPermission.isAuthorize();
                })
                .orElse(true);
    }
}
