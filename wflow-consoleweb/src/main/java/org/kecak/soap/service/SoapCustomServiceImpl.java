package org.kecak.soap.service;

import org.enhydra.shark.xpdl.elements.Vendor;
import org.joget.apps.app.dao.*;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.service.UserviewService;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.kecak.soap.model.BankAccountMaster;
import org.kecak.soap.model.ReturnMessage;
import org.kecak.soap.model.VendorMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("soapCustomService")
public class SoapCustomServiceImpl implements SoapCustomService{
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
    @Autowired
    FormDefinitionDao formDefinitionDao;

    @Override
    public ReturnMessage processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nonnull Map<String, String> workflowVariable) {
        AppDefinition appDef = appDefinitionDao.loadVersion(appId, appVersion);
        AppUtil.setCurrentAppDefinition(appDef);

        ReturnMessage returnMessage = new ReturnMessage();

        String processDefId = appId + "#" + (appVersion == 0 ? appDefinitionDao.getPublishedVersion(appId) : appVersion) + "#" + processId;
        WorkflowProcessResult result = workflowManager.processStart(processDefId);

        if(result == null) {
            returnMessage.setStatus("E");
            returnMessage.setMessage1("Error starting process ["+processDefId+"]");
        } else {
            returnMessage.setStatus("S");
            returnMessage.setMessage1(result.getProcess().getInstanceId());
            returnMessage.setMessage2(result.getActivities().stream().map(a -> a.getId()).collect(Collectors.joining(";")));
        }

        return returnMessage;
    }

    @Override
    public ReturnMessage submitVendorMasterData(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull VendorMaster vendorMaster) {
        AppDefinition appDef = appDefinitionDao.loadVersion(appId, appVersion);
        AppUtil.setCurrentAppDefinition(appDef);

        ReturnMessage returnMessage = new ReturnMessage();

        Form vendorMasterForm = loadFormByFormDefId(appId, appVersion, VendorMaster.FORM_DEF_ID);
        if(vendorMasterForm == null) {
            returnMessage.setStatus("E");
            returnMessage.setMessage1("Form ["+VendorMaster.FORM_DEF_ID+"] does not exist");
            return returnMessage;
        }

        Form bankAccountMasterForm = loadFormByFormDefId(appId, appVersion, BankAccountMaster.FORM_DEF_ID);
        if(bankAccountMasterForm == null) {
            returnMessage.setStatus("E");
            returnMessage.setMessage1("Form ["+BankAccountMaster.FORM_DEF_ID+"] does not exist");
            return returnMessage;
        }

        returnMessage.setStatus("S");

        // Save Bank Account
        @Nonnull final List<String> bankAccountIds = new ArrayList<>();
        for(BankAccountMaster bankAccountMaster : vendorMaster.getBankAccountList()) {
            String id = bankAccountMaster.getId();
            final FormData formData = new FormData();
            formData.setPrimaryKeyValue(id);
            formData.addRequestParameterValues(FormUtil.PROPERTY_ID, new String[] {id});
            final FormData resultFormData = formService.submitForm(bankAccountMasterForm, formData, false);

            if(resultFormData.getFormErrors() != null && !resultFormData.getFormErrors().isEmpty()) {
                // overwrite Success status with error
                String errorMessage = String.join("; ", resultFormData.getFormErrors().values());
                LogUtil.warn(getClass().getName(), "Error submitting form [" + BankAccountMaster.FORM_DEF_ID + "] messages [" + errorMessage + "]");
                returnMessage.setStatus("E");
                returnMessage.setMessage1(String.join("; ", errorMessage));
                return returnMessage;
            } else {
                bankAccountIds.add(resultFormData.getPrimaryKeyValue());
            }
        }

        // if no error
        if("S".equalsIgnoreCase(returnMessage.getStatus())) {
            // Save Vendor
            final FormData vendorMasterFormData = new FormData();
            vendorMasterFormData.setPrimaryKeyValue(vendorMaster.getId());
            vendorMasterFormData.addRequestParameterValues(FormUtil.PROPERTY_ID, new String[]{vendorMaster.getId()});
            vendorMasterFormData.addRequestParameterValues(VendorMaster.FIELD_NAME, new String[]{vendorMaster.getName()});
            vendorMasterFormData.addRequestParameterValues(VendorMaster.FIELD_BANK_ACCOUNT, new String[]{String.join(";", bankAccountIds)});
            FormData vendorMasterFormDataResult = formService.submitForm(vendorMasterForm, vendorMasterFormData, false);
            if (vendorMasterFormDataResult.getFormErrors() != null && !vendorMasterFormDataResult.getFormErrors().isEmpty()) {
                // overwrite Success status with error
                String errorMessage = String.join("; ", vendorMasterFormDataResult.getFormErrors().values());
                LogUtil.warn(getClass().getName(), "Error submitting form [" + VendorMaster.FORM_DEF_ID + "] messages [" + errorMessage + "]");
                returnMessage.setStatus("E");
                returnMessage.setMessage1(String.join("; ", errorMessage));
            }
        }

        return returnMessage;
    }

    protected Form loadFormByFormDefId(String appId, Long version, String formDefId) {
        Form form = null;
        try {
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            FormDefinition formDef = formDefinitionDao.loadById(formDefId, appDef);

            if (formDef != null && formDef.getJson() != null) {
                String formJson = formDef.getJson();
                form = (Form) formService.loadFormFromJson(formJson, new FormData());
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }
        return form;
    }
}
