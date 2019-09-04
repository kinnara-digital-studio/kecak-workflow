package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.WorkflowVariable;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.*;


@Endpoint
public class SoapMonitoringEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> processIdExpression;
    private XPathExpression<Element> processDefIdExpression;
    private XPathExpression<Element> processNameExpression;
    private XPathExpression<Element> activityDefIdExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;
    private XPathExpression<Element> usernameExpression;
    private XPathExpression<Element> replaceUserExpression;
    private XPathExpression<Element> activityIdExpression;
    private XPathExpression<Element> abortCurrentExpression;
    private XPathExpression<Element> variableExpression;
    private XPathExpression<Element> valueExpression;
    private XPathExpression<Element> packageIdExpression;
    private XPathExpression<Element> versionExpression;

    @Autowired
    private WorkflowUserManager workflowUserManager;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private AppService appService;

    public SoapMonitoringEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            processIdExpression = xpathFactory.compile("//xps:processId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processIdExpression = null;
        }
        try {
            processDefIdExpression = xpathFactory.compile("//xps:processDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processDefIdExpression = null;
        }
        try {
            processNameExpression = xpathFactory.compile("//xps:processName", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processNameExpression = null;
        }

        try {
            activityDefIdExpression = xpathFactory.compile("//xps:activityDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            activityDefIdExpression = null;
        }

        try {
            sortExpression = xpathFactory.compile("//xps:sort", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            sortExpression = null;
        }
        try {
            descExpression = xpathFactory.compile("//xps:desc", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            descExpression = null;
        }
        try {
            startExpression = xpathFactory.compile("//xps:start", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            startExpression = null;
        }
        try {
            rowsExpression = xpathFactory.compile("//xps:rows", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            rowsExpression = null;
        }
        try {
            usernameExpression = xpathFactory.compile("//xps:username", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            usernameExpression = null;
        }
        try {
            replaceUserExpression = xpathFactory.compile("//xps:replaceUser", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            replaceUserExpression = null;
        }
        try {
            activityIdExpression = xpathFactory.compile("//xps:activityId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            activityIdExpression = null;
        }
        try {
            abortCurrentExpression = xpathFactory.compile("//xps:abortCurrent", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            abortCurrentExpression = null;
        }
        try {
            variableExpression = xpathFactory.compile("//xps:variable", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            variableExpression = null;
        }
        try {
            valueExpression = xpathFactory.compile("//xps:value", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            valueExpression = null;
        }
        try {
            packageIdExpression = xpathFactory.compile("//xps:packageId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            packageIdExpression = null;
        }
        try {
            versionExpression = xpathFactory.compile("//xps:version", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            versionExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityAbortRequest")
    public @ResponsePayload Element handleActivityAbort(@RequestPayload Element activityAbortElement) {
        final String processId = processIdExpression.evaluate(activityAbortElement).get(0).getValue();
        final String activityDefId = activityDefIdExpression.evaluate(activityAbortElement).get(0).getValue();
        appService.getAppDefinitionForWorkflowProcess(processId);
        workflowManager.activityAbort(processId, activityDefId);

        Element response = new Element("ActivityAbortResponse",namespace);
        response.addContent(new Element("processId",namespace).setText(processId));
        response.addContent(new Element("activityDefId",namespace).setText(activityDefId));
        response.addContent(new Element("status",namespace).setText("aborted"));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityListRequest")
    public @ResponsePayload Element handleActivityList(@RequestPayload Element activityListElement) {
        final String processId = processIdExpression.evaluate(activityListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(activityListElement).get(0).getValue();
        final String descString = descExpression.evaluate(activityListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(activityListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(activityListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);

        Element response = new Element("ActivityListResponse",namespace);
        Collection<WorkflowActivity> activityList = workflowManager.getActivityList(processId, start, rows, sort, desc);

        Integer total = workflowManager.getActivitySize(processId);
        Element data = new Element("data",namespace);
        for (WorkflowActivity workflowActivity : activityList) {
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(workflowActivity.getId());
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(workflowActivity.getId()));
            item.addContent(new Element("name",namespace).setText(workflowActivity.getName()));
            item.addContent(new Element("state",namespace).setText(workflowActivity.getState()));
            item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(workflowActivity.getCreatedTime(), null, AppUtil.getAppDateFormat())));
            item.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityReassignRequest")
    public @ResponsePayload Element handleActivityReassign(@RequestPayload Element activityReassignElement) {
        final String username = usernameExpression.evaluate(activityReassignElement).get(0).getValue();
        final String replaceUser = replaceUserExpression.evaluate(activityReassignElement).get(0).getValue();
        final String activityId = activityIdExpression.evaluate(activityReassignElement).get(0).getValue();
        workflowManager.assignmentReassign(null, null, activityId, username, replaceUser);

        Element response = new Element("ActivityReassignResponse",namespace);
        response.addContent(new Element("activityId",namespace).setText(activityId));
        response.addContent(new Element("username",namespace).setText(username));
        response.addContent(new Element("replaceUser",namespace).setText(replaceUser));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityReevaluateRequest")
    public @ResponsePayload Element handleActivityReevaluate(@RequestPayload Element activityReevaluateElement) {
        final String activityId = activityIdExpression.evaluate(activityReevaluateElement).get(0).getValue();
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.reevaluateAssignmentsForActivity(activityId);

        Element response = new Element("ActivityReevaluateResponse",namespace);
        response.addContent(new Element("status",namespace).setText("Success"));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityStartRequest")
    public @ResponsePayload Element handleActivityStart(@RequestPayload Element activityStartElement) {
        final String processId = processIdExpression.evaluate(activityStartElement).get(0).getValue();
        final String activityDefId = activityDefIdExpression.evaluate(activityStartElement).get(0).getValue();
        final String abortCurrentString = abortCurrentExpression.evaluate(activityStartElement).get(0).getValue();
        final boolean abortFlag = (abortCurrentString == null)?false:Boolean.parseBoolean(abortCurrentString);
        appService.getAppDefinitionForWorkflowProcess(processId);
        boolean result = workflowManager.activityStart(processId, activityDefId, abortFlag);
        Element response = new Element("ActivityStartResponse",namespace);
        response.addContent(new Element("processId",namespace).setText(processId));
        response.addContent(new Element("activityDefId",namespace).setText(activityDefId));
        response.addContent(new Element("result",namespace).setText(String.valueOf(result)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityVariableRequest")
    public @ResponsePayload Element handleActivityVariable(@RequestPayload Element activityVariableElement) {
        final String activityId = activityIdExpression.evaluate(activityVariableElement).get(0).getValue();
        final String variable = variableExpression.evaluate(activityVariableElement).get(0).getValue();
        final String value = valueExpression.evaluate(activityVariableElement).get(0).getValue();

        Element response = new Element("ActivityVariableResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.activityVariable(activityId, variable, value);
        LogUtil.info(getClass().getName(), "Activity variable " + variable + " set to " + value);
        response.addContent(new Element("status",namespace).setText("variableSet"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ActivityViewRequest")
    public @ResponsePayload Element handleActivityView(@RequestPayload Element activityViewElement) {
        final String activityId = activityIdExpression.evaluate(activityViewElement).get(0).getValue();

        Element response = new Element("ActivityViewResponse",namespace);
        WorkflowActivity activity = workflowManager.getActivityById(activityId);
        if (activity == null || activity.getId() == null) {
            response.addContent(new Element("status",namespace).setText("Activity Not Found"));
        }
        WorkflowActivity activityInfo = workflowManager.getRunningActivityInfo(activityId);
        double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(activityId);
        WorkflowActivity trackWflowActivity = workflowManager.getRunningActivityInfo(activityId);

        response.addContent(new Element("activityId",namespace).setText(activity.getId()));
        response.addContent(new Element("activityDefId",namespace).setText(activity.getActivityDefId()));
        response.addContent(new Element("processId",namespace).setText(activity.getProcessId()));
        response.addContent(new Element("processDefId",namespace).setText(activity.getProcessDefId()));
        response.addContent(new Element("processVersion",namespace).setText(activity.getProcessVersion()));
        response.addContent(new Element("processName",namespace).setText(activity.getProcessName()));
        response.addContent(new Element("activityName",namespace).setText(activity.getName()));
        response.addContent(new Element("description",namespace).setText(activity.getDescription()));
        response.addContent(new Element("participant",namespace).setText(activity.getPerformer()));
        response.addContent(new Element("acceptedUser",namespace).setText(activity.getNameOfAcceptedUser()));

        //new added attribute
        response.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));
        response.addContent(new Element("state",namespace).setText(activityInfo.getState()));
        response.addContent(new Element("createdTime",namespace).setText(trackWflowActivity.getCreatedTime().toString()));
        response.addContent(new Element("dateLimit",namespace).setText(trackWflowActivity.getLimit()));
        response.addContent(new Element("dueDate",namespace).setText(TimeZoneUtil.convertToTimeZone(trackWflowActivity.getDue(), null, AppUtil.getAppDateFormat())));
        response.addContent(new Element("delay",namespace).setText(trackWflowActivity.getDelay()));
        response.addContent(new Element("finishTime",namespace).setText(TimeZoneUtil.convertToTimeZone(trackWflowActivity.getFinishTime(), null, AppUtil.getAppDateFormat())));
        response.addContent(new Element("timeConsumingFromDateCreated",namespace).setText(trackWflowActivity.getTimeConsumingFromDateCreated()));


        String[] assignmentUsers = activityInfo.getAssignmentUsers();
        for (String user : assignmentUsers) {
            response.addContent(new Element("assignee",namespace).setText(user));
        }
        Collection<WorkflowVariable> variableList = workflowManager.getActivityVariableList(activityId);
        Element var = new Element("variable",namespace);
        for (WorkflowVariable variable : variableList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element(variable.getId(),namespace).setText(variable.getVal().toString()));
            var.addContent(item);
        }
        response.addContent(var);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CompletedProcessListRequest")
    public @ResponsePayload Element handleCompletedProcessList(@RequestPayload Element completedProcessListElement) {
        final String packageId = packageIdExpression.evaluate(completedProcessListElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(completedProcessListElement).get(0).getValue();
        final String processName = processNameExpression.evaluate(completedProcessListElement).get(0).getValue();
        final String version = versionExpression.evaluate(completedProcessListElement).get(0).getValue();
        String sort = sortExpression.evaluate(completedProcessListElement).get(0).getValue();
        final String descString = descExpression.evaluate(completedProcessListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(completedProcessListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(completedProcessListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);

        Element response = new Element("CompletedProcessListResponse",namespace);
        if ("startedTime".equals(sort)) {
            sort = "Started";
        } else if ("createdTime".equals(sort)) {
            sort = "Created";
        }
        Collection<WorkflowProcess> processList = workflowManager.getCompletedProcessList(packageId, processId, processName, version, sort, desc, start, rows);

        Integer total = workflowManager.getCompletedProcessSize(packageId, processId, processName, version);
        Element data = new Element("data",namespace);
        for (WorkflowProcess workflowProcess : processList) {
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningProcess(workflowProcess.getInstanceId());
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(workflowProcess.getInstanceId()));
            item.addContent(new Element("name",namespace).setText(workflowProcess.getName()));
            item.addContent(new Element("version",namespace).setText(workflowProcess.getVersion()));
            item.addContent(new Element("state",namespace).setText(workflowProcess.getState()));
            item.addContent(new Element("startedTime",namespace).setText(TimeZoneUtil.convertToTimeZone(workflowProcess.getStartedTime(), null, AppUtil.getAppDateFormat())));
            item.addContent(new Element("requesterId",namespace).setText(workflowProcess.getRequesterId()));
            item.addContent(new Element("due",namespace).setText(workflowProcess.getDue() != null ? TimeZoneUtil.convertToTimeZone(workflowProcess.getDue(), null, AppUtil.getAppDateFormat()) : "-"));
            item.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));

            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessCopyRequest")
    public @ResponsePayload Element handleProcessCopy(@RequestPayload Element processCopyElement) {
        String processId = processIdExpression.evaluate(processCopyElement).get(0).getValue();
        String processDefId = processDefIdExpression.evaluate(processCopyElement).get(0).getValue();
        String abortCurrentString = abortCurrentExpression.evaluate(processCopyElement).get(0).getValue();

        Element response = new Element("ProcessCopyResponse",namespace);
        appService.getAppDefinitionForWorkflowProcess(processId);
        boolean abortFlag = (abortCurrentString != null) ? Boolean.parseBoolean(abortCurrentString) : false;
        processDefId = processDefId.replaceAll(":", "#");
        WorkflowProcessResult processResult = workflowManager.processCopyFromInstanceId(processId, processDefId, abortFlag);
        String newProcessId = "";
        String[] startedActivities = new String[0];
        WorkflowProcess processStarted = processResult.getProcess();
        Element actElement = new Element("activities",namespace);
        if (processStarted != null) {
            newProcessId = processStarted.getInstanceId();
            Collection<WorkflowActivity> activities = processResult.getActivities();
            for (WorkflowActivity act : activities) {
                actElement.addContent(new Element("activityId",namespace).setText(act.getId()));
            }
        }
        response.addContent(new Element("processDefId",namespace).setText(processDefId));
        response.addContent(new Element("processId",namespace).setText(newProcessId));
        response.addContent(actElement);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "MonitoringProcessVariableRequest")
    public @ResponsePayload Element handleMonitoringProcessVariable(@RequestPayload Element processVariableElement) {
        final String processId = processIdExpression.evaluate(processVariableElement).get(0).getValue();
        final String variable = variableExpression.evaluate(processVariableElement).get(0).getValue();
        final String value = valueExpression.evaluate(processVariableElement).get(0).getValue();

        Element response = new Element("MonitoringProcessVariableResponse",namespace);
        appService.getAppDefinitionForWorkflowProcess(processId);
        workflowManager.processVariable(processId, variable, value);
        LogUtil.info(getClass().getName(), "Process variable " + variable + " set to " + value);
        response.addContent(new Element("status",namespace).setText("variableSet"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "MonitoringProcessViewRequest")
    public @ResponsePayload Element handleMonitoringProcessView(@RequestPayload Element processViewElement) {
        final String processId = processIdExpression.evaluate(processViewElement).get(0).getValue();

        Element response = new Element("MonitoringProcessViewResponse",namespace);
        WorkflowProcess process = workflowManager.getRunningProcessById(processId);
        if (process == null || process.getId() == null) {
            response.addContent(new Element("status",namespace).setText("Process Not Found"));
        }

        double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningProcess(processId);
        WorkflowProcess trackWflowProcess = workflowManager.getRunningProcessInfo(processId);

        response.addContent(new Element("processDefId",namespace).setText(process.getId()));
        response.addContent(new Element("processId",namespace).setText(process.getInstanceId()));
        response.addContent(new Element("packageId",namespace).setText(process.getPackageId()));
        response.addContent(new Element("packageName",namespace).setText(process.getPackageName()));
        response.addContent(new Element("name",namespace).setText(process.getName()));
        response.addContent(new Element("version",namespace).setText(process.getVersion()));
        response.addContent(new Element("states",namespace).setText(process.getState()));
        response.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));

        response.addContent(new Element("requester",namespace).setText(process.getRequesterId()));
        response.addContent(new Element("states",namespace).setText(process.getState()));
        response.addContent(new Element("startedTime",namespace).setText(TimeZoneUtil.convertToTimeZone(trackWflowProcess.getCreatedTime(), null, AppUtil.getAppDateFormat())));
        response.addContent(new Element("limit",namespace).setText(trackWflowProcess.getLimit()));
        response.addContent(new Element("dueDate",namespace).setText(TimeZoneUtil.convertToTimeZone(trackWflowProcess.getDue(), null, AppUtil.getAppDateFormat())));
        response.addContent(new Element("delay",namespace).setText(trackWflowProcess.getDelay()));
        response.addContent(new Element("finishTime",namespace).setText(TimeZoneUtil.convertToTimeZone(trackWflowProcess.getFinishTime(), null, AppUtil.getAppDateFormat())));
        response.addContent(new Element("timeConsumingFromDateCreated",namespace).setText(trackWflowProcess.getTimeConsumingFromDateCreated()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RunningActivityCompleteRequest")
    public @ResponsePayload Element handleRunningActivityComplete(@RequestPayload Element runningActivityCompleteElement) {
        final String processDefId = processDefIdExpression.evaluate(runningActivityCompleteElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(runningActivityCompleteElement).get(0).getValue();
        final String activityId = activityIdExpression.evaluate(runningActivityCompleteElement).get(0).getValue();

        Element response = new Element("RunningActivityCompleteResponse",namespace);
        String username = workflowUserManager.getCurrentUsername();
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.assignmentForceComplete(processDefId, processId, activityId, username);
        response.addContent(new Element("status",namespace).setText("completed"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RunningActivityReassignRequest")
    public @ResponsePayload Element handleRunningActivityReassign(@RequestPayload Element runningActivityReassignElement) {
        final String processDefId = processDefIdExpression.evaluate(runningActivityReassignElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(runningActivityReassignElement).get(0).getValue();
        final String activityId = activityIdExpression.evaluate(runningActivityReassignElement).get(0).getValue();
        final String username = usernameExpression.evaluate(runningActivityReassignElement).get(0).getValue();
        final String replaceUser = replaceUserExpression.evaluate(runningActivityReassignElement).get(0).getValue();

        Element response = new Element("RunningActivityReassignResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.assignmentReassign(processDefId, processId, activityId, username, replaceUser);
        response.addContent(new Element("status",namespace).setText("reassigned"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RunningProcessListRequest")
    public @ResponsePayload Element handleRunningProcessList(@RequestPayload Element runningProcessListElement) {
        final String packageId = packageIdExpression.evaluate(runningProcessListElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(runningProcessListElement).get(0).getValue();
        final String processName = processNameExpression.evaluate(runningProcessListElement).get(0).getValue();
        final String version = versionExpression.evaluate(runningProcessListElement).get(0).getValue();
        String sort = sortExpression.evaluate(runningProcessListElement).get(0).getValue();
        final String descString = descExpression.evaluate(runningProcessListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(runningProcessListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(runningProcessListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);

        Element response = new Element("RunningProcessListResponse",namespace);
        if ("startedTime".equals(sort)) {
            sort = "Started";
        } else if ("createdTime".equals(sort)) {
            sort = "Created";
        }

        Collection<WorkflowProcess> processList = workflowManager.getRunningProcessList(packageId, processId, processName, version, sort, desc, start, rows);

        Integer total = workflowManager.getRunningProcessSize(packageId, processId, processName, version);
        Element data = new Element("data",namespace);
        for (WorkflowProcess workflowProcess : processList) {
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningProcess(workflowProcess.getInstanceId());
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(workflowProcess.getInstanceId()));
            item.addContent(new Element("name",namespace).setText(workflowProcess.getName()));
            item.addContent(new Element("state",namespace).setText(workflowProcess.getState()));
            item.addContent(new Element("version",namespace).setText(workflowProcess.getVersion()));
            item.addContent(new Element("startedTime",namespace).setText(TimeZoneUtil.convertToTimeZone(workflowProcess.getStartedTime(), null, AppUtil.getAppDateFormat())));
            item.addContent(new Element("requesterId",namespace).setText(workflowProcess.getRequesterId()));
            item.addContent(new Element("due",namespace).setText(workflowProcess.getDue() != null ? TimeZoneUtil.convertToTimeZone(workflowProcess.getDue(), null, AppUtil.getAppDateFormat()) : "-"));
            item.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));

            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UserReevaluateRequest")
    public @ResponsePayload Element handleUserReeavaluate(@RequestPayload Element userReeavaluateElement) {
        final String username = usernameExpression.evaluate(userReeavaluateElement).get(0).getValue();
        workflowManager.reevaluateAssignmentsForUser(username);
        Element response = new Element("UserReevaluateResponse",namespace);
        response.addContent(new Element("status",namespace).setText("reevaluated"));
        return response;
    }

}
