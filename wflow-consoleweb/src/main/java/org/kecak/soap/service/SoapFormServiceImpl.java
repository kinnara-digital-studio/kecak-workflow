package org.kecak.soap.service;

import org.joget.apps.app.dao.*;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
import org.joget.apps.userview.service.UserviewService;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Map;

@Service("soapFormService")
public class SoapFormServiceImpl implements SoapFormService {
    @Autowired
    FormService formService;
    @Autowired
    WorkflowManager workflowManager;
    @Autowired
    AppDefinitionDao appDefinitionDao;
    @Autowired
    DatalistDefinitionDao datalistDefinitionDao;
    @Autowired
    UserviewDefinitionDao userviewDefinitionDao;
    @Autowired
    MessageDao messageDao;
    @Autowired
    EnvironmentVariableDao environmentVariableDao;
    @Autowired
    PluginDefaultPropertiesDao pluginDefaultPropertiesDao;
    @Autowired
    PackageDefinitionDao packageDefinitionDao;
    @Autowired
    PluginManager pluginManager;
    @Autowired
    FormDataDao formDataDao;
    @Autowired
    UserviewService userviewService;
    @Autowired
    AppService appService;


    @Override
    public void formSubmit(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String formDefId, @Nonnull Map<String, String> data) {
        final FormData formData =  new FormData();
        for(Map.Entry<String, String> e : data.entrySet()) {
            formData.addRequestParameterValues(e.getKey(), new String[] {e.getValue()});
        }

        appService.submitForm(appId, appVersion.toString(), formDefId, formData, false);
    }
}
