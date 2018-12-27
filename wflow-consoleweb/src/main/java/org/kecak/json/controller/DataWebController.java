package org.kecak.json.controller;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormService;
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
import org.kecak.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

@Controller
public class DataWebController {

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
    private FormService formService;

    /**
     * Submit form into table, can be used to save master data
     * @param request HTTP Request, request body contains form field values
     * @param response HTTP response
     * @param appId Application ID
     * @param appVersion put 0 for current published app
     * @param formDefId Form ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/submit", method = RequestMethod.POST)
    public void formSubmit(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") final String appId, @RequestParam("appVersion") final String appVersion, @RequestParam("formDefId") final String formDefId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing ["+request.getRequestURI()+"] in method ["+request.getMethod()+"] as ["+ WorkflowUtil.getCurrentUsername() +"]" );

        try {
            // get version, version 0 indicates published version
            Long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if(appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request);

            // submit form
            final FormData result = appService.submitForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, formData, false);

            // construct response
            final JSONObject jsonResponse = new JSONObject();
            if(result.getFormErrors() != null && !result.getFormErrors().isEmpty()) {
                // show error message
                result.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonResponse.put(key, value);
                    } catch (JSONException ignored) {
                    }
                });
            } else {
                jsonResponse.put("id", result.getPrimaryKeyValue());
                jsonResponse.put("primaryKey", result.getPrimaryKeyValue());
            }

            response.getWriter().write(jsonResponse.toString());
        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Start new process
     * @param request HTTP Request, request body contains form field values
     * @param response HTTP Response
     * @param appId Application ID
     * @param appVersion put 0 for current published app
     * @param processId Process ID
     */
    @RequestMapping(value = "/json/data/app/(*:appId)/version/(*:appVersion)/process/(*:processId)/start", method = RequestMethod.POST)
    public void processStart(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("appId") String appId, @RequestParam("appVersion") String appVersion, @RequestParam("processId") String processId) throws IOException, JSONException{
        LogUtil.info(getClass().getName(), "Executing ["+request.getRequestURI()+"] in method ["+request.getMethod()+"] as ["+ WorkflowUtil.getCurrentUsername() +"]" );

        try {
            // get version, version 0 indicates published version
            Long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + version + "]");
            }

            // get processDefId
            String processDefId = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId).getId();

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request);

            JSONObject jsonResponse = new JSONObject();

            // trigger run process
            WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, null, null, null);
            if(processResult != null) {
                jsonResponse.put("status", processResult.getStatus());

                if (processResult.getProcess() != null) {
                    jsonResponse.put("id", processResult.getProcess().getInstanceId());
                    jsonResponse.put("processId", processResult.getProcess().getInstanceId());
                    jsonResponse.put("processDefId", processResult.getProcess().getId());

                    // execute store binder
                    formData.setPrimaryKeyValue(processResult.getProcess().getInstanceId());
                }

                if (processResult.getActivities() != null && !processResult.getActivities().isEmpty()) {
                    jsonResponse.put("activityId", new JSONArray(processResult.getActivities().stream().map(WorkflowActivity::getId).collect(Collectors.toList())));
                }
            }

            if (formData.getFormErrors() != null && !formData.getFormErrors().isEmpty()) {
                // show error message
                formData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonResponse.put(key, value);
                    } catch (JSONException ignored) {
                    }
                });
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Complete assignment form
     * @param request HTTP Request, request body contains form field values
     * @param response HTTP Response
     * @param assignmentId Assignment ID
     */
    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)/complete", method = RequestMethod.POST)
    public void assignmentComplete(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("assignmentId") String assignmentId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing ["+request.getRequestURI()+"] in method ["+request.getMethod()+"] as ["+ WorkflowUtil.getCurrentUsername() +"]" );

        try {
            WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
            if (assignmentId == null) {
                // check if assignment available
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid assignment [" + assignmentId + "]");
            }


            AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + assignment.getProcessId() + "]");
            }

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request);

            FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, new HashMap<>());

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            if (resultFormData.getFormErrors() != null && !resultFormData.getFormErrors().isEmpty()) {
                // show error message
                resultFormData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonResponse.put(key, value);
                    } catch (JSONException ignored) {
                    }
                });
            } else {
                jsonResponse.put("id", resultFormData.getPrimaryKeyValue());
                jsonResponse.put("processId", resultFormData.getProcessId());
                jsonResponse.put("activityId", resultFormData.getActivityId());

                WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(resultFormData.getProcessId());
                if(nextAssignment != null) {
                    JSONObject jsonAssignment = new JSONObject();
                    jsonAssignment.put("processId", nextAssignment.getProcessId());
                    jsonAssignment.put("processDefId", nextAssignment.getProcessDefId());
                    jsonAssignment.put("activityId", nextAssignment.getActivityId());
                    jsonResponse.put("assignment", jsonAssignment);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    @RequestMapping(value = "/json/data/assignment/(*:assignmentId)/view", method = RequestMethod.GET)
    public void assignmentView(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("assignmentId") String assignmentId) throws IOException, JSONException {
        LogUtil.info(getClass().getName(), "Executing ["+request.getRequestURI()+"] in method ["+request.getMethod()+"] as ["+ WorkflowUtil.getCurrentUsername() +"]" );

        try {
            WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
            if (assignmentId == null) {
                // check if assignment available
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid assignment [" + assignmentId + "]");
            }


            AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + assignment.getProcessId() + "]");
            }

            // read request body and convert request body to json
            final FormData formData = extractBodyToFormData(request);

            FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, new HashMap<>());

            // return processResult
            JSONObject jsonResponse = new JSONObject();
            if (resultFormData.getFormErrors() != null && !resultFormData.getFormErrors().isEmpty()) {
                // show error message
                resultFormData.getFormErrors().forEach((key, value) -> {
                    try {
                        jsonResponse.put(key, value);
                    } catch (JSONException ignored) {
                    }
                });
            } else {
                jsonResponse.put("id", resultFormData.getPrimaryKeyValue());
                jsonResponse.put("processId", resultFormData.getProcessId());
                jsonResponse.put("activityId", resultFormData.getActivityId());

                WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(resultFormData.getProcessId());
                if(nextAssignment != null) {
                    JSONObject jsonAssignment = new JSONObject();
                    jsonAssignment.put("processId", nextAssignment.getProcessId());
                    jsonAssignment.put("processDefId", nextAssignment.getProcessDefId());
                    jsonAssignment.put("activityId", nextAssignment.getActivityId());
                    jsonResponse.put("assignment", jsonAssignment);
                }
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (ApiException e) {
            response.sendError(e.getErrorCode(), e.getMessage());
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }

    /**
     * Convert request body to form data
     * @param request
     * @return form data
     * @throws IOException
     * @throws JSONException
     */
    private FormData extractBodyToFormData(final HttpServletRequest request) throws IOException, JSONException {
        final FormData formData = new FormData();

        // read request body and convert request body to json
        JSONObject jsonBody = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
        Iterator i = jsonBody.keys();
        while(i.hasNext()) {
            String key = String.valueOf(i.next());

            // convert json to field data
            formData.addRequestParameterValues(key, new String[] {jsonBody.optString(key)});
        }

        formData.addRequestParameterValues(AssignmentCompleteButton.DEFAULT_ID, new String[] {"true"});

        // use field "ID" as primary key if possible
        if(jsonBody.has(FormUtil.PROPERTY_ID)) {
            formData.setPrimaryKeyValue(jsonBody.getString(FormUtil.PROPERTY_ID));
        }

        formData.setDoValidation(true);

        return formData;
    }
}
