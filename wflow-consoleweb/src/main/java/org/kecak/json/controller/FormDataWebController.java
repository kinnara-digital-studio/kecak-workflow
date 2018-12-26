package org.kecak.json.controller;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Collectors;

@Controller
public class FormDataWebController {

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

    /**
     * Submit form into table, can be used to save master data
     * @param request HTTP Request, request body contains form field values
     * @param writer
     * @param appId Application ID
     * @param appVersion put 0 for current published app
     * @param formDefId Form ID
     */
    @RequestMapping(value = "/json/form/app/(*:appId)/version/(*:appVersion)/submit/(*:formDefId)", method = RequestMethod.POST)
    public void submit(HttpServletRequest request, Writer writer, @RequestParam("appId") String appId, @RequestParam("appVersion") String appVersion, @RequestParam("formDefId") String formDefId) {
        // get version, version 0 indicates published version
        Long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

        // get current App
        AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
        if(appDefinition == null) {
            // TODO : check if app valid
            return;
        }

        final FormData formData = new FormData();

        // read request body
        try(BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {

            // convert request body to json
            JSONObject jsonBody = new JSONObject(br.lines().collect(Collectors.joining()));
            Iterator<String> i = jsonBody.keys();
            while(i.hasNext()) {
                String key = i.next();

                // convert json to field data
                formData.addRequestParameterValues(key, new String[] {jsonBody.optString(key)});
            }

            // use field "ID" as primary key if possible
            if(jsonBody.has(FormUtil.PROPERTY_ID)) {
                formData.setPrimaryKeyValue(jsonBody.getString(FormUtil.PROPERTY_ID));
            }
        } catch (IOException | JSONException e) {
            // TODO : do something, send back error message to client
        }

        // submit form
        final FormData result = appService.submitForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), formDefId, formData, false);

        // TODO : return processResult
    }

    /**
     * Start new process
     * @param request HTTP Request, request body contains form field values
     * @param writer
     * @param appId Application ID
     * @param appVersion put 0 for current published app
     * @param processId Process ID
     */
    @RequestMapping(value = "/json/form/app/(*:appId)/version/(*:appVersion)/start/(*:processId)", method = RequestMethod.POST)
    public void processStart(HttpServletRequest request, Writer writer, @RequestParam("appId") String appId, @RequestParam("appVersion") String appVersion, @RequestParam("processId") String processId) {
        // get version, version 0 indicates published version
        Long version = Long.parseLong(appVersion) == 0 ? appDefinitionDao.getPublishedVersion(appId) : Long.parseLong(appVersion);

        // get current App
        AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, version);
        if(appDefinition == null) {
            // TODO : check if app valid
            return;
        }

        // get processDefId
        String processDefId = appService.getWorkflowProcessForApp(appDefinition.getAppId(), appDefinition.getVersion().toString(), processId).getId();

        final FormData formData = new FormData();

        // read request body
        try(BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {

            // convert request body to json
            JSONObject jsonBody = new JSONObject(br.lines().collect(Collectors.joining()));
            Iterator<String> i = jsonBody.keys();
            while(i.hasNext()) {
                String key = i.next();

                // convert json to field data
                formData.addRequestParameterValues(key, new String[] {jsonBody.optString(key)});
            }
        } catch (IOException | JSONException e) {
            // TODO : do something, send back error message to client
        }

        // trigger run process
        WorkflowProcessResult processResult = appService.submitFormToStartProcess(appDefinition.getAppId(), appDefinition.getVersion().toString(), processDefId, formData, null, null, null);

        // TODO : return processResult
    }

    /**
     * Complete assignment form
     * @param request HTTP Request, request body contains form field values
     * @param writer
     * @param assignmentId Assignment ID
     */
    @RequestMapping(value = "/json/form/complete/(*:assignmentId)", method = RequestMethod.POST)
    public void complete(HttpServletRequest request, Writer writer, @RequestParam("assignmentId") String assignmentId) {
        WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
        if(assignmentId == null) {
            // TODO : check if assignment available
            return;
        }


        AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
        if(appDefinition == null) {
            // TODO : check if app valid
            return;
        }

        final FormData formData = new FormData();

        // read request body
        try(BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {

            // convert request body to json
            JSONObject jsonBody = new JSONObject(br.lines().collect(Collectors.joining()));
            Iterator<String> i = jsonBody.keys();
            while(i.hasNext()) {
                String key = i.next();

                // convert json to field data
                formData.addRequestParameterValues(key, new String[] {jsonBody.optString(key)});
            }

            // TODO : return processResult
        } catch (IOException | JSONException e) {
            // TODO : do something, send back error message to client
        }

        FormData resultFormData = appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, null);

        // TODO : do something with result data
    }
}
