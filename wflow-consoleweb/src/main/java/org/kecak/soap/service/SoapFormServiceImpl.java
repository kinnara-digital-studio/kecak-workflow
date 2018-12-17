package org.kecak.soap.service;

import org.joget.apps.app.dao.*;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
import org.joget.apps.userview.service.UserviewService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
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
    public String formSubmit(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String formDefId, @Nonnull Map<String, String> data) {
        final FormData formData = collectFormData(data);
        final FormData result = appService.submitForm(appId, appVersion.toString(), formDefId, formData, false);
        return result.getPrimaryKeyValue();
    }

    @Override
    public void formAssignmentComplete(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String assignmentId, @Nonnull Map<String, String> fieldData, @Nonnull Map<String, String> workflowVariables) {
        final FormData formData = collectFormData(fieldData);

        AppDefinition appDef = appDefinitionDao.loadVersion(appId, appVersion);
        if(appDef == null) {
            LogUtil.warn(getClass().getName(), "Application form appId [" + appId + "] appVersion [" + appVersion + "] not found");
            return;
        }

        WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
        if(assignment == null) {
            LogUtil.warn(getClass().getName(), "Assignment [" + assignmentId + "] not found");
            return;
        }

        PackageActivityForm activityForm = appService.viewAssignmentForm(appDef, assignment, formData, null);
        Form form = activityForm.getForm();
        appService.completeAssignmentForm(form, assignment, formData, workflowVariables);
    }


    protected FormData collectFormData(Map<String, String> fieldData) {
        final FormData formData = new FormData();
        formData.setPrimaryKeyValue(fieldData.get("id"));
        for(Map.Entry<String, String> e : fieldData.entrySet()) {
            formData.addRequestParameterValues(e.getKey(), new String[] {e.getValue()});
        }
        return formData;
    }
}
