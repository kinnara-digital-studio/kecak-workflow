package org.joget.apps.app.lib;

import org.joget.apps.app.model.DefaultHashVariablePlugin;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.AuthTokenService;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class AuthTokenHashVariable extends DefaultHashVariablePlugin {
    private final static String LABEL = "Auth Token Hash Variable";

    @Override
    public String getPrefix() {
        return "authToken";
    }

    @Override
    public String processHashVariable(String variableKey) {
        ApplicationContext applicationContext = AppUtil.getApplicationContext();
        AuthTokenService authTokenService = (AuthTokenService) applicationContext.getBean("authTokenService");
        DirectoryManager directoryManager = (DirectoryManager) applicationContext.getBean("directoryManager");

        // get username
        String username = Optional.ofNullable(variableKey)

                // get current username from directory
                .map(directoryManager::getUserByUsername)
                .map(User::getUsername)
                .orElse(WorkflowUtil.getCurrentUsername());

        return authTokenService.generateToken(username, null);
    }

    @Override
    public String getName() {
        return LABEL;
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getDescription() {
        return getClass().getPackage().getImplementationTitle();
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }
}
