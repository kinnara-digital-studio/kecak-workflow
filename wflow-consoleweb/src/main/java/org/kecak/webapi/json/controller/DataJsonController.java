package org.kecak.webapi.json.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DataJsonController {

    private final static String MESSAGE_VALIDATION_ERROR = "Validation Error";
    private final static String MESSAGE_SUCCESS = "Success";

    @Autowired
    private WorkflowUserManager workflowUserManager;
    @Autowired
    @Qualifier("main")
    private DirectoryManager directoryManager;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private AppService appService;
    @Autowired
    private AppDefinitionDao appDefinitionDao;
    @Autowired
    private DataListService dataListService;
    @Autowired
    private DatalistDefinitionDao datalistDefinitionDao;

    /**
     * Submit form into table, can be used to save master data
     *
     * @param request    HTTP Request, request body contains form field values
     * @param response   HTTP response
     * @param appId      Application ID
     * @param appVersion put 0 for current published app
     * @param formDefId  Form ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/submit", method = RequestMethod.POST)
    public void formSubmit(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") final String appId, @RequestParam("appVersion") final String appVersion, @RequestParam("formDefId") final String formDefId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get version, version 0 indicates published version
            long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            Form form = appService.viewDataForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, null, null, null, null, null, null);

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);

            // submit form
            final FormData result = appService.submitForm(form, formData, false);

            // construct response
            final JSONObject jsonResponse = new JSONObject();
            if (result.getFormErrors() != null && !result.getFormErrors().isEmpty()) {
                // show error message
                result.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonResponse.put(key, value);
                    } catch (JSONException ignored) {
                    }
                });
            } else {
                // set current data as response
                FormRowSet rowSet = appService.loadFormData(form, result.getPrimaryKeyValue());
                if(rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    jsonResponse.put("data", jsonData);
                    jsonResponse.put("message", MESSAGE_SUCCESS);
                    jsonResponse.put("digest", getDigest(jsonData));
                }
            }

            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.PUT)
    public void formUpdate(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") final String appId, @RequestParam("appVersion") final String appVersion, @RequestParam("formDefId") final String formDefId, @RequestParam("primaryKey") final String primaryKey) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get version, version 0 indicates published version
            long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            Form form = appService.viewDataForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, null, null, null, null, null, null);

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);
            formData.setPrimaryKeyValue(primaryKey);

            // submit form
            final FormData result = appService.submitForm(form, formData, false);

            // construct response
            final JSONObject jsonResponse = new JSONObject();
            if (result.getFormErrors() != null && !result.getFormErrors().isEmpty()) {
                final JSONObject jsonError = new JSONObject();
                // show error message
                result.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });
                jsonResponse.put("message", MESSAGE_VALIDATION_ERROR);
                jsonResponse.put("error", jsonError);
            } else {
                // set current data as response
                FormRowSet rowSet = appService.loadFormData(form, result.getPrimaryKeyValue());
                if(rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    jsonResponse.put("data", jsonData);
                    jsonResponse.put("message", MESSAGE_SUCCESS);
                    jsonResponse.put("digest", getDigest(jsonData));
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.GET)
    public void formLoad(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") final String appId, @RequestParam("appVersion") final String appVersion, @RequestParam("formDefId") final String formDefId, @RequestParam("primaryKey") final String primaryKey) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        String digestParameter = request.getParameter("digest");
        try {
            // get version, version 0 indicates published version
            long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            Form form = appService.viewDataForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, null, null, null, null, null, null);
            FormRowSet rowSet = appService.loadFormData(form, primaryKey);
            if(rowSet == null || rowSet.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                // construct response
                JSONObject jsonData = new JSONObject(rowSet.get(0));
                String digest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if(!Objects.equals(digestParameter, digest)) {
                    jsonResponse.put("data", jsonData);
                }

                jsonResponse.put("message", MESSAGE_SUCCESS);
                jsonResponse.put("digest", digest);

                response.getWriter().write(jsonResponse.toString());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Start new process
     *
     * @param request    HTTP Request, request body contains form field values
     * @param response   HTTP Response
     * @param appId      Application ID
     * @param appVersion put 0 for current published app
     * @param processId  Process ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/process/(*:processId)", method = RequestMethod.POST)
    public void processStart(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") String appId, @RequestParam("appVersion") String appVersion, @RequestParam("processId") String processId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            // get version, version 0 indicates published version
            long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // get processDefId
            String processDefId = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId).getId();

            // get process form
            PackageActivityForm packageActivityForm = appService.viewStartProcessForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, null, "");
            if (packageActivityForm == null || packageActivityForm.getForm() == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Start Process [" + processDefId + "] has not been mapped to form");
            }

            Form form = packageActivityForm.getForm();

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);

            JSONObject jsonResponse = new JSONObject();

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, null, null, null);
            if(processResult != null) {
                JSONObject jsonProcess = new JSONObject();
                if (processResult.getProcess() != null) {
                    WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(processResult.getProcess().getInstanceId());
                    if(nextAssignment != null) {
                        jsonProcess.put("processId", nextAssignment.getProcessId());
                        jsonProcess.put("activityId", nextAssignment.getActivityId());
                        jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                        jsonProcess.put("dueDate", nextAssignment.getDueDate());
                        jsonProcess.put("priority", nextAssignment.getPriority());
                    }
                }

                if (processResult.getActivities() != null && !processResult.getActivities().isEmpty()) {
                    jsonProcess.put("activityIds", new JSONArray(processResult.getActivities().stream().map(WorkflowActivity::getId).collect(Collectors.toList())));
                }

                jsonResponse.put("process", jsonProcess);
            }

            if (formData.getFormErrors() != null && !formData.getFormErrors().isEmpty()) {
                jsonResponse.put("message", MESSAGE_VALIDATION_ERROR);

                JSONObject jsonError = new JSONObject();
                // show error message
                formData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });

                jsonResponse.put("error", jsonError);
            } else {
                FormRowSet rowSet = appService.loadFormData(form, formData.getPrimaryKeyValue());
                if(rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);

                    // construct response
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    String digest = getDigest(jsonData);

                    jsonResponse.put("data", jsonData);
                    jsonResponse.put("message", MESSAGE_SUCCESS);
                    jsonResponse.put("digest", digest);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }

    }

    /**
     * Complete assignment form
     *
     * @param request      HTTP Request, request body contains form field values
     * @param response     HTTP Response
     * @param assignmentId Assignment ID
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.POST)
    public void assignmentComplete(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("assignmentId") String assignmentId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
            if (assignment == null) {
                // check if assignment available
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Assignment [" + assignmentId + "] not available");
            }

            AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + assignment.getProcessId() + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // get assignment form
            PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, null, "", "");
            final Form form = packageActivityForm.getForm();

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);

            Map<String, String> workflowVariables = formData.getRequestParams().entrySet().stream().collect(HashMap::new, (m, e) -> {
                Element element = FormUtil.findElement(e.getKey(), form, formData, true);
                String workflowVariable = element.getPropertyString("workflowVariable");

                if(!Objects.isNull(workflowVariable) && !workflowVariable.isEmpty())
                    m.put(element.getPropertyString("workflowVariable"), String.join(";", e.getValue()));
            }, Map::putAll);

            FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, workflowVariables);

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            if (resultFormData.getFormErrors() != null && !resultFormData.getFormErrors().isEmpty()) {
                jsonResponse.put("message", MESSAGE_VALIDATION_ERROR);

                JSONObject jsonError = new JSONObject();
                // show error message
                resultFormData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });

                jsonResponse.put("error", jsonError);

            } else {
                WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(resultFormData.getProcessId());
                if(nextAssignment != null) {
                    JSONObject jsonProcess = new JSONObject();
                    jsonProcess.put("processId", nextAssignment.getProcessId());
                    jsonProcess.put("activityId", nextAssignment.getActivityId());
                    jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                    jsonProcess.put("dueDate", nextAssignment.getDueDate());
                    jsonProcess.put("priority", nextAssignment.getPriority());

                    jsonResponse.put("process", jsonProcess);
                }

                FormRowSet rowSet = appService.loadFormData(form, resultFormData.getPrimaryKeyValue());
                if(rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);

                    // construct response
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    String digest = getDigest(jsonData);

                    jsonResponse.put("data", jsonData);
                    jsonResponse.put("message", MESSAGE_SUCCESS);
                    jsonResponse.put("digest", digest);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.GET)
    public void assignmentView(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("assignmentId") String assignmentId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        String digestParameter = request.getParameter("digest");

        try {
            WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
            if (assignment == null) {
                // check if assignment available
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid assignment [" + assignmentId + "]");
            }

            AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Application not found for assignment [" + assignment.getActivityId() + "] process [" + assignment.getProcessId() + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // retrieve data
            FormData formData = new FormData();
            PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, formData, "");
            FormRowSet rowSet = appService.loadFormData(packageActivityForm.getForm(), formData.getPrimaryKeyValue());

            if(rowSet == null || rowSet.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                JSONObject jsonData = new JSONObject(rowSet.get(0));
                String digest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("digest", digest);
                jsonResponse.put("message", MESSAGE_SUCCESS);

                if(!Objects.equals(digest, digestParameter))
                    jsonResponse.put("data", jsonData);

                response.getWriter().write(jsonResponse.toString());
            }

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/list/(*:dataListId)", method = RequestMethod.GET)
    public void listLoad(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") final String appId, @RequestParam("appVersion") final String appVersion, @RequestParam("dataListId") final String dataListId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        String digestParam = request.getParameter("digest");

        try {
            // get version, version 0 indicates published version
            long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // set current app definition
            AppUtil.setCurrentAppDefinition(appDefinition);

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if(dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // paging
            String pageParameter = request.getParameter("page");
            int page = pageParameter == null || pageParameter.isEmpty() ? 0 : Integer.parseInt(pageParameter);
            int pageSize = page == 0 ? DataList.MAXIMUM_PAGE_SIZE : DataList.DEFAULT_PAGE_SIZE;

            getCollectFilters(request.getParameterMap(), dataList);
            DataListCollection<Map<String, Object>> collections = dataList.getRows(pageSize, page == 0 ? 0 : ((page - 1) * pageSize));

            // apply formatting
            for (Map<String, Object> row : collections) {
                row.entrySet().forEach(e -> e.setValue(format(dataList, row, e.getKey())));
            }

            JSONArray jsonData = new JSONArray();

            for (Map<String, Object> row : collections) {
                try {
                    JSONObject jsonRow = new JSONObject();
                    for (String field : row.keySet()) {
                        jsonRow.put(field, format(dataList, row, field));
                    }
                    jsonData.put(jsonRow);
                } catch (JSONException e) {
                    jsonData.put(new JSONObject(row));
                }
            }

            JSONObject jsonResponse = new JSONObject();
            try {
                String digest = getDigest(jsonData);

                jsonResponse.put("total", jsonData.length());
                if(!Objects.equals(digest, digestParam))
                    jsonResponse.put("data", jsonData);
                jsonResponse.put("digest", digest);
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Convert request body to form data
     *
     * @param request HTTP Request
     * @return form data
     * @throws IOException
     * @throws JSONException
     */
    private FormData extractBodyToFormData(final HttpServletRequest request, final Form form) throws IOException, JSONException {
        final FormData formData = new FormData();

        // read request body and convert request body to json
        JSONObject jsonBody = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
        Iterator i = jsonBody.keys();
        while (i.hasNext()) {
            String key = String.valueOf(i.next());
            String value = jsonBody.getString(key);

            if (key != null && value != null) {

                // convert json to field data
                Element element = FormUtil.findElement(key, form, new FormData());
                if (element != null)
                    formData.addRequestParameterValues(FormUtil.getElementParameterName(element), new String[]{value});
            }
        }

        formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[]{"true"});

        // use field "ID" as primary key if possible
        if (jsonBody.has(FormUtil.PROPERTY_ID)) {
            formData.setPrimaryKeyValue(jsonBody.getString(FormUtil.PROPERTY_ID));
        }

        formData.setDoValidation(true);

        return formData;
    }

    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     * @param json JSON array object
     * @return digest value
     */
    private String getDigest(JSONArray json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     * @param json JSON object
     * @return digest value
     */
    private String getDigest(JSONObject json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    /**
     *
     * @param requestParameters Request parameter
     * @param dataList Input/Output parameter
     */
    private void getCollectFilters(@Nonnull final Map<String, String[]> requestParameters, @Nonnull final DataList dataList) {
        Arrays.stream(dataList.getFilters())
                .peek(f -> {
                    if(!(f.getType() instanceof DataListFilterTypeDefault))
                        LogUtil.warn(getClass().getName(), "DataList filter ["+f.getName()+"] is not instance of ["+DataListFilterTypeDefault.class.getName()+"], filter will be ignored");
                })
                .filter(f -> Objects.nonNull(requestParameters.get(f.getName())) && f.getType() instanceof DataListFilterTypeDefault)
                .forEach(f -> f.getType().setProperty("defaultValue", String.join(";", requestParameters.get(f.getName()))));
    }

    private @Nonnull String format(DataList dataList, Map<String, Object> row, String field) {
        if(dataList.getColumns() == null) {
            return String.valueOf(row.get(field));
        }

        for(DataListColumn column : dataList.getColumns()) {
            if(!field.equals(column.getName())) {
                continue;
            }

            String value = String.valueOf(row.get(field));
            if(column.getFormats() == null) {
                return value;
            }

            for(DataListColumnFormat format : column.getFormats()) {
                if(format != null) {
                    return format.format(dataList, column, row, value).replaceAll("<[^>]*>", "");
                }
            }
        }

        return String.valueOf(row.get(field));
    }
}
