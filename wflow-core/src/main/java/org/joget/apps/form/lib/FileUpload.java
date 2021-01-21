package org.joget.apps.form.lib;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.UserviewPermission;
import org.joget.commons.util.FileLimitException;
import org.joget.commons.util.FileManager;
import org.joget.commons.util.FileStore;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileUpload extends Element implements FormBuilderPaletteElement, FileDownloadSecurity, AceFormElement, AdminLteFormElement {

    private final static Pattern DATA_PATTERN = Pattern.compile("data:(?<mime>[\\w/\\-\\.]+);(?<properties>(\\w+=[^;]+;)*)base64,(?<data>.*)");

    public String getName() {
        return "File Upload";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "FileUpload Element";
    }

    @SuppressWarnings("unchecked")
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "fileUpload.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {

        // set value
        String[] values = getElementValues(formData);

        //check is there a stored value
        String storedValue = formData.getStoreBinderDataProperty(this);
        if (storedValue != null) {
            values = storedValue.split(";");
        }


        Map<String, String> tempFilePaths = new HashMap<String, String>();
        Map<String, String> filePaths = new HashMap<String, String>();

        String primaryKeyValue = getPrimaryKeyValue(formData);
        String formDefId = "";
        Form form = FormUtil.findRootForm(this);
        if (form != null) {
            formDefId = form.getPropertyString(FormUtil.PROPERTY_ID);
        }
        String appId = "";
        String appVersion = "";

        AppDefinition appDef = AppUtil.getCurrentAppDefinition();

        if (appDef != null) {
            appId = appDef.getId();
            appVersion = appDef.getVersion().toString();
        }

        for (String value : values) {
            // check if the file is in temp file
            File file = FileManager.getFileByPath(value);

            if (file != null) {
                tempFilePaths.put(value, file.getName());
            } else if (value != null && !value.isEmpty()) {
                // determine actual path for the file uploads
                String fileName = value;
                String encodedFileName = fileName;
                if (fileName != null) {
                    try {
                        encodedFileName = URLEncoder.encode(fileName, "UTF8").replaceAll("\\+", "%20");
                    } catch (UnsupportedEncodingException ex) {
                        // ignore
                    }
                }

                String filePath = "/web/client/app/" + appId + "/" + appVersion + "/form/download/" + formDefId + "/" + primaryKeyValue + "/" + encodedFileName + ".";
                if (Boolean.valueOf(getPropertyString("attachment")).booleanValue()) {
                    filePath += "?attachment=true";
                }
                filePaths.put(filePath, value);
            }
        }

        if (!tempFilePaths.isEmpty()) {
            dataModel.put("tempFilePaths", tempFilePaths);
        }
        if (!filePaths.isEmpty()) {
            dataModel.put("filePaths", filePaths);
        }

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    @Override
    public FormData formatDataForValidation(FormData formData) {
        // check for file removal
        String postfix = "_remove";
        String filePathPostfix = "_path";
        String id = FormUtil.getElementParameterName(this);
        if (id != null) {
            String[] tempFilenames = formData.getRequestParameterValues(id);
            String[] tempExisting = formData.getRequestParameterValues(id + filePathPostfix);

            if ((tempFilenames != null && tempFilenames.length > 0) || (tempExisting != null && tempExisting.length > 0)) {
                List<String> filenames = new ArrayList<String>();
                if (tempFilenames != null && tempFilenames.length > 0) {
                    filenames.addAll(Arrays.asList(tempFilenames));
                }
                List<String> removalFlag = new ArrayList<String>();
                String[] tempRemove = formData.getRequestParameterValues(id + postfix);
                if (tempRemove != null && tempRemove.length > 0) {
                    removalFlag.addAll(Arrays.asList(tempRemove));
                }

                List<String> existingFilePath = new ArrayList<String>();
                if (tempExisting != null && tempExisting.length > 0) {
                    existingFilePath.addAll(Arrays.asList(tempExisting));
                }

                for (String filename : existingFilePath) {
                    if (!removalFlag.contains(filename)) {
                        filenames.add(filename);
                    }
                }

                if (filenames.isEmpty()) {
                    formData.addRequestParameterValues(id, new String[]{""});
                } else if (!"true".equals(getPropertyString("multiple"))) {
                    formData.addRequestParameterValues(id, new String[]{filenames.get(0)});
                } else {
                    formData.addRequestParameterValues(id, filenames.toArray(new String[]{}));
                }
            }
        }
        return formData;
    }

    @Override
    public FormRowSet formatData(FormData formData) {
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String[] values = getElementValues(formData);
            if (values != null && values.length > 0) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                List<String> resultedValue = new ArrayList<String>();
                List<String> filePaths = new ArrayList<String>();

                for (String value : values) {
                    // check if the file is in temp file
                    File file = FileManager.getFileByPath(value);
                    if (file != null) {
                        filePaths.add(value);
                        resultedValue.add(file.getName());

                    } else {
                        resultedValue.add(value);
                    }
                }

                if (!filePaths.isEmpty()) {
                    result.putTempFilePath(id, filePaths.toArray(new String[]{}));
                }

                // formulate values
                String delimitedValue = FormUtil.generateElementPropertyValues(resultedValue.toArray(new String[]{}));
                String paramName = FormUtil.getElementParameterName(this);
                formData.addRequestParameterValues(paramName, resultedValue.toArray(new String[]{}));

                // set value into Properties and FormRowSet object
                result.setProperty(id, delimitedValue);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
    }

    @Override
    public String getElementValue(FormData formData) {
        if (isReadOnlyLabel() || asAttachment(formData)) {
            return getFileDownloadLink(formData);
        } else {
            return super.getElementValue(formData);
        }
    }

    private String getFileDownloadLink(FormData formData) {
        AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
        // set value
        String[] values = getElementValues(formData);
        return Arrays.stream(values)
                .filter(Objects::nonNull)
                .map(fileName -> {
                    // determine actual path for the file uploads
                    String appId = appDefinition.getAppId();
                    long appVersion = appDefinition.getVersion();
                    String formDefId = Optional.ofNullable(FormUtil.findRootForm(this))
                            .map(new Function<Form, String>() {
                                @Override
                                public String apply(Form f) {
                                    return f.getPropertyString(FormUtil.PROPERTY_ID);
                                }
                            })
                            .orElse("");
                    String encodedFileName = fileName;
                    String primaryKeyValue = formData.getPrimaryKeyValue();
                    try {
                        encodedFileName = URLEncoder.encode(fileName, "UTF8").replaceAll("\\+", "%20");
                    } catch (UnsupportedEncodingException ex) {
                        // ignore
                    }

                    String filePath = "/web/client/app/" + appId + "/" + appVersion + "/form/download/" + formDefId + "/" + primaryKeyValue + "/" + encodedFileName + ".";
                    if (asAttachment(formData)) {
                        filePath += "?attachment=true";
                    }

                    return filePath;
                })
                .collect(Collectors.joining(";"));
    }

    private boolean isReadOnlyLabel() {
        return "true".equalsIgnoreCase(getPropertyString(FormUtil.PROPERTY_READONLY))
                && "true".equalsIgnoreCase(getPropertyString(FormUtil.PROPERTY_READONLY_LABEL));
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>FileUpload</label><input type='file' />";
    }

    public String getLabel() {
        return "File Upload";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/fileUpload.json", null, true, "message/form/FileUpload");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    public int getFormBuilderPosition() {
        return 900;
    }

    public String getFormBuilderIcon() {
        return null;
    }

    @Override
    public Boolean selfValidate(FormData formData) {
        String id = FormUtil.getElementParameterName(this);
        Boolean valid = true;
        String error = "";
        try {
            String value = FormUtil.getElementPropertyValue(this, formData);

            File file = FileManager.getFileByPath(value);
            if (file != null) {
                if (getPropertyString("maxSize") != null && !getPropertyString("maxSize").isEmpty()) {
                    long maxSize = Long.parseLong(getPropertyString("maxSize")) * 1024;

                    if (file.length() > maxSize) {
                        valid = false;
                        error += getPropertyString("maxSizeMsg") + " ";

                    }
                }
                if (getPropertyString("fileType") != null && !getPropertyString("fileType").isEmpty()) {
                    String[] fileType = getPropertyString("fileType").split(";");
                    String filename = file.getName().toUpperCase();
                    Boolean found = false;
                    for (String type : fileType) {
                        if (filename.endsWith(type.toUpperCase())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        valid = false;
                        error += getPropertyString("fileTypeMsg");
                    }
                }
            }

            if (!valid) {
                formData.addFormError(id, error);
            }
        } catch (Exception e) {
        }

        return valid;
    }

    public boolean isDownloadAllowed(@SuppressWarnings("rawtypes") Map requestParameters) {
        String permissionType = getPropertyString("permissionType");
        if (permissionType.equals("public")) {
            return true;
        } else if (permissionType.equals("custom")) {
            Object permissionElement = getProperty("permissionPlugin");
            if (permissionElement != null && permissionElement instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map elementMap = (Map) permissionElement;
                String className = (String) elementMap.get("className");
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) elementMap.get("properties");

                //convert it to plugin
                PluginManager pm = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                UserviewPermission plugin = (UserviewPermission) pm.getPlugin(className);
                if (plugin != null && plugin instanceof FormPermission) {
                    WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
                    ExtDirectoryManager dm = (ExtDirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
                    String username = workflowUserManager.getCurrentUsername();
                    User user = dm.getUserByUsername(username);

                    plugin.setProperties(properties);
                    plugin.setCurrentUser(user);
                    plugin.setRequestParameters(requestParameters);

                    return plugin.isAuthorize();
                }
            }
            return false;
        } else {
            return !WorkflowUtil.isCurrentUserAnonymous();
        }
    }

    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceFileUpload.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel) {
        String template = "AdminLteTheme/AdminLteFileUpload.ftl";
        return renderTemplate(template, formData, dataModel);
    }

    private boolean asAttachment(FormData formData) {
        return Boolean.parseBoolean(getPropertyString("attachment"))
                || "true".equalsIgnoreCase(formData.getRequestParameter(PARAMETER_AS_LINK));
    }

    @Override
    public String[] handleMultipartDataRequest(Map<String, String[]> requestParameterData, Element element, FormData formData) {
        final String elementId = element.getPropertyString("id");

        List<String> filePathList = new ArrayList<>();

        try {
            MultipartFile[] fileStore = FileStore.getFiles(elementId);
            if(fileStore != null) {
                for (MultipartFile file : fileStore) {
                    final String filePath = FileManager.storeFile(file);
                    filePathList.add(filePath);
                }
            }
        } catch (FileLimitException e) {
            LogUtil.error(getClassName(), e, e.getMessage());
        }

        if(filePathList.isEmpty()) {
            return new String[0];
        } else {
            return filePathList.toArray(new String[0]);
        }
    }

    @Override
    public String[] handleJsonDataRequest(String requestBodyPayload, Element element, FormData formData) {
        try {
            String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);

            JSONObject jsonBody = new JSONObject(requestBodyPayload);

            String value = jsonBody.optString(elementId);
            if(value == null)
                return new String[0];

            JSONArray jsonValue;
            try {
                jsonValue = new JSONArray(value);
            } catch (JSONException e) {
                jsonValue = new JSONArray();
                jsonValue.put(value);
            }

            List<String> result = new ArrayList<>();
            for(int i = 0, size = jsonValue.length(); i < size; i++) {
                try {
                    String uri = jsonValue.getString(i);
                    Matcher m = DATA_PATTERN.matcher(uri);
                    if(m.find()) {
                        String contentType = m.group("mime");
                        String extension = contentType.split("/")[1];
                        String fileName = getFileName(m.group("properties"), extension);
                        String base64 = m.group("data");

                        MultipartFile multipartFile = decodeFile(fileName, contentType, base64.trim());
                        result.add(FileManager.storeFile(multipartFile));
                    }
                } catch (JSONException e) {
                    LogUtil.error(getClassName(), e, e.getMessage());
                }
            }

            return result.toArray(new String[0]);
        } catch (JSONException e) {
            LogUtil.error(getClassName(), e, e.getMessage());
            return new String[0];
        }
    }

    /**
     * Decode base64 to file
     *
     * @param uri
     * @return
     */
    @Nullable
    protected MultipartFile decodeFile(@Nonnull String uri) throws IllegalArgumentException {
        Matcher m = DATA_PATTERN.matcher(uri);

        if(m.find()) {
            String contentType = m.group("mime");
            String extension = contentType.split("/")[1];
            String fileName = getFileName(m.group("properties"), extension);
            String base64 = m.group("data");

            return decodeFile(fileName, contentType, base64.trim());
        } else {
            return null;
        }
    }

    /**
     *
     * @param properties
     * @param extension
     * @return
     */
    protected String getFileName(String properties, String extension) {
        for(String prop : properties.split(";")) {
            String[] keyValue = prop.split("=", 2);
            if(keyValue.length > 1 && keyValue[0].equalsIgnoreCase("filename")) {
                return keyValue[1];
            }
        }

        return "file." + extension;
    }

    /**
     * Decode base64 to file
     *
     * @param filename
     * @param contentType
     * @param base64EncodedFile
     * @return
     */
    @Nullable
    protected MultipartFile decodeFile(@Nonnull String filename, String contentType, @Nonnull String base64EncodedFile) throws IllegalArgumentException {
        if (base64EncodedFile.isEmpty())
            return null;

        byte[] data = Base64.getDecoder().decode(base64EncodedFile);
        return new MockMultipartFile(filename, filename, contentType, data);
    }
}
