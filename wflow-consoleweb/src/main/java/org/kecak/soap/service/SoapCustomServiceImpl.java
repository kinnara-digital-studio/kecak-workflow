package org.kecak.soap.service;

import org.joget.apps.app.dao.*;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FileUtil;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.service.UserviewService;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.commons.util.FileManager;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.kecak.soap.model.BankAccountMaster;
import org.kecak.soap.model.ReturnMessage;
import org.kecak.soap.model.SoapException;
import org.kecak.soap.model.VendorMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        String processDefId = appService.getWorkflowProcessForApp(appId, String.valueOf(appVersion), processId).getId();
        WorkflowProcessResult result = workflowManager.processStart(processDefId, workflowVariable);

        if(result == null) {
            returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
            returnMessage.setMessage1("Error starting process ["+processDefId+"]");
        } else {
            returnMessage.setStatus(ReturnMessage.MessageStatus.SUCCESS);
            returnMessage.setMessage1(result.getProcess().getInstanceId());
            returnMessage.setMessage2(result.getActivities().stream().map(a -> a.getId()).collect(Collectors.joining(";")));
        }

        return returnMessage;
    }

    @Override
    public ReturnMessage startSlip(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId,
                                   @Nonnull String tipeDokumen, @Nonnull String inputBy, @Nonnull String poNumber,
                                   @Nonnull String invoiceNumber, @Nonnull String invoiceDate, @Nonnull String vendorNumber,
                                   @Nonnull String vendorName, @Nonnull String jumlahTagihan, @Nonnull String bankName,
                                   @Nonnull String ppnMasukan, @Nonnull String ppnWapu, @Nonnull String uangMuka,
                                   @Nonnull String pph21, @Nonnull String pph22, @Nonnull String pph23, @Nonnull String jumlahDibayar,
                                   @Nonnull Map<String, String> attachment) {
//        AppDefinition appDef = appDefinitionDao.loadVersion(appId, appVersion);
//        AppUtil.setCurrentAppDefinition(appDef);
//
//        ReturnMessage returnMessage = new ReturnMessage();
//
//        String processDefId = appService.getWorkflowProcessForApp(appId, String.valueOf(appVersion), processId).getId();
//
//        Map<String, String> workflowVariable = new HashMap<>();
//        workflowVariable.put("status", status);
//        workflowVariable.put("tipeDokumen", tipeDokumen);
//        workflowVariable.put("primaryKey", primaryKey);
//        workflowVariable.put("no_dokumen", nomorDokumen);
//        workflowVariable.put("tgl_dokumen", tanggalDokumen);
//        workflowVariable.put("catatan", catatan);
//        workflowVariable.put("urlFrontApp", urlFrontApp);
//        workflowVariable.put("frontAppProc", frontAppProcessId);
//        workflowVariable.put("refNumber", refNumber);
//        workflowVariable.put("jml_dibayar", jumlahDibayar);
//        workflowVariable.put("no_vendor", nomorVendor);
//        workflowVariable.put("nik", nik);
//
//        WorkflowProcessResult result = workflowManager.processStart(processDefId, workflowVariable);
//
//        if(result == null) {
//            returnMessage.setStatus("E");
//            returnMessage.setMessage1("Error starting process ["+processDefId+"]");
//        } else {
//            returnMessage.setStatus("S");
//            returnMessage.setMessage1(result.getProcess().getInstanceId());
//            returnMessage.setMessage2(result.getActivities().stream().map(a -> a.getId()).collect(Collectors.joining(";")));
//        }
//
//        return returnMessage;


        try {
            // get version, version 0 indicates published version
            long version = appVersion == 0 ? appDefinitionDao.getPublishedVersion(appId) : appVersion;

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new SoapException("Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // get processDefId
            String processDefId = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId).getId();

            // check for permission
            if (!workflowManager.isUserInWhiteList(processDefId)) {
                throw new SoapException("User [" + WorkflowUtil.getCurrentUsername() + "] is not allowed to start process [" + processDefId + "]");
            }

            // get process form
            PackageActivityForm packageActivityForm = appService.viewStartProcessForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, null, "");
            if (packageActivityForm == null || packageActivityForm.getForm() == null) {
                throw new SoapException("Start Process [" + processDefId + "] has not been mapped to form");
            }

            Form form = packageActivityForm.getForm();

            // read request body and convert request body to json
//            final FormData formData = extractBodyToFormData(request, form);

            final FormData formData = new FormData();

            formData.addRequestParameterValues("tipe_dokumen", new String[]{tipeDokumen});

            formData.addRequestParameterValues("input_slip_oleh", new String[] {inputBy});
            formData.addRequestParameterValues("nomor_po", new String[] {poNumber});
//            formData.addRequestParameterValues("tanggal_input_slip", new String[] {""});
            formData.addRequestParameterValues("nomor_invoice", new String[] {invoiceNumber});
            formData.addRequestParameterValues("tanggal_invoice", new String[] {invoiceDate});
            formData.addRequestParameterValues("nomor_vendor", new String[] {vendorNumber});
            formData.addRequestParameterValues("vendor", new String[] {vendorName});
            formData.addRequestParameterValues("jml_tagihan", new String[] {jumlahTagihan});
            formData.addRequestParameterValues("bank_pembayaran", new String[] {bankName});
            formData.addRequestParameterValues("ppn_masukan", new String[] {ppnMasukan});
            formData.addRequestParameterValues("ppn_wapu_hutang", new String[] {ppnWapu});
            formData.addRequestParameterValues("pph_23", new String[] {pph23});
            formData.addRequestParameterValues("uang_muka", new String[] {uangMuka});
            formData.addRequestParameterValues("pph_22", new String[] {pph22});
            formData.addRequestParameterValues("pph_21", new String[] {pph21});
            formData.addRequestParameterValues("jml_bayar", new String[] {jumlahDibayar});

            final Element elementAttachment = FormUtil.findElement("attachment", form, formData);

            for (String elementPropertyValue : FormUtil.getElementPropertyValues(elementAttachment, formData)) {
                LogUtil.info(getClass().getName(), "elementPropertyValue ["+elementPropertyValue+"]");
            }

            formData.addRequestParameterValues(FormUtil.getElementParameterName(elementAttachment), attachment.entrySet().stream()
                    .map(e -> FileManager.storeFile(new MockMultipartFile(e.getKey(), e.getKey(), null, hexStringToByteArray(e.getValue()))))
                    .filter(Objects::nonNull)
                    .toArray(String[]::new));

            formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[]{"true"});
            formData.setDoValidation(true);

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, null, null, null);

            if (formData.getFormErrors() != null && !formData.getFormErrors().isEmpty()) {
                // show error message
                throw new SoapException(formData.getFormErrors().entrySet().stream()
                        .map(e -> "Field ["+e.getKey()+"] Error ["+e.getValue()+"]")
                        .collect(Collectors.joining("; ")));
            } else {
                ReturnMessage returnMessage = new ReturnMessage();

                FormRowSet rowSet = appService.loadFormData(form, formData.getPrimaryKeyValue());
                if(rowSet == null || rowSet.isEmpty()) {
                    returnMessage.setStatus(ReturnMessage.MessageStatus.WARNING);
                    returnMessage.setMessage1(formData.getPrimaryKeyValue());
                    returnMessage.setMessage2("No content");
                } else {
                    returnMessage.setStatus(ReturnMessage.MessageStatus.SUCCESS);
                    returnMessage.setMessage1("New process has been generated");
                    returnMessage.setMessage2("Process ID : " + formData.getPrimaryKeyValue());
                }
                return returnMessage;
            }



        } catch (SoapException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
            returnMessage.setMessage1(e.getMessage());
            return returnMessage;
        }

    }

    @Override
    public ReturnMessage submitVendorMasterData(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull VendorMaster vendorMaster) {
        AppDefinition appDef = appDefinitionDao.loadVersion(appId, appVersion);
        AppUtil.setCurrentAppDefinition(appDef);

        ReturnMessage returnMessage = new ReturnMessage();

        Form vendorMasterForm = loadFormByFormDefId(appId, appVersion, VendorMaster.FORM_DEF_ID);
        if(vendorMasterForm == null) {
            returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
            returnMessage.setMessage1("Form ["+VendorMaster.FORM_DEF_ID+"] does not exist");
            return returnMessage;
        }

        Form bankAccountMasterForm = loadFormByFormDefId(appId, appVersion, BankAccountMaster.FORM_DEF_ID);
        if(bankAccountMasterForm == null) {
            returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
            returnMessage.setMessage1("Form ["+BankAccountMaster.FORM_DEF_ID+"] does not exist");
            return returnMessage;
        }

        returnMessage.setStatus(ReturnMessage.MessageStatus.SUCCESS);

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
                returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
                returnMessage.setMessage1(String.join("; ", errorMessage));
                return returnMessage;
            } else {
                bankAccountIds.add(resultFormData.getPrimaryKeyValue());
            }
        }

        // if no error
        if(ReturnMessage.MessageStatus.SUCCESS == returnMessage.getStatus()) {
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
                returnMessage.setStatus(ReturnMessage.MessageStatus.ERROR);
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
                form = formService.loadFormFromJson(formJson, new FormData());
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
        }
        return form;
    }

    /**
     * Credit to {@see <a href="https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java">link</a>}
     * @param s
     * @return
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
