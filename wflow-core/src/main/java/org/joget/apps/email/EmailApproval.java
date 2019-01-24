package org.joget.apps.email;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.kecak.apps.app.model.DefaultEmailProcessorPlugin;

import java.util.Map;

public class EmailApproval extends DefaultEmailProcessorPlugin {
    @Override
    public void parse(String from, String subject, String body, Map<String, Object> properties) {
        LogUtil.info(getClassName(), "parsing from ["+from+"] subject ["+subject+"] body ["+body+"]");
    }

    @Override
    public String getName() {
        return "Email Approval";
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    @Override
    public String getLabel() {
        return getName();
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/email/emailApproval.json", new String[] {getClassName()}, false);
    }
}
