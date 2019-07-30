package org.kecak.webapi.json.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.model.PackageDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.AuthTokenService;
import org.joget.apps.datalist.model.*;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.workflow.lib.AssignmentCompleteButton;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.dao.WorkflowProcessLinkDao;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.stream.Stream;

@Controller
public class DataJsonController {
    private final static String FIELD_MESSAGE = "message";
    private final static String FIELD_DATA = "data";
    private final static String FIELD_VALIDATION_ERROR = "validation_error";
    private final static String FIELD_DIGEST = "digest";
    private final static String FIELD_TOTAL = "total";

    private final static String MESSAGE_VALIDATION_ERROR = "Validation Error";
    private final static String MESSAGE_SUCCESS = "Success";

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
    @Autowired
    private FormService formService;
    @Autowired
    private WorkflowProcessLinkDao workflowProcessLinkDao;
    @Autowired
    AuthTokenService authTokenService;

    /**
     * Submit form into table, can be used to save master data
     *
     * @param request       HTTP Request, request body contains form field values
     * @param response      HTTP response
     * @param appId         Application ID
     * @param appVersion    put 0 for current published app
     * @param formDefId     Form ID
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/submit", method = RequestMethod.POST)
    public void postFormSubmit(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("appId") final String appId,
                               @RequestParam("appVersion") final String appVersion,
                               @RequestParam("formDefId") final String formDefId)
            throws IOException, JSONException {

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
                final JSONObject jsonError = new JSONObject();

                // show error message
                result.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });

                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
            } else {
                // set current data as response
                FormRowSet rowSet = appService.loadFormData(form, result.getPrimaryKeyValue());
                if (rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    jsonResponse.put(FIELD_DATA, jsonData);
                    jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                    jsonResponse.put(FIELD_DIGEST, getDigest(jsonData));
                }
            }

            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Update data in Form
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param appVersion    Application version
     * @param formDefId     Form Definition ID
     * @param primaryKey    Primary Key
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.PUT)
    public void putFormData(final HttpServletRequest request, final HttpServletResponse response,
                            @RequestParam("appId") final String appId,
                            @RequestParam("appVersion") final String appVersion,
                            @RequestParam("formDefId") final String formDefId,
                            @RequestParam("primaryKey") final String primaryKey)
            throws IOException, JSONException {

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
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
            } else {
                // set current data as response
                FormRowSet rowSet = appService.loadFormData(form, result.getPrimaryKeyValue());
                if (rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    jsonResponse.put(FIELD_DATA, jsonData);
                    jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                    jsonResponse.put(FIELD_DIGEST, getDigest(jsonData));
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get Form Data
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param appVersion    Application version
     * @param formDefId     Form Definition ID
     * @param primaryKey    Primary Key
     * @param digest        Digest
     * @throws IOException
     * @throws JSONException
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/(*:primaryKey)", method = RequestMethod.GET)
    public void getFormData(final HttpServletRequest request, final HttpServletResponse response,
                            @RequestParam("appId") final String appId,
                            @RequestParam("appVersion") final String appVersion,
                            @RequestParam("formDefId") final String formDefId,
                            @RequestParam("primaryKey") final String primaryKey,
                            @RequestParam(value = "digest", required = false) final String digest)
            throws IOException, JSONException {

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
            FormRowSet rowSet = appService.loadFormData(form, primaryKey);
            if (rowSet == null || rowSet.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                // construct response
                JSONObject jsonData = new JSONObject(rowSet.get(0));
                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                if (!Objects.equals(currentDigest, digest)) {
                    jsonResponse.put(FIELD_DATA, jsonData);
                }

                jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.getWriter().write(jsonResponse.toString());
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get List Count
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param appVersion    Application version
     * @param dataListId    DataList ID
     * @throws IOException
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/list/(*:dataListId)/count", method = RequestMethod.GET)
    public void getListCount(final HttpServletRequest request, final HttpServletResponse response,
                             @RequestParam("appId") final String appId,
                             @RequestParam("appVersion") final String appVersion,
                             @RequestParam("dataListId") final String dataListId)
            throws IOException {

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

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            getCollectFilters(request.getParameterMap(), dataList);

            int total = dataList.getSize();

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Retrieve dataList data
     *
     * @param request     HTTP Request
     * @param response    HTTP Response
     * @param appId       Application ID
     * @param appVersion  Application version
     * @param dataListId  DataList ID
     * @param page        paging every 10 rows, page = 0 will show all data without paging
     * @param start       from row index (index starts from 0)
     * @param sort        order list by specified field name
     * @param desc        optional true/false
     * @param digest      hash calculation of data json
     * @throws IOException
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/list/(*:dataListId)", method = RequestMethod.GET)
    public void getList(final HttpServletRequest request, final HttpServletResponse response,
                        @RequestParam("appId") final String appId,
                        @RequestParam("appVersion") final String appVersion,
                        @RequestParam("dataListId") final String dataListId,
                        @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                        @RequestParam(value = "start", required = false) final Integer start,
                        @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                        @RequestParam(value = "sort", required = false) final String sort,
                        @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                        @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

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

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // configure sorting
            if (sort != null) {
                dataList.setDefaultSortColumn(sort);

                // order ASC / DESC
                dataList.setDefaultOrder(desc ? DataList.ORDER_DESCENDING_VALUE : DataList.ORDER_ASCENDING_VALUE);
            }

            // paging
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                JSONArray jsonData = Optional.ofNullable((DataListCollection<Map<String, Object>>)dataList.getRows(pageSize, rowStart))
                        .orElse(new DataListCollection<>())
                        .stream()

                        // reformat content value
                        .peek(row -> row.entrySet().forEach(e -> e.setValue(format(dataList, row, e.getKey()))))

                        // collect as JSON
                        .collect(JSONArray::new, JSONArray::put, (a1, a2) -> {
                            for(int i = 0, size = a2.length(); i < size; i++) {
                                try {
                                    a1.put(a2.get(i));
                                } catch (JSONException ignored) { }
                            }
                        });

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
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
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/process/(*:processId)", method = RequestMethod.POST)
    public void postProcessStart(final HttpServletRequest request, final HttpServletResponse response,
                                 @RequestParam("appId") String appId,
                                 @RequestParam("appVersion") String appVersion,
                                 @RequestParam("processId") String processId)
            throws IOException, JSONException {

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

            // check for permission
            if (!workflowManager.isUserInWhiteList(processDefId)) {
                throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User [" + WorkflowUtil.getCurrentUsername() + "] is not allowed to start process [" + processDefId + "]");
            }

            // get process form
            PackageActivityForm packageActivityForm = appService.viewStartProcessForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, null, "");
            if (packageActivityForm == null || packageActivityForm.getForm() == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Start Process [" + processDefId + "] has not been mapped to form");
            }

            Form form = packageActivityForm.getForm();

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);

            JSONObject jsonResponse = new JSONObject();

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, workflowVariables, null, null);

            if (formData.getFormErrors() != null && !formData.getFormErrors().isEmpty()) {
                JSONObject jsonError = new JSONObject();
                // show error message
                formData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });

                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);
            } else {
                FormRowSet rowSet = appService.loadFormData(form, formData.getPrimaryKeyValue());
                if (rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);

                    // construct response
                    FormRow row = rowSet.get(0);
                    JSONObject jsonData = new JSONObject(row);

                    if (processResult != null) {
                        JSONObject jsonProcess = new JSONObject();
                        if (processResult.getProcess() != null) {
                            WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(processResult.getProcess().getInstanceId());
                            if (nextAssignment != null) {
                                jsonProcess.put("processId", nextAssignment.getProcessId());
                                jsonProcess.put("activityId", nextAssignment.getActivityId());
                                jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                                jsonProcess.put("dueDate", nextAssignment.getDueDate());
                                jsonProcess.put("priority", nextAssignment.getPriority());

                                jsonData.put("processId", nextAssignment.getProcessId());
                                jsonData.put("activityId", nextAssignment.getActivityId());
                            }
                        }

                        if (processResult.getActivities() != null && !processResult.getActivities().isEmpty()) {
                            jsonProcess.put("activityIds", new JSONArray(processResult.getActivities().stream().map(WorkflowActivity::getId).collect(Collectors.toList())));
                        }

                        jsonResponse.put("process", jsonProcess);
                    }

                    String digest = getDigest(jsonData);
                    jsonResponse.put(FIELD_DATA, jsonData);
                    jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                    jsonResponse.put(FIELD_DIGEST, digest);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Post Assignment Complete
     *
     * Complete assignment form
     *
     * @param request           HTTP Request, request body contains form field values
     * @param response          HTTP Response
     * @param assignmentId      Assignment ID
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.POST)
    public void postAssignmentComplete(final HttpServletRequest request, final HttpServletResponse response,
                                       @RequestParam("assignmentId") String assignmentId)
            throws IOException, JSONException {

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
            PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, null, "");
            final Form form = packageActivityForm.getForm();

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request, form);

            Map<String, String> workflowVariables = generateWorkflowVariable(form, formData);

            FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, workflowVariables);

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            if (resultFormData.getFormErrors() != null && !resultFormData.getFormErrors().isEmpty()) {
                JSONObject jsonError = new JSONObject();
                // show error message
                resultFormData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonError.put(key, value);
                    } catch (JSONException ignored) { }
                });

                jsonResponse.put(FIELD_VALIDATION_ERROR, jsonError);
                jsonResponse.put(FIELD_MESSAGE, MESSAGE_VALIDATION_ERROR);

            } else {
                WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(resultFormData.getProcessId());
                if (nextAssignment != null) {
                    JSONObject jsonProcess = new JSONObject();
                    jsonProcess.put("processId", nextAssignment.getProcessId());
                    jsonProcess.put("activityId", nextAssignment.getActivityId());
                    jsonProcess.put("dateCreated", nextAssignment.getDateCreated());
                    jsonProcess.put("dueDate", nextAssignment.getDueDate());
                    jsonProcess.put("priority", nextAssignment.getPriority());

                    jsonResponse.put("process", jsonProcess);
                }

                FormRowSet rowSet = appService.loadFormData(form, resultFormData.getPrimaryKeyValue());
                if (rowSet == null || rowSet.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);

                    // construct response
                    FormRow row = rowSet.get(0);

                    JSONObject jsonData = new JSONObject(row);

                    jsonData.put("activityId", nextAssignment.getActivityId());
                    jsonData.put("processId", nextAssignment.getProcessId());

                    String digest = getDigest(jsonData);

                    jsonResponse.put(FIELD_DATA, jsonData);
                    jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);
                    jsonResponse.put(FIELD_DIGEST, digest);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get Assignment

     * Get assignment data
     *
     * @param request           HTTP Request
     * @param response          HTTP Response
     * @param assignmentId      Assingment ID
     * @param digest            Digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)", method = RequestMethod.GET)
    public void getAssignment(final HttpServletRequest request, final HttpServletResponse response,
                              @RequestParam("assignmentId") final String assignmentId,
                              @RequestParam(value = "digest", required = false) String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

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
            Map<String, String> parameterMap = new HashMap<>();
            parameterMap.put("activityId", assignmentId);
            FormData formData = formService.retrieveFormDataFromRequestMap(new FormData(), parameterMap);
            PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, formData, "");
            FormRowSet rowSet = appService.loadFormData(packageActivityForm.getForm(), formData.getPrimaryKeyValue());

            if (rowSet == null || rowSet.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);

                try {
                    JSONObject jsonData = new JSONObject(rowSet.get(0));
                    jsonData.put("activityId", assignment.getActivityId());
                    jsonData.put("processId", assignment.getProcessId());

                    String currentDigest = getDigest(jsonData);

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put(FIELD_DIGEST, currentDigest);
                    jsonResponse.put(FIELD_MESSAGE, MESSAGE_SUCCESS);

                    if (!Objects.equals(digest, currentDigest))
                        jsonResponse.put(FIELD_DATA, jsonData);

                    response.getWriter().write(jsonResponse.toString());
                } catch (JSONException e) {
                    throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Get Assignment Count
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param version       Application version
     * @param processId     Process Definition ID
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignments/count", method = RequestMethod.GET)
    public void getAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                    @RequestParam(value = "appId", required = false) final String appId,
                                    @RequestParam(value = "version", required = false) final Long version,
                                    @RequestParam(value = "processId", required = false) final String processId) throws IOException {
        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            String processDefId = validateAndDetermineProcessDefId(appId, version, processId);
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);

            try {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put(FIELD_TOTAL, total);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Get Assignment List
     *
     * @param request       HTTP Request
     * @param response      HTTP Response
     * @param appId         Application ID
     * @param version       Application version
     * @param processId     Process Def ID
     * @param page          Page starts from 1
     * @param start         From index (index starts from 0)
     * @param rows          Page size (rows = 0 means load all data)
     * @param sort          Sort by field
     * @param desc          Descending (true/false)
     * @param digest        Digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/data/assignments", method = RequestMethod.GET)
    public void getAssignments(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam(value = "appId", required = false) final String appId,
                               @RequestParam(value = "version", required = false) final Long version,
                               @RequestParam(value = "processId", required = false) final String processId,
                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                               @RequestParam(value = "start", required = false) final Integer start,
                               @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                               @RequestParam(value = "sort", required = false) final String sort,
                               @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                               @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");

        try {
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            String processDefId = validateAndDetermineProcessDefId(appId, version, processId);

            // get total data
            int total = workflowManager.getAssignmentSize(appId, processDefId, null);

            FormRowSet resultRowSet = workflowManager.getAssignmentPendingAndAcceptedList(appId, processDefId, null, sort, desc, rowStart, pageSize).stream()
                    .map(WorkflowAssignment::getActivityId)
                    .map(workflowManager::getAssignment)
                    .map(assignment -> {
                        final AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
                        if (appDefinition == null) {
                            LogUtil.warn(getClass().getName(), "Application not found for assignment [" + assignment.getActivityId() + "] process [" + assignment.getProcessId() + "]");
                            FormRow emptyRow = new FormRow();
                            emptyRow.setProperty("processId", assignment.getProcessId());
                            emptyRow.setProperty("activityId", assignment.getActivityId());
                            emptyRow.setProperty("assigneeId", assignment.getAssigneeId());
                            return emptyRow;
                        }

                        final FormData formData = new FormData();
                        final PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, formData, "");
                        Form form = packageActivityForm.getForm();
                        if ("assignmentForm".equals(form.getPropertyString("id"))) {
                            // form not found, get workflow variable as form set
                            LogUtil.info(getClass().getName(), "No form is attached to assignment [" + assignment.getActivityId() + "]");
                            FormRow row = workflowManager.getActivityVariableList(assignment.getActivityId()).stream()
                                    .peek(workflowVariable -> LogUtil.info(getClass().getName(), "workflow variables [" + workflowVariable.getId() + "] [" + workflowVariable.getVal() + "]"))
                                    .filter(workflowVariable -> workflowVariable.getVal() != null)
                                    .collect(FormRow::new, (formRow, workflowVariable) -> {
                                        formRow.setProperty(workflowVariable.getId(), workflowVariable.getVal().toString());
                                    }, FormRow::putAll);

                            row.setProperty(FormUtil.PROPERTY_ID, formData.getPrimaryKeyValue());
                            row.setProperty("activityId", assignment.getActivityId());
                            row.setProperty("processId", assignment.getProcessId());
                            row.setProperty("assigneeId", assignment.getAssigneeId());

                            return row;
                        } else {
                            FormRowSet rowSet = appService.loadFormData(form, formData.getPrimaryKeyValue());
                            if (rowSet == null || rowSet.isEmpty())
                                return null;

                            FormRow row = rowSet.get(0);
                            row.setProperty("activityId", assignment.getActivityId());
                            row.setProperty("processId", assignment.getProcessId());
                            row.setProperty("assigneeId", assignment.getAssigneeId());

                            return row;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(FormRowSet::new, FormRowSet::add, FormRowSet::addAll);

            JSONArray jsonData = new JSONArray();
            for (FormRow row : resultRowSet) {
                try {
                    JSONObject jsonRow = new JSONObject();
                    for (Object key : row.keySet()) {
                        jsonRow.put(key.toString(), row.get(key));
                    }
                    jsonData.put(jsonRow);
                } catch (JSONException e) {
                    jsonData.put(new JSONObject(row));
                }
            }

            try {
                JSONObject jsonResponse = new JSONObject();
                String currentDigest = getDigest(jsonData);

                jsonResponse.put(FIELD_TOTAL, total);

                if (!Objects.equals(currentDigest, digest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
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

        formData.setDoValidation(true);
        formData.addRequestParameterValues(FormUtil.getElementParameterName(form) + "_SUBMITTED", new String[]{""});
        formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[]{"true"});

        // use field "ID" as primary key if possible
        if (jsonBody.has(FormUtil.PROPERTY_ID)) {
            formData.setPrimaryKeyValue(jsonBody.getString(FormUtil.PROPERTY_ID));
        }

        formData.setDoValidation(true);

        return formData;
    }


    /**
     * Get DataList Assignments
     * 
     * Same functionality as DataList Inbox plugin
     * 
     * @param request
     * @param response
     * @param appId
     * @param appVersion
     * @param dataListId
     * @param processId
     * @param activityDefIds
     * @param page
     * @param start
     * @param rows
     * @param sort
     * @param desc
     * @param digest
     * @throws IOException
     */
    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/assignments/list/(*:dataListId)", method = RequestMethod.GET)
    public void getDataListAssignments(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("appId") final String appId,
                               @RequestParam("appVersion") final String appVersion,
                               @RequestParam("dataListId") final String dataListId,
                               @RequestParam(value = "processId", required = false) final String[] processId,
                               @RequestParam(value = "activityId", required = false) final String[] activityDefIds,
                               @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
                               @RequestParam(value = "start", required = false) final Integer start,
                               @RequestParam(value = "rows", required = false, defaultValue = "0") final Integer rows,
                               @RequestParam(value = "sort", required = false) final String sort,
                               @RequestParam(value = "desc", required = false, defaultValue = "false") final Boolean desc,
                               @RequestParam(value = "digest", required = false) final String digest)
            throws IOException {

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

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            // configure sorting
            if (sort != null) {
                dataList.setDefaultSortColumn(sort);

                // order ASC / DESC
                dataList.setDefaultOrder(desc ? DataList.ORDER_DESCENDING_VALUE : DataList.ORDER_ASCENDING_VALUE);
            }

            // paging
            int pageSize = rows != null && rows > 0 ? rows : page != null && page > 0 ? DataList.DEFAULT_PAGE_SIZE : DataList.MAXIMUM_PAGE_SIZE;
            int rowStart = start != null ? start : page != null && page > 0 ? ((page - 1) * pageSize) : 0;

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                @Nonnull List<String> pids = convertMultiValueParameterToList(processId);
                @Nonnull List<String> aids = convertMultiValueParameterToList(activityDefIds);

                @Nonnull Collection<WorkflowAssignment> assignmentList = getAssignmentList(dataListId, pids, aids,null, null, null, null);

                // get original process ID from assignments
                final Map<String, Collection<String>> mapPrimaryKeyToProcessId = workflowProcessLinkDao.getOriginalIds(assignmentList.stream().map(WorkflowAssignment::getProcessId).collect(Collectors.toList()));
                @Nonnull List<String> originalPids = mapPrimaryKeyToProcessId
                        .keySet()
                        .stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                addFilterById(dataList, originalPids);

                JSONArray jsonData = Optional.ofNullable((DataListCollection<Map<String, Object>>)dataList.getRows(pageSize, rowStart))
                        .orElse(new DataListCollection<>())
                        .stream()

                        // reformat content value
                        .peek(row -> row.entrySet().forEach(e -> e.setValue(format(dataList, row, e.getKey()))))

                        .peek(row -> {
                            // add process information
                            String primaryKey = row.get(dataList.getBinder().getPrimaryKeyColumnName()).toString();
                            mapPrimaryKeyToProcessId
                                    .get(primaryKey)
                                    .stream()
                                    .map(workflowManager::getAssignmentByProcess)
                                    .findFirst()
                                    .ifPresent(a -> {
                                        row.put("activityId", a.getActivityId());
                                        row.put("processId", a.getProcessId());
                                        row.put("assigneeId", a.getAssigneeId());
                                    });

                        })

                        // collect as JSON
                        .collect(JSONArray::new, JSONArray::put, (a1, a2) -> {
                            for(int i = 0, size = a2.length(); i < size; i++) {
                                try {
                                    a1.put(a2.get(i));
                                } catch (JSONException ignored) { }
                            }
                        });

                String currentDigest = getDigest(jsonData);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                if (!Objects.equals(digest, currentDigest))
                    jsonResponse.put(FIELD_DATA, jsonData);

                jsonResponse.put(FIELD_DIGEST, currentDigest);

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Convert Multi Value Parameter to List
     *
     * @param parameter request parameter values
     * @return sorted list
     */
    private List<String> convertMultiValueParameterToList(String[] parameter) {
        return Optional.ofNullable(parameter)
                .map(Arrays::stream)
                .orElse(Stream.of(""))
                .map(s -> s.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/json/app/(*:appId)/(~:appVersion)/data/assignments/list/(*:dataListId)/count", method = RequestMethod.GET)
    public void getDataListAssignmentsCount(final HttpServletRequest request, final HttpServletResponse response,
                                            @RequestParam("appId") final String appId,
                                            @RequestParam("appVersion") final String appVersion,
                                            @RequestParam("dataListId") final String dataListId,
                                            @RequestParam(value = "processId", required = false) final String[] processId,
                                            @RequestParam(value = "activityId", required = false) final String[] activityId)
            throws IOException {

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

            // get dataList definition
            DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(dataListId, appDefinition);
            if (datalistDefinition == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "DataList Definition for dataList [" + dataListId + "] not found");
            }

            // get dataList
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if (dataList == null) {
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Error generating dataList [" + dataListId + "]");
            }

            getCollectFilters(request.getParameterMap(), dataList);

            try {
                @Nonnull List<String> pids = convertMultiValueParameterToList(processId);
                @Nonnull List<String> aids = convertMultiValueParameterToList(activityId);

                @Nonnull Collection<WorkflowAssignment> assignmentList = getAssignmentList(dataListId, pids, aids,null, null, null, null);

                // get original process ID from assignments
                @Nonnull List<String> originalPids = workflowProcessLinkDao
                        .getOriginalIds(assignmentList.stream().map(WorkflowAssignment::getProcessId).collect(Collectors.toList()))
                        .keySet()
                        .stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                addFilterById(dataList, originalPids);

                JSONObject jsonResponse = new JSONObject();

                jsonResponse.put(FIELD_TOTAL, dataList.getSize());

                response.setStatus(HttpServletResponse.SC_OK);

                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                throw new ApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
            response.sendError(e.getErrorCode(), e.getMessage());
        }
    }


    /**
     * Get Assignment List
     * Get assignment of dataList
     *
     * @param dataListId
     * @param processIds
     * @param activityDefIds
     * @param sort
     * @param desc
     * @param start
     * @param size
     * @return
     */
    @Nonnull
    private Collection<WorkflowAssignment> getAssignmentList(@Nonnull String dataListId, @Nonnull final List<String> processIds, @Nonnull final List<String> activityDefIds, String sort, Boolean desc, Integer start, Integer size) {
        @Nonnull final AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        @Nonnull final ApplicationContext ac = AppUtil.getApplicationContext();
        @Nonnull final WorkflowManager workflowManager = (WorkflowManager)ac.getBean("workflowManager");
        @Nonnull final AppService appService = (AppService)ac.getBean("appService");
        @Nonnull final PackageDefinition packageDef = appDef.getPackageDefinition();

        return processIds.stream()
                .filter(Objects::nonNull)
                .map(s -> {
                    if(s.isEmpty()) {
                        return "";
                    } else {
                        WorkflowProcess p = appService.getWorkflowProcessForApp(appDef.getId(), appDef.getVersion().toString(), s);
                        if (p == null) {
                            LogUtil.warn(getClass().getName(), "Process [" + s + "] is not defined for this app");
                            return "";
                        }
                        return p.getId();
                    }
                })

                // get assignments
                .flatMap(pid -> activityDefIds.stream()
                        .map(aid -> {
                            if (!dataListId.isEmpty()) {
                                return workflowManager.getAssignmentListLite(packageDef.getId(), pid, null, aid, sort, desc, start, size);
                            } else {
                                return workflowManager.getAssignmentList(packageDef.getId(), pid, null, aid, sort, desc, start, size);
                            }
                        })
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream))
                .collect(Collectors.toList());

//        if (processId == null || processId.isEmpty()) {
//            PackageDefinition packageDef = appDef.getPackageDefinition();
//            if (packageDef != null) {
//                if (!dataListId.isEmpty()) {
//                    return workflowManager.getAssignmentListLite(packageDef.getId(), null, null, null, sort, desc, start, size);
//                }
//                return workflowManager.getAssignmentList(packageDef.getId(), null, null, null, sort, desc, start, size);
//            }
//        } else {
//            WorkflowProcess process = appService.getWorkflowProcessForApp(appDef.getId(), appDef.getVersion().toString(), processId);
//            if (process != null) {
//                if (activityDefIds == null || activityDefIds.length == 0) {
//                    // Filter by PROCESS ID
//                    if (!dataListId.isEmpty()) {
//                        return workflowManager.getAssignmentListLite(null, process.getId(), null, null, sort, desc, start, size);
//                    } else {
//                        return workflowManager.getAssignmentList(null, process.getId(), null, null, sort, desc, start, size);
//                    }
//                } else {
//                    // Filter by PROCESS ID and ACTIVITY ID
//                    return Arrays.stream(activityDefIds)
//                            .map(a -> a.split(";"))
//                            .flatMap(Arrays::stream)
//                            .map(String::trim)
//                            .filter(s -> !s.isEmpty())
//                            .map(activityDefId -> {
//                                if (!dataListId.isEmpty()) {
//                                    return workflowManager.getAssignmentListLite(null, process.getId(), null, activityDefId, sort, desc, start, size);
//                                } else {
//                                    return workflowManager.getAssignmentList(null, process.getId(), null, activityDefId, sort, desc, start, size);
//                                }
//                            })
//                            .flatMap(Collection::stream)
//                            .collect(Collectors.toList());
//                }
//            }
//        }
//        return new ArrayList<>();
    }

    private String nullIfEmpty(String s) {
        return s.isEmpty() ? null : s;
    }


    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     *
     * @param json  JSON array object
     * @return      digest value
     */
    private String getDigest(JSONArray json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    /**
     * Calculate digest (version if I may call) but will omit "elementUniqueKey"
     *
     * @param json JSON object
     * @return digest value
     */
    private String getDigest(JSONObject json) {
        return json == null || json.toString() == null ? null : DigestUtils.sha256Hex(json.toString());
    }

    /**
     * @param requestParameters Request parameter
     * @param dataList          Input/Output parameter
     */
    private void getCollectFilters(@Nonnull final Map<String, String[]> requestParameters, @Nonnull final DataList dataList) {
        Arrays.stream(dataList.getFilters())
                .peek(f -> {
                    if (!(f.getType() instanceof DataListFilterTypeDefault))
                        LogUtil.warn(getClass().getName(), "DataList filter [" + f.getName() + "] is not instance of [" + DataListFilterTypeDefault.class.getName() + "], filter will be ignored");
                })
                .filter(f -> Objects.nonNull(requestParameters.get(f.getName())) && f.getType() instanceof DataListFilterTypeDefault)
                .forEach(f -> f.getType().setProperty("defaultValue", String.join(";", requestParameters.get(f.getName()))));
    }

    /**
     * Format
     *
     * @param dataList  DataList
     * @param row       Row
     * @param field     Field
     * @return
     */
    private @Nonnull
    String format(DataList dataList, Map<String, Object> row, String field) {
        if (dataList.getColumns() == null) {
            return String.valueOf(row.get(field));
        }

        for (DataListColumn column : dataList.getColumns()) {
            if (!field.equals(column.getName())) {
                continue;
            }

            String value = String.valueOf(row.get(field));
            if (column.getFormats() == null) {
                return value;
            }

            for (DataListColumnFormat format : column.getFormats()) {
                if (format != null) {
                    return format.format(dataList, column, row, value).replaceAll("<[^>]*>", "");
                }
            }
        }

        return String.valueOf(row.get(field));
    }

    /**
     * Validate and Determine Process ID
     *
     * @param appId         Application ID
     * @param appVersion    Application version
     * @param processId     Process ID
     * @return
     * @throws ApiException
     */
    private String validateAndDetermineProcessDefId(String appId, Long appVersion, String processId) throws ApiException {
        if (processId == null) {
            return null;
        }

        if (appId == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Parameter [appId] and [version] is required for filtering by [processId]");
        }

        AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, appVersion == null || appVersion == 0 ? appDefinitionDao.getPublishedVersion(appId) : appVersion);
        if (appDefinition == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Application ID [" + appId + "] is not available");
        }

        WorkflowProcess workflowProcess = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId);
        if (workflowProcess == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Process [" + processId + "] for Application ID [" + appId + "] is not available");
        }

        WorkflowProcess process = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId);
        if (process == null) {
            throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + processId + "] for application [" + appId + "] version [" + appVersion + "]");
        }

        return process.getId();
    }

    /**
     * Generate Workflow Variable
     *
     * @param form          Form
     * @param formData      Form Data
     * @return
     */
    private Map<String, String> generateWorkflowVariable(@Nonnull final Form form, @Nonnull final FormData formData) {
        return formData.getRequestParams().entrySet().stream().collect(HashMap::new, (m, e) -> {
            Element element = FormUtil.findElement(e.getKey(), form, formData, true);
            if (Objects.isNull(element))
                return;

            String workflowVariable = element.getPropertyString("workflowVariable");

            if (Objects.isNull(workflowVariable) || workflowVariable.isEmpty())
                return;

            m.put(element.getPropertyString("workflowVariable"), String.join(";", e.getValue()));
        }, Map::putAll);
    }

    /**
     * Create filter by ID
     *
     * @param dataList
     * @param originalPids
     * @return
     */
    private DataList addFilterById(DataList dataList, List<String> originalPids) {
        DataListFilterQueryObject filterQueryObject = new DataListFilterQueryObject();
        filterQueryObject.setOperator("AND");

        final List<String> values = new ArrayList<>();
        String prefix = dataList.getBinder().getColumnName("id") + " in (";
        String suffix = ")";
        String sql = originalPids
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    values.add(s);
                    return "?";
                })
                .collect(Collectors.joining(", "));
        filterQueryObject.setQuery(sql.isEmpty() ? " id is null" : (prefix + sql + suffix));
        filterQueryObject.setValues(values.toArray(new String[0]));
        dataList.addFilterQueryObject(filterQueryObject);
        return dataList;
    }
}
