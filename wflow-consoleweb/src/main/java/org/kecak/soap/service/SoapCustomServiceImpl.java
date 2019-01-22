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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.soap.model.BankAccountMaster;
import org.kecak.soap.model.ReturnMessage;
import org.kecak.soap.model.SoapException;
import org.kecak.soap.model.VendorMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;
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
                                   @Nonnull String tipeDokumen, @Nonnull String inputBy, @Nonnull String inputDate, @Nonnull String poNumber,
                                   @Nonnull String invoiceNumber, @Nonnull String invoiceDate, @Nonnull String vendorNumber,
                                   @Nonnull String vendorName, @Nonnull String jumlahTagihan, @Nonnull String bankName,
                                   @Nonnull String ppnMasukan, @Nonnull String ppnWapu, @Nonnull String hutangPpnWapu, @Nonnull String uangMuka,
                                   @Nonnull String pph21, @Nonnull String pph22, @Nonnull String pph23,
                                   @Nonnull String jumlahDibayar, @Nonnull Map<String, String> attachment) {
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

            addRequestParameterValue(form, formData, "referensi_dokumen", poNumber);
            addRequestParameterValue(form, formData, "tipe_dokumen", tipeDokumen);
            addRequestParameterValue(form, formData, "input_slip_oleh", inputBy);
            addRequestParameterValue(form, formData, "nomor_po", poNumber);
            addRequestParameterValue(form, formData, "tanggal_input_slip", inputDate);
            addRequestParameterValue(form, formData, "nomor_invoice", invoiceNumber);
            addRequestParameterValue(form, formData, "tanggal_invoice", invoiceDate);
            addRequestParameterValue(form, formData, "nomor_vendor", vendorNumber);
            addRequestParameterValue(form, formData, "vendor", vendorName);
            addRequestParameterValue(form, formData, "jml_tagihan", jumlahTagihan);
            addRequestParameterValue(form, formData, "bank_pembayaran", bankName);
            addRequestParameterValue(form, formData, "ppn_masukan", ppnMasukan);
            addRequestParameterValue(form, formData, "ppn_wapu", ppnWapu);
            addRequestParameterValue(form, formData, "ppn_wapu_hutang", hutangPpnWapu);
            addRequestParameterValue(form, formData, "pph_23", pph23);
            addRequestParameterValue(form, formData, "uang_muka", uangMuka);
            addRequestParameterValue(form, formData, "pph_22", pph22);
            addRequestParameterValue(form, formData, "pph_21", pph21);
            addRequestParameterValue(form, formData, "jml_bayar", jumlahDibayar);
            addRequestParameterValue(form, formData, "ref_amount", jumlahDibayar);

            int i = 0;
            for (Map.Entry<String, String> e : attachment.entrySet()) {
                try {
                    String filePath = FileManager.storeFile(new MockMultipartFile(e.getKey(), e.getKey(), null, hexStringToByteArray(e.getValue())));
                    String uuid = UUID.randomUUID().toString();

                    JSONObject json = new JSONObject();
                    json.put(FormUtil.PROPERTY_ID, uuid);
                    json.put("tipe", "OTHERS");
                    json.put("file", e.getKey());
                    json.put("deskripsi", e.getKey());

                    JSONObject jsonTempFilePathMap = new JSONObject();
                    JSONArray jsonArrayFile = new JSONArray();
                    jsonArrayFile.put(filePath);
                    jsonTempFilePathMap.put("file", jsonArrayFile);
                    json.put(FormUtil.PROPERTY_TEMP_FILE_PATH, jsonTempFilePathMap);

                    addRequestParameterValueGrid(form, formData, "lampiran", i, json);

                    i++;
                } catch (JSONException ex) {
                    LogUtil.error(getClass().getName(), ex,ex.getMessage());
                }
            }

//            addRequestParameterValues(form, formData, "attachment", attachment.entrySet().stream()
//                    .map(e -> FileManager.storeFile(new MockMultipartFile(e.getKey(), e.getKey(), null, hexStringToByteArray(e.getValue()))))
//                    .filter(Objects::nonNull)
//                    .toArray(String[]::new));

            formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[]{"true"});
            formData.addRequestParameterValues(FormUtil.getElementParameterName(form) + "_SUBMITTED", new String[]{""});

            formData.setDoValidation(true);

            Map<String, String> workflowVariables = extractWorkflowVariables(form, formData);

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, workflowVariables, null, null);

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

    /**
     * Extract workflow variable from form
     * @param form
     * @param formData
     * @return
     */
    private Map<String, String> extractWorkflowVariables(@Nonnull Form form, @Nonnull FormData formData) {
        return formData.getRequestParams().entrySet().stream().collect(HashMap::new, (m, e) -> {
            Element element = FormUtil.findElement(e.getKey(), form, formData, true);
            if(element != null) {
                String workflowVariable = element.getPropertyString("workflowVariable");

                if (!Objects.isNull(workflowVariable) && !workflowVariable.isEmpty())
                    m.put(element.getPropertyString("workflowVariable"), String.join(";", e.getValue()));
            }
        }, Map::putAll);
    }

    private void addRequestParameterValue(@Nonnull Form form, @Nonnull FormData formData, @Nonnull String fieldId, @Nonnull String value) {
        addRequestParameterValues(form, formData, fieldId, new String[]{ value });
    }

    private void addRequestParameterValues(@Nonnull Form form, @Nonnull FormData formData, @Nonnull String fieldId, @Nonnull String[] values) {
        Element element = FormUtil.findElement(fieldId, form, formData, true);
        if(element == null) {
            LogUtil.warn(getClass().getName(), "Element ["+fieldId+"] is not found");
            return;
        }

        String parameterName = FormUtil.getElementParameterName(element) ;
        formData.addRequestParameterValues(parameterName, values);
    }

    private void addRequestParameterValueGrid(@Nonnull Form form, @Nonnull FormData formData, @Nonnull String fieldId, int index, @Nonnull JSONObject value) {
        Element element = FormUtil.findElement(fieldId, form, formData, true);
        if(element == null) {
            LogUtil.warn(getClass().getName(), "Element ["+fieldId+"] is not found");
            return;
        }

        String parameterName = FormUtil.getElementParameterName(element) + "_jsonrow_" + index;
        formData.addRequestParameterValues(parameterName, new String[]{value.toString()});
    }
}
